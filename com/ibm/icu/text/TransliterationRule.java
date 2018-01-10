package com.ibm.icu.text;

import com.ibm.icu.impl.Utility;



















































































































































class TransliterationRule
{
  private StringMatcher anteContext;
  private StringMatcher key;
  private StringMatcher postContext;
  private UnicodeReplacer output;
  private String pattern;
  UnicodeMatcher[] segments;
  private int anteContextLength;
  private int keyLength;
  byte flags;
  static final int ANCHOR_START = 1;
  static final int ANCHOR_END = 2;
  private final RuleBasedTransliterator.Data data;
  private static final String COPYRIGHT = "© IBM Corporation 1999-2001. All rights reserved.";
  
  public TransliterationRule(String input, int anteContextPos, int postContextPos, String output, int cursorPos, int cursorOffset, UnicodeMatcher[] segs, boolean anchorStart, boolean anchorEnd, RuleBasedTransliterator.Data theData)
  {
    data = theData;
    

    if (anteContextPos < 0) {
      anteContextLength = 0;
    } else {
      if (anteContextPos > input.length()) {
        throw new IllegalArgumentException("Invalid ante context");
      }
      anteContextLength = anteContextPos;
    }
    if (postContextPos < 0) {
      keyLength = (input.length() - anteContextLength);
    } else {
      if ((postContextPos < anteContextLength) || (postContextPos > input.length()))
      {
        throw new IllegalArgumentException("Invalid post context");
      }
      keyLength = (postContextPos - anteContextLength);
    }
    if (cursorPos < 0) {
      cursorPos = output.length();
    } else if (cursorPos > output.length()) {
      throw new IllegalArgumentException("Invalid cursor position");
    }
    




    segments = segs;
    
    pattern = input;
    flags = 0;
    if (anchorStart) {
      flags = ((byte)(flags | 0x1));
    }
    if (anchorEnd) {
      flags = ((byte)(flags | 0x2));
    }
    
    anteContext = null;
    if (anteContextLength > 0) {
      anteContext = new StringMatcher(pattern.substring(0, anteContextLength), 0, data);
    }
    

    key = null;
    if (keyLength > 0) {
      key = new StringMatcher(pattern.substring(anteContextLength, anteContextLength + keyLength), 0, data);
    }
    

    int postContextLength = pattern.length() - keyLength - anteContextLength;
    postContext = null;
    if (postContextLength > 0) {
      postContext = new StringMatcher(pattern.substring(anteContextLength + keyLength), 0, data);
    }
    

    this.output = new StringReplacer(output, cursorPos + cursorOffset, data);
  }
  




  public int getAnteContextLength()
  {
    return anteContextLength + ((flags & 0x1) != 0 ? 1 : 0);
  }
  





  final int getIndexValue()
  {
    if (anteContextLength == pattern.length())
    {

      return -1;
    }
    int c = UTF16.charAt(pattern, anteContextLength);
    return data.lookupMatcher(c) == null ? c & 0xFF : -1;
  }
  











  final boolean matchesIndexValue(int v)
  {
    UnicodeMatcher m = key != null ? key : postContext;
    return m != null ? m.matchesIndexValue(v) : true;
  }
  











































  public boolean masks(TransliterationRule r2)
  {
    int len = pattern.length();
    int left = anteContextLength;
    int left2 = anteContextLength;
    int right = pattern.length() - left;
    int right2 = pattern.length() - left2;
    




    if ((left == left2) && (right == right2) && (keyLength <= keyLength) && (pattern.regionMatches(0, pattern, 0, len)))
    {


      return (flags == flags) || (((flags & 0x1) == 0) && ((flags & 0x2) == 0)) || (((flags & 0x1) != 0) && ((flags & 0x2) != 0));
    }
    


    return (left <= left2) && ((right < right2) || ((right == right2) && (keyLength <= keyLength))) && (pattern.regionMatches(left2 - left, pattern, 0, len));
  }
  


  static final int posBefore(Replaceable str, int pos)
  {
    return pos > 0 ? pos - UTF16.getCharCount(str.char32At(pos - 1)) : pos - 1;
  }
  

  static final int posAfter(Replaceable str, int pos)
  {
    return (pos >= 0) && (pos < str.length()) ? pos + UTF16.getCharCount(str.char32At(pos)) : pos + 1;
  }
  

































  public int matchAndReplace(Replaceable text, Transliterator.Position pos, boolean incremental)
  {
    if (segments != null) {
      for (int i = 0; i < segments.length; i++) {
        ((StringMatcher)segments[i]).resetMatch();
      }
    }
    

    int[] intRef = new int[1];
    













    int anteLimit = posBefore(text, contextStart);
    



    intRef[0] = posBefore(text, start);
    int match;
    if (anteContext != null) {
      match = anteContext.matches(text, intRef, anteLimit, false);
      if (match != 2) {
        return 0;
      }
    }
    
    int oText = intRef[0];
    
    int minOText = posAfter(text, oText);
    


    if (((flags & 0x1) != 0) && (oText != anteLimit)) {
      return 0;
    }
    


    intRef[0] = start;
    
    if (key != null) {
      match = key.matches(text, intRef, limit, incremental);
      if (match != 2) {
        return match;
      }
    }
    
    int keyLimit = intRef[0];
    
    if (postContext != null) {
      if ((incremental) && (keyLimit == limit))
      {



        return 1;
      }
      
      match = postContext.matches(text, intRef, contextLimit, incremental);
      if (match != 2) {
        return match;
      }
    }
    
    oText = intRef[0];
    


    if ((flags & 0x2) != 0) {
      if (oText != contextLimit) {
        return 0;
      }
      if (incremental) {
        return 1;
      }
    }
    





    int newLength = output.replace(text, start, keyLimit, intRef);
    int lenDelta = newLength - (keyLimit - start);
    int newStart = intRef[0];
    
    oText += lenDelta;
    limit += lenDelta;
    contextLimit += lenDelta;
    
    start = Math.max(minOText, Math.min(Math.min(oText, limit), newStart));
    return 2;
  }
  





  public String toRule(boolean escapeUnprintable)
  {
    StringBuffer rule = new StringBuffer();
    



    StringBuffer quoteBuf = new StringBuffer();
    


    boolean emitBraces = (anteContext != null) || (postContext != null);
    


    if ((flags & 0x1) != 0) {
      rule.append('^');
    }
    

    Utility.appendToRule(rule, anteContext, escapeUnprintable, quoteBuf);
    
    if (emitBraces) {
      Utility.appendToRule(rule, 123, true, escapeUnprintable, quoteBuf);
    }
    
    Utility.appendToRule(rule, key, escapeUnprintable, quoteBuf);
    
    if (emitBraces) {
      Utility.appendToRule(rule, 125, true, escapeUnprintable, quoteBuf);
    }
    
    Utility.appendToRule(rule, postContext, escapeUnprintable, quoteBuf);
    

    if ((flags & 0x2) != 0) {
      rule.append('$');
    }
    
    Utility.appendToRule(rule, " > ", true, escapeUnprintable, quoteBuf);
    


    Utility.appendToRule(rule, output.toReplacerPattern(escapeUnprintable), true, escapeUnprintable, quoteBuf);
    

    Utility.appendToRule(rule, 59, true, escapeUnprintable, quoteBuf);
    
    return rule.toString();
  }
  



  public String toString()
  {
    return getClass().getName() + '{' + toRule(true) + '}';
  }
  



  void addSourceSetTo(UnicodeSet toUnionTo)
  {
    int limit = anteContextLength + keyLength;
    for (int i = anteContextLength; i < limit;) {
      int ch = UTF16.charAt(pattern, i);
      i += UTF16.getCharCount(ch);
      UnicodeMatcher matcher = data.lookupMatcher(ch);
      if (matcher == null) {
        toUnionTo.add(ch);
      } else {
        matcher.addMatchSetTo(toUnionTo);
      }
    }
  }
  



  void addTargetSetTo(UnicodeSet toUnionTo)
  {
    output.addReplacementSetTo(toUnionTo);
  }
}
