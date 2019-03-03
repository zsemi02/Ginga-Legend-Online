package com.ginga.gingammorpg.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Mob extends Creature{

	
	public int id, health, max_health, styleID, damage, level, xpdrop;

	public float rotation=90;
	Body body;
	World world;
	Label nameLabel;
	Sprite hp;
	Texture sheet;
	float elapsed = 0f;
	public Animation<TextureRegion> anim;
	int hpSizeTorenderMath = 0;
	int hpRenderPixel = 100;
	
	public Mob(float width, float height,int id, String name, float x, float y, int Health, int max_health, int style_id, int damage, int level, int xpDrop, float rotation, Skin skin, World world) {
		super(width, height, name, id, skin);
		position.set(x, y);
		this.max_health = max_health;
		this.styleID = style_id;
		this.damage = damage;
		this.level =level;
		this.xpdrop = xpDrop;
		this.rotation = rotation;
		this.world = world;
		
		hpSizeTorenderMath = max_health/hpRenderPixel;
		
		
		entityType = EntityType.MOB;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set(x, y);
		body = world.createBody(bodyDef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width, height);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		
		body.createFixture(fixtureDef);
		shape.dispose();
		
	}
	public void render(SpriteBatch batch){
		elapsed+=Gdx.graphics.getDeltaTime();
		if(anim == null){
			//anim = GifDecoder.loadGIFAnimation(PlayMode.LOOP, Gdx.files.internal("img/character/style1.gif").read());
			//t = new Texture("img/character/gin1.png");
			//style = new Sprite(t);
			try{
			sheet = new Texture(ParseStyleID.parse(styleID));	//késöbb megcsinálni normálisan Style ID alapján.
			TextureRegion[] animRegion = new TextureRegion[2];
			animRegion[0] = TextureRegion.split(sheet, 64, 64)[0][0];
			animRegion[1] = TextureRegion.split(sheet, 64, 64)[0][1];
			anim = new Animation<TextureRegion>(0.1f, animRegion);
			anim.setPlayMode(PlayMode.LOOP);
			}catch(java.lang.NullPointerException e){
				System.out.println("Styleid crash: "+styleID);
				e.printStackTrace();
			}
			
			nameLabel = new Label("Lv."+level+" "+name, skin, "namelabel");
			
			nameLabel.setPosition(body.getPosition().x-nameLabel.getWidth()/2, body.getPosition().y+height+height/2);
			//style.setSize(width*2, height*2);
			Texture hpTexture = new Texture("img/hp.png");
			hp = new Sprite(hpTexture);
			hp.setSize(Health/hpSizeTorenderMath, 6);
			hp.setPosition(getPosition().x-hp.getWidth()/2, getPosition().y+height+3);
		}
		
		
		if(!world.isLocked()){
		//style.setPosition(getPosition().x-width, getPosition().y-height);
	
		body.setTransform(getPosition().x, getPosition().y, body.getAngle());
		}
		//style.draw(batch);
		
		batch.draw(anim.getKeyFrame(elapsed, true), getPosition().x-width, getPosition().y-height, width, height, width*2, height*2, 1f, 1f, rotation, true);
		
		
		nameLabel.draw(batch, 1);
		hp.draw(batch);
		batch.flush();
		
	}
	
	public void update(){
		hp.setSize(Health/hpSizeTorenderMath, 6);
		hp.setPosition(getPosition().x-hp.getWidth()/2, getPosition().y+height+3);
		nameLabel.setPosition(body.getPosition().x-nameLabel.getWidth()/2, body.getPosition().y+height+height/2);
	}
	
	public Body getBody(){
		return body;
	}
	
	public void dispose(){
		Gdx.app.postRunnable(new Runnable() {
			
			@Override
			public void run() {
				sheet.dispose();
				
			}
		});
	}
	
	
}

