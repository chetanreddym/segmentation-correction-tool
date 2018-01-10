package com.ibm.icu.text;

import com.ibm.icu.impl.BOCU;
import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.IntTrie;
import com.ibm.icu.impl.Trie.DataManipulate;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.util.VersionInfo;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;






























































public final class RuleBasedCollator
  extends Collator
{
  static final byte BYTE_FIRST_TAILORED_ = 4;
  static final byte BYTE_COMMON_ = 5;
  static final int COMMON_TOP_2_ = 134;
  static final int COMMON_BOTTOM_2_ = 5;
  static final int CE_CASE_BIT_MASK_ = 192;
  static final int CE_TAG_SHIFT_ = 24;
  static final int CE_TAG_MASK_ = 251658240;
  static final int CE_SPECIAL_FLAG_ = -268435456;
  static final int CE_SURROGATE_TAG_ = 5;
  static final int CE_PRIMARY_MASK_ = -65536;
  static final int CE_SECONDARY_MASK_ = 65280;
  static final int CE_TERTIARY_MASK_ = 255;
  static final int CE_PRIMARY_SHIFT_ = 16;
  static final int CE_SECONDARY_SHIFT_ = 8;
  static final int CE_CONTINUATION_MARKER_ = 192;
  int m_expansionOffset_;
  int m_contractionOffset_;
  boolean m_isJamoSpecial_;
  int m_defaultVariableTopValue_;
  boolean m_defaultIsFrenchCollation_;
  boolean m_defaultIsAlternateHandlingShifted_;
  int m_defaultCaseFirst_;
  boolean m_defaultIsCaseLevel_;
  int m_defaultDecomposition_;
  int m_defaultStrength_;
  boolean m_defaultIsHiragana4_;
  int m_variableTopValue_;
  boolean m_isHiragana4_;
  int m_caseFirst_;
  int[] m_expansion_;
  char[] m_contractionIndex_;
  int[] m_contractionCE_;
  IntTrie m_trie_;
  int[] m_expansionEndCE_;
  byte[] m_expansionEndCEMaxSize_;
  byte[] m_unsafe_;
  byte[] m_contractionEnd_;
  String m_rules_;
  char m_minUnsafe_;
  char m_minContractionEnd_;
  VersionInfo m_version_;
  VersionInfo m_UCA_version_;
  VersionInfo m_UCD_version_;
  static final RuleBasedCollator UCA_;
  static final UCAConstants UCA_CONSTANTS_;
  static final char[] UCA_CONTRACTIONS_;
  static final int IMPLICIT_BASE_BYTE_;
  static final int IMPLICIT_LIMIT_BYTE_;
  static final int IMPLICIT_4BYTE_BOUNDARY_;
  static final int LAST_MULTIPLIER_;
  static final int LAST2_MULTIPLIER_;
  static final int IMPLICIT_BASE_3BYTE_;
  static final int IMPLICIT_BASE_4BYTE_;
  static final int BYTES_TO_AVOID_ = 3;
  static final int OTHER_COUNT_ = 253;
  static final int LAST_COUNT_ = 126;
  static final int LAST_COUNT2_ = 12;
  static final int IMPLICIT_3BYTE_COUNT_ = 1;
  static final byte SORT_LEVEL_TERMINATOR_ = 1;
  private static final int DEFAULT_MIN_HEURISTIC_ = 768;
  private static final char HEURISTIC_SIZE_ = 'Р';
  private static final char HEURISTIC_OVERFLOW_MASK_ = '῿';
  private static final int HEURISTIC_SHIFT_ = 3;
  private static final char HEURISTIC_OVERFLOW_OFFSET_ = 'Ā';
  private static final char HEURISTIC_MASK_ = '\007';
  private int m_caseSwitch_;
  private int m_common3_;
  private int m_mask3_;
  private int m_addition3_;
  private int m_top3_;
  private int m_bottom3_;
  private int m_topCount3_;
  private int m_bottomCount3_;
  private static final int CASE_SWITCH_ = 192;
  private static final int NO_CASE_SWITCH_ = 0;
  private static final int CE_REMOVE_CASE_ = 63;
  private static final int CE_KEEP_CASE_ = 255;
  private static final int CE_CASE_MASK_3_ = 255;
  private static final double PROPORTION_2_ = 0.5D;
  private static final double PROPORTION_3_ = 0.667D;
  private static final byte BYTE_ZERO_ = 0;
  private static final byte BYTE_LEVEL_SEPARATOR_ = 1;
  private static final byte BYTE_SORTKEY_GLUE_ = 2;
  private static final byte BYTE_SHIFT_PREFIX_ = 3;
  private static final byte BYTE_UNSHIFTED_MIN_ = 3;
  private static final byte BYTE_FIRST_UCA_ = 5;
  private static final byte BYTE_LAST_LATIN_PRIMARY_ = 76;
  private static final byte BYTE_FIRST_NON_LATIN_PRIMARY_ = 77;
  private static final byte BYTE_UNSHIFTED_MAX_ = -1;
  private static final int TOTAL_2_ = 128;
  private static final int FLAG_BIT_MASK_CASE_SWITCH_OFF_ = 128;
  private static final int FLAG_BIT_MASK_CASE_SWITCH_ON_ = 64;
  private static final int COMMON_TOP_CASE_SWITCH_OFF_3_ = 133;
  private static final int COMMON_TOP_CASE_SWITCH_LOWER_3_ = 69;
  private static final int COMMON_TOP_CASE_SWITCH_UPPER_3_ = 197;
  private static final int COMMON_BOTTOM_3_ = 5;
  private static final int COMMON_BOTTOM_CASE_SWITCH_UPPER_3_ = 134;
  private static final int COMMON_BOTTOM_CASE_SWITCH_LOWER_3_ = 5;
  private static final int TOP_COUNT_2_ = 64;
  private static final int BOTTOM_COUNT_2_ = 64;
  private static final int COMMON_2_ = 5;
  private static final int COMMON_UPPER_FIRST_3_ = 197;
  private static final int COMMON_NORMAL_3_ = 5;
  private static final int COMMON_4_ = -1;
  private static final int MIN_BINARY_DATA_SIZE_ = 264;
  private boolean m_isSimple3_;
  private boolean m_isFrenchCollation_;
  private boolean m_isAlternateHandlingShifted_;
  private boolean m_isCaseLevel_;
  private static final int SORT_BUFFER_INIT_SIZE_ = 128;
  private static final int SORT_BUFFER_INIT_SIZE_1_ = 1024;
  private static final int SORT_BUFFER_INIT_SIZE_2_ = 128;
  private static final int SORT_BUFFER_INIT_SIZE_3_ = 128;
  private static final int SORT_BUFFER_INIT_SIZE_CASE_ = 32;
  private static final int SORT_BUFFER_INIT_SIZE_4_ = 128;
  private static final int CE_CONTINUATION_TAG_ = 192;
  private static final int CE_REMOVE_CONTINUATION_MASK_ = -193;
  private static final int LAST_BYTE_MASK_ = 255;
  private static final int CE_RESET_TOP_VALUE_ = -1627389181;
  private static final int CE_NEXT_TOP_VALUE_ = -392822013;
  private static final byte SORT_CASE_BYTE_START_ = -128;
  private static final byte SORT_CASE_SHIFT_START_ = 7;
  private static final int CE_BUFFER_SIZE_ = 512;
  
  public RuleBasedCollator(String rules)
    throws Exception
  {
    if ((rules == null) || (rules.length() == 0)) {
      throw new IllegalArgumentException("Collation rules can not be null");
    }
    
    init(rules);
  }
  






  public Object clone()
    throws CloneNotSupportedException
  {
    RuleBasedCollator result = (RuleBasedCollator)super.clone();
    

    result.initUtility();
    
    return result;
  }
  





  public CollationElementIterator getCollationElementIterator(String source)
  {
    return new CollationElementIterator(source, this);
  }
  








  public CollationElementIterator getCollationElementIterator(CharacterIterator source)
  {
    CharacterIterator newsource = (CharacterIterator)source.clone();
    return new CollationElementIterator(newsource, this);
  }
  














  public void setHiraganaQuaternary(boolean flag)
  {
    m_isHiragana4_ = flag;
  }
  








  public void setHiraganaQuaternaryDefault()
  {
    m_isHiragana4_ = m_defaultIsHiragana4_;
  }
  















  public void setUpperCaseFirst(boolean upperfirst)
  {
    if (upperfirst) {
      if (m_caseFirst_ != 25) {
        latinOneRegenTable_ = true;
      }
      m_caseFirst_ = 25;
    }
    else {
      if (m_caseFirst_ != 16) {
        latinOneRegenTable_ = true;
      }
      m_caseFirst_ = 16;
    }
    updateInternalState();
  }
  

















  public void setLowerCaseFirst(boolean lowerfirst)
  {
    if (lowerfirst) {
      if (m_caseFirst_ != 24) {
        latinOneRegenTable_ = true;
      }
      m_caseFirst_ = 24;
    }
    else {
      if (m_caseFirst_ != 16) {
        latinOneRegenTable_ = true;
      }
      m_caseFirst_ = 16;
    }
    updateInternalState();
  }
  











  public final void setCaseFirstDefault()
  {
    if (m_caseFirst_ != m_defaultCaseFirst_) {
      latinOneRegenTable_ = true;
    }
    m_caseFirst_ = m_defaultCaseFirst_;
    updateInternalState();
  }
  








  public void setAlternateHandlingDefault()
  {
    m_isAlternateHandlingShifted_ = m_defaultIsAlternateHandlingShifted_;
    updateInternalState();
  }
  








  public void setCaseLevelDefault()
  {
    m_isCaseLevel_ = m_defaultIsCaseLevel_;
    updateInternalState();
  }
  








  public void setDecompositionDefault()
  {
    setDecomposition(m_defaultDecomposition_);
  }
  








  public void setFrenchCollationDefault()
  {
    if (m_isFrenchCollation_ != m_defaultIsFrenchCollation_) {
      latinOneRegenTable_ = true;
    }
    m_isFrenchCollation_ = m_defaultIsFrenchCollation_;
    updateInternalState();
  }
  








  public void setStrengthDefault()
  {
    setStrength(m_defaultStrength_);
  }
  














  public void setFrenchCollation(boolean flag)
  {
    if (m_isFrenchCollation_ != flag) {
      latinOneRegenTable_ = true;
    }
    m_isFrenchCollation_ = flag;
    updateInternalState();
  }
  





















  public void setAlternateHandlingShifted(boolean shifted)
  {
    m_isAlternateHandlingShifted_ = shifted;
    updateInternalState();
  }
  






















  public void setCaseLevel(boolean flag)
  {
    m_isCaseLevel_ = flag;
    updateInternalState();
  }
  



















  public void setStrength(int newStrength)
  {
    super.setStrength(newStrength);
    updateInternalState();
  }
  



























  public int setVariableTop(String varTop)
  {
    if ((varTop == null) || (varTop.length() == 0)) {
      throw new IllegalArgumentException("Variable top argument string can not be null or zero in length.");
    }
    

    m_srcUtilColEIter_.setText(varTop);
    int ce = m_srcUtilColEIter_.next();
    



    if ((m_srcUtilColEIter_.getOffset() != varTop.length()) || (ce == -1))
    {
      throw new IllegalArgumentException("Variable top argument string is a contraction that does not exist in the Collation order");
    }
    


    int nextCE = m_srcUtilColEIter_.next();
    
    if ((nextCE != -1) && ((!isContinuation(nextCE)) || ((nextCE & 0xFFFF0000) != 0)))
    {
      throw new IllegalArgumentException("Variable top argument string can only have a single collation element that has less than or equal to two PRIMARY strength bytes");
    }
    



    m_variableTopValue_ = ((ce & 0xFFFF0000) >> 16);
    
    return ce & 0xFFFF0000;
  }
  










  public void setVariableTop(int varTop)
  {
    m_variableTopValue_ = ((varTop & 0xFFFF0000) >> 16);
  }
  









  public String getRules()
  {
    return m_rules_;
  }
  










  public String getRules(boolean fullrules)
  {
    if (!fullrules) {
      return m_rules_;
    }
    
    return UCA_m_rules_.concat(m_rules_);
  }
  









  public UnicodeSet getTailoredSet()
  {
    try
    {
      CollationRuleParser src = new CollationRuleParser(getRules());
      return src.getTailoredSet();
    } catch (Exception e) {
      throw new InternalError("A tailoring rule should not have errors. Something is quite wrong!");
    }
  }
  
























  public CollationKey getCollationKey(String source)
  {
    if (source == null) {
      return null;
    }
    int strength = getStrength();
    m_utilCompare0_ = m_isCaseLevel_;
    m_utilCompare1_ = true;
    m_utilCompare2_ = (strength >= 1);
    m_utilCompare3_ = (strength >= 2);
    m_utilCompare4_ = (strength >= 3);
    m_utilCompare5_ = (strength == 15);
    
    m_utilBytesCount0_ = 0;
    m_utilBytesCount1_ = 0;
    m_utilBytesCount2_ = 0;
    m_utilBytesCount3_ = 0;
    m_utilBytesCount4_ = 0;
    m_utilBytesCount5_ = 0;
    m_utilCount0_ = 0;
    m_utilCount1_ = 0;
    m_utilCount2_ = 0;
    m_utilCount3_ = 0;
    m_utilCount4_ = 0;
    m_utilCount5_ = 0;
    boolean doFrench = (m_isFrenchCollation_) && (m_utilCompare2_);
    


    int commonBottom4 = (m_variableTopValue_ >>> 8) + 1 & 0xFF;
    byte hiragana4 = 0;
    if ((m_isHiragana4_) && (m_utilCompare4_))
    {
      hiragana4 = (byte)commonBottom4;
      commonBottom4++;
    }
    
    int bottomCount4 = 255 - commonBottom4;
    
    if ((m_utilCompare5_) && (Normalizer.quickCheck(source, Normalizer.NFD, 0) != Normalizer.YES))
    {



      source = Normalizer.decompose(source, false);
    }
    else if ((getDecomposition() != 16) && (Normalizer.quickCheck(source, Normalizer.FCD, 0) != Normalizer.YES))
    {



      source = Normalizer.normalize(source, Normalizer.FCD);
    }
    getSortKeyBytes(source, doFrench, hiragana4, commonBottom4, bottomCount4);
    
    byte[] sortkey = getSortKey(source, doFrench, commonBottom4, bottomCount4);
    
    return new CollationKey(source, sortkey);
  }
  











  public boolean isUpperCaseFirst()
  {
    return m_caseFirst_ == 25;
  }
  











  public boolean isLowerCaseFirst()
  {
    return m_caseFirst_ == 24;
  }
  












  public boolean isAlternateHandlingShifted()
  {
    return m_isAlternateHandlingShifted_;
  }
  









  public boolean isCaseLevel()
  {
    return m_isCaseLevel_;
  }
  








  public boolean isFrenchCollation()
  {
    return m_isFrenchCollation_;
  }
  








  public boolean isHiraganaQuaternary()
  {
    return m_isHiragana4_;
  }
  







  public int getVariableTop()
  {
    return m_variableTopValue_ << 16;
  }
  











  public boolean equals(Object obj)
  {
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    RuleBasedCollator other = (RuleBasedCollator)obj;
    
    if ((getStrength() != other.getStrength()) || (getDecomposition() != other.getDecomposition()) || (m_caseFirst_ != m_caseFirst_) || (m_caseSwitch_ != m_caseSwitch_) || (m_isAlternateHandlingShifted_ != m_isAlternateHandlingShifted_) || (m_isCaseLevel_ != m_isCaseLevel_) || (m_isFrenchCollation_ != m_isFrenchCollation_) || (m_isHiragana4_ != m_isHiragana4_))
    {







      return false;
    }
    boolean rules = m_rules_ == m_rules_;
    if ((!rules) && (m_rules_ != null) && (m_rules_ != null)) {
      rules = m_rules_.equals(m_rules_);
    }
    if ((!rules) || (!ICUDebug.enabled("collation"))) {
      return rules;
    }
    if ((m_addition3_ != m_addition3_) || (m_bottom3_ != m_bottom3_) || (m_bottomCount3_ != m_bottomCount3_) || (m_common3_ != m_common3_) || (m_isSimple3_ != m_isSimple3_) || (m_mask3_ != m_mask3_) || (m_minContractionEnd_ != m_minContractionEnd_) || (m_minUnsafe_ != m_minUnsafe_) || (m_top3_ != m_top3_) || (m_topCount3_ != m_topCount3_) || (!Arrays.equals(m_unsafe_, m_unsafe_)))
    {









      return false;
    }
    if (!m_trie_.equals(m_trie_))
    {

      for (int i = 1114111; i >= 0; i--)
      {
        int v = m_trie_.getCodePointValue(i);
        int otherv = m_trie_.getCodePointValue(i);
        if (v != otherv) {
          int mask = v & 0xFF000000;
          if (mask == (otherv & 0xFF000000)) {
            v &= 0xFFFFFF;
            otherv &= 0xFFFFFF;
            if (mask == -251658240) {
              v -= (m_expansionOffset_ << 4);
              otherv -= (m_expansionOffset_ << 4);
            }
            else if (mask == -234881024) {
              v -= m_contractionOffset_;
              otherv -= m_contractionOffset_;
            }
            if (v == otherv) {}
          }
          else
          {
            return false;
          }
        }
      } }
    if ((Arrays.equals(m_contractionCE_, m_contractionCE_)) && (Arrays.equals(m_contractionEnd_, m_contractionEnd_)) && (Arrays.equals(m_contractionIndex_, m_contractionIndex_)) && (Arrays.equals(m_expansion_, m_expansion_)) && (Arrays.equals(m_expansionEndCE_, m_expansionEndCE_)))
    {




      for (int i = 0; i < m_expansionEndCE_.length;) {
        if (m_expansionEndCEMaxSize_[i] != m_expansionEndCEMaxSize_[i])
        {
          return false;
        }
        return true;
      }
    }
    return false;
  }
  





  public int hashCode()
  {
    String rules = getRules();
    if (rules == null) {
      rules = "";
    }
    return rules.hashCode();
  }
  


























  public int compare(String source, String target)
  {
    if (source == target) {
      return 0;
    }
    

    int offset = getFirstUnmatchedOffset(source, target);
    
    if (latinOneUse_) {
      if (((offset < source.length()) && (source.charAt(offset) > 'ÿ')) || ((offset < target.length()) && (target.charAt(offset) > 'ÿ')))
      {



        return compareRegular(source, target, offset);
      }
      return compareUseLatin1(source, target, offset);
    }
    
    return compareRegular(source, target, offset);
  }
  






  static abstract interface AttributeValue
  {
    public static final int DEFAULT_ = -1;
    




    public static final int PRIMARY_ = 0;
    




    public static final int SECONDARY_ = 1;
    




    public static final int TERTIARY_ = 2;
    




    public static final int DEFAULT_STRENGTH_ = 2;
    




    public static final int CE_STRENGTH_LIMIT_ = 3;
    




    public static final int QUATERNARY_ = 3;
    




    public static final int IDENTICAL_ = 15;
    




    public static final int STRENGTH_LIMIT_ = 16;
    




    public static final int OFF_ = 16;
    




    public static final int ON_ = 17;
    




    public static final int SHIFTED_ = 20;
    




    public static final int NON_IGNORABLE_ = 21;
    




    public static final int LOWER_FIRST_ = 24;
    




    public static final int UPPER_FIRST_ = 25;
    




    public static final int LIMIT_ = 29;
  }
  





  static abstract interface Attribute
  {
    public static final int FRENCH_COLLATION_ = 0;
    




    public static final int ALTERNATE_HANDLING_ = 1;
    




    public static final int CASE_FIRST_ = 2;
    




    public static final int CASE_LEVEL_ = 3;
    




    public static final int NORMALIZATION_MODE_ = 4;
    




    public static final int STRENGTH_ = 5;
    




    public static final int HIRAGANA_QUATERNARY_MODE_ = 6;
    




    public static final int LIMIT_ = 7;
  }
  





  static class DataManipulate
    implements Trie.DataManipulate
  {
    private static DataManipulate m_instance_;
    





    public final int getFoldingOffset(int ce)
    {
      if ((RuleBasedCollator.isSpecial(ce)) && (RuleBasedCollator.getTag(ce) == 5)) {
        return ce & 0xFFFFFF;
      }
      return 0;
    }
    



    public static final DataManipulate getInstance()
    {
      if (m_instance_ == null) {
        m_instance_ = new DataManipulate();
      }
      return m_instance_;
    }
    









    private DataManipulate() {}
  }
  








  static final class UCAConstants
  {
    int[] FIRST_TERTIARY_IGNORABLE_ = new int[2];
    int[] LAST_TERTIARY_IGNORABLE_ = new int[2];
    int[] FIRST_PRIMARY_IGNORABLE_ = new int[2];
    int[] FIRST_SECONDARY_IGNORABLE_ = new int[2];
    int[] LAST_SECONDARY_IGNORABLE_ = new int[2];
    int[] LAST_PRIMARY_IGNORABLE_ = new int[2];
    int[] FIRST_VARIABLE_ = new int[2];
    int[] LAST_VARIABLE_ = new int[2];
    int[] FIRST_NON_VARIABLE_ = new int[2];
    int[] LAST_NON_VARIABLE_ = new int[2];
    int[] RESET_TOP_VALUE_ = new int[2];
    int[] FIRST_IMPLICIT_ = new int[2];
    int[] LAST_IMPLICIT_ = new int[2];
    int[] FIRST_TRAILING_ = new int[2];
    int[] LAST_TRAILING_ = new int[2];
    




















    int PRIMARY_TOP_MIN_;
    




















    int PRIMARY_IMPLICIT_MIN_;
    




















    int PRIMARY_IMPLICIT_MAX_;
    




















    int PRIMARY_TRAILING_MIN_;
    




















    int PRIMARY_TRAILING_MAX_;
    




















    int PRIMARY_SPECIAL_MIN_;
    




















    int PRIMARY_SPECIAL_MAX_;
    




















    UCAConstants() {}
  }
  




















  static
  {
    try
    {
      UCA_ = new RuleBasedCollator();
      UCA_CONSTANTS_ = new UCAConstants();
      InputStream i = UCA_.getClass().getResourceAsStream("/com/ibm/icu/impl/data/ucadata.icu");
      

      BufferedInputStream b = new BufferedInputStream(i, 90000);
      CollatorReader reader = new CollatorReader(b);
      UCA_CONTRACTIONS_ = reader.read(UCA_, UCA_CONSTANTS_);
      b.close();
      i.close();
      
      IMPLICIT_BASE_BYTE_ = UCA_CONSTANTS_PRIMARY_IMPLICIT_MIN_;
      
      IMPLICIT_LIMIT_BYTE_ = IMPLICIT_BASE_BYTE_ + 4;
      IMPLICIT_4BYTE_BOUNDARY_ = 31878;
      
      LAST_MULTIPLIER_ = 2;
      LAST2_MULTIPLIER_ = 21;
      IMPLICIT_BASE_3BYTE_ = (IMPLICIT_BASE_BYTE_ << 24) + 197376;
      IMPLICIT_BASE_4BYTE_ = (IMPLICIT_BASE_BYTE_ + 1 << 24) + 197379;
      
      UCA_.init();
      ResourceBundle rb = ICULocaleData.getLocaleElements(Locale.ENGLISH);
      UCA_m_rules_ = ((String)rb.getObject("%%UCARULES"));
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
  }
  











  RuleBasedCollator()
  {
    initUtility();
  }
  






  RuleBasedCollator(Locale locale)
  {
    ResourceBundle rb = ICULocaleData.getLocaleElements(locale);
    initUtility();
    if (rb != null) {
      try {
        Object elements = rb.getObject("CollationElements");
        if (elements != null) {
          Object[][] rules = (Object[][])elements;
          m_rules_ = ((String)rules[1][1]);
          
          if ((rules[0][1] instanceof byte[]))
          {
            byte[] map = (byte[])rules[0][1];
            BufferedInputStream input = new BufferedInputStream(new ByteArrayInputStream(map));
            

            CollatorReader reader = new CollatorReader(input, false);
            if (map.length > 264) {
              reader.read(this, null);
            }
            else {
              reader.readHeader(this);
              reader.readOptions(this);
              
              setWithUCATables();
            }
            


            if ((!m_UCA_version_.equals(UCA_m_UCA_version_)) || (!m_UCD_version_.equals(UCA_m_UCD_version_)))
            {
              init((String)rules[1][1]);
              return;
            }
            init();
            return;
          }
          


          init((String)rules[1][1]);
          return;
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    
    setWithUCAData();
  }
  






  final void setWithUCATables()
  {
    m_contractionOffset_ = UCA_m_contractionOffset_;
    m_expansionOffset_ = UCA_m_expansionOffset_;
    m_expansion_ = UCA_m_expansion_;
    m_contractionIndex_ = UCA_m_contractionIndex_;
    m_contractionCE_ = UCA_m_contractionCE_;
    m_trie_ = UCA_m_trie_;
    m_expansionEndCE_ = UCA_m_expansionEndCE_;
    m_expansionEndCEMaxSize_ = UCA_m_expansionEndCEMaxSize_;
    m_unsafe_ = UCA_m_unsafe_;
    m_contractionEnd_ = UCA_m_contractionEnd_;
    m_minUnsafe_ = UCA_m_minUnsafe_;
    m_minContractionEnd_ = UCA_m_minContractionEnd_;
  }
  



  final void setWithUCAData()
  {
    latinOneFailed_ = true;
    
    m_addition3_ = UCA_m_addition3_;
    m_bottom3_ = UCA_m_bottom3_;
    m_bottomCount3_ = UCA_m_bottomCount3_;
    m_caseFirst_ = UCA_m_caseFirst_;
    m_caseSwitch_ = UCA_m_caseSwitch_;
    m_common3_ = UCA_m_common3_;
    m_contractionOffset_ = UCA_m_contractionOffset_;
    setDecomposition(UCA_.getDecomposition());
    m_defaultCaseFirst_ = UCA_m_defaultCaseFirst_;
    m_defaultDecomposition_ = UCA_m_defaultDecomposition_;
    m_defaultIsAlternateHandlingShifted_ = UCA_m_defaultIsAlternateHandlingShifted_;
    
    m_defaultIsCaseLevel_ = UCA_m_defaultIsCaseLevel_;
    m_defaultIsFrenchCollation_ = UCA_m_defaultIsFrenchCollation_;
    m_defaultIsHiragana4_ = UCA_m_defaultIsHiragana4_;
    m_defaultStrength_ = UCA_m_defaultStrength_;
    m_defaultVariableTopValue_ = UCA_m_defaultVariableTopValue_;
    m_expansionOffset_ = UCA_m_expansionOffset_;
    m_isAlternateHandlingShifted_ = UCA_m_isAlternateHandlingShifted_;
    m_isCaseLevel_ = UCA_m_isCaseLevel_;
    m_isFrenchCollation_ = UCA_m_isFrenchCollation_;
    m_isHiragana4_ = UCA_m_isHiragana4_;
    m_isJamoSpecial_ = UCA_m_isJamoSpecial_;
    m_isSimple3_ = UCA_m_isSimple3_;
    m_mask3_ = UCA_m_mask3_;
    m_minContractionEnd_ = UCA_m_minContractionEnd_;
    m_minUnsafe_ = UCA_m_minUnsafe_;
    m_rules_ = UCA_m_rules_;
    setStrength(UCA_.getStrength());
    m_top3_ = UCA_m_top3_;
    m_topCount3_ = UCA_m_topCount3_;
    m_variableTopValue_ = UCA_m_variableTopValue_;
    setWithUCATables();
    latinOneFailed_ = false;
  }
  










  final boolean isUnsafe(char ch)
  {
    if (ch < m_minUnsafe_) {
      return false;
    }
    
    if (ch >= '℀') {
      if ((UTF16.isLeadSurrogate(ch)) || (UTF16.isTrailSurrogate(ch)))
      {
        return true;
      }
      ch = (char)(ch & 0x1FFF);
      ch = (char)(ch + 'Ā');
    }
    int value = m_unsafe_[(ch >> '\003')];
    return (value >> (ch & 0x7) & 0x1) != 0;
  }
  






  final boolean isContractionEnd(char ch)
  {
    if (UTF16.isTrailSurrogate(ch)) {
      return true;
    }
    
    if (ch < m_minContractionEnd_) {
      return false;
    }
    
    if (ch >= '℀') {
      ch = (char)(ch & 0x1FFF);
      ch = (char)(ch + 'Ā');
    }
    int value = m_contractionEnd_[(ch >> '\003')];
    return (value >> (ch & 0x7) & 0x1) != 0;
  }
  





  static int getTag(int ce)
  {
    return (ce & 0xF000000) >> 24;
  }
  





  static boolean isSpecial(int ce)
  {
    return (ce & 0xF0000000) == -268435456;
  }
  





  static final boolean isContinuation(int ce)
  {
    return (ce != -1) && ((ce & 0xC0) == 192);
  }
  































































































































































  boolean latinOneUse_ = false;
  boolean latinOneRegenTable_ = false;
  boolean latinOneFailed_ = false;
  
  int latinOneTableLen_ = 0;
  int[] latinOneCEs_ = null;
  
  private StringCharacterIterator m_srcUtilIter_;
  
  private CollationElementIterator m_srcUtilColEIter_;
  
  private StringCharacterIterator m_tgtUtilIter_;
  
  private CollationElementIterator m_tgtUtilColEIter_;
  
  private boolean m_utilCompare0_;
  
  private boolean m_utilCompare1_;
  
  private boolean m_utilCompare2_;
  
  private boolean m_utilCompare3_;
  
  private boolean m_utilCompare4_;
  
  private boolean m_utilCompare5_;
  
  private byte[] m_utilBytes0_;
  
  private byte[] m_utilBytes1_;
  
  private byte[] m_utilBytes2_;
  
  private byte[] m_utilBytes3_;
  private byte[] m_utilBytes4_;
  private byte[] m_utilBytes5_;
  private int m_utilBytesCount0_;
  private int m_utilBytesCount1_;
  private int m_utilBytesCount2_;
  private int m_utilBytesCount3_;
  private int m_utilBytesCount4_;
  private int m_utilBytesCount5_;
  private int m_utilCount0_;
  private int m_utilCount1_;
  private int m_utilCount2_;
  private int m_utilCount3_;
  private int m_utilCount4_;
  private int m_utilCount5_;
  private int m_utilFrenchStart_;
  private int m_utilFrenchEnd_;
  private int[] m_srcUtilCEBuffer_;
  private int[] m_tgtUtilCEBuffer_;
  private int m_srcUtilCEBufferSize_;
  private int m_tgtUtilCEBufferSize_;
  private int m_srcUtilContOffset_;
  private int m_tgtUtilContOffset_;
  private int m_srcUtilOffset_;
  private int m_tgtUtilOffset_;
  private static final int ENDOFLATINONERANGE_ = 255;
  private static final int LATINONETABLELEN_ = 305;
  private static final int BAIL_OUT_CE_ = -16777216;
  ContractionInfo m_ContInfo_;
  
  private void init(String rules)
    throws Exception
  {
    setWithUCAData();
    CollationParsedRuleBuilder builder = new CollationParsedRuleBuilder(rules);
    

    builder.setRules(this);
    m_rules_ = rules;
    init();
    initUtility();
  }
  
  private final int compareRegular(String source, String target, int offset) {
    int strength = getStrength();
    
    m_utilCompare0_ = m_isCaseLevel_;
    m_utilCompare1_ = true;
    m_utilCompare2_ = (strength >= 1);
    m_utilCompare3_ = (strength >= 2);
    m_utilCompare4_ = (strength >= 3);
    m_utilCompare5_ = (strength == 15);
    boolean doFrench = (m_isFrenchCollation_) && (m_utilCompare2_);
    boolean doShift4 = (m_isAlternateHandlingShifted_) && (m_utilCompare4_);
    boolean doHiragana4 = (m_isHiragana4_) && (m_utilCompare4_);
    
    if ((doHiragana4) && (doShift4)) {
      String sourcesub = source.substring(offset);
      String targetsub = target.substring(offset);
      return compareBySortKeys(sourcesub, targetsub);
    }
    

    int lowestpvalue = m_isAlternateHandlingShifted_ ? m_variableTopValue_ << 16 : 0;
    
    m_srcUtilCEBufferSize_ = 0;
    m_tgtUtilCEBufferSize_ = 0;
    int result = doPrimaryCompare(doHiragana4, lowestpvalue, source, target, offset);
    
    if ((m_srcUtilCEBufferSize_ == -1) && (m_tgtUtilCEBufferSize_ == -1))
    {



      return result;
    }
    
    int hiraganaresult = result;
    
    if (m_utilCompare2_) {
      result = doSecondaryCompare(doFrench);
      if (result != 0) {
        return result;
      }
    }
    
    if (m_utilCompare0_) {
      result = doCaseCompare();
      if (result != 0) {
        return result;
      }
    }
    
    if (m_utilCompare3_) {
      result = doTertiaryCompare();
      if (result != 0) {
        return result;
      }
    }
    
    if (doShift4) {
      result = doQuaternaryCompare(lowestpvalue);
      if (result != 0) {
        return result;
      }
    }
    else if ((doHiragana4) && (hiraganaresult != 0))
    {

      return hiraganaresult;
    }
    




    if (m_utilCompare5_) {
      return doIdenticalCompare(source, target, offset, true);
    }
    return 0;
  }
  














  private final int doPrimaryBytes(int ce, boolean notIsContinuation, boolean doShift, int leadPrimary, int commonBottom4, int bottomCount4)
  {
    int p2 = ce >>= 16 & 0xFF;
    int p1 = ce >>> 8;
    if (doShift) {
      if (m_utilCount4_ > 0) {
        while (m_utilCount4_ > bottomCount4) {
          m_utilBytes4_ = append(m_utilBytes4_, m_utilBytesCount4_, (byte)(commonBottom4 + bottomCount4));
          
          m_utilBytesCount4_ += 1;
          m_utilCount4_ -= bottomCount4;
        }
        m_utilBytes4_ = append(m_utilBytes4_, m_utilBytesCount4_, (byte)(commonBottom4 + (m_utilCount4_ - 1)));
        

        m_utilBytesCount4_ += 1;
        m_utilCount4_ = 0;
      }
      

      if (p1 != 0)
      {
        m_utilBytes4_ = append(m_utilBytes4_, m_utilBytesCount4_, (byte)p1);
        
        m_utilBytesCount4_ += 1;
      }
      if (p2 != 0) {
        m_utilBytes4_ = append(m_utilBytes4_, m_utilBytesCount4_, (byte)p2);
        
        m_utilBytesCount4_ += 1;


      }
      



    }
    else if (p1 != 0) {
      if (notIsContinuation) {
        if (leadPrimary == p1) {
          m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)p2);
          
          m_utilBytesCount1_ += 1;
        }
        else {
          if (leadPrimary != 0) {
            m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)(p1 > leadPrimary ? -1 : 3));
            



            m_utilBytesCount1_ += 1;
          }
          if (p2 == 0)
          {
            m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)p1);
            

            m_utilBytesCount1_ += 1;
            leadPrimary = 0;
          }
          else if ((p1 < 77) || ((p1 > UCA_CONSTANTS_LAST_NON_VARIABLE_[0] >>> 24) && (p1 < UCA_CONSTANTS_FIRST_IMPLICIT_[0] >>> 24)))
          {






            leadPrimary = 0;
            m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)p1);
            

            m_utilBytesCount1_ += 1;
            m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)p2);
            

            m_utilBytesCount1_ += 1;
          }
          else {
            leadPrimary = p1;
            m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)p1);
            

            m_utilBytesCount1_ += 1;
            m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)p2);
            
            m_utilBytesCount1_ += 1;
          }
        }
      }
      else
      {
        m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)p1);
        
        m_utilBytesCount1_ += 1;
        if (p2 != 0) {
          m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)p2);
          

          m_utilBytesCount1_ += 1;
        }
      }
    }
    
    return leadPrimary;
  }
  








  private final void doSecondaryBytes(int ce, boolean notIsContinuation, boolean doFrench)
  {
    int s = ce >>= 8 & 0xFF;
    if (s != 0) {
      if (!doFrench)
      {
        if ((s == 5) && (notIsContinuation)) {
          m_utilCount2_ += 1;
        }
        else {
          if (m_utilCount2_ > 0) {
            if (s > 5) {
              while (m_utilCount2_ > 64) {
                m_utilBytes2_ = append(m_utilBytes2_, m_utilBytesCount2_, (byte)70);
                

                m_utilBytesCount2_ += 1;
                m_utilCount2_ -= 64;
              }
              m_utilBytes2_ = append(m_utilBytes2_, m_utilBytesCount2_, (byte)(134 - (m_utilCount2_ - 1)));
              


              m_utilBytesCount2_ += 1;
            }
            else {
              while (m_utilCount2_ > 64) {
                m_utilBytes2_ = append(m_utilBytes2_, m_utilBytesCount2_, (byte)69);
                

                m_utilBytesCount2_ += 1;
                m_utilCount2_ -= 64;
              }
              m_utilBytes2_ = append(m_utilBytes2_, m_utilBytesCount2_, (byte)(5 + (m_utilCount2_ - 1)));
              


              m_utilBytesCount2_ += 1;
            }
            m_utilCount2_ = 0;
          }
          m_utilBytes2_ = append(m_utilBytes2_, m_utilBytesCount2_, (byte)s);
          
          m_utilBytesCount2_ += 1;
        }
      }
      else {
        m_utilBytes2_ = append(m_utilBytes2_, m_utilBytesCount2_, (byte)s);
        
        m_utilBytesCount2_ += 1;
        




        if (notIsContinuation) {
          if (m_utilFrenchStart_ != -1)
          {

            reverseBuffer(m_utilBytes2_);
            m_utilFrenchStart_ = -1;
          }
        }
        else {
          if (m_utilFrenchStart_ == -1) {
            m_utilFrenchStart_ = (m_utilBytesCount2_ - 2);
          }
          m_utilFrenchEnd_ = (m_utilBytesCount2_ - 1);
        }
      }
    }
  }
  




  private void reverseBuffer(byte[] buffer)
  {
    int start = m_utilFrenchStart_;
    int end = m_utilFrenchEnd_;
    while (start < end) {
      byte b = buffer[start];
      buffer[(start++)] = buffer[end];
      buffer[(end--)] = b;
    }
  }
  





  private final int doCaseShift(int caseshift)
  {
    if (caseshift == 0) {
      m_utilBytes0_ = append(m_utilBytes0_, m_utilBytesCount0_, (byte)Byte.MIN_VALUE);
      
      m_utilBytesCount0_ += 1;
      caseshift = 7;
    }
    return caseshift;
  }
  









  private final int doCaseBytes(int tertiary, boolean notIsContinuation, int caseshift)
  {
    caseshift = doCaseShift(caseshift);
    
    if ((notIsContinuation) && (tertiary != 0)) {
      byte casebits = (byte)(tertiary & 0xC0);
      if (m_caseFirst_ == 25) {
        if (casebits == 0) {
          int tmp46_45 = (m_utilBytesCount0_ - 1); byte[] tmp46_37 = m_utilBytes0_;tmp46_37[tmp46_45] = ((byte)(tmp46_37[tmp46_45] | 1 << --caseshift));

        }
        else
        {
          caseshift = doCaseShift(caseshift - 1); int 
            tmp78_77 = (m_utilBytesCount0_ - 1); byte[] tmp78_69 = m_utilBytes0_;tmp78_69[tmp78_77] = ((byte)(tmp78_69[tmp78_77] | (casebits >> 6 & 0x1) << --caseshift));
        }
        

      }
      else if (casebits != 0) {
        int tmp113_112 = (m_utilBytesCount0_ - 1); byte[] tmp113_104 = m_utilBytes0_;tmp113_104[tmp113_112] = ((byte)(tmp113_104[tmp113_112] | 1 << --caseshift));
        

        caseshift = doCaseShift(caseshift); int 
          tmp140_139 = (m_utilBytesCount0_ - 1); byte[] tmp140_131 = m_utilBytes0_;tmp140_131[tmp140_139] = ((byte)(tmp140_131[tmp140_139] | (casebits >> 7 & 0x1) << --caseshift));
      }
      else
      {
        caseshift--;
      }
    }
    

    return caseshift;
  }
  






  private final void doTertiaryBytes(int tertiary, boolean notIsContinuation)
  {
    if (tertiary != 0)
    {

      if ((tertiary == m_common3_) && (notIsContinuation)) {
        m_utilCount3_ += 1;
      }
      else {
        int common3 = m_common3_ & 0xFF;
        if ((tertiary > common3) && (m_common3_ == 5)) {
          tertiary += m_addition3_;
        }
        else if ((tertiary <= common3) && (m_common3_ == 197))
        {
          tertiary -= m_addition3_;
        }
        if (m_utilCount3_ > 0) {
          if (tertiary > common3) {
            while (m_utilCount3_ > m_topCount3_) {
              m_utilBytes3_ = append(m_utilBytes3_, m_utilBytesCount3_, (byte)(m_top3_ - m_topCount3_));
              

              m_utilBytesCount3_ += 1;
              m_utilCount3_ -= m_topCount3_;
            }
            m_utilBytes3_ = append(m_utilBytes3_, m_utilBytesCount3_, (byte)(m_top3_ - (m_utilCount3_ - 1)));
            


            m_utilBytesCount3_ += 1;
          }
          else {
            while (m_utilCount3_ > m_bottomCount3_) {
              m_utilBytes3_ = append(m_utilBytes3_, m_utilBytesCount3_, (byte)(m_bottom3_ + m_bottomCount3_));
              

              m_utilBytesCount3_ += 1;
              m_utilCount3_ -= m_bottomCount3_;
            }
            m_utilBytes3_ = append(m_utilBytes3_, m_utilBytesCount3_, (byte)(m_bottom3_ + (m_utilCount3_ - 1)));
            


            m_utilBytesCount3_ += 1;
          }
          m_utilCount3_ = 0;
        }
        m_utilBytes3_ = append(m_utilBytes3_, m_utilBytesCount3_, (byte)tertiary);
        
        m_utilBytesCount3_ += 1;
      }
    }
  }
  










  private final void doQuaternaryBytes(boolean isCodePointHiragana, int commonBottom4, int bottomCount4, byte hiragana4)
  {
    if (isCodePointHiragana) {
      if (m_utilCount4_ > 0) {
        while (m_utilCount4_ > bottomCount4) {
          m_utilBytes4_ = append(m_utilBytes4_, m_utilBytesCount4_, (byte)(commonBottom4 + bottomCount4));
          

          m_utilBytesCount4_ += 1;
          m_utilCount4_ -= bottomCount4;
        }
        m_utilBytes4_ = append(m_utilBytes4_, m_utilBytesCount4_, (byte)(commonBottom4 + (m_utilCount4_ - 1)));
        

        m_utilBytesCount4_ += 1;
        m_utilCount4_ = 0;
      }
      m_utilBytes4_ = append(m_utilBytes4_, m_utilBytesCount4_, hiragana4);
      
      m_utilBytesCount4_ += 1;
    }
    else {
      m_utilCount4_ += 1;
    }
  }
  













  private final void getSortKeyBytes(String source, boolean doFrench, byte hiragana4, int commonBottom4, int bottomCount4)
  {
    int backupDecomposition = getDecomposition();
    setDecomposition(16);
    m_srcUtilIter_.setText(source);
    m_srcUtilColEIter_.setText(m_srcUtilIter_);
    m_utilFrenchStart_ = -1;
    m_utilFrenchEnd_ = -1;
    



    boolean doShift = false;
    boolean notIsContinuation = false;
    
    int leadPrimary = 0;
    int caseShift = 0;
    for (;;)
    {
      int ce = m_srcUtilColEIter_.next();
      if (ce == -1) {
        break;
      }
      
      if (ce != 0)
      {


        notIsContinuation = !isContinuation(ce);
        






        boolean isPrimaryByteIgnorable = (ce & 0xFFFF0000) == 0;
        

        boolean isSmallerThanVariableTop = ce >>> 16 <= m_variableTopValue_;
        
        doShift = ((m_isAlternateHandlingShifted_) && (((notIsContinuation) && (isSmallerThanVariableTop) && (!isPrimaryByteIgnorable)) || ((!notIsContinuation) && (doShift)))) || ((doShift) && (isPrimaryByteIgnorable));
        



        if ((!doShift) || (!isPrimaryByteIgnorable))
        {






          leadPrimary = doPrimaryBytes(ce, notIsContinuation, doShift, leadPrimary, commonBottom4, bottomCount4);
          

          if (!doShift)
          {

            if (m_utilCompare2_) {
              doSecondaryBytes(ce, notIsContinuation, doFrench);
            }
            
            int t = ce & 0xFF;
            if (!notIsContinuation) {
              t = ce & 0xFF3F;
            }
            
            if (m_utilCompare0_) {
              caseShift = doCaseBytes(t, notIsContinuation, caseShift);
            }
            else if (notIsContinuation) {
              t ^= m_caseSwitch_;
            }
            
            t &= m_mask3_;
            
            if (m_utilCompare3_) {
              doTertiaryBytes(t, notIsContinuation);
            }
            
            if ((m_utilCompare4_) && (notIsContinuation))
              doQuaternaryBytes(m_srcUtilColEIter_.m_isCodePointHiragana_, commonBottom4, bottomCount4, hiragana4);
          }
        }
      } }
    setDecomposition(backupDecomposition);
    if (m_utilFrenchStart_ != -1)
    {
      reverseBuffer(m_utilBytes2_);
    }
  }
  













  private final byte[] getSortKey(String source, boolean doFrench, int commonBottom4, int bottomCount4)
  {
    if (m_utilCompare2_) {
      doSecondary(doFrench);
      if (m_utilCompare0_) {
        doCase();
      }
      if (m_utilCompare3_) {
        doTertiary();
        if (m_utilCompare4_) {
          doQuaternary(commonBottom4, bottomCount4);
          if (m_utilCompare5_) {
            doIdentical(source);
          }
        }
      }
    }
    
    m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)0);
    m_utilBytesCount1_ += 1;
    byte[] result = (byte[])m_utilBytes1_.clone();
    return result;
  }
  




  private final void doFrench()
  {
    for (int i = 0; i < m_utilBytesCount2_; i++) {
      byte s = m_utilBytes2_[(m_utilBytesCount2_ - i - 1)];
      
      if (s == 5) {
        m_utilCount2_ += 1;
      }
      else {
        if (m_utilCount2_ > 0)
        {
          if ((s & 0xFF) > 5)
          {
            while (m_utilCount2_ > 64) {
              m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)70);
              

              m_utilBytesCount1_ += 1;
              m_utilCount2_ -= 64;
            }
            m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)(134 - (m_utilCount2_ - 1)));
            


            m_utilBytesCount1_ += 1;
          }
          else {
            while (m_utilCount2_ > 64) {
              m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)69);
              

              m_utilBytesCount1_ += 1;
              m_utilCount2_ -= 64;
            }
            m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)(5 + (m_utilCount2_ - 1)));
            


            m_utilBytesCount1_ += 1;
          }
          m_utilCount2_ = 0;
        }
        m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, s);
        m_utilBytesCount1_ += 1;
      }
    }
    if (m_utilCount2_ > 0) {
      while (m_utilCount2_ > 64) {
        m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)69);
        

        m_utilBytesCount1_ += 1;
        m_utilCount2_ -= 64;
      }
      m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)(5 + (m_utilCount2_ - 1)));
      

      m_utilBytesCount1_ += 1;
    }
  }
  




  private final void doSecondary(boolean doFrench)
  {
    if (m_utilCount2_ > 0) {
      while (m_utilCount2_ > 64) {
        m_utilBytes2_ = append(m_utilBytes2_, m_utilBytesCount2_, (byte)69);
        

        m_utilBytesCount2_ += 1;
        m_utilCount2_ -= 64;
      }
      m_utilBytes2_ = append(m_utilBytes2_, m_utilBytesCount2_, (byte)(5 + (m_utilCount2_ - 1)));
      

      m_utilBytesCount2_ += 1;
    }
    
    m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)1);
    
    m_utilBytesCount1_ += 1;
    
    if (doFrench) {
      doFrench();
    }
    else {
      if (m_utilBytes1_.length <= m_utilBytesCount1_ + m_utilBytesCount2_)
      {
        m_utilBytes1_ = increase(m_utilBytes1_, m_utilBytesCount1_, m_utilBytesCount2_);
      }
      
      System.arraycopy(m_utilBytes2_, 0, m_utilBytes1_, m_utilBytesCount1_, m_utilBytesCount2_);
      
      m_utilBytesCount1_ += m_utilBytesCount2_;
    }
  }
  








  private static final byte[] increase(byte[] buffer, int size, int incrementsize)
  {
    byte[] result = new byte[buffer.length + incrementsize];
    System.arraycopy(buffer, 0, result, 0, size);
    return result;
  }
  








  private static final int[] increase(int[] buffer, int size, int incrementsize)
  {
    int[] result = new int[buffer.length + incrementsize];
    System.arraycopy(buffer, 0, result, 0, size);
    return result;
  }
  



  private final void doCase()
  {
    m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)1);
    
    m_utilBytesCount1_ += 1;
    if (m_utilBytes1_.length <= m_utilBytesCount1_ + m_utilBytesCount0_) {
      m_utilBytes1_ = increase(m_utilBytes1_, m_utilBytesCount1_, m_utilBytesCount0_);
    }
    
    System.arraycopy(m_utilBytes0_, 0, m_utilBytes1_, m_utilBytesCount1_, m_utilBytesCount0_);
    
    m_utilBytesCount1_ += m_utilBytesCount0_;
  }
  



  private final void doTertiary()
  {
    if (m_utilCount3_ > 0) {
      if (m_common3_ != 5) {
        while (m_utilCount3_ >= m_topCount3_) {
          m_utilBytes3_ = append(m_utilBytes3_, m_utilBytesCount3_, (byte)(m_top3_ - m_topCount3_));
          
          m_utilBytesCount3_ += 1;
          m_utilCount3_ -= m_topCount3_;
        }
        m_utilBytes3_ = append(m_utilBytes3_, m_utilBytesCount3_, (byte)(m_top3_ - m_utilCount3_));
        
        m_utilBytesCount3_ += 1;
      }
      else {
        while (m_utilCount3_ > m_bottomCount3_) {
          m_utilBytes3_ = append(m_utilBytes3_, m_utilBytesCount3_, (byte)(m_bottom3_ + m_bottomCount3_));
          

          m_utilBytesCount3_ += 1;
          m_utilCount3_ -= m_bottomCount3_;
        }
        m_utilBytes3_ = append(m_utilBytes3_, m_utilBytesCount3_, (byte)(m_bottom3_ + (m_utilCount3_ - 1)));
        

        m_utilBytesCount3_ += 1;
      }
    }
    m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)1);
    
    m_utilBytesCount1_ += 1;
    if (m_utilBytes1_.length <= m_utilBytesCount1_ + m_utilBytesCount3_) {
      m_utilBytes1_ = increase(m_utilBytes1_, m_utilBytesCount1_, m_utilBytesCount3_);
    }
    
    System.arraycopy(m_utilBytes3_, 0, m_utilBytes1_, m_utilBytesCount1_, m_utilBytesCount3_);
    
    m_utilBytesCount1_ += m_utilBytesCount3_;
  }
  



  private final void doQuaternary(int commonbottom4, int bottomcount4)
  {
    if (m_utilCount4_ > 0) {
      while (m_utilCount4_ > bottomcount4) {
        m_utilBytes4_ = append(m_utilBytes4_, m_utilBytesCount4_, (byte)(commonbottom4 + bottomcount4));
        
        m_utilBytesCount4_ += 1;
        m_utilCount4_ -= bottomcount4;
      }
      m_utilBytes4_ = append(m_utilBytes4_, m_utilBytesCount4_, (byte)(commonbottom4 + (m_utilCount4_ - 1)));
      

      m_utilBytesCount4_ += 1;
    }
    m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)1);
    
    m_utilBytesCount1_ += 1;
    if (m_utilBytes1_.length <= m_utilBytesCount1_ + m_utilBytesCount4_) {
      m_utilBytes1_ = increase(m_utilBytes1_, m_utilBytesCount1_, m_utilBytesCount4_);
    }
    
    System.arraycopy(m_utilBytes4_, 0, m_utilBytes1_, m_utilBytesCount1_, m_utilBytesCount4_);
    
    m_utilBytesCount1_ += m_utilBytesCount4_;
  }
  






  private final void doIdentical(String source)
  {
    int isize = BOCU.getCompressionLength(source);
    m_utilBytes1_ = append(m_utilBytes1_, m_utilBytesCount1_, (byte)1);
    
    m_utilBytesCount1_ += 1;
    if (m_utilBytes1_.length <= m_utilBytesCount1_ + isize) {
      m_utilBytes1_ = increase(m_utilBytes1_, m_utilBytesCount1_, 1 + isize);
    }
    
    m_utilBytesCount1_ = BOCU.compress(source, m_utilBytes1_, m_utilBytesCount1_);
  }
  










  private final int getFirstUnmatchedOffset(String source, String target)
  {
    int result = 0;
    int slength = source.length();
    int tlength = target.length();
    int minlength = slength;
    if (minlength > tlength) {
      minlength = tlength;
    }
    

    while ((result < minlength) && (source.charAt(result) == target.charAt(result)) && (!CollationElementIterator.isThaiPreVowel(source.charAt(result)))) {
      result++;
    }
    if (result > 0)
    {



      char schar = '\000';
      char tchar = '\000';
      if (result < minlength) {
        schar = source.charAt(result);
        tchar = target.charAt(result);
      }
      else {
        schar = source.charAt(minlength - 1);
        if (isUnsafe(schar)) {
          tchar = schar;
        } else {
          if (slength == tlength) {
            return result;
          }
          if (slength < tlength) {
            tchar = target.charAt(result);
          }
          else
            schar = source.charAt(result);
        }
      }
      if ((isUnsafe(schar)) || (isUnsafe(tchar)))
      {


        do
        {



          result--;
        }
        while ((result > 0) && (isUnsafe(source.charAt(result))));
      }
    }
    return result;
  }
  










  private static final byte[] append(byte[] array, int appendindex, byte value)
  {
    if (appendindex + 1 >= array.length) {
      array = increase(array, appendindex, 128);
    }
    array[appendindex] = value;
    return array;
  }
  








  private final int compareBySortKeys(String source, String target)
  {
    CollationKey sourcekey = getCollationKey(source);
    CollationKey targetkey = getCollationKey(target);
    return sourcekey.compareTo(targetkey);
  }
  
























  private final int doPrimaryCompare(boolean doHiragana4, int lowestpvalue, String source, String target, int textoffset)
  {
    m_srcUtilIter_.setText(source);
    m_srcUtilColEIter_.setText(m_srcUtilIter_, textoffset);
    m_tgtUtilIter_.setText(target);
    m_tgtUtilColEIter_.setText(m_tgtUtilIter_, textoffset);
    

    if (!m_isAlternateHandlingShifted_) {
      int hiraganaresult = 0;
      for (;;) {
        int sorder = 0;
        do
        {
          sorder = m_srcUtilColEIter_.next();
          m_srcUtilCEBuffer_ = append(m_srcUtilCEBuffer_, m_srcUtilCEBufferSize_, sorder);
          
          m_srcUtilCEBufferSize_ += 1;
          sorder &= 0xFFFF0000;
        } while (sorder == 0);
        
        int torder = 0;
        do {
          torder = m_tgtUtilColEIter_.next();
          m_tgtUtilCEBuffer_ = append(m_tgtUtilCEBuffer_, m_tgtUtilCEBufferSize_, torder);
          
          m_tgtUtilCEBufferSize_ += 1;
          torder &= 0xFFFF0000;
        } while (torder == 0);
        

        if (sorder == torder)
        {

          if (m_srcUtilCEBuffer_[(m_srcUtilCEBufferSize_ - 1)] == -1)
          {
            if (m_tgtUtilCEBuffer_[(m_tgtUtilCEBufferSize_ - 1)] == -1)
              break;
            return -1;
          }
          

          if (m_tgtUtilCEBuffer_[(m_tgtUtilCEBufferSize_ - 1)] == -1)
          {
            return 1;
          }
          if ((doHiragana4) && (hiraganaresult == 0) && (m_srcUtilColEIter_.m_isCodePointHiragana_ != m_tgtUtilColEIter_.m_isCodePointHiragana_))
          {

            if (m_srcUtilColEIter_.m_isCodePointHiragana_) {
              hiraganaresult = -1;
            }
            else {
              hiraganaresult = 1;
            }
          }
        }
        else
        {
          return endPrimaryCompare(sorder, torder);
        }
      }
      
      return hiraganaresult;
    }
    int sorder;
    int torder;
    do { sorder = getPrimaryShiftedCompareCE(m_srcUtilColEIter_, lowestpvalue, true);
      
      torder = getPrimaryShiftedCompareCE(m_tgtUtilColEIter_, lowestpvalue, false);
      
      if (sorder != torder) break;
    } while (m_srcUtilCEBuffer_[(m_srcUtilCEBufferSize_ - 1)] != -1);
    



    break label335;
    


    return endPrimaryCompare(sorder, torder);
    
    label335:
    
    return 0;
  }
  











  private final int endPrimaryCompare(int sorder, int torder)
  {
    boolean isSourceNullOrder = m_srcUtilCEBuffer_[(m_srcUtilCEBufferSize_ - 1)] == -1;
    

    boolean isTargetNullOrder = m_tgtUtilCEBuffer_[(m_tgtUtilCEBufferSize_ - 1)] == -1;
    

    m_srcUtilCEBufferSize_ = -1;
    m_tgtUtilCEBufferSize_ = -1;
    if (isSourceNullOrder) {
      return -1;
    }
    if (isTargetNullOrder) {
      return 1;
    }
    
    sorder >>>= 16;
    torder >>>= 16;
    if (sorder < torder) {
      return -1;
    }
    return 1;
  }
  













  private final int getPrimaryShiftedCompareCE(CollationElementIterator coleiter, int lowestpvalue, boolean isSrc)
  {
    boolean shifted = false;
    int result = 0;
    int[] cebuffer = m_srcUtilCEBuffer_;
    int cebuffersize = m_srcUtilCEBufferSize_;
    if (!isSrc) {
      cebuffer = m_tgtUtilCEBuffer_;
      cebuffersize = m_tgtUtilCEBufferSize_;
    }
    for (;;) {
      result = coleiter.next();
      if (result == -1) {
        cebuffer = append(cebuffer, cebuffersize, result);
        cebuffersize++;
        break;
      }
      if ((result != 0) && ((!shifted) || ((result & 0xFFFF0000) != 0)))
      {






        if (isContinuation(result)) {
          if ((result & 0xFFFF0000) != 0)
          {

            if (shifted) {
              result = result & 0xFFFF0000 | 0xC0;
              

              cebuffer = append(cebuffer, cebuffersize, result);
              cebuffersize++;
            }
            else
            {
              cebuffer = append(cebuffer, cebuffersize, result);
              cebuffersize++;
              break;
            }
            
          }
          else if (!shifted) {
            cebuffer = append(cebuffer, cebuffersize, result);
            cebuffersize++;
          }
        }
        else
        {
          if (Utility.compareUnsigned(result & 0xFFFF0000, lowestpvalue) > 0)
          {
            cebuffer = append(cebuffer, cebuffersize, result);
            cebuffersize++;
            break;
          }
          
          if ((result & 0xFFFF0000) != 0) {
            shifted = true;
            result &= 0xFFFF0000;
            cebuffer = append(cebuffer, cebuffersize, result);
            cebuffersize++;
          }
          else
          {
            cebuffer = append(cebuffer, cebuffersize, result);
            cebuffersize++;
            shifted = false;
          }
        }
      }
    }
    
    if (isSrc) {
      m_srcUtilCEBuffer_ = cebuffer;
      m_srcUtilCEBufferSize_ = cebuffersize;
    }
    else {
      m_tgtUtilCEBuffer_ = cebuffer;
      m_tgtUtilCEBufferSize_ = cebuffersize;
    }
    result &= 0xFFFF0000;
    return result;
  }
  









  private static final int[] append(int[] array, int appendindex, int value)
  {
    if (appendindex + 1 >= array.length) {
      array = increase(array, appendindex, 512);
    }
    array[appendindex] = value;
    return array;
  }
  






  private final int doSecondaryCompare(boolean doFrench)
  {
    if (!doFrench) {
      int soffset = 0;
      int toffset = 0;
      for (;;) {
        int sorder = 0;
        while (sorder == 0) {
          sorder = m_srcUtilCEBuffer_[(soffset++)] & 0xFF00;
        }
        
        int torder = 0;
        while (torder == 0) {
          torder = m_tgtUtilCEBuffer_[(toffset++)] & 0xFF00;
        }
        

        if (sorder == torder) {
          if (m_srcUtilCEBuffer_[(soffset - 1)] == -1)
          {
            if (m_tgtUtilCEBuffer_[(toffset - 1)] == -1)
              break;
            return -1;
          }
          

          if (m_tgtUtilCEBuffer_[(toffset - 1)] == -1)
          {
            return 1;
          }
        }
        else {
          if (m_srcUtilCEBuffer_[(soffset - 1)] == -1)
          {
            return -1;
          }
          if (m_tgtUtilCEBuffer_[(toffset - 1)] == -1)
          {
            return 1;
          }
          return sorder < torder ? -1 : 1;
        }
      }
    }
    else {
      m_srcUtilContOffset_ = 0;
      m_tgtUtilContOffset_ = 0;
      m_srcUtilOffset_ = (m_srcUtilCEBufferSize_ - 2);
      m_tgtUtilOffset_ = (m_tgtUtilCEBufferSize_ - 2);
      for (;;) {
        int sorder = getSecondaryFrenchCE(true);
        int torder = getSecondaryFrenchCE(false);
        if (sorder == torder) {
          if ((m_srcUtilOffset_ < 0) && (m_tgtUtilOffset_ < 0)) break; if (m_srcUtilCEBuffer_[m_srcUtilOffset_] == -1) {
            break;
          }
          
        }
        else
        {
          return sorder < torder ? -1 : 1;
        }
      }
    }
    return 0;
  }
  





  private final int getSecondaryFrenchCE(boolean isSrc)
  {
    int result = 0;
    int offset = m_srcUtilOffset_;
    int continuationoffset = m_srcUtilContOffset_;
    int[] cebuffer = m_srcUtilCEBuffer_;
    if (!isSrc) {
      offset = m_tgtUtilOffset_;
      continuationoffset = m_tgtUtilContOffset_;
      cebuffer = m_tgtUtilCEBuffer_;
    }
    

    while ((result == 0) && (offset >= 0)) {
      if (continuationoffset == 0) {
        result = cebuffer[offset];
        while (isContinuation(cebuffer[(offset--)])) {}
        

        if (isContinuation(cebuffer[(offset + 1)]))
        {
          continuationoffset = offset;
          offset += 2;
        }
      }
      else {
        result = cebuffer[(offset++)];
        if (!isContinuation(result))
        {
          offset = continuationoffset;
          
          continuationoffset = 0;
          continue;
        }
      }
      result &= 0xFF00;
    }
    if (isSrc) {
      m_srcUtilOffset_ = offset;
      m_srcUtilContOffset_ = continuationoffset;
    }
    else {
      m_tgtUtilOffset_ = offset;
      m_tgtUtilContOffset_ = continuationoffset;
    }
    return result;
  }
  




  private final int doCaseCompare()
  {
    int soffset = 0;
    int toffset = 0;
    for (;;) {
      int sorder = 0;
      int torder = 0;
      
      while ((sorder & 0x3F) == 0) {
        sorder = m_srcUtilCEBuffer_[(soffset++)];
        if (!isContinuation(sorder)) {
          sorder &= 0xFF;
          sorder ^= m_caseSwitch_;
        }
        else {
          sorder = 0;
        }
      }
      

      while ((torder & 0x3F) == 0) {
        torder = m_tgtUtilCEBuffer_[(toffset++)];
        if (!isContinuation(torder)) {
          torder &= 0xFF;
          torder ^= m_caseSwitch_;
        }
        else {
          torder = 0;
        }
      }
      
      sorder &= 0xC0;
      torder &= 0xC0;
      if (sorder == torder)
      {
        if (m_srcUtilCEBuffer_[(soffset - 1)] == -1)
        {
          if (m_tgtUtilCEBuffer_[(toffset - 1)] == -1)
            break;
          return -1;
        }
        

        if (m_tgtUtilCEBuffer_[(toffset - 1)] == -1)
        {
          return 1;
        }
      }
      else {
        if (m_srcUtilCEBuffer_[(soffset - 1)] == -1)
        {
          return -1;
        }
        if (m_tgtUtilCEBuffer_[(soffset - 1)] == -1)
        {
          return 1;
        }
        return sorder < torder ? -1 : 1;
      }
    }
    return 0;
  }
  




  private final int doTertiaryCompare()
  {
    int soffset = 0;
    int toffset = 0;
    for (;;) {
      int sorder = 0;
      int torder = 0;
      
      while ((sorder & 0x3F) == 0) {
        sorder = m_srcUtilCEBuffer_[(soffset++)] & m_mask3_;
        if (!isContinuation(sorder)) {
          sorder ^= m_caseSwitch_;
        }
        else {
          sorder &= 0x3F;
        }
      }
      

      while ((torder & 0x3F) == 0) {
        torder = m_tgtUtilCEBuffer_[(toffset++)] & m_mask3_;
        if (!isContinuation(torder)) {
          torder ^= m_caseSwitch_;
        }
        else {
          torder &= 0x3F;
        }
      }
      
      if (sorder == torder) {
        if (m_srcUtilCEBuffer_[(soffset - 1)] == -1)
        {
          if (m_tgtUtilCEBuffer_[(toffset - 1)] == -1)
            break;
          return -1;
        }
        

        if (m_tgtUtilCEBuffer_[(toffset - 1)] == -1)
        {
          return 1;
        }
      }
      else {
        if (m_srcUtilCEBuffer_[(soffset - 1)] == -1)
        {
          return -1;
        }
        if (m_tgtUtilCEBuffer_[(toffset - 1)] == -1)
        {
          return 1;
        }
        return sorder < torder ? -1 : 1;
      }
    }
    return 0;
  }
  






  private final int doQuaternaryCompare(int lowestpvalue)
  {
    boolean sShifted = true;
    boolean tShifted = true;
    int soffset = 0;
    int toffset = 0;
    for (;;) {
      int sorder = 0;
      int torder = 0;
      
      while ((sorder == 0) || ((isContinuation(sorder)) && (!sShifted))) {
        sorder = m_srcUtilCEBuffer_[(soffset++)];
        if (isContinuation(sorder)) {
          if (sShifted) {}


        }
        else if ((Utility.compareUnsigned(sorder, lowestpvalue) > 0) || ((sorder & 0xFFFF0000) == 0))
        {


          sorder = -65536;
          sShifted = false;
        }
        else {
          sShifted = true;
        }
      }
      sorder >>>= 16;
      
      while ((torder == 0) || ((isContinuation(torder)) && (!tShifted))) {
        torder = m_tgtUtilCEBuffer_[(toffset++)];
        if (isContinuation(torder)) {
          if (tShifted) {}


        }
        else if ((Utility.compareUnsigned(torder, lowestpvalue) > 0) || ((torder & 0xFFFF0000) == 0))
        {


          torder = -65536;
          tShifted = false;
        }
        else {
          tShifted = true;
        }
      }
      torder >>>= 16;
      
      if (sorder == torder) {
        if (m_srcUtilCEBuffer_[(soffset - 1)] == -1)
        {
          if (m_tgtUtilCEBuffer_[(toffset - 1)] == -1)
            break;
          return -1;
        }
        

        if (m_tgtUtilCEBuffer_[(toffset - 1)] == -1)
        {
          return 1;
        }
      }
      else {
        if (m_srcUtilCEBuffer_[(soffset - 1)] == -1)
        {
          return -1;
        }
        if (m_tgtUtilCEBuffer_[(toffset - 1)] == -1)
        {
          return 1;
        }
        return sorder < torder ? -1 : 1;
      }
    }
    return 0;
  }
  














  private static final int doIdenticalCompare(String source, String target, int offset, boolean normalize)
  {
    if (normalize) {
      if (Normalizer.quickCheck(source, Normalizer.NFD, 0) != Normalizer.YES)
      {
        source = Normalizer.decompose(source, false);
      }
      
      if (Normalizer.quickCheck(target, Normalizer.NFD, 0) != Normalizer.YES)
      {
        target = Normalizer.decompose(target, false);
      }
      offset = 0;
    }
    
    return doStringCompare(source, target, offset);
  }
  












  private static final int doStringCompare(String source, String target, int offset)
  {
    char schar = '\000';
    char tchar = '\000';
    int slength = source.length();
    int tlength = target.length();
    int minlength = Math.min(slength, tlength);
    while (offset < minlength) {
      schar = source.charAt(offset);
      tchar = target.charAt(offset++);
      if (schar != tchar) {
        break;
      }
    }
    
    if ((schar == tchar) && (offset == minlength)) {
      if (slength > minlength) {
        return 1;
      }
      if (tlength > minlength) {
        return -1;
      }
      return 0;
    }
    

    if ((schar >= 55296) && (tchar >= 55296))
    {
      schar = fixupUTF16(schar);
      tchar = fixupUTF16(tchar);
    }
    

    return schar < tchar ? -1 : 1;
  }
  



  private static final char fixupUTF16(char ch)
  {
    if (ch >= 57344) {
      ch = (char)(ch - 'ࠀ');
    }
    else {
      ch = (char)(ch + ' ');
    }
    return ch;
  }
  



  private void updateInternalState()
  {
    if (m_caseFirst_ == 25) {
      m_caseSwitch_ = 192;
    }
    else {
      m_caseSwitch_ = 0;
    }
    
    if ((m_isCaseLevel_) || (m_caseFirst_ == 16)) {
      m_mask3_ = 63;
      m_common3_ = 5;
      m_addition3_ = 128;
      m_top3_ = 133;
      m_bottom3_ = 5;
    }
    else {
      m_mask3_ = 255;
      m_addition3_ = 64;
      if (m_caseFirst_ == 25) {
        m_common3_ = 197;
        m_top3_ = 197;
        m_bottom3_ = 134;
      } else {
        m_common3_ = 5;
        m_top3_ = 69;
        m_bottom3_ = 5;
      }
    }
    

    int total3 = m_top3_ - 5 - 1;
    
    m_topCount3_ = ((int)(0.667D * total3));
    m_bottomCount3_ = (total3 - m_topCount3_);
    
    if ((!m_isCaseLevel_) && (getStrength() == 2) && (!m_isFrenchCollation_) && (!m_isAlternateHandlingShifted_))
    {
      m_isSimple3_ = true;
    }
    else {
      m_isSimple3_ = false;
    }
    if ((!m_isCaseLevel_) && (getStrength() <= 2) && (!m_isAlternateHandlingShifted_) && (!latinOneFailed_))
    {
      if ((latinOneCEs_ == null) || (latinOneRegenTable_)) {
        if (setUpLatinOne()) {
          latinOneUse_ = true;
        } else {
          latinOneUse_ = false;
          latinOneFailed_ = true;
        }
        latinOneRegenTable_ = false;
      } else {
        latinOneUse_ = true;
      }
    } else {
      latinOneUse_ = false;
    }
  }
  




  private final void init()
  {
    for (m_minUnsafe_ = '\000'; m_minUnsafe_ < '̀'; 
        m_minUnsafe_ = ((char)(m_minUnsafe_ + '\001')))
    {
      if (isUnsafe(m_minUnsafe_)) {
        break;
      }
    }
    
    for (m_minContractionEnd_ = '\000'; 
        m_minContractionEnd_ < '̀'; 
        m_minContractionEnd_ = ((char)(m_minContractionEnd_ + '\001')))
    {
      if (isContractionEnd(m_minContractionEnd_)) {
        break;
      }
    }
    latinOneFailed_ = true;
    setStrength(m_defaultStrength_);
    setDecomposition(m_defaultDecomposition_);
    m_variableTopValue_ = m_defaultVariableTopValue_;
    m_isFrenchCollation_ = m_defaultIsFrenchCollation_;
    m_isAlternateHandlingShifted_ = m_defaultIsAlternateHandlingShifted_;
    m_isCaseLevel_ = m_defaultIsCaseLevel_;
    m_caseFirst_ = m_defaultCaseFirst_;
    m_isHiragana4_ = m_defaultIsHiragana4_;
    latinOneFailed_ = false;
    updateInternalState();
  }
  


  private final void initUtility()
  {
    m_srcUtilIter_ = new StringCharacterIterator(new String(""));
    m_srcUtilColEIter_ = new CollationElementIterator(m_srcUtilIter_, this);
    m_tgtUtilIter_ = new StringCharacterIterator(new String(""));
    m_tgtUtilColEIter_ = new CollationElementIterator(m_tgtUtilIter_, this);
    m_utilBytes0_ = new byte[32];
    m_utilBytes1_ = new byte['Ѐ'];
    m_utilBytes2_ = new byte[''];
    m_utilBytes3_ = new byte[''];
    m_utilBytes4_ = new byte[''];
    m_srcUtilCEBuffer_ = new int['Ȁ'];
    m_tgtUtilCEBuffer_ = new int['Ȁ'];
  }
  



  private class shiftValues
  {
    private shiftValues() {}
    


    shiftValues(RuleBasedCollator.1 x1)
    {
      this(); }
    int primShift = 24;
    int secShift = 24;
    int terShift = 24;
  }
  
  private final void addLatinOneEntry(char ch, int CE, shiftValues sh)
  {
    int primary1 = 0;int primary2 = 0;int secondary = 0;int tertiary = 0;
    boolean reverseSecondary = false;
    if (!isContinuation(CE)) {
      tertiary = CE & m_mask3_;
      tertiary ^= m_caseSwitch_;
      reverseSecondary = true;
    } else {
      tertiary = (byte)(CE & 0xFF3F);
      tertiary &= 0x3F;
      reverseSecondary = false;
    }
    
    secondary = CE >>>= 8 & 0xFF;
    primary2 = CE >>>= 8 & 0xFF;
    primary1 = CE >>> 8;
    
    if (primary1 != 0) {
      latinOneCEs_[ch] |= primary1 << primShift;
      primShift -= 8;
    }
    if (primary2 != 0) {
      if (primShift < 0) {
        latinOneCEs_[ch] = -16777216;
        latinOneCEs_[(latinOneTableLen_ + ch)] = -16777216;
        latinOneCEs_[(2 * latinOneTableLen_ + ch)] = -16777216;
        return;
      }
      latinOneCEs_[ch] |= primary2 << primShift;
      primShift -= 8;
    }
    if (secondary != 0) {
      if ((reverseSecondary) && (m_isFrenchCollation_)) {
        latinOneCEs_[(latinOneTableLen_ + ch)] >>>= 8;
        latinOneCEs_[(latinOneTableLen_ + ch)] |= secondary << 24;
      } else {
        latinOneCEs_[(latinOneTableLen_ + ch)] |= secondary << secShift;
      }
      secShift -= 8;
    }
    if (tertiary != 0) {
      latinOneCEs_[(2 * latinOneTableLen_ + ch)] |= tertiary << terShift;
      terShift -= 8;
    }
  }
  
  private final void resizeLatinOneTable(int newSize)
  {
    int[] newTable = new int[3 * newSize];
    int sizeToCopy = newSize < latinOneTableLen_ ? newSize : latinOneTableLen_;
    
    System.arraycopy(latinOneCEs_, 0, newTable, 0, sizeToCopy);
    System.arraycopy(latinOneCEs_, latinOneTableLen_, newTable, newSize, sizeToCopy);
    System.arraycopy(latinOneCEs_, 2 * latinOneTableLen_, newTable, 2 * newSize, sizeToCopy);
    latinOneTableLen_ = newSize;
    latinOneCEs_ = newTable;
  }
  
  private final boolean setUpLatinOne() {
    if (latinOneCEs_ == null) {
      latinOneCEs_ = new int['Γ'];
      latinOneTableLen_ = 305;
    } else {
      Arrays.fill(latinOneCEs_, 0);
    }
    if (m_ContInfo_ == null) {
      m_ContInfo_ = new ContractionInfo(null);
    }
    char ch = '\000';
    

    CollationElementIterator it = getCollationElementIterator("");
    
    shiftValues s = new shiftValues(null);
    int CE = 0;
    char contractionOffset = 'Ā';
    
    for (ch = '\000'; ch <= 'ÿ'; ch = (char)(ch + '\001')) {
      primShift = 24;secShift = 24;terShift = 24;
      if (ch < 'Ā') {
        CE = m_trie_.getLatin1LinearValue(ch);
      } else {
        CE = m_trie_.getLeadValue(ch);
        if (CE == -268435456) {
          CE = UCA_m_trie_.getLeadValue(ch);
        }
      }
      if (!isSpecial(CE)) {
        addLatinOneEntry(ch, CE, s);
      } else {
        switch (getTag(CE))
        {


        case 1: 
          it.setText(UCharacter.toString(ch));
          while ((CE = it.next()) != -1) {
            if ((primShift < 0) || (secShift < 0) || (terShift < 0)) {
              latinOneCEs_[ch] = -16777216;
              latinOneCEs_[(latinOneTableLen_ + ch)] = -16777216;
              latinOneCEs_[(2 * latinOneTableLen_ + ch)] = -16777216;
              break;
            }
            addLatinOneEntry(ch, CE, s);
          }
          break;
        





        case 2: 
          if ((CE & 0xFFF000) != 0) {
            return false;
          }
          
          int UCharOffset = (CE & 0xFFFFFF) - m_contractionOffset_;
          
          CE |= (contractionOffset & 0xFFF) << '\f';
          
          latinOneCEs_[ch] = CE;
          latinOneCEs_[(latinOneTableLen_ + ch)] = CE;
          latinOneCEs_[(2 * latinOneTableLen_ + ch)] = CE;
          


          do
          {
            CE = m_contractionCE_[UCharOffset];
            if ((isSpecial(CE)) && (getTag(CE) == 1))
            {



              int offset = ((CE & 0xFFFFF0) >> 4) - m_expansionOffset_;
              int size = CE & 0xF;
              
              if (size != 0) {
                for (i = 0; i < size; i++) {
                  if ((primShift < 0) || (secShift < 0) || (terShift < 0)) {
                    latinOneCEs_[contractionOffset] = -16777216;
                    latinOneCEs_[(latinOneTableLen_ + contractionOffset)] = -16777216;
                    latinOneCEs_[(2 * latinOneTableLen_ + contractionOffset)] = -16777216;
                    break;
                  }
                  addLatinOneEntry(contractionOffset, m_expansion_[(offset + i)], s);
                }
              } else {
                while (m_expansion_[offset] != 0) { int i;
                  if ((primShift < 0) || (secShift < 0) || (terShift < 0)) {
                    latinOneCEs_[contractionOffset] = -16777216;
                    latinOneCEs_[(latinOneTableLen_ + contractionOffset)] = -16777216;
                    latinOneCEs_[(2 * latinOneTableLen_ + contractionOffset)] = -16777216;
                    break;
                  }
                  addLatinOneEntry(contractionOffset, m_expansion_[(offset++)], s);
                }
              }
              contractionOffset = (char)(contractionOffset + '\001');
            } else if (!isSpecial(CE)) {
              contractionOffset = (char)(contractionOffset + '\001');addLatinOneEntry(contractionOffset, CE, s);
            } else {
              latinOneCEs_[contractionOffset] = -16777216;
              latinOneCEs_[(latinOneTableLen_ + contractionOffset)] = -16777216;
              latinOneCEs_[(2 * latinOneTableLen_ + contractionOffset)] = -16777216;
              contractionOffset = (char)(contractionOffset + '\001');
            }
            UCharOffset++;
            primShift = 24;secShift = 24;terShift = 24;
            if (contractionOffset == latinOneTableLen_) {
              resizeLatinOneTable(2 * latinOneTableLen_);
            }
          } while (m_contractionIndex_[UCharOffset] != 65535);
          
          break;
        default: 
          latinOneFailed_ = true;
          return false;
        }
        
      }
    }
    if (contractionOffset < latinOneTableLen_) {
      resizeLatinOneTable(contractionOffset);
    }
    return true; }
  
  private class ContractionInfo { private ContractionInfo() {}
    ContractionInfo(RuleBasedCollator.1 x1) { this(); }
    


    int index;
  }
  

  private int getLatinOneContraction(int strength, int CE, String s)
  {
    int len = s.length();
    
    int UCharOffset = (CE & 0xFFF) - m_contractionOffset_;
    int offset = 1;
    int latinOneOffset = (CE & 0xFFF000) >>> 12;
    char schar = '\000';char tchar = '\000';
    








    for (;;)
    {
      if (m_ContInfo_.index == len) {
        return latinOneCEs_[(strength * latinOneTableLen_ + latinOneOffset)];
      }
      schar = s.charAt(m_ContInfo_.index);
      


      while (schar > (tchar = m_contractionIndex_[(UCharOffset + offset)])) {
        offset++;
      }
      
      if (schar == tchar) {
        m_ContInfo_.index += 1;
        return latinOneCEs_[(strength * latinOneTableLen_ + latinOneOffset + offset)];
      }
      

      if (schar > 'ÿ') {
        return -16777216;
      }
      
      int isZeroCE = m_trie_.getLeadValue(schar);
      if (isZeroCE != 0) break;
      m_ContInfo_.index += 1;
    }
    

    return latinOneCEs_[(strength * latinOneTableLen_ + latinOneOffset)];
  }
  













  private final int compareUseLatin1(String source, String target, int startOffset)
  {
    int sLen = source.length();
    int tLen = target.length();
    
    int strength = getStrength();
    
    int sIndex = startOffset;int tIndex = startOffset;
    char sChar = '\000';char tChar = '\000';
    int sOrder = 0;int tOrder = 0;
    
    boolean endOfSource = false;
    


    boolean haveContractions = false;
    

    int offset = latinOneTableLen_;
    



    for (;;)
    {
      if (sIndex == sLen) {
        endOfSource = true;
      }
      else {
        sChar = source.charAt(sIndex++);
        
        if (sChar > 'ÿ')
        {
          return compareRegular(source, target, startOffset);
        }
        sOrder = latinOneCEs_[sChar];
        if (isSpecial(sOrder))
        {

          if (getTag(sOrder) == 2) {
            m_ContInfo_.index = sIndex;
            sOrder = getLatinOneContraction(0, sOrder, source);
            sIndex = m_ContInfo_.index;
            haveContractions = true;
          }
          

          if (isSpecial(sOrder))
          {
            return compareRegular(source, target, startOffset);
          }
        }
      }
      while (sOrder != 0)
      {





























        while (tOrder == 0)
        {
          if (tIndex == tLen) {
            if (endOfSource) {
              break;
            }
            return 1;
          }
          
          tChar = target.charAt(tIndex++);
          if (tChar > 'ÿ')
          {
            return compareRegular(source, target, startOffset);
          }
          tOrder = latinOneCEs_[tChar];
          if (isSpecial(tOrder))
          {
            if (getTag(tOrder) == 2) {
              m_ContInfo_.index = tIndex;
              tOrder = getLatinOneContraction(0, tOrder, target);
              tIndex = m_ContInfo_.index;
              haveContractions = true;
            }
            if (isSpecial(tOrder))
            {
              return compareRegular(source, target, startOffset);
            }
          }
        }
        if (endOfSource) {
          return -1;
        }
        
        if (sOrder == tOrder) {
          sOrder = 0;tOrder = 0;
        }
        else
        {
          if (((sOrder ^ tOrder) & 0xFF000000) != 0)
          {
            if (sOrder >>> 8 < tOrder >>> 8) {
              return -1;
            }
            return 1;
          }
          




          sOrder <<= 8;
          tOrder <<= 8;
        }
      }
    }
    


    if (strength >= 1)
    {

      endOfSource = false;
      
      if (!m_isFrenchCollation_)
      {




        sIndex = startOffset;tIndex = startOffset;
        
        for (;;)
        {
          if (sIndex == sLen) {
            endOfSource = true;
          }
          else {
            sChar = source.charAt(sIndex++);
            sOrder = latinOneCEs_[(offset + sChar)];
            if (isSpecial(sOrder)) {
              m_ContInfo_.index = sIndex;
              sOrder = getLatinOneContraction(1, sOrder, source);
              sIndex = m_ContInfo_.index;
            }
          }
          while (sOrder != 0)
          {












            while (tOrder == 0) {
              if (tIndex == tLen) {
                if (endOfSource) {
                  break;
                }
                return 1;
              }
              
              tChar = target.charAt(tIndex++);
              tOrder = latinOneCEs_[(offset + tChar)];
              if (isSpecial(tOrder)) {
                m_ContInfo_.index = tIndex;
                tOrder = getLatinOneContraction(1, tOrder, target);
                tIndex = m_ContInfo_.index;
              }
            }
            if (endOfSource) {
              return -1;
            }
            
            if (sOrder == tOrder) {
              sOrder = 0;tOrder = 0;
            }
            else
            {
              if (((sOrder ^ tOrder) & 0xFF000000) != 0) {
                if (sOrder >>> 8 < tOrder >>> 8) {
                  return -1;
                }
                return 1;
              }
              
              sOrder <<= 8;
              tOrder <<= 8;
            }
          }
        } }
      if (haveContractions)
      {
        return compareRegular(source, target, startOffset);
      }
      
      sIndex = sLen;tIndex = tLen;
      
      for (;;)
      {
        if (sIndex == startOffset) {
          endOfSource = true;
        }
        else {
          sChar = source.charAt(--sIndex);
          sOrder = latinOneCEs_[(offset + sChar)];
        }
        while (sOrder != 0)
        {








          while (tOrder == 0) {
            if (tIndex == startOffset) {
              if (endOfSource) {
                break;
              }
              return 1;
            }
            
            tChar = target.charAt(--tIndex);
            tOrder = latinOneCEs_[(offset + tChar)];
          }
          
          if (endOfSource) {
            return -1;
          }
          
          if (sOrder == tOrder) {
            sOrder = 0;tOrder = 0;
          }
          else
          {
            if (((sOrder ^ tOrder) & 0xFF000000) != 0) {
              if (sOrder >>> 8 < tOrder >>> 8) {
                return -1;
              }
              return 1;
            }
            
            sOrder <<= 8;
            tOrder <<= 8;
          }
        }
      }
    }
    
    if (strength >= 2)
    {
      offset += latinOneTableLen_;
      
      sIndex = startOffset;tIndex = startOffset;
      endOfSource = false;
      for (;;)
      {
        if (sIndex == sLen) {
          endOfSource = true;
        }
        else {
          sChar = source.charAt(sIndex++);
          sOrder = latinOneCEs_[(offset + sChar)];
          if (isSpecial(sOrder)) {
            m_ContInfo_.index = sIndex;
            sOrder = getLatinOneContraction(2, sOrder, source);
            sIndex = m_ContInfo_.index;
          }
        }
        while (sOrder != 0)
        {











          while (tOrder == 0) {
            if (tIndex == tLen) {
              if (endOfSource) {
                return 0;
              }
              return 1;
            }
            
            tChar = target.charAt(tIndex++);
            tOrder = latinOneCEs_[(offset + tChar)];
            if (isSpecial(tOrder)) {
              m_ContInfo_.index = tIndex;
              tOrder = getLatinOneContraction(2, tOrder, target);
              tIndex = m_ContInfo_.index;
            }
          }
          if (endOfSource) {
            return -1;
          }
          if (sOrder == tOrder) {
            sOrder = 0;tOrder = 0;
          }
          else {
            if (((sOrder ^ tOrder) & 0xFF000000) != 0) {
              if (sOrder >>> 8 < tOrder >>> 8) {
                return -1;
              }
              return 1;
            }
            
            sOrder <<= 8;
            tOrder <<= 8;
          }
        }
      } }
    return 0;
  }
}
