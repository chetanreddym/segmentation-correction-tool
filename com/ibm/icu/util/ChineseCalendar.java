package com.ibm.icu.util;

import com.ibm.icu.text.ChineseDateFormat;
import com.ibm.icu.text.DateFormat;
import java.util.Locale;























































































public class ChineseCalendar
  extends Calendar
{
  private transient CalendarAstronomer astro = new CalendarAstronomer();
  




  private transient CalendarCache winterSolsticeCache = new CalendarCache();
  




  private transient CalendarCache newYearCache = new CalendarCache();
  






  private transient boolean isLeapYear;
  







  public ChineseCalendar() {}
  







  public ChineseCalendar(TimeZone zone, Locale locale)
  {
    super(zone, locale);
  }
  









  public static int IS_LEAP_MONTH = 22;
  



  private static final int FIELD_COUNT = IS_LEAP_MONTH + 1;
  







  protected int[] handleCreateFields()
  {
    return new int[FIELD_COUNT];
  }
  




































  private static final int[][] LIMITS = { { 1, 1, 83333, 83333 }, { 1, 1, 70, 70 }, { 0, 0, 11, 11 }, { 1, 1, 50, 55 }, { 1, 1, 5, 6 }, { 1, 1, 29, 30 }, { 1, 1, 353, 385 }, new int[0], { -1, -1, 5, 5 }, new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], { -5000001, -5000001, 5000001, 5000001 }, new int[0], { -5000000, -5000000, 5000000, 5000000 }, new int[0], new int[0], { 0, 0, 1, 1 } };
  





























  protected int handleGetLimit(int field, int limitType)
  {
    return LIMITS[field][limitType];
  }
  



  protected int handleGetExtendedYear()
  {
    int year;
    


    if (newestStamp(0, 1, 0) <= getStamp(19)) {
      year = internalGet(19, 1);
    } else {
      int cycle = internalGet(0, 1) - 1;
      year = cycle * 60 + internalGet(1, 1);
    }
    return year;
  }
  







  protected int handleGetMonthLength(int extendedYear, int month)
  {
    int thisStart = handleComputeMonthStart(extendedYear, month, true) - 2440588 + 1;
    
    int nextStart = newMoonNear(thisStart + 25, true);
    return nextStart - thisStart;
  }
  






  protected DateFormat handleGetDateFormat(String pattern, Locale locale)
  {
    return new ChineseDateFormat(pattern, locale);
  }
  



  static final int[][][] CHINESE_DATE_PRECEDENCE = { { { 5 }, { 3, 7 }, { 4, 7 }, { 8, 7 }, { 3, 18 }, { 4, 18 }, { 8, 18 }, { 6 }, { 37, IS_LEAP_MONTH } }, { { 3 }, { 4 }, { 8 }, { 40, 7 }, { 40, 18 } } };
  




  private static final int CHINESE_EPOCH_YEAR = -2636;
  




  private static final long CHINA_OFFSET = 28800000L;
  




  private static final int SYNODIC_GAP = 25;
  





  protected int[][][] getFieldResolutionTable()
  {
    return CHINESE_DATE_PRECEDENCE;
  }
  











  private void offsetMonth(int newMoon, int dom, int delta)
  {
    newMoon += (int)(29.530588853D * (delta - 0.5D));
    

    newMoon = newMoonNear(newMoon, true);
    

    int jd = newMoon + 2440588 - 1 + dom;
    


    if (dom > 29) {
      set(20, jd - 1);
      



      complete();
      if (getActualMaximum(5) >= dom) {
        set(20, jd);
      }
    } else {
      set(20, jd);
    }
  }
  



  public void add(int field, int amount)
  {
    switch (field) {
    case 2: 
      if (amount != 0) {
        int dom = get(5);
        int day = get(20) - 2440588;
        int moon = day - dom + 1;
        offsetMonth(moon, dom, amount);
      }
      break;
    default: 
      super.add(field, amount);
    }
    
  }
  



  public void roll(int field, int amount)
  {
    switch (field) {
    case 2: 
      if (amount != 0) {
        int dom = get(5);
        int day = get(20) - 2440588;
        int moon = day - dom + 1;
        






        int m = get(2);
        if (isLeapYear) {
          if (get(IS_LEAP_MONTH) == 1) {
            m++;



          }
          else
          {


            int moon1 = moon - (int)(29.530588853D * (m - 0.5D));
            
            moon1 = newMoonNear(moon1, true);
            if (isLeapMonthBetween(moon1, moon)) {
              m++;
            }
          }
        }
        


        int n = isLeapYear ? 13 : 12;
        int newM = (m + amount) % n;
        if (newM < 0) {
          newM += n;
        }
        
        if (newM != m) {
          offsetMonth(moon, dom, newM - m);
        }
      }
      break;
    default: 
      super.roll(field, amount);
    }
    
  }
  






























  private static final long daysToMillis(int days)
  {
    return days * 86400000L - 28800000L;
  }
  




  private static final int millisToDays(long millis)
  {
    return (int)Calendar.floorDivide(millis + 28800000L, 86400000L);
  }
  












  private int winterSolstice(int gyear)
  {
    long cacheValue = winterSolsticeCache.get(gyear);
    
    if (cacheValue == CalendarCache.EMPTY)
    {



      long ms = daysToMillis(computeGregorianMonthStart(gyear, 11) + 1 - 2440588);
      
      astro.setTime(ms);
      

      long solarLong = astro.getSunTime(CalendarAstronomer.WINTER_SOLSTICE, true);
      
      cacheValue = millisToDays(solarLong);
      winterSolsticeCache.put(gyear, cacheValue);
    }
    return (int)cacheValue;
  }
  









  private int newMoonNear(int days, boolean after)
  {
    astro.setTime(daysToMillis(days));
    long newMoon = astro.getMoonTime(CalendarAstronomer.NEW_MOON, after);
    
    return millisToDays(newMoon);
  }
  






  private int synodicMonthsBetween(int day1, int day2)
  {
    return (int)Math.round((day2 - day1) / 29.530588853D);
  }
  






  private int majorSolarTerm(int days)
  {
    astro.setTime(daysToMillis(days));
    

    int term = ((int)Math.floor(6.0D * astro.getSunLongitude() / 3.141592653589793D) + 2) % 12;
    if (term < 1) {
      term += 12;
    }
    return term;
  }
  





  private boolean hasNoMajorSolarTerm(int newMoon)
  {
    int mst = majorSolarTerm(newMoon);
    int nmn = newMoonNear(newMoon + 25, true);
    int mstt = majorSolarTerm(nmn);
    return mst == mstt;
  }
  



















  private boolean isLeapMonthBetween(int newMoon1, int newMoon2)
  {
    if (synodicMonthsBetween(newMoon1, newMoon2) >= 50) {
      throw new IllegalArgumentException("isLeapMonthBetween(" + newMoon1 + ", " + newMoon2 + "): Invalid parameters");
    }
    


    return (newMoon2 >= newMoon1) && ((isLeapMonthBetween(newMoon1, newMoonNear(newMoon2 - 25, false))) || (hasNoMajorSolarTerm(newMoon2)));
  }
  




















  protected void handleComputeFields(int julianDay)
  {
    computeChineseFields(julianDay - 2440588, getGregorianYear(), getGregorianMonth(), true);
  }
  
























  private void computeChineseFields(int days, int gyear, int gmonth, boolean setAllFields)
  {
    int solsticeAfter = winterSolstice(gyear);
    int solsticeBefore; if (days < solsticeAfter) {
      solsticeBefore = winterSolstice(gyear - 1);
    } else {
      solsticeBefore = solsticeAfter;
      solsticeAfter = winterSolstice(gyear + 1);
    }
    



    int firstMoon = newMoonNear(solsticeBefore + 1, true);
    int lastMoon = newMoonNear(solsticeAfter + 1, false);
    int thisMoon = newMoonNear(days + 1, false);
    
    isLeapYear = (synodicMonthsBetween(firstMoon, lastMoon) == 12);
    
    int month = synodicMonthsBetween(firstMoon, thisMoon);
    if ((isLeapYear) && (isLeapMonthBetween(firstMoon, thisMoon))) {
      month--;
    }
    if (month < 1) {
      month += 12;
    }
    
    boolean isLeapMonth = (isLeapYear) && (hasNoMajorSolarTerm(thisMoon)) && (!isLeapMonthBetween(firstMoon, newMoonNear(thisMoon - 25, false)));
    


    internalSet(2, month - 1);
    internalSet(IS_LEAP_MONTH, isLeapMonth ? 1 : 0);
    
    if (setAllFields)
    {
      int year = gyear - 62900;
      if ((month < 11) || (gmonth >= 6))
      {
        year++;
      }
      int dayOfMonth = days - thisMoon + 1;
      
      internalSet(19, year);
      

      int[] yearOfCycle = new int[1];
      int cycle = Calendar.floorDivide(year - 1, 60, yearOfCycle);
      internalSet(0, cycle + 1);
      internalSet(1, yearOfCycle[0] + 1);
      
      internalSet(5, dayOfMonth);
      




      int newYear = newYear(gyear);
      if (days < newYear) {
        newYear = newYear(gyear - 1);
      }
      internalSet(6, days - newYear + 1);
    }
  }
  










  private int newYear(int gyear)
  {
    long cacheValue = newYearCache.get(gyear);
    
    if (cacheValue == CalendarCache.EMPTY)
    {
      int solsticeBefore = winterSolstice(gyear - 1);
      int solsticeAfter = winterSolstice(gyear);
      int newMoon1 = newMoonNear(solsticeBefore + 1, true);
      int newMoon2 = newMoonNear(newMoon1 + 25, true);
      int newMoon11 = newMoonNear(solsticeAfter + 1, false);
      
      if ((synodicMonthsBetween(newMoon1, newMoon11) == 12) && ((hasNoMajorSolarTerm(newMoon1)) || (hasNoMajorSolarTerm(newMoon2))))
      {
        cacheValue = newMoonNear(newMoon2 + 25, true);
      } else {
        cacheValue = newMoon2;
      }
      
      newYearCache.put(gyear, cacheValue);
    }
    return (int)cacheValue;
  }
  















  protected int handleComputeMonthStart(int eyear, int month, boolean useMonth)
  {
    if ((month < 0) || (month > 11)) {
      int[] rem = new int[1];
      eyear += Calendar.floorDivide(month, 12, rem);
      month = rem[0];
    }
    
    int gyear = eyear + 62900 - 1;
    int newYear = newYear(gyear);
    int newMoon = newMoonNear(newYear + month * 29, true);
    
    int julianDay = newMoon + 2440588;
    

    int saveMonth = internalGet(2);
    int saveIsLeapMonth = internalGet(IS_LEAP_MONTH);
    

    int isLeapMonth = useMonth ? saveIsLeapMonth : 0;
    
    computeGregorianFields(julianDay);
    

    computeChineseFields(newMoon, getGregorianYear(), getGregorianMonth(), false);
    

    if ((month != internalGet(2)) || (isLeapMonth != internalGet(IS_LEAP_MONTH)))
    {
      newMoon = newMoonNear(newMoon + 25, true);
      julianDay = newMoon + 2440588;
    }
    
    internalSet(2, saveMonth);
    internalSet(IS_LEAP_MONTH, saveIsLeapMonth);
    
    return julianDay - 1;
  }
}
