package com.ginga.gingammorpg.server;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.ginga.gingammorpg.server.packets.LoginPacket;
import com.ginga.gingammorpg.server.packets.MovePacket;

public class Data {

	public Data() {
		
	}
	
	
	public static Data parser(int length, int[] b){
		if(b[0] == OpCodes.LOGIN){
			String in ="";
			for(int k=1;k<length;k++ ){
				System.out.println(b[k]);
				in+=Character.toString((char) b[k]);
			}
			return new LoginPacket(in);
		}
		return null;
	}
	
	
	public static Data MoveParser(float x, float y){
		return new MovePacket(x, y);
		
	}
	
}
