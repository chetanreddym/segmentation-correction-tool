package com.ibm.icu.text;













/**
 * @deprecated
 */
public class UnicodeToHexTransliterator
  extends Transliterator
{
  private static final String COPYRIGHT = "© IBM Corporation 1999. All rights reserved.";
  











  static final String _ID = "Any-Hex";
  











  private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  




  private static final char ZERO = '0';
  



  private static final char POUND = '#';
  



  private static final char BACKSLASH = '\\';
  



  private String pattern;
  



  private String prefix;
  



  private String suffix;
  



  private int minDigits;
  



  private boolean uppercase;
  




  /**
   * @deprecated
   */
  public UnicodeToHexTransliterator(String pattern, boolean uppercase, UnicodeFilter filter)
  {
    super("Any-Hex", filter);
    this.uppercase = uppercase;
    applyPattern(pattern);
  }
  


  /**
   * @deprecated
   */
  public UnicodeToHexTransliterator(String pattern)
  {
    this(pattern, true, null);
  }
  

  /**
   * @deprecated
   */
  public UnicodeToHexTransliterator()
  {
    super("Any-Hex", null);
    pattern = "\\\\u0000";
    prefix = "\\u";
    suffix = "";
    minDigits = 4;
    uppercase = true;
  }
  























  /**
   * @deprecated
   */
  public void applyPattern(String thePattern)
  {
    StringBuffer prefixBuf = null;
    StringBuffer suffixBuf = null;
    int minDigits = 0;
    int maxDigits = 0;
    






    int mode = 0;
    
    for (int i = 0; i < thePattern.length(); i++) {
      char c = thePattern.charAt(i);
      boolean isLiteral = false;
      if (c == '\\') {
        if (i + 1 < thePattern.length()) {
          isLiteral = true;
          c = thePattern.charAt(++i);
        }
        else {
          throw new IllegalArgumentException("Trailing '\\'");
        }
      }
      
      if (!isLiteral) {
        switch (c)
        {

        case '#': 
          if (mode == 0) {
            mode++;
          } else if (mode != 1)
          {
            throw new IllegalArgumentException("Unquoted '#'");
          }
          maxDigits++;
          break;
        
        case '0': 
          if (mode < 2) {
            mode = 2;
          } else if (mode != 2)
          {
            throw new IllegalArgumentException("Unquoted '0'");
          }
          minDigits++;
          maxDigits++;
          break;
        default: 
          isLiteral = true;
        }
        
      }
      
      if (isLiteral) {
        if (mode == 0) {
          if (prefixBuf == null) {
            prefixBuf = new StringBuffer();
          }
          prefixBuf.append(c);
        }
        else
        {
          mode = 3;
          if (suffixBuf == null) {
            suffixBuf = new StringBuffer();
          }
          suffixBuf.append(c);
        }
      }
    }
    
    if ((minDigits < 1) || (maxDigits > 4))
    {
      throw new IllegalArgumentException("Invalid min/max digit count");
    }
    
    pattern = thePattern;
    prefix = (prefixBuf == null ? "" : prefixBuf.toString());
    suffix = (suffixBuf == null ? "" : suffixBuf.toString());
    this.minDigits = minDigits;
  }
  
  /**
   * @deprecated
   */
  public String toPattern()
  {
    return pattern;
  }
  

  /**
   * @deprecated
   */
  public String getPrefix()
  {
    return prefix;
  }
  





  /**
   * @deprecated
   */
  public void setPrefix(String prefix)
  {
    this.prefix = prefix;
  }
  
  /**
   * @deprecated
   */
  public boolean isUppercase()
  {
    return uppercase;
  }
  






  /**
   * @deprecated
   */
  public void setUppercase(boolean outputUppercase)
  {
    uppercase = outputUppercase;
  }
  






  /**
   * @deprecated
   */
  protected void handleTransliterate(Replaceable text, Transliterator.Position offsets, boolean incremental)
  {
    int cursor = start;
    int limit = limit;
    
    StringBuffer hex = new StringBuffer(prefix);
    int prefixLen = prefix.length();
    
    while (cursor < limit) {
      char c = text.charAt(cursor);
      
      hex.setLength(prefixLen);
      boolean showRest = false;
      for (int i = 3; i >= 0; i--) {
        int d = c >> i * 4 & 0xF;
        if ((showRest) || (d != 0) || (minDigits > i)) {
          hex.append(HEX_DIGITS[d]);
          showRest = true;
        }
      }
      hex.append(suffix);
      
      text.replace(cursor, cursor + 1, hex.toString());
      int len = hex.length();
      cursor += len;
      len--;
      limit += len;
    }
    
    contextLimit += limit - limit;
    limit = limit;
    start = cursor;
  }
}
