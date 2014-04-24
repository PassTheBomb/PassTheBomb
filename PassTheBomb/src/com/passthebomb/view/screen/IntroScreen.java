package com.passthebomb.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;
import com.passthebomb.controller.ScreenManager;
import com.passthebomb.controller.ScreenSwitchTask;
import com.passthebomb.model.local.Screen;

public class IntroScreen implements com.badlogic.gdx.Screen{
	
	private final float TITLE_WIDTH = 256;
	private final float TITLE_HEIGHT = 64;
	
	private SpriteBatch batch = null;
	private Texture titleTexture;
	private float resizeFactor;
	
	public IntroScreen(com.badlogic.gdx.Screen lastScreen) {
		batch = new SpriteBatch();
		titleTexture = new Texture(Gdx.files.internal("title.png"));
		resizeFactor = Gdx.graphics.getWidth()/800;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(titleTexture, resizeFactor*(-TITLE_WIDTH/2), resizeFactor*(-TITLE_HEIGHT/2), resizeFactor*TITLE_WIDTH, resizeFactor*TITLE_HEIGHT);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		batch.getProjectionMatrix().setToOrtho2D(-width/2, -height/2, width, height);
	}


	@Override
	public void show() {
		/* schedule to show main menu screen after 2 seconds */
		Timer.schedule(new ScreenSwitchTask(Screen.MAIN_MENU), 1f);
	}

	@Override
	public void hide() {
		/* dispose intro screen because it won't be needed anymore */
		ScreenManager.getInstance().dispose(Screen.INTRO);
	}

	@Override
	public void pause() {
		this.dispose();
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

}