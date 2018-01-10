package com.ibm.icu.text;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ChineseCalendar;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;























public class ChineseDateFormatSymbols
  extends DateFormatSymbols
{
  String[] isLeapMonth;
  
  public ChineseDateFormatSymbols()
  {
    this(Locale.getDefault());
  }
  




  public ChineseDateFormatSymbols(Locale locale)
  {
    super(ChineseCalendar.class, locale);
  }
  





  public ChineseDateFormatSymbols(Calendar cal, Locale locale)
  {
    super(cal == null ? null : cal.getClass(), locale);
  }
  



  public String getLeapMonth(int isLeapMonth)
  {
    return this.isLeapMonth[isLeapMonth];
  }
  



  protected void constructCalendarSpecific(ResourceBundle bundle)
  {
    super.constructCalendarSpecific(bundle);
    if (bundle != null) {
      try {
        isLeapMonth = bundle.getStringArray("IsLeapMonth");
      }
      catch (MissingResourceException e) {}
    }
  }
}
