package com.ginga.gingammorpg.server.packets;

import java.io.IOException;

import com.ginga.gingammorpg.server.OpCodes;
import com.ginga.gingammorpg.server.UserHandler;

public class LevelPacket extends Packet{
	UserHandler u;
	public LevelPacket(UserHandler u){
	this.u = u;	
	}
	public void send(){
		try {
			u.output.writeByte(OpCodes.SET_LEVEL);
			u.output.writeInt(u.level);
			u.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
