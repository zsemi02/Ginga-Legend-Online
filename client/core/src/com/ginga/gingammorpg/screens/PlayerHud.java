package com.ginga.gingammorpg.screens;


import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.io.IOException;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Align;
import com.ginga.gingammorpg.entity.Item;
import com.ginga.gingammorpg.net.PacketTypes;
import com.ginga.gingammorpg.screens.inventory.Inventory;
import com.ginga.gingammorpg.screens.inventory.InventorySlot;


//Inventory: Inventory ablak megjelenik az ablak közepén, ráhúz egy Table-t, majd DragAndDrop-ot.


public class PlayerHud{
	Stage stage;
	Skin HudSkin, MenuSkin;
	Label levelLabel, moneyLabel, hpLabel, expLabel, manaLabel, targetNameLabel, targetLevelLabel;
	int level, exp, money, mana, maxmana ,hp, maxHP, needexp;
	ProgressBar hpProgress, manaProgress, expProgress;
	GameScreen screen;
	Table targetTable, Chattext;
	String targetName = "N/A";
	int targetLevel = 0;
	boolean targetVisible = false;
	ScrollPane chatWindow;
	TextField ChatTextField;
	Window InventoryWindow;
	Inventory inventory;
	int[][] Slots;
	DragAndDrop dragndrop = new DragAndDrop();
	
	public static enum OpenType{
		CHAT,
		INVENTORY
	}
	OpenType ReasonForOpen = OpenType.CHAT;
	
	
	public PlayerHud(final GameScreen screen,int level, int exp,int needexp, int money, int mana,int maxmana, int hp, int maxHP,int[][] Slots){
		this.level = level;
		this.exp = exp;
		this.money = money;
		this.mana = mana;
		this.hp = hp;
		this.maxHP = maxHP;
		this.maxmana = maxmana;
		this.needexp = needexp;
		this.screen = screen;
		this.Slots = Slots;
		stage = new Stage();
		
		
		
		
		
		TextureAtlas atlas = new TextureAtlas("ui/hud.atlas");
		TextureAtlas menu = new TextureAtlas("ui/LoginTextField.atlas");
		Texture spell_attack = new Texture("ui/spell_attack.png");
		Image spell_attack_sprite = new Image(spell_attack);
		
		MenuSkin = new Skin(Gdx.files.internal("jsons/MenuSkin.json"), menu);
		
		
		InventoryWindow = new Window("Inventory", MenuSkin);
		InventoryWindow.setPosition(100, 100);
		//
		inventory = new Inventory(InventoryWindow, stage, Slots);
		InventoryWindow.setVisible(false);
		for(int i=0;i<inventory.slots.length;i++){
			if(inventory.slots[i].getItem() == null) continue;
			if(inventory.slots[i].getItem().ID != 0)
					inventory.slots[i].addListener(new TextTooltip(inventory.slots[i].getItem().name, MenuSkin));
		
		}
		
		//
		HudSkin = new Skin(Gdx.files.internal("jsons/HudSkin.json"), atlas);
		ProgressBarStyle pbStyle = new ProgressBarStyle();
		ProgressBarStyle pbmanaStyle = new ProgressBarStyle();
		ProgressBarStyle pbexpStyle = new ProgressBarStyle();
		pbStyle.background = HudSkin.getDrawable("hud");
		pbmanaStyle.background = HudSkin.getDrawable("hud");
		pbexpStyle.background = HudSkin.getDrawable("hud");
		Pixmap pixmap = new Pixmap(1, 20, Format.RGB888);
		Pixmap pixmapmana = new Pixmap(1, 20, Format.RGB888);
		Pixmap pixmapexp = new Pixmap(1, 20, Format.RGB888);
		pixmap.setColor(Color.RED);
		pixmap.fill();
		pixmapmana.setColor(Color.BLUE);
		pixmapmana.fill();
		pixmapexp.setColor(Color.GREEN);
		pixmapexp.fill();
		Drawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
		
		pbStyle.knobBefore = drawable;
		pixmap.dispose();
		drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmapmana)));
		pbmanaStyle.knobBefore = drawable;
		pixmapmana.dispose();
		drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmapexp)));
		pbexpStyle.knobBefore = drawable;
		pixmapexp.dispose();
		
		hpProgress = new ProgressBar(0, maxHP, 1, false, pbStyle);
		manaProgress = new ProgressBar(0,maxmana,1,false,pbmanaStyle);
		expProgress = new ProgressBar(0,needexp,1,false,pbexpStyle);
		
		levelLabel = new Label("Level   "+String.valueOf(level), HudSkin);
		expLabel = new Label(String.valueOf(exp), HudSkin);
		moneyLabel = new Label("Gold   "+String.valueOf(money), HudSkin);
		manaLabel = new Label(String.valueOf(mana), HudSkin);
		hpLabel = new Label(String.valueOf(hp), HudSkin);
		levelLabel.setAlignment(Align.center);
		moneyLabel.setAlignment(Align.center);
		
		hpProgress.setValue(hp);
		hpProgress.setWidth(100);
		hpProgress.setHeight(30);
		
		manaProgress.setValue(mana);
		manaProgress.setWidth(100);
		manaProgress.setHeight(30);
		
		expProgress.setValue(0);
		expProgress.setWidth(100);
		expProgress.setHeight(30);
		
		
		Table mainHUDTable = new Table();
		mainHUDTable.setFillParent(true);
			mainHUDTable.setDebug(false);
			
			Chattext = new Table(HudSkin);
			/*Table topLeftTable = new Table();
			//topLeftTable.setFillParent(true);
				topLeftTable.setDebug(true);
				topLeftTable.add(levelLabel).expand().row();
				topLeftTable.add(moneyLabel);
				topLeftTable.top().left().padTop(0).padLeft(0);
			Table bottomLeftTable = new Table();
			//bottomLeftTable.setFillParent(true);
				bottomLeftTable.setDebug(true);
				bottomLeftTable.add(hpLabel).expand().row();
				bottomLeftTable.add(manaLabel).row();
				bottomLeftTable.add(expLabel);
				bottomLeftTable.bottom().left().padBottom(0).padLeft(0);*/
			Table SpellHud = new Table();
			//SpellHud.setFillParent(true);
				SpellHud.setDebug(false);
				SpellHud.left();
				SpellHud.background(HudSkin.getDrawable("hud"));
				
				SpellHud.add(spell_attack_sprite).size(expProgress.getHeight(), expProgress.getHeight());
				/*
				 * Check the level, and add the other spells too!
				 * */
			
			/*mainHUDTable.add(topLeftTable).expand().top().left();
			mainHUDTable.add(bottomLeftTable).expand().bottom().left();*/
			//mainHUDTable.add(SpellHud).bottom();
			mainHUDTable.left();
			mainHUDTable.add(levelLabel).width(hpProgress.getWidth()).row();
			mainHUDTable.add(moneyLabel).width(hpProgress.getWidth()).row();
			mainHUDTable.row().padTop(Gdx.graphics.getHeight()-(moneyLabel.getHeight()+levelLabel.getHeight()+hpProgress.getHeight()+manaProgress.getHeight()+expProgress.getHeight()));
			
			mainHUDTable.add(hpProgress);
			ChatTextField = new TextField("", MenuSkin,"login");
			//ChatTextField.setFocusTraversal(true);
			ChatTextField.setTextFieldListener(new TextFieldListener() {
				
				@Override
				public void keyTyped(TextField textField, char c) {
					
					if(c == '\n' || c=='\r'){
						//sendMsg(ChatTextField.getText());
						
						try {
							screen.out.writeByte(PacketTypes.CHAT_MESSAGE);
							screen.out.writeUTF(ChatTextField.getText());
							screen.out.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
						ChatTextField.setText("");
						stage.unfocusAll();
						//Gdx.input.setInputProcessor(screen.input);
					}
					
				}
			});
			//stage.setKeyboardFocus(ChatTextField);
			//ChatTextField.getOnscreenKeyboard().show(true);
			chatWindow = new ScrollPane(Chattext);
			chatWindow.setWidth(300);
			chatWindow.scrollTo(0, 0, 0, 0);
			
			mainHUDTable.add(chatWindow).row();
			
			
			mainHUDTable.add(manaProgress);
			mainHUDTable.add(ChatTextField).row();
			mainHUDTable.add(expProgress);
			mainHUDTable.add(SpellHud).width(Gdx.graphics.getWidth()-hpProgress.getWidth()).height(expProgress.getHeight());
			mainHUDTable.pack();
			
			
			targetTable = new Table();
			targetTable.setFillParent(true);
			targetTable.right().top();
			targetNameLabel = new Label("N/A", HudSkin);
			targetLevelLabel = new Label("N/A", HudSkin);
			targetNameLabel.setAlignment(Align.center);
			targetLevelLabel.setAlignment(Align.center);
			targetTable.add(targetNameLabel).top().right().width(hpProgress.getWidth()).row();
			targetTable.add(targetLevelLabel).top().right().width(hpProgress.getWidth());
			targetTable.pack();
			targetTable.setVisible(targetVisible);
			
			
				for(final InventorySlot i : inventory.slots){
					//if(i.getItem() == null) continue;
					dragndrop.addSource(new Source(i) {

						final Payload payload = new Payload();
						@Override
						public Payload dragStart(InputEvent event, float x, float y, int pointer) {
							if(i.getItem() == null) return null;
							if(i.getItem().ID == 0) return null;
							System.out.println("Started dragging");
							payload.setObject(i.getItem());
							payload.setDragActor(new Image(i.getItem().ItemTexture));
							i.DeleteItem();
							
							return payload;
							
						}
						@Override
						public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
							if(target == null){
								i.setItem((Item)(payload.getObject()));
								
							}else{
								i.removeListener(i.getListeners().get(0));
								//dragndrop.removeSource(this);
								for(int i=0;i<inventory.slots.length;i++){
									
									if(inventory.slots[i].getItem() == null) continue;
									if(inventory.slots[i].getItem().ID != 0)
											inventory.slots[i].addListener(new TextTooltip(inventory.slots[i].getItem().name, MenuSkin));
								
								}
							}
						}
					});
					for(final InventorySlot tar : inventory.slots){
					dragndrop.addTarget(new Target(tar) {
						
						@Override
						public void drop(Source source, Payload payload, float x, float y, int pointer) {
							tar.setItem((Item) payload.getObject());
							try {
								screen.out.writeByte(PacketTypes.CHANGE_ITEM);
								screen.out.writeInt(((InventorySlot)source.getActor()).place);
								screen.out.writeInt(0);
								screen.out.writeByte(PacketTypes.CHANGE_ITEM);
								screen.out.writeInt(tar.place);
								screen.out.writeInt(((Item)payload.getObject()).ID);
								screen.out.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
						@Override
						public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
							
							if(tar.getItem() != null){
								return false;
							}
							return true;
						}
					});
						}
					
				}
			
			
			stage.addActor(mainHUDTable);
			stage.addActor(targetTable);
			
		
		
			
	}
	
	public void Open(OpenType o){
		if(o == OpenType.CHAT){
			stage.setKeyboardFocus(ChatTextField);
			
		}else if(o == OpenType.INVENTORY){
			stage.setKeyboardFocus(InventoryWindow);
		}
	}
	
	public void render(){
		hpProgress.setValue(screen.p.Health);
		manaProgress.setValue(screen.p.mana);
		expProgress.setValue(screen.p.xp);
		levelLabel.setText("Level   "+String.valueOf(level));
		moneyLabel.setText("Gold   "+String.valueOf(money));
		if(targetVisible){
			targetNameLabel.setText(targetName);
			targetLevelLabel.setText(String.valueOf(targetLevel));
		}
		targetTable.setVisible(targetVisible);
		chatWindow.scrollTo(0, 0, 0, 0);
		
		
		stage.act();
		stage.draw();
		
		
	}
	
	public void setNeededXP(int newNeededXP){
		expProgress.setRange(0, newNeededXP);
	}
	public void setMaxHealth(int newMaxHP){
		hpProgress.setRange(0, newMaxHP);
	}
	public void setMaxMana(int newMaxMana){
		manaProgress.setRange(0, newMaxMana);
	}
	public void sendMsg(String msg){
		Chattext.add(msg,"chat").row();
	}
	
	public Inventory getInventory(){
		return inventory;
	}
	
	public void setInventoryvisible(boolean visible){
		InventoryWindow.setVisible(visible);
	}

}
