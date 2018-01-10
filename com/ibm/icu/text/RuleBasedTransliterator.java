package com.ibm.icu.text;

import java.util.Hashtable;
























































































































































































































































































/**
 * @deprecated
 */
public class RuleBasedTransliterator
  extends Transliterator
{
  private Data data;
  private static final String COPYRIGHT = "© IBM Corporation 1999. All rights reserved.";
  
  /**
   * @deprecated
   */
  public RuleBasedTransliterator(String ID, String rules, int direction, UnicodeFilter filter)
  {
    super(ID, filter);
    if ((direction != 0) && (direction != 1)) {
      throw new IllegalArgumentException("Invalid direction");
    }
    
    TransliteratorParser parser = new TransliteratorParser();
    parser.parse(rules, direction);
    if ((idBlock.length() != 0) || (compoundFilter != null))
    {
      throw new IllegalArgumentException("::ID blocks illegal in RuleBasedTransliterator constructor");
    }
    
    data = data;
    setMaximumContextLength(data.ruleSet.getMaximumContextLength());
  }
  




  /**
   * @deprecated
   */
  public RuleBasedTransliterator(String ID, String rules)
  {
    this(ID, rules, 0, null);
  }
  
  RuleBasedTransliterator(String ID, Data data, UnicodeFilter filter) {
    super(ID, filter);
    this.data = data;
    setMaximumContextLength(ruleSet.getMaximumContextLength());
  }
  

























  /**
   * @deprecated
   */
  protected void handleTransliterate(Replaceable text, Transliterator.Position index, boolean incremental)
  {
    int loopCount = 0;
    int loopLimit = limit - start << 4;
    if (loopLimit < 0) {
      loopLimit = Integer.MAX_VALUE;
    }
    

    while ((start < limit) && (loopCount <= loopLimit) && (data.ruleSet.transliterate(text, index, incremental)))
    {
      loopCount++; } }
  
  static class Data { public TransliterationRuleSet ruleSet;
    Hashtable variableNames;
    Object[] variables;
    char variablesBase;
    
    public Data() { variableNames = new Hashtable();
      ruleSet = new TransliterationRuleSet();
    }
    





































    public UnicodeMatcher lookupMatcher(int standIn)
    {
      int i = standIn - variablesBase;
      return (i >= 0) && (i < variables.length) ? (UnicodeMatcher)variables[i] : null;
    }
    




    public UnicodeReplacer lookupReplacer(int standIn)
    {
      int i = standIn - variablesBase;
      return (i >= 0) && (i < variables.length) ? (UnicodeReplacer)variables[i] : null;
    }
  }
  









  /**
   * @deprecated
   */
  public String toRules(boolean escapeUnprintable)
  {
    return data.ruleSet.toRules(escapeUnprintable);
  }
  

  /**
   * @deprecated
   */
  protected UnicodeSet handleGetSourceSet()
  {
    return data.ruleSet.getSourceTargetSet(false);
  }
  

  /**
   * @deprecated
   */
  public UnicodeSet getTargetSet()
  {
    return data.ruleSet.getSourceTargetSet(true);
  }
}
