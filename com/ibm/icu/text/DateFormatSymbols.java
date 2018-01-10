package com.ibm.icu.text;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;










































































public class DateFormatSymbols
  implements Serializable, Cloneable
{
  public DateFormatSymbols()
  {
    initializeData(Locale.getDefault());
  }
  









  public DateFormatSymbols(Locale locale)
  {
    initializeData(locale);
  }
  





  String[] eras = null;
  






  String[] months = null;
  







  String[] shortMonths = null;
  







  String[] weekdays = null;
  







  String[] shortWeekdays = null;
  






  String[] ampms = null;
  

























  String[][] zoneStrings = null;
  







  static final String patternChars = "GyMdkHmsSEDFwWahKzu";
  







  String localPatternChars = null;
  

  static final long serialVersionUID = -5987973545549424702L;
  

  static final int millisPerHour = 3600000;
  

  public String[] getEras()
  {
    return duplicate(eras);
  }
  




  public void setEras(String[] newEras)
  {
    eras = duplicate(newEras);
  }
  




  public String[] getMonths()
  {
    return duplicate(months);
  }
  




  public void setMonths(String[] newMonths)
  {
    months = duplicate(newMonths);
  }
  




  public String[] getShortMonths()
  {
    return duplicate(shortMonths);
  }
  




  public void setShortMonths(String[] newShortMonths)
  {
    shortMonths = duplicate(newShortMonths);
  }
  





  public String[] getWeekdays()
  {
    return duplicate(weekdays);
  }
  






  public void setWeekdays(String[] newWeekdays)
  {
    weekdays = duplicate(newWeekdays);
  }
  





  public String[] getShortWeekdays()
  {
    return duplicate(shortWeekdays);
  }
  






  public void setShortWeekdays(String[] newShortWeekdays)
  {
    shortWeekdays = duplicate(newShortWeekdays);
  }
  




  public String[] getAmPmStrings()
  {
    return duplicate(ampms);
  }
  




  public void setAmPmStrings(String[] newAmpms)
  {
    ampms = duplicate(newAmpms);
  }
  




  public String[][] getZoneStrings()
  {
    return duplicate(zoneStrings);
  }
  




  public void setZoneStrings(String[][] newZoneStrings)
  {
    zoneStrings = duplicate(newZoneStrings);
  }
  




  public String getLocalPatternChars()
  {
    return new String(localPatternChars);
  }
  





  public void setLocalPatternChars(String newLocalPatternChars)
  {
    localPatternChars = newLocalPatternChars;
  }
  



  public Object clone()
  {
    try
    {
      DateFormatSymbols other = (DateFormatSymbols)super.clone();
      copyMembers(this, other);
      return other;
    } catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  




  public int hashCode()
  {
    int hashcode = 0;
    for (int index = 0; index < zoneStrings[0].length; index++)
      hashcode ^= zoneStrings[0][index].hashCode();
    return hashcode;
  }
  




  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if ((obj == null) || (getClass() != obj.getClass())) return false;
    DateFormatSymbols that = (DateFormatSymbols)obj;
    return (Utility.arrayEquals(eras, eras)) && (Utility.arrayEquals(months, months)) && (Utility.arrayEquals(shortMonths, shortMonths)) && (Utility.arrayEquals(weekdays, weekdays)) && (Utility.arrayEquals(shortWeekdays, shortWeekdays)) && (Utility.arrayEquals(ampms, ampms)) && (Utility.arrayEquals(zoneStrings, zoneStrings)) && (Utility.arrayEquals(localPatternChars, localPatternChars));
  }
  
















  private void initializeData(Locale desiredLocale)
  {
    ResourceBundle rb = ICULocaleData.getLocaleElements(desiredLocale);
    



    eras = rb.getStringArray("Eras");
    months = rb.getStringArray("MonthNames");
    shortMonths = rb.getStringArray("MonthAbbreviations");
    
    String[] lWeekdays = rb.getStringArray("DayNames");
    weekdays = new String[8];
    weekdays[0] = "";
    System.arraycopy(lWeekdays, 0, weekdays, 1, lWeekdays.length);
    
    String[] sWeekdays = rb.getStringArray("DayAbbreviations");
    shortWeekdays = new String[8];
    shortWeekdays[0] = "";
    System.arraycopy(sWeekdays, 0, shortWeekdays, 1, sWeekdays.length);
    
    ampms = rb.getStringArray("AmPmMarkers");
    


    Object[] zoneObject = (Object[])rb.getObject("zoneStrings");
    zoneStrings = new String[zoneObject.length][];
    for (int i = 0; i < zoneStrings.length; i++) {
      zoneStrings[i] = ((String[])zoneObject[i]);
    }
    
    localPatternChars = rb.getString("localPatternChars");
  }
  









  final int getZoneIndex(String ID)
  {
    int result = _getZoneIndex(ID);
    if (result >= 0) {
      return result;
    }
    

    int n = TimeZone.countEquivalentIDs(ID);
    if (n > 1) {
      for (int i = 0; i < n; i++) {
        String equivID = TimeZone.getEquivalentID(ID, i);
        if (!equivID.equals(ID)) {
          int equivResult = _getZoneIndex(equivID);
          if (equivResult >= 0) {
            return equivResult;
          }
        }
      }
    }
    
    return -1;
  }
  



  private int _getZoneIndex(String ID)
  {
    for (int index = 0; index < zoneStrings.length; index++)
    {
      if (ID.equalsIgnoreCase(zoneStrings[index][0])) { return index;
      }
    }
    return -1;
  }
  






  private final String[] duplicate(String[] srcArray)
  {
    return (String[])srcArray.clone();
  }
  
  private final String[][] duplicate(String[][] srcArray)
  {
    String[][] aCopy = new String[srcArray.length][];
    for (int i = 0; i < srcArray.length; i++)
      aCopy[i] = duplicate(srcArray[i]);
    return aCopy;
  }
  






  private final void copyMembers(DateFormatSymbols src, DateFormatSymbols dst)
  {
    eras = duplicate(eras);
    months = duplicate(months);
    shortMonths = duplicate(shortMonths);
    weekdays = duplicate(weekdays);
    shortWeekdays = duplicate(shortWeekdays);
    ampms = duplicate(ampms);
    zoneStrings = duplicate(zoneStrings);
    localPatternChars = new String(localPatternChars);
  }
  












































































  public DateFormatSymbols(Calendar cal, Locale locale)
  {
    this(cal == null ? null : cal.getClass(), locale);
  }
  





  public DateFormatSymbols(Class calendarClass, Locale locale)
  {
    this(locale);
    if (calendarClass != null) {
      ResourceBundle bundle = null;
      try {
        bundle = getDateFormatBundle(calendarClass, locale);
      }
      catch (MissingResourceException e) {
        if (!GregorianCalendar.class.isAssignableFrom(calendarClass))
        {

          throw e;
        }
      }
      constructCalendarSpecific(bundle);
    }
  }
  









  public DateFormatSymbols(ResourceBundle bundle, Locale locale)
  {
    this(locale);
    constructCalendarSpecific(bundle);
  }
  
















  protected void constructCalendarSpecific(ResourceBundle bundle)
  {
    if (bundle != null) {
      try {
        String[] temp = bundle.getStringArray("DayNames");
        setWeekdays(temp);
        setShortWeekdays(temp);
        
        temp = bundle.getStringArray("DayAbbreviations");
        setShortWeekdays(temp);
      }
      catch (MissingResourceException e) {}
      try {
        String[] temp = bundle.getStringArray("MonthNames");
        setMonths(temp);
        setShortMonths(temp);
        
        temp = bundle.getStringArray("MonthAbbreviations");
        setShortMonths(temp);
      }
      catch (MissingResourceException e) {}
      try {
        String[] temp = bundle.getStringArray("Eras");
        setEras(temp);
      }
      catch (MissingResourceException e) {}
    }
  }
  












  public static ResourceBundle getDateFormatBundle(Class calendarClass, Locale locale)
    throws MissingResourceException
  {
    String fullName = calendarClass.getName();
    int lastDot = fullName.lastIndexOf('.');
    String className = fullName.substring(lastDot + 1);
    
    String bundleName = className + "Symbols";
    
    ResourceBundle result = null;
    try {
      result = ICULocaleData.getResourceBundle(bundleName, locale);
    }
    catch (MissingResourceException e)
    {
      if (!GregorianCalendar.class.isAssignableFrom(calendarClass))
      {

        throw e;
      }
    }
    return result;
  }
  





  public static ResourceBundle getDateFormatBundle(Calendar cal, Locale locale)
    throws MissingResourceException
  {
    return getDateFormatBundle(cal == null ? null : cal.getClass(), locale);
  }
}
