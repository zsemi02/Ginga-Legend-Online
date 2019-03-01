package com.ginga.gingammorpg.screens.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.ginga.gingammorpg.entity.Item;

public class Inventory {

	Window inventoryWindow;
	Stage stage;
	Table InventoryTable;
	public InventorySlot[] slots = new InventorySlot[8];
	int [][] Slots;
	public Actor Selected = null;
	
	
	
	public Inventory(Window inventoryWindow, Stage stage, int[][] Slots) {
		this.inventoryWindow=inventoryWindow;
		this.stage = stage;
		this.Slots = Slots;
		
		inventoryWindow.setWidth(32*6);
		inventoryWindow.setHeight(32*3);
		inventoryWindow.align(Align.bottomLeft);
		stage.addActor(inventoryWindow);
		InventoryTable = new Table();
		inventoryWindow.add(InventoryTable);
		//InventoryTable.setFillParent(true);
		InventoryTable.left();
		
		
		
		
		InventoryTable.setDebug(true);
		for(int i=0;i<8;i++){
			slots[i] = new InventorySlot(i, Slots[i][0], Slots[i][1]);
		}

		for(int i=0;i<3;i++){
			InventoryTable.add(slots[i]).width(slots[i].width).height(slots[i].height);
		}
		InventoryTable.row();
		for(int i=3;i<8;i++){
			InventoryTable.add(slots[i]).width(slots[i].width).height(slots[i].height);
		}
		
		InventoryTable.pack();
		}
	
	public void render(){
		

		
	}
}
