package com.ibm.icu.impl;

import com.ibm.icu.text.Normalizer;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.RangeValueIterator.Element;

public final class NormalizerImpl
{
  static final NormalizerImpl IMPL;
  static final int UNSIGNED_BYTE_MASK = 255;
  static final long UNSIGNED_INT_MASK = 4294967295L;
  private static final String DATA_FILE_NAME = "data/unorm.icu";
  public static final int QC_NFC = 17;
  public static final int QC_NFKC = 34;
  public static final int QC_NFD = 4;
  public static final int QC_NFKD = 8;
  public static final int QC_ANY_NO = 15;
  public static final int QC_MAYBE = 16;
  public static final int QC_ANY_MAYBE = 48;
  public static final int QC_MASK = 63;
  private static final int COMBINES_FWD = 64;
  private static final int COMBINES_BACK = 128;
  public static final int COMBINES_ANY = 192;
  private static final int CC_SHIFT = 8;
  public static final int CC_MASK = 65280;
  private static final int EXTRA_SHIFT = 16;
  private static final int EXTRA_INDEX_TOP = 64512;
  private static final int EXTRA_SURROGATE_MASK = 1023;
  private static final int EXTRA_SURROGATE_TOP = 1008;
  private static final int EXTRA_HANGUL = 1008;
  private static final int EXTRA_JAMO_L = 1009;
  
  static
  {
    try
    {
      IMPL = new NormalizerImpl();
    }
    catch (Exception e)
    {
      throw new RuntimeException(e.getMessage());
    }
  }
  

  private static final int EXTRA_JAMO_V = 1010;
  
  private static final int EXTRA_JAMO_T = 1011;
  
  private static final long MIN_SPECIAL = 4227858432L;
  
  private static final long SURROGATES_TOP = 4293918720L;
  
  private static final long MIN_HANGUL = 4293918720L;
  
  private static final long MIN_JAMO_V = 4294049792L;
  
  private static final long JAMO_V_TOP = 4294115328L;
  
  static final int INDEX_TRIE_SIZE = 0;
  
  static final int INDEX_CHAR_COUNT = 1;
  
  static final int INDEX_COMBINE_DATA_COUNT = 2;
  
  static final int INDEX_COMBINE_FWD_COUNT = 3;
  
  static final int INDEX_COMBINE_BOTH_COUNT = 4;
  
  static final int INDEX_COMBINE_BACK_COUNT = 5;
  
  public static final int INDEX_MIN_NFC_NO_MAYBE = 6;
  
  public static final int INDEX_MIN_NFKC_NO_MAYBE = 7;
  
  public static final int INDEX_MIN_NFD_NO_MAYBE = 8;
  
  public static final int INDEX_MIN_NFKD_NO_MAYBE = 9;
  
  static final int INDEX_FCD_TRIE_SIZE = 10;
  
  static final int INDEX_AUX_TRIE_SIZE = 11;
  
  static final int INDEX_CANON_SET_COUNT = 12;
  
  static final int INDEX_TOP = 32;
  
  private static final int AUX_UNSAFE_SHIFT = 11;
  
  private static final int AUX_COMP_EX_SHIFT = 10;
  
  private static final int AUX_NFC_SKIPPABLE_F_SHIFT = 12;
  
  private static final int AUX_MAX_FNC = 1024;
  
  private static final int AUX_UNSAFE_MASK = 2048;
  
  private static final int AUX_FNC_MASK = 1023;
  
  private static final int AUX_COMP_EX_MASK = 1024;
  
  private static final long AUX_NFC_SKIP_F_MASK = 4096L;
  
  static final int SET_INDEX_CANON_SETS_LENGTH = 0;
  
  static final int SET_INDEX_CANON_BMP_TABLE_LENGTH = 1;
  
  static final int SET_INDEX_CANON_SUPP_TABLE_LENGTH = 2;
  
  static final int SET_INDEX_TOP = 32;
  
  static final int CANON_SET_INDICIES_INDEX = 0;
  
  static final int CANON_SET_START_SETS_INDEX = 1;
  
  static final int CANON_SET_BMP_TABLE_INDEX = 2;
  
  static final int CANON_SET_SUPP_TABLE_INDEX = 3;
  
  static final int CANON_SET_MAX_CANON_SETS = 16384;
  
  static final int CANON_SET_BMP_MASK = 49152;
  
  static final int CANON_SET_BMP_IS_INDEX = 16384;
  
  private static final int MAX_BUFFER_SIZE = 20;
  
  public static final int COMPARE_EQUIV = 524288;
  
  static FCDTrieImpl fcdTrieImpl;
  
  static NormTrieImpl normTrieImpl;
  
  static AuxTrieImpl auxTrieImpl;
  
  private static int[] indexes;
  
  static char[] combiningTable;
  
  static char[] extraData;
  
  static Object[] canonStartSets;
  
  static boolean isDataLoaded;
  
  static boolean isFormatVersion_2_1;
  
  static boolean isFormatVersion_2_2;
  
  private static final int DATA_BUFFER_SIZE = 25000;
  
  public static final int MIN_WITH_LEAD_CC = 768;
  
  private static final int DECOMP_FLAG_LENGTH_HAS_CC = 128;
  
  private static final int DECOMP_LENGTH_MASK = 127;
  
  private static final int BMP_INDEX_LENGTH = 2048;
  
  private static final int SURROGATE_BLOCK_BITS = 5;
  
  public static final int JAMO_L_BASE = 4352;
  public static final int JAMO_V_BASE = 4449;
  public static final int JAMO_T_BASE = 4519;
  public static final int HANGUL_BASE = 44032;
  public static final int JAMO_L_COUNT = 19;
  public static final int JAMO_V_COUNT = 21;
  public static final int JAMO_T_COUNT = 28;
  public static final int HANGUL_COUNT = 11172;
  private static final int OPTIONS_NX_MASK = 31;
  private static final int OPTIONS_UNICODE_MASK = 224;
  private static final int OPTIONS_SETS_MASK = 255;
  private static final int OPTIONS_UNICODE_SHIFT = 5;
  static final class NormTrieImpl
    implements Trie.DataManipulate
  {
    static IntTrie normTrie = null;
    


    NormTrieImpl() {}
    


    public int getFoldingOffset(int value)
    {
      return 2048 + (value >> 11 & 0x7FE0);
    }
  }
  
  static final class FCDTrieImpl
    implements Trie.DataManipulate
  {
    static CharTrie fcdTrie = null;
    


    FCDTrieImpl() {}
    


    public int getFoldingOffset(int value)
    {
      return value;
    }
  }
  
  static final class AuxTrieImpl implements Trie.DataManipulate {
    static CharTrie auxTrie = null;
    


    AuxTrieImpl() {}
    


    public int getFoldingOffset(int value)
    {
      return (value & 0x3FF) << 5;
    }
  }
  















































  public static int getFromIndexesArr(int index)
  {
    return indexes[index];
  }
  





  private NormalizerImpl()
    throws java.io.IOException
  {
    if (!isDataLoaded)
    {

      java.io.InputStream i = getClass().getResourceAsStream("data/unorm.icu");
      java.io.BufferedInputStream b = new java.io.BufferedInputStream(i, 25000);
      NormalizerDataReader reader = new NormalizerDataReader(b);
      

      indexes = reader.readIndexes(32);
      
      byte[] normBytes = new byte[indexes[0]];
      
      int combiningTableTop = indexes[2];
      combiningTable = new char[combiningTableTop];
      
      int extraDataTop = indexes[1];
      extraData = new char[extraDataTop];
      
      byte[] fcdBytes = new byte[indexes[10]];
      byte[] auxBytes = new byte[indexes[11]];
      canonStartSets = new Object['䀀'];
      
      fcdTrieImpl = new FCDTrieImpl();
      normTrieImpl = new NormTrieImpl();
      auxTrieImpl = new AuxTrieImpl();
      


      reader.read(normBytes, fcdBytes, auxBytes, extraData, combiningTable, canonStartSets);
      

      NormTrieImpl.normTrie = new IntTrie(new java.io.ByteArrayInputStream(normBytes), normTrieImpl);
      FCDTrieImpl.fcdTrie = new CharTrie(new java.io.ByteArrayInputStream(fcdBytes), fcdTrieImpl);
      AuxTrieImpl.auxTrie = new CharTrie(new java.io.ByteArrayInputStream(auxBytes), auxTrieImpl);
      


      isDataLoaded = true;
      

      byte[] formatVersion = reader.getDataFormatVersion();
      
      isFormatVersion_2_1 = (formatVersion[0] > 2) || ((formatVersion[0] == 2) && (formatVersion[1] >= 1));
      


      isFormatVersion_2_2 = (formatVersion[0] > 2) || ((formatVersion[0] == 2) && (formatVersion[1] >= 2));
      



      b.close();
      i.close();
    }
  }
  














  private static boolean isHangulWithoutJamoT(char c)
  {
    c = (char)(c - 44032);
    return (c < '⮤') && (c % '\034' == 0);
  }
  


  private static boolean isNorm32Regular(long norm32)
  {
    return norm32 < 4227858432L;
  }
  
  private static boolean isNorm32LeadSurrogate(long norm32)
  {
    return (4227858432L <= norm32) && (norm32 < 4293918720L);
  }
  
  private static boolean isNorm32HangulOrJamo(long norm32)
  {
    return norm32 >= 4293918720L;
  }
  




  private static boolean isHangulJamoNorm32HangulOrJamoL(long norm32)
  {
    return norm32 < 4294049792L;
  }
  




  private static boolean isJamoVTNorm32JamoV(long norm32)
  {
    return norm32 < 4294115328L;
  }
  

  public static long getNorm32(char c)
  {
    return 0xFFFFFFFF & NormTrieImpl.normTrie.getLeadValue(c);
  }
  




  public static long getNorm32FromSurrogatePair(long norm32, char c2)
  {
    return 0xFFFFFFFF & NormTrieImpl.normTrie.getTrailValue((int)norm32, c2);
  }
  
  private static long getNorm32(int c)
  {
    return 0xFFFFFFFF & NormTrieImpl.normTrie.getCodePointValue(c);
  }
  
  private static long getNorm32(int c, int mask) {
    long norm32 = getNorm32(UTF16.getLeadSurrogate(c));
    if (((norm32 & mask) > 0L) && (isNorm32LeadSurrogate(norm32)))
    {
      norm32 = getNorm32FromSurrogatePair(norm32, UTF16.getTrailSurrogate(c));
    }
    return norm32;
  }
  






  private static long getNorm32(char[] p, int start, int mask)
  {
    long norm32 = getNorm32(p[start]);
    if (((norm32 & mask) > 0L) && (isNorm32LeadSurrogate(norm32)))
    {
      norm32 = getNorm32FromSurrogatePair(norm32, p[(start + 1)]);
    }
    return norm32;
  }
  
  public static char getFCD16(char c) {
    return FCDTrieImpl.fcdTrie.getLeadValue(c);
  }
  


  public static char getFCD16FromSurrogatePair(char fcd16, char c2)
  {
    return FCDTrieImpl.fcdTrie.getTrailValue(fcd16, c2);
  }
  
  public static int getFCD16(int c) { return FCDTrieImpl.fcdTrie.getCodePointValue(c); }
  


  private static int getExtraDataIndex(long norm32) { return (int)(norm32 >> 16); }
  
  private static final class DecomposeArgs { private DecomposeArgs() {}
    DecomposeArgs(NormalizerImpl.1 x0) { this(); }
    


    int cc;
    
    int trailCC;
    
    int length;
  }
  

  private static int decompose(long norm32, int qcMask, DecomposeArgs args)
  {
    int p = getExtraDataIndex(norm32);
    length = extraData[(p++)];
    
    if (((norm32 & qcMask & 0x8) != 0L) && (length >= 256))
    {
      p += (length >> 7 & 0x1) + (length & 0x7F);
      length >>= 8;
    }
    
    if ((length & 0x80) > 0)
    {
      char bothCCs = extraData[(p++)];
      cc = (0xFF & bothCCs >> '\b');
      trailCC = (0xFF & bothCCs);
    }
    else {
      cc = (args.trailCC = 0);
    }
    
    length &= 0x7F;
    return p;
  }
  






  private static int decompose(long norm32, DecomposeArgs args)
  {
    int p = getExtraDataIndex(norm32);
    length = extraData[(p++)];
    
    if ((length & 0x80) > 0)
    {
      char bothCCs = extraData[(p++)];
      cc = (0xFF & bothCCs >> '\b');
      trailCC = (0xFF & bothCCs);
    }
    else {
      cc = (args.trailCC = 0);
    }
    
    length &= 0x7F;
    return p; }
  
  private static final class NextCCArgs { private NextCCArgs() {}
    
    NextCCArgs(NormalizerImpl.1 x0) { this(); }
    

    char[] source;
    
    int next;
    
    int limit;
    
    char c;
    
    char c2;
  }
  
  private static int getNextCC(NextCCArgs args)
  {
    c = source[(next++)];
    
    long norm32 = getNorm32(c);
    if ((norm32 & 0xFF00) == 0L) {
      c2 = '\000';
      return 0;
    }
    if (!isNorm32LeadSurrogate(norm32)) {
      c2 = '\000';

    }
    else if ((next != limit) && (UTF16.isTrailSurrogate(args.c2 = source[next])))
    {
      next += 1;
      norm32 = getNorm32FromSurrogatePair(norm32, c2);
    } else {
      c2 = '\000';
      return 0;
    }
    

    return (int)(0xFF & norm32 >> 8); }
  
  private static final class PrevArgs { private PrevArgs() {}
    
    PrevArgs(NormalizerImpl.1 x0) { this(); }
    


    char[] src;
    

    int start;
    
    int current;
    
    char c;
    
    char c2;
  }
  

  private static long getPrevNorm32(PrevArgs args, int minC, int mask)
  {
    c = src[(--current)];
    c2 = '\000';
    



    if (c < minC)
      return 0L;
    if (!UTF16.isSurrogate(c))
      return getNorm32(c);
    if (UTF16.isLeadSurrogate(c))
    {
      return 0L; }
    if ((current != start) && (UTF16.isLeadSurrogate(args.c2 = src[(current - 1)])))
    {
      current -= 1;
      long norm32 = getNorm32(c2);
      
      if ((norm32 & mask) == 0L)
      {


        return 0L;
      }
      
      return getNorm32FromSurrogatePair(norm32, c);
    }
    

    c2 = '\000';
    return 0L;
  }
  





  private static int getPrevCC(PrevArgs args)
  {
    return (int)(0xFF & getPrevNorm32(args, 768, 65280) >> 8);
  }
  






  public static boolean isNFDSafe(long norm32, int ccOrQCMask, int decompQCMask)
  {
    if ((norm32 & ccOrQCMask) == 0L) {
      return true;
    }
    

    if ((isNorm32Regular(norm32)) && ((norm32 & decompQCMask) != 0L)) {
      DecomposeArgs args = new DecomposeArgs(null);
      
      decompose(norm32, decompQCMask, args);
      return cc == 0;
    }
    
    return (norm32 & 0xFF00) == 0L;
  }
  






  public static boolean isTrueStarter(long norm32, int ccOrQCMask, int decompQCMask)
  {
    if ((norm32 & ccOrQCMask) == 0L) {
      return true;
    }
    

    if ((norm32 & decompQCMask) != 0L)
    {
      DecomposeArgs args = new DecomposeArgs(null);
      
      int p = decompose(norm32, decompQCMask, args);
      
      if (cc == 0) {
        int qcMask = ccOrQCMask & 0x3F;
        

        if ((getNorm32(extraData, p, qcMask) & qcMask) == 0L)
        {
          return true;
        }
      }
    }
    return false;
  }
  






















  private static int insertOrdered(char[] source, int start, int current, int p, char c, char c2, int cc)
  {
    int trailCC = cc;
    
    if ((start < current) && (cc != 0)) {
      int back;
      int preBack = back = current;
      PrevArgs prevArgs = new PrevArgs(null);
      current = current;
      start = start;
      src = source;
      
      int prevCC = getPrevCC(prevArgs);
      preBack = current;
      
      if (cc < prevCC)
      {
        trailCC = prevCC;
        back = preBack;
        while (start < preBack) {
          prevCC = getPrevCC(prevArgs);
          preBack = current;
          if (cc >= prevCC) {
            break;
          }
          back = preBack;
        }
        








        int r = p;
        do {
          source[(--r)] = source[(--current)];
        } while (back != current);
      }
    }
    

    source[current] = c;
    if (c2 != 0) {
      source[(current + 1)] = c2;
    }
    

    return trailCC;
  }
  





























  private static int mergeOrdered(char[] source, int start, int current, char[] data, int next, int limit, boolean isOrdered)
  {
    int trailCC = 0;
    

    boolean adjacent = current == next;
    NextCCArgs ncArgs = new NextCCArgs(null);
    source = data;
    next = next;
    limit = limit;
    
    if (start == current) { if (isOrdered) {}
    } else {
      while (next < limit) {
        int cc = getNextCC(ncArgs);
        if (cc == 0)
        {
          trailCC = 0;
          if (adjacent) {
            current = next;
          } else {
            data[(current++)] = c;
            if (c2 != 0) {
              data[(current++)] = c2;
            }
          }
          if (isOrdered) {
            break;
          }
          start = current;
        }
        else {
          int r = current + (c2 == 0 ? 1 : 2);
          trailCC = insertOrdered(source, start, current, r, c, c2, cc);
          
          current = r;
        }
      }
    }
    
    if (next == limit)
    {
      return trailCC;
    }
    if (!adjacent)
    {
      do {
        source[(current++)] = data[(next++)];
      } while (next != limit);
      limit = current;
    }
    PrevArgs prevArgs = new PrevArgs(null);
    src = data;
    start = start;
    current = limit;
    return getPrevCC(prevArgs);
  }
  





  private static int mergeOrdered(char[] source, int start, int current, char[] data, int next, int limit)
  {
    return mergeOrdered(source, start, current, data, next, limit, true);
  }
  




  public static boolean checkFCD(char[] src, int srcStart, int srcLimit, UnicodeSet nx)
  {
    int prevCC = 0;
    int i = srcStart;int length = srcLimit;
    
    for (;;)
    {
      if (i == length)
        return true;
      char c; if ((c = src[(i++)]) < '̀') {
        prevCC = -c; } else { char fcd16;
        if ((fcd16 = getFCD16(c)) == 0) {
          prevCC = 0;
        }
        else
        {
          char c2;
          

          if (UTF16.isLeadSurrogate(c))
          {
            if ((i != length) && (UTF16.isTrailSurrogate(c2 = src[i]))) {
              i++;
              fcd16 = getFCD16FromSurrogatePair(fcd16, c2);
            } else {
              c2 = '\000';
              fcd16 = '\000';
            }
          } else {
            c2 = '\000';
          }
          
          if (nx_contains(nx, c, c2)) {
            prevCC = 0;




          }
          else
          {




            int cc = fcd16 >> '\b';
            if (cc != 0) {
              if (prevCC < 0)
              {


                if (!nx_contains(nx, -prevCC)) {
                  prevCC = FCDTrieImpl.fcdTrie.getBMPValue((char)-prevCC) & 0xFF;
                }
                else
                {
                  prevCC = 0;
                }
              }
              

              if (cc < prevCC) {
                return false;
              }
            }
            prevCC = fcd16 & 0xFF;
          }
        }
      }
    }
  }
  









  public static com.ibm.icu.text.Normalizer.QuickCheckResult quickCheck(char[] src, int srcStart, int srcLimit, int minNoMaybe, int qcMask, boolean allowMaybe, UnicodeSet nx)
  {
    ComposePartArgs args = new ComposePartArgs(null);
    
    int start = srcStart;
    
    if (!isDataLoaded) {
      return Normalizer.MAYBE;
    }
    
    int ccOrQCMask = 0xFF00 | qcMask;
    com.ibm.icu.text.Normalizer.QuickCheckResult result = Normalizer.YES;
    char prevCC = '\000';
    
    for (;;)
    {
      if (srcStart == srcLimit)
        return result;
      char c; long norm32; if (((c = src[(srcStart++)]) < minNoMaybe) || (((norm32 = getNorm32(c)) & ccOrQCMask) == 0L))
      {


        prevCC = '\000';
      }
      else
      {
        char c2;
        if (isNorm32LeadSurrogate(norm32))
        {
          if ((srcStart != srcLimit) && (UTF16.isTrailSurrogate(c2 = src[srcStart]))) {
            srcStart++;
            norm32 = getNorm32FromSurrogatePair(norm32, c2);
          } else {
            norm32 = 0L;
            c2 = '\000';
          }
        } else {
          c2 = '\000';
        }
        if (nx_contains(nx, c, c2))
        {
          norm32 = 0L;
        }
        

        char cc = (char)(int)(norm32 >> 8 & 0xFF);
        if ((cc != 0) && (cc < prevCC)) {
          return Normalizer.NO;
        }
        prevCC = cc;
        

        long qcNorm32 = norm32 & qcMask;
        if ((qcNorm32 & 0xF) >= 1L) {
          result = Normalizer.NO;
          break; }
        if (qcNorm32 != 0L)
        {
          if (allowMaybe) {
            result = Normalizer.MAYBE;


          }
          else
          {

            int decompQCMask = qcMask << 2 & 0xF;
            



            int prevStarter = srcStart - 1;
            if (UTF16.isTrailSurrogate(src[prevStarter]))
            {

              prevStarter--;
            }
            prevStarter = findPreviousStarter(src, start, prevStarter, ccOrQCMask, decompQCMask, (char)minNoMaybe);
            




            srcStart = findNextStarter(src, srcStart, srcLimit, qcMask, decompQCMask, (char)minNoMaybe);
            


            prevCC = prevCC;
            

            char[] buffer = composePart(args, prevStarter, src, srcStart, srcLimit, qcMask, nx);
            

            if (0 != strCompare(buffer, 0, length, src, prevStarter, srcStart - prevStarter, false)) {
              result = Normalizer.NO;
              break;
            }
          }
        }
      }
    }
    
    return result;
  }
  








  public static int getDecomposition(int c, boolean compat, char[] dest, int destStart, int destCapacity)
  {
    if ((0xFFFFFFFF & c) <= 1114111L)
    {
      int minNoMaybe;
      

      int qcMask;
      
      if (!compat) {
        minNoMaybe = indexes[8];
        qcMask = 4;
      } else {
        minNoMaybe = indexes[9];
        qcMask = 8;
      }
      
      if (c < minNoMaybe)
      {
        if (destCapacity > 0) {
          dest[0] = ((char)c);
        }
        return -1;
      }
      

      long norm32 = getNorm32(c);
      if ((norm32 & qcMask) == 0L)
      {
        if (c <= 65535) {
          if (destCapacity > 0) {
            dest[0] = ((char)c);
          }
          return -1;
        }
        if (destCapacity >= 2) {
          dest[0] = UTF16.getLeadSurrogate(c);
          dest[1] = UTF16.getTrailSurrogate(c);
        }
        return -2;
      }
      if (isNorm32HangulOrJamo(norm32))
      {


        c -= 44032;
        
        char c2 = (char)(c % 28);
        c /= 28;
        int length; if (c2 > 0) {
          if (destCapacity >= 3) {
            dest[2] = ((char)('ᆧ' + c2));
          }
          length = 3;
        } else {
          length = 2;
        }
        
        if (destCapacity >= 2) {
          dest[1] = ((char)(4449 + c % 21));
          dest[0] = ((char)(4352 + c / 21));
        }
        return length;
      }
      



      DecomposeArgs args = new DecomposeArgs(null);
      
      int p = decompose(norm32, qcMask, args);
      if (length <= destCapacity) {
        int limit = p + length;
        do {
          dest[(destStart++)] = extraData[(p++)];
        } while (p < limit);
      }
      return length;
    }
    
    return 0;
  }
  





  public static int decompose(char[] src, int srcIndex, int srcLimit, char[] dest, int destIndex, int destLimit, boolean compat, int[] outTrailCC, UnicodeSet nx)
  {
    char[] buffer = new char[3];
    

    char minNoMaybe;
    

    int qcMask;
    

    if (!compat) {
      minNoMaybe = (char)indexes[8];
      qcMask = 4;
    } else {
      minNoMaybe = (char)indexes[9];
      qcMask = 8;
    }
    

    int ccOrQCMask = 0xFF00 | qcMask;
    int reorderStartIndex = 0;
    int prevCC = 0;
    long norm32 = 0L;
    char c = '\000';
    int pStart = 0;
    int trailCC;
    int cc = trailCC = -1;
    


    for (;;)
    {
      int prevSrc = srcIndex;
      
      while ((srcIndex != srcLimit) && (((c = src[srcIndex]) < minNoMaybe) || (((norm32 = getNorm32(c)) & ccOrQCMask) == 0L)))
      {
        prevCC = 0;
        srcIndex++;
      }
      
      int length;
      if (srcIndex != prevSrc) {
        length = srcIndex - prevSrc;
        if (destIndex + length <= destLimit) {
          System.arraycopy(src, prevSrc, dest, destIndex, length);
        }
        
        destIndex += length;
        reorderStartIndex = destIndex;
      }
      

      if (srcIndex == srcLimit) {
        break;
      }
      

      srcIndex++;
      





      char c2;
      




      char[] p;
      




      if (isNorm32HangulOrJamo(norm32)) {
        if (nx_contains(nx, c)) {
          c2 = '\000';
          p = null;
          length = 1;
        }
        else {
          p = buffer;
          pStart = 0;
          cc = trailCC = 0;
          
          c = (char)(c - 44032);
          
          c2 = (char)(c % '\034');
          c = (char)(c / '\034');
          if (c2 > 0) {
            buffer[2] = ((char)('ᆧ' + c2));
            length = 3;
          } else {
            length = 2;
          }
          
          buffer[1] = ((char)(4449 + c % '\025'));
          buffer[0] = ((char)(4352 + c / '\025'));
        }
      } else {
        if (isNorm32Regular(norm32)) {
          c2 = '\000';
          length = 1;

        }
        else if ((srcIndex != srcLimit) && (UTF16.isTrailSurrogate(c2 = src[srcIndex])))
        {
          srcIndex++;
          length = 2;
          norm32 = getNorm32FromSurrogatePair(norm32, c2);
        } else {
          c2 = '\000';
          length = 1;
          norm32 = 0L;
        }
        


        if (nx_contains(nx, c, c2))
        {
          cc = trailCC = 0;
          p = null;
        } else if ((norm32 & qcMask) == 0L)
        {
          cc = trailCC = (int)(0xFF & norm32 >> 8);
          p = null;
          pStart = -1;
        } else {
          DecomposeArgs arg = new DecomposeArgs(null);
          


          pStart = decompose(norm32, qcMask, arg);
          p = extraData;
          length = length;
          cc = cc;
          trailCC = trailCC;
          if (length == 1)
          {
            c = p[pStart];
            c2 = '\000';
            p = null;
            pStart = -1;
          }
        }
      }
      



      if (destIndex + length <= destLimit) {
        int reorderSplit = destIndex;
        if (p == null)
        {
          if ((cc != 0) && (cc < prevCC))
          {


            destIndex += length;
            trailCC = insertOrdered(dest, reorderStartIndex, reorderSplit, destIndex, c, c2, cc);
          }
          else
          {
            dest[(destIndex++)] = c;
            if (c2 != 0) {
              dest[(destIndex++)] = c2;
            }
            
          }
          

        }
        else if ((cc != 0) && (cc < prevCC))
        {


          destIndex += length;
          trailCC = mergeOrdered(dest, reorderStartIndex, reorderSplit, p, pStart, pStart + length);
        }
        else
        {
          do {
            dest[(destIndex++)] = p[(pStart++)];
            length--; } while (length > 0);
        }
        
      }
      else
      {
        destIndex += length;
      }
      
      prevCC = trailCC;
      if (prevCC == 0) {
        reorderStartIndex = destIndex;
      }
    }
    
    outTrailCC[0] = prevCC;
    
    return destIndex; }
  
  private static final class NextCombiningArgs { private NextCombiningArgs() {}
    
    NextCombiningArgs(NormalizerImpl.1 x0) { this(); }
    

    char[] source;
    
    int start;
    
    char c;
    
    char c2;
    
    int combiningIndex;
    char cc;
  }
  
  private static int getNextCombining(NextCombiningArgs args, int limit, UnicodeSet nx)
  {
    c = source[(start++)];
    long norm32 = getNorm32(c);
    

    c2 = '\000';
    combiningIndex = 0;
    cc = '\000';
    
    if ((norm32 & 0xFFC0) == 0L) {
      return 0;
    }
    if (!isNorm32Regular(norm32))
    {
      if (isNorm32HangulOrJamo(norm32))
      {
        combiningIndex = ((int)(0xFFFFFFFF & (0xFFF0 | norm32 >> 16)));
        
        return (int)(norm32 & 0xC0);
      }
      
      if ((start != limit) && (UTF16.isTrailSurrogate(args.c2 = source[start])))
      {
        start += 1;
        norm32 = getNorm32FromSurrogatePair(norm32, c2);
      } else {
        c2 = '\000';
        return 0;
      }
    }
    
    if (nx_contains(nx, c, c2)) {
      return 0;
    }
    
    cc = ((char)(byte)(int)(norm32 >> 8));
    
    int combineFlags = (int)(norm32 & 0xC0);
    if (combineFlags != 0) {
      int index = getExtraDataIndex(norm32);
      combiningIndex = (index > 0 ? extraData[(index - 1)] : 0);
    }
    
    return combineFlags;
  }
  










  private static int getCombiningIndexFromStarter(char c, char c2)
  {
    long norm32 = getNorm32(c);
    if (c2 != 0) {
      norm32 = getNorm32FromSurrogatePair(norm32, c2);
    }
    return extraData[(getExtraDataIndex(norm32) - 1)];
  }
  





















  private static int combine(char[] table, int tableStart, int combineBackIndex, int[] outValues)
  {
    if (outValues.length < 2) {
      throw new IllegalArgumentException();
    }
    int key;
    for (;;)
    {
      key = table[(tableStart++)];
      if (key >= combineBackIndex) {
        break;
      }
      tableStart += ((table[tableStart] & 0x8000) != 0 ? 2 : 1);
    }
    

    if ((key & 0x7FFF) == combineBackIndex)
    {
      int value = table[tableStart];
      

      key = (int)(0xFFFFFFFF & (value & 0x2000) + 1);
      

      int value2;
      
      if ((value & 0x8000) != 0) {
        if ((value & 0x4000) != 0)
        {
          value = (int)(0xFFFFFFFF & (value & 0x3FF | 0xD800));
          value2 = table[(tableStart + 1)];
        }
        else {
          value = table[(tableStart + 1)];
          value2 = 0;
        }
      }
      else {
        value &= 0x1FFF;
        value2 = 0;
      }
      outValues[0] = value;
      outValues[1] = value2;
      return key;
    }
    
    return 0;
  }
  
  private static final class RecomposeArgs { private RecomposeArgs() {}
    
    RecomposeArgs(NormalizerImpl.1 x0) { this(); }
    




    char[] source;
    



    int start;
    


    int limit;
  }
  



  private static char recompose(RecomposeArgs args, UnicodeSet nx)
  {
    int value = 0;int value2 = 0;
    


    int[] outValues = new int[2];
    int starter = -1;
    int combineFwdIndex = 0;
    boolean starterIsSupplementary = false;
    int prevCC = 0;
    
    NextCombiningArgs ncArg = new NextCombiningArgs(null);
    source = source;
    
    cc = '\000';
    c2 = '\000';
    for (;;)
    {
      start = start;
      int combineFlags = getNextCombining(ncArg, limit, nx);
      int combineBackIndex = combiningIndex;
      start = start;
      
      if (((combineFlags & 0x80) != 0) && (starter != -1)) { int remove;
        int q; int r; if ((combineBackIndex & 0x8000) != 0)
        {


          remove = -1;
          c2 = source[starter];
          if (combineBackIndex == 65522)
          {


            c2 = ((char)(c2 - 'ᄀ'));
            if (c2 < '\023') {
              remove = start - 1;
              c = ((char)(44032 + (c2 * '\025' + (c - 'ᅡ')) * 28));
              
              if ((start != limit) && ((ncArg.c2 = (char)(source[start] - 'ᆧ')) < '\034'))
              {

                start += 1; NextCombiningArgs 
                  tmp248_246 = ncArg;248246c = ((char)(248246c + c2));
              }
              if (!nx_contains(nx, c)) {
                source[starter] = c;
              }
              else {
                if (!isHangulWithoutJamoT(c)) {
                  start -= 1;
                }
                
                remove = start;
              }
            }
          }
          

          if (remove != -1)
          {
            q = remove;
            r = start;
            while (r < limit) {
              source[(q++)] = source[(r++)];
            }
            start = remove;
            limit = q;
          }
          
          c2 = '\000';
        }
        else
        {
          int result;
          





          if (((combineFwdIndex & 0x8000) == 0) && ((prevCC < cc) || (prevCC == 0)) && (0 != (result = combine(combiningTable, combineFwdIndex, combineBackIndex, outValues))) && (!nx_contains(nx, (char)value, (char)value2)))
          {









            value = outValues[0];
            value2 = outValues[1];
            


            remove = c2 == 0 ? start - 1 : start - 2;
            

            source[starter] = ((char)value);
            if (starterIsSupplementary) {
              if (value2 != 0)
              {
                source[(starter + 1)] = ((char)value2);
              }
              else
              {
                starterIsSupplementary = false;
                q = starter + 1;
                r = q + 1;
                while (r < remove) {
                  source[(q++)] = source[(r++)];
                }
                remove--;
              }
            } else if (value2 != 0)
            {

              starterIsSupplementary = true;
              
              starter++;
              q = remove;
              remove++;r = remove;
              while (starter < q) {
                source[(--r)] = source[(--q)];
              }
              source[starter] = ((char)value2);
              starter--;
            }
            



            if (remove < start) {
              q = remove;
              r = start;
              while (r < limit) {
                source[(q++)] = source[(r++)];
              }
              start = remove;
              limit = q;
            }
            



            if (start == limit) {
              return (char)prevCC;
            }
            

            if (result > 1) {
              combineFwdIndex = getCombiningIndexFromStarter((char)value, (char)value2); continue;
            }
            
            starter = -1;
            



            continue;
          }
        }
      }
      
      prevCC = cc;
      if (start == limit) {
        return (char)prevCC;
      }
      

      if (cc == 0)
      {
        if ((combineFlags & 0x40) != 0)
        {
          if (c2 == 0) {
            starterIsSupplementary = false;
            starter = start - 1;
          } else {
            starterIsSupplementary = false;
            starter = start - 2;
          }
          combineFwdIndex = combineBackIndex;
        }
        else {
          starter = -1;
        }
      }
    }
  }
  





  private static int findPreviousStarter(char[] src, int srcStart, int current, int ccOrQCMask, int decompQCMask, char minNoMaybe)
  {
    PrevArgs args = new PrevArgs(null);
    src = src;
    start = srcStart;
    current = current;
    
    while (start < current) {
      long norm32 = getPrevNorm32(args, minNoMaybe, ccOrQCMask | decompQCMask);
      if (isTrueStarter(norm32, ccOrQCMask, decompQCMask)) {
        break;
      }
    }
    return current;
  }
  










  private static int findNextStarter(char[] src, int start, int limit, int qcMask, int decompQCMask, char minNoMaybe)
  {
    int ccOrQCMask = 0xFF00 | qcMask;
    
    DecomposeArgs decompArgs = new DecomposeArgs(null);
    

    while (start != limit)
    {

      char c = src[start];
      if (c < minNoMaybe) {
        break;
      }
      
      long norm32 = getNorm32(c);
      if ((norm32 & ccOrQCMask) == 0L) {
        break;
      }
      char c2;
      if (isNorm32LeadSurrogate(norm32))
      {
        if ((start + 1 == limit) || (!UTF16.isTrailSurrogate(c2 = src[(start + 1)]))) {
          break;
        }
        

        norm32 = getNorm32FromSurrogatePair(norm32, c2);
        
        if ((norm32 & ccOrQCMask) == 0L) {
          break;
        }
      } else {
        c2 = '\000';
      }
      

      if ((norm32 & decompQCMask) != 0L)
      {

        int p = decompose(norm32, decompQCMask, decompArgs);
        


        if ((cc == 0) && ((getNorm32(extraData, p, qcMask) & qcMask) == 0L)) {
          break;
        }
      }
      
      start += (c2 == 0 ? 1 : 2);
    }
    
    return start; }
  
  private static final class ComposePartArgs { private ComposePartArgs() {}
    
    ComposePartArgs(NormalizerImpl.1 x0) { this(); }
    


    int prevCC;
    
    int length;
  }
  

  private static char[] composePart(ComposePartArgs args, int prevStarter, char[] src, int start, int limit, int qcMask, UnicodeSet nx)
  {
    boolean compat = (qcMask & 0x22) != 0;
    

    int[] outTrailCC = new int[1];
    char[] buffer = new char[(limit - prevStarter) * 20];
    for (;;)
    {
      length = decompose(src, prevStarter, start, buffer, 0, buffer.length, compat, outTrailCC, nx);
      

      if (length <= buffer.length) {
        break;
      }
      buffer = new char[length];
    }
    


    int recomposeLimit = length;
    
    if (length >= 2) {
      RecomposeArgs rcArgs = new RecomposeArgs(null);
      source = buffer;
      start = 0;
      limit = recomposeLimit;
      prevCC = recompose(rcArgs, nx);
      recomposeLimit = limit;
    }
    

    length = recomposeLimit;
    return buffer;
  }
  




  private static boolean composeHangul(char prev, char c, long norm32, char[] src, int[] srcIndex, int limit, boolean compat, char[] dest, int destIndex, UnicodeSet nx)
  {
    int start = srcIndex[0];
    if (isJamoVTNorm32JamoV(norm32))
    {

      prev = (char)(prev - 'ᄀ');
      if (prev < '\023') {
        c = (char)(44032 + (prev * '\025' + (c - 'ᅡ')) * 28);
        



        if (start != limit)
        {

          char next = src[start];
          char t; if ((t = (char)(next - 'ᆧ')) < '\034')
          {
            start++;
            c = (char)(c + t);
          } else if (compat)
          {

            norm32 = getNorm32(next);
            if ((isNorm32Regular(norm32)) && ((norm32 & 0x8) != 0L))
            {
              DecomposeArgs dcArgs = new DecomposeArgs(null);
              int p = decompose(norm32, 8, dcArgs);
              if ((length == 1) && ((t = (char)(extraData[p] - 'ᆧ')) < '\034'))
              {


                start++;
                c = (char)(c + t);
              }
            }
          }
        }
        if (nx_contains(nx, c)) {
          if (!isHangulWithoutJamoT(c)) {
            start--;
          }
          return false;
        }
        dest[destIndex] = c;
        srcIndex[0] = start;
        return true;
      }
    } else if (isHangulWithoutJamoT(prev))
    {

      c = (char)(prev + (c - 'ᆧ'));
      if (nx_contains(nx, c)) {
        return false;
      }
      dest[destIndex] = c;
      srcIndex[0] = start;
      return true;
    }
    return false;
  }
  













  public static int compose(char[] src, int srcIndex, int srcLimit, char[] dest, int destIndex, int destLimit, boolean compat, UnicodeSet nx)
  {
    int[] ioIndex = new int[1];
    char minNoMaybe;
    int qcMask; if (!compat) {
      minNoMaybe = (char)indexes[6];
      qcMask = 17;
    } else {
      minNoMaybe = (char)indexes[7];
      qcMask = 34;
    }
    























    int prevStarter = srcIndex;
    
    int ccOrQCMask = 0xFF00 | qcMask;
    int reorderStartIndex = 0;
    int prevCC = 0;
    

    long norm32 = 0L;
    char c = '\000';
    

    for (;;)
    {
      int prevSrc = srcIndex;
      
      while ((srcIndex != srcLimit) && (((c = src[srcIndex]) < minNoMaybe) || (((norm32 = getNorm32(c)) & ccOrQCMask) == 0L)))
      {
        prevCC = 0;
        srcIndex++;
      }
      
      int length;
      
      if (srcIndex != prevSrc) {
        length = srcIndex - prevSrc;
        if (destIndex + length <= destLimit) {
          System.arraycopy(src, prevSrc, dest, destIndex, length);
        }
        destIndex += length;
        reorderStartIndex = destIndex;
        


        prevStarter = srcIndex - 1;
        if ((UTF16.isTrailSurrogate(src[prevStarter])) && (prevSrc < prevStarter) && (UTF16.isLeadSurrogate(src[(prevStarter - 1)])))
        {

          prevStarter--;
        }
        
        prevSrc = srcIndex;
      }
      

      if (srcIndex == srcLimit) {
        break;
      }
      

      srcIndex++;
      









      int cc;
      









      char c2;
      








      if (isNorm32HangulOrJamo(norm32))
      {





        prevCC = cc = 0;
        reorderStartIndex = destIndex;
        ioIndex[0] = srcIndex;
        if (destIndex > 0) { if (composeHangul(src[(prevSrc - 1)], c, norm32, src, ioIndex, srcLimit, compat, dest, destIndex <= destLimit ? destIndex - 1 : 0, nx))
          {





            srcIndex = ioIndex[0];
            prevStarter = srcIndex;
            continue;
          }
        }
        srcIndex = ioIndex[0];
        


        c2 = '\000';
        length = 1;
        prevStarter = prevSrc;
      } else {
        if (isNorm32Regular(norm32)) {
          c2 = '\000';
          length = 1;

        }
        else if ((srcIndex != srcLimit) && (UTF16.isTrailSurrogate(c2 = src[srcIndex])))
        {
          srcIndex++;
          length = 2;
          norm32 = getNorm32FromSurrogatePair(norm32, c2);
        }
        else {
          c2 = '\000';
          length = 1;
          norm32 = 0L;
        }
        
        ComposePartArgs args = new ComposePartArgs(null);
        

        if (nx_contains(nx, c, c2))
        {
          cc = 0;
        } else if ((norm32 & qcMask) == 0L) {
          cc = (int)(0xFF & norm32 >> 8);







        }
        else
        {






          int decompQCMask = qcMask << 2 & 0xF;
          




          if (isTrueStarter(norm32, 0xFF00 | qcMask, decompQCMask)) {
            prevStarter = prevSrc;
          }
          else {
            destIndex -= prevSrc - prevStarter;
          }
          

          srcIndex = findNextStarter(src, srcIndex, srcLimit, qcMask, decompQCMask, minNoMaybe);
          

          prevCC = prevCC;
          
          length = length;
          char[] p = composePart(args, prevStarter, src, srcIndex, srcLimit, qcMask, nx);
          
          if (p == null) {
            break;
          }
          

          prevCC = prevCC;
          length = length;
          


          if (destIndex + length <= destLimit) {
            int i = 0;
            while (i < length) {
              dest[(destIndex++)] = p[(i++)];
              length--;
            }
          }
          else
          {
            destIndex += length;
          }
          
          prevStarter = srcIndex;
          continue;
        }
      }
      

      if (destIndex + length <= destLimit) {
        if ((cc != 0) && (cc < prevCC))
        {

          int reorderSplit = destIndex;
          destIndex += length;
          prevCC = insertOrdered(dest, reorderStartIndex, reorderSplit, destIndex, c, c2, cc);
        }
        else
        {
          dest[(destIndex++)] = c;
          if (c2 != 0) {
            dest[(destIndex++)] = c2;
          }
          prevCC = cc;
        }
      }
      else
      {
        destIndex += length;
        prevCC = cc;
      }
    }
    
    return destIndex;
  }
  
















  private static int findSafeFCD(char[] src, int start, int limit, char fcd16)
  {
    while ((fcd16 & 0xFF) != 0)
    {



      if (start == limit) {
        break;
      }
      char c = src[start];
      

      if ((c < '̀') || ((fcd16 = getFCD16(c)) == 0)) {
        break;
      }
      
      if (!UTF16.isLeadSurrogate(c)) {
        if (fcd16 <= 'ÿ') {
          break;
        }
        start++; } else { char c2;
        if ((start + 1 == limit) || (!UTF16.isTrailSurrogate(c2 = src[(start + 1)]))) {
          break;
        }
        fcd16 = getFCD16FromSurrogatePair(fcd16, c2);
        if (fcd16 <= 'ÿ') {
          break;
        }
        start += 2;
      }
    }
    



    return start;
  }
  



  private static int decomposeFCD(char[] src, int start, int decompLimit, char[] dest, int[] destIndexArr, UnicodeSet nx)
  {
    char[] p = null;
    int pStart = -1;
    




    DecomposeArgs args = new DecomposeArgs(null);
    int destIndex = destIndexArr[0];
    













    int reorderStartIndex = destIndex;
    int prevCC = 0;
    
    while (start < decompLimit) {
      char c = src[(start++)];
      long norm32 = getNorm32(c);
      char c2; if (isNorm32Regular(norm32)) {
        c2 = '\000';
        length = 1;






      }
      else if ((start != decompLimit) && (UTF16.isTrailSurrogate(c2 = src[start]))) {
        start++;
        length = 2;
        norm32 = getNorm32FromSurrogatePair(norm32, c2);
      } else {
        c2 = '\000';
        length = 1;
        norm32 = 0L;
      }
      


      if (nx_contains(nx, c, c2))
      {
        cc = (args.trailCC = 0);
        p = null;
      } else if ((norm32 & 0x4) == 0L)
      {
        cc = (args.trailCC = (int)(0xFF & norm32 >> 8));
        
        p = null;
      }
      else
      {
        pStart = decompose(norm32, args);
        p = extraData;
        if (length == 1)
        {
          c = p[pStart];
          c2 = '\000';
          p = null;
        }
      }
      


      if (destIndex + length <= dest.length) {
        int reorderSplit = destIndex;
        if (p == null)
        {
          if ((cc != 0) && (cc < prevCC))
          {

            destIndex += length;
            trailCC = insertOrdered(dest, reorderStartIndex, reorderSplit, destIndex, c, c2, cc);

          }
          else
          {
            dest[(destIndex++)] = c;
            if (c2 != 0) {
              dest[(destIndex++)] = c2;
            }
            
          }
          
        }
        else if ((cc != 0) && (cc < prevCC))
        {

          destIndex += length;
          trailCC = mergeOrdered(dest, reorderStartIndex, reorderSplit, p, pStart, pStart + length);
        }
        else
        {
          do
          {
            dest[(destIndex++)] = p[(pStart++)];
          } while (--397395length > 0);
        }
        
      }
      else
      {
        destIndex += length;
      }
      
      prevCC = trailCC;
      if (prevCC == 0) {
        reorderStartIndex = destIndex;
      }
    }
    destIndexArr[0] = destIndex;
    return prevCC;
  }
  









  public static int makeFCD(char[] src, int srcStart, int srcLimit, char[] dest, int destStart, int destLimit, UnicodeSet nx)
  {
    int decompStart = srcStart;
    int destIndex = destStart;
    int prevCC = 0;
    char c = '\000';
    int fcd16 = 0;
    int[] destIndexArr = new int[1];
    destIndexArr[0] = destIndex;
    

    for (;;)
    {
      int prevSrc = srcStart;
      

      while (srcStart != srcLimit)
      {
        if ((c = src[srcStart]) < '̀') {
          prevCC = -c;
        } else { if ((fcd16 = getFCD16(c)) != 0) break;
          prevCC = 0;
        }
        

        srcStart++;
      }
      





      int length;
      




      if (srcStart != prevSrc) {
        length = srcStart - prevSrc;
        if (destIndex + length <= destLimit) {
          System.arraycopy(src, prevSrc, dest, destIndex, length);
        }
        destIndex += length;
        prevSrc = srcStart;
        


        if (prevCC < 0)
        {

          if (!nx_contains(nx, -prevCC)) {
            prevCC = getFCD16(-prevCC) & 0xFF;
          } else {
            prevCC = 0;
          }
          




          decompStart = prevSrc - 1;
        }
      }
      







      if (srcStart == srcLimit) {
        break;
      }
      

      if (prevCC == 0) {
        decompStart = prevSrc;
      }
      

      srcStart++;
      
      char c2;
      if (UTF16.isLeadSurrogate(c))
      {
        if ((srcStart != srcLimit) && (UTF16.isTrailSurrogate(c2 = src[srcStart])))
        {
          srcStart++;
          fcd16 = getFCD16FromSurrogatePair((char)fcd16, c2);
        } else {
          c2 = '\000';
          fcd16 = 0;
        }
      } else {
        c2 = '\000';
      }
      

      if (nx_contains(nx, c, c2)) {
        fcd16 = 0;
      }
      
      int cc = fcd16 >> 8;
      if ((cc == 0) || (cc >= prevCC))
      {
        if (cc == 0) {
          decompStart = prevSrc;
        }
        prevCC = fcd16 & 0xFF;
        

        length = c2 == 0 ? 1 : 2;
        if (destIndex + length <= destLimit) {
          dest[(destIndex++)] = c;
          if (c2 != 0) {
            dest[(destIndex++)] = c2;
          }
        } else {
          destIndex += length;
        }
        

      }
      else
      {

        destIndex -= prevSrc - decompStart;
        





        srcStart = findSafeFCD(src, srcStart, srcLimit, (char)fcd16);
        




        destIndexArr[0] = destIndex;
        prevCC = decomposeFCD(src, decompStart, srcStart, dest, destIndexArr, nx);
        
        decompStart = srcStart;
        destIndex = destIndexArr[0];
      }
    }
    
    return destIndex;
  }
  

  public static int getCombiningClass(int c)
  {
    long norm32 = getNorm32(c);
    return (char)(int)(norm32 >> 8 & 0xFF);
  }
  
  public static boolean isFullCompositionExclusion(int c) {
    if (isFormatVersion_2_1) {
      int aux = AuxTrieImpl.auxTrie.getCodePointValue(c);
      return (aux & 0x400) != 0;
    }
    return false;
  }
  
  public static boolean isCanonSafeStart(int c)
  {
    if (isFormatVersion_2_1) {
      int aux = AuxTrieImpl.auxTrie.getCodePointValue(c);
      return (aux & 0x800) == 0;
    }
    return false;
  }
  

  public static boolean getCanonStartSet(int c, USerializedSet fillSet)
  {
    if ((fillSet != null) && (canonStartSets != null))
    {







      int i = 0;
      
      int[] indexes = (int[])canonStartSets[0];
      char[] startSets = (char[])canonStartSets[1];
      char[] table;
      int start; int limit; if (c <= 65535) {
        table = (char[])canonStartSets[2];
        start = 0;
        limit = table.length;
        

        while (start < limit - 2) {
          i = (char)((start + limit) / 4 * 2);
          if (c < table[i]) {
            limit = i;
          } else {
            start = i;
          }
        }
        

        if (c == table[start]) {
          i = table[(start + 1)];
          if ((i & 0xC000) == 16384)
          {

            i &= 0x3FFF;
            return fillSet.getSet(startSets, i - indexes.length);
          }
          

          fillSet.setToOne(i);
          return true;
        }
      }
      else {
        char j = '\000';
        
        table = (char[])canonStartSets[3];
        start = 0;
        limit = table.length;
        
        char high = (char)(c >> 16);
        char low = (char)c;
        

        while (start < limit - 3)
        {
          i = (char)((start + limit) / 6 * 3);
          j = (char)(table[i] & 0x1F);
          int tableVal = table[(i + 1)];
          int lowInt = low;
          if ((high < j) || ((tableVal > lowInt) && (high == j))) {
            limit = i;
          } else {
            start = i;
          }
          




          if (ICUDebug.enabled()) {
            System.err.println("\t\t j = " + Utility.hex(j, 4) + "\t i = " + Utility.hex(i, 4) + "\t high = " + Utility.hex(high) + "\t low = " + Utility.hex(lowInt, 4) + "\t table[i+1]: " + Utility.hex(tableVal, 4));
          }
        }
        







        char h = table[start];
        

        int tableVal1 = table[(start + 1)];
        int lowInt = low;
        
        if ((high == (h & 0x1F)) && (lowInt == tableVal1)) {
          int tableVal2 = table[(start + 2)];
          i = tableVal2;
          if ((h & 0x8000) == 0)
          {
            return fillSet.getSet(startSets, i - indexes.length);
          }
          




          int temp = (h & 0x1F00) << '\b';
          i |= temp;
          fillSet.setToOne(i);
          return true;
        }
      }
    }
    

    return false;
  }
  

  public static int getFC_NFKC_Closure(int c, char[] dest)
  {
    int destCapacity;
    if (dest == null) {
      destCapacity = 0;
    } else {
      destCapacity = dest.length;
    }
    
    int aux = AuxTrieImpl.auxTrie.getCodePointValue(c);
    
    aux &= 0x3FF;
    if (aux != 0)
    {
      int index = aux;
      

      int s = extraData[index];
      int length; if (s < 65280)
      {
        length = 1;
      } else {
        length = s & 0xFF;
        index++;
      }
      if ((0 < length) && (length <= destCapacity)) {
        System.arraycopy(extraData, index, dest, 0, length);
      }
      return length;
    }
    return 0;
  }
  



  public static boolean isNFSkippable(int c, com.ibm.icu.text.Normalizer.Mode mode, long mask)
  {
    mask &= 0xFFFFFFFF;
    


    long norm32 = getNorm32(c);
    
    if ((norm32 & mask) != 0L) {
      return false;
    }
    
    if ((mode == Normalizer.NFD) || (mode == Normalizer.NFKD) || (mode == Normalizer.NONE)) {
      return true;
    }
    


    if ((norm32 & 0x4) == 0L) {
      return true;
    }
    

    if (isNorm32HangulOrJamo(norm32))
    {
      return !isHangulWithoutJamoT((char)c);
    }
    


    if (!isFormatVersion_2_2) {
      return false;
    }
    

    char aux = AuxTrieImpl.auxTrie.getCodePointValue(c);
    return (aux & 0x1000) == 0L;
  }
  














  public static UnicodeSet addPropertyStarts(UnicodeSet set)
  {
    TrieIterator normIter = new TrieIterator(NormTrieImpl.normTrie);
    RangeValueIterator.Element normResult = new RangeValueIterator.Element();
    
    while (normIter.next(normResult)) {
      set.add(start);
    }
    

    TrieIterator fcdIter = new TrieIterator(FCDTrieImpl.fcdTrie);
    RangeValueIterator.Element fcdResult = new RangeValueIterator.Element();
    
    while (fcdIter.next(fcdResult)) {
      set.add(start);
    }
    
    if (isFormatVersion_2_1)
    {
      TrieIterator auxIter = new TrieIterator(AuxTrieImpl.auxTrie);
      RangeValueIterator.Element auxResult = new RangeValueIterator.Element();
      while (auxIter.next(auxResult)) {
        set.add(start);
      }
    }
    
    for (int c = 44032; c < 55204; c += 28) {
      set.add(c);
      set.add(c + 1);
    }
    set.add(55204);
    return set;
  }
  








  public CharTrie getFCDTrie()
  {
    return FCDTrieImpl.fcdTrie;
  }
  















  private static class CmpEquivLevel
  {
    char[] source;
    














    int start;
    













    int s;
    













    int limit;
    














    private CmpEquivLevel() {}
    














    CmpEquivLevel(NormalizerImpl.1 x0)
    {
      this();
    }
  }
  












  private static int decompose(int c, char[] buffer)
  {
    int length = 0;
    long norm32 = 0xFFFFFFFF & NormTrieImpl.normTrie.getCodePointValue(c);
    if ((norm32 & 0x4) != 0L) {
      if (isNorm32HangulOrJamo(norm32))
      {


        c -= 44032;
        
        char c2 = (char)(c % 28);
        c /= 28;
        if (c2 > 0) {
          buffer[2] = ((char)('ᆧ' + c2));
          length = 3;
        } else {
          length = 2;
        }
        buffer[1] = ((char)(4449 + c % 21));
        buffer[0] = ((char)(4352 + c / 21));
        return length;
      }
      
      DecomposeArgs args = new DecomposeArgs(null);
      int index = decompose(norm32, args);
      System.arraycopy(extraData, index, buffer, 0, length);
      return length;
    }
    
    return 0;
  }
  

  private static int foldCase(int c, char[] dest, int destStart, int destLimit, int options)
  {
    String src = UTF16.valueOf(c);
    String foldedStr = com.ibm.icu.lang.UCharacter.foldCase(src, options);
    char[] foldedC = foldedStr.toCharArray();
    for (int i = 0; i < foldedC.length; i++) {
      if (destStart < destLimit) {
        dest[destStart] = foldedC[i];
      }
      

      destStart++;
    }
    return c == UTF16.charAt(foldedStr, 0) ? -destStart : destStart;
  }
  


















  public static int cmpEquivFold(String s1, String s2, int options)
  {
    return cmpEquivFold(s1.toCharArray(), 0, s1.length(), s2.toCharArray(), 0, s2.length(), options);
  }
  









  public static int cmpEquivFold(char[] s1, int s1Start, int s1Limit, char[] s2, int s2Start, int s2Limit, int options)
  {
    char[] cSource1 = s1;
    char[] cSource2 = s2;
    



    CmpEquivLevel[] stack1 = { new CmpEquivLevel(null), new CmpEquivLevel(null) };
    


    CmpEquivLevel[] stack2 = { new CmpEquivLevel(null), new CmpEquivLevel(null) };
    




    char[] decomp1 = new char[8];
    char[] decomp2 = new char[8];
    

    char[] fold1 = new char[32];
    char[] fold2 = new char[32];
    
















    int start1 = s1Start;
    int limit1 = s1Limit;
    
    int start2 = s2Start;
    int limit2 = s2Limit;
    int level2;
    int level1 = level2 = 0;
    int c2; int c1 = c2 = -1;
    int cp2; int cp1 = cp2 = -1;
    


    for (;;)
    {
      if (c1 < 0) {
        for (;;)
        {
          if (s1Start >= limit1) {
            if (level1 == 0) {
              c1 = -1;
              break;
            }
          } else {
            c1 = cSource1[s1Start];
            s1Start++;
            break;
          }
          
          do
          {
            level1--;
            start1 = start;
          } while (start1 == -1);
          s1Start = s;
          limit1 = limit;
          cSource1 = source;
        }
      }
      
      if (c2 < 0) {
        for (;;)
        {
          if (s2Start >= limit2) {
            if (level2 == 0) {
              c2 = -1;
              break;
            }
          } else {
            c2 = cSource2[s2Start];
            s2Start++;
            break;
          }
          
          do
          {
            level2--;
            start2 = start;
          } while (start2 == -1);
          s2Start = s;
          limit2 = limit;
          cSource2 = source;
        }
      }
      



      if (c1 == c2) {
        if (c1 < 0) {
          return 0;
        }
        c1 = c2 = -1;
      } else {
        if (c1 < 0)
          return -1;
        if (c2 < 0) {
          return 1;
        }
        



        cp1 = c1;
        if (UTF16.isSurrogate((char)c1))
        {
          char c;
          if (UTF16.isLeadSurrogate((char)c1)) {
            if ((s1Start != limit1) && (UTF16.isTrailSurrogate(c = cSource1[s1Start])))
            {


              cp1 = UCharacterProperty.getRawSupplementary((char)c1, c);
            }
          }
          else if ((start1 <= s1Start - 2) && (UTF16.isLeadSurrogate(c = cSource1[(s1Start - 2)])))
          {

            cp1 = UCharacterProperty.getRawSupplementary(c, (char)c1);
          }
        }
        
        cp2 = c2;
        if (UTF16.isSurrogate((char)c2))
        {
          char c;
          if (UTF16.isLeadSurrogate((char)c2)) {
            if ((s2Start != limit2) && (UTF16.isTrailSurrogate(c = cSource2[s2Start])))
            {


              cp2 = UCharacterProperty.getRawSupplementary((char)c2, c);
            }
          }
          else if ((start2 <= s2Start - 2) && (UTF16.isLeadSurrogate(c = cSource2[(s2Start - 2)])))
          {

            cp2 = UCharacterProperty.getRawSupplementary(c, (char)c2);
          }
        }
        

        int length;
        
        if ((level1 < 2) && ((options & 0x10000) != 0) && ((length = foldCase(cp1, fold1, 0, 32, options)) >= 0))
        {


          if (UTF16.isSurrogate((char)c1)) {
            if (UTF16.isLeadSurrogate((char)c1))
            {

              s1Start++;


            }
            else
            {

              s2Start--;
              c2 = cSource2[(s2Start - 1)];
            }
          }
          

          0start = start1;
          0s = s1Start;
          0limit = limit1;
          0source = cSource1;
          level1++;
          
          cSource1 = fold1;
          start1 = s1Start = 0;
          limit1 = length;
          

          c1 = -1;


        }
        else if ((level2 < 2) && ((options & 0x10000) != 0) && ((length = foldCase(cp2, fold2, 0, 32, options)) >= 0))
        {


          if (UTF16.isSurrogate((char)c2)) {
            if (UTF16.isLeadSurrogate((char)c2))
            {

              s2Start++;


            }
            else
            {

              s1Start--;
              c1 = cSource1[(s1Start - 1)];
            }
          }
          

          0start = start2;
          0s = s2Start;
          0limit = limit2;
          0source = cSource2;
          level2++;
          
          cSource2 = fold2;
          start2 = s2Start = 0;
          limit2 = length;
          

          c2 = -1;


        }
        else if ((level1 < 2) && ((options & 0x80000) != 0) && (0 != (length = decompose(cp1, decomp1))))
        {


          if (UTF16.isSurrogate((char)c1)) {
            if (UTF16.isLeadSurrogate((char)c1))
            {

              s1Start++;


            }
            else
            {

              s2Start--;
              c2 = cSource2[(s2Start - 1)];
            }
          }
          

          start = start1;
          s = s1Start;
          limit = limit1;
          source = cSource1;
          level1++;
          

          cSource1 = decomp1;
          start1 = s1Start = 0;
          limit1 = length;
          

          if (level1 < 2) {
            start = -1;
          }
          
          c1 = -1;
        }
        else
        {
          if ((level2 >= 2) || ((options & 0x80000) == 0) || (0 == (length = decompose(cp2, decomp2)))) {
            break;
          }
          
          if (UTF16.isSurrogate((char)c2)) {
            if (UTF16.isLeadSurrogate((char)c2))
            {

              s2Start++;


            }
            else
            {

              s1Start--;
              c1 = cSource1[(s1Start - 1)];
            }
          }
          

          start = start2;
          s = s2Start;
          limit = limit2;
          source = cSource2;
          level2++;
          

          cSource2 = decomp2;
          start2 = s2Start = 0;
          limit2 = length;
          

          if (level2 < 2) {
            start = -1;
          }
          

          c2 = -1;
        }
      }
    }
    















    if ((c1 >= 55296) && (c2 >= 55296) && ((options & 0x8000) != 0))
    {



      if (((c1 > 56319) || (s1Start == limit1) || (!UTF16.isTrailSurrogate(cSource1[s1Start]))) && ((!UTF16.isTrailSurrogate((char)c1)) || (start1 == s1Start - 1) || (!UTF16.isLeadSurrogate(cSource1[(s1Start - 2)]))))
      {













        c1 -= 10240;
      }
      
      if (((c2 > 56319) || (s2Start == limit2) || (!UTF16.isTrailSurrogate(cSource2[s2Start]))) && ((!UTF16.isTrailSurrogate((char)c2)) || (start2 == s2Start - 1) || (!UTF16.isLeadSurrogate(cSource2[(s2Start - 2)]))))
      {













        c2 -= 10240;
      }
    }
    
    return c1 - c2;
  }
  







  private static int strCompare(char[] s1, int s1Start, int s1Limit, char[] s2, int s2Start, int s2Limit, boolean codePointOrder)
  {
    int start1 = s1Start;
    int start2 = s2Start;
    


    int length1 = s1Limit - s1Start;
    int length2 = s2Limit - s2Start;
    
    int lengthResult;
    
    if (length1 < length2) {
      lengthResult = -1;
      limit1 = start1 + length1;
    } else if (length1 == length2) {
      lengthResult = 0;
      limit1 = start1 + length1;
    } else {
      lengthResult = 1;
      limit1 = start1 + length2;
    }
    
    if (s1 == s2) {
      return lengthResult;
    }
    char c1;
    char c2;
    for (;;) {
      if (s1Start == limit1) {
        return lengthResult;
      }
      
      c1 = s1[s1Start];
      c2 = s2[s2Start];
      if (c1 != c2) {
        break;
      }
      s1Start++;
      s2Start++;
    }
    

    int limit1 = start1 + length1;
    int limit2 = start2 + length2;
    


    if ((c1 >= 55296) && (c2 >= 55296) && (codePointOrder))
    {

      if (((c1 > 56319) || (s1Start + 1 == limit1) || (!UTF16.isTrailSurrogate(s1[(s1Start + 1)]))) && ((!UTF16.isTrailSurrogate(c1)) || (start1 == s1Start) || (!UTF16.isLeadSurrogate(s1[(s1Start - 1)]))))
      {









        c1 = (char)(c1 - '⠀');
      }
      
      if (((c2 > 56319) || (s2Start + 1 == limit2) || (!UTF16.isTrailSurrogate(s2[(s2Start + 1)]))) && ((!UTF16.isTrailSurrogate(c2)) || (start2 == s2Start) || (!UTF16.isLeadSurrogate(s2[(s2Start - 1)]))))
      {









        c2 = (char)(c2 - '⠀');
      }
    }
    

    return c1 - c2;
  }
  













































  private static final UnicodeSet[] nxCache = new UnicodeSet['Ā'];
  






  private static final int NX_HANGUL = 1;
  






  private static final int NX_CJK_COMPAT = 2;
  






  private static final synchronized UnicodeSet internalGetNXHangul()
  {
    if (nxCache[1] == null) {
      nxCache[1] = new UnicodeSet(44032, 55203);
    }
    return nxCache[1];
  }
  

  private static final synchronized UnicodeSet internalGetNXCJKCompat()
  {
    if (nxCache[2] == null)
    {



      UnicodeSet set = new UnicodeSet("[:Ideographic:]");
      

      UnicodeSet hasDecomp = new UnicodeSet();
      

      com.ibm.icu.text.UnicodeSetIterator it = new com.ibm.icu.text.UnicodeSetIterator(set);
      int start;
      int end;
      for (; 
          (it.nextRange()) && (codepoint != com.ibm.icu.text.UnicodeSetIterator.IS_STRING); 
          

          start <= end)
      {
        start = codepoint;
        end = codepointEnd;
        continue;
        long norm32 = getNorm32(start);
        if ((norm32 & 0x4) > 0L) {
          hasDecomp.add(start);
        }
        start++;
      }
      


      nxCache[2] = hasDecomp;
    }
    

    return nxCache[2];
  }
  
  private static final synchronized UnicodeSet internalGetNXUnicode(int options) {
    options &= 0xE0;
    if (options == 0) {
      return null;
    }
    
    if (nxCache[options] == null)
    {
      UnicodeSet set = new UnicodeSet();
      
      switch (options) {
      case 32: 
        set.applyPattern("[:^Age=3.2:]");
        break;
      default: 
        return null;
      }
      
      nxCache[options] = set;
    }
    
    return nxCache[options];
  }
  
  private static final synchronized UnicodeSet internalGetNX(int options)
  {
    options &= 0xFF;
    
    if (nxCache[options] == null)
    {
      if (options == 1) {
        return internalGetNXHangul();
      }
      if (options == 2) {
        return internalGetNXCJKCompat();
      }
      if (((options & 0xE0) != 0) && ((options & 0x1F) == 0)) {
        return internalGetNXUnicode(options);
      }
      




      UnicodeSet set = new UnicodeSet();
      
      UnicodeSet other;
      if (((options & 0x1) != 0) && (null != (other = internalGetNXHangul()))) {
        set.addAll(other);
      }
      if (((options & 0x2) != 0) && (null != (other = internalGetNXCJKCompat()))) {
        set.addAll(other);
      }
      if (((options & 0xE0) != 0) && (null != (other = internalGetNXUnicode(options)))) {
        set.addAll(other);
      }
      
      nxCache[options] = set;
    }
    return nxCache[options];
  }
  
  public static final UnicodeSet getNX(int options) {
    if ((options &= 0xFF) == 0)
    {
      return null;
    }
    return internalGetNX(options);
  }
  
  private static final boolean nx_contains(UnicodeSet nx, int c)
  {
    return (nx != null) && (nx.contains(c));
  }
  
  private static final boolean nx_contains(UnicodeSet nx, char c, char c2) {
    if (nx != null) {} return nx.contains(c2 == 0 ? c : UCharacterProperty.getRawSupplementary(c, c2));
  }
}
