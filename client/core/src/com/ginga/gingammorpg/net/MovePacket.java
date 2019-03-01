package com.ginga.gingammorpg.net;


import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.ginga.gingammorpg.screens.MainMenu;



public class MovePacket {
	int packettype = PacketTypes.MOVE;
	float x,y, rotation;
	byte[] s = new byte[9];
	DataOutputStream out;
	public MovePacket(float x, float y, float rotation, DataOutputStream out) {
		
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.out = out;
		
		//byte[] b = ByteBuffer.allocate(4).putFloat(x).array();
		 
		/* byte[] b = ByteBuffer.allocate(4).putFloat(x).array();
		for(int i=1; i<b.length+1;i++){
			s[i]=b[i-1];
		}
		byte[] a = ByteBuffer.allocate(4).putFloat(y).array();
		for(int i=b.length+1; i<b.length+a.length+1;i++){
			s[i]=a[i-a.length-1];
		}
		s[0] = PacketTypes.MOVE;
		
		for(int i=1;i<s.length;i++){
			s[i] = (byte) (s[i]&0xFF);
		}
		
		for(int i=1;i<s.length;i++){
			System.out.print((s[i])+" ");
		}
		
		System.out.print(" - "+ByteBuffer.wrap(b).order(ByteOrder.BIG_ENDIAN).getFloat()+"  "+ByteBuffer.wrap(a).order(ByteOrder.BIG_ENDIAN).getFloat());
		System.out.println();*/
	}
	
	public void Send(){
		try {
			out.writeByte(packettype);
			out.writeFloat(x);
			out.writeFloat(y);
			out.writeFloat(rotation);
			out.flush();
		} catch (IOException e) {
			Gdx.app.postRunnable(new Runnable() {
				
				@Override
				public void run() {
					System.out.println("An error occoured while sending the Movement package!");
					((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenu());
				}
			});
			e.printStackTrace();
		}
		
	}

}
