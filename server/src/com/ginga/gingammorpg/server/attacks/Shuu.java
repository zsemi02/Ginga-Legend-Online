package com.ginga.gingammorpg.server.attacks;

import com.ginga.gingammorpg.server.AttackInterface;
import com.ginga.gingammorpg.server.Server;

public class Shuu extends AttackInterface{

	public Shuu(Server server) {
		super(server);
		ID = 4;
		ATTACK_RANGE = 500;
		MANA_COST = 20;
		REQUIRED_LEVEL = 10;
		AVERAGE_DAMAGE = 40;
	}

}
