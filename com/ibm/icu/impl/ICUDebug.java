package com.ibm.icu.impl;

import com.ibm.icu.util.VersionInfo;
import java.io.PrintStream;





























public final class ICUDebug
{
  private static String params;
  private static boolean debug;
  private static boolean help;
  public static final String javaVersionString;
  public static final boolean isJDK14OrHigher;
  public static final VersionInfo javaVersion;
  
  public static VersionInfo getInstanceLenient(String s)
  {
    char[] chars = s.toCharArray();
    int r = 0;int w = 0;int count = 0;
    boolean numeric = false;
    while (r < chars.length) {
      char c = chars[(r++)];
      if ((c < '0') || (c > '9')) {
        if (numeric) {
          if (count == 3) {
            break;
          }
          
          numeric = false;
          chars[(w++)] = '.';
          count++;
        }
      } else {
        numeric = true;
        chars[(w++)] = c;
      }
    }
    while ((w > 0) && (chars[(w - 1)] == '.')) {
      w--;
    }
    
    String vs = new String(chars, 0, w);
    
    return VersionInfo.getInstance(vs);
  }
  
  static
  {
    try
    {
      params = System.getProperty("ICUDebug");
    }
    catch (SecurityException e) {}
    

    debug = params != null;
    help = (debug) && ((params.equals("")) || (params.indexOf("help") != -1));
    

    if (debug) {
      System.out.println("\nICUDebug=" + params);
    }
    

    javaVersionString = System.getProperty("java.version");
    






































    javaVersion = getInstanceLenient(javaVersionString);
    
    VersionInfo java14Version = VersionInfo.getInstance("1.4.0");
    
    isJDK14OrHigher = javaVersion.compareTo(java14Version) >= 0;
  }
  
  public static boolean enabled() {
    return debug;
  }
  
  public static boolean enabled(String arg) {
    if (debug) {
      boolean result = params.indexOf(arg) != -1;
      if (help) System.out.println("\nICUDebug.enabled(" + arg + ") = " + result);
      return result;
    }
    return false;
  }
  
  public static String value(String arg) {
    String result = "false";
    if (debug) {
      int index = params.indexOf(arg);
      if (index != -1) {
        index += arg.length();
        if ((params.length() > index) && (params.charAt(index) == '=')) {
          index++;
          int limit = params.indexOf(",", index);
          result = params.substring(index, limit == -1 ? params.length() : limit);
        }
        result = "true";
      }
      
      if (help) System.out.println("\nICUDebug.value(" + arg + ") = " + result);
    }
    return result;
  }
  
  public static void main(String[] args)
  {
    String[] tests = { "1.3.0", "1.3.0_02", "1.3.1ea", "1.4.1b43", "___41___5", "x1.4.51xx89ea.7f" };
    






    for (int i = 0; i < tests.length; i++) {
      System.out.println(tests[i] + " => " + getInstanceLenient(tests[i]));
    }
  }
  
  public ICUDebug() {}
}
