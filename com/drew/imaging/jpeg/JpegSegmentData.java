package com.drew.imaging.jpeg;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JpegSegmentData
  implements Serializable
{
  private static final long serialVersionUID = 7110175216435025451L;
  @NotNull
  private final HashMap<Byte, List<byte[]>> _segmentDataMap = new HashMap(10);
  
  public JpegSegmentData() {}
  
  public void addSegment(byte paramByte, @NotNull byte[] paramArrayOfByte)
  {
    List localList = getOrCreateSegmentList(paramByte);
    localList.add(paramArrayOfByte);
  }
  
  @Nullable
  public byte[] getSegment(byte paramByte)
  {
    return getSegment(paramByte, 0);
  }
  
  @Nullable
  public byte[] getSegment(byte paramByte, int paramInt)
  {
    List localList = getSegmentList(paramByte);
    if ((localList == null) || (localList.size() <= paramInt)) {
      return null;
    }
    return (byte[])localList.get(paramInt);
  }
  
  @NotNull
  public Iterable<byte[]> getSegments(byte paramByte)
  {
    List localList = getSegmentList(paramByte);
    return localList == null ? new ArrayList() : localList;
  }
  
  @Nullable
  public List<byte[]> getSegmentList(byte paramByte)
  {
    return (List)_segmentDataMap.get(Byte.valueOf(paramByte));
  }
  
  @NotNull
  private List<byte[]> getOrCreateSegmentList(byte paramByte)
  {
    Object localObject;
    if (_segmentDataMap.containsKey(Byte.valueOf(paramByte)))
    {
      localObject = (List)_segmentDataMap.get(Byte.valueOf(paramByte));
    }
    else
    {
      localObject = new ArrayList();
      _segmentDataMap.put(Byte.valueOf(paramByte), localObject);
    }
    return localObject;
  }
  
  public int getSegmentCount(byte paramByte)
  {
    List localList = getSegmentList(paramByte);
    return localList == null ? 0 : localList.size();
  }
  
  public void removeSegmentOccurrence(byte paramByte, int paramInt)
  {
    List localList = (List)_segmentDataMap.get(Byte.valueOf(paramByte));
    localList.remove(paramInt);
  }
  
  public void removeSegment(byte paramByte)
  {
    _segmentDataMap.remove(Byte.valueOf(paramByte));
  }
  
  public boolean containsSegment(byte paramByte)
  {
    return _segmentDataMap.containsKey(Byte.valueOf(paramByte));
  }
  
  public static void toFile(@NotNull File paramFile, @NotNull JpegSegmentData paramJpegSegmentData)
    throws IOException
  {
    FileOutputStream localFileOutputStream = null;
    try
    {
      localFileOutputStream = new FileOutputStream(paramFile);
      new ObjectOutputStream(localFileOutputStream).writeObject(paramJpegSegmentData);
    }
    finally
    {
      if (localFileOutputStream != null) {
        localFileOutputStream.close();
      }
    }
  }
  
  @NotNull
  public static JpegSegmentData fromFile(@NotNull File paramFile)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream localObjectInputStream = null;
    try
    {
      localObjectInputStream = new ObjectInputStream(new FileInputStream(paramFile));
      JpegSegmentData localJpegSegmentData = (JpegSegmentData)localObjectInputStream.readObject();
      return localJpegSegmentData;
    }
    finally
    {
      if (localObjectInputStream != null) {
        localObjectInputStream.close();
      }
    }
  }
}
