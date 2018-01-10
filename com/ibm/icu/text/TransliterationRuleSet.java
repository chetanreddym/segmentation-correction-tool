package com.ibm.icu.text;

import java.util.Vector;




















































class TransliterationRuleSet
{
  private Vector ruleVector;
  private int maxContextLength;
  private TransliterationRule[] rules;
  private int[] index;
  private static final String COPYRIGHT = "© IBM Corporation 1999-2001. All rights reserved.";
  
  public TransliterationRuleSet()
  {
    ruleVector = new Vector();
    maxContextLength = 0;
  }
  



  public int getMaximumContextLength()
  {
    return maxContextLength;
  }
  




  public void addRule(TransliterationRule rule)
  {
    ruleVector.addElement(rule);
    int len;
    if ((len = rule.getAnteContextLength()) > maxContextLength) {
      maxContextLength = len;
    }
    
    rules = null;
  }
  




















  public void freeze()
  {
    int n = ruleVector.size();
    index = new int['ā'];
    Vector v = new Vector(2 * n);
    


    int[] indexValue = new int[n];
    for (int j = 0; j < n; j++) {
      TransliterationRule r = (TransliterationRule)ruleVector.elementAt(j);
      indexValue[j] = r.getIndexValue();
    }
    for (int x = 0; x < 256; x++) {
      index[x] = v.size();
      for (int j = 0; j < n; j++) {
        if (indexValue[j] >= 0) {
          if (indexValue[j] == x) {
            v.addElement(ruleVector.elementAt(j));
          }
          

        }
        else
        {
          TransliterationRule r = (TransliterationRule)ruleVector.elementAt(j);
          if (r.matchesIndexValue(x)) {
            v.addElement(r);
          }
        }
      }
    }
    index['Ā'] = v.size();
    


    rules = new TransliterationRule[v.size()];
    v.copyInto(rules);
    
    StringBuffer errors = null;
    







    for (int x = 0; x < 256; x++) {
      for (int j = index[x]; j < index[(x + 1)] - 1; j++) {
        TransliterationRule r1 = rules[j];
        for (int k = j + 1; k < index[(x + 1)]; k++) {
          TransliterationRule r2 = rules[k];
          if (r1.masks(r2)) {
            if (errors == null) {
              errors = new StringBuffer();
            } else {
              errors.append("\n");
            }
            errors.append("Rule " + r1 + " masks " + r2);
          }
        }
      }
    }
    
    if (errors != null) {
      throw new IllegalArgumentException(errors.toString());
    }
  }
  














  public boolean transliterate(Replaceable text, Transliterator.Position pos, boolean incremental)
  {
    int indexByte = text.char32At(start) & 0xFF;
    for (int i = index[indexByte]; i < index[(indexByte + 1)]; i++) {
      int m = rules[i].matchAndReplace(text, pos, incremental);
      switch (m)
      {




      case 2: 
        return true;
      




      case 1: 
        return false;
      }
      
    }
    start += UTF16.getCharCount(text.char32At(start));
    



    return true;
  }
  



  String toRules(boolean escapeUnprintable)
  {
    int count = ruleVector.size();
    StringBuffer ruleSource = new StringBuffer();
    for (int i = 0; i < count; i++) {
      if (i != 0) {
        ruleSource.append('\n');
      }
      TransliterationRule r = (TransliterationRule)ruleVector.elementAt(i);
      
      ruleSource.append(r.toRule(escapeUnprintable));
    }
    return ruleSource.toString();
  }
  



  UnicodeSet getSourceTargetSet(boolean getTarget)
  {
    UnicodeSet set = new UnicodeSet();
    int count = ruleVector.size();
    for (int i = 0; i < count; i++) {
      TransliterationRule r = (TransliterationRule)ruleVector.elementAt(i);
      
      if (getTarget) {
        r.addTargetSetTo(set);
      } else {
        r.addSourceSetTo(set);
      }
    }
    return set;
  }
}
