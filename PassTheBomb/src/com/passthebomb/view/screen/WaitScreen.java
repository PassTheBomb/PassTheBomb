package com.passthebomb.view.screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.passthebomb.controller.ScreenManager;
import com.passthebomb.model.local.PROTOCAL;
import com.passthebomb.model.local.Screen;
import com.passthebomb.security.ClientAuthentication;
import com.passthebomb.security.Keys;
import com.passthebomb.security.Security;

public class WaitScreen implements com.badlogic.gdx.Screen{

	final String HOST = "192.168.81.2";
	final int PORT = 5432;
	
	private final float TITLE_WIDTH = 256;
	private final float TITLE_HEIGHT = 64;
	
	private SpriteBatch batch = null;
	private Texture titleTexture;
	private BitmapFont font;
	private Socket socket;
	private BufferedReader inChannel;
	private int numOfPlayerJoined;
	private float resizeFactor;
	private ProtocalScreen lastScreen;
	private PROTOCAL protocal;
	private Security s;
	private Keys k;
	
	public Security getS() {
		return s;
	}

	public Keys getK() {
		return k;
	}
	
	protected PROTOCAL getProtocal() {
		return protocal;
	}

	public WaitScreen(com.badlogic.gdx.Screen lastScreen) {
		this.lastScreen = (ProtocalScreen)lastScreen;
		this.protocal =	this.lastScreen.chosedProtocal;
		System.out.println(this.protocal.name());
		batch = new SpriteBatch();    
        font = new BitmapFont();
        font.setColor(Color.RED);
        numOfPlayerJoined = 1;
        this.startSession();
        try {
        	boolean result = this.verificaiton();
        	System.out.println("Verification return"+result);
        	if (!result) {
        		System.out.println("Verification failed");
				this.socket.close();
				ScreenManager.getInstance().show(Screen.MAIN_MENU, WaitScreen.this);
			}
		} catch (IOException e) {
			System.err.println("Can not get input and output stream");
			ScreenManager.getInstance().show(Screen.MAIN_MENU, WaitScreen.this);
		}
        System.out.println("Verification passed");
        this.titleTexture = new Texture(Gdx.files.internal("title.png"));
        resizeFactor = Gdx.graphics.getWidth()/800;
    }
	
	private void startSession() {
		try {
			this.socket = new Socket(HOST, PORT);
			inChannel = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Cannot Find Server");
			returnMain();
			
		} catch (IOException e) {
			System.err.println("Cannot Establish Connection");
			returnMain();
		}
	}
	
	private boolean verificaiton() throws IOException {
		s = new Security();
		k = new Keys();
		k.generateRSAKeyPair();
		
		InputStream in = this.socket.getInputStream();
		OutputStream out = this.socket.getOutputStream();
		
		ClientAuthentication sa = new ClientAuthentication(s, k);
		
		if (this.protocal == PROTOCAL.NOPROTOCAL) {
			return sa.NOPROTOCOL(in, out);
		} else if (this.protocal == PROTOCAL.T2) {
			return sa.T2(in, out);
		} else if (this.protocal == PROTOCAL.T3) {
			return sa.T3(in, out);
		} else if (this.protocal == PROTOCAL.T4) {
			return sa.T4(in, out);
		} else if (this.protocal == PROTOCAL.T5) {
			return sa.T5(in, out);
		}
		
		return false;
	}
	
	@Override
	public void render(float delta) {
		//Clear the screen
		Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        batch.begin();
		
		try {
			String in = inChannel.readLine();
			numOfPlayerJoined = Integer.parseInt(in);
		} catch (IOException e) {
			System.err.println("Connection Error");
			returnMain();
		}
		
		String printString = "Number of player joined: " + numOfPlayerJoined;
		
		batch.draw(titleTexture, resizeFactor*(400-TITLE_WIDTH/2), resizeFactor*320, TITLE_WIDTH, TITLE_HEIGHT);
		font.draw(batch, printString, 250, 250);
		
		batch.end();
		if(numOfPlayerJoined == 4) {
			ScreenManager.getInstance().show(Screen.GAME, this);
		}
	}

	public int getNumOfPlayerJoined() {
		return numOfPlayerJoined;
	}
	public Socket getSocket() {
		return socket;
	}
	
	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		
	}

	@Override
	public void hide() {
		ScreenManager.getInstance().dispose(Screen.WAIT);
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
		font.dispose();
	}
	
	public void returnMain(){
		Gdx.app.postRunnable(new Runnable() {
	         public void run() {
	        	 ScreenManager.getInstance().show(Screen.MAIN_MENU, WaitScreen.this);
	         }
		});
	}

}
