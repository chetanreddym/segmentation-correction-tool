package com.ibm.icu.impl;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.UCharacterIterator;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.RangeValueIterator.Element;
import com.ibm.icu.util.VersionInfo;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
























































































































public final class UCharacterProperty
  implements Trie.DataManipulate
{
  public CharTrie m_trie_;
  public char[] m_trieIndex_;
  public char[] m_trieData_;
  public int m_trieInitialValue_;
  public int[] m_property_;
  public VersionInfo m_unicodeVersion_;
  public static final int EXC_UPPERCASE_ = 0;
  public static final int EXC_LOWERCASE_ = 1;
  public static final int EXC_TITLECASE_ = 2;
  public static final int EXC_UNUSED_ = 3;
  public static final int EXC_NUMERIC_VALUE_ = 4;
  public static final int EXC_DENOMINATOR_VALUE_ = 5;
  public static final int EXC_MIRROR_MAPPING_ = 6;
  public static final int EXC_SPECIAL_CASING_ = 7;
  public static final int EXC_CASE_FOLDING_ = 8;
  public static final int EXC_COMBINING_CLASS_ = 9;
  public static final int MAX_CASE_MAP_SIZE = 10;
  public static final String TURKISH_ = "tr";
  public static final String AZERBAIJANI_ = "az";
  public static final String LITHUANIAN_ = "lt";
  public static final char LATIN_CAPITAL_LETTER_I_WITH_DOT_ABOVE_ = 'İ';
  public static final char LATIN_SMALL_LETTER_DOTLESS_I_ = 'ı';
  public static final char LATIN_SMALL_LETTER_I_ = 'i';
  public static final int TYPE_MASK = 31;
  public static final int EXCEPTION_MASK = 32;
  public static final int MIRROR_MASK = 2048;
  static final int MY_MASK = 30;
  private static final long UNSIGNED_INT_MASK = 4294967295L;
  
  public void setIndexData(CharTrie.FriendAgent friendagent)
  {
    m_trieIndex_ = friendagent.getPrivateIndex();
    m_trieData_ = friendagent.getPrivateData();
    m_trieInitialValue_ = friendagent.getPrivateInitialValue();
  }
  







  public int getFoldingOffset(int value)
  {
    if ((value & 0x8000) != 0) {
      return value & 0x7FFF;
    }
    
    return 0;
  }
  









  public int getProperty(int ch)
  {
    if ((ch < 55296) || ((ch > 56319) && (ch < 65536)))
    {

      try
      {

        return m_property_[m_trieData_[((m_trieIndex_[(ch >> 5)] << '\002') + (ch & 0x1F))]];

      }
      catch (ArrayIndexOutOfBoundsException e)
      {

        return m_property_[m_trieInitialValue_];
      }
    }
    if (ch <= 56319) {
      return m_property_[m_trieData_[((m_trieIndex_[(320 + (ch >> 5))] << '\002') + (ch & 0x1F))]];
    }
    





    if (ch <= 1114111)
    {

      return m_property_[m_trie_.getSurrogateValue(UTF16.getLeadSurrogate(ch), (char)(ch & 0x3FF))];
    }
    





    return m_property_[m_trieInitialValue_];
  }
  







  public static int getSignedValue(int prop)
  {
    return prop >> 20;
  }
  





  public static int getExceptionIndex(int prop)
  {
    return prop >> 20 & 0x7FF;
  }
  







  public static int getUnsignedValue(int prop)
  {
    return prop >> 20 & 0x7FF;
  }
  









  public boolean hasExceptionValue(int index, int indicator)
  {
    return (m_exception_[index] & 1 << indicator) != 0;
  }
  










  public int getExceptionValue(int props, int etype)
  {
    int index = getExceptionIndex(props);
    if (hasExceptionValue(index, etype))
    {

      if (etype == 9) {
        return m_exception_[index];
      }
      
      index = addExceptionOffset(m_exception_[index], etype, ++index);
      return m_exception_[index];
    }
    return -1;
  }
  










  public int getException(int index, int etype)
  {
    if (etype == 9) {
      return m_exception_[index];
    }
    
    index = addExceptionOffset(m_exception_[index], etype, ++index);
    return m_exception_[index];
  }
  




















  public int getFoldCase(int index)
  {
    char single = m_case_[index];
    if ((55296 <= single) && (single <= 57343))
    {




      if (single <= 56319) {
        char trail = m_case_[(index + 1)];
        if ((55296 <= trail) && (trail <= 57343))
        {
          return getRawSupplementary(single, trail);
        }
      }
      else
      {
        char lead = m_case_[(index - 1)];
        if ((55296 <= lead) && (lead <= 56319))
        {
          return getRawSupplementary(lead, single);
        }
      }
    }
    return single;
  }
  




  public void getFoldCase(int index, int count, StringBuffer str)
  {
    
    


    while (count > 0) {
      str.append(m_case_[index]);
      index++;
      count--;
    }
  }
  





  public void getUpperCase(int index, StringBuffer buffer)
  {
    int count = m_case_[index];
    

    index += (count & 0x1F) + 1;
    count = count >> 5 & 0x1F;
    
    for (int j = 0; j < count; j++) {
      buffer.append(m_case_[(index + j)]);
    }
  }
  





  public void getTitleCase(int index, StringBuffer buffer)
  {
    int count = m_case_[index];
    

    index += (count & 0x1F) + 1 + (count >> 5 & 0x1F);
    
    count = count >> 10 & 0x1F;
    
    for (int j = 0; j < count; j++) {
      buffer.append(m_case_[(index + j)]);
    }
  }
  





  public void getLowerCase(int index, StringBuffer buffer)
  {
    int count = m_case_[index] & 0x1F;
    

    index++;
    for (int j = 0; j < count; j++) {
      buffer.append(m_case_[(index + j)]);
    }
  }
  







  public int getAdditional(int codepoint, int column)
  {
    if (column == -1) {
      return getProperty(codepoint);
    }
    if ((column < 0) || (column >= m_additionalColumnsCount_)) {
      return 0;
    }
    return m_additionalVectors_[(m_additionalTrie_.getCodePointValue(codepoint) + column)];
  }
  






















  public VersionInfo getAge(int codepoint)
  {
    int version = getAdditional(codepoint, 0) >> 24;
    return VersionInfo.getInstance(version >> 4 & 0xF, version & 0xF, 0, 0);
  }
  
  private static final class BinaryProperties
  {
    int column;
    long mask;
    
    public BinaryProperties(int column, long mask) {
      this.column = column;
      this.mask = mask;
    } }
  
  BinaryProperties[] binProps = { new BinaryProperties(1, 1024L), new BinaryProperties(1, 512L), new BinaryProperties(1, 2L), new BinaryProperties(-1, 2048L), new BinaryProperties(1, 8L), new BinaryProperties(1, 8388608L), new BinaryProperties(1, 16777216L), new BinaryProperties(1, 4096L), new BinaryProperties(1, 8192L), new BinaryProperties(0, 0L), new BinaryProperties(1, -2147483648L), new BinaryProperties(1, 131072L), new BinaryProperties(1, 262144L), new BinaryProperties(1, 256L), new BinaryProperties(1, 16L), new BinaryProperties(1, 1073741824L), new BinaryProperties(1, 536870912L), new BinaryProperties(1, 2048L), new BinaryProperties(1, 524288L), new BinaryProperties(1, 1048576L), new BinaryProperties(1, 4L), new BinaryProperties(1, 67108864L), new BinaryProperties(1, 16384L), new BinaryProperties(1, 128L), new BinaryProperties(1, 65536L), new BinaryProperties(1, 32L), new BinaryProperties(1, 2097152L), new BinaryProperties(1, 33554432L), new BinaryProperties(1, 64L), new BinaryProperties(1, 4194304L), new BinaryProperties(1, 32768L), new BinaryProperties(1, 1L), new BinaryProperties(1, 268435456L), new BinaryProperties(1, 134217728L), new BinaryProperties(-1, 32768L) };
  







  char[] m_case_;
  







  int[] m_exception_;
  







  CharTrie m_additionalTrie_;
  







  int[] m_additionalVectors_;
  







  int m_additionalColumnsCount_;
  






  int m_maxBlockScriptValue_;
  






  int m_maxJTGValue_;
  







  public boolean hasBinaryProperty(int codepoint, int property)
  {
    if ((property < 0) || (35 <= property))
    {
      return false; }
    if (property == 9) {
      return NormalizerImpl.isFullCompositionExclusion(codepoint);
    }
    
    return (0xFFFFFFFF & getAdditional(codepoint, binProps[property].column) & binProps[property].mask) != 0L;
  }
  









  public static int getRawSupplementary(char lead, char trail)
  {
    return (lead << '\n') + trail + -56613888;
  }
  



  public static UCharacterProperty getInstance()
    throws RuntimeException
  {
    if (INSTANCE_ == null) {
      try {
        INSTANCE_ = new UCharacterProperty();
      }
      catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return INSTANCE_;
  }
  











  public int getSpecialLowerCase(Locale locale, int index, int ch, UCharacterIterator uchariter, StringBuffer buffer)
  {
    int exception = getException(index, 7);
    
    if (exception < 0) {
      int offset = uchariter.getIndex();
      





      if ((locale.getLanguage().equals("lt")) && (((ch != 73) && (ch != 74) && (ch != 302)) || ((isFollowedByMOREABOVE(uchariter, offset)) || (ch == 204) || (ch == 205) || (ch == 296))))
      {






























        switch (ch) {
        case 73: 
          buffer.append('i');
          buffer.append('̇');
          return 2;
        case 74: 
          buffer.append('j');
          buffer.append('̇');
          return 2;
        case 302: 
          buffer.append('į');
          buffer.append('̇');
          return 2;
        case 204: 
          buffer.append('i');
          buffer.append('̇');
          buffer.append('̀');
          return 3;
        case 205: 
          buffer.append('i');
          buffer.append('̇');
          buffer.append('́');
          return 3;
        case 296: 
          buffer.append('i');
          buffer.append('̇');
          buffer.append('̃');
          return 3;
        }
        
      }
      String language = locale.getLanguage();
      if ((language.equals("tr")) || (language.equals("az"))) {
        if (ch == 304)
        {






          buffer.append('i');
          return 1;
        }
        if ((ch == 775) && (isPrecededByI(uchariter, offset)))
        {






          return 0;
        }
        
        if ((ch == 73) && (!isFollowedByDotAbove(uchariter, offset)))
        {









          buffer.append('ı');
          return 1;
        }
      }
      
      if (ch == 304)
      {





        buffer.append('i');
        buffer.append('̇');
        return 2;
      }
      
      if ((ch == 931) && (isCFINAL(uchariter, offset)) && (isNotCINITIAL(uchariter, offset)))
      {








        buffer.append('ς');
        return 1;
      }
      

      if (hasExceptionValue(index, 1)) {
        int oldlength = buffer.length();
        UTF16.append(buffer, getException(index, 1));
        
        return buffer.length() - oldlength;
      }
      
      UTF16.append(buffer, ch);
      return UTF16.getCharCount(ch);
    }
    

    index = exception & 0xFFFF;
    int oldlength = buffer.length();
    getLowerCase(index, buffer);
    return buffer.length() - oldlength;
  }
  











  public int toLowerCase(Locale locale, int ch, UCharacterIterator uchariter, StringBuffer buffer)
  {
    int props = getProperty(ch);
    if ((props & 0x20) == 0) {
      int type = props & 0x1F;
      if ((type == 1) || (type == 3))
      {
        ch += getSignedValue(props);
      }
    } else {
      int index = getExceptionIndex(props);
      if (hasExceptionValue(index, 7))
      {
        return getSpecialLowerCase(locale, index, ch, uchariter, buffer);
      }
      
      if (hasExceptionValue(index, 1))
      {
        ch = getException(index, 1);
      }
    }
    UTF16.append(buffer, ch);
    return UTF16.getCharCount(ch);
  }
  









  public int toLowerCase(Locale locale, int ch, UCharacterIterator uchariter, char[] buffer)
  {
    int props = getProperty(ch);
    if ((props & 0x20) == 0) {
      int type = props & 0x1F;
      if ((type == 1) || (type == 3))
      {
        ch += getSignedValue(props);
      }
    } else {
      int index = getExceptionIndex(props);
      if (hasExceptionValue(index, 7))
      {
        StringBuffer strbuffer = new StringBuffer(1);
        int result = getSpecialLowerCase(locale, index, ch, uchariter, strbuffer);
        
        strbuffer.getChars(0, result, buffer, 0);
        return result;
      }
      if (hasExceptionValue(index, 1)) {
        ch = getException(index, 1);
      }
    }
    if (ch < 65536) {
      buffer[0] = ((char)ch);
      return 1;
    }
    buffer[0] = UTF16.getLeadSurrogate(ch);
    buffer[1] = UTF16.getTrailSurrogate(ch);
    return 2;
  }
  










  public void toLowerCase(Locale locale, String str, int start, int limit, StringBuffer result)
  {
    UCharacterIterator ucharIter = UCharacterIterator.getInstance(str);
    int strIndex = start;
    
    while (strIndex < limit) {
      ucharIter.setIndex(strIndex);
      int ch = ucharIter.currentCodePoint();
      
      toLowerCase(locale, ch, ucharIter, result);
      strIndex++;
      if (ch >= 65536) {
        strIndex++;
      }
    }
  }
  














  public int getSpecialUpperOrTitleCase(Locale locale, int index, int ch, UCharacterIterator uchariter, boolean upperflag, StringBuffer buffer)
  {
    int exception = getException(index, 7);
    
    if (exception < 0) {
      String language = locale.getLanguage();
      
      if (((language.equals("tr")) || (language.equals("az"))) && (ch == 105))
      {







        buffer.append('İ');
        return 1;
      }
      
      if ((language.equals("lt")) && (ch == 775) && (isPrecededBySoftDotted(uchariter, uchariter.getIndex())))
      {







        return 0;
      }
      

      if ((!upperflag) && (hasExceptionValue(index, 2)))
      {
        ch = getException(index, 2);

      }
      else if (hasExceptionValue(index, 0))
      {
        ch = getException(index, 0);
      }
      

      UTF16.append(buffer, ch);
      return UTF16.getCharCount(ch);
    }
    

    index = exception & 0xFFFF;
    int oldlength = buffer.length();
    if (upperflag) {
      getUpperCase(index, buffer);
    }
    else {
      getTitleCase(index, buffer);
    }
    return buffer.length() - oldlength;
  }
  











  public int toUpperOrTitleCase(Locale locale, int ch, UCharacterIterator uchariter, boolean upperflag, StringBuffer buffer)
  {
    int props = getProperty(ch);
    if ((props & 0x20) == 0) {
      int type = props & 0x1F;
      if (type == 2) {
        ch -= getSignedValue(props);
      }
    } else {
      int index = getExceptionIndex(props);
      if (hasExceptionValue(index, 7))
      {
        return getSpecialUpperOrTitleCase(locale, index, ch, uchariter, upperflag, buffer);
      }
      
      if ((!upperflag) && (hasExceptionValue(index, 2)))
      {
        ch = getException(index, 2);

      }
      else if (hasExceptionValue(index, 0))
      {
        ch = getException(index, 0);
      }
    }
    

    UTF16.append(buffer, ch);
    return UTF16.getCharCount(ch);
  }
  











  public int toUpperOrTitleCase(Locale locale, int ch, UCharacterIterator uchariter, boolean upperflag, char[] buffer)
  {
    int props = getProperty(ch);
    if ((props & 0x20) == 0) {
      int type = props & 0x1F;
      if (type == 2) {
        ch -= getSignedValue(props);
      }
    } else {
      int index = getExceptionIndex(props);
      if (hasExceptionValue(index, 7))
      {
        StringBuffer strbuffer = new StringBuffer(1);
        int result = getSpecialUpperOrTitleCase(locale, index, ch, uchariter, upperflag, strbuffer);
        

        strbuffer.getChars(0, result, buffer, 0);
        return result;
      }
      if ((!upperflag) && (hasExceptionValue(index, 2)))
      {
        ch = getException(index, 2);

      }
      else if (hasExceptionValue(index, 0))
      {
        ch = getException(index, 0);
      }
    }
    

    if (ch < 65536) {
      buffer[0] = ((char)ch);
      return 1;
    }
    buffer[0] = UTF16.getLeadSurrogate(ch);
    buffer[1] = UTF16.getTrailSurrogate(ch);
    return 2;
  }
  







  public String toUpperCase(Locale locale, String str, int start, int limit)
  {
    UCharacterIterator ucharIter = UCharacterIterator.getInstance(str);
    int strIndex = start;
    StringBuffer result = new StringBuffer(limit - start);
    
    while (strIndex < limit) {
      ucharIter.setIndex(strIndex);
      int ch = ucharIter.currentCodePoint();
      
      toUpperOrTitleCase(locale, ch, ucharIter, true, result);
      strIndex++;
      if (ch >= 65536) {
        strIndex++;
      }
    }
    return result.toString();
  }
  


















  public String toTitleCase(Locale locale, String str, BreakIterator breakiter)
  {
    UCharacterIterator ucharIter = UCharacterIterator.getInstance(str);
    int length = str.length();
    StringBuffer result = new StringBuffer();
    
    breakiter.setText(str);
    
    int index = breakiter.first();
    
    while ((index != -1) && (index < length))
    {
      int ch = UTF16.charAt(str, index);
      ucharIter.setIndex(index);
      index += UTF16.getCharCount(ch);
      toUpperOrTitleCase(locale, ch, ucharIter, false, result);
      int next = breakiter.next();
      if ((index != -1) && (index < next))
      {
        toLowerCase(locale, str, index, next, result);
      }
      index = next;
    }
    return result.toString();
  }
  
















































































  public static boolean isRuleWhiteSpace(int c)
  {
    UCharacterProperty property = getInstance();
    return ((property.getProperty(c) & 0x1F) == 16) || (property.hasBinaryProperty(c, 31));
  }
  








  public int getMaxValues(int column)
  {
    switch (column) {
    case 0: 
      return m_maxBlockScriptValue_;
    case 2: 
      return m_maxJTGValue_;
    }
    return 0;
  }
  






  public static int getMask(int type)
  {
    return 1 << type;
  }
  







































  private static UCharacterProperty INSTANCE_ = null;
  




  private static final String DATA_FILE_NAME_ = "data/uprops.icu";
  




  private static final int DATA_BUFFER_SIZE_ = 25000;
  




  private static final int EXC_GROUP_ = 8;
  




  private static final int EXC_GROUP_MASK_ = 255;
  



  private static final int EXC_DIGIT_MASK_ = 65535;
  



  private static final byte[] FLAGS_OFFSET_ = { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8 };
  





  private static final int VALUE_SHIFT_ = 20;
  





  private static final int UNSIGNED_VALUE_MASK_AFTER_SHIFT_ = 2047;
  




  private static final int RESERVED_SHIFT_ = 15;
  




  private static final int BIDI_SHIFT_ = 6;
  




  private static final int MIRROR_SHIFT_ = 11;
  




  private static final int NUMERIC_TYPE_SHIFT = 12;
  




  private static final int CASE_SENSITIVE_SHIFT_ = 15;
  




  private static final int EXCEPTION_BIT = 32;
  




  private static final int VALUE_BITS_ = 65516;
  




  private static final int MIN_VALUE_ = -2048;
  




  private static final int MAX_VALUE_ = 2047;
  




  private static int MAX_EXCEPTIONS_COUNT_ = 4096;
  

  private static final int LAST_5_BIT_MASK_ = 31;
  

  private static final int SHIFT_5_ = 5;
  

  private static final int SHIFT_10_ = 10;
  

  private static final int SUPPLEMENTARY_FOLD_INDICATOR_MASK_ = 32768;
  

  private static final int SUPPLEMENTARY_FOLD_OFFSET_MASK_ = 32767;
  

  private static final int LEAD_SURROGATE_SHIFT_ = 10;
  

  private static final int SURROGATE_OFFSET_ = -56613888;
  

  private static final char LATIN_CAPITAL_LETTER_I_ = 'I';
  

  private static final char COMBINING_DOT_ABOVE_ = '̇';
  

  private static final int LATIN_SMALL_LETTER_J_ = 106;
  

  private static final int LATIN_SMALL_LETTER_I_WITH_OGONEK_ = 303;
  

  private static final int LATIN_SMALL_LETTER_I_WITH_TILDE_BELOW_ = 7725;
  

  private static final int LATIN_SMALL_LETTER_I_WITH_DOT_BELOW_ = 7883;
  

  private static final int COMBINING_MARK_ABOVE_CLASS_ = 230;
  

  private static final int LATIN_CAPITAL_LETTER_J_ = 74;
  

  private static final int LATIN_CAPITAL_I_WITH_OGONEK_ = 302;
  

  private static final int LATIN_CAPITAL_I_WITH_TILDE_ = 296;
  

  private static final int LATIN_CAPITAL_I_WITH_GRAVE_ = 204;
  
  private static final int LATIN_CAPITAL_I_WITH_ACUTE_ = 205;
  
  private static final int COMBINING_GRAVE_ACCENT_ = 768;
  
  private static final int COMBINING_ACUTE_ACCENT_ = 769;
  
  private static final int COMBINING_TILDE_ = 771;
  
  private static final char GREEK_CAPITAL_LETTER_SIGMA_ = 'Σ';
  
  private static final char GREEK_SMALL_LETTER_SIGMA_ = 'σ';
  
  private static final char GREEK_SMALL_LETTER_RHO_ = 'ς';
  
  private static final int HYPHEN_ = 8208;
  
  private static final int SOFT_HYPHEN_ = 173;
  
  private static final int LAST_CHAR_MASK_ = 65535;
  
  private static final int LAST_BYTE_MASK_ = 255;
  
  private static final int SHIFT_16_ = 16;
  
  private static final int WHITE_SPACE_PROPERTY_ = 0;
  
  private static final int BIDI_CONTROL_PROPERTY_ = 1;
  
  private static final int JOIN_CONTROL_PROPERTY_ = 2;
  
  private static final int DASH_PROPERTY_ = 3;
  
  private static final int HYPHEN_PROPERTY_ = 4;
  
  private static final int QUOTATION_MARK_PROPERTY_ = 5;
  
  private static final int TERMINAL_PUNCTUATION_PROPERTY_ = 6;
  
  private static final int MATH_PROPERTY_ = 7;
  
  private static final int HEX_DIGIT_PROPERTY_ = 8;
  
  private static final int ASCII_HEX_DIGIT_PROPERTY_ = 9;
  
  private static final int ALPHABETIC_PROPERTY_ = 10;
  
  private static final int IDEOGRAPHIC_PROPERTY_ = 11;
  
  private static final int DIACRITIC_PROPERTY_ = 12;
  
  private static final int EXTENDER_PROPERTY_ = 13;
  
  private static final int LOWERCASE_PROPERTY_ = 14;
  
  private static final int UPPERCASE_PROPERTY_ = 15;
  
  private static final int NONCHARACTER_CODE_POINT_PROPERTY_ = 16;
  
  private static final int GRAPHEME_EXTEND_PROPERTY_ = 17;
  
  private static final int GRAPHEME_LINK_PROPERTY_ = 18;
  
  private static final int IDS_BINARY_OPERATOR_PROPERTY_ = 19;
  
  private static final int IDS_TRINARY_OPERATOR_PROPERTY_ = 20;
  
  private static final int RADICAL_PROPERTY_ = 21;
  
  private static final int UNIFIED_IDEOGRAPH_PROPERTY_ = 22;
  
  private static final int DEFAULT_IGNORABLE_CODE_POINT_PROPERTY_ = 23;
  
  private static final int DEPRECATED_PROPERTY_ = 24;
  
  private static final int SOFT_DOTTED_PROPERTY_ = 25;
  
  private static final int LOGICAL_ORDER_EXCEPTION_PROPERTY_ = 26;
  
  private static final int XID_START_PROPERTY_ = 27;
  
  private static final int XID_CONTINUE_PROPERTY_ = 28;
  
  private static final int ID_START_PROPERTY_ = 29;
  
  private static final int ID_CONTINUE_PROPERTY_ = 30;
  
  private static final int GRAPHEME_BASE_PROPERTY_ = 31;
  
  private static final int BINARY_1_TOP_PROPERTY_ = 32;
  
  private static final int FIRST_NIBBLE_SHIFT_ = 4;
  
  private static final int LAST_NIBBLE_MASK_ = 15;
  
  private static final int AGE_SHIFT_ = 24;
  
  private static final int TAB = 9;
  
  private static final int LF = 10;
  
  private static final int FF = 12;
  
  private static final int CR = 13;
  
  private static final int U_A = 65;
  
  private static final int U_Z = 90;
  
  private static final int U_a = 97;
  
  private static final int U_z = 122;
  
  private static final int DEL = 127;
  
  private static final int NL = 133;
  
  private static final int NBSP = 160;
  
  private static final int CGJ = 847;
  
  private static final int FIGURESP = 8199;
  
  private static final int HAIRSP = 8202;
  
  private static final int ZWNJ = 8204;
  
  private static final int ZWJ = 8205;
  
  private static final int RLM = 8207;
  
  private static final int NNBSP = 8239;
  
  private static final int WJ = 8288;
  
  private static final int INHSWAP = 8298;
  
  private static final int NOMDIG = 8303;
  
  private static final int ZWNBSP = 65279;
  

  private UCharacterProperty()
    throws IOException
  {
    InputStream i = getClass().getResourceAsStream("data/uprops.icu");
    BufferedInputStream b = new BufferedInputStream(i, 25000);
    
    UCharacterPropertyReader reader = new UCharacterPropertyReader(b);
    reader.read(this);
    b.close();
    i.close();
    m_trie_.putIndexData(this);
  }
  




















































































  private boolean isPrecededBySoftDotted(UCharacterIterator uchariter, int offset)
  {
    uchariter.setIndex(offset);
    
    int ch = uchariter.previousCodePoint();
    
    while (ch != -1) {
      if (isSoftDotted(ch)) {
        return true;
      }
      
      int cc = NormalizerImpl.getCombiningClass(ch);
      if ((cc == 0) || (cc == 230))
      {

        return false;
      }
      ch = uchariter.previousCodePoint();
    }
    
    return false;
  }
  










  private boolean isCFINAL(UCharacterIterator uchariter, int offset)
  {
    uchariter.setIndex(offset);
    uchariter.nextCodePoint();
    int ch = uchariter.nextCodePoint();
    
    while (ch != -1) {
      int cat = getProperty(ch) & 0x1F;
      if (isCased(ch, cat)) {
        return false;
      }
      if (!isCaseIgnorable(ch, cat)) {
        return true;
      }
      ch = uchariter.nextCodePoint();
    }
    
    return true;
  }
  









  private boolean isNotCINITIAL(UCharacterIterator uchariter, int offset)
  {
    uchariter.setIndex(offset);
    int ch = uchariter.previousCodePoint();
    
    while (ch != -1) {
      int cat = getProperty(ch) & 0x1F;
      if (isCased(ch, cat)) {
        return true;
      }
      if (!isCaseIgnorable(ch, cat)) {
        return false;
      }
      ch = uchariter.previousCodePoint();
    }
    
    return false;
  }
  















































  private boolean isPrecededByI(UCharacterIterator uchariter, int offset)
  {
    uchariter.setIndex(offset);
    for (;;) {
      int c = uchariter.previousCodePoint();
      if (c < 0) {
        break;
      }
      if (c == 73) {
        return true;
      }
      
      int cc = NormalizerImpl.getCombiningClass(c);
      if ((cc == 0) || (cc == 230))
      {

        return false;
      }
    }
    
    return false;
  }
  










  private static boolean isFollowedByMOREABOVE(UCharacterIterator uchariter, int offset)
  {
    uchariter.setIndex(offset);
    uchariter.nextCodePoint();
    int ch = uchariter.nextCodePoint();
    
    while (ch != -1) {
      int cc = NormalizerImpl.getCombiningClass(ch);
      if (cc == 230) {
        return true;
      }
      if (cc == 0) {
        return false;
      }
      ch = uchariter.nextCodePoint();
    }
    
    return false;
  }
  










  private static boolean isFollowedByDotAbove(UCharacterIterator uchariter, int offset)
  {
    uchariter.setIndex(offset);
    uchariter.nextCodePoint();
    int ch = uchariter.nextCodePoint();
    
    while (ch != -1) {
      if (ch == 775) {
        return true;
      }
      int cc = NormalizerImpl.getCombiningClass(ch);
      if ((cc == 0) || (cc == 230)) {
        return false;
      }
      ch = uchariter.nextCodePoint();
    }
    
    return false;
  }
  






  private static boolean isCaseIgnorable(int ch, int cat)
  {
    return (cat == 6) || (cat == 7) || (cat == 16) || (cat == 4) || (cat == 26) || (ch == 39) || (ch == 173) || (ch == 8217);
  }
  














  private boolean isCased(int ch, int cat)
  {
    boolean result = (cat == 3) || (cat == 1) || (cat == 2);
    

    if (result) {
      return result;
    }
    int prop = getAdditional(ch, 1);
    return (compareAdditionalType(prop, 15)) || (compareAdditionalType(prop, 14));
  }
  





  private boolean isSoftDotted(int ch)
  {
    return compareAdditionalType(getAdditional(ch, 1), 25);
  }
  









  private int addExceptionOffset(int evalue, int indicator, int address)
  {
    int result = address;
    if (indicator >= 8) {
      result += FLAGS_OFFSET_[(evalue & 0xFF)];
      evalue >>= 8;
      indicator -= 8;
    }
    int mask = (1 << indicator) - 1;
    result += FLAGS_OFFSET_[(evalue & mask)];
    return result;
  }
  






  private boolean compareAdditionalType(int property, int type)
  {
    return (property & 1 << type) != 0;
  }
  



























  public UnicodeSet addPropertyStarts(UnicodeSet set)
  {
    TrieIterator propsIter = new TrieIterator(m_trie_);
    RangeValueIterator.Element propsResult = new RangeValueIterator.Element();
    while (propsIter.next(propsResult)) {
      set.add(start);
    }
    
    TrieIterator propsVectorsIter = new TrieIterator(m_additionalTrie_);
    RangeValueIterator.Element propsVectorsResult = new RangeValueIterator.Element();
    while (propsVectorsIter.next(propsVectorsResult)) {
      set.add(start);
    }
    




    set.add(9);
    set.add(14);
    set.add(28);
    set.add(32);
    set.add(133);
    set.add(134);
    

    set.add(127);
    set.add(8202);
    set.add(8208);
    set.add(8298);
    set.add(8304);
    set.add(65279);
    set.add(65280);
    

    set.add(160);
    set.add(161);
    set.add(8199);
    set.add(8200);
    set.add(8239);
    set.add(8240);
    

    set.add(12295);
    set.add(12296);
    set.add(19968);
    set.add(19969);
    set.add(20108);
    set.add(20109);
    set.add(19977);
    set.add(19978);
    set.add(22235);
    set.add(22236);
    set.add(20116);
    set.add(20117);
    set.add(20845);
    set.add(20846);
    set.add(19971);
    set.add(19972);
    set.add(20843);
    set.add(20844);
    set.add(20061);
    set.add(20062);
    

    set.add(97);
    set.add(123);
    set.add(65);
    set.add(91);
    

    set.add(8288);
    set.add(65520);
    set.add(65532);
    set.add(917504);
    set.add(921600);
    

    set.add(847);
    set.add(848);
    

    set.add(8204);
    set.add(8206);
    

    set.add(4352);
    int value = 1;
    int value2;
    for (int c = 4442; c <= 4447; c++) {
      value2 = UCharacter.getIntPropertyValue(c, 4107);
      if (value != value2) {
        value = value2;
        set.add(c);
      }
    }
    
    set.add(4448);
    value = 2;
    for (c = 4515; c <= 4519; c++) {
      value2 = UCharacter.getIntPropertyValue(c, 4107);
      if (value != value2) {
        value = value2;
        set.add(c);
      }
    }
    
    set.add(4520);
    value = 3;
    for (c = 4602; c <= 4607; c++) {
      value2 = UCharacter.getIntPropertyValue(c, 4107);
      if (value != value2) {
        value = value2;
        set.add(c);
      }
    }
    











    return set;
  }
  





















































































  public UnicodeSet getInclusions()
  {
    UnicodeSet set = new UnicodeSet();
    NormalizerImpl.addPropertyStarts(set);
    addPropertyStarts(set);
    return set;
  }
}
