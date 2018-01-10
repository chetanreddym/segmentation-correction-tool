package com.ibm.icu.util;

import java.util.Date;
import java.util.Locale;
































































public class HebrewCalendar
  extends Calendar
{
  private static String copyright = "Copyright © 1997-1998 IBM Corp. All Rights Reserved.";
  





  public static final int TISHRI = 0;
  





  public static final int HESHVAN = 1;
  





  public static final int KISLEV = 2;
  





  public static final int TEVET = 3;
  





  public static final int SHEVAT = 4;
  





  public static final int ADAR_1 = 5;
  





  public static final int ADAR = 6;
  





  public static final int NISAN = 7;
  





  public static final int IYAR = 8;
  





  public static final int SIVAN = 9;
  





  public static final int TAMUZ = 10;
  




  public static final int AV = 11;
  




  public static final int ELUL = 12;
  




  private static final int[][] LIMITS = { { 0, 0, 0, 0 }, { 1, 1, 5000000, 5000000 }, { 0, 0, 12, 12 }, { 1, 1, 51, 56 }, { 0, 0, 5, 6 }, { 1, 1, 29, 30 }, { 1, 1, 353, 385 }, new int[0], { -1, -1, 4, 6 }, new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], { -5000001, -5000001, 5000001, 5000001 }, new int[0], { -5000000, -5000000, 5000000, 5000000 }, new int[0], new int[0] };
  
































  private static final int[][] MONTH_LENGTH = { { 30, 30, 30 }, { 29, 29, 30 }, { 29, 30, 30 }, { 29, 29, 29 }, { 30, 30, 30 }, { 30, 30, 30 }, { 29, 29, 29 }, { 30, 30, 30 }, { 29, 29, 29 }, { 30, 30, 30 }, { 29, 29, 29 }, { 30, 30, 30 }, { 29, 29, 29 } };
  




















  private static final int[][] MONTH_START = { { 0, 0, 0 }, { 30, 30, 30 }, { 59, 59, 60 }, { 88, 89, 90 }, { 117, 118, 119 }, { 147, 148, 149 }, { 147, 148, 149 }, { 176, 177, 178 }, { 206, 207, 208 }, { 235, 236, 237 }, { 265, 266, 267 }, { 294, 295, 296 }, { 324, 325, 326 }, { 353, 354, 355 } };
  



















  private static final int[][] LEAP_MONTH_START = { { 0, 0, 0 }, { 30, 30, 30 }, { 59, 59, 60 }, { 88, 89, 90 }, { 117, 118, 119 }, { 147, 148, 149 }, { 177, 178, 179 }, { 206, 207, 208 }, { 236, 237, 238 }, { 265, 266, 267 }, { 295, 296, 297 }, { 324, 325, 326 }, { 354, 355, 356 }, { 383, 384, 385 } };
  




















  private static CalendarCache cache = new CalendarCache();
  
  private static final long HOUR_PARTS = 1080L;
  
  private static final long DAY_PARTS = 25920L;
  private static final int MONTH_DAYS = 29;
  private static final long MONTH_FRACT = 13753L;
  private static final long MONTH_PARTS = 765433L;
  private static final long BAHARAD = 12084L;
  
  public HebrewCalendar()
  {
    this(TimeZone.getDefault(), Locale.getDefault());
  }
  






  public HebrewCalendar(TimeZone zone)
  {
    this(zone, Locale.getDefault());
  }
  






  public HebrewCalendar(Locale aLocale)
  {
    this(TimeZone.getDefault(), aLocale);
  }
  








  public HebrewCalendar(TimeZone zone, Locale aLocale)
  {
    super(zone, aLocale);
    setTimeInMillis(System.currentTimeMillis());
  }
  











  public HebrewCalendar(int year, int month, int date)
  {
    super(TimeZone.getDefault(), Locale.getDefault());
    set(1, year);
    set(2, month);
    set(5, date);
  }
  






  public HebrewCalendar(Date date)
  {
    super(TimeZone.getDefault(), Locale.getDefault());
    setTime(date);
  }
  



















  public HebrewCalendar(int year, int month, int date, int hour, int minute, int second)
  {
    super(TimeZone.getDefault(), Locale.getDefault());
    set(1, year);
    set(2, month);
    set(5, date);
    set(11, hour);
    set(12, minute);
    set(13, second);
  }
  


































  public void add(int field, int amount)
  {
    switch (field)
    {





    case 2: 
      int month = get(2);
      int year = get(1);
      
      if (amount > 0) {
        acrossAdar1 = month < 5;
        month += amount;
        for (;;) {
          if ((acrossAdar1) && (month >= 5) && (!isLeapYear(year))) {
            month++;
          }
          if (month <= 12) {
            break;
          }
          month -= 13;
          year++;
          acrossAdar1 = true;
        }
      }
      boolean acrossAdar1 = month > 5;
      month += amount;
      for (;;) {
        if ((acrossAdar1) && (month <= 5) && (!isLeapYear(year))) {
          month--;
        }
        if (month >= 0) {
          break;
        }
        month += 13;
        year--;
        acrossAdar1 = true;
      }
      
      set(2, month);
      set(1, year);
      pinField(5);
      break;
    

    default: 
      super.add(field, amount);
    }
    
  }
  
































  public void roll(int field, int amount)
  {
    switch (field)
    {
    case 2: 
      int month = get(2);
      int year = get(1);
      
      boolean leapYear = isLeapYear(year);
      int yearLength = monthsInYear(year);
      int newMonth = month + amount % yearLength;
      



      if (!leapYear) {
        if ((amount > 0) && (month < 5) && (newMonth >= 5)) {
          newMonth++;
        } else if ((amount < 0) && (month > 5) && (newMonth <= 5)) {
          newMonth--;
        }
      }
      set(2, (newMonth + 13) % 13);
      pinField(5);
      return;
    }
    
    super.roll(field, amount);
  }
  










































  private static long startOfYear(int year)
  {
    long day = cache.get(year);
    
    if (day == CalendarCache.EMPTY) {
      int months = (235 * year - 234) / 19;
      
      long frac = months * 13753L + 12084L;
      day = months * 29 + frac / 25920L;
      frac %= 25920L;
      
      int wd = (int)(day % 7L);
      
      if ((wd == 2) || (wd == 4) || (wd == 6))
      {
        day += 1L;
        wd = (int)(day % 7L);
      }
      if ((wd == 1) && (frac > 16404L) && (!isLeapYear(year)))
      {


        day += 2L;
      }
      else if ((wd == 0) && (frac > 23269L) && (isLeapYear(year - 1)))
      {


        day += 1L;
      }
      cache.put(year, day);
    }
    return day;
  }
  








  private static int absoluteDayToDayOfWeek(long day)
  {
    return (int)(day % 7L) + 1;
  }
  







  private final int yearType(int year)
  {
    int yearLength = handleGetYearLength(year);
    
    if (yearLength > 380) {
      yearLength -= 30;
    }
    
    int type = 0;
    
    switch (yearLength) {
    case 353: 
      type = 0; break;
    case 354: 
      type = 1; break;
    case 355: 
      type = 2; break;
    default: 
      throw new RuntimeException("Illegal year length " + yearLength + " in year " + year);
    }
    
    return type;
  }
  






  private static final boolean isLeapYear(int year)
  {
    int x = (year * 12 + 17) % 19;
    return x >= (x < 0 ? -7 : 12);
  }
  
  private static int monthsInYear(int year) {
    return isLeapYear(year) ? 13 : 12;
  }
  






  protected int handleGetLimit(int field, int limitType)
  {
    return LIMITS[field][limitType];
  }
  




  protected int handleGetMonthLength(int extendedYear, int month)
  {
    switch (month)
    {
    case 1: 
    case 2: 
      return MONTH_LENGTH[month][yearType(extendedYear)];
    }
    
    
    return MONTH_LENGTH[month][0];
  }
  




  protected int handleGetYearLength(int eyear)
  {
    return (int)(startOfYear(eyear + 1) - startOfYear(eyear));
  }
  
























  protected void handleComputeFields(int julianDay)
  {
    long d = julianDay - 347997;
    long m = d * 25920L / 765433L;
    int year = (int)((19L * m + 234L) / 235L) + 1;
    long ys = startOfYear(year);
    int dayOfYear = (int)(d - ys);
    

    while (dayOfYear < 1) {
      year--;
      ys = startOfYear(year);
      dayOfYear = (int)(d - ys);
    }
    

    int yearType = yearType(year);
    int[][] monthStart = isLeapYear(year) ? LEAP_MONTH_START : MONTH_START;
    
    int month = 0;
    while (dayOfYear > monthStart[month][yearType]) {
      month++;
    }
    month--;
    int dayOfMonth = dayOfYear - monthStart[month][yearType];
    
    internalSet(0, 0);
    internalSet(1, year);
    internalSet(19, year);
    internalSet(2, month);
    internalSet(5, dayOfMonth);
    internalSet(6, dayOfYear);
  }
  



  protected int handleGetExtendedYear()
  {
    int year;
    


    if (newerField(19, 1) == 19) {
      year = internalGet(19, 1);
    } else {
      year = internalGet(1, 1);
    }
    return year;
  }
  










  protected int handleComputeMonthStart(int eyear, int month, boolean useMonth)
  {
    while (month < 0) {
      month += monthsInYear(--eyear);
    }
    
    while (month > 12) {
      month -= monthsInYear(eyear++);
    }
    
    long day = startOfYear(eyear);
    
    if (month != 0) {
      if (isLeapYear(eyear)) {
        day += LEAP_MONTH_START[month][yearType(eyear)];
      } else {
        day += MONTH_START[month][yearType(eyear)];
      }
    }
    
    return (int)(day + 347997L);
  }
}
