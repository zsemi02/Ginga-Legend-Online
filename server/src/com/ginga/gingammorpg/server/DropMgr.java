package com.ginga.gingammorpg.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import com.ginga.gingammorpg.server.packets.LootPacket;


public class DropMgr {
	ArrayList<Item> drops = new ArrayList<Item>();
	ArrayList<UserHandler> permissionToLoot = new ArrayList<UserHandler>();
	Connection conn;
	int mobid;
	Server server;
	
	public DropMgr(int mobid, Server server){
		conn = server.conn;
		this.mobid = mobid;
		this.server = server;
	}
	public void CalculateDrops(){
		permissionToLoot.clear();
		drops.clear();
		String qGetDrops = "SELECT * FROM `items` INNER JOIN `drops` ON `items`.`id`=`drops`.`itemid` WHERE `id` IN (SELECT `itemid` FROM `drops` WHERE `mobid` = ?) GROUP BY `id`;";
		try {
			PreparedStatement statement = conn.prepareStatement(qGetDrops);
			statement.setInt(1, mobid);
			ResultSet items = statement.executeQuery();
			while(items.next()){
				int chance = items.getInt("chance");
				int itemid = items.getInt("id");
				Random rand = new Random();
				int c = rand.nextInt(100);
				if(c <= chance){
					for(int i=0;i<server.Items.size();i++){
						Item curr = server.Items.get(i);
						if(curr.id == itemid){
							drops.add(curr);
							break;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void Send(){
		for(int i=0; i<permissionToLoot.size(); i++){
			UserHandler curr = permissionToLoot.get(i);
			server.packets.add(new LootPacket(curr, drops));
		}
	}
	
}
