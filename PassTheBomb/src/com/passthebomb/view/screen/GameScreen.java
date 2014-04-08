package com.passthebomb.view.screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.passthebomb.model.local.Opponent;
import com.passthebomb.model.local.Player;
import com.passthebomb.view.gui.Background;

public class GameScreen implements Screen {
	private final int[] screenSize = {1024,1024};

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Vector3 touchPos;
	private Background bg;
	private Player player;
	private Opponent[] oppList = new Opponent[4];

	private Touchpad touchpad;
	private TouchpadStyle touchpadStyle;
	private Skin touchpadSkin;
	private Drawable touchBackground;
	private Drawable touchKnob;
	private Stage stage;

	volatile static String HOST = "192.168.1.132"; // MODIFY THIS FIELD AS REQUIRED.
	static final int PORT = 5432;

	DatagramSocket udpSocket;
	Socket hostSocket;
	private PrintWriter outputToHost;
	private BufferedReader inputFromHost;
	private String output;		// String output to server.
	private String input; 		// String input from server.
	private WaitScreen lastScreen;

	private int updateCtr = 0;

	private int id;				// Player id.
	private int bombHolder; 	// Indicates the player id which holds the bomb.
	private Vector3[] posList;
	private boolean[] bombList;

	private Listener runnableListener;
	private Thread listener;

	public GameScreen(com.badlogic.gdx.Screen lastScreen) {
		this.lastScreen = (WaitScreen)lastScreen;
		try {
			// Set up connections and i/o streams.
			udpSocket = new DatagramSocket(PORT);
			hostSocket = this.lastScreen.getSocket();
			outputToHost = new PrintWriter(hostSocket.getOutputStream(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		runnableListener = new Listener(this.lastScreen.getSocket(), this);
		listener = new Thread(runnableListener);
		listener.start();

		try {
			synchronized(this) {
				this.wait(); 	// Wait for listener for initialization data.
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		id = runnableListener.getWhoAmI();
		posList = runnableListener.getPositionList();
		bombList = runnableListener.getBombList();

		// Setup with first line of input.

		batch = new SpriteBatch();
		bg = Background.createBG(new Texture(Gdx.files.internal("background.png")), new Vector2(0,0), screenSize);

		Texture[] playerTexture = new Texture[2];
		Texture[] oppTexture = new Texture[2];
		playerTexture[0] = new Texture(Gdx.files.internal("player_r.png"));
		playerTexture[1] = new Texture(Gdx.files.internal("player_rb.png"));
		oppTexture[0] = new Texture(Gdx.files.internal("opp_r.png"));	
		oppTexture[1] = new Texture(Gdx.files.internal("opp_rb.png"));	
		player = new Player(new Vector2(posList[id].x,posList[id].y), playerTexture, bombList[id], bg);

		for(int i = 0; i < 4; i++) {
			if (i != id) {
				oppList[i] =  new Opponent(new Vector2(posList[i].x,posList[i].y), oppTexture, bombList[i], bg);
			}
			else {
				oppList[i] = null;
			}
		}

		touchpadSkin = new Skin();
		touchpadSkin.add("touchBackground", new Texture("touchBackground.png"));
		touchpadSkin.add("touchKnob", new Texture("touchKnob.png"));
		touchpadStyle = new TouchpadStyle();
		touchBackground = touchpadSkin.getDrawable("touchBackground");
		touchKnob = touchpadSkin.getDrawable("touchKnob");
		touchpadStyle.background = touchBackground;
		touchpadStyle.knob = touchKnob;
		touchpad = new Touchpad(10, touchpadStyle);
		touchpad.setBounds(15, 15, 200, 200);
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),true, batch);
		stage.addActor(touchpad);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		camera.position.set(player.getAbsPos().x, player.getAbsPos().y, 0);


		posList = runnableListener.getPositionList();
		bombList = runnableListener.getBombList();

		for(int i = 0; i < 4; i++) {
			if (i != id) {
				oppList[i].move(posList[i]);
				oppList[i].setBomb(bombList[i]);
			}
		}
		player.setBomb(bombList[id]);


		// set the clear colour to r, g, b, a
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		// clear screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// update camera
		camera.update();
		// set SpriteBatch to camera coordinate system
		batch.setProjectionMatrix(camera.combined);
		// start new batch
		batch.begin();
		// draw in the new batch
		batch.draw(bg.getBackgroundImg(), 0, 0);
		batch.draw(player.getCharImg(), player.getCharImgX(), player.getCharImgY());

		for(int i = 0; i < 4; i++) {
			if (i != id) {
				batch.draw(oppList[i].getCharImg(), oppList[i].getCharImgX(), oppList[i].getCharImgY());
			}
		}
		// end batch. **Note, all image rendering updates should go between
		// begin and end
		batch.end();

		stage.act(Gdx.graphics.getDeltaTime());	    
		stage.draw();


		touchPos = new Vector3(touchpad.getKnobPercentX()*10, touchpad.getKnobPercentY()*10, 0);
		//System.out.println("X: "+touchpad.getKnobPercentX());
		//System.out.println("Y: "+touchpad.getKnobPercentY());
		//System.out.println("Moving (GameScreen) X: "+(touchPos.x)+" Y:"+(touchPos.y));
		// change position from global coordinates to camera coordinates
		//camera.translate(x, y);
		player.move(touchPos);

		/*// acquire touch input
		if (Gdx.input.isTouched()) {
			// acquire touch position
			touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			// change position from global coordinates to camera coordinates
			camera.unproject(touchPos);
			player.move(touchPos);
			// update the cicle position

		}*/
		player.update();


		int collidedTarget = -1;
		for (int i = 0; i < 4; i++) {
			if (i != id){
				if (player.collide(oppList[i])) {
					collidedTarget = i;
				}
			}
		}


		//compile message to send to server;
		/*if (updateCtr < 2){
			updateCtr++;
		}
		else{*/
		outputToHost.println();
		updateCtr = 0;
		//}
		
		String output = id+","+player.getAbsPos().x+","+player.getAbsPos().y
						+","+collidedTarget+","+player.getBombState();
		try {
			byte[] outputBuffer = output.getBytes("UTF8");
			udpSocket.send(
					new DatagramPacket(
							outputBuffer, 
							outputBuffer.length, 
							InetAddress.getByName(GameScreen.HOST), 
							GameScreen.PORT));
		} catch (Exception e) { 
			System.out.println(e);
		}
	}

	@Override
	public void resize(int width, int height) {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, width, height);
		batch.setProjectionMatrix(camera.combined);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		batch.dispose();
		player.dispose();
		for (int i = 0 ; i < 4; i ++) {
			if (i != id) {
				oppList[i].dispose();
			}
		}
		bg.dispose();
	}


}

class Listener implements Runnable {
	private final static int PLAYER_LIMIT = 4;

	private BufferedReader inputFromHost;
	private PrintWriter outputToHost;
	private Socket socket;
	private DatagramSocket UDPSocket;

	private volatile static int whoAmI = -1;
	private volatile static Vector3[] positionList = new Vector3[PLAYER_LIMIT];
	private volatile static boolean[] bombList = new boolean[PLAYER_LIMIT];

	private GameScreen mainThread;

	public Listener(Socket socket, GameScreen mainThread) {
		this.socket = socket;
		this.mainThread = mainThread;

		try {
			UDPSocket = new DatagramSocket(GameScreen.PORT);
			UDPSocket.setSoTimeout(1000);
		} catch (SocketException e) {
			System.out.println(e);
		}
	}

	@Override
	public void run() {
		try {
			inputFromHost 
			= new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			outputToHost = new PrintWriter(socket.getOutputStream(),true);
		} catch (IOException e) {
			System.out.println(e);
		}
		
		outputToHost.println("ready");
		
		try {
			// Process initialization data over TCP.
			String input = inputFromHost.readLine();
			String[] passedInfo = input.split(";");
			whoAmI = Integer.parseInt(passedInfo[0]);
			String[] currentPlayerInfo;
			int player,x,y;
			for (int i = 1; i < passedInfo.length; i++){
				currentPlayerInfo = passedInfo[i].split(",");
				player = Integer.parseInt(currentPlayerInfo[0]);
				x = Integer.parseInt(currentPlayerInfo[1]);
				y = Integer.parseInt(currentPlayerInfo[2]);
				positionList[player] = new Vector3(x,y,0);
				bombList[player] = Boolean.parseBoolean(currentPlayerInfo[3]);
			}

			synchronized(mainThread) {
				mainThread.notify();	
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		while(true) {
			try {
				// Get UDP input from server, if any.
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);

				try {
					UDPSocket.receive(packet); // Blocks for the specified socket timeout.
				} catch (SocketTimeoutException e) {
					
					// If no UDP packets are received with the timeout period, check for any TCP packet.
					if (inputFromHost.ready()) {
						if (inputFromHost.readLine().equals("Exploded")) {
							
							// Close connections.
							inputFromHost.close();
							outputToHost.close();
							socket.close();

							// TODO
							// TODO
							// TODO: Change screen to credit screen.
							// For now, server doesn't send over who has the bomb. 
							// If you have the bomb, you just lose immediately (:

							break;
						}
					}
				}

				String input = new String(buf, "UTF8");
				String[] passedInfo = input.split(",");

				/*for (int i = 0; i < passedInfo.length; i++) {
						System.out.println(passedInfo[i]);
					}*/
				int player = Integer.parseInt(passedInfo[0]);
				float x = Float.parseFloat(passedInfo[1]);
				float y = Float.parseFloat(passedInfo[2]);
				positionList[player] = new Vector3(x,y,0);
				bombList[player] = Boolean.parseBoolean(passedInfo[3]);;
			} 
			
			catch (Exception e) {
				System.out.println(e);
			}
		}

	}

	public int getWhoAmI() {
		return whoAmI;
	}

	public Vector3[] getPositionList() {
		return positionList;
	}

	public boolean[] getBombList() {
		return bombList;
	}
}
