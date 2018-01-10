package com.ibm.icu.impl.data;

import java.util.ListResourceBundle;















public class BuddhistCalendarSymbols
  extends ListResourceBundle
{
  private static String copyright = "Copyright © 1998 IBM Corp. All Rights Reserved.";
  
  static final Object[][] fContents = { { "Eras", { "BE" } } };
  

  public BuddhistCalendarSymbols() {}
  
  public synchronized Object[][] getContents()
  {
    return fContents;
  }
}
