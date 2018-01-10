package com.ibm.icu.text;

import com.ibm.icu.impl.UCharacterProperty;
import java.util.Locale;




















class TitlecaseTransliterator
  extends Transliterator
{
  static final String _ID = "Any-Title";
  private Locale loc;
  static final UnicodeSet SKIP = new UnicodeSet("[­ ’ \\' [:Mn:] [:Me:] [:Cf:] [:Lm:] [:Sk:]]");
  




  static final UnicodeSet CASED = new UnicodeSet("[[:Lu:] [:Ll:] [:Lt:]]");
  


  static void register()
  {
    Transliterator.registerFactory("Any-Title", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new TitlecaseTransliterator(Locale.US);
      }
      
    });
    Transliterator.registerSpecialInverse("Title", "Lower", false);
  }
  


  public TitlecaseTransliterator(Locale loc)
  {
    super("Any-Title", null);
    this.loc = loc;
    
    setMaximumContextLength(2);
  }
  






  protected void handleTransliterate(Replaceable text, Transliterator.Position offsets, boolean incremental)
  {
    boolean doTitle = true;
    


    int c;
    

    for (int start = start - 1; start >= contextStart; start -= UTF16.getCharCount(c)) {
      c = text.char32At(start);
      if (!SKIP.contains(c))
      {

        doTitle = !CASED.contains(c);
        break;
      }
    }
    



    int textPos = start;
    if (textPos >= limit) { return;
    }
    


    UCharacterIterator original = UCharacterIterator.getInstance(text);
    



    int limit = limit;
    



    while (textPos < limit) {
      original.setIndex(textPos);
      int cp = original.currentCodePoint();
      int oldLen = UTF16.getCharCount(cp);
      
      if (!SKIP.contains(cp)) { int newLen;
        if (doTitle) {
          newLen = m_charppty_.toUpperOrTitleCase(loc, cp, original, false, buffer);
        } else {
          newLen = m_charppty_.toLowerCase(loc, cp, original, buffer);
        }
        doTitle = !CASED.contains(cp);
        if (newLen >= 0) {
          text.replace(textPos, textPos + oldLen, buffer, 0, newLen);
          if (newLen != oldLen) {
            textPos += newLen;
            limit += newLen - oldLen;
            contextLimit += newLen - oldLen;
            continue;
          }
        }
      }
      textPos += oldLen;
    }
    start = limit;
  }
  
  private char[] buffer = new char[10];
  


  private static final UCharacterProperty m_charppty_ = UCharacterProperty.getInstance();
}
