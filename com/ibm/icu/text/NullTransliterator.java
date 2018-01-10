package com.ibm.icu.text;









/**
 * @deprecated
 */
public class NullTransliterator
  extends Transliterator
{
  private static final String COPYRIGHT = "© IBM Corporation 2000. All rights reserved.";
  







  static String SHORT_ID = "Null";
  static String _ID = "Any-Null";
  
  /**
   * @deprecated
   */
  public NullTransliterator()
  {
    super(_ID, null);
  }
  

  /**
   * @deprecated
   */
  protected void handleTransliterate(Replaceable text, Transliterator.Position offsets, boolean incremental)
  {
    start = limit;
  }
}
