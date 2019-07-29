package com.ginga.gingammorpg.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.ginga.gingammorpg.entity.EntityPacket;
import com.ginga.gingammorpg.entity.Item;
import com.ginga.gingammorpg.entity.Mob;
import com.ginga.gingammorpg.entity.RemotePlayer;
import com.ginga.gingammorpg.screens.GameScreen;
import com.ginga.gingammorpg.screens.MainMenu;

public class MainPacketListener implements Runnable{
	DataInputStream in;
	GameScreen game;

	public MainPacketListener(DataInputStream in, GameScreen game) {
		this.in= in;
		this.game = game;
	}

	@Override
	public void run() {
		while(true){
			
				for(int i=0;i<game.packets.size();i++){
					Packet curr = game.packets.get(i);
					if(curr != null)
						curr.send();
					game.packets.remove(curr);
				}
			
		try {
			
			if(in.available() > 0){
				byte OP = in.readByte();
				
				if(OP == PacketTypes.ADD_ENTITY){
					byte type = in.readByte();
					if(type == EntityPacket.PLAYER){
						int id = in.readInt();
						String name = in.readUTF();
						int remStyleid = in.readByte();
						float xRemote = in.readFloat();
						float yRemote = in.readFloat();
						float Remoterotation = in.readFloat();
						byte remoteStartingRegion = in.readByte();
						int RemoteHP = in.readInt();
						int RemoteMaxHp = in.readInt();
						int remoteLevel = in.readInt();
						boolean IsLoaded = false;
						//System.out.println(name+" "+xRemote+" "+yRemote+" "+RemotePlayers.size());
						for(int i=0;i<game.RemotePlayers.size();i++){
							//System.out.println("called");
							RemotePlayer rem = game.RemotePlayers.get(i);
							//System.out.println(rem.ID);
							if(rem.ID == id){
								rem.RealPos.x = xRemote;
								rem.RealPos.y = yRemote;
								rem.rotation = Remoterotation;
								rem.Health = RemoteHP;
								if(rem.Health > RemoteHP){
									game.DamagedEntityEvent(rem,rem.Health-RemoteHP);
								}
								rem.maxHealth = RemoteMaxHp;
								rem.level = remoteLevel;
								IsLoaded = true;
								break;
							}
						}//
						if(!IsLoaded){
									if(!game.world.isLocked()){
									RemotePlayer remote = new RemotePlayer(30, 30, name, id, xRemote, yRemote, remoteStartingRegion, RemoteHP, RemoteMaxHp, remStyleid, game.world, game.skin, remoteLevel, game);
									game.RemotePlayers.add(remote);
									}
									
									//System.out.println("NEW REMOTE PLAYER AT "+x+" "+y+" id: "+id+" - "+RemotePlayers.size());
								
						}
					}else if(type == EntityPacket.MOB){
						int MobId = in.readInt();
						String MobName = in.readUTF();
						float Mobx = in.readFloat();
						float Moby = in.readFloat();
						int MobHealth = in.readInt();
						int MobMaxHealth = in.readInt();
						int MobStyleID = in.readInt();
						int MobDamage = in.readInt();
						int MobLevel = in.readInt();
						int MobXpDrop = in.readInt();
						float MobRotation = in.readFloat();
						boolean isLoaded = false;
						for(int k=0; k<game.Mobs.size();k++){
							Mob current = game.Mobs.get(k);
							if(current.ID == MobId){
								current.position.set(Mobx, Moby);
								if(current.Health > MobHealth){
									game.DamagedEntityEvent(current,current.Health-MobHealth);
								}
								current.Health = MobHealth;
								current.rotation = MobRotation;
								isLoaded = true;
								break;
							}
						}
						if(!isLoaded){
							if(!game.world.isLocked()){
							Mob newMob = new Mob(30, 30, MobId, MobName, Mobx, Moby, MobHealth, MobMaxHealth, MobStyleID, MobDamage, MobLevel, MobXpDrop, MobRotation, game.skin, game.world, game);
							game.Mobs.add(newMob);
							
							}
						}
						
						
					}
				}else if(OP == PacketTypes.REMOVE_ENTITY){
					byte Removetype = in.readByte();
					int RemovableID = in.readInt();
					
					if(Removetype == EntityPacket.PLAYER){
					for(int k = 0;k<game.RemotePlayers.size();k++){
						RemotePlayer rem = game.RemotePlayers.get(k);
						if(rem.ID == RemovableID){
							if(game.Selected == rem){
								game.hud.targetVisible=false;
							}
							game.world.destroyBody(rem.getBody());
							game.RemotePlayers.remove(rem);
							rem.dispose();
						}
					}
					}else if(Removetype == EntityPacket.MOB){
						for(int k = 0;k<game.Mobs.size();k++){
							Mob rem = game.Mobs.get(k);
							if(rem.ID == RemovableID){
								
								if(game.Selected == rem){
									game.hud.targetVisible=false;
								}
								do{
									if(!game.world.isLocked()){
										game.world.destroyBody(rem.getBody());
										game.Mobs.remove(rem);
										rem.dispose();
									}
								}while(game.world.isLocked());
								
							}
						}
					}
				}else if(OP == PacketTypes.DISCONNECT){
					Gdx.app.postRunnable(new Runnable() {
						
						@Override
						public void run() {
							System.out.println("Disconnect packet received");
							((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
							
						
						}
					});
					break;
				}else if(OP == PacketTypes.SET_COORDINATES){
					byte newRegionByte = in.readByte();
					String newRegionString = in.readUTF();
					float newx = in.readFloat();
					float newy = in.readFloat();
					float newRotation = in.readFloat();
					game.RegionByte = newRegionByte;
					game.RegionName = newRegionString;
					game.ChangePosition(newRegionString, newx, newy, newRotation);
					
				}else if(OP == PacketTypes.SET_HEALTH){
					int newHP = in.readInt();
					if (newHP < game.p.Health){
						game.DamagedEntityEvent(game.p, game.p.Health-newHP);
					}
					game.p.Health = newHP;
				
				
			}else if(OP == PacketTypes.SET_MANA){
				int newMana = in.readInt();
				game.p.mana = newMana;
				
				
			
			}else if(OP == PacketTypes.SET_EXP){
				int newExp = in.readInt();
				game.p.xp = newExp;
				
			}else if(OP == PacketTypes.SET_NEEDEDXP){
				int newNeededExp = in.readInt();
				game.p.neededxp = newNeededExp;
				game.hud.setNeededXP(game.p.neededxp);
				
			
			}else if(OP == PacketTypes.SET_MAXHP){
				int newMaxHP = in.readInt();
				game.p.maxhealth = newMaxHP;
				game.hud.setMaxHealth(game.p.maxhealth);
				
			
			}else if(OP == PacketTypes.SET_MAXMANA){
				int newMaxMana = in.readInt();
				game.p.maxmana = newMaxMana;
				game.hud.setMaxMana(game.p.maxmana);
				
			
			}else if(OP == PacketTypes.SET_LEVEL){
				int newlevel = in.readInt();
				game.p.level = newlevel;
				game.hud.level = game.p.level;
				game.p.setnameLabelLevel(newlevel);
				//Play animation
				
			
			}else if(OP == PacketTypes.CHAT_MESSAGE){
				String from = in.readUTF();
				String msg = in.readUTF();
				game.hud.sendMsg("<"+from+">: "+msg);
				
			}else if(OP == PacketTypes.LOOT_ITEMS){
				int size = in.readInt();
				for(int i=0;i<size;i++){
					int id = in.readInt();
					int damage = in.readInt();
					int defense = in.readInt();
					int health = in.readInt();
					String name = in.readUTF();
					String file = in.readUTF();
					Item lootItem = new Item(id, name, damage, health, defense, file);
					game.lootMgr.lootItems.add(lootItem);
				}
				for(Item i : game.lootMgr.lootItems){
					System.out.println(i.name);
				}
			}
			
				
			}
			
			for(int i=0;i<game.RemotePlayers.size();i++){
				RemotePlayer rem = game.RemotePlayers.get(i);
				//System.out.println("local pos: "+p.getPosition().x+", "+p.getPosition().y+" remote: "+rem.getPosition().x+", "+rem.getPosition().y);
				if(((game.p.getPosition().x - rem.getPosition().x) > 1000f || (game.p.getPosition().x - rem.getPosition().x) < -1000f) || ((game.p.getPosition().y - rem.getPosition().y) > 1000f || (game.p.getPosition().y - rem.getPosition().y) < -1000f)){
					//System.out.println("X: "+(p.getPosition().x-rem.getPosition().x)+" Y: "+(p.getPosition().y-rem.getPosition().y));
					game.RemotePlayers.remove(rem);
					
				}
			}
			

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
			
		} // While end
	}

}
