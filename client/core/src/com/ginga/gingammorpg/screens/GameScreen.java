package com.ginga.gingammorpg.screens;

import java.awt.image.PixelGrabber;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.handler.MessageContext.Scope;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ginga.gingammorpg.Attack;
import com.ginga.gingammorpg.Attack.AttackType;
import com.ginga.gingammorpg.InputController;
import com.ginga.gingammorpg.entity.Creature;
import com.ginga.gingammorpg.entity.EntityPacket;
import com.ginga.gingammorpg.entity.Mob;
import com.ginga.gingammorpg.entity.Player;
import com.ginga.gingammorpg.entity.RemotePlayer;
import com.ginga.gingammorpg.net.MapIDs;
import com.ginga.gingammorpg.net.MovePacket;
import com.ginga.gingammorpg.net.PacketTypes;

public class GameScreen implements Screen{

	
	World world;
	Box2DDebugRenderer debugrenderer;
	OrthographicCamera camera, MapCamera;
	Player p;
	SpriteBatch batch;
	DataOutputStream out;
	DataInputStream in;
	Socket socket;
	Vector2 velocity = new Vector2();
	float x,y;
	byte RegionByte;
	String username;
	String RegionName;
	TiledMap region;
	TiledMapRenderer RegionRenderer, MapRenderer;
	int TileSize = 8;
	int[] backlayers = {0}; 
	int[] forelayers = {1}; 
	int MapWidth, MapHeight;
	float mapScale = 3;
	ArrayList<RemotePlayer> RemotePlayers = new ArrayList<RemotePlayer>();
	ArrayList<Mob> Mobs = new ArrayList<Mob>();
	Skin skin;
	int health, MaxHealth, level, xp, needexp, money, mana, maxmana;
	Body hitBody;
	Vector2 testPoint = new Vector2(0,0);
	PlayerHud hud;
	boolean SpellCasted = false, isTravel = false, showMap=false, Showinventory=false;
	Attack attack;
	Creature Selected;
	InputController input;
	Label SwitchMapLablel;
	String ChangeTo = null;
	Vector2 toCoord = null;
	TmxMapLoader loader;
	int[][] Slots;
	InputMultiplexer inputMultiplexer;
	
	
	public GameScreen(Socket socket, float x, float y, byte RegionByte, String username, int health, int maxHealth, int level, int xp,int needexp, int money, int mana, int maxmana, int[][] Slots) {
		this.socket = socket;
		this.health = health;
		this.x = x;
		this.y = y;
		this.RegionByte = RegionByte;
		this.username = username;
		this.MaxHealth = maxHealth;
		this.level = level;
		this.xp = xp;
		this.money = money;
		this.needexp = needexp;
		this.mana = mana;
		this.maxmana = maxmana;
		this.Slots = Slots;
		RegionName = MapIDs.Parsebyte(RegionByte);
		inputMultiplexer = new InputMultiplexer();
		}
	
	
	@Override
	public void show() {
		batch = new SpriteBatch();
		try {
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		world = new World(new Vector2(0,0), false);
		loader = new TmxMapLoader();
		region = loader.load("maps/"+RegionName+".tmx");
		
		MapWidth = region.getProperties().get("width", Integer.class);
		MapHeight = region.getProperties().get("height", Integer.class);
		
		RegionRenderer = new OrthogonalTiledMapRenderer(region,mapScale);
		MapRenderer = new OrthogonalTiledMapRenderer(region,1.5f);
		debugrenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		MapCamera = new OrthographicCamera(MapWidth*TileSize*mapScale, MapHeight*TileSize*mapScale);
		
		//camera.setToOrtho(false, camera.viewportWidth/2, camera.viewportHeight/2);
		
		TextureAtlas a = new TextureAtlas("ui/LoginTextField.atlas");
		skin = new Skin(Gdx.files.internal("jsons/MenuSkin.json"),a);
		SwitchMapLablel = new Label("Press [Enter] to go to XXXXX", skin);
		p = new Player(x, y, health,MaxHealth, 30,30, world, username, 0, out, MapWidth*TileSize*mapScale, MapHeight*TileSize*mapScale, skin, level, mana, maxmana, xp, needexp);
		camera.position.set(p.getPosition().x, p.getPosition().y, 0);
		camera.update();
		attack = new Attack(out);
		
		
		hud = new PlayerHud(this,level, xp,needexp, money, mana,maxmana, health, MaxHealth, Slots);
		inputMultiplexer.addProcessor(hud.stage);
		
		Vector3 tempv3 = new Vector3(Gdx.graphics.getHeight()/2+Gdx.graphics.getHeight()/2/2, Gdx.graphics.getWidth()/2-SwitchMapLablel.getWidth()/2,0);
		tempv3 = camera.unproject(tempv3);
		SwitchMapLablel.setPosition(tempv3.x, tempv3.y);
		SwitchMapLablel.setVisible(false);
	
		//batch.setProjectionMatrix(camera.combined);
		//p = new Player(0, 1, 1, 1, 10, world);
		
		input = new InputController(){
			@Override
			public boolean keyDown(int keycode) {
				switch(keycode){
				case Keys.W:
					if(!world.isLocked())
					velocity.add(0, p.getSpeed());
					break;
				case Keys.A:
					if(!world.isLocked())
					velocity.add(-p.getSpeed(), 0);
					break;
				case Keys.S:
					if(!world.isLocked())
					velocity.add(0, -p.getSpeed());
					break;
				case Keys.D:
					if(!world.isLocked())
					velocity.add(p.getSpeed(), 0);
					break;
				case Keys.ESCAPE:
					try {
						out.writeByte(PacketTypes.DISCONNECT);
						out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Gdx.app.postRunnable(new Runnable() {
						
						@Override
						public void run() {
							((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenu());
						}
					});
					break;
				case Keys.ENTER:
					if(!isTravel){
						if(ChangeTo != null && toCoord != null){
							try {
								out.writeByte(PacketTypes.SET_COORDINATES);
								out.writeUTF(ChangeTo);
								out.writeFloat(toCoord.x);
								out.writeFloat(toCoord.y);
								out.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						isTravel = true;
					}
					break;
				case Keys.M:
					showMap = !showMap;
					break;
				case Keys.E:
					Showinventory = !Showinventory;
					hud.setInventoryvisible(Showinventory);
					hud.Open(PlayerHud.OpenType.INVENTORY);
					//Gdx.input.setInputProcessor(hud.stage);
					break;
				case Keys.NUM_1:
					SpellCasted = true;
					if(Selected != null){
						attack.PerformAttack(p, Selected, AttackType.SPELL_ATTACK);
						
					}
					
					break;
				}
					
				return true;
			}
			@Override
			public boolean keyUp(int keycode) {
				//System.out.println(p.getBody().getPosition().x+" "+p.getBody().getPosition().y);
				switch(keycode){
				case Keys.W:
					if(!world.isLocked())
					velocity.add(0, -p.getSpeed());
					break;
				case Keys.A:
					if(!world.isLocked())
					velocity.add(p.getSpeed(), 0);
					break;
				case Keys.S:
					if(!world.isLocked())
					velocity.add(0, p.getSpeed());
					break;
				case Keys.D:
					if(!world.isLocked())
					velocity.add(-p.getSpeed(), 0);
					break;
				case Keys.T:
					hud.Open(PlayerHud.OpenType.CHAT);
					//Gdx.input.setInputProcessor(hud.stage);
					break;
				case Keys.ENTER:
					isTravel=false;
					break;
				case Keys.NUM_1:
					SpellCasted=false;
					break;
				}
				return true;
			}
			
			QueryCallback callback = new QueryCallback() {
				
				@Override
				public boolean reportFixture(Fixture fixture) {
					if(fixture.getBody() == p.getBody()) return true;
					
					if(fixture.testPoint(testPoint.x, testPoint.y)){
						hitBody = fixture.getBody();
						return false;
					}else{
					return true;
					}
				}
			};
			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				Vector2 click = ScreenCoordsToWorldCoords(screenX, screenY, camera);
				//System.out.println(deb.x+" "+deb.y);
				testPoint.set(click);
				hitBody = null;
				world.QueryAABB(callback, testPoint.x-0.1f, testPoint.y-0.1f, testPoint.x+0.1f, testPoint.y+0.1f);
				
				if(hitBody != null){
					for(int i=0;i<RemotePlayers.size();i++){
						RemotePlayer rem = RemotePlayers.get(i);
						if(rem.getBody() == hitBody){
							Selected = rem;
							hud.targetName = rem.name;
							hud.targetLevel = rem.level;
							hud.targetVisible = true;
							System.out.println("Selected "+rem.name+" "+rem.Health+" "+rem.ID);
							
						}
					}
					for(int i=0;i<Mobs.size();i++){
						Mob mob = Mobs.get(i);
						if(mob.getBody() == hitBody){
							Selected = mob;
							hud.targetName = mob.name;
							hud.targetLevel = mob.level;
							hud.targetVisible = true;
							System.out.println("Selected "+mob.name+" "+mob.Health+" "+mob.ID);
						}
					}
				}else{
					hud.targetVisible=false;
				}
				
				
				
				return true;
			}
			
			
		};
		//Gdx.input.setInputProcessor(input);
		inputMultiplexer.addProcessor(input);
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		ScheduledExecutorService listener = Executors.newSingleThreadScheduledExecutor();
		listener.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					
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
									for(int i=0;i<RemotePlayers.size();i++){
										//System.out.println("called");
										RemotePlayer rem = RemotePlayers.get(i);
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
												if(!world.isLocked()){
												RemotePlayer remote = new RemotePlayer(30, 30, name, id, xRemote, yRemote, remoteStartingRegion, RemoteHP, RemoteMaxHp, remStyleid, world, skin, remoteLevel);
												RemotePlayers.add(remote);
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
									for(int k=0; k<Mobs.size();k++){
										Mob current = Mobs.get(k);
										if(current.ID == MobId){
											current.position.set(Mobx, Moby);
											current.Health = MobHealth;
											current.rotation = MobRotation;
											isLoaded = true;
											break;
										}
									}
									if(!isLoaded){
										if(!world.isLocked()){
										Mob newMob = new Mob(30, 30, MobId, MobName, Mobx, Moby, MobHealth, MobMaxHealth, MobStyleID, MobDamage, MobLevel, MobXpDrop, MobRotation, skin, world);
										Mobs.add(newMob);
										
										}
									}
									
									
								}
							}else if(OP == PacketTypes.REMOVE_ENTITY){
								byte Removetype = in.readByte();
								int RemovableID = in.readInt();
								System.out.println(Removetype);
								
								if(Removetype == EntityPacket.PLAYER){
								for(int k = 0;k<RemotePlayers.size();k++){
									RemotePlayer rem = RemotePlayers.get(k);
									if(rem.ID == RemovableID){
										if(Selected == rem){
											hud.targetVisible=false;
										}
										world.destroyBody(rem.getBody());
										RemotePlayers.remove(rem);
										rem.dispose();
									}
								}
								}else if(Removetype == EntityPacket.MOB){
									for(int k = 0;k<Mobs.size();k++){
										Mob rem = Mobs.get(k);
										if(rem.ID == RemovableID){
											if(Selected == rem){
												hud.targetVisible=false;
											}
											world.destroyBody(rem.getBody());
											Mobs.remove(rem);
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
								RegionByte = newRegionByte;
								RegionName = newRegionString;
								ChangePosition(newRegionString, newx, newy, newRotation);
							}else if(OP == PacketTypes.SET_HEALTH){
								int newHP = in.readInt();
								p.Health = newHP;
							
							
						}else if(OP == PacketTypes.SET_MANA){
							int newMana = in.readInt();
							p.mana = newMana;
							
							
						
						}else if(OP == PacketTypes.SET_EXP){
							int newExp = in.readInt();
							p.xp = newExp;
							
						}else if(OP == PacketTypes.SET_NEEDEDXP){
							int newNeededExp = in.readInt();
							p.neededxp = newNeededExp;
							hud.setNeededXP(p.neededxp);
							
						
						}else if(OP == PacketTypes.SET_MAXHP){
							int newMaxHP = in.readInt();
							p.maxhealth = newMaxHP;
							hud.setMaxHealth(p.maxhealth);
							
						
						}else if(OP == PacketTypes.SET_MAXMANA){
							int newMaxMana = in.readInt();
							p.maxmana = newMaxMana;
							hud.setMaxMana(p.maxmana);
							
						
						}else if(OP == PacketTypes.SET_LEVEL){
							int newlevel = in.readInt();
							p.level = newlevel;
							hud.level = p.level;
							p.setnameLabelLevel(newlevel);
							//Play animation
							
						
						}else if(OP == PacketTypes.CHAT_MESSAGE){
							String from = in.readUTF();
							String msg = in.readUTF();
							hud.sendMsg("<"+from+">: "+msg);
						}
						
							
						}
						
						for(int i=0;i<RemotePlayers.size();i++){
							RemotePlayer rem = RemotePlayers.get(i);
							//System.out.println("local pos: "+p.getPosition().x+", "+p.getPosition().y+" remote: "+rem.getPosition().x+", "+rem.getPosition().y);
							if(((p.getPosition().x - rem.getPosition().x) > 1000f || (p.getPosition().x - rem.getPosition().x) < -1000f) || ((p.getPosition().y - rem.getPosition().y) > 1000f || (p.getPosition().y - rem.getPosition().y) < -1000f)){
								//System.out.println("X: "+(p.getPosition().x-rem.getPosition().x)+" Y: "+(p.getPosition().y-rem.getPosition().y));
								RemotePlayers.remove(rem);
								
							}
						}
						
						/*try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							
								Thread.currentThread().interrupt();;
							
							e.printStackTrace();
						}*/
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		},0,1000/40,TimeUnit.MILLISECONDS);
		
		
		
		
		
	}

	
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	
			camera.position.set(p.getPosition().x, p.getPosition().y, 0);
		if(camera.position.x-camera.viewportWidth/2 < 0){
			camera.position.x=camera.viewportWidth/2;
			
		}
		if(camera.position.y-camera.viewportHeight/2 < 0){
			camera.position.y=camera.viewportHeight/2;
	
		}
		if(camera.position.x+camera.viewportWidth/2 > MapWidth*TileSize*mapScale){
			camera.position.x = MapWidth*TileSize*mapScale-camera.viewportWidth/2;
		}
		if(camera.position.y+camera.viewportHeight/2 > MapHeight*TileSize*mapScale){
			camera.position.y = MapHeight*TileSize*mapScale-camera.viewportHeight/2;
		}
		camera.update();
		// Map travel
		if(camera.position.y == camera.viewportHeight/2){
			toCoord = new Vector2(p.getPosition().x, MapHeight*TileSize*mapScale-p.height*mapScale-10);
			switch(RegionByte){
			case MapIDs.FUTAGO:
				//Go to Kai
				SwitchMapLablel.setText("Press [Enter] to go to Kai");
				SwitchMapLablel.setVisible(true);
				ChangeTo = "Kai";
				break;
			case MapIDs.HOKKAIDO:
				SwitchMapLablel.setText("Press [Enter] to go to Mutsu");
				SwitchMapLablel.setVisible(true);
				ChangeTo = "Mutsu";
				break;
			case MapIDs.KAI:
				SwitchMapLablel.setText("Press [Enter] to go to Mie");
				SwitchMapLablel.setVisible(true);
				ChangeTo = "Mie";
				break;
			case MapIDs.KYUSHU:
				SwitchMapLablel.setText("There is only the endless ocean... So beautiful");
				SwitchMapLablel.setVisible(true);
				ChangeTo = null;
				break;
			case MapIDs.MIE:
				SwitchMapLablel.setText("Press [Enter] to go to Shikoku");
				SwitchMapLablel.setVisible(true);
				ChangeTo = "Shikoku";
				break;
			case MapIDs.MUTSU:
				SwitchMapLablel.setText("Press [Enter] to go to Futago Pass");
				SwitchMapLablel.setVisible(true);
				ChangeTo = "Futago";
				break;
			case MapIDs.SHIKOKU:
				SwitchMapLablel.setText("Press [Enter] to go to Kyushu");
				SwitchMapLablel.setVisible(true);
				ChangeTo = "Kyushu";
				break;
			default:
				break;
			}
		}else if(camera.position.y == MapHeight*TileSize*mapScale-camera.viewportHeight/2){
			toCoord = new Vector2(p.getPosition().x, p.height*mapScale+10);
			switch(RegionByte){
			case MapIDs.FUTAGO:
				//Go to Mutsu
				SwitchMapLablel.setText("Press [Enter] to go to Mutsu");
				SwitchMapLablel.setVisible(true);
				ChangeTo = "Mutsu";
				break;
			case MapIDs.HOKKAIDO:
				SwitchMapLablel.setText("There is only the endless ocean... So beautiful");
				SwitchMapLablel.setVisible(true);
				ChangeTo = null;
				break;
			case MapIDs.KAI:
				SwitchMapLablel.setText("Press [Enter] to go to Futago Pass");
				SwitchMapLablel.setVisible(true);
				ChangeTo = "Futago";
				break;
			case MapIDs.KYUSHU:
				SwitchMapLablel.setText("Press [Enter] to go to Shikoku");
				SwitchMapLablel.setVisible(true);
				ChangeTo = "Shikoku";
				break;
			case MapIDs.MIE:
				SwitchMapLablel.setText("Press [Enter] to go to Kai");
				SwitchMapLablel.setVisible(true);
				ChangeTo = "Kai";
				break;
			case MapIDs.MUTSU:
				SwitchMapLablel.setText("Press [Enter] to go to Hokkaido");
				SwitchMapLablel.setVisible(true);
				ChangeTo = "Hokkaido";
				break;
			case MapIDs.SHIKOKU:
				SwitchMapLablel.setText("Press [Enter] to go to Mie");
				SwitchMapLablel.setVisible(true);
				ChangeTo = "Mie";
				break;
			default:
				break;
			}
		}else{
			SwitchMapLablel.setVisible(false);
			
			ChangeTo = null;
			toCoord = null;
			
		}
		
		batch.setProjectionMatrix(camera.combined);
		RegionRenderer.setView(camera);
		RegionRenderer.render(backlayers);
		
		if(!world.isLocked()){
			p.getBody().setLinearVelocity(velocity);
			
		}
		
		world.step(1/60f, 8, 6);
		if(!world.isLocked()){
		debugrenderer.render(world, camera.combined);
		p.update();
		batch.begin();
		//Gdx.gl.glDepthMask(true);
		p.render(batch);
		for(int i=0;i<RemotePlayers.size();i++){
			RemotePlayer rem = RemotePlayers.get(i);
			rem.render(batch);
			rem.update();
			
		}
		for(int i=0;i<Mobs.size();i++){
			Mob remMob = Mobs.get(i);
			remMob.render(batch);
			remMob.update();
			
		}
		if(region.getLayers().size()>1)
			RegionRenderer.render(forelayers);
		batch.flush();
		batch.end();
		
		}
		hud.render();
		batch.begin();
		Vector3 tempv3 = new Vector3(Gdx.graphics.getWidth()/2-SwitchMapLablel.getWidth()/2, Gdx.graphics.getHeight()/2+Gdx.graphics.getHeight()/2/2,0);
		tempv3 = camera.unproject(tempv3);
		SwitchMapLablel.setPosition(tempv3.x, tempv3.y);
		if(SwitchMapLablel.isVisible())
			SwitchMapLablel.draw(batch, 1);
		
		if(showMap){
			MapRenderer.setView(MapCamera);
			MapRenderer.render();
		}
		
		
		
		batch.flush();
		batch.end();
		
		
		
	}
	
	

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		world.dispose();
		debugrenderer.dispose();
		region.dispose();
		skin.dispose();
	}
	
	public Vector2 ScreenCoordsToWorldCoords(float x, float y, OrthographicCamera camera){
		Vector3 v = camera.unproject(new Vector3(x,y,0));
		return new Vector2(v.x, v.y);
	}
	
	public void ChangePosition(final String regionName, float x, float y, float rotation){
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
			
				
				region = new TmxMapLoader().load("maps/"+regionName+".tmx");
				MapWidth = region.getProperties().get("width", Integer.class);
				MapHeight = region.getProperties().get("height", Integer.class);
				p.mapHeight = MapHeight*TileSize*mapScale;
				p.mapWidth = MapWidth*TileSize*mapScale;
				camera.update();
				
				RegionRenderer = new OrthogonalTiledMapRenderer(region, mapScale);
				MapRenderer = new OrthogonalTiledMapRenderer(region,1.5f);
				MapCamera.update();
			}
		});
		
		do{
		if(!world.isLocked())
			p.getBody().setTransform(x, y, 0);
		}while(world.isLocked());
		p.getPosition().set(x, y);
		p.rotation = rotation;
		
	
		MovePacket m = new MovePacket(x, y, rotation, out);
		m.Send();
		for(int i=0;i<Mobs.size();i++){
			world.destroyBody(Mobs.get(i).getBody());
			Mobs.get(i).dispose();
		}
		for(int i=0;i<RemotePlayers.size();i++){
			world.destroyBody(RemotePlayers.get(i).getBody());
			RemotePlayers.get(i).dispose();
		}
		Mobs.clear();
		RemotePlayers.clear();
		
		
		
		
	}

}
