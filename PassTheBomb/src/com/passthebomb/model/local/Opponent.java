package com.passthebomb.model.local;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.passthebomb.view.gui.Background;

/**
 * The opponent is a playable character that is not controlled by the device
 * owner, but controlled by others over the network
 * 
 */
public class Opponent extends Character {

	/**
	 * Creates an opponent.
	 * 
	 * @param absStartPos
	 *            The opponent starting position.
	 * @param oppTexture
	 *            The opponent image set consisting of one without a bomb and
	 *            one with a bomb.
	 * @param bomb
	 *            Is the opponent carrying a bomb?
	 * @param bg
	 *            The background the opponent is using.
	 */
	public Opponent(Vector2 absStartPos, Sprite[] oppTexture, boolean bomb,
			Background bg) {
		super(absStartPos, oppTexture, bomb, bg);
	}

	@Override
	public void update() {
	}

	@Override
	public void move(Vector3 tgtPos) {
		getCharBox().x = tgtPos.x;
		getCharBox().y = tgtPos.y;

	}

	@Override
	protected boolean collide(Character c) {
		return false;
	}

}