package other;





public class TimeUtil
{
  public TimeUtil() {}
  



  public static String convertSecsToStr(int seconds)
  {
    if (seconds < 0) { return "-1";
    }
    

    int secs = seconds % 60;
    int minutes = seconds / 60;
    int mins = minutes % 60;
    int hours = minutes / 60;
    String time;
    String time; if (hours < 10) {
      time = "0" + hours + ":";
    }
    else {
      time = hours + ":";
    }
    
    if (mins < 10) {
      time = time + "0" + mins + ":";
    }
    else {
      time = time + mins + ":";
    }
    
    if (secs < 10) {
      time = time + "0" + secs;
    }
    else {
      time = time + secs;
    }
    
    return time;
  }
  




  public static int convertStrToSecs(String timeStr)
  {
    int secs = 0;
    
    timeStr = timeStr.trim();
    
    secs += Integer.parseInt(timeStr.substring(6, 8));
    secs += Integer.parseInt(timeStr.substring(3, 5)) * 60;
    secs += Integer.parseInt(timeStr.substring(0, 2)) * 60 * 60;
    
    return secs;
  }
  


  public static String secondsToMinSecStr(int totalSeconds)
  {
    if (totalSeconds < 0) { return "-1";
    }
    int iMinutes = totalSeconds / 60;
    int iSeconds = totalSeconds % 60;
    return iMinutes + ":" + iSeconds;
  }
}
