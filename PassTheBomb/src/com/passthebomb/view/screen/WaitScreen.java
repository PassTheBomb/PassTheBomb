package com.passthebomb.view.screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.passthebomb.controller.ScreenManager;
import com.passthebomb.model.local.Screen;

public class WaitScreen implements com.badlogic.gdx.Screen{

	final String HOST = "localhost";
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
	
	public WaitScreen(com.badlogic.gdx.Screen lastScreen) {
		batch = new SpriteBatch();    
        font = new BitmapFont();
        font.setColor(Color.RED);
        numOfPlayerJoined = 1;
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
        this.titleTexture = new Texture(Gdx.files.internal("title.png"));
        resizeFactor = Gdx.graphics.getWidth()/800;
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
