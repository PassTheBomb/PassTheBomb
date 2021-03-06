package com.passthebomb;

import com.badlogic.gdx.Game;
/*import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.passthebomb.model.Background;
import com.passthebomb.model.Opponent;
import com.passthebomb.model.Player;*/
import com.passthebomb.controller.ScreenManager;
import com.passthebomb.model.local.Screen;

//public class PassTheBomb implements ApplicationListener {
public class PassTheBomb extends Game {

	@Override
	public void create() {
		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().show(Screen.INTRO, null);
	}
/*	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Vector3 touchPos;
	private Background bg;
	private Player player1;
	private Opponent player2;
	
	@Override
	public void create() {		
		// create new camera
		camera = new OrthographicCamera();
		// set camera to orthographic, size same as screen, y axis pointing up
		camera.setToOrtho(false,800,480);

		batch = new SpriteBatch();
		bg = Background.createBG(new Texture(Gdx.files.internal("background.jpg")), new Vector2(-624,-804));
		player1 = new Player(new Vector2(774, 1044),new Texture(Gdx.files.internal("circle_r.png")), false, bg);
		player2 = new Opponent(new Vector2(1274, 1044),new Texture(Gdx.files.internal("circle_r.png")), false, bg);

	}

	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
		player1.dispose();
		player2.dispose();
		bg.dispose();
	}

	@Override
	public void render() {
		// set the clear colour to r, g, b, a
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		// clear screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// update camera
		camera.update();
		// set SpriteBatch to camera coordinate system
		batch.setProjectionMatrix(camera.combined);
		// start new batch
		batch.begin();
		// draw in the new batch
		batch.draw(bg.getBackgroundImg(), bg.getBackgroundPos().x, bg.getBackgroundPos().y);
		batch.draw(player1.getCharImg(), player1.getCharImgX(), player1.getCharImgY());
		batch.draw(player2.getCharImg(), player2.getCharImgX(), player2.getCharImgY());
		// end batch. **Note, all image rendering updates should go between
		// begin and end
		batch.end();
		// acquire touch input
		if (Gdx.input.isTouched()) {
			// acquire touch position
			touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			// change position from global coordinates to camera coordinates
			camera.unproject(touchPos);
			player1.move(touchPos);
			// update the cicle position
			
		}
		player1.collide(player2);
		player2.collide(player1);
		player1.update();
		player2.update();
	}


	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}*/
}
