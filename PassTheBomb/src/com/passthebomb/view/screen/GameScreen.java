package com.passthebomb.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.passthebomb.model.Background;
import com.passthebomb.model.Opponent;
import com.passthebomb.model.Player;

public class GameScreen implements Screen {
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Vector3 touchPos;
	private Background bg;
	private Player player1;
	private Opponent player2;
	
	public GameScreen() {
		batch = new SpriteBatch();
		bg = Background.createBG(new Texture(Gdx.files.internal("background.jpg")), new Vector2(-624,-804));
		player1 = new Player(new Vector2(774, 1044),new Texture(Gdx.files.internal("circle_r.png")), false, bg);
		player2 = new Opponent(new Vector2(1274, 1044),new Texture(Gdx.files.internal("circle_r.png")), false, bg);
	}

	@Override
	public void render(float delta) {
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
		batch.dispose();
		player1.dispose();
		player2.dispose();
		bg.dispose();
	}

}
