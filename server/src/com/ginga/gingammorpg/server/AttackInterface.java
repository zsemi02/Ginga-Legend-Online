package com.ginga.gingammorpg.server;

import java.util.Random;

import com.ginga.gingammorpg.server.packets.EntityPacket;

public class AttackInterface {
	public byte ID;
	public int ATTACK_RANGE;
	public int MANA_COST;
	public int AVERAGE_DAMAGE = 1;
	public int REQUIRED_LEVEL = 1;
	protected Server server;
	
	public AttackInterface(Server server) {
		this.server = server;
	}
	
	
	public void Apply(UserHandler performer, byte EntityType, int ID){
		if(performer.level < REQUIRED_LEVEL) return;
		if(performer.mana < MANA_COST)
			return;
		performer.mana -= MANA_COST;
		server.sendPlayerMana(performer);
		
		int baseAttackPower = performer.level*10;
		for(int i=0;i<3;i++){
			if(performer.Items[i][0] != null){
				baseAttackPower+=performer.Items[i][0].damage;
			}
		}
		Random r = new Random();
		//int finalDamage =baseAttackPower+ r.nextInt(performer.level + performer.level+1)-performer.level;
		int finalDamage =baseAttackPower+ r.nextInt(AVERAGE_DAMAGE);
		
		
		if(EntityType == EntityPacket.MOB){
			Mob victim = null;
			for(int i=0;i<server.Mobs.size();i++){
				Mob curr = server.Mobs.get(i);
				if(curr.id == ID){
					victim = curr;
					
					break;
				}
			}

			if(victim == null) return;
				if(!((performer.x - victim.x) < ATTACK_RANGE && (performer.x - victim.x) > -ATTACK_RANGE && (performer.y - victim.y) < ATTACK_RANGE && (performer.y - victim.y) > -ATTACK_RANGE)){
					return;
				}
				if(victim.isDead){
					return;
				}
				BeforeDamage(performer, victim);
				
			victim.target = performer;
			victim.MobState = Mob.MobAIStates.ATTACKING;
			
			victim.health-=finalDamage;
	
			if(victim.health <= 0){
				//DEAD
				victim.isDead = true;
				server.removeMob(victim.id);
				RespawnMob repsawn = new RespawnMob(victim.id, 2);
				server.RespawnMobs.add(repsawn);
				performer.xp+=victim.xpdrop;
				server.sendPlayerExp(performer);
				server.levelUpPlayer(performer);
			}
			AfterDamage(performer, victim);
			
	}else if(EntityType == EntityPacket.PLAYER){	// Mob end
		
		UserHandler victim = null;
		for(int i=0;i<server.Players.size();i++){
			UserHandler curr = server.Players.get(i);
			if(curr.id == ID){
				victim = curr;
				
				break;
			}
		}
		if(victim == null) return;
		if(!((performer.x - victim.x) < ATTACK_RANGE && (performer.x - victim.x) > -ATTACK_RANGE && (performer.y - victim.y) < ATTACK_RANGE && (performer.y - victim.y) > -ATTACK_RANGE))
			return;
		for(int i=0;i<3;i++){
			if(victim.Items[i][0] == null) continue;
			finalDamage-=victim.Items[i][0].defense;
		}
		
		BeforeDamage(performer, victim);
		victim.Health-=finalDamage;
		server.sendPlayerHealth(victim);
		System.out.println(performer.username+" Attacked "+victim.username+" Dealt "+finalDamage);
		if(victim.Health <= 0){
			//DEAD
			server.removePlayer(victim.id);
			//SET TO START POSITION
			server.setPlayerCoords(victim, victim.Startregion, 100, 100, 90); //Set this to start coordinates
			victim.Health = victim.MaxHealth;
			server.sendPlayerHealth(victim);
		}
		AfterDamage(performer, victim);
	}// Player end
		
	
}
	
	public void BeforeDamage(UserHandler performer, Mob victim) {
	
	}
	
	public void BeforeDamage(UserHandler performer, UserHandler victim) {
		
	}
	
	public void AfterDamage(UserHandler performer, Mob victim) {
	}
	public void AfterDamage(UserHandler performer, UserHandler victim) {
		
	}
	
}
