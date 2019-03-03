package com.ginga.gingammorpg.net;

import java.io.DataInputStream;
import java.io.IOException;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.ginga.gingammorpg.entity.EntityPacket;
import com.ginga.gingammorpg.entity.Mob;
import com.ginga.gingammorpg.entity.RemotePlayer;
import com.ginga.gingammorpg.screens.GameScreen;
import com.ginga.gingammorpg.screens.MainMenu;

public class MainPacketListener extends Thread{
	DataInputStream in;
	GameScreen game;

	public MainPacketListener(DataInputStream in, GameScreen game) {
		this.in= in;
		this.game = game;
	}

	@Override
	public void run() {
		while(true){
		try {
			
			if(in.available() > 0){
				byte OP = in.readByte();
				if(OP != PacketTypes.ADD_ENTITY){
					System.out.println("Recv. OP: "+OP);
				}
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
								rem.position.x = xRemote;
								rem.position.y = yRemote;
								rem.rotation = Remoterotation;
								rem.Health = RemoteHP;
								rem.maxHealth = RemoteMaxHp;
								rem.level = remoteLevel;
								IsLoaded = true;
								break;
							}
						}//
						if(!IsLoaded){
									if(!game.world.isLocked()){
									RemotePlayer remote = new RemotePlayer(30, 30, name, id, xRemote, yRemote, remoteStartingRegion, RemoteHP, RemoteMaxHp, remStyleid, game.world, game.skin, remoteLevel);
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
								current.Health = MobHealth;
								current.rotation = MobRotation;
								isLoaded = true;
								break;
							}
						}
						if(!isLoaded){
							if(!game.world.isLocked()){
							Mob newMob = new Mob(30, 30, MobId, MobName, Mobx, Moby, MobHealth, MobMaxHealth, MobStyleID, MobDamage, MobLevel, MobXpDrop, MobRotation, game.skin, game.world);
							game.Mobs.add(newMob);
							
							}
						}
						
						
					}
				}else if(OP == PacketTypes.REMOVE_ENTITY){
					byte Removetype = in.readByte();
					int RemovableID = in.readInt();
					System.out.println("Removetype: "+Removetype);
					
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
								game.world.destroyBody(rem.getBody());
								game.Mobs.remove(rem);
								rem.dispose();
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
		}
	}

}
