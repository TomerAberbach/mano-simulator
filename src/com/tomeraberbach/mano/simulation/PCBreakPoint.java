package com.tomeraberbach.mano.simulation;

public class PCBreakPoint implements IBreakPoint {
	public enum PCBreakPointType {
    TYPE_IN_RANGE,
    TYPE_PC,
    TYPE_NOT_IN_RANGE,
  }
	PCBreakPointType type;
	int range_start, range_end;
	static IBreakPoint instance = null;
	
	private PCBreakPoint(PCBreakPointType ty, int rs, int re) {
		instance = instance == null ? this : instance;
		type = ty;
		range_start = rs;
		range_end = re;
	}

	public static PCBreakPoint BreakOnPC(int pc) {
		return new PCBreakPoint(PCBreakPointType.TYPE_PC, pc, 0);
	}
	public static PCBreakPoint BreakInRange(int st, int ed) {
		return new PCBreakPoint(PCBreakPointType.TYPE_IN_RANGE, st, ed);
	}
	public static PCBreakPoint BreakNotInRange(int st, int ed) {
		return new PCBreakPoint(PCBreakPointType.TYPE_NOT_IN_RANGE, st, ed);
	}

	public boolean shouldBreak(Computer c) {
		int pc = c.pc().value();

		switch(type) {
		case TYPE_IN_RANGE:
			return range_start <= pc && range_end > pc;
		case TYPE_NOT_IN_RANGE:
			return range_start > pc || range_end <= pc;
		case TYPE_PC:
			return range_start == pc;
		}
		// dummy
		return false;
	}

	public String encode() {
		switch(type) {
		case TYPE_IN_RANGE:
			return "@" + Integer.toHexString(range_start) + ":" + Integer.toHexString(range_end);
		case TYPE_NOT_IN_RANGE:
			return "@" + Integer.toHexString(range_start) + "-" + Integer.toHexString(range_end);
		case TYPE_PC:
			return "@" + Integer.toHexString(range_start );
		}
		// dummy
		return "";
	}

	@Override
	public int hashCode() {
		return type.hashCode() + 3 * range_start + 7 * range_end + 11;
	}

	@Override
	public IBreakPoint parse(String s) {
		s = s.substring(1);
		String[] ds;
		PCBreakPointType ty = PCBreakPointType.TYPE_PC;
		if (s.contains(":")) {
			ty = PCBreakPointType.TYPE_IN_RANGE;
			ds = s.split(":");
		}
		else if (s.contains("-")) {
			ty = PCBreakPointType.TYPE_NOT_IN_RANGE;
			ds = s.split("-");
		} else {
			ds = new String[] { s, "0" };
		}
		return new PCBreakPoint(ty, Integer.parseInt(ds[0], 16), Integer.parseInt(ds[1], 16));
	}
}
