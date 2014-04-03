package com.passthebomb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;

/**
 * Constantly listens for new client connections.
 * Creates a ClientManager thread whenever the correct number of players 
 * have arrived, and continues listening for new connections thereafter.
 */
public class Server {

	static final int PLAYERS_PER_GAME = 4;

	public static void main(String[] args) throws Exception {
		
		// Suppress warning that serverSocket is never closed.
		@SuppressWarnings("resource") 
		ServerSocket serverSocket = new ServerSocket(5432);
		LinkedList<Socket> clientSockets = new LinkedList<Socket>();

		int count = 0; 

		/*
		 * For every 4 clients connected, we create a manager thread which will
		 * manage the client information.
		 * 
		 * Server thread continues to run and listens for new connections.
		 */
		while (true) {
			Socket socket = serverSocket.accept();
			clientSockets.add(socket);
			count++;
			System.out.println(count + " p connected.");
			for(Socket client : clientSockets) {
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				out.println(clientSockets.size());
			}

			if (count == PLAYERS_PER_GAME) {

				// Create copy of the list of client sockets.
				LinkedList<Socket> copyOfClientSockets = new LinkedList<Socket>(clientSockets);

				// Pass the clientSockets to the clientManager.
				Thread clientManager = new Thread(new ClientManager(copyOfClientSockets));
				System.out.println("Manager thread created.");
				clientManager.start();

				// Reset count and clear the existing list of clients sockets. 
				count = 0;
				clientSockets.clear();
			}

			Thread.sleep(1000);
		}
	}
}

/**
 * Responsible for starting and managing client threads.
 * Receives client updates and pushes out information to all clients.
 * 
 * If performance is too slow, we may need to implement one dedicated thread for receiving updates, per client.
 */
class ClientManager implements Runnable {

	private LinkedList<Socket> clientSockets;
	private LinkedList<BufferedReader> inputFromClients;
	private LinkedList<PrintWriter> outputToClients;
	private int size;
	private int bombTimer; 
	private int bombHolder;
	private boolean[] bombList = new boolean[Server.PLAYERS_PER_GAME];

	public ClientManager(LinkedList<Socket> clients) {
		clientSockets = clients;
		inputFromClients = new LinkedList<BufferedReader>();
		outputToClients = new LinkedList<PrintWriter>();
		size = Server.PLAYERS_PER_GAME;

		// Set bomb timer to 30sec.
		bombTimer = 60000;

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

	@Override
	public void run() {
		try {
			for (int i = 0; i < Server.PLAYERS_PER_GAME; i++){
				bombList[i] = false;
			}
			Random rand = new Random();
			bombHolder = rand.nextInt(Server.PLAYERS_PER_GAME);
			bombList[bombHolder] = true;
			
			// Inform client of their id and who is the bomb holder. 
			for (int i = 0; i < size; i++) {
				String initInfo = i + ";0,824,1024," + bombList[0] + ";1,924,1024," + bombList[1] + ";2,1124,1024," + bombList[2] + ";3,1224,1024," + bombList[3];
				outputToClients.get(i).println(initInfo);
			}

			long startTime = System.currentTimeMillis();

			// Receive client information and update all clients constantly.
			while (true) {
				for (int i = 0; i < size; i++) {
					
					// Is the buffer ready to be read? If not, I'll check the next buffer.
					if (inputFromClients.get(i).ready()) {
						
						/* Receive: 
						 * 	"id, x_coordinate, y_coordinate, bomb_from, bomb_to" */
						String input[] = inputFromClients.get(i).readLine().split(",");
						
						int collidedPlayerNo = Integer.parseInt(input[3]);
						boolean carryBomb = Boolean.parseBoolean(input[4]);
						if (collidedPlayerNo != -1 && carryBomb && carryBomb == bombList[i]){
							bombList[i] = false;
							bombList[collidedPlayerNo] = true;
						}
						//To-Do: Must handle collision checker. "Handshake" the collision
						
						// Transmit to all other clients.
						for (int j = 0; j < size; j++) {
							outputToClients.get(j).println(input[0]+","+input[1]+","+input[2]+","+bombList[i]);
						}
					}
				}

				// Periodically check if bomb has expired then exit loop.
				if (System.currentTimeMillis() - startTime >= bombTimer) {
					System.out.println("Bomb Exploded.");
					break;
				}
			}
			
			// Inform all clients that the bomb has exploded.
			for (int i = 0; i < size; i++) {
				outputToClients.get(i).println("Exploded");
			}
			
			// Perform clean up logic.
			for (int i = 0; i < size; i++) {
				outputToClients.get(i).close();
				inputFromClients.get(i).close();
				clientSockets.get(i).close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}	
}
