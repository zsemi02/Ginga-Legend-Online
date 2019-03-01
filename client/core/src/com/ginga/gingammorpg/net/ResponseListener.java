package com.ginga.gingammorpg.net;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.ginga.gingammorpg.screens.MainMenu;

public class ResponseListener implements HttpResponseListener{

	MainMenu menu;
	public ResponseListener(MainMenu menu) {
		this.menu = menu;
	}
	
	@Override
	public void handleHttpResponse(HttpResponse httpResponse) {
		String resp = httpResponse.getResultAsString();
		System.out.println(resp);
		if(!resp.startsWith("validate")){
			if(resp.startsWith("salt")){
				String response = resp.replace("salt", "");
				menu.salt = response;
			}
				try {
					menu.ValidateAccount(resp);
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		}else{
			String response = resp.replace("validate", "");
			System.out.println(response);
			
			menu.Login(response);
		}
	}

	@Override
	public void failed(Throwable t) {
		
		menu.ErrorOut();
		
	}

	@Override
	public void cancelled() {
		menu.ErrorOut();
	}

}
