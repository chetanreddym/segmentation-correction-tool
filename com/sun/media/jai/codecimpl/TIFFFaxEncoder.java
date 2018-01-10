package com.sun.media.jai.codecimpl;








class TIFFFaxEncoder
{
  private static final int WHITE = 0;
  






  private static final int BLACK = 1;
  






  private static byte[] byteTable = { 8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
  




















  private static int[] termCodesBlack = { 230686730, 1073741827, -1073741822, -2147483646, 1610612739, 805306372, 536870916, 402653189, 335544326, 268435462, 134217735, 167772167, 234881031, 67108872, 117440520, 201326601, 96469002, 100663306, 33554442, 216006667, 218103819, 226492427, 115343371, 83886091, 48234507, 50331659, 211812364, 212860940, 213909516, 214958092, 109051916, 110100492, 111149068, 112197644, 220200972, 221249548, 222298124, 223346700, 224395276, 225443852, 113246220, 114294796, 228589580, 229638156, 88080396, 89128972, 90177548, 91226124, 104857612, 105906188, 85983244, 87031820, 37748748, 57671692, 58720268, 40894476, 41943052, 92274700, 93323276, 45088780, 46137356, 94371852, 106954764, 108003340 };
  




















  private static int[] termCodesWhite = { 889192456, 469762054, 1879048196, -2147483644, -1342177276, -1073741820, -536870908, -268435452, -1744830459, -1610612731, 939524101, 1073741829, 536870918, 201326598, -805306362, -738197498, -1476395002, -1409286138, 1308622855, 402653191, 268435463, 771751943, 100663303, 134217735, 1342177287, 1442840583, 637534215, 1207959559, 805306375, 33554440, 50331656, 436207624, 452984840, 301989896, 318767112, 335544328, 352321544, 369098760, 385875976, 671088648, 687865864, 704643080, 721420296, 738197512, 754974728, 67108872, 83886088, 167772168, 184549384, 1375731720, 1392508936, 1409286152, 1426063368, 603979784, 620757000, 1476395016, 1493172232, 1509949448, 1526726664, 1241513992, 1258291208, 838860808, 855638024, 872415240 };
  




















  private static int[] makeupCodesBlack = { 0, 62914570, 209715212, 210763788, 95420428, 53477388, 54525964, 55574540, 56623117, 57147405, 38797325, 39321613, 39845901, 40370189, 59768845, 60293133, 60817421, 61341709, 61865997, 62390285, 42991629, 43515917, 44040205, 44564493, 47185933, 47710221, 52428813, 52953101, 16777227, 25165835, 27262987, 18874380, 19922956, 20971532, 22020108, 23068684, 24117260, 29360140, 30408716, 31457292, 32505868, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
  



















  private static int[] makeupCodesWhite = { 0, -671088635, -1879048187, 1543503878, 1845493767, 905969672, 922746888, 1677721608, 1694498824, 1744830472, 1728053256, 1711276041, 1719664649, 1761607689, 1769996297, 1778384905, 1786773513, 1795162121, 1803550729, 1811939337, 1820327945, 1828716553, 1837105161, 1275068425, 1283457033, 1291845641, 1610612742, 1300234249, 16777227, 25165835, 27262987, 18874380, 19922956, 20971532, 22020108, 23068684, 24117260, 29360140, 30408716, 31457292, 32505868, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
  



















  private static int[] passMode = { 268435460 };
  





  private static int[] vertMode = { 100663303, 201326598, 1610612739, -2147483647, 1073741827, 134217734, 67108871 };
  











  private static int[] horzMode = { 536870915 };
  





  private static int[][] termCodes = { termCodesWhite, termCodesBlack };
  




  private static int[][] makeupCodes = { makeupCodesWhite, makeupCodesBlack };
  




  private static int[][] pass = { passMode, passMode };
  



  private static int[][] vert = { vertMode, vertMode };
  



  private static int[][] horz = { horzMode, horzMode };
  




  private boolean inverseFill;
  




  private int bits;
  




  private int ndex;
  




  TIFFFaxEncoder(boolean inverseFill)
  {
    this.inverseFill = inverseFill;
  }
  







  private int nextState(byte[] data, int base, int bitOffset, int maxOffset)
  {
    if (data == null) {
      return maxOffset;
    }
    
    int next = base + (bitOffset >>> 3);
    

    if (next >= data.length) {
      return maxOffset;
    }
    int end = base + (maxOffset >>> 3);
    if (end == data.length) {
      end--;
    }
    int extra = bitOffset & 0x7;
    


    if ((data[next] & 128 >>> extra) != 0) {
      int testbyte = (data[next] ^ 0xFFFFFFFF) & 255 >>> extra;
      while ((next < end) && 
        (testbyte == 0))
      {

        testbyte = (data[(++next)] ^ 0xFFFFFFFF) & 0xFF; }
    }
    int testbyte;
    if ((testbyte = data[next] & 255 >>> extra) != 0) {
      bitOffset = (next - base) * 8 + byteTable[testbyte];
      return bitOffset < maxOffset ? bitOffset : maxOffset;
    }
    while (next < end) {
      if ((testbyte = data[(++next)] & 0xFF) != 0)
      {
        bitOffset = (next - base) * 8 + byteTable[testbyte];
        return bitOffset < maxOffset ? bitOffset : maxOffset;
      }
    }
    
    bitOffset = (next - base) * 8 + byteTable[testbyte];
    return bitOffset < maxOffset ? bitOffset : maxOffset;
  }
  



  private void initBitBuf()
  {
    ndex = 0;
    bits = 0;
  }
  








  private int add1DBits(byte[] buf, int where, int count, int color)
  {
    int len = where;
    
    int sixtyfours = count >>> 6;
    count &= 0x3F;
    if (sixtyfours != 0) {
      for (; sixtyfours > 40; sixtyfours -= 40) {
        int mask = makeupCodes[color][40];
        bits |= (mask & 0xFFF80000) >>> ndex;
        ndex += (mask & 0xFFFF);
        while (ndex > 7) {
          buf[(len++)] = ((byte)(bits >>> 24));
          bits <<= 8;
          ndex -= 8;
        }
      }
      
      int mask = makeupCodes[color][sixtyfours];
      bits |= (mask & 0xFFF80000) >>> ndex;
      ndex += (mask & 0xFFFF);
      while (ndex > 7) {
        buf[(len++)] = ((byte)(bits >>> 24));
        bits <<= 8;
        ndex -= 8;
      }
    }
    
    int mask = termCodes[color][count];
    bits |= (mask & 0xFFF80000) >>> ndex;
    ndex += (mask & 0xFFFF);
    while (ndex > 7) {
      buf[(len++)] = ((byte)(bits >>> 24));
      bits <<= 8;
      ndex -= 8;
    }
    
    return len - where;
  }
  







  private int add2DBits(byte[] buf, int where, int[][] mode, int entry)
  {
    int len = where;
    int color = 0;
    
    int mask = mode[color][entry];
    bits |= (mask & 0xFFF80000) >>> ndex;
    ndex += (mask & 0xFFFF);
    while (ndex > 7) {
      buf[(len++)] = ((byte)(bits >>> 24));
      bits <<= 8;
      ndex -= 8;
    }
    
    return len - where;
  }
  








  private int addEOL(boolean is1DMode, boolean addFill, boolean add1, byte[] buf, int where)
  {
    int len = where;
    





    if (addFill)
    {






      ndex += (ndex <= 4 ? 4 - ndex : 12 - ndex);
    }
    



    if (is1DMode) {
      bits |= 1048576 >>> ndex;
      ndex += 12;
    } else {
      bits |= (add1 ? 1572864 : 1048576) >>> ndex;
      ndex += 13;
    }
    
    while (ndex > 7) {
      buf[(len++)] = ((byte)(bits >>> 24));
      bits <<= 8;
      ndex -= 8;
    }
    
    return len - where;
  }
  





  private int addEOFB(byte[] buf, int where)
  {
    int len = where;
    



    bits |= 1048832 >>> ndex;
    



    ndex += 24;
    



    while (ndex > 0) {
      buf[(len++)] = ((byte)(bits >>> 24));
      bits <<= 8;
      ndex -= 8;
    }
    
    return len - where;
  }
  











  private int encode1D(byte[] data, int rowOffset, int colOffset, int rowLength, byte[] compData, int compOffset)
  {
    int lineAddr = rowOffset;
    int bitIndex = colOffset;
    
    int last = bitIndex + rowLength;
    int outIndex = compOffset;
    



    int testbit = (data[(lineAddr + (bitIndex >>> 3))] & 0xFF) >>> 7 - (bitIndex & 0x7) & 0x1;
    

    int currentColor = 1;
    if (testbit != 0) {
      outIndex += add1DBits(compData, outIndex, 0, 0);
    } else {
      currentColor = 0;
    }
    



    while (bitIndex < last) {
      int bitCount = nextState(data, lineAddr, bitIndex, last) - bitIndex;
      
      outIndex += add1DBits(compData, outIndex, bitCount, currentColor);
      
      bitIndex += bitCount;
      currentColor ^= 0x1;
    }
    
    return outIndex - compOffset;
  }
  


















  synchronized int encodeRLE(byte[] data, int rowOffset, int colOffset, int rowLength, byte[] compData)
  {
    initBitBuf();
    



    int outIndex = encode1D(data, rowOffset, colOffset, rowLength, compData, 0);
    




    while (ndex > 0) {
      compData[(outIndex++)] = ((byte)(bits >>> 24));
      bits <<= 8;
      ndex -= 8;
    }
    



    if (inverseFill) {
      byte[] flipTable = TIFFFaxDecoder.flipTable;
      for (int i = 0; i < outIndex; i++) {
        compData[i] = flipTable[(compData[i] & 0xFF)];
      }
    }
    
    return outIndex;
  }
  



























  synchronized int encodeT4(boolean is1DMode, boolean isEOLAligned, byte[] data, int lineStride, int colOffset, int width, int height, byte[] compData)
  {
    byte[] refData = data;
    int lineAddr = 0;
    int outIndex = 0;
    
    initBitBuf();
    
    int KParameter = 2;
    for (int numRows = 0; numRows < height; numRows++) {
      if ((is1DMode) || (numRows % KParameter == 0))
      {
        outIndex += addEOL(is1DMode, isEOLAligned, true, compData, outIndex);
        


        outIndex += encode1D(data, lineAddr, colOffset, width, compData, outIndex);
      }
      else
      {
        outIndex += addEOL(is1DMode, isEOLAligned, false, compData, outIndex);
        


        int refAddr = lineAddr - lineStride;
        

        int a0 = colOffset;
        int last = a0 + width;
        
        int testbit = (data[(lineAddr + (a0 >>> 3))] & 0xFF) >>> 7 - (a0 & 0x7) & 0x1;
        

        int a1 = testbit != 0 ? a0 : nextState(data, lineAddr, a0, last);
        

        testbit = (refData[(refAddr + (a0 >>> 3))] & 0xFF) >>> 7 - (a0 & 0x7) & 0x1;
        
        int b1 = testbit != 0 ? a0 : nextState(refData, refAddr, a0, last);
        


        int color = 0;
        for (;;)
        {
          int b2 = nextState(refData, refAddr, b1, last);
          if (b2 < a1) {
            outIndex += add2DBits(compData, outIndex, pass, 0);
            a0 = b2;
          } else {
            int tmp = b1 - a1 + 3;
            if ((tmp <= 6) && (tmp >= 0)) {
              outIndex += add2DBits(compData, outIndex, vert, tmp);
              
              a0 = a1;
            } else {
              int a2 = nextState(data, lineAddr, a1, last);
              outIndex += add2DBits(compData, outIndex, horz, 0);
              
              outIndex += add1DBits(compData, outIndex, a1 - a0, color);
              
              outIndex += add1DBits(compData, outIndex, a2 - a1, color ^ 0x1);
              
              a0 = a2;
            }
          }
          if (a0 >= last) {
            break;
          }
          color = (data[(lineAddr + (a0 >>> 3))] & 0xFF) >>> 7 - (a0 & 0x7) & 0x1;
          
          a1 = nextState(data, lineAddr, a0, last);
          b1 = nextState(refData, refAddr, a0, last);
          testbit = (refData[(refAddr + (b1 >>> 3))] & 0xFF) >>> 7 - (b1 & 0x7) & 0x1;
          
          if (testbit == color) {
            b1 = nextState(refData, refAddr, b1, last);
          }
        }
      }
      

      lineAddr += lineStride;
    }
    
    for (int i = 0; i < 6; i++) {
      outIndex += addEOL(is1DMode, isEOLAligned, true, compData, outIndex);
    }
    




    while (ndex > 0) {
      compData[(outIndex++)] = ((byte)(bits >>> 24));
      bits <<= 8;
      ndex -= 8;
    }
    

    if (inverseFill) {
      for (int i = 0; i < outIndex; i++) {
        compData[i] = TIFFFaxDecoder.flipTable[(compData[i] & 0xFF)];
      }
    }
    
    return outIndex;
  }
  























  public synchronized int encodeT6(byte[] data, int lineStride, int colOffset, int width, int height, byte[] compData)
  {
    byte[] refData = null;
    int refAddr = 0;
    int lineAddr = 0;
    int outIndex = 0;
    
    initBitBuf();
    



    while (height-- != 0) {
      int a0 = colOffset;
      int last = a0 + width;
      
      int testbit = (data[(lineAddr + (a0 >>> 3))] & 0xFF) >>> 7 - (a0 & 0x7) & 0x1;
      

      int a1 = testbit != 0 ? a0 : nextState(data, lineAddr, a0, last);
      

      testbit = refData == null ? 0 : (refData[(refAddr + (a0 >>> 3))] & 0xFF) >>> 7 - (a0 & 0x7) & 0x1;
      

      int b1 = testbit != 0 ? a0 : nextState(refData, refAddr, a0, last);
      




      int color = 0;
      for (;;)
      {
        int b2 = nextState(refData, refAddr, b1, last);
        if (b2 < a1) {
          outIndex += add2DBits(compData, outIndex, pass, 0);
          a0 = b2;
        } else {
          int tmp = b1 - a1 + 3;
          if ((tmp <= 6) && (tmp >= 0)) {
            outIndex += add2DBits(compData, outIndex, vert, tmp);
            a0 = a1;
          } else {
            int a2 = nextState(data, lineAddr, a1, last);
            outIndex += add2DBits(compData, outIndex, horz, 0);
            outIndex += add1DBits(compData, outIndex, a1 - a0, color);
            outIndex += add1DBits(compData, outIndex, a2 - a1, color ^ 0x1);
            a0 = a2;
          }
        }
        if (a0 >= last) {
          break;
        }
        color = (data[(lineAddr + (a0 >>> 3))] & 0xFF) >>> 7 - (a0 & 0x7) & 0x1;
        
        a1 = nextState(data, lineAddr, a0, last);
        b1 = nextState(refData, refAddr, a0, last);
        testbit = refData == null ? 0 : (refData[(refAddr + (b1 >>> 3))] & 0xFF) >>> 7 - (b1 & 0x7) & 0x1;
        

        if (testbit == color) {
          b1 = nextState(refData, refAddr, b1, last);
        }
      }
      
      refData = data;
      refAddr = lineAddr;
      lineAddr += lineStride;
    }
    




    outIndex += addEOFB(compData, outIndex);
    

    if (inverseFill) {
      for (int i = 0; i < outIndex; i++) {
        compData[i] = TIFFFaxDecoder.flipTable[(compData[i] & 0xFF)];
      }
    }
    
    return outIndex;
  }
}
