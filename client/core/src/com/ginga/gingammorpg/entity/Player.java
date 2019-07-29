package com.ginga.gingammorpg.entity;


import java.io.DataOutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.ginga.gingammorpg.net.MovePacket;
import com.ginga.gingammorpg.screens.GameScreen;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Player extends Creature{
	
	Body playerbody;
	Vector2 lastPos = new Vector2();
	DataOutputStream out;
	Sprite style, hp;
	String username;
	public float mapWidth, mapHeight;
	float speed = 100;
	Label nameLabel;
	World world;
	public int level, maxhealth, mana, maxmana, xp, neededxp;
	float elapsed = 0f;
	Animation<TextureRegion> anim;
	public float rotation = 90;
	int hpSizeTorenderMath = 0;
	int hpRenderPixel = 100;
	GameScreen game;
	
	
	public Player(float x, float y, int health, int maxhealth, float width, float height, World world, String name, int ID, DataOutputStream out, float mapWidth, float mapHeight, Skin skin, int level, int mana, int maxmana, int xp, int neededXp, GameScreen game){
		super(width, height, name, ID, skin, game);
		getPosition().x = x;
		getPosition().y = y;
		lastPos.set(getPosition());
		Health = health;
		this.out = out;
		this.username = name;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.world = world;
		this.level = level;
		this.maxhealth = maxhealth;
		this.mana = mana;
		this.maxmana = maxmana;
		this.xp = xp;
		this.neededxp = neededXp;
		this.game = game;
		
		hpSizeTorenderMath = maxhealth/hpRenderPixel;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.x = getPosition().x;
		bodyDef.position.y = getPosition().y;
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		playerbody = world.createBody(bodyDef);
		
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width, height);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		
		playerbody.createFixture(fixtureDef);
		shape.dispose();
		
		entityType = EntityType.PLAYER;
		
	
		//Texture sheet = new Texture("img/character/style1sheet.png");	//késöbb megcsinálni normálisan Style ID alapján.
		Texture sheet = new Texture("img/character/style1sheet.png");	//késöbb megcsinálni normálisan Style ID alapján.
		
		TextureRegion[] animRegion = new TextureRegion[sheet.getWidth()/64];
		TextureRegion[][] split = TextureRegion.split(sheet, 64, 64);	
		for(int i=0; i < sheet.getWidth()/64; i++){
			animRegion[i] = split[0][i];
		}									
		anim = new Animation<TextureRegion>(0.1f, animRegion);
		anim.setPlayMode(PlayMode.LOOP);
		
		//style = new Sprite(t);
		//style.setSize(width*2, height*2);
		nameLabel = new Label("Lv."+level+" "+name, skin, "namelabel");
		
		Texture hpTexture = new Texture("img/hp.png");
		hp = new Sprite(hpTexture);
		hp.setSize(Health/hpSizeTorenderMath, 6);
		hp.setPosition(getPosition().x-Health/2, getPosition().y+height+3);
		
		
	}
	public Body getBody(){
		return playerbody;
	}
	
	public void render(SpriteBatch batch){
		elapsed+=Gdx.graphics.getDeltaTime();
		if(playerbody.getLinearVelocity().x ==0 && playerbody.getLinearVelocity().y == 0){
			anim.setFrameDuration(1f);
		}else{
			anim.setFrameDuration(0.1f);
			CalculateRotation();
		}
		
		batch.draw(anim.getKeyFrame(elapsed,true), getPosition().x-width, getPosition().y-height, width, height,width*2, height*2,1f,1f,rotation,true);

		//style.draw(batch);
		//batch.draw(anim.getKeyFrame(elapsed,true), getPosition().x-width, getPosition().y-height, width*2, height*2);
		
		nameLabel.draw(batch, 1);
		hp.draw(batch);
		batch.flush();
	}
	
	public void update(){
		if(!world.isLocked()){
		if(playerbody.getPosition().x-width < 0){
			playerbody.setTransform(width, playerbody.getPosition().y, playerbody.getAngle());
			
			
		}
		if(playerbody.getPosition().y-height < 0){
			playerbody.setTransform(playerbody.getPosition().x, height, playerbody.getAngle());
			
		}
		if(playerbody.getPosition().x+width > mapWidth){
			playerbody.setTransform(mapHeight-height, playerbody.getPosition().y, playerbody.getAngle());
			
		}
		if(playerbody.getPosition().y+height > mapHeight){
			
			playerbody.setTransform(playerbody.getPosition().x, mapHeight-height, playerbody.getAngle());
			
		}
		
		getPosition().set(playerbody.getPosition());
		nameLabel.setPosition(playerbody.getPosition().x-nameLabel.getWidth()/2, playerbody.getPosition().y+height+height/2);
		}
		//style.setX(getPosition().x-width);
		//style.setY(getPosition().y-height);
		if(lastPos.x != getPosition().x || lastPos.y != getPosition().y){
			if(((lastPos.x-getPosition().x)>0.001f || (getPosition().x-lastPos.x)>0.001f) ||(lastPos.y-getPosition().y)>0.001f || (getPosition().y-lastPos.y)>0.001f){
			game.packets.add(new MovePacket(getPosition().x, getPosition().y,rotation, out));
			lastPos.set(getPosition());
			}
		}
		hp.setSize(Health/hpSizeTorenderMath, 6);
		hp.setPosition(getPosition().x-hp.getWidth()/2, getPosition().y+height+3);
		
		
		
		
		
	}
	
	public void setnameLabelLevel(final int level){
		Gdx.app.postRunnable(new Runnable() {
			
			@Override
			public void run() {
				nameLabel.setText("Lv."+level+" "+name);
				
			}
		});
	}
	public float getSpeed(){
		return speed;
	}
	
	void CalculateRotation(){
		if(playerbody.getLinearVelocity().x > 0 && playerbody.getLinearVelocity().y ==0){
			rotation = 0f;
		}
		if(playerbody.getLinearVelocity().x < 0 && playerbody.getLinearVelocity().y ==0){
			rotation = 180f;
		}
		if(playerbody.getLinearVelocity().y > 0 && playerbody.getLinearVelocity().x == 0){
			rotation = 90f;
		}
		if(playerbody.getLinearVelocity().y < 0 && playerbody.getLinearVelocity().x == 0){
			rotation = -90f;
		}
		if(playerbody.getLinearVelocity().x > 0 && playerbody.getLinearVelocity().y >0){
			rotation = 45f;
		}
		if(playerbody.getLinearVelocity().x < 0 && playerbody.getLinearVelocity().y <0){
			rotation = 225;
		}
		if(playerbody.getLinearVelocity().x > 0 && playerbody.getLinearVelocity().y <0){
			rotation = -45f;
		}
		if(playerbody.getLinearVelocity().x < 0 && playerbody.getLinearVelocity().y >0){
			rotation = 135f;
		}
	}
	

	
}
