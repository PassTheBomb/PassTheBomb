package com.passthebomb.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.passthebomb.controller.ScreenManager;
import com.passthebomb.model.local.PROTOCAL;
import com.passthebomb.model.local.Screen;

public class ProtocalScreen implements com.badlogic.gdx.Screen{

	private Stage stage;
	private TextButton btnServer;
	private TextButton btnT2;
	private TextButton btnT3;
	private TextButton btnT4;
	private TextButton btnT5;
	public String str = "Lobby";
	public PROTOCAL chosedProtocal;
	public String ip;
	
	private float resizeFactor;
	
	public ProtocalScreen(com.badlogic.gdx.Screen lastScreen) {
		ip = ((MainMenuScreen)lastScreen).getIP();
		
		this.stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		Skin skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"));
		
		resizeFactor = Gdx.graphics.getWidth()/800;
		
		this.btnServer = new TextButton("No Protocal", skin);
		this.btnServer.setBounds(resizeFactor*250, resizeFactor*390, resizeFactor*300, resizeFactor*60);
		this.btnServer.addListener(new ClickListener() {
			public void touchUp(InputEvent e, float x, float y, int point, int button) {
				chosedProtocal = PROTOCAL.NOPROTOCAL;
				ScreenManager.getInstance().show(Screen.WAIT, ProtocalScreen.this);
			}
		});
		this.stage.addActor(this.btnServer);
		
		this.btnT2 = new TextButton("T2", skin);
		this.btnT2.setBounds(resizeFactor*250, resizeFactor*300, resizeFactor*300, resizeFactor*60);
		this.btnT2.addListener(new ClickListener() {
			public void touchUp(InputEvent e, float x, float y, int point, int button) {
				chosedProtocal = PROTOCAL.T2;
				ScreenManager.getInstance().show(Screen.WAIT, ProtocalScreen.this);
			}
		});
		this.stage.addActor(this.btnT2);
		
		this.btnT3 = new TextButton("T3", skin);
		this.btnT3.setBounds(resizeFactor*250, resizeFactor*210, resizeFactor*300, resizeFactor*60);
		this.btnT3.addListener(new ClickListener() {
			public void touchUp(InputEvent e, float x, float y, int point, int button) {
				chosedProtocal = PROTOCAL.T3;
				ScreenManager.getInstance().show(Screen.WAIT, ProtocalScreen.this);
			}
		});
		this.stage.addActor(this.btnT3);
		
		this.btnT4 = new TextButton("T4", skin);
		this.btnT4.setBounds(resizeFactor*250, resizeFactor*120, resizeFactor*300, resizeFactor*60);
		this.btnT4.addListener(new ClickListener() {
			public void touchUp(InputEvent e, float x, float y, int point, int button) {
				chosedProtocal = PROTOCAL.T4;
				ScreenManager.getInstance().show(Screen.WAIT, ProtocalScreen.this);
			}
		});
		this.stage.addActor(this.btnT4);
		
		this.btnT5 = new TextButton("T5", skin);
		this.btnT5.setBounds(resizeFactor*250, resizeFactor*30, resizeFactor*300, resizeFactor*60);
		this.btnT5.addListener(new ClickListener() {
			public void touchUp(InputEvent e, float x, float y, int point, int button) {
				chosedProtocal = PROTOCAL.T5;
				ScreenManager.getInstance().show(Screen.WAIT, ProtocalScreen.this);
			}
		});
		this.stage.addActor(this.btnT5);
		
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		ScreenManager.getInstance().dispose(Screen.PROTOCAL);
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
		
	}

}
