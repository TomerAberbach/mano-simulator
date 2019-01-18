package com.tomeraberbach.mano.simulation;

public class InstructionBreakPoint implements IBreakPoint {
  String mnemonic;
  static IBreakPoint instance = null;
  InstructionBreakPoint(String mn) {
	instance = instance == null ? this : instance;
    mnemonic = mn;
  }

  public static InstructionBreakPoint BreakMnemonic(String mnemonic) {
    return new InstructionBreakPoint(mnemonic);
  }

  public boolean shouldBreak(Computer c) {
    return c.ram().values().get(c.pc().value()).getInstruction().split(" ")[0].toLowerCase().equals(mnemonic.toLowerCase());
  }

  public String encode() {
    return "%" + mnemonic;
  }

  @Override
  public int hashCode() {
	return 23 * mnemonic.hashCode();
  }

  @Override
  public IBreakPoint parse(String s) {
	s = s.substring(1);
	return new InstructionBreakPoint(s);
  }


}
