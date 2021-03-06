package com.ibm.icu.text;

import java.text.ParsePosition;































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































class NumeratorSubstitution
  extends NFSubstitution
{
  private static final String copyrightNotice = "Copyright ©1997-1998 IBM Corp.  All rights reserved.";
  double denominator;
  
  NumeratorSubstitution(int pos, double denominator, NFRuleSet ruleSet, RuleBasedNumberFormat formatter, String description)
  {
    super(pos, ruleSet, formatter, description);
    



    this.denominator = denominator;
  }
  








  public boolean equals(Object that)
  {
    if (super.equals(that)) {
      NumeratorSubstitution that2 = (NumeratorSubstitution)that;
      return denominator == denominator;
    }
    return false;
  }
  









  public long transformNumber(long number)
  {
    return Math.round(number * denominator);
  }
  




  public double transformNumber(double number)
  {
    return Math.round(number * denominator);
  }
  











  public Number doParse(String text, ParsePosition parsePosition, double baseValue, double upperBound, boolean lenientParse)
  {
    return super.doParse(text, parsePosition, baseValue, upperBound, false);
  }
  






  public double composeRuleValue(double newRuleValue, double oldRuleValue)
  {
    return newRuleValue / oldRuleValue;
  }
  




  public double calcUpperBound(double oldUpperBound)
  {
    return denominator;
  }
  







  char tokenChar()
  {
    return '<';
  }
}
