package com.ginga.gingammorpg.server.packets;

import java.io.IOException;

import com.ginga.gingammorpg.server.OpCodes;
import com.ginga.gingammorpg.server.UserHandler;

public class MaxManaPacket extends Packet{
	UserHandler u;
	public MaxManaPacket(UserHandler u){
	this.u = u;	
	}
	public void send(){
		try {
			u.output.writeByte(OpCodes.SET_MAXMANA);
			u.output.writeInt(u.max_mana);
			u.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
