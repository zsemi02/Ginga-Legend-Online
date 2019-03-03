package com.ginga.gingammorpg.server.packets;

import java.io.DataOutputStream;
import java.io.IOException;

import com.ginga.gingammorpg.server.OpCodes;
import com.ginga.gingammorpg.server.UserHandler;

public class EntityPacket extends Packet{
	
	public static final byte PLAYER = 1;
	public static final byte MOB = 2;
	
	float x,y;
	int health, maxHealth, id, StyleID, level;
	byte StartingRegion, type;
	DataOutputStream out;
	String name;
	float rotation;
	int damage, xpdrop;
	UserHandler u;
	
	public EntityPacket(byte type, float x, float y,float rotation, int health, int maxHealth, int StyleID, byte StartingRegion,int id, String name,int level, UserHandler u) {
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
		this.u = u;
		out = u.output;
	}
	public EntityPacket(byte type, float x, float y,float rotation, int health, int maxHealth, int StyleID, int id, String name,int level,  UserHandler u, int damage, int xpdrop) {
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
		this.damage = damage;
		this.xpdrop = xpdrop;
		this.u = u;
		out = u.output;
	}
	
	public void send(){
		if(type == EntityPacket.PLAYER) SendPlayer();
		if(type == EntityPacket.MOB) sendMob();
	}
	public void SendPlayer(){
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
			
		} catch (IOException e) {
			e.printStackTrace();
			u.logout();
		}
	}
	public void sendMob(){
		try {
			out.writeByte(OpCodes.ADD_ENTITY);
			out.writeByte(EntityPacket.MOB);
			out.writeInt(id);
			out.writeUTF(name);
			out.writeFloat(x);
			out.writeFloat(y);
			out.writeInt(health);
			out.writeInt(maxHealth);
			out.writeInt(StyleID);
			out.writeInt(damage);
			out.writeInt(level);
			out.writeInt(xpdrop);
			out.writeFloat(rotation);
			out.flush();
		
		} catch (IOException e) {
			e.printStackTrace();
			u.logout();
		}
	}
	
}
