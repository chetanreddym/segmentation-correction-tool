package com.lotontech.talk;

import java.io.DataInputStream;
import java.io.EOFException;

public class Allophone implements java.io.Serializable
{
  public long magicNumber = 0L;
  public long dataOffset = 0L;
  public long dataSize = 0L;
  public long encoding = 0L;
  public long sampleRate = 0L;
  public long channels = 0L;
  public byte[] data;
  
  public Allophone(DataInputStream paramDataInputStream, String paramString) throws Exception
  {
    magicNumber = (paramDataInputStream.readByte() * 16777216 + paramDataInputStream.readByte() * 65536 + paramDataInputStream.readByte() * 256 + paramDataInputStream.readByte());
    dataOffset = (paramDataInputStream.readByte() * 16777216 + paramDataInputStream.readByte() * 65536 + paramDataInputStream.readByte() * 256 + paramDataInputStream.readByte());
    dataSize = (paramDataInputStream.readByte() * 16777216 + paramDataInputStream.readByte() * 65536 + paramDataInputStream.readByte() * 256 + paramDataInputStream.readByte());
    encoding = (paramDataInputStream.readByte() * 16777216 + paramDataInputStream.readByte() * 65536 + paramDataInputStream.readByte() * 256 + paramDataInputStream.readByte());
    sampleRate = (paramDataInputStream.readByte() * 16777216 + paramDataInputStream.readByte() * 65536 + paramDataInputStream.readByte() * 256 + paramDataInputStream.readByte());
    channels = (paramDataInputStream.readByte() * 16777216 + paramDataInputStream.readByte() * 65536 + paramDataInputStream.readByte() * 256 + paramDataInputStream.readByte());
    
    System.out.println(paramString + " size=" + dataSize);
    System.out.println(paramString + " offset=" + dataOffset);
    
    byte[] arrayOfByte = new byte[100000];
    
    int i = 0;
    int j = 24;
    
    try
    {
      for (;;)
      {
        arrayOfByte[i] = paramDataInputStream.readByte();
        if (j >= dataOffset) i++;
        j++;
      }
    }
    catch (EOFException localEOFException)
    {
      i = (int)dataSize;
      data = new byte[i];
      for (int k = 0; k < i; k++) data[k] = arrayOfByte[k];
    }
  }
}
