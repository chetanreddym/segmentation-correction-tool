package com.drew.lang;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.SuppressWarnings;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

public class RandomAccessFileReader
  implements BufferReader
{
  @NotNull
  private final RandomAccessFile _file;
  private final long _length;
  private int _currentIndex;
  private boolean _isMotorolaByteOrder = true;
  
  @SuppressWarnings(value="EI_EXPOSE_REP2", justification="Design intent")
  public RandomAccessFileReader(@NotNull RandomAccessFile paramRandomAccessFile)
    throws IOException
  {
    if (paramRandomAccessFile == null) {
      throw new NullPointerException();
    }
    _file = paramRandomAccessFile;
    _length = _file.length();
  }
  
  public long getLength()
  {
    return _length;
  }
  
  public void setMotorolaByteOrder(boolean paramBoolean)
  {
    _isMotorolaByteOrder = paramBoolean;
  }
  
  public boolean isMotorolaByteOrder()
  {
    return _isMotorolaByteOrder;
  }
  
  private byte read()
    throws BufferBoundsException
  {
    int i;
    try
    {
      i = _file.read();
    }
    catch (IOException localIOException)
    {
      throw new BufferBoundsException("IOException reading from file.", localIOException);
    }
    if (i < 0) {
      throw new BufferBoundsException("Unexpected end of file encountered.");
    }
    assert (i <= 255);
    _currentIndex += 1;
    return (byte)i;
  }
  
  private void seek(int paramInt)
    throws BufferBoundsException
  {
    if (paramInt == _currentIndex) {
      return;
    }
    try
    {
      _file.seek(paramInt);
      _currentIndex = paramInt;
    }
    catch (IOException localIOException)
    {
      throw new BufferBoundsException("IOException seeking in file.", localIOException);
    }
  }
  
  public short getUInt8(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 1);
    seek(paramInt);
    return (short)(read() & 0xFF);
  }
  
  public byte getInt8(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 1);
    seek(paramInt);
    return read();
  }
  
  public int getUInt16(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 2);
    seek(paramInt);
    if (_isMotorolaByteOrder) {
      return read() << 8 & 0xFF00 | read() & 0xFF;
    }
    return read() & 0xFF | read() << 8 & 0xFF00;
  }
  
  public short getInt16(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 2);
    seek(paramInt);
    if (_isMotorolaByteOrder) {
      return (short)((short)read() << 8 & 0xFF00 | (short)read() & 0xFF);
    }
    return (short)((short)read() & 0xFF | (short)read() << 8 & 0xFF00);
  }
  
  public long getUInt32(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 4);
    seek(paramInt);
    if (_isMotorolaByteOrder) {
      return read() << 24 & 0xFF000000 | read() << 16 & 0xFF0000 | read() << 8 & 0xFF00 | read() & 0xFF;
    }
    return read() & 0xFF | read() << 8 & 0xFF00 | read() << 16 & 0xFF0000 | read() << 24 & 0xFF000000;
  }
  
  public int getInt32(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 4);
    seek(paramInt);
    if (_isMotorolaByteOrder) {
      return read() << 24 & 0xFF000000 | read() << 16 & 0xFF0000 | read() << 8 & 0xFF00 | read() & 0xFF;
    }
    return read() & 0xFF | read() << 8 & 0xFF00 | read() << 16 & 0xFF0000 | read() << 24 & 0xFF000000;
  }
  
  public long getInt64(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 8);
    seek(paramInt);
    if (_isMotorolaByteOrder) {
      return read() << 56 & 0xFF00000000000000 | read() << 48 & 0xFF000000000000 | read() << 40 & 0xFF0000000000 | read() << 32 & 0xFF00000000 | read() << 24 & 0xFF000000 | read() << 16 & 0xFF0000 | read() << 8 & 0xFF00 | read() & 0xFF;
    }
    return read() & 0xFF | read() << 8 & 0xFF00 | read() << 16 & 0xFF0000 | read() << 24 & 0xFF000000 | read() << 32 & 0xFF00000000 | read() << 40 & 0xFF0000000000 | read() << 48 & 0xFF000000000000 | read() << 56 & 0xFF00000000000000;
  }
  
  public float getS15Fixed16(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 4);
    seek(paramInt);
    if (_isMotorolaByteOrder)
    {
      float f1 = (read() & 0xFF) << 8 | read() & 0xFF;
      int j = (read() & 0xFF) << 8 | read() & 0xFF;
      return (float)(f1 + j / 65536.0D);
    }
    int i = read() & 0xFF | (read() & 0xFF) << 8;
    float f2 = read() & 0xFF | (read() & 0xFF) << 8;
    return (float)(f2 + i / 65536.0D);
  }
  
  public float getFloat32(int paramInt)
    throws BufferBoundsException
  {
    return Float.intBitsToFloat(getInt32(paramInt));
  }
  
  public double getDouble64(int paramInt)
    throws BufferBoundsException
  {
    return Double.longBitsToDouble(getInt64(paramInt));
  }
  
  @NotNull
  public byte[] getBytes(int paramInt1, int paramInt2)
    throws BufferBoundsException
  {
    checkBounds(paramInt1, paramInt2);
    seek(paramInt1);
    byte[] arrayOfByte = new byte[paramInt2];
    int i;
    try
    {
      i = _file.read(arrayOfByte);
      _currentIndex += i;
    }
    catch (IOException localIOException)
    {
      throw new BufferBoundsException("Unexpected end of file encountered.", localIOException);
    }
    if (i != paramInt2) {
      throw new BufferBoundsException("Unexpected end of file encountered.");
    }
    return arrayOfByte;
  }
  
  @NotNull
  public String getString(int paramInt1, int paramInt2)
    throws BufferBoundsException
  {
    return new String(getBytes(paramInt1, paramInt2));
  }
  
  @NotNull
  public String getString(int paramInt1, int paramInt2, String paramString)
    throws BufferBoundsException
  {
    checkBounds(paramInt1, paramInt2);
    byte[] arrayOfByte = getBytes(paramInt1, paramInt2);
    try
    {
      return new String(arrayOfByte, paramString);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return new String(arrayOfByte);
  }
  
  @NotNull
  public String getNullTerminatedString(int paramInt1, int paramInt2)
    throws BufferBoundsException
  {
    checkBounds(paramInt1, paramInt2);
    seek(paramInt1);
    for (int i = 0; (paramInt1 + i < _length) && (read() != 0) && (i < paramInt2); i++) {}
    byte[] arrayOfByte = getBytes(paramInt1, i);
    return new String(arrayOfByte);
  }
  
  private void checkBounds(int paramInt1, int paramInt2)
    throws BufferBoundsException
  {
    if (paramInt2 < 0) {
      throw new BufferBoundsException("Requested negative number of bytes.");
    }
    if (paramInt1 < 0) {
      throw new BufferBoundsException("Requested data from a negative index within the file.");
    }
    if (paramInt1 + paramInt2 - 1L >= _length) {
      throw new BufferBoundsException("Requested data from beyond the end of the file.");
    }
  }
}
