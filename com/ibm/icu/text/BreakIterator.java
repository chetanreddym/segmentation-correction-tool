package com.ibm.icu.text;

import java.lang.ref.SoftReference;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Locale;











































































































































































































public abstract class BreakIterator
  implements Cloneable
{
  public static final int DONE = -1;
  public static final int KIND_CHARACTER = 0;
  public static final int KIND_WORD = 1;
  public static final int KIND_LINE = 2;
  public static final int KIND_SENTENCE = 3;
  public static final int KIND_TITLE = 4;
  
  protected BreakIterator() {}
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new InternalError();
    }
  }
  







  public abstract int first();
  







  public abstract int last();
  







  public abstract int next(int paramInt);
  







  public abstract int next();
  







  public abstract int previous();
  







  public abstract int following(int paramInt);
  







  static abstract class BreakIteratorServiceShim
  {
    BreakIteratorServiceShim() {}
    






    public abstract Object registerInstance(BreakIterator paramBreakIterator, Locale paramLocale, int paramInt);
    






    public abstract boolean unregister(Object paramObject);
    






    public abstract Locale[] getAvailableLocales();
    






    public abstract BreakIterator createBreakIterator(Locale paramLocale, int paramInt);
  }
  






  public int preceding(int offset)
  {
    int pos = following(offset);
    while ((pos >= offset) && (pos != -1))
      pos = previous();
    return pos;
  }
  











  public boolean isBoundary(int offset)
  {
    if (offset == 0) {
      return true;
    }
    
    return following(offset - 1) == offset;
  }
  









  public abstract int current();
  








  public abstract CharacterIterator getText();
  








  public void setText(String newText)
  {
    setText(new StringCharacterIterator(newText));
  }
  

























  private static final SoftReference[] iterCache = new SoftReference[5];
  

  private static BreakIteratorServiceShim shim;
  

  public abstract void setText(CharacterIterator paramCharacterIterator);
  

  public static BreakIterator getWordInstance()
  {
    return getWordInstance(Locale.getDefault());
  }
  







  public static BreakIterator getWordInstance(Locale where)
  {
    return getBreakInstance(where, 1);
  }
  








  public static BreakIterator getLineInstance()
  {
    return getLineInstance(Locale.getDefault());
  }
  








  public static BreakIterator getLineInstance(Locale where)
  {
    return getBreakInstance(where, 2);
  }
  








  public static BreakIterator getCharacterInstance()
  {
    return getCharacterInstance(Locale.getDefault());
  }
  








  public static BreakIterator getCharacterInstance(Locale where)
  {
    return getBreakInstance(where, 0);
  }
  







  public static BreakIterator getSentenceInstance()
  {
    return getSentenceInstance(Locale.getDefault());
  }
  






  public static BreakIterator getSentenceInstance(Locale where)
  {
    return getBreakInstance(where, 3);
  }
  









  public static BreakIterator getTitleInstance()
  {
    return getTitleInstance(Locale.getDefault());
  }
  









  public static BreakIterator getTitleInstance(Locale where)
  {
    return getBreakInstance(where, 4);
  }
  










  public static Object registerInstance(BreakIterator iter, Locale locale, int kind)
  {
    return getShim().registerInstance(iter, locale, kind);
  }
  






  public static boolean unregister(Object key)
  {
    if (key == null) {
      throw new IllegalArgumentException("registry key must not be null");
    }
    








    if (shim != null) {
      return shim.unregister(key);
    }
    return false;
  }
  



  private static BreakIterator getBreakInstance(Locale where, int kind)
  {
    if (iterCache[kind] != null) {
      BreakIteratorCache cache = (BreakIteratorCache)iterCache[kind].get();
      if ((cache != null) && 
        (cache.getLocale().equals(where))) {
        return cache.createBreakInstance();
      }
    }
    


    BreakIterator result = getShim().createBreakIterator(where, kind);
    
    BreakIteratorCache cache = new BreakIteratorCache(where, result);
    iterCache[kind] = new SoftReference(cache);
    return result;
  }
  








  public static synchronized Locale[] getAvailableLocales()
  {
    return getShim().getAvailableLocales();
  }
  


  private static final class BreakIteratorCache
  {
    BreakIteratorCache(Locale where, BreakIterator iter)
    {
      this.where = where;
      this.iter = ((BreakIterator)iter.clone());
    }
    
    Locale getLocale() {
      return where;
    }
    
    BreakIterator createBreakInstance() {
      return (BreakIterator)iter.clone();
    }
    



    private BreakIterator iter;
    


    private Locale where;
  }
  


  private static BreakIteratorServiceShim getShim()
  {
    if (shim == null) {
      try {
        Class cls = Class.forName("com.ibm.icu.text.BreakIteratorFactory");
        shim = (BreakIteratorServiceShim)cls.newInstance();
      }
      catch (Exception e)
      {
        e.printStackTrace();
        throw new RuntimeException(e.getMessage());
      }
    }
    
    return shim;
  }
}
