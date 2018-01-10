package com.ibm.icu.text;











class FunctionReplacer
  implements UnicodeReplacer
{
  private Transliterator translit;
  









  private UnicodeReplacer replacer;
  









  public FunctionReplacer(Transliterator theTranslit, UnicodeReplacer theReplacer)
  {
    translit = theTranslit;
    replacer = theReplacer;
  }
  







  public int replace(Replaceable text, int start, int limit, int[] cursor)
  {
    int len = replacer.replace(text, start, limit, cursor);
    limit = start + len;
    

    limit = translit.transliterate(text, start, limit);
    
    return limit - start;
  }
  


  public String toReplacerPattern(boolean escapeUnprintable)
  {
    StringBuffer rule = new StringBuffer("&");
    rule.append(translit.getID());
    rule.append("( ");
    rule.append(replacer.toReplacerPattern(escapeUnprintable));
    rule.append(" )");
    return rule.toString();
  }
  




  public void addReplacementSetTo(UnicodeSet toUnionTo)
  {
    toUnionTo.addAll(translit.getTargetSet());
  }
}
