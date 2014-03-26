package com.passthebomb.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.passthebomb.controller.ScreenSwitchHandler;
import com.passthebomb.model.Screen;
import com.passthebomb.view.gui.Button;
import com.passthebomb.view.gui.Button.ButtonHandler;
import com.passthebomb.view.gui.Label;

public class MainMenuScreen implements com.badlogic.gdx.Screen{
	
	private static String HEAD_LABEL = "Pass the Bomb";
	private static String HOST_BUTTON_LABEL = "Host";
	private static String JOIN_BUTTON_LABEL = "Join";
	private static String EXIT_BUTTON_LABEL = "Exit";
	
	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private Label headingLabel = null;
	private Button hostButton = null;
	private Button joinButton = null;
	private Button exitButton = null;
	private int lineHeight = 0;
	
	/**
	 * Initializer
	 */
	public MainMenuScreen() {
		this.batch = new SpriteBatch();
		this.font = new BitmapFont();
		this.lineHeight = Math.round(2.5f * font.getCapHeight());
		this.headingLabel = new Label(HEAD_LABEL, this.font);
		this.hostButton = new Button(HOST_BUTTON_LABEL, this.font, new ScreenSwitchHandler(Screen.GAME));
		this.joinButton = new Button(JOIN_BUTTON_LABEL, this.font, new ScreenSwitchHandler(Screen.LOBBY));
		this.exitButton = new Button(EXIT_BUTTON_LABEL, this.font, new ButtonHandler() {
			
			@Override
			public void onClick() {
				Gdx.app.exit();
				
			}
		});
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		headingLabel.draw(batch);
		hostButton.draw(batch, camera);
		joinButton.draw(batch, camera);
		exitButton.draw(batch, camera);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, width, height);
		batch.setProjectionMatrix(camera.combined);
		int centerX = width / 2;
		int centerY = height / 2;
		headingLabel.setX(centerX - headingLabel.getWidth() / 2);
		headingLabel.setY(centerY + 2 * lineHeight);
		hostButton.setX(centerX - hostButton.getWidth() / 2);
		hostButton.setY(centerY + lineHeight);
		joinButton.setX(centerX - joinButton.getWidth() / 2);
		joinButton.setY(centerY);
		exitButton.setX(centerX - exitButton.getWidth() / 2);
		exitButton.setY(centerY - lineHeight); 
		
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
		font.dispose();
		batch.dispose();	
	}

}
