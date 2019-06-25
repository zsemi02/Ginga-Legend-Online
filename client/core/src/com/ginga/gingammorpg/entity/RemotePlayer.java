package com.ginga.gingammorpg.entity;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ginga.gingammorpg.screens.GameScreen;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class RemotePlayer extends Creature{
	//public float x,y;
	public int maxHealth, StyleID, level;
	public byte StartingRegion;
	Sprite style, hp;
	public World world;
	Body body;
	Texture t;
	Label nameLabel;
	public Animation<TextureRegion> anim;
	float elapsed;
	public float rotation = 90;
	Texture sheet;
	int hpSizeTorenderMath = 0;
	int hpRenderPixel = 100;
	public Vector2 RealPos = new Vector2();
	
	public RemotePlayer(float width, float height, String name, int ID, float x, float y, byte StartingRegion, int health, int maxHealth, int StyleID, World world, Skin skin, int level, GameScreen game) {
		
		super(width, height, name, ID, skin, game);
		entityType = EntityType.PLAYER;
		position.set(x, y);
		this.Health = health;
		this.maxHealth = maxHealth;
		this.StyleID = StyleID;
		this.StartingRegion = StartingRegion;
		this.world = world;
		this.level = level;
		
		hpSizeTorenderMath = maxHealth/hpRenderPixel;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set(x, y);
		body = world.createBody(bodyDef);
		RealPos.set(x,y);
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
			
			//sheet = new Texture("img/character/style1sheet.png");	//késöbb megcsinálni normálisan Style ID alapján.
			sheet = Assets.defaultChar;
			
			TextureRegion[] animRegion = new TextureRegion[sheet.getWidth()/64];
			for(int i = 0; i<sheet.getWidth()/64; i++){
				animRegion[i] = TextureRegion.split(sheet, 64, 64)[0][i];
			}
			
			anim = new Animation<TextureRegion>(0.1f, animRegion);
			anim.setPlayMode(PlayMode.LOOP);
			
			
			nameLabel = new Label("Lv."+level+" "+name, skin, "namelabel");
			nameLabel.setPosition(body.getPosition().x-nameLabel.getWidth()/2, body.getPosition().y+height+height/2);
			//style.setSize(width*2, height*2);
			Texture hpTexture = new Texture("img/hp.png");
			hp = new Sprite(hpTexture);
			hp.setSize(Health, 6);
			hp.setPosition(getPosition().x-Health/2, getPosition().y+height+3);
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
		position.x += (RealPos.x-position.x)/10;
		position.y += (RealPos.y-position.y)/10;
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
				if(sheet != null)
					sheet.dispose();
				
			}
		});
	}
}
