package com.ibm.icu.text;

import com.ibm.icu.impl.Utility;

















































/**
 * @deprecated
 */
public class HexToUnicodeTransliterator
  extends Transliterator
{
  private static final String COPYRIGHT = "© IBM Corporation 1999. All rights reserved.";
  static final String _ID = "Hex-Any";
  private static final String DEFAULT_PATTERN = "\\\\u0000;\\\\U0000;u+0000;U+0000";
  private static final char SEMICOLON = ';';
  private static final char ZERO = '0';
  private static final char POUND = '#';
  private static final char BACKSLASH = '\\';
  private String pattern;
  private char[] affixes;
  private int affixCount;
  
  /**
   * @deprecated
   */
  public HexToUnicodeTransliterator()
  {
    super("Hex-Any", null);
    applyPattern("\\\\u0000;\\\\U0000;u+0000;U+0000");
  }
  
  /**
   * @deprecated
   */
  public HexToUnicodeTransliterator(String thePattern)
  {
    this(thePattern, null);
  }
  

  /**
   * @deprecated
   */
  public HexToUnicodeTransliterator(String thePattern, UnicodeFilter theFilter)
  {
    super("Hex-Any", theFilter);
    applyPattern(thePattern);
  }
  



































  /**
   * @deprecated
   */
  public void applyPattern(String pattern)
  {
    StringBuffer affixes = new StringBuffer();
    affixCount = 0;
    






    int mode = 0;
    
    int prefixLen = 0;int suffixLen = 0;int minDigits = 0;int maxDigits = 0;
    int start = 0;
    





    char c = '\000';
    boolean isLiteral = false;
    for (int i = 0; i <= pattern.length(); i++)
    {
      if (i == pattern.length())
      {
        if ((i <= 0) || ((c == ';') && (!isLiteral))) break;
        c = ';';
        isLiteral = false;

      }
      else
      {
        c = pattern.charAt(i);
        isLiteral = false;
      }
      
      if (c == '\\') {
        if (i + 1 < pattern.length()) {
          isLiteral = true;
          c = pattern.charAt(++i);
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
        case ';': 
          if ((minDigits < 1) || (maxDigits > 4) || (prefixLen > 65535) || (suffixLen > 65535))
          {


            throw new IllegalArgumentException("Suffix or prefix too long");
          }
          


          if (start == affixes.length()) {
            affixes.append("AAAA");
          }
          
          affixes.setCharAt(start++, (char)prefixLen);
          affixes.setCharAt(start++, (char)suffixLen);
          affixes.setCharAt(start++, (char)minDigits);
          affixes.setCharAt(start, (char)maxDigits);
          start = affixes.length();
          affixCount += 1;
          prefixLen = suffixLen = minDigits = maxDigits = mode = 0;
          break;
        default: 
          isLiteral = true;
        }
        
      }
      
      if (isLiteral) {
        if (start == affixes.length())
        {


          affixes.append("AAAA");
        }
        affixes.append(c);
        if (mode == 0) {
          prefixLen++;
        }
        else
        {
          mode = 3;
          suffixLen++;
        }
      }
    }
    


    this.pattern = pattern;
    int len = affixes.length();
    this.affixes = new char[len];
    Utility.getChars(affixes, 0, len, this.affixes, 0);
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
  protected void handleTransliterate(Replaceable text, Transliterator.Position offsets, boolean isIncremental)
  {
    int cursor = start;
    int limit = limit;
    


    while (cursor < limit)
    {




      int j = 0; for (int ipat = 0; j < affixCount; j++)
      {

        int prefixLen = affixes[(ipat++)];
        int suffixLen = affixes[(ipat++)];
        int minDigits = affixes[(ipat++)];
        int maxDigits = affixes[(ipat++)];
        


        int curs = cursor;
        boolean match = true;
        
        for (int i = 0; i < prefixLen; i++) {
          if ((curs >= limit) && 
            (i > 0))
          {



            if (isIncremental) {
              break label413;
            }
            match = false;
            break;
          }
          
          char c = text.charAt(curs++);
          if (c != affixes[(ipat + i)]) {
            match = false;
            break;
          }
        }
        
        if (match) {
          char u = '\000';
          int digitCount = 0;
          for (;;) {
            if (curs >= limit)
            {
              if ((curs > cursor) && (isIncremental)) {
                break label413;
              }
            }
            else {
              int digit = Character.digit(text.charAt(curs), 16);
              if (digit >= 0)
              {

                curs++;
                u = (char)(u << '\004');
                u = (char)(u | (char)digit);
                digitCount++; if (digitCount == maxDigits)
                  break;
              }
            }
          }
          match = digitCount >= minDigits;
          
          if (match) {
            for (i = 0; i < suffixLen; i++) {
              if (curs >= limit)
              {
                if ((curs > cursor) && (isIncremental)) {
                  break label413;
                }
                match = false;
                break;
              }
              char c = text.charAt(curs++);
              if (c != affixes[(ipat + prefixLen + i)]) {
                match = false;
                break;
              }
            }
            
            if (match)
            {
              text.replace(cursor, curs, String.valueOf(u));
              limit -= curs - cursor - 1;
              



              break;
            }
          }
        }
        
        ipat += prefixLen + suffixLen;
      }
      
      cursor++;
    }
    label413:
    contextLimit += limit - limit;
    limit = limit;
    start = cursor;
  }
}
