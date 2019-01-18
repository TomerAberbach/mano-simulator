package com.tomeraberbach.mano.simulation;

import java.security.InvalidAlgorithmParameterException;

public class BreakPointParser {
	static {
		IBreakPoint instances[] = {
			new UnaryLogicalBreakPoint(null, null),
			BinaryLogicalBreakPoint.BreakIfEither(null, null),
			new InstructionBreakPoint(null),
			PCBreakPoint.BreakOnPC(0),
			new ParensBreakPoint(),
		};
	}
	public static IBreakPoint parse(String desc) throws InvalidAlgorithmParameterException {
		desc = desc.trim();
		System.out.println("Parse: " + desc);
		// just a single lookahead will do~
		char c = desc.charAt(0);
		switch(c) {
		case '(':
			return ParensBreakPoint.instance.parse(desc);
		case '&':
		case '|':
			return BinaryLogicalBreakPoint.instance.parse(desc);
		case '!':
			return UnaryLogicalBreakPoint.instance.parse(desc);
		case '%':
			return InstructionBreakPoint.instance.parse(desc);
		case '@':
			return PCBreakPoint.instance.parse(desc);
		}
		throw new InvalidAlgorithmParameterException("Invalid lookahead " + c);
	}
}
