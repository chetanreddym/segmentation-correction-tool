package com.ibm.icu.text;

import com.ibm.icu.impl.NormalizerImpl;
import com.ibm.icu.impl.UCharacterProperty;
import java.util.Comparator;





































































































































































public final class UTF16
{
  public static final int SINGLE_CHAR_BOUNDARY = 1;
  public static final int LEAD_SURROGATE_BOUNDARY = 2;
  public static final int TRAIL_SURROGATE_BOUNDARY = 5;
  public static final int CODEPOINT_MIN_VALUE = 0;
  public static final int CODEPOINT_MAX_VALUE = 1114111;
  public static final int SUPPLEMENTARY_MIN_VALUE = 65536;
  public static final int LEAD_SURROGATE_MIN_VALUE = 55296;
  public static final int TRAIL_SURROGATE_MIN_VALUE = 56320;
  public static final int LEAD_SURROGATE_MAX_VALUE = 56319;
  public static final int TRAIL_SURROGATE_MAX_VALUE = 57343;
  public static final int SURROGATE_MIN_VALUE = 55296;
  public static final int SURROGATE_MAX_VALUE = 57343;
  private static final int LEAD_SURROGATE_SHIFT_ = 10;
  private static final int TRAIL_SURROGATE_MASK_ = 1023;
  private static final int LEAD_SURROGATE_OFFSET_ = 55232;
  
  private UTF16() {}
  
  public static int charAt(String source, int offset16)
  {
    if ((offset16 < 0) || (offset16 >= source.length())) {
      throw new StringIndexOutOfBoundsException(offset16);
    }
    
    char single = source.charAt(offset16);
    if ((single < 55296) || (single > 57343))
    {
      return single;
    }
    




    if (single <= 56319) {
      offset16++;
      if (source.length() != offset16) {
        char trail = source.charAt(offset16);
        if ((trail >= 56320) && (trail <= 57343))
        {
          return UCharacterProperty.getRawSupplementary(single, trail);
        }
        
      }
    }
    else
    {
      offset16--;
      if (offset16 >= 0)
      {
        char lead = source.charAt(offset16);
        if ((lead >= 55296) && (lead <= 56319))
        {
          return UCharacterProperty.getRawSupplementary(lead, single);
        }
      }
    }
    
    return single;
  }
  



















  public static int charAt(StringBuffer source, int offset16)
  {
    if ((offset16 < 0) || (offset16 >= source.length())) {
      throw new StringIndexOutOfBoundsException(offset16);
    }
    
    char single = source.charAt(offset16);
    if (!isSurrogate(single)) {
      return single;
    }
    




    if (single <= 56319)
    {
      offset16++;
      if (source.length() != offset16)
      {
        char trail = source.charAt(offset16);
        if (isTrailSurrogate(trail)) {
          return UCharacterProperty.getRawSupplementary(single, trail);
        }
      }
    }
    else {
      offset16--;
      if (offset16 >= 0)
      {

        char lead = source.charAt(offset16);
        if (isLeadSurrogate(lead)) {
          return UCharacterProperty.getRawSupplementary(lead, single);
        }
      }
    }
    return single;
  }
  






















  public static int charAt(char[] source, int start, int limit, int offset16)
  {
    offset16 += start;
    if ((offset16 < start) || (offset16 >= limit)) {
      throw new ArrayIndexOutOfBoundsException(offset16);
    }
    
    char single = source[offset16];
    if (!isSurrogate(single)) {
      return single;
    }
    



    if (single <= 56319) {
      offset16++;
      if (offset16 >= limit) {
        return single;
      }
      char trail = source[offset16];
      if (isTrailSurrogate(trail)) {
        return UCharacterProperty.getRawSupplementary(single, trail);
      }
    }
    else {
      if (offset16 == start) {
        return single;
      }
      offset16--;
      char lead = source[offset16];
      if (isLeadSurrogate(lead))
        return UCharacterProperty.getRawSupplementary(lead, single);
    }
    return single;
  }
  



















  public static int charAt(Replaceable source, int offset16)
  {
    if ((offset16 < 0) || (offset16 >= source.length())) {
      throw new StringIndexOutOfBoundsException(offset16);
    }
    
    char single = source.charAt(offset16);
    if (!isSurrogate(single)) {
      return single;
    }
    




    if (single <= 56319)
    {
      offset16++;
      if (source.length() != offset16)
      {
        char trail = source.charAt(offset16);
        if (isTrailSurrogate(trail)) {
          return UCharacterProperty.getRawSupplementary(single, trail);
        }
      }
    }
    else {
      offset16--;
      if (offset16 >= 0)
      {

        char lead = source.charAt(offset16);
        if (isLeadSurrogate(lead)) {
          return UCharacterProperty.getRawSupplementary(lead, single);
        }
      }
    }
    return single;
  }
  









  public static int getCharCount(int char32)
  {
    if (char32 < 65536) {
      return 1;
    }
    return 2;
  }
  






















  public static int bounds(String source, int offset16)
  {
    char ch = source.charAt(offset16);
    if (isSurrogate(ch)) {
      if (isLeadSurrogate(ch))
      {
        offset16++; if ((offset16 < source.length()) && (isTrailSurrogate(source.charAt(offset16))))
        {
          return 2;
        }
      }
      else
      {
        offset16--;
        if ((offset16 >= 0) && (isLeadSurrogate(source.charAt(offset16)))) {
          return 5;
        }
      }
    }
    return 1;
  }
  






















  public static int bounds(StringBuffer source, int offset16)
  {
    char ch = source.charAt(offset16);
    if (isSurrogate(ch)) {
      if (isLeadSurrogate(ch))
      {
        offset16++; if ((offset16 < source.length()) && (isTrailSurrogate(source.charAt(offset16))))
        {
          return 2;
        }
      }
      else
      {
        offset16--;
        if ((offset16 >= 0) && (isLeadSurrogate(source.charAt(offset16))))
        {
          return 5;
        }
      }
    }
    return 1;
  }
  

























  public static int bounds(char[] source, int start, int limit, int offset16)
  {
    offset16 += start;
    if ((offset16 < start) || (offset16 >= limit)) {
      throw new ArrayIndexOutOfBoundsException(offset16);
    }
    char ch = source[offset16];
    if (isSurrogate(ch)) {
      if (isLeadSurrogate(ch)) {
        offset16++;
        if ((offset16 < limit) && (isTrailSurrogate(source[offset16]))) {
          return 2;
        }
      }
      else {
        offset16--;
        if ((offset16 >= start) && (isLeadSurrogate(source[offset16]))) {
          return 5;
        }
      }
    }
    return 1;
  }
  






  public static boolean isSurrogate(char char16)
  {
    return (55296 <= char16) && (char16 <= 57343);
  }
  







  public static boolean isTrailSurrogate(char char16)
  {
    return (56320 <= char16) && (char16 <= 57343);
  }
  







  public static boolean isLeadSurrogate(char char16)
  {
    return (55296 <= char16) && (char16 <= 56319);
  }
  











  public static char getLeadSurrogate(int char32)
  {
    if (char32 >= 65536) {
      return (char)(55232 + (char32 >> 10));
    }
    

    return '\000';
  }
  










  public static char getTrailSurrogate(int char32)
  {
    if (char32 >= 65536) {
      return (char)(56320 + (char32 & 0x3FF));
    }
    

    return (char)char32;
  }
  












  public static String valueOf(int char32)
  {
    if ((char32 < 0) || (char32 > 1114111)) {
      throw new IllegalArgumentException("Illegal codepoint");
    }
    return toString(char32);
  }
  
















  public static String valueOf(String source, int offset16)
  {
    switch (bounds(source, offset16)) {
    case 2: 
      return source.substring(offset16, offset16 + 2);
    case 5: 
      return source.substring(offset16 - 1, offset16 + 1); }
    return source.substring(offset16, offset16 + 1);
  }
  

















  public static String valueOf(StringBuffer source, int offset16)
  {
    switch (bounds(source, offset16)) {
    case 2: 
      return source.substring(offset16, offset16 + 2);
    case 5: 
      return source.substring(offset16 - 1, offset16 + 1); }
    return source.substring(offset16, offset16 + 1);
  }
  























  public static String valueOf(char[] source, int start, int limit, int offset16)
  {
    switch (bounds(source, start, limit, offset16)) {
    case 2: 
      return new String(source, start + offset16, 2);
    case 5: 
      return new String(source, start + offset16 - 1, 2);
    }
    return new String(source, start + offset16, 1);
  }
  











  public static int findOffsetFromCodePoint(String source, int offset32)
  {
    int size = source.length();
    int result = 0;
    int count = offset32;
    if ((offset32 < 0) || (offset32 > size)) {
      throw new StringIndexOutOfBoundsException(offset32);
    }
    while ((result < size) && (count > 0))
    {
      char ch = source.charAt(result);
      if ((isLeadSurrogate(ch)) && (result + 1 < size) && (isTrailSurrogate(source.charAt(result + 1))))
      {
        result++;
      }
      
      count--;
      result++;
    }
    if (count != 0) {
      throw new StringIndexOutOfBoundsException(offset32);
    }
    return result;
  }
  












  public static int findOffsetFromCodePoint(StringBuffer source, int offset32)
  {
    int size = source.length();
    int result = 0;
    int count = offset32;
    if ((offset32 < 0) || (offset32 > size)) {
      throw new StringIndexOutOfBoundsException(offset32);
    }
    while ((result < size) && (count > 0))
    {
      char ch = source.charAt(result);
      if ((isLeadSurrogate(ch)) && (result + 1 < size) && (isTrailSurrogate(source.charAt(result + 1))))
      {
        result++;
      }
      
      count--;
      result++;
    }
    if (count != 0) {
      throw new StringIndexOutOfBoundsException(offset32);
    }
    return result;
  }
  














  public static int findOffsetFromCodePoint(char[] source, int start, int limit, int offset32)
  {
    int result = start;
    int count = offset32;
    if (offset32 > limit - start) {
      throw new ArrayIndexOutOfBoundsException(offset32);
    }
    while ((result < limit) && (count > 0))
    {
      char ch = source[result];
      if ((isLeadSurrogate(ch)) && (result + 1 < limit) && (isTrailSurrogate(source[(result + 1)])))
      {
        result++;
      }
      
      count--;
      result++;
    }
    if (count != 0) {
      throw new ArrayIndexOutOfBoundsException(offset32);
    }
    return result - start;
  }
  





















  public static int findCodePointOffset(String source, int offset16)
  {
    if ((offset16 < 0) || (offset16 > source.length())) {
      throw new StringIndexOutOfBoundsException(offset16);
    }
    
    int result = 0;
    
    boolean hadLeadSurrogate = false;
    
    for (int i = 0; i < offset16; i++)
    {
      char ch = source.charAt(i);
      if ((hadLeadSurrogate) && (isTrailSurrogate(ch))) {
        hadLeadSurrogate = false;
      }
      else
      {
        hadLeadSurrogate = isLeadSurrogate(ch);
        result++;
      }
    }
    
    if (offset16 == source.length()) {
      return result;
    }
    


    if ((hadLeadSurrogate) && (isTrailSurrogate(source.charAt(offset16)))) {
      result--;
    }
    
    return result;
  }
  





















  public static int findCodePointOffset(StringBuffer source, int offset16)
  {
    if ((offset16 < 0) || (offset16 > source.length())) {
      throw new StringIndexOutOfBoundsException(offset16);
    }
    
    int result = 0;
    
    boolean hadLeadSurrogate = false;
    
    for (int i = 0; i < offset16; i++)
    {
      char ch = source.charAt(i);
      if ((hadLeadSurrogate) && (isTrailSurrogate(ch))) {
        hadLeadSurrogate = false;
      }
      else
      {
        hadLeadSurrogate = isLeadSurrogate(ch);
        result++;
      }
    }
    
    if (offset16 == source.length()) {
      return result;
    }
    


    if ((hadLeadSurrogate) && (isTrailSurrogate(source.charAt(offset16))))
    {
      result--;
    }
    
    return result;
  }
  

























  public static int findCodePointOffset(char[] source, int start, int limit, int offset16)
  {
    offset16 += start;
    if (offset16 > limit) {
      throw new StringIndexOutOfBoundsException(offset16);
    }
    
    int result = 0;
    
    boolean hadLeadSurrogate = false;
    
    for (int i = start; i < offset16; i++)
    {
      char ch = source[i];
      if ((hadLeadSurrogate) && (isTrailSurrogate(ch))) {
        hadLeadSurrogate = false;
      }
      else
      {
        hadLeadSurrogate = isLeadSurrogate(ch);
        result++;
      }
    }
    
    if (offset16 == limit) {
      return result;
    }
    


    if ((hadLeadSurrogate) && (isTrailSurrogate(source[offset16]))) {
      result--;
    }
    
    return result;
  }
  












  public static StringBuffer append(StringBuffer target, int char32)
  {
    if ((char32 < 0) || (char32 > 1114111)) {
      throw new IllegalArgumentException("Illegal codepoint");
    }
    

    if (char32 >= 65536)
    {
      target.append(getLeadSurrogate(char32));
      target.append(getTrailSurrogate(char32));
    }
    else {
      target.append((char)char32);
    }
    return target;
  }
  












  public static int append(char[] target, int limit, int char32)
  {
    if ((char32 < 0) || (char32 > 1114111)) {
      throw new IllegalArgumentException("Illegal codepoint");
    }
    
    if (char32 >= 65536)
    {
      target[(limit++)] = getLeadSurrogate(char32);
      target[(limit++)] = getTrailSurrogate(char32);
    }
    else {
      target[(limit++)] = ((char)char32);
    }
    return limit;
  }
  






  public static int countCodePoint(String source)
  {
    if ((source == null) || (source.length() == 0)) {
      return 0;
    }
    return findCodePointOffset(source, source.length());
  }
  






  public static int countCodePoint(StringBuffer source)
  {
    if ((source == null) || (source.length() == 0)) {
      return 0;
    }
    return findCodePointOffset(source, source.length());
  }
  









  public static int countCodePoint(char[] source, int start, int limit)
  {
    if ((source == null) || (source.length == 0)) {
      return 0;
    }
    return findCodePointOffset(source, start, limit, limit - start);
  }
  










  public static void setCharAt(StringBuffer target, int offset16, int char32)
  {
    int count = 1;
    char single = target.charAt(offset16);
    
    if (isSurrogate(single))
    {

      if ((isLeadSurrogate(single)) && (target.length() > offset16 + 1) && (isTrailSurrogate(target.charAt(offset16 + 1))))
      {
        count++;



      }
      else if ((isTrailSurrogate(single)) && (offset16 > 0) && (isLeadSurrogate(target.charAt(offset16 - 1))))
      {

        offset16--;
        count++;
      }
    }
    
    target.replace(offset16, offset16 + count, valueOf(char32));
  }
  















  public static int setCharAt(char[] target, int limit, int offset16, int char32)
  {
    if (offset16 >= limit) {
      throw new ArrayIndexOutOfBoundsException(offset16);
    }
    int count = 1;
    char single = target[offset16];
    
    if (isSurrogate(single))
    {

      if ((isLeadSurrogate(single)) && (target.length > offset16 + 1) && (isTrailSurrogate(target[(offset16 + 1)])))
      {
        count++;



      }
      else if ((isTrailSurrogate(single)) && (offset16 > 0) && (isLeadSurrogate(target[(offset16 - 1)])))
      {

        offset16--;
        count++;
      }
    }
    

    String str = valueOf(char32);
    int result = limit;
    int strlength = str.length();
    target[offset16] = str.charAt(0);
    if (count == strlength) {
      if (count == 2) {
        target[(offset16 + 1)] = str.charAt(1);
      }
      
    }
    else
    {
      System.arraycopy(target, offset16 + count, target, offset16 + strlength, limit - (offset16 + count));
      
      if (count < strlength)
      {

        target[(offset16 + 1)] = str.charAt(1);
        result++;
        if (result < target.length) {
          target[result] = '\000';
        }
        
      }
      else
      {
        result--;
        target[result] = '\000';
      }
    }
    return result;
  }
  











  public static int moveCodePointOffset(String source, int offset16, int shift32)
  {
    int size = source.length();
    if ((offset16 < 0) || (shift32 + offset16 > size)) {
      throw new StringIndexOutOfBoundsException(offset16);
    }
    
    int result = offset16;
    int count = shift32;
    while ((result < size) && (count > 0))
    {
      char ch = source.charAt(result);
      if ((isLeadSurrogate(ch)) && (result + 1 < size) && (isTrailSurrogate(source.charAt(result + 1))))
      {
        result++;
      }
      
      count--;
      result++;
    }
    if (count != 0) {
      throw new StringIndexOutOfBoundsException(shift32);
    }
    return result;
  }
  











  public static int moveCodePointOffset(StringBuffer source, int offset16, int shift32)
  {
    int size = source.length();
    if ((offset16 < 0) || (shift32 + offset16 > size)) {
      throw new StringIndexOutOfBoundsException(offset16);
    }
    
    int result = offset16;
    int count = shift32;
    while ((result < size) && (count > 0))
    {
      char ch = source.charAt(result);
      if ((isLeadSurrogate(ch)) && (result + 1 < size) && (isTrailSurrogate(source.charAt(result + 1))))
      {
        result++;
      }
      
      count--;
      result++;
    }
    if (count != 0) {
      throw new StringIndexOutOfBoundsException(shift32);
    }
    return result;
  }
  













  public static int moveCodePointOffset(char[] source, int start, int limit, int offset16, int shift32)
  {
    offset16 += start;
    if (shift32 + offset16 > limit) {
      throw new StringIndexOutOfBoundsException(offset16);
    }
    
    int result = offset16;
    int count = shift32;
    while ((result < limit) && (count > 0))
    {
      char ch = source[result];
      if ((isLeadSurrogate(ch)) && (result + 1 < limit) && (isTrailSurrogate(source[(result + 1)])))
      {
        result++;
      }
      
      count--;
      result++;
    }
    if (count != 0) {
      throw new StringIndexOutOfBoundsException(shift32);
    }
    return result - start;
  }
  






















  public static StringBuffer insert(StringBuffer target, int offset16, int char32)
  {
    String str = valueOf(char32);
    if ((offset16 != target.length()) && (bounds(target, offset16) == 5))
    {
      offset16++;
    }
    target.insert(offset16, str);
    return target;
  }
  






















  public static int insert(char[] target, int limit, int offset16, int char32)
  {
    String str = valueOf(char32);
    if ((offset16 != limit) && (bounds(target, 0, limit, offset16) == 5))
    {
      offset16++;
    }
    int size = str.length();
    if (limit + size > target.length) {
      throw new ArrayIndexOutOfBoundsException(offset16 + size);
    }
    System.arraycopy(target, offset16, target, offset16 + size, limit - offset16);
    
    target[offset16] = str.charAt(0);
    if (size == 2) {
      target[(offset16 + 1)] = str.charAt(1);
    }
    return limit + size;
  }
  










  public static StringBuffer delete(StringBuffer target, int offset16)
  {
    int count = 1;
    switch (bounds(target, offset16)) {
    case 2: 
      count++;
      break;
    case 5: 
      count++;
      offset16--;
    }
    
    target.delete(offset16, offset16 + count);
    return target;
  }
  











  public static int delete(char[] target, int limit, int offset16)
  {
    int count = 1;
    switch (bounds(target, 0, limit, offset16)) {
    case 2: 
      count++;
      break;
    case 5: 
      count++;
      offset16--;
    }
    
    System.arraycopy(target, offset16 + count, target, offset16, limit - (offset16 + count));
    
    target[(limit - count)] = '\000';
    return limit - count;
  }
  




















  public static int indexOf(String source, int char32)
  {
    if ((char32 < 0) || (char32 > 1114111))
    {
      throw new IllegalArgumentException("Argument char32 is not a valid codepoint");
    }
    

    if ((char32 < 55296) || ((char32 > 57343) && (char32 < 65536)))
    {

      return source.indexOf((char)char32);
    }
    
    if (char32 < 65536) {
      int result = source.indexOf((char)char32);
      if (result >= 0) {
        if ((isLeadSurrogate((char)char32)) && (result < source.length() - 1) && (isTrailSurrogate(source.charAt(result + 1))))
        {

          return indexOf(source, char32, result + 1);
        }
        
        if ((result > 0) && (isLeadSurrogate(source.charAt(result - 1))))
        {
          return indexOf(source, char32, result + 1);
        }
      }
      return result;
    }
    
    String char32str = toString(char32);
    return source.indexOf(char32str);
  }
  

























  public static int indexOf(String source, String str)
  {
    int strLength = str.length();
    
    if ((!isTrailSurrogate(str.charAt(0))) && (!isLeadSurrogate(str.charAt(strLength - 1))))
    {
      return source.indexOf(str);
    }
    
    int result = source.indexOf(str);
    int resultEnd = result + strLength;
    if (result >= 0)
    {
      if ((isLeadSurrogate(str.charAt(strLength - 1))) && (result < source.length() - 1) && (isTrailSurrogate(source.charAt(resultEnd + 1))))
      {

        return indexOf(source, str, resultEnd + 1);
      }
      
      if ((isTrailSurrogate(str.charAt(0))) && (result > 0) && (isLeadSurrogate(source.charAt(result - 1))))
      {
        return indexOf(source, str, resultEnd + 1);
      }
    }
    return result;
  }
  






















  public static int indexOf(String source, int char32, int fromIndex)
  {
    if ((char32 < 0) || (char32 > 1114111)) {
      throw new IllegalArgumentException("Argument char32 is not a valid codepoint");
    }
    

    if ((char32 < 55296) || ((char32 > 57343) && (char32 < 65536)))
    {

      return source.indexOf((char)char32, fromIndex);
    }
    
    if (char32 < 65536) {
      int result = source.indexOf((char)char32, fromIndex);
      if (result >= 0) {
        if ((isLeadSurrogate((char)char32)) && (result < source.length() - 1) && (isTrailSurrogate(source.charAt(result + 1))))
        {

          return indexOf(source, char32, result + 1);
        }
        
        if ((result > 0) && (isLeadSurrogate(source.charAt(result - 1))))
        {
          return indexOf(source, char32, result + 1);
        }
      }
      return result;
    }
    
    String char32str = toString(char32);
    return source.indexOf(char32str, fromIndex);
  }
  



























  public static int indexOf(String source, String str, int fromIndex)
  {
    int strLength = str.length();
    
    if ((!isTrailSurrogate(str.charAt(0))) && (!isLeadSurrogate(str.charAt(strLength - 1))))
    {
      return source.indexOf(str, fromIndex);
    }
    
    int result = source.indexOf(str, fromIndex);
    int resultEnd = result + strLength;
    if (result >= 0)
    {
      if ((isLeadSurrogate(str.charAt(strLength - 1))) && (result < source.length() - 1) && (isTrailSurrogate(source.charAt(resultEnd))))
      {

        return indexOf(source, str, resultEnd + 1);
      }
      
      if ((isTrailSurrogate(str.charAt(0))) && (result > 0) && (isLeadSurrogate(source.charAt(result - 1))))
      {
        return indexOf(source, str, resultEnd + 1);
      }
    }
    return result;
  }
  




















  public static int lastIndexOf(String source, int char32)
  {
    if ((char32 < 0) || (char32 > 1114111)) {
      throw new IllegalArgumentException("Argument char32 is not a valid codepoint");
    }
    

    if ((char32 < 55296) || ((char32 > 57343) && (char32 < 65536)))
    {

      return source.lastIndexOf((char)char32);
    }
    
    if (char32 < 65536) {
      int result = source.lastIndexOf((char)char32);
      if (result >= 0) {
        if ((isLeadSurrogate((char)char32)) && (result < source.length() - 1) && (isTrailSurrogate(source.charAt(result + 1))))
        {

          return lastIndexOf(source, char32, result - 1);
        }
        
        if ((result > 0) && (isLeadSurrogate(source.charAt(result - 1))))
        {
          return lastIndexOf(source, char32, result - 1);
        }
      }
      return result;
    }
    
    String char32str = toString(char32);
    return source.lastIndexOf(char32str);
  }
  

























  public static int lastIndexOf(String source, String str)
  {
    int strLength = str.length();
    
    if ((!isTrailSurrogate(str.charAt(0))) && (!isLeadSurrogate(str.charAt(strLength - 1))))
    {
      return source.lastIndexOf(str);
    }
    
    int result = source.lastIndexOf(str);
    if (result >= 0)
    {
      if ((isLeadSurrogate(str.charAt(strLength - 1))) && (result < source.length() - 1) && (isTrailSurrogate(source.charAt(result + strLength + 1))))
      {

        return lastIndexOf(source, str, result - 1);
      }
      
      if ((isTrailSurrogate(str.charAt(0))) && (result > 0) && (isLeadSurrogate(source.charAt(result - 1))))
      {
        return lastIndexOf(source, str, result - 1);
      }
    }
    return result;
  }
  































  public static int lastIndexOf(String source, int char32, int fromIndex)
  {
    if ((char32 < 0) || (char32 > 1114111)) {
      throw new IllegalArgumentException("Argument char32 is not a valid codepoint");
    }
    

    if ((char32 < 55296) || ((char32 > 57343) && (char32 < 65536)))
    {

      return source.lastIndexOf((char)char32, fromIndex);
    }
    
    if (char32 < 65536) {
      int result = source.lastIndexOf((char)char32, fromIndex);
      if (result >= 0) {
        if ((isLeadSurrogate((char)char32)) && (result < source.length() - 1) && (isTrailSurrogate(source.charAt(result + 1))))
        {

          return lastIndexOf(source, char32, result - 1);
        }
        
        if ((result > 0) && (isLeadSurrogate(source.charAt(result - 1))))
        {
          return lastIndexOf(source, char32, result - 1);
        }
      }
      return result;
    }
    
    String char32str = toString(char32);
    return source.lastIndexOf(char32str, fromIndex);
  }
  





































  public static int lastIndexOf(String source, String str, int fromIndex)
  {
    int strLength = str.length();
    
    if ((!isTrailSurrogate(str.charAt(0))) && (!isLeadSurrogate(str.charAt(strLength - 1))))
    {
      return source.lastIndexOf(str, fromIndex);
    }
    
    int result = source.lastIndexOf(str, fromIndex);
    if (result >= 0)
    {
      if ((isLeadSurrogate(str.charAt(strLength - 1))) && (result < source.length() - 1) && (isTrailSurrogate(source.charAt(result + strLength))))
      {

        return lastIndexOf(source, str, result - 1);
      }
      
      if ((isTrailSurrogate(str.charAt(0))) && (result > 0) && (isLeadSurrogate(source.charAt(result - 1))))
      {
        return lastIndexOf(source, str, result - 1);
      }
    }
    return result;
  }
  































  public static String replace(String source, int oldChar32, int newChar32)
  {
    if ((oldChar32 <= 0) || (oldChar32 > 1114111)) {
      throw new IllegalArgumentException("Argument oldChar32 is not a valid codepoint");
    }
    
    if ((newChar32 <= 0) || (newChar32 > 1114111)) {
      throw new IllegalArgumentException("Argument newChar32 is not a valid codepoint");
    }
    

    int index = indexOf(source, oldChar32);
    if (index == -1) {
      return source;
    }
    String newChar32Str = toString(newChar32);
    int oldChar32Size = 1;
    int newChar32Size = newChar32Str.length();
    StringBuffer result = new StringBuffer(source);
    int resultIndex = index;
    
    if (oldChar32 >= 65536) {
      oldChar32Size = 2;
    }
    
    while (index != -1) {
      int endResultIndex = resultIndex + oldChar32Size;
      result.replace(resultIndex, endResultIndex, newChar32Str);
      int lastEndIndex = index + oldChar32Size;
      index = indexOf(source, oldChar32, lastEndIndex);
      resultIndex += newChar32Size + index - lastEndIndex;
    }
    return result.toString();
  }
  


































  public static String replace(String source, String oldStr, String newStr)
  {
    int index = indexOf(source, oldStr);
    if (index == -1) {
      return source;
    }
    int oldStrSize = oldStr.length();
    int newStrSize = newStr.length();
    StringBuffer result = new StringBuffer(source);
    int resultIndex = index;
    
    while (index != -1) {
      int endResultIndex = resultIndex + oldStrSize;
      result.replace(resultIndex, endResultIndex, newStr);
      int lastEndIndex = index + oldStrSize;
      index = indexOf(source, oldStr, lastEndIndex);
      resultIndex += newStrSize + index - lastEndIndex;
    }
    return result.toString();
  }
  















  public static StringBuffer reverse(StringBuffer source)
  {
    StringBuffer result = source.reverse();
    int resultLength = result.length();
    int maxLeadLength = resultLength - 2;
    int i = 0;
    while (i < resultLength) {
      if ((i <= maxLeadLength) && (isTrailSurrogate(result.charAt(i))) && (isLeadSurrogate(result.charAt(i + 1))))
      {
        char trail = result.charAt(i);
        result.deleteCharAt(i);
        result.insert(i + 1, trail);
        i += 2;
      }
      else {
        i++;
      }
    }
    return result;
  }
  















  public static boolean hasMoreCodePointsThan(String source, int number)
  {
    if (number < 0) {
      return true;
    }
    if (source == null) {
      return false;
    }
    int length = source.length();
    



    if (length + 1 >> 1 > number) {
      return true;
    }
    

    int maxsupplementary = length - number;
    if (maxsupplementary <= 0) {
      return false;
    }
    





    int start = 0;
    for (;;) {
      if (length == 0) {
        return false;
      }
      if (number == 0) {
        return true;
      }
      if ((isLeadSurrogate(source.charAt(start++))) && (start != length) && (isTrailSurrogate(source.charAt(start))))
      {
        start++;
        maxsupplementary--; if (maxsupplementary <= 0)
        {
          return false;
        }
      }
      number--;
    }
  }
  





















  public static boolean hasMoreCodePointsThan(char[] source, int start, int limit, int number)
  {
    int length = limit - start;
    if ((length < 0) || (start < 0) || (limit < 0)) {
      throw new IndexOutOfBoundsException("Start and limit indexes should be non-negative and start <= limit");
    }
    
    if (number < 0) {
      return true;
    }
    if (source == null) {
      return false;
    }
    



    if (length + 1 >> 1 > number) {
      return true;
    }
    

    int maxsupplementary = length - number;
    if (maxsupplementary <= 0) {
      return false;
    }
    




    for (;;)
    {
      if (length == 0) {
        return false;
      }
      if (number == 0) {
        return true;
      }
      if ((isLeadSurrogate(source[(start++)])) && (start != limit) && (isTrailSurrogate(source[start])))
      {
        start++;
        maxsupplementary--; if (maxsupplementary <= 0)
        {
          return false;
        }
      }
      number--;
    }
  }
  
















  public static boolean hasMoreCodePointsThan(StringBuffer source, int number)
  {
    if (number < 0) {
      return true;
    }
    if (source == null) {
      return false;
    }
    int length = source.length();
    



    if (length + 1 >> 1 > number) {
      return true;
    }
    

    int maxsupplementary = length - number;
    if (maxsupplementary <= 0) {
      return false;
    }
    





    int start = 0;
    for (;;) {
      if (length == 0) {
        return false;
      }
      if (number == 0) {
        return true;
      }
      if ((isLeadSurrogate(source.charAt(start++))) && (start != length) && (isTrailSurrogate(source.charAt(start))))
      {
        start++;
        maxsupplementary--; if (maxsupplementary <= 0)
        {
          return false;
        }
      }
      number--;
    }
  }
  



  public static final class StringComparator
    implements Comparator
  {
    public static final int FOLD_CASE_DEFAULT = 0;
    


    public static final int FOLD_CASE_EXCLUDE_SPECIAL_I = 1;
    


    private int m_codePointCompare_;
    

    private int m_foldCase_;
    

    private boolean m_ignoreCase_;
    

    private static final int CODE_POINT_COMPARE_SURROGATE_OFFSET_ = 10240;
    


    public StringComparator()
    {
      this(false, false, 0);
    }
    

















    public StringComparator(boolean codepointcompare, boolean ignorecase, int foldcaseoption)
    {
      setCodePointCompare(codepointcompare);
      m_ignoreCase_ = ignorecase;
      if ((foldcaseoption < 0) || (foldcaseoption > 1))
      {
        throw new IllegalArgumentException("Invalid fold case option");
      }
      m_foldCase_ = foldcaseoption;
    }
    








































    public void setCodePointCompare(boolean flag)
    {
      if (flag) {
        m_codePointCompare_ = 32768;
      }
      else {
        m_codePointCompare_ = 0;
      }
    }
    













    public void setIgnoreCase(boolean ignorecase, int foldcaseoption)
    {
      m_ignoreCase_ = ignorecase;
      if ((foldcaseoption < 0) || (foldcaseoption > 1))
      {
        throw new IllegalArgumentException("Invalid fold case option");
      }
      m_foldCase_ = foldcaseoption;
    }
    







    public boolean getCodePointCompare()
    {
      return m_codePointCompare_ == 32768;
    }
    






    public boolean getIgnoreCase()
    {
      return m_ignoreCase_;
    }
    








    public int getIgnoreCaseOption()
    {
      return m_foldCase_;
    }
    













    public int compare(Object a, Object b)
    {
      String str1 = (String)a;
      String str2 = (String)b;
      
      if (str1 == str2) {
        return 0;
      }
      if (str1 == null) {
        return -1;
      }
      if (str2 == null) {
        return 1;
      }
      
      if (m_ignoreCase_) {
        return compareCaseInsensitive(str1, str2);
      }
      return compareCaseSensitive(str1, str2);
    }
    





























    private int compareCaseInsensitive(String s1, String s2)
    {
      return NormalizerImpl.cmpEquivFold(s1, s2, m_foldCase_ | m_codePointCompare_ | 0x10000);
    }
    











    private int compareCaseSensitive(String s1, String s2)
    {
      int length1 = s1.length();
      int length2 = s2.length();
      int minlength = length1;
      int result = 0;
      if (length1 < length2) {
        result = -1;
      }
      else if (length1 > length2) {
        result = 1;
        minlength = length2;
      }
      
      char c1 = '\000';
      char c2 = '\000';
      for (int index = 0; 
          index < minlength; index++) {
        c1 = s1.charAt(index);
        c2 = s2.charAt(index);
        
        if (c1 != c2) {
          break;
        }
      }
      
      if (index == minlength) {
        return result;
      }
      
      boolean codepointcompare = m_codePointCompare_ == 32768;
      

      if ((c1 >= 55296) && (c2 >= 55296) && (codepointcompare))
      {


        if (((c1 > 56319) || (index + 1 == length1) || (!UTF16.isTrailSurrogate(s1.charAt(index + 1)))) && ((!UTF16.isTrailSurrogate(c1)) || (index == 0) || (!UTF16.isLeadSurrogate(s1.charAt(index - 1)))))
        {







          c1 = (char)(c1 - '⠀');
        }
        
        if (((c2 > 56319) || (index + 1 == length2) || (!UTF16.isTrailSurrogate(s2.charAt(index + 1)))) && ((!UTF16.isTrailSurrogate(c2)) || (index == 0) || (!UTF16.isLeadSurrogate(s2.charAt(index - 1)))))
        {







          c2 = (char)(c2 - '⠀');
        }
      }
      

      return c1 - c2;
    }
  }
  

































  public static String toString(int ch)
  {
    if (ch < 65536) {
      return String.valueOf((char)ch);
    }
    
    StringBuffer result = new StringBuffer();
    result.append(getLeadSurrogate(ch));
    result.append(getTrailSurrogate(ch));
    return result.toString();
  }
}
