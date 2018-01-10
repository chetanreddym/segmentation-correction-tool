package com.ibm.icu.util;

import java.util.Date;







































public class SimpleTimeZoneAdapter
  extends java.util.TimeZone
{
  private SimpleTimeZone zone;
  
  public SimpleTimeZoneAdapter(SimpleTimeZone zone)
  {
    this.zone = zone;
  }
  



  public String getID()
  {
    return zone.getID();
  }
  



  public void setID(String ID)
  {
    zone.setID(ID);
  }
  



  public boolean hasSameRules(java.util.TimeZone other)
  {
    return ((other instanceof SimpleTimeZoneAdapter)) && (zone.hasSameRules(zone));
  }
  





  public int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis)
  {
    return zone.getOffset(era, year, month, day, dayOfWeek, millis);
  }
  


















  public int getRawOffset()
  {
    return zone.getRawOffset();
  }
  



  public void setRawOffset(int offsetMillis)
  {
    zone.setRawOffset(offsetMillis);
  }
  



  public boolean useDaylightTime()
  {
    return zone.useDaylightTime();
  }
  



  public boolean inDaylightTime(Date date)
  {
    return zone.inDaylightTime(date);
  }
  



  public Object clone()
  {
    return new SimpleTimeZoneAdapter((SimpleTimeZone)zone.clone());
  }
  



  public synchronized int hashCode()
  {
    return zone.hashCode();
  }
  







  public boolean equals(Object obj)
  {
    if ((obj instanceof SimpleTimeZoneAdapter)) {
      obj = zone;
    }
    return zone.equals(obj);
  }
  





  public String toString()
  {
    return zone.toString();
  }
}
