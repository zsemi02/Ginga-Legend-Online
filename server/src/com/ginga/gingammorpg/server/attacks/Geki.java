package com.ginga.gingammorpg.server.attacks;

import com.ginga.gingammorpg.server.AttackInterface;
import com.ginga.gingammorpg.server.Server;

public class Geki extends AttackInterface{

	public Geki(Server server) {
		super(server);
		ID = 5;
		ATTACK_RANGE = 500;
		MANA_COST = 22;
		REQUIRED_LEVEL = 20;
		AVERAGE_DAMAGE = 60;
	}

}
