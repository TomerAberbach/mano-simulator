package com.tomeraberbach.mano.simulation;

import java.security.InvalidAlgorithmParameterException;

public class UnaryLogicalBreakPoint implements IBreakPoint {
  public enum UnaryLogicalBreakPointType {
    TYPE_NOT
  }
  UnaryLogicalBreakPointType type;
  IBreakPoint b0;
  static IBreakPoint instance = null;

  UnaryLogicalBreakPoint(UnaryLogicalBreakPointType ty, IBreakPoint lhs) {
	instance = instance == null ? this : instance;
	b0 = lhs;
    type = ty;
  }

  public static UnaryLogicalBreakPoint BreakIfNot(IBreakPoint lhs) {
    return new UnaryLogicalBreakPoint(UnaryLogicalBreakPointType.TYPE_NOT, lhs);
  }

  public boolean shouldBreak(Computer c) {
    return /* type == UnaryLogicalBreakPointType.TYPE_NOT && */ !b0.shouldBreak(c);
  }

  public String encode() {
    /* if (type == UnaryLogicalBreakPointType.TYPE_NOT) */
      return "!(" + b0.encode() + ")";
  }

  @Override
	public int hashCode() {
		return type.hashCode() + 19 * b0.hashCode();
	}

public IBreakPoint parse(String s) {
	try {
		return new UnaryLogicalBreakPoint(UnaryLogicalBreakPointType.TYPE_NOT, BreakPointParser.parse(s.substring(1)));
	} catch (InvalidAlgorithmParameterException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}
}
