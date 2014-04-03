package com.passthebomb.model.local;

import com.passthebomb.view.screen.CreditsScreen;
import com.passthebomb.view.screen.GameScreen;
import com.passthebomb.view.screen.IntroScreen;
import com.passthebomb.view.screen.LobbyListScreen;
import com.passthebomb.view.screen.MainMenuScreen;
import com.passthebomb.view.screen.WaitScreen;

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
    
    LOBBY {
    	@Override
        public com.badlogic.gdx.Screen getScreenInstance(com.badlogic.gdx.Screen lastScreen) {
             return new LobbyListScreen(lastScreen);
        }
    };
 
    public abstract com.badlogic.gdx.Screen getScreenInstance(com.badlogic.gdx.Screen lastScreen);
}
