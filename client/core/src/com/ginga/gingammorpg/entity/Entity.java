package com.ginga.gingammorpg.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Entity {
	public float width, height;
	public int ID;
	
	public final Vector2 position = new Vector2();
	Texture texture;
	
	public Entity(float width, float height, int ID){
		this.width = width;
		this.height = height;
		this.ID = ID;
	}
	
	public enum EntityType{
		ITEM,
		PLAYER,
		NPC,
		MOB
	}
	EntityType entityType;
	
	public EntityType getEntityType(){
		return entityType;
	}

	public Vector2 getPosition() {
		return position;
	}

	
	
}
