package com.ibm.icu.text;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.util.Currency;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.ChoiceFormat;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;














public final class DecimalFormatSymbols
  implements Cloneable, Serializable
{
  private char zeroDigit;
  private char groupingSeparator;
  private char decimalSeparator;
  private char perMill;
  private char percent;
  private char digit;
  private char patternSeparator;
  private String infinity;
  private String NaN;
  private char minusSign;
  private String currencySymbol;
  private String intlCurrencySymbol;
  private char monetarySeparator;
  private char exponential;
  private String exponentSeparator;
  private char padEscape;
  private char plusSign;
  private Locale locale;
  static final long serialVersionUID = 5772796243397350300L;
  private static final int currentSerialVersion = 3;
  
  public DecimalFormatSymbols()
  {
    initialize(Locale.getDefault());
  }
  




  public DecimalFormatSymbols(Locale locale)
  {
    initialize(locale);
  }
  




  public char getZeroDigit()
  {
    return zeroDigit;
  }
  




  public void setZeroDigit(char zeroDigit)
  {
    this.zeroDigit = zeroDigit;
  }
  




  public char getGroupingSeparator()
  {
    return groupingSeparator;
  }
  




  public void setGroupingSeparator(char groupingSeparator)
  {
    this.groupingSeparator = groupingSeparator;
  }
  




  public char getDecimalSeparator()
  {
    return decimalSeparator;
  }
  




  public void setDecimalSeparator(char decimalSeparator)
  {
    this.decimalSeparator = decimalSeparator;
  }
  




  public char getPerMill()
  {
    return perMill;
  }
  




  public void setPerMill(char perMill)
  {
    this.perMill = perMill;
  }
  




  public char getPercent()
  {
    return percent;
  }
  




  public void setPercent(char percent)
  {
    this.percent = percent;
  }
  




  public char getDigit()
  {
    return digit;
  }
  




  public void setDigit(char digit)
  {
    this.digit = digit;
  }
  





  public char getPatternSeparator()
  {
    return patternSeparator;
  }
  





  public void setPatternSeparator(char patternSeparator)
  {
    this.patternSeparator = patternSeparator;
  }
  







  public String getInfinity()
  {
    return infinity;
  }
  





  public void setInfinity(String infinity)
  {
    this.infinity = infinity;
  }
  






  public String getNaN()
  {
    return NaN;
  }
  





  public void setNaN(String NaN)
  {
    this.NaN = NaN;
  }
  






  public char getMinusSign()
  {
    return minusSign;
  }
  






  public void setMinusSign(char minusSign)
  {
    this.minusSign = minusSign;
  }
  





  public String getCurrencySymbol()
  {
    return currencySymbol;
  }
  





  public void setCurrencySymbol(String currency)
  {
    currencySymbol = currency;
  }
  





  public String getInternationalCurrencySymbol()
  {
    return intlCurrencySymbol;
  }
  





  public void setInternationalCurrencySymbol(String currency)
  {
    intlCurrencySymbol = currency;
  }
  





  public char getMonetaryDecimalSeparator()
  {
    return monetarySeparator;
  }
  





  public void setMonetaryDecimalSeparator(char sep)
  {
    monetarySeparator = sep;
  }
  









  public String getExponentSeparator()
  {
    return exponentSeparator;
  }
  









  public void setExponentSeparator(String exp)
  {
    exponentSeparator = exp;
  }
  









  public char getPlusSign()
  {
    return plusSign;
  }
  









  public void setPlusSign(char plus)
  {
    plusSign = plus;
  }
  












  public char getPadEscape()
  {
    return padEscape;
  }
  











  public void setPadEscape(char c)
  {
    padEscape = c;
  }
  




  public Locale getLocale()
  {
    return locale;
  }
  


  public Object clone()
  {
    try
    {
      return (DecimalFormatSymbols)super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  



  public boolean equals(Object obj)
  {
    if (obj == null) return false;
    if (this == obj) return true;
    if (getClass() != obj.getClass()) return false;
    DecimalFormatSymbols other = (DecimalFormatSymbols)obj;
    return (zeroDigit == zeroDigit) && (groupingSeparator == groupingSeparator) && (decimalSeparator == decimalSeparator) && (percent == percent) && (perMill == perMill) && (digit == digit) && (minusSign == minusSign) && (patternSeparator == patternSeparator) && (infinity.equals(infinity)) && (NaN.equals(NaN)) && (currencySymbol.equals(currencySymbol)) && (intlCurrencySymbol.equals(intlCurrencySymbol)) && (padEscape == padEscape) && (plusSign == plusSign) && (exponentSeparator.equals(exponentSeparator)) && (monetarySeparator == monetarySeparator);
  }
  


















  public int hashCode()
  {
    int result = zeroDigit;
    result = result * 37 + groupingSeparator;
    result = result * 37 + decimalSeparator;
    return result;
  }
  




  private void initialize(Locale locale)
  {
    this.locale = locale;
    
    String[][] data = (String[][])cachedLocaleData.get(locale);
    
    if (data == null) {
      data = new String[1][];
      ResourceBundle rb = ICULocaleData.getLocaleElements(locale);
      data[0] = rb.getStringArray("NumberElements");
      
      cachedLocaleData.put(locale, data);
    }
    String[] numberElements = data[0];
    

    decimalSeparator = numberElements[0].charAt(0);
    groupingSeparator = numberElements[1].charAt(0);
    
    patternSeparator = (numberElements[2].length() > 0 ? numberElements[2].charAt(0) : ';');
    

    percent = numberElements[3].charAt(0);
    zeroDigit = numberElements[4].charAt(0);
    digit = numberElements[5].charAt(0);
    minusSign = numberElements[6].charAt(0);
    

    exponentSeparator = (numberElements.length >= 9 ? numberElements[7] : "E");
    
    perMill = (numberElements.length >= 9 ? numberElements[8].charAt(0) : '‰');
    
    infinity = (numberElements.length >= 10 ? numberElements[9] : "∞");
    
    NaN = (numberElements.length >= 11 ? numberElements[10] : "�");
    


    plusSign = '+';
    padEscape = '*';
    



    String currname = null;
    Currency curr = Currency.getInstance(locale);
    if (curr != null) {
      intlCurrencySymbol = curr.getCurrencyCode();
      boolean[] isChoiceFormat = new boolean[1];
      currname = curr.getName(locale, 0, isChoiceFormat);
      



      currencySymbol = (isChoiceFormat[0] != 0 ? new ChoiceFormat(currname).format(2.0D) : currname);
    }
    else
    {
      intlCurrencySymbol = "XXX";
      currencySymbol = "¤";
    }
    
    monetarySeparator = numberElements[0].charAt(0);
  }
  









  private void readObject(ObjectInputStream stream)
    throws IOException, ClassNotFoundException
  {
    stream.defaultReadObject();
    if (serialVersionOnStream < 1)
    {

      monetarySeparator = decimalSeparator;
      exponential = 'E';
    }
    if (serialVersionOnStream < 2) {
      padEscape = '*';
      plusSign = '+';
      exponentSeparator = String.valueOf(exponential);
    }
    



    if (serialVersionOnStream < 3)
    {




      locale = Locale.getDefault();
    }
    serialVersionOnStream = 3;
  }
  













































































































































































  private int serialVersionOnStream = 3;
  



  private static final Hashtable cachedLocaleData = new Hashtable(3);
}
