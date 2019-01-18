package com.tomeraberbach.mano.simulation;

import java.security.InvalidAlgorithmParameterException;

public class BinaryLogicalBreakPoint implements IBreakPoint {
  public enum BinaryLogicalBreakPointType {
    TYPE_AND,
    TYPE_OR
  }
  BinaryLogicalBreakPointType type;
  IBreakPoint b0, b1;
  static IBreakPoint instance = null;
  
  private BinaryLogicalBreakPoint(BinaryLogicalBreakPointType ty, IBreakPoint lhs, IBreakPoint rhs) {
	instance = instance == null ? this : instance;
    b0 = lhs;
    b1 = rhs;
    type = ty;
  }

  public static BinaryLogicalBreakPoint BreakIfEither(IBreakPoint lhs, IBreakPoint rhs) {
    return new BinaryLogicalBreakPoint(BinaryLogicalBreakPointType.TYPE_OR, lhs, rhs);
  }

  public static BinaryLogicalBreakPoint BreakIfBoth(IBreakPoint lhs, IBreakPoint rhs) {
    return new BinaryLogicalBreakPoint(BinaryLogicalBreakPointType.TYPE_AND, lhs, rhs);
  }

  public boolean shouldBreak(Computer c) {
    if (b0.shouldBreak(c)) {
      if (type == BinaryLogicalBreakPointType.TYPE_OR) return true;
      return b1.shouldBreak(c);
    }
    return type == BinaryLogicalBreakPointType.TYPE_OR && b1.shouldBreak(c);
  }

  public String encode() {
    if (type == BinaryLogicalBreakPointType.TYPE_OR) {
      return "|(" + b0.encode() + ")(" + b1.encode() + ")";
    }
    return "&(" + b0.encode() + ")(" + b1.encode() + ")";
  }

  @Override
	public int hashCode() {
		return type.hashCode() + 13 * b0.hashCode() + 17 * b1.hashCode();
	}

@Override
public IBreakPoint parse(String s) {
	char op = s.charAt(0);
	s = s.substring(2).trim();
	int i = ParensBreakPoint.readBalancedParens(s);
	IBreakPoint lhs;
	try {
		lhs = BreakPointParser.parse(s.substring(0, i-1));
	} catch (InvalidAlgorithmParameterException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
	IBreakPoint rhs;
	try {
		String start2 = s.substring(i+1);
		int endi = ParensBreakPoint.readBalancedParens(start2);
		rhs = BreakPointParser.parse(start2.substring(0, endi-1).trim());
	} catch (InvalidAlgorithmParameterException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
	if (op == '&') {
		return BreakIfBoth(lhs, rhs);
	} else if (op == '|') {
		return BreakIfEither(lhs, rhs);
	} else {
		// junk
		return null;
	}
}
}
