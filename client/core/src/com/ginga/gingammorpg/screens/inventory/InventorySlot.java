package com.ginga.gingammorpg.screens.inventory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.ginga.gingammorpg.entity.Item;

public class InventorySlot extends Widget{
	
	float width = 32, height=32;
	public int place;
	public Item item;
	public int ID;
	
	public InventorySlot(int place, Item item, int Quantity) {
		this.place = place;
		this.item = item;
		
	}
	public InventorySlot(int place, int ID, int Quantity) {
		this.place = place;
		this.ID = ID;
		if(ID != 0)
			this.item = new Item(32, 32, ID, "");
		
	}
	
	public void draw(Batch batch, float parentAlpha){
		validate();
		if(ID != 0 && item != null)
			batch.draw(item.getTexture(), getX(), getY());
		
			
	}
	
	public Item getItem(){
		return item;
	}
	
	public void DeleteItem(){
		item = null;
		ID = 0;
	}
	public void setItem(Item i){
		item = i;
		ID = i.ID;
	}
	
}
