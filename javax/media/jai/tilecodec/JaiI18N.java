package javax.media.jai.tilecodec;

import com.sun.media.jai.util.PropertyUtil;









class JaiI18N
{
  JaiI18N() {}
  
  static String packageName = "javax.media.jai.tilecodec";
  
  public static String getString(String key) {
    return PropertyUtil.getString(packageName, key);
  }
}
