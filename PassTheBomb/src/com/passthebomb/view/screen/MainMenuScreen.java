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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.passthebomb.controller.ScreenManager;
import com.passthebomb.model.local.Screen;

public class MainMenuScreen implements com.badlogic.gdx.Screen{
	
	final private String JOIN = "Join";
	final private String EXIT = "Exit";
	
	private final float TITLE_WIDTH = 256;
	private final float TITLE_HEIGHT = 64;
	
	private SpriteBatch batch = null;
	private Texture titleTexture;
	private OrthographicCamera camera = null;	
	private Stage stage;
	private TextButton btnJoin;
	private TextButton btnExit;
	private TextField textField;
	private float resizeFactor;
	private float resizeHightFactor;
	
	public String str = "Main";
	
	/**
	 * Initializer
	 */
	public MainMenuScreen(com.badlogic.gdx.Screen lastScreen) {
		
		this.batch = new SpriteBatch();
		
		this.titleTexture = new Texture(Gdx.files.internal("title.png"));
		
		this.stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		Skin skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"));
	  
	    resizeFactor = Gdx.graphics.getWidth()/800;
	    resizeHightFactor = Gdx.graphics.getHeight()/480;
		
		this.btnJoin = new TextButton(JOIN, skin);
		this.btnJoin.setBounds((Gdx.graphics.getWidth()-resizeFactor*300)/2, resizeHightFactor*120, resizeFactor*300, resizeFactor*60);
		this.btnJoin.addListener(new ClickListener() {
			public void touchUp(InputEvent e, float x, float y, int point, int button) {
				
				ScreenManager.getInstance().show(Screen.PROTOCAL, MainMenuScreen.this);
			}
		});
		this.stage.addActor(this.btnJoin);
		
		this.btnExit = new TextButton(EXIT, skin);
		this.btnExit.setBounds((Gdx.graphics.getWidth()-resizeFactor*300)/2, resizeHightFactor*40, resizeFactor*300, resizeFactor*60);
		this.btnExit.addListener(new ClickListener() {
			public void touchUp(InputEvent e, float x, float y, int point, int button) {
				Gdx.app.exit();
			}
		});
		this.stage.addActor(this.btnExit);
		
		this.textField = new TextField("", skin);
		this.textField.setBounds((Gdx.graphics.getWidth()-resizeFactor*300)/2, resizeHightFactor*340, resizeFactor*300, resizeFactor*60);
		this.textField.setMessageText("Input host IP");
		this.stage.addActor(textField);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		batch.draw(titleTexture, (Gdx.graphics.getWidth()-resizeFactor*TITLE_WIDTH)/2, resizeHightFactor*220, resizeFactor*TITLE_WIDTH, resizeFactor*TITLE_HEIGHT);
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
	}

	@Override
	public void hide() {
		ScreenManager.getInstance().dispose(Screen.MAIN_MENU);
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
	
	public String getIP(){
		return textField.getText();
	}

}
