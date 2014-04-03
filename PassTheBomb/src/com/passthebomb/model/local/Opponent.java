package com.passthebomb.model.local;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.passthebomb.view.gui.Background;

public class Opponent extends Character {
	/*
	private final float VEL = 50;
	private final float RADIUS = 32;
	private final double DELTA_TIME = 0.17;
	private Vector3 tgtPos, tgtPosCpy;
	private int collideCtr = 0;
	private boolean collide;*/
	
	public Opponent(Vector2 absStartPos, Texture[] imgSet, boolean bomb, Background bg) {
		super(absStartPos, imgSet, bomb, bg);
		/*
		tgtPos = new Vector3(absStartPos.x+bg.getBackgroundPos().x, absStartPos.y+bg.getBackgroundPos().y,0);
		this.collide=false;*/
	}

	@Override
	public void update() {
		/*
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
		}*/
		
	}

	@Override
	public void move(Vector3 tgtPos) {
		setAbsPos(tgtPos.x,tgtPos.y);
		getCharBox().x = getAbsPos().x + getBg().getBackgroundPos().x;
		getCharBox().y = getAbsPos().y + getBg().getBackgroundPos().y;
		
	}

	@Override
	protected boolean collide(Character c) {
		return false;
		// TODO Auto-generated method stub
		
	}

	
}