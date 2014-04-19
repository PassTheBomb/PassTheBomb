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
import com.passthebomb.security.MsgHandler;
import com.passthebomb.security.Security;

public class WaitScreen implements com.badlogic.gdx.Screen{

	final String HOST = "172.16.31.45";
	final int PORT = 5432;
	
	private final float TITLE_WIDTH = 256;
	private final float TITLE_HEIGHT = 64;
	
	private SpriteBatch batch = null;
	private Texture titleTexture;
	private BitmapFont font;
	private Socket socket;
	private int numOfPlayerJoined;
	private float resizeFactor;
	private ProtocalScreen lastScreen;
	private PROTOCAL protocal;
	
	public WaitScreen(com.badlogic.gdx.Screen lastScreen) {
		System.out.println("WaitScreen");
		this.lastScreen = (ProtocalScreen)lastScreen;
		this.protocal =	this.lastScreen.chosedProtocal;
		batch = new SpriteBatch();    
        font = new BitmapFont();
        font.setColor(Color.RED);
        numOfPlayerJoined = 1;
        this.startSession();
        System.out.println("Session started");
        try {
        	boolean result = this.verificaiton();
        	if (!result) {
        		System.out.println("Verification failed");
				this.socket.close();
				this.returnMain();
			}
		} catch (IOException e) {
			System.err.println("Can not get input and output stream");
			this.returnMain();
		}
        System.out.println("Verification passed");
        this.titleTexture = new Texture(Gdx.files.internal("title.png"));
        resizeFactor = Gdx.graphics.getWidth()/800;
    }
	
	private void startSession() {
		try {
			System.out.println("Connecting");
			this.socket = new Socket(HOST, PORT);
		} catch (UnknownHostException e) {
			System.err.println("Cannot Find Server");
			this.returnMain();
			
		} catch (IOException e) {
			System.err.println("Cannot Establish Connection");
			this.returnMain();
		}
	}
	
	private boolean verificaiton() throws IOException {
		Security s = new Security();
		Keys k = new Keys();
		k.generateRSAKeyPair();
		
		InputStream in = this.socket.getInputStream();
		OutputStream out = this.socket.getOutputStream();
		
		ClientAuthentication sa = new ClientAuthentication(s, k);
		
		if (this.protocal == PROTOCAL.NOPROTOCAL) {
			return true;
		} else if (this.protocal == PROTOCAL.T2) {
			return sa.T2(in, out);
		} else if (this.protocal == PROTOCAL.T3) {
			return sa.T2(in, out);
		} else if (this.protocal == PROTOCAL.T4) {
			return sa.T2(in, out);
		} else if (this.protocal == PROTOCAL.T5) {
			return sa.T2(in, out);
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
			String in = MsgHandler.acquireNetworkMsg(this.socket.getInputStream()).toString();
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
		ScreenManager.getInstance().show(Screen.MAIN_MENU, this);
	}

}
