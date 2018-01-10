package com.ibm.icu.text;

import com.ibm.icu.impl.UCharacterProperty;
import java.util.Locale;





















class LowercaseTransliterator
  extends Transliterator
{
  static final String _ID = "Any-Lower";
  private Locale loc;
  
  static void register()
  {
    Transliterator.registerFactory("Any-Lower", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new LowercaseTransliterator(Locale.US);
      }
      
    });
    Transliterator.registerSpecialInverse("Lower", "Upper", true);
  }
  





  public LowercaseTransliterator(Locale loc)
  {
    super("Any-Lower", null);
    this.loc = loc;
  }
  



  protected void handleTransliterate(Replaceable text, Transliterator.Position offsets, boolean isIncremental)
  {
    int textPos = start;
    if (textPos >= limit) { return;
    }
    


    UCharacterIterator original = UCharacterIterator.getInstance(text);
    



    int limit = limit;
    


    while (textPos < limit) {
      original.setIndex(textPos);
      int cp = original.currentCodePoint();
      int oldLen = UTF16.getCharCount(cp);
      int newLen = m_charppty_.toLowerCase(loc, cp, original, buffer);
      if (newLen >= 0) {
        text.replace(textPos, textPos + oldLen, buffer, 0, newLen);
        if (newLen != oldLen) {
          textPos += newLen;
          limit += newLen - oldLen;
          contextLimit += newLen - oldLen;
          continue;
        }
      }
      textPos += oldLen;
    }
    start = limit;
  }
  
  private char[] buffer = new char[10];
  


  private static final UCharacterProperty m_charppty_ = ;
}
