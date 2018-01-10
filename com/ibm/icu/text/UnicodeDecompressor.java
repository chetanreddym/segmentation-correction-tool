package com.ibm.icu.text;
















































































public final class UnicodeDecompressor
  implements SCSU
{
  private int fCurrentWindow = 0;
  

  private int[] fOffsets = new int[8];
  

  private int fMode = 0;
  

  private static final int BUFSIZE = 3;
  

  private byte[] fBuffer = new byte[3];
  

  private int fBufferLength = 0;
  







  public UnicodeDecompressor()
  {
    reset();
  }
  







  public static String decompress(byte[] buffer)
  {
    char[] buf = decompress(buffer, 0, buffer.length);
    return new String(buf);
  }
  











  public static char[] decompress(byte[] buffer, int start, int limit)
  {
    UnicodeDecompressor comp = new UnicodeDecompressor();
    



    int len = Math.max(2, 2 * (limit - start));
    char[] temp = new char[len];
    
    int charCount = comp.decompress(buffer, start, limit, null, temp, 0, len);
    

    char[] result = new char[charCount];
    System.arraycopy(temp, 0, result, 0, charCount);
    return result;
  }
  



























  public int decompress(byte[] byteBuffer, int byteBufferStart, int byteBufferLimit, int[] bytesRead, char[] charBuffer, int charBufferStart, int charBufferLimit)
  {
    int bytePos = byteBufferStart;
    

    int ucPos = charBufferStart;
    

    int aByte = 0;
    


    if ((charBuffer.length < 2) || (charBufferLimit - charBufferStart < 2)) {
      throw new IllegalArgumentException("charBuffer.length < 2");
    }
    

    if (fBufferLength > 0)
    {
      int newBytes = 0;
      

      if (fBufferLength != 3) {
        newBytes = fBuffer.length - fBufferLength;
        

        if (byteBufferLimit - byteBufferStart < newBytes) {
          newBytes = byteBufferLimit - byteBufferStart;
        }
        System.arraycopy(byteBuffer, byteBufferStart, fBuffer, fBufferLength, newBytes);
      }
      


      fBufferLength = 0;
      

      int count = decompress(fBuffer, 0, fBuffer.length, null, charBuffer, charBufferStart, charBufferLimit);
      



      ucPos += count;
      bytePos += newBytes;
    }
    


    while ((bytePos < byteBufferLimit) && (ucPos < charBufferLimit)) {
      switch (fMode)
      {
      case 0: 
        do
        {
          aByte = byteBuffer[(bytePos++)] & 0xFF;
          switch (aByte) {
          case 128: case 129: case 130: case 131: 
          case 132: case 133: case 134: case 135: 
          case 136: case 137: case 138: case 139: 
          case 140: case 141: case 142: case 143: 
          case 144: case 145: case 146: case 147: 
          case 148: case 149: case 150: case 151: 
          case 152: case 153: case 154: case 155: 
          case 156: case 157: case 158: case 159: 
          case 160: case 161: case 162: case 163: 
          case 164: case 165: case 166: case 167: 
          case 168: case 169: case 170: case 171: 
          case 172: case 173: case 174: case 175: 
          case 176: case 177: case 178: case 179: 
          case 180: case 181: case 182: case 183: 
          case 184: case 185: case 186: case 187: 
          case 188: case 189: case 190: case 191: 
          case 192: case 193: case 194: case 195: 
          case 196: case 197: case 198: case 199: 
          case 200: case 201: case 202: case 203: 
          case 204: case 205: case 206: case 207: 
          case 208: case 209: case 210: case 211: 
          case 212: case 213: case 214: case 215: 
          case 216: case 217: case 218: case 219: 
          case 220: case 221: case 222: case 223: 
          case 224: case 225: case 226: case 227: 
          case 228: case 229: case 230: case 231: 
          case 232: case 233: case 234: case 235: 
          case 236: case 237: case 238: case 239: 
          case 240: case 241: case 242: case 243: 
          case 244: case 245: case 246: case 247: 
          case 248: case 249: case 250: case 251: 
          case 252: case 253: case 254: case 255: 
            if (fOffsets[fCurrentWindow] <= 65535) {
              charBuffer[(ucPos++)] = ((char)(aByte + fOffsets[fCurrentWindow] - 128));




            }
            else
            {




              if (ucPos + 1 >= charBufferLimit) {
                bytePos--;
                System.arraycopy(byteBuffer, bytePos, fBuffer, 0, byteBufferLimit - bytePos);
                

                fBufferLength = (byteBufferLimit - bytePos);
                bytePos += fBufferLength;
                
                break label2307;
              }
              int normalizedBase = fOffsets[fCurrentWindow] - 65536;
              
              charBuffer[(ucPos++)] = ((char)(55296 + (normalizedBase >> 10)));
              
              charBuffer[(ucPos++)] = ((char)(56320 + (normalizedBase & 0x3FF) + (aByte & 0x7F)));
            }
            
            break;
          case 0: case 9: case 10: case 13: 
          case 32: case 33: case 34: case 35: 
          case 36: case 37: case 38: case 39: 
          case 40: case 41: case 42: case 43: 
          case 44: case 45: case 46: case 47: 
          case 48: case 49: case 50: case 51: 
          case 52: case 53: case 54: case 55: 
          case 56: case 57: case 58: case 59: 
          case 60: case 61: case 62: case 63: 
          case 64: case 65: case 66: case 67: 
          case 68: case 69: case 70: case 71: 
          case 72: case 73: case 74: case 75: 
          case 76: case 77: case 78: case 79: 
          case 80: case 81: case 82: case 83: 
          case 84: case 85: case 86: case 87: 
          case 88: case 89: case 90: case 91: 
          case 92: case 93: case 94: case 95: 
          case 96: case 97: case 98: case 99: 
          case 100: case 101: case 102: case 103: 
          case 104: case 105: case 106: 
          case 107: case 108: case 109: 
          case 110: case 111: case 112: 
          case 113: case 114: case 115: 
          case 116: case 117: case 118: 
          case 119: case 120: case 121: 
          case 122: case 123: case 124: 
          case 125: case 126: case 127: 
            charBuffer[(ucPos++)] = ((char)aByte);
            break;
          



          case 14: 
            if (bytePos + 1 >= byteBufferLimit) {
              bytePos--;
              System.arraycopy(byteBuffer, bytePos, fBuffer, 0, byteBufferLimit - bytePos);
              

              fBufferLength = (byteBufferLimit - bytePos);
              bytePos += fBufferLength;
              
              break label2307;
            }
            aByte = byteBuffer[(bytePos++)];
            charBuffer[(ucPos++)] = ((char)(aByte << 8 | byteBuffer[(bytePos++)] & 0xFF));
            
            break;
          

          case 15: 
            fMode = 1;
            break;
          case 1: case 2: 
          case 3: 
          case 4: 
          case 5: 
          case 6: 
          case 7: 
          case 8: 
            if (bytePos >= byteBufferLimit) {
              bytePos--;
              System.arraycopy(byteBuffer, bytePos, fBuffer, 0, byteBufferLimit - bytePos);
              

              fBufferLength = (byteBufferLimit - bytePos);
              bytePos += fBufferLength;
              

              break label2307;
            }
            
            int dByte = byteBuffer[(bytePos++)] & 0xFF;
            charBuffer[(ucPos++)] = ((char)(dByte + ((dByte >= 0) && (dByte < 128) ? SCSU.sOffsets[(aByte - 1)] : fOffsets[(aByte - 1)] - 128)));
            



            break;
          case 16: case 17: 
          case 18: case 19: 
          case 20: case 21: 
          case 22: case 23: 
            fCurrentWindow = (aByte - 16);
            break;
          case 24: case 25: 
          case 26: case 27: 
          case 28: 
          case 29: 
          case 30: 
          case 31: 
            if (bytePos >= byteBufferLimit) {
              bytePos--;
              System.arraycopy(byteBuffer, bytePos, fBuffer, 0, byteBufferLimit - bytePos);
              

              fBufferLength = (byteBufferLimit - bytePos);
              bytePos += fBufferLength;
              
              break label2307;
            }
            fCurrentWindow = (aByte - 24);
            fOffsets[fCurrentWindow] = SCSU.sOffsetTable[(byteBuffer[(bytePos++)] & 0xFF)];
            
            break;
          



          case 11: 
            if (bytePos + 1 >= byteBufferLimit) {
              bytePos--;
              System.arraycopy(byteBuffer, bytePos, fBuffer, 0, byteBufferLimit - bytePos);
              

              fBufferLength = (byteBufferLimit - bytePos);
              bytePos += fBufferLength;
              
              break label2307;
            }
            aByte = byteBuffer[(bytePos++)] & 0xFF;
            fCurrentWindow = ((aByte & 0xE0) >> 5);
            fOffsets[fCurrentWindow] = (65536 + 128 * ((aByte & 0x1F) << 8 | byteBuffer[(bytePos++)] & 0xFF));
            

            break;
          }
          if (bytePos >= byteBufferLimit) break; } while (ucPos < charBufferLimit);
        





































































































































































































        break;
      


      case 1: 
        while ((bytePos < byteBufferLimit) && (ucPos < charBufferLimit)) {
          aByte = byteBuffer[(bytePos++)] & 0xFF;
          switch (aByte) {
          case 232: case 233: 
          case 234: case 235: 
          case 236: case 237: 
          case 238: 
          case 239: 
            if (bytePos >= byteBufferLimit) {
              bytePos--;
              System.arraycopy(byteBuffer, bytePos, fBuffer, 0, byteBufferLimit - bytePos);
              

              fBufferLength = (byteBufferLimit - bytePos);
              bytePos += fBufferLength;
              
              break label2307;
            }
            fCurrentWindow = (aByte - 232);
            fOffsets[fCurrentWindow] = SCSU.sOffsetTable[(byteBuffer[(bytePos++)] & 0xFF)];
            
            fMode = 0;
            break;
          




          case 241: 
            if (bytePos + 1 >= byteBufferLimit) {
              bytePos--;
              System.arraycopy(byteBuffer, bytePos, fBuffer, 0, byteBufferLimit - bytePos);
              

              fBufferLength = (byteBufferLimit - bytePos);
              bytePos += fBufferLength;
              
              break label2307;
            }
            aByte = byteBuffer[(bytePos++)] & 0xFF;
            fCurrentWindow = ((aByte & 0xE0) >> 5);
            fOffsets[fCurrentWindow] = (65536 + 128 * ((aByte & 0x1F) << 8 | byteBuffer[(bytePos++)] & 0xFF));
            

            fMode = 0;
            break;
          case 224: case 225: 
          case 226: case 227: 
          case 228: case 229: 
          case 230: 
          case 231: 
            fCurrentWindow = (aByte - 224);
            fMode = 0;
            break;
          




          case 240: 
            if (bytePos >= byteBufferLimit - 1) {
              bytePos--;
              System.arraycopy(byteBuffer, bytePos, fBuffer, 0, byteBufferLimit - bytePos);
              

              fBufferLength = (byteBufferLimit - bytePos);
              bytePos += fBufferLength;
              
              break label2307;
            }
            aByte = byteBuffer[(bytePos++)];
            charBuffer[(ucPos++)] = ((char)(aByte << 8 | byteBuffer[(bytePos++)] & 0xFF));
            
            break;
          


          default: 
            if (bytePos >= byteBufferLimit) {
              bytePos--;
              System.arraycopy(byteBuffer, bytePos, fBuffer, 0, byteBufferLimit - bytePos);
              

              fBufferLength = (byteBufferLimit - bytePos);
              bytePos += fBufferLength;
              
              break label2307;
            }
            charBuffer[(ucPos++)] = ((char)(aByte << 8 | byteBuffer[(bytePos++)] & 0xFF));
          }
          
        }
      }
      
    }
    

    label2307:
    

    if (bytesRead != null) {
      bytesRead[0] = (bytePos - byteBufferStart);
    }
    
    return ucPos - charBufferStart;
  }
  





  public void reset()
  {
    fOffsets[0] = 128;
    fOffsets[1] = 192;
    fOffsets[2] = 1024;
    fOffsets[3] = 1536;
    fOffsets[4] = 2304;
    fOffsets[5] = 12352;
    fOffsets[6] = 12448;
    fOffsets[7] = 65280;
    

    fCurrentWindow = 0;
    fMode = 0;
    fBufferLength = 0;
  }
}