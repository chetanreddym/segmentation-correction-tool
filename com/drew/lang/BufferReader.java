package com.drew.lang;

import com.drew.lang.annotations.NotNull;

public abstract interface BufferReader
{
  public abstract long getLength();
  
  public abstract void setMotorolaByteOrder(boolean paramBoolean);
  
  public abstract boolean isMotorolaByteOrder();
  
  public abstract short getUInt8(int paramInt)
    throws BufferBoundsException;
  
  public abstract byte getInt8(int paramInt)
    throws BufferBoundsException;
  
  public abstract int getUInt16(int paramInt)
    throws BufferBoundsException;
  
  public abstract short getInt16(int paramInt)
    throws BufferBoundsException;
  
  public abstract long getUInt32(int paramInt)
    throws BufferBoundsException;
  
  public abstract int getInt32(int paramInt)
    throws BufferBoundsException;
  
  public abstract long getInt64(int paramInt)
    throws BufferBoundsException;
  
  public abstract float getS15Fixed16(int paramInt)
    throws BufferBoundsException;
  
  public abstract float getFloat32(int paramInt)
    throws BufferBoundsException;
  
  public abstract double getDouble64(int paramInt)
    throws BufferBoundsException;
  
  @NotNull
  public abstract byte[] getBytes(int paramInt1, int paramInt2)
    throws BufferBoundsException;
  
  @NotNull
  public abstract String getString(int paramInt1, int paramInt2)
    throws BufferBoundsException;
  
  @NotNull
  public abstract String getString(int paramInt1, int paramInt2, String paramString)
    throws BufferBoundsException;
  
  @NotNull
  public abstract String getNullTerminatedString(int paramInt1, int paramInt2)
    throws BufferBoundsException;
}
