<<<<<<< HEAD:PassTheBomb/src/com/me/passthebomb/Opponent.java
package com.me.passthebomb;
=======
package com.passthebomb.model.local;
>>>>>>> Setup-structure:PassTheBomb/src/com/passthebomb/model/local/Opponent.java

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.passthebomb.view.gui.Background;

public class Opponent extends Character {
	private final float VEL = 50;
	private final float RADIUS = 32;
	private final double DELTA_TIME = 0.17;
	private Vector3 tgtPos, tgtPosCpy;
	private int collideCtr = 0;
	private boolean collide;
	
<<<<<<< HEAD:PassTheBomb/src/com/me/passthebomb/Opponent.java
	protected Opponent(Vector2 absStartPos, Texture[] imgSet, boolean bomb, Background bg) {
		super(absStartPos, imgSet, bomb, bg);
=======
	public Opponent(Vector2 absStartPos, Texture img, boolean bomb, Background bg) {
		super(absStartPos, img, bomb, bg);
>>>>>>> Setup-structure:PassTheBomb/src/com/passthebomb/model/local/Opponent.java
		tgtPos = new Vector3(absStartPos.x+bg.getBackgroundPos().x, absStartPos.y+bg.getBackgroundPos().y,0);
		this.collide=false;
	}

	@Override
	public void update() {
		getCharBox().x = getAbsPos().x + getBg().getBackgroundPos().x;
		getCharBox().y = getAbsPos().y + getBg().getBackgroundPos().y;
		if(collide){
			tgtPosCpy = tgtPos.cpy();
			getCharBox().x += tgtPosCpy.x*DELTA_TIME;
			getCharBox().y += tgtPosCpy.y*DELTA_TIME;
			System.out.println("COLLIDE");
			collideCtr++;
			if (collideCtr > 2){
				collideCtr = 0;
				collide = false;
				getAbsPos().x = getCharBox().x - getBg().getBackgroundPos().x;
				getAbsPos().y = getCharBox().y - getBg().getBackgroundPos().y;
			}
			if (getAbsPos().x - RADIUS < 100){
				getAbsPos().x = 100 + RADIUS;
			}

			else if (getAbsPos().x + RADIUS > 1900){
				getAbsPos().x = 1900 - RADIUS;
			}
			if (getAbsPos().y - RADIUS < 100){
				getAbsPos().y = 100 + RADIUS;
			}

			else if (getAbsPos().y + RADIUS > 1900){
				getAbsPos().y = 1900 - RADIUS;
			}
		}
		
	}

	@Override
	protected void move(Vector3 tgtPos) {
		setAbsPos(tgtPos.x,tgtPos.y);
		
	}

	@Override
	public void collide(Character c) {
		Circle thisHitBox = getCharBox();
		Circle otherHitBox = c.getCharBox();
		if(thisHitBox.overlaps(otherHitBox)){
			collide = true;
			tgtPos = new Vector3((thisHitBox.x - otherHitBox.x),(thisHitBox.y - otherHitBox.y), 0);
			tgtPos.scl(VEL/tgtPos.len());
			collideCtr = 0;
		}
		
	}
	
}