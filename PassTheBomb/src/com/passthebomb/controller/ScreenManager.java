package com.passthebomb.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.IntMap;
import com.passthebomb.model.Screen;

public class ScreenManager {
	
	private static ScreenManager instance;
	private Game game;
	private IntMap<com.badlogic.gdx.Screen> screens;
	
	private ScreenManager() {
		screens = new IntMap<com.badlogic.gdx.Screen>();
	}
	
	/**
	 * Singleton pattern to create an instance of ScreenManger if
	 * it hasn't been created before
	 * @return Instance of ScreenManager
	 */
	public static ScreenManager getInstance() {
		if(instance == null) {
			instance = new ScreenManager();
		}
		return instance;
	}
	
	/**
	 * Initialize the ScreenManager as soon as the Game
	 * application is opened
	 * @param game
	 */
	public void initialize(Game game) {
		this.game = game;
	}
	
	/**
	 * This function is called when the screen changes
	 * @param screen
	 */
	public void show(Screen screen) {
		if(game == null) return;
		if(!screens.containsKey(screen.ordinal())) {
			screens.put(screen.ordinal(), screen.getScreenInstance());
		}
		game.setScreen(screens.get(screen.ordinal()));
	}
	
	public void dispose(Screen screen) {
        if (!screens.containsKey(screen.ordinal())) return;
        screens.remove(screen.ordinal()).dispose();
    }
 
    public void dispose() {
        for (com.badlogic.gdx.Screen screen : screens.values()) {
            screen.dispose();
        }
        screens.clear();
        instance = null;
    } 
}
