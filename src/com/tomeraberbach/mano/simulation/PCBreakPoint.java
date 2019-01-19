package com.tomeraberbach.mano.simulation;

import java.util.HashMap;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class PCBreakPoint implements IBreakPoint {
	public enum PCBreakPointType {
    TYPE_IN_RANGE,
    TYPE_PC,
    TYPE_NOT_IN_RANGE,
    TYPE_ON_SYMBOL,
    TYPE_ON_SYMBOL_REF,
  }
	PCBreakPointType type;
	int range_start, range_end;
	String symbol = null;
	static IBreakPoint instance = null;
	
	private PCBreakPoint(PCBreakPointType ty, int rs, int re) {
		instance = instance == null ? this : instance;
		type = ty;
		range_start = rs;
		range_end = re;
	}
	
	private PCBreakPoint(boolean indirect, String sym) {
		instance = instance == null ? this : instance;
		type = indirect ? PCBreakPointType.TYPE_ON_SYMBOL_REF : PCBreakPointType.TYPE_ON_SYMBOL;
		range_start = -1;
		range_end = -1;
		symbol = sym;
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

	public static PCBreakPoint BreakOnSymbol(String sym) {
		return new PCBreakPoint(false, sym);
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
		case TYPE_ON_SYMBOL:
			String sv = c.ram().values().get(pc).getLabel();
			return sv.equals(symbol);
		case TYPE_ON_SYMBOL_REF: {
			HashMap<String, Integer> svc = c.ram().labelCache;
			int sn;
			if (svc.containsKey(symbol)) 
				sn = svc.get(symbol);
			else {
				RAM ram = c.ram();
				OptionalInt spos = IntStream
						.range(0, ram.maxAddress())
						.filter(i -> ram.values().get(i).getLabel().equals(symbol))
						.findFirst();
				if (!spos.isPresent()) {
					svc.put(symbol, -1); // can't add labels late though
					return false;
				}
				svc.put(symbol, sn = spos.getAsInt());
			}
			if (sn == -1)
				return false;
			sn = c.ram().values().get(sn).value();
			return c.pc().value() == sn;
		}
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
		case TYPE_ON_SYMBOL:
			return "^" + symbol;
		case TYPE_ON_SYMBOL_REF:
			return "^*" + symbol;
		}
		// dummy
		return "";
	}

	@Override
	public int hashCode() {
		return type.hashCode() + 3 * range_start + 7 * range_end + 11 * (symbol!=null?symbol.hashCode():1);
	}

	@Override
	public IBreakPoint parse(String s) {
		boolean isym = s.charAt(0) == '^';
		s = s.substring(1);
		
		if (isym)
			if (s.charAt(0) == '*')
				return new PCBreakPoint(true, s.substring(1));
			else
				return new PCBreakPoint(false, s);
		
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
