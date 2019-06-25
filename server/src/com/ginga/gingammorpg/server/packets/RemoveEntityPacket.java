package com.ginga.gingammorpg.server.packets;

import java.io.DataOutputStream;
import java.io.IOException;

import com.ginga.gingammorpg.server.OpCodes;
import com.ginga.gingammorpg.server.UserHandler;

public class RemoveEntityPacket extends Packet{

	int id;
	DataOutputStream out;
	byte type;
	UserHandler u;
	public RemoveEntityPacket(int id, UserHandler u, byte type) {
		this.id = id;
		this.u= u;
		this.type = type;
		out = u.output;
	}
	
	public void send(){
		try {
			out.writeByte(OpCodes.REMOVE_ENTITY);
			out.writeByte(type);
			out.writeInt(id);
			out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
