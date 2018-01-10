package com.ibm.icu.impl;

import com.ibm.icu.text.Replaceable;
import com.ibm.icu.text.ReplaceableString;
import com.ibm.icu.text.Transliterator.Position;
import com.ibm.icu.text.UnicodeMatcher;
























public class UtilityExtensions
{
  public UtilityExtensions() {}
  
  public static void appendToRule(StringBuffer rule, String text, boolean isLiteral, boolean escapeUnprintable, StringBuffer quoteBuf)
  {
    for (int i = 0; i < text.length(); i++)
    {
      Utility.appendToRule(rule, text.charAt(i), isLiteral, escapeUnprintable, quoteBuf);
    }
  }
  







  public static void appendToRule(StringBuffer rule, UnicodeMatcher matcher, boolean escapeUnprintable, StringBuffer quoteBuf)
  {
    if (matcher != null) {
      appendToRule(rule, matcher.toPattern(escapeUnprintable), true, escapeUnprintable, quoteBuf);
    }
  }
  





  public static String formatInput(ReplaceableString input, Transliterator.Position pos)
  {
    StringBuffer appendTo = new StringBuffer();
    formatInput(appendTo, input, pos);
    return Utility.escape(appendTo.toString());
  }
  






  public static StringBuffer formatInput(StringBuffer appendTo, ReplaceableString input, Transliterator.Position pos)
  {
    if ((0 <= contextStart) && (contextStart <= start) && (start <= limit) && (limit <= contextLimit) && (contextLimit <= input.length()))
    {






      String b = input.substring(contextStart, start);
      String c = input.substring(start, limit);
      String d = input.substring(limit, contextLimit);
      
      appendTo.append('{').append(b).append('|').append(c).append('|').append(d).append('}');


    }
    else
    {

      appendTo.append("INVALID Position {cs=" + contextStart + ", s=" + start + ", l=" + limit + ", cl=" + contextLimit + "} on " + input);
    }
    


    return appendTo;
  }
  



  public static String formatInput(Replaceable input, Transliterator.Position pos)
  {
    return formatInput((ReplaceableString)input, pos);
  }
  




  public static StringBuffer formatInput(StringBuffer appendTo, Replaceable input, Transliterator.Position pos)
  {
    return formatInput(appendTo, (ReplaceableString)input, pos);
  }
}
