package com.ibm.icu.text;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;

























































































































































































































































public abstract class DateFormat
  extends Format
{
  protected Calendar calendar;
  protected NumberFormat numberFormat;
  public static final int ERA_FIELD = 0;
  public static final int YEAR_FIELD = 1;
  public static final int MONTH_FIELD = 2;
  public static final int DATE_FIELD = 3;
  public static final int HOUR_OF_DAY1_FIELD = 4;
  public static final int HOUR_OF_DAY0_FIELD = 5;
  public static final int MINUTE_FIELD = 6;
  public static final int SECOND_FIELD = 7;
  public static final int MILLISECOND_FIELD = 8;
  public static final int DAY_OF_WEEK_FIELD = 9;
  public static final int DAY_OF_YEAR_FIELD = 10;
  public static final int DAY_OF_WEEK_IN_MONTH_FIELD = 11;
  public static final int WEEK_OF_YEAR_FIELD = 12;
  public static final int WEEK_OF_MONTH_FIELD = 13;
  public static final int AM_PM_FIELD = 14;
  public static final int HOUR1_FIELD = 15;
  public static final int HOUR0_FIELD = 16;
  public static final int TIMEZONE_FIELD = 17;
  private static final long serialVersionUID = 7218322306649953788L;
  public static final int FULL = 0;
  public static final int LONG = 1;
  public static final int MEDIUM = 2;
  public static final int SHORT = 3;
  public static final int DEFAULT = 2;
  
  public final StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition fieldPosition)
  {
    if ((obj instanceof Calendar))
      return format((Calendar)obj, toAppendTo, fieldPosition);
    if ((obj instanceof Date))
      return format((Date)obj, toAppendTo, fieldPosition);
    if ((obj instanceof Number)) {
      return format(new Date(((Number)obj).longValue()), toAppendTo, fieldPosition);
    }
    
    throw new IllegalArgumentException("Cannot format given Object as a Date");
  }
  

























  public abstract StringBuffer format(Calendar paramCalendar, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  
























  public final StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition)
  {
    calendar.setTime(date);
    return format(calendar, toAppendTo, fieldPosition);
  }
  






  public final String format(Date date)
  {
    return format(date, new StringBuffer(), new FieldPosition(0)).toString();
  }
  











  public Date parse(String text)
    throws ParseException
  {
    ParsePosition pos = new ParsePosition(0);
    Date result = parse(text, pos);
    if (pos.getIndex() == 0) {
      throw new ParseException("Unparseable date: \"" + text + "\"", pos.getErrorIndex());
    }
    return result;
  }
  























  public abstract void parse(String paramString, Calendar paramCalendar, ParsePosition paramParsePosition);
  























  public final Date parse(String text, ParsePosition pos)
  {
    int start = pos.getIndex();
    calendar.clear();
    parse(text, calendar, pos);
    if (pos.getIndex() != start) {
      try {
        return calendar.getTime();

      }
      catch (IllegalArgumentException e)
      {
        pos.setIndex(start);
        pos.setErrorIndex(start);
      }
    }
    return null;
  }
  







  public Object parseObject(String source, ParsePosition pos)
  {
    return parse(source, pos);
  }
  




































  public static final DateFormat getTimeInstance()
  {
    return get(-1, 2, Locale.getDefault());
  }
  








  public static final DateFormat getTimeInstance(int style)
  {
    return get(-1, style, Locale.getDefault());
  }
  










  public static final DateFormat getTimeInstance(int style, Locale aLocale)
  {
    return get(-1, style, aLocale);
  }
  






  public static final DateFormat getDateInstance()
  {
    return get(2, -1, Locale.getDefault());
  }
  








  public static final DateFormat getDateInstance(int style)
  {
    return get(style, -1, Locale.getDefault());
  }
  










  public static final DateFormat getDateInstance(int style, Locale aLocale)
  {
    return get(style, -1, aLocale);
  }
  






  public static final DateFormat getDateTimeInstance()
  {
    return get(2, 2, Locale.getDefault());
  }
  











  public static final DateFormat getDateTimeInstance(int dateStyle, int timeStyle)
  {
    return get(dateStyle, timeStyle, Locale.getDefault());
  }
  










  public static final DateFormat getDateTimeInstance(int dateStyle, int timeStyle, Locale aLocale)
  {
    return get(dateStyle, timeStyle, aLocale);
  }
  




  public static final DateFormat getInstance()
  {
    return getDateTimeInstance(3, 3);
  }
  





  public static Locale[] getAvailableLocales()
  {
    return ICULocaleData.getAvailableLocales();
  }
  






  public void setCalendar(Calendar newCalendar)
  {
    calendar = newCalendar;
  }
  





  public Calendar getCalendar()
  {
    return calendar;
  }
  





  public void setNumberFormat(NumberFormat newNumberFormat)
  {
    numberFormat = newNumberFormat;
    


    numberFormat.setParseIntegerOnly(true);
  }
  






  public NumberFormat getNumberFormat()
  {
    return numberFormat;
  }
  





  public void setTimeZone(TimeZone zone)
  {
    calendar.setTimeZone(zone);
  }
  





  public TimeZone getTimeZone()
  {
    return calendar.getTimeZone();
  }
  









  public void setLenient(boolean lenient)
  {
    calendar.setLenient(lenient);
  }
  




  public boolean isLenient()
  {
    return calendar.isLenient();
  }
  




  public int hashCode()
  {
    return numberFormat.hashCode();
  }
  





  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if ((obj == null) || (getClass() != obj.getClass())) return false;
    DateFormat other = (DateFormat)obj;
    return (calendar.isEquivalentTo(calendar)) && (numberFormat.equals(numberFormat));
  }
  





  public Object clone()
  {
    DateFormat other = (DateFormat)super.clone();
    calendar = ((Calendar)calendar.clone());
    numberFormat = ((NumberFormat)numberFormat.clone());
    return other;
  }
  








  private static DateFormat get(int dateStyle, int timeStyle, Locale loc)
  {
    if ((timeStyle < -1) || (timeStyle > 3)) {
      throw new IllegalArgumentException("Illegal time style " + timeStyle);
    }
    if ((dateStyle < -1) || (dateStyle > 3)) {
      throw new IllegalArgumentException("Illegal date style " + dateStyle);
    }
    try {
      return new SimpleDateFormat(timeStyle, dateStyle, loc);
    } catch (MissingResourceException e) {}
    return new SimpleDateFormat("M/d/yy h:mm a");
  }
  













  protected DateFormat() {}
  












  public static final DateFormat getDateInstance(Calendar cal, int dateStyle, Locale locale)
  {
    return getDateTimeInstance(cal, dateStyle, -1, locale);
  }
  


















  public static final DateFormat getTimeInstance(Calendar cal, int timeStyle, Locale locale)
  {
    return getDateTimeInstance(cal, -1, timeStyle, locale);
  }
  























  public static final DateFormat getDateTimeInstance(Calendar cal, int dateStyle, int timeStyle, Locale locale)
  {
    return cal.getDateTimeFormat(dateStyle, timeStyle, locale);
  }
  



  public static final DateFormat getInstance(Calendar cal, Locale locale)
  {
    return getDateTimeInstance(cal, 3, 3, locale);
  }
  



  public static final DateFormat getInstance(Calendar cal)
  {
    return getInstance(cal, Locale.getDefault());
  }
  



  public static final DateFormat getDateInstance(Calendar cal, int dateStyle)
  {
    return getDateInstance(cal, dateStyle, Locale.getDefault());
  }
  



  public static final DateFormat getTimeInstance(Calendar cal, int timeStyle)
  {
    return getTimeInstance(cal, timeStyle, Locale.getDefault());
  }
  



  public static final DateFormat getDateTimeInstance(Calendar cal, int dateStyle, int timeStyle)
  {
    return getDateTimeInstance(cal, dateStyle, timeStyle, Locale.getDefault());
  }
}
