package com.ibm.icu.text;

import com.ibm.icu.impl.UCharacterProperty;
import java.util.Locale;




















class UppercaseTransliterator
  extends Transliterator
{
  static final String _ID = "Any-Upper";
  private Locale loc;
  
  static void register()
  {
    Transliterator.registerFactory("Any-Upper", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new UppercaseTransliterator(Locale.US);
      }
    });
  }
  




  public UppercaseTransliterator(Locale loc)
  {
    super("Any-Upper", null);
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
      int newLen = m_charppty_.toUpperOrTitleCase(loc, cp, original, true, buffer);
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
