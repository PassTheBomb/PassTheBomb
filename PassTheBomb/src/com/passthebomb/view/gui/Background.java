package com.passthebomb.view.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * The Background Class. Stores the Background image, its size and its absolute
 * position. Only one instance of the Background class can be active.
 * 
 * 
 */
public class Background {
	private static Background instance = null;
<<<<<<< HEAD
	private final float LEFT_LIM = 0;
	private final float RIGHT_LIM;
	private final float TOP_LIM;
	private final float BOTTOM_LIM = 0;
	
	private Texture bgImg;
	private Vector2 bgPos;
	
	private Background(Texture bgImg, Vector2 startPos, float[] bgSize){
=======
	private final int LEFT_LIM = 0;
	private final int RIGHT_LIM;
	private final int TOP_LIM;
	private final int BOTTOM_LIM = 0;

	private Texture bgImg;

	/**
	 * Hidden background constructor. Used by the static createBG call to create
	 * a single instance.
	 * 
	 * @param bgImg
	 *            the background image to be stored
	 * @param startPos
	 *            the background absolute position in pixels
	 * @param bgSize
	 *            the size of the background in pixels
	 */
	private Background(Texture bgImg, Vector2 startPos, int[] bgSize) {
>>>>>>> Enwei
		this.bgImg = bgImg;
		this.RIGHT_LIM = bgSize[0];
		this.TOP_LIM = bgSize[1];
	}
<<<<<<< HEAD
	
	public static Background createBG(Texture bgImg, Vector2 startPos, float[] bgSize){
		if (instance == null){
=======

	/**
	 * Creates one instance of the Background and returns it. If a Background
	 * already exists, then that existing Background is returned instead
	 * 
	 * @param bgImg
	 *            the background image to be stored
	 * @param startPos
	 *            the background absolute position in pixels
	 * @param bgSize
	 *            the size of the background in pixels
	 * @return a Background instance
	 */
	public static Background createBG(Texture bgImg, Vector2 startPos,
			int[] bgSize) {
		if (instance == null) {
>>>>>>> Enwei
			instance = new Background(bgImg, startPos, bgSize);
		}
		return instance;
	}
<<<<<<< HEAD
	
	public float[] getLimits(){
		float[] output = {TOP_LIM,BOTTOM_LIM,LEFT_LIM,RIGHT_LIM};
=======

	/**
	 * Acquire the background boundaries.
	 * 
	 * @return an integer array of 4 values, <top limit, bottom limit, left
	 *         limit, and right limit> in that particular order
	 */
	public int[] getLimits() {
		int[] output = { TOP_LIM, BOTTOM_LIM, LEFT_LIM, RIGHT_LIM };
>>>>>>> Enwei
		return output;
	}

	/*
	 * public void scrollX(float val){ bgPos.x += val; if (bgPos.x < RIGHT_LIM){
	 * bgPos.x = RIGHT_LIM; } else if (bgPos.x > LEFT_LIM){ bgPos.x = LEFT_LIM;
	 * } }
	 * 
	 * public void scrollY(float val){ bgPos.y += val; if (bgPos.y < TOP_LIM){
	 * bgPos.y = TOP_LIM; } else if (bgPos.y > BOTTOM_LIM){ bgPos.y =
	 * BOTTOM_LIM; } }
	 */

	/**
	 * Disposes of the background image
	 * 
	 */
	public void dispose() {
		bgImg.dispose();
	}

	/**
	 * Acquires the background image
	 * 
	 * @return the stored background image
	 */
	public Texture getBackgroundImg() {
		return bgImg;
	}
}
