package com.ginga.gingammorpg.entity;

import com.badlogic.gdx.graphics.Texture;

public class Item extends Entity{
	
	int maxQuantity;
	public Texture ItemTexture;
	public String name = "";
	public enum Type{
		HELMET,
		WEAPON,
		SPECIAL,
		OTHER
	}
	
	public Item(float width, float height, int ID, String name) {
		super(width, height, ID);
		
		if(name.equalsIgnoreCase("")){
			switch (ID) {
			case 1:
				name = "TestItem";
				break;
			case 2:
				name="Katana";
				break;
				
			default:
				name="N/A";
				break;
			}
		}
		if(name.equalsIgnoreCase("Katana")){
			ItemTexture = new Texture("img/items/katana_item.png");
		}else if(name.equalsIgnoreCase("TestItem")){
			ItemTexture = new Texture("img/items/katana_item.png");	//Change
		}else{
			ItemTexture = new Texture("img/items/katana_item.png");	//Unknowed Image | Later
		}
		this.name = name;
		
	}
	
	public Texture getTexture(){
		return ItemTexture;
	}
	
}
