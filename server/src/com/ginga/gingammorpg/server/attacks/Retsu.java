package com.ginga.gingammorpg.server.attacks;

import com.ginga.gingammorpg.server.AttackInterface;
import com.ginga.gingammorpg.server.Server;

public class Retsu extends AttackInterface{

	public Retsu(Server server) {
		super(server);
		ID = 3;
		ATTACK_RANGE = 500;
		MANA_COST = 10;
		REQUIRED_LEVEL = 5;
		AVERAGE_DAMAGE = 20;
	}

}
