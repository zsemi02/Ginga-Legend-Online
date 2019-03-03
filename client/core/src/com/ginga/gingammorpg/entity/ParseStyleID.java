package com.ginga.gingammorpg.entity;

import java.util.HashMap;

public class ParseStyleID {

	public static String parse(int id){
		HashMap<Integer, String> skins = new HashMap<Integer, String>();
		skins.put(1, "img/character/style1sheet.png");
		skins.put(2, "img/character/Bear.png");
		return skins.get(id);
	}
	
}
