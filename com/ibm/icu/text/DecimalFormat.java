package com.ibm.icu.text;

import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.util.Currency;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.text.ChoiceFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Locale;















































































































































































































































































































































































































































































public class DecimalFormat
  extends NumberFormat
{
  private static final int STATUS_INFINITE = 0;
  private static final int STATUS_POSITIVE = 1;
  private static final int STATUS_LENGTH = 2;
  
  public DecimalFormat()
  {
    Locale def = Locale.getDefault();
    String pattern = NumberFormat.getPattern(def, 0);
    
    symbols = new DecimalFormatSymbols(def);
    setCurrency(Currency.getInstance(def));
    applyPattern(pattern, false);
  }
  


















  public DecimalFormat(String pattern)
  {
    Locale def = Locale.getDefault();
    symbols = new DecimalFormatSymbols(def);
    setCurrency(Currency.getInstance(def));
    applyPattern(pattern, false);
  }
  





















  public DecimalFormat(String pattern, DecimalFormatSymbols symbols)
  {
    this.symbols = ((DecimalFormatSymbols)symbols.clone());
    setCurrencyForSymbols();
    applyPattern(pattern, false);
  }
  





  public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition)
  {
    fieldPosition.setBeginIndex(0);
    fieldPosition.setEndIndex(0);
    
    if (Double.isNaN(number))
    {
      if (fieldPosition.getField() == 0) {
        fieldPosition.setBeginIndex(result.length());
      }
      
      result.append(symbols.getNaN());
      
      if (fieldPosition.getField() == 0) {
        fieldPosition.setEndIndex(result.length());
      }
      
      addPadding(result, fieldPosition, 0, 0);
      return result;
    }
    










    boolean isNegative = (number < 0.0D) || ((number == 0.0D) && (1.0D / number < 0.0D));
    if (isNegative) { number = -number;
    }
    
    if (multiplier != 1) { number *= multiplier;
    }
    
    if (roundingDouble > 0.0D)
    {

      number = round(number, roundingDouble, roundingMode, isNegative);
    }
    int suffixLen;
    if (Double.isInfinite(number))
    {
      ??? = appendAffix(result, isNegative, true);
      
      if (fieldPosition.getField() == 0) {
        fieldPosition.setBeginIndex(result.length());
      }
      
      result.append(symbols.getInfinity());
      
      if (fieldPosition.getField() == 0) {
        fieldPosition.setEndIndex(result.length());
      }
      
      suffixLen = appendAffix(result, isNegative, false);
      
      addPadding(result, fieldPosition, ???, suffixLen);
      return result;
    }
    


    synchronized (digitList) {
      digitList.set(number, useExponentialNotation ? getMinimumIntegerDigits() + getMaximumFractionDigits() : getMaximumFractionDigits(), !useExponentialNotation);
      



      suffixLen = subformat(result, fieldPosition, isNegative, false);return suffixLen;
    }
  }
  















  private static double round(double number, double roundingInc, int mode, boolean isNegative)
  {
    double div = number / roundingInc;
    switch (mode) {
    case 2: 
      return (isNegative ? Math.floor(div) : Math.ceil(div)) * roundingInc;
    
    case 3: 
      return (isNegative ? Math.ceil(div) : Math.floor(div)) * roundingInc;
    
    case 1: 
      return Math.floor(div) * roundingInc;
    case 0: 
      return Math.ceil(div) * roundingInc;
    case 7: 
      if (div != Math.floor(div)) {
        throw new ArithmeticException("Rounding necessary");
      }
      return number;
    }
    
    
    double ceil = Math.ceil(div);
    double ceildiff = ceil * roundingInc - number;
    double floor = Math.floor(div);
    double floordiff = number - floor * roundingInc;
    switch (mode)
    {

    case 6: 
      if (ceildiff != floordiff) {
        return Math.rint(div) * roundingInc;
      }
      floor /= 2.0D;
      return (floor == Math.floor(floor) ? Math.floor(div) : Math.floor(div) + 1.0D) * roundingInc;
    

    case 5: 
      return (floordiff <= ceildiff ? floor : ceil) * roundingInc;
    case 4: 
      return (ceildiff <= floordiff ? ceil : floor) * roundingInc; }
    
    throw new IllegalArgumentException("Invalid rounding mode: " + mode);
  }
  





  public StringBuffer format(long number, StringBuffer result, FieldPosition fieldPosition)
  {
    fieldPosition.setBeginIndex(0);
    fieldPosition.setEndIndex(0);
    



    if (roundingIncrement != null) {
      return format(java.math.BigDecimal.valueOf(number), result, fieldPosition);
    }
    
    boolean isNegative = number < 0L;
    if (isNegative) { number = -number;
    }
    


    long cutoff;
    


    if (multiplier != 1) {
      ??? = false;
      if (number < 0L) {
        long cutoff = Long.MIN_VALUE / multiplier;
        ??? = number < cutoff;
      } else {
        cutoff = Long.MAX_VALUE / multiplier;
        ??? = number > cutoff;
      }
      if (??? != 0) {
        return format(BigInteger.valueOf(isNegative ? -number : number), result, fieldPosition);
      }
    }
    

    number *= multiplier;
    synchronized (digitList) {
      digitList.set(number, useExponentialNotation ? getMinimumIntegerDigits() + getMaximumFractionDigits() : 0);
      

      cutoff = subformat(result, fieldPosition, isNegative, true);return cutoff;
    }
  }
  







  public StringBuffer format(BigInteger number, StringBuffer result, FieldPosition fieldPosition)
  {
    if (roundingIncrement != null) {
      return format(new java.math.BigDecimal(number), result, fieldPosition);
    }
    
    if (multiplier != 1) {
      number = number.multiply(BigInteger.valueOf(multiplier));
    }
    


    synchronized (digitList) {
      digitList.set(number, useExponentialNotation ? getMinimumIntegerDigits() + getMaximumFractionDigits() : 0);
      

      StringBuffer localStringBuffer = subformat(result, fieldPosition, number.signum() < 0, false);return localStringBuffer;
    }
  }
  





  public StringBuffer format(java.math.BigDecimal number, StringBuffer result, FieldPosition fieldPosition)
  {
    if (multiplier != 1) {
      number = number.multiply(java.math.BigDecimal.valueOf(multiplier));
    }
    
    if (roundingIncrement != null) {
      number = number.divide(roundingIncrement, 0, roundingMode).multiply(roundingIncrement);
    }
    



    synchronized (digitList) {
      digitList.set(number, useExponentialNotation ? getMinimumIntegerDigits() + getMaximumFractionDigits() : getMaximumFractionDigits(), !useExponentialNotation);
      


      StringBuffer localStringBuffer = subformat(result, fieldPosition, number.signum() < 0, false);return localStringBuffer;
    }
  }
  












  public StringBuffer format(com.ibm.icu.math.BigDecimal number, StringBuffer result, FieldPosition fieldPosition)
  {
    if (multiplier != 1) {
      number = number.multiply(com.ibm.icu.math.BigDecimal.valueOf(multiplier));
    }
    
    if (roundingIncrement != null) {
      ??? = new com.ibm.icu.math.BigDecimal(roundingIncrement);
      number = number.divide(???, 0, roundingMode).multiply(???);
    }
    



    synchronized (digitList) {
      digitList.set(number, useExponentialNotation ? getMinimumIntegerDigits() + getMaximumFractionDigits() : getMaximumFractionDigits(), !useExponentialNotation);
      


      StringBuffer localStringBuffer = subformat(result, fieldPosition, number.signum() < 0, false);return localStringBuffer;
    }
  }
  









  private boolean isGroupingPosition(int pos)
  {
    boolean result = false;
    if ((isGroupingUsed()) && (pos > 0) && (groupingSize > 0)) {
      if ((groupingSize2 > 0) && (pos > groupingSize)) {
        result = (pos - groupingSize) % groupingSize2 == 0;
      } else {
        result = pos % groupingSize == 0;
      }
    }
    return result;
  }
  




















  private StringBuffer subformat(StringBuffer result, FieldPosition fieldPosition, boolean isNegative, boolean isInteger)
  {
    char zero = symbols.getZeroDigit();
    int zeroDelta = zero - '0';
    char grouping = symbols.getGroupingSeparator();
    char decimal = isCurrencyFormat ? symbols.getMonetaryDecimalSeparator() : symbols.getDecimalSeparator();
    

    int maxIntDig = getMaximumIntegerDigits();
    int minIntDig = getMinimumIntegerDigits();
    






    if (digitList.isZero())
    {
      digitList.decimalAt = 0;
    }
    
    int prefixLen = appendAffix(result, isNegative, true);
    int i;
    if (useExponentialNotation)
    {

      if (fieldPosition.getField() == 0) {
        fieldPosition.setBeginIndex(result.length());
        fieldPosition.setEndIndex(-1);
      } else if (fieldPosition.getField() == 1) {
        fieldPosition.setBeginIndex(-1);
      }
      












      int exponent = digitList.decimalAt;
      if ((maxIntDig > 1) && (maxIntDig != minIntDig))
      {
        exponent = exponent > 0 ? (exponent - 1) / maxIntDig : exponent / maxIntDig - 1;
        
        exponent *= maxIntDig;
      }
      else
      {
        exponent -= ((minIntDig > 0) || (getMinimumFractionDigits() > 0) ? minIntDig : 1);
      }
      





      int minimumDigits = minIntDig + getMinimumFractionDigits();
      


      int integerDigits = digitList.isZero() ? minIntDig : digitList.decimalAt - exponent;
      
      int totalDigits = digitList.count;
      if (minimumDigits > totalDigits) totalDigits = minimumDigits;
      if (integerDigits > totalDigits) { totalDigits = integerDigits;
      }
      for (i = 0; i < totalDigits; i++)
      {
        if (i == integerDigits)
        {

          if (fieldPosition.getField() == 0) {
            fieldPosition.setEndIndex(result.length());
          }
          
          result.append(decimal);
          

          if (fieldPosition.getField() == 1) {
            fieldPosition.setBeginIndex(result.length());
          }
        }
        result.append(i < digitList.count ? (char)(digitList.digits[i] + zeroDelta) : zero);
      }
      



      if ((digitList.isZero()) && (totalDigits == 0)) {
        result.append(zero);
      }
      

      if (fieldPosition.getField() == 0) {
        if (fieldPosition.getEndIndex() < 0) {
          fieldPosition.setEndIndex(result.length());
        }
      } else if (fieldPosition.getField() == 1) {
        if (fieldPosition.getBeginIndex() < 0) {
          fieldPosition.setBeginIndex(result.length());
        }
        fieldPosition.setEndIndex(result.length());
      }
      




      result.append(symbols.getExponentSeparator());
      



      if (digitList.isZero()) { exponent = 0;
      }
      boolean negativeExponent = exponent < 0;
      if (negativeExponent) {
        exponent = -exponent;
        result.append(symbols.getMinusSign());
      } else if (exponentSignAlwaysShown) {
        result.append(symbols.getPlusSign());
      }
      digitList.set(exponent);
      for (i = digitList.decimalAt; i < minExponentDigits; i++) result.append(zero);
      for (i = 0; i < digitList.decimalAt; i++)
      {
        result.append(i < digitList.count ? (char)(digitList.digits[i] + zeroDelta) : zero);
      }
      

    }
    else
    {
      if (fieldPosition.getField() == 0) {
        fieldPosition.setBeginIndex(result.length());
      }
      




      int count = minIntDig;
      int digitIndex = 0;
      if ((digitList.decimalAt > 0) && (count < digitList.decimalAt)) {
        count = digitList.decimalAt;
      }
      




      if (count > maxIntDig)
      {
        count = maxIntDig;
        digitIndex = digitList.decimalAt - count;
      }
      
      int sizeBeforeIntegerPart = result.length();
      for (i = count - 1; i >= 0; i--)
      {
        if ((i < digitList.decimalAt) && (digitIndex < digitList.count))
        {

          result.append((char)(digitList.digits[(digitIndex++)] + zeroDelta));

        }
        else
        {
          result.append(zero);
        }
        

        if (isGroupingPosition(i)) {
          result.append(grouping);
        }
      }
      

      if (fieldPosition.getField() == 0) {
        fieldPosition.setEndIndex(result.length());
      }
      


      boolean fractionPresent = (getMinimumFractionDigits() > 0) || ((!isInteger) && (digitIndex < digitList.count));
      




      if ((!fractionPresent) && (result.length() == sizeBeforeIntegerPart)) {
        result.append(zero);
      }
      
      if ((decimalSeparatorAlwaysShown) || (fractionPresent)) {
        result.append(decimal);
      }
      
      if (fieldPosition.getField() == 1) {
        fieldPosition.setBeginIndex(result.length());
      }
      
      for (i = 0; i < getMaximumFractionDigits(); i++)
      {





        if ((i >= getMinimumFractionDigits()) && ((isInteger) || (digitIndex >= digitList.count))) {
          break;
        }
        



        if (-1 - i > digitList.decimalAt - 1)
        {
          result.append(zero);




        }
        else if ((!isInteger) && (digitIndex < digitList.count))
        {
          result.append((char)(digitList.digits[(digitIndex++)] + zeroDelta));
        }
        else
        {
          result.append(zero);
        }
      }
      

      if (fieldPosition.getField() == 1) {
        fieldPosition.setEndIndex(result.length());
      }
    }
    
    int suffixLen = appendAffix(result, isNegative, false);
    

    addPadding(result, fieldPosition, prefixLen, suffixLen);
    return result;
  }
  

  private final void addPadding(StringBuffer result, FieldPosition fieldPosition, int prefixLen, int suffixLen)
  {
    if (formatWidth > 0) {
      int len = formatWidth - result.length();
      if (len > 0) {
        char[] padding = new char[len];
        for (int i = 0; i < len; i++) {
          padding[i] = pad;
        }
        switch (padPosition) {
        case 1: 
          result.insert(prefixLen, padding);
          break;
        case 0: 
          result.insert(0, padding);
          break;
        case 2: 
          result.insert(result.length() - suffixLen, padding);
          break;
        case 3: 
          result.append(padding);
        }
        
        if ((padPosition == 0) || (padPosition == 1))
        {
          fieldPosition.setBeginIndex(fieldPosition.getBeginIndex() + len);
          fieldPosition.setEndIndex(fieldPosition.getEndIndex() + len);
        }
      }
    }
  }
  









  public Number parse(String text, ParsePosition parsePosition)
  {
    int backup;
    







    int i = backup = parsePosition.getIndex();
    



    if ((formatWidth > 0) && ((padPosition == 0) || (padPosition == 1)))
    {
      i = skipPadding(text, i);
    }
    if (text.regionMatches(i, symbols.getNaN(), 0, symbols.getNaN().length()))
    {
      i += symbols.getNaN().length();
      
      if ((formatWidth > 0) && ((padPosition == 2) || (padPosition == 3)))
      {
        i = skipPadding(text, i);
      }
      parsePosition.setIndex(i);
      return new Double(NaN.0D);
    }
    

    i = backup;
    
    boolean[] status = new boolean[2];
    if (!subparse(text, parsePosition, digitList, false, status)) {
      parsePosition.setIndex(backup);
      return null;
    }
    

    if (status[0] != 0) {
      return new Double(status[1] != 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY);
    }
    



    if ((status[1] == 0) && (digitList.isZero())) {
      return new Double(-0.0D);
    }
    


    int mult = multiplier;
    while (mult % 10 == 0) {
      digitList.decimalAt -= 1;
      mult /= 10;
    }
    

    if ((mult == 1) && (digitList.isIntegral())) {
      BigInteger n = digitList.getBigInteger(status[1]);
      return n.bitLength() < 64 ? new Long(n.longValue()) : n;
    }
    



    java.math.BigDecimal n = digitList.getBigDecimal(status[1]);
    if (mult != 1) {
      n = n.divide(java.math.BigDecimal.valueOf(mult), 6);
    }
    
    return n;
  }
  



















  private final boolean subparse(String text, ParsePosition parsePosition, DigitList digits, boolean isExponent, boolean[] status)
  {
    int position = parsePosition.getIndex();
    int oldStart = parsePosition.getIndex();
    

    if ((formatWidth > 0) && (padPosition == 0)) {
      position = skipPadding(text, position);
    }
    

    int posMatch = compareAffix(text, position, false, true);
    int negMatch = compareAffix(text, position, true, true);
    if ((posMatch >= 0) && (negMatch >= 0)) {
      if (posMatch > negMatch) {
        negMatch = -1;
      } else if (negMatch > posMatch) {
        posMatch = -1;
      }
    }
    if (posMatch >= 0) {
      position += posMatch;
    } else if (negMatch >= 0) {
      position += negMatch;
    } else {
      parsePosition.setErrorIndex(position);
      return false;
    }
    

    if ((formatWidth > 0) && (padPosition == 1)) {
      position = skipPadding(text, position);
    }
    

    status[0] = false;
    if ((!isExponent) && (text.regionMatches(position, symbols.getInfinity(), 0, symbols.getInfinity().length())))
    {

      position += symbols.getInfinity().length();
      status[0] = true;



    }
    else
    {


      decimalAt = (digits.count = 0);
      char zero = symbols.getZeroDigit();
      char decimal = isCurrencyFormat ? symbols.getMonetaryDecimalSeparator() : symbols.getDecimalSeparator();
      
      char grouping = symbols.getGroupingSeparator();
      String exponentSep = symbols.getExponentSeparator();
      boolean sawDecimal = false;
      boolean sawExponent = false;
      boolean sawDigit = false;
      int exponent = 0;
      int digit = 0;
      


      int digitCount = 0;
      
      int backup = -1;
      for (; position < text.length(); position++)
      {
        char ch = text.charAt(position);
        











        digit = ch - zero;
        if ((digit < 0) || (digit > 9)) { digit = Character.digit(ch, 10);
        }
        if (digit == 0)
        {

          backup = -1;
          sawDigit = true;
          

          if (count == 0)
          {

            if (sawDecimal)
            {



              decimalAt -= 1;
            }
          }
          else {
            digitCount++;
            digits.append((char)(digit + 48));
          }
        }
        else if ((digit > 0) && (digit <= 9))
        {
          sawDigit = true;
          digitCount++;
          digits.append((char)(digit + 48));
          

          backup = -1;
        }
        else if ((!isExponent) && (ch == decimal))
        {


          if ((isParseIntegerOnly()) || (sawDecimal)) break;
          decimalAt = digitCount;
          sawDecimal = true;
        }
        else if ((!isExponent) && (ch == grouping) && (isGroupingUsed()))
        {
          if (sawDecimal) {
            break;
          }
          


          backup = position;
        } else {
          if ((isExponent) || (sawExponent) || (!text.regionMatches(position, exponentSep, 0, exponentSep.length()))) {
            break;
          }
          

          boolean negExp = false;
          int pos = position + exponentSep.length();
          if (pos < text.length()) {
            ch = text.charAt(pos);
            if (ch == symbols.getPlusSign()) {
              pos++;
            } else if (ch == symbols.getMinusSign()) {
              pos++;
              negExp = true;
            }
          }
          
          DigitList exponentDigits = new DigitList();
          count = 0;
          while (pos < text.length()) {
            digit = text.charAt(pos) - zero;
            if ((digit < 0) || (digit > 9))
            {





              digit = Character.digit(text.charAt(pos), 10);
            }
            if ((digit < 0) || (digit > 9)) break;
            exponentDigits.append((char)(digit + 48));
            pos++;
          }
          



          if (count <= 0) break;
          decimalAt = count;
          exponent = (int)exponentDigits.getLong();
          if (negExp) {
            exponent = -exponent;
          }
          position = pos;
          sawExponent = true; break;
        }
      }
      




      if (backup != -1) { position = backup;
      }
      
      if (!sawDecimal) { decimalAt = digitCount;
      }
      
      decimalAt += exponent;
      




      if ((!sawDigit) && (digitCount == 0)) {
        parsePosition.setIndex(oldStart);
        parsePosition.setErrorIndex(oldStart);
        return false;
      }
    }
    

    if ((formatWidth > 0) && (padPosition == 2)) {
      position = skipPadding(text, position);
    }
    

    if (posMatch >= 0) {
      posMatch = compareAffix(text, position, false, false);
    }
    if (negMatch >= 0) {
      negMatch = compareAffix(text, position, true, false);
    }
    if ((posMatch >= 0) && (negMatch >= 0)) {
      if (posMatch > negMatch) {
        negMatch = -1;
      } else if (negMatch > posMatch) {
        posMatch = -1;
      }
    }
    

    if ((posMatch >= 0 ? 1 : 0) == (negMatch >= 0 ? 1 : 0)) {
      parsePosition.setErrorIndex(position);
      return false;
    }
    
    position += (posMatch >= 0 ? posMatch : negMatch);
    

    if ((formatWidth > 0) && (padPosition == 3)) {
      position = skipPadding(text, position);
    }
    
    parsePosition.setIndex(position);
    
    status[1] = (posMatch >= 0 ? 1 : false);
    
    if (parsePosition.getIndex() == oldStart) {
      parsePosition.setErrorIndex(position);
      return false;
    }
    return true;
  }
  




  private final int skipPadding(String text, int position)
  {
    while ((position < text.length()) && (text.charAt(position) == pad)) {
      position++;
    }
    return position;
  }
  











  private int compareAffix(String text, int pos, boolean isNegative, boolean isPrefix)
  {
    if (currencyChoice != null) {
      if (isPrefix) {
        return compareComplexAffix(isNegative ? negPrefixPattern : posPrefixPattern, text, pos);
      }
      
      return compareComplexAffix(isNegative ? negSuffixPattern : posSuffixPattern, text, pos);
    }
    


    if (isPrefix) {
      return compareSimpleAffix(isNegative ? negativePrefix : positivePrefix, text, pos);
    }
    
    return compareSimpleAffix(isNegative ? negativeSuffix : positiveSuffix, text, pos);
  }
  











  private static int compareSimpleAffix(String affix, String input, int pos)
  {
    int start = pos;
    for (int i = 0; i < affix.length();) {
      int c = UTF16.charAt(affix, i);
      int len = UTF16.getCharCount(c);
      if (UCharacterProperty.isRuleWhiteSpace(c))
      {





        boolean literalMatch = false;
        
        while ((pos < input.length()) && (UTF16.charAt(input, pos) == c)) {
          literalMatch = true;
          i += len;
          pos += len;
          if (i == affix.length()) {
            break;
          }
          c = UTF16.charAt(affix, i);
          len = UTF16.getCharCount(c);
          if (!UCharacterProperty.isRuleWhiteSpace(c)) {
            break;
          }
        }
        

        i = skipRuleWhiteSpace(affix, i);
        



        int s = pos;
        pos = skipUWhiteSpace(input, pos);
        if ((pos == s) && (!literalMatch)) {
          return -1;
        }
      }
      else if ((pos < input.length()) && (UTF16.charAt(input, pos) == c))
      {
        i += len;
        pos += len;
      } else {
        return -1;
      }
    }
    
    return pos - start;
  }
  



  private static int skipRuleWhiteSpace(String text, int pos)
  {
    while (pos < text.length()) {
      int c = UTF16.charAt(text, pos);
      if (!UCharacterProperty.isRuleWhiteSpace(c)) {
        break;
      }
      pos += UTF16.getCharCount(c);
    }
    return pos;
  }
  



  private static int skipUWhiteSpace(String text, int pos)
  {
    while (pos < text.length()) {
      int c = UTF16.charAt(text, pos);
      if (!UCharacter.isUWhiteSpace(c)) {
        break;
      }
      pos += UTF16.getCharCount(c);
    }
    return pos;
  }
  







  private int compareComplexAffix(String affixPat, String text, int pos)
  {
    for (int i = 0; (i < affixPat.length()) && (pos >= 0);) {
      char c = affixPat.charAt(i++);
      if (c == '\'') {
        for (;;) {
          int j = affixPat.indexOf('\'', i);
          if (j == i) {
            pos = match(text, pos, 39);
            i = j + 1;
            break; }
          if (j > i) {
            pos = match(text, pos, affixPat.substring(i, j));
            i = j + 1;
            if ((i >= affixPat.length()) || (affixPat.charAt(i) != '\''))
              break;
            pos = match(text, pos, 39);
            i++;


          }
          else
          {


            throw new RuntimeException();
          }
        }
      }
      

      switch (c)
      {

      case '¤': 
        boolean intl = (i < affixPat.length()) && (affixPat.charAt(i) == '¤');
        
        if (intl) {
          i++;
          pos = match(text, pos, getCurrency().getCurrencyCode());
        } else {
          ParsePosition ppos = new ParsePosition(pos);
          currencyChoice.parse(text, ppos);
          pos = ppos.getIndex() == pos ? -1 : ppos.getIndex();
        }
        break;
      case '%': 
        c = symbols.getPercent();
        break;
      case '‰': 
        c = symbols.getPerMill();
        break;
      case '-': 
        c = symbols.getMinusSign();
      }
      
      pos = match(text, pos, c);
      if (UCharacterProperty.isRuleWhiteSpace(c)) {
        i = skipRuleWhiteSpace(affixPat, i);
      }
    }
    
    return pos;
  }
  




  static final int match(String text, int pos, int ch)
  {
    if (UCharacterProperty.isRuleWhiteSpace(ch))
    {

      int s = pos;
      pos = skipUWhiteSpace(text, pos);
      if (pos == s) {
        return -1;
      }
      return pos;
    }
    return (pos >= 0) && (UTF16.charAt(text, pos) == ch) ? pos + UTF16.getCharCount(ch) : -1;
  }
  





  static final int match(String text, int pos, String str)
  {
    for (int i = 0; (i < str.length()) && (pos >= 0);) {
      int ch = UTF16.charAt(str, i);
      i += UTF16.getCharCount(ch);
      pos = match(text, pos, ch);
      if (UCharacterProperty.isRuleWhiteSpace(ch)) {
        i = skipRuleWhiteSpace(str, i);
      }
    }
    return pos;
  }
  





  public DecimalFormatSymbols getDecimalFormatSymbols()
  {
    try
    {
      return (DecimalFormatSymbols)symbols.clone();
    } catch (Exception foo) {}
    return null;
  }
  








  public void setDecimalFormatSymbols(DecimalFormatSymbols newSymbols)
  {
    symbols = ((DecimalFormatSymbols)newSymbols.clone());
    setCurrencyForSymbols();
    expandAffixes();
  }
  
















  private void setCurrencyForSymbols()
  {
    DecimalFormatSymbols def = new DecimalFormatSymbols(symbols.getLocale());
    

    if ((symbols.getCurrencySymbol().equals(def.getCurrencySymbol())) && (symbols.getInternationalCurrencySymbol().equals(def.getInternationalCurrencySymbol())))
    {


      setCurrency(Currency.getInstance(symbols.getLocale()));
    } else {
      setCurrency(null);
    }
  }
  




  public String getPositivePrefix()
  {
    return positivePrefix;
  }
  




  public void setPositivePrefix(String newValue)
  {
    positivePrefix = newValue;
    posPrefixPattern = null;
  }
  




  public String getNegativePrefix()
  {
    return negativePrefix;
  }
  




  public void setNegativePrefix(String newValue)
  {
    negativePrefix = newValue;
    negPrefixPattern = null;
  }
  




  public String getPositiveSuffix()
  {
    return positiveSuffix;
  }
  




  public void setPositiveSuffix(String newValue)
  {
    positiveSuffix = newValue;
    posSuffixPattern = null;
  }
  




  public String getNegativeSuffix()
  {
    return negativeSuffix;
  }
  




  public void setNegativeSuffix(String newValue)
  {
    negativeSuffix = newValue;
    negSuffixPattern = null;
  }
  







  public int getMultiplier()
  {
    return multiplier;
  }
  







  public void setMultiplier(int newValue)
  {
    if (newValue <= 0) {
      throw new IllegalArgumentException("Bad multiplier: " + newValue);
    }
    multiplier = newValue;
  }
  









  public java.math.BigDecimal getRoundingIncrement()
  {
    return roundingIncrement;
  }
  











  public void setRoundingIncrement(java.math.BigDecimal newValue)
  {
    int i = newValue == null ? 0 : newValue.compareTo(java.math.BigDecimal.valueOf(0L));
    
    if (i < 0) {
      throw new IllegalArgumentException("Illegal rounding increment");
    }
    if (i == 0) {
      roundingIncrement = null;
      roundingDouble = 0.0D;
    } else {
      roundingIncrement = newValue;
      roundingDouble = newValue.doubleValue();
    }
  }
  











  public void setRoundingIncrement(double newValue)
  {
    if (newValue < 0.0D) {
      throw new IllegalArgumentException("Illegal rounding increment");
    }
    roundingDouble = newValue;
    roundingIncrement = (newValue > 0.0D ? new java.math.BigDecimal(String.valueOf(newValue)) : null);
  }
  











  public int getRoundingMode()
  {
    return roundingMode;
  }
  














  public void setRoundingMode(int roundingMode)
  {
    if ((roundingMode < 0) || (roundingMode > 7))
    {
      throw new IllegalArgumentException("Invalid rounding mode: " + roundingMode);
    }
    
    this.roundingMode = roundingMode;
  }
  










  public int getFormatWidth()
  {
    return formatWidth;
  }
  













  public void setFormatWidth(int width)
  {
    if (width < 0) {
      throw new IllegalArgumentException("Illegal format width");
    }
    formatWidth = width;
  }
  










  public char getPadCharacter()
  {
    return pad;
  }
  











  public void setPadCharacter(char padChar)
  {
    pad = padChar;
  }
  


















  public int getPadPosition()
  {
    return padPosition;
  }
  





















  public void setPadPosition(int padPos)
  {
    if ((padPos < 0) || (padPos > 3)) {
      throw new IllegalArgumentException("Illegal pad position");
    }
    padPosition = padPos;
  }
  










  public boolean isScientificNotation()
  {
    return useExponentialNotation;
  }
  











  public void setScientificNotation(boolean useScientific)
  {
    useExponentialNotation = useScientific;
    if ((useExponentialNotation) && (minExponentDigits < 1)) {
      minExponentDigits = 1;
    }
  }
  










  public byte getMinimumExponentDigits()
  {
    return minExponentDigits;
  }
  













  public void setMinimumExponentDigits(byte minExpDig)
  {
    if (minExpDig < 1) {
      throw new IllegalArgumentException("Exponent digits must be >= 1");
    }
    minExponentDigits = minExpDig;
  }
  












  public boolean isExponentSignAlwaysShown()
  {
    return exponentSignAlwaysShown;
  }
  













  public void setExponentSignAlwaysShown(boolean expSignAlways)
  {
    exponentSignAlwaysShown = expSignAlways;
  }
  








  public int getGroupingSize()
  {
    return groupingSize;
  }
  








  public void setGroupingSize(int newValue)
  {
    groupingSize = ((byte)newValue);
  }
  


















  public int getSecondaryGroupingSize()
  {
    return groupingSize2;
  }
  









  public void setSecondaryGroupingSize(int newValue)
  {
    groupingSize2 = ((byte)newValue);
  }
  





  public boolean isDecimalSeparatorAlwaysShown()
  {
    return decimalSeparatorAlwaysShown;
  }
  













  public void setDecimalSeparatorAlwaysShown(boolean newValue)
  {
    decimalSeparatorAlwaysShown = newValue;
  }
  


  public Object clone()
  {
    try
    {
      DecimalFormat other = (DecimalFormat)super.clone();
      symbols = ((DecimalFormatSymbols)symbols.clone());
      return other;
    } catch (Exception e) {
      throw new InternalError();
    }
  }
  




  public boolean equals(Object obj)
  {
    if (obj == null) return false;
    if (!super.equals(obj)) return false;
    DecimalFormat other = (DecimalFormat)obj;
    



    return ((posPrefixPattern == posPrefixPattern) && (positivePrefix.equals(positivePrefix))) || ((posPrefixPattern != null) && (posPrefixPattern.equals(posPrefixPattern)) && (((posSuffixPattern == posSuffixPattern) && (positiveSuffix.equals(positiveSuffix))) || ((posSuffixPattern != null) && (posSuffixPattern.equals(posSuffixPattern)) && (((negPrefixPattern == negPrefixPattern) && (negativePrefix.equals(negativePrefix))) || ((negPrefixPattern != null) && (negPrefixPattern.equals(negPrefixPattern)) && (((negSuffixPattern == negSuffixPattern) && (negativeSuffix.equals(negativeSuffix))) || ((negSuffixPattern != null) && (negSuffixPattern.equals(negSuffixPattern)) && (multiplier == multiplier) && (groupingSize == groupingSize) && (groupingSize2 == groupingSize2) && (decimalSeparatorAlwaysShown == decimalSeparatorAlwaysShown) && (useExponentialNotation == useExponentialNotation) && ((!useExponentialNotation) || (minExponentDigits == minExponentDigits)) && (symbols.equals(symbols)))))))));
  }
  


























  public int hashCode()
  {
    return super.hashCode() * 37 + positivePrefix.hashCode();
  }
  






  public String toPattern()
  {
    return toPattern(false);
  }
  





  public String toLocalizedPattern()
  {
    return toPattern(true);
  }
  








  private void expandAffixes()
  {
    currencyChoice = null;
    

    StringBuffer buffer = new StringBuffer();
    if (posPrefixPattern != null) {
      expandAffix(posPrefixPattern, buffer, false);
      positivePrefix = buffer.toString();
    }
    if (posSuffixPattern != null) {
      expandAffix(posSuffixPattern, buffer, false);
      positiveSuffix = buffer.toString();
    }
    if (negPrefixPattern != null) {
      expandAffix(negPrefixPattern, buffer, false);
      negativePrefix = buffer.toString();
    }
    if (negSuffixPattern != null) {
      expandAffix(negSuffixPattern, buffer, false);
      negativeSuffix = buffer.toString();
    }
  }
  































  private void expandAffix(String pattern, StringBuffer buffer, boolean doFormat)
  {
    buffer.setLength(0);
    for (int i = 0; i < pattern.length();) {
      char c = pattern.charAt(i++);
      if (c == '\'') {
        for (;;) {
          int j = pattern.indexOf('\'', i);
          if (j == i) {
            buffer.append('\'');
            i = j + 1;
            break; }
          if (j > i) {
            buffer.append(pattern.substring(i, j));
            i = j + 1;
            if ((i >= pattern.length()) || (pattern.charAt(i) != '\''))
              break;
            buffer.append('\'');
            i++;


          }
          else
          {


            throw new RuntimeException();
          }
        }
      }
      

      switch (c)
      {




      case '¤': 
        boolean intl = (i < pattern.length()) && (pattern.charAt(i) == '¤');
        
        if (intl) {
          i++;
        }
        String s = null;
        Currency currency = getCurrency();
        if (currency != null) {
          if (!intl) {
            boolean[] isChoiceFormat = new boolean[1];
            s = currency.getName(symbols.getLocale(), 0, isChoiceFormat);
            

            if (isChoiceFormat[0] != 0)
            {



              if (!doFormat)
              {



                if (currencyChoice == null) {
                  currencyChoice = new ChoiceFormat(s);
                }
                






                s = String.valueOf('¤');
              } else {
                FieldPosition pos = new FieldPosition(0);
                currencyChoice.format(digitList.getDouble(), buffer, pos);
                continue;
              }
            }
          } else {
            s = currency.getCurrencyCode();
          }
        } else {
          s = intl ? symbols.getInternationalCurrencySymbol() : symbols.getCurrencySymbol();
        }
        
        buffer.append(s);
        break;
      case '%': 
        c = symbols.getPercent();
        break;
      case '‰': 
        c = symbols.getPerMill();
        break;
      case '-': 
        c = symbols.getMinusSign();
      }
      
      buffer.append(c);
    }
  }
  






  private int appendAffix(StringBuffer buf, boolean isNegative, boolean isPrefix)
  {
    if (currencyChoice != null) {
      String affixPat = null;
      if (isPrefix) {
        affixPat = isNegative ? negPrefixPattern : posPrefixPattern;
      } else {
        affixPat = isNegative ? negSuffixPattern : posSuffixPattern;
      }
      StringBuffer affixBuf = new StringBuffer();
      expandAffix(affixPat, affixBuf, true);
      buf.append(affixBuf.toString());
      return affixBuf.length();
    }
    
    String affix = null;
    if (isPrefix) {
      affix = isNegative ? negativePrefix : positivePrefix;
    } else {
      affix = isNegative ? negativeSuffix : positiveSuffix;
    }
    buf.append(affix);
    return affix.length();
  }
  





  private void appendAffixPattern(StringBuffer buffer, boolean isNegative, boolean isPrefix, boolean localized)
  {
    String affixPat = null;
    if (isPrefix) {
      affixPat = isNegative ? negPrefixPattern : posPrefixPattern;
    } else {
      affixPat = isNegative ? negSuffixPattern : posSuffixPattern;
    }
    

    if (affixPat == null) {
      String affix = null;
      if (isPrefix) {
        affix = isNegative ? negativePrefix : positivePrefix;
      } else {
        affix = isNegative ? negativeSuffix : positiveSuffix;
      }
      
      buffer.append('\'');
      for (int i = 0; i < affix.length(); i++) {
        char ch = affix.charAt(i);
        if (ch == '\'') {
          buffer.append(ch);
        }
        buffer.append(ch);
      }
      buffer.append('\'');
      return;
    }
    
    if (!localized) {
      buffer.append(affixPat);
    }
    else {
      for (int i = 0; i < affixPat.length(); i++) {
        char ch = affixPat.charAt(i);
        switch (ch) {
        case '\'': 
          int j = affixPat.indexOf('\'', i + 1);
          if (j < 0) {
            throw new IllegalArgumentException("Malformed affix pattern: " + affixPat);
          }
          buffer.append(affixPat.substring(i, j + 1));
          i = j;
          break;
        case '‰': 
          ch = symbols.getPerMill();
          break;
        case '%': 
          ch = symbols.getPercent();
          break;
        case '-': 
          ch = symbols.getMinusSign();
        }
        
        buffer.append(ch);
      }
    }
  }
  



  private String toPattern(boolean localized)
  {
    StringBuffer result = new StringBuffer();
    char zero = localized ? symbols.getZeroDigit() : '0';
    char digit = localized ? symbols.getDigit() : '#';
    char group = localized ? symbols.getGroupingSeparator() : ',';
    

    int roundingDecimalPos = 0;
    String roundingDigits = null;
    int padPos = formatWidth > 0 ? padPosition : -1;
    String padSpec = formatWidth > 0 ? 2 + (localized ? symbols.getPadEscape() : '*') + pad : null;
    

    int i;
    
    if (roundingIncrement != null) {
      i = roundingIncrement.scale();
      roundingDigits = roundingIncrement.movePointRight(i).toString();
      roundingDecimalPos = roundingDigits.length() - i;
    }
    for (int part = 0; part < 2; part++)
    {
      if (padPos == 0) {
        result.append(padSpec);
      }
      


      appendAffixPattern(result, part != 0, true, localized);
      if (padPos == 1) {
        result.append(padSpec);
      }
      int sub0Start = result.length();
      int g = isGroupingUsed() ? Math.max(0, groupingSize) : 0;
      if ((g > 0) && (groupingSize2 > 0) && (groupingSize2 != groupingSize)) {
        g += groupingSize2;
      }
      int maxIntDig = useExponentialNotation ? getMaximumIntegerDigits() : Math.max(Math.max(g, getMinimumIntegerDigits()), roundingDecimalPos) + 1;
      

      for (i = maxIntDig; i > 0; i--) {
        if ((!useExponentialNotation) && (i < maxIntDig) && (isGroupingPosition(i)))
        {
          result.append(group);
        }
        if (roundingDigits != null) {
          int pos = roundingDecimalPos - i;
          if ((pos >= 0) && (pos < roundingDigits.length())) {
            result.append((char)(roundingDigits.charAt(pos) - '0' + zero));
            continue;
          }
        }
        result.append(i <= getMinimumIntegerDigits() ? zero : digit);
      }
      if ((getMaximumFractionDigits() > 0) || (decimalSeparatorAlwaysShown)) {
        result.append(localized ? symbols.getDecimalSeparator() : '.');
      }
      
      int pos = roundingDecimalPos;
      for (i = 0; i < getMaximumFractionDigits(); i++)
        if ((roundingDigits != null) && (pos < roundingDigits.length()))
        {
          result.append(pos < 0 ? zero : (char)(roundingDigits.charAt(pos) - '0' + zero));
          
          pos++;
        }
        else {
          result.append(i < getMinimumFractionDigits() ? zero : digit);
        }
      if (useExponentialNotation) {
        result.append(localized ? symbols.getExponentSeparator() : "E");
        
        if (exponentSignAlwaysShown) {
          result.append(localized ? symbols.getPlusSign() : '+');
        }
        
        for (i = 0; i < minExponentDigits; i++) {
          result.append(zero);
        }
      }
      if ((padSpec != null) && (!useExponentialNotation)) {
        int add = formatWidth - result.length() + sub0Start - (part == 0 ? positivePrefix.length() + positiveSuffix.length() : negativePrefix.length() + negativeSuffix.length());
        


        while (add > 0) {
          result.insert(sub0Start, digit);
          maxIntDig++;
          add--;
          


          if ((add > 1) && (isGroupingPosition(maxIntDig))) {
            result.insert(sub0Start, group);
            add--;
          }
        }
      }
      if (padPos == 2) {
        result.append(padSpec);
      }
      


      appendAffixPattern(result, part != 0, false, localized);
      if (padPos == 3) {
        result.append(padSpec);
      }
      if (part == 0) {
        if ((negativeSuffix.equals(positiveSuffix)) && (negativePrefix.equals(symbols.getMinusSign() + positivePrefix))) {
          break;
        }
        
        result.append(localized ? symbols.getPatternSeparator() : ';');
      }
    }
    

    return result.toString();
  }
  

















  public void applyPattern(String pattern)
  {
    applyPattern(pattern, false);
  }
  


















  public void applyLocalizedPattern(String pattern)
  {
    applyPattern(pattern, true);
  }
  



  private void applyPattern(String pattern, boolean localized)
  {
    char zeroDigit = '0';
    char groupingSeparator = ',';
    char decimalSeparator = '.';
    char percent = '%';
    char perMill = '‰';
    char digit = '#';
    char separator = ';';
    String exponent = "E";
    char plus = '+';
    char padEscape = '*';
    char minus = '-';
    if (localized) {
      zeroDigit = symbols.getZeroDigit();
      groupingSeparator = symbols.getGroupingSeparator();
      decimalSeparator = symbols.getDecimalSeparator();
      percent = symbols.getPercent();
      perMill = symbols.getPerMill();
      digit = symbols.getDigit();
      separator = symbols.getPatternSeparator();
      exponent = symbols.getExponentSeparator();
      plus = symbols.getPlusSign();
      padEscape = symbols.getPadEscape();
      minus = symbols.getMinusSign();
    }
    char nineDigit = (char)(zeroDigit + '\t');
    
    boolean gotNegative = false;
    
    int pos = 0;
    

    for (int part = 0; (part < 2) && (pos < pattern.length()); part++)
    {




      int subpart = 1;int sub0Start = 0;int sub0Limit = 0;int sub2Limit = 0;
      






      StringBuffer prefix = new StringBuffer();
      StringBuffer suffix = new StringBuffer();
      int decimalPos = -1;
      int multiplier = 1;
      int digitLeftCount = 0;int zeroDigitCount = 0;int digitRightCount = 0;
      byte groupingCount = -1;
      byte groupingCount2 = -1;
      int padPos = -1;
      char padChar = '\000';
      int incrementPos = -1;
      long incrementVal = 0L;
      byte expDigits = -1;
      boolean expSignAlways = false;
      boolean isCurrency = false;
      

      StringBuffer affix = prefix;
      
      int start = pos;
      for (; 
          
          pos < pattern.length(); pos++) {
        char ch = pattern.charAt(pos);
        switch (subpart)
        {








        case 0: 
          if (ch == digit) {
            if (zeroDigitCount > 0) {
              digitRightCount++;
            } else {
              digitLeftCount++;
            }
            if ((groupingCount >= 0) && (decimalPos < 0)) {
              groupingCount = (byte)(groupingCount + 1);
            }
          } else if ((ch >= zeroDigit) && (ch <= nineDigit)) {
            if (digitRightCount > 0) {
              throw new IllegalArgumentException("Unexpected '0' in pattern \"" + pattern + '"');
            }
            

            zeroDigitCount++;
            if ((groupingCount >= 0) && (decimalPos < 0)) {
              groupingCount = (byte)(groupingCount + 1);
            }
            if (ch != zeroDigit) {
              int p = digitLeftCount + zeroDigitCount + digitRightCount;
              
              if (incrementPos >= 0) {
                while (incrementPos < p) {
                  incrementVal *= 10L;
                  incrementPos++;
                }
              } else {
                incrementPos = p;
              }
              incrementVal += ch - zeroDigit;
            }
          } else if (ch == groupingSeparator)
          {




            if ((ch == '\'') && (pos + 1 < pattern.length())) {
              char after = pattern.charAt(pos + 1);
              if ((after != digit) && ((after < zeroDigit) || (after > nineDigit)))
              {


                if (after == '\'') {
                  pos++;
                }
                else {
                  if (groupingCount < 0) {
                    subpart = 3; continue;
                  }
                  
                  subpart = 2;
                  affix = suffix;
                  sub0Limit = pos--;
                  
                  continue;
                }
              }
            }
            
            if (decimalPos >= 0) {
              throw new IllegalArgumentException("Grouping separator after decimal in pattern \"" + pattern + '"');
            }
            

            groupingCount2 = groupingCount;
            groupingCount = 0;
          } else if (ch == decimalSeparator) {
            if (decimalPos >= 0) {
              throw new IllegalArgumentException("Multiple decimal separators in pattern \"" + pattern + '"');
            }
            




            decimalPos = digitLeftCount + zeroDigitCount + digitRightCount;
          } else {
            if (pattern.regionMatches(pos, exponent, 0, exponent.length())) {
              if (expDigits >= 0) {
                throw new IllegalArgumentException("Multiple exponential symbols in pattern \"" + pattern + '"');
              }
              


              if (groupingCount >= 0) {
                throw new IllegalArgumentException("Grouping separator in exponential pattern \"" + pattern + '"');
              }
              



              if ((pos + 1 < pattern.length()) && (pattern.charAt(pos + 1) == plus))
              {
                expSignAlways = true;
                pos++;
              }
              

              expDigits = 0;
              do
              {
                expDigits = (byte)(expDigits + 1);pos++;
              } while ((pos < pattern.length()) && (pattern.charAt(pos) == zeroDigit));
              


              if ((digitLeftCount + zeroDigitCount < 1) || (expDigits < 1))
              {
                throw new IllegalArgumentException("Malformed exponential pattern \"" + pattern + '"');
              }
            }
            


            subpart = 2;
            affix = suffix;
            sub0Limit = pos--; }
          break;
        




        case 1: 
        case 2: 
          if ((ch == digit) || (ch == groupingSeparator) || (ch == decimalSeparator) || ((ch >= zeroDigit) && (ch <= nineDigit)))
          {




            if (subpart == 1) {
              subpart = 0;
              sub0Start = pos--;
              continue; }
            if (ch == '\'')
            {







              if ((pos + 1 < pattern.length()) && (pattern.charAt(pos + 1) == '\''))
              {
                pos++;
              }
              else {
                subpart += 2;
                continue;
              }
            }
          }
          else if (ch == '¤')
          {

            boolean doubled = (pos + 1 < pattern.length()) && (pattern.charAt(pos + 1) == '¤');
            




            if (doubled) {
              pos++;
              affix.append(ch);
            }
            isCurrency = true;
          }
          else if (ch == '\'')
          {


            if ((pos + 1 < pattern.length()) && (pattern.charAt(pos + 1) == '\''))
            {
              pos++;
              affix.append(ch);
            } else {
              subpart += 2;
            }
          } else {
            if (ch == separator)
            {

              if ((subpart == 1) || (part == 1)) {
                throw new IllegalArgumentException("Unquoted special character '" + ch + "' in pattern \"" + pattern + '"');
              }
              


              sub2Limit = pos++;
              break label1408; }
            if ((ch == percent) || (ch == perMill))
            {
              if (multiplier != 1) {
                throw new IllegalArgumentException("Too many percent/permille characters in pattern \"" + pattern + '"');
              }
              

              multiplier = ch == percent ? 100 : 1000;
              
              ch = ch == percent ? '%' : '‰';
            }
            else if (ch == minus)
            {
              ch = '-';
            }
            else if (ch == padEscape) {
              if (padPos >= 0) {
                throw new IllegalArgumentException("Multiple pad specifiers");
              }
              
              if (pos + 1 == pattern.length()) {
                throw new IllegalArgumentException("Invalid pad specifier");
              }
              
              padPos = pos++;
              padChar = pattern.charAt(pos);
              continue;
            } }
          affix.append(ch);
          break;
        


        case 3: 
        case 4: 
          if (ch == '\'') {
            if ((pos + 1 < pattern.length()) && (pattern.charAt(pos + 1) == '\''))
            {
              pos++;
              affix.append(ch);
            } else {
              subpart -= 2;
            }
          }
          




          affix.append(ch);
        }
        
      }
      label1408:
      if ((subpart == 3) || (subpart == 4)) {
        throw new IllegalArgumentException("Unterminated quote in " + pattern);
      }
      
      if (sub0Limit == 0) {
        sub0Limit = pattern.length();
      }
      
      if (sub2Limit == 0) {
        sub2Limit = pattern.length();
      }
      













      if ((zeroDigitCount == 0) && (digitLeftCount > 0) && (decimalPos >= 0))
      {
        int n = decimalPos;
        if (n == 0) n++;
        digitRightCount = digitLeftCount - n;
        digitLeftCount = n - 1;
        zeroDigitCount = 1;
      }
      

      if (((decimalPos < 0) && (digitRightCount > 0)) || ((decimalPos >= 0) && ((decimalPos < digitLeftCount) || (decimalPos > digitLeftCount + zeroDigitCount))) || (groupingCount == 0) || (groupingCount2 == 0) || (subpart > 2))
      {




        throw new IllegalArgumentException("Malformed pattern \"" + pattern + '"');
      }
      


      if (padPos >= 0) {
        if (padPos == start) {
          padPos = 0;
        } else if (padPos + 2 == sub0Start) {
          padPos = 1;
        } else if (padPos == sub0Limit) {
          padPos = 2;
        } else if (padPos + 2 == sub2Limit) {
          padPos = 3;
        } else {
          throw new IllegalArgumentException("Illegal pad position");
        }
      }
      
      if (part == 0)
      {





        posPrefixPattern = (this.negPrefixPattern = prefix.toString());
        posSuffixPattern = (this.negSuffixPattern = suffix.toString());
        
        useExponentialNotation = (expDigits >= 0);
        if (useExponentialNotation) {
          minExponentDigits = expDigits;
          exponentSignAlwaysShown = expSignAlways;
        }
        isCurrencyFormat = isCurrency;
        int digitTotalCount = digitLeftCount + zeroDigitCount + digitRightCount;
        



        int effectiveDecimalPos = decimalPos >= 0 ? decimalPos : digitTotalCount;
        setMinimumIntegerDigits(effectiveDecimalPos - digitLeftCount);
        


        setMaximumIntegerDigits(useExponentialNotation ? digitLeftCount + getMinimumIntegerDigits() : 309);
        
        setMaximumFractionDigits(decimalPos >= 0 ? digitTotalCount - decimalPos : 0);
        
        setMinimumFractionDigits(decimalPos >= 0 ? digitLeftCount + zeroDigitCount - decimalPos : 0);
        
        setGroupingUsed(groupingCount > 0);
        groupingSize = (groupingCount > 0 ? groupingCount : 0);
        groupingSize2 = ((groupingCount2 > 0) && (groupingCount2 != groupingCount) ? groupingCount2 : 0);
        
        this.multiplier = multiplier;
        setDecimalSeparatorAlwaysShown((decimalPos == 0) || (decimalPos == digitTotalCount));
        
        if (padPos >= 0) {
          padPosition = padPos;
          formatWidth = (sub0Limit - sub0Start);
          pad = padChar;
        } else {
          formatWidth = 0;
        }
        if (incrementVal != 0L)
        {

          int scale = incrementPos - effectiveDecimalPos;
          roundingIncrement = java.math.BigDecimal.valueOf(incrementVal, scale > 0 ? scale : 0);
          
          if (scale < 0) {
            roundingIncrement = roundingIncrement.movePointRight(-scale);
          }
          
          roundingDouble = roundingIncrement.doubleValue();
          roundingMode = 6;
        } else {
          setRoundingIncrement(null);
        }
        

      }
      else
      {
        negPrefixPattern = prefix.toString();
        negSuffixPattern = suffix.toString();
        gotNegative = true;
      }
    }
    




    if (pattern.length() == 0) {
      posPrefixPattern = (this.posSuffixPattern = "");
      setMinimumIntegerDigits(0);
      setMaximumIntegerDigits(309);
      setMinimumFractionDigits(0);
      setMaximumFractionDigits(340);
    }
    







    if ((!gotNegative) || ((negPrefixPattern.equals(posPrefixPattern)) && (negSuffixPattern.equals(posSuffixPattern))))
    {

      negSuffixPattern = posSuffixPattern;
      negPrefixPattern = (symbols.getMinusSign() + posPrefixPattern);
    }
    




    expandAffixes();
    

    if (formatWidth > 0) {
      formatWidth += positivePrefix.length() + positiveSuffix.length();
    }
  }
  









  public void setMaximumIntegerDigits(int newValue)
  {
    super.setMaximumIntegerDigits(Math.min(newValue, 309));
  }
  





  public void setMinimumIntegerDigits(int newValue)
  {
    super.setMinimumIntegerDigits(Math.min(newValue, 309));
  }
  














  public void setCurrency(Currency theCurrency)
  {
    super.setCurrency(theCurrency);
    
    if (isCurrencyFormat) {
      if (theCurrency != null) {
        setRoundingIncrement(theCurrency.getRoundingIncrement());
        
        int d = theCurrency.getDefaultFractionDigits();
        setMinimumFractionDigits(d);
        setMaximumFractionDigits(d);
      }
      

      expandAffixes();
    }
  }
  





  public void setMaximumFractionDigits(int newValue)
  {
    super.setMaximumFractionDigits(Math.min(newValue, 340));
  }
  





  public void setMinimumFractionDigits(int newValue)
  {
    super.setMinimumFractionDigits(Math.min(newValue, 340));
  }
  








  private void readObject(ObjectInputStream stream)
    throws IOException, ClassNotFoundException
  {
    stream.defaultReadObject();
    













    if (getMaximumIntegerDigits() > 309) {
      setMaximumIntegerDigits(309);
    }
    if (getMaximumFractionDigits() > 340) {
      setMaximumFractionDigits(340);
    }
    if (serialVersionOnStream < 2) {
      exponentSignAlwaysShown = false;
      roundingDouble = 0.0D;
      roundingIncrement = null;
      roundingMode = 6;
      formatWidth = 0;
      pad = ' ';
      padPosition = 0;
      if (serialVersionOnStream < 1)
      {
        useExponentialNotation = false;
      }
    }
    if (serialVersionOnStream < 3)
    {

      setCurrencyForSymbols();
    }
    serialVersionOnStream = 3;
    digitList = new DigitList();
  }
  




  private transient DigitList digitList = new DigitList();
  






  private String positivePrefix = "";
  







  private String positiveSuffix = "";
  






  private String negativePrefix = "-";
  







  private String negativeSuffix = "";
  









  private String posPrefixPattern;
  









  private String posSuffixPattern;
  









  private String negPrefixPattern;
  









  private String negSuffixPattern;
  









  private ChoiceFormat currencyChoice;
  








  private int multiplier = 1;
  









  private byte groupingSize = 3;
  







  private byte groupingSize2 = 0;
  







  private boolean decimalSeparatorAlwaysShown = false;
  




  private transient boolean isCurrencyFormat = false;
  









  private DecimalFormatSymbols symbols = null;
  










  private boolean useExponentialNotation;
  










  private byte minExponentDigits;
  










  private boolean exponentSignAlwaysShown = false;
  










  private java.math.BigDecimal roundingIncrement = null;
  






  private transient double roundingDouble = 0.0D;
  










  private int roundingMode = 6;
  







  private int formatWidth = 0;
  







  private char pad = ' ';
  









  private int padPosition = 0;
  







  static final int currentSerialVersion = 3;
  







  private int serialVersionOnStream = 3;
  public static final int PAD_BEFORE_PREFIX = 0;
  public static final int PAD_AFTER_PREFIX = 1;
  public static final int PAD_BEFORE_SUFFIX = 2;
  public static final int PAD_AFTER_SUFFIX = 3;
  private static final char PATTERN_ZERO_DIGIT = '0';
  private static final char PATTERN_GROUPING_SEPARATOR = ',';
  private static final char PATTERN_DECIMAL_SEPARATOR = '.';
  private static final char PATTERN_DIGIT = '#';
  static final String PATTERN_EXPONENT = "E";
  static final char PATTERN_PLUS_SIGN = '+';
  private static final char PATTERN_PER_MILLE = '‰';
  private static final char PATTERN_PERCENT = '%';
  static final char PATTERN_PAD_ESCAPE = '*';
  private static final char PATTERN_MINUS = '-';
  private static final char PATTERN_SEPARATOR = ';';
  private static final char CURRENCY_SIGN = '¤';
  private static final char QUOTE = '\'';
  static final int DOUBLE_INTEGER_DIGITS = 309;
  static final int DOUBLE_FRACTION_DIGITS = 340;
  static final long serialVersionUID = 864413376551465018L;
}
