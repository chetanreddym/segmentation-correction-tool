package com.ibm.icu.util;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.SimpleDateFormat;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;





















































































public abstract class TimeZone
  implements Serializable, Cloneable
{
  public static final int SHORT = 0;
  public static final int LONG = 1;
  private static final int ONE_MINUTE = 60000;
  private static final int ONE_HOUR = 3600000;
  private static Hashtable cachedLocaleData = new Hashtable(3);
  






  static final long serialVersionUID = 3581463369166924961L;
  






  private String ID;
  







  public TimeZone() {}
  







  public abstract int getOffset(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  






  int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds, int monthLength, int prevMonthLength)
  {
    return getOffset(era, year, month, day, dayOfWeek, milliseconds);
  }
  








  int getOffset(int eyear, int month, int dayOfMonth, int dayOfWeek, int milliseconds, int monthLength, int prevMonthLength)
  {
    int era = 1;
    if (eyear < 1) {
      era = 0;
      eyear = 1 - eyear;
    }
    return getOffset(era, eyear, month, dayOfMonth, dayOfWeek, milliseconds, monthLength, prevMonthLength);
  }
  






  public abstract void setRawOffset(int paramInt);
  






  public abstract int getRawOffset();
  






  public String getID()
  {
    return ID;
  }
  






  public void setID(String ID)
  {
    if (ID == null) {
      throw new NullPointerException();
    }
    this.ID = ID;
  }
  









  public final String getDisplayName()
  {
    return getDisplayName(false, 1, Locale.getDefault());
  }
  











  public final String getDisplayName(Locale locale)
  {
    return getDisplayName(false, 1, locale);
  }
  










  public final String getDisplayName(boolean daylight, int style)
  {
    return getDisplayName(daylight, style, Locale.getDefault());
  }
  























  public String getDisplayName(boolean daylight, int style, Locale locale)
  {
    if ((style != 0) && (style != 1)) {
      throw new IllegalArgumentException("Illegal style: " + style);
    }
    

    SoftReference data = (SoftReference)cachedLocaleData.get(locale);
    SimpleDateFormat format;
    if ((data == null) || ((format = (SimpleDateFormat)data.get()) == null))
    {
      format = new SimpleDateFormat(null, locale);
      cachedLocaleData.put(locale, new SoftReference(format));
    }
    

    SimpleTimeZone tz;
    

    if ((daylight) && (useDaylightTime())) {
      int savings = 3600000;
      try {
        savings = ((SimpleTimeZone)this).getDSTSavings();
      } catch (ClassCastException e) {}
      tz = new SimpleTimeZone(getRawOffset(), getID(), 0, 1, 0, 0, 1, 1, 0, 0, savings);

    }
    else
    {
      tz = new SimpleTimeZone(getRawOffset(), getID());
    }
    format.applyPattern(style == 1 ? "zzzz" : "z");
    format.setTimeZone(tz);
    

    return format.format(new Date(864000000L));
  }
  












  public abstract boolean useDaylightTime();
  











  public abstract boolean inDaylightTime(Date paramDate);
  











  public static synchronized TimeZone getTimeZone(String ID)
  {
    TimeZone zone = TimeZoneData.get(ID);
    if (zone == null) zone = parseCustomTimeZone(ID);
    if (zone == null) zone = (TimeZone)GMT.clone();
    return zone;
  }
  









  public static String[] getAvailableIDs(int rawOffset)
  {
    return TimeZoneData.getAvailableIDs(rawOffset);
  }
  










  public static String[] getAvailableIDs(String country)
  {
    return TimeZoneData.getAvailableIDs(country);
  }
  







  public static String[] getAvailableIDs()
  {
    return TimeZoneData.getAvailableIDs();
  }
  













  public static int countEquivalentIDs(String id)
  {
    return TimeZoneData.countEquivalentIDs(id);
  }
  


















  public static String getEquivalentID(String id, int index)
  {
    return TimeZoneData.getEquivalentID(id, index);
  }
  












  public static synchronized TimeZone getDefault()
  {
    if (defaultZone == null)
    {

















      java.util.TimeZone _default = java.util.TimeZone.getDefault();
      
      String zoneID = _default.getID();
      defaultZone = TimeZoneData.get(zoneID);
      if (defaultZone == null)
      {


        try
        {



          java.util.SimpleTimeZone s = (java.util.SimpleTimeZone)_default;
          defaultZone = new SimpleTimeZone(s.getRawOffset(), s.getID());
        }
        catch (ClassCastException e) {}
      } else if (zoneID == null) {
        zoneID = "GMT";
      }
      









      if (defaultZone == null) {
        defaultZone = getTimeZone(zoneID);
      }
    }
    
    return (TimeZone)defaultZone.clone();
  }
  








  public static synchronized void setDefault(TimeZone zone)
  {
    defaultZone = zone;
    

    try
    {
      java.util.TimeZone.setDefault(new SimpleTimeZoneAdapter((SimpleTimeZone)zone));
    }
    catch (ClassCastException e) {}
  }
  








  public boolean hasSameRules(TimeZone other)
  {
    return (other != null) && (getRawOffset() == other.getRawOffset()) && (useDaylightTime() == other.useDaylightTime());
  }
  




  public Object clone()
  {
    try
    {
      TimeZone other = (TimeZone)super.clone();
      ID = ID;
      return other;
    } catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  











  private static TimeZone defaultZone = null;
  
  static final String GMT_ID = "GMT";
  
  private static final int GMT_ID_LENGTH = 3;
  private static final String CUSTOM_ID = "Custom";
  private static NumberFormat numberFormat = null;
  
  private static final TimeZone GMT = new SimpleTimeZone(0, "GMT");
  






  private static final SimpleTimeZone parseCustomTimeZone(String id)
  {
    if ((id.length() > 3) && (id.regionMatches(true, 0, "GMT", 0, 3)))
    {
      ParsePosition pos = new ParsePosition(3);
      boolean negative = false;
      

      if (id.charAt(pos.getIndex()) == '-') {
        negative = true;
      } else if (id.charAt(pos.getIndex()) != '+')
        return null;
      pos.setIndex(pos.getIndex() + 1);
      

      synchronized (TimeZone.class) {
        if (numberFormat == null) {
          numberFormat = NumberFormat.getInstance();
          numberFormat.setParseIntegerOnly(true);
        }
      }
      
      synchronized (numberFormat)
      {
        int start = pos.getIndex();
        Number n = numberFormat.parse(id, pos);
        if (n == null) { SimpleTimeZone localSimpleTimeZone1 = null;return localSimpleTimeZone1; }
        int offset = n.intValue();
        
        if ((pos.getIndex() < id.length()) && (id.charAt(pos.getIndex()) == ':'))
        {

          offset *= 60;
          pos.setIndex(pos.getIndex() + 1);
          n = numberFormat.parse(id, pos);
          if (n == null) { localSimpleTimeZone2 = null;return localSimpleTimeZone2; }
          offset += n.intValue();







        }
        else if ((offset < 30) && (pos.getIndex() - start <= 2)) {
          offset *= 60;
        } else {
          offset = offset % 100 + offset / 100 * 60;
        }
        
        if (negative) offset = -offset;
        SimpleTimeZone localSimpleTimeZone2 = new SimpleTimeZone(offset * 60000, "Custom");return localSimpleTimeZone2;
      }
    }
    
    return null;
  }
}
