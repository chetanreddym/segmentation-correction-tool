package com.ibm.icu.text;

import com.ibm.icu.impl.Utility;




























































class StringMatcher
  implements UnicodeMatcher, UnicodeReplacer
{
  private String pattern;
  private int matchStart;
  private int matchLimit;
  private int segmentNumber;
  private final RuleBasedTransliterator.Data data;
  
  public StringMatcher(String theString, int segmentNum, RuleBasedTransliterator.Data theData)
  {
    data = theData;
    pattern = theString;
    matchStart = (this.matchLimit = -1);
    segmentNumber = segmentNum;
  }
  
















  public StringMatcher(String theString, int start, int limit, int segmentNum, RuleBasedTransliterator.Data theData)
  {
    this(theString.substring(start, limit), segmentNum, theData);
  }
  










  public int matches(Replaceable text, int[] offset, int limit, boolean incremental)
  {
    int[] cursor = { offset[0] };
    int i; if (limit < cursor[0])
    {
      for (i = pattern.length() - 1; i >= 0; i--) {
        char keyChar = pattern.charAt(i);
        UnicodeMatcher subm = data.lookupMatcher(keyChar);
        if (subm == null) {
          if ((cursor[0] > limit) && (keyChar == text.charAt(cursor[0])))
          {
            cursor[0] -= 1;
          } else {
            return 0;
          }
        } else {
          int m = subm.matches(text, cursor, limit, incremental);
          
          if (m != 2) {
            return m;
          }
        }
      }
      


      if (matchStart < 0) {
        matchStart = (cursor[0] + 1);
        matchLimit = (offset[0] + 1);
      }
    } else {
      for (i = 0; i < pattern.length(); i++) {
        if ((incremental) && (cursor[0] == limit))
        {

          return 1;
        }
        char keyChar = pattern.charAt(i);
        UnicodeMatcher subm = data.lookupMatcher(keyChar);
        if (subm == null)
        {


          if ((cursor[0] < limit) && (keyChar == text.charAt(cursor[0])))
          {
            cursor[0] += 1;
          } else {
            return 0;
          }
        } else {
          int m = subm.matches(text, cursor, limit, incremental);
          
          if (m != 2) {
            return m;
          }
        }
      }
      
      matchStart = offset[0];
      matchLimit = cursor[0];
    }
    
    offset[0] = cursor[0];
    return 2;
  }
  


  public String toPattern(boolean escapeUnprintable)
  {
    StringBuffer result = new StringBuffer();
    StringBuffer quoteBuf = new StringBuffer();
    if (segmentNumber > 0) {
      result.append('(');
    }
    for (int i = 0; i < pattern.length(); i++) {
      char keyChar = pattern.charAt(i);
      UnicodeMatcher m = data.lookupMatcher(keyChar);
      if (m == null) {
        Utility.appendToRule(result, keyChar, false, escapeUnprintable, quoteBuf);
      } else {
        Utility.appendToRule(result, m.toPattern(escapeUnprintable), true, escapeUnprintable, quoteBuf);
      }
    }
    
    if (segmentNumber > 0) {
      result.append(')');
    }
    
    Utility.appendToRule(result, -1, true, escapeUnprintable, quoteBuf);
    
    return result.toString();
  }
  


  public boolean matchesIndexValue(int v)
  {
    if (pattern.length() == 0) {
      return true;
    }
    int c = UTF16.charAt(pattern, 0);
    UnicodeMatcher m = data.lookupMatcher(c);
    return m == null ? false : (c & 0xFF) == v ? true : m.matchesIndexValue(v);
  }
  



  public void addMatchSetTo(UnicodeSet toUnionTo)
  {
    int ch;
    

    for (int i = 0; i < pattern.length(); i += UTF16.getCharCount(ch)) {
      ch = UTF16.charAt(pattern, i);
      UnicodeMatcher matcher = data.lookupMatcher(ch);
      if (matcher == null) {
        toUnionTo.add(ch);
      } else {
        matcher.addMatchSetTo(toUnionTo);
      }
    }
  }
  






  public int replace(Replaceable text, int start, int limit, int[] cursor)
  {
    int outLen = 0;
    

    int dest = limit;
    

    if ((matchStart >= 0) && 
      (matchStart != matchLimit)) {
      text.copy(matchStart, matchLimit, dest);
      outLen = matchLimit - matchStart;
    }
    

    text.replace(start, limit, "");
    
    return outLen;
  }
  



  public String toReplacerPattern(boolean escapeUnprintable)
  {
    StringBuffer rule = new StringBuffer("$");
    Utility.appendNumber(rule, segmentNumber, 10, 1);
    return rule.toString();
  }
  



  public void resetMatch()
  {
    matchStart = (this.matchLimit = -1);
  }
  
  public void addReplacementSetTo(UnicodeSet toUnionTo) {}
}
