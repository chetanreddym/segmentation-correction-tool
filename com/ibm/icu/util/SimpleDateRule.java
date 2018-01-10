package com.ibm.icu.util;

import java.util.Date;



























public class SimpleDateRule
  implements DateRule
{
  public SimpleDateRule(int month, int dayOfMonth)
  {
    this.month = month;
    this.dayOfMonth = dayOfMonth;
    dayOfWeek = 0;
  }
  











  public SimpleDateRule(int month, int dayOfMonth, int dayOfWeek, boolean after)
  {
    this.month = month;
    this.dayOfMonth = dayOfMonth;
    this.dayOfWeek = (after ? dayOfWeek : -dayOfWeek);
  }
  












  public Date firstAfter(Date start)
  {
    if ((startDate != null) && (start.before(startDate))) {
      start = startDate;
    }
    return doFirstBetween(start, endDate);
  }
  















  public Date firstBetween(Date start, Date end)
  {
    if ((startDate != null) && (start.before(startDate))) {
      start = startDate;
    }
    if ((endDate != null) && (end.after(endDate))) {
      end = endDate;
    }
    return doFirstBetween(start, end);
  }
  












  public boolean isOn(Date date)
  {
    if ((startDate != null) && (date.before(startDate))) {
      return false;
    }
    if ((endDate != null) && (date.after(endDate))) {
      return false;
    }
    
    Calendar c = calendar;
    
    synchronized (c) {
      c.setTime(date);
      
      int dayOfYear = c.get(6);
      
      c.setTime(computeInYear(c.get(1), c));
      



      boolean bool = c.get(6) == dayOfYear;return bool;
    }
  }
  





  public boolean isBetween(Date start, Date end)
  {
    return firstBetween(start, end) != null;
  }
  
  private Date doFirstBetween(Date start, Date end)
  {
    Calendar c = calendar;
    
    synchronized (c) {
      c.setTime(start);
      
      int year = c.get(1);
      int month = c.get(2);
      


      if (month > this.month) {
        year++;
      }
      

      Date result = computeInYear(year, c);
      


      if ((month == this.month) && (result.before(start))) {
        result = computeInYear(year + 1, c);
      }
      
      if ((end != null) && (result.after(end))) {
        localDate1 = null;return localDate1;
      }
      Date localDate1 = result;return localDate1;
    }
  }
  
  private Date computeInYear(int year, Calendar c)
  {
    if (c == null) { c = calendar;
    }
    synchronized (c) {
      c.clear();
      c.set(0, c.getMaximum(0));
      c.set(1, year);
      c.set(2, month);
      c.set(5, dayOfMonth);
      


      if (dayOfWeek != 0) {
        c.setTime(c.getTime());
        weekday = c.get(7);
        



        int delta = 0;
        if (dayOfWeek > 0)
        {

          delta = (dayOfWeek - weekday + 7) % 7;
        }
        else if (dayOfWeek < 0)
        {

          delta = -((dayOfWeek + weekday + 7) % 7);
        }
        
        c.add(5, delta);
      }
      
      int weekday = c.getTime();return weekday;
    }
  }
  


  public void setCalendar(Calendar c)
  {
    calendar = c;
  }
  
  static GregorianCalendar gCalendar = new GregorianCalendar(new SimpleTimeZone(0, "UTC"));
  
  Calendar calendar = gCalendar;
  
  private int month;
  
  private int dayOfMonth;
  private int dayOfWeek;
  private Date startDate = null;
  private Date endDate = null;
}
