package com.ibm.icu.impl.data;

import com.ibm.icu.util.Holiday;
import com.ibm.icu.util.SimpleHoliday;
import java.util.ListResourceBundle;










public class HolidayBundle_ja_JP
  extends ListResourceBundle
{
  public HolidayBundle_ja_JP() {}
  
  private static final Holiday[] fHolidays = { new SimpleHoliday(1, 11, 0, "National Foundation Day") };
  

  private static final Object[][] fContents = { { "holidays", fHolidays } };
  
  public synchronized Object[][] getContents() {
    return fContents;
  }
}