package com.sun.media.jai.util;

import java.io.PrintStream;













public class Rational
{
  public long num;
  public long denom;
  private static final int MAX_TERMS = 20;
  
  public Rational(long num, long denom)
  {
    this.num = num;
    this.denom = denom;
  }
  
  public Rational(Rational r) {
    num = num;
    denom = denom;
  }
  









  public static Rational createFromFrac(long[] terms, int len)
  {
    Rational r = new Rational(0L, 1L);
    for (int i = len - 1; i >= 0; i--) {
      r.add(terms[i]);
      if (i != 0) {
        r.invert();
      }
    }
    
    return r;
  }
  







  public static Rational approximate(float f, float tol)
  {
    float rem = f;
    long[] d = new long[20];
    int index = 0;
    for (int i = 0; i < 20; i++) {
      int k = (int)Math.floor(rem);
      d[(index++)] = k;
      
      rem -= k;
      if (rem == 0.0F) {
        break;
      }
      rem = 1.0F / rem;
    }
    


    Rational r = null;
    for (int i = 1; i <= index; i++) {
      r = createFromFrac(d, i);
      if (Math.abs(r.floatValue() - f) < tol) {
        return r;
      }
    }
    
    return r;
  }
  





  public static Rational approximate(double f, double tol)
  {
    double rem = f;
    long[] d = new long[20];
    int index = 0;
    for (int i = 0; i < 20; i++) {
      long k = Math.floor(rem);
      d[(index++)] = k;
      
      rem -= k;
      if (rem == 0.0D) {
        break;
      }
      rem = 1.0D / rem;
    }
    


    Rational r = null;
    for (int i = 1; i <= index; i++) {
      r = createFromFrac(d, i);
      if (Math.abs(r.doubleValue() - f) < tol) {
        return r;
      }
    }
    
    return r;
  }
  
  private static long gcd(long m, long n) {
    if (m < 0L) {
      m = -m;
    }
    if (n < 0L) {
      n = -n;
    }
    
    while (n > 0L) {
      long tmp = m % n;
      m = n;
      n = tmp;
    }
    return m;
  }
  
  private void normalize()
  {
    if (denom < 0L) {
      num = (-num);
      denom = (-denom);
    }
    
    long gcd = gcd(num, denom);
    if (gcd > 1L) {
      num /= gcd;
      denom /= gcd;
    }
  }
  


  public void add(long i)
  {
    num += i * denom;
    normalize();
  }
  


  public void add(Rational r)
  {
    num = (num * denom + num * denom);
    denom *= denom;
    normalize();
  }
  


  public void subtract(long i)
  {
    num -= i * denom;
    normalize();
  }
  


  public void subtract(Rational r)
  {
    num = (num * denom - num * denom);
    denom *= denom;
    normalize();
  }
  


  public void multiply(long i)
  {
    num *= i;
    normalize();
  }
  


  public void multiply(Rational r)
  {
    num *= num;
    denom *= denom;
    normalize();
  }
  


  public void invert()
  {
    long tmp = num;
    num = denom;
    denom = tmp;
  }
  


  public float floatValue()
  {
    return (float)num / (float)denom;
  }
  


  public double doubleValue()
  {
    return num / denom;
  }
  


  public String toString()
  {
    return num + "/" + denom;
  }
  



  public static int ceil(long num, long denom)
  {
    int ret = (int)(num / denom);
    
    if ((num > 0L) && 
      (num % denom != 0L)) {
      ret++;
    }
    

    return ret;
  }
  



  public static int floor(long num, long denom)
  {
    int ret = (int)(num / denom);
    
    if ((num < 0L) && 
      (num % denom != 0L)) {
      ret--;
    }
    

    return ret;
  }
  



  public static void main(String[] args)
  {
    float f = Float.parseFloat(args[0]);
    for (int i = 1; i < 15; i++) {
      Rational r = approximate(f, (float)Math.pow(10.0D, -i));
      System.out.println(r + " = " + r.floatValue());
    }
  }
}
