package com.ibm.icu.util;

import com.ibm.icu.lang.UCharacter;




















public class CaseInsensitiveString
{
  private String string;
  private int hash = 0;
  



  public CaseInsensitiveString(String s)
  {
    string = s;
  }
  



  public String getString()
  {
    return string;
  }
  


  public boolean equals(Object o)
  {
    try
    {
      return string.equalsIgnoreCase(string);
    } catch (ClassCastException e) {
      try {
        return string.equalsIgnoreCase((String)o);
      } catch (ClassCastException e2) {} }
    return false;
  }
  






  public int hashCode()
  {
    if (hash == 0) {
      hash = UCharacter.foldCase(string, true).hashCode();
    }
    return hash;
  }
}
