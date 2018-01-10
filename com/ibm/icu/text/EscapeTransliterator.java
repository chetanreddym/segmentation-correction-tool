package com.ibm.icu.text;

import com.ibm.icu.impl.Utility;



































































class EscapeTransliterator
  extends Transliterator
{
  private String prefix;
  private String suffix;
  private int radix;
  private int minDigits;
  private boolean grokSupplementals;
  private EscapeTransliterator supplementalHandler;
  
  static void register()
  {
    Transliterator.registerFactory("Any-Hex/Unicode", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new EscapeTransliterator("Any-Hex/Unicode", "U+", "", 16, 4, true, null);

      }
      

    });
    Transliterator.registerFactory("Any-Hex/Java", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new EscapeTransliterator("Any-Hex/Java", "\\u", "", 16, 4, false, null);

      }
      

    });
    Transliterator.registerFactory("Any-Hex/C", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new EscapeTransliterator("Any-Hex/C", "\\u", "", 16, 4, true, new EscapeTransliterator("", "\\U", "", 16, 8, true, null));

      }
      


    });
    Transliterator.registerFactory("Any-Hex/XML", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new EscapeTransliterator("Any-Hex/XML", "&#x", ";", 16, 1, true, null);

      }
      

    });
    Transliterator.registerFactory("Any-Hex/XML10", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new EscapeTransliterator("Any-Hex/XML10", "&#", ";", 10, 1, true, null);

      }
      

    });
    Transliterator.registerFactory("Any-Hex/Perl", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new EscapeTransliterator("Any-Hex/Perl", "\\x{", "}", 16, 1, true, null);

      }
      

    });
    Transliterator.registerFactory("Any-Hex", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new EscapeTransliterator("Any-Hex", "\\u", "", 16, 4, false, null);
      }
    });
  }
  







  EscapeTransliterator(String ID, String prefix, String suffix, int radix, int minDigits, boolean grokSupplementals, EscapeTransliterator supplementalHandler)
  {
    super(ID, null);
    this.prefix = prefix;
    this.suffix = suffix;
    this.radix = radix;
    this.minDigits = minDigits;
    this.grokSupplementals = grokSupplementals;
    this.supplementalHandler = supplementalHandler;
  }
  



  protected void handleTransliterate(Replaceable text, Transliterator.Position pos, boolean incremental)
  {
    int start = start;
    int limit = limit;
    
    StringBuffer buf = new StringBuffer(prefix);
    int prefixLen = prefix.length();
    boolean redoPrefix = false;
    
    while (start < limit) {
      int c = grokSupplementals ? text.char32At(start) : text.charAt(start);
      int charLen = grokSupplementals ? UTF16.getCharCount(c) : 1;
      
      if (((c & 0xFFFF0000) != 0) && (supplementalHandler != null)) {
        buf.setLength(0);
        buf.append(supplementalHandler.prefix);
        Utility.appendNumber(buf, c, supplementalHandler.radix, supplementalHandler.minDigits);
        
        buf.append(supplementalHandler.suffix);
        redoPrefix = true;
      } else {
        if (redoPrefix) {
          buf.setLength(0);
          buf.append(prefix);
          redoPrefix = false;
        } else {
          buf.setLength(prefixLen);
        }
        Utility.appendNumber(buf, c, radix, minDigits);
        buf.append(suffix);
      }
      
      text.replace(start, start + charLen, buf.toString());
      start += buf.length();
      limit += buf.length() - charLen;
    }
    
    contextLimit += limit - limit;
    limit = limit;
    start = start;
  }
}
