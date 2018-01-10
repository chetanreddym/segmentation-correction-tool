package com.ibm.icu.text;

import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.impl.Utility;
import java.text.ParsePosition;
import java.util.Vector;













































final class NFRuleSet
{
  private static final String copyrightNotice = "Copyright ©1997-1998 IBM Corp.  All rights reserved.";
  private String name;
  private NFRule[] rules;
  private NFRule negativeNumberRule = null;
  





  private NFRule[] fractionRules = new NFRule[3];
  








  private boolean isFractionRuleSet = false;
  










  public NFRuleSet(String[] descriptions, int index)
    throws IllegalArgumentException
  {
    String description = descriptions[index];
    




    if (description.charAt(0) == '%') {
      int pos = description.indexOf(':');
      if (pos == -1) {
        throw new IllegalArgumentException("Rule set name doesn't end in colon");
      }
      name = description.substring(0, pos);
      while ((pos < description.length()) && (UCharacterProperty.isRuleWhiteSpace(description.charAt(++pos)))) {}
      

      description = description.substring(pos);
      descriptions[index] = description;

    }
    else
    {

      name = "%default";
    }
    
    if (description.length() == 0) {
      throw new IllegalArgumentException("Empty rule set description");
    }
  }
  

















  public void parseRules(String description, RuleBasedNumberFormat owner)
  {
    Vector ruleDescriptions = new Vector();
    
    int oldP = 0;
    int p = description.indexOf(';');
    while (oldP != -1) {
      if (p != -1) {
        ruleDescriptions.addElement(description.substring(oldP, p));
        oldP = p + 1;
      } else {
        if (oldP < description.length()) {
          ruleDescriptions.addElement(description.substring(oldP));
        }
        oldP = p;
      }
      p = description.indexOf(';', p + 1);
    }
    



    Vector tempRules = new Vector();
    


    NFRule predecessor = null;
    for (int i = 0; i < ruleDescriptions.size(); i++)
    {


      Object temp = NFRule.makeRules((String)ruleDescriptions.elementAt(i), this, predecessor, owner);
      

      if ((temp instanceof NFRule)) {
        tempRules.addElement(temp);
        predecessor = (NFRule)temp;
      }
      else if ((temp instanceof NFRule[])) {
        NFRule[] rulesToAdd = (NFRule[])temp;
        
        for (int j = 0; j < rulesToAdd.length; j++) {
          tempRules.addElement(rulesToAdd[j]);
          predecessor = rulesToAdd[j];
        }
      }
    }
    
    ruleDescriptions = null;
    




    long defaultBaseValue = 0L;
    



    int i = 0;
    while (i < tempRules.size()) {
      NFRule rule = (NFRule)tempRules.elementAt(i);
      
      switch ((int)rule.getBaseValue())
      {




      case 0: 
        rule.setBaseValue(defaultBaseValue);
        if (!isFractionRuleSet) {
          defaultBaseValue += 1L;
        }
        i++;
        break;
      


      case -1: 
        negativeNumberRule = rule;
        tempRules.removeElementAt(i);
        break;
      


      case -2: 
        fractionRules[0] = rule;
        tempRules.removeElementAt(i);
        break;
      


      case -3: 
        fractionRules[1] = rule;
        tempRules.removeElementAt(i);
        break;
      


      case -4: 
        fractionRules[2] = rule;
        tempRules.removeElementAt(i);
        break;
      



      default: 
        if (rule.getBaseValue() < defaultBaseValue) {
          throw new IllegalArgumentException("Rules are not in order");
        }
        defaultBaseValue = rule.getBaseValue();
        if (!isFractionRuleSet) {
          defaultBaseValue += 1L;
        }
        i++;
      }
      
    }
    


    rules = new NFRule[tempRules.size()];
    tempRules.copyInto((Object[])rules);
  }
  







  public void makeIntoFractionRuleSet()
  {
    isFractionRuleSet = true;
  }
  









  public boolean equals(Object that)
  {
    if (!(that instanceof NFRuleSet)) {
      return false;
    }
    
    NFRuleSet that2 = (NFRuleSet)that;
    
    if ((!name.equals(name)) || (!Utility.objectEquals(negativeNumberRule, negativeNumberRule)) || (!Utility.objectEquals(fractionRules[0], fractionRules[0])) || (!Utility.objectEquals(fractionRules[1], fractionRules[1])) || (!Utility.objectEquals(fractionRules[2], fractionRules[2])) || (rules.length != rules.length) || (isFractionRuleSet != isFractionRuleSet))
    {






      return false;
    }
    

    for (int i = 0; i < rules.length; i++) {
      if (!rules[i].equals(rules[i])) {
        return false;
      }
    }
    

    return true;
  }
  







  public String toString()
  {
    StringBuffer result = new StringBuffer();
    

    result.append(name + ":\n");
    

    for (int i = 0; i < rules.length; i++) {
      result.append("    " + rules[i].toString() + "\n");
    }
    

    if (negativeNumberRule != null) {
      result.append("    " + negativeNumberRule.toString() + "\n");
    }
    if (fractionRules[0] != null) {
      result.append("    " + fractionRules[0].toString() + "\n");
    }
    if (fractionRules[1] != null) {
      result.append("    " + fractionRules[1].toString() + "\n");
    }
    if (fractionRules[2] != null) {
      result.append("    " + fractionRules[2].toString() + "\n");
    }
    
    return result.toString();
  }
  







  public boolean isFractionSet()
  {
    return isFractionRuleSet;
  }
  



  public String getName()
  {
    return name;
  }
  











  public void format(long number, StringBuffer toInsertInto, int pos)
  {
    NFRule applicableRule = findNormalRule(number);
    
    applicableRule.doFormat(number, toInsertInto, pos);
  }
  







  public void format(double number, StringBuffer toInsertInto, int pos)
  {
    NFRule applicableRule = findRule(number);
    
    applicableRule.doFormat(number, toInsertInto, pos);
  }
  





  private NFRule findRule(double number)
  {
    if (isFractionRuleSet) {
      return findFractionRuleSetRule(number);
    }
    



    if (number < 0.0D) {
      if (negativeNumberRule != null) {
        return negativeNumberRule;
      }
      number = -number;
    }
    


    if (number != Math.floor(number))
    {

      if ((number < 1.0D) && (fractionRules[1] != null)) {
        return fractionRules[1];
      }
      

      if (fractionRules[0] != null) {
        return fractionRules[0];
      }
    }
    

    if (fractionRules[2] != null) {
      return fractionRules[2];
    }
    


    return findNormalRule(Math.round(number));
  }
  



















  private NFRule findNormalRule(long number)
  {
    if (isFractionRuleSet) {
      return findFractionRuleSetRule(number);
    }
    


    if (number < 0L) {
      if (negativeNumberRule != null) {
        return negativeNumberRule;
      }
      number = -number;
    }
    













    int lo = 0;
    int hi = rules.length;
    if (hi > 0) {
      while (lo < hi) {
        int mid = (lo + hi) / 2;
        if (rules[mid].getBaseValue() == number) {
          return rules[mid];
        }
        if (rules[mid].getBaseValue() > number) {
          hi = mid;
        }
        else {
          lo = mid + 1;
        }
      }
      NFRule result = rules[(hi - 1)];
      





      if (result.shouldRollBack(number)) {
        result = rules[(hi - 2)];
      }
      return result;
    }
    
    return fractionRules[2];
  }
  























  private NFRule findFractionRuleSetRule(double number)
  {
    long leastCommonMultiple = rules[0].getBaseValue();
    for (int i = 1; i < rules.length; i++) {
      leastCommonMultiple = lcm(leastCommonMultiple, rules[i].getBaseValue());
    }
    long numerator = Math.round(number * leastCommonMultiple);
    


    long difference = Long.MAX_VALUE;
    int winner = 0;
    for (int i = 0; i < rules.length; i++)
    {





      long tempDifference = numerator * rules[i].getBaseValue() % leastCommonMultiple;
      



      if (leastCommonMultiple - tempDifference < tempDifference) {
        tempDifference = leastCommonMultiple - tempDifference;
      }
      




      if (tempDifference < difference) {
        difference = tempDifference;
        winner = i;
        if (difference == 0L) {
          break;
        }
      }
    }
    






    if ((winner + 1 < rules.length) && (rules[(winner + 1)].getBaseValue() == rules[winner].getBaseValue()))
    {
      if ((Math.round(number * rules[winner].getBaseValue()) < 1L) || (Math.round(number * rules[winner].getBaseValue()) >= 2L))
      {
        winner++;
      }
    }
    

    return rules[winner];
  }
  




  private static long lcm(long x, long y)
  {
    long x1 = x;
    long y1 = y;
    
    int p2 = 0;
    while (((x1 & 1L) == 0L) && ((y1 & 1L) == 0L)) {
      p2++;
      x1 >>= 1;
      y1 >>= 1;
    }
    
    long t;
    if ((x1 & 1L) == 1L) {
      t = -y1;
    } else {
      t = x1;
    }
    
    for (goto 113; t != 0L;) {
      while ((t & 1L) == 0L) {
        t >>= 1;
      }
      if (t > 0L) {
        x1 = t;
      } else {
        y1 = -t;
      }
      t = x1 - y1;
    }
    long gcd = x1 << p2;
    

    return x / gcd * y;
  }
  




























  public Number parse(String text, ParsePosition parsePosition, double upperBound)
  {
    ParsePosition highWaterMark = new ParsePosition(0);
    Number result = new Long(0L);
    Number tempResult = null;
    

    if (text.length() == 0) {
      return result;
    }
    

    if (negativeNumberRule != null) {
      tempResult = negativeNumberRule.doParse(text, parsePosition, false, upperBound);
      if (parsePosition.getIndex() > highWaterMark.getIndex()) {
        result = tempResult;
        highWaterMark.setIndex(parsePosition.getIndex());
      }
      



      parsePosition.setIndex(0);
    }
    

    for (int i = 0; i < 3; i++) {
      if (fractionRules[i] != null) {
        tempResult = fractionRules[i].doParse(text, parsePosition, false, upperBound);
        if (parsePosition.getIndex() > highWaterMark.getIndex()) {
          result = tempResult;
          highWaterMark.setIndex(parsePosition.getIndex());
        }
        



        parsePosition.setIndex(0);
      }
    }
    









    for (int i = rules.length - 1; (i >= 0) && (highWaterMark.getIndex() < text.length()); i--) {
      if ((isFractionRuleSet) || (rules[i].getBaseValue() < upperBound))
      {


        tempResult = rules[i].doParse(text, parsePosition, isFractionRuleSet, upperBound);
        if (parsePosition.getIndex() > highWaterMark.getIndex()) {
          result = tempResult;
          highWaterMark.setIndex(parsePosition.getIndex());
        }
        



        parsePosition.setIndex(0);
      }
    }
    


    parsePosition.setIndex(highWaterMark.getIndex());
    




    return result;
  }
}
