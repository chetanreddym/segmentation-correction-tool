package com.sun.media.jai.codecimpl;










public class TIFFLZWDecoder
{
  byte[][] stringTable;
  







  byte[] data = null;
  byte[] uncompData; int tableIndex; int bitsToGet = 9;
  int bytePointer;
  int bitPointer;
  int dstIndex;
  int w;
  int h; int predictor; int samplesPerPixel; int nextData = 0;
  int nextBits = 0;
  
  int[] andTable = { 511, 1023, 2047, 4095 };
  




  public TIFFLZWDecoder(int w, int predictor, int samplesPerPixel)
  {
    this.w = w;
    this.predictor = predictor;
    this.samplesPerPixel = samplesPerPixel;
  }
  







  public byte[] decode(byte[] data, byte[] uncompData, int h)
  {
    if ((data[0] == 0) && (data[1] == 1)) {
      throw new UnsupportedOperationException(JaiI18N.getString("TIFFLZWDecoder0"));
    }
    
    initializeStringTable();
    
    this.data = data;
    this.h = h;
    this.uncompData = uncompData;
    

    bytePointer = 0;
    bitPointer = 0;
    dstIndex = 0;
    

    nextData = 0;
    nextBits = 0;
    
    int oldCode = 0;
    

    int uncompDataLength = uncompData.length;
    int code;
    while (((code = getNextCode()) != 257) && (dstIndex < uncompDataLength))
    {
      if (code == 256)
      {
        initializeStringTable();
        code = getNextCode();
        
        if (code == 257) {
          break;
        }
        
        writeString(stringTable[code]);
        oldCode = code;


      }
      else if (code < tableIndex)
      {
        byte[] string = stringTable[code];
        
        writeString(string);
        addStringToTable(stringTable[oldCode], string[0]);
        oldCode = code;
      }
      else
      {
        byte[] string = stringTable[oldCode];
        string = composeString(string, string[0]);
        writeString(string);
        addStringToTable(string);
        oldCode = code;
      }
    }
    




    if (predictor == 2)
    {

      for (int j = 0; j < h; j++)
      {
        int count = samplesPerPixel * (j * w + 1);
        
        for (int i = samplesPerPixel; i < w * samplesPerPixel; i++)
        {
          int tmp288_286 = count; byte[] tmp288_285 = uncompData;tmp288_285[tmp288_286] = ((byte)(tmp288_285[tmp288_286] + uncompData[(count - samplesPerPixel)]));
          count++;
        }
      }
    }
    
    return uncompData;
  }
  




  public void initializeStringTable()
  {
    stringTable = new byte['က'][];
    
    for (int i = 0; i < 256; i++) {
      stringTable[i] = new byte[1];
      stringTable[i][0] = ((byte)i);
    }
    
    tableIndex = 258;
    bitsToGet = 9;
  }
  



  public void writeString(byte[] string)
  {
    if (dstIndex < uncompData.length) {
      int maxIndex = Math.min(string.length, uncompData.length - dstIndex);
      

      for (int i = 0; i < maxIndex; i++) {
        uncompData[(dstIndex++)] = string[i];
      }
    }
  }
  


  public void addStringToTable(byte[] oldString, byte newString)
  {
    int length = oldString.length;
    byte[] string = new byte[length + 1];
    System.arraycopy(oldString, 0, string, 0, length);
    string[length] = newString;
    

    stringTable[(tableIndex++)] = string;
    
    if (tableIndex == 511) {
      bitsToGet = 10;
    } else if (tableIndex == 1023) {
      bitsToGet = 11;
    } else if (tableIndex == 2047) {
      bitsToGet = 12;
    }
  }
  




  public void addStringToTable(byte[] string)
  {
    stringTable[(tableIndex++)] = string;
    
    if (tableIndex == 511) {
      bitsToGet = 10;
    } else if (tableIndex == 1023) {
      bitsToGet = 11;
    } else if (tableIndex == 2047) {
      bitsToGet = 12;
    }
  }
  


  public byte[] composeString(byte[] oldString, byte newString)
  {
    int length = oldString.length;
    byte[] string = new byte[length + 1];
    System.arraycopy(oldString, 0, string, 0, length);
    string[length] = newString;
    
    return string;
  }
  



  public int getNextCode()
  {
    try
    {
      nextData = (nextData << 8 | data[(bytePointer++)] & 0xFF);
      nextBits += 8;
      
      if (nextBits < bitsToGet) {
        nextData = (nextData << 8 | data[(bytePointer++)] & 0xFF);
        nextBits += 8;
      }
      
      int code = nextData >> nextBits - bitsToGet & andTable[(bitsToGet - 9)];
      
      nextBits -= bitsToGet;
      
      return code;
    }
    catch (ArrayIndexOutOfBoundsException e) {}
    return 257;
  }
}
