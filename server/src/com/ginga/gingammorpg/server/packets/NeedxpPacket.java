package com.ginga.gingammorpg.server.packets;

import java.io.IOException;

import com.ginga.gingammorpg.server.OpCodes;
import com.ginga.gingammorpg.server.UserHandler;

public class NeedxpPacket extends Packet{
	UserHandler u;
	public NeedxpPacket(UserHandler u){
	this.u = u;	
	}
	public void send(){
		try {
			u.output.writeByte(OpCodes.SET_NEEDEDXP);
			u.output.writeInt(u.needxp);
			u.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
