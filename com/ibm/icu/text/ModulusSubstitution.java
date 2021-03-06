package com.ibm.icu.text;

import java.text.ParsePosition;


































































































































































































































































































































































































































































































































































































































































































































































































































































class ModulusSubstitution
  extends NFSubstitution
{
  private static final String copyrightNotice = "Copyright ©1997-1998 IBM Corp.  All rights reserved.";
  double divisor;
  NFRule ruleToUse;
  
  ModulusSubstitution(int pos, double divisor, NFRule rulePredecessor, NFRuleSet ruleSet, RuleBasedNumberFormat formatter, String description)
  {
    super(pos, ruleSet, formatter, description);
    



    this.divisor = divisor;
    





    if (description.equals(">>>")) {
      ruleToUse = rulePredecessor;
    } else {
      ruleToUse = null;
    }
  }
  





  public void setDivisor(int radix, int exponent)
  {
    divisor = Math.pow(radix, exponent);
  }
  









  public boolean equals(Object that)
  {
    if (super.equals(that)) {
      ModulusSubstitution that2 = (ModulusSubstitution)that;
      
      return divisor == divisor;
    }
    return false;
  }
  















  public void doSubstitution(long number, StringBuffer toInsertInto, int pos)
  {
    if (ruleToUse == null) {
      super.doSubstitution(number, toInsertInto, pos);

    }
    else
    {
      long numberToFormat = transformNumber(number);
      ruleToUse.doFormat(numberToFormat, toInsertInto, pos + this.pos);
    }
  }
  










  public void doSubstitution(double number, StringBuffer toInsertInto, int pos)
  {
    if (ruleToUse == null) {
      super.doSubstitution(number, toInsertInto, pos);

    }
    else
    {
      double numberToFormat = transformNumber(number);
      
      ruleToUse.doFormat(numberToFormat, toInsertInto, pos + this.pos);
    }
  }
  





  public long transformNumber(long number)
  {
    return Math.floor(number % divisor);
  }
  





  public double transformNumber(double number)
  {
    return Math.floor(number % divisor);
  }
  















  public Number doParse(String text, ParsePosition parsePosition, double baseValue, double upperBound, boolean lenientParse)
  {
    if (ruleToUse == null) {
      return super.doParse(text, parsePosition, baseValue, upperBound, lenientParse);
    }
    



    Number tempResult = ruleToUse.doParse(text, parsePosition, false, upperBound);
    
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
  














  public double composeRuleValue(double newRuleValue, double oldRuleValue)
  {
    return oldRuleValue - oldRuleValue % divisor + newRuleValue;
  }
  




  public double calcUpperBound(double oldUpperBound)
  {
    return divisor;
  }
  







  public boolean isModulusSubstitution()
  {
    return true;
  }
  



  char tokenChar()
  {
    return '>';
  }
}
