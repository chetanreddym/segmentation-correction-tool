package com.ibm.icu.text;

import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.NormalizerImpl;
import com.ibm.icu.lang.UCharacter;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Locale;





















































































































































public final class StringSearch
  extends SearchIterator
{
  private int m_textBeginOffset_;
  private int m_textLimitOffset_;
  private int m_matchedIndex_;
  private Pattern m_pattern_;
  private RuleBasedCollator m_collator_;
  private CollationElementIterator m_colEIter_;
  private CollationElementIterator m_utilColEIter_;
  private int m_ceMask_;
  private StringBuffer m_canonicalPrefixAccents_;
  private StringBuffer m_canonicalSuffixAccents_;
  private boolean m_isCanonicalMatch_;
  private static final int MAX_TABLE_SIZE_ = 257;
  private static final int INITIAL_ARRAY_SIZE_ = 256;
  private static final int SECOND_LAST_BYTE_SHIFT_ = 8;
  private static final int LAST_BYTE_MASK_ = 255;
  
  public StringSearch(String pattern, CharacterIterator target, RuleBasedCollator collator, BreakIterator breakiter)
  {
    super(target, breakiter);
    m_textBeginOffset_ = targetText.getBeginIndex();
    m_textLimitOffset_ = targetText.getEndIndex();
    m_collator_ = collator;
    m_colEIter_ = m_collator_.getCollationElementIterator(target);
    m_utilColEIter_ = collator.getCollationElementIterator("");
    m_ceMask_ = getMask(m_collator_.getStrength());
    m_isCanonicalMatch_ = false;
    m_pattern_ = new Pattern(pattern);
    m_matchedIndex_ = -1;
    
    initialize();
  }
  














  public StringSearch(String pattern, CharacterIterator target, RuleBasedCollator collator)
  {
    this(pattern, target, collator, BreakIterator.getCharacterInstance());
  }
  

















  public StringSearch(String pattern, CharacterIterator target, Locale locale)
  {
    this(pattern, target, (RuleBasedCollator)Collator.getInstance(locale), BreakIterator.getCharacterInstance(locale));
  }
  

















  public StringSearch(String pattern, String target)
  {
    this(pattern, new StringCharacterIterator(target), (RuleBasedCollator)Collator.getInstance(), BreakIterator.getCharacterInstance());
  }
  















  public int getStrength()
  {
    return m_collator_.getStrength();
  }
  

















  public RuleBasedCollator getCollator()
  {
    return m_collator_;
  }
  





  public String getPattern()
  {
    return m_pattern_.targetText;
  }
  









  public int getIndex()
  {
    int result = m_colEIter_.getOffset();
    if (isOutOfBounds(m_textBeginOffset_, m_textLimitOffset_, result)) {
      return -1;
    }
    return result;
  }
  








  public boolean isCanonical()
  {
    return m_isCanonicalMatch_;
  }
  




























  public void setStrength(int newStrength)
  {
    m_collator_.setStrength(newStrength);
    initialize();
  }
  














  public void setCollator(RuleBasedCollator collator)
  {
    if (collator == null) {
      throw new IllegalArgumentException("Collator can not be null");
    }
    m_collator_ = collator;
    m_ceMask_ = getMask(m_collator_.getStrength());
    
    initialize();
    m_colEIter_.setCollator(m_collator_);
    m_utilColEIter_.setCollator(m_collator_);
  }
  














  public void setPattern(String pattern)
  {
    if ((pattern == null) || (pattern.length() <= 0)) {
      throw new IllegalArgumentException("Pattern to search for can not be null or of length 0");
    }
    
    m_pattern_.targetText = pattern;
    initialize();
  }
  










  public void setTarget(CharacterIterator text)
  {
    super.setTarget(text);
    m_textBeginOffset_ = targetText.getBeginIndex();
    m_textLimitOffset_ = targetText.getEndIndex();
    m_colEIter_.setText(targetText);
  }
  




















  public void setIndex(int position)
  {
    super.setIndex(position);
    m_matchedIndex_ = -1;
    m_colEIter_.setExactOffset(position);
  }
  









  public void setCanonical(boolean allowCanonical)
  {
    m_isCanonicalMatch_ = allowCanonical;
    if (m_isCanonicalMatch_ == true) {
      if (m_canonicalPrefixAccents_ == null) {
        m_canonicalPrefixAccents_ = new StringBuffer();
      }
      else {
        m_canonicalPrefixAccents_.delete(0, m_canonicalPrefixAccents_.length());
      }
      
      if (m_canonicalSuffixAccents_ == null) {
        m_canonicalSuffixAccents_ = new StringBuffer();
      }
      else {
        m_canonicalSuffixAccents_.delete(0, m_canonicalSuffixAccents_.length());
      }
    }
  }
  






















  public void reset()
  {
    super.reset();
    m_isCanonicalMatch_ = false;
    m_ceMask_ = getMask(m_collator_.getStrength());
    
    initialize();
    m_colEIter_.setCollator(m_collator_);
    m_colEIter_.reset();
    m_utilColEIter_.setCollator(m_collator_);
  }
  
















  protected int handleNext(int start)
  {
    if (m_pattern_.m_CELength_ == 0) {
      matchLength = 0;
      if ((m_matchedIndex_ == -1) && (start == m_textBeginOffset_)) {
        m_matchedIndex_ = start;
        return m_matchedIndex_;
      }
      
      targetText.setIndex(start);
      char ch = targetText.current();
      
      char ch2 = targetText.next();
      if (ch2 == 65535) {
        m_matchedIndex_ = -1;
      }
      else {
        m_matchedIndex_ = targetText.getIndex();
      }
      if ((UTF16.isLeadSurrogate(ch)) && (UTF16.isTrailSurrogate(ch2))) {
        targetText.next();
        m_matchedIndex_ = targetText.getIndex();
      }
    }
    else {
      if (matchLength <= 0)
      {




        m_matchedIndex_ = -1;
      }
      

      if (m_isCanonicalMatch_)
      {
        handleNextCanonical(start);
      }
      else {
        handleNextExact(start);
      }
    }
    if (m_matchedIndex_ == -1) {
      targetText.setIndex(m_textLimitOffset_);
    }
    else {
      targetText.setIndex(m_matchedIndex_);
    }
    return m_matchedIndex_;
  }
  














  protected int handlePrevious(int start)
  {
    if (m_pattern_.m_CELength_ == 0) {
      matchLength = 0;
      
      targetText.setIndex(start);
      char ch = targetText.previous();
      if (ch == 65535) {
        m_matchedIndex_ = -1;
      }
      else {
        m_matchedIndex_ = targetText.getIndex();
        if ((UTF16.isTrailSurrogate(ch)) && 
          (UTF16.isLeadSurrogate(targetText.previous()))) {
          m_matchedIndex_ = targetText.getIndex();
        }
      }
    }
    else
    {
      if (matchLength == 0)
      {




        m_matchedIndex_ = -1;
      }
      if (m_isCanonicalMatch_)
      {
        handlePreviousCanonical(start);
      }
      else {
        handlePreviousExact(start);
      }
    }
    
    if (m_matchedIndex_ == -1) {
      targetText.setIndex(m_textBeginOffset_);
    }
    else {
      targetText.setIndex(m_matchedIndex_);
    }
    return m_matchedIndex_;
  }
  




  private static class Pattern
  {
    protected String targetText;
    



    protected int[] m_CE_;
    



    protected int m_CELength_;
    



    protected boolean m_hasPrefixAccents_;
    



    protected boolean m_hasSuffixAccents_;
    



    protected int m_defaultShiftSize_;
    



    protected char[] m_shift_;
    



    protected char[] m_backShift_;
    



    protected Pattern(String pattern)
    {
      targetText = pattern;
      m_CE_ = new int['Ā'];
      m_CELength_ = 0;
      m_hasPrefixAccents_ = false;
      m_hasSuffixAccents_ = false;
      m_defaultShiftSize_ = 1;
      m_shift_ = new char['ā'];
      m_backShift_ = new char['ā'];
    }
  }
  













































































  private int[] m_utilBuffer_ = new int[2];
  













  private static final int hash(int ce)
  {
    return CollationElementIterator.primaryOrder(ce) % 257;
  }
  








  private static final char getFCD(CharacterIterator str, int offset)
  {
    str.setIndex(offset);
    char ch = str.current();
    char result = NormalizerImpl.getFCD16(ch);
    
    if ((result != 0) && (str.getEndIndex() != offset + 1) && (UTF16.isLeadSurrogate(ch)))
    {
      ch = str.next();
      if (UTF16.isTrailSurrogate(ch)) {
        result = NormalizerImpl.getFCD16FromSurrogatePair(result, ch);
      } else {
        result = '\000';
      }
    }
    return result;
  }
  







  private static final char getFCD(String str, int offset)
  {
    char ch = str.charAt(offset);
    char result = NormalizerImpl.getFCD16(ch);
    
    if ((result != 0) && (str.length() != offset + 1) && (UTF16.isLeadSurrogate(ch)))
    {
      ch = str.charAt(offset + 1);
      if (UTF16.isTrailSurrogate(ch)) {
        result = NormalizerImpl.getFCD16FromSurrogatePair(result, ch);
      } else {
        result = '\000';
      }
    }
    return result;
  }
  









  private final int getCE(int ce)
  {
    ce &= m_ceMask_;
    
    if (m_collator_.isAlternateHandlingShifted())
    {




      if (m_collator_.m_variableTopValue_ << 16 > ce) {
        if (m_collator_.getStrength() == 3) {
          ce = CollationElementIterator.primaryOrder(ce);
        }
        else {
          ce = 0;
        }
      }
    }
    
    return ce;
  }
  








  private static final int[] append(int offset, int value, int[] array)
  {
    if (offset >= array.length) {
      int[] temp = new int[offset + 256];
      System.arraycopy(array, 0, temp, 0, array.length);
      array = temp;
    }
    array[offset] = value;
    return array;
  }
  








  private final int initializePatternCETable()
  {
    m_utilColEIter_.setText(m_pattern_.targetText);
    
    int offset = 0;
    int result = 0;
    int ce = m_utilColEIter_.next();
    
    while (ce != -1) {
      int newce = getCE(ce);
      if (newce != 0) {
        m_pattern_.m_CE_ = append(offset, newce, m_pattern_.m_CE_);
        offset++;
      }
      result += m_utilColEIter_.getMaxExpansion(ce) - 1;
      ce = m_utilColEIter_.next();
    }
    
    m_pattern_.m_CE_ = append(offset, 0, m_pattern_.m_CE_);
    m_pattern_.m_CELength_ = offset;
    
    return result;
  }
  





  private final int initializePattern()
  {
    m_pattern_.m_hasPrefixAccents_ = (getFCD(m_pattern_.targetText, 0) >> '\b' != 0);
    
    m_pattern_.m_hasSuffixAccents_ = ((getFCD(m_pattern_.targetText, m_pattern_.targetText.length() - 1) & 0xFF) != 0);
    



    return initializePatternCETable();
  }
  




















  private final void setShiftTable(char[] shift, char[] backshift, int[] cetable, int cesize, int expansionsize, char defaultforward, char defaultbackward)
  {
    for (int count = 0; count < 257; count++) {
      shift[count] = defaultforward;
    }
    cesize--;
    for (int count = 0; count < cesize; count++)
    {
      int temp = defaultforward - count - 1;
      shift[hash(cetable[count])] = (temp > 1 ? (char)temp : '\001');
    }
    shift[hash(cetable[cesize])] = '\001';
    
    shift[hash(0)] = '\001';
    
    for (int count = 0; count < 257; count++) {
      backshift[count] = defaultbackward;
    }
    for (int count = cesize; count > 0; count--)
    {
      backshift[hash(cetable[count])] = (count > expansionsize ? (char)(count - expansionsize) : '\001');
    }
    
    backshift[hash(cetable[0])] = '\001';
    backshift[hash(0)] = '\001';
  }
  


























  private final void initialize()
  {
    int expandlength = initializePattern();
    if (m_pattern_.m_CELength_ > 0) {
      char minlength = m_pattern_.m_CELength_ > expandlength ? (char)(m_pattern_.m_CELength_ - expandlength) : '\001';
      
      m_pattern_.m_defaultShiftSize_ = minlength;
      setShiftTable(m_pattern_.m_shift_, m_pattern_.m_backShift_, m_pattern_.m_CE_, m_pattern_.m_CELength_, expandlength, minlength, minlength);

    }
    else
    {
      m_pattern_.m_defaultShiftSize_ = 0;
    }
  }
  







  private final boolean isBreakUnit(int start, int end)
  {
    if (breakIterator != null) {
      int startindex = breakIterator.first();
      int endindex = breakIterator.last();
      

      if ((start < startindex) || (start > endindex) || (end < startindex) || (end > endindex))
      {
        return false;
      }
      


      boolean result = ((start == startindex) || (breakIterator.following(start - 1) == start)) && ((end == endindex) || (breakIterator.following(end - 1) == end));
      


      if (result)
      {
        m_utilColEIter_.setText(targetText, start);
        for (int count = 0; count < m_pattern_.m_CELength_; 
            count++) {
          int ce = getCE(m_utilColEIter_.next());
          if (ce == 0) {
            count--;

          }
          else if (ce != m_pattern_.m_CE_[count]) {
            return false;
          }
        }
        int nextce = m_utilColEIter_.next();
        
        while ((m_utilColEIter_.getOffset() == end) && (getCE(nextce) == 0)) {
          nextce = m_utilColEIter_.next();
        }
        if ((nextce != -1) && (m_utilColEIter_.getOffset() == end))
        {

          return false;
        }
      }
      return result;
    }
    return true;
  }
  











  private final int getNextBaseOffset(CharacterIterator text, int textoffset)
  {
    if (textoffset < text.getEndIndex()) {
      while (text.getIndex() < text.getEndIndex()) {
        int result = textoffset;
        if (getFCD(text, textoffset++) >> '\b' == 0)
        {
          return result;
        }
      }
      return text.getEndIndex();
    }
    return textoffset;
  }
  








  private final int getNextBaseOffset(int textoffset)
  {
    if ((m_pattern_.m_hasSuffixAccents_) && (textoffset < m_textLimitOffset_))
    {
      targetText.setIndex(textoffset);
      targetText.previous();
      if ((getFCD(targetText, targetText.getIndex()) & 0xFF) != 0) {
        return getNextBaseOffset(targetText, textoffset);
      }
    }
    return textoffset;
  }
  












  private int shiftForward(int textoffset, int ce, int patternceindex)
  {
    if (ce != -1) {
      int shift = m_pattern_.m_shift_[hash(ce)];
      

      int adjust = m_pattern_.m_CELength_ - patternceindex;
      if ((adjust > 1) && (shift >= adjust)) {
        shift -= adjust - 1;
      }
      textoffset += shift;
    }
    else {
      textoffset += m_pattern_.m_defaultShiftSize_;
    }
    
    textoffset = getNextBaseOffset(textoffset);
    





    return textoffset;
  }
  








  private final int getNextSafeOffset(int textoffset, int end)
  {
    int result = textoffset;
    targetText.setIndex(result);
    while ((result != end) && (m_collator_.isUnsafe(targetText.current())))
    {
      result++;
      targetText.setIndex(result);
    }
    return result;
  }
  























  private final boolean checkExtraMatchAccents(int start, int end)
  {
    boolean result = false;
    if (m_pattern_.m_hasPrefixAccents_) {
      targetText.setIndex(start);
      
      if ((UTF16.isLeadSurrogate(targetText.next())) && 
        (!UTF16.isTrailSurrogate(targetText.next()))) {
        targetText.previous();
      }
      

      String str = getString(targetText, start, end);
      if (Normalizer.quickCheck(str, Normalizer.NFD, 0) == Normalizer.NO)
      {
        int safeoffset = getNextSafeOffset(start, end);
        if (safeoffset != end) {
          safeoffset++;
        }
        String decomp = Normalizer.decompose(str.substring(0, safeoffset - start), false);
        
        m_utilColEIter_.setText(decomp);
        int firstce = m_pattern_.m_CE_[0];
        boolean ignorable = true;
        int ce = 0;
        int offset = 0;
        while (ce != firstce) {
          offset = m_utilColEIter_.getOffset();
          if ((ce != firstce) && (ce != 0))
          {
            ignorable = false;
          }
          ce = m_utilColEIter_.next();
        }
        m_utilColEIter_.setExactOffset(offset);
        m_utilColEIter_.previous();
        offset = m_utilColEIter_.getOffset();
        result = (!ignorable) && (UCharacter.getCombiningClass(UTF16.charAt(decomp, offset)) != 0);
      }
    }
    

    return result;
  }
  





















  private final boolean hasAccentsBeforeMatch(int start, int end)
  {
    if (m_pattern_.m_hasPrefixAccents_)
    {
      boolean ignorable = true;
      int firstce = m_pattern_.m_CE_[0];
      m_colEIter_.setExactOffset(start);
      int ce = getCE(m_colEIter_.next());
      while (ce != firstce) {
        if (ce != 0) {
          ignorable = false;
        }
        ce = getCE(m_colEIter_.next());
      }
      if ((!ignorable) && (m_colEIter_.isInBuffer()))
      {
        return true;
      }
      

      boolean accent = getFCD(targetText, start) >> '\b' != 0;
      
      if (!accent) {
        return checkExtraMatchAccents(start, end);
      }
      if (!ignorable) {
        return true;
      }
      if (start > m_textBeginOffset_) {
        targetText.setIndex(start);
        targetText.previous();
        if ((getFCD(targetText, targetText.getIndex()) & 0xFF) != 0)
        {
          m_colEIter_.setExactOffset(start);
          ce = m_colEIter_.previous();
          if ((ce != -1) && (ce != 0))
          {
            return true;
          }
        }
      }
    }
    
    return false;
  }
  















  private final boolean hasAccentsAfterMatch(int start, int end)
  {
    if (m_pattern_.m_hasSuffixAccents_) {
      targetText.setIndex(end);
      if ((end > m_textBeginOffset_) && (UTF16.isTrailSurrogate(targetText.previous())))
      {
        if ((targetText.getIndex() > m_textBeginOffset_) && (!UTF16.isLeadSurrogate(targetText.previous())))
        {
          targetText.next();
        }
      }
      if ((getFCD(targetText, targetText.getIndex()) & 0xFF) != 0) {
        int firstce = m_pattern_.m_CE_[0];
        m_colEIter_.setExactOffset(start);
        while (getCE(m_colEIter_.next()) != firstce) {}
        
        int count = 1;
        while (count < m_pattern_.m_CELength_) {
          if (getCE(m_colEIter_.next()) == 0)
          {
            count--;
          }
          count++;
        }
        int ce = getCE(m_colEIter_.next());
        if ((ce != -1) && (ce != 0))
        {
          if (m_colEIter_.getOffset() <= end) {
            return true;
          }
          if (getFCD(targetText, end) >> '\b' != 0)
          {
            return true;
          }
        }
      }
    }
    return false;
  }
  








  private static final boolean isOutOfBounds(int textstart, int textlimit, int offset)
  {
    return (offset < textstart) || (offset > textlimit);
  }
  







  private final boolean checkIdentical(int start, int end)
  {
    if (m_collator_.getStrength() != 15) {
      return true;
    }
    
    String textstr = getString(targetText, start, end - start);
    if (Normalizer.quickCheck(textstr, Normalizer.NFD, 0) == Normalizer.NO)
    {
      textstr = Normalizer.decompose(textstr, false);
    }
    String patternstr = m_pattern_.targetText;
    if (Normalizer.quickCheck(patternstr, Normalizer.NFD, 0) == Normalizer.NO)
    {
      patternstr = Normalizer.decompose(patternstr, false);
    }
    return textstr.equals(patternstr);
  }
  






  private final boolean checkRepeatedMatch(int start, int limit)
  {
    if (m_matchedIndex_ == -1) {
      return false;
    }
    int end = limit - 1;
    int lastmatchend = m_matchedIndex_ + matchLength - 1;
    if (!isOverlapping()) {
      return ((start >= m_matchedIndex_) && (start <= lastmatchend)) || ((end >= m_matchedIndex_) && (end <= lastmatchend));
    }
    

    return start == m_matchedIndex_;
  }
  













  private final boolean checkNextExactContractionMatch(int start, int end)
  {
    char endchar = '\000';
    if (end < m_textLimitOffset_) {
      targetText.setIndex(end);
      endchar = targetText.current();
    }
    char poststartchar = '\000';
    if (start + 1 < m_textLimitOffset_) {
      targetText.setIndex(start + 1);
      poststartchar = targetText.current();
    }
    if ((m_collator_.isUnsafe(endchar)) || (m_collator_.isUnsafe(poststartchar)))
    {

      int bufferedCEOffset = m_colEIter_.m_CEBufferOffset_;
      boolean hasBufferedCE = bufferedCEOffset > 0;
      m_colEIter_.setExactOffset(start);
      int temp = start;
      while (bufferedCEOffset > 0)
      {






        m_colEIter_.next();
        if (m_colEIter_.getOffset() != temp) {
          start = temp;
          temp = m_colEIter_.getOffset();
        }
        bufferedCEOffset--;
      }
      
      int count = 0;
      while (count < m_pattern_.m_CELength_) {
        int ce = getCE(m_colEIter_.next());
        if (ce != 0)
        {

          if ((hasBufferedCE) && (count == 0) && (m_colEIter_.getOffset() != temp))
          {
            start = temp;
            temp = m_colEIter_.getOffset();
          }
          if (ce != m_pattern_.m_CE_[count]) {
            end++;
            end = getNextBaseOffset(end);
            m_utilBuffer_[0] = start;
            m_utilBuffer_[1] = end;
            return false;
          }
          count++;
        }
      } }
    m_utilBuffer_[0] = start;
    m_utilBuffer_[1] = end;
    return true;
  }
  


















  private final boolean checkNextExactMatch(int textoffset)
  {
    int start = m_colEIter_.getOffset();
    if (!checkNextExactContractionMatch(start, textoffset))
    {
      m_utilBuffer_[0] = m_utilBuffer_[1];
      return false;
    }
    
    start = m_utilBuffer_[0];
    textoffset = m_utilBuffer_[1];
    
    if ((!isBreakUnit(start, textoffset)) || (checkRepeatedMatch(start, textoffset)) || (hasAccentsBeforeMatch(start, textoffset)) || (!checkIdentical(start, textoffset)) || (hasAccentsAfterMatch(start, textoffset)))
    {



      textoffset++;
      textoffset = getNextBaseOffset(textoffset);
      m_utilBuffer_[0] = textoffset;
      return false;
    }
    

    m_matchedIndex_ = start;
    matchLength = (textoffset - start);
    return true;
  }
  









  private final int getPreviousBaseOffset(CharacterIterator text, int textoffset)
  {
    if (textoffset > m_textBeginOffset_) {
      for (;;) {
        int result = textoffset;
        text.setIndex(result);
        if ((UTF16.isTrailSurrogate(text.previous())) && 
          (text.getIndex() != text.getBeginIndex()) && (!UTF16.isLeadSurrogate(text.previous())))
        {
          text.next();
        }
        
        textoffset = text.getIndex();
        char fcd = getFCD(text, textoffset);
        if (fcd >> '\b' == 0) {
          if ((fcd & 0xFF) != 0) {
            return textoffset;
          }
          return result;
        }
        if (textoffset == m_textBeginOffset_) {
          return m_textBeginOffset_;
        }
      }
    }
    return textoffset;
  }
  









  private int getUnblockedAccentIndex(StringBuffer accents, int[] accentsindex)
  {
    int index = 0;
    int length = accents.length();
    int cclass = 0;
    int result = 0;
    while (index < length) {
      int codepoint = UTF16.charAt(accents, index);
      int tempclass = UCharacter.getCombiningClass(codepoint);
      if (tempclass != cclass) {
        cclass = tempclass;
        accentsindex[result] = index;
        result++;
      }
      if (UCharacter.isSupplementary(codepoint)) {
        index += 2;
      }
      else {
        index++;
      }
    }
    accentsindex[result] = length;
    return result;
  }
  













  private static final StringBuffer merge(StringBuffer source1, CharacterIterator source2, int start2, int end2, StringBuffer source3)
  {
    StringBuffer result = new StringBuffer();
    if ((source1 != null) && (source1.length() != 0))
    {
      if (ICUDebug.isJDK14OrHigher) {
        result.append(source1);
      } else {
        result.append(source1.toString());
      }
    }
    source2.setIndex(start2);
    while (source2.getIndex() < end2) {
      result.append(source2.current());
      source2.next();
    }
    if ((source3 != null) && (source3.length() != 0))
    {
      if (ICUDebug.isJDK14OrHigher) {
        result.append(source3);
      } else {
        result.append(source3.toString());
      }
    }
    return result;
  }
  






  private final boolean checkCollationMatch(CollationElementIterator coleiter)
  {
    int patternceindex = m_pattern_.m_CELength_;
    int offset = 0;
    while (patternceindex > 0) {
      int ce = getCE(coleiter.next());
      if (ce != 0)
      {

        if (ce != m_pattern_.m_CE_[offset]) {
          return false;
        }
        offset++;
        patternceindex--;
      } }
    return true;
  }
  


















  private int doNextCanonicalPrefixMatch(int start, int end)
  {
    if ((getFCD(targetText, start) & 0xFF) == 0)
    {
      return -1;
    }
    
    start = targetText.getIndex();
    int offset = getNextBaseOffset(targetText, start);
    start = getPreviousBaseOffset(start);
    
    StringBuffer accents = new StringBuffer();
    String accentstr = getString(targetText, start, offset - start);
    
    if (Normalizer.quickCheck(accentstr, Normalizer.NFD, 0) == Normalizer.NO)
    {
      accentstr = Normalizer.decompose(accentstr, false);
    }
    accents.append(accentstr);
    
    int[] accentsindex = new int['Ā'];
    int accentsize = getUnblockedAccentIndex(accents, accentsindex);
    int count = (2 << accentsize - 1) - 2;
    while (count > 0)
    {
      m_canonicalPrefixAccents_.delete(0, m_canonicalPrefixAccents_.length());
      
      for (int k = 0; 
          k < accentsindex[0]; k++) {
        m_canonicalPrefixAccents_.append(accents.charAt(k));
      }
      

      for (int i = 0; i <= accentsize - 1; i++) {
        int mask = 1 << accentsize - i - 1;
        if ((count & mask) != 0) {
          for (int j = accentsindex[i]; j < accentsindex[(i + 1)]; 
              j++) {
            m_canonicalPrefixAccents_.append(accents.charAt(j));
          }
        }
      }
      StringBuffer match = merge(m_canonicalPrefixAccents_, targetText, offset, end, m_canonicalSuffixAccents_);
      




      m_utilColEIter_.setText(match.toString());
      if (checkCollationMatch(m_utilColEIter_)) {
        return start;
      }
      count--;
    }
    return -1;
  }
  








  private final int getPreviousSafeOffset(int start, int textoffset)
  {
    int result = textoffset;
    targetText.setIndex(textoffset);
    while ((result >= start) && (m_collator_.isUnsafe(targetText.previous()))) {
      result = targetText.getIndex();
    }
    if (result != start)
    {
      result = targetText.getIndex();
    }
    return result;
  }
  














  private int doNextCanonicalSuffixMatch(int textoffset)
  {
    int safelength = 0;
    
    int safeoffset = m_textBeginOffset_;
    StringBuffer safetext;
    if ((textoffset != m_textBeginOffset_) && (m_canonicalSuffixAccents_.length() > 0) && (m_collator_.isUnsafe(m_canonicalSuffixAccents_.charAt(0))))
    {

      safeoffset = getPreviousSafeOffset(m_textBeginOffset_, textoffset);
      
      safelength = textoffset - safeoffset;
      safetext = merge(null, targetText, safeoffset, textoffset, m_canonicalSuffixAccents_);
    }
    else
    {
      safetext = m_canonicalSuffixAccents_;
    }
    

    CollationElementIterator coleiter = m_utilColEIter_;
    coleiter.setText(safetext.toString());
    

    int ceindex = m_pattern_.m_CELength_ - 1;
    boolean isSafe = true;
    
    while (ceindex >= 0) {
      int textce = coleiter.previous();
      if (textce == -1)
      {
        if (coleiter == m_colEIter_) {
          return -1;
        }
        coleiter = m_colEIter_;
        if (safetext != m_canonicalSuffixAccents_) {
          safetext.delete(0, safetext.length());
        }
        coleiter.setExactOffset(safeoffset);
        
        isSafe = false;
      }
      else {
        textce = getCE(textce);
        if ((textce != 0) && (textce != m_pattern_.m_CE_[ceindex]))
        {

          int failedoffset = coleiter.getOffset();
          if ((isSafe) && (failedoffset >= safelength))
          {
            return -1;
          }
          
          if (isSafe) {
            failedoffset += safeoffset;
          }
          

          int result = doNextCanonicalPrefixMatch(failedoffset, textoffset);
          
          if (result != -1)
          {
            m_colEIter_.setExactOffset(result);
          }
          return result;
        }
        
        if (textce == m_pattern_.m_CE_[ceindex]) {
          ceindex--;
        }
      }
    }
    if (isSafe) {
      int result = coleiter.getOffset();
      
      int leftoverces = m_CEBufferOffset_;
      if (result >= safelength) {
        result = textoffset;
      }
      else {
        result += safeoffset;
      }
      m_colEIter_.setExactOffset(result);
      m_colEIter_.m_CEBufferOffset_ = leftoverces;
      return result;
    }
    
    return coleiter.getOffset();
  }
  


















  private boolean doNextCanonicalMatch(int textoffset)
  {
    int offset = m_colEIter_.getOffset();
    targetText.setIndex(textoffset);
    if ((UTF16.isTrailSurrogate(targetText.previous())) && (targetText.getIndex() > m_textBeginOffset_))
    {
      if (!UTF16.isLeadSurrogate(targetText.previous())) {
        targetText.next();
      }
    }
    if ((getFCD(targetText, targetText.getIndex()) & 0xFF) == 0) {
      if (m_pattern_.m_hasPrefixAccents_) {
        offset = doNextCanonicalPrefixMatch(offset, textoffset);
        if (offset != -1) {
          m_colEIter_.setExactOffset(offset);
          return true;
        }
      }
      return false;
    }
    
    if (!m_pattern_.m_hasSuffixAccents_) {
      return false;
    }
    
    StringBuffer accents = new StringBuffer();
    
    int baseoffset = getPreviousBaseOffset(targetText, textoffset);
    
    String accentstr = getString(targetText, baseoffset, textoffset - baseoffset);
    
    if (Normalizer.quickCheck(accentstr, Normalizer.NFD, 0) == Normalizer.NO)
    {
      accentstr = Normalizer.decompose(accentstr, false);
    }
    accents.append(accentstr);
    

    int[] accentsindex = new int['Ā'];
    int size = getUnblockedAccentIndex(accents, accentsindex);
    

    int count = (2 << size - 1) - 2;
    while (count > 0) {
      m_canonicalSuffixAccents_.delete(0, m_canonicalSuffixAccents_.length());
      

      for (int k = 0; k < accentsindex[0]; k++) {
        m_canonicalSuffixAccents_.append(accents.charAt(k));
      }
      

      for (int i = 0; i <= size - 1; i++) {
        int mask = 1 << size - i - 1;
        if ((count & mask) != 0) {
          for (int j = accentsindex[i]; j < accentsindex[(i + 1)]; 
              j++) {
            m_canonicalSuffixAccents_.append(accents.charAt(j));
          }
        }
      }
      offset = doNextCanonicalSuffixMatch(baseoffset);
      if (offset != -1) {
        return true;
      }
      count--;
    }
    return false;
  }
  








  private final int getPreviousBaseOffset(int textoffset)
  {
    if ((m_pattern_.m_hasPrefixAccents_) && (textoffset > m_textBeginOffset_)) {
      int offset = textoffset;
      if (getFCD(targetText, offset) >> '\b' != 0) {
        return getPreviousBaseOffset(targetText, textoffset);
      }
    }
    return textoffset;
  }
  













  private boolean checkNextCanonicalContractionMatch(int start, int end)
  {
    char schar = '\000';
    char echar = '\000';
    if (end < m_textLimitOffset_) {
      targetText.setIndex(end);
      echar = targetText.current();
    }
    if (start < m_textLimitOffset_) {
      targetText.setIndex(start + 1);
      schar = targetText.current();
    }
    if ((m_collator_.isUnsafe(echar)) || (m_collator_.isUnsafe(schar))) {
      int expansion = m_colEIter_.m_CEBufferOffset_;
      boolean hasExpansion = expansion > 0;
      m_colEIter_.setExactOffset(start);
      int temp = start;
      while (expansion > 0)
      {






        m_colEIter_.next();
        if (m_colEIter_.getOffset() != temp) {
          start = temp;
          temp = m_colEIter_.getOffset();
        }
        expansion--;
      }
      
      int count = 0;
      while (count < m_pattern_.m_CELength_) {
        int ce = getCE(m_colEIter_.next());
        

        if (ce != 0)
        {

          if ((hasExpansion) && (count == 0) && (m_colEIter_.getOffset() != temp))
          {
            start = temp;
            temp = m_colEIter_.getOffset();
          }
          
          if ((count == 0) && (ce != m_pattern_.m_CE_[0]))
          {


            int expected = m_pattern_.m_CE_[0];
            if ((getFCD(targetText, start) & 0xFF) != 0) {
              ce = getCE(m_colEIter_.next());
              

              while ((ce != expected) && (ce != -1) && (m_colEIter_.getOffset() <= end)) {
                ce = getCE(m_colEIter_.next());
              }
            }
          }
          if (ce != m_pattern_.m_CE_[count]) {
            end++;
            end = getNextBaseOffset(end);
            m_utilBuffer_[0] = start;
            m_utilBuffer_[1] = end;
            return false;
          }
          count++;
        }
      } }
    m_utilBuffer_[0] = start;
    m_utilBuffer_[1] = end;
    return true;
  }
  


















  private boolean checkNextCanonicalMatch(int textoffset)
  {
    if (((m_pattern_.m_hasSuffixAccents_) && (m_canonicalSuffixAccents_.length() != 0)) || ((m_pattern_.m_hasPrefixAccents_) && (m_canonicalPrefixAccents_.length() != 0)))
    {


      m_matchedIndex_ = getPreviousBaseOffset(m_colEIter_.getOffset());
      matchLength = (textoffset - m_matchedIndex_);
      return true;
    }
    
    int start = m_colEIter_.getOffset();
    if (!checkNextCanonicalContractionMatch(start, textoffset))
    {
      m_utilBuffer_[0] = m_utilBuffer_[1];
      return false;
    }
    start = m_utilBuffer_[0];
    textoffset = m_utilBuffer_[1];
    start = getPreviousBaseOffset(start);
    
    if ((checkRepeatedMatch(start, textoffset)) || (!isBreakUnit(start, textoffset)) || (!checkIdentical(start, textoffset)))
    {

      textoffset++;
      textoffset = getNextBaseOffset(targetText, textoffset);
      m_utilBuffer_[0] = textoffset;
      return false;
    }
    
    m_matchedIndex_ = start;
    matchLength = (textoffset - start);
    return true;
  }
  










  private int reverseShift(int textoffset, int ce, int patternceindex)
  {
    if (isOverlapping()) {
      if (textoffset != m_textLimitOffset_) {
        textoffset--;
      }
      else {
        textoffset -= m_pattern_.m_defaultShiftSize_;
      }
      
    }
    else if (ce != -1) {
      int shift = m_pattern_.m_backShift_[hash(ce)];
      


      int adjust = patternceindex;
      if ((adjust > 1) && (shift > adjust)) {
        shift -= adjust - 1;
      }
      textoffset -= shift;
    }
    else {
      textoffset -= m_pattern_.m_defaultShiftSize_;
    }
    

    textoffset = getPreviousBaseOffset(textoffset);
    return textoffset;
  }
  










  private boolean checkPreviousExactContractionMatch(int start, int end)
  {
    char echar = '\000';
    if (end < m_textLimitOffset_) {
      targetText.setIndex(end);
      echar = targetText.current();
    }
    char schar = '\000';
    if (start + 1 < m_textLimitOffset_) {
      targetText.setIndex(start + 1);
      schar = targetText.current();
    }
    if ((m_collator_.isUnsafe(echar)) || (m_collator_.isUnsafe(schar)))
    {
      int expansion = m_colEIter_.m_CEBufferSize_ - m_colEIter_.m_CEBufferOffset_;
      
      boolean hasExpansion = expansion > 0;
      m_colEIter_.setExactOffset(end);
      int temp = end;
      while (expansion > 0)
      {






        m_colEIter_.previous();
        if (m_colEIter_.getOffset() != temp) {
          end = temp;
          temp = m_colEIter_.getOffset();
        }
        expansion--;
      }
      
      int count = m_pattern_.m_CELength_;
      while (count > 0) {
        int ce = getCE(m_colEIter_.previous());
        

        if (ce != 0)
        {

          if ((hasExpansion) && (count == 0) && (m_colEIter_.getOffset() != temp))
          {
            end = temp;
            temp = m_colEIter_.getOffset();
          }
          if (ce != m_pattern_.m_CE_[(count - 1)]) {
            start--;
            start = getPreviousBaseOffset(targetText, start);
            m_utilBuffer_[0] = start;
            m_utilBuffer_[1] = end;
            return false;
          }
          count--;
        }
      } }
    m_utilBuffer_[0] = start;
    m_utilBuffer_[1] = end;
    return true;
  }
  

















  private final boolean checkPreviousExactMatch(int textoffset)
  {
    int end = m_colEIter_.getOffset();
    if (!checkPreviousExactContractionMatch(textoffset, end)) {
      return false;
    }
    textoffset = m_utilBuffer_[0];
    end = m_utilBuffer_[1];
    


    if ((checkRepeatedMatch(textoffset, end)) || (!isBreakUnit(textoffset, end)) || (hasAccentsBeforeMatch(textoffset, end)) || (!checkIdentical(textoffset, end)) || (hasAccentsAfterMatch(textoffset, end)))
    {



      textoffset--;
      textoffset = getPreviousBaseOffset(targetText, textoffset);
      m_utilBuffer_[0] = textoffset;
      return false;
    }
    m_matchedIndex_ = textoffset;
    matchLength = (end - textoffset);
    return true;
  }
  

















  private int doPreviousCanonicalSuffixMatch(int start, int end)
  {
    targetText.setIndex(end);
    if ((UTF16.isTrailSurrogate(targetText.previous())) && (targetText.getIndex() > m_textBeginOffset_))
    {
      if (!UTF16.isLeadSurrogate(targetText.previous())) {
        targetText.next();
      }
    }
    if ((getFCD(targetText, targetText.getIndex()) & 0xFF) == 0)
    {
      return -1;
    }
    end = getNextBaseOffset(targetText, end);
    
    StringBuffer accents = new StringBuffer();
    int offset = getPreviousBaseOffset(targetText, end);
    
    String accentstr = getString(targetText, offset, end - offset);
    if (Normalizer.quickCheck(accentstr, Normalizer.NFD, 0) == Normalizer.NO)
    {
      accentstr = Normalizer.decompose(accentstr, false);
    }
    accents.append(accentstr);
    
    int[] accentsindex = new int['Ā'];
    int accentsize = getUnblockedAccentIndex(accents, accentsindex);
    int count = (2 << accentsize - 1) - 2;
    while (count > 0) {
      m_canonicalSuffixAccents_.delete(0, m_canonicalSuffixAccents_.length());
      

      for (int k = 0; k < accentsindex[0]; k++) {
        m_canonicalSuffixAccents_.append(accents.charAt(k));
      }
      

      for (int i = 0; i <= accentsize - 1; i++) {
        int mask = 1 << accentsize - i - 1;
        if ((count & mask) != 0) {
          for (int j = accentsindex[i]; j < accentsindex[(i + 1)]; 
              j++) {
            m_canonicalSuffixAccents_.append(accents.charAt(j));
          }
        }
      }
      StringBuffer match = merge(m_canonicalPrefixAccents_, targetText, start, offset, m_canonicalSuffixAccents_);
      



      m_utilColEIter_.setText(match.toString());
      if (checkCollationMatch(m_utilColEIter_)) {
        return end;
      }
      count--;
    }
    return -1;
  }
  















  private int doPreviousCanonicalPrefixMatch(int textoffset)
  {
    int safeoffset = textoffset;
    StringBuffer safetext;
    if ((textoffset > m_textBeginOffset_) && (m_collator_.isUnsafe(m_canonicalPrefixAccents_.charAt(m_canonicalPrefixAccents_.length() - 1))))
    {

      safeoffset = getNextSafeOffset(textoffset, m_textLimitOffset_);
      
      safetext = merge(m_canonicalPrefixAccents_, targetText, textoffset, safeoffset, null);
    }
    else
    {
      safetext = m_canonicalPrefixAccents_;
    }
    

    CollationElementIterator coleiter = m_utilColEIter_;
    coleiter.setText(safetext.toString());
    

    int ceindex = 0;
    boolean isSafe = true;
    int prefixlength = m_canonicalPrefixAccents_.length();
    
    while (ceindex < m_pattern_.m_CELength_) {
      int textce = coleiter.next();
      if (textce == -1)
      {
        if (coleiter == m_colEIter_) {
          return -1;
        }
        if (safetext != m_canonicalPrefixAccents_) {
          safetext.delete(0, safetext.length());
        }
        coleiter = m_colEIter_;
        coleiter.setExactOffset(safeoffset);
        
        isSafe = false;
      }
      else {
        textce = getCE(textce);
        if ((textce != 0) && (textce != m_pattern_.m_CE_[ceindex]))
        {

          int failedoffset = coleiter.getOffset();
          if ((isSafe) && (failedoffset <= prefixlength))
          {
            return -1;
          }
          
          if (isSafe) {
            failedoffset = safeoffset - failedoffset;
            if (safetext != m_canonicalPrefixAccents_) {
              safetext.delete(0, safetext.length());
            }
          }
          

          int result = doPreviousCanonicalSuffixMatch(textoffset, failedoffset);
          
          if (result != -1)
          {
            m_colEIter_.setExactOffset(result);
          }
          return result;
        }
        
        if (textce == m_pattern_.m_CE_[ceindex]) {
          ceindex++;
        }
      }
    }
    if (isSafe) {
      int result = coleiter.getOffset();
      
      int leftoverces = m_CEBufferSize_ - m_CEBufferOffset_;
      
      if (result <= prefixlength) {
        result = textoffset;
      }
      else {
        result = textoffset + (safeoffset - result);
      }
      m_colEIter_.setExactOffset(result);
      m_colEIter_.m_CEBufferOffset_ = (m_colEIter_.m_CEBufferSize_ - leftoverces);
      
      return result;
    }
    
    return coleiter.getOffset();
  }
  


















  private boolean doPreviousCanonicalMatch(int textoffset)
  {
    int offset = m_colEIter_.getOffset();
    if (getFCD(targetText, textoffset) >> '\b' == 0) {
      if (m_pattern_.m_hasSuffixAccents_) {
        offset = doPreviousCanonicalSuffixMatch(textoffset, offset);
        if (offset != -1) {
          m_colEIter_.setExactOffset(offset);
          return true;
        }
      }
      return false;
    }
    
    if (!m_pattern_.m_hasPrefixAccents_) {
      return false;
    }
    
    StringBuffer accents = new StringBuffer();
    
    int baseoffset = getNextBaseOffset(targetText, textoffset);
    
    String textstr = getString(targetText, textoffset, baseoffset - textoffset);
    
    if (Normalizer.quickCheck(textstr, Normalizer.NFD, 0) == Normalizer.NO)
    {
      textstr = Normalizer.decompose(textstr, false);
    }
    accents.append(textstr);
    

    int[] accentsindex = new int['Ā'];
    int size = getUnblockedAccentIndex(accents, accentsindex);
    

    int count = (2 << size - 1) - 2;
    while (count > 0) {
      m_canonicalPrefixAccents_.delete(0, m_canonicalPrefixAccents_.length());
      

      for (int k = 0; k < accentsindex[0]; k++) {
        m_canonicalPrefixAccents_.append(accents.charAt(k));
      }
      

      for (int i = 0; i <= size - 1; i++) {
        int mask = 1 << size - i - 1;
        if ((count & mask) != 0) {
          for (int j = accentsindex[i]; j < accentsindex[(i + 1)]; 
              j++) {
            m_canonicalPrefixAccents_.append(accents.charAt(j));
          }
        }
      }
      offset = doPreviousCanonicalPrefixMatch(baseoffset);
      if (offset != -1) {
        return true;
      }
      count--;
    }
    return false;
  }
  








  private boolean checkPreviousCanonicalContractionMatch(int start, int end)
  {
    int temp = end;
    

    char echar = '\000';
    char schar = '\000';
    if (end < m_textLimitOffset_) {
      targetText.setIndex(end);
      echar = targetText.current();
    }
    if (start + 1 < m_textLimitOffset_) {
      targetText.setIndex(start + 1);
      schar = targetText.current();
    }
    if ((m_collator_.isUnsafe(echar)) || (m_collator_.isUnsafe(schar))) {
      int expansion = m_colEIter_.m_CEBufferSize_ - m_colEIter_.m_CEBufferOffset_;
      
      boolean hasExpansion = expansion > 0;
      m_colEIter_.setExactOffset(end);
      while (expansion > 0)
      {






        m_colEIter_.previous();
        if (m_colEIter_.getOffset() != temp) {
          end = temp;
          temp = m_colEIter_.getOffset();
        }
        expansion--;
      }
      
      int count = m_pattern_.m_CELength_;
      while (count > 0) {
        int ce = getCE(m_colEIter_.previous());
        

        if (ce != 0)
        {

          if ((hasExpansion) && (count == 0) && (m_colEIter_.getOffset() != temp))
          {
            end = temp;
            temp = m_colEIter_.getOffset();
          }
          if ((count == m_pattern_.m_CELength_) && (ce != m_pattern_.m_CE_[(m_pattern_.m_CELength_ - 1)]))
          {


            int expected = m_pattern_.m_CE_[(m_pattern_.m_CELength_ - 1)];
            targetText.setIndex(end);
            if ((UTF16.isTrailSurrogate(targetText.previous())) && 
              (targetText.getIndex() > m_textBeginOffset_) && (!UTF16.isLeadSurrogate(targetText.previous())))
            {
              targetText.next();
            }
            
            end = targetText.getIndex();
            if ((getFCD(targetText, end) & 0xFF) != 0) {
              ce = getCE(m_colEIter_.previous());
              

              while ((ce != expected) && (ce != -1) && (m_colEIter_.getOffset() <= start)) {
                ce = getCE(m_colEIter_.previous());
              }
            }
          }
          if (ce != m_pattern_.m_CE_[(count - 1)]) {
            start--;
            start = getPreviousBaseOffset(start);
            m_utilBuffer_[0] = start;
            m_utilBuffer_[1] = end;
            return false;
          }
          count--;
        }
      } }
    m_utilBuffer_[0] = start;
    m_utilBuffer_[1] = end;
    return true;
  }
  


















  private boolean checkPreviousCanonicalMatch(int textoffset)
  {
    if (((m_pattern_.m_hasSuffixAccents_) && (m_canonicalSuffixAccents_.length() != 0)) || ((m_pattern_.m_hasPrefixAccents_) && (m_canonicalPrefixAccents_.length() != 0)))
    {


      m_matchedIndex_ = textoffset;
      matchLength = (getNextBaseOffset(m_colEIter_.getOffset()) - textoffset);
      
      return true;
    }
    
    int end = m_colEIter_.getOffset();
    if (!checkPreviousCanonicalContractionMatch(textoffset, end))
    {
      return false;
    }
    textoffset = m_utilBuffer_[0];
    end = m_utilBuffer_[1];
    end = getNextBaseOffset(end);
    
    if ((checkRepeatedMatch(textoffset, end)) || (!isBreakUnit(textoffset, end)) || (!checkIdentical(textoffset, end)))
    {

      textoffset--;
      textoffset = getPreviousBaseOffset(textoffset);
      m_utilBuffer_[0] = textoffset;
      return false;
    }
    
    m_matchedIndex_ = textoffset;
    matchLength = (end - textoffset);
    return true;
  }
  





  private void handleNextExact(int start)
  {
    int textoffset = shiftForward(start, -1, m_pattern_.m_CELength_);
    

    int targetce = 0;
    while (textoffset <= m_textLimitOffset_) {
      m_colEIter_.setExactOffset(textoffset);
      int patternceindex = m_pattern_.m_CELength_ - 1;
      boolean found = false;
      int lastce = -1;
      
      do
      {
        do
        {
          targetce = m_colEIter_.previous();
          if (targetce == -1) {
            found = false;
            break;
          }
          targetce = getCE(targetce);
        } while ((targetce == 0) && (m_colEIter_.isInBuffer()));
        




        if ((lastce == -1) || (lastce == 0))
        {
          lastce = targetce;
        }
        if (targetce == m_pattern_.m_CE_[patternceindex])
        {
          found = true;
          break;
        }
      } while (m_colEIter_.m_CEBufferOffset_ > 0);
      found = false;
      



      targetce = lastce;
      
      while ((found) && (patternceindex > 0)) {
        targetce = m_colEIter_.previous();
        if (targetce == -1) {
          found = false;
          break;
        }
        targetce = getCE(targetce);
        if (targetce != 0)
        {


          patternceindex--;
          found = (found) && (targetce == m_pattern_.m_CE_[patternceindex]);
        }
      }
      if (!found) {
        textoffset = shiftForward(textoffset, targetce, patternceindex);
        

        patternceindex = m_pattern_.m_CELength_;
      }
      else
      {
        if (checkNextExactMatch(textoffset))
        {
          return;
        }
        textoffset = m_utilBuffer_[0];
      } }
    setMatchNotFound();
  }
  





  private void handleNextCanonical(int start)
  {
    boolean hasPatternAccents = (m_pattern_.m_hasSuffixAccents_) || (m_pattern_.m_hasPrefixAccents_);
    




    int textoffset = shiftForward(start, -1, m_pattern_.m_CELength_);
    
    m_canonicalPrefixAccents_.delete(0, m_canonicalPrefixAccents_.length());
    m_canonicalSuffixAccents_.delete(0, m_canonicalSuffixAccents_.length());
    int targetce = 0;
    
    while (textoffset <= m_textLimitOffset_)
    {
      m_colEIter_.setExactOffset(textoffset);
      int patternceindex = m_pattern_.m_CELength_ - 1;
      boolean found = false;
      int lastce = -1;
      


      do
      {
        targetce = m_colEIter_.previous();
        if (targetce == -1) {
          found = false;
          break;
        }
        targetce = getCE(targetce);
        if ((lastce == -1) || (lastce == 0))
        {
          lastce = targetce;
        }
        if (targetce == m_pattern_.m_CE_[patternceindex])
        {
          found = true;
          break;
        }
      } while (m_colEIter_.m_CEBufferOffset_ > 0);
      found = false;
      


      targetce = lastce;
      
      while ((found) && (patternceindex > 0)) {
        targetce = m_colEIter_.previous();
        if (targetce == -1) {
          found = false;
          break;
        }
        targetce = getCE(targetce);
        if (targetce != 0)
        {


          patternceindex--;
          found = (found) && (targetce == m_pattern_.m_CE_[patternceindex]);
        }
      }
      
      if ((hasPatternAccents) && (!found)) {
        found = doNextCanonicalMatch(textoffset);
      }
      
      if (!found) {
        textoffset = shiftForward(textoffset, targetce, patternceindex);
        
        patternceindex = m_pattern_.m_CELength_;
      }
      else
      {
        if (checkNextCanonicalMatch(textoffset)) {
          return;
        }
        textoffset = m_utilBuffer_[0];
      } }
    setMatchNotFound();
  }
  





  private void handlePreviousExact(int start)
  {
    int textoffset = reverseShift(start, -1, m_pattern_.m_CELength_);
    
    while (textoffset >= m_textBeginOffset_)
    {
      m_colEIter_.setExactOffset(textoffset);
      int patternceindex = 1;
      int targetce = 0;
      boolean found = false;
      int firstce = -1;
      

      do
      {
        do
        {
          targetce = m_colEIter_.next();
          if (targetce == -1) {
            found = false;
            break;
          }
          targetce = getCE(targetce);
          if ((firstce == -1) || (firstce == 0))
          {
            firstce = targetce;
          }
        } while (targetce == 0);
        

        if (targetce == m_pattern_.m_CE_[0]) {
          found = true;
          break;
        }
      } while ((m_colEIter_.m_CEBufferOffset_ != -1) && (m_colEIter_.m_CEBufferOffset_ != m_colEIter_.m_CEBufferSize_));
      


      found = false;
      



      targetce = firstce;
      
      while ((found) && (patternceindex < m_pattern_.m_CELength_)) {
        targetce = m_colEIter_.next();
        if (targetce == -1) {
          found = false;
          break;
        }
        targetce = getCE(targetce);
        if (targetce != 0)
        {


          found = (found) && (targetce == m_pattern_.m_CE_[patternceindex]);
          patternceindex++;
        }
      }
      if (!found) {
        textoffset = reverseShift(textoffset, targetce, patternceindex);
        patternceindex = 0;
      }
      else
      {
        if (checkPreviousExactMatch(textoffset)) {
          return;
        }
        textoffset = m_utilBuffer_[0];
      } }
    setMatchNotFound();
  }
  





  private void handlePreviousCanonical(int start)
  {
    boolean hasPatternAccents = (m_pattern_.m_hasSuffixAccents_) || (m_pattern_.m_hasPrefixAccents_);
    




    int textoffset = reverseShift(start, -1, m_pattern_.m_CELength_);
    
    m_canonicalPrefixAccents_.delete(0, m_canonicalPrefixAccents_.length());
    m_canonicalSuffixAccents_.delete(0, m_canonicalSuffixAccents_.length());
    
    while (textoffset >= m_textBeginOffset_)
    {
      m_colEIter_.setExactOffset(textoffset);
      int patternceindex = 1;
      int targetce = 0;
      boolean found = false;
      int firstce = -1;
      



      do
      {
        targetce = m_colEIter_.next();
        if (targetce == -1) {
          found = false;
          break;
        }
        targetce = getCE(targetce);
        if ((firstce == -1) || (firstce == 0))
        {
          firstce = targetce;
        }
        
        if (targetce == m_pattern_.m_CE_[0])
        {
          found = true;
          break;
        }
      } while ((m_colEIter_.m_CEBufferOffset_ != -1) && (m_colEIter_.m_CEBufferOffset_ != m_colEIter_.m_CEBufferSize_));
      


      found = false;
      



      targetce = firstce;
      
      while ((found) && (patternceindex < m_pattern_.m_CELength_)) {
        targetce = m_colEIter_.next();
        if (targetce == -1) {
          found = false;
          break;
        }
        targetce = getCE(targetce);
        if (targetce != 0)
        {


          found = (found) && (targetce == m_pattern_.m_CE_[patternceindex]);
          patternceindex++;
        }
      }
      
      if ((hasPatternAccents) && (!found)) {
        found = doPreviousCanonicalMatch(textoffset);
      }
      
      if (!found) {
        textoffset = reverseShift(textoffset, targetce, patternceindex);
        patternceindex = 0;
      }
      else
      {
        if (checkPreviousCanonicalMatch(textoffset)) {
          return;
        }
        textoffset = m_utilBuffer_[0];
      } }
    setMatchNotFound();
  }
  








  private static final String getString(CharacterIterator text, int start, int length)
  {
    StringBuffer result = new StringBuffer(length);
    int offset = text.getIndex();
    text.setIndex(start);
    for (int i = 0; i < length; i++) {
      result.append(text.current());
      text.next();
    }
    text.setIndex(offset);
    return result.toString();
  }
  





  private static final int getMask(int strength)
  {
    switch (strength)
    {
    case 0: 
      return -65536;
    case 1: 
      return 65280;
    }
    
    return -1;
  }
  







  private void setMatchNotFound()
  {
    m_matchedIndex_ = -1;
    setMatchLength(0);
  }
}
