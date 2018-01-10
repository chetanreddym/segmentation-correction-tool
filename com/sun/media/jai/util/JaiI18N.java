package com.sun.media.jai.util;






class JaiI18N
{
  JaiI18N() {}
  




  static String packageName = "com.sun.media.jai.util";
  
  public static String getString(String key) {
    return PropertyUtil.getString(packageName, key);
  }
}
