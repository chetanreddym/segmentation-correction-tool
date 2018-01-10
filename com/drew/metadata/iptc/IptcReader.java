package com.drew.metadata.iptc;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;
import java.util.Date;
import java.util.GregorianCalendar;

public class IptcReader
  implements MetadataReader
{
  public IptcReader() {}
  
  public void extract(@NotNull BufferReader paramBufferReader, @NotNull Metadata paramMetadata)
  {
    IptcDirectory localIptcDirectory = (IptcDirectory)paramMetadata.getOrCreateDirectory(IptcDirectory.class);
    int i = 0;
    while (i < paramBufferReader.getLength())
    {
      int j;
      try
      {
        j = paramBufferReader.getUInt8(i);
      }
      catch (BufferBoundsException localBufferBoundsException1)
      {
        localIptcDirectory.addError("Unable to read starting byte of IPTC tag");
        break;
      }
      if (j != 28)
      {
        localIptcDirectory.addError("Invalid start to IPTC tag");
        break;
      }
      if (i + 5 >= paramBufferReader.getLength())
      {
        localIptcDirectory.addError("Too few bytes remain for a valid IPTC tag");
        break;
      }
      i++;
      int k;
      int m;
      int n;
      try
      {
        k = paramBufferReader.getUInt8(i++);
        m = paramBufferReader.getUInt8(i++);
        n = paramBufferReader.getUInt16(i);
        i += 2;
      }
      catch (BufferBoundsException localBufferBoundsException2)
      {
        localIptcDirectory.addError("IPTC data segment ended mid-way through tag descriptor");
        return;
      }
      if (i + n > paramBufferReader.getLength())
      {
        localIptcDirectory.addError("Data for tag extends beyond end of IPTC segment");
        break;
      }
      try
      {
        processTag(paramBufferReader, localIptcDirectory, k, m, i, n);
      }
      catch (BufferBoundsException localBufferBoundsException3)
      {
        localIptcDirectory.addError("Error processing IPTC tag");
        break;
      }
      i += n;
    }
  }
  
  private void processTag(@NotNull BufferReader paramBufferReader, @NotNull Directory paramDirectory, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws BufferBoundsException
  {
    int i = paramInt2 | paramInt1 << 8;
    Object localObject;
    switch (i)
    {
    case 512: 
      int j = paramBufferReader.getUInt16(paramInt3);
      paramDirectory.setInt(i, j);
      return;
    case 522: 
      paramDirectory.setInt(i, paramBufferReader.getUInt8(paramInt3));
      return;
    case 542: 
    case 567: 
      if (paramInt4 >= 8)
      {
        localObject = paramBufferReader.getString(paramInt3, paramInt4);
        try
        {
          int k = Integer.parseInt(((String)localObject).substring(0, 4));
          int m = Integer.parseInt(((String)localObject).substring(4, 6)) - 1;
          int n = Integer.parseInt(((String)localObject).substring(6, 8));
          Date localDate = new GregorianCalendar(k, m, n).getTime();
          paramDirectory.setDate(i, localDate);
          return;
        }
        catch (NumberFormatException localNumberFormatException) {}
      }
      break;
    }
    String str;
    if (paramInt4 < 1) {
      str = "";
    } else {
      str = paramBufferReader.getString(paramInt3, paramInt4, System.getProperty("file.encoding"));
    }
    if (paramDirectory.containsTag(i))
    {
      localObject = paramDirectory.getStringArray(i);
      String[] arrayOfString;
      if (localObject == null)
      {
        arrayOfString = new String[1];
      }
      else
      {
        arrayOfString = new String[localObject.length + 1];
        System.arraycopy(localObject, 0, arrayOfString, 0, localObject.length);
      }
      arrayOfString[(arrayOfString.length - 1)] = str;
      paramDirectory.setStringArray(i, arrayOfString);
    }
    else
    {
      paramDirectory.setString(i, str);
    }
  }
}
