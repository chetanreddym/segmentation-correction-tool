package com.drew.metadata.icc;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class IccReader
  implements MetadataReader
{
  public IccReader() {}
  
  public void extract(@NotNull BufferReader paramBufferReader, @NotNull Metadata paramMetadata)
  {
    IccDirectory localIccDirectory = (IccDirectory)paramMetadata.getOrCreateDirectory(IccDirectory.class);
    try
    {
      localIccDirectory.setInt(0, paramBufferReader.getInt32(0));
      set4ByteString(localIccDirectory, 4, paramBufferReader);
      setInt32(localIccDirectory, 8, paramBufferReader);
      set4ByteString(localIccDirectory, 12, paramBufferReader);
      set4ByteString(localIccDirectory, 16, paramBufferReader);
      set4ByteString(localIccDirectory, 20, paramBufferReader);
      setDate(localIccDirectory, 24, paramBufferReader);
      set4ByteString(localIccDirectory, 36, paramBufferReader);
      set4ByteString(localIccDirectory, 40, paramBufferReader);
      setInt32(localIccDirectory, 44, paramBufferReader);
      set4ByteString(localIccDirectory, 48, paramBufferReader);
      int i = paramBufferReader.getInt32(52);
      if (i != 0) {
        if (i <= 538976288) {
          localIccDirectory.setInt(52, i);
        } else {
          localIccDirectory.setString(52, getStringFromInt32(i));
        }
      }
      setInt32(localIccDirectory, 64, paramBufferReader);
      setInt64(localIccDirectory, 56, paramBufferReader);
      float[] arrayOfFloat = { paramBufferReader.getS15Fixed16(68), paramBufferReader.getS15Fixed16(72), paramBufferReader.getS15Fixed16(76) };
      localIccDirectory.setObject(68, arrayOfFloat);
      int j = paramBufferReader.getInt32(128);
      localIccDirectory.setInt(128, j);
      for (int k = 0; k < j; k++)
      {
        int m = 132 + k * 12;
        int n = paramBufferReader.getInt32(m);
        int i1 = paramBufferReader.getInt32(m + 4);
        int i2 = paramBufferReader.getInt32(m + 8);
        byte[] arrayOfByte = paramBufferReader.getBytes(i1, i2);
        localIccDirectory.setByteArray(n, arrayOfByte);
      }
    }
    catch (BufferBoundsException localBufferBoundsException)
    {
      localIccDirectory.addError(String.format("Reading ICC Header %s:%s", new Object[] { localBufferBoundsException.getClass().getSimpleName(), localBufferBoundsException.getMessage() }));
    }
  }
  
  private void set4ByteString(@NotNull Directory paramDirectory, int paramInt, @NotNull BufferReader paramBufferReader)
    throws BufferBoundsException
  {
    int i = paramBufferReader.getInt32(paramInt);
    if (i != 0) {
      paramDirectory.setString(paramInt, getStringFromInt32(i));
    }
  }
  
  private void setInt32(@NotNull Directory paramDirectory, int paramInt, @NotNull BufferReader paramBufferReader)
    throws BufferBoundsException
  {
    int i = paramBufferReader.getInt32(paramInt);
    if (i != 0) {
      paramDirectory.setInt(paramInt, i);
    }
  }
  
  private void setInt64(@NotNull Directory paramDirectory, int paramInt, @NotNull BufferReader paramBufferReader)
    throws BufferBoundsException
  {
    long l = paramBufferReader.getInt64(paramInt);
    if (l != 0L) {
      paramDirectory.setLong(paramInt, l);
    }
  }
  
  private void setDate(@NotNull IccDirectory paramIccDirectory, int paramInt, @NotNull BufferReader paramBufferReader)
    throws BufferBoundsException
  {
    int i = paramBufferReader.getUInt16(paramInt);
    int j = paramBufferReader.getUInt16(paramInt + 2);
    int k = paramBufferReader.getUInt16(paramInt + 4);
    int m = paramBufferReader.getUInt16(paramInt + 6);
    int n = paramBufferReader.getUInt16(paramInt + 8);
    int i1 = paramBufferReader.getUInt16(paramInt + 10);
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    localCalendar.set(i, j, k, m, n, i1);
    Date localDate = localCalendar.getTime();
    paramIccDirectory.setDate(paramInt, localDate);
  }
  
  @NotNull
  public static String getStringFromInt32(int paramInt)
  {
    byte[] arrayOfByte = { (byte)((paramInt & 0xFF000000) >> 24), (byte)((paramInt & 0xFF0000) >> 16), (byte)((paramInt & 0xFF00) >> 8), (byte)(paramInt & 0xFF) };
    return new String(arrayOfByte);
  }
}
