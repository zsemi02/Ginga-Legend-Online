package com.ginga.gingammorpg.server.attacks;

import com.ginga.gingammorpg.server.AttackInterface;
import com.ginga.gingammorpg.server.Server;

public class Attack extends AttackInterface{
	
	public Attack(Server server) {
		super(server);
		ID = 1;
		ATTACK_RANGE = 100;
		MANA_COST = 5;
		AVERAGE_DAMAGE = 5;
	}

}
