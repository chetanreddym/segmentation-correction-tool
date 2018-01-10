package ocr.tag;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class GetDateTime
{
  public GetDateTime() {}
  
  public static String getInstance()
  {
    Calendar calendar = new GregorianCalendar();
    return calendar.getTime();
  }
  









  public static String getCurrentDateTime()
  {
    Calendar c = Calendar.getInstance();
    int month = c.get(2) + 1;
    int day_of_month = c.get(5);
    int year = c.get(1);
    int hour_of_day = c.get(11);
    int minute = c.get(12);
    String minuteStr = Integer.toString(minute);
    
    if (minute <= 9) {
      minuteStr = "0" + minuteStr;
    }
    
    String dateStr = month + "/" + 
      day_of_month + "/" + 
      year + " " + 
      hour_of_day + ":" + 
      minuteStr;
    
    return dateStr;
  }
  
  public static String getCurrentDateTimeWithSeconds() {
    Calendar c = Calendar.getInstance();
    int month = c.get(2) + 1;
    int day_of_month = c.get(5);
    int year = c.get(1);
    int hour_of_day = c.get(11);
    int minute = c.get(12);
    String minuteStr = Integer.toString(minute);
    int second = c.get(13);
    String secondStr = Integer.toString(second);
    
    if (minute <= 9) {
      minuteStr = "0" + minuteStr;
    }
    if (second <= 9) {
      secondStr = "0" + secondStr;
    }
    
    String dateStr = month + "/" + 
      day_of_month + "/" + 
      year + " " + 
      hour_of_day + ":" + 
      minuteStr + ":" + 
      secondStr;
    
    return dateStr;
  }
  
  public static String getCurrentTime() {
    Calendar c = Calendar.getInstance();
    int hour_of_day = c.get(11);
    int minute = c.get(12);
    String minuteStr = Integer.toString(minute);
    int second = c.get(13);
    String secondStr = Integer.toString(second);
    
    if (minute <= 9) {
      minuteStr = "0" + minuteStr;
    }
    if (second <= 9) {
      secondStr = "0" + secondStr;
    }
    
    String dateStr = hour_of_day + ":" + 
      minuteStr + ":" + 
      secondStr;
    
    return dateStr;
  }
}
