package com.ginga.gingammorpg.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.URIResolver;


import com.ginga.gingammorpg.server.packets.EntityPacket;
import com.ginga.gingammorpg.server.packets.RemoveEntityPacket;






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
	
	public Server() {
		try {
			//String PrivateKeyStr = "MIIEpAIBAAKCAQEAnSfN836JV3klzC5QdUPhWauUitFnkFvadk44xuVXunz5hmUFA2mSYh9hY4Clo7xtTxjC8Ja/gqDvsaQlv4I+c1652epPygK4JyhxHrIgKEHFyTxa4LXHHW8nezS2LYLuGoWIVeu80xkN7jzgE28XLO5y4SKEpIz9vLLebrLVxD5W4O5kVNxqtuotm1RvH6wDH8g0kdIn7EtS8XyAd8CE+STZ9zT6fzxaOm67R/llajiQJydAExrXbiKss791ibkr8n1Y+WfEofPYDQmlrzpgXIfeDiGwetd8QPyn7kYdW4o3PfES6OApMKfFCQXdpT4Jryez+wjWeB7tVwIExVJ5+QIDAQABAoIBADvbp2QxNBqvOChXE6o2mqTO55sgO3QOqF0bWiDXxdxwMZJw88Hi+jCJh0yg+XYuFOxloAqHQJZ+ug1NWlthPmwvDlbkGlP1STMRAlUQv5LVyoHljS+9zQN3DPCumR0om4xahB1F1vwItPejFC4SyB8DC5qYzTDnytWOw44ia619ACjGyZAs/oT1AlWOm6Mb/rYrykYTOojF3EnuDFOnQ86DkQNTT5gPmVnCSOPNvBa2Rg3vpWrD28TcHBvIAhqAQXVKRI0Z4IwzsIEFSWfRCg0ZP+RqACSwhR7z4Kb33ePthf3QBxSzUUvfJFe8FakNZ3xI3suOVJmRPs8So6vO/gECgYEA0RDHujtUjJsGDly9acAk65BVGN19LlRhlikUdRgBDXQEfUqQL8Evp9U7ArjhcJMnTMoQ6T2w4IZN3thLFaDNTBR4vKX2CjLZSidSEKlyZPWmWy+gP6gXkn5c5EwM7Iof8t0yIbhHwF7lF7bj4ETvxDG5yXsWjcVdw/tMbWCom1kCgYEAwG+yoRXfwDmDmo1IGiykq8W80iHYoUWzP7U0Woea9meJA8+SKIvjNXKj/hIH0LHggA3VzBJB69J6+csV4X6Gtrn7kRzhUAqttvlT9HhkoJO0eqCK3sGN8yjnMCuPmHFORSoFmA+w+1wK1UnChDR7ovusCIYWllBILspaL5ohH6ECgYEAwEKrOlOHjIqgBiM5OYAvM8aWy3gcv7dvyvTaUFiT1zhjTIl+kbwaREDutLEq+SkKki6dYLGP8Nrxz8afPjOTuKx24B3LZ1OdyfjhGluJzNivdNoWh5PgoaK9cGGT3Q+lE+ZhTOs4aOubyLQzWbJrwMRt86DTe+sOMMXwYgHq7HkCgYEAsE3ln1XWEFvhKdjkxS4/lCxuySo/OcoM5oJSu9pfa/8Bdd9XbhRzjsVAYAcO5/H/1/JU/UmA4diN2ItquZRdQc31IEcQWm/eJbQaafFfaArLIEoz0NAOCEhiPyy5u5Wbexx70YwWvsPeHPkd4FfhKjpfq9OFoCNfbpbvt4sDa+ECgYAwxrN5zCGhvUDRoRfhFzPXy0gWe3iQzRy7VXCc/PJH4Lg9k3laj50IAK9sAPBM2sd4czwGc0Q6pIFeGXj7HJwpHXbUurcr7Wf6ChPW6Uvk1kCYIhBC/2sht95qqzrlCRtO6RBZMzdj1o2faMrMzaHnz21UfJ1ocAnn9kIgMMLbMw==";
			String PrivateKeyStr = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCdJ83zfolXeSXM"
+"LlB1Q+FZq5SK0WeQW9p2TjjG5Ve6fPmGZQUDaZJiH2FjgKWjvG1PGMLwlr+CoO+x"
+"pCW/gj5zXrnZ6k/KArgnKHEesiAoQcXJPFrgtccdbyd7NLYtgu4ahYhV67zTGQ3u"
+"POATbxcs7nLhIoSkjP28st5ustXEPlbg7mRU3Gq26i2bVG8frAMfyDSR0ifsS1Lx"
+"fIB3wIT5JNn3NPp/PFo6brtH+WVqOJAnJ0ATGtduIqyzv3WJuSvyfVj5Z8Sh89gN"
+"CaWvOmBch94OIbB613xA/KfuRh1bijc98RLo4Ckwp8UJBd2lPgmvJ7P7CNZ4Hu1X"
+"AgTFUnn5AgMBAAECggEAO9unZDE0Gq84KFcTqjaapM7nmyA7dA6oXRtaINfF3HAx"
+"knDzweL6MImHTKD5di4U7GWgCodAln66DU1aW2E+bC8OVuQaU/VJMxECVRC/ktXK"
+"geWNL73NA3cM8K6ZHSibjFqEHUXW/Ai096MULhLIHwMLmpjNMOfK1Y7DjiJrrX0A"
+"KMbJkCz+hPUCVY6boxv+tivKRhM6iMXcSe4MU6dDzoORA1NPmA+ZWcJI4828FrZG"
+"De+lasPbxNwcG8gCGoBBdUpEjRngjDOwgQVJZ9EKDRk/5GoAJLCFHvPgpvfd4+2F"
+"/dAHFLNRS98kV7wVqQ1nfEjey45UmZE+zxKjq87+AQKBgQDREMe6O1SMmwYOXL1p"
+"wCTrkFUY3X0uVGGWKRR1GAENdAR9SpAvwS+n1TsCuOFwkydMyhDpPbDghk3e2EsV"
+"oM1MFHi8pfYKMtlKJ1IQqXJk9aZbL6A/qBeSflzkTAzsih/y3TIhuEfAXuUXtuPg"
+"RO/EMbnJexaNxV3D+0xtYKibWQKBgQDAb7KhFd/AOYOajUgaLKSrxbzSIdihRbM/"
+"tTRah5r2Z4kDz5Ioi+M1cqP+EgfQseCADdXMEkHr0nr5yxXhfoa2ufuRHOFQCq22"
+"+VP0eGSgk7R6oIrewY3zKOcwK4+YcU5FKgWYD7D7XArVScKENHui+6wIhhaWUEgu"
+"ylovmiEfoQKBgQDAQqs6U4eMiqAGIzk5gC8zxpbLeBy/t2/K9NpQWJPXOGNMiX6R"
+"vBpEQO60sSr5KQqSLp1gsY/w2vHPxp8+M5O4rHbgHctnU53J+OEaW4nM2K902haH"
+"k+Chor1wYZPdD6UT5mFM6zho65vItDNZsmvAxG3zoNN76w4wxfBiAerseQKBgQCw"
+"TeWfVdYQW+Ep2OTFLj+ULG7JKj85ygzmglK72l9r/wF131duFHOOxUBgBw7n8f/X"
+"8lT9SYDh2I3Yi2q5lF1BzfUgRxBab94ltBpp8V9oCssgSjPQ0A4ISGI/LLm7lZt7"
+"HHvRjBa+w94c+R3gV+EqOl+r04WgI19ulu+3iwNr4QKBgDDGs3nMIaG9QNGhF+EX"
+"M9fLSBZ7eJDNHLtVcJz88kfguD2TeVqPnQgAr2wA8Ezax3hzPAZzRDqkgV4ZePsc"
+"nCkddtS6tyvtZ/oKE9bpS+TWQJgiEEL/ayG33mqrOuUJG07pEFkzN2PWjZ9oyszN"
+"oefPbVR8nWhwCef2QiAwwtsz";
			///TODO: GENERATE DINAMICALY
			PrivateKeyStr = PrivateKeyStr.replaceAll("\\s+", "");
			byte[] privateByte = Base64.getDecoder().decode(PrivateKeyStr);
			
			
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateByte);
			//X509EncodedKeySpec keySpec = new X509EncodedKeySpec(privateByte);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			
			PrivateKey pkey = keyFactory.generatePrivate(keySpec);
			privateKey = pkey;
			
			server = new ServerSocket(PORT);
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/gingammorpg?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
			
			
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
														
														EntityPacket e = new EntityPacket(EntityPacket.PLAYER, u2.x, u2.y,u2.rotation, u2.Health, u2.MaxHealth, u2.CharacterStyleID, u2.startRegionByte, u2.id, u2.username,u2.level, u.output);
														boolean ok = e.SendPlayer();
														
														if(!ok){
															logout(u);
														}else{
															//System.out.println("Sent to: "+u.username+" Data: "+u2.username+" "+u2.x+" "+u2.id);
														}
														
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
											boolean MobSentOut = current.SendOut(u.output);
											if(!MobSentOut){
												logout(u);
											}
										}
										}
									}
									
								}//
							}
							}
							for(int i=0;i<Mobs.size();i++){
								Mob mob = Mobs.get(i);
								mob.act();
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
			
			
		} catch (IOException | ClassNotFoundException | SQLException | InvalidKeySpecException | NoSuchAlgorithmException e) {
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
				RemoveEntityPacket rem = new RemoveEntityPacket(u.id, sendto.output, EntityPacket.PLAYER);
				boolean isSent = rem.Send();
				if(!isSent){
					logout(sendto);
				}
					
				
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
		int baseAttackPower = performer.level*10;
		for(int i=0;i<3;i++){
			if(performer.Items[i][0] != null){
				baseAttackPower+=performer.Items[i][0].damage;
			}
		}
		// LATER ADD RARE SPECIAL ITEMS, THAT ADDS BOOSTS.
		
		//	NEED TO REMAKE THIS SECTION. THE WHOLE IS A MESS
		Random r = new Random();
		int finalDamage =baseAttackPower+ r.nextInt(performer.level + performer.level+1)-performer.level;
		if(Attacktype == AttackTypes.SPELL_ATTACK){
		//////// 	SPELL_ATTACK
			if(performer.mana < AttackTypes.SPELL_ATTACK_MANACOST){
				return;
			}
			performer.mana-=AttackTypes.SPELL_ATTACK_MANACOST;
			sendPlayerMana(performer);
		if(EntityType == EntityPacket.MOB){
			Mob victim = null;
			for(int i=0;i<Mobs.size();i++){
				Mob curr = Mobs.get(i);
				if(curr.id == ID){
					victim = curr;
					
					break;
				}
			}
			
			if(victim != null){
				if(!((performer.x - victim.x) < AttackTypes.SPELL_ATTACK_RANGE && (performer.x - victim.x) > -AttackTypes.SPELL_ATTACK_RANGE && (performer.y - victim.y) < AttackTypes.SPELL_ATTACK_RANGE && (performer.y - victim.y) > -AttackTypes.SPELL_ATTACK_RANGE)){
					return;
				}
				if(victim.isDead){
					return;
				}
				
				//************
				victim.target = performer;
				victim.MobState = Mob.MobAIStates.ATTACKING;
				//***********
				
				System.out.println(performer.username+" Attacked "+victim.name+" and dealt "+finalDamage+" Damage");
				//Send out attack animation to everyone
				victim.health-=finalDamage;
				if(victim.health <= 0){
					//DEAD
					victim.isDead = true;
					removeMob(victim.id);
					RespawnMob repsawn = new RespawnMob(victim.id, 2);
					RespawnMobs.add(repsawn);
					performer.xp+=victim.xpdrop;
					sendPlayerExp(performer);
					levelUpPlayer(performer);
				}
				//AI
				
			}
		}else if(EntityType == EntityPacket.PLAYER){
			
			UserHandler victim = null;
			for(int i=0;i<Players.size();i++){
				UserHandler curr = Players.get(i);
				if(curr.id == ID){
					victim = curr;
					
					break;
				}
			}
			if(victim != null){
				if(!((performer.x - victim.x) < AttackTypes.SPELL_ATTACK_RANGE && (performer.x - victim.x) > -AttackTypes.SPELL_ATTACK_RANGE && (performer.y - victim.y) < AttackTypes.SPELL_ATTACK_RANGE && (performer.y - victim.y) > -AttackTypes.SPELL_ATTACK_RANGE)){
					return;
				}
				System.out.println(performer.username+" Attacked "+victim.username+" and dealt "+finalDamage+" Damage");
				
			victim.Health-=finalDamage;
			sendPlayerHealth(victim, victim.Health);
			if(victim.Health <= 0){
				//DEAD
				removePlayer(victim.id);
				//SET TO START POSITION
				setPlayerCoords(victim, victim.Startregion, 100, 100, 90); //Majd a kezdõ koordinátákat a kezdõ helyre állítani.
				victim.Health = victim.MaxHealth;
				sendPlayerHealth(victim, victim.Health);
			}
			}
		}
		
	}		/////////Spell attack end
		
	}
	
	public void removeMob(int id){
		for(int i=0;i<Players.size();i++){
			UserHandler sendto = Players.get(i);
			if(sendto.LoggedIn){
				RemoveEntityPacket rem = new RemoveEntityPacket(id, sendto.output, EntityPacket.MOB);
				boolean isSent = rem.Send();
				if(!isSent){
					logout(sendto);
				}
					
				
			}
		}
	}
	public void removePlayer(int id){
		for(int i=0;i<Players.size();i++){
			UserHandler sendto = Players.get(i);
			if(sendto.LoggedIn){
				RemoveEntityPacket rem = new RemoveEntityPacket(id, sendto.output, EntityPacket.PLAYER);
				boolean isSent = rem.Send();
				if(!isSent){
					logout(sendto);
				}
				
				
			}
		}
	}
	
	public void setPlayerCoords(UserHandler u, String region, float x, float y, float rotation){
		u.Region = region;
		u.regionByte = MapParser.parse(region);
		u.x = x;
		u.y = y;
		u.rotation = rotation;
		
		try {
			u.output.writeByte(OpCodes.SET_COORDINATES);
			u.output.writeByte(u.regionByte);
			u.output.writeUTF(u.Region);
			u.output.writeFloat(u.x);
			u.output.writeFloat(u.y);
			u.output.writeFloat(u.rotation);
			u.output.flush();
			for(int i=0;i<Players.size();i++){
				UserHandler u2 = Players.get(i);
				if(u2.LoggedIn){
					if(u2.regionByte != u.regionByte){
						RemoveEntityPacket rem = new RemoveEntityPacket(u.id, u2.output, EntityPacket.PLAYER);
						rem.Send();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendPlayerHealth(UserHandler u, int hp){
		try {
			u.output.writeByte(OpCodes.SET_HEALTH);
			u.output.writeInt(hp);
			u.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendPlayerMaxHealth(UserHandler u, int Maxhp){
		try {
			u.output.writeByte(OpCodes.SET_MAXHP);
			u.output.writeInt(Maxhp);
			u.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void sendPlayerMana(UserHandler u){
		try {
			u.output.writeByte(OpCodes.SET_MANA);
			u.output.writeInt(u.mana);
			u.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void sendPlayerExp(UserHandler u){
		try {
			u.output.writeByte(OpCodes.SET_EXP);
			u.output.writeInt(u.xp);
			u.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void levelUpPlayer(UserHandler u){
		if(u.xp >= u.needxp){
			int remainExp = u.xp-u.needxp;
			u.level++;
			u.needxp = u.level*u.level*100;
			u.xp = remainExp;
			saveUser(state, u);
			try {
				u.output.writeByte(OpCodes.SET_LEVEL);
				u.output.writeInt(u.level);
				u.output.flush();
				u.output.writeByte(OpCodes.SET_NEEDEDXP);
				u.output.writeInt(u.needxp);
				u.output.flush();
				u.output.writeByte(OpCodes.SET_EXP);
				u.output.writeInt(u.xp);
				u.output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			levelUpPlayer(u);
		}
	}
	public void ChatMessage(String msg, String from){
		if(!msg.startsWith("/")){
		for(int i=0;i<Players.size();i++){
			UserHandler u = Players.get(i);
			try {
				u.output.writeByte(OpCodes.CHAT_MESSAGE);
				u.output.writeUTF(from);
				u.output.writeUTF(msg);
				u.output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
						try {
							tosend.output.writeByte(OpCodes.CHAT_MESSAGE);
							tosend.output.writeUTF("<[PRIVATE]"+from+">");
							tosend.output.writeUTF(msg.substring(tosend.username.length()+4));
							tosend.output.flush();
							u.output.writeByte(OpCodes.CHAT_MESSAGE);
							u.output.writeUTF("<[PRIVATE]"+from+">->"+"<"+parts[1]+">");
							u.output.writeUTF(msg.substring(tosend.username.length()+4));
							u.output.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}else{
			try {
				u.output.writeByte(OpCodes.CHAT_MESSAGE);
				u.output.writeUTF("<Server>");
				u.output.writeUTF("No command like that");
				u.output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		}
	
	}
	
	
	
}
