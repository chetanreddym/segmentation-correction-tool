package com.ibm.icu.impl.data;

import java.util.ListResourceBundle;















public class ChineseCalendarSymbols
  extends ListResourceBundle
{
  static final Object[][] fContents = { { "IsLeapMonth", { "", "*" } }, { "DateTimePatterns", { "h:mm:ss a z", "h:mm:ss a z", "h:mm:ss a", "h:mm a", "EEEE y'x'G-Ml-d", "y'x'G-Ml-d", "y'x'G-Ml-d", "y'x'G-Ml-d", "{1} {0}" } } };
  









  public ChineseCalendarSymbols() {}
  









  public synchronized Object[][] getContents()
  {
    return fContents;
  }
}
