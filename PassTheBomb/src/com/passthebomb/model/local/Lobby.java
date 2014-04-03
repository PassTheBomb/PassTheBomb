package com.passthebomb.model.local;

public class Lobby {
	public String ip;
	public String hostDeviceID;
	public int dbID;
	
	public Lobby(String ip, String hostDeviceID, int dbID) {
		this.ip = ip;
		this.hostDeviceID = hostDeviceID;
		this.dbID = dbID;
	}
}
