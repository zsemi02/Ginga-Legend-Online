package com.ginga.gingammorpg.server;

public class RespawnMob {

	int id, RespawnTime, CurrentRound=0;
	public RespawnMob(int id, int RoundToRespawn) {
		this.id = id;
		this.RespawnTime = RoundToRespawn;
	}
	
	
}
