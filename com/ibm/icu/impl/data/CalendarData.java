package com.ibm.icu.impl.data;

import java.util.ListResourceBundle;












public class CalendarData
  extends ListResourceBundle
{
  public CalendarData() {}
  
  public Object[][] getContents()
  {
    return new Object[][] { { "Weekend", { "7", "0", "2", "0" } } };
  }
}
