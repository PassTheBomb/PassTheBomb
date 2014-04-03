package com.passthebomb.model.socket;

import java.io.*;
import java.util.ArrayList;

import com.passthebomb.model.local.Player;

public class ServerMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int INIT = 0, TIMEOUT = 2;
	private String message;
	private ArrayList<Player> player;
	
	public ServerMessage() {
		
	}

}
