package com.passthebomb.model.local;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.passthebomb.view.gui.Background;

public abstract class Character {
	private final float[] limits;
	private final int RADIUS = 32;
	private final int BOUND_OFFSET = 80;

	private Vector2 absPos;
	private Circle circle;
	private Texture[] circleImgSet;
	private Background bg;
	
	private boolean carryingBomb;
	private boolean bombPass;
	private boolean alive;
	
	
	protected Character(Vector2 absStartPos, Texture[] imgSet, boolean bomb, Background bg) {
		//this.absPos=absStartPos;
		this.circle = new Circle(absStartPos, RADIUS);
		this.circleImgSet = imgSet;
		this.carryingBomb = bomb;
		this.bg = bg;
		//Vector2 temp = new Vector2(this.bg.getBackgroundPos().x,this.bg.getBackgroundPos().y);
		this.alive = true;
		//this.circle = new Circle(temp.add(absStartPos), RADIUS);
		this.bombPass = false;
		this.limits = bg.getLimits();
	}
	
	protected abstract void update();
	
	protected abstract void move(Vector3 tgtPos);
	
	protected abstract boolean collide(Character c);
	
	public void checkLimits(){
		if (circle.x<BOUND_OFFSET){
			circle.x = BOUND_OFFSET;
		}
		else if (circle.x>limits[3]-BOUND_OFFSET){
			circle.x = limits[3]-BOUND_OFFSET;
		}
		if (circle.y<BOUND_OFFSET){
			circle.y = BOUND_OFFSET;
		}
		else if (circle.y>limits[0]-BOUND_OFFSET){
			circle.y = limits[0]-BOUND_OFFSET;
		}
	}
	
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

	public Vector2 getAbsPos() {
		return new Vector2(circle.x, circle.y);
	}

	protected void setAbsPos(Vector2 absPos) {
		circle.x = absPos.x;
		circle.y = absPos.y;
	}

	protected void setAbsPos(float x, float y) {
		circle.x = x;
		circle.y = y;
	}
	
	protected Background getBg() {
		return bg;
	}


	public void setBomb(boolean bombState){
		if (carryingBomb != bombState){
			bombPass = true;
		}
		carryingBomb = bombState;
	}

	public boolean getBombState(){
		return carryingBomb;
	}

	public boolean getBombPass(){
		return bombPass;
	}
	
	public void resetBombPass(){
		bombPass = false;
	}
	protected boolean isAlive() {
		return alive;
	}

	protected void death() {
		this.alive = false;
	}
	
}
