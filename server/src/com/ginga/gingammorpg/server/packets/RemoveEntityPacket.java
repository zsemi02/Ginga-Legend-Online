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
	
	public boolean Send(){
		try {
			out.writeByte(OpCodes.REMOVE_ENTITY);
			out.writeByte(type);
			System.out.println(type);
			out.writeInt(id);
			out.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
