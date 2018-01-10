package com.ibm.icu.util;

import com.ibm.icu.impl.ICULocaleData;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;




















public abstract class Holiday
  implements DateRule
{
  private String name;
  private DateRule rule;
  
  public static Holiday[] getHolidays()
  {
    return getHolidays(Locale.getDefault());
  }
  



  public static Holiday[] getHolidays(Locale locale)
  {
    Holiday[] result = noHolidays;
    try
    {
      ResourceBundle bundle = ICULocaleData.getResourceBundle("HolidayBundle", locale);
      
      result = (Holiday[])bundle.getObject("holidays");
    }
    catch (MissingResourceException e) {}
    
    return result;
  }
  










  public Date firstAfter(Date start)
  {
    return rule.firstAfter(start);
  }
  












  public Date firstBetween(Date start, Date end)
  {
    return rule.firstBetween(start, end);
  }
  










  public boolean isOn(Date date)
  {
    return rule.isOn(date);
  }
  




  public boolean isBetween(Date start, Date end)
  {
    return rule.isBetween(start, end);
  }
  













  protected Holiday(String name, DateRule rule)
  {
    this.name = name;
    this.rule = rule;
  }
  



  public String getDisplayName()
  {
    return getDisplayName(Locale.getDefault());
  }
  











  public String getDisplayName(Locale locale)
  {
    String name = this.name;
    try
    {
      ResourceBundle bundle = ICULocaleData.getResourceBundle("HolidayBundle", locale);
      name = bundle.getString(name);
    }
    catch (MissingResourceException e) {}
    

    return name;
  }
  


  public DateRule getRule()
  {
    return rule;
  }
  


  public void setRule(DateRule rule)
  {
    this.rule = rule;
  }
  



  private static Holiday[] noHolidays = new Holiday[0];
}
