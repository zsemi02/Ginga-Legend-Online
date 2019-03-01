package com.ginga.gingammorpg.net;

public class MapIDs {
	
	public static final byte FUTAGO = 1;
	public static final byte HOKKAIDO = 2;
	public static final byte SHIKOKU = 3;
	public static final byte MIE = 4;
	public static final byte KAI = 5;
	public static final byte MUTSU = 6;
	public static final byte KYUSHU = 7;

	
	public static String Parsebyte(byte id){
		switch(id){
			case FUTAGO:
				return "Futago";
			case HOKKAIDO:
				return "Hokkaido";
			case SHIKOKU:
				return "Shikoku";
			case MIE:
				return "Mie";
			case KAI:
				return "Kai";
			case MUTSU:
				return "Mutsu";
			case KYUSHU:
				return "Kyushu";
			default:
				System.out.println("Wrong region byte");
				return "Error";
		}
	}
	
	
	
	
}
