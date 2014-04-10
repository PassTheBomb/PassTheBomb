package com.passthebomb.model.local;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.passthebomb.view.gui.Background;

/**
 * The Character abstract class. Contains attributes and abstract methods
 * related to a character. The hit-box is a hitBox.
 * 
 */
public abstract class Character {
	private final int RADIUS = 32;
	private final int BOUND_OFFSET = 80;

	private Circle hitBox;
	private Texture[] imgSet;
	private Background bg;

	private boolean carryingBomb;
	private boolean bombPass;
	private boolean alive;

	/**
	 * Creates a character.
	 * 
	 * @param absStartPos
	 *            The starting position of the character. Reference point is at
	 *            the centre of the character circle.
	 * @param imgSet
	 *            The image set of the character, without and with the bomb
	 * @param bomb
	 *            Is the character holding the bomb?
	 * @param bg
	 *            The background that the character will be playing on
	 */
	protected Character(Vector2 absStartPos, Texture[] imgSet, boolean bomb,
			Background bg) {
		this.hitBox = new Circle(absStartPos, RADIUS);
		this.imgSet = imgSet;
		this.carryingBomb = bomb;
		this.bg = bg;
		this.alive = true;
		this.bombPass = false;
	}

	/**
	 * Refreshes the character. To be called if the character changes
	 * 
	 */
	protected abstract void update();

	/**
	 * Moves the character. Movement type depends on implementation
	 * 
	 * @param tgtPos
	 *            the movement input
	 */
	protected abstract void move(Vector3 tgtPos);

	/**
	 * Method for collision check. Depends on implementation.
	 * 
	 * @param c
	 *            another character
	 * @return did this player and the other character collide?
	 */
	protected abstract boolean collide(Character c);
	

	/**
	 * Checks if the character has exceeded background limits, and places it
	 * back inside the limits if exceeded.
	 * 
	 */
	public void checkLimits() {
		if (hitBox.x < BOUND_OFFSET) {
			hitBox.x = BOUND_OFFSET;
		} else if (hitBox.x > bg.getLimits()[3] - BOUND_OFFSET) {
			hitBox.x = bg.getLimits()[3] - BOUND_OFFSET;
		}
		if (hitBox.y < BOUND_OFFSET) {
			hitBox.y = BOUND_OFFSET;
		} else if (hitBox.y > bg.getLimits()[0] - BOUND_OFFSET) {
			hitBox.y = bg.getLimits()[0] - BOUND_OFFSET;
		}
	}

	/**
	 * Acquires the character hit-box
	 * 
	 * @return character hit-box
	 */
	protected Circle getCharBox() {
		return hitBox;
	}

	/**
	 * Acquires the character image given a current character bomb setup
	 * 
	 * @return character image corresponding to its current bomb state
	 */
	public Texture getCharImg() {
		if (carryingBomb) {
			return imgSet[1];
		} else {
			return imgSet[0];
		}
	}

	/**
	 * Acquires the character image X position. is a offset to the hit-box X
	 * position by the radius of the circle, to the left
	 * 
	 * @return x position of the character image
	 */
	public float getCharImgX() {
		return hitBox.x - RADIUS;
	}

	/**
	 * Acquires the character image Y position. is a offset to the hit-box Y
	 * position by the radius of the circle, downwards
	 * 
	 * @return y position of the character image
	 */
	public float getCharImgY() {
		return hitBox.y - RADIUS;
	}

	/**
	 * Disposes of the character images
	 * 
	 */
	public void dispose() {
		imgSet[0].dispose();
		imgSet[1].dispose();
	}

	/**
	 * Acquires the absolute position of the character, based on its hit-box
	 * position
	 * 
	 */
	public Vector2 getAbsPos() {
		return new Vector2(hitBox.x, hitBox.y);
	}

	/**
	 * Sets the absolute position of the character, changing its hit-box
	 * position
	 * 
	 */
	public void setAbsPos(Vector2 absPos) {
		hitBox.x = absPos.x;
		hitBox.y = absPos.y;
	}

	/**
	 * Sets the absolute position of the character, changing its hit-box
	 * position
	 * 
	 */
	public void setAbsPos(float x, float y) {
		hitBox.x = x;
		hitBox.y = y;
	}

	/**
	 * Acquires the background being used by this character
	 * 
	 */
	public Background getBg() {
		return bg;
	}

	/**
	 * Sets the bomb carrying status of this character
	 * 
	 * @param bombState
	 *            the bomb carrying status it will be set to
	 */
	public void setBomb(boolean bombState) {
		if (carryingBomb != bombState) {
			bombPass = true;
		}
		carryingBomb = bombState;
	}

	/**
	 * Acquires the bomb carrying status of this character
	 * 
	 * @return this character's current bomb status
	 */
	public boolean getBombState() {
		return carryingBomb;
	}

	/**
	 * Did the player recently give away or receive a bomb?
	 * 
	 * @return whether if the character made recent changes to bomb status
	 */
	public boolean getBombPass() {
		return bombPass;
	}

	/**
	 * The bomb pass status is reset to false.
	 * 
	 */
	public void resetBombPass() {
		bombPass = false;
	}

	/**
	 * Checks if this character is alive
	 * 
	 * @return true if alive, false otherwise
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Kills the player, sets alive status to false
	 */
	public void death() {
		this.alive = false;
	}

}
