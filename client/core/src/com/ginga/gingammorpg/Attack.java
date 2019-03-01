package com.ginga.gingammorpg;

import java.io.DataOutputStream;
import java.io.IOException;

import com.ginga.gingammorpg.entity.Creature;
import com.ginga.gingammorpg.entity.Entity.EntityType;
import com.ginga.gingammorpg.entity.EntityPacket;
import com.ginga.gingammorpg.entity.Player;
import com.ginga.gingammorpg.net.AttackPacket;
import com.ginga.gingammorpg.net.PacketTypes;

public class Attack {
	
	public enum AttackType{
		SPELL_ATTACK
	}
	
	
	
	Creature Attacker, Victim;
	AttackType attackType;
	DataOutputStream out;
	
	public Attack(DataOutputStream out) {
		this.out = out;
	}
	
	public void PerformAttack(Creature Attacker, Creature Victim, AttackType attacktype){
		this.Attacker = Attacker;
		this.Victim = Victim;
		this.attackType = attacktype;
		
		switch(attacktype){
		
		case SPELL_ATTACK:
			if((Attacker.getPosition().x - Victim.getPosition().x) < AttackTypes.SPELL_ATTACK_RANGE && (Attacker.getPosition().x - Victim .getPosition().x) > -AttackTypes.SPELL_ATTACK_RANGE && (Attacker.getPosition().y - Victim.getPosition().y) < AttackTypes.SPELL_ATTACK_RANGE && (Attacker.getPosition().y - Victim.getPosition().y) > -AttackTypes.SPELL_ATTACK_RANGE){
				
				/*try {
					out.writeByte(PacketTypes.PERFORM_ATTACK);
					if(Victim.getEntityType().equals(EntityType.MOB)){
						out.writeByte(EntityPacket.MOB);
					}else if(Victim.getEntityType().equals(EntityType.PLAYER)){
						out.writeByte(EntityPacket.PLAYER);
					}
					out.writeInt(Victim.ID);
					out.writeByte(AttackTypes.SPELL_ATTACK);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}*/
				new AttackPacket(out, Attacker, Victim, AttackTypes.SPELL_ATTACK).send();
			}else{
				System.out.println("You need to be closer to use this attack on the target!");
			}
			
			break;
		default:
			break;
		
		
		
		}
		
		
	}
	
	
	
	
}
