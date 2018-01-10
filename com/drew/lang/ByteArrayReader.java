package com.drew.lang;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.SuppressWarnings;
import java.io.UnsupportedEncodingException;

public class ByteArrayReader
  implements BufferReader
{
  @NotNull
  private final byte[] _buffer;
  private boolean _isMotorolaByteOrder = true;
  
  @SuppressWarnings(value="EI_EXPOSE_REP2", justification="Design intent")
  public ByteArrayReader(@NotNull byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    _buffer = paramArrayOfByte;
  }
  
  public long getLength()
  {
    return _buffer.length;
  }
  
  public void setMotorolaByteOrder(boolean paramBoolean)
  {
    _isMotorolaByteOrder = paramBoolean;
  }
  
  public boolean isMotorolaByteOrder()
  {
    return _isMotorolaByteOrder;
  }
  
  public short getUInt8(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 1);
    return (short)(_buffer[paramInt] & 0xFF);
  }
  
  public byte getInt8(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 1);
    return _buffer[paramInt];
  }
  
  public int getUInt16(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 2);
    if (_isMotorolaByteOrder) {
      return _buffer[paramInt] << 8 & 0xFF00 | _buffer[(paramInt + 1)] & 0xFF;
    }
    return _buffer[(paramInt + 1)] << 8 & 0xFF00 | _buffer[paramInt] & 0xFF;
  }
  
  public short getInt16(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 2);
    if (_isMotorolaByteOrder) {
      return (short)((short)_buffer[paramInt] << 8 & 0xFF00 | (short)_buffer[(paramInt + 1)] & 0xFF);
    }
    return (short)((short)_buffer[(paramInt + 1)] << 8 & 0xFF00 | (short)_buffer[paramInt] & 0xFF);
  }
  
  public long getUInt32(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 4);
    if (_isMotorolaByteOrder) {
      return _buffer[paramInt] << 24 & 0xFF000000 | _buffer[(paramInt + 1)] << 16 & 0xFF0000 | _buffer[(paramInt + 2)] << 8 & 0xFF00 | _buffer[(paramInt + 3)] & 0xFF;
    }
    return _buffer[(paramInt + 3)] << 24 & 0xFF000000 | _buffer[(paramInt + 2)] << 16 & 0xFF0000 | _buffer[(paramInt + 1)] << 8 & 0xFF00 | _buffer[paramInt] & 0xFF;
  }
  
  public int getInt32(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 4);
    if (_isMotorolaByteOrder) {
      return _buffer[paramInt] << 24 & 0xFF000000 | _buffer[(paramInt + 1)] << 16 & 0xFF0000 | _buffer[(paramInt + 2)] << 8 & 0xFF00 | _buffer[(paramInt + 3)] & 0xFF;
    }
    return _buffer[(paramInt + 3)] << 24 & 0xFF000000 | _buffer[(paramInt + 2)] << 16 & 0xFF0000 | _buffer[(paramInt + 1)] << 8 & 0xFF00 | _buffer[paramInt] & 0xFF;
  }
  
  public long getInt64(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 8);
    if (_isMotorolaByteOrder) {
      return _buffer[paramInt] << 56 & 0xFF00000000000000 | _buffer[(paramInt + 1)] << 48 & 0xFF000000000000 | _buffer[(paramInt + 2)] << 40 & 0xFF0000000000 | _buffer[(paramInt + 3)] << 32 & 0xFF00000000 | _buffer[(paramInt + 4)] << 24 & 0xFF000000 | _buffer[(paramInt + 5)] << 16 & 0xFF0000 | _buffer[(paramInt + 6)] << 8 & 0xFF00 | _buffer[(paramInt + 7)] & 0xFF;
    }
    return _buffer[(paramInt + 7)] << 56 & 0xFF00000000000000 | _buffer[(paramInt + 6)] << 48 & 0xFF000000000000 | _buffer[(paramInt + 5)] << 40 & 0xFF0000000000 | _buffer[(paramInt + 4)] << 32 & 0xFF00000000 | _buffer[(paramInt + 3)] << 24 & 0xFF000000 | _buffer[(paramInt + 2)] << 16 & 0xFF0000 | _buffer[(paramInt + 1)] << 8 & 0xFF00 | _buffer[paramInt] & 0xFF;
  }
  
  public float getS15Fixed16(int paramInt)
    throws BufferBoundsException
  {
    checkBounds(paramInt, 4);
    if (_isMotorolaByteOrder)
    {
      f = (_buffer[paramInt] & 0xFF) << 8 | _buffer[(paramInt + 1)] & 0xFF;
      i = (_buffer[(paramInt + 2)] & 0xFF) << 8 | _buffer[(paramInt + 3)] & 0xFF;
      return (float)(f + i / 65536.0D);
    }
    float f = (_buffer[(paramInt + 3)] & 0xFF) << 8 | _buffer[(paramInt + 2)] & 0xFF;
    int i = (_buffer[(paramInt + 1)] & 0xFF) << 8 | _buffer[paramInt] & 0xFF;
    return (float)(f + i / 65536.0D);
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
    byte[] arrayOfByte = new byte[paramInt2];
    System.arraycopy(_buffer, paramInt1, arrayOfByte, 0, paramInt2);
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
    for (int i = 0; (paramInt1 + i < _buffer.length) && (_buffer[(paramInt1 + i)] != 0) && (i < paramInt2); i++) {}
    byte[] arrayOfByte = getBytes(paramInt1, i);
    return new String(arrayOfByte);
  }
  
  private void checkBounds(int paramInt1, int paramInt2)
    throws BufferBoundsException
  {
    if ((paramInt2 < 0) || (paramInt1 < 0) || (paramInt1 + paramInt2 - 1L >= _buffer.length)) {
      throw new BufferBoundsException(_buffer, paramInt1, paramInt2);
    }
  }
}
