package com.ginga.gingammorpg.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Creature extends Entity{
	
	public String name = "N/A";
	public int Health;
	Skin skin;
	

	public Creature(float width, float height, String name, int ID, Skin skin) {
		super(width, height, ID);
		this.name = name;
		this.skin = skin;

				/*TextureAtlas a = new TextureAtlas("ui/LoginTextField.atlas");
				skin = new Skin(Gdx.files.internal("jsons/MenuSkin.json"),a);*/
				
				
						//nameLabel = new Label(name, skin, "namelabel");
				
		
	
		
		
		
		
	}

	
	
	
	
	public float getHealth() {
		return Health;
	}

	public void setHealth(int health) {
		Health = health;
	}
	
	public void render(SpriteBatch batch){
		//nameLabel.setPosition((getPosision().x*30+10*30), getPosision().y*30);
		
		//nameLabel.draw(batch, 1);
		
	}
	
	
}
