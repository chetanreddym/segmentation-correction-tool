package javax.media.jai.widget;

import com.sun.media.jai.util.PropertyUtil;















/**
 * @deprecated
 */
class JaiI18N
{
  JaiI18N() {}
  
  static String packageName = "javax.media.jai.widget";
  
  public static String getString(String key) {
    return PropertyUtil.getString(packageName, key);
  }
}
