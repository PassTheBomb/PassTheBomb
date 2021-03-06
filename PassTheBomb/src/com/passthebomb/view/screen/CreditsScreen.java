package com.passthebomb.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.passthebomb.controller.ScreenManager;
import com.passthebomb.model.local.Screen;

public class CreditsScreen implements com.badlogic.gdx.Screen{
	
	//final private String JOIN = "Join";
	//final private String EXIT = "Exit";
	
	private final float TITLE_WIDTH = 256;
	private final float TITLE_HEIGHT = 64;
	
	private SpriteBatch batch = null;
	private Texture titleTexture;
	private OrthographicCamera camera = null;	
	private Stage stage;

	private TextButton btnExit;
	private float resizeFactor;
	public String str = "Main";
	
	private boolean amIWin = false;
	private GameScreen lastScreen;
	/**
	 * Initializer
	 */
	public CreditsScreen(com.badlogic.gdx.Screen lastScreen) {
		
		this.lastScreen = (GameScreen)lastScreen;
		this.amIWin = this.lastScreen.isAmIWin();
		
		this.batch = new SpriteBatch();
		
		this.titleTexture = new Texture(Gdx.files.internal("title.png"));
		
		this.stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		Skin skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"));
	  
	    resizeFactor = Gdx.graphics.getWidth()/800;
	    
	    String buttonString;
	    if (amIWin) {
			buttonString = "You WIN!";
		} else{
			buttonString = "You LOSE";
		}
		
		this.btnExit = new TextButton(buttonString, skin);
		this.btnExit.setBounds(resizeFactor*250, resizeFactor*100, resizeFactor*300, resizeFactor*60);
		this.btnExit.addListener(new ClickListener() {
			public void touchUp(InputEvent e, float x, float y, int point, int button) {
				ScreenManager.getInstance().show(Screen.MAIN_MENU, CreditsScreen.this);
			}
		});
		this.stage.addActor(this.btnExit);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		batch.draw(titleTexture, resizeFactor*(400-TITLE_WIDTH/2), resizeFactor*320, TITLE_WIDTH, TITLE_HEIGHT);
		batch.end();
		
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
		ScreenManager.getInstance().dispose(Screen.CREDITS);
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
	}

}
