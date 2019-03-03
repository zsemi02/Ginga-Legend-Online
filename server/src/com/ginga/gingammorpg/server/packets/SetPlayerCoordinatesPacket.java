package com.ginga.gingammorpg.server.packets;

import java.io.IOException;

import com.ginga.gingammorpg.server.OpCodes;
import com.ginga.gingammorpg.server.Server;
import com.ginga.gingammorpg.server.UserHandler;

public class SetPlayerCoordinatesPacket extends Packet{
	UserHandler u;
	String region;
	float x, y, rotation;
	Server server;
	public SetPlayerCoordinatesPacket(UserHandler u, String region, float x, float y, float rotation, Server server){
		this.u = u;
		this.region = region;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.server = server;
	}
	
	public void send(){
		try {
			u.output.writeByte(OpCodes.SET_COORDINATES);
			u.output.writeByte(u.regionByte);
			u.output.writeUTF(u.Region);
			u.output.writeFloat(u.x);
			u.output.writeFloat(u.y);
			u.output.writeFloat(u.rotation);
			u.output.flush();
			for(int i=0;i<server.Players.size();i++){
				UserHandler u2 = server.Players.get(i);
				if(u2.LoggedIn){
					if(u2.regionByte != u.regionByte){
						server.packets.add(new RemoveEntityPacket(u.id, u2, EntityPacket.PLAYER));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
