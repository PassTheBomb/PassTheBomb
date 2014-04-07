package com.passthebomb.view.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Background {
	private static Background instance = null;
	private final int LEFT_LIM = 0;
	private final int RIGHT_LIM = -224;
	private final int TOP_LIM = -544;
	private final int BOTTOM_LIM = 0;
	
	private Texture bgImg;
	private Vector2 bgPos;
	
	private Background(Texture bgImg, Vector2 startPos){
		this.bgImg = bgImg;
		this.bgPos = startPos;
	}
	
	public static Background createBG(Texture bgImg, Vector2 startPos){
		if (instance == null){
			instance = new Background(bgImg, startPos);
		}
		return instance;
	}
	
	public void scrollX(float val){
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
	}
	
	public void dispose(){
		bgImg.dispose();
	}
	
	public Texture getBackgroundImg(){
		return bgImg;
	}
	
	public Vector2 getBackgroundPos(){
		return bgPos;
	}
}
