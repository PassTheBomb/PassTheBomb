package com.passthebomb.controller;

import com.passthebomb.model.Screen;
import com.passthebomb.view.gui.Button.ButtonHandler;

public class ScreenSwitchHandler implements ButtonHandler{
	
	private Screen screen = null;
	
	public ScreenSwitchHandler(Screen screen) {
		this.screen = screen;
	}

	@Override
	public void onClick() {
		ScreenManager.getInstance().show(screen);
	}

}
