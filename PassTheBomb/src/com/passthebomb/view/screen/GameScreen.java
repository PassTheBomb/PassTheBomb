package com.passthebomb.view.screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
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

/**
 * The game screen is the active screen during game play. This GameScreen class
 * controls what is drawn and manages the update process.
 * 
 */
public class GameScreen implements Screen {
	private final int[] screenSize = { 1024, 1024 };

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Vector3 touchPos;
	private Background bg;
	private Player player;
	private Opponent[] oppList = new Opponent[4];

	private BitmapFont font;
	private TextBounds fontBounds;

	private Touchpad touchpad;
	private TouchpadStyle touchpadStyle;
	private Skin touchpadSkin;
	private Drawable touchBackground;
	private Drawable touchKnob;
	private Stage stage;

	Socket hostSocket;
	private PrintWriter outputToHost;
	private WaitScreen lastScreen;

	private int id; // Player id.
	private Vector3[] posList;
	private boolean[] bombList;

	private Listener runnableListener;
	private Thread listener;

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
			outputToHost = new PrintWriter(hostSocket.getOutputStream(), true);
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

		batch = new SpriteBatch();
		if (!Gdx.files.internal("background.png").exists()) {
			System.err.println("Cannot find background img");
		} else {

			bg = Background.createBG(
					new Texture(Gdx.files.internal("background.png")),
					new Vector2(0, 0), screenSize);
		}

		Texture[] playerTexture = new Texture[2];
		Texture[] oppTexture = new Texture[2];

		if (!Gdx.files.internal("player_r.png").exists()
				|| !Gdx.files.internal("player_rb.png").exists()) {
			System.err.println("Cannot find player img");
		} else {
			playerTexture[0] = new Texture(Gdx.files.internal("player_r.png"));
			playerTexture[1] = new Texture(Gdx.files.internal("player_rb.png"));
		}

		if (!Gdx.files.internal("opp_r.png").exists()
				|| !Gdx.files.internal("opp_rb.png").exists()) {
			System.err.println("Cannot find opponent img");
		} else {
			oppTexture[0] = new Texture(Gdx.files.internal("opp_r.png"));
			oppTexture[1] = new Texture(Gdx.files.internal("opp_rb.png"));
		}
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
		touchpadSkin.add("touchBackground", new Texture("touchBackground.png"));
		touchpadSkin.add("touchKnob", new Texture("touchKnob.png"));
		touchpadStyle = new TouchpadStyle();
		touchBackground = touchpadSkin.getDrawable("touchBackground");
		touchKnob = touchpadSkin.getDrawable("touchKnob");
		touchpadStyle.background = touchBackground;
		touchpadStyle.knob = touchKnob;
		touchpad = new Touchpad(10, touchpadStyle);
		touchpad.setBounds(15, 15, 200, 200);
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				true, batch);
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
					player.getCharImgY());
		}

		for (int i = 0; i < 4; i++) {
			if (i != id && oppList[i].isAlive()) {
				batch.draw(oppList[i].getCharImg(), oppList[i].getCharImgX(),
						oppList[i].getCharImgY());
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
		outputToHost.println(id + "," + player.getAbsPos().x + ","
				+ player.getAbsPos().y + "," + collidedTarget + ","
				+ player.getBombState());

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
	}

	/**
	 * Returns to the main screen.
	 * 
	 */
	public void returnMain() {
		Thread.currentThread().interrupt();
		// TODO
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
	private PrintWriter outputToHost;
	private Socket socket;

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
	}

	@Override
	public void run() {
		try {
			// Establish i/o stream
			inputFromHost = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			outputToHost = new PrintWriter(socket.getOutputStream(), true);

			// Acquire initial setup data
			outputToHost.println("ready");
			input = inputFromHost.readLine();
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
				if (inputFromHost.ready()) {
					input = inputFromHost.readLine();
					if (input.equals("quit")) {
						System.err.println("Server terminated");
						mainThread.returnMain();
						active = false;
					} else {
						passedInfo = inputFromHost.readLine().split(",");
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
