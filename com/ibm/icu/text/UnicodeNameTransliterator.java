package com.ibm.icu.text;

import com.ibm.icu.lang.UCharacter;















class UnicodeNameTransliterator
  extends Transliterator
{
  static final String _ID = "Any-Name";
  static final String OPEN_DELIM = "\\N{";
  static final char CLOSE_DELIM = '}';
  static final int OPEN_DELIM_LEN = 3;
  
  static void register()
  {
    Transliterator.registerFactory("Any-Name", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new UnicodeNameTransliterator(null);
      }
    });
  }
  


  public UnicodeNameTransliterator(UnicodeFilter filter)
  {
    super("Any-Name", filter);
  }
  



  protected void handleTransliterate(Replaceable text, Transliterator.Position offsets, boolean isIncremental)
  {
    int cursor = start;
    int limit = limit;
    
    StringBuffer str = new StringBuffer();
    str.append("\\N{");
    


    while (cursor < limit) {
      int c = text.char32At(cursor);
      String name; if ((name = UCharacter.getExtendedName(c)) != null)
      {
        str.setLength(3);
        str.append(name).append('}');
        
        int clen = UTF16.getCharCount(c);
        text.replace(cursor, cursor + clen, str.toString());
        int len = str.length();
        cursor += len;
        limit += len - clen;
      } else {
        cursor++;
      }
    }
    
    contextLimit += limit - limit;
    limit = limit;
    start = cursor;
  }
}
