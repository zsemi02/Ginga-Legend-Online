package com.ginga.gingammorpg;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.ginga.gingammorpg.screens.Splash;


public class GingaMMORPG extends Game {
	static String IP = "127.0.0.1";
	public static final String TITLE="Ginga Legend Online", VERSION="0.5.0", LOGIN_SERVER="http://"+IP+"/Ginga/auth.php", GAME_SERVER=IP, SALT_SERVER="http://"+IP+"/Ginga/salt.php";
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
