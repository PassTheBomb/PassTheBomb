package com.passthebomb.view.screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.passthebomb.controller.ScreenManager;
import com.passthebomb.model.local.Screen;

public class WaitScreen implements com.badlogic.gdx.Screen{

	final String HOST = "192.168.82.9";
	final int PORT = 5432;
	
	private SpriteBatch batch;
	private BitmapFont font;
	private Socket socket;
	private BufferedReader inChannel;
	private int numOfPlayerJoined;
	
	public WaitScreen(com.badlogic.gdx.Screen lastScreen) {
		batch = new SpriteBatch();    
        font = new BitmapFont();
        font.setColor(Color.RED);
        numOfPlayerJoined = 1;
        try {
			this.socket = new Socket(HOST, PORT);
			inChannel = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }	
	@Override
	public void render(float delta) {
		//Clear the screen
		Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
		batch.begin();
		
		try {
			if(inChannel.ready()) {
				numOfPlayerJoined = Integer.parseInt(inChannel.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		font.draw(batch, String.valueOf(numOfPlayerJoined), 250, 250);
		if(numOfPlayerJoined == 4) {
			ScreenManager.getInstance().show(Screen.GAME, this);
		}
		batch.end();
	}

	public int getNumOfPlayerJoined() {
		return numOfPlayerJoined;
	}
	public Socket getSocket() {
		return socket;
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		ScreenManager.getInstance().dispose(Screen.WAIT);
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
		// TODO Auto-generated method stub
		batch.dispose();
		font.dispose();
	}

}
