package com.ginga.gingammorpg.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Assets {
	
	public static Texture defaultChar;
	public static BitmapFont Arial;
	
	public Assets(){
		defaultChar = new Texture("img/character/style1sheet.png");
		Arial = new BitmapFont(Gdx.files.internal("assets/fonts/Arial.fnt"), Gdx.files.internal("assets/fonts/Arial.png"), false);
	}
}
