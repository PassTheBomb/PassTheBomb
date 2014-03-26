package com.passthebomb.controller;

import com.badlogic.gdx.utils.Timer.Task;
import com.passthebomb.model.Screen;

public class ScreenSwitchTask extends Task{
	
	private Screen screen = null;
	
	public ScreenSwitchTask(Screen screen) {
		this.screen = screen;
	}

	@Override
	/**
	 * This method is in charge of changing screen
	 */
	public void run() {
		ScreenManager.getInstance().show(screen);
	}

}
