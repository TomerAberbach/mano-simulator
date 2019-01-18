package com.tomeraberbach.mano.simulation;

public interface IBreakPoint {
  boolean shouldBreak(Computer c);
  String encode();
  IBreakPoint parse(String s);
}
