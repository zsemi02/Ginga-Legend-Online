package com.ginga.gingammorpg.entity;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ginga.gingammorpg.DamageRender;
import com.ginga.gingammorpg.screens.GameScreen;

public class Creature extends Entity{
	
	public String name = "N/A";
	public int Health;
	Skin skin;
	ArrayList<DamageRender> DamagesToRender = new ArrayList<DamageRender>();
	int DmgAnimateTime = 5000;
	long time;
	long delta;
	BitmapFont dmgFont;
	GameScreen game;
	public Creature(float width, float height, String name, int ID, Skin skin, GameScreen game) {
		super(width, height, ID);
		this.name = name;
		this.skin = skin;
		this.game = game;
				/*TextureAtlas a = new TextureAtlas("ui/LoginTextField.atlas");
				skin = new Skin(Gdx.files.internal("jsons/MenuSkin.json"),a);*/	
						//nameLabel = new Label(name, skin, "namelabel");
		time = System.currentTimeMillis();
		delta = 0;
		dmgFont = game.assets.Arial;
	}

	
	
	public float getHealth() {
		return Health;
	}

	public void setHealth(int health) {
		Health = health;
	}
	public void render(SpriteBatch batch) {}
	public void render(SpriteBatch batch, Vector2 coords){
		if (!DamagesToRender.isEmpty()){
			for (int i = 0; i < DamagesToRender.size(); i++){
				if(!DamagesToRender.get(i).isFinished){

					
					dmgFont.draw(batch,
							Integer.toString(DamagesToRender.get(i).DamageValue),
							DamagesToRender.get(i).loc.x,
							DamagesToRender.get(i).loc.y);
					batch.flush();
					DamagesToRender.get(i).update();
				}else {
					DamagesToRender.remove(i);
				}
			}
		}

	}
	public void AddDamageToRender(int dmg, Vector2 coords) { DamagesToRender.add(new DamageRender(dmg, DmgAnimateTime, coords, game)); }
	
}
