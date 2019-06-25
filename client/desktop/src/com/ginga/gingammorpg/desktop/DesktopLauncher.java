package com.ginga.gingammorpg.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.ginga.gingammorpg.GingaMMORPG;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Ginga MMORPG";
		config.width = 1024;
		config.height = 768;
		config.fullscreen = false;
		/*config.width = 512;
		config.height = 384;*/
		config.resizable = false;
		config.useGL30 = true;
		ShaderProgram.prependVertexCode = "#version 140\n#define varying out\n#define attribute in\n";
		  ShaderProgram.prependFragmentCode = "#version 140\n#define varying in\n#define texture2D texture\n#define gl_FragColor fragColor\nout vec4 fragColor;\n";
		
		new LwjglApplication(new GingaMMORPG(), config);
	}
}
