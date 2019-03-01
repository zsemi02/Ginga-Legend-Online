package com.ginga.gingammorpg.server;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Main {

	
	
	public static void main(String[] args) {
		Server server = new Server();
	}
	
	public static byte[] decrypt(PrivateKey key, byte[] encrypted) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		
		return cipher.doFinal(encrypted);
	}

}
