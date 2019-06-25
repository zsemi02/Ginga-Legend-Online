package com.ginga.gingammorpg;

import java.io.DataOutputStream;

import com.ginga.gingammorpg.entity.Creature;
import com.ginga.gingammorpg.net.AttackPacket;
import com.ginga.gingammorpg.screens.GameScreen;

public class Attack {
	
	public enum AttackType{
		SPELL_ATTACK,
		SPELL_ZETSU
	}
	
	
	int VictimHP;
	Creature Attacker, Victim;
	AttackType attackType;
	DataOutputStream out;
	GameScreen game;
	public Attack(DataOutputStream out, GameScreen game) {
		this.out = out;
		this.game = game;
	}
	
	public void PerformAttack(Creature Attacker, Creature Victim, AttackType attacktype){
		this.Attacker = Attacker;
		this.Victim = Victim;
		this.attackType = attacktype;
		VictimHP = Victim.Health;
		if(!game.Mobs.contains(Victim) && !game.RemotePlayers.contains(Victim)) return;
		switch(attacktype){
		
		case SPELL_ATTACK:
			if((Attacker.getPosition().x - Victim.getPosition().x) < AttackTypes.SPELL_ATTACK_RANGE && (Attacker.getPosition().x - Victim .getPosition().x) > -AttackTypes.SPELL_ATTACK_RANGE && (Attacker.getPosition().y - Victim.getPosition().y) < AttackTypes.SPELL_ATTACK_RANGE && (Attacker.getPosition().y - Victim.getPosition().y) > -AttackTypes.SPELL_ATTACK_RANGE){

				game.packets.add(new AttackPacket(out, Attacker, Victim, AttackTypes.SPELL_ATTACK));
			}else{
				System.out.println("You need to be closer to use this attack on the target!");
			}
			
			break;
		case SPELL_ZETSU:
			if((Attacker.getPosition().x - Victim.getPosition().x) < AttackTypes.SPELL_ZETSU_TENROU_BATTOUGA_RANGE && (Attacker.getPosition().x - Victim .getPosition().x) > -AttackTypes.SPELL_ZETSU_TENROU_BATTOUGA_RANGE && (Attacker.getPosition().y - Victim.getPosition().y) < AttackTypes.SPELL_ZETSU_TENROU_BATTOUGA_RANGE && (Attacker.getPosition().y - Victim.getPosition().y) > -AttackTypes.SPELL_ZETSU_TENROU_BATTOUGA_RANGE){

				game.packets.add(new AttackPacket(out, Attacker, Victim, AttackTypes.SPELL_ZETSU_TENROU_BATTOUGA));
				//Animate
			}else{
				System.out.println("You need to be closer to use this attack on the target!");
			}
			break;
		default:
			break;
		
		
		
		}
		
		
	}
	
	
	
	
}
