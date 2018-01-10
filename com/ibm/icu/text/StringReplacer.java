package com.ibm.icu.text;

import com.ibm.icu.impl.Utility;























































class StringReplacer
  implements UnicodeReplacer
{
  private String output;
  private int cursorPos;
  private boolean hasCursor;
  private boolean isComplex;
  private final RuleBasedTransliterator.Data data;
  
  public StringReplacer(String theOutput, int theCursorPos, RuleBasedTransliterator.Data theData)
  {
    output = theOutput;
    cursorPos = theCursorPos;
    hasCursor = true;
    data = theData;
    isComplex = true;
  }
  









  public StringReplacer(String theOutput, RuleBasedTransliterator.Data theData)
  {
    output = theOutput;
    cursorPos = 0;
    hasCursor = false;
    data = theData;
    isComplex = true;
  }
  



















  public int replace(Replaceable text, int start, int limit, int[] cursor)
  {
    int newStart = 0;
    


    int outLen;
    

    if (!isComplex) {
      text.replace(start, limit, output);
      outLen = output.length();
      

      newStart = cursorPos;




    }
    else
    {



      StringBuffer buf = new StringBuffer();
      
      isComplex = false;
      









      int tempStart = text.length();
      int destStart = tempStart;
      if (start > 0) {
        int len = UTF16.getCharCount(text.char32At(start - 1));
        text.copy(start - len, start, tempStart);
        destStart += len;
      } else {
        text.replace(tempStart, tempStart, "￿");
        destStart++;
      }
      int destLimit = destStart;
      int tempExtra = 0;
      
      for (int oOutput = 0; oOutput < output.length();) {
        if (oOutput == cursorPos)
        {
          newStart = destLimit - destStart;
        }
        int c = UTF16.charAt(output, oOutput);
        




        int nextIndex = oOutput + UTF16.getCharCount(c);
        if (nextIndex == output.length()) {
          tempExtra = UTF16.getCharCount(text.char32At(limit));
          text.copy(limit, limit + tempExtra, destLimit);
        }
        
        UnicodeReplacer r = data.lookupReplacer(c);
        if (r == null)
        {
          UTF16.append(buf, c);
        } else {
          isComplex = true;
          

          if (buf.length() > 0) {
            text.replace(destLimit, destLimit, buf.toString());
            destLimit += buf.length();
            buf.setLength(0);
          }
          

          int len = r.replace(text, destLimit, destLimit, cursor);
          destLimit += len;
        }
        oOutput = nextIndex;
      }
      
      if (buf.length() > 0) {
        text.replace(destLimit, destLimit, buf.toString());
        destLimit += buf.length();
      }
      if (oOutput == cursorPos)
      {
        newStart = destLimit - destStart;
      }
      
      outLen = destLimit - destStart;
      

      text.copy(destStart, destLimit, start);
      text.replace(tempStart + outLen, destLimit + tempExtra + outLen, "");
      

      text.replace(start + outLen, limit + outLen, "");
    }
    
    if (hasCursor)
    {



      if (cursorPos < 0) {
        newStart = start;
        int n = cursorPos;
        
        while ((n < 0) && (newStart > 0)) {
          newStart -= UTF16.getCharCount(text.char32At(newStart - 1));
          n++;
        }
        newStart += n;
      } else if (cursorPos > output.length()) {
        newStart = start + outLen;
        int n = cursorPos - output.length();
        
        while ((n > 0) && (newStart < text.length())) {
          newStart += UTF16.getCharCount(text.char32At(newStart));
          n--;
        }
        newStart += n;
      }
      else
      {
        newStart += start;
      }
      
      cursor[0] = newStart;
    }
    
    return outLen;
  }
  


  public String toReplacerPattern(boolean escapeUnprintable)
  {
    StringBuffer rule = new StringBuffer();
    StringBuffer quoteBuf = new StringBuffer();
    
    int cursor = cursorPos;
    

    if ((hasCursor) && (cursor < 0)) {
      while (cursor++ < 0) {
        Utility.appendToRule(rule, 64, true, escapeUnprintable, quoteBuf);
      }
    }
    

    for (int i = 0; i < output.length(); i++) {
      if ((hasCursor) && (i == cursor)) {
        Utility.appendToRule(rule, 124, true, escapeUnprintable, quoteBuf);
      }
      char c = output.charAt(i);
      
      UnicodeReplacer r = data.lookupReplacer(c);
      if (r == null) {
        Utility.appendToRule(rule, c, false, escapeUnprintable, quoteBuf);
      } else {
        StringBuffer buf = new StringBuffer(" ");
        buf.append(r.toReplacerPattern(escapeUnprintable));
        buf.append(' ');
        Utility.appendToRule(rule, buf.toString(), true, escapeUnprintable, quoteBuf);
      }
    }
    




    if ((hasCursor) && (cursor > output.length())) {
      cursor -= output.length();
      while (cursor-- > 0) {
        Utility.appendToRule(rule, 64, true, escapeUnprintable, quoteBuf);
      }
      Utility.appendToRule(rule, 124, true, escapeUnprintable, quoteBuf);
    }
    
    Utility.appendToRule(rule, -1, true, escapeUnprintable, quoteBuf);
    

    return rule.toString();
  }
  


  public void addReplacementSetTo(UnicodeSet toUnionTo)
  {
    int ch;
    

    for (int i = 0; i < output.length(); i += UTF16.getCharCount(ch)) {
      ch = UTF16.charAt(output, i);
      UnicodeReplacer r = data.lookupReplacer(ch);
      if (r == null) {
        toUnionTo.add(ch);
      } else {
        r.addReplacementSetTo(toUnionTo);
      }
    }
  }
}
