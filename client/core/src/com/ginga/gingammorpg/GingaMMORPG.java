package com.ginga.gingammorpg;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.ginga.gingammorpg.screens.Splash;


public class GingaMMORPG extends Game {
	
	public static final String TITLE="Ginga Legend Online", VERSION="0.5.0", LOGIN_SERVER="http://192.168.1.101/Ginga/auth.php", GAME_SERVER="192.168.1.101", SALT_SERVER="http://192.168.1.101/Ginga/salt.php";
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
