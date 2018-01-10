package com.ibm.icu.impl.data;

import java.util.ListResourceBundle;















public class IslamicCalendarSymbols
  extends ListResourceBundle
{
  private static String copyright = "Copyright © 1998-1999 IBM Corp. All Rights Reserved.";
  
  static final Object[][] fContents = { { "MonthNames", { "Muharram", "Safar", "Rabi' I", "Rabi' II", "Jumada I", "Jumada I", "Rajab", "Sha'ban", "Ramadan", "Shawwal", "Dhu'l-Qi'dah", "Dhu'l-Hijjah" } }, { "Eras", { "AH" } } };
  








  public IslamicCalendarSymbols() {}
  







  public synchronized Object[][] getContents()
  {
    return fContents;
  }
}
