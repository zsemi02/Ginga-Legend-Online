package com.ginga.gingammorpg.server.packets;

import java.io.IOException;

import com.ginga.gingammorpg.server.OpCodes;
import com.ginga.gingammorpg.server.UserHandler;

public class ChatMessagePacket extends Packet{
	UserHandler u;
	String from, msg;
	public ChatMessagePacket(UserHandler u, String from, String msg){
		this.u = u;
		this.from = from;
		this.msg = msg;
	}
	public void send(){
		try{
			u.output.writeByte(OpCodes.CHAT_MESSAGE);
			u.output.writeUTF(from);
			u.output.writeUTF(msg);
			u.output.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
