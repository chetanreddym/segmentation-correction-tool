package com.ibm.icu.text;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.UCharacterProperty;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.ResourceBundle;



















































































































































































































































































































































































































































































































public final class RuleBasedNumberFormat
  extends NumberFormat
{
  private static final String copyrightNotice = "Copyright ©1997-1998 IBM Corp.  All rights reserved.";
  public static final int SPELLOUT = 1;
  public static final int ORDINAL = 2;
  public static final int DURATION = 3;
  private NFRuleSet[] ruleSets = null;
  




  private NFRuleSet defaultRuleSet = null;
  




  private Locale locale = null;
  





  private Collator collator = null;
  





  private DecimalFormatSymbols decimalFormatSymbols = null;
  



  private boolean lenientParse = false;
  




  private String lenientParseRules = null;
  











  public RuleBasedNumberFormat(String description)
  {
    locale = Locale.getDefault();
    init(description);
  }
  












  public RuleBasedNumberFormat(String description, Locale locale)
  {
    this.locale = locale;
    init(description);
  }
  











  public RuleBasedNumberFormat(Locale locale, int format)
  {
    this.locale = locale;
    



    ResourceBundle bundle = ICULocaleData.getResourceBundle("LocaleElements", locale);
    String description = "";
    


    switch (format) {
    case 1: 
      description = bundle.getString("SpelloutRules");
      break;
    
    case 2: 
      description = bundle.getString("OrdinalRules");
      break;
    
    case 3: 
      description = bundle.getString("DurationRules");
    }
    
    

    init(description);
  }
  









  public RuleBasedNumberFormat(int format)
  {
    this(Locale.getDefault(), format);
  }
  








  public Object clone()
  {
    return super.clone();
  }
  







  public boolean equals(Object that)
  {
    if (!(that instanceof RuleBasedNumberFormat)) {
      return false;
    }
    

    RuleBasedNumberFormat that2 = (RuleBasedNumberFormat)that;
    

    if ((!locale.equals(locale)) || (lenientParse != lenientParse)) {
      return false;
    }
    

    if (ruleSets.length != ruleSets.length) {
      return false;
    }
    for (int i = 0; i < ruleSets.length; i++) {
      if (!ruleSets[i].equals(ruleSets[i])) {
        return false;
      }
    }
    
    return true;
  }
  











  public String toString()
  {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < ruleSets.length; i++) {
      result.append(ruleSets[i].toString());
    }
    return result.toString();
  }
  





  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeUTF(toString());
  }
  





  private void readObject(ObjectInputStream in)
    throws IOException
  {
    String description = in.readUTF();
    




    RuleBasedNumberFormat temp = new RuleBasedNumberFormat(description);
    ruleSets = ruleSets;
    defaultRuleSet = defaultRuleSet;
  }
  











  public String[] getRuleSetNames()
  {
    int count = 0;
    for (int i = 0; i < ruleSets.length; i++) {
      if (!ruleSets[i].getName().startsWith("%%")) {
        count++;
      }
    }
    

    String[] result = new String[count];
    count = 0;
    for (int i = ruleSets.length - 1; i >= 0; i--) {
      if (!ruleSets[i].getName().startsWith("%%")) {
        result[(count++)] = ruleSets[i].getName();
      }
    }
    
    return result;
  }
  






  public String format(double number, String ruleSet)
    throws IllegalArgumentException
  {
    if (ruleSet.startsWith("%%")) {
      throw new IllegalArgumentException("Can't use internal rule set");
    }
    return format(number, findRuleSet(ruleSet));
  }
  










  public String format(long number, String ruleSet)
    throws IllegalArgumentException
  {
    if (ruleSet.startsWith("%%")) {
      throw new IllegalArgumentException("Can't use internal rule set");
    }
    return format(number, findRuleSet(ruleSet));
  }
  













  public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition ignore)
  {
    toAppendTo.append(format(number, defaultRuleSet));
    return toAppendTo;
  }
  

















  public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition ignore)
  {
    toAppendTo.append(format(number, defaultRuleSet));
    return toAppendTo;
  }
  







  public StringBuffer format(BigInteger number, StringBuffer toAppendTo, FieldPosition pos)
  {
    return format(new com.ibm.icu.math.BigDecimal(number), toAppendTo, pos);
  }
  







  public StringBuffer format(java.math.BigDecimal number, StringBuffer toAppendTo, FieldPosition pos)
  {
    return format(new com.ibm.icu.math.BigDecimal(number), toAppendTo, pos);
  }
  








  public StringBuffer format(com.ibm.icu.math.BigDecimal number, StringBuffer toAppendTo, FieldPosition pos)
  {
    return format(number.doubleValue(), toAppendTo, pos);
  }
  




















  public Number parse(String text, ParsePosition parsePosition)
  {
    String workingText = text.substring(parsePosition.getIndex());
    ParsePosition workingPos = new ParsePosition(0);
    Number tempResult = null;
    


    Number result = new Long(0L);
    ParsePosition highWaterMark = new ParsePosition(workingPos.getIndex());
    




    for (int i = ruleSets.length - 1; i >= 0; i--)
    {
      if (!ruleSets[i].getName().startsWith("%%"))
      {




        tempResult = ruleSets[i].parse(workingText, workingPos, Double.MAX_VALUE);
        if (workingPos.getIndex() > highWaterMark.getIndex()) {
          result = tempResult;
          highWaterMark.setIndex(workingPos.getIndex());
        }
        






        if (highWaterMark.getIndex() == workingText.length()) {
          break;
        }
        


        workingPos.setIndex(0);
      }
    }
    

    parsePosition.setIndex(parsePosition.getIndex() + highWaterMark.getIndex());
    



    return result;
  }
  
































  public void setLenientParseMode(boolean enabled)
  {
    lenientParse = enabled;
    


    if (!enabled) {
      collator = null;
    }
  }
  






  public boolean lenientParseEnabled()
  {
    return lenientParse;
  }
  






  public void setDefaultRuleSet(String ruleSetName)
  {
    if (ruleSetName == null) {
      initDefaultRuleSet();
    } else { if (ruleSetName.startsWith("%%")) {
        throw new IllegalArgumentException("cannot use private rule set: " + ruleSetName);
      }
      defaultRuleSet = findRuleSet(ruleSetName);
    }
  }
  









  NFRuleSet getDefaultRuleSet()
  {
    return defaultRuleSet;
  }
  






  Collator getCollator()
  {
    if ((collator == null) && (lenientParse))
    {

      try
      {

        RuleBasedCollator temp = (RuleBasedCollator)Collator.getInstance(locale);
        String rules = temp.getRules() + lenientParseRules;
        
        collator = new RuleBasedCollator(rules);
        collator.setDecomposition(17);

      }
      catch (Exception e)
      {
        e.printStackTrace();
        collator = null;
      }
    }
    


    return collator;
  }
  









  DecimalFormatSymbols getDecimalFormatSymbols()
  {
    if (decimalFormatSymbols == null) {
      decimalFormatSymbols = new DecimalFormatSymbols(locale);
    }
    return decimalFormatSymbols;
  }
  
















  private void init(String description)
  {
    description = stripWhitespace(description);
    




    int lp = description.indexOf("%%lenient-parse:");
    if (lp != -1)
    {


      if ((lp == 0) || (description.charAt(lp - 1) == ';'))
      {


        int lpEnd = description.indexOf(";%", lp);
        
        if (lpEnd == -1) {
          lpEnd = description.length() - 1;
        }
        int lpStart = lp + "%%lenient-parse:".length();
        while (UCharacterProperty.isRuleWhiteSpace(description.charAt(lpStart))) {
          lpStart++;
        }
        


        lenientParseRules = description.substring(lpStart, lpEnd);
        
        StringBuffer temp = new StringBuffer(description.substring(0, lp));
        if (lpEnd + 1 < description.length()) {
          temp.append(description.substring(lpEnd + 1));
        }
        description = temp.toString();
      }
    }
    



    int numRuleSets = 0;
    for (int p = description.indexOf(";%"); p != -1; p = description.indexOf(";%", p)) {
      numRuleSets++;
      p++;
    }
    numRuleSets++;
    

    ruleSets = new NFRuleSet[numRuleSets];
    







    String[] ruleSetDescriptions = new String[numRuleSets];
    
    int curRuleSet = 0;
    int start = 0;
    for (int p = description.indexOf(";%"); p != -1; p = description.indexOf(";%", start)) {
      ruleSetDescriptions[curRuleSet] = description.substring(start, p + 1);
      ruleSets[curRuleSet] = new NFRuleSet(ruleSetDescriptions, curRuleSet);
      curRuleSet++;
      start = p + 1;
    }
    ruleSetDescriptions[curRuleSet] = description.substring(start);
    ruleSets[curRuleSet] = new NFRuleSet(ruleSetDescriptions, curRuleSet);
    





    initDefaultRuleSet();
    



    for (int i = 0; i < ruleSets.length; i++) {
      ruleSets[i].parseRules(ruleSetDescriptions[i], this);
      ruleSetDescriptions[i] = null;
    }
  }
  








  private String stripWhitespace(String description)
  {
    StringBuffer result = new StringBuffer();
    

    int start = 0;
    for (goto 125; (start != -1) && (start < description.length());)
    {

      while ((start < description.length()) && (UCharacterProperty.isRuleWhiteSpace(description.charAt(start)))) {
        start++;
      }
      

      if ((start < description.length()) && (description.charAt(start) == ';')) {
        start++;


      }
      else
      {

        int p = description.indexOf(';', start);
        if (p == -1)
        {

          result.append(description.substring(start));
          start = -1;
        }
        else if (p < description.length()) {
          result.append(description.substring(start, p + 1));
          start = p + 1;


        }
        else
        {


          start = -1;
        }
      } }
    return result.toString();
  }
  









  private void initDefaultRuleSet()
  {
    for (int i = ruleSets.length - 1; i >= 0; i--) {
      if (!ruleSets[i].getName().startsWith("%%")) {
        defaultRuleSet = ruleSets[i];
        return;
      }
    }
    defaultRuleSet = ruleSets[(ruleSets.length - 1)];
  }
  
















  String format(double number, NFRuleSet ruleSet)
  {
    StringBuffer result = new StringBuffer();
    ruleSet.format(number, result, 0);
    return result.toString();
  }
  

















  String format(long number, NFRuleSet ruleSet)
  {
    StringBuffer result = new StringBuffer();
    ruleSet.format(number, result, 0);
    return result.toString();
  }
  




  NFRuleSet findRuleSet(String name)
    throws IllegalArgumentException
  {
    for (int i = 0; i < ruleSets.length; i++) {
      if (ruleSets[i].getName().equals(name)) {
        return ruleSets[i];
      }
    }
    throw new IllegalArgumentException("No rule set named " + name);
  }
}
