package com.ibm.icu.text;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.LocaleUtility;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;





























































































































































































































public abstract class Collator
  implements Comparator, Cloneable
{
  public static final int PRIMARY = 0;
  public static final int SECONDARY = 1;
  public static final int TERTIARY = 2;
  public static final int QUATERNARY = 3;
  public static final int IDENTICAL = 15;
  public static final int NO_DECOMPOSITION = 16;
  public static final int CANONICAL_DECOMPOSITION = 17;
  private static ServiceShim shim;
  
  public void setStrength(int newStrength)
  {
    if ((newStrength != 0) && (newStrength != 1) && (newStrength != 2) && (newStrength != 3) && (newStrength != 15))
    {



      throw new IllegalArgumentException("Incorrect comparison level.");
    }
    m_strength_ = newStrength;
  }
  






























  public void setDecomposition(int decomposition)
  {
    if ((decomposition != 16) && (decomposition != 17))
    {
      throw new IllegalArgumentException("Wrong decomposition mode.");
    }
    m_decomposition_ = decomposition;
  }
  













  public static final Collator getInstance()
  {
    return getInstance(Locale.getDefault());
  }
  



  public Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }
  
  static abstract class ServiceShim {
    ServiceShim() {}
    
    abstract Collator getInstance(Locale paramLocale);
    
    abstract Object registerInstance(Collator paramCollator, Locale paramLocale);
    
    abstract Object registerFactory(Collator.CollatorFactory paramCollatorFactory);
    
    abstract boolean unregister(Object paramObject);
    
    abstract Locale[] getAvailableLocales();
    
    abstract String getDisplayName(Locale paramLocale1, Locale paramLocale2);
  }
  
  public static abstract class CollatorFactory {
    public boolean visible() {
      return true;
    }
    







    public abstract Collator createCollator(Locale paramLocale);
    







    public String getDisplayName(Locale objectLocale, Locale displayLocale)
    {
      if (visible()) {
        Set supported = getSupportedLocaleIDs();
        String name = LocaleUtility.canonicalLocaleString(objectLocale.toString());
        if (supported.contains(name)) {
          return objectLocale.getDisplayName(displayLocale);
        }
      }
      return null;
    }
    









    public abstract Set getSupportedLocaleIDs();
    








    protected CollatorFactory() {}
  }
  








  private static ServiceShim getShim()
  {
    if (shim == null) {
      try {
        Class cls = Class.forName("com.ibm.icu.text.CollatorServiceShim");
        shim = (ServiceShim)cls.newInstance();
      }
      catch (Exception e)
      {
        e.printStackTrace();
        throw new RuntimeException(e.getMessage());
      }
    }
    
    return shim;
  }
  












  public static final Collator getInstance(Locale locale)
  {
    if (shim == null) {
      return new RuleBasedCollator(locale);
    }
    return shim.getInstance(locale);
  }
  









  public static final Object registerInstance(Collator collator, Locale locale)
  {
    return getShim().registerInstance(collator, locale);
  }
  







  public static final Object registerFactory(CollatorFactory factory)
  {
    return getShim().registerFactory(factory);
  }
  





  public static final boolean unregister(Object registryKey)
  {
    if (shim == null) {
      return false;
    }
    return shim.unregister(registryKey);
  }
  






  public static Locale[] getAvailableLocales()
  {
    if (shim == null) {
      return ICULocaleData.getAvailableLocales();
    }
    return shim.getAvailableLocales();
  }
  






  public static String getDisplayName(Locale objectLocale, Locale displayLocale)
  {
    return getShim().getDisplayName(objectLocale, displayLocale);
  }
  





  public static String getDisplayName(Locale objectLocale)
  {
    return getShim().getDisplayName(objectLocale, Locale.getDefault());
  }
  
















  public int getStrength()
  {
    return m_strength_;
  }
  














  public int getDecomposition()
  {
    return m_decomposition_;
  }
  





















  public int compare(Object source, Object target)
  {
    if ((!(source instanceof String)) || (!(target instanceof String))) {
      throw new IllegalArgumentException("Arguments have to be of type String");
    }
    return compare((String)source, (String)target);
  }
  













  public boolean equals(String source, String target)
  {
    return compare(source, target) == 0;
  }
  








  public UnicodeSet getTailoredSet()
  {
    return new UnicodeSet(0, 1114111);
  }
  






















































































































  private int m_strength_ = 2;
  



  private int m_decomposition_ = 17;
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract int compare(String paramString1, String paramString2);
  
  public abstract CollationKey getCollationKey(String paramString);
  
  public abstract int setVariableTop(String paramString);
  
  public abstract int getVariableTop();
  
  public abstract void setVariableTop(int paramInt);
  
  protected Collator() {}
}
