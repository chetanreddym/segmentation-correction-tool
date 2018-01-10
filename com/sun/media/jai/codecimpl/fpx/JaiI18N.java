package com.sun.media.jai.codecimpl.fpx;

import com.sun.media.jai.codecimpl.util.PropertyUtil;








class JaiI18N
{
  JaiI18N() {}
  
  static String packageName = "com.sun.media.jai.codecimpl.fpx";
  
  public static String getString(String key) {
    return PropertyUtil.getString(packageName, key);
  }
}
