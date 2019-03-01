package com.ginga.gingammorpg.server.packets;

import com.ginga.gingammorpg.server.Data;

public class LoginPacket extends Data{
	
	
	public String username;
	
	
	public LoginPacket(String username) {
		this.username = username;
		
	}
	
	
}
