package com.ginga.gingammorpg.net;

import java.io.DataOutputStream;
import java.io.IOException;

import com.ginga.gingammorpg.AttackTypes;
import com.ginga.gingammorpg.entity.Creature;
import com.ginga.gingammorpg.entity.EntityPacket;
import com.ginga.gingammorpg.entity.Entity.EntityType;

public class AttackPacket extends Packet{
	Creature Attacker, Victim;
	byte spell;
	DataOutputStream out;
	
	public AttackPacket(DataOutputStream out, Creature Attacker, Creature Victim, byte spell){
		this.Attacker = Attacker;
		this.Victim = Victim;
		this.spell = spell;
		this.out = out;
	}
	
	public void send(){
		try {
			out.writeByte(PacketTypes.PERFORM_ATTACK);
			if(Victim.getEntityType().equals(EntityType.MOB)){
				out.writeByte(EntityPacket.MOB);
			}else if(Victim.getEntityType().equals(EntityType.PLAYER)){
				out.writeByte(EntityPacket.PLAYER);
			}
			out.writeInt(Victim.ID);
			out.writeByte(spell);
			out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
