package com.ginga.gingammorpg.server.attacks;

import com.ginga.gingammorpg.server.AttackInterface;
import com.ginga.gingammorpg.server.Mob;
import com.ginga.gingammorpg.server.Server;
import com.ginga.gingammorpg.server.UserHandler;

public class Zetsu extends AttackInterface{

	public Zetsu(Server server) {
		super(server);
		ID = 2;
		ATTACK_RANGE = 500;
		MANA_COST = 60;
		REQUIRED_LEVEL = 50;
		AVERAGE_DAMAGE = 300;
	}
	

	public void BeforeDamage(UserHandler performer, Mob victim){
		server.setPlayerCoords(performer, performer.Region, victim.x, victim.y, performer.rotation);
	}
	
	public void BeforeDamage(UserHandler performer, UserHandler victim){
		server.setPlayerCoords(performer, performer.Region, victim.x, victim.y, performer.rotation);
	}

}
