package com.passthebomb.model.local;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.passthebomb.view.gui.Background;

/**
 * The Player is a controllable character controllable by the device owner.
 * 
 */
public class Player extends Character {
	private final int VEL = 30;
	private final double DELTA_TIME = 0.17;
	private Vector3 tgtPos, tgtPosCpy;

	private boolean isMoving;
	private boolean collide;

	private int collideCtr;
	private int bombPassCtr;
	
	private long passTime = 0;
	private boolean passTimeSet = false;
	/**
	 * Creates a player.
	 * 
	 * @param absStartPos
	 *            The player's starting position.
	 * @param playerTexture
	 *            The player's image set, consisting of one without a bomb and
	 *            one with a bomb
	 * @param bomb
	 *            Is the player carrying a bomb?
	 * @param bg
	 *            The background the player is using
	 */
	public Player(Vector2 absStartPos, Sprite[] playerTexture, boolean bomb,
			Background bg) {
		super(absStartPos, playerTexture, bomb, bg);
		isMoving = false;
		collide = false;
		collideCtr = 0;
		bombPassCtr = 0;
		tgtPos = new Vector3(0, 0, 0);

	}

	@Override
	public void update() {
		if (isAlive()) {
			if (isMoving) {
				if (getBombPass()) {
					bombPassCtr++;
					if (bombPassCtr > 12) {
						resetBombPass();
						bombPassCtr = 0;
					}
				} else {
					tgtPosCpy = tgtPos.cpy();
					getCharBox().x += tgtPosCpy.x;// * DELTA_TIME;
					getCharBox().y += tgtPosCpy.y;// * DELTA_TIME;
					/*
					 * tgtPosCpy.add(new
					 * Vector3(-getCharBox().x,-getCharBox().y,0)); if
					 * (tgtPosCpy.len()> VEL){
					 * tgtPosCpy.scl(VEL/tgtPosCpy.len()); getCharBox().x +=
					 * tgtPosCpy.x * DELTA_TIME; getCharBox().y += tgtPosCpy.y *
					 * DELTA_TIME; System.out.println("RUN"); } else if
					 * (tgtPosCpy.len()> 0.1){ getCharBox().x +=
					 * tgtPosCpy.x*DELTA_TIME; getCharBox().y +=
					 * tgtPosCpy.y*DELTA_TIME; System.out.println("SLOW"); }
					 * else{ isMoving = false; System.out.println("STOP"); }
					 */
				}

			}
			if (collide) {
				tgtPosCpy = tgtPos.cpy();
				getCharBox().x += tgtPosCpy.x * DELTA_TIME;
				getCharBox().y += tgtPosCpy.y * DELTA_TIME;
				collideCtr++;
				if (collideCtr > 2) {
					collideCtr = 0;
					collide = false;
				}
			}
			if (getBombPass() && !passTimeSet){
				passTime = System.currentTimeMillis();
				passTimeSet = true;
			}
			else if (getBombPass() && passTimeSet){
				if (System.currentTimeMillis() - passTime > 1000){
					passTimeSet = false;
					resetBombPass();
				}
			}
			checkLimits();
		}

	}

	@Override
	public void move(Vector3 tgtPos) {
		if (!collide) {
			this.tgtPos = tgtPos;
			this.isMoving = true;
		}

	}

	@Override
	public boolean collide(Character c) {
		Circle thisHitBox = getCharBox();
		Circle otherHitBox = c.getCharBox();
		if (thisHitBox.overlaps(otherHitBox)) {
			collide = true;
			isMoving = false;
			tgtPos = new Vector3((thisHitBox.x - otherHitBox.x),
					(thisHitBox.y - otherHitBox.y), 0);
			tgtPos.scl(VEL / tgtPos.len());
			collideCtr = 0;
			return true;
		} else {
			return false;
		}

	}

	/*
	 * private void scrollBG(){ float diffX = 0; float diffY = 0; Circle charBox
	 * = super.getCharBox(); if(charBox.x<BOUND_OFFSET+RADIUS){ diffX =
	 * BOUND_OFFSET - charBox.x + RADIUS; charBox.x = BOUND_OFFSET+RADIUS;
	 * 
	 * } else if(charBox.x>800 - BOUND_OFFSET - RADIUS){ diffX = 800 -
	 * BOUND_OFFSET - charBox.x - RADIUS; charBox.x = 800-BOUND_OFFSET-RADIUS; }
	 * if(charBox.y<BOUND_OFFSET+RADIUS){ diffY = BOUND_OFFSET - charBox.y +
	 * RADIUS; charBox.y = BOUND_OFFSET+RADIUS; } else if
	 * (charBox.y>480-BOUND_OFFSET-RADIUS){ diffY = 480 - BOUND_OFFSET -
	 * charBox.y - RADIUS; charBox.y = 480 - BOUND_OFFSET-RADIUS; }
	 * getBg().scrollX(diffX); getBg().scrollY(diffY); tgtPos.x+=diffX;
	 * tgtPos.y+=diffY; }
	 */

}