package com.ibm.icu.lang;

import com.ibm.icu.text.UCharacterIterator;


























































public final class UScriptRun
{
  private static final String copyrightNotice = "Copyright ©1999-2002 IBM Corp.  All rights reserved.";
  
  public UScriptRun()
  {
    char[] nullChars = null;
    
    reset(nullChars, 0, 0);
  }
  








  public UScriptRun(String text)
  {
    reset(text);
  }
  










  public UScriptRun(String text, int start, int count)
  {
    reset(text, start, count);
  }
  








  public UScriptRun(char[] chars)
  {
    reset(chars);
  }
  










  public UScriptRun(char[] chars, int start, int count)
  {
    reset(chars, start, count);
  }
  






  public final void reset()
  {
    scriptStart = textStart;
    scriptLimit = textStart;
    scriptCode = -1;
    parenSP = -1;
    
    text.setToStart();
  }
  











  public final void reset(int start, int count)
    throws IllegalArgumentException
  {
    int len = 0;
    
    if (text != null) {
      len = text.getLength();
    }
    
    if ((start < 0) || (count < 0) || (start > len - count)) {
      throw new IllegalArgumentException();
    }
    
    textStart = start;
    textLimit = (start + count);
    
    reset();
  }
  











  public final void reset(char[] chars, int start, int count)
  {
    if (chars == null) {
      chars = emptyCharArray;
    }
    
    text = UCharacterIterator.getInstance(chars, start, start + count);
    
    reset(start, count);
  }
  








  public final void reset(char[] chars)
  {
    int length = 0;
    
    if (chars != null) {
      length = chars.length;
    }
    
    reset(chars, 0, length);
  }
  











  public final void reset(String text, int start, int count)
  {
    char[] chars = null;
    
    if (text != null) {
      chars = text.toCharArray();
    }
    
    reset(chars, start, count);
  }
  








  public final void reset(String text)
  {
    int length = 0;
    
    if (text != null) {
      length = text.length();
    }
    
    reset(text, 0, length);
  }
  









  public final int getScriptStart()
  {
    return scriptStart;
  }
  







  public final int getScriptLimit()
  {
    return scriptLimit;
  }
  








  public final int getScriptCode()
  {
    return scriptCode;
  }
  








  public final boolean next()
  {
    int startSP = parenSP;
    

    if (scriptLimit >= textLimit) {
      return false;
    }
    
    scriptCode = 0;
    scriptStart = scriptLimit;
    
    int ch;
    
    while ((ch = text.nextCodePoint()) != -1) { int i;
      int sc = UScript.getScript(i);
      int pairIndex = getPairIndex(i);
      






      if (pairIndex >= 0) {
        if ((pairIndex & 0x1) == 0)
        {







          if (++parenSP >= PAREN_STACK_DEPTH) {
            parenSP = 0;
          }
          
          parenStack[parenSP] = new ParenStackEntry(pairIndex, scriptCode);
        } else if (parenSP >= 0) {
          int pi = pairIndex & 0xFFFFFFFE;
          
          while ((parenSP >= 0) && (parenStackparenSP].pairIndex != pi)) {
            parenSP -= 1;
          }
          
          if (parenSP < startSP) {
            startSP = parenSP;
          }
          
          if (parenSP >= 0) {
            sc = parenStackparenSP].scriptCode;
          }
        }
      }
      
      if (sameScript(scriptCode, sc)) {
        if ((scriptCode <= 1) && (sc > 1)) {
          scriptCode = sc;
          


          while (startSP < parenSP) {
            parenStackscriptCode = scriptCode;
          }
        }
        


        if ((pairIndex >= 0) && ((pairIndex & 0x1) != 0) && (parenSP >= 0)) {
          parenSP -= 1;
          startSP--;
        }
        
      }
      else
      {
        text.previousCodePoint();
        break;
      }
    }
    
    scriptLimit = (textStart + text.getIndex());
    return true;
  }
  









  private static boolean sameScript(int scriptOne, int scriptTwo)
  {
    return (scriptOne <= 1) || (scriptTwo <= 1) || (scriptOne == scriptTwo);
  }
  

  private static final class ParenStackEntry
  {
    int pairIndex;
    
    int scriptCode;
    

    public ParenStackEntry(int thePairIndex, int theScriptCode)
    {
      pairIndex = thePairIndex;
      scriptCode = theScriptCode;
    }
  }
  
  private char[] emptyCharArray = new char[0];
  
  private UCharacterIterator text;
  
  private int textStart;
  
  private int textLimit;
  
  private int scriptStart;
  private int scriptLimit;
  private int scriptCode;
  private static int PAREN_STACK_DEPTH = 128;
  private static ParenStackEntry[] parenStack = new ParenStackEntry[PAREN_STACK_DEPTH];
  



  private int parenSP;
  



  private static final byte highBit(int n)
  {
    if (n <= 0) {
      return -32;
    }
    
    byte bit = 0;
    
    if (n >= 65536) {
      n >>= 16;
      bit = (byte)(bit + 16);
    }
    
    if (n >= 256) {
      n >>= 8;
      bit = (byte)(bit + 8);
    }
    
    if (n >= 16) {
      n >>= 4;
      bit = (byte)(bit + 4);
    }
    
    if (n >= 4) {
      n >>= 2;
      bit = (byte)(bit + 2);
    }
    
    if (n >= 2) {
      n >>= 1;
      bit = (byte)(bit + 1);
    }
    
    return bit;
  }
  






  private static int getPairIndex(int ch)
  {
    int probe = pairedCharPower;
    int index = 0;
    
    if (ch >= pairedChars[pairedCharExtra]) {
      index = pairedCharExtra;
    }
    
    while (probe > 1) {
      probe >>= 1;
      
      if (ch >= pairedChars[(index + probe)]) {
        index += probe;
      }
    }
    
    if (pairedChars[index] != ch) {
      index = -1;
    }
    
    return index;
  }
  
  private static int[] pairedChars = { 40, 41, 60, 62, 91, 93, 123, 125, 171, 187, 8216, 8217, 8220, 8221, 8249, 8250, 12296, 12297, 12298, 12299, 12300, 12301, 12302, 12303, 12304, 12305, 12308, 12309, 12310, 12311, 12312, 12313, 12314, 12315 };
  


















  private static int pairedCharPower = 1 << highBit(pairedChars.length);
  private static int pairedCharExtra = pairedChars.length - pairedCharPower;
}
