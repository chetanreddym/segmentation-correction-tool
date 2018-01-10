package com.sun.media.jai.rmi;

import com.sun.media.jai.util.PropertyUtil;








class JaiI18N
{
  JaiI18N() {}
  
  static String packageName = "com.sun.media.jai.rmi";
  
  public static String getString(String key) {
    return PropertyUtil.getString(packageName, key);
  }
}
