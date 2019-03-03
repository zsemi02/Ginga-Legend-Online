package com.ginga.gingammorpg.server.packets;

import com.ginga.gingammorpg.server.Data;

public class MovePacket extends Packet{
	float x,y;
	
	public MovePacket(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	
	
	
}
