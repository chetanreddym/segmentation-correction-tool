package com.ibm.icu.impl;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.Replaceable;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeMatcher;










public final class Utility
{
  private static final char APOSTROPHE = '\'';
  private static final char BACKSLASH = '\\';
  private static final int MAGIC_UNSIGNED = Integer.MIN_VALUE;
  private static final char ESCAPE = 'ꖥ';
  static final byte ESCAPE_BYTE = -91;
  
  public Utility() {}
  
  public static final boolean arrayEquals(Object[] source, Object target)
  {
    if (source == null) return target == null;
    if (!(target instanceof Object[])) return false;
    Object[] targ = (Object[])target;
    return (source.length == targ.length) && (arrayRegionMatches(source, 0, targ, 0, source.length));
  }
  





  public static final boolean arrayEquals(int[] source, Object target)
  {
    if (source == null) return target == null;
    if (!(target instanceof int[])) return false;
    int[] targ = (int[])target;
    return (source.length == targ.length) && (arrayRegionMatches(source, 0, targ, 0, source.length));
  }
  






  public static final boolean arrayEquals(double[] source, Object target)
  {
    if (source == null) return target == null;
    if (!(target instanceof double[])) return false;
    double[] targ = (double[])target;
    return (source.length == targ.length) && (arrayRegionMatches(source, 0, targ, 0, source.length));
  }
  





  public static final boolean arrayEquals(Object source, Object target)
  {
    if (source == null) { return target == null;
    }
    
    if ((source instanceof Object[]))
      return arrayEquals((Object[])source, target);
    if ((source instanceof int[]))
      return arrayEquals((int[])source, target);
    if ((source instanceof double[]))
      return arrayEquals((int[])source, target);
    return source.equals(target);
  }
  








  public static final boolean arrayRegionMatches(Object[] source, int sourceStart, Object[] target, int targetStart, int len)
  {
    int sourceEnd = sourceStart + len;
    int delta = targetStart - sourceStart;
    for (int i = sourceStart; i < sourceEnd; i++) {
      if (!arrayEquals(source[i], target[(i + delta)]))
        return false;
    }
    return true;
  }
  








  public static final boolean arrayRegionMatches(char[] source, int sourceStart, char[] target, int targetStart, int len)
  {
    int sourceEnd = sourceStart + len;
    int delta = targetStart - sourceStart;
    for (int i = sourceStart; i < sourceEnd; i++) {
      if (source[i] != target[(i + delta)])
        return false;
    }
    return true;
  }
  









  public static final boolean arrayRegionMatches(int[] source, int sourceStart, int[] target, int targetStart, int len)
  {
    int sourceEnd = sourceStart + len;
    int delta = targetStart - sourceStart;
    for (int i = sourceStart; i < sourceEnd; i++) {
      if (source[i] != target[(i + delta)])
        return false;
    }
    return true;
  }
  










  public static final boolean arrayRegionMatches(double[] source, int sourceStart, double[] target, int targetStart, int len)
  {
    int sourceEnd = sourceStart + len;
    int delta = targetStart - sourceStart;
    for (int i = sourceStart; i < sourceEnd; i++) {
      if (source[i] != target[(i + delta)])
        return false;
    }
    return true;
  }
  



  public static final boolean objectEquals(Object source, Object target)
  {
    if (source == null) {
      return target == null;
    }
    return source.equals(target);
  }
  

























  public static final String arrayToRLEString(int[] a)
  {
    StringBuffer buffer = new StringBuffer();
    
    appendInt(buffer, a.length);
    int runValue = a[0];
    int runLength = 1;
    for (int i = 1; i < a.length; i++) {
      int s = a[i];
      if ((s == runValue) && (runLength < 65535)) {
        runLength++;
      } else {
        encodeRun(buffer, runValue, runLength);
        runValue = s;
        runLength = 1;
      }
    }
    encodeRun(buffer, runValue, runLength);
    return buffer.toString();
  }
  














  public static final String arrayToRLEString(short[] a)
  {
    StringBuffer buffer = new StringBuffer();
    
    buffer.append((char)(a.length >> 16));
    buffer.append((char)a.length);
    short runValue = a[0];
    int runLength = 1;
    for (int i = 1; i < a.length; i++) {
      short s = a[i];
      if ((s == runValue) && (runLength < 65535)) { runLength++;
      } else {
        encodeRun(buffer, runValue, runLength);
        runValue = s;
        runLength = 1;
      }
    }
    encodeRun(buffer, runValue, runLength);
    return buffer.toString();
  }
  













  public static final String arrayToRLEString(char[] a)
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append((char)(a.length >> 16));
    buffer.append((char)a.length);
    char runValue = a[0];
    int runLength = 1;
    for (int i = 1; i < a.length; i++) {
      char s = a[i];
      if ((s == runValue) && (runLength < 65535)) { runLength++;
      } else {
        encodeRun(buffer, (short)runValue, runLength);
        runValue = s;
        runLength = 1;
      }
    }
    encodeRun(buffer, (short)runValue, runLength);
    return buffer.toString();
  }
  













  public static final String arrayToRLEString(byte[] a)
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append((char)(a.length >> 16));
    buffer.append((char)a.length);
    byte runValue = a[0];
    int runLength = 1;
    byte[] state = new byte[2];
    for (int i = 1; i < a.length; i++) {
      byte b = a[i];
      if ((b == runValue) && (runLength < 255)) { runLength++;
      } else {
        encodeRun(buffer, runValue, runLength, state);
        runValue = b;
        runLength = 1;
      }
    }
    encodeRun(buffer, runValue, runLength, state);
    


    if (state[0] != 0) { appendEncodedByte(buffer, (byte)0, state);
    }
    return buffer.toString();
  }
  




  private static final void encodeRun(StringBuffer buffer, int value, int length)
  {
    if (length < 4) {
      for (int j = 0; j < length; j++) {
        if (value == 42405) {
          appendInt(buffer, value);
        }
        appendInt(buffer, value);
      }
    }
    else {
      if (length == 42405) {
        if (value == 42405) {
          appendInt(buffer, 42405);
        }
        appendInt(buffer, value);
        length--;
      }
      appendInt(buffer, 42405);
      appendInt(buffer, length);
      appendInt(buffer, value);
    }
  }
  

  private static final void appendInt(StringBuffer buffer, int value)
  {
    buffer.append((char)(value >>> 16));
    buffer.append((char)(value & 0xFFFF));
  }
  




  private static final void encodeRun(StringBuffer buffer, short value, int length)
  {
    if (length < 4) {
      for (int j = 0; j < length; j++) {
        if (value == 42405) buffer.append(42405);
        buffer.append((char)value);
      }
    }
    else {
      if (length == 42405) {
        if (value == 42405) buffer.append(42405);
        buffer.append((char)value);
        length--;
      }
      buffer.append(42405);
      buffer.append((char)length);
      buffer.append((char)value);
    }
  }
  




  private static final void encodeRun(StringBuffer buffer, byte value, int length, byte[] state)
  {
    if (length < 4) {
      for (int j = 0; j < length; j++) {
        if (value == -91) appendEncodedByte(buffer, (byte)-91, state);
        appendEncodedByte(buffer, value, state);
      }
    }
    else {
      if (length == -91) {
        if (value == -91) appendEncodedByte(buffer, (byte)-91, state);
        appendEncodedByte(buffer, value, state);
        length--;
      }
      appendEncodedByte(buffer, (byte)-91, state);
      appendEncodedByte(buffer, (byte)length, state);
      appendEncodedByte(buffer, value, state);
    }
  }
  








  private static final void appendEncodedByte(StringBuffer buffer, byte value, byte[] state)
  {
    if (state[0] != 0) {
      char c = (char)(state[1] << 8 | value & 0xFF);
      buffer.append(c);
      state[0] = 0;
    }
    else {
      state[0] = 1;
      state[1] = value;
    }
  }
  


  public static final int[] RLEStringToIntArray(String s)
  {
    int length = getInt(s, 0);
    int[] array = new int[length];
    int ai = 0;int i = 1;
    
    int maxI = s.length() / 2;
    while ((ai < length) && (i < maxI)) {
      int c = getInt(s, i++);
      
      if (c == 42405) {
        c = getInt(s, i++);
        if (c == 42405) {
          array[(ai++)] = c;
        } else {
          int runLength = c;
          int runValue = getInt(s, i++);
          for (int j = 0; j < runLength; j++) {
            array[(ai++)] = runValue;
          }
        }
      }
      else {
        array[(ai++)] = c;
      }
    }
    
    if ((ai != length) || (i != maxI)) {
      throw new InternalError("Bad run-length encoded int array");
    }
    
    return array;
  }
  
  static final int getInt(String s, int i) { return s.charAt(2 * i) << '\020' | s.charAt(2 * i + 1); }
  





  public static final short[] RLEStringToShortArray(String s)
  {
    int length = s.charAt(0) << '\020' | s.charAt(1);
    short[] array = new short[length];
    int ai = 0;
    for (int i = 2; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == 42405) {
        c = s.charAt(++i);
        if (c == 42405) {
          array[(ai++)] = ((short)c);
        } else {
          int runLength = c;
          short runValue = (short)s.charAt(++i);
          for (int j = 0; j < runLength; j++) array[(ai++)] = runValue;
        }
      }
      else {
        array[(ai++)] = ((short)c);
      }
    }
    
    if (ai != length) {
      throw new InternalError("Bad run-length encoded short array");
    }
    return array;
  }
  



  public static final char[] RLEStringToCharArray(String s)
  {
    int length = s.charAt(0) << '\020' | s.charAt(1);
    char[] array = new char[length];
    int ai = 0;
    for (int i = 2; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == 42405) {
        c = s.charAt(++i);
        if (c == 42405) {
          array[(ai++)] = c;
        } else {
          int runLength = c;
          char runValue = s.charAt(++i);
          for (int j = 0; j < runLength; j++) array[(ai++)] = runValue;
        }
      }
      else {
        array[(ai++)] = c;
      }
    }
    
    if (ai != length) {
      throw new InternalError("Bad run-length encoded short array");
    }
    return array;
  }
  


  public static final byte[] RLEStringToByteArray(String s)
  {
    int length = s.charAt(0) << '\020' | s.charAt(1);
    byte[] array = new byte[length];
    boolean nextChar = true;
    char c = '\000';
    int node = 0;
    int runLength = 0;
    int i = 2;
    for (int ai = 0; ai < length;)
    {
      byte b;
      


      if (nextChar) {
        c = s.charAt(i++);
        b = (byte)(c >> '\b');
        nextChar = false;
      }
      else {
        b = (byte)(c & 0xFF);
        nextChar = true;
      }
      




      switch (node)
      {
      case 0: 
        if (b == -91) {
          node = 1;
        }
        else {
          array[(ai++)] = b;
        }
        break;
      

      case 1: 
        if (b == -91) {
          array[(ai++)] = -91;
          node = 0;
        }
        else {
          runLength = b;
          
          if (runLength < 0) runLength += 256;
          node = 2;
        }
        break;
      

      case 2: 
        for (int j = 0; j < runLength; j++) array[(ai++)] = b;
        node = 0;
      }
      
    }
    
    if (node != 0) {
      throw new InternalError("Bad run-length encoded byte array");
    }
    if (i != s.length()) {
      throw new InternalError("Excess data in RLE byte array string");
    }
    return array;
  }
  
  public static String LINE_SEPARATOR = System.getProperty("line.separator");
  




  public static final String formatForSource(String s)
  {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < s.length();) {
      if (i > 0) buffer.append('+').append(LINE_SEPARATOR);
      buffer.append("        \"");
      int count = 11;
      while ((i < s.length()) && (count < 80)) {
        char c = s.charAt(i++);
        if ((c < ' ') || (c == '"') || (c == '\\'))
        {



          buffer.append('\\');
          buffer.append(HEX_DIGIT[((c & 0x1C0) >> '\006')]);
          buffer.append(HEX_DIGIT[((c & 0x38) >> '\003')]);
          buffer.append(HEX_DIGIT[(c & 0x7)]);
          count += 4;
        }
        else if (c <= '~') {
          buffer.append(c);
          count++;
        }
        else {
          buffer.append("\\u");
          buffer.append(HEX_DIGIT[((c & 0xF000) >> '\f')]);
          buffer.append(HEX_DIGIT[((c & 0xF00) >> '\b')]);
          buffer.append(HEX_DIGIT[((c & 0xF0) >> '\004')]);
          buffer.append(HEX_DIGIT[(c & 0xF)]);
          count += 6;
        }
      }
      buffer.append('"');
    }
    return buffer.toString();
  }
  
  static final char[] HEX_DIGIT = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  




  public static final String format1ForSource(String s)
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("\"");
    for (int i = 0; i < s.length();) {
      char c = s.charAt(i++);
      if ((c < ' ') || (c == '"') || (c == '\\'))
      {



        buffer.append('\\');
        buffer.append(HEX_DIGIT[((c & 0x1C0) >> '\006')]);
        buffer.append(HEX_DIGIT[((c & 0x38) >> '\003')]);
        buffer.append(HEX_DIGIT[(c & 0x7)]);
      }
      else if (c <= '~') {
        buffer.append(c);
      }
      else {
        buffer.append("\\u");
        buffer.append(HEX_DIGIT[((c & 0xF000) >> '\f')]);
        buffer.append(HEX_DIGIT[((c & 0xF00) >> '\b')]);
        buffer.append(HEX_DIGIT[((c & 0xF0) >> '\004')]);
        buffer.append(HEX_DIGIT[(c & 0xF)]);
      }
    }
    buffer.append('"');
    return buffer.toString();
  }
  



  public static final String escape(String s)
  {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < s.length();) {
      int c = UTF16.charAt(s, i);
      i += UTF16.getCharCount(c);
      if ((c >= 32) && (c <= 127)) {
        if (c == 92) {
          buf.append("\\\\");
        } else {
          buf.append((char)c);
        }
      } else {
        boolean four = c <= 65535;
        buf.append(four ? "\\u" : "\\U");
        hex(c, four ? 4 : 8, buf);
      }
    }
    return buf.toString();
  }
  

  private static final char[] UNESCAPE_MAP = { 'a', '\007', 'b', '\b', 'e', '\033', 'f', '\f', 'n', '\n', 'r', '\r', 't', '\t', 'v', '\013' };
  





















  public static int unescapeAt(String s, int[] offset16)
  {
    int result = 0;
    int n = 0;
    int minDig = 0;
    int maxDig = 0;
    int bitsPerDigit = 4;
    

    boolean braces = false;
    

    int offset = offset16[0];
    int length = s.length();
    if ((offset < 0) || (offset >= length)) {
      return -1;
    }
    

    int c = UTF16.charAt(s, offset);
    offset += UTF16.getCharCount(c);
    
    int dig;
    switch (c) {
    case 117: 
      minDig = maxDig = 4;
      break;
    case 85: 
      minDig = maxDig = 8;
      break;
    case 120: 
      minDig = 1;
      if ((offset < length) && (UTF16.charAt(s, offset) == 123)) {
        offset++;
        braces = true;
        maxDig = 8;
      } else {
        maxDig = 2;
      }
      break;
    default: 
      dig = UCharacter.digit(c, 8);
      if (dig >= 0) {
        minDig = 1;
        maxDig = 3;
        n = 1;
        bitsPerDigit = 3;
        result = dig;
      }
      break;
    }
    if (minDig != 0) {
      while ((offset < length) && (n < maxDig)) {
        c = UTF16.charAt(s, offset);
        dig = UCharacter.digit(c, bitsPerDigit == 3 ? 8 : 16);
        if (dig < 0) {
          break;
        }
        result = result << bitsPerDigit | dig;
        offset += UTF16.getCharCount(c);
        n++;
      }
      if (n < minDig) {
        return -1;
      }
      if (braces) {
        if (c != 125) {
          return -1;
        }
        offset++;
      }
      offset16[0] = offset;
      return result;
    }
    

    for (int i = 0; i < UNESCAPE_MAP.length; i += 2) {
      if (c == UNESCAPE_MAP[i]) {
        offset16[0] = offset;
        return UNESCAPE_MAP[(i + 1)]; }
      if (c < UNESCAPE_MAP[i]) {
        break;
      }
    }
    

    if ((c == 99) && (offset < length)) {
      c = UTF16.charAt(s, offset);
      offset16[0] = (offset + UTF16.getCharCount(c));
      return 0x1F & c;
    }
    


    offset16[0] = offset;
    return c;
  }
  




  public static String unescape(String s)
  {
    StringBuffer buf = new StringBuffer();
    int[] pos = new int[1];
    for (int i = 0; i < s.length();) {
      char c = s.charAt(i++);
      if (c == '\\') {
        pos[0] = i;
        int e = unescapeAt(s, pos);
        if (e < 0) {
          throw new IllegalArgumentException("Invalid escape sequence " + s.substring(i - 1, Math.min(i + 8, s.length())));
        }
        
        UTF16.append(buf, e);
        i = pos[0];
      } else {
        buf.append(c);
      }
    }
    return buf.toString();
  }
  




  public static String unescapeLeniently(String s)
  {
    StringBuffer buf = new StringBuffer();
    int[] pos = new int[1];
    for (int i = 0; i < s.length();) {
      char c = s.charAt(i++);
      if (c == '\\') {
        pos[0] = i;
        int e = unescapeAt(s, pos);
        if (e < 0) {
          buf.append(c);
        } else {
          UTF16.append(buf, e);
          i = pos[0];
        }
      } else {
        buf.append(c);
      }
    }
    return buf.toString();
  }
  





  public static String hex(char ch)
  {
    StringBuffer temp = new StringBuffer();
    return hex(ch, temp).toString();
  }
  





  public static String hex(String s)
  {
    StringBuffer temp = new StringBuffer();
    return hex(s, temp).toString();
  }
  





  public static String hex(StringBuffer s)
  {
    return hex(s.toString());
  }
  





  public static StringBuffer hex(char ch, StringBuffer output)
  {
    return appendNumber(output, ch, 16, 4);
  }
  






  public static StringBuffer hex(int ch, int width, StringBuffer output)
  {
    return appendNumber(output, ch, 16, width);
  }
  




  public static String hex(int ch, int width)
  {
    StringBuffer buf = new StringBuffer();
    return appendNumber(buf, ch, 16, width).toString();
  }
  





  public static StringBuffer hex(String s, StringBuffer result)
  {
    for (int i = 0; i < s.length(); i++) {
      if (i != 0) result.append(',');
      hex(s.charAt(i), result);
    }
    return result;
  }
  












  public static void split(String s, char divider, String[] output)
  {
    int last = 0;
    int current = 0;
    
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == divider) {
        output[(current++)] = s.substring(last, i);
        last = i + 1;
      }
    }
    output[(current++)] = s.substring(last, i);
    while (current < output.length) {
      output[(current++)] = "";
    }
  }
  











  public static int lookup(String source, String[] target)
  {
    for (int i = 0; i < target.length; i++) {
      if (source.equals(target[i])) return i;
    }
    return -1;
  }
  





  public static int skipWhitespace(String str, int pos)
  {
    while (pos < str.length()) {
      int c = UTF16.charAt(str, pos);
      if (!UCharacterProperty.isRuleWhiteSpace(c)) {
        break;
      }
      pos += UTF16.getCharCount(c);
    }
    return pos;
  }
  



  public static void skipWhitespace(String str, int[] pos)
  {
    pos[0] = skipWhitespace(str, pos[0]);
  }
  


  public static String deleteRuleWhiteSpace(String str)
  {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < str.length();) {
      int ch = UTF16.charAt(str, i);
      i += UTF16.getCharCount(ch);
      if (!UCharacterProperty.isRuleWhiteSpace(ch))
      {

        UTF16.append(buf, ch); }
    }
    return buf.toString();
  }
  











  public static boolean parseChar(String id, int[] pos, char ch)
  {
    int start = pos[0];
    skipWhitespace(id, pos);
    if ((pos[0] == id.length()) || (id.charAt(pos[0]) != ch))
    {
      pos[0] = start;
      return false;
    }
    pos[0] += 1;
    return true;
  }
  




















  public static int parsePattern(String rule, int pos, int limit, String pattern, int[] parsedInts)
  {
    int[] p = new int[1];
    int intCount = 0;
    for (int i = 0; i < pattern.length(); i++) {
      char cpat = pattern.charAt(i);
      
      switch (cpat) {
      case ' ': 
        if (pos >= limit) {
          return -1;
        }
        c = rule.charAt(pos++);
        if (!UCharacterProperty.isRuleWhiteSpace(c)) {
          return -1;
        }
      
      case '~': 
        pos = skipWhitespace(rule, pos);
        break;
      case '#': 
        p[0] = pos;
        parsedInts[(intCount++)] = parseInteger(rule, p, limit);
        if (p[0] == pos)
        {
          return -1;
        }
        pos = p[0];
        break;
      }
      if (pos >= limit) {
        return -1;
      }
      char c = (char)UCharacter.toLowerCase(rule.charAt(pos++));
      if (c != cpat) {
        return -1;
      }
    }
    

    return pos;
  }
  


















  public static int parsePattern(String pat, Replaceable text, int index, int limit)
  {
    int ipat = 0;
    

    if (ipat == pat.length()) {
      return index;
    }
    
    int cpat = UTF16.charAt(pat, ipat);
    
    while (index < limit) {
      int c = text.char32At(index);
      

      if (cpat == 126) {
        if (UCharacterProperty.isRuleWhiteSpace(c)) {
          index += UTF16.getCharCount(c);
          continue;
        }
        ipat++; if (ipat == pat.length()) {
          return index;

        }
        


      }
      else if (c == cpat) {
        int n = UTF16.getCharCount(c);
        index += n;
        ipat += n;
        if (ipat == pat.length()) {
          return index;
        }
        

      }
      else
      {
        return -1;
      }
      
      cpat = UTF16.charAt(pat, ipat);
    }
    
    return -1;
  }
  







  public static int parseInteger(String rule, int[] pos, int limit)
  {
    int count = 0;
    int value = 0;
    int p = pos[0];
    int radix = 10;
    
    if (rule.regionMatches(true, p, "0x", 0, 2)) {
      p += 2;
      radix = 16;
    } else if ((p < limit) && (rule.charAt(p) == '0')) {
      p++;
      count = 1;
      radix = 8;
    }
    
    while (p < limit) {
      int d = UCharacter.digit(rule.charAt(p++), radix);
      if (d < 0) {
        p--;
        break;
      }
      count++;
      int v = value * radix + d;
      if (v <= value)
      {



        return 0;
      }
      value = v;
    }
    if (count > 0) {
      pos[0] = p;
    }
    return value;
  }
  















  public static String parseUnicodeIdentifier(String str, int[] pos)
  {
    StringBuffer buf = new StringBuffer();
    int p = pos[0];
    while (p < str.length()) {
      int ch = UTF16.charAt(str, p);
      if (buf.length() == 0) {
        if (UCharacter.isUnicodeIdentifierStart(ch)) {
          UTF16.append(buf, ch);
        } else {
          return null;
        }
      } else {
        if (!UCharacter.isUnicodeIdentifierPart(ch)) break;
        UTF16.append(buf, ch);
      }
      


      p += UTF16.getCharCount(ch);
    }
    pos[0] = p;
    return buf.toString();
  }
  





  public static StringBuffer trim(StringBuffer b)
  {
    for (int i = 0; (i < b.length()) && (Character.isWhitespace(b.charAt(i))); i++) {}
    b.delete(0, i);
    for (i = b.length() - 1; (i >= 0) && (Character.isWhitespace(b.charAt(i))); i--) {}
    return b.delete(i + 1, b.length());
  }
  

  static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
  









  public static StringBuffer appendNumber(StringBuffer result, int n)
  {
    return appendNumber(result, n, 10, 1);
  }
  













  private static void recursiveAppendNumber(StringBuffer result, int n, int radix, int minDigits)
  {
    int digit = n % radix;
    
    if ((n >= radix) || (minDigits > 1)) {
      recursiveAppendNumber(result, n / radix, radix, minDigits - 1);
    }
    
    result.append(DIGITS[digit]);
  }
  














  public static StringBuffer appendNumber(StringBuffer result, int n, int radix, int minDigits)
    throws IllegalArgumentException
  {
    if ((radix < 2) || (radix > 36)) {
      throw new IllegalArgumentException("Illegal radix " + radix);
    }
    

    int abs = n;
    
    if (n < 0) {
      abs = -n;
      result.append("-");
    }
    
    recursiveAppendNumber(result, abs, radix, minDigits);
    
    return result;
  }
  


















  public static int parseNumber(String text, int[] pos, int radix)
  {
    int n = 0;
    int p = pos[0];
    while (p < text.length()) {
      int ch = UTF16.charAt(text, p);
      int d = UCharacter.digit(ch, radix);
      if (d < 0) {
        break;
      }
      n = radix * n + d;
      

      if (n < 0) {
        return -1;
      }
      p++;
    }
    if (p == pos[0]) {
      return -1;
    }
    pos[0] = p;
    return n;
  }
  



  public static boolean isUnprintable(int c)
  {
    return (c < 32) || (c > 126);
  }
  






  public static boolean escapeUnprintable(StringBuffer result, int c)
  {
    if (isUnprintable(c)) {
      result.append('\\');
      if ((c & 0xFFFF0000) != 0) {
        result.append('U');
        result.append(DIGITS[(0xF & c >> 28)]);
        result.append(DIGITS[(0xF & c >> 24)]);
        result.append(DIGITS[(0xF & c >> 20)]);
        result.append(DIGITS[(0xF & c >> 16)]);
      } else {
        result.append('u');
      }
      result.append(DIGITS[(0xF & c >> 12)]);
      result.append(DIGITS[(0xF & c >> 8)]);
      result.append(DIGITS[(0xF & c >> 4)]);
      result.append(DIGITS[(0xF & c)]);
      return true;
    }
    return false;
  }
  
















  public static int quotedIndexOf(String text, int start, int limit, String setOfChars)
  {
    for (int i = start; i < limit; i++) {
      char c = text.charAt(i);
      if (c == '\\') {
        i++;
      } else if (c == '\'') {
        do {
          i++; if (i >= limit) break; } while (text.charAt(i) != '\'');
      } else if (setOfChars.indexOf(c) >= 0) {
        return i;
      }
    }
    return -1;
  }
  














  public static void getChars(StringBuffer src, int srcBegin, int srcEnd, char[] dst, int dstBegin)
  {
    if (srcBegin == srcEnd) {
      return;
    }
    src.getChars(srcBegin, srcEnd, dst, dstBegin);
  }
  
























  public static void appendToRule(StringBuffer rule, int c, boolean isLiteral, boolean escapeUnprintable, StringBuffer quoteBuf)
  {
    if ((isLiteral) || ((escapeUnprintable) && (isUnprintable(c))))
    {
      if (quoteBuf.length() > 0)
      {








        while ((quoteBuf.length() >= 2) && (quoteBuf.charAt(0) == '\'') && (quoteBuf.charAt(1) == '\'')) {
          rule.append('\\').append('\'');
          quoteBuf.delete(0, 2);
        }
        

        int trailingCount = 0;
        

        while ((quoteBuf.length() >= 2) && (quoteBuf.charAt(quoteBuf.length() - 2) == '\'') && (quoteBuf.charAt(quoteBuf.length() - 1) == '\'')) {
          quoteBuf.setLength(quoteBuf.length() - 2);
          trailingCount++;
        }
        if (quoteBuf.length() > 0) {
          rule.append('\'');
          
          if (ICUDebug.isJDK14OrHigher) {
            rule.append(quoteBuf);
          } else {
            rule.append(quoteBuf.toString());
          }
          rule.append('\'');
          quoteBuf.setLength(0);
        }
        while (trailingCount-- > 0) {
          rule.append('\\').append('\'');
        }
      }
      if (c != -1)
      {




        if (c == 32) {
          int len = rule.length();
          if ((len > 0) && (rule.charAt(len - 1) != ' ')) {
            rule.append(' ');
          }
        } else if ((!escapeUnprintable) || (!escapeUnprintable(rule, c))) {
          UTF16.append(rule, c);
        }
        
      }
      
    }
    else if ((quoteBuf.length() == 0) && ((c == 39) || (c == 92)))
    {
      rule.append('\\').append((char)c);




    }
    else if ((quoteBuf.length() > 0) || ((c >= 33) && (c <= 126) && ((c < 48) || (c > 57)) && ((c < 65) || (c > 90)) && ((c < 97) || (c > 122))) || (UCharacterProperty.isRuleWhiteSpace(c)))
    {




      UTF16.append(quoteBuf, c);
      
      if (c == 39) {
        quoteBuf.append((char)c);
      }
      
    }
    else
    {
      UTF16.append(rule, c);
    }
  }
  







  public static void appendToRule(StringBuffer rule, String text, boolean isLiteral, boolean escapeUnprintable, StringBuffer quoteBuf)
  {
    for (int i = 0; i < text.length(); i++)
    {
      appendToRule(rule, text.charAt(i), isLiteral, escapeUnprintable, quoteBuf);
    }
  }
  






  public static void appendToRule(StringBuffer rule, UnicodeMatcher matcher, boolean escapeUnprintable, StringBuffer quoteBuf)
  {
    if (matcher != null) {
      appendToRule(rule, matcher.toPattern(escapeUnprintable), true, escapeUnprintable, quoteBuf);
    }
  }
  








  public static final int compareUnsigned(int source, int target)
  {
    source += Integer.MIN_VALUE;
    target += Integer.MIN_VALUE;
    if (source < target) {
      return -1;
    }
    if (source > target) {
      return 1;
    }
    return 0;
  }
  









  public static final byte highBit(int n)
  {
    if (n <= 0) {
      return -1;
    }
    
    byte bit = 0;
    
    if (n >= 65536) {
      n >>= 16;
      bit = (byte)(bit + 16);
    }
    
    if (n >= 256) {
      n >>= 8;
      bit = (byte)(bit + 8);
    }
    
    if (n >= 16) {
      n >>= 4;
      bit = (byte)(bit + 4);
    }
    
    if (n >= 4) {
      n >>= 2;
      bit = (byte)(bit + 2);
    }
    
    if (n >= 2) {
      n >>= 1;
      bit = (byte)(bit + 1);
    }
    
    return bit;
  }
}
