package com.ginga.gingammorpg.server.packets;

import java.io.IOException;

import com.ginga.gingammorpg.server.OpCodes;
import com.ginga.gingammorpg.server.UserHandler;

public class HealthPacket extends Packet{
	UserHandler u;
	public HealthPacket(UserHandler u){
	this.u = u;	
	}
	public void send(){
		try {
			u.output.writeByte(OpCodes.SET_HEALTH);
			u.output.writeInt(u.Health);
			u.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
