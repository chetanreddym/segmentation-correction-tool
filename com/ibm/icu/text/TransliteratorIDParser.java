package com.ibm.icu.text;

import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.CaseInsensitiveString;
import java.text.ParsePosition;
import java.util.Hashtable;
import java.util.Vector;









































class TransliteratorIDParser
{
  private static final char ID_DELIM = ';';
  private static final char TARGET_SEP = '-';
  private static final char VARIANT_SEP = '/';
  private static final char OPEN_REV = '(';
  private static final char CLOSE_REV = ')';
  private static final String ANY = "Any";
  private static final int FORWARD = 0;
  private static final int REVERSE = 1;
  private static final Hashtable SPECIAL_INVERSES = new Hashtable();
  

  TransliteratorIDParser() {}
  


  private static class Specs
  {
    public String source;
    

    public String target;
    
    public String variant;
    
    public String filter;
    
    public boolean sawSource;
    

    Specs(String s, String t, String v, boolean sawS, String f)
    {
      source = s;
      target = t;
      variant = v;
      sawSource = sawS;
      filter = f;
    }
  }
  



  static class SingleID
  {
    public String canonID;
    


    public String basicID;
    


    public String filter;
    



    SingleID(String c, String b, String f)
    {
      canonID = c;
      basicID = b;
      filter = f;
    }
    
    SingleID(String c, String b) { this(c, b, null); }
    
    Transliterator getInstance() {
      Transliterator t;
      if ((basicID == null) || (basicID.length() == 0)) {
        t = Transliterator.getBasicInstance("Any-Null", canonID);
      } else {
        t = Transliterator.getBasicInstance(basicID, canonID);
      }
      if ((t != null) && 
        (filter != null)) {
        t.setFilter(new UnicodeSet(filter));
      }
      
      return t;
    }
  }
  









  public static SingleID parseFilterID(String id, int[] pos)
  {
    int start = pos[0];
    Specs specs = parseFilterID(id, pos, true);
    if (specs == null) {
      pos[0] = start;
      return null;
    }
    

    SingleID single = specsToID(specs, 0);
    filter = filter;
    return single;
  }
  












  public static SingleID parseSingleID(String id, int[] pos, int dir)
  {
    int start = pos[0];
    


    Specs specsA = null;
    Specs specsB = null;
    boolean sawParen = false;
    


    for (int pass = 1; pass <= 2; pass++) {
      if (pass == 2) {
        specsA = parseFilterID(id, pos, true);
        if (specsA == null) {
          pos[0] = start;
          return null;
        }
      }
      if (Utility.parseChar(id, pos, '(')) {
        sawParen = true;
        if (Utility.parseChar(id, pos, ')')) break;
        specsB = parseFilterID(id, pos, true);
        
        if ((specsB != null) && (Utility.parseChar(id, pos, ')'))) break;
        pos[0] = start;
        return null;
      }
    }
    


    SingleID single;
    

    if (sawParen) {
      if (dir == 0) {
        single = specsToID(specsA, 0);
        canonID = (canonID + '(' + specsToID0canonID + ')');
        
        if (specsA != null) {
          filter = filter;
        }
      } else {
        single = specsToID(specsB, 0);
        canonID = (canonID + '(' + specsToID0canonID + ')');
        
        if (specsB != null) {
          filter = filter;
        }
      }
    }
    else {
      if (dir == 0) {
        single = specsToID(specsA, 0);
      } else {
        single = specsToSpecialInverse(specsA);
        if (single == null) {
          single = specsToID(specsA, 1);
        }
      }
      filter = filter;
    }
    
    return single;
  }
  























  public static UnicodeSet parseGlobalFilter(String id, int[] pos, int dir, int[] withParens, StringBuffer canonID)
  {
    UnicodeSet filter = null;
    int start = pos[0];
    
    if (withParens[0] == -1) {
      withParens[0] = (Utility.parseChar(id, pos, '(') ? 1 : 0);
    } else if ((withParens[0] == 1) && 
      (!Utility.parseChar(id, pos, '('))) {
      pos[0] = start;
      return null;
    }
    

    Utility.skipWhitespace(id, pos);
    
    if (UnicodeSet.resemblesPattern(id, pos[0])) {
      ParsePosition ppos = new ParsePosition(pos[0]);
      try {
        filter = new UnicodeSet(id, ppos, null);
      } catch (IllegalArgumentException e) {
        pos[0] = start;
        return null;
      }
      
      String pattern = id.substring(pos[0], ppos.getIndex());
      pos[0] = ppos.getIndex();
      
      if ((withParens[0] == 1) && (!Utility.parseChar(id, pos, ')'))) {
        pos[0] = start;
        return null;
      }
      



      if (canonID != null) {
        if (dir == 0) {
          if (withParens[0] == 1) {
            pattern = String.valueOf('(') + pattern + ')';
          }
          canonID.append(pattern + ';');
        } else {
          if (withParens[0] == 0) {
            pattern = String.valueOf('(') + pattern + ')';
          }
          canonID.insert(0, pattern + ';');
        }
      }
    }
    
    return filter;
  }
  
























  public static boolean parseCompoundID(String id, int dir, StringBuffer canonID, Vector list, UnicodeSet[] globalFilter)
  {
    int[] pos = { 0 };
    int[] withParens = new int[1];
    list.removeAllElements();
    
    globalFilter[0] = null;
    canonID.setLength(0);
    

    withParens[0] = 0;
    UnicodeSet filter = parseGlobalFilter(id, pos, dir, withParens, canonID);
    if (filter != null) {
      if (!Utility.parseChar(id, pos, ';'))
      {
        canonID.setLength(0);
        pos[0] = 0;
      }
      if (dir == 0) {
        globalFilter[0] = filter;
      }
    }
    
    boolean sawDelimiter = true;
    for (;;) {
      SingleID single = parseSingleID(id, pos, dir);
      if (single == null) {
        break;
      }
      if (dir == 0) {
        list.addElement(single);
      } else {
        list.insertElementAt(single, 0);
      }
      if (!Utility.parseChar(id, pos, ';')) {
        sawDelimiter = false;
        break;
      }
    }
    
    if (list.size() == 0) {
      return false;
    }
    

    for (int i = 0; i < list.size(); i++) {
      SingleID single = (SingleID)list.elementAt(i);
      canonID.append(canonID);
      if (i != list.size() - 1) {
        canonID.append(';');
      }
    }
    


    if (sawDelimiter) {
      withParens[0] = 1;
      filter = parseGlobalFilter(id, pos, dir, withParens, canonID);
      if (filter != null)
      {
        Utility.parseChar(id, pos, ';');
        
        if (dir == 1) {
          globalFilter[0] = filter;
        }
      }
    }
    

    Utility.skipWhitespace(id, pos[0]);
    if (pos[0] != id.length()) {
      return false;
    }
    
    return true;
  }
  












  public static int instantiateList(Vector list, Transliterator insert, int insertIndex)
  {
    Transliterator t;
    











    for (int i = 0; i <= list.size();) {
      if (insertIndex == i) {
        list.insertElementAt(insert, i++);

      }
      else
      {

        if (i == list.size()) {
          break;
        }
        
        SingleID single = (SingleID)list.elementAt(i);
        if (basicID.length() == 0) {
          list.removeElementAt(i);
          if (insertIndex > i) {
            insertIndex--;
          }
        } else {
          t = single.getInstance();
          if (t == null) {
            throw new IllegalArgumentException("Illegal ID " + canonID);
          }
          list.setElementAt(t, i);
          i++;
        }
      }
    }
    
    if (list.size() == 0) {
      t = Transliterator.getBasicInstance("Any-Null", null);
      if (t == null)
      {
        throw new IllegalArgumentException("Internal error; cannot instantiate Any-Null");
      }
      list.addElement(t);
    }
    
    return insertIndex;
  }
  










  public static String[] IDtoSTV(String id)
  {
    String source = "Any";
    String target = null;
    String variant = "";
    
    int sep = id.indexOf('-');
    int var = id.indexOf('/');
    if (var < 0) {
      var = id.length();
    }
    boolean isSourcePresent = false;
    
    if (sep < 0)
    {
      target = id.substring(0, var);
      variant = id.substring(var);
    } else if (sep < var)
    {
      if (sep > 0) {
        source = id.substring(0, sep);
        isSourcePresent = true;
      }
      target = id.substring(++sep, var);
      variant = id.substring(var);
    }
    else {
      if (var > 0) {
        source = id.substring(0, var);
        isSourcePresent = true;
      }
      variant = id.substring(var, sep++);
      target = id.substring(sep);
    }
    
    if (variant.length() > 0) {
      variant = variant.substring(1);
    }
    
    return new String[] { source, target, variant, isSourcePresent ? "" : null };
  }
  







  public static String STVtoID(String source, String target, String variant)
  {
    StringBuffer id = new StringBuffer(source);
    if (id.length() == 0) {
      id.append("Any");
    }
    id.append('-').append(target);
    if ((variant != null) && (variant.length() != 0)) {
      id.append('/').append(variant);
    }
    return id.toString();
  }
  

































  public static void registerSpecialInverse(String target, String inverseTarget, boolean bidirectional)
  {
    SPECIAL_INVERSES.put(new CaseInsensitiveString(target), inverseTarget);
    if ((bidirectional) && (!target.equalsIgnoreCase(inverseTarget))) {
      SPECIAL_INVERSES.put(new CaseInsensitiveString(inverseTarget), target);
    }
  }
  























  private static Specs parseFilterID(String id, int[] pos, boolean allowFilter)
  {
    String first = null;
    String source = null;
    String target = null;
    String variant = null;
    String filter = null;
    char delimiter = '\000';
    int specCount = 0;
    int start = pos[0];
    


    for (;;)
    {
      Utility.skipWhitespace(id, pos);
      if (pos[0] == id.length()) {
        break;
      }
      

      if ((allowFilter) && (filter == null) && (UnicodeSet.resemblesPattern(id, pos[0])))
      {

        ParsePosition ppos = new ParsePosition(pos[0]);
        UnicodeSet set = new UnicodeSet(id, ppos, null);
        filter = id.substring(pos[0], ppos.getIndex());
        pos[0] = ppos.getIndex();
      }
      else
      {
        if (delimiter == 0) {
          char c = id.charAt(pos[0]);
          if (((c == '-') && (target == null)) || ((c == '/') && (variant == null)))
          {
            delimiter = c;
            pos[0] += 1;
            continue;
          }
        }
        



        if ((delimiter == 0) && (specCount > 0)) {
          break;
        }
        
        String spec = Utility.parseUnicodeIdentifier(id, pos);
        if (spec == null) {
          break;
        }
        



        switch (delimiter) {
        case '\000': 
          first = spec;
          break;
        case '-': 
          target = spec;
          break;
        case '/': 
          variant = spec;
        }
        
        specCount++;
        delimiter = '\000';
      }
    }
    

    if (first != null) {
      if (target == null) {
        target = first;
      } else {
        source = first;
      }
    }
    

    if ((source == null) && (target == null)) {
      pos[0] = start;
      return null;
    }
    

    boolean sawSource = true;
    if (source == null) {
      source = "Any";
      sawSource = false;
    }
    if (target == null) {
      target = "Any";
    }
    
    return new Specs(source, target, variant, sawSource, filter);
  }
  






  private static SingleID specsToID(Specs specs, int dir)
  {
    String canonID = "";
    String basicID = "";
    String basicPrefix = "";
    if (specs != null) {
      StringBuffer buf = new StringBuffer();
      if (dir == 0) {
        if (sawSource) {
          buf.append(source).append('-');
        } else {
          basicPrefix = source + '-';
        }
        buf.append(target);
      } else {
        buf.append(target).append('-').append(source);
      }
      if (variant != null) {
        buf.append('/').append(variant);
      }
      basicID = basicPrefix + buf.toString();
      if (filter != null) {
        buf.insert(0, filter);
      }
      canonID = buf.toString();
    }
    return new SingleID(canonID, basicID);
  }
  






  private static SingleID specsToSpecialInverse(Specs specs)
  {
    if (!source.equalsIgnoreCase("Any")) {
      return null;
    }
    String inverseTarget = (String)SPECIAL_INVERSES.get(new CaseInsensitiveString(target));
    
    if (inverseTarget != null)
    {


      StringBuffer buf = new StringBuffer();
      if (filter != null) {
        buf.append(filter);
      }
      if (sawSource) {
        buf.append("Any").append('-');
      }
      buf.append(inverseTarget);
      
      String basicID = "Any-" + inverseTarget;
      
      if (variant != null) {
        buf.append('/').append(variant);
        basicID = basicID + '/' + variant;
      }
      return new SingleID(buf.toString(), basicID);
    }
    return null;
  }
}
