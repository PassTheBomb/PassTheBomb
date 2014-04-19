package com.passthebomb.model.local;

import com.passthebomb.view.screen.*;

public enum Screen {
	INTRO {
        @Override
        public com.badlogic.gdx.Screen getScreenInstance(com.badlogic.gdx.Screen lastScreen) {
            return new IntroScreen(lastScreen);
        }
    },
	
	WAIT {
        @Override
        public com.badlogic.gdx.Screen getScreenInstance(com.badlogic.gdx.Screen lastScreen) {
            return new WaitScreen(lastScreen);
        }
    },
 
    MAIN_MENU {
        @Override
        public com.badlogic.gdx.Screen getScreenInstance(com.badlogic.gdx.Screen lastScreen) {
             return new MainMenuScreen(lastScreen);
        }
    },
 
    GAME {
        @Override
        public com.badlogic.gdx.Screen getScreenInstance(com.badlogic.gdx.Screen lastScreen) {
             return new GameScreen(lastScreen);
        }
    },
 
    CREDITS {
        @Override
        public com.badlogic.gdx.Screen getScreenInstance(com.badlogic.gdx.Screen lastScreen) {
             return new CreditsScreen(lastScreen);
        }
    },
    
    PROTOCAL {
    	@Override
        public com.badlogic.gdx.Screen getScreenInstance(com.badlogic.gdx.Screen lastScreen) {
             return new ProtocalScreen(lastScreen);
        }
    };
 
    public abstract com.badlogic.gdx.Screen getScreenInstance(com.badlogic.gdx.Screen lastScreen);
}
