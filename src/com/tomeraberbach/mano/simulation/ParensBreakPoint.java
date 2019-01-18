package com.tomeraberbach.mano.simulation;

import java.security.InvalidAlgorithmParameterException;

public class ParensBreakPoint implements IBreakPoint {
	static IBreakPoint instance = null;
	public ParensBreakPoint() {
		instance = instance == null ? this : instance;
	}
	@Override
	public boolean shouldBreak(Computer c) {
		// never used 
		return false;
	}

	@Override
	public String encode() {
		// never used 
		return null;
	}
	public static int readBalancedParens(String s) {
		// read balanced parens
		int pcount = 1;
		int i = 0;
		while (pcount > 0) {
			if (i >= s.length())
				// assume parens end at eos
				break;
			char c = s.charAt(i++);
			if (c == ')') pcount--;
			else if (c == '(') pcount++;
		}
		return i;
	}
	@Override
	public IBreakPoint parse(String s) {
		s = s.substring(1);
		s = s.substring(0, readBalancedParens(s) - 1);
		try {
			return BreakPointParser.parse(s);
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
