package com.ibm.icu.text;

import com.ibm.icu.impl.SortedSetRelation;
import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.impl.UPropertyAliases;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.util.VersionInfo;
import java.text.ParsePosition;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

































































































































































































































































public class UnicodeSet
  extends UnicodeFilter
{
  private static final int LOW = 0;
  private static final int HIGH = 1114112;
  public static final int MIN_VALUE = 0;
  public static final int MAX_VALUE = 1114111;
  private int len;
  private int[] list;
  private int[] rangeList;
  private int[] buffer;
  TreeSet strings = new TreeSet();
  









  private String pat = null;
  


  private static final int START_EXTRA = 16;
  


  private static final int GROW_EXTRA = 16;
  

  private static final String ANY_ID = "ANY";
  

  private static final String ASCII_ID = "ASCII";
  

  private static UnicodeSet INCLUSIONS = null;
  







  public UnicodeSet()
  {
    list = new int[17];
    list[(len++)] = 1114112;
  }
  



  public UnicodeSet(UnicodeSet other)
  {
    set(other);
  }
  







  public UnicodeSet(int start, int end)
  {
    this();
    complement(start, end);
  }
  







  public UnicodeSet(String pattern)
  {
    this(pattern, true);
  }
  









  public UnicodeSet(String pattern, boolean ignoreWhitespace)
  {
    this(pattern, ignoreWhitespace ? 1 : 0);
  }
  









  public UnicodeSet(String pattern, int options)
  {
    this();
    applyPattern(pattern, options);
  }
  











  public UnicodeSet(String pattern, ParsePosition pos, SymbolTable symbols)
  {
    this();
    applyPattern(pattern, pos, symbols, 1);
  }
  





























  public Object clone()
  {
    return new UnicodeSet(this);
  }
  








  public UnicodeSet set(int start, int end)
  {
    clear();
    complement(start, end);
    return this;
  }
  





  public UnicodeSet set(UnicodeSet other)
  {
    list = ((int[])list.clone());
    len = len;
    pat = pat;
    strings = ((TreeSet)strings.clone());
    return this;
  }
  








  public final UnicodeSet applyPattern(String pattern)
  {
    return applyPattern(pattern, true);
  }
  










  public UnicodeSet applyPattern(String pattern, boolean ignoreWhitespace)
  {
    return applyPattern(pattern, ignoreWhitespace ? 1 : 0);
  }
  










  public UnicodeSet applyPattern(String pattern, int options)
  {
    ParsePosition pos = new ParsePosition(0);
    applyPattern(pattern, pos, null, options);
    
    int i = pos.getIndex();
    

    if ((options & 0x1) != 0) {
      i = Utility.skipWhitespace(pattern, i);
    }
    
    if (i != pattern.length()) {
      throw new IllegalArgumentException("Parse of \"" + pattern + "\" failed at " + i);
    }
    
    return this;
  }
  




  public static boolean resemblesPattern(String pattern, int pos)
  {
    return ((pos + 1 < pattern.length()) && (pattern.charAt(pos) == '[')) || (resemblesPropertyPattern(pattern, pos));
  }
  





  private static void _appendToPat(StringBuffer buf, String s, boolean escapeUnprintable)
  {
    for (int i = 0; i < s.length(); i += UTF16.getCharCount(i)) {
      _appendToPat(buf, UTF16.charAt(s, i), escapeUnprintable);
    }
  }
  



  private static void _appendToPat(StringBuffer buf, int c, boolean escapeUnprintable)
  {
    if ((escapeUnprintable) && (Utility.isUnprintable(c)))
    {

      if (Utility.escapeUnprintable(buf, c)) {
        return;
      }
    }
    
    switch (c) {
    case 36: 
    case 38: 
    case 45: 
    case 58: 
    case 91: 
    case 92: 
    case 93: 
    case 94: 
    case 123: 
    case 125: 
      buf.append('\\');
      break;
    
    default: 
      if (UCharacterProperty.isRuleWhiteSpace(c)) {
        buf.append('\\');
      }
      break;
    }
    UTF16.append(buf, c);
  }
  





  public String toPattern(boolean escapeUnprintable)
  {
    StringBuffer result = new StringBuffer();
    return _toPattern(result, escapeUnprintable).toString();
  }
  





  private StringBuffer _toPattern(StringBuffer result, boolean escapeUnprintable)
  {
    if (pat != null)
    {
      int backslashCount = 0;
      for (int i = 0; i < pat.length();) {
        int c = UTF16.charAt(pat, i);
        i += UTF16.getCharCount(c);
        if ((escapeUnprintable) && (Utility.isUnprintable(c)))
        {



          if (backslashCount % 2 == 1) {
            result.setLength(result.length() - 1);
          }
          Utility.escapeUnprintable(result, c);
          backslashCount = 0;
        } else {
          UTF16.append(result, c);
          if (c == 92) {
            backslashCount++;
          } else {
            backslashCount = 0;
          }
        }
      }
      return result;
    }
    
    return _generatePattern(result, escapeUnprintable);
  }
  






  public StringBuffer _generatePattern(StringBuffer result, boolean escapeUnprintable)
  {
    result.append('[');
    










    int count = getRangeCount();
    



    if ((count > 1) && (getRangeStart(0) == 0) && (getRangeEnd(count - 1) == 1114111))
    {



      result.append('^');
      
      for (int i = 1; i < count; i++) {
        int start = getRangeEnd(i - 1) + 1;
        int end = getRangeStart(i) - 1;
        _appendToPat(result, start, escapeUnprintable);
        if (start != end) {
          result.append('-');
          _appendToPat(result, end, escapeUnprintable);
        }
        
      }
    }
    else
    {
      for (int i = 0; i < count; i++) {
        int start = getRangeStart(i);
        int end = getRangeEnd(i);
        _appendToPat(result, start, escapeUnprintable);
        if (start != end) {
          result.append('-');
          _appendToPat(result, end, escapeUnprintable);
        }
      }
    }
    
    if (strings.size() > 0) {
      Iterator it = strings.iterator();
      while (it.hasNext()) {
        result.append('{');
        _appendToPat(result, (String)it.next(), escapeUnprintable);
        result.append('}');
      }
    }
    return result.append(']');
  }
  






  public int size()
  {
    int n = 0;
    int count = getRangeCount();
    for (int i = 0; i < count; i++) {
      n += getRangeEnd(i) - getRangeStart(i) + 1;
    }
    return n + strings.size();
  }
  





  public boolean isEmpty()
  {
    return (len == 1) && (strings.size() == 0);
  }
  














  public boolean matchesIndexValue(int v)
  {
    for (int i = 0; i < getRangeCount(); i++) {
      int low = getRangeStart(i);
      int high = getRangeEnd(i);
      if ((low & 0xFF00) == (high & 0xFF00)) {
        if (((low & 0xFF) <= v) && (v <= (high & 0xFF))) {
          return true;
        }
      } else if (((low & 0xFF) <= v) || (v <= (high & 0xFF))) {
        return true;
      }
    }
    if (strings.size() != 0) {
      Iterator it = strings.iterator();
      while (it.hasNext()) {
        String s = (String)it.next();
        




        int c = UTF16.charAt(s, 0);
        if ((c & 0xFF) == v) {
          return true;
        }
      }
    }
    return false;
  }
  








  public int matches(Replaceable text, int[] offset, int limit, boolean incremental)
  {
    if (offset[0] == limit)
    {


      if (contains(65535)) {
        return incremental ? 1 : 2;
      }
      return 0;
    }
    
    if (strings.size() != 0)
    {







      Iterator it = strings.iterator();
      boolean forward = offset[0] < limit;
      



      char firstChar = text.charAt(offset[0]);
      


      int highWaterLength = 0;
      
      while (it.hasNext()) {
        String trial = (String)it.next();
        





        char c = trial.charAt(forward ? 0 : trial.length() - 1);
        


        if ((forward) && (c > firstChar)) break;
        if (c == firstChar)
        {
          int len = matchRest(text, offset[0], limit, trial);
          
          if (incremental) {
            int maxLen = forward ? limit - offset[0] : offset[0] - limit;
            if (len == maxLen)
            {
              return 1;
            }
          }
          
          if (len == trial.length())
          {
            if (len > highWaterLength) {
              highWaterLength = len;
            }
            

            if ((forward) && (len < highWaterLength)) {
              break;
            }
          }
        }
      }
      


      if (highWaterLength != 0) {
        offset[0] += (forward ? highWaterLength : -highWaterLength);
        return 2;
      }
    }
    return super.matches(text, offset, limit, incremental);
  }
  






















  private static int matchRest(Replaceable text, int start, int limit, String s)
  {
    int slen = s.length();
    int maxLen; if (start < limit) {
      maxLen = limit - start;
      if (maxLen > slen) maxLen = slen;
      for (int i = 1; i < maxLen; i++) {
        if (text.charAt(start + i) != s.charAt(i)) return 0;
      }
    } else {
      maxLen = start - limit;
      if (maxLen > slen) maxLen = slen;
      slen--;
      for (int i = 1; i < maxLen; i++) {
        if (text.charAt(start - i) != s.charAt(slen - i)) return 0;
      }
    }
    return maxLen;
  }
  







  public void addMatchSetTo(UnicodeSet toUnionTo)
  {
    toUnionTo.addAll(this);
  }
  







  public int indexOf(int c)
  {
    if ((c < 0) || (c > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(c, 6));
    }
    int i = 0;
    int n = 0;
    for (;;) {
      int start = list[(i++)];
      if (c < start) {
        return -1;
      }
      int limit = list[(i++)];
      if (c < limit) {
        return n + c - start;
      }
      n += limit - start;
    }
  }
  








  public int charAt(int index)
  {
    if (index >= 0)
    {


      int len2 = len & 0xFFFFFFFE;
      for (int i = 0; i < len2;) {
        int start = list[(i++)];
        int count = list[(i++)] - start;
        if (index < count) {
          return start + index;
        }
        index -= count;
      }
    }
    return -1;
  }
  











  public UnicodeSet add(int start, int end)
  {
    if ((start < 0) || (start > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
    }
    if ((end < 0) || (end > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
    }
    if (start < end) {
      add(range(start, end), 2, 0);
    } else if (start == end) {
      add(start);
    }
    return this;
  }
  
























  public final UnicodeSet add(int c)
  {
    if ((c < 0) || (c > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(c, 6));
    }
    



    int i = findCodePoint(c);
    

    if ((i & 0x1) != 0) { return this;
    }
    











    if (c == list[i] - 1)
    {
      list[i] = c;
      
      if (c == 1114111) {
        ensureCapacity(len + 1);
        list[(len++)] = 1114112;
      }
      if ((i > 0) && (c == list[(i - 1)]))
      {




        System.arraycopy(list, i + 1, list, i - 1, len - i - 1);
        len -= 2;
      }
      
    }
    else if ((i > 0) && (c == list[(i - 1)]))
    {
      list[(i - 1)] += 1;








    }
    else
    {








      if (len + 2 > list.length) {
        int[] temp = new int[len + 2 + 16];
        if (i != 0) System.arraycopy(list, 0, temp, 0, i);
        System.arraycopy(list, i, temp, i + 2, len - i);
        list = temp;
      } else {
        System.arraycopy(list, i, list, i + 2, len - i);
      }
      
      list[i] = c;
      list[(i + 1)] = (c + 1);
      len += 2;
    }
    
    pat = null;
    return this;
  }
  










  public final UnicodeSet add(String s)
  {
    int cp = getSingleCP(s);
    if (cp < 0) {
      strings.add(s);
      pat = null;
    } else {
      add(cp, cp);
    }
    return this;
  }
  




  private static int getSingleCP(String s)
  {
    if (s.length() < 1) {
      throw new IllegalArgumentException("Can't use zero-length strings in UnicodeSet");
    }
    if (s.length() > 2) return -1;
    if (s.length() == 1) { return s.charAt(0);
    }
    
    int cp = UTF16.charAt(s, 0);
    if (cp > 65535) {
      return cp;
    }
    return -1;
  }
  



  public final UnicodeSet addAll(String s)
  {
    int cp;
    


    for (int i = 0; i < s.length(); i += UTF16.getCharCount(cp)) {
      cp = UTF16.charAt(s, i);
      add(cp, cp);
    }
    return this;
  }
  






  public final UnicodeSet retainAll(String s)
  {
    return retainAll(fromAll(s));
  }
  






  public final UnicodeSet complementAll(String s)
  {
    return complementAll(fromAll(s));
  }
  






  public final UnicodeSet removeAll(String s)
  {
    return removeAll(fromAll(s));
  }
  






  public static UnicodeSet from(String s)
  {
    return new UnicodeSet().add(s);
  }
  






  public static UnicodeSet fromAll(String s)
  {
    return new UnicodeSet().addAll(s);
  }
  











  public UnicodeSet retain(int start, int end)
  {
    if ((start < 0) || (start > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
    }
    if ((end < 0) || (end > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
    }
    if (start <= end) {
      retain(range(start, end), 2, 0);
    } else {
      clear();
    }
    return this;
  }
  







  public final UnicodeSet retain(int c)
  {
    return retain(c, c);
  }
  







  public final UnicodeSet retain(String s)
  {
    int cp = getSingleCP(s);
    if (cp < 0) {
      boolean isIn = strings.contains(s);
      if ((isIn) && (size() == 1)) {
        return this;
      }
      clear();
      strings.add(s);
      pat = null;
    } else {
      retain(cp, cp);
    }
    return this;
  }
  











  public UnicodeSet remove(int start, int end)
  {
    if ((start < 0) || (start > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
    }
    if ((end < 0) || (end > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
    }
    if (start <= end) {
      retain(range(start, end), 2, 2);
    }
    return this;
  }
  







  public final UnicodeSet remove(int c)
  {
    return remove(c, c);
  }
  







  public final UnicodeSet remove(String s)
  {
    int cp = getSingleCP(s);
    if (cp < 0) {
      strings.remove(s);
      pat = null;
    } else {
      remove(cp, cp);
    }
    return this;
  }
  











  public UnicodeSet complement(int start, int end)
  {
    if ((start < 0) || (start > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
    }
    if ((end < 0) || (end > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
    }
    if (start <= end) {
      xor(range(start, end), 2, 0);
    }
    pat = null;
    return this;
  }
  





  public final UnicodeSet complement(int c)
  {
    return complement(c, c);
  }
  




  public UnicodeSet complement()
  {
    if (list[0] == 0) {
      System.arraycopy(list, 1, list, 0, len - 1);
      len -= 1;
    } else {
      ensureCapacity(len + 1);
      System.arraycopy(list, 0, list, 1, len);
      list[0] = 0;
      len += 1;
    }
    pat = null;
    return this;
  }
  








  public final UnicodeSet complement(String s)
  {
    int cp = getSingleCP(s);
    if (cp < 0) {
      if (strings.contains(s)) strings.remove(s); else
        strings.add(s);
      pat = null;
    } else {
      complement(cp, cp);
    }
    return this;
  }
  





  public boolean contains(int c)
  {
    if ((c < 0) || (c > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(c, 6));
    }
    









    int i = findCodePoint(c);
    
    return (i & 0x1) != 0;
  }
  




















  private final int findCodePoint(int c)
  {
    if (c < list[0]) { return 0;
    }
    
    if ((len >= 2) && (c >= list[(len - 2)])) return len - 1;
    int lo = 0;
    int hi = len - 1;
    
    for (;;)
    {
      int i = lo + hi >>> 1;
      if (i == lo) return hi;
      if (c < list[i]) {
        hi = i;
      } else {
        lo = i;
      }
    }
  }
  


























































































































  public boolean contains(int start, int end)
  {
    if ((start < 0) || (start > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
    }
    if ((end < 0) || (end > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
    }
    



    int i = findCodePoint(start);
    return ((i & 0x1) != 0) && (end < list[i]);
  }
  







  public final boolean contains(String s)
  {
    int cp = getSingleCP(s);
    if (cp < 0) {
      return strings.contains(s);
    }
    return contains(cp);
  }
  










  public boolean containsAll(UnicodeSet c)
  {
    int n = c.getRangeCount();
    for (int i = 0; i < n; i++) {
      if (!contains(c.getRangeStart(i), c.getRangeEnd(i))) {
        return false;
      }
    }
    if (!strings.containsAll(strings)) return false;
    return true;
  }
  



  public boolean containsAll(String s)
  {
    int cp;
    


    for (int i = 0; i < s.length(); i += UTF16.getCharCount(cp)) {
      cp = UTF16.charAt(s, i);
      if (!contains(cp)) return false;
    }
    return true;
  }
  







  public boolean containsNone(int start, int end)
  {
    if ((start < 0) || (start > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
    }
    if ((end < 0) || (end > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
    }
    int i = -1;
    for (;;) {
      if (start < list[(++i)]) break;
    }
    return ((i & 0x1) == 0) && (end < list[i]);
  }
  









  public boolean containsNone(UnicodeSet c)
  {
    int n = c.getRangeCount();
    for (int i = 0; i < n; i++) {
      if (!containsNone(c.getRangeStart(i), c.getRangeEnd(i))) {
        return false;
      }
    }
    if (!SortedSetRelation.hasRelation(strings, 5, strings)) return false;
    return true;
  }
  



  public boolean containsNone(String s)
  {
    int cp;
    


    for (int i = 0; i < s.length(); i += UTF16.getCharCount(cp)) {
      cp = UTF16.charAt(s, i);
      if (contains(cp)) return false;
    }
    return true;
  }
  







  public final boolean containsSome(int start, int end)
  {
    return !containsNone(start, end);
  }
  






  public final boolean containsSome(UnicodeSet s)
  {
    return !containsNone(s);
  }
  






  public final boolean containsSome(String s)
  {
    return !containsNone(s);
  }
  










  public UnicodeSet addAll(UnicodeSet c)
  {
    add(list, len, 0);
    strings.addAll(strings);
    return this;
  }
  









  public UnicodeSet retainAll(UnicodeSet c)
  {
    retain(list, len, 0);
    strings.retainAll(strings);
    return this;
  }
  









  public UnicodeSet removeAll(UnicodeSet c)
  {
    retain(list, len, 2);
    strings.removeAll(strings);
    return this;
  }
  








  public UnicodeSet complementAll(UnicodeSet c)
  {
    xor(list, len, 0);
    SortedSetRelation.doOperation(strings, 5, strings);
    return this;
  }
  




  public UnicodeSet clear()
  {
    list[0] = 1114112;
    len = 1;
    pat = null;
    strings.clear();
    return this;
  }
  






  public int getRangeCount()
  {
    return len / 2;
  }
  








  public int getRangeStart(int index)
  {
    return list[(index * 2)];
  }
  








  public int getRangeEnd(int index)
  {
    return list[(index * 2 + 1)] - 1;
  }
  




  public UnicodeSet compact()
  {
    if (len != list.length) {
      int[] temp = new int[len];
      System.arraycopy(list, 0, temp, 0, len);
      list = temp;
    }
    rangeList = null;
    buffer = null;
    return this;
  }
  









  public boolean equals(Object o)
  {
    try
    {
      UnicodeSet that = (UnicodeSet)o;
      if (len != len) return false;
      for (int i = 0; i < len; i++) {
        if (list[i] != list[i]) return false;
      }
      if (!strings.equals(strings)) return false;
    } catch (Exception e) {
      return false;
    }
    return true;
  }
  






  public int hashCode()
  {
    int result = len;
    for (int i = 0; i < len; i++) {
      result *= 1000003;
      result += list[i];
    }
    return result;
  }
  



  public String toString()
  {
    return toPattern(true);
  }
  































  void applyPattern(String pattern, ParsePosition pos, SymbolTable symbols, int options)
  {
    StringBuffer rebuiltPat = new StringBuffer();
    _applyPattern(pattern, pos, symbols, rebuiltPat, options);
    pat = rebuiltPat.toString();
  }
  







  void _applyPattern(String pattern, ParsePosition pos, SymbolTable symbols, StringBuffer rebuiltPat, int options)
  {
    boolean rebuildPattern = false;
    StringBuffer newPat = new StringBuffer("[");
    int nestedPatStart = -1;
    boolean nestedPatDone = false;
    StringBuffer multiCharBuffer = new StringBuffer();
    

    boolean invert = false;
    clear();
    
    int NONE = -1;
    int lastChar = -1;
    boolean isLastLiteral = false;
    char lastOp = '\000';
    





















    int mode = 0;
    int start = pos.getIndex();
    int i = start;
    int limit = pattern.length();
    



    char[] varValueBuffer = null;
    int ivarValueBuffer = 0;
    int anchor = 0;
    
    while (i < limit)
    {







      UnicodeSet nestedSet = null;
      boolean isLiteral = false;
      int c; if (varValueBuffer != null) {
        if (ivarValueBuffer < varValueBuffer.length) {
          c = UTF16.charAt(varValueBuffer, 0, varValueBuffer.length, ivarValueBuffer);
          ivarValueBuffer += UTF16.getCharCount(c);
          UnicodeMatcher m = symbols.lookupMatcher(c);
          try {
            nestedSet = (UnicodeSet)m;
          } catch (ClassCastException e) {
            throw new IllegalArgumentException("Syntax error");
          }
          nestedPatDone = false;
        } else {
          varValueBuffer = null;
          c = UTF16.charAt(pattern, i);
          i += UTF16.getCharCount(c);
        }
      } else {
        c = UTF16.charAt(pattern, i);
        i += UTF16.getCharCount(c);
      }
      
      if (((options & 0x1) == 0) || (!UCharacterProperty.isRuleWhiteSpace(c)))
      {



        if (anchor > 0) {
          anchor++;
        }
        

        switch (mode) {
        case 0: 
          if (resemblesPropertyPattern(pattern, i - 1)) {
            mode = 3;
          } else {
            if (c == 91) {
              mode = 1;
              continue;
            }
            throw new IllegalArgumentException("Missing opening '['");
          }
          break;
        case 1:  mode = 2;
          switch (c) {
          case 94: 
            invert = true;
            newPat.append((char)c);
            mode = 15;
            break;
          case 45: 
            isLiteral = true;
          }
          
          break;
        case 15: 
          mode = 2;
          if (c == 45) {
            isLiteral = true;
          }
          




          break;
        }
        
        



        if (varValueBuffer == null)
        {


          if (resemblesPropertyPattern(pattern, i - 1)) {
            ParsePosition pp = new ParsePosition(i - 1);
            nestedSet = new UnicodeSet();
            nestedSet.applyPropertyPattern(pattern, pp);
            nestedPatStart = newPat.length();
            nestedPatDone = true;
            
            switch (lastOp) {
            case '&': 
            case '-': 
              newPat.append(lastOp);
            }
            
            



            if (mode == 3) {
              newPat.deleteCharAt(0);
            }
            newPat.append(pattern.substring(i - 1, pp.getIndex()));
            rebuildPattern = true;
            
            i = pp.getIndex();
            
            if (mode == 3)
            {


              set(nestedSet);
              mode = 5;
              break;


            }
            



          }
          else if (c == 92) {
            int[] offset = { i };
            int escaped = Utility.unescapeAt(pattern, offset);
            if (escaped == -1) {
              int sta = Math.max(i - 8, 0);
              int lim = Math.min(i + 16, pattern.length());
              throw new IllegalArgumentException("Invalid escape sequence " + pattern.substring(sta, i - 1) + "|" + pattern.substring(i - 1, lim));
            }
            


            i = offset[0];
            isLiteral = true;
            c = escaped;



          }
          else
          {


            if ((symbols != null) && (!isLiteral) && (c == 36)) {
              pos.setIndex(i);
              String name = symbols.parseReference(pattern, pos, limit);
              if (name != null) {
                varValueBuffer = symbols.lookup(name);
                if (varValueBuffer == null) {
                  throw new IllegalArgumentException("Undefined variable: " + name);
                }
                
                ivarValueBuffer = 0;
                i = pos.getIndex(); continue;
              }
              

              anchor = 1;
              
              continue;
            }
            



            if ((!isLiteral) && (c == 91))
            {
              nestedPatStart = newPat.length();
              


              pos.setIndex(--i);
              switch (lastOp) {
              case '&': 
              case '-': 
                newPat.append(lastOp);
              }
              
              nestedSet = new UnicodeSet();
              nestedSet._applyPattern(pattern, pos, symbols, newPat, options);
              nestedPatDone = true;
              i = pos.getIndex();
            } else if ((!isLiteral) && (c == 123))
            {
              int length = 0;
              int st = i;
              multiCharBuffer.setLength(0);
              while (i < pattern.length()) {
                int ch = UTF16.charAt(pattern, i);
                i += UTF16.getCharCount(ch);
                if (ch == 125) {
                  length = -length;
                  break; }
                if (ch == 92) {
                  int[] offset = { i };
                  ch = Utility.unescapeAt(pattern, offset);
                  if (ch == -1) {
                    int sta = Math.max(i - 8, 0);
                    int lim = Math.min(i + 16, pattern.length());
                    throw new IllegalArgumentException("Invalid escape sequence " + pattern.substring(sta, i - 1) + "|" + pattern.substring(i - 1, lim));
                  }
                  


                  i = offset[0];
                }
                length--;
                UTF16.append(multiCharBuffer, ch);
              }
              if (length < 1) {
                throw new IllegalArgumentException("Invalid multicharacter string");
              }
              


              add(multiCharBuffer.toString());
              newPat.append('{').append(pattern.substring(st, i));
              rebuildPattern = true;
              continue;
            }
          }
        }
        







        if (nestedSet != null) {
          if (lastChar != -1) {
            if (lastOp != 0) {
              throw new IllegalArgumentException("Illegal rhs for " + lastChar + lastOp);
            }
            add(lastChar, lastChar);
            if (nestedPatDone)
            {



              StringBuffer s = new StringBuffer();
              _appendToPat(s, lastChar, false);
              newPat.insert(nestedPatStart, s.toString());
            } else {
              _appendToPat(newPat, lastChar, false);
            }
            lastChar = -1;
          }
          switch (lastOp) {
          case '-': 
            removeAll(nestedSet);
            break;
          case '&': 
            retainAll(nestedSet);
            break;
          case '\000': 
            addAll(nestedSet);
          }
          
          


          if (!nestedPatDone) {
            if (lastOp != 0) {
              newPat.append(lastOp);
            }
            nestedSet._toPattern(newPat, false);
          }
          rebuildPattern = true;
          
          lastOp = '\000';
        } else {
          if ((!isLiteral) && (c == 93))
          {

            if ((anchor > 2) || (anchor == 1)) {
              throw new IllegalArgumentException("Syntax error near $" + pattern);
            }
            
            if (anchor == 2) {
              rebuildPattern = true;
              newPat.append('$');
              add(65535);
            }
            mode = 4;
            break; }
          if ((lastOp == 0) && (!isLiteral) && ((c == 45) || (c == 38))) {
            lastOp = (char)c;
          } else if (lastOp == '-') {
            if ((lastChar >= c) || (lastChar == -1))
            {

              throw new IllegalArgumentException("Invalid range " + lastChar + '-' + c);
            }
            
            add(lastChar, c);
            _appendToPat(newPat, lastChar, false);
            newPat.append('-');
            _appendToPat(newPat, c, false);
            lastOp = '\000';
            lastChar = -1;
          } else { if (lastOp != 0)
            {
              throw new IllegalArgumentException("Unquoted " + lastOp);
            }
            if (lastChar != -1)
            {
              add(lastChar, lastChar);
              _appendToPat(newPat, lastChar, false);
            }
            lastChar = c;
            isLastLiteral = isLiteral;
          }
        }
      } }
    if (mode < 4) {
      throw new IllegalArgumentException("Missing ']'");
    }
    



    if ((lastChar == 36) && (!isLastLiteral)) {
      rebuildPattern = true;
      newPat.append((char)lastChar);
      add(65535);

    }
    else if (lastChar != -1) {
      add(lastChar, lastChar);
      _appendToPat(newPat, lastChar, false);
    }
    

    if (lastOp == '-')
    {
      add(lastOp, lastOp);
      newPat.append('-');
    } else if (lastOp == '&') {
      throw new IllegalArgumentException("Unquoted trailing " + lastOp);
    }
    
    if (mode == 4) {
      newPat.append(']');
    }
    





    if ((options & 0x2) != 0) {
      closeOver(2);
    }
    




    if (invert) {
      complement();
    }
    
    pos.setIndex(i);
    


    if (rebuildPattern) {
      rebuiltPat.append(newPat.toString());
    } else {
      _generatePattern(rebuiltPat, false);
    }
  }
  










  private void ensureCapacity(int newLen)
  {
    if (newLen <= list.length) return;
    int[] temp = new int[newLen + 16];
    System.arraycopy(list, 0, temp, 0, len);
    list = temp;
  }
  
  private void ensureBufferCapacity(int newLen) {
    if ((buffer != null) && (newLen <= buffer.length)) return;
    buffer = new int[newLen + 16];
  }
  


  private int[] range(int start, int end)
  {
    if (rangeList == null) {
      rangeList = new int[] { start, end + 1, 1114112 };
    } else {
      rangeList[0] = start;
      rangeList[1] = (end + 1);
    }
    return rangeList;
  }
  






  private UnicodeSet xor(int[] other, int otherLen, int polarity)
  {
    ensureBufferCapacity(len + otherLen);
    int i = 0;int j = 0;int k = 0;
    int a = list[(i++)];
    int b;
    if ((polarity == 1) || (polarity == 2)) {
      b = 0;
      if (other[j] == 0) {
        j++;
        b = other[j];
      }
    } else {
      b = other[(j++)];
    }
    
    for (;;)
    {
      if (a < b) {
        buffer[(k++)] = a;
        a = list[(i++)];
      } else if (b < a) {
        buffer[(k++)] = b;
        b = other[(j++)];
      } else { if (a == 1114112)
          break;
        a = list[(i++)];
        b = other[(j++)];
      } }
    buffer[(k++)] = 1114112;
    len = k;
    



    int[] temp = list;
    list = buffer;
    buffer = temp;
    pat = null;
    return this;
  }
  




  private UnicodeSet add(int[] other, int otherLen, int polarity)
  {
    ensureBufferCapacity(len + otherLen);
    int i = 0;int j = 0;int k = 0;
    int a = list[(i++)];
    int b = other[(j++)];
    

    for (;;)
    {
      switch (polarity) {
      case 0: 
        if (a < b)
        {
          if ((k > 0) && (a <= buffer[(k - 1)]))
          {
            a = max(list[i], buffer[(--k)]);
          }
          else {
            buffer[(k++)] = a;
            a = list[i];
          }
          i++;
          polarity ^= 0x1;
        } else if (b < a) {
          if ((k > 0) && (b <= buffer[(k - 1)])) {
            b = max(other[j], buffer[(--k)]);
          } else {
            buffer[(k++)] = b;
            b = other[j];
          }
          j++;
          polarity ^= 0x2;
        } else {
          if (a == 1114112) {
            break label624;
          }
          if ((k > 0) && (a <= buffer[(k - 1)])) {
            a = max(list[i], buffer[(--k)]);
          }
          else {
            buffer[(k++)] = a;
            a = list[i];
          }
          i++;
          polarity ^= 0x1;
          b = other[(j++)];polarity ^= 0x2;
        }
        break;
      case 3: 
        if (b <= a) {
          if (a == 1114112) break label624;
          buffer[(k++)] = a;
        } else {
          if (b == 1114112) break label624;
          buffer[(k++)] = b;
        }
        a = list[(i++)];polarity ^= 0x1;
        b = other[(j++)];polarity ^= 0x2;
        break;
      case 1: 
        if (a < b) {
          buffer[(k++)] = a;a = list[(i++)];polarity ^= 0x1;
        } else if (b < a) {
          b = other[(j++)];polarity ^= 0x2;
        } else {
          if (a == 1114112) break label624;
          a = list[(i++)];polarity ^= 0x1;
          b = other[(j++)];polarity ^= 0x2;
        }
        break;
      case 2: 
        if (b < a) {
          buffer[(k++)] = b;b = other[(j++)];polarity ^= 0x2;
        } else if (a < b) {
          a = list[(i++)];polarity ^= 0x1;
        } else {
          if (a == 1114112) break label624;
          a = list[(i++)];polarity ^= 0x1;
          b = other[(j++)];polarity ^= 0x2;
        }
        break; }
    }
    label624:
    buffer[(k++)] = 1114112;
    len = k;
    
    int[] temp = list;
    list = buffer;
    buffer = temp;
    pat = null;
    return this;
  }
  




  private UnicodeSet retain(int[] other, int otherLen, int polarity)
  {
    ensureBufferCapacity(len + otherLen);
    int i = 0;int j = 0;int k = 0;
    int a = list[(i++)];
    int b = other[(j++)];
    

    for (;;)
    {
      switch (polarity) {
      case 0: 
        if (a < b) {
          a = list[(i++)];polarity ^= 0x1;
        } else if (b < a) {
          b = other[(j++)];polarity ^= 0x2;
        } else {
          if (a == 1114112) break label512;
          buffer[(k++)] = a;a = list[(i++)];polarity ^= 0x1;
          b = other[(j++)];polarity ^= 0x2;
        }
        break;
      case 3: 
        if (a < b) {
          buffer[(k++)] = a;a = list[(i++)];polarity ^= 0x1;
        } else if (b < a) {
          buffer[(k++)] = b;b = other[(j++)];polarity ^= 0x2;
        } else {
          if (a == 1114112) break label512;
          buffer[(k++)] = a;a = list[(i++)];polarity ^= 0x1;
          b = other[(j++)];polarity ^= 0x2;
        }
        break;
      case 1: 
        if (a < b) {
          a = list[(i++)];polarity ^= 0x1;
        } else if (b < a) {
          buffer[(k++)] = b;b = other[(j++)];polarity ^= 0x2;
        } else {
          if (a == 1114112) break label512;
          a = list[(i++)];polarity ^= 0x1;
          b = other[(j++)];polarity ^= 0x2;
        }
        break;
      case 2: 
        if (b < a) {
          b = other[(j++)];polarity ^= 0x2;
        } else if (a < b) {
          buffer[(k++)] = a;a = list[(i++)];polarity ^= 0x1;
        } else {
          if (a == 1114112) break label512;
          a = list[(i++)];polarity ^= 0x1;
          b = other[(j++)];polarity ^= 0x2;
        }
        break; }
    }
    label512:
    buffer[(k++)] = 1114112;
    len = k;
    
    int[] temp = list;
    list = buffer;
    buffer = temp;
    pat = null;
    return this;
  }
  
  private static final int max(int a, int b) {
    return a > b ? a : b;
  }
  
  private static abstract interface Filter
  {
    public abstract boolean contains(int paramInt);
  }
  
  private static class NumericValueFilter
    implements UnicodeSet.Filter
  {
    double value;
    
    NumericValueFilter(double value) { this.value = value; }
    
    public boolean contains(int ch) { return UCharacter.getUnicodeNumericValue(ch) == value; }
  }
  
  private static class GeneralCategoryMaskFilter implements UnicodeSet.Filter {
    int mask;
    
    GeneralCategoryMaskFilter(int mask) { this.mask = mask; }
    
    public boolean contains(int ch) { return (1 << UCharacter.getType(ch) & mask) != 0; }
  }
  
  private static class IntPropertyFilter implements UnicodeSet.Filter {
    int prop;
    int value;
    
    IntPropertyFilter(int prop, int value) {
      this.prop = prop;
      this.value = value;
    }
    
    public boolean contains(int ch) { return UCharacter.getIntPropertyValue(ch, prop) == value; }
  }
  


  static final VersionInfo NO_VERSION = VersionInfo.getInstance(0, 0, 0, 0);
  public static final int IGNORE_SPACE = 1;
  public static final int CASE = 2;
  
  private static class VersionFilter implements UnicodeSet.Filter { VersionFilter(VersionInfo version) { this.version = version; }
    
    public boolean contains(int ch) { VersionInfo v = UCharacter.getAge(ch);
      

      return (v != UnicodeSet.NO_VERSION) && (v.compareTo(version) <= 0);
    }
    
    VersionInfo version;
  }
  
  private static synchronized UnicodeSet getInclusions() { if (INCLUSIONS == null) {
      UCharacterProperty property = UCharacterProperty.getInstance();
      INCLUSIONS = property.getInclusions();
    }
    return INCLUSIONS;
  }
  
















  private UnicodeSet applyFilter(Filter filter)
  {
    clear();
    
    int startHasProperty = -1;
    UnicodeSet inclusions = getInclusions();
    int limitRange = inclusions.getRangeCount();
    
    for (int j = 0; j < limitRange; j++)
    {
      int start = inclusions.getRangeStart(j);
      int end = inclusions.getRangeEnd(j);
      

      for (int ch = start; ch <= end; ch++)
      {

        if (filter.contains(ch)) {
          if (startHasProperty < 0) {
            startHasProperty = ch;
          }
        } else if (startHasProperty >= 0) {
          add(startHasProperty, ch - 1);
          startHasProperty = -1;
        }
      }
    }
    if (startHasProperty >= 0) {
      add(startHasProperty, 1114111);
    }
    
    return this;
  }
  






  private static String mungeCharName(String source)
  {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < source.length();) {
      int ch = UTF16.charAt(source, i);
      i += UTF16.getCharCount(ch);
      if (UCharacterProperty.isRuleWhiteSpace(ch)) {
        if ((buf.length() != 0) && (buf.charAt(buf.length() - 1) != ' '))
        {


          ch = 32; }
      } else
        UTF16.append(buf, ch);
    }
    if ((buf.length() != 0) && (buf.charAt(buf.length() - 1) == ' '))
    {
      buf.setLength(buf.length() - 1);
    }
    return buf.toString();
  }
  


























  public UnicodeSet applyIntPropertyValue(int prop, int value)
  {
    if (prop == 8192) {
      applyFilter(new GeneralCategoryMaskFilter(value));
    } else {
      applyFilter(new IntPropertyFilter(prop, value));
    }
    return this;
  }
  





























  public UnicodeSet applyPropertyAlias(String propertyAlias, String valueAlias)
  {
    boolean mustNotBeEmpty = false;
    int p;
    int v; if (valueAlias.length() > 0) {
      p = UCharacter.getPropertyEnum(propertyAlias);
      

      if (p == 4101) {
        p = 8192;
      }
      
      if (((p >= 0) && (p < 35)) || ((p >= 4096) && (p < 4108)) || ((p >= 8192) && (p < 8193)))
      {
        try
        {
          v = UCharacter.getPropertyValueEnum(p, valueAlias);
        }
        catch (IllegalArgumentException e) {
          if (p == 4098) {
            v = Integer.parseInt(Utility.deleteRuleWhiteSpace(valueAlias));
            

            mustNotBeEmpty = true;
          } else {
            throw e;
          }
          
        }
      }
      else
      {
        switch (p)
        {
        case 12288: 
          double value = Double.parseDouble(Utility.deleteRuleWhiteSpace(valueAlias));
          applyFilter(new NumericValueFilter(value));
          return this;
        




        case 16389: 
        case 16395: 
          String buf = mungeCharName(valueAlias);
          int ch = p == 16389 ? UCharacter.getCharFromExtendedName(buf) : UCharacter.getCharFromName1_0(buf);
          


          if (ch == -1) {
            throw new IllegalArgumentException("Invalid character name");
          }
          clear();
          add(ch);
          return this;
        




        case 16384: 
          VersionInfo version = VersionInfo.getInstance(mungeCharName(valueAlias));
          applyFilter(new VersionFilter(version));
          return this;
        }
        
        


        throw new IllegalArgumentException("Unsupported property");
      }
      

    }
    else
    {
      try
      {
        p = 8192;
        v = UCharacter.getPropertyValueEnum(p, propertyAlias);
      } catch (IllegalArgumentException e) {
        try {
          p = 4106;
          v = UCharacter.getPropertyValueEnum(p, propertyAlias);
        } catch (IllegalArgumentException e2) {
          try {
            p = UCharacter.getPropertyEnum(propertyAlias);
          } catch (IllegalArgumentException e3) {
            p = -1;
          }
          if ((p >= 0) && (p < 35)) {
            v = 1;
          } else { if (p == -1) {
              if (0 == UPropertyAliases.compare("ANY", propertyAlias)) {
                set(0, 1114111);
                return this; }
              if (0 == UPropertyAliases.compare("ASCII", propertyAlias)) {
                set(0, 127);
                return this;
              }
              
              throw new IllegalArgumentException("Invalid property alias");
            }
            


            throw new IllegalArgumentException("Missing property value");
          }
        }
      }
    }
    
    applyIntPropertyValue(p, v);
    
    if ((mustNotBeEmpty) && (isEmpty()))
    {

      throw new IllegalArgumentException("Invalid property value");
    }
    
    return this;
  }
  








  private static boolean resemblesPropertyPattern(String pattern, int pos)
  {
    if (pos + 5 > pattern.length()) {
      return false;
    }
    

    return (pattern.regionMatches(pos, "[:", 0, 2)) || (pattern.regionMatches(true, pos, "\\p", 0, 2)) || (pattern.regionMatches(pos, "\\N", 0, 2));
  }
  




  private UnicodeSet applyPropertyPattern(String pattern, ParsePosition ppos)
  {
    int pos = ppos.getIndex();
    



    if (pos + 5 > pattern.length()) {
      return null;
    }
    
    boolean posix = false;
    boolean isName = false;
    boolean invert = false;
    

    if (pattern.regionMatches(pos, "[:", 0, 2)) {
      posix = true;
      pos = Utility.skipWhitespace(pattern, pos + 2);
      if ((pos < pattern.length()) && (pattern.charAt(pos) == '^')) {
        pos++;
        invert = true;
      }
    } else if ((pattern.regionMatches(true, pos, "\\p", 0, 2)) || (pattern.regionMatches(pos, "\\N", 0, 2)))
    {
      char c = pattern.charAt(pos + 1);
      invert = c == 'P';
      isName = c == 'N';
      pos = Utility.skipWhitespace(pattern, pos + 2);
      if ((pos == pattern.length()) || (pattern.charAt(pos++) != '{'))
      {
        return null;
      }
    }
    else {
      return null;
    }
    

    int close = pattern.indexOf(posix ? ":]" : "}", pos);
    if (close < 0)
    {
      return null;
    }
    



    int equals = pattern.indexOf('=', pos);
    String propName;
    String valueName; if ((equals >= 0) && (equals < close) && (!isName))
    {
      propName = pattern.substring(pos, equals);
      valueName = pattern.substring(equals + 1, close);

    }
    else
    {
      propName = pattern.substring(pos, close);
      valueName = "";
      

      if (isName)
      {




        valueName = propName;
        propName = "na";
      }
    }
    
    applyPropertyAlias(propName, valueName);
    
    if (invert) {
      complement();
    }
    

    ppos.setIndex(close + (posix ? 2 : 1));
    
    return this;
  }
  
















































  public UnicodeSet closeOver(int attribute)
  {
    if ((attribute & 0x2) != 0) {
      UnicodeSet foldSet = new UnicodeSet();
      int n = getRangeCount();
      for (int i = 0; i < n; i++) {
        int start = getRangeStart(i);
        int end = getRangeEnd(i);
        for (int cp = start; cp <= end; cp++) {
          foldSet.caseCloseOne(UTF16.valueOf(cp));
        }
      }
      if (strings.size() > 0) {
        Iterator it = strings.iterator();
        while (it.hasNext()) {
          foldSet.caseCloseOne(UCharacter.foldCase((String)it.next(), true));
        }
      }
      
      set(foldSet);
    }
    return this;
  }
  











  private void caseCloseOne(String folded)
  {
    String[] equiv = (String[])CASE_EQUIV_CLASS.get(folded);
    if (equiv == null)
    {

      add(folded);
    } else {
      for (int i = 0; i < equiv.length; i++) {
        add(equiv[i]);
      }
    }
  }
  





















  private static Map CASE_EQUIV_CLASS = null;
  






































  private static final boolean DEFAULT_CASE_MAP = true;
  






































  private static final String CASE_PAIRS = "AaBbCcDdEeFfGgHhIiJjLlMmNnOoPpQqRrTtUuVvWwXxYyZzÀàÁáÂâÃãÄäÆæÇçÈèÉéÊêËëÌìÍíÎîÏïÐðÑñÒòÓóÔôÕõÖöØøÙùÚúÛûÜüÝýÞþÿŸĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįĲĳĴĵĶķĹĺĻļĽľĿŀŁłŃńŅņŇňŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŹźŻżŽžƂƃƄƅƇƈƋƌƑƒƕǶƘƙƞȠƠơƢƣƤƥƧƨƬƭƯưƳƴƵƶƸƹƼƽƿǷǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜƎǝǞǟǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǯǴǵǸǹǺǻǼǽǾǿȀȁȂȃȄȅȆȇȈȉȊȋȌȍȎȏȐȑȒȓȔȕȖȗȘșȚțȜȝȞȟȢȣȤȥȦȧȨȩȪȫȬȭȮȯȰȱȲȳƁɓƆɔƉɖƊɗƏəƐɛƓɠƔɣƗɨƖɩƜɯƝɲƟɵƦʀƩʃƮʈƱʊƲʋƷʒΆάΈέΉήΊίΑαΓγΔδΖζΗηΛλΝνΞξΟοΤτΥυΧχΨψΪϊΫϋΌόΎύΏώϘϙϚϛϜϝϞϟϠϡϢϣϤϥϦϧϨϩϪϫϬϭϮϯАаБбВвГгДдЕеЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯяЀѐЁёЂђЃѓЄєЅѕІіЇїЈјЉљЊњЋћЌќЍѝЎўЏџѠѡѢѣѤѥѦѧѨѩѪѫѬѭѮѯѰѱѲѳѴѵѶѷѸѹѺѻѼѽѾѿҀҁҊҋҌҍҎҏҐґҒғҔҕҖҗҘҙҚқҜҝҞҟҠҡҢңҤҥҦҧҨҩҪҫҬҭҮүҰұҲҳҴҵҶҷҸҹҺһҼҽҾҿӁӂӃӄӅӆӇӈӉӊӋӌӍӎӐӑӒӓӔӕӖӗӘәӚӛӜӝӞӟӠӡӢӣӤӥӦӧӨөӪӫӬӭӮӯӰӱӲӳӴӵӸӹԀԁԂԃԄԅԆԇԈԉԊԋԌԍԎԏԱաԲբԳգԴդԵեԶզԷէԸըԹթԺժԻիԼլԽխԾծԿկՀհՁձՂղՃճՄմՅյՆնՇշՈոՉչՊպՋջՌռՍսՎվՏտՐրՑցՒւՓփՔքՕօՖֆḀḁḂḃḄḅḆḇḈḉḊḋḌḍḎḏḐḑḒḓḔḕḖḗḘḙḚḛḜḝḞḟḠḡḢḣḤḥḦḧḨḩḪḫḬḭḮḯḰḱḲḳḴḵḶḷḸḹḺḻḼḽḾḿṀṁṂṃṄṅṆṇṈṉṊṋṌṍṎṏṐṑṒṓṔṕṖṗṘṙṚṛṜṝṞṟṢṣṤṥṦṧṨṩṪṫṬṭṮṯṰṱṲṳṴṵṶṷṸṹṺṻṼṽṾṿẀẁẂẃẄẅẆẇẈẉẊẋẌẍẎẏẐẑẒẓẔẕẠạẢảẤấẦầẨẩẪẫẬậẮắẰằẲẳẴẵẶặẸẹẺẻẼẽẾếỀềỂểỄễỆệỈỉỊịỌọỎỏỐốỒồỔổỖỗỘộỚớỜờỞởỠỡỢợỤụỦủỨứỪừỬửỮữỰựỲỳỴỵỶỷỸỹἀἈἁἉἂἊἃἋἄἌἅἍἆἎἇἏἐἘἑἙἒἚἓἛἔἜἕἝἠἨἡἩἢἪἣἫἤἬἥἭἦἮἧἯἰἸἱἹἲἺἳἻἴἼἵἽἶἾἷἿὀὈὁὉὂὊὃὋὄὌὅὍὑὙὓὛὕὝὗὟὠὨὡὩὢὪὣὫὤὬὥὭὦὮὧὯὰᾺάΆὲῈέΈὴῊήΉὶῚίΊὸῸόΌὺῪύΎὼῺώΏᾰᾸᾱᾹῐῘῑῙῠῨῡῩῥῬⅠⅰⅡⅱⅢⅲⅣⅳⅤⅴⅥⅵⅦⅶⅧⅷⅨⅸⅩⅹⅪⅺⅫⅻⅬⅼⅭⅽⅮⅾⅯⅿⒶⓐⒷⓑⒸⓒⒹⓓⒺⓔⒻⓕⒼⓖⒽⓗⒾⓘⒿⓙⓀⓚⓁⓛⓂⓜⓃⓝⓄⓞⓅⓟⓆⓠⓇⓡⓈⓢⓉⓣⓊⓤⓋⓥⓌⓦⓍⓧⓎⓨⓏⓩＡａＢｂＣｃＤｄＥｅＦｆＧｇＨｈＩｉＪｊＫｋＬｌＭｍＮｎＯｏＰｐＱｑＲｒＳｓＴｔＵｕＶｖＷｗＸｘＹｙＺｚ";
  





































  private static final String[][] CASE_NONPAIRS = { { "aʾ", "ẚ" }, { "ff", "ﬀ" }, { "ffi", "ﬃ" }, { "ffl", "ﬄ" }, { "fi", "ﬁ" }, { "fl", "ﬂ" }, { "ẖ", "ẖ" }, { "i̇", "İ" }, { "ǰ", "ǰ" }, { "K", "k", "K" }, { "S", "s", "ſ" }, { "ss", "ß" }, { "st", "ﬅ", "ﬆ" }, { "ẗ", "ẗ" }, { "ẘ", "ẘ" }, { "ẙ", "ẙ" }, { "Å", "å", "Å" }, { "Ǆ", "ǅ", "ǆ" }, { "Ǉ", "ǈ", "ǉ" }, { "Ǌ", "ǋ", "ǌ" }, { "Ǳ", "ǲ", "ǳ" }, { "ŉ", "ʼn" }, { "άι", "ᾴ" }, { "ήι", "ῄ" }, { "ᾶ", "ᾶ" }, { "ᾶι", "ᾷ" }, { "αι", "ᾳ", "ᾼ" }, { "Β", "β", "ϐ" }, { "Ε", "ε", "ϵ" }, { "ῆ", "ῆ" }, { "ῆι", "ῇ" }, { "ηι", "ῃ", "ῌ" }, { "Θ", "θ", "ϑ", "ϴ" }, { "ͅ", "Ι", "ι", "ι" }, { "ῒ", "ῒ" }, { "ΐ", "ΐ", "ΐ" }, { "ῗ", "ῗ" }, { "ῖ", "ῖ" }, { "Κ", "κ", "ϰ" }, { "µ", "Μ", "μ" }, { "Π", "π", "ϖ" }, { "Ρ", "ρ", "ϱ" }, { "ῤ", "ῤ" }, { "Σ", "ς", "σ", "ϲ" }, { "ῢ", "ῢ" }, { "ΰ", "ΰ", "ΰ" }, { "ῧ", "ῧ" }, { "ὐ", "ὐ" }, { "ὒ", "ὒ" }, { "ὔ", "ὔ" }, { "ὖ", "ὖ" }, { "ῦ", "ῦ" }, { "Φ", "φ", "ϕ" }, { "Ω", "ω", "Ω" }, { "ῶ", "ῶ" }, { "ῶι", "ῷ" }, { "ωι", "ῳ", "ῼ" }, { "ώι", "ῴ" }, { "եւ", "և" }, { "մե", "ﬔ" }, { "մի", "ﬕ" }, { "մխ", "ﬗ" }, { "մն", "ﬓ" }, { "վն", "ﬖ" }, { "Ṡ", "ṡ", "ẛ" }, { "ἀι", "ᾀ", "ᾈ" }, { "ἁι", "ᾁ", "ᾉ" }, { "ἂι", "ᾂ", "ᾊ" }, { "ἃι", "ᾃ", "ᾋ" }, { "ἄι", "ᾄ", "ᾌ" }, { "ἅι", "ᾅ", "ᾍ" }, { "ἆι", "ᾆ", "ᾎ" }, { "ἇι", "ᾇ", "ᾏ" }, { "ἠι", "ᾐ", "ᾘ" }, { "ἡι", "ᾑ", "ᾙ" }, { "ἢι", "ᾒ", "ᾚ" }, { "ἣι", "ᾓ", "ᾛ" }, { "ἤι", "ᾔ", "ᾜ" }, { "ἥι", "ᾕ", "ᾝ" }, { "ἦι", "ᾖ", "ᾞ" }, { "ἧι", "ᾗ", "ᾟ" }, { "ὠι", "ᾠ", "ᾨ" }, { "ὡι", "ᾡ", "ᾩ" }, { "ὢι", "ᾢ", "ᾪ" }, { "ὣι", "ᾣ", "ᾫ" }, { "ὤι", "ᾤ", "ᾬ" }, { "ὥι", "ᾥ", "ᾭ" }, { "ὦι", "ᾦ", "ᾮ" }, { "ὧι", "ᾧ", "ᾯ" }, { "ὰι", "ᾲ" }, { "ὴι", "ῂ" }, { "ὼι", "ῲ" }, { "𐐀", "𐐨" }, { "𐐁", "𐐩" }, { "𐐂", "𐐪" }, { "𐐃", "𐐫" }, { "𐐄", "𐐬" }, { "𐐅", "𐐭" }, { "𐐆", "𐐮" }, { "𐐇", "𐐯" }, { "𐐈", "𐐰" }, { "𐐉", "𐐱" }, { "𐐊", "𐐲" }, { "𐐋", "𐐳" }, { "𐐌", "𐐴" }, { "𐐍", "𐐵" }, { "𐐎", "𐐶" }, { "𐐏", "𐐷" }, { "𐐐", "𐐸" }, { "𐐑", "𐐹" }, { "𐐒", "𐐺" }, { "𐐓", "𐐻" }, { "𐐔", "𐐼" }, { "𐐕", "𐐽" }, { "𐐖", "𐐾" }, { "𐐗", "𐐿" }, { "𐐘", "𐑀" }, { "𐐙", "𐑁" }, { "𐐚", "𐑂" }, { "𐐛", "𐑃" }, { "𐐜", "𐑄" }, { "𐐝", "𐑅" }, { "𐐞", "𐑆" }, { "𐐟", "𐑇" }, { "𐐠", "𐑈" }, { "𐐡", "𐑉" }, { "𐐢", "𐑊" }, { "𐐣", "𐑋" }, { "𐐤", "𐑌" }, { "𐐥", "𐑍" } };
  
















































































































































  static
  {
    CASE_EQUIV_CLASS = new HashMap();
    

    for (int i = 0; i < "AaBbCcDdEeFfGgHhIiJjLlMmNnOoPpQqRrTtUuVvWwXxYyZzÀàÁáÂâÃãÄäÆæÇçÈèÉéÊêËëÌìÍíÎîÏïÐðÑñÒòÓóÔôÕõÖöØøÙùÚúÛûÜüÝýÞþÿŸĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįĲĳĴĵĶķĹĺĻļĽľĿŀŁłŃńŅņŇňŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŹźŻżŽžƂƃƄƅƇƈƋƌƑƒƕǶƘƙƞȠƠơƢƣƤƥƧƨƬƭƯưƳƴƵƶƸƹƼƽƿǷǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜƎǝǞǟǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǯǴǵǸǹǺǻǼǽǾǿȀȁȂȃȄȅȆȇȈȉȊȋȌȍȎȏȐȑȒȓȔȕȖȗȘșȚțȜȝȞȟȢȣȤȥȦȧȨȩȪȫȬȭȮȯȰȱȲȳƁɓƆɔƉɖƊɗƏəƐɛƓɠƔɣƗɨƖɩƜɯƝɲƟɵƦʀƩʃƮʈƱʊƲʋƷʒΆάΈέΉήΊίΑαΓγΔδΖζΗηΛλΝνΞξΟοΤτΥυΧχΨψΪϊΫϋΌόΎύΏώϘϙϚϛϜϝϞϟϠϡϢϣϤϥϦϧϨϩϪϫϬϭϮϯАаБбВвГгДдЕеЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯяЀѐЁёЂђЃѓЄєЅѕІіЇїЈјЉљЊњЋћЌќЍѝЎўЏџѠѡѢѣѤѥѦѧѨѩѪѫѬѭѮѯѰѱѲѳѴѵѶѷѸѹѺѻѼѽѾѿҀҁҊҋҌҍҎҏҐґҒғҔҕҖҗҘҙҚқҜҝҞҟҠҡҢңҤҥҦҧҨҩҪҫҬҭҮүҰұҲҳҴҵҶҷҸҹҺһҼҽҾҿӁӂӃӄӅӆӇӈӉӊӋӌӍӎӐӑӒӓӔӕӖӗӘәӚӛӜӝӞӟӠӡӢӣӤӥӦӧӨөӪӫӬӭӮӯӰӱӲӳӴӵӸӹԀԁԂԃԄԅԆԇԈԉԊԋԌԍԎԏԱաԲբԳգԴդԵեԶզԷէԸըԹթԺժԻիԼլԽխԾծԿկՀհՁձՂղՃճՄմՅյՆնՇշՈոՉչՊպՋջՌռՍսՎվՏտՐրՑցՒւՓփՔքՕօՖֆḀḁḂḃḄḅḆḇḈḉḊḋḌḍḎḏḐḑḒḓḔḕḖḗḘḙḚḛḜḝḞḟḠḡḢḣḤḥḦḧḨḩḪḫḬḭḮḯḰḱḲḳḴḵḶḷḸḹḺḻḼḽḾḿṀṁṂṃṄṅṆṇṈṉṊṋṌṍṎṏṐṑṒṓṔṕṖṗṘṙṚṛṜṝṞṟṢṣṤṥṦṧṨṩṪṫṬṭṮṯṰṱṲṳṴṵṶṷṸṹṺṻṼṽṾṿẀẁẂẃẄẅẆẇẈẉẊẋẌẍẎẏẐẑẒẓẔẕẠạẢảẤấẦầẨẩẪẫẬậẮắẰằẲẳẴẵẶặẸẹẺẻẼẽẾếỀềỂểỄễỆệỈỉỊịỌọỎỏỐốỒồỔổỖỗỘộỚớỜờỞởỠỡỢợỤụỦủỨứỪừỬửỮữỰựỲỳỴỵỶỷỸỹἀἈἁἉἂἊἃἋἄἌἅἍἆἎἇἏἐἘἑἙἒἚἓἛἔἜἕἝἠἨἡἩἢἪἣἫἤἬἥἭἦἮἧἯἰἸἱἹἲἺἳἻἴἼἵἽἶἾἷἿὀὈὁὉὂὊὃὋὄὌὅὍὑὙὓὛὕὝὗὟὠὨὡὩὢὪὣὫὤὬὥὭὦὮὧὯὰᾺάΆὲῈέΈὴῊήΉὶῚίΊὸῸόΌὺῪύΎὼῺώΏᾰᾸᾱᾹῐῘῑῙῠῨῡῩῥῬⅠⅰⅡⅱⅢⅲⅣⅳⅤⅴⅥⅵⅦⅶⅧⅷⅨⅸⅩⅹⅪⅺⅫⅻⅬⅼⅭⅽⅮⅾⅯⅿⒶⓐⒷⓑⒸⓒⒹⓓⒺⓔⒻⓕⒼⓖⒽⓗⒾⓘⒿⓙⓀⓚⓁⓛⓂⓜⓃⓝⓄⓞⓅⓟⓆⓠⓇⓡⓈⓢⓉⓣⓊⓤⓋⓥⓌⓦⓍⓧⓎⓨⓏⓩＡａＢｂＣｃＤｄＥｅＦｆＧｇＨｈＩｉＪｊＫｋＬｌＭｍＮｎＯｏＰｐＱｑＲｒＳｓＴｔＵｕＶｖＷｗＸｘＹｙＺｚ".length(); i += 2) {
      String[] a = new String[2];
      a[0] = String.valueOf("AaBbCcDdEeFfGgHhIiJjLlMmNnOoPpQqRrTtUuVvWwXxYyZzÀàÁáÂâÃãÄäÆæÇçÈèÉéÊêËëÌìÍíÎîÏïÐðÑñÒòÓóÔôÕõÖöØøÙùÚúÛûÜüÝýÞþÿŸĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįĲĳĴĵĶķĹĺĻļĽľĿŀŁłŃńŅņŇňŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŹźŻżŽžƂƃƄƅƇƈƋƌƑƒƕǶƘƙƞȠƠơƢƣƤƥƧƨƬƭƯưƳƴƵƶƸƹƼƽƿǷǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜƎǝǞǟǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǯǴǵǸǹǺǻǼǽǾǿȀȁȂȃȄȅȆȇȈȉȊȋȌȍȎȏȐȑȒȓȔȕȖȗȘșȚțȜȝȞȟȢȣȤȥȦȧȨȩȪȫȬȭȮȯȰȱȲȳƁɓƆɔƉɖƊɗƏəƐɛƓɠƔɣƗɨƖɩƜɯƝɲƟɵƦʀƩʃƮʈƱʊƲʋƷʒΆάΈέΉήΊίΑαΓγΔδΖζΗηΛλΝνΞξΟοΤτΥυΧχΨψΪϊΫϋΌόΎύΏώϘϙϚϛϜϝϞϟϠϡϢϣϤϥϦϧϨϩϪϫϬϭϮϯАаБбВвГгДдЕеЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯяЀѐЁёЂђЃѓЄєЅѕІіЇїЈјЉљЊњЋћЌќЍѝЎўЏџѠѡѢѣѤѥѦѧѨѩѪѫѬѭѮѯѰѱѲѳѴѵѶѷѸѹѺѻѼѽѾѿҀҁҊҋҌҍҎҏҐґҒғҔҕҖҗҘҙҚқҜҝҞҟҠҡҢңҤҥҦҧҨҩҪҫҬҭҮүҰұҲҳҴҵҶҷҸҹҺһҼҽҾҿӁӂӃӄӅӆӇӈӉӊӋӌӍӎӐӑӒӓӔӕӖӗӘәӚӛӜӝӞӟӠӡӢӣӤӥӦӧӨөӪӫӬӭӮӯӰӱӲӳӴӵӸӹԀԁԂԃԄԅԆԇԈԉԊԋԌԍԎԏԱաԲբԳգԴդԵեԶզԷէԸըԹթԺժԻիԼլԽխԾծԿկՀհՁձՂղՃճՄմՅյՆնՇշՈոՉչՊպՋջՌռՍսՎվՏտՐրՑցՒւՓփՔքՕօՖֆḀḁḂḃḄḅḆḇḈḉḊḋḌḍḎḏḐḑḒḓḔḕḖḗḘḙḚḛḜḝḞḟḠḡḢḣḤḥḦḧḨḩḪḫḬḭḮḯḰḱḲḳḴḵḶḷḸḹḺḻḼḽḾḿṀṁṂṃṄṅṆṇṈṉṊṋṌṍṎṏṐṑṒṓṔṕṖṗṘṙṚṛṜṝṞṟṢṣṤṥṦṧṨṩṪṫṬṭṮṯṰṱṲṳṴṵṶṷṸṹṺṻṼṽṾṿẀẁẂẃẄẅẆẇẈẉẊẋẌẍẎẏẐẑẒẓẔẕẠạẢảẤấẦầẨẩẪẫẬậẮắẰằẲẳẴẵẶặẸẹẺẻẼẽẾếỀềỂểỄễỆệỈỉỊịỌọỎỏỐốỒồỔổỖỗỘộỚớỜờỞởỠỡỢợỤụỦủỨứỪừỬửỮữỰựỲỳỴỵỶỷỸỹἀἈἁἉἂἊἃἋἄἌἅἍἆἎἇἏἐἘἑἙἒἚἓἛἔἜἕἝἠἨἡἩἢἪἣἫἤἬἥἭἦἮἧἯἰἸἱἹἲἺἳἻἴἼἵἽἶἾἷἿὀὈὁὉὂὊὃὋὄὌὅὍὑὙὓὛὕὝὗὟὠὨὡὩὢὪὣὫὤὬὥὭὦὮὧὯὰᾺάΆὲῈέΈὴῊήΉὶῚίΊὸῸόΌὺῪύΎὼῺώΏᾰᾸᾱᾹῐῘῑῙῠῨῡῩῥῬⅠⅰⅡⅱⅢⅲⅣⅳⅤⅴⅥⅵⅦⅶⅧⅷⅨⅸⅩⅹⅪⅺⅫⅻⅬⅼⅭⅽⅮⅾⅯⅿⒶⓐⒷⓑⒸⓒⒹⓓⒺⓔⒻⓕⒼⓖⒽⓗⒾⓘⒿⓙⓀⓚⓁⓛⓂⓜⓃⓝⓄⓞⓅⓟⓆⓠⓇⓡⓈⓢⓉⓣⓊⓤⓋⓥⓌⓦⓍⓧⓎⓨⓏⓩＡａＢｂＣｃＤｄＥｅＦｆＧｇＨｈＩｉＪｊＫｋＬｌＭｍＮｎＯｏＰｐＱｑＲｒＳｓＴｔＵｕＶｖＷｗＸｘＹｙＺｚ".charAt(i));
      a[1] = String.valueOf("AaBbCcDdEeFfGgHhIiJjLlMmNnOoPpQqRrTtUuVvWwXxYyZzÀàÁáÂâÃãÄäÆæÇçÈèÉéÊêËëÌìÍíÎîÏïÐðÑñÒòÓóÔôÕõÖöØøÙùÚúÛûÜüÝýÞþÿŸĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįĲĳĴĵĶķĹĺĻļĽľĿŀŁłŃńŅņŇňŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŹźŻżŽžƂƃƄƅƇƈƋƌƑƒƕǶƘƙƞȠƠơƢƣƤƥƧƨƬƭƯưƳƴƵƶƸƹƼƽƿǷǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜƎǝǞǟǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǯǴǵǸǹǺǻǼǽǾǿȀȁȂȃȄȅȆȇȈȉȊȋȌȍȎȏȐȑȒȓȔȕȖȗȘșȚțȜȝȞȟȢȣȤȥȦȧȨȩȪȫȬȭȮȯȰȱȲȳƁɓƆɔƉɖƊɗƏəƐɛƓɠƔɣƗɨƖɩƜɯƝɲƟɵƦʀƩʃƮʈƱʊƲʋƷʒΆάΈέΉήΊίΑαΓγΔδΖζΗηΛλΝνΞξΟοΤτΥυΧχΨψΪϊΫϋΌόΎύΏώϘϙϚϛϜϝϞϟϠϡϢϣϤϥϦϧϨϩϪϫϬϭϮϯАаБбВвГгДдЕеЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯяЀѐЁёЂђЃѓЄєЅѕІіЇїЈјЉљЊњЋћЌќЍѝЎўЏџѠѡѢѣѤѥѦѧѨѩѪѫѬѭѮѯѰѱѲѳѴѵѶѷѸѹѺѻѼѽѾѿҀҁҊҋҌҍҎҏҐґҒғҔҕҖҗҘҙҚқҜҝҞҟҠҡҢңҤҥҦҧҨҩҪҫҬҭҮүҰұҲҳҴҵҶҷҸҹҺһҼҽҾҿӁӂӃӄӅӆӇӈӉӊӋӌӍӎӐӑӒӓӔӕӖӗӘәӚӛӜӝӞӟӠӡӢӣӤӥӦӧӨөӪӫӬӭӮӯӰӱӲӳӴӵӸӹԀԁԂԃԄԅԆԇԈԉԊԋԌԍԎԏԱաԲբԳգԴդԵեԶզԷէԸըԹթԺժԻիԼլԽխԾծԿկՀհՁձՂղՃճՄմՅյՆնՇշՈոՉչՊպՋջՌռՍսՎվՏտՐրՑցՒւՓփՔքՕօՖֆḀḁḂḃḄḅḆḇḈḉḊḋḌḍḎḏḐḑḒḓḔḕḖḗḘḙḚḛḜḝḞḟḠḡḢḣḤḥḦḧḨḩḪḫḬḭḮḯḰḱḲḳḴḵḶḷḸḹḺḻḼḽḾḿṀṁṂṃṄṅṆṇṈṉṊṋṌṍṎṏṐṑṒṓṔṕṖṗṘṙṚṛṜṝṞṟṢṣṤṥṦṧṨṩṪṫṬṭṮṯṰṱṲṳṴṵṶṷṸṹṺṻṼṽṾṿẀẁẂẃẄẅẆẇẈẉẊẋẌẍẎẏẐẑẒẓẔẕẠạẢảẤấẦầẨẩẪẫẬậẮắẰằẲẳẴẵẶặẸẹẺẻẼẽẾếỀềỂểỄễỆệỈỉỊịỌọỎỏỐốỒồỔổỖỗỘộỚớỜờỞởỠỡỢợỤụỦủỨứỪừỬửỮữỰựỲỳỴỵỶỷỸỹἀἈἁἉἂἊἃἋἄἌἅἍἆἎἇἏἐἘἑἙἒἚἓἛἔἜἕἝἠἨἡἩἢἪἣἫἤἬἥἭἦἮἧἯἰἸἱἹἲἺἳἻἴἼἵἽἶἾἷἿὀὈὁὉὂὊὃὋὄὌὅὍὑὙὓὛὕὝὗὟὠὨὡὩὢὪὣὫὤὬὥὭὦὮὧὯὰᾺάΆὲῈέΈὴῊήΉὶῚίΊὸῸόΌὺῪύΎὼῺώΏᾰᾸᾱᾹῐῘῑῙῠῨῡῩῥῬⅠⅰⅡⅱⅢⅲⅣⅳⅤⅴⅥⅵⅦⅶⅧⅷⅨⅸⅩⅹⅪⅺⅫⅻⅬⅼⅭⅽⅮⅾⅯⅿⒶⓐⒷⓑⒸⓒⒹⓓⒺⓔⒻⓕⒼⓖⒽⓗⒾⓘⒿⓙⓀⓚⓁⓛⓂⓜⓃⓝⓄⓞⓅⓟⓆⓠⓇⓡⓈⓢⓉⓣⓊⓤⓋⓥⓌⓦⓍⓧⓎⓨⓏⓩＡａＢｂＣｃＤｄＥｅＦｆＧｇＨｈＩｉＪｊＫｋＬｌＭｍＮｎＯｏＰｐＱｑＲｒＳｓＴｔＵｕＶｖＷｗＸｘＹｙＺｚ".charAt(i + 1));
      CASE_EQUIV_CLASS.put(a[0], a);
      CASE_EQUIV_CLASS.put(a[1], a);
    }
    

    for (i = 0; i < CASE_NONPAIRS.length; i++) {
      String[] a = CASE_NONPAIRS[i];
      for (int j = 0; j < a.length; j++) {
        CASE_EQUIV_CLASS.put(a[j], a);
      }
    }
  }
}
