package com.sun.media.jai.util;










public class MathJAI
{
  public MathJAI() {}
  








  public static final int nextPositivePowerOf2(int n)
  {
    if (n < 2) {
      return 2;
    }
    
    int power = 1;
    while (power < n) {
      power <<= 1;
    }
    
    return power;
  }
  





  public static final boolean isPositivePowerOf2(int n)
  {
    return n == nextPositivePowerOf2(n);
  }
}
