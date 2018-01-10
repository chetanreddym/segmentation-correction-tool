package com.ibm.icu.text;

import java.text.ParsePosition;












































abstract class NFSubstitution
{
  private static final String copyrightNotice = "Copyright ©1997-1998 IBM Corp.  All rights reserved.";
  int pos;
  NFRuleSet ruleSet = null;
  




  DecimalFormat numberFormat = null;
  



























  public static NFSubstitution makeSubstitution(int pos, NFRule rule, NFRule rulePredecessor, NFRuleSet ruleSet, RuleBasedNumberFormat formatter, String description)
  {
    if (description.length() == 0) {
      return new NullSubstitution(pos, ruleSet, formatter, description);
    }
    
    switch (description.charAt(0))
    {


    case '<': 
      if (rule.getBaseValue() == -1L) {
        throw new IllegalArgumentException("<< not allowed in negative-number rule");
      }
      


      if ((rule.getBaseValue() == -2L) || (rule.getBaseValue() == -3L) || (rule.getBaseValue() == -4L))
      {

        return new IntegralPartSubstitution(pos, ruleSet, formatter, description);
      }
      


      if (ruleSet.isFractionSet()) {
        return new NumeratorSubstitution(pos, rule.getBaseValue(), formatter.getDefaultRuleSet(), formatter, description);
      }
      



      return new MultiplierSubstitution(pos, rule.getDivisor(), ruleSet, formatter, description);
    





    case '>': 
      if (rule.getBaseValue() == -1L) {
        return new AbsoluteValueSubstitution(pos, ruleSet, formatter, description);
      }
      


      if ((rule.getBaseValue() == -2L) || (rule.getBaseValue() == -3L) || (rule.getBaseValue() == -4L))
      {

        return new FractionalPartSubstitution(pos, ruleSet, formatter, description);
      }
      


      if (ruleSet.isFractionSet()) {
        throw new IllegalArgumentException(">> not allowed in fraction rule set");
      }
      


      return new ModulusSubstitution(pos, rule.getDivisor(), rulePredecessor, ruleSet, formatter, description);
    




    case '=': 
      return new SameValueSubstitution(pos, ruleSet, formatter, description);
    }
    
    
    throw new IllegalArgumentException("Illegal substitution character");
  }
  














  NFSubstitution(int pos, NFRuleSet ruleSet, RuleBasedNumberFormat formatter, String description)
  {
    this.pos = pos;
    




    if ((description.length() >= 2) && (description.charAt(0) == description.charAt(description.length() - 1)))
    {
      description = description.substring(1, description.length() - 1);
    }
    else if (description.length() != 0) {
      throw new IllegalArgumentException("Illegal substitution syntax");
    }
    



    if (description.length() == 0) {
      this.ruleSet = ruleSet;




    }
    else if (description.charAt(0) == '%') {
      this.ruleSet = formatter.findRuleSet(description);





    }
    else if ((description.charAt(0) == '#') || (description.charAt(0) == '0')) {
      numberFormat = new DecimalFormat(description);
      numberFormat.setDecimalFormatSymbols(formatter.getDecimalFormatSymbols());






    }
    else if (description.charAt(0) == '>') {
      this.ruleSet = ruleSet;
      numberFormat = null;

    }
    else
    {
      throw new IllegalArgumentException("Illegal substitution syntax");
    }
  }
  










  public void setDivisor(int radix, int exponent) {}
  









  public boolean equals(Object that)
  {
    if (getClass() == that.getClass()) {
      NFSubstitution that2 = (NFSubstitution)that;
      
      if (pos == pos) if ((ruleSet == null ? 0 : ruleSet == null ? 1 : 1) == 0) {} return (numberFormat == null ? false : numberFormat == null ? true : numberFormat.equals(numberFormat));
    }
    

    return false;
  }
  









  public String toString()
  {
    if (ruleSet != null) {
      return tokenChar() + ruleSet.getName() + tokenChar();
    }
    return tokenChar() + numberFormat.toPattern() + tokenChar();
  }
  














  public void doSubstitution(long number, StringBuffer toInsertInto, int pos)
  {
    if (ruleSet != null)
    {


      long numberToFormat = transformNumber(number);
      
      ruleSet.format(numberToFormat, toInsertInto, pos + this.pos);

    }
    else
    {

      double numberToFormat = transformNumber(number);
      if (numberFormat.getMaximumFractionDigits() == 0) {
        numberToFormat = Math.floor(numberToFormat);
      }
      
      toInsertInto.insert(pos + this.pos, numberFormat.format(numberToFormat));
    }
  }
  











  public void doSubstitution(double number, StringBuffer toInsertInto, int pos)
  {
    double numberToFormat = transformNumber(number);
    


    if ((numberToFormat == Math.floor(numberToFormat)) && (ruleSet != null)) {
      ruleSet.format(numberToFormat, toInsertInto, pos + this.pos);




    }
    else if (ruleSet != null) {
      ruleSet.format(numberToFormat, toInsertInto, pos + this.pos);
    } else {
      toInsertInto.insert(pos + this.pos, numberFormat.format(numberToFormat));
    }
  }
  




















  public abstract long transformNumber(long paramLong);
  




















  public abstract double transformNumber(double paramDouble);
  




















  public Number doParse(String text, ParsePosition parsePosition, double baseValue, double upperBound, boolean lenientParse)
  {
    upperBound = calcUpperBound(upperBound);
    


    Number tempResult;
    


    if (ruleSet != null) {
      tempResult = ruleSet.parse(text, parsePosition, upperBound);
      if ((lenientParse) && (!ruleSet.isFractionSet()) && (parsePosition.getIndex() == 0)) {
        tempResult = NumberFormat.getInstance().parse(text, parsePosition);
      }
    }
    else
    {
      tempResult = numberFormat.parse(text, parsePosition);
    }
    




    if (parsePosition.getIndex() != 0) {
      double result = tempResult.doubleValue();
      






















      result = composeRuleValue(result, baseValue);
      if (result == result) {
        return new Long(result);
      }
      return new Double(result);
    }
    


    return tempResult;
  }
  









  public abstract double composeRuleValue(double paramDouble1, double paramDouble2);
  









  public abstract double calcUpperBound(double paramDouble);
  









  public final int getPos()
  {
    return pos;
  }
  





  abstract char tokenChar();
  





  public boolean isNullSubstitution()
  {
    return false;
  }
  





  public boolean isModulusSubstitution()
  {
    return false;
  }
}
