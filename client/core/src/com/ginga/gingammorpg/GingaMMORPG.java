package com.ginga.gingammorpg;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.ginga.gingammorpg.screens.Splash;


public class GingaMMORPG extends Game {
	
	public static final String TITLE="Ginga MMORPG", VERSION="0.0.0", LOGIN_SERVER="http://127.0.0.1/Ginga/auth.php", GAME_SERVER="127.0.0.1", SALT_SERVER="http://127.0.0.1/Ginga/salt.php";
	public static final int GAME_PORT = 7755;
	
	@Override
	public void create () {
		setScreen(new Splash());
		Gdx.graphics.setTitle(TITLE+" : "+VERSION);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
	}
}
