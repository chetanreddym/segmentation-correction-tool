package com.drew.lang;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import java.io.Serializable;

public class Rational
  extends Number
  implements Serializable
{
  private static final long serialVersionUID = 510688928138848770L;
  private final long _numerator;
  private final long _denominator;
  
  public Rational(long paramLong1, long paramLong2)
  {
    _numerator = paramLong1;
    _denominator = paramLong2;
  }
  
  public double doubleValue()
  {
    return _numerator / _denominator;
  }
  
  public float floatValue()
  {
    return (float)_numerator / (float)_denominator;
  }
  
  public final byte byteValue()
  {
    return (byte)(int)doubleValue();
  }
  
  public final int intValue()
  {
    return (int)doubleValue();
  }
  
  public final long longValue()
  {
    return doubleValue();
  }
  
  public final short shortValue()
  {
    return (short)(int)doubleValue();
  }
  
  public final long getDenominator()
  {
    return _denominator;
  }
  
  public final long getNumerator()
  {
    return _numerator;
  }
  
  @NotNull
  public Rational getReciprocal()
  {
    return new Rational(_denominator, _numerator);
  }
  
  public boolean isInteger()
  {
    return (_denominator == 1L) || ((_denominator != 0L) && (_numerator % _denominator == 0L)) || ((_denominator == 0L) && (_numerator == 0L));
  }
  
  @NotNull
  public String toString()
  {
    return _numerator + "/" + _denominator;
  }
  
  @NotNull
  public String toSimpleString(boolean paramBoolean)
  {
    if ((_denominator == 0L) && (_numerator != 0L)) {
      return toString();
    }
    if (isInteger()) {
      return Integer.toString(intValue());
    }
    if ((_numerator != 1L) && (_denominator % _numerator == 0L))
    {
      long l = _denominator / _numerator;
      return new Rational(1L, l).toSimpleString(paramBoolean);
    }
    Rational localRational = getSimplifiedInstance();
    if (paramBoolean)
    {
      String str = Double.toString(localRational.doubleValue());
      if (str.length() < 5) {
        return str;
      }
    }
    return localRational.toString();
  }
  
  private boolean tooComplexForSimplification()
  {
    double d = (Math.min(_denominator, _numerator) - 1L) / 5.0D + 2.0D;
    return d > 1000.0D;
  }
  
  public boolean equals(@Nullable Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof Rational))) {
      return false;
    }
    Rational localRational = (Rational)paramObject;
    return doubleValue() == localRational.doubleValue();
  }
  
  public int hashCode()
  {
    return 23 * (int)_denominator + (int)_numerator;
  }
  
  @NotNull
  public Rational getSimplifiedInstance()
  {
    if (tooComplexForSimplification()) {
      return this;
    }
    for (int i = 2; i <= Math.min(_denominator, _numerator); i++) {
      if (((i % 2 != 0) || (i <= 2)) && ((i % 5 != 0) || (i <= 5)) && (_denominator % i == 0L) && (_numerator % i == 0L)) {
        return new Rational(_numerator / i, _denominator / i);
      }
    }
    return this;
  }
}
