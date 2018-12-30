package com.deskind.martiniboot.trade;

import java.util.Arrays;
import java.util.List;

public class SymbolGenerator {
	
	private static byte pointer;
	private static List<String> symbols = Arrays.asList("R_10", "R_25", "R_33", "R_50", "R_75", "R_100");
	
	public static String getSymbol() {
		if(pointer == symbols.size())
			pointer = 0;
		return symbols.get(pointer++);
	}

}
