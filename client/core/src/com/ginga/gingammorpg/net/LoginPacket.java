package com.ginga.gingammorpg.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class LoginPacket {
	byte packettype = PacketTypes.LOGIN;
	String user, pass;
	byte[] s;
	DataOutputStream out;
	PublicKey pubKey;
	byte[] encryptedPass;
	
	public LoginPacket(String user,String sha256pass, PublicKey pubKey, DataOutputStream out) {
		this.user = user;
		this.out = out;
		this.pass = sha256pass;
		this.pubKey = pubKey;
		
		s = ("o"+user).getBytes();
		s[0]=packettype;
		try {
			encryptedPass = encrypt(pubKey, pass);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public void Send(){
		try {
			out.writeByte(packettype);
			out.writeUTF(user);
			out.write(encryptedPass);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public byte[] encrypt(PublicKey key, String message) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		
		return cipher.doFinal(message.getBytes());
	}
	
	
}
