package com.ginga.gingammorpg.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Item extends Entity{
	
	int maxQuantity;
	public Texture ItemTexture;
	public String name = "";
	int attack, health, defense;
	public enum Type{
		HELMET,
		WEAPON,
		SPECIAL,
		OTHER
	}
	
	public Item(int ID, String name, int attack, int health, int defense, final String file) {
		super(32, 32, ID);
		if(ID != 0){
		Gdx.app.postRunnable(new Runnable() {
			
			@Override
			public void run() {
				ItemTexture = new Texture("img/items/"+file+".png");
			}
		});
		
		this.attack = attack;
		this.health = health;
		this.defense = defense;
		this.name = name;
		}
		
	}
	
	public Texture getTexture(){
		return ItemTexture;
	}
	
}
