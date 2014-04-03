package com.passthebomb.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.passthebomb.controller.ScreenManager;
import com.passthebomb.model.local.Screen;

public class IntroScreen implements com.badlogic.gdx.Screen{

	private static final String PASS = "Pass The BOMB";
	
	private SpriteBatch batch = null;
	private BitmapFont font = null; 
	private float captionX = 0;
	private float captionY = 0;
	
	public IntroScreen() {
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("data/ui/font.fnt"),Gdx.files.internal("data/ui/font.png"),false);
		TextBounds wholeCaptionBounds = font.getBounds(PASS);
		captionX = -wholeCaptionBounds.width/2;
		captionY = wholeCaptionBounds.height/2;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		font.setColor(0f, 0f, 0f, 1f);
		font.scale(2);
		font.draw(batch, PASS, captionX, captionY);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		batch.getProjectionMatrix().setToOrtho2D(-width/2, -height/2, width, height);
	}


	@Override
	public void show() {
		/* schedule to show main menu screen after 2 seconds */
		//Timer.schedule(new ScreenSwitchTask(Screen.MAIN_MENU), 2f);
		ScreenManager.getInstance().show(Screen.MAIN_MENU);
	}

	@Override
	public void hide() {
		/* dispose intro screen because it won't be needed anymore */
		ScreenManager.getInstance().dispose(Screen.INTRO);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		font.dispose();
		batch.dispose();
	}

}