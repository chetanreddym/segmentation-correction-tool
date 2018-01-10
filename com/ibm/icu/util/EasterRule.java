package com.ibm.icu.util;

import java.util.Date;






































































































































class EasterRule
  implements DateRule
{
  public EasterRule(int daysAfterEaster, boolean isOrthodox)
  {
    this.daysAfterEaster = daysAfterEaster;
    if (isOrthodox) {
      orthodox.setGregorianChange(new Date(Long.MAX_VALUE));
      calendar = orthodox;
    }
  }
  



  public Date firstAfter(Date start)
  {
    if ((startDate != null) && (start.before(startDate))) {
      start = startDate;
    }
    return doFirstBetween(start, null);
  }
  





  public Date firstBetween(Date start, Date end)
  {
    if ((startDate != null) && (start.before(startDate))) {
      start = startDate;
    }
    return doFirstBetween(start, end);
  }
  



  public boolean isOn(Date date)
  {
    if ((startDate != null) && (date.before(startDate))) {
      return false;
    }
    
    synchronized (calendar) {
      calendar.setTime(date);
      int dayOfYear = calendar.get(6);
      
      calendar.setTime(computeInYear(calendar.getTime(), calendar));
      
      boolean bool = calendar.get(6) == dayOfYear;return bool;
    }
  }
  



  public boolean isBetween(Date start, Date end)
  {
    return firstBetween(start, end) != null;
  }
  



  private Date doFirstBetween(Date start, Date end)
  {
    synchronized (calendar)
    {
      Date result = computeInYear(start, calendar);
      




      if (result.before(start))
      {
        calendar.setTime(start);
        calendar.get(1);
        calendar.add(1, 1);
        



        result = computeInYear(calendar.getTime(), calendar);
      }
      

      if ((end != null) && (result.after(end)))
      {
        localDate1 = null;return localDate1;
      }
      Date localDate1 = result;return localDate1;
    }
  }
  









  private Date computeInYear(Date date, GregorianCalendar cal)
  {
    if (cal == null) { cal = calendar;
    }
    synchronized (cal) {
      cal.setTime(date);
      
      int year = cal.get(1);
      int g = year % 19;
      int i = 0;
      int j = 0;
      
      if (cal.getTime().after(cal.getGregorianChange()))
      {

        int c = year / 100;
        int h = (c - c / 4 - (8 * c + 13) / 25 + 19 * g + 15) % 30;
        i = h - h / 28 * (1 - h / 28 * (29 / (h + 1)) * ((21 - g) / 11));
        j = (year + year / 4 + i + 2 - c + c / 4) % 7;

      }
      else
      {
        i = (19 * g + 15) % 30;
        j = (year + year / 4 + i) % 7;
      }
      int l = i - j;
      int m = 3 + (l + 40) / 44;
      int d = l + 28 - 31 * (m / 4);
      
      cal.clear();
      cal.set(0, 1);
      cal.set(1, year);
      cal.set(2, m - 1);
      cal.set(5, d);
      cal.getTime();
      cal.add(5, daysAfterEaster);
      
      Date localDate = cal.getTime();return localDate;
    }
  }
  
  static GregorianCalendar gregorian = new GregorianCalendar(new SimpleTimeZone(0, "UTC"));
  static GregorianCalendar orthodox = new GregorianCalendar(new SimpleTimeZone(0, "UTC"));
  
  int daysAfterEaster;
  Date startDate = null;
  GregorianCalendar calendar = gregorian;
}
