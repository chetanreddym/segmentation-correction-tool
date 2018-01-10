package com.ibm.icu.util;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.LocaleUtility;
import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;





























































public class Currency
  implements Serializable
{
  private String isoCode;
  public static final int SYMBOL_NAME = 0;
  public static final int LONG_NAME = 1;
  private static ServiceShim shim;
  
  private static ServiceShim getShim()
  {
    if (shim == null) {
      try {
        Class cls = Class.forName("com.ibm.icu.util.CurrencyServiceShim");
        shim = (ServiceShim)cls.newInstance();
      }
      catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e.getMessage());
      }
    }
    return shim;
  }
  




  public static Currency getInstance(Locale locale)
  {
    if (shim == null) {
      return createCurrency(locale);
    }
    return shim.createInstance(locale);
  }
  


  static Currency createCurrency(Locale loc)
  {
    String country = loc.getCountry();
    String variant = loc.getVariant();
    if ((variant.equals("PREEURO")) || (variant.equals("EURO"))) {
      country = country + '_' + variant;
    }
    ResourceBundle bundle = ICULocaleData.getLocaleElements(new Locale("", "", ""));
    Object[][] cm = (Object[][])bundle.getObject("CurrencyMap");
    

    String curriso = null;
    for (int i = 0; i < cm.length; i++) {
      if (country.equals((String)cm[i][0])) {
        curriso = (String)cm[i][1];
        break;
      }
    }
    
    return curriso != null ? new Currency(curriso) : null;
  }
  



  public static Currency getInstance(String theISOCode)
  {
    return new Currency(theISOCode);
  }
  




  public static Object registerInstance(Currency currency, Locale locale)
  {
    return getShim().registerInstance(currency, locale);
  }
  




  public static boolean unregister(Object registryKey)
  {
    if (registryKey == null) {
      throw new IllegalArgumentException("registryKey must not be null");
    }
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
  






  public int hashCode()
  {
    return isoCode.hashCode();
  }
  



  public boolean equals(Object rhs)
  {
    try
    {
      return equals((Currency)rhs);
    }
    catch (ClassCastException e) {}
    return false;
  }
  




  public boolean equals(Currency c)
  {
    if (c == null) return false;
    if (c == this) return true;
    return (c.getClass() == Currency.class) && (isoCode.equals(isoCode));
  }
  




  public String getCurrencyCode()
  {
    return isoCode;
  }
  




























  public String getName(Locale locale, int nameStyle, boolean[] isChoiceFormat)
  {
    if ((nameStyle < 0) || (nameStyle > 1)) {
      throw new IllegalArgumentException();
    }
    









    String s = null;
    

    while (locale != null) {
      ResourceBundle rb = ICULocaleData.getLocaleElements(locale);
      try
      {
        Object[][] currencies = (Object[][])rb.getObject("Currencies");
        
        for (int i = 0; i < currencies.length; i++) {
          if (isoCode.equals((String)currencies[i][0])) {
            s = ((String[])currencies[i][1])[nameStyle];
            break;
          }
        }
      }
      catch (MissingResourceException e) {}
      


      if (s != null) {
        break;
      }
      locale = LocaleUtility.fallback(locale);
    }
    





    isChoiceFormat[0] = false;
    if (s != null) {
      int i = 0;
      while ((i < s.length()) && (s.charAt(i) == '=') && (i < 2)) {
        i++;
      }
      isChoiceFormat[0] = (i == 1 ? 1 : false);
      if (i != 0)
      {
        s = s.substring(1);
      }
      return s;
    }
    

    return isoCode;
  }
  






  public int getDefaultFractionDigits()
  {
    return findData()[0].intValue();
  }
  





  public double getRoundingIncrement()
  {
    Integer[] data = findData();
    
    int data1 = data[1].intValue();
    


    if (data1 == 0) {
      return 0.0D;
    }
    
    int data0 = data[0].intValue();
    

    if ((data0 < 0) || (data0 >= POW10.length)) {
      return 0.0D;
    }
    


    return data1 / POW10[data0];
  }
  



  public String toString()
  {
    return isoCode;
  }
  



  private Currency(String theISOCode)
  {
    isoCode = theISOCode;
  }
  








  private Integer[] findData()
  {
    try
    {
      ResourceBundle root = ICULocaleData.getLocaleElements("");
      
      Object[][] currencyMeta = (Object[][])root.getObject("CurrencyMeta");
      
      Integer[] i = null;
      int defaultPos = -1;
      



      for (int j = 0; j < currencyMeta.length; j++) {
        Object[] row = currencyMeta[j];
        String s = (String)row[0];
        int c = isoCode.compareToIgnoreCase(s);
        if (c == 0) {
          i = (Integer[])row[1];
        }
        else {
          if ("DEFAULT".equalsIgnoreCase(s)) {
            defaultPos = j;
          }
          if ((c < 0) && (defaultPos >= 0)) {
            break;
          }
        }
      }
      if ((i == null) && (defaultPos >= 0)) {
        i = (Integer[])currencyMeta[defaultPos][1];
      }
      
      if ((i != null) && (i.length >= 2)) {
        return i;
      }
    }
    catch (MissingResourceException e) {}
    

    return LAST_RESORT_DATA;
  }
  




  private static final Integer[] LAST_RESORT_DATA = { new Integer(2), new Integer(0) };
  


  private static final int[] POW10 = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };
  
  static abstract class ServiceShim
  {
    ServiceShim() {}
    
    abstract Locale[] getAvailableLocales();
    
    abstract Currency createInstance(Locale paramLocale);
    
    abstract Object registerInstance(Currency paramCurrency, Locale paramLocale);
    
    abstract boolean unregister(Object paramObject);
  }
}
