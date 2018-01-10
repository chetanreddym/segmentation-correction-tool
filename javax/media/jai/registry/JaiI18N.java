package javax.media.jai.registry;

import com.sun.media.jai.util.PropertyUtil;









class JaiI18N
{
  JaiI18N() {}
  
  static String packageName = "javax.media.jai.registry";
  
  public static String getString(String key) {
    return PropertyUtil.getString(packageName, key);
  }
}
