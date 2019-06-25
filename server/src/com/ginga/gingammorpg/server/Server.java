package com.ginga.gingammorpg.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ginga.gingammorpg.server.attacks.Attack;
import com.ginga.gingammorpg.server.attacks.Zetsu;
import com.ginga.gingammorpg.server.packets.ChatMessagePacket;
import com.ginga.gingammorpg.server.packets.EntityPacket;
import com.ginga.gingammorpg.server.packets.ExpPacket;
import com.ginga.gingammorpg.server.packets.HealthPacket;
import com.ginga.gingammorpg.server.packets.LevelPacket;
import com.ginga.gingammorpg.server.packets.ManaPacket;
import com.ginga.gingammorpg.server.packets.MaxHealthPacket;
import com.ginga.gingammorpg.server.packets.MaxManaPacket;
import com.ginga.gingammorpg.server.packets.NeedxpPacket;
import com.ginga.gingammorpg.server.packets.Packet;
import com.ginga.gingammorpg.server.packets.RemoveEntityPacket;
import com.ginga.gingammorpg.server.packets.SetPlayerCoordinatesPacket;






public class Server {
	
	 ServerSocket server;
	 Socket client;
	public static final int PORT = 7755;
	static boolean running = true;
	public PrivateKey privateKey;
	public ArrayList<UserHandler> Players = new ArrayList<UserHandler>();
	public ArrayList<Mob> Mobs = new ArrayList<Mob>();
	public ArrayList<RespawnMob> RespawnMobs = new ArrayList<RespawnMob>();
	public ArrayList<Item> Items = new ArrayList<Item>();
	int saveintervals = 1;
	Statement state;
	long memoryUsage = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
	ArrayList<AttackInterface> Attacks = new ArrayList<AttackInterface>();
	public ArrayList<Packet> packets = new ArrayList<Packet>();
	
	public Server() {
		try {
			//String PrivateKeyStr = "MIIEpAIBAAKCAQEAnSfN836JV3klzC5QdUPhWauUitFnkFvadk44xuVXunz5hmUFA2mSYh9hY4Clo7xtTxjC8Ja/gqDvsaQlv4I+c1652epPygK4JyhxHrIgKEHFyTxa4LXHHW8nezS2LYLuGoWIVeu80xkN7jzgE28XLO5y4SKEpIz9vLLebrLVxD5W4O5kVNxqtuotm1RvH6wDH8g0kdIn7EtS8XyAd8CE+STZ9zT6fzxaOm67R/llajiQJydAExrXbiKss791ibkr8n1Y+WfEofPYDQmlrzpgXIfeDiGwetd8QPyn7kYdW4o3PfES6OApMKfFCQXdpT4Jryez+wjWeB7tVwIExVJ5+QIDAQABAoIBADvbp2QxNBqvOChXE6o2mqTO55sgO3QOqF0bWiDXxdxwMZJw88Hi+jCJh0yg+XYuFOxloAqHQJZ+ug1NWlthPmwvDlbkGlP1STMRAlUQv5LVyoHljS+9zQN3DPCumR0om4xahB1F1vwItPejFC4SyB8DC5qYzTDnytWOw44ia619ACjGyZAs/oT1AlWOm6Mb/rYrykYTOojF3EnuDFOnQ86DkQNTT5gPmVnCSOPNvBa2Rg3vpWrD28TcHBvIAhqAQXVKRI0Z4IwzsIEFSWfRCg0ZP+RqACSwhR7z4Kb33ePthf3QBxSzUUvfJFe8FakNZ3xI3suOVJmRPs8So6vO/gECgYEA0RDHujtUjJsGDly9acAk65BVGN19LlRhlikUdRgBDXQEfUqQL8Evp9U7ArjhcJMnTMoQ6T2w4IZN3thLFaDNTBR4vKX2CjLZSidSEKlyZPWmWy+gP6gXkn5c5EwM7Iof8t0yIbhHwF7lF7bj4ETvxDG5yXsWjcVdw/tMbWCom1kCgYEAwG+yoRXfwDmDmo1IGiykq8W80iHYoUWzP7U0Woea9meJA8+SKIvjNXKj/hIH0LHggA3VzBJB69J6+csV4X6Gtrn7kRzhUAqttvlT9HhkoJO0eqCK3sGN8yjnMCuPmHFORSoFmA+w+1wK1UnChDR7ovusCIYWllBILspaL5ohH6ECgYEAwEKrOlOHjIqgBiM5OYAvM8aWy3gcv7dvyvTaUFiT1zhjTIl+kbwaREDutLEq+SkKki6dYLGP8Nrxz8afPjOTuKx24B3LZ1OdyfjhGluJzNivdNoWh5PgoaK9cGGT3Q+lE+ZhTOs4aOubyLQzWbJrwMRt86DTe+sOMMXwYgHq7HkCgYEAsE3ln1XWEFvhKdjkxS4/lCxuySo/OcoM5oJSu9pfa/8Bdd9XbhRzjsVAYAcO5/H/1/JU/UmA4diN2ItquZRdQc31IEcQWm/eJbQaafFfaArLIEoz0NAOCEhiPyy5u5Wbexx70YwWvsPeHPkd4FfhKjpfq9OFoCNfbpbvt4sDa+ECgYAwxrN5zCGhvUDRoRfhFzPXy0gWe3iQzRy7VXCc/PJH4Lg9k3laj50IAK9sAPBM2sd4czwGc0Q6pIFeGXj7HJwpHXbUurcr7Wf6ChPW6Uvk1kCYIhBC/2sht95qqzrlCRtO6RBZMzdj1o2faMrMzaHnz21UfJ1ocAnn9kIgMMLbMw==";
			
			
			server = new ServerSocket(PORT);
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/gingammorpg?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");	//TODO: rewrite 127.0.0.1 to static IP
			
			
			Statement state = conn.createStatement();
			this.state = state;
			//Load entities from database, and store/handle them here. No database save, only reading.
			String Query = "SELECT * FROM `mobs`";
			ResultSet mobsQ = state.executeQuery(Query);
			while(mobsQ.next()){
				int Mobid = mobsQ.getInt("id");
				String Mobname = mobsQ.getString("name");
				String Mobregion = mobsQ.getString("region");
				float Mobx = mobsQ.getFloat("x");
				float Moby = mobsQ.getFloat("y");
				int Mobhealth = mobsQ.getInt("health");
				int Mobmax_health  =mobsQ.getInt("max_health");
				int MobStyleID = mobsQ.getInt("style_id");
				int Mobdamage = mobsQ.getInt("damage");
				int Moblevel = mobsQ.getInt("level");
				int mobxp_drop = mobsQ.getInt("xp_drop");
				float Mobrotation = mobsQ.getFloat("rotation");
				
				Mob current = new Mob(Mobid, Mobname, Mobregion, Mobx, Moby, Mobhealth, Mobmax_health, MobStyleID, Mobdamage, Moblevel, mobxp_drop, Mobrotation, this);
				Mobs.add(current);
			}
			
			
			Query = "SELECT * FROM `items`";
			ResultSet itemSet = state.executeQuery(Query);
			while(itemSet.next()){
				int ItemId = itemSet.getInt("id");
				String ItemName = itemSet.getString("name");
				int ItemDamage = itemSet.getInt("damage");
				int ItemHealth = itemSet.getInt("health");
				int ItemDefense = itemSet.getInt("defense");
				String ItemImage = itemSet.getString("image");
				int ItemMaxQuantity = itemSet.getInt("max_quantity");
				
				Item current = new Item(ItemId, ItemName, ItemMaxQuantity, ItemDamage, ItemHealth, ItemDefense, ItemImage);
				Items.add(current);
			}
			/// Add attacks to list
			Attacks.add(new Attack(this));
			Attacks.add(new Zetsu(this));
			
			System.out.println("Server is up on port "+PORT+" With RAM: ");
			System.out.println(RamUsage());
			
			
			ScheduledExecutorService exec = (ScheduledExecutorService) Executors.newSingleThreadScheduledExecutor();
			exec.scheduleAtFixedRate(new Runnable() {
				
				@Override
				public void run() {
					for(int i=0;i<Players.size();i++){
						UserHandler u = Players.get(i);
						if(u.LoggedIn){
							boolean ok = saveUser(state, u);			 //state.execute("UPDATE `users` SET `money`='"+u.money+"', `x`='"+u.x+"', `y`='"+u.y+"', `region`='"+u.Region+"', `max_health`='"+u.MaxHealth+"', `health`='"+u.Health+"' WHERE `id`='"+u.id+"'");
							if(!ok){
								System.out.println(u.username+" Data has been saved to database.");
								
							}
						}
					}
					System.out.println(RamUsage());
					for(int i=0;i<RespawnMobs.size();i++){
						RespawnMob respawn = RespawnMobs.get(i);
						respawn.CurrentRound++;
						if(respawn.CurrentRound >= respawn.RespawnTime){
							for(int k=0;k<Mobs.size();k++){
								Mob m = Mobs.get(k);
								if(m.id == respawn.id){
									m.health = m.max_health;
									m.isDead = false;
									
								}
							}
							RespawnMobs.remove(respawn);
						}
					}
					try {
						PreparedStatement deletePrivs = conn.prepareStatement("DELETE FROM `auth`");
						deletePrivs.executeUpdate();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				
			}, saveintervals, saveintervals, TimeUnit.MINUTES);
			
			ScheduledExecutorService entityThread = Executors.newSingleThreadScheduledExecutor();
			entityThread.scheduleAtFixedRate(new Runnable() {	//entity packet sendout
				
				@Override
				public void run() {
							for(int i=0;i<Players.size();i++){
								UserHandler u = Players.get(i);
								if(u != null){
								if(u.LoggedIn){
									for(int k=0;k<Players.size();k++){
										if(u != Players.get(k)){
										UserHandler u2 = Players.get(k);
										if(u2 != null){
											if(u2.LoggedIn){
												///send out packets
												if(u.regionByte == u2.regionByte){
													if((u.x -u2.x < 1000 && u.x-u2.x > -1000) && (u.y -u2.y < 1000 && u.y-u2.y > -1000)){
														
														EntityPacket e = new EntityPacket(EntityPacket.PLAYER, u2.x, u2.y,u2.rotation, u2.Health, u2.MaxHealth, u2.CharacterStyleID, u2.startRegionByte, u2.id, u2.username,u2.level, u);
														packets.add(e);
														/*boolean ok = e.SendPlayer();
														
														if(!ok){
															logout(u);
														}else{
															//System.out.println("Sent to: "+u.username+" Data: "+u2.username+" "+u2.x+" "+u2.id);
														}*/
														
													}
												}
											}//
										}
										}
									}
									
									//
									
									for(int k = 0;k<Mobs.size();k++){
										Mob current = Mobs.get(k);
										if(u.regionByte == current.RegionByte){
										if((u.x -current.x < 1000 && u.x-current.x > -1000) && (u.y -current.y < 1000 && u.y-current.y > -1000)){
											if(!current.isDead)
												packets.add(new EntityPacket(EntityPacket.MOB, current.x, current.y, current.rotation, current.health, current.max_health, current.styleID, current.id, current.name, current.level, u, current.damage, current.xpdrop));
											
										}
										}
									}
									for(int k =0;k<RespawnMobs.size();k++){
										RespawnMob res = RespawnMobs.get(k);
										packets.add(new RemoveEntityPacket(res.id, u, EntityPacket.MOB));
									}
									
								}//
							}
							}
							for(int i=0;i<Mobs.size();i++){
								Mob mob = Mobs.get(i);
								mob.act();
							}
							for(int i=0;i<packets.size();i++){
								Packet p = packets.get(i);
								p.send();
								packets.remove(p);
							}
					
				}
			},0,1000/20,TimeUnit.MILLISECONDS);
			
			
			
			Thread ui = new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(running){
						String Scannerinput = "";
						Scanner scanner = new Scanner(System.in);
						System.out.println();
						System.out.print(">> ");
						Scannerinput = scanner.nextLine();
						switch(Scannerinput){
						case "shutdown":
							
							
							
							System.out.println("Saving Player datas...");
							for(int i=0;i<Players.size();i++){
								UserHandler u = Players.get(i);
								
									boolean ok = saveUser(state, u); 				//state.execute("UPDATE `users` SET `money`='"+u.money+"', `x`='"+u.x+"', `y`='"+u.y+"', `region`='"+u.Region+"', `max_health`='"+u.MaxHealth+"', `health`='"+u.Health+"' WHERE `id`='"+u.id+"'");
									if(!ok){
										System.out.println(u.username+" Data has been saved to database.");
										if(u.LoggedIn){
											try {
												u.output.writeByte(OpCodes.DISCONNECT);
												u.output.flush();
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
											
											u.t.interrupt();
									}
								
							}
							System.out.println("Shutting down...");
							entityThread.shutdown();
							exec.shutdown();
							System.exit(1);
							break;
						case "players":
							for(int i=0;i<Players.size();i++){
								System.out.println(Players.get(i).username);
							}
							break;
						case "memory":
							System.out.println(RamUsage());
							break;
						default:
							System.out.println("*No Command found*");
							break;
						}
						
					}
				}
			});
			ui.setName("ui");
			ui.start();
			
			while(running){
				client = server.accept();
				System.out.println("Client connected from "+client.getInetAddress());
				UserHandler u = new UserHandler(this,client, state, conn);
				Players.add(u);
				u.setPriority(2);
				
			}
			
			
		} catch (IOException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void logout(UserHandler u){
			if(u != null){
				if(u.LoggedIn){
					try {
						u.output.writeByte(OpCodes.DISCONNECT);
						u.output.flush();
						u.running = false;
					} catch (IOException e) {
						u.LoggedIn = false;
						logout(u);
						e.printStackTrace();
					}
				}else{
					u.running = false;
				}
			}
		
			boolean ok = saveUser(state, u);					 /*state.execute("UPDATE `users` SET `money`='"+u.money+"', `x`='"+u.x+"', `y`='"+u.y+"', `region`='"+u.Region+"', `max_health`='"+u.MaxHealth+"', `health`='"+u.Health+"' WHERE `id`='"+u.id+"'");*/
			if(!ok){
				System.out.println(u.username+" Data has been saved to database.");
			}
		
			Players.remove(u);
		
		for(int i=0;i<Players.size();i++){
			UserHandler sendto = Players.get(i);
			if(sendto.LoggedIn){
				packets.add(new RemoveEntityPacket(u.id, sendto, EntityPacket.PLAYER));
				
					
				
			}
		}
		u.t.interrupt();
		
		System.out.println(u.username+" Disconnected");
	}
	
	public String RamUsage(){
		long Total_ram = Runtime.getRuntime().totalMemory();
		long Free_ram = Runtime.getRuntime().freeMemory();
		long used = Total_ram-Free_ram;
		long total_mb = Total_ram/1024/1024;
		long free_mb = Free_ram/1024/1024;
		long used_mb = used/1024/1024;
		double usage_percentage = ((double)used/Total_ram)*100;
		return total_mb+"MB Total | "+free_mb+"MB Free | "+used_mb+"MB Used | "+usage_percentage+"% Usage";
		
	}
	
	public boolean saveUser(Statement state, UserHandler u){
		String query = "UPDATE `users` SET `money`='"+u.money+"', `x`='"+u.x+"', `y`='"+u.y+"', `region`='"+u.Region+"', `max_health`='"+u.MaxHealth+"', `health`='"+u.Health+"', `level`='"+u.level+"', `xp`='"+u.xp+"', `need_xp`='"+u.needxp+"', `mana`='"+u.mana+"', `max_mana`='"+u.max_mana+"' WHERE `id`='"+u.id+"'";
		String query2 = "UPDATE `inventory` SET ";
		for(int i=0;i<8;i++){
			if(u.Items[i][0] == null)
				query2+="`slot_"+i+"`='0', ";
			else
				query2+="`slot_"+i+"`='"+u.Items[i][0].id+"', ";
		}
		query2 = query2.substring(0, query2.length()-2);
		/*for(int i=0;i<8;i++){
			query2+="`slot_"+i+"_quantity`='"+u.Items[i][1]+"', ";
		}
		query2 = query2.substring(0, query2.length()-2);*/		/** Later, when item quantity is implemented **/
		query2 += " WHERE `owner_id`='"+u.id+"'";
		try {
			state.execute(query2);
			return state.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		
	}
	
			//////APPLY ATTACK
	public void ApplyAttack(UserHandler performer, byte EntityType, int ID, byte Attacktype){
		for(int i=0; i<Attacks.size();i++){
			AttackInterface curr = Attacks.get(i);
			if(curr.ID == Attacktype){
				curr.Apply(performer, EntityType, ID);
				break;
			}
		}
		
		
	}
	
	public void removeMob(int id){
		for(int i=0;i<Players.size();i++){
			UserHandler sendto = Players.get(i);
				packets.add(new RemoveEntityPacket(id, sendto, EntityPacket.MOB));
		}
	}
	public void removePlayer(int id){
		for(int i=0;i<Players.size();i++){
			UserHandler sendto = Players.get(i);
			if(sendto.LoggedIn){
				packets.add(new RemoveEntityPacket(id, sendto, EntityPacket.PLAYER));
			}
		}
	}
	
	public void setPlayerCoords(UserHandler u, String region, float x, float y, float rotation){
		u.Region = region;
		u.regionByte = MapParser.parse(region);
		u.x = x;
		u.y = y;
		u.rotation = rotation;
		packets.add(new SetPlayerCoordinatesPacket(u, region, x, y, rotation, this));
		System.out.println("Set coords for "+u.username);
		
		
	}
	
	public void sendPlayerHealth(UserHandler u){
		packets.add(new HealthPacket(u));
		
	}
	
	public void sendPlayerMaxHealth(UserHandler u){
		packets.add(new MaxHealthPacket(u));
	}
	public void sendPlayerMana(UserHandler u){
		packets.add(new ManaPacket(u));
	}
	public void sendPlayerExp(UserHandler u){
		packets.add(new ExpPacket(u));
		
	}
	
	public void levelUpPlayer(UserHandler u){
		if(u.xp >= u.needxp){
			int remainExp = u.xp-u.needxp;
			u.level++;
			u.needxp = u.level*u.level*100;
			u.MaxHealth += 20;
			u.max_mana += 10;
			u.xp = remainExp;
			saveUser(state, u);
			packets.add(new LevelPacket(u));
			packets.add(new NeedxpPacket(u));
			packets.add(new ExpPacket(u));
			packets.add(new MaxHealthPacket(u));
			packets.add(new MaxManaPacket(u));
			
			levelUpPlayer(u);
		}
	}
	public void ChatMessage(String msg, String from){
		if(!msg.startsWith("/")){
		for(int i=0;i<Players.size();i++){
			UserHandler u = Players.get(i);
			packets.add(new ChatMessagePacket(u, from, msg));
		}
	}
	}
	
	public void ChatMessage(String msg, String from, UserHandler u){
		if(msg.startsWith("/")){
		
		
		if(msg.startsWith("/pm")){
			
			String[] parts = msg.split(" ");
			for(int i=0;i<Players.size();i++){
				UserHandler tosend = Players.get(i);
				if(tosend.LoggedIn){
					if(tosend.username.equalsIgnoreCase(parts[1])){
						packets.add(new ChatMessagePacket(tosend, "<[PRIVATE]"+from+">", msg.substring(tosend.username.length()+4)));
						packets.add(new ChatMessagePacket(u, "<[PRIVATE]"+from+">->"+"<"+parts[1]+">", msg.substring(tosend.username.length()+4)));
					}
				}
			}
		}else{
			packets.add(new ChatMessagePacket(u, "<Server>", "No command like that"));
		}
		}
	
	}

	
	
}
