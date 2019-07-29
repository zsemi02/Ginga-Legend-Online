package com.ginga.gingammorpg.server;

public class Item {
	public int id, MaxQuantity, damage, health, defense;
	public String name, itemImage;
	
	public Item(int id, String name, int MaxQuantity, int damage, int health, int defense, String itemImage) {
		this.id = id;
		this.MaxQuantity = MaxQuantity;
		this.damage = damage;
		this.health = health;
		this.defense = defense;
		this.name = name;
		this.itemImage = itemImage;
	}
	
	public static Item ParseID(int id, Server server){
		for(int i=0;i<server.Items.size();i++){
			Item item = server.Items.get(i);
			if(item.id == id){
				return item;
			}
		}
		return null;
	}
	
	
}
