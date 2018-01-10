package ocr.gui.leftPanel;

import java.util.Comparator;

public class StringComparator
  implements Comparator
{
  StringComparator() {}
  
  public int compare(Object o1, Object o2)
  {
    int res = 0;
    String str1 = (String)o1;
    String str2 = (String)o2;
    
    if (str1.compareTo(str2) < 0) {
      res = -1;
    } else if (str1.compareTo(str2) > 0) {
      res = 1;
    } else if (str1.compareTo(str2) == 0) {
      res = 0;
    }
    return res;
  }
  
  public boolean equals(String str1) {
    return true;
  }
}
