package com.ginga.gingammorpg.server.packets;

import java.io.IOException;

import com.ginga.gingammorpg.server.OpCodes;
import com.ginga.gingammorpg.server.UserHandler;

public class ManaPacket extends Packet{
	UserHandler u;
	public ManaPacket(UserHandler u){
	this.u = u;	
	}
	public void send(){
		try {
			u.output.writeByte(OpCodes.SET_MANA);
			u.output.writeInt(u.mana);
			u.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
