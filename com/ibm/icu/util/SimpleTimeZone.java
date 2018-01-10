package com.ibm.icu.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;



















public class SimpleTimeZone
  extends TimeZone
{
  private int startMonth;
  private int startDay;
  private int startDayOfWeek;
  private int startTime;
  private int startTimeMode;
  private int endMonth;
  private int endDay;
  private int endDayOfWeek;
  private int endTime;
  private int endTimeMode;
  private int startYear;
  private int rawOffset;
  
  public SimpleTimeZone(int rawOffset, String ID)
  {
    this.rawOffset = rawOffset;
    setID(ID);
    dstSavings = 3600000;
  }
  




























































  public SimpleTimeZone(int rawOffset, String ID, int startMonth, int startDay, int startDayOfWeek, int startTime, int endMonth, int endDay, int endDayOfWeek, int endTime)
  {
    this(rawOffset, ID, startMonth, startDay, startDayOfWeek, startTime, 0, endMonth, endDay, endDayOfWeek, endTime, 0, 3600000);
  }
  














  public SimpleTimeZone(int rawOffset, String ID, int startMonth, int startDay, int startDayOfWeek, int startTime, int endMonth, int endDay, int endDayOfWeek, int endTime, int dstSavings)
  {
    this(rawOffset, ID, startMonth, startDay, startDayOfWeek, startTime, 0, endMonth, endDay, endDayOfWeek, endTime, 0, dstSavings);
  }
  










  SimpleTimeZone(int rawOffset, String ID, int startMonth, int startDay, int startDayOfWeek, int startTime, int startTimeMode, int endMonth, int endDay, int endDayOfWeek, int endTime, int endTimeMode, int dstSavings)
  {
    setID(ID);
    this.rawOffset = rawOffset;
    this.startMonth = startMonth;
    this.startDay = startDay;
    this.startDayOfWeek = startDayOfWeek;
    this.startTime = startTime;
    this.startTimeMode = startTimeMode;
    this.endMonth = endMonth;
    this.endDay = endDay;
    this.endDayOfWeek = endDayOfWeek;
    this.endTime = endTime;
    this.endTimeMode = endTimeMode;
    this.dstSavings = dstSavings;
    
    decodeRules();
    if (dstSavings <= 0) {
      throw new IllegalArgumentException("Illegal DST savings");
    }
  }
  



  SimpleTimeZone(String ID, int[] data, int i)
  {
    setID(ID);
    rawOffset = (data[(i + 1)] * 1000);
    if (data[i] == 0) {
      dstSavings = 3600000;
    } else {
      startMonth = data[(i + 2)];
      startDay = data[(i + 3)];
      startDayOfWeek = data[(i + 4)];
      startTime = (data[(i + 5)] * 60000);
      startTimeMode = data[(i + 6)];
      endMonth = data[(i + 7)];
      endDay = data[(i + 8)];
      endDayOfWeek = data[(i + 9)];
      endTime = (data[(i + 10)] * 60000);
      endTimeMode = data[(i + 11)];
      dstSavings = (data[(i + 12)] * 60000);
      decodeRules();
      if (dstSavings <= 0) {
        throw new IllegalArgumentException("Illegal DST savings");
      }
    }
  }
  






  public void setStartYear(int year)
  {
    startYear = year;
  }
  






















  public void setStartRule(int month, int dayOfWeekInMonth, int dayOfWeek, int time)
  {
    startMonth = month;
    startDay = dayOfWeekInMonth;
    startDayOfWeek = dayOfWeek;
    startTime = time;
    startTimeMode = 0;
    
    decodeStartRule();
  }
  











  public void setStartRule(int month, int dayOfMonth, int time)
  {
    setStartRule(month, dayOfMonth, 0, time);
  }
  

















  public void setStartRule(int month, int dayOfMonth, int dayOfWeek, int time, boolean after)
  {
    if (after) {
      setStartRule(month, dayOfMonth, -dayOfWeek, time);
    } else {
      setStartRule(month, -dayOfMonth, -dayOfWeek, time);
    }
  }
  




















  public void setEndRule(int month, int dayOfWeekInMonth, int dayOfWeek, int time)
  {
    endMonth = month;
    endDay = dayOfWeekInMonth;
    endDayOfWeek = dayOfWeek;
    endTime = time;
    endTimeMode = 0;
    
    decodeEndRule();
  }
  












  public void setEndRule(int month, int dayOfMonth, int time)
  {
    setEndRule(month, dayOfMonth, 0, time);
  }
  

















  public void setEndRule(int month, int dayOfMonth, int dayOfWeek, int time, boolean after)
  {
    if (after) {
      setEndRule(month, dayOfMonth, -dayOfWeek, time);
    } else {
      setEndRule(month, -dayOfMonth, -dayOfWeek, time);
    }
  }
  

































  public int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis)
  {
    if ((month < 0) || (month > 11))
    {
      throw new IllegalArgumentException("Illegal month " + month);
    }
    


    if (internalCal == null)
    {


      internalCal = new GregorianCalendar(0, 0, 0);
    }
    
    int monthLength;
    int prevMonthLength;
    if ((era == 1) && (internalCal.isLeapYear(year))) {
      monthLength = staticLeapMonthLength[month];
      prevMonthLength = month > 1 ? staticLeapMonthLength[(month - 1)] : 31;
    } else {
      monthLength = staticMonthLength[month];
      prevMonthLength = month > 1 ? staticMonthLength[(month - 1)] : 31;
    }
    
    return getOffset(era, year, month, day, dayOfWeek, millis, monthLength, prevMonthLength);
  }
  











































  int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis, int monthLength, int prevMonthLength)
  {
    if ((era != 1) && (era != 0)) {
      throw new IllegalArgumentException("Illegal era " + era);
    }
    if ((month < 0) || (month > 11))
    {
      throw new IllegalArgumentException("Illegal month " + month);
    }
    if ((day < 1) || (day > monthLength))
    {
      throw new IllegalArgumentException("Illegal day " + day);
    }
    if ((dayOfWeek < 1) || (dayOfWeek > 7))
    {
      throw new IllegalArgumentException("Illegal day of week " + dayOfWeek);
    }
    if ((millis < 0) || (millis >= 86400000))
    {
      throw new IllegalArgumentException("Illegal millis " + millis);
    }
    if ((monthLength < 28) || (monthLength > 31))
    {
      throw new IllegalArgumentException("Illegal month length " + monthLength);
    }
    if ((prevMonthLength < 28) || (prevMonthLength > 31))
    {
      throw new IllegalArgumentException("Illegal previous month length " + prevMonthLength);
    }
    

    int result = rawOffset;
    

    if ((!useDaylight) || (year < startYear) || (era != 1)) { return result;
    }
    

    boolean southern = startMonth > endMonth;
    


    int startCompare = compareToRule(month, monthLength, prevMonthLength, day, dayOfWeek, millis, startTimeMode == 2 ? -rawOffset : 0, startMode, startMonth, startDayOfWeek, startDay, startTime);
    



    int endCompare = 0;
    






    if (southern != startCompare >= 0)
    {


      endCompare = compareToRule(month, monthLength, prevMonthLength, day, dayOfWeek, millis, endTimeMode == 2 ? -rawOffset : endTimeMode == 0 ? dstSavings : 0, endMode, endMonth, endDayOfWeek, endDay, endTime);
    }
    









    if (((!southern) && (startCompare >= 0) && (endCompare < 0)) || ((southern) && ((startCompare >= 0) || (endCompare < 0))))
    {
      result += dstSavings;
    }
    return result;
  }
  














  private static int compareToRule(int month, int monthLen, int prevMonthLen, int dayOfMonth, int dayOfWeek, int millis, int millisDelta, int ruleMode, int ruleMonth, int ruleDayOfWeek, int ruleDay, int ruleMillis)
  {
    millis += millisDelta;
    while (millis >= 86400000) {
      millis -= 86400000;
      dayOfMonth++;
      dayOfWeek = 1 + dayOfWeek % 7;
      if (dayOfMonth > monthLen) {
        dayOfMonth = 1;
        



        month++;
      }
    }
    while (millis < 0) {
      millis += 86400000;
      dayOfMonth--;
      dayOfWeek = 1 + (dayOfWeek + 5) % 7;
      if (dayOfMonth < 1) {
        dayOfMonth = prevMonthLen;
        month--;
      }
    }
    
    if (month < ruleMonth) return -1;
    if (month > ruleMonth) { return 1;
    }
    int ruleDayOfMonth = 0;
    switch (ruleMode)
    {
    case 1: 
      ruleDayOfMonth = ruleDay;
      break;
    
    case 2: 
      if (ruleDay > 0) {
        ruleDayOfMonth = 1 + (ruleDay - 1) * 7 + (7 + ruleDayOfWeek - (dayOfWeek - dayOfMonth + 1)) % 7;
      }
      else
      {
        ruleDayOfMonth = monthLen + (ruleDay + 1) * 7 - (7 + (dayOfWeek + monthLen - dayOfMonth) - ruleDayOfWeek) % 7;
      }
      
      break;
    case 3: 
      ruleDayOfMonth = ruleDay + (49 + ruleDayOfWeek - ruleDay - dayOfWeek + dayOfMonth) % 7;
      
      break;
    case 4: 
      ruleDayOfMonth = ruleDay - (49 - ruleDayOfWeek + ruleDay + dayOfWeek - dayOfMonth) % 7;
    }
    
    



    if (dayOfMonth < ruleDayOfMonth) return -1;
    if (dayOfMonth > ruleDayOfMonth) { return 1;
    }
    if (millis < ruleMillis) return -1;
    if (millis > ruleMillis) return 1;
    return 0;
  }
  







  public int getRawOffset()
  {
    return rawOffset;
  }
  







  public void setRawOffset(int offsetMillis)
  {
    rawOffset = offsetMillis;
  }
  






  public void setDSTSavings(int millisSavedDuringDST)
  {
    if (millisSavedDuringDST <= 0) {
      throw new IllegalArgumentException("Illegal DST savings");
    }
    dstSavings = millisSavedDuringDST;
  }
  






  public int getDSTSavings()
  {
    return dstSavings;
  }
  





  public boolean useDaylightTime()
  {
    return useDaylight;
  }
  





  public boolean inDaylightTime(Date date)
  {
    GregorianCalendar gc = new GregorianCalendar(this);
    gc.setTime(date);
    return gc.inDaylightTime();
  }
  




  public Object clone()
  {
    return super.clone();
  }
  






  public synchronized int hashCode()
  {
    return startMonth ^ startDay ^ startDayOfWeek ^ startTime ^ endMonth ^ endDay ^ endDayOfWeek ^ endTime ^ rawOffset;
  }
  









  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (!(obj instanceof SimpleTimeZone)) {
      return false;
    }
    SimpleTimeZone that = (SimpleTimeZone)obj;
    
    return (getID().equals(that.getID())) && (hasSameRules(that));
  }
  






  public boolean hasSameRules(TimeZone other)
  {
    if (this == other) return true;
    if (!(other instanceof SimpleTimeZone)) return false;
    SimpleTimeZone that = (SimpleTimeZone)other;
    return (super.hasSameRules(other)) && ((!useDaylight) || ((dstSavings == dstSavings) && (startMode == startMode) && (startMonth == startMonth) && (startDay == startDay) && (startDayOfWeek == startDayOfWeek) && (startTime == startTime) && (startTimeMode == startTimeMode) && (endMode == endMode) && (endMonth == endMonth) && (endDay == endDay) && (endDayOfWeek == endDayOfWeek) && (endTime == endTime) && (endTimeMode == endTimeMode) && (startYear == startYear)));
  }
  




















  public String toString()
  {
    return getClass().getName() + "[id=" + getID() + ",offset=" + rawOffset + ",dstSavings=" + dstSavings + ",useDaylight=" + useDaylight + ",startYear=" + startYear + ",startMode=" + startMode + ",startMonth=" + startMonth + ",startDay=" + startDay + ",startDayOfWeek=" + startDayOfWeek + ",startTime=" + startTime + ",startTimeMode=" + startTimeMode + ",endMode=" + endMode + ",endMonth=" + endMonth + ",endDay=" + endDay + ",endDayOfWeek=" + endDayOfWeek + ",endTime=" + endTime + ",endTimeMode=" + endTimeMode + ']';
  }
  


































































































































































  private boolean useDaylight = false;
  



  private static final int millisPerHour = 3600000;
  


  private static final int millisPerDay = 86400000;
  


  private final byte[] monthLength = staticMonthLength;
  private static final byte[] staticMonthLength = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
  private static final byte[] staticLeapMonthLength = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
  
  private static GregorianCalendar internalCal = null;
  








  private int startMode;
  








  private int endMode;
  








  private int dstSavings;
  








  private static final int DOM_MODE = 1;
  








  private static final int DOW_IN_MONTH_MODE = 2;
  








  private static final int DOW_GE_DOM_MODE = 3;
  








  private static final int DOW_LE_DOM_MODE = 4;
  








  static final int WALL_TIME = 0;
  







  static final int STANDARD_TIME = 1;
  







  static final int UTC_TIME = 2;
  







  static final long serialVersionUID = -403250971215465050L;
  







  static final int currentSerialVersion = 2;
  







  private int serialVersionOnStream = 2;
  































































  private void decodeRules()
  {
    decodeStartRule();
    decodeEndRule();
  }
  























  private void decodeStartRule()
  {
    useDaylight = ((startDay != 0) && (endDay != 0));
    if (startDay != 0) {
      if ((startMonth < 0) || (startMonth > 11)) {
        throw new IllegalArgumentException("Illegal start month " + startMonth);
      }
      
      if ((startTime < 0) || (startTime >= 86400000)) {
        throw new IllegalArgumentException("Illegal start time " + startTime);
      }
      
      if (startDayOfWeek == 0) {
        startMode = 1;
      } else {
        if (startDayOfWeek > 0) {
          startMode = 2;
        } else {
          startDayOfWeek = (-startDayOfWeek);
          if (startDay > 0) {
            startMode = 3;
          } else {
            startDay = (-startDay);
            startMode = 4;
          }
        }
        if (startDayOfWeek > 7) {
          throw new IllegalArgumentException("Illegal start day of week " + startDayOfWeek);
        }
      }
      
      if (startMode == 2) {
        if ((startDay < -5) || (startDay > 5)) {
          throw new IllegalArgumentException("Illegal start day of week in month " + startDay);
        }
      }
      else if ((startDay < 1) || (startDay > staticMonthLength[startMonth])) {
        throw new IllegalArgumentException("Illegal start day " + startDay);
      }
    }
  }
  





  private void decodeEndRule()
  {
    useDaylight = ((startDay != 0) && (endDay != 0));
    if (endDay != 0) {
      if ((endMonth < 0) || (endMonth > 11)) {
        throw new IllegalArgumentException("Illegal end month " + endMonth);
      }
      
      if ((endTime < 0) || (endTime >= 86400000)) {
        throw new IllegalArgumentException("Illegal end time " + endTime);
      }
      
      if (endDayOfWeek == 0) {
        endMode = 1;
      } else {
        if (endDayOfWeek > 0) {
          endMode = 2;
        } else {
          endDayOfWeek = (-endDayOfWeek);
          if (endDay > 0) {
            endMode = 3;
          } else {
            endDay = (-endDay);
            endMode = 4;
          }
        }
        if (endDayOfWeek > 7) {
          throw new IllegalArgumentException("Illegal end day of week " + endDayOfWeek);
        }
      }
      
      if (endMode == 2) {
        if ((endDay < -5) || (endDay > 5)) {
          throw new IllegalArgumentException("Illegal end day of week in month " + endDay);
        }
      }
      else if ((endDay < 1) || (endDay > staticMonthLength[endMonth])) {
        throw new IllegalArgumentException("Illegal end day " + endDay);
      }
    }
  }
  










  private void makeRulesCompatible()
  {
    switch (startMode)
    {
    case 1: 
      startDay = (1 + startDay / 7);
      startDayOfWeek = 1;
      break;
    

    case 3: 
      if (startDay != 1)
        startDay = (1 + startDay / 7);
      break;
    case 4: 
      if (startDay >= 30) {
        startDay = -1;
      } else {
        startDay = (1 + startDay / 7);
      }
      break;
    }
    switch (endMode)
    {
    case 1: 
      endDay = (1 + endDay / 7);
      endDayOfWeek = 1;
      break;
    

    case 3: 
      if (endDay != 1)
        endDay = (1 + endDay / 7);
      break;
    case 4: 
      if (endDay >= 30) {
        endDay = -1;
      } else {
        endDay = (1 + endDay / 7);
      }
      



      break;
    }
    
    


    switch (startTimeMode) {
    case 2: 
      startTime += rawOffset;
    }
    
    while (startTime < 0) {
      startTime += 86400000;
      startDayOfWeek = (1 + (startDayOfWeek + 5) % 7);
    }
    while (startTime >= 86400000) {
      startTime -= 86400000;
      startDayOfWeek = (1 + startDayOfWeek % 7);
    }
    
    switch (endTimeMode) {
    case 2: 
      endTime += rawOffset + dstSavings;
      break;
    case 1: 
      endTime += dstSavings;
    }
    while (endTime < 0) {
      endTime += 86400000;
      endDayOfWeek = (1 + (endDayOfWeek + 5) % 7);
    }
    while (endTime >= 86400000) {
      endTime -= 86400000;
      endDayOfWeek = (1 + endDayOfWeek % 7);
    }
  }
  




  private byte[] packRules()
  {
    byte[] rules = new byte[6];
    rules[0] = ((byte)startDay);
    rules[1] = ((byte)startDayOfWeek);
    rules[2] = ((byte)endDay);
    rules[3] = ((byte)endDayOfWeek);
    

    rules[4] = ((byte)startTimeMode);
    rules[5] = ((byte)endTimeMode);
    
    return rules;
  }
  




  private void unpackRules(byte[] rules)
  {
    startDay = rules[0];
    startDayOfWeek = rules[1];
    endDay = rules[2];
    endDayOfWeek = rules[3];
    

    if (rules.length >= 6) {
      startTimeMode = rules[4];
      endTimeMode = rules[5];
    }
  }
  



  private int[] packTimes()
  {
    int[] times = new int[2];
    times[0] = startTime;
    times[1] = endTime;
    return times;
  }
  



  private void unpackTimes(int[] times)
  {
    startTime = times[0];
    endTime = times[1];
  }
  

















  private void writeObject(ObjectOutputStream stream)
    throws IOException
  {
    byte[] rules = packRules();
    int[] times = packTimes();
    

    makeRulesCompatible();
    

    stream.defaultWriteObject();
    

    stream.writeInt(rules.length);
    stream.write(rules);
    stream.writeObject(times);
    


    unpackRules(rules);
    unpackTimes(times);
  }
  






  private void readObject(ObjectInputStream stream)
    throws IOException, ClassNotFoundException
  {
    stream.defaultReadObject();
    
    if (serialVersionOnStream < 1)
    {



      if (startDayOfWeek == 0) startDayOfWeek = 1;
      if (endDayOfWeek == 0) { endDayOfWeek = 1;
      }
      

      startMode = (this.endMode = 2);
      dstSavings = 3600000;


    }
    else
    {

      int length = stream.readInt();
      byte[] rules = new byte[length];
      stream.readFully(rules);
      unpackRules(rules);
    }
    
    if (serialVersionOnStream >= 2) {
      int[] times = (int[])stream.readObject();
      unpackTimes(times);
    }
    
    serialVersionOnStream = 2;
  }
}
