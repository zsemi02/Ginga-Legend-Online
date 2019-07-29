package com.ginga.gingammorpg.server;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.ginga.gingammorpg.server.packets.LoginPacket;
import com.ginga.gingammorpg.server.packets.MovePacket;

public class Data {

	public Data() {
		
	}
	
	
	
	
	
	public static MovePacket MoveParser(float x, float y){
		return new MovePacket(x, y);
		
	}
	
}
