package com.ibm.icu.text;

import com.ibm.icu.lang.UCharacter;












































public final class ArabicShaping
{
  private final int options;
  private boolean isLogical;
  public static final int LENGTH_GROW_SHRINK = 0;
  public static final int LENGTH_FIXED_SPACES_NEAR = 1;
  public static final int LENGTH_FIXED_SPACES_AT_END = 2;
  public static final int LENGTH_FIXED_SPACES_AT_BEGINNING = 3;
  public static final int LENGTH_MASK = 3;
  public static final int TEXT_DIRECTION_LOGICAL = 0;
  public static final int TEXT_DIRECTION_VISUAL_LTR = 4;
  public static final int TEXT_DIRECTION_MASK = 4;
  public static final int LETTERS_NOOP = 0;
  public static final int LETTERS_SHAPE = 8;
  public static final int LETTERS_UNSHAPE = 16;
  public static final int LETTERS_SHAPE_TASHKEEL_ISOLATED = 24;
  public static final int LETTERS_MASK = 24;
  public static final int DIGITS_NOOP = 0;
  public static final int DIGITS_EN2AN = 32;
  public static final int DIGITS_AN2EN = 64;
  public static final int DIGITS_EN2AN_INIT_LR = 96;
  public static final int DIGITS_EN2AN_INIT_AL = 128;
  private static final int DIGITS_RESERVED = 160;
  public static final int DIGITS_MASK = 224;
  public static final int DIGIT_TYPE_AN = 0;
  public static final int DIGIT_TYPE_AN_EXTENDED = 256;
  public static final int DIGIT_TYPE_MASK = 256;
  private static final int IRRELEVANT = 4;
  private static final int LAMTYPE = 16;
  private static final int ALEFTYPE = 32;
  private static final int LINKR = 1;
  private static final int LINKL = 2;
  private static final int LINK_MASK = 3;
  
  public int shape(char[] source, int sourceStart, int sourceLength, char[] dest, int destStart, int destSize)
    throws ArabicShapingException
  {
    if (source == null) {
      throw new IllegalArgumentException("source can not be null");
    }
    if ((sourceStart < 0) || (sourceLength < 0) || (sourceStart + sourceLength > source.length)) {
      throw new IllegalArgumentException("bad source start (" + sourceStart + ") or length (" + sourceLength + ") for buffer of length " + source.length);
    }
    

    if ((dest == null) && (destSize != 0)) {
      throw new IllegalArgumentException("null dest requires destSize == 0");
    }
    if ((destSize != 0) && ((destStart < 0) || (destSize < 0) || (destStart + destSize > dest.length)))
    {
      throw new IllegalArgumentException("bad dest start (" + destStart + ") or size (" + destSize + ") for buffer of length " + dest.length);
    }
    


    return internalShape(source, sourceStart, sourceLength, dest, destStart, destSize);
  }
  








  public void shape(char[] source, int start, int length)
    throws ArabicShapingException
  {
    if ((options & 0x3) == 0) {
      throw new ArabicShapingException("Cannot shape in place with length option grow/shrink.");
    }
    shape(source, start, length, source, start, length);
  }
  






  public String shape(String text)
    throws ArabicShapingException
  {
    char[] src = text.toCharArray();
    char[] dest = src;
    if (((options & 0x3) == 0) && ((options & 0x18) == 16))
    {

      dest = new char[src.length * 2];
    }
    int len = shape(src, 0, src.length, dest, 0, dest.length);
    
    return new String(dest, 0, len);
  }
  















  public ArabicShaping(int options)
  {
    this.options = options;
    if ((options & 0xE0) > 128) {
      throw new IllegalArgumentException("bad DIGITS options");
    }
    isLogical = ((options & 0x4) == 0);
  }
  



































































































































































  public boolean equals(Object rhs)
  {
    return (rhs != null) && (rhs.getClass() == ArabicShaping.class) && (options == options);
  }
  





  public int hashCode()
  {
    return options;
  }
  


  public String toString()
  {
    StringBuffer buf = new StringBuffer(super.toString());
    buf.append('[');
    switch (options & 0x3) {
    case 0:  buf.append("grow/shrink"); break;
    case 1:  buf.append("spaces near"); break;
    case 2:  buf.append("spaces at end"); break;
    case 3:  buf.append("spaces at beginning");
    }
    switch (options & 0x4) {
    case 0:  buf.append(", logical"); break;
    case 4:  buf.append(", visual");
    }
    switch (options & 0x18) {
    case 0:  buf.append(", no letter shaping"); break;
    case 8:  buf.append(", shape letters"); break;
    case 24:  buf.append(", shape letters tashkeel isolated"); break;
    case 16:  buf.append(", unshape letters");
    }
    switch (options & 0xE0) {
    case 0:  buf.append(", no digit shaping"); break;
    case 32:  buf.append(", shape digits to AN"); break;
    case 64:  buf.append(", shape digits to EN"); break;
    case 96:  buf.append(", shape digits to AN contextually: default EN"); break;
    case 128:  buf.append(", shape digits to AN contextually: default AL");
    }
    switch (options & 0x100) {
    case 0:  buf.append(", standard Arabic-Indic digits"); break;
    case 256:  buf.append(", extended Arabic-Indic digits");
    }
    buf.append("]");
    
    return buf.toString();
  }
  













  private static final int[] irrelevantPos = { 0, 2, 4, 6, 8, 10, 12, 14 };
  


  private static final char[] convertLamAlef = { 'آ', 'آ', 'أ', 'أ', 'إ', 'إ', 'ا', 'ا' };
  









  private static final char[] convertNormalizedLamAlef = { 'آ', 'أ', 'إ', 'ا' };
  





  private static final int[] araLink = { 4385, 4897, 5377, 5921, 6403, 7457, 7939, 8961, 9475, 10499, 11523, 12547, 13571, 14593, 15105, 15617, 16129, 16643, 17667, 18691, 19715, 20739, 21763, 22787, 23811, 0, 0, 0, 0, 0, 3, 24835, 25859, 26883, 27923, 28931, 29955, 30979, 32001, 32513, 33027, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 34049, 34561, 35073, 35585, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 33, 33, 0, 33, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 3, 3, 3, 3, 1, 1 };
  












































































  private static final int[] presLink = { 3, 3, 3, 0, 3, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 32, 33, 32, 33, 0, 1, 32, 33, 0, 2, 3, 1, 32, 33, 0, 2, 3, 1, 0, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 16, 18, 19, 17, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 1, 0, 1, 0, 2, 3, 1, 0, 1, 0, 1, 0, 1, 0, 1 };
  







































  private static int[] convertFEto06 = { 1611, 1611, 1612, 1612, 1613, 1613, 1614, 1614, 1615, 1615, 1616, 1616, 1617, 1617, 1618, 1618, 1569, 1570, 1570, 1571, 1571, 1572, 1572, 1573, 1573, 1574, 1574, 1574, 1574, 1575, 1575, 1576, 1576, 1576, 1576, 1577, 1577, 1578, 1578, 1578, 1578, 1579, 1579, 1579, 1579, 1580, 1580, 1580, 1580, 1581, 1581, 1581, 1581, 1582, 1582, 1582, 1582, 1583, 1583, 1584, 1584, 1585, 1585, 1586, 1586, 1587, 1587, 1587, 1587, 1588, 1588, 1588, 1588, 1589, 1589, 1589, 1589, 1590, 1590, 1590, 1590, 1591, 1591, 1591, 1591, 1592, 1592, 1592, 1592, 1593, 1593, 1593, 1593, 1594, 1594, 1594, 1594, 1601, 1601, 1601, 1601, 1602, 1602, 1602, 1602, 1603, 1603, 1603, 1603, 1604, 1604, 1604, 1604, 1605, 1605, 1605, 1605, 1606, 1606, 1606, 1606, 1607, 1607, 1607, 1607, 1608, 1608, 1609, 1609, 1610, 1610, 1610, 1610, 1628, 1628, 1629, 1629, 1630, 1630, 1631, 1631 };
  











  private static final int[][][] shapeTable = { { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 1, 0, 3 }, { 0, 1, 0, 1 } }, { { 0, 0, 2, 2 }, { 0, 0, 1, 2 }, { 0, 1, 1, 2 }, { 0, 1, 1, 3 } }, { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 1, 0, 3 }, { 0, 1, 0, 3 } }, { { 0, 0, 1, 2 }, { 0, 0, 1, 2 }, { 0, 1, 1, 2 }, { 0, 1, 1, 3 } } };
  














  private void shapeToArabicDigitsWithContext(char[] dest, int start, int length, char digitBase, boolean lastStrongWasAL)
  {
    digitBase = (char)(digitBase - '0');
    
    int i = start + length;
    do { char ch = dest[i];
      switch (UCharacter.getDirection(ch)) {
      case 0: 
      case 1: 
        lastStrongWasAL = false;
        break;
      case 13: 
        lastStrongWasAL = true;
        break;
      case 2: 
        if ((lastStrongWasAL) && (ch <= '9')) {
          dest[i] = ((char)(ch + digitBase));
        }
        break;
      }
      i--; } while (i >= start);
  }
  


























  private static void invertBuffer(char[] buffer, int start, int length)
  {
    int i = start; for (int j = start + length - 1; i < j; j--) {
      char temp = buffer[i];
      buffer[i] = buffer[j];
      buffer[j] = temp;i++;
    }
  }
  









  private static char changeLamAlef(char ch)
  {
    switch (ch) {
    case 'آ':  return 'ٜ';
    case 'أ':  return 'ٝ';
    case 'إ':  return 'ٞ';
    case 'ا':  return 'ٟ'; }
    return '\000';
  }
  





  private static int specialChar(char ch)
  {
    if (((ch > 'ء') && (ch < 'ئ')) || (ch == 'ا') || ((ch > 'خ') && (ch < 'س')) || ((ch > 'ه') && (ch < 'ي')) || (ch == 'ة'))
    {



      return 1; }
    if ((ch >= 'ً') && (ch <= 'ْ'))
      return 2;
    if (((ch >= 'ٓ') && (ch <= 'ٕ')) || (ch == 'ٰ') || ((ch >= 65136) && (ch <= 65151)))
    {

      return 3;
    }
    return 0;
  }
  






  private static int getLink(char ch)
  {
    if ((ch >= 'آ') && (ch <= 'ۓ'))
      return araLink[(ch - 'آ')];
    if (ch == '‍')
      return 3;
    if ((ch >= '⁭') && (ch <= '⁯'))
      return 4;
    if ((ch >= 65136) && (ch <= 65276)) {
      return presLink[(ch - 65136)];
    }
    return 0;
  }
  







  private static int countSpacesLeft(char[] dest, int start, int count)
  {
    int i = start; for (int e = start + count; i < e; i++) {
      if (dest[i] != ' ') {
        return i - start;
      }
    }
    return count;
  }
  


  private static int countSpacesRight(char[] dest, int start, int count)
  {
    int i = start + count;
    do { if (dest[i] != ' ') {
        return start + count - 1 - i;
      }
      i--; } while (i >= start);
    



    return count;
  }
  



  private static boolean isTashkeelChar(char ch)
  {
    return (ch >= 'ً') && (ch <= 'ْ');
  }
  



  private static boolean isAlefChar(char ch)
  {
    return (ch == 'آ') || (ch == 'أ') || (ch == 'إ') || (ch == 'ا');
  }
  



  private static boolean isLamAlefChar(char ch)
  {
    return (ch >= 65269) && (ch <= 65276);
  }
  
  private static boolean isNormalizedLamAlefChar(char ch) {
    return (ch >= 'ٜ') && (ch <= 'ٟ');
  }
  







  private int calculateSize(char[] source, int sourceStart, int sourceLength)
  {
    int destSize = sourceLength;
    
    switch (options & 0x18) {
    case 8: 
    case 24: 
      if (isLogical) {
        int i = sourceStart; for (int e = sourceStart + sourceLength - 1; i < e; i++) {
          if ((source[i] == 'ل') && (isAlefChar(source[(i + 1)]))) {
            destSize--;
          }
        }
      } else {
        int i = sourceStart + 1; for (int e = sourceStart + sourceLength; i < e; i++) {
          if ((source[i] == 'ل') && (isAlefChar(source[(i - 1)]))) {
            destSize--;
          }
        }
      }
      break;
    
    case 16: 
      int i = sourceStart;int e = sourceStart + sourceLength;
      for (;;) { if (isLamAlefChar(source[i])) {
          destSize++;
        }
        i++; if (i >= e) {
          break;
        }
      }
    }
    
    




    return destSize;
  }
  










  private int removeLamAlefSpaces(char[] dest, int start, int length)
  {
    int lenOptions = options & 0x3;
    if (!isLogical) {
      switch (lenOptions) {
      case 3:  lenOptions = 2; break;
      case 2:  lenOptions = 3; break;
      }
      
    }
    
    if (lenOptions == 1) {
      int i = start; for (int e = i + length; i < e; i++) {
        if (dest[i] == 65535) {
          dest[i] = ' ';
        }
      }
    } else {
      int e = start + length;
      int w = e;
      int r = e;
      do {
        char ch = dest[r];
        if (ch != 65535) {
          w--;
          if (w != r) {
            dest[w] = ch;
          }
        }
        r--; } while (r >= start);
      








      if (lenOptions == 2) {
        while (w > start) {
          dest[(--w)] = ' ';
        }
      } else {
        if (w > start)
        {
          r = w;
          w = start;
          while (r < e) {
            dest[(w++)] = dest[(r++)];
          }
        } else {
          w = e;
        }
        if (lenOptions == 0) {
          length = w - start;
        } else {
          while (w < e) {
            dest[(w++)] = ' ';
          }
        }
      }
    }
    return length;
  }
  












  private int expandLamAlef(char[] dest, int start, int length, int lacount)
    throws ArabicShapingException
  {
    int lenOptions = options & 0x3;
    if (!isLogical) {
      switch (lenOptions) {
      case 3:  lenOptions = 2; break;
      case 2:  lenOptions = 3; break;
      }
      
    }
    
    switch (lenOptions)
    {
    case 0: 
      int r = start + length;int w = r + lacount;
      do { char ch = dest[r];
        if (isNormalizedLamAlefChar(ch)) {
          dest[(--w)] = 'ل';
          dest[(--w)] = convertNormalizedLamAlef[(ch - 'ٜ')];
        } else {
          dest[(--w)] = ch;
        }
        r--; } while (r >= start);
      








      length += lacount;
      break;
    

    case 1: 
      if (isNormalizedLamAlefChar(dest[start])) {
        throw new ArabicShapingException("no space for lamalef");
      }
      int i = start + length;
      do { char ch = dest[i];
        if (isNormalizedLamAlefChar(ch)) {
          if (dest[(i - 1)] == ' ') {
            dest[i] = 'ل';
            dest[(--i)] = convertNormalizedLamAlef[(ch - 'ٜ')];
          } else {
            throw new ArabicShapingException("no space for lamalef");
          }
        }
        i--; } while (i > start);
      










      break;
    

    case 2: 
      if (lacount > countSpacesLeft(dest, start, length)) {
        throw new ArabicShapingException("no space for lamalef");
      }
      int r = start + lacount;int w = start; for (int e = start + length; r < e; r++) {
        char ch = dest[r];
        if (isNormalizedLamAlefChar(ch)) {
          dest[(w++)] = convertNormalizedLamAlef[(ch - 'ٜ')];
          dest[(w++)] = 'ل';
        } else {
          dest[(w++)] = ch;
        }
      }
      
      break;
    

    case 3: 
      if (lacount > countSpacesRight(dest, start, length)) {
        throw new ArabicShapingException("no space for lamalef");
      }
      int r = start + length - lacount;int w = start + length;
      do { char ch = dest[r];
        if (isNormalizedLamAlefChar(ch)) {
          dest[(--w)] = 'ل';
          dest[(--w)] = convertNormalizedLamAlef[(ch - 'ٜ')];
        } else {
          dest[(--w)] = ch;
        }
        r--; } while (r >= start);
    }
    
    









    return length;
  }
  




  private int normalize(char[] dest, int start, int length)
  {
    int lacount = 0;
    int i = start; for (int e = i + length; i < e; i++) {
      char ch = dest[i];
      if ((ch >= 65136) && (ch <= 65276)) {
        if (isLamAlefChar(ch)) {
          lacount++;
        }
        dest[i] = ((char)convertFEto06[(ch - 65136)]);
      }
    }
    return lacount;
  }
  










  private int shapeUnicode(char[] dest, int start, int length, int destSize, int tashkeelFlag)
  {
    normalize(dest, start, length);
    





    boolean lamalef_found = false;
    int i = start + length - 1;
    int currLink = getLink(dest[i]);
    int nextLink = 0;
    int prevLink = 0;
    int lastLink = 0;
    int prevPos = i;
    int lastPos = i;
    int nx = -2;
    int nw = 0;
    
    while (i >= 0)
    {
      if (((currLink & 0xFF00) > 0) || (isTashkeelChar(dest[i]))) {
        nw = i - 1;
        nx = -2;
        while (nx < 0) {
          if (nw == -1) {
            nextLink = 0;
            nx = Integer.MAX_VALUE;
          } else {
            nextLink = getLink(dest[nw]);
            if ((nextLink & 0x4) == 0) {
              nx = nw;
            } else {
              nw--;
            }
          }
        }
        
        if (((currLink & 0x20) > 0) && ((lastLink & 0x10) > 0)) {
          lamalef_found = true;
          char wLamalef = changeLamAlef(dest[i]);
          if (wLamalef != 0)
          {
            dest[i] = 65535;
            dest[lastPos] = wLamalef;
            i = lastPos;
          }
          
          lastLink = prevLink;
          currLink = getLink(wLamalef);
        }
        




        int flag = specialChar(dest[i]);
        
        int shape = shapeTable[(nextLink & 0x3)][(lastLink & 0x3)][(currLink & 0x3)];
        


        if (flag == 1) {
          shape &= 0x1;
        } else if (flag == 2) {
          if ((tashkeelFlag == 0) && ((lastLink & 0x2) != 0) && ((nextLink & 0x1) != 0) && (dest[i] != 'ٌ') && (dest[i] != 'ٍ') && (((nextLink & 0x20) != 32) || ((lastLink & 0x10) != 16)))
          {






            shape = 1;
          } else {
            shape = 0;
          }
        }
        
        if (flag == 2) {
          if (tashkeelFlag < 2) {
            dest[i] = ((char)(65136 + irrelevantPos[(dest[i] - 'ً')] + shape));
          }
        } else {
          dest[i] = ((char)(65136 + (currLink >> 8) + shape));
        }
      }
      

      if ((currLink & 0x4) == 0) {
        prevLink = lastLink;
        lastLink = currLink;
        prevPos = lastPos;
        lastPos = i;
      }
      
      i--;
      if (i == nx) {
        currLink = nextLink;
        nx = -2;
      } else if (i != -1) {
        currLink = getLink(dest[i]);
      }
    }
    



    if (lamalef_found) {
      destSize = removeLamAlefSpaces(dest, start, length);
    } else {
      destSize = length;
    }
    
    return destSize;
  }
  







  private int deShapeUnicode(char[] dest, int start, int length, int destSize)
    throws ArabicShapingException
  {
    int lamalef_count = normalize(dest, start, length);
    

    if (lamalef_count != 0)
    {
      destSize = expandLamAlef(dest, start, length, lamalef_count);
    } else {
      destSize = length;
    }
    
    return destSize;
  }
  




  private int internalShape(char[] source, int sourceStart, int sourceLength, char[] dest, int destStart, int destSize)
    throws ArabicShapingException
  {
    if (sourceLength == 0) {
      return 0;
    }
    
    if (destSize == 0) {
      if (((options & 0x18) != 0) && ((options & 0x3) == 0))
      {

        return calculateSize(source, sourceStart, sourceLength);
      }
      return sourceLength;
    }
    


    char[] temp = new char[sourceLength * 2];
    System.arraycopy(source, sourceStart, temp, 0, sourceLength);
    
    if (isLogical) {
      invertBuffer(temp, 0, sourceLength);
    }
    
    int outputSize = sourceLength;
    
    switch (options & 0x18) {
    case 24: 
      outputSize = shapeUnicode(temp, 0, sourceLength, destSize, 1);
      break;
    
    case 8: 
      outputSize = shapeUnicode(temp, 0, sourceLength, destSize, 0);
      break;
    
    case 16: 
      outputSize = deShapeUnicode(temp, 0, sourceLength, destSize);
      break;
    }
    
    


    if (outputSize > destSize) {
      throw new ArabicShapingException("not enough room for result data");
    }
    
    if ((options & 0xE0) != 0) {
      char digitBase = '0';
      switch (options & 0x100) {
      case 0: 
        digitBase = '٠';
        break;
      
      case 256: 
        digitBase = '۰';
        break;
      }
      
      


      switch (options & 0xE0)
      {
      case 32: 
        int digitDelta = digitBase - '0';
        for (int i = 0; i < outputSize; i++) {
          char ch = temp[i];
          if ((ch <= '9') && (ch >= '0')) {
            int tmp334_332 = i; char[] tmp334_330 = temp;tmp334_330[tmp334_332] = ((char)(tmp334_330[tmp334_332] + digitDelta));
          }
        }
        
        break;
      

      case 64: 
        char digitTop = (char)(digitBase + '\t');
        int digitDelta = '0' - digitBase;
        for (int i = 0; i < outputSize; i++) {
          char ch = temp[i];
          if ((ch <= digitTop) && (ch >= digitBase)) {
            int tmp400_398 = i; char[] tmp400_396 = temp;tmp400_396[tmp400_398] = ((char)(tmp400_396[tmp400_398] + digitDelta));
          }
        }
        
        break;
      
      case 96: 
        shapeToArabicDigitsWithContext(temp, 0, outputSize, digitBase, false);
        break;
      
      case 128: 
        shapeToArabicDigitsWithContext(temp, 0, outputSize, digitBase, true);
        break;
      }
      
    }
    


    if (isLogical) {
      invertBuffer(temp, 0, outputSize);
    }
    
    System.arraycopy(temp, 0, dest, destStart, outputSize);
    
    return outputSize;
  }
}
