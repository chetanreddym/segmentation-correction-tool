package com.ibm.icu.impl.data;

import java.util.ListResourceBundle;















public class HebrewCalendarSymbols
  extends ListResourceBundle
{
  private static String copyright = "Copyright © 1998 IBM Corp. All Rights Reserved.";
  
  static final Object[][] fContents = { { "MonthNames", { "Tishri", "Heshvan", "Kislev", "Tevet", "Shevat", "Adar I", "Adar", "Nisan", "Iyar", "Sivan", "Tamuz", "Av", "Elul" } }, { "Eras", { "AM" } } };
  








  public HebrewCalendarSymbols() {}
  








  public synchronized Object[][] getContents()
  {
    return fContents;
  }
}
