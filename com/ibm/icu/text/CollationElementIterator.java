package com.ibm.icu.text;

import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.IntTrie;
import com.ibm.icu.impl.NormalizerImpl;
import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.lang.UCharacter;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;















































































public final class CollationElementIterator
{
  public static final int NULLORDER = -1;
  public static final int IGNORABLE = 0;
  boolean m_isCodePointHiragana_;
  int m_FCDStart_;
  int m_CEBufferOffset_;
  int m_CEBufferSize_;
  private boolean m_isForwards_;
  private CharacterIterator m_source_;
  private int m_bufferOffset_;
  private StringBuffer m_buffer_;
  private int m_FCDLimit_;
  private RuleBasedCollator m_collator_;
  private boolean m_isHiragana4_;
  private int[] m_CEBuffer_;
  private static final int CE_BUFFER_INIT_SIZE_ = 512;
  private Backup m_utilSpecialBackUp_;
  private Backup m_utilSpecialEntryBackUp_;
  private Backup m_utilSpecialDiscontiguousBackUp_;
  private StringCharacterIterator m_srcUtilIter_;
  private StringBuffer m_utilStringBuffer_;
  private StringBuffer m_utilSkippedBuffer_;
  private CollationElementIterator m_utilColEIter_;
  private static final int FULL_ZERO_COMBINING_CLASS_FAST_LIMIT_ = 192;
  private static final int LEAD_ZERO_COMBINING_CLASS_FAST_LIMIT_ = 768;
  private static final int LAST_BYTE_MASK_ = 255;
  private static final int SECOND_LAST_BYTE_SHIFT_ = 8;
  static final int CE_NOT_FOUND_ = -268435456;
  private static final int CE_EXPANSION_ = -251658240;
  private static final int CE_CONTRACTION_ = -234881024;
  private static final int CE_THAI_ = -218103808;
  private static final int CE_NO_MORE_CES_ = 65793;
  private static final int CE_NO_MORE_CES_PRIMARY_ = 65536;
  private static final int CE_NO_MORE_CES_SECONDARY_ = 256;
  private static final int CE_NO_MORE_CES_TERTIARY_ = 1;
  private static final int CE_NOT_FOUND_TAG_ = 0;
  static final int CE_EXPANSION_TAG_ = 1;
  static final int CE_CONTRACTION_TAG_ = 2;
  private static final int CE_THAI_TAG_ = 3;
  private static final int CE_CHARSET_TAG_ = 4;
  private static final int CE_HANGUL_SYLLABLE_TAG_ = 6;
  private static final int CE_LEAD_SURROGATE_TAG_ = 7;
  private static final int CE_TRAIL_SURROGATE_TAG_ = 8;
  private static final int CE_CJK_IMPLICIT_TAG_ = 9;
  private static final int CE_IMPLICIT_TAG_ = 10;
  private static final int CE_SPEC_PROC_TAG_ = 11;
  private static final int CE_LONG_PRIMARY_TAG_ = 12;
  private static final int CE_CE_TAGS_COUNT = 13;
  private static final int CE_BYTE_COMMON_ = 5;
  private static final int HANGUL_SBASE_ = 44032;
  private static final int HANGUL_LBASE_ = 4352;
  private static final int HANGUL_VBASE_ = 4449;
  private static final int HANGUL_TBASE_ = 4519;
  private static final int HANGUL_VCOUNT_ = 21;
  private static final int HANGUL_TCOUNT_ = 28;
  private static final int CJK_BASE_ = 19968;
  private static final int CJK_LIMIT_ = 40960;
  private static final int CJK_COMPAT_USED_BASE_ = 64014;
  private static final int CJK_COMPAT_USED_LIMIT_ = 64048;
  private static final int CJK_A_BASE_ = 13312;
  private static final int CJK_A_LIMIT_ = 19904;
  private static final int CJK_B_BASE_ = 131072;
  private static final int CJK_B_LIMIT_ = 173792;
  private static final int NON_CJK_OFFSET_ = 1114112;
  
  public int getOffset()
  {
    if (m_bufferOffset_ != -1) {
      if (m_isForwards_) {
        return m_FCDLimit_;
      }
      return m_FCDStart_;
    }
    return m_source_.getIndex();
  }
  











  public int getMaxExpansion(int ce)
  {
    int start = 0;
    int limit = m_collator_.m_expansionEndCE_.length;
    long unsignedce = ce & 0xFFFFFFFF;
    while (start < limit - 1) {
      int mid = start + (limit - start >> 1);
      long midce = m_collator_.m_expansionEndCE_[mid] & 0xFFFFFFFF;
      if (unsignedce <= midce) {
        limit = mid;
      }
      else {
        start = mid;
      }
    }
    int result = 1;
    if (m_collator_.m_expansionEndCE_[start] == ce) {
      result = m_collator_.m_expansionEndCEMaxSize_[start];
    }
    else if ((limit < m_collator_.m_expansionEndCE_.length) && (m_collator_.m_expansionEndCE_[limit] == ce))
    {
      result = m_collator_.m_expansionEndCEMaxSize_[limit];
    }
    else if ((ce & 0xFFFF) == 192) {
      result = 2;
    }
    return result;
  }
  













  public void reset()
  {
    m_source_.setIndex(m_source_.getBeginIndex());
    updateInternalState();
  }
  






















  public int next()
  {
    m_isForwards_ = true;
    if (m_CEBufferSize_ > 0) {
      if (m_CEBufferOffset_ < m_CEBufferSize_)
      {
        return m_CEBuffer_[(m_CEBufferOffset_++)];
      }
      m_CEBufferSize_ = 0;
      m_CEBufferOffset_ = 0;
    }
    
    char ch = nextChar();
    

    if (ch == 65535) {
      return -1;
    }
    if (m_collator_.m_isHiragana4_) {
      m_isCodePointHiragana_ = ((ch >= '぀') && (ch <= 'ゞ') && ((ch <= 'ゔ') || (ch >= 'ゝ')));
    }
    

    int result = -1;
    if (ch <= 'ÿ')
    {


      result = m_collator_.m_trie_.getLatin1LinearValue(ch);
      if (RuleBasedCollator.isSpecial(result)) {
        result = nextSpecial(m_collator_, result, ch);
      }
    }
    else {
      result = m_collator_.m_trie_.getLeadValue(ch);
      
      if (RuleBasedCollator.isSpecial(result))
      {
        result = nextSpecial(m_collator_, result, ch);
      }
      if (result == -268435456)
      {


        result = UCA_m_trie_.getLeadValue(ch);
        if (RuleBasedCollator.isSpecial(result))
        {
          result = nextSpecial(RuleBasedCollator.UCA_, result, ch);
        }
      }
    }
    return result;
  }
  






















  public int previous()
  {
    if ((m_source_.getIndex() <= 0) && (m_isForwards_))
    {

      m_source_.setIndex(m_source_.getEndIndex());
      updateInternalState();
    }
    m_isForwards_ = false;
    int result = -1;
    if (m_CEBufferSize_ > 0) {
      if (m_CEBufferOffset_ > 0) {
        return m_CEBuffer_[(--m_CEBufferOffset_)];
      }
      m_CEBufferSize_ = 0;
      m_CEBufferOffset_ = 0;
    }
    char ch = previousChar();
    if (ch == 65535) {
      return -1;
    }
    if (m_collator_.m_isHiragana4_) {
      m_isCodePointHiragana_ = ((ch >= '぀') && (ch <= 'ゟ'));
    }
    if ((m_collator_.isContractionEnd(ch)) && (!isBackwardsStart())) {
      result = previousSpecial(m_collator_, -234881024, ch);
    }
    else {
      if ((m_bufferOffset_ < 0) && (m_source_.getIndex() != 0) && (isThaiPreVowel(peekCharacter(-1))))
      {

        backupInternalState(m_utilSpecialBackUp_);
        



        m_source_.previous();
        if ((m_source_.getIndex() == 0) || (!isThaiPreVowel(peekCharacter(-1))))
        {
          result = -218103808;




        }
        else
        {



          int noReordered = 1;
          
          while ((m_source_.getIndex() != 0) && (isThaiPreVowel(m_source_.previous()))) {
            noReordered++;
          }
          if ((noReordered & 0x1) != 0)
          {
            result = -218103808;
          } else {
            result = m_collator_.m_trie_.getLeadValue(ch);
          }
        }
        updateInternalState(m_utilSpecialBackUp_);
      }
      else if (ch <= 'ÿ') {
        result = m_collator_.m_trie_.getLatin1LinearValue(ch);
        if (RuleBasedCollator.isSpecial(result)) {
          result = previousSpecial(m_collator_, result, ch);
        }
      }
      else {
        result = m_collator_.m_trie_.getLeadValue(ch);
      }
      if (RuleBasedCollator.isSpecial(result)) {
        result = previousSpecial(m_collator_, result, ch);
      }
      if (result == -268435456) {
        if ((!isBackwardsStart()) && (m_collator_.isContractionEnd(ch)))
        {
          result = -234881024;
        }
        else {
          result = UCA_m_trie_.getLeadValue(ch);
        }
        

        if (RuleBasedCollator.isSpecial(result)) {
          result = previousSpecial(RuleBasedCollator.UCA_, result, ch);
        }
      }
    }
    
    return result;
  }
  







  public static final int primaryOrder(int ce)
  {
    return (ce & 0xFFFF0000) >>> 16;
  }
  







  public static final int secondaryOrder(int ce)
  {
    return (ce & 0xFF00) >> 8;
  }
  








  public static final int tertiaryOrder(int ce)
  {
    return ce & 0xFF;
  }
  






















  public void setOffset(int offset)
  {
    m_source_.setIndex(offset);
    char ch = m_source_.current();
    if ((ch != 65535) && (m_collator_.isUnsafe(ch)))
    {

      if (UTF16.isTrailSurrogate(ch))
      {
        prevch = m_source_.previous();
        if (!UTF16.isLeadSurrogate(prevch)) {
          m_source_.setIndex(offset);
        }
        
      }
      else
      {
        while (m_source_.getIndex() > 0) { char prevch;
          if (!m_collator_.isUnsafe(ch)) {
            break;
          }
          ch = m_source_.previous();
        }
        updateInternalState();
        int prevoffset = 0;
        while (m_source_.getIndex() <= offset) {
          prevoffset = m_source_.getIndex();
          next();
        }
        m_source_.setIndex(prevoffset);
      }
    }
    updateInternalState();
    

    offset = m_source_.getIndex();
    if (offset == m_source_.getBeginIndex())
    {

      m_isForwards_ = false;
    }
    else if (offset == m_source_.getEndIndex())
    {

      m_isForwards_ = true;
    }
  }
  







  public void setText(String source)
  {
    m_srcUtilIter_.setText(source);
    m_source_ = m_srcUtilIter_;
    updateInternalState();
  }
  







  public void setText(CharacterIterator source)
  {
    m_source_ = source;
    m_source_.setIndex(m_source_.getBeginIndex());
    updateInternalState();
  }
  










  public boolean equals(Object that)
  {
    if (that == this) {
      return true;
    }
    if ((that instanceof CollationElementIterator)) {
      CollationElementIterator thatceiter = (CollationElementIterator)that;
      
      if ((m_collator_.equals(m_collator_)) && (m_source_.equals(m_source_)))
      {
        return true;
      }
    }
    return false;
  }
  













  CollationElementIterator(String source, RuleBasedCollator collator)
  {
    m_srcUtilIter_ = new StringCharacterIterator(source);
    m_utilStringBuffer_ = new StringBuffer();
    m_source_ = m_srcUtilIter_;
    m_collator_ = collator;
    m_CEBuffer_ = new int['Ȁ'];
    m_buffer_ = new StringBuffer();
    m_utilSpecialBackUp_ = new Backup();
    updateInternalState();
  }
  












  CollationElementIterator(CharacterIterator source, RuleBasedCollator collator)
  {
    m_srcUtilIter_ = new StringCharacterIterator("");
    m_utilStringBuffer_ = new StringBuffer();
    m_source_ = source;
    m_collator_ = collator;
    m_CEBuffer_ = new int['Ȁ'];
    m_buffer_ = new StringBuffer();
    m_utilSpecialBackUp_ = new Backup();
    updateInternalState();
  }
  



































  void setCollator(RuleBasedCollator collator)
  {
    m_collator_ = collator;
    updateInternalState();
  }
  












  void setExactOffset(int offset)
  {
    m_source_.setIndex(offset);
    updateInternalState();
  }
  




  boolean isInBuffer()
  {
    return m_bufferOffset_ > 0;
  }
  






  static final boolean isThaiBaseConsonant(char ch)
  {
    return (ch >= 'ก') && (ch <= 'ฮ');
  }
  






  static final boolean isThaiPreVowel(char ch)
  {
    return ((ch >= 'เ') && (ch <= 'ไ')) || ((ch >= 'ເ') && (ch <= 'ໄ'));
  }
  














  void setText(CharacterIterator source, int offset)
  {
    m_source_ = source;
    m_source_.setIndex(offset);
    updateInternalState();
  }
  





  private static final class Backup
  {
    protected int m_FCDLimit_;
    




    protected int m_FCDStart_;
    



    protected boolean m_isCodePointHiragana_;
    



    protected int m_bufferOffset_;
    



    protected int m_offset_;
    



    protected StringBuffer m_buffer_;
    




    protected Backup()
    {
      m_buffer_ = new StringBuffer();
    }
  }
  



























































































































































  private void updateInternalState()
  {
    m_isCodePointHiragana_ = false;
    m_buffer_.setLength(0);
    m_bufferOffset_ = -1;
    m_CEBufferOffset_ = 0;
    m_CEBufferSize_ = 0;
    m_FCDLimit_ = -1;
    m_FCDStart_ = m_source_.getEndIndex();
    m_isHiragana4_ = m_collator_.m_isHiragana4_;
    m_isForwards_ = true;
  }
  




  private void backupInternalState(Backup backup)
  {
    m_offset_ = m_source_.getIndex();
    m_FCDLimit_ = m_FCDLimit_;
    m_FCDStart_ = m_FCDStart_;
    m_isCodePointHiragana_ = m_isCodePointHiragana_;
    m_bufferOffset_ = m_bufferOffset_;
    m_buffer_.setLength(0);
    if (m_bufferOffset_ >= 0)
    {
      if (ICUDebug.isJDK14OrHigher) {
        m_buffer_.append(m_buffer_);
      } else {
        m_buffer_.append(m_buffer_.toString());
      }
    }
  }
  




  private void updateInternalState(Backup backup)
  {
    m_source_.setIndex(m_offset_);
    m_isCodePointHiragana_ = m_isCodePointHiragana_;
    m_bufferOffset_ = m_bufferOffset_;
    m_FCDLimit_ = m_FCDLimit_;
    m_FCDStart_ = m_FCDStart_;
    m_buffer_.setLength(0);
    if (m_bufferOffset_ >= 0)
    {
      m_buffer_.append(m_buffer_.toString());
    }
  }
  





  private int getCombiningClass(char ch)
  {
    if ((ch >= '̀') && (m_collator_.isUnsafe(ch)))
    {
      return NormalizerImpl.getCombiningClass(ch);
    }
    return 0;
  }
  







  private void normalize()
  {
    int size = m_FCDLimit_ - m_FCDStart_;
    m_buffer_.setLength(0);
    m_source_.setIndex(m_FCDStart_);
    for (int i = 0; i < size; i++) {
      m_buffer_.append(m_source_.current());
      m_source_.next();
    }
    String decomp = Normalizer.decompose(m_buffer_.toString(), false);
    m_buffer_.setLength(0);
    m_buffer_.append(decomp);
    m_bufferOffset_ = 0;
  }
  

















  private boolean FCDCheck(char ch, int offset)
  {
    boolean result = true;
    


    m_FCDStart_ = offset;
    m_source_.setIndex(offset);
    
    char fcd = NormalizerImpl.getFCD16(ch);
    if ((fcd != 0) && (UTF16.isLeadSurrogate(ch))) {
      ch = m_source_.next();
      if (UTF16.isTrailSurrogate(ch)) {
        fcd = NormalizerImpl.getFCD16FromSurrogatePair(fcd, ch);
      } else {
        fcd = '\000';
      }
    }
    
    int prevTrailCC = fcd & 0xFF;
    
    if (prevTrailCC != 0)
    {
      for (;;)
      {
        ch = m_source_.next();
        if (ch == 65535) {
          break;
        }
        
        fcd = NormalizerImpl.getFCD16(ch);
        if ((fcd != 0) && (UTF16.isLeadSurrogate(ch))) {
          ch = m_source_.next();
          if (UTF16.isTrailSurrogate(ch)) {
            fcd = NormalizerImpl.getFCD16FromSurrogatePair(fcd, ch);
          } else {
            fcd = '\000';
          }
        }
        int leadCC = fcd >>> '\b';
        if (leadCC == 0) {
          break;
        }
        

        if (leadCC < prevTrailCC) {
          result = false;
        }
        
        prevTrailCC = fcd & 0xFF;
      }
    }
    m_FCDLimit_ = m_source_.getIndex();
    m_source_.setIndex(m_FCDStart_);
    m_source_.next();
    return result;
  }
  




  private char nextChar()
  {
    char result;
    



    if (m_bufferOffset_ < 0)
    {

      result = m_source_.current();
    }
    else
    {
      if (m_bufferOffset_ >= m_buffer_.length())
      {

        m_source_.setIndex(m_FCDLimit_);
        m_bufferOffset_ = -1;
        m_buffer_.setLength(0);
        return nextChar();
      }
      return m_buffer_.charAt(m_bufferOffset_++);
    }
    int startoffset = m_source_.getIndex();
    if ((result < 'À') || (m_collator_.getDecomposition() == 16) || (m_bufferOffset_ >= 0) || (m_FCDLimit_ > startoffset))
    {



      m_source_.next();
      return result;
    }
    
    if (result < '̀')
    {

      char next = m_source_.next();
      if ((next == 65535) || (next <= '̀'))
      {
        return result;
      }
    }
    


    if (!FCDCheck(result, startoffset)) {
      normalize();
      result = m_buffer_.charAt(0);
      m_bufferOffset_ = 1;
    }
    return result;
  }
  






  private void normalizeBackwards()
  {
    normalize();
    m_bufferOffset_ = m_buffer_.length();
  }
  


















  private boolean FCDCheckBackwards(char ch, int offset)
  {
    boolean result = true;
    char fcd = '\000';
    m_FCDLimit_ = (offset + 1);
    m_source_.setIndex(offset);
    if (!UTF16.isSurrogate(ch)) {
      fcd = NormalizerImpl.getFCD16(ch);
    }
    else if ((UTF16.isTrailSurrogate(ch)) && (m_FCDLimit_ > 0))
    {
      char trailch = ch;
      ch = m_source_.previous();
      if (UTF16.isLeadSurrogate(ch)) {
        fcd = NormalizerImpl.getFCD16(ch);
        if (fcd != 0) {
          fcd = NormalizerImpl.getFCD16FromSurrogatePair(fcd, trailch);
        }
      }
      else
      {
        fcd = '\000';
      }
    }
    
    int leadCC = fcd >>> '\b';
    


    while (leadCC != 0) {
      offset = m_source_.getIndex();
      if (offset == 0) {
        break;
      }
      ch = m_source_.previous();
      if (!UTF16.isSurrogate(ch)) {
        fcd = NormalizerImpl.getFCD16(ch);
      }
      else if ((UTF16.isTrailSurrogate(ch)) && (m_source_.getIndex() > 0)) {
        char trail = ch;
        ch = m_source_.previous();
        if (UTF16.isLeadSurrogate(ch)) {
          fcd = NormalizerImpl.getFCD16(ch);
        }
        if (fcd != 0) {
          fcd = NormalizerImpl.getFCD16FromSurrogatePair(fcd, trail);
        }
      }
      else {
        fcd = '\000';
      }
      int prevTrailCC = fcd & 0xFF;
      if (leadCC < prevTrailCC) {
        result = false;
      }
      leadCC = fcd >>> '\b';
    }
    


    if (fcd == 0) {
      m_FCDStart_ = offset;
    }
    else {
      m_FCDStart_ = m_source_.getIndex();
    }
    m_source_.setIndex(m_FCDLimit_);
    return result;
  }
  






  private char previousChar()
  {
    if (m_bufferOffset_ >= 0) {
      m_bufferOffset_ -= 1;
      if (m_bufferOffset_ >= 0) {
        return m_buffer_.charAt(m_bufferOffset_);
      }
      

      m_buffer_.setLength(0);
      if (m_FCDStart_ == m_source_.getBeginIndex()) {
        m_FCDStart_ = -1;
        m_source_.setIndex(m_source_.getBeginIndex());
        return 65535;
      }
      
      m_FCDLimit_ = m_FCDStart_;
      m_source_.setIndex(m_FCDStart_);
      return previousChar();
    }
    

    char result = m_source_.previous();
    int startoffset = m_source_.getIndex();
    if ((result < '̀') || (m_collator_.getDecomposition() == 16) || (m_FCDStart_ <= startoffset) || (m_source_.getIndex() == 0))
    {

      return result;
    }
    char ch = m_source_.previous();
    if (ch < 'À')
    {
      m_source_.next();
      return result;
    }
    
    if (!FCDCheckBackwards(result, startoffset)) {
      normalizeBackwards();
      m_bufferOffset_ -= 1;
      result = m_buffer_.charAt(m_bufferOffset_);
    }
    else
    {
      m_source_.setIndex(startoffset);
    }
    return result;
  }
  




  private final boolean isBackwardsStart()
  {
    return ((m_bufferOffset_ < 0) && (m_source_.getIndex() == 0)) || ((m_bufferOffset_ == 0) && (m_FCDStart_ <= 0));
  }
  





  private final boolean isEnd()
  {
    if (m_bufferOffset_ >= 0) {
      if (m_bufferOffset_ != m_buffer_.length()) {
        return false;
      }
      

      return m_FCDLimit_ == m_source_.getEndIndex();
    }
    
    return m_source_.getEndIndex() == m_source_.getIndex();
  }
  












  private final int nextSurrogate(RuleBasedCollator collator, int ce, char trail)
  {
    if (!UTF16.isTrailSurrogate(trail)) {
      updateInternalState(m_utilSpecialBackUp_);
      return 0;
    }
    

    int result = m_trie_.getTrailValue(ce, trail);
    if (result == -268435456) {
      updateInternalState(m_utilSpecialBackUp_);
    }
    return result;
  }
  






  private int getExpansionOffset(RuleBasedCollator collator, int ce)
  {
    return ((ce & 0xFFFFF0) >> 4) - m_expansionOffset_;
  }
  







  private int nextThai(RuleBasedCollator collator, int ce, char ch)
  {
    if ((m_bufferOffset_ != -1) || (isEnd()))
    {



      return m_expansion_[getExpansionOffset(collator, ce)];
    }
    
    if (!isEnd())
    {




      m_FCDStart_ = (m_source_.getIndex() - 1);
      char thCh = nextChar();
      int cp = thCh;
      if ((UTF16.isLeadSurrogate(thCh)) && 
        (!isEnd())) {
        backupInternalState(m_utilSpecialBackUp_);
        char trailCh = nextChar();
        if (UTF16.isTrailSurrogate(trailCh)) {
          cp = UCharacterProperty.getRawSupplementary(thCh, trailCh);
        }
        else
        {
          updateInternalState(m_utilSpecialBackUp_);
        }
      }
      



      if (m_bufferOffset_ < 0)
      {
        m_buffer_.replace(0, m_buffer_.length(), Normalizer.decompose(UTF16.toString(cp), false));
        


        if ((m_buffer_.length() >= 2) && (UTF16.isLeadSurrogate(m_buffer_.charAt(0))) && (UTF16.isTrailSurrogate(m_buffer_.charAt(1))))
        {

          m_buffer_.insert(2, ch);
        }
        else {
          m_buffer_.insert(1, ch);
        }
        m_FCDLimit_ = m_source_.getIndex();












      }
      else if (UCharacter.isSupplementary(cp)) {
        m_buffer_.insert(2, ch);
      }
      else {
        m_buffer_.insert(1, ch);
      }
      



























































      m_bufferOffset_ = 0;
      return 0;
    }
    return m_expansion_[getExpansionOffset(collator, ce)];
  }
  








  private int getContractionOffset(RuleBasedCollator collator, int ce)
  {
    return (ce & 0xFFFFFF) - m_contractionOffset_;
  }
  





  private boolean isSpecialPrefixTag(int ce)
  {
    return (RuleBasedCollator.isSpecial(ce)) && (RuleBasedCollator.getTag(ce) == 11);
  }
  















  private int nextSpecialPrefix(RuleBasedCollator collator, int ce, Backup entrybackup)
  {
    backupInternalState(m_utilSpecialBackUp_);
    updateInternalState(entrybackup);
    previousChar();
    




    for (;;)
    {
      int entryoffset = getContractionOffset(collator, ce);
      int offset = entryoffset;
      if (isBackwardsStart()) {
        ce = m_contractionCE_[offset];
      }
      else {
        char previous = previousChar();
        while (previous > m_contractionIndex_[offset])
        {
          offset++;
        }
        
        if (previous == m_contractionIndex_[offset])
        {

          ce = m_contractionCE_[offset];



        }
        else
        {


          int isZeroCE = m_trie_.getLeadValue(previous);
          
          if (isZeroCE == 0) {
            continue;
          }
          if (UTF16.isSurrogate(previous))
          {





            if (isBackwardsStart()) continue;
            char lead = previousChar();
            if (UTF16.isLeadSurrogate(lead)) {
              isZeroCE = m_trie_.getLeadValue(lead);
              if (RuleBasedCollator.getTag(isZeroCE) == 5)
              {
                int finalCE = m_trie_.getTrailValue(isZeroCE, previous);
                
                if (finalCE == 0) {
                  continue;
                }
                
              }
              
            }
            else
            {
              nextChar();
              continue;
            }
            nextChar();
          }
          






          ce = m_contractionCE_[entryoffset];
        }
        
        if (!isSpecialPrefixTag(ce)) {
          break;
        }
      }
    }
    



    if (ce != -268435456)
    {
      updateInternalState(m_utilSpecialBackUp_);
    }
    else
    {
      updateInternalState(entrybackup);
    }
    return ce;
  }
  





  private boolean isContractionTag(int ce)
  {
    return (RuleBasedCollator.isSpecial(ce)) && (RuleBasedCollator.getTag(ce) == 2);
  }
  














  private void setDiscontiguous(StringBuffer skipped)
  {
    if (m_bufferOffset_ >= 0) {
      m_buffer_.replace(0, m_bufferOffset_, skipped.toString());
    }
    else {
      m_FCDLimit_ = m_source_.getIndex();
      m_buffer_.setLength(0);
      m_buffer_.append(skipped.toString());
    }
    
    m_bufferOffset_ = 0;
  }
  




  private char currentChar()
  {
    if (m_bufferOffset_ < 0) {
      char result = m_source_.previous();
      m_source_.next();
      return result;
    }
    



    return m_buffer_.charAt(m_bufferOffset_ - 1);
  }
  









  private int nextDiscontiguous(RuleBasedCollator collator, int entryoffset)
  {
    int offset = entryoffset;
    boolean multicontraction = false;
    
    if (m_utilSkippedBuffer_ == null) {
      m_utilSkippedBuffer_ = new StringBuffer();
    }
    else {
      m_utilSkippedBuffer_.setLength(0);
    }
    char ch = currentChar();
    m_utilSkippedBuffer_.append(currentChar());
    
    if (m_utilSpecialDiscontiguousBackUp_ == null) {
      m_utilSpecialDiscontiguousBackUp_ = new Backup();
    }
    backupInternalState(m_utilSpecialDiscontiguousBackUp_);
    char nextch = ch;
    for (;;) {
      ch = nextch;
      nextch = nextChar();
      if ((nextch == 65535) || (getCombiningClass(nextch) == 0))
      {



        if (!multicontraction) break;
        if (nextch != 65535) {
          previousChar();
        }
        setDiscontiguous(m_utilSkippedBuffer_);
        return m_contractionCE_[offset];
      }
      


      offset++;
      while (nextch > m_contractionIndex_[offset]) {
        offset++;
      }
      
      int ce = -268435456;
      if ((nextch != m_contractionIndex_[offset]) || (getCombiningClass(nextch) == getCombiningClass(ch)))
      {

        m_utilSkippedBuffer_.append(nextch);
      }
      else
      {
        ce = m_contractionCE_[offset];
        

        if (ce == -268435456) {
          break;
        }
        if (isContractionTag(ce))
        {
          offset = getContractionOffset(collator, ce);
          if (m_contractionCE_[offset] != -268435456) {
            multicontraction = true;
            backupInternalState(m_utilSpecialDiscontiguousBackUp_);
          }
        }
        else {
          setDiscontiguous(m_utilSkippedBuffer_);
          return ce;
        }
      }
    }
    updateInternalState(m_utilSpecialDiscontiguousBackUp_);
    

    previousChar();
    return m_contractionCE_[entryoffset];
  }
  







  private int nextContraction(RuleBasedCollator collator, int ce)
  {
    backupInternalState(m_utilSpecialBackUp_);
    int entryce = -268435456;
    for (;;) {
      int entryoffset = getContractionOffset(collator, ce);
      int offset = entryoffset;
      
      if (isEnd()) {
        ce = m_contractionCE_[offset];
        if (ce != -268435456) {
          break;
        }
        ce = entryce;
        updateInternalState(m_utilSpecialBackUp_); break;
      }
      



      byte maxCC = (byte)(m_contractionIndex_[offset] & 0xFF);
      
      byte allSame = (byte)(m_contractionIndex_[offset] >> '\b');
      char ch = nextChar();
      offset++;
      while (ch > m_contractionIndex_[offset])
      {
        offset++;
      }
      
      if (ch == m_contractionIndex_[offset])
      {

        ce = m_contractionCE_[offset];

      }
      else
      {
        int isZeroCE = m_trie_.getLeadValue(ch);
        
        if (isZeroCE == 0) {
          continue;
        }
        if (UTF16.isLeadSurrogate(ch)) {
          if (isEnd()) continue;
          backupInternalState(m_utilSpecialBackUp_);
          char trail = nextChar();
          if (UTF16.isTrailSurrogate(trail))
          {
            if (RuleBasedCollator.getTag(isZeroCE) == 5)
            {
              int finalCE = m_trie_.getTrailValue(isZeroCE, trail);
              
              if (finalCE == 0) {
                continue;
              }
              
            }
          }
          else
          {
            updateInternalState(m_utilSpecialBackUp_);
            
            continue;
          }
          updateInternalState(m_utilSpecialBackUp_);
        }
        




        byte sCC;
        




        if ((maxCC == 0) || ((sCC = (byte)getCombiningClass(ch)) == 0) || (sCC > maxCC) || ((allSame != 0) && (sCC == maxCC)) || (isEnd()))
        {


          previousChar();
          ce = m_contractionCE_[entryoffset];

        }
        else
        {
          char nextch = nextChar();
          if (nextch != 65535) {
            previousChar();
          }
          if (getCombiningClass(nextch) == 0) {
            previousChar();
            
            ce = m_contractionCE_[entryoffset];
          }
          else {
            ce = nextDiscontiguous(collator, entryoffset);
          }
        }
      }
      
      if (ce == -268435456)
      {
        updateInternalState(m_utilSpecialBackUp_);
        ce = entryce;
        break;
      }
      

      if (!isContractionTag(ce)) {
        break;
      }
      

      if (m_contractionCE_[entryoffset] != -268435456)
      {


        entryce = m_contractionCE_[entryoffset];
        backupInternalState(m_utilSpecialBackUp_);
        if (m_utilSpecialBackUp_.m_bufferOffset_ >= 0) {
          m_utilSpecialBackUp_.m_bufferOffset_ -= 1;
        }
        else {
          m_utilSpecialBackUp_.m_offset_ -= 1;
        }
      }
    }
    return ce;
  }
  






  private int nextLongPrimary(int ce)
  {
    m_CEBuffer_[1] = ((ce & 0xFF) << 24 | 0xC0);
    
    m_CEBufferOffset_ = 1;
    m_CEBufferSize_ = 2;
    m_CEBuffer_[0] = ((ce & 0xFFFF00) << 8 | 0x500 | 0x5);
    
    return m_CEBuffer_[0];
  }
  





  private int getExpansionCount(int ce)
  {
    return ce & 0xF;
  }
  










  private int nextExpansion(RuleBasedCollator collator, int ce)
  {
    int offset = getExpansionOffset(collator, ce);
    m_CEBufferSize_ = getExpansionCount(ce);
    m_CEBufferOffset_ = 1;
    m_CEBuffer_[0] = m_expansion_[offset];
    if (m_CEBufferSize_ != 0)
    {
      for (int i = 1; i < m_CEBufferSize_; i++) {
        m_CEBuffer_[i] = m_expansion_[(offset + i)];
      }
    }
    else
    {
      m_CEBufferSize_ = 1;
      while (m_expansion_[offset] != 0) {
        m_CEBuffer_[(m_CEBufferSize_++)] = m_expansion_[(++offset)];
      }
    }
    


    if (m_CEBufferSize_ == 1) {
      m_CEBufferSize_ = 0;
      m_CEBufferOffset_ = 0;
    }
    return m_CEBuffer_[0];
  }
  





  private int nextImplicit(int codepoint)
  {
    if (!UCharacter.isLegal(codepoint))
    {

      return 0;
    }
    int result = getImplicitPrimary(codepoint);
    m_CEBuffer_[0] = (result & 0xFFFF0000 | 0x505);
    
    m_CEBuffer_[1] = ((result & 0xFFFF) << 16 | 0xC0);
    m_CEBufferOffset_ = 1;
    m_CEBufferSize_ = 2;
    return m_CEBuffer_[0];
  }
  





  private int nextSurrogate(char ch)
  {
    char nextch = nextChar();
    if ((nextch != 65535) && (UTF16.isTrailSurrogate(nextch)))
    {
      int codepoint = UCharacterProperty.getRawSupplementary(ch, nextch);
      return nextImplicit(codepoint);
    }
    if (nextch != 65535) {
      previousChar();
    }
    return 0;
  }
  







  private int nextHangul(RuleBasedCollator collator, char ch)
  {
    char L = (char)(ch - 44032);
    



    char T = (char)(L % '\034');
    L = (char)(L / '\034');
    char V = (char)(L % '\025');
    L = (char)(L / '\025');
    

    L = (char)(L + 'ᄀ');
    V = (char)(V + 'ᅡ');
    T = (char)(T + 'ᆧ');
    


    m_CEBufferSize_ = 0;
    if (!m_isJamoSpecial_) {
      m_CEBuffer_[(m_CEBufferSize_++)] = m_trie_.getLeadValue(L);
      
      m_CEBuffer_[(m_CEBufferSize_++)] = m_trie_.getLeadValue(V);
      

      if (T != 'ᆧ') {
        m_CEBuffer_[(m_CEBufferSize_++)] = m_trie_.getLeadValue(T);
      }
      
      m_CEBufferOffset_ = 1;
      return m_CEBuffer_[0];
    }
    





    m_buffer_.append(L);
    m_buffer_.append(V);
    if (T != 'ᆧ') {
      m_buffer_.append(T);
    }
    m_FCDLimit_ = m_source_.getIndex();
    m_FCDStart_ = (m_FCDLimit_ - 1);
    

    return 0;
  }
  








  private int nextSpecial(RuleBasedCollator collator, int ce, char ch)
  {
    int codepoint = ch;
    Backup entrybackup = m_utilSpecialEntryBackUp_;
    
    if (entrybackup != null) {
      m_utilSpecialEntryBackUp_ = null;
    }
    else {
      entrybackup = new Backup();
    }
    backupInternalState(entrybackup);
    try
    {
      for (;;)
      {
        switch (RuleBasedCollator.getTag(ce))
        {
        case 0: 
          return ce;
        case 5: 
          if (isEnd()) {
            return 0;
          }
          backupInternalState(m_utilSpecialBackUp_);
          char trail = nextChar();
          ce = nextSurrogate(collator, ce, trail);
          

          codepoint = UCharacterProperty.getRawSupplementary(ch, trail);
          
          break;
        case 3: 
          ce = nextThai(collator, ce, ch);
          break;
        case 11: 
          ce = nextSpecialPrefix(collator, ce, entrybackup);
          break;
        case 2: 
          ce = nextContraction(collator, ce);
          break;
        case 12: 
          return nextLongPrimary(ce);
        case 1: 
          return nextExpansion(collator, ce);
        

        case 9: 
          return nextImplicit(codepoint);
        case 10: 
          return nextImplicit(codepoint);
        case 8: 
          return 0;
        case 7: 
          return nextSurrogate(ch);
        case 6: 
          return nextHangul(collator, ch);
        
        case 4: 
          return -268435456;
        default: 
          ce = 0;
        }
        
        if (!RuleBasedCollator.isSpecial(ce)) {
          break;
        }
      }
    } finally {
      m_utilSpecialEntryBackUp_ = entrybackup;
    }
    return ce;
  }
  






  private int previousThai(RuleBasedCollator collator, int ce, char ch)
  {
    if ((m_bufferOffset_ >= 0) || (m_source_.getIndex() == 0))
    {

      return m_expansion_[getExpansionOffset(collator, ce)];
    }
    





    boolean innorm = m_bufferOffset_ >= 0;
    char prevch = previousChar();
    if (!isThaiPreVowel(prevch))
    {
      if (prevch != 65535) {
        nextChar();
      }
      
      return m_expansion_[getExpansionOffset(collator, ce)];
    }
    




    boolean reorder = true;
    m_FCDStart_ = m_source_.getIndex();
    if (innorm)
    {

      if (m_collator_.isContractionEnd(ch)) {
        reorder = false;
      }
      m_bufferOffset_ = 2;

    }
    else
    {
      String decomp = Normalizer.decompose(UTF16.toString(ch), false);
      

      for (int i = decomp.length() - 1; i >= 0; i--) {
        if (m_collator_.isContractionEnd(decomp.charAt(i))) {
          reorder = false;
          break;
        }
      }
      
      m_buffer_.replace(0, m_buffer_.length(), decomp);
      m_bufferOffset_ = (m_buffer_.length() + 1);
      m_FCDLimit_ = (m_FCDStart_ + 2);
    }
    if (reorder) {
      m_buffer_.insert(1, prevch);
    }
    else {
      m_buffer_.insert(0, prevch);
    }
    return 0;
  }
  











  private int previousSpecialPrefix(RuleBasedCollator collator, int ce)
  {
    backupInternalState(m_utilSpecialBackUp_);
    for (;;)
    {
      int offset = getContractionOffset(collator, ce);
      int entryoffset = offset;
      if (isBackwardsStart()) {
        ce = m_contractionCE_[offset];
      }
      else {
        char prevch = previousChar();
        while (prevch > m_contractionIndex_[offset])
        {

          offset++;
        }
        if (prevch == m_contractionIndex_[offset]) {
          ce = m_contractionCE_[offset];



        }
        else
        {


          int isZeroCE = m_trie_.getLeadValue(prevch);
          
          if (isZeroCE == 0) {
            continue;
          }
          if ((UTF16.isTrailSurrogate(prevch)) || (UTF16.isLeadSurrogate(prevch)))
          {






            if (isBackwardsStart()) continue;
            char lead = previousChar();
            if (UTF16.isLeadSurrogate(lead)) {
              isZeroCE = m_trie_.getLeadValue(lead);
              if (RuleBasedCollator.getTag(isZeroCE) == 5)
              {
                int finalCE = m_trie_.getTrailValue(isZeroCE, prevch);
                

                if (finalCE == 0) {
                  continue;
                }
                
              }
            }
            else
            {
              nextChar();
              
              continue;
            }
            nextChar();
          }
          






          ce = m_contractionCE_[entryoffset];
        }
        
        if (!isSpecialPrefixTag(ce)) {
          break;
        }
      }
    }
    

    updateInternalState(m_utilSpecialBackUp_);
    return ce;
  }
  










  private int previousContraction(RuleBasedCollator collator, int ce, char ch)
  {
    m_utilStringBuffer_.setLength(0);
    

    char prevch = previousChar();
    boolean atStart = false;
    while ((collator.isUnsafe(ch)) || (isThaiPreVowel(prevch))) {
      m_utilStringBuffer_.insert(0, ch);
      ch = prevch;
      if (isBackwardsStart()) {
        atStart = true;
        break;
      }
      prevch = previousChar();
    }
    if (!atStart)
    {
      nextChar();
    }
    
    m_utilStringBuffer_.insert(0, ch);
    




    int originaldecomp = collator.getDecomposition();
    
    collator.setDecomposition(16);
    if (m_utilColEIter_ == null) {
      m_utilColEIter_ = new CollationElementIterator(m_utilStringBuffer_.toString(), collator);

    }
    else
    {
      m_utilColEIter_.m_collator_ = collator;
      m_utilColEIter_.setText(m_utilStringBuffer_.toString());
    }
    ce = m_utilColEIter_.next();
    m_CEBufferSize_ = 0;
    while (ce != -1) {
      if (m_CEBufferSize_ == m_CEBuffer_.length) {
        try
        {
          int[] tempbuffer = new int[m_CEBuffer_.length + 50];
          System.arraycopy(m_CEBuffer_, 0, tempbuffer, 0, m_CEBuffer_.length);
          
          m_CEBuffer_ = tempbuffer;
        }
        catch (Exception e) {
          e.printStackTrace();
          return -1;
        }
      }
      m_CEBuffer_[(m_CEBufferSize_++)] = ce;
      ce = m_utilColEIter_.next();
    }
    collator.setDecomposition(originaldecomp);
    m_CEBufferOffset_ = (m_CEBufferSize_ - 1);
    return m_CEBuffer_[m_CEBufferOffset_];
  }
  





  private int previousLongPrimary(int ce)
  {
    m_CEBufferSize_ = 0;
    m_CEBuffer_[(m_CEBufferSize_++)] = ((ce & 0xFFFF00) << 8 | 0x500 | 0x5);
    
    m_CEBuffer_[(m_CEBufferSize_++)] = ((ce & 0xFF) << 24 | 0xC0);
    
    m_CEBufferOffset_ = (m_CEBufferSize_ - 1);
    return m_CEBuffer_[m_CEBufferOffset_];
  }
  







  private int previousExpansion(RuleBasedCollator collator, int ce)
  {
    int offset = getExpansionOffset(collator, ce);
    m_CEBufferSize_ = getExpansionCount(ce);
    if (m_CEBufferSize_ != 0)
    {
      for (i = 0; i < m_CEBufferSize_; i++) {
        m_CEBuffer_[i] = m_expansion_[(offset + i)];
      }
      
    }
    else
    {
      while (m_expansion_[(offset + m_CEBufferSize_)] != 0) { int i;
        m_CEBuffer_[m_CEBufferSize_] = m_expansion_[(offset + m_CEBufferSize_)];
        
        m_CEBufferSize_ += 1;
      }
    }
    m_CEBufferOffset_ = (m_CEBufferSize_ - 1);
    return m_CEBuffer_[m_CEBufferOffset_];
  }
  






  private int previousHangul(RuleBasedCollator collator, char ch)
  {
    char L = (char)(ch - 44032);
    

    char T = (char)(L % '\034');
    L = (char)(L / '\034');
    char V = (char)(L % '\025');
    L = (char)(L / '\025');
    

    L = (char)(L + 'ᄀ');
    V = (char)(V + 'ᅡ');
    T = (char)(T + 'ᆧ');
    
    m_CEBufferSize_ = 0;
    if (!m_isJamoSpecial_) {
      m_CEBuffer_[(m_CEBufferSize_++)] = m_trie_.getLeadValue(L);
      
      m_CEBuffer_[(m_CEBufferSize_++)] = m_trie_.getLeadValue(V);
      
      if (T != 'ᆧ') {
        m_CEBuffer_[(m_CEBufferSize_++)] = m_trie_.getLeadValue(T);
      }
      
      m_CEBufferOffset_ = (m_CEBufferSize_ - 1);
      return m_CEBuffer_[m_CEBufferOffset_];
    }
    



    m_buffer_.append(L);
    m_buffer_.append(V);
    if (T != 'ᆧ') {
      m_buffer_.append(T);
    }
    
    m_FCDStart_ = m_source_.getIndex();
    m_FCDLimit_ = (m_FCDStart_ + 1);
    return 0;
  }
  






  private int previousImplicit(int codepoint)
  {
    if (!UCharacter.isLegal(codepoint)) {
      return 0;
    }
    int result = getImplicitPrimary(codepoint);
    m_CEBufferSize_ = 2;
    m_CEBufferOffset_ = 1;
    m_CEBuffer_[0] = (result & 0xFFFF0000 | 0x505);
    
    m_CEBuffer_[1] = ((result & 0xFFFF) << 16 | 0xC0);
    return m_CEBuffer_[1];
  }
  





  private int previousSurrogate(char ch)
  {
    if (isBackwardsStart())
    {
      return 0;
    }
    char prevch = previousChar();
    
    if (UTF16.isLeadSurrogate(prevch)) {
      return previousImplicit(UCharacterProperty.getRawSupplementary(prevch, ch));
    }
    
    if (prevch != 65535) {
      nextChar();
    }
    return 0;
  }
  








  private int previousSpecial(RuleBasedCollator collator, int ce, char ch)
  {
    for (;;)
    {
      switch (RuleBasedCollator.getTag(ce)) {
      case 0: 
        return ce;
      

      case 5: 
        return 0;
      case 3: 
        ce = previousThai(collator, ce, ch);
        break;
      case 11: 
        ce = previousSpecialPrefix(collator, ce);
        break;
      
      case 2: 
        if (isBackwardsStart())
        {
          ce = m_contractionCE_[getContractionOffset(collator, ce)];
        }
        else
        {
          return previousContraction(collator, ce, ch); }
        break;
      case 12:  return previousLongPrimary(ce);
      case 1: 
        return previousExpansion(collator, ce);
      case 6: 
        return previousHangul(collator, ch);
      case 7: 
        return 0;
      case 8: 
        return previousSurrogate(ch);
      
      case 9: 
        return previousImplicit(ch);
      
      case 10: 
        return previousImplicit(ch);
      case 4: 
        return -268435456;
      default: 
        ce = 0;
      }
      if (!RuleBasedCollator.isSpecial(ce)) {
        break;
      }
    }
    return ce;
  }
  





  private static final int getImplicitPrimary(int cp)
  {
    cp = swapCJK(cp);
    












    int last0 = cp - RuleBasedCollator.IMPLICIT_4BYTE_BOUNDARY_;
    if (last0 < 0) {
      int last1 = cp / 126;
      last0 = cp % 126;
      
      int last2 = last1 / 253;
      last1 %= 253;
      return RuleBasedCollator.IMPLICIT_BASE_3BYTE_ + (last2 << 24) + (last1 << 16) + (last0 * RuleBasedCollator.LAST_MULTIPLIER_ << 8);
    }
    


    int last1 = last0 / 12;
    last0 %= 12;
    
    int last2 = last1 / 253;
    last1 %= 253;
    
    int last3 = last2 / 253;
    last2 %= 253;
    return RuleBasedCollator.IMPLICIT_BASE_4BYTE_ + (last3 << 24) + (last2 << 16) + (last1 << 8) + last0 * RuleBasedCollator.LAST2_MULTIPLIER_;
  }
  








  private static final int swapCJK(int cp)
  {
    if (cp >= 19968) {
      if (cp < 40960) {
        return cp - 19968;
      }
      if (cp < 64014) {
        return cp + 1114112;
      }
      if (cp < 64048) {
        return cp - 64014 + 20992;
      }
      if (cp < 131072) {
        return cp + 1114112;
      }
      if (cp < 173792) {
        return cp;
      }
      return cp + 1114112;
    }
    if (cp < 13312) {
      return cp + 1114112;
    }
    if (cp < 19904) {
      return cp - 13312 + 20992 + 34;
    }
    
    return cp + 1114112;
  }
  









  private char peekCharacter(int offset)
  {
    if (offset != 0) {
      int currentoffset = m_source_.getIndex();
      m_source_.setIndex(currentoffset + offset);
      char result = m_source_.current();
      m_source_.setIndex(currentoffset);
      return result;
    }
    
    return m_source_.current();
  }
}
