package com.ginga.gingammorpg.server.packets;

import java.io.IOException;

import com.ginga.gingammorpg.server.OpCodes;
import com.ginga.gingammorpg.server.UserHandler;

public class MaxHealthPacket extends Packet{
	UserHandler u;
	public MaxHealthPacket(UserHandler u){
	this.u = u;	
	}
	public void send(){
		try {
			u.output.writeByte(OpCodes.SET_MAXHP);
			u.output.writeInt(u.MaxHealth);
			u.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
