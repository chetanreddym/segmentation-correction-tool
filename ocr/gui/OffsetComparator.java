package ocr.gui;

import java.util.Comparator;

public class OffsetComparator implements Comparator<Object>
{
  public OffsetComparator() {}
  
  public int compare(Object s1, Object s2) {
    return new Integer((String)s1).compareTo(new Integer((String)s2));
  }
}
