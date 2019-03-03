package com.ginga.gingammorpg.server;

import java.io.DataOutputStream;
import java.io.IOException;

import com.ginga.gingammorpg.server.packets.EntityPacket;

public class Mob {
	
	int id, health, max_health, styleID, damage, level, xpdrop;
	String name, region;
	public float x;
	public float y;
	float rotation;
	byte RegionByte;
	public boolean isDead = false;
	public static enum MobAIStates{
		IDLE,
		ATTACKING
	}
	public MobAIStates MobState = MobAIStates.IDLE;
	public UserHandler target = null;
	public int TicksToAct = 40;
	public int currentTicks = 0;
	Server server;

	public Mob(int id, String name, String region, float x, float y, int health, int max_health, int style_id, int damage, int level, int xpDrop, float rotation, Server server) {
		this.id = id;
		this.name = name;
		this.region = region;
		this.x = x;
		this.y = y;
		this.health = health;
		this.max_health = max_health;
		this.styleID = style_id;
		this.damage = damage;
		this.level =level;
		this.xpdrop = xpDrop;
		this.rotation = rotation;
		this.RegionByte = MapParser.parse(region);
		this.server=server;
	}
	
	public void Attack(UserHandler target){
		target.Health-=damage;	//Later maybe randomize the damage
		server.sendPlayerHealth(target);
		if(target.Health <= 0){
			server.removePlayer(target.id);
			server.setPlayerCoords(target, target.Region, 100, 100, 90);
			target.Health=target.MaxHealth;
			server.sendPlayerHealth(target);
			target = null;
		}
	}
	
	public void act(){					// Run this in every second in a separate thread in the Main (?)
		
		if(currentTicks >= TicksToAct){
		if(MobState == MobAIStates.IDLE){
			
		}else if(MobState == MobAIStates.ATTACKING){
			if(target == null || isDead){
				MobState=MobAIStates.IDLE;
				target = null;
				
			}else{
				
				Attack(target);
				// Play animation
				System.out.println(name+" has attacked "+target.username);
				if(!((target.x - x < 100 && target.x - x > -100) && (target.y - y < 100 && target.y - y > -100))){
					System.out.println("Target for "+name+" is null");
					target = null;
				}
			}
			
		}
		currentTicks = 0;
	}else{
		currentTicks++;
	}
		
	}
	
	public boolean SendOut(DataOutputStream out){
		if(!isDead){
		try {
			out.writeByte(OpCodes.ADD_ENTITY);
			out.writeByte(EntityPacket.MOB);
			out.writeInt(id);
			out.writeUTF(name);
			out.writeFloat(x);
			out.writeFloat(y);
			out.writeInt(health);
			out.writeInt(max_health);
			out.writeInt(styleID);
			out.writeInt(damage);
			out.writeInt(level);
			out.writeInt(xpdrop);
			out.writeFloat(rotation);
			out.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		}else{
			return true;
		}
		
	}
	
	
}
