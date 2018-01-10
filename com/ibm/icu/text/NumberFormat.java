package com.ibm.icu.text;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.LocaleUtility;
import com.ibm.icu.util.Currency;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

































































































































































public abstract class NumberFormat
  extends Format
{
  private static final int NUMBERSTYLE = 0;
  private static final int CURRENCYSTYLE = 1;
  private static final int PERCENTSTYLE = 2;
  private static final int SCIENTIFICSTYLE = 3;
  private static final int INTEGERSTYLE = 4;
  public static final int INTEGER_FIELD = 0;
  public static final int FRACTION_FIELD = 1;
  private static NumberFormatShim shim;
  
  public final StringBuffer format(Object number, StringBuffer toAppendTo, FieldPosition pos)
  {
    if ((number instanceof Long))
      return format(((Long)number).longValue(), toAppendTo, pos);
    if ((number instanceof BigInteger))
      return format((BigInteger)number, toAppendTo, pos);
    if ((number instanceof java.math.BigDecimal))
      return format((java.math.BigDecimal)number, toAppendTo, pos);
    if ((number instanceof com.ibm.icu.math.BigDecimal))
      return format((com.ibm.icu.math.BigDecimal)number, toAppendTo, pos);
    if ((number instanceof Number)) {
      return format(((Number)number).doubleValue(), toAppendTo, pos);
    }
    throw new IllegalArgumentException("Cannot format given Object as a Number");
  }
  





  public final Object parseObject(String source, ParsePosition parsePosition)
  {
    return parse(source, parsePosition);
  }
  




  public final String format(double number)
  {
    return format(number, new StringBuffer(), new FieldPosition(0)).toString();
  }
  





  public final String format(long number)
  {
    return format(number, new StringBuffer(), new FieldPosition(0)).toString();
  }
  





  public final String format(BigInteger number)
  {
    return format(number, new StringBuffer(), new FieldPosition(0)).toString();
  }
  





  public final String format(java.math.BigDecimal number)
  {
    return format(number, new StringBuffer(), new FieldPosition(0)).toString();
  }
  





  public final String format(com.ibm.icu.math.BigDecimal number)
  {
    return format(number, new StringBuffer(), new FieldPosition(0)).toString();
  }
  









  public abstract StringBuffer format(double paramDouble, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  









  public abstract StringBuffer format(long paramLong, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  









  public abstract StringBuffer format(BigInteger paramBigInteger, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  









  public abstract StringBuffer format(java.math.BigDecimal paramBigDecimal, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  









  public abstract StringBuffer format(com.ibm.icu.math.BigDecimal paramBigDecimal, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  








  public abstract Number parse(String paramString, ParsePosition paramParsePosition);
  








  public Number parse(String text)
    throws ParseException
  {
    ParsePosition parsePosition = new ParsePosition(0);
    Number result = parse(text, parsePosition);
    if (parsePosition.getIndex() == 0) {
      throw new ParseException("Unparseable number: \"" + text + "\"", 0);
    }
    

    return result;
  }
  









  public boolean isParseIntegerOnly()
  {
    return parseIntegerOnly;
  }
  





  public void setParseIntegerOnly(boolean value)
  {
    parseIntegerOnly = value;
  }
  










  public static final NumberFormat getInstance()
  {
    return getInstance(Locale.getDefault(), 0);
  }
  






  public static NumberFormat getInstance(Locale inLocale)
  {
    return getInstance(inLocale, 0);
  }
  



  public static final NumberFormat getNumberInstance()
  {
    return getInstance(Locale.getDefault(), 0);
  }
  



  public static NumberFormat getNumberInstance(Locale inLocale)
  {
    return getInstance(inLocale, 0);
  }
  











  public static final NumberFormat getIntegerInstance()
  {
    return getInstance(Locale.getDefault(), 4);
  }
  












  public static NumberFormat getIntegerInstance(Locale inLocale)
  {
    return getInstance(inLocale, 4);
  }
  




  public static final NumberFormat getCurrencyInstance()
  {
    return getInstance(Locale.getDefault(), 1);
  }
  




  public static NumberFormat getCurrencyInstance(Locale inLocale)
  {
    return getInstance(inLocale, 1);
  }
  




  public static final NumberFormat getPercentInstance()
  {
    return getInstance(Locale.getDefault(), 2);
  }
  




  public static NumberFormat getPercentInstance(Locale inLocale)
  {
    return getInstance(inLocale, 2);
  }
  





  public static final NumberFormat getScientificInstance()
  {
    return getInstance(Locale.getDefault(), 3);
  }
  





  public static NumberFormat getScientificInstance(Locale inLocale)
  {
    return getInstance(inLocale, 3);
  }
  







  public static abstract class NumberFormatFactory
  {
    public static final int FORMAT_NUMBER = 0;
    






    public static final int FORMAT_CURRENCY = 1;
    






    public static final int FORMAT_PERCENT = 2;
    





    public static final int FORMAT_SCIENTIFIC = 3;
    





    public static final int FORMAT_INTEGER = 4;
    






    public boolean visible()
    {
      return true;
    }
    





    public abstract Set getSupportedLocaleNames();
    




    public abstract NumberFormat createFormat(Locale paramLocale, int paramInt);
    




    protected NumberFormatFactory() {}
  }
  




  public static abstract class SimpleNumberFormatFactory
    extends NumberFormat.NumberFormatFactory
  {
    final Set localeNames;
    



    final boolean visible;
    




    public SimpleNumberFormatFactory(Locale locale)
    {
      this(locale, true);
    }
    


    public SimpleNumberFormatFactory(Locale locale, boolean visible)
    {
      localeNames = Collections.singleton(LocaleUtility.canonicalLocaleString(locale));
      this.visible = visible;
    }
    


    public final boolean visible()
    {
      return visible;
    }
    


    public final Set getSupportedLocaleNames()
    {
      return localeNames;
    }
  }
  












  private static NumberFormatShim getShim()
  {
    if (shim == null) {
      try {
        Class cls = Class.forName("com.ibm.icu.text.NumberFormatServiceShim");
        shim = (NumberFormatShim)cls.newInstance();
      }
      catch (Exception e)
      {
        e.printStackTrace();
        throw new RuntimeException(e.getMessage());
      }
    }
    
    return shim;
  }
  




  public static Locale[] getAvailableLocales()
  {
    if (shim == null) {
      return ICULocaleData.getAvailableLocales();
    }
    return getShim().getAvailableLocales();
  }
  







  public static Object registerFactory(NumberFormatFactory factory)
  {
    if (factory == null) {
      throw new IllegalArgumentException("factory must not be null");
    }
    return getShim().registerFactory(factory);
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
  





  public int hashCode()
  {
    return maximumIntegerDigits * 37 + maxFractionDigits;
  }
  








  public boolean equals(Object obj)
  {
    if (obj == null) return false;
    if (this == obj)
      return true;
    if (getClass() != obj.getClass())
      return false;
    NumberFormat other = (NumberFormat)obj;
    return (maximumIntegerDigits == maximumIntegerDigits) && (minimumIntegerDigits == minimumIntegerDigits) && (maximumFractionDigits == maximumFractionDigits) && (minimumFractionDigits == minimumFractionDigits) && (groupingUsed == groupingUsed) && (parseIntegerOnly == parseIntegerOnly);
  }
  









  public Object clone()
  {
    NumberFormat other = (NumberFormat)super.clone();
    return other;
  }
  









  public boolean isGroupingUsed()
  {
    return groupingUsed;
  }
  






  public void setGroupingUsed(boolean newValue)
  {
    groupingUsed = newValue;
  }
  








  public int getMaximumIntegerDigits()
  {
    return maximumIntegerDigits;
  }
  











  public void setMaximumIntegerDigits(int newValue)
  {
    maximumIntegerDigits = Math.max(0, newValue);
    if (minimumIntegerDigits > maximumIntegerDigits) {
      minimumIntegerDigits = maximumIntegerDigits;
    }
  }
  








  public int getMinimumIntegerDigits()
  {
    return minimumIntegerDigits;
  }
  











  public void setMinimumIntegerDigits(int newValue)
  {
    minimumIntegerDigits = Math.max(0, newValue);
    if (minimumIntegerDigits > maximumIntegerDigits) {
      maximumIntegerDigits = minimumIntegerDigits;
    }
  }
  








  public int getMaximumFractionDigits()
  {
    return maximumFractionDigits;
  }
  











  public void setMaximumFractionDigits(int newValue)
  {
    maximumFractionDigits = Math.max(0, newValue);
    if (maximumFractionDigits < minimumFractionDigits) {
      minimumFractionDigits = maximumFractionDigits;
    }
  }
  








  public int getMinimumFractionDigits()
  {
    return minimumFractionDigits;
  }
  











  public void setMinimumFractionDigits(int newValue)
  {
    minimumFractionDigits = Math.max(0, newValue);
    if (maximumFractionDigits < minimumFractionDigits) {
      maximumFractionDigits = minimumFractionDigits;
    }
  }
  








  public void setCurrency(Currency theCurrency)
  {
    currency = theCurrency;
  }
  




  public Currency getCurrency()
  {
    return currency;
  }
  


  private static NumberFormat getInstance(Locale desiredLocale, int choice)
  {
    if (shim == null) {
      return createInstance(desiredLocale, choice);
    }
    return getShim().createInstance(desiredLocale, choice);
  }
  


  static NumberFormat createInstance(Locale desiredLocale, int choice)
  {
    String pattern = getPattern(desiredLocale, choice);
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(desiredLocale);
    DecimalFormat format = new DecimalFormat(pattern, symbols);
    




    if (choice == 4) {
      format.setMaximumFractionDigits(0);
      format.setDecimalSeparatorAlwaysShown(false);
      format.setParseIntegerOnly(true);
    }
    return format;
  }
  



























  protected static String getPattern(Locale forLocale, int choice)
  {
    if (choice == 3)
    {

      return "#E0";
    }
    












    ResourceBundle rb = ICULocaleData.getLocaleElements(forLocale);
    String[] numberPatterns = rb.getStringArray("NumberPatterns");
    















    int entry = choice == 4 ? 0 : choice;
    return numberPatterns[entry];
  }
  











  private void readObject(ObjectInputStream stream)
    throws IOException, ClassNotFoundException
  {
    stream.defaultReadObject();
    if (serialVersionOnStream < 1)
    {
      maximumIntegerDigits = maxIntegerDigits;
      minimumIntegerDigits = minIntegerDigits;
      maximumFractionDigits = maxFractionDigits;
      minimumFractionDigits = minFractionDigits;
    }
    


    if ((minimumIntegerDigits > maximumIntegerDigits) || (minimumFractionDigits > maximumFractionDigits) || (minimumIntegerDigits < 0) || (minimumFractionDigits < 0))
    {

      throw new InvalidObjectException("Digit count range invalid");
    }
    serialVersionOnStream = 1;
  }
  







  private void writeObject(ObjectOutputStream stream)
    throws IOException
  {
    maxIntegerDigits = (maximumIntegerDigits > 127 ? Byte.MAX_VALUE : (byte)maximumIntegerDigits);
    
    minIntegerDigits = (minimumIntegerDigits > 127 ? Byte.MAX_VALUE : (byte)minimumIntegerDigits);
    
    maxFractionDigits = (maximumFractionDigits > 127 ? Byte.MAX_VALUE : (byte)maximumFractionDigits);
    
    minFractionDigits = (minimumFractionDigits > 127 ? Byte.MAX_VALUE : (byte)minimumFractionDigits);
    
    stream.defaultWriteObject();
  }
  

















  private boolean groupingUsed = true;
  
















  private byte maxIntegerDigits = 40;
  
















  private byte minIntegerDigits = 1;
  
















  private byte maxFractionDigits = 3;
  
















  private byte minFractionDigits = 0;
  






  private boolean parseIntegerOnly = false;
  










  private int maximumIntegerDigits = 40;
  








  private int minimumIntegerDigits = 1;
  








  private int maximumFractionDigits = 3;
  








  private int minimumFractionDigits = 0;
  









  private Currency currency;
  









  static final int currentSerialVersion = 1;
  








  private int serialVersionOnStream = 1;
  static final long serialVersionUID = -2308460125733713944L;
  
  protected NumberFormat() {}
  
  static abstract class NumberFormatShim
  {
    NumberFormatShim() {}
    
    abstract Locale[] getAvailableLocales();
    
    abstract Object registerFactory(NumberFormat.NumberFormatFactory paramNumberFormatFactory);
    
    abstract boolean unregister(Object paramObject);
    
    abstract NumberFormat createInstance(Locale paramLocale, int paramInt);
  }
}
