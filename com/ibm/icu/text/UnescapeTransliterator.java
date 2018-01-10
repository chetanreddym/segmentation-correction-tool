package com.ibm.icu.text;

import com.ibm.icu.lang.UCharacter;







































class UnescapeTransliterator
  extends Transliterator
{
  private char[] spec;
  private static final char END = '￿';
  
  static void register()
  {
    Transliterator.registerFactory("Hex-Any/Unicode", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new UnescapeTransliterator("Hex-Any/Unicode", new char[] { '\002', '\000', '\020', '\004', '\006', 'U', '+', 65535 });


      }
      


    });
    Transliterator.registerFactory("Hex-Any/Java", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new UnescapeTransliterator("Hex-Any/Java", new char[] { '\002', '\000', '\020', '\004', '\004', '\\', 'u', 65535 });


      }
      


    });
    Transliterator.registerFactory("Hex-Any/C", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new UnescapeTransliterator("Hex-Any/C", new char[] { '\002', '\000', '\020', '\004', '\004', '\\', 'u', '\002', '\000', '\020', '\b', '\b', '\\', 'U', 65535 });


      }
      



    });
    Transliterator.registerFactory("Hex-Any/XML", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new UnescapeTransliterator("Hex-Any/XML", new char[] { '\003', '\001', '\020', '\001', '\006', '&', '#', 'x', ';', 65535 });


      }
      


    });
    Transliterator.registerFactory("Hex-Any/XML10", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new UnescapeTransliterator("Hex-Any/XML10", new char[] { '\002', '\001', '\n', '\001', '\007', '&', '#', ';', 65535 });


      }
      


    });
    Transliterator.registerFactory("Hex-Any/Perl", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new UnescapeTransliterator("Hex-Any/Perl", new char[] { '\003', '\001', '\020', '\001', '\006', '\\', 'x', '{', '}', 65535 });


      }
      


    });
    Transliterator.registerFactory("Hex-Any", new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new UnescapeTransliterator("Hex-Any", new char[] { '\002', '\000', '\020', '\004', '\006', 'U', '+', '\002', '\000', '\020', '\004', '\004', '\\', 'u', '\002', '\000', '\020', '\b', '\b', '\\', 'U', '\003', '\001', '\020', '\001', '\006', '&', '#', 'x', ';', '\002', '\001', '\n', '\001', '\007', '&', '#', ';', '\003', '\001', '\020', '\001', '\006', '\\', 'x', '{', '}', 65535 });
      }
    });
  }
  










  UnescapeTransliterator(String ID, char[] spec)
  {
    super(ID, null);
    this.spec = spec;
  }
  



  protected void handleTransliterate(Replaceable text, Transliterator.Position pos, boolean isIncremental)
  {
    int start = start;
    int limit = limit;
    


    while (start < limit)
    {


      int j = 0; for (int ipat = 0; spec[ipat] != 65535; j++)
      {

        int prefixLen = spec[(ipat++)];
        int suffixLen = spec[(ipat++)];
        int radix = spec[(ipat++)];
        int minDigits = spec[(ipat++)];
        int maxDigits = spec[(ipat++)];
        


        int s = start;
        boolean match = true;
        
        for (int i = 0; i < prefixLen; i++) {
          if ((s >= limit) && 
            (i > 0))
          {



            if (isIncremental) {
              break label461;
            }
            match = false;
            break;
          }
          
          char c = text.charAt(s++);
          if (c != spec[(ipat + i)]) {
            match = false;
            break;
          }
        }
        
        if (match) {
          int u = 0;
          int digitCount = 0;
          for (;;) {
            if (s >= limit)
            {
              if ((s > start) && (isIncremental)) {
                break label461;
              }
            }
            else {
              int ch = text.char32At(s);
              int digit = UCharacter.digit(ch, radix);
              if (digit >= 0)
              {

                s += UTF16.getCharCount(ch);
                u = u * radix + digit;
                digitCount++; if (digitCount == maxDigits)
                  break;
              }
            }
          }
          match = digitCount >= minDigits;
          
          if (match) {
            for (i = 0; i < suffixLen; i++) {
              if (s >= limit)
              {
                if ((s > start) && (isIncremental)) {
                  break label461;
                }
                match = false;
                break;
              }
              char c = text.charAt(s++);
              if (c != spec[(ipat + prefixLen + i)]) {
                match = false;
                break;
              }
            }
            
            if (match)
            {
              String str = UTF16.valueOf(u);
              text.replace(start, s, str);
              limit -= s - start - str.length();
              



              break;
            }
          }
        }
        
        ipat += prefixLen + suffixLen;
      }
      
      if (start < limit) {
        start += UTF16.getCharCount(text.char32At(start));
      }
    }
    label461:
    contextLimit += limit - limit;
    limit = limit;
    start = start;
  }
}
