package com.sun.media.jai.codecimpl.util;

import java.text.MessageFormat;
import java.util.Locale;










class JaiI18N
{
  JaiI18N() {}
  
  static String packageName = "com.sun.media.jai.codecimpl.util";
  
  public static String getString(String key) {
    return PropertyUtil.getString(packageName, key);
  }
  
  public static String formatMsg(String key, Object[] args) {
    MessageFormat mf = new MessageFormat(getString(key));
    mf.setLocale(Locale.getDefault());
    
    return mf.format(args);
  }
}
