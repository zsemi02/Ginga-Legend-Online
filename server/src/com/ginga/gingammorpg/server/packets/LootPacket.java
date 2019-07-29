package com.ginga.gingammorpg.server.packets;

import java.io.IOException;
import java.util.ArrayList;

import com.ginga.gingammorpg.server.Item;
import com.ginga.gingammorpg.server.OpCodes;
import com.ginga.gingammorpg.server.UserHandler;

public class LootPacket extends Packet{

	UserHandler user;
	ArrayList<Item> Itemlist;
	public LootPacket(UserHandler u, ArrayList<Item> Itemlist){
		this.user = u;
		this.Itemlist = Itemlist;
	}
	
	public void send(){
		/*StringBuilder buffer = new StringBuilder();
		for(int i=0;i<Itemlist.size(); i++){
			buffer.append(Itemlist.get(i).id+":");
		}
		buffer.deleteCharAt(buffer.length()-1);
		String FinalBuffer = buffer.toString();
		System.out.println(FinalBuffer);*/
		
			try {
				user.output.writeByte(OpCodes.LOOT_ITEMS);
				//user.output.writeUTF(FinalBuffer);
				user.output.writeInt(Itemlist.size());
				for(int i=0;i<Itemlist.size();i++){
					Item currItem = Itemlist.get(i);
					
					user.output.writeInt(currItem.id);
					user.output.writeInt(currItem.damage);
					user.output.writeInt(currItem.defense);
					user.output.writeInt(currItem.health);
					user.output.writeUTF(currItem.name);
					user.output.writeUTF(currItem.itemImage);
				}
				user.output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
}
