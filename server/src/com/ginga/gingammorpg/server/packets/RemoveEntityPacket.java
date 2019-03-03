package com.ginga.gingammorpg.server.packets;

import java.io.DataOutputStream;
import java.io.IOException;

import com.ginga.gingammorpg.server.OpCodes;

public class RemoveEntityPacket extends Packet{

	int id;
	DataOutputStream out;
	byte type;
	public RemoveEntityPacket(int id, DataOutputStream out, byte type) {
		this.id = id;
		this.out = out;
		this.type = type;
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
