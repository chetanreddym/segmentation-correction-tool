package com.ibm.icu.text;

import com.ibm.icu.impl.UCharacterName;
import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.lang.UCharacter;
















class NameUnicodeTransliterator
  extends Transliterator
{
  char openDelimiter;
  char closeDelimiter;
  static final String _ID = "Name-Any";
  static final String OPEN_PAT = "\\N~{~";
  static final char OPEN_DELIM = '\\';
  static final char CLOSE_DELIM = '}';
  static final char SPACE = ' ';
  
  static void register()
  {
    Transliterator.registerFactory("Name-Any", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new NameUnicodeTransliterator(null);
      }
    });
  }
  


  public NameUnicodeTransliterator(UnicodeFilter filter)
  {
    super("Name-Any", filter);
  }
  




  protected void handleTransliterate(Replaceable text, Transliterator.Position offsets, boolean isIncremental)
  {
    int maxLen = UCharacterName.getInstance().getMaxCharNameLength() + 1;
    
    StringBuffer name = new StringBuffer(maxLen);
    

    UnicodeSet legal = new UnicodeSet();
    UCharacterName.getInstance().getCharNameCharacters(legal);
    
    int cursor = start;
    int limit = limit;
    



    int mode = 0;
    int openPos = -1;
    

    while (cursor < limit) {
      int c = text.char32At(cursor);
      
      switch (mode) {
      case 0: 
        if (c == 92) {
          openPos = cursor;
          int i = Utility.parsePattern("\\N~{~", text, cursor, limit);
          if ((i >= 0) && (i < limit)) {
            mode = 1;
            name.setLength(0);
            cursor = i;
            continue;
          }
        }
        







        break;
      case 1: 
        if (UCharacterProperty.isRuleWhiteSpace(c))
        {
          if ((name.length() > 0) && (name.charAt(name.length() - 1) != ' '))
          {
            name.append(' ');
            

            if (name.length() > maxLen) {
              mode = 0;
            }
          }
        }
        else
        {
          if (c == 125)
          {
            int len = name.length();
            

            if ((len > 0) && (name.charAt(len - 1) == ' '))
            {
              name.setLength(--len);
            }
            
            c = UCharacter.getCharFromExtendedName(name.toString());
            if (c != -1)
            {


              cursor++;
              
              String str = UTF16.valueOf(c);
              text.replace(openPos, cursor, str);
              



              int delta = cursor - openPos - str.length();
              cursor -= delta;
              limit -= delta;
            }
            


            mode = 0;
            openPos = -1;
            continue;
          }
          
          if (legal.contains(c)) {
            UTF16.append(name, c);
            

            if (name.length() >= maxLen) {
              mode = 0;
            }
            
          }
          else
          {
            cursor--;
            mode = 0;
          }
        }
        break;
      }
      
      cursor += UTF16.getCharCount(c);
    }
    
    contextLimit += limit - limit;
    limit = limit;
    

    start = ((isIncremental) && (openPos >= 0) ? openPos : cursor);
  }
}
