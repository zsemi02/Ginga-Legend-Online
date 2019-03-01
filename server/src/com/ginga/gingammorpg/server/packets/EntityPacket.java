package com.ginga.gingammorpg.server.packets;

import java.io.DataOutputStream;
import java.io.IOException;

import com.ginga.gingammorpg.server.OpCodes;

public class EntityPacket {
	
	public static final byte PLAYER = 1;
	public static final byte MOB = 2;
	
	float x,y;
	int health, maxHealth, id, StyleID, level;
	byte StartingRegion, type;
	DataOutputStream out;
	String name;
	float rotation;
	
	public EntityPacket(byte type, float x, float y,float rotation, int health, int maxHealth, int StyleID, byte StartingRegion,int id, String name,int level, DataOutputStream out) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.health = health;
		this.maxHealth = maxHealth;
		this.StyleID = StyleID;
		this.StartingRegion = StartingRegion;
		this.out = out;
		this.id = id;
		this.name = name;
		this.level = level;
		this.rotation = rotation;
	}
	
	
	public boolean SendPlayer(){
		try {
			out.writeByte(OpCodes.ADD_ENTITY);
			out.writeByte(type);
			out.writeInt(id);
			out.writeUTF(name);
			out.writeByte(StyleID); // Later
			out.writeFloat(x);
			out.writeFloat(y);
			out.writeFloat(rotation);
			out.writeByte(StartingRegion);
			out.writeInt(health);
			out.writeInt(maxHealth);
			out.writeInt(level);
			out.flush();
			return true;
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return false;
		}
	}
	
	
}
