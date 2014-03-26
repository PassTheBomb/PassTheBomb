package com.passthebomb.model;

import com.passthebomb.view.screen.CreditsScreen;
import com.passthebomb.view.screen.GameScreen;
import com.passthebomb.view.screen.IntroScreen;
import com.passthebomb.view.screen.LobbyListScreen;
import com.passthebomb.view.screen.MainMenuScreen;
import com.passthebomb.view.screen.WaitScreen;

public enum Screen {
	INTRO {
        @Override
        public com.badlogic.gdx.Screen getScreenInstance() {
            return new IntroScreen();
        }
    },
	
	WAIT {
        @Override
        public com.badlogic.gdx.Screen getScreenInstance() {
            return new WaitScreen();
        }
    },
 
    MAIN_MENU {
        @Override
        public com.badlogic.gdx.Screen getScreenInstance() {
             return new MainMenuScreen();
        }
    },
 
    GAME {
        @Override
        public com.badlogic.gdx.Screen getScreenInstance() {
             return new GameScreen();
        }
    },
 
    CREDITS {
        @Override
        public com.badlogic.gdx.Screen getScreenInstance() {
             return new CreditsScreen();
        }
    },
    
    LOBBY {
    	@Override
        public com.badlogic.gdx.Screen getScreenInstance() {
             return new LobbyListScreen();
        }
    };
 
    public abstract com.badlogic.gdx.Screen getScreenInstance();
}
