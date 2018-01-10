package com.sun.media.jai.iterator;

import com.sun.media.jai.util.PropertyUtil;








class JaiI18N
{
  JaiI18N() {}
  
  static String packageName = "com.sun.media.jai.iterator";
  
  public static String getString(String key) {
    return PropertyUtil.getString(packageName, key);
  }
}
