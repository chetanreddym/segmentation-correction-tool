package filesystem;

import java.net.UnknownHostException;

public class OsUtil
{
  public OsUtil() {}
  
  public static String getComputerName()
  {
    try {
      return java.net.InetAddress.getLocalHost().getHostName();
    }
    catch (UnknownHostException e) {
      e.printStackTrace();
    }
    
    return "Unknown";
  }
}
