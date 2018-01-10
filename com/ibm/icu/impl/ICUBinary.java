package com.ibm.icu.impl;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;






































































public final class ICUBinary
{
  private static final byte MAGIC1 = -38;
  private static final byte MAGIC2 = 39;
  private static final byte BIG_ENDIAN_ = 1;
  private static final byte CHAR_SET_ = 0;
  private static final byte CHAR_SIZE_ = 2;
  private static final String MAGIC_NUMBER_AUTHENTICATION_FAILED_ = "ICU data file error: Not an ICU data file";
  private static final String HEADER_AUTHENTICATION_FAILED_ = "ICU data file error: Header authentication failed, please check if you have a valid ICU data file";
  
  public ICUBinary() {}
  
  public static final byte[] readHeader(InputStream inputStream, byte[] dataFormatIDExpected, Authenticate authenticate)
    throws IOException
  {
    DataInputStream input = new DataInputStream(inputStream);
    char headersize = input.readChar();
    headersize = (char)(headersize - '\002');
    
    byte magic1 = input.readByte();
    headersize = (char)(headersize - '\001');
    byte magic2 = input.readByte();
    headersize = (char)(headersize - '\001');
    if ((magic1 != -38) || (magic2 != 39)) {
      throw new IOException("ICU data file error: Not an ICU data file");
    }
    
    input.readChar();
    headersize = (char)(headersize - '\002');
    input.readChar();
    headersize = (char)(headersize - '\002');
    byte bigendian = input.readByte();
    headersize = (char)(headersize - '\001');
    byte charset = input.readByte();
    headersize = (char)(headersize - '\001');
    byte charsize = input.readByte();
    headersize = (char)(headersize - '\001');
    input.readByte();
    headersize = (char)(headersize - '\001');
    
    byte[] dataFormatID = new byte[4];
    input.readFully(dataFormatID);
    headersize = (char)(headersize - '\004');
    byte[] dataVersion = new byte[4];
    input.readFully(dataVersion);
    headersize = (char)(headersize - '\004');
    byte[] unicodeVersion = new byte[4];
    input.readFully(unicodeVersion);
    headersize = (char)(headersize - '\004');
    input.skipBytes(headersize);
    
    if ((bigendian != 1) || (charset != 0) || (charsize != 2) || (!Arrays.equals(dataFormatIDExpected, dataFormatID)) || ((authenticate != null) && (!authenticate.isDataVersionAcceptable(dataVersion))))
    {



      throw new IOException("ICU data file error: Header authentication failed, please check if you have a valid ICU data file");
    }
    return unicodeVersion;
  }
  
  public static abstract interface Authenticate
  {
    public abstract boolean isDataVersionAcceptable(byte[] paramArrayOfByte);
  }
}
