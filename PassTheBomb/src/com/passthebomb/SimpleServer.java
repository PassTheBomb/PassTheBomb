package com.passthebomb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Sets up initial connections.
 * Creates a ClientManager thread whenever the correct number of players have arrived.
 */
public class SimpleServer {

	static final int PLAYERS_PER_GAME = 4;
	static final int TCP_PORT = 5432;
	static final int UDP_PORT = 5555;
	static final int UDP_PORT_OUT = 5556;
	
	static SimpleClientManager clientManager;

	public static void main(String[] args) throws IOException {

		// Suppress warning that serverSocket is never closed.
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(TCP_PORT);

		LinkedList<Socket> clientSockets = new LinkedList<Socket>();
		ArrayList<PrintWriter> outArrayList = new ArrayList<PrintWriter>();

		DatagramSocket udpSocketListener = new DatagramSocket(SimpleServer.UDP_PORT);
		DatagramSocket udpSocketSender = new DatagramSocket(SimpleServer.UDP_PORT_OUT);

		// Start UDP_Listener thread.
		Thread udpListener = new Simple_UDP_Listener(udpSocketListener);
		udpListener.start();

		int count = 0; 

		System.out.println("Waiting for connection..");

		while (true) {

			Socket socket = serverSocket.accept();
			System.out.println(count + " p connected");

			clientSockets.add(socket);
			outArrayList.add(new PrintWriter(socket.getOutputStream(), true));
			count++;

			// Message currently connected clients about the number of players 
			// that have been connected already.
			for (PrintWriter out : outArrayList) {
				out.println(clientSockets.size());
				System.out.println(out + ";" + count);
			}

			if (count == PLAYERS_PER_GAME) {

				// Generate list of IP addresses per game.
				LinkedList<String> ipAddresses = new LinkedList<String>();
				for (Socket s : clientSockets) {
					ipAddresses.add(s.getInetAddress().getHostAddress());
				}

				for (PrintWriter out: outArrayList) {
					out.close();
				}

				// Pass the clientSockets, list of IPs and the udpSocketSender to the clientManager.
				clientManager = new SimpleClientManager(clientSockets, ipAddresses, udpSocketSender, udpSocketListener);
				Thread clientManagerThread = new Thread(clientManager);
				System.out.println("Client manager thread created.");
				clientManagerThread.start();

				return;
			}
		}
	}
}

/**
 * Receives client updates and pushes out information to all clients.
 */
class SimpleClientManager implements Runnable {

	private DatagramSocket udpSocketSender;
	private DatagramSocket udpSocketListener;
	private String inputString; // From UDP
	private LinkedList<String> ipAddresses;
	private LinkedList<Socket> clientSockets; 	// TCP - important messages over TCP only.
	private LinkedList<BufferedReader> inputFromClients;
	private LinkedList<PrintWriter> outputToClients;
	private int size;

	private int bombTimer; 
	private int bombHolder;
	private boolean[] bombList;

	public SimpleClientManager(LinkedList<Socket> clients, 
								LinkedList<String> ipAddresses, 
								DatagramSocket udpSocketSender,
								DatagramSocket udpSocketListener) {
		
		this.udpSocketSender = udpSocketSender;
		this.udpSocketListener = udpSocketListener;
		this.ipAddresses = ipAddresses;
		clientSockets = clients;
		inputFromClients = new LinkedList<BufferedReader>();
		outputToClients = new LinkedList<PrintWriter>();
		size = SimpleServer.PLAYERS_PER_GAME;
		bombList = new boolean[SimpleServer.PLAYERS_PER_GAME];
		inputString = null;

		// Set bomb timer to a minimum of baseTime with a variable extraTime. 
		Random randomExtraTime = new Random();
		int baseTime = 30000; 								// 30 sec.
		int extraTime = 1000 * randomExtraTime.nextInt(10); // +0-10 sec.
		bombTimer = baseTime + extraTime;					// total time.

		// Assign bomb holder randomly.
		Random rand = new Random();
		bombHolder = rand.nextInt(size);
		for (int i = 0; i < size; i++) {
			bombList[i] = false;
		}
		bombList[bombHolder] = true;

		try {
			for (Socket s : clientSockets) {
				inputFromClients.add(
						new BufferedReader(
								new InputStreamReader(s.getInputStream()) ));

				outputToClients.add(new PrintWriter(s.getOutputStream(), true));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void set(String str) {
		inputString = str;
	}	

	@Override
	public void run() {
		try {
			// Inform client of their id and who is the bomb holder over TCP. 
			for (int i = 0; i < size; i++) {
				System.out.println("Initialize " + i);
				inputFromClients.get(i).readLine();

				String initInfo = i + ";0,312,512," + bombList[0] + ";1,412,512," + bombList[1] + ";2,712,512," + bombList[2] + ";3,612,512," + bombList[3];
				outputToClients.get(i).println(initInfo);

				System.out.println(initInfo + " to client " + i);
			}

			long startTime = System.currentTimeMillis();

			// Wait for the first UDP packet to arrive (should take a few ms only).
			while (true) {
				if (inputString != null) {
					break;
				}
			}

			// Create DatagramPackets ONCE, so that we do not keep creating NEW PACKETS all the time.
			// When we need to change the output data, just call the set method (see below).
			LinkedList<DatagramPacket> udpPackets = new LinkedList<DatagramPacket>();
			for (String ip : ipAddresses) {
				udpPackets.add(new DatagramPacket(null, 24, 
						InetAddress.getByName(ip), 
						SimpleServer.UDP_PORT_OUT));
			}

			// Receive client information and update all clients constantly.
			// Uses UDP for performance reasons.
			while (true) {

				String input[] = inputString.split(",");

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
				for (int i = 0; i < size; i++) {
					udpPackets.get(i).setData(outputBuffer);
					udpSocketSender.send(udpPackets.get(i));
				}
				//						for (String ip : ipAddresses) {
				//							udpSocketSender.send(
				//									new DatagramPacket(
				//											outputBuffer, 
				//											outputBuffer.length, 
				//											InetAddress.getByName(ip), 
				//											SimpleServer.UDP_PORT_OUT));
				//						}

				// Periodically check if bomb has expired.
				if (System.currentTimeMillis() - startTime >= bombTimer) {
					
					System.out.println("Bomb Exploded.");

					// Inform all clients that the bomb has exploded over TCP.
					for (int i = 0; i < size; i++) {
						outputToClients.get(i).println("Exploded");
					}

					// Clean up and close TCP connections.
					for (int i = 0; i < size; i++) {
						outputToClients.get(i).close();
						inputFromClients.get(i).close();
						clientSockets.get(i).close();
					}
					
					// Close UDP sockets.
					udpSocketSender.close();
					udpSocketListener.close();

					return;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

// Receives all UDP packets and redirects to the correct players in the correct game.
class Simple_UDP_Listener extends Thread {

	DatagramSocket socket;

	Simple_UDP_Listener(DatagramSocket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		// Put this OUTSIDE the while loop. We do not want to keep creating packets.
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length); 
		
		while (true) {
			try {
				socket.receive(packet); // Blocks till packet is received.
				SimpleServer.clientManager.set(new String(buf, "UTF8")); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
