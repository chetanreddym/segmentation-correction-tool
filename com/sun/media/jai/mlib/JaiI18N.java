package com.sun.media.jai.mlib;

import com.sun.media.jai.util.PropertyUtil;








class JaiI18N
{
  JaiI18N() {}
  
  static String packageName = "com.sun.media.jai.mlib";
  
  public static String getString(String key) {
    return PropertyUtil.getString(packageName, key);
  }
}
