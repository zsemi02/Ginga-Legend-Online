package com.ginga.gingammorpg.server.packets;

import java.util.ArrayList;
import java.util.HashMap;

import com.ginga.gingammorpg.server.Item;
import com.ginga.gingammorpg.server.ItemSlot;
import com.ginga.gingammorpg.server.OpCodes;
import com.ginga.gingammorpg.server.UserHandler;

public class LoginPacket extends Packet{
	
	float x,y;
	byte RegionByte;
	int Health, MaxHealth, level, xp, needxp, money, mana, maxmana;
	ArrayList<ItemSlot> inventory;
	UserHandler u;
	
	
	public LoginPacket(UserHandler u, float x, float y, byte RegionByte, int Health, int MaxHealth, int level, int xp, int needxp, int money, int mana, int maxmana, ArrayList<ItemSlot> Inventory) {
		
		this.Health = Health;
		this.x = x;
		this.y = y;
		this.RegionByte = RegionByte;
		this.MaxHealth = MaxHealth;
		this.level = level;
		this.xp = xp;
		this.money = money;
		this.needxp = needxp;
		this.mana = mana;
		this.maxmana = maxmana;
		this.inventory = Inventory;
		this.u = u;
	}
	
	public void send(){
		try{
		u.output.write(OpCodes.LOGIN);
		u.output.writeFloat(x);
		u.output.writeFloat(y);
		u.output.writeByte(RegionByte);
		u.output.writeInt(Health);
		u.output.writeInt(MaxHealth);
		u.output.writeInt(level);
		u.output.writeInt(xp);
		u.output.writeInt(needxp);
		u.output.writeInt(money);
		u.output.writeInt(mana);
		u.output.writeInt(maxmana);
		for(int k=0;k<inventory.size();k++){
			System.out.println("Inventory size: "+inventory.size());
			Item curritem = inventory.get(k).item;
			int currq = inventory.get(k).Quantity;
			u.output.writeInt(curritem.id);
			u.output.writeInt(curritem.damage);
			u.output.writeInt(curritem.defense);
			u.output.writeInt(curritem.health);
			u.output.writeInt(currq);
			u.output.writeUTF(curritem.name);
			u.output.writeUTF(curritem.itemImage);
		}
		u.output.flush();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
