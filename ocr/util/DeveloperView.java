package ocr.util;

import java.io.PrintStream;






public class DeveloperView
{
  public DeveloperView() {}
  
  public static void print(String txt)
  {
    String developerModeProp = System.getProperty("DEVELOPER_MODE");
    boolean DEV_MODE = false;
    
    if (developerModeProp != null) {
      DEV_MODE = Boolean.valueOf(developerModeProp).booleanValue();
    }
    if (DEV_MODE) {
      System.out.print(txt);
    }
  }
  



  public static void println(Object txt)
  {
    String developerModeProp = System.getProperty("DEVELOPER_MODE");
    boolean DEV_MODE = false;
    
    if (developerModeProp != null) {
      DEV_MODE = Boolean.valueOf(developerModeProp).booleanValue();
    }
    if (DEV_MODE) {
      System.out.println(txt);
    }
  }
  




  public static void handleException(Exception ioe)
  {
    String developerModeProp = System.getProperty("DEVELOPER_MODE");
    boolean DEV_MODE = false;
    
    if (developerModeProp != null) {
      DEV_MODE = Boolean.valueOf(developerModeProp).booleanValue();
    }
    if (DEV_MODE) {
      ioe.printStackTrace();
    }
  }
}
