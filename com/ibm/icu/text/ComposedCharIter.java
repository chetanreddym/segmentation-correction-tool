package com.ibm.icu.text;

import com.ibm.icu.impl.NormalizerImpl;






















































/**
 * @deprecated
 */
public final class ComposedCharIter
{
  /**
   * @deprecated
   */
  public static final char DONE = '￿';
  private int options;
  private boolean compat;
  
  /**
   * @deprecated
   */
  public ComposedCharIter()
  {
    compat = false;
    options = 0;
  }
  











  /**
   * @deprecated
   */
  public ComposedCharIter(boolean compat, int options)
  {
    this.compat = compat;
    this.options = options;
  }
  

  /**
   * @deprecated
   */
  public boolean hasNext()
  {
    if (nextChar == -1) {
      findNextChar();
    }
    return nextChar != -1;
  }
  




  /**
   * @deprecated
   */
  public char next()
  {
    if (nextChar == -1) {
      findNextChar();
    }
    curChar = nextChar;
    nextChar = -1;
    return (char)curChar;
  }
  





  /**
   * @deprecated
   */
  public String decomposition()
  {
    return new String(decompBuf, 0, bufLen);
  }
  
  private void findNextChar() {
    int c = curChar + 1;
    
    while (c < 65535) {
      bufLen = NormalizerImpl.getDecomposition(c, compat, decompBuf, 0, decompBuf.length);
      

      if (bufLen > 0) {
        break label59;
      }
      

      c++;
    }
    c = -1;
    
    label59:
    
    nextChar = c;
  }
  


  private char[] decompBuf = new char[100];
  private int bufLen = 0;
  private int curChar = 0;
  private int nextChar = -1;
}
