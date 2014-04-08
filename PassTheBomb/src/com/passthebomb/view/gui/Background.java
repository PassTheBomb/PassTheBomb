package com.passthebomb.view.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Background {
	private static Background instance = null;
	private final int LEFT_LIM = 0;
	private final int RIGHT_LIM;
	private final int TOP_LIM;
	private final int BOTTOM_LIM = 0;
	
	private Texture bgImg;
	private Vector2 bgPos;
	
	private Background(Texture bgImg, Vector2 startPos, int[] bgSize){
		this.bgImg = bgImg;
		this.bgPos = startPos;
		this.RIGHT_LIM = bgSize[0];
		this.TOP_LIM = bgSize[1];
	}
	
	public static Background createBG(Texture bgImg, Vector2 startPos, int[] bgSize){
		if (instance == null){
			instance = new Background(bgImg, startPos, bgSize);
		}
		return instance;
	}
	
	public int[] getLimits(){
		int[] output = {TOP_LIM,BOTTOM_LIM,LEFT_LIM,RIGHT_LIM};
		return output;
	}
	/*public void scrollX(float val){
		bgPos.x += val;
		if (bgPos.x < RIGHT_LIM){
			bgPos.x = RIGHT_LIM;
		}
		else if (bgPos.x > LEFT_LIM){
			bgPos.x = LEFT_LIM;
		}
	}
	
	public void scrollY(float val){
		bgPos.y += val;
		if (bgPos.y < TOP_LIM){
			bgPos.y = TOP_LIM;
		}
		else if (bgPos.y > BOTTOM_LIM){
			bgPos.y = BOTTOM_LIM;
		}
	}*/
	
	public void dispose(){
		bgImg.dispose();
	}
	
	public Texture getBackgroundImg(){
		return bgImg;
	}
	
	/*public Vector2 getBackgroundPos(){
		return bgPos;
	}*/
}
