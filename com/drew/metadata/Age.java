package com.drew.metadata;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

public class Age
{
  private int _years;
  private int _months;
  private int _days;
  private int _hours;
  private int _minutes;
  private int _seconds;
  
  @Nullable
  public static Age fromPanasonicString(@NotNull String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    if ((paramString.length() != 19) || (paramString.startsWith("9999:99:99"))) {
      return null;
    }
    try
    {
      int i = Integer.parseInt(paramString.substring(0, 4));
      int j = Integer.parseInt(paramString.substring(5, 7));
      int k = Integer.parseInt(paramString.substring(8, 10));
      int m = Integer.parseInt(paramString.substring(11, 13));
      int n = Integer.parseInt(paramString.substring(14, 16));
      int i1 = Integer.parseInt(paramString.substring(17, 19));
      return new Age(i, j, k, m, n, i1);
    }
    catch (NumberFormatException localNumberFormatException) {}
    return null;
  }
  
  public Age(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    _years = paramInt1;
    _months = paramInt2;
    _days = paramInt3;
    _hours = paramInt4;
    _minutes = paramInt5;
    _seconds = paramInt6;
  }
  
  public int getYears()
  {
    return _years;
  }
  
  public int getMonths()
  {
    return _months;
  }
  
  public int getDays()
  {
    return _days;
  }
  
  public int getHours()
  {
    return _hours;
  }
  
  public int getMinutes()
  {
    return _minutes;
  }
  
  public int getSeconds()
  {
    return _seconds;
  }
  
  public String toString()
  {
    return String.format("%04d:%02d:%02d %02d:%02d:%02d", new Object[] { Integer.valueOf(_years), Integer.valueOf(_months), Integer.valueOf(_days), Integer.valueOf(_hours), Integer.valueOf(_minutes), Integer.valueOf(_seconds) });
  }
  
  public String toFriendlyString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    appendAgePart(localStringBuilder, _years, "year");
    appendAgePart(localStringBuilder, _months, "month");
    appendAgePart(localStringBuilder, _days, "day");
    appendAgePart(localStringBuilder, _hours, "hour");
    appendAgePart(localStringBuilder, _minutes, "minute");
    appendAgePart(localStringBuilder, _seconds, "second");
    return localStringBuilder.toString();
  }
  
  private static void appendAgePart(StringBuilder paramStringBuilder, int paramInt, String paramString)
  {
    if (paramInt == 0) {
      return;
    }
    if (paramStringBuilder.length() != 0) {
      paramStringBuilder.append(' ');
    }
    paramStringBuilder.append(paramInt).append(' ').append(paramString);
    if (paramInt != 1) {
      paramStringBuilder.append('s');
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    Age localAge = (Age)paramObject;
    if (_days != _days) {
      return false;
    }
    if (_hours != _hours) {
      return false;
    }
    if (_minutes != _minutes) {
      return false;
    }
    if (_months != _months) {
      return false;
    }
    if (_seconds != _seconds) {
      return false;
    }
    return _years == _years;
  }
  
  public int hashCode()
  {
    int i = _years;
    i = 31 * i + _months;
    i = 31 * i + _days;
    i = 31 * i + _hours;
    i = 31 * i + _minutes;
    i = 31 * i + _seconds;
    return i;
  }
}
