package com.passthebomb.view.screen;

import java.io.*;
import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.passthebomb.controller.ScreenManager;
import com.passthebomb.model.local.Opponent;
import com.passthebomb.model.local.Player;
import com.passthebomb.model.local.Screen;
import com.passthebomb.security.MsgHandler;
import com.passthebomb.view.gui.Background;

/**
 * The game screen is the active screen during game play. This GameScreen class
 * controls what is drawn and manages the update process.
 * 
 */
public class GameScreen implements com.badlogic.gdx.Screen {
	private final float[] screenSize = {1024, 1024};
	private final float PLAYER_TEXTURE_SIZE = 150f;
	private final float JOYSTICK_TEXTURE_SIZE = 128f;
	private final float DEFAULT_SCREEN_SIZE = 800f;
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Vector3 touchPos;
	private Background bg;
	private Player player;
	private Opponent[] oppList = new Opponent[4];
	     
	private BitmapFont font;
	private TextBounds fontBounds;
	private TextureAtlas imgpack;
	private Array<Sprite> textureset;
	private Sprite[] playerTexture = new Sprite[2];
	private Sprite[] oppTexture = new Sprite[2];

	private Touchpad touchpad;
	private TouchpadStyle touchpadStyle;
	private Skin touchpadSkin;
	private Drawable touchBackground;
	private Drawable touchKnob;
	private Stage stage;

	Socket hostSocket;
	private WaitScreen lastScreen;

	private int id; // Player id.
	private Vector3[] posList;
	private boolean[] bombList;
	private static boolean amIWin;

	private Listener runnableListener;
	private Thread listener;
	
	private OutputStream out = null;
	
	private float resizeFactor;
	
	
	public boolean isAmIWin() {
		return amIWin;
	}
	
	protected static void setAmIWin(boolean amIWin) {
		GameScreen.amIWin = amIWin;
	}

	/**
	 * Creates a GameScreen class. Is usually created when a game is
	 * successfully established.
	 * 
	 * @param lastScreen
	 *            The previous screen the application was at. Usually the
	 *            WaitScreen.
	 */
	public GameScreen(com.badlogic.gdx.Screen lastScreen) {
		this.lastScreen = (WaitScreen) lastScreen;
		try {
			// Set up connections and stream to update server.
			hostSocket = this.lastScreen.getSocket();
			out = hostSocket.getOutputStream();
		} catch (Exception e) {
			System.err
					.println("Connection Error. Cannot establish server updater.");
			returnMain();
		}
		// Set up Listener thread to constantly listen for server broadcasts.
		runnableListener = new Listener(this.lastScreen.getSocket(), this);
		listener = new Thread(runnableListener);
		listener.start();

		try {
			synchronized (this) {
				// Wait for the listener to acquire initial data
				this.wait();
			}
		} catch (InterruptedException e1) {
			System.err
					.println("Interrupted while pulling initial data from server.");
			returnMain();
		}

		// Setup
		id = runnableListener.getWhoAmI();
		posList = runnableListener.getPositionList();
		bombList = runnableListener.getBombList();
		
		//Setup with first line of input.
		resizeFactor = Gdx.graphics.getWidth()/DEFAULT_SCREEN_SIZE;	

		batch = new SpriteBatch();
		if (!Gdx.files.internal("background.png").exists()) {
			System.err.println("Cannot find background img");
		} else {

			bg = Background.createBG(
					new Texture(Gdx.files.internal("background.png")),
					new Vector2(0, 0), screenSize);
		}
		if (!Gdx.files.internal("player_r_s.png").exists()){
			System.err.println("Cannot find img");
		} else {
			imgpack = new TextureAtlas(Gdx.files.internal("imgpack.txt"));
			textureset = imgpack.createSprites();
		}
		oppTexture[0] = textureset.get(0);
		oppTexture[1] = textureset.get(1);
		playerTexture[0] = textureset.get(2);
		playerTexture[1] = textureset.get(3);
		player = new Player(new Vector2(posList[id].x, posList[id].y),
				playerTexture, bombList[id], bg);

		for (int i = 0; i < 4; i++) {
			if (i != id) {
				oppList[i] = new Opponent(new Vector2(posList[i].x,
						posList[i].y), oppTexture, bombList[i], bg);
			} else {
				oppList[i] = null;
			}
		}

		font = new BitmapFont();
		font.setColor(Color.WHITE);

		touchpadSkin = new Skin();
		touchpadSkin.add("touchBackground", textureset.get(4));
		touchpadSkin.add("touchKnob", textureset.get(5));
		touchpadStyle = new TouchpadStyle();
		touchBackground = touchpadSkin.getDrawable("touchBackground");
		touchKnob = touchpadSkin.getDrawable("touchKnob");
		touchpadStyle.background = touchBackground;
		touchpadStyle.knob = touchKnob;
		touchpad = new Touchpad(10, touchpadStyle);
		
		touchpad.setBounds(15, 15, JOYSTICK_TEXTURE_SIZE*resizeFactor, JOYSTICK_TEXTURE_SIZE*resizeFactor);
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),true, batch);

		stage.addActor(touchpad);
		Gdx.input.setInputProcessor(stage);
		// End Setup
	}

	@Override
	public void render(float delta) {
		// Camera centres on player and will track player throughout the game
		camera.position.set(player.getAbsPos().x, player.getAbsPos().y, 0);

		// Acquire new data from Listener and update information
		posList = runnableListener.getPositionList();
		bombList = runnableListener.getBombList();

		if (posList == null || bombList == null) {
			System.err.println("Data uninitialized");
		}

		for (int i = 0; i < 4; i++) {
			if (i != id) {
				oppList[i].move(posList[i]);
				oppList[i].setBomb(bombList[i]);
			}
		}
		player.setBomb(bombList[id]);

		// Start drawing things to render
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bg.getBackgroundImg(), 0, 0);
		
		if (player.isAlive()) {
			fontBounds = font.getBounds("Player");
			font.draw(batch, "Player", player.getAbsPos().x - fontBounds.width
					/ 2, player.getAbsPos().y + 50);
			batch.draw(player.getCharImg(), player.getCharImgX(),
					player.getCharImgY(), PLAYER_TEXTURE_SIZE*resizeFactor, PLAYER_TEXTURE_SIZE*resizeFactor);
		}

		for (int i = 0; i < 4; i++) {
			if (i != id && oppList[i].isAlive()) {
				batch.draw(oppList[i].getCharImg(), oppList[i].getCharImgX(), oppList[i].getCharImgY(), PLAYER_TEXTURE_SIZE*resizeFactor, PLAYER_TEXTURE_SIZE*resizeFactor);
				fontBounds = font.getBounds(String.valueOf(i + 1));
				font.draw(batch, String.valueOf(i + 1),
						oppList[i].getAbsPos().x - fontBounds.width / 2,
						oppList[i].getAbsPos().y + 50);
			}
		}
		batch.end();

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		// Acquire player input
		touchPos = new Vector3(touchpad.getKnobPercentX() * 10,
				touchpad.getKnobPercentY() * 10, 0);
		player.move(touchPos);
		player.update();

		int collidedTarget = -1;
		for (int i = 0; i < 4; i++) {
			if (i != id) {
				if (player.collide(oppList[i])) {
					collidedTarget = i;
				}
			}
		}

		// Transmit player data to server
		String msg = id + "," + player.getAbsPos().x + ","
				+ player.getAbsPos().y + "," + collidedTarget + ","
				+ player.getBombState();
		try {
			out.write(MsgHandler.createNetworkMsg(msg.getBytes("UTF8")));
			out.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Couldn't send data to server");
			e.printStackTrace();
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

	}

	@Override
	public void hide() {
		ScreenManager.getInstance().dispose(Screen.GAME);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		batch.dispose();
		player.dispose();
		for (int i = 0; i < 4; i++) {
			if (i != id) {
				oppList[i].dispose();
			}
		}
		bg.dispose();
		imgpack.dispose();

		// Close i/o streams and sockets when disposed.
		try {
			runnableListener.getInputFromHost().close();
			if (!hostSocket.isClosed()) {
				hostSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Returns to the main screen.
	 * 
	 */
	public void returnMain() {
		Thread.currentThread().interrupt();
		
		Gdx.app.postRunnable(new Runnable() {
	         public void run() {
	        	 ScreenManager.getInstance().show(Screen.MAIN_MENU, GameScreen.this);
	         }
		});
	}
	
	public void goToCredit() {
		Gdx.app.postRunnable(new Runnable() {
	         public void run() {
	        	 ScreenManager.getInstance().show(Screen.CREDITS, GameScreen.this);
	         }
		});
		
	}

}

/**
 * A Runnable that listens on the server game data broadcasts.
 * 
 */
class Listener implements Runnable {
	private final static int PLAYER_LIMIT = 4;

	private BufferedReader inputFromHost;
	private String input;
	private String[] passedInfo;
	private Socket socket;
	private InputStream in;
	private OutputStream out;

	private volatile static int whoAmI = -1;
	private volatile static Vector3[] positionList = new Vector3[PLAYER_LIMIT];
	private volatile static boolean[] bombList = new boolean[PLAYER_LIMIT];

	private GameScreen mainThread;

	private boolean active;

	/**
	 * Creates a Listener.
	 * 
	 * @param socket
	 *            The socket that will be listened to.
	 * @param mainThread
	 *            The main game thread the Listener runs from. Used to
	 *            synchronize initial data setup
	 */
	public Listener(Socket socket, GameScreen mainThread) {
		this.socket = socket;
		this.mainThread = mainThread;
		this.active = true;
		try {
			this.in = socket.getInputStream();
			this.out = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedReader getInputFromHost() {
		return inputFromHost;
	}

	@Override
	public void run() {
		try {
			// Establish i/o stream
			inputFromHost = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			// Acquire initial setup data
			//outputToHost.println("ready");
			out.write(MsgHandler.createNetworkMsg(new String("ready").getBytes()));
			out.flush();

			//input = inputFromHost.readLine();
			input = new String(MsgHandler.acquireNetworkMsg(in), "UTF8");
			passedInfo = input.split(";");
			try {
				whoAmI = Integer.parseInt(passedInfo[0]);
				String[] currentPlayerInfo;
				int player, x, y;
				for (int i = 1; i < passedInfo.length; i++) {
					currentPlayerInfo = passedInfo[i].split(",");
					player = Integer.parseInt(currentPlayerInfo[0]);
					x = Integer.parseInt(currentPlayerInfo[1]);
					y = Integer.parseInt(currentPlayerInfo[2]);
					positionList[player] = new Vector3(x, y, 0);
					bombList[player] = Boolean
							.parseBoolean(currentPlayerInfo[3]);
				}
			} catch (Exception e) {
				System.err
						.println("Server input format mismatch. Unable to initialize game.");
				mainThread.returnMain();
				active = false;
			}

			synchronized (mainThread) {
				// Notify main game thread that the initial data has been
				// acquired, so that the main game thread will pull the initial
				// data
				mainThread.notify();
			}

		} catch (IOException e) {
			System.err
					.println("Connection Error. Failed to acquire initialization data from server due to Listener fault");
			mainThread.returnMain();
			active = false;
		}

		// Switch to broadcast listening loop
		while (active) {
			try {
				if (in.available() > 0) {
					input = new String(MsgHandler.acquireNetworkMsg(in), "UTF8");
					if (input.equals("quit")) {
						System.err.println("Server terminated");
						mainThread.returnMain();
						active = false;
						socket.close();
					} else if (input.equals("Exploded")) {
						
						System.out.println("Exploded");
						
						// If I have bomb, I lose the game
						boolean doIHaveBomb = bombList[whoAmI];
						
						// Change screen to credit screen to see who wins / lose. 
						GameScreen.setAmIWin(!doIHaveBomb);
						//ScreenManager.getInstance().show(Screen.CREDITS, this.mainThread);
						mainThread.goToCredit();
						active = false;
						
					} else {
						passedInfo = input.split(",");
						try {
							int player = Integer.parseInt(passedInfo[0]);
							float x = Float.parseFloat(passedInfo[1]);
							float y = Float.parseFloat(passedInfo[2]);
							positionList[player] = new Vector3(x, y, 0);
							bombList[player] = Boolean
									.parseBoolean(passedInfo[3]);
						} catch (Exception e) {
							System.err.println("Server input format mismatch.");
						}
					}
				}
			} catch (IOException e) {
				if (socket.isClosed()) {
					System.err.println("Socket is Closed");
				}
				System.err.println("Connection Error");
				mainThread.returnMain();
				active = false;
			}
		}

	}

	/**
	 * Acquires the player id
	 * 
	 * @return player id
	 */
	public int getWhoAmI() {
		return whoAmI;
	}

	/**
	 * Acquires the position data for all players
	 * 
	 * @return list of position data in player id order
	 */
	public Vector3[] getPositionList() {
		return positionList;
	}

	/**
	 * Acquires the bomb status data for all players
	 * 
	 * @return list of bomb status data in player id order
	 */
	public boolean[] getBombList() {
		return bombList;
	}

}
