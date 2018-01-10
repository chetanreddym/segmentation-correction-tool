package com.ibm.icu.text;

import com.ibm.icu.impl.NormalizerImpl;
import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.impl.Utility;
import java.text.CharacterIterator;






















































































































public final class Normalizer
  implements Cloneable
{
  private char[] buffer = new char[100];
  private int bufferStart = 0;
  private int bufferPos = 0;
  private int bufferLimit = 0;
  
  private static final int COMPAT_BIT = 1;
  
  private static final int DECOMP_BIT = 2;
  
  private static final int COMPOSE_BIT = 4;
  
  private UCharacterIterator text;
  private Mode mode = NFC;
  private int options = 0;
  

  private int currentIndex;
  

  private int nextIndex;
  

  public static final int UNICODE_3_2 = 32;
  

  public static final int DONE = -1;
  

  public static class Mode
  {
    private int modeValue;
    

    Mode(int x0, Normalizer.1 x1)
    {
      this(x0);
    }
    
    private Mode(int value) { modeValue = value; }
    





    protected int normalize(char[] src, int srcStart, int srcLimit, char[] dest, int destStart, int destLimit, UnicodeSet nx)
    {
      int srcLen = srcLimit - srcStart;
      int destLen = destLimit - destStart;
      if (srcLen > destLen) {
        return srcLen;
      }
      System.arraycopy(src, srcStart, dest, destStart, srcLen);
      return srcLen;
    }
    




    protected int normalize(char[] src, int srcStart, int srcLimit, char[] dest, int destStart, int destLimit, int options)
    {
      return normalize(src, srcStart, srcLimit, dest, destStart, destLimit, NormalizerImpl.getNX(options));
    }
    






    protected String normalize(String src, int options)
    {
      return src;
    }
    


    protected int getMinC()
    {
      return -1;
    }
    


    protected int getMask()
    {
      return -1;
    }
    


    protected Normalizer.IsPrevBoundary getPrevBoundary()
    {
      return null;
    }
    


    protected Normalizer.IsNextBoundary getNextBoundary()
    {
      return null;
    }
    



    protected Normalizer.QuickCheckResult quickCheck(char[] src, int start, int limit, boolean allowMaybe, UnicodeSet nx)
    {
      if (allowMaybe) {
        return Normalizer.MAYBE;
      }
      return Normalizer.NO;
    }
    


    protected boolean isNFSkippable(int c)
    {
      return true;
    }
  }
  





  public static final Mode NONE = new Mode(1, null);
  




  public static final Mode NFD = new NFDMode(2, null);
  
  private static final class NFDMode extends Normalizer.Mode { NFDMode(int x0, Normalizer.1 x1) { this(x0); }
    
    private NFDMode(int value) { super(null); }
    

    protected int normalize(char[] src, int srcStart, int srcLimit, char[] dest, int destStart, int destLimit, UnicodeSet nx)
    {
      int[] trailCC = new int[1];
      return NormalizerImpl.decompose(src, srcStart, srcLimit, dest, destStart, destLimit, false, trailCC, nx);
    }
    

    protected String normalize(String src, int options)
    {
      return Normalizer.decompose(src, false);
    }
    
    protected int getMinC() { return 768; }
    
    protected Normalizer.IsPrevBoundary getPrevBoundary() {
      return new Normalizer.IsPrevNFDSafe(null);
    }
    
    protected Normalizer.IsNextBoundary getNextBoundary() { return new Normalizer.IsNextNFDSafe(null); }
    
    protected int getMask() {
      return 65284;
    }
    
    protected Normalizer.QuickCheckResult quickCheck(char[] src, int start, int limit, boolean allowMaybe, UnicodeSet nx)
    {
      return NormalizerImpl.quickCheck(src, start, limit, NormalizerImpl.getFromIndexesArr(8), 4, allowMaybe, nx);
    }
    






    protected boolean isNFSkippable(int c)
    {
      return NormalizerImpl.isNFSkippable(c, this, 65284L);
    }
  }
  






  public static final Mode NFKD = new NFKDMode(3, null);
  
  private static final class NFKDMode extends Normalizer.Mode { NFKDMode(int x0, Normalizer.1 x1) { this(x0); }
    
    private NFKDMode(int value) { super(null); }
    

    protected int normalize(char[] src, int srcStart, int srcLimit, char[] dest, int destStart, int destLimit, UnicodeSet nx)
    {
      int[] trailCC = new int[1];
      return NormalizerImpl.decompose(src, srcStart, srcLimit, dest, destStart, destLimit, true, trailCC, nx);
    }
    
    protected String normalize(String src, int options)
    {
      return Normalizer.decompose(src, true);
    }
    
    protected int getMinC() { return 768; }
    
    protected Normalizer.IsPrevBoundary getPrevBoundary() {
      return new Normalizer.IsPrevNFDSafe(null);
    }
    
    protected Normalizer.IsNextBoundary getNextBoundary() { return new Normalizer.IsNextNFDSafe(null); }
    
    protected int getMask() {
      return 65288;
    }
    
    protected Normalizer.QuickCheckResult quickCheck(char[] src, int start, int limit, boolean allowMaybe, UnicodeSet nx)
    {
      return NormalizerImpl.quickCheck(src, start, limit, NormalizerImpl.getFromIndexesArr(9), 8, allowMaybe, nx);
    }
    






    protected boolean isNFSkippable(int c)
    {
      return NormalizerImpl.isNFSkippable(c, this, 65288L);
    }
  }
  






  public static final Mode NFC = new NFCMode(4, null);
  
  private static final class NFCMode extends Normalizer.Mode { NFCMode(int x0, Normalizer.1 x1) { this(x0); }
    
    private NFCMode(int value) { super(null); }
    

    protected int normalize(char[] src, int srcStart, int srcLimit, char[] dest, int destStart, int destLimit, UnicodeSet nx)
    {
      return NormalizerImpl.compose(src, srcStart, srcLimit, dest, destStart, destLimit, false, nx);
    }
    

    protected String normalize(String src, int options)
    {
      return Normalizer.compose(src, false);
    }
    
    protected int getMinC() {
      return NormalizerImpl.getFromIndexesArr(6);
    }
    
    protected Normalizer.IsPrevBoundary getPrevBoundary()
    {
      return new Normalizer.IsPrevTrueStarter(null);
    }
    
    protected Normalizer.IsNextBoundary getNextBoundary() { return new Normalizer.IsNextTrueStarter(null); }
    
    protected int getMask() {
      return 65297;
    }
    
    protected Normalizer.QuickCheckResult quickCheck(char[] src, int start, int limit, boolean allowMaybe, UnicodeSet nx)
    {
      return NormalizerImpl.quickCheck(src, start, limit, NormalizerImpl.getFromIndexesArr(6), 17, allowMaybe, nx);
    }
    






    protected boolean isNFSkippable(int c)
    {
      return NormalizerImpl.isNFSkippable(c, this, 65473L);
    }
  }
  








  public static final Mode DEFAULT = NFC;
  




  public static final Mode NFKC = new NFKCMode(5, null);
  
  private static final class NFKCMode extends Normalizer.Mode { NFKCMode(int x0, Normalizer.1 x1) { this(x0); }
    
    private NFKCMode(int value) { super(null); }
    

    protected int normalize(char[] src, int srcStart, int srcLimit, char[] dest, int destStart, int destLimit, UnicodeSet nx)
    {
      return NormalizerImpl.compose(src, srcStart, srcLimit, dest, destStart, destLimit, true, nx);
    }
    

    protected String normalize(String src, int options)
    {
      return Normalizer.compose(src, true);
    }
    
    protected int getMinC() { return NormalizerImpl.getFromIndexesArr(7); }
    

    protected Normalizer.IsPrevBoundary getPrevBoundary()
    {
      return new Normalizer.IsPrevTrueStarter(null);
    }
    
    protected Normalizer.IsNextBoundary getNextBoundary() { return new Normalizer.IsNextTrueStarter(null); }
    
    protected int getMask() {
      return 65314;
    }
    
    protected Normalizer.QuickCheckResult quickCheck(char[] src, int start, int limit, boolean allowMaybe, UnicodeSet nx)
    {
      return NormalizerImpl.quickCheck(src, start, limit, NormalizerImpl.getFromIndexesArr(7), 34, allowMaybe, nx);
    }
    






    protected boolean isNFSkippable(int c)
    {
      return NormalizerImpl.isNFSkippable(c, this, 65474L);
    }
  }
  








  public static final Mode FCD = new FCDMode(6, null);
  
  private static final class FCDMode extends Normalizer.Mode { FCDMode(int x0, Normalizer.1 x1) { this(x0); }
    
    private FCDMode(int value) { super(null); }
    

    protected int normalize(char[] src, int srcStart, int srcLimit, char[] dest, int destStart, int destLimit, UnicodeSet nx)
    {
      return NormalizerImpl.makeFCD(src, srcStart, srcLimit, dest, destStart, destLimit, nx);
    }
    
    protected String normalize(String src, int options) {
      return Normalizer.makeFCD(src, options);
    }
    
    protected int getMinC() { return 768; }
    
    protected Normalizer.IsPrevBoundary getPrevBoundary() {
      return new Normalizer.IsPrevNFDSafe(null);
    }
    
    protected Normalizer.IsNextBoundary getNextBoundary() { return new Normalizer.IsNextNFDSafe(null); }
    
    protected int getMask() {
      return 65284;
    }
    
    protected Normalizer.QuickCheckResult quickCheck(char[] src, int start, int limit, boolean allowMaybe, UnicodeSet nx)
    {
      return NormalizerImpl.checkFCD(src, start, limit, nx) ? Normalizer.YES : Normalizer.NO;
    }
    
    protected boolean isNFSkippable(int c) {
      return NormalizerImpl.getFCD16(c) > 1;
    }
  }
  












  /**
   * @deprecated
   */
  public static final Mode NO_OP = NONE;
  













  /**
   * @deprecated
   */
  public static final Mode COMPOSE = NFC;
  













  /**
   * @deprecated
   */
  public static final Mode COMPOSE_COMPAT = NFKC;
  













  /**
   * @deprecated
   */
  public static final Mode DECOMP = NFD;
  













  /**
   * @deprecated
   */
  public static final Mode DECOMP_COMPAT = NFKD;
  




  /**
   * @deprecated
   */
  public static final int IGNORE_HANGUL = 1;
  





  public static final class QuickCheckResult
  {
    private int resultValue;
    





    QuickCheckResult(int x0, Normalizer.1 x1)
    {
      this(x0);
    }
    
    private QuickCheckResult(int value) { resultValue = value; }
  }
  




  public static final QuickCheckResult NO = new QuickCheckResult(0, null);
  




  public static final QuickCheckResult YES = new QuickCheckResult(1, null);
  





  public static final QuickCheckResult MAYBE = new QuickCheckResult(2, null);
  







  public static final int FOLD_CASE_DEFAULT = 0;
  







  public static final int INPUT_IS_FCD = 131072;
  






  public static final int COMPARE_IGNORE_CASE = 65536;
  






  public static final int COMPARE_CODE_POINT_ORDER = 32768;
  






  public static final int FOLD_CASE_EXCLUDE_SPECIAL_I = 1;
  






  public static final int COMPARE_NORM_OPTIONS_SHIFT = 20;
  






  private static final int MAX_BUF_SIZE_COMPOSE = 2;
  






  private static final int MAX_BUF_SIZE_DECOMPOSE = 3;
  







  public Normalizer(String str, Mode mode, int opt)
  {
    text = UCharacterIterator.getInstance(str);
    this.mode = mode;
    options = opt;
  }
  














  public Normalizer(CharacterIterator iter, Mode mode, int opt)
  {
    text = UCharacterIterator.getInstance((CharacterIterator)iter.clone());
    

    this.mode = mode;
    options = opt;
  }
  









  public Normalizer(UCharacterIterator iter, Mode mode, int options)
  {
    try
    {
      text = ((UCharacterIterator)iter.clone());
      this.mode = mode;
      this.options = options;
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e.toString());
    }
  }
  









  public Object clone()
  {
    try
    {
      Normalizer copy = (Normalizer)super.clone();
      text = ((UCharacterIterator)text.clone());
      
      if (buffer != null) {
        buffer = new char[buffer.length];
        System.arraycopy(buffer, 0, buffer, 0, buffer.length);
      }
      return copy;
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError(e.toString());
    }
  }
  













  public static String compose(String str, boolean compat)
  {
    return compose(str, compat, 0);
  }
  











  public static String compose(String str, boolean compat, int options)
  {
    char[] dest = new char[str.length() * 2];
    int destSize = 0;
    char[] src = str.toCharArray();
    UnicodeSet nx = NormalizerImpl.getNX(options);
    for (;;) {
      destSize = NormalizerImpl.compose(src, 0, src.length, dest, 0, dest.length, compat, nx);
      

      if (destSize <= dest.length) {
        return new String(dest, 0, destSize);
      }
      dest = new char[destSize];
    }
  }
  















  public static int compose(char[] source, char[] target, boolean compat, int options)
  {
    UnicodeSet nx = NormalizerImpl.getNX(options);
    int length = NormalizerImpl.compose(source, 0, source.length, target, 0, target.length, compat, nx);
    

    if (length <= target.length) {
      return length;
    }
    throw new IndexOutOfBoundsException(Integer.toString(length));
  }
  





















  public static int compose(char[] src, int srcStart, int srcLimit, char[] dest, int destStart, int destLimit, boolean compat, int options)
  {
    UnicodeSet nx = NormalizerImpl.getNX(options);
    int length = NormalizerImpl.compose(src, srcStart, srcLimit, dest, destStart, destLimit, compat, nx);
    

    if (length <= destLimit - destStart) {
      return length;
    }
    throw new IndexOutOfBoundsException(Integer.toString(length));
  }
  













  public static String decompose(String str, boolean compat)
  {
    return decompose(str, compat, 0);
  }
  











  public static String decompose(String str, boolean compat, int options)
  {
    char[] dest = new char[str.length() * 3];
    int[] trailCC = new int[1];
    int destSize = 0;
    UnicodeSet nx = NormalizerImpl.getNX(options);
    for (;;) {
      destSize = NormalizerImpl.decompose(str.toCharArray(), 0, str.length(), dest, 0, dest.length, compat, trailCC, nx);
      

      if (destSize <= dest.length) {
        return new String(dest, 0, destSize);
      }
      dest = new char[destSize];
    }
  }
  
















  public static int decompose(char[] source, char[] target, boolean compat, int options)
  {
    int[] trailCC = new int[1];
    UnicodeSet nx = NormalizerImpl.getNX(options);
    int length = NormalizerImpl.decompose(source, 0, source.length, target, 0, target.length, compat, trailCC, nx);
    

    if (length <= target.length) {
      return length;
    }
    throw new IndexOutOfBoundsException(Integer.toString(length));
  }
  





















  public static int decompose(char[] src, int srcStart, int srcLimit, char[] dest, int destStart, int destLimit, boolean compat, int options)
  {
    int[] trailCC = new int[1];
    UnicodeSet nx = NormalizerImpl.getNX(options);
    int length = NormalizerImpl.decompose(src, srcStart, srcLimit, dest, destStart, destLimit, compat, trailCC, nx);
    

    if (length <= destLimit - destStart) {
      return length;
    }
    throw new IndexOutOfBoundsException(Integer.toString(length));
  }
  
  private static String makeFCD(String src, int options)
  {
    int srcLen = src.length();
    char[] dest = new char[3 * srcLen];
    int length = 0;
    UnicodeSet nx = NormalizerImpl.getNX(options);
    for (;;) {
      length = NormalizerImpl.makeFCD(src.toCharArray(), 0, srcLen, dest, 0, dest.length, nx);
      
      if (length <= dest.length) {
        return new String(dest, 0, length);
      }
      dest = new char[length];
    }
  }
  















  public static String normalize(String str, Mode mode, int options)
  {
    return mode.normalize(str, options);
  }
  











  public static String normalize(String src, Mode mode)
  {
    return normalize(src, mode, 0);
  }
  














  public static int normalize(char[] source, char[] target, Mode mode, int options)
  {
    int length = normalize(source, 0, source.length, target, 0, target.length, mode, options);
    if (length <= target.length) {
      return length;
    }
    throw new IndexOutOfBoundsException(Integer.toString(length));
  }
  






















  public static int normalize(char[] src, int srcStart, int srcLimit, char[] dest, int destStart, int destLimit, Mode mode, int options)
  {
    int length = mode.normalize(src, srcStart, srcLimit, dest, destStart, destLimit, options);
    
    if (length <= destLimit - destStart) {
      return length;
    }
    throw new IndexOutOfBoundsException(Integer.toString(length));
  }
  












  public static String normalize(int char32, Mode mode, int options)
  {
    return normalize(UTF16.valueOf(char32), mode, options);
  }
  








  public static String normalize(int char32, Mode mode)
  {
    return normalize(UTF16.valueOf(char32), mode, 0);
  }
  









  public static QuickCheckResult quickCheck(String source, Mode mode)
  {
    return mode.quickCheck(source.toCharArray(), 0, source.length(), true, null);
  }
  











  public static QuickCheckResult quickCheck(String source, Mode mode, int options)
  {
    return mode.quickCheck(source.toCharArray(), 0, source.length(), true, NormalizerImpl.getNX(options));
  }
  












  public static QuickCheckResult quickCheck(char[] source, Mode mode, int options)
  {
    return mode.quickCheck(source, 0, source.length, true, NormalizerImpl.getNX(options));
  }
  
























  public static QuickCheckResult quickCheck(char[] source, int start, int limit, Mode mode, int options)
  {
    return mode.quickCheck(source, start, limit, true, NormalizerImpl.getNX(options));
  }
  


























  public static boolean isNormalized(char[] src, int start, int limit, Mode mode, int options)
  {
    return mode.quickCheck(src, start, limit, false, NormalizerImpl.getNX(options)) == YES;
  }
  









  public static boolean isNormalized(String str, Mode mode, int options)
  {
    return mode.quickCheck(str.toCharArray(), 0, str.length(), false, NormalizerImpl.getNX(options)) == YES;
  }
  












  public static boolean isNormalized(int char32, Mode mode, int options)
  {
    return isNormalized(UTF16.valueOf(char32), mode, options);
  }
  






















































  public static int compare(char[] s1, int s1Start, int s1Limit, char[] s2, int s2Start, int s2Limit, int options)
  {
    return internalCompare(s1, s1Start, s1Limit, s2, s2Start, s2Limit, options);
  }
  




































  public static int compare(String s1, String s2, int options)
  {
    return compare(s1.toCharArray(), 0, s1.length(), s2.toCharArray(), 0, s2.length(), options);
  }
  




































  public static int compare(char[] s1, char[] s2, int options)
  {
    return compare(s1, 0, s1.length, s2, 0, s2.length, options);
  }
  










  public static int compare(int char32a, int char32b, int options)
  {
    return compare(UTF16.valueOf(char32a), UTF16.valueOf(char32b), options);
  }
  













  public static int compare(int charA, String str2, int options)
  {
    return compare(UTF16.valueOf(charA), str2, options);
  }
  















































  public static int concatenate(char[] left, int leftStart, int leftLimit, char[] right, int rightStart, int rightLimit, char[] dest, int destStart, int destLimit, Mode mode, int options)
  {
    char[] buffer = new char[100];
    





    if (dest == null) {
      throw new IllegalArgumentException();
    }
    

    if ((right == dest) && (rightStart < destLimit) && (destStart < rightLimit)) {
      throw new IllegalArgumentException("overlapping right and dst ranges");
    }
    




















    UCharacterIterator iter = UCharacterIterator.getInstance(left, leftStart, leftLimit);
    
    iter.setIndex(iter.getLength());
    
    int bufferLength = previous(iter, buffer, 0, buffer.length, mode, false, null, options);
    
    int leftBoundary = iter.getIndex();
    
    if (bufferLength > buffer.length) {
      char[] newBuf = new char[buffer.length * 2];
      buffer = newBuf;
      newBuf = null;
      
      System.arraycopy(left, leftBoundary, buffer, 0, bufferLength);
    }
    





    iter = UCharacterIterator.getInstance(right, rightStart, rightLimit);
    
    int rightBoundary = next(iter, buffer, bufferLength, buffer.length - bufferLength, mode, false, null, options);
    

    if (bufferLength > buffer.length) {
      char[] newBuf = new char[buffer.length * 2];
      buffer = newBuf;
      newBuf = null;
      
      System.arraycopy(right, rightBoundary, buffer, bufferLength, rightBoundary);
    }
    

    bufferLength += rightBoundary;
    

    if ((left != dest) && (leftBoundary > 0) && (destLimit > 0)) {
      System.arraycopy(left, 0, dest, 0, Math.min(leftBoundary, destLimit));
    }
    int destLength = leftBoundary;
    

    if (destLimit > destLength) {
      destLength += normalize(buffer, 0, bufferLength, dest, destLength, destLimit, mode, options);
    }
    else
    {
      destLength += normalize(buffer, 0, bufferLength, null, 0, 0, mode, options);
    }
    

    rightStart += rightBoundary;
    int rightLength = rightLimit - rightStart;
    if ((rightLength > 0) && (destLimit > destLength)) {
      System.arraycopy(right, rightStart, dest, destLength, Math.min(rightLength, destLength));
    }
    

    destLength += rightLength;
    
    if (destLength <= destLimit - destStart) {
      return destLength;
    }
    throw new IndexOutOfBoundsException(Integer.toString(destLength));
  }
  



























  public static String concatenate(char[] left, char[] right, Mode mode, int options)
  {
    char[] result = new char[(left.length + right.length) * 3];
    for (;;)
    {
      int length = concatenate(left, 0, left.length, right, 0, right.length, result, 0, result.length, mode, options);
      


      if (length <= result.length) {
        return new String(result, 0, length);
      }
      result = new char[length];
    }
  }
  



























  public static String concatenate(String left, String right, Mode mode, int options)
  {
    char[] result = new char[(left.length() + right.length()) * 3];
    for (;;)
    {
      int length = concatenate(left.toCharArray(), 0, left.length(), right.toCharArray(), 0, right.length(), result, 0, result.length, mode, options);
      


      if (length <= result.length) {
        return new String(result, 0, length);
      }
      result = new char[length];
    }
  }
  







  public static int getFC_NFKC_Closure(int c, char[] dest)
  {
    return NormalizerImpl.getFC_NFKC_Closure(c, dest);
  }
  





  public static String getFC_NFKC_Closure(int c)
  {
    char[] dest = new char[10];
    for (;;) {
      int length = getFC_NFKC_Closure(c, dest);
      if (length <= dest.length) {
        return new String(dest, 0, length);
      }
      dest = new char[length];
    }
  }
  








  public int current()
  {
    if ((bufferPos < bufferLimit) || (nextNormalize())) {
      return getCodePointAt(bufferPos);
    }
    return -1;
  }
  







  public int next()
  {
    if ((bufferPos < bufferLimit) || (nextNormalize())) {
      int c = getCodePointAt(bufferPos);
      bufferPos += (c > 65535 ? 2 : 1);
      return c;
    }
    return -1;
  }
  








  public int previous()
  {
    if ((bufferPos > 0) || (previousNormalize())) {
      int c = getCodePointAt(bufferPos - 1);
      bufferPos -= (c > 65535 ? 2 : 1);
      return c;
    }
    return -1;
  }
  





  public void reset()
  {
    text.setIndex(0);
    currentIndex = (this.nextIndex = 0);
    clearBuffer();
  }
  








  public void setIndexOnly(int index)
  {
    text.setIndex(index);
    currentIndex = (this.nextIndex = index);
    clearBuffer();
  }
  




















  public int setIndex(int index)
  {
    setIndexOnly(index);
    return current();
  }
  




  /**
   * @deprecated
   */
  public int getBeginIndex()
  {
    return 0;
  }
  





  /**
   * @deprecated
   */
  public int getEndIndex()
  {
    return text.getLength() - 1;
  }
  




  public int first()
  {
    reset();
    return next();
  }
  






  public int last()
  {
    text.setToLimit();
    currentIndex = (this.nextIndex = text.getIndex());
    clearBuffer();
    return previous();
  }
  














  public int getIndex()
  {
    if (bufferPos < bufferLimit) {
      return currentIndex;
    }
    return nextIndex;
  }
  







  public int startIndex()
  {
    return 0;
  }
  






  public int endIndex()
  {
    return text.getLength();
  }
  




























  public void setMode(Mode newMode)
  {
    mode = newMode;
  }
  




  public Mode getMode()
  {
    return mode;
  }
  
















  public void setOption(int option, boolean value)
  {
    if (value) {
      options |= option;
    } else {
      options &= (option ^ 0xFFFFFFFF);
    }
  }
  





  public int getOption(int option)
  {
    if ((options & option) != 0) {
      return 1;
    }
    return 0;
  }
  









  public int getText(char[] fillIn)
  {
    return text.getText(fillIn);
  }
  




  public int getLength()
  {
    return text.getLength();
  }
  




  public String getText()
  {
    return text.getText();
  }
  






  public void setText(StringBuffer newText)
  {
    UCharacterIterator newIter = UCharacterIterator.getInstance(newText);
    if (newIter == null) {
      throw new InternalError("Could not create a new UCharacterIterator");
    }
    text = newIter;
    reset();
  }
  






  public void setText(char[] newText)
  {
    UCharacterIterator newIter = UCharacterIterator.getInstance(newText);
    if (newIter == null) {
      throw new InternalError("Could not create a new UCharacterIterator");
    }
    text = newIter;
    reset();
  }
  






  public void setText(String newText)
  {
    UCharacterIterator newIter = UCharacterIterator.getInstance(newText);
    if (newIter == null) {
      throw new InternalError("Could not create a new UCharacterIterator");
    }
    text = newIter;
    reset();
  }
  






  public void setText(CharacterIterator newText)
  {
    UCharacterIterator newIter = UCharacterIterator.getInstance(newText);
    if (newIter == null) {
      throw new InternalError("Could not create a new UCharacterIterator");
    }
    text = newIter;
    reset();
  }
  





  public void setText(UCharacterIterator newText)
  {
    try
    {
      UCharacterIterator newIter = (UCharacterIterator)newText.clone();
      if (newIter == null) {
        throw new InternalError("Could not create a new UCharacterIterator");
      }
      text = newIter;
      reset();
    } catch (CloneNotSupportedException e) {
      throw new InternalError("Could not clone the UCharacterIterator");
    }
  }
  

















  private static long getPrevNorm32(UCharacterIterator src, int minC, int mask, char[] chars)
  {
    int ch = 0;
    
    if ((ch = src.previous()) == -1) {
      return 0L;
    }
    chars[0] = ((char)ch);
    chars[1] = '\000';
    


    if (chars[0] < minC)
      return 0L;
    if (!UTF16.isSurrogate(chars[0]))
      return NormalizerImpl.getNorm32(chars[0]);
    if ((UTF16.isLeadSurrogate(chars[0])) || (src.getIndex() == 0))
    {
      chars[1] = ((char)src.current());
      return 0L; }
    if (UTF16.isLeadSurrogate(chars[1] = (char)src.previous())) {
      long norm32 = NormalizerImpl.getNorm32(chars[1]);
      if ((norm32 & mask) == 0L)
      {

        return 0L;
      }
      
      return NormalizerImpl.getNorm32FromSurrogatePair(norm32, chars[0]);
    }
    

    src.moveIndex(1);
    return 0L;
  }
  
  private static abstract interface IsPrevBoundary
  {
    public abstract boolean isPrevBoundary(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, char[] paramArrayOfChar);
  }
  
  private static final class IsPrevNFDSafe implements Normalizer.IsPrevBoundary {
    IsPrevNFDSafe(Normalizer.1 x0) {
      this();
    }
    







    public boolean isPrevBoundary(UCharacterIterator src, int minC, int ccOrQCMask, char[] chars)
    {
      return NormalizerImpl.isNFDSafe(Normalizer.getPrevNorm32(src, minC, ccOrQCMask, chars), ccOrQCMask, ccOrQCMask & 0x3F);
    }
    
    private IsPrevNFDSafe() {}
  }
  
  private static final class IsPrevTrueStarter implements Normalizer.IsPrevBoundary {
    IsPrevTrueStarter(Normalizer.1 x0) { this(); }
    










    public boolean isPrevBoundary(UCharacterIterator src, int minC, int ccOrQCMask, char[] chars)
    {
      int decompQCMask = ccOrQCMask << 2 & 0xF;
      long norm32 = Normalizer.getPrevNorm32(src, minC, ccOrQCMask | decompQCMask, chars);
      return NormalizerImpl.isTrueStarter(norm32, ccOrQCMask, decompQCMask);
    }
    

    private IsPrevTrueStarter() {}
  }
  

  private static int findPreviousIterationBoundary(UCharacterIterator src, IsPrevBoundary obj, int minC, int mask, char[] buffer, int[] startIndex)
  {
    char[] chars = new char[2];
    


    startIndex[0] = buffer.length;
    chars[0] = '\000';
    while ((src.getIndex() > 0) && (chars[0] != '￿')) {
      boolean isBoundary = obj.isPrevBoundary(src, minC, mask, chars);
      


      if (startIndex[0] < (chars[1] == 0 ? 1 : 2))
      {

        char[] newBuf = new char[buffer.length * 2];
        
        System.arraycopy(buffer, startIndex[0], newBuf, newBuf.length - (buffer.length - startIndex[0]), buffer.length - startIndex[0]);
        


        startIndex[0] += newBuf.length - buffer.length;
        
        buffer = newBuf;
        newBuf = null;
      }
      

      int tmp118_117 = 0; int[] tmp118_115 = startIndex; int tmp122_121 = (tmp118_115[tmp118_117] - 1);tmp118_115[tmp118_117] = tmp122_121;buffer[tmp122_121] = chars[0];
      if (chars[1] != 0) {
        int tmp141_140 = 0; int[] tmp141_138 = startIndex; int tmp145_144 = (tmp141_138[tmp141_140] - 1);tmp141_138[tmp141_140] = tmp145_144;buffer[tmp145_144] = chars[1];
      }
      

      if (isBoundary) {
        break;
      }
    }
    

    return buffer.length - startIndex[0];
  }
  








  private static int previous(UCharacterIterator src, char[] dest, int destStart, int destLimit, Mode mode, boolean doNormalize, boolean[] pNeededToNormalize, int options)
  {
    int[] startIndex = new int[1];
    


    int destCapacity = destLimit - destStart;
    int destLength = 0;
    char[] buffer = new char[100];
    
    if (pNeededToNormalize != null) {
      pNeededToNormalize[0] = false;
    }
    char minC = (char)mode.getMinC();
    int mask = mode.getMask();
    IsPrevBoundary isPreviousBoundary = mode.getPrevBoundary();
    
    if (isPreviousBoundary == null) {
      destLength = 0;
      int c; if ((c = src.previous()) >= 0) {
        destLength = 1;
        if (UTF16.isTrailSurrogate((char)c)) {
          int c2 = src.previous();
          if (c2 != -1) {
            if (UTF16.isLeadSurrogate((char)c2)) {
              if (destCapacity >= 2) {
                dest[1] = ((char)c);
                destLength = 2;
              }
              
              c = c2;
            } else {
              src.moveIndex(1);
            }
          }
        }
        
        if (destCapacity > 0) {
          dest[0] = ((char)c);
        }
      }
      return destLength;
    }
    
    int bufferLength = findPreviousIterationBoundary(src, isPreviousBoundary, minC, mask, buffer, startIndex);
    


    if (bufferLength > 0) {
      if (doNormalize) {
        destLength = normalize(buffer, startIndex[0], startIndex[0] + bufferLength, dest, destStart, destLimit, mode, options);
        



        if (pNeededToNormalize != null) {
          pNeededToNormalize[0] = ((destLength != bufferLength) || (Utility.arrayRegionMatches(buffer, 0, dest, destStart, destLimit)) ? 1 : false);


        }
        


      }
      else if (destCapacity > 0) {
        System.arraycopy(buffer, startIndex[0], dest, 0, bufferLength < destCapacity ? bufferLength : destCapacity);
      }
    }
    





    return destLength;
  }
  
























  private static long getNextNorm32(UCharacterIterator src, int minC, int mask, int[] chars)
  {
    chars[0] = src.next();
    chars[1] = 0;
    
    if (chars[0] < minC) {
      return 0L;
    }
    
    long norm32 = NormalizerImpl.getNorm32((char)chars[0]);
    if (UTF16.isLeadSurrogate((char)chars[0])) {
      if ((src.current() != -1) && (UTF16.isTrailSurrogate((char)(chars[1] = src.current()))))
      {
        src.moveIndex(1);
        if ((norm32 & mask) == 0L)
        {
          return 0L;
        }
        
        return NormalizerImpl.getNorm32FromSurrogatePair(norm32, (char)chars[1]);
      }
      

      return 0L;
    }
    
    return norm32;
  }
  
  private static abstract interface IsNextBoundary {
    public abstract boolean isNextBoundary(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, int[] paramArrayOfInt);
  }
  
  private static final class IsNextNFDSafe implements Normalizer.IsNextBoundary {
    IsNextNFDSafe(Normalizer.1 x0) {
      this();
    }
    
    public boolean isNextBoundary(UCharacterIterator src, int minC, int ccOrQCMask, int[] chars)
    {
      return NormalizerImpl.isNFDSafe(Normalizer.getNextNorm32(src, minC, ccOrQCMask, chars), ccOrQCMask, ccOrQCMask & 0x3F);
    }
    
    private IsNextNFDSafe() {}
  }
  
  private static final class IsNextTrueStarter
    implements Normalizer.IsNextBoundary
  {
    IsNextTrueStarter(Normalizer.1 x0)
    {
      this();
    }
    



    public boolean isNextBoundary(UCharacterIterator src, int minC, int ccOrQCMask, int[] chars)
    {
      int decompQCMask = ccOrQCMask << 2 & 0xF;
      long norm32 = Normalizer.getNextNorm32(src, minC, ccOrQCMask | decompQCMask, chars);
      return NormalizerImpl.isTrueStarter(norm32, ccOrQCMask, decompQCMask);
    }
    

    private IsNextTrueStarter() {}
  }
  
  private static int findNextIterationBoundary(UCharacterIterator src, IsNextBoundary obj, int minC, int mask, char[] buffer)
  {
    int[] chars = new int[2];
    int bufferIndex = 0;
    
    if (src.current() == -1) {
      return 0;
    }
    
    chars[0] = src.next();
    buffer[0] = ((char)chars[0]);
    bufferIndex = 1;
    
    if ((UTF16.isLeadSurrogate((char)chars[0])) && (src.current() != -1))
    {
      if (UTF16.isTrailSurrogate((char)(chars[1] = src.next()))) {
        buffer[(bufferIndex++)] = ((char)chars[1]);
      } else {
        src.moveIndex(-1);
      }
    }
    



    while (src.current() != -1) {
      if (obj.isNextBoundary(src, minC, mask, chars))
      {
        src.moveIndex(chars[1] == 0 ? -1 : -2);
        break;
      }
      if (bufferIndex + (chars[1] == 0 ? 1 : 2) <= buffer.length) {
        buffer[(bufferIndex++)] = ((char)chars[0]);
        if (chars[1] != 0) {
          buffer[(bufferIndex++)] = ((char)chars[1]);
        }
      } else {
        char[] newBuf = new char[buffer.length * 2];
        System.arraycopy(buffer, 0, newBuf, 0, bufferIndex);
        buffer = newBuf;
        buffer[(bufferIndex++)] = ((char)chars[0]);
        if (chars[1] != 0) {
          buffer[(bufferIndex++)] = ((char)chars[1]);
        }
      }
    }
    


    return bufferIndex;
  }
  





  private static int next(UCharacterIterator src, char[] dest, int destStart, int destLimit, Mode mode, boolean doNormalize, boolean[] pNeededToNormalize, int options)
  {
    char[] buffer = new char[100];
    




    int destCapacity = destLimit - destStart;
    int destLength = 0;
    int[] startIndex = new int[1];
    if (pNeededToNormalize != null) {
      pNeededToNormalize[0] = false;
    }
    
    char minC = (char)mode.getMinC();
    int mask = mode.getMask();
    IsNextBoundary isNextBoundary = mode.getNextBoundary();
    
    if (isNextBoundary == null) {
      destLength = 0;
      int c = src.next();
      if (c != -1) {
        destLength = 1;
        if (UTF16.isLeadSurrogate((char)c)) {
          int c2 = src.next();
          if (c2 != -1) {
            if (UTF16.isTrailSurrogate((char)c2)) {
              if (destCapacity >= 2) {
                dest[1] = ((char)c2);
                destLength = 2;
              }
            }
            else {
              src.moveIndex(-1);
            }
          }
        }
        
        if (destCapacity > 0) {
          dest[0] = ((char)c);
        }
      }
      return destLength;
    }
    
    int bufferLength = findNextIterationBoundary(src, isNextBoundary, minC, mask, buffer);
    
    if (bufferLength > 0) {
      if (doNormalize) {
        destLength = mode.normalize(buffer, startIndex[0], bufferLength, dest, destStart, destLimit, options);
        

        if (pNeededToNormalize != null) {
          pNeededToNormalize[0] = ((destLength != bufferLength) || (Utility.arrayRegionMatches(buffer, startIndex[0], dest, destStart, destLength)) ? 1 : false);

        }
        


      }
      else if (destCapacity > 0) {
        System.arraycopy(buffer, 0, dest, destStart, Math.min(bufferLength, destCapacity));
      }
    }
    




    return destLength;
  }
  
  private void clearBuffer() {
    bufferLimit = (this.bufferStart = this.bufferPos = 0);
  }
  
  private boolean nextNormalize()
  {
    clearBuffer();
    currentIndex = nextIndex;
    text.setIndex(nextIndex);
    
    bufferLimit = next(text, buffer, bufferStart, buffer.length, mode, true, null, options);
    
    nextIndex = text.getIndex();
    return bufferLimit > 0;
  }
  
  private boolean previousNormalize()
  {
    clearBuffer();
    nextIndex = currentIndex;
    text.setIndex(currentIndex);
    bufferLimit = previous(text, buffer, bufferStart, buffer.length, mode, true, null, options);
    
    currentIndex = text.getIndex();
    bufferPos = bufferLimit;
    return bufferLimit > 0;
  }
  
  private int getCodePointAt(int index) {
    if (UTF16.isSurrogate(buffer[index])) {
      if (UTF16.isLeadSurrogate(buffer[index])) {
        if ((index + 1 < bufferLimit) && (UTF16.isTrailSurrogate(buffer[(index + 1)])))
        {
          return UCharacterProperty.getRawSupplementary(buffer[index], buffer[(index + 1)]);
        }
        

      }
      else if ((UTF16.isTrailSurrogate(buffer[index])) && 
        (index > 0) && (UTF16.isLeadSurrogate(buffer[(index - 1)]))) {
        return UCharacterProperty.getRawSupplementary(buffer[(index - 1)], buffer[index]);
      }
    }
    



    return buffer[index];
  }
  




  public static boolean isNFSkippable(int c, Mode mode)
  {
    return mode.isNFSkippable(c);
  }
  



  private static int internalCompare(char[] s1, int s1Start, int s1Limit, char[] s2, int s2Start, int s2Limit, int options)
  {
    char[] fcd1 = new char['Ĭ'];
    char[] fcd2 = new char['Ĭ'];
    



    if ((s1 == null) || (s1Start < 0) || (s1Limit < 0) || (s2 == null) || (s2Start < 0) || (s2Limit < 0) || (s1Limit < s1Start) || (s2Limit < s2Start))
    {



      throw new IllegalArgumentException();
    }
    
    UnicodeSet nx = NormalizerImpl.getNX(options >> 20);
    options |= 0x80000;
    int result = 0;
    










    Mode mode;
    









    if ((options & 0x1) > 0) {
      mode = NFD;
      options &= 0xFFFDFFFF;
    } else {
      mode = FCD;
    }
    if ((options & 0x20000) == 0)
    {




      boolean isFCD1 = YES == mode.quickCheck(s1, s1Start, s1Limit, true, nx);
      boolean isFCD2 = YES == mode.quickCheck(s2, s2Start, s2Limit, true, nx);
      



      char[] dest;
      



      if (!isFCD1) {
        int fcdLen1 = mode.normalize(s1, 0, s1.length, fcd1, 0, fcd1.length, nx);
        


        if (fcdLen1 > fcd1.length) {
          dest = new char[fcdLen1];
          fcdLen1 = mode.normalize(s1, 0, s1.length, dest, 0, dest.length, nx);
          

          s1 = dest;
        } else {
          s1 = fcd1;
        }
        s1Limit = fcdLen1;
        s1Start = 0;
      }
      
      if (!isFCD2) {
        int fcdLen2 = mode.normalize(s2, s2Start, s2Limit, fcd2, 0, fcd2.length, nx);
        


        if (fcdLen2 > fcd2.length) {
          dest = new char[fcdLen2];
          fcdLen2 = mode.normalize(s2, s2Start, s2Limit, dest, 0, dest.length, nx);
          

          s2 = dest;
        } else {
          s2 = fcd2;
        }
        s2Limit = fcdLen2;
        s2Start = 0;
      }
    }
    


    result = NormalizerImpl.cmpEquivFold(s1, s1Start, s1Limit, s2, s2Start, s2Limit, options);
    
    return result;
  }
}
