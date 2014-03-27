package com.passthebomb.model.local;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.passthebomb.view.gui.Background;

public abstract class Character {
	private final int RADIUS = 32;

	private Vector2 absPos;
	private Circle circle;
	private Texture[] circleImgSet;
	private Background bg;
	
	private boolean carryingBomb;
	private boolean alive;
	
	
	protected Character(Vector2 absStartPos, Texture[] imgSet, boolean bomb, Background bg) {
		this.absPos=absStartPos;
		this.circleImgSet = imgSet;
		this.carryingBomb = bomb;
		this.bg = bg;
		Vector2 temp = new Vector2(this.bg.getBackgroundPos().x,this.bg.getBackgroundPos().y);
		this.alive = true;
		this.circle = new Circle(temp.add(absStartPos), RADIUS);
	}
	
	protected abstract void update();
	
	protected abstract void move(Vector3 tgtPos);
	
	protected abstract void collide(Character c);
	
	protected Circle getCharBox() {
		return circle;
	}
	
	public Texture getCharImg() {
		if(carryingBomb){
			return circleImgSet[1];
		}
		else{
			return circleImgSet[0];
		}
	}
	
	public float getCharImgX(){
		return circle.x - RADIUS;
	}

	public float getCharImgY(){
		return circle.y - RADIUS;
	}
	
	public void dispose() {
		circleImgSet[0].dispose();
		circleImgSet[1].dispose();
	}

	protected Vector2 getAbsPos() {
		return absPos;
	}

	protected void setAbsPos(Vector2 absPos) {
		this.absPos = absPos;
		circle.x += bg.getBackgroundPos().x + absPos.x;
		circle.y += bg.getBackgroundPos().y + absPos.y;
	}

	protected void setAbsPos(float x, float y) {
		this.absPos.x = x;
		this.absPos.y = y;
		circle.x += bg.getBackgroundPos().x + absPos.x;
		circle.y += bg.getBackgroundPos().y + absPos.y;
	}
	
	protected Background getBg() {
		return bg;
	}

	protected boolean isCarryingBomb() {
		return carryingBomb;
	}

	protected void changeBombState(){
		carryingBomb = !carryingBomb;
	}

	protected boolean isAlive() {
		return alive;
	}

	protected void death() {
		this.alive = false;
	}
	
}
