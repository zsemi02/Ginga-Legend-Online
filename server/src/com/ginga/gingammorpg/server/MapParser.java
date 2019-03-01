package com.ginga.gingammorpg.server;

public class MapParser {

	public static byte parse(String s){
		switch(s){
		case "futago":
			return MapIDs.FUTAGO;
		case "hokkaido":
			return MapIDs.HOKKAIDO;
		case "shikoku":
			return MapIDs.SHIKOKU;
		case "mie":
			return MapIDs.MIE;
		case "kai":
			return MapIDs.KAI;
		case "mutsu":
			return MapIDs.MUTSU;
		case "kyushu":
			return MapIDs.KYUSHU;
		case "Futago":
			return MapIDs.FUTAGO;
		case "Hokkaido":
			return MapIDs.HOKKAIDO;
		case "Shikoku":
			return MapIDs.SHIKOKU;
		case "Mie":
			return MapIDs.MIE;
		case "Kai":
			return MapIDs.KAI;
		case "Mutsu":
			return MapIDs.MUTSU;
		case "Kyushu":
			return MapIDs.KYUSHU;
		default:
			return 0;
		}
	}
	
	
}
