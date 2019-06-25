package com.ginga.gingammorpg;

import com.badlogic.gdx.math.Vector2;
import com.ginga.gingammorpg.screens.GameScreen;

public class DamageRender {
	
	public boolean isFinished = false;
	public int DamageValue = 0;
	long StartTime = 0;
	long duration;
	long tmpTime = 0;
	public Vector2 loc;

	public DamageRender(int dmg, long duration, Vector2 coords, GameScreen game){
		this.DamageValue = dmg;
		StartTime = System.currentTimeMillis();
		this.duration = duration;
		this.loc = new Vector2(coords);
		this.tmpTime = System.currentTimeMillis();
	}
	
	public void update(){
		if ((System.currentTimeMillis() - tmpTime) >= duration/90){
			tmpTime = System.currentTimeMillis();
			loc.x += 10;
			loc.y += 10;
		}
		if ((System.currentTimeMillis() - StartTime) >= duration){
			isFinished = true;
		}
	}
}
