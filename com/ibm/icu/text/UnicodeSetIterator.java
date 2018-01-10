package com.ibm.icu.text;

import java.util.Iterator;
import java.util.TreeSet;


















































public class UnicodeSetIterator
{
  public static int IS_STRING = -1;
  





  public int codepoint;
  




  public int codepointEnd;
  




  public String string;
  




  private UnicodeSet set;
  





  public UnicodeSetIterator(UnicodeSet set)
  {
    reset(set);
  }
  





  public UnicodeSetIterator()
  {
    reset(new UnicodeSet());
  }
  


















  public boolean next()
  {
    if (nextElement <= endElement) {
      codepoint = (this.codepointEnd = nextElement++);
      return true;
    }
    if (range < endRange) {
      loadRange(++range);
      codepoint = (this.codepointEnd = nextElement++);
      return true;
    }
    


    if (stringIterator == null) return false;
    codepoint = IS_STRING;
    string = ((String)stringIterator.next());
    if (!stringIterator.hasNext()) stringIterator = null;
    return true;
  }
  



















  public boolean nextRange()
  {
    if (nextElement <= endElement) {
      codepointEnd = endElement;
      codepoint = nextElement;
      nextElement = (endElement + 1);
      return true;
    }
    if (range < endRange) {
      loadRange(++range);
      codepointEnd = endElement;
      codepoint = nextElement;
      nextElement = (endElement + 1);
      return true;
    }
    


    if (stringIterator == null) return false;
    codepoint = IS_STRING;
    string = ((String)stringIterator.next());
    if (!stringIterator.hasNext()) stringIterator = null;
    return true;
  }
  






  public void reset(UnicodeSet set)
  {
    this.set = set;
    reset();
  }
  



  public void reset()
  {
    endRange = (set.getRangeCount() - 1);
    range = 0;
    endElement = -1;
    nextElement = 0;
    if (endRange >= 0) {
      loadRange(range);
    }
    stringIterator = null;
    if (set.strings != null) {
      stringIterator = set.strings.iterator();
      if (!stringIterator.hasNext()) { stringIterator = null;
      }
    }
  }
  


  private int endRange = 0;
  private int range = 0;
  

  protected int endElement;
  

  protected int nextElement;
  

  private Iterator stringIterator = null;
  






  protected void loadRange(int range)
  {
    nextElement = set.getRangeStart(range);
    endElement = set.getRangeEnd(range);
  }
}
