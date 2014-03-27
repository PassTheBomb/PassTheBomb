<<<<<<< HEAD:PassTheBomb/src/com/me/passthebomb/PassTheBomb.java
package com.me.passthebomb;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PassTheBomb implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Vector3 touchPos;
	private Background bg;
	private Player player1;
	private Opponent player2;
	private Opponent player3;
	private Opponent player4;
	
	@Override
	public void create() {		
		// create new camera
		camera = new OrthographicCamera();
		// set camera to orthographic, size same as screen, y axis pointing up
		camera.setToOrtho(false,800,480);

		batch = new SpriteBatch();
		bg = Background.createBG(new Texture(Gdx.files.internal("background.jpg")), new Vector2(-624,-804));
		Texture[] player1Texture = new Texture[2];
		player1Texture[0] = new Texture(Gdx.files.internal("circle_r.png"));
		player1Texture[1] = new Texture(Gdx.files.internal("circle_r_bomb.png"));	
		Texture[] player2Texture = new Texture[2];
		player2Texture[0] = new Texture(Gdx.files.internal("circle_b.png"));
		player2Texture[1] = new Texture(Gdx.files.internal("circle_b_bomb.png"));	
		Texture[] player3Texture = new Texture[2];
		player3Texture[0] = new Texture(Gdx.files.internal("circle_b.png"));
		player3Texture[1] = new Texture(Gdx.files.internal("circle_b_bomb.png"));	
		Texture[] player4Texture = new Texture[2];
		player4Texture[0] = new Texture(Gdx.files.internal("circle_b.png"));
		player4Texture[1] = new Texture(Gdx.files.internal("circle_b_bomb.png"));
		player1 = new Player(new Vector2(774, 1044), player1Texture, true, bg);
		player2 = new Opponent(new Vector2(1274, 1044), player2Texture, false, bg);
		player3 = new Opponent(new Vector2(1274, 1244), player3Texture, true, bg);
		player4 = new Opponent(new Vector2(1274, 844), player4Texture, false, bg);

	}

	@Override
	public void dispose() {
		batch.dispose();
		player1.dispose();
		player2.dispose();
		player3.dispose();
		player4.dispose();
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
		batch.draw(player3.getCharImg(), player3.getCharImgX(), player3.getCharImgY());
		batch.draw(player4.getCharImg(), player4.getCharImgX(), player4.getCharImgY());
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
		player1.collide(player3);
		player1.collide(player4);
		player2.collide(player1);
		player3.collide(player1);
		player4.collide(player1);
		player1.update();
		player2.update();
		player3.update();
		player4.update();
	}


	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
=======
package com.passthebomb.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.passthebomb.model.local.Opponent;
import com.passthebomb.model.local.Player;
import com.passthebomb.view.gui.Background;

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
>>>>>>> Setup-structure:PassTheBomb/src/com/passthebomb/view/screen/GameScreen.java
