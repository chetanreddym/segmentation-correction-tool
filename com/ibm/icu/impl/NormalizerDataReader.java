package com.ibm.icu.impl;

import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;






































































































































































































































































































final class NormalizerDataReader
  implements ICUBinary.Authenticate
{
  private static final boolean debug = ICUDebug.enabled("NormalizerDataReader");
  

  private DataInputStream dataInputStream;
  


  protected NormalizerDataReader(InputStream inputStream)
    throws IOException
  {
    if (debug) { System.out.println("Bytes in inputStream " + inputStream.available());
    }
    ICUBinary.readHeader(inputStream, DATA_FORMAT_ID, this);
    
    if (debug) { System.out.println("Bytes left in inputStream " + inputStream.available());
    }
    dataInputStream = new DataInputStream(inputStream);
    
    if (debug) System.out.println("Bytes left in dataInputStream " + dataInputStream.available());
  }
  
  protected int[] readIndexes(int length)
    throws IOException
  {
    int[] indexes = new int[length];
    
    for (int i = 0; i < length; i++) {
      indexes[i] = dataInputStream.readInt();
    }
    return indexes;
  }
  














  protected void read(byte[] normBytes, byte[] fcdBytes, byte[] auxBytes, char[] extraData, char[] combiningTable, Object[] canonStartSets)
    throws IOException
  {
    dataInputStream.read(normBytes);
    



    for (int i = 0; i < extraData.length; i++) {
      extraData[i] = dataInputStream.readChar();
    }
    

    for (int i = 0; i < combiningTable.length; i++) {
      combiningTable[i] = dataInputStream.readChar();
    }
    

    dataInputStream.read(fcdBytes);
    


    dataInputStream.read(auxBytes);
    

    int[] canonStartSetsIndexes = new int[32];
    
    for (int i = 0; i < canonStartSetsIndexes.length; i++) {
      canonStartSetsIndexes[i] = dataInputStream.readChar();
    }
    
    char[] startSets = new char[canonStartSetsIndexes[0] - 32];
    
    for (int i = 0; i < startSets.length; i++) {
      startSets[i] = dataInputStream.readChar();
    }
    char[] bmpTable = new char[canonStartSetsIndexes[1]];
    for (int i = 0; i < bmpTable.length; i++) {
      bmpTable[i] = dataInputStream.readChar();
    }
    char[] suppTable = new char[canonStartSetsIndexes[2]];
    for (int i = 0; i < suppTable.length; i++) {
      suppTable[i] = dataInputStream.readChar();
    }
    canonStartSets[0] = canonStartSetsIndexes;
    canonStartSets[1] = startSets;
    canonStartSets[2] = bmpTable;
    canonStartSets[3] = suppTable;
  }
  
  public byte[] getDataFormatVersion() {
    return DATA_FORMAT_VERSION;
  }
  
  public boolean isDataVersionAcceptable(byte[] version)
  {
    return (version[0] == DATA_FORMAT_VERSION[0]) && (version[2] == DATA_FORMAT_VERSION[2]) && (version[3] == DATA_FORMAT_VERSION[3]);
  }
  















  private static final byte[] DATA_FORMAT_ID = { 78, 111, 114, 109 };
  
  private static final byte[] DATA_FORMAT_VERSION = { 2, 2, 5, 2 };
}
