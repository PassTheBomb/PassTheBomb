package com.passthebomb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Constantly listens for new client connections.
 * Creates a ClientManager thread whenever the correct number of players 
 * have arrived, and continues listening for new connections thereafter.
 */
public class Server {

	static final int PLAYERS_PER_GAME = 4;
	static final int TCP_PORT = 5432;
	static final int UDP_PORT = 5555;

	static int game_no = 0; // For each new game created, the ocunt increases by 1.

	// Global list of ALL clients in all games.
	// @Guarded by Server.class.
	volatile static LinkedList<String> activeConnections; 

	volatile static LinkedList<ClientManager> managers;

	public static void main(String[] args) {

		activeConnections = new LinkedList<String>();
		managers = new LinkedList<ClientManager>();

		// Suppress warning that serverSocket is never closed.
		@SuppressWarnings("resource") 
		try {
		ServerSocket serverSocket = new ServerSocket(TCP_PORT);
		} catch (Exception e) {
			System.out.println("Exception in TCP Port.");
		}
		LinkedList<Socket> clientSockets = new LinkedList<Socket>();
		ArrayList<PrintWriter> outArrayList = new ArrayList<PrintWriter>();
		
		// Start UDP Listener.
		Thread udpListener = new UDP_Listener();
		udpListener.start();

		int count = 0; 

		/*
		 * For every 4 clients connected, we create a manager thread which will
		 * manage the client information.
		 * 
		 * Server thread continues to run and listens for new connections.
		 */
		while (true) {
			System.out.println("Waiting for connection..");
			Socket socket = serverSocket.accept();
			clientSockets.add(socket);
			outArrayList.add(new PrintWriter(socket.getOutputStream(), true));

			String IP = socket.getInetAddress().toString();
			
			synchronized(Server.class) {
				activeConnections.add(IP);
			}
			
			System.out.println(count + " p connected to game number " + game_no 
					+ " with IP: " + IP);

			count++;

			for (PrintWriter out : outArrayList) {
				out.println(clientSockets.size());
				System.out.println(out + ";" + count);
			}

			if (count == PLAYERS_PER_GAME) {
				
				// Generate list of IP addresses per game.
				LinkedList<String> ipAddresses = new LinkedList<String>();
				for (Socket s : clientSockets) {
					ipAddresses.add(s.getInetAddress().toString());
				}

				// Create copy of the list of client sockets.
				LinkedList<Socket> copyOfClientSockets = new LinkedList<Socket>(clientSockets);

				// Pass the clientSockets and their IPs to the clientManager, plus the game number.
				ClientManager clientManager = new ClientManager(copyOfClientSockets, ipAddresses, game_no);
				managers.add(clientManager);
				Thread clientManagerThread = new Thread(clientManager);
				System.out.println("Manager thread created.");
				clientManagerThread.start();

				// Reset count and clear the existing list of clients sockets. 
				count = 0;
				clientSockets.clear();
				outArrayList.clear();

				// Increment the game number.
				game_no++; 

			}
		}
	}
}

/**
 * Responsible for starting and managing client threads.
 * Receives client updates and pushes out information to all clients.
 */
class ClientManager implements Runnable {

	// udpBuffer is not synchronized for performance reasons;
	// packet loss is not a big deal, which is why we use UDP in the first place.
	private DatagramSocket udpSocket;
	private volatile String inputString; // From UDP
	private LinkedList<String> ipAddresses;
	private LinkedList<Socket> clientSockets; 	// TCP - important messages over TCP only.
	private LinkedList<BufferedReader> inputFromClients;
	private LinkedList<PrintWriter> outputToClients;
	private int size;
	private int game_no;

	private int bombTimer; 
	private int bombHolder;
	private boolean[] bombList;

	public ClientManager(LinkedList<Socket> clients, LinkedList<String> ipAddresses, int game_no) {
		try {
			udpSocket = new DatagramSocket(Server.UDP_PORT);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		inputString = "";
		this.ipAddresses = ipAddresses;
		clientSockets = clients;
		inputFromClients = new LinkedList<BufferedReader>();
		outputToClients = new LinkedList<PrintWriter>();
		size = Server.PLAYERS_PER_GAME;
		this.game_no = game_no;
		bombList = new boolean[Server.PLAYERS_PER_GAME];

		// Set bomb timer to a minimum of baseTime with a variable extraTime 
		Random randomExtraTime = new Random();
		int baseTime = 30000; 								// 30 sec.
		int extraTime = 1000 * randomExtraTime.nextInt(10); // +0-10 sec.
		bombTimer = baseTime + extraTime;					// total time. 

		try {
			for (Socket s : clientSockets) {
				inputFromClients.add(
						new BufferedReader(
								new InputStreamReader(s.getInputStream()) ));

				// Set the second param of the PrintWriter constructor to true
				// to enable AUTO-FLUSHING.
				outputToClients.add(new PrintWriter(s.getOutputStream(), true));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the buffer attribute in the ClientManager class.
	 */
	public void set(String str) {
		inputString = str;
	}	

	@Override
	public void run() {
		try {

			for (int i = 0; i < size; i++) {
				bombList[i] = false;
			}

			Random rand = new Random();
			bombHolder = rand.nextInt(size);
			bombList[bombHolder] = true;

			// Inform client of their id and who is the bomb holder over TCP. 
			for (int i = 0; i < size; i++) {
				inputFromClients.get(i).readLine();

				String initInfo = i + ";0,312,512," + bombList[0] + ";1,412,512," + bombList[1] + ";2,712,512," + bombList[2] + ";3,612,512," + bombList[3];
				outputToClients.get(i).println(initInfo);
				
				System.out.println(initInfo + " to client " + i);
			}

			long startTime = System.currentTimeMillis();

			// Receive client information and update all clients constantly.
			// Uses UDP for performance reasons.
			while (true) {
				if (!inputString.equals("")) { // Extract the stuff from the buffer.
					inputString = ""; 

					String input[] = inputString.split(",");

					try {
						// Process packet information.
						int playerNo = Integer.parseInt(input[0]);
						int collidedPlayerNo = Integer.parseInt(input[3]);
						boolean carryBomb = Boolean.parseBoolean(input[4]);
						if (collidedPlayerNo != -1 && carryBomb && carryBomb == bombList[playerNo]) {
							bombList[playerNo] = false;
							bombList[collidedPlayerNo] = true;
						}
						
						// Generate output string to be sent to rest of clients.
						String output = input[0]+","+input[1]+","+input[2]+","+bombList[playerNo];
						byte[] outputBuffer = output.getBytes("UTF8");
						
						// Transmit packet to all other clients over UDP.
						for (String ip : ipAddresses) {
							udpSocket.send(
									new DatagramPacket(
											outputBuffer, 
											outputBuffer.length, 
											InetAddress.getByName(ip), 
											Server.UDP_PORT));
						}
						
					} catch (Exception e) { 
						System.out.println("Error in processing UDP packet.");
					}
				}
				
				// Periodically check if bomb has expired then exit loop.
				if (System.currentTimeMillis() - startTime >= bombTimer) {
					System.out.println("Bomb Exploded.");
					break;
				}
			}

			// After exiting loop, inform all clients that the bomb has exploded.
			// Done over TCP.
			for (int i = 0; i < size; i++) {
				outputToClients.get(i).println("Exploded");
			}

			// Clean up and close TCP connections.
			int index = game_no * 4; 
			for (int i = 0; i < size; i++) {
				outputToClients.get(i).close();
				inputFromClients.get(i).close();
				clientSockets.get(i).close();

				synchronized(Server.class) {
					// Replace the ips from the global list of connections so that
					// the UDP Listener will not re-use them.
					Server.activeConnections.set(index+i, "gameover"); 
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

// Receives all UDP packets and redirects to the correct players in the correct game.
class UDP_Listener extends Thread {

	DatagramSocket socket;

	UDP_Listener() {
		try {
			socket = new DatagramSocket(Server.UDP_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet); // Blocks till packet is received.
				
				String received = new String(buf, "UTF8");
				System.out.println(received);

				String clientIP = packet.getAddress().toString();
				
				/* Using the IP of the packet, determine the global client number
				 * and hence determine the game number, so that we can "re-direct"
				 * the packet to the correct game out of the many games running. */
				
				int globalClientNo = Server.activeConnections.indexOf(clientIP);
				int game_no = (globalClientNo) / Server.PLAYERS_PER_GAME; // integer division

				// Give the packet data to the correct client manager.
				Server.managers.get(game_no).set(received);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
