package com.ginga.gingammorpg.server.packets;

import java.io.IOException;

import com.ginga.gingammorpg.server.OpCodes;
import com.ginga.gingammorpg.server.UserHandler;

public class ExpPacket extends Packet{
	UserHandler u;
	public ExpPacket(UserHandler u){
	this.u = u;	
	}
	public void send(){
		try {
			u.output.writeByte(OpCodes.SET_EXP);
			u.output.writeInt(u.xp);
			u.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
