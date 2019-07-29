package com.ginga.gingammorpg.screens;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.ginga.gingammorpg.GingaMMORPG;
import com.ginga.gingammorpg.entity.Item;
import com.ginga.gingammorpg.net.LoginPacket;
import com.ginga.gingammorpg.net.PacketTypes;
import com.ginga.gingammorpg.net.ResponseListener;
import com.ginga.gingammorpg.screens.inventory.InventorySlot;

public class MainMenu implements Screen{
	private Stage stage;
	private Table table;
	private TextButton ExitButton, StartButton;
	private Skin skin, InputFieldSkin;
	private Label username, password;
	private TextField usernameTextField, passwordTextField;
	private TextureAtlas atlas, inputFieldAtlas;
	private BitmapFont white, black;
	public boolean AccountExist = false;
	public PublicKey GlobalpubKey = null;
	private MainMenu menu;
	HttpResponseListener httpResponseListener;
	String NoUser = "User not exists";
	String UserFound = "";
	public Window connectingWindow;
	float x,y;
	byte RegionByte;
	int health, maxhealt, level, xp, needexp, money, mana, maxmana;
	InventorySlot[] Slots = new InventorySlot[8];
	public String salt = "";
	String LoginID = "";

	@Override
	public void show() {
		stage = new Stage();
		menu = this;
		Gdx.input.setInputProcessor(stage);
		
		white = new BitmapFont(Gdx.files.internal("fonts/Arial.fnt"));
		black = new BitmapFont(Gdx.files.internal("fonts/Arial-black.fnt"));
		
		atlas = new TextureAtlas("ui/MainMenu.atlas");
		skin = new Skin(atlas);
		
		
		inputFieldAtlas = new TextureAtlas("ui/LoginTextField.atlas");
		InputFieldSkin = new Skin(Gdx.files.internal("jsons/MenuSkin.json"),inputFieldAtlas);
		
		
		
		
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = black;
		labelStyle.fontColor = Color.BLACK;
		
		username = new Label("Username", labelStyle);
		password = new Label("Password", labelStyle);
		
		table = new Table();
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Texture backTexture = new Texture("ui/mainmenuback.png");
		
		Drawable back = new TextureRegionDrawable(new TextureRegion(backTexture));
		/*back.setRightWidth(Gdx.graphics.getWidth());
		back.setBottomHeight(Gdx.graphics.getHeight());*/
		
		table.setBackground(back);
		TextButtonStyle MainMenuButton = new TextButtonStyle();
		MainMenuButton.up = skin.getDrawable("MenuButtonUp");
		MainMenuButton.down = skin.getDrawable("MenuButtonDown");
		MainMenuButton.over = skin.getDrawable("MenuButtonHover");
		MainMenuButton.pressedOffsetX = -1;
		MainMenuButton.pressedOffsetY = -1;
		
		MainMenuButton.font = black;
		
		usernameTextField = new TextField("", InputFieldSkin, "login");
		passwordTextField = new TextField("", InputFieldSkin, "login");
		
		usernameTextField.setSize(50, 10);
		passwordTextField.setSize(50, 10);
		passwordTextField.setPasswordMode(true);
		passwordTextField.setPasswordCharacter('*');
		
		
		
		
		StartButton = new TextButton("Login", MainMenuButton);
		//StartButton.pad(5);
		
		StartButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				
				connectingWindow.setVisible(true);
				
				
				
				httpResponseListener = new ResponseListener(menu);
				HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder();
				HttpRequest request = httpRequestBuilder.newRequest().method(HttpMethods.GET).url(GingaMMORPG.LOGIN_SERVER).content("getpubkey=1").build();
				HttpRequest requestsalt = httpRequestBuilder.newRequest().method(HttpMethods.GET).url(GingaMMORPG.SALT_SERVER).content("name="+usernameTextField.getText().toString()).build();
				Gdx.net.sendHttpRequest(requestsalt, httpResponseListener);
				Gdx.net.sendHttpRequest(request, httpResponseListener);
			}
		});
		
		ExitButton = new TextButton("Exit", MainMenuButton);
		ExitButton.setPosition(10, 10);
		
		ExitButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		
		connectingWindow = new Window("Connecting...", InputFieldSkin);
		connectingWindow.setVisible(false);
		connectingWindow.pad(75);
		
		connectingWindow.pack();
		
		
		
		table.add(username);
		table.row();
		table.add(usernameTextField);
		table.row();
		table.add(password);
		table.row();
		table.add(passwordTextField);
		table.row();
		table.add(StartButton).padTop(10);
	
		
		stage.addActor(table);
		stage.addActor(ExitButton);
		stage.addActor(connectingWindow);
		
		//End Show
	}

	public void ErrorOut(){
		Gdx.app.postRunnable(new Runnable() {
			
			@Override
			public void run() {
				connectingWindow.reset();
				connectingWindow.pad(75);
				connectingWindow.add("Error while connecting to the login server!").row();
				connectingWindow.pack();
			}
		});
	}
	
	
	public void Login(String resp){
		//
		UserFound = "Account "+usernameTextField.getText().toString()+" has been found!";
		if(resp.equals(md5(NoUser))){
			Gdx.app.postRunnable(new Runnable() {
				
				@Override
				public void run() {
			connectingWindow.reset();
			connectingWindow.pad(75);
			connectingWindow.add("No User Found With This Username/Password").row();
			System.out.println("No User Found With This Username/Password");
			connectingWindow.pack();
				}
			});
			
		}else if(resp.equals(md5(UserFound))){
			new Thread(new Runnable() {
				public void run() {
					final Socket connect;
					try {
						connect = new Socket(GingaMMORPG.GAME_SERVER, GingaMMORPG.GAME_PORT);
						DataOutputStream writer = new DataOutputStream(connect.getOutputStream());
						BufferedReader reader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
						DataInputStream in = new DataInputStream(connect.getInputStream());
						System.out.println(salt);
						LoginPacket login = new LoginPacket(usernameTextField.getText().toString(),sha256(salt+passwordTextField.getText().toString()), GlobalpubKey, LoginID ,writer);
						login.Send();
						while(true){
							int[] bytes = new int[1024];
							
							while(reader.ready()){
								bytes[0] = in.readByte();
								if(bytes[0] == PacketTypes.LOGIN){
									x = in.readFloat();
									y = in.readFloat();
									RegionByte = in.readByte();
									health = in.readInt();
									maxhealt = in.readInt();
									level = in.readInt();
									xp = in.readInt();
									needexp = in.readInt();
									money = in.readInt();
									mana = in.readInt();
									maxmana = in.readInt();
									for(int i=0;i<8;i++){
										int itemid = in.readInt();
										int itemdamage = in.readInt();
										int itemdefense = in.readInt();
										int itemhealth = in.readInt();
										int quantity = in.readInt();
										String itemname = in.readUTF();
										String filename = in.readUTF();
										Item currentitem = new Item(itemid, itemname, itemdamage, itemhealth, itemdefense, filename);
										InventorySlot currslot = new InventorySlot(i, currentitem.ID == 0 ? null : currentitem, quantity);
										Slots[i] = currslot;
										System.out.println(currslot.place+" "+currentitem.ID+" "+currentitem.name);
									}
									break;
								}
								
							}
							if(bytes[0] == PacketTypes.LOGIN){ //49 = 1
								if(RegionByte != 0){
									/*reader.close();
									in.close();*/
									
								System.out.println("Calling new screen...");
								Gdx.app.postRunnable(new Runnable() {
									
									@Override
									public void run() {
										((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen(connect,x,y,RegionByte, usernameTextField.getText().toString(), health, maxhealt, level, xp, needexp, money, mana, maxmana, Slots)); 
										
									}
								});
								Thread.currentThread().interrupt();
								
								return;
								}
							}else{
								Gdx.app.postRunnable(new Runnable() {
									
									@Override
									public void run() {
										
										connectingWindow.reset();
										connectingWindow.pad(75);
										connectingWindow.add("Error while connecting to the server!").row();
										connectingWindow.pack();
										
									}
								});
								Thread.sleep(1000);
							}
							}//while end
					} catch (UnknownHostException e) {
						e.printStackTrace();

					} catch (IOException e) {
						Gdx.app.postRunnable(new Runnable() {
							
							@Override
							public void run() {
								
								connectingWindow.reset();
								connectingWindow.pad(75);
								connectingWindow.add("Can't reach the game server!").row();
								connectingWindow.pack();
								System.out.println("Can't reach the game server!");
							}
						});
						
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			}).start();
		}else{
			connectingWindow.reset();
			connectingWindow.pad(75);
			connectingWindow.add("Can't reach the login server!").row();
			connectingWindow.pack();
			
		}
		
	}
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.draw();
		stage.act(delta);
		
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		stage.dispose();
		atlas.dispose();
		skin.dispose();
		inputFieldAtlas.dispose();
		InputFieldSkin.dispose();
		white.dispose();
		black.dispose();
		
	}
	
	
	//Receive the public key, encrypt the pw, send a request to check if account is exist, if yes, log in.
	public void ValidateAccount(String pub) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, UnsupportedEncodingException{
		//System.out.println(pub.length());
		LoginID = pub.split("@")[1];
		pub = pub.split("@")[0];
		pub = pub.replace("-----BEGIN PUBLIC KEY-----\n", "").replace("-----END PUBLIC KEY-----", "").replaceAll("\n", "");
		byte[] publicByte = Base64.getDecoder().decode(pub);
		
		
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicByte);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		
		PublicKey pkey = keyFactory.generatePublic(keySpec);
		GlobalpubKey = pkey;
		String usertosend = usernameTextField.getText().toString();
		String passtosend = sha256(salt+passwordTextField.getText().toString());
		String finalpass = new String(Base64.getEncoder().encodeToString(encrypt(GlobalpubKey, passtosend)));
		
		//System.out.println(finalpass);
		HttpRequestBuilder builder = new HttpRequestBuilder();
		HttpRequest req = builder.newRequest().method(HttpMethods.POST).url(GingaMMORPG.LOGIN_SERVER).content("username="+usertosend+"&password="+finalpass+"&id="+LoginID).build();
		Gdx.net.sendHttpRequest(req, httpResponseListener);
	}
	
	public byte[] encrypt(PublicKey key, String message) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		
		return cipher.doFinal(message.getBytes());
	}
	
	public byte[] decrypt(PrivateKey key, byte[] encrypted) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		
		return cipher.doFinal(encrypted);
	}
	
public static String md5(String md5){
	
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<array.length;i++){
				sb.append(Integer.toHexString(array[i] & 0xFF | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
public static String sha256(String sha256){
	
	try {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] array = md.digest(sha256.getBytes());
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<array.length;i++){
			sb.append(Integer.toHexString(array[i] & 0xFF | 0x100).substring(1, 3));
		}
		return sb.toString();
	} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	}
	return null;
}
	

}
