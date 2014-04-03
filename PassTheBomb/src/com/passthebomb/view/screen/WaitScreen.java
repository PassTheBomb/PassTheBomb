package com.passthebomb.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.passthebomb.controller.ScreenManager;
import com.passthebomb.model.local.Screen;

public class WaitScreen implements com.badlogic.gdx.Screen{

	private SpriteBatch batch;
	private BitmapFont font;
	private Screen lastScreen;
	private MainMenuScreen screen;
	private LobbyListScreen lobby;
	
	public WaitScreen(com.badlogic.gdx.Screen lastScreen) {
		batch = new SpriteBatch();    
        font = new BitmapFont();
        font.setColor(Color.RED);
        if(lastScreen.getClass() == MainMenuScreen.class) {
        	this.lastScreen = Screen.MAIN_MENU;
        	this.screen = (MainMenuScreen)lastScreen;
        } else {
        	this.lastScreen = Screen.LOBBY;
        	this.lobby = (LobbyListScreen)lastScreen;
        }
	}
	
	@Override
	public void render(float delta) {
		//Clear the screen
		Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
		batch.begin();
		if(this.screen != null) {
			font.draw(batch, this.screen.str, 250, 300);
		} else {
			font.draw(batch, this.lobby.str, 250, 300);
		}
		
		batch.end();
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
