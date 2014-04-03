package com.passthebomb.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.passthebomb.controller.ScreenManager;
import com.passthebomb.model.local.Screen;

public class MainMenuScreen implements com.badlogic.gdx.Screen{
	
	final private String HOST = "Host";
	final private String JOIN = "Join";
	final private String EXIT = "Exit";
	
	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;	
	private Stage stage;
	private TextButton btnHost;
	private TextButton btnJoin;
	private TextButton btnExit;
	
	/**
	 * Initializer
	 */
	public MainMenuScreen() {
		this.batch = new SpriteBatch();
		
		this.stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		Skin skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"));
		
		this.btnHost = new TextButton(HOST, skin);
		this.btnHost.setBounds(250, 300, 300, 60);
		this.btnHost.addListener(new ClickListener() {
			public void touchUp(InputEvent e, float x, float y, int point, int button) {
				ScreenManager.getInstance().show(Screen.WAIT);
			}
		});
		this.stage.addActor(this.btnHost);
		
		this.btnJoin = new TextButton(JOIN, skin);
		this.btnJoin.setBounds(250, 210, 300, 60);
		this.btnJoin.addListener(new ClickListener() {
			public void touchUp(InputEvent e, float x, float y, int point, int button) {
				ScreenManager.getInstance().show(Screen.GAME);
			}
		});
		this.stage.addActor(this.btnJoin);
		
		this.btnExit = new TextButton(EXIT, skin);
		this.btnExit.setBounds(250, 120, 300, 60);
		this.btnExit.addListener(new ClickListener() {
			public void touchUp(InputEvent e, float x, float y, int point, int button) {
				Gdx.app.exit();
			}
		});
		this.stage.addActor(this.btnExit);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, width, height);
		batch.setProjectionMatrix(camera.combined);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		ScreenManager.getInstance().dispose(Screen.MAIN_MENU);
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
		batch.dispose();	
	}

}
