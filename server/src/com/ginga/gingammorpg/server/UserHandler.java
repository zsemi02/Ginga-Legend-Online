package com.ginga.gingammorpg.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedExceptionAction;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.ginga.gingammorpg.server.packets.EntityPacket;
import com.ginga.gingammorpg.server.packets.LoginPacket;
import com.ginga.gingammorpg.server.packets.MovePacket;
import com.mysql.cj.result.FloatValueFactory;

public class UserHandler extends Thread{
	
	Socket client;
	Statement state;
	String username,  md5pass;
	
	byte[] encryptedPassInput = new byte[1024];
	byte[] encryptedPass;
	int EncryptedLength = 0;
	
	float x,y, rotation=90;
	String Region;
	byte regionByte, startRegionByte, AttackVictimByte;
	int money, id;
	String Startregion;
	int Health, MaxHealth;
	int CharacterStyleID;
	int[][] ItemSlots = new int[8][2];
	int level, xp, needxp, mana, max_mana;
	long lastupdated = System.currentTimeMillis();
	boolean running = true;
	Connection conn;
	Item[][] Items = new Item[8][2];
	String LoginID;
	String PrivateKey;
	java.security.PrivateKey decryptkey;
	
	
	
	//spells
	boolean Spell_Attack=false;
	public boolean LoggedIn = false;
	Server server;
	DataOutputStream output;
	public Thread t = this;
	public UserHandler(Server main,Socket client, Statement state, Connection conn) {
		this.client = client;
		this.state = state;
		this.conn = conn;
		//LOAD ALL DATA FROM DATABASE HERE (with "state")!!!
		
		this.server = main;
		//
		this.start();
		
	}
	
	@Override
	public void run() {
		
			try {
					BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
					output = new DataOutputStream(client.getOutputStream());
					DataInputStream be = new DataInputStream(client.getInputStream());
				
					while(running){
						
						if(input.ready()){
							int i=0;
							int[] b = new int[1024];
							while(input.ready()){
								
								//int inp = input.read();
								byte op = be.readByte();
								b[i] = op;
								i++;
								
								
								if(b[0] == OpCodes.MOVE){
									x = be.readFloat();
									y = be.readFloat();
									rotation = be.readFloat();
									//System.out.println(x+" "+y);
								}else if(b[0] == OpCodes.LOGIN){
									
									username = be.readUTF();
									LoginID = be.readUTF();
									EncryptedLength = be.read(encryptedPassInput);
									encryptedPass = new byte[EncryptedLength];
									System.out.println(LoginID);
									for(int k=0;k<EncryptedLength;k++){
										encryptedPass[k] = encryptedPassInput[k];
									}
								}else if(b[0] == OpCodes.DISCONNECT){
									server.logout(this);
									
								}else if(b[0] == OpCodes.PERFORM_ATTACK){
									AttackVictimByte = be.readByte();
									int VictimID = be.readInt();
									byte AttackType = be.readByte();
									server.ApplyAttack(this, AttackVictimByte, VictimID, AttackType);
								
							}else if(b[0] == OpCodes.CHAT_MESSAGE){
								String msg = be.readUTF();
								if(msg.startsWith("/")){
									server.ChatMessage(msg, username,this);
								}else{
								server.ChatMessage(msg, username);
								}
							}else if(b[0] == OpCodes.SET_COORDINATES){
								String toRegion = be.readUTF();
								float newX = be.readFloat();
								float newY = be.readFloat();
								server.setPlayerCoords(this, toRegion, newX, newY, 90);
							}else if(b[0] == OpCodes.CHANGE_ITEM){
								int place = be.readInt();
								int id = be.readInt();
								Items[place][0] = Item.ParseID(id, server);
								
									int HealthToAdd = 0;
									for(int k=0;k<3;k++){
										if(Items[k][0] == null) continue;
										if(Items[k][0].health > 0){
											HealthToAdd+=Items[k][0].health;
										}
									}
									MaxHealth = 100+HealthToAdd;
									server.sendPlayerMaxHealth(this, MaxHealth);
									if(Health > MaxHealth){
										Health = MaxHealth;
										server.sendPlayerHealth(this, Health);
									}
								
							}
								
							}
							
							
							
							switch(b[0]){ //49 = 1
							case OpCodes.LOGIN:
								
								PreparedStatement keyReq = conn.prepareStatement("SELECT `privkey` from `auth` where hash=?");
								keyReq.setString(1, LoginID);
								ResultSet priv = keyReq.executeQuery();
								int count = 0;
								while(priv.next()){
									count++;
								}
									if(count == 1){
										priv.first();
										PrivateKey = priv.getString("privkey");
										PreparedStatement delete = conn.prepareStatement("DELETE FROM `auth` WHERE hash=?");
										delete.setString(1, LoginID);
										delete.executeUpdate();
									}
								PrivateKey = priv.getString("privkey");
								PrivateKey = PrivateKey.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").replaceAll("\n", "").replaceAll("\\s", "");
								System.out.println(PrivateKey);
								byte[] PrivByte = Base64.getDecoder().decode(PrivateKey);
								PKCS8EncodedKeySpec keyspec = new PKCS8EncodedKeySpec(PrivByte);
								KeyFactory keyFactory = KeyFactory.getInstance("RSA");
								
								try {
									java.security.PrivateKey privkey = keyFactory.generatePrivate(keyspec);
									decryptkey = privkey;
								} catch (InvalidKeySpecException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								
								byte[] decuser = Main.decrypt(decryptkey, encryptedPass);
								md5pass = new String(decuser);
								
								System.out.println(md5pass);
								//decoded
								PreparedStatement resultQuery = conn.prepareStatement("SELECT * FROM `users` WHERE name=? AND password=? ");
								resultQuery.setString(1, username);
								resultQuery.setString(2, md5pass);
								ResultSet resultrow = resultQuery.executeQuery();
								int counter = 0;
								while(resultrow.next()){
									counter++;
								}
								
								if(counter==1){
									
									
									
									
									ResultSet result = state.executeQuery("SELECT * FROM `users` WHERE `name`='"+username+"' AND `password`='"+md5pass+"'");
									result.next();
									
									x = result.getFloat("x");
									y = result.getFloat("y");
									money = result.getInt("money");
									id = result.getInt("id");
									Date date = new Date();
									Timestamp time = new Timestamp(date.getTime());
									Region = result.getString("region");
									regionByte = MapParser.parse(Region);
									Startregion = result.getString("starting_region");
									startRegionByte = MapParser.parse(Startregion);
									Health = result.getInt("health");
									MaxHealth = result.getInt("max_health");
									CharacterStyleID = result.getInt("characterstyle_id");
									level = result.getInt("level");
									xp = result.getInt("xp");
									needxp = result.getInt("need_xp");
									mana = result.getInt("mana");
									max_mana = result.getInt("max_mana");
									Spell_Attack = result.getBoolean("spell_attack");
									state.execute("UPDATE `users` SET `lastlogin`='"+time+"' WHERE `id`='"+id+"'");
									
									ResultSet inventoryResult = state.executeQuery("SELECT * FROM `inventory` WHERE `owner_id`='"+id+"'");
									inventoryResult.next();
									
									for(int k=0;k<ItemSlots.length;k++){
										ItemSlots[k][0] = inventoryResult.getInt("slot_"+k);
										ItemSlots[k][1] = inventoryResult.getInt("slot_"+k+"_quantity");
										//System.out.println("Item id "+ItemSlots[k][0]+" added to "+k+" slot with "+ItemSlots[k][1]+" Amount");
										if(ItemSlots[k][0] == 0) continue;
										for(int g=0;g<server.Items.size();g++){
											if(ItemSlots[k][0] == server.Items.get(g).id){
												Items[k][0] = server.Items.get(g);
											}
											
										}
									}
									
									for(int k=0;k<server.Players.size();k++){
										UserHandler u = server.Players.get(k);
										if(u != this){
											if(u.LoggedIn){
												if(u.id == this.id){
													server.logout(u);
												}
											}
										}
									}
									
									System.out.println(x+" "+y+" "+regionByte);
									
									output.write(OpCodes.LOGIN);
									output.writeFloat(x);
									output.writeFloat(y);
									output.writeByte(regionByte);
									output.writeInt(Health);
									output.writeInt(MaxHealth);
									output.writeInt(level);
									output.writeInt(xp);
									output.writeInt(needxp);
									output.writeInt(money);
									output.writeInt(mana);
									output.writeInt(max_mana);
									for(int k=0;k<ItemSlots.length;k++){
										output.writeInt(ItemSlots[k][0]);
										output.writeInt(ItemSlots[k][1]);
									}
									output.flush();
									System.out.println("Player "+username+" connected to the server");
									server.ChatMessage("Player "+username+" connected to the server", "<Server>");
									
									
								}else{
									output.writeByte(0);
									try {
										client.close();
										server.Players.remove(this);
										this.join();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
								
								
								
								break;
								
							case OpCodes.MOVE:
								LoggedIn = true;
								MovePacket Movedata = (MovePacket) Data.MoveParser(x, y);
								DecimalFormat df = new DecimalFormat("#.####");
								x = Movedata.getX();
								y = Movedata.getY();
								
								
								break;
							}
							
							
							
						}
						
						if(System.currentTimeMillis()-lastupdated >= 1000){
							if(mana < max_mana){
							mana+=1;	//Késöbb mana_regen_speedet csinálni
							server.sendPlayerMana(this);
							}
							if(Health < MaxHealth){
								Health+=1;	
								server.sendPlayerHealth(this, Health);
							}
							lastupdated = System.currentTimeMillis();
						}
						
						
						try {
							Thread.sleep(5);
							
						} catch (InterruptedException e) {
							this.interrupt();
						}
					}
					client.close();
					
	
			} catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | SQLException e) {
				e.printStackTrace();
			}
	}
	
	

}
