package com.ibm.icu.util;

import java.util.Date;
import java.util.Locale;



































public class BuddhistCalendar
  extends GregorianCalendar
{
  private static String copyright = "Copyright © 1998 IBM Corp. All Rights Reserved.";
  





  public static final int BE = 0;
  





  private static final int BUDDHIST_ERA_START = -543;
  






  public BuddhistCalendar() {}
  






  public BuddhistCalendar(TimeZone zone)
  {
    super(zone);
  }
  






  public BuddhistCalendar(Locale aLocale)
  {
    super(aLocale);
  }
  








  public BuddhistCalendar(TimeZone zone, Locale aLocale)
  {
    super(zone, aLocale);
  }
  






  public BuddhistCalendar(Date date)
  {
    this();
    setTime(date);
  }
  











  public BuddhistCalendar(int year, int month, int date)
  {
    super(year, month, date);
  }
  



















  public BuddhistCalendar(int year, int month, int date, int hour, int minute, int second)
  {
    super(year, month, date, hour, minute, second);
  }
  






  protected int handleGetExtendedYear()
  {
    int year;
    





    if (newerField(19, 1) == 19) {
      year = internalGet(19, 1);
    }
    else {
      year = internalGet(1, 1);
    }
    return year;
  }
  



  protected int handleComputeMonthStart(int eyear, int month, boolean useMonth)
  {
    return super.handleComputeMonthStart(eyear + 64993, month, useMonth);
  }
  


  protected void handleComputeFields(int julianDay)
  {
    super.handleComputeFields(julianDay);
    int y = internalGet(19) - 64993;
    internalSet(19, y);
    internalSet(0, 0);
    internalSet(1, y);
  }
  





  protected int handleGetLimit(int field, int limitType)
  {
    if (field == 0) {
      return 0;
    }
    return super.handleGetLimit(field, limitType);
  }
}
