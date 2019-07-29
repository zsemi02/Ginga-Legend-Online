package com.ginga.gingammorpg.screens.inventory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.ginga.gingammorpg.entity.Item;

public class InventorySlot extends Widget{
	
	float width = 32, height=32;
	public int place;
	public Item item;
	public int quantity;
	
	public InventorySlot(int place, Item item, int Quantity) {
		this.place = place;
		this.item = item;
		this.quantity = Quantity;
	}
	
	
	public void draw(Batch batch, float parentAlpha){
		validate();
		if(item != null)
			batch.draw(item.getTexture(), getX(), getY());
		
			
	}
	
	public Item getItem(){
		return item;
	}
	
	public void DeleteItem(){
		item = null;
	}
	public void setItem(Item i){
		item = i;
	}
	
}
