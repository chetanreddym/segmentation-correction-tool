package com.drew.metadata.exif;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;
import java.util.HashSet;
import java.util.Set;

public class ExifReader
  implements MetadataReader
{
  @NotNull
  private static final int[] BYTES_PER_FORMAT = { 0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8 };
  private static final int MAX_FORMAT_CODE = 12;
  private static final int FMT_BYTE = 1;
  private static final int FMT_STRING = 2;
  private static final int FMT_USHORT = 3;
  private static final int FMT_ULONG = 4;
  private static final int FMT_URATIONAL = 5;
  private static final int FMT_SBYTE = 6;
  private static final int FMT_UNDEFINED = 7;
  private static final int FMT_SSHORT = 8;
  private static final int FMT_SLONG = 9;
  private static final int FMT_SRATIONAL = 10;
  private static final int FMT_SINGLE = 11;
  private static final int FMT_DOUBLE = 12;
  public static final int TAG_EXIF_SUB_IFD_OFFSET = 34665;
  public static final int TAG_INTEROP_OFFSET = 40965;
  public static final int TAG_GPS_INFO_OFFSET = 34853;
  public static final int TAG_MAKER_NOTE_OFFSET = 37500;
  public static final int TIFF_HEADER_START_OFFSET = 6;
  
  public ExifReader() {}
  
  public void extract(@NotNull BufferReader paramBufferReader, @NotNull Metadata paramMetadata)
  {
    ExifSubIFDDirectory localExifSubIFDDirectory = (ExifSubIFDDirectory)paramMetadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
    if (paramBufferReader.getLength() <= 14L)
    {
      localExifSubIFDDirectory.addError("Exif data segment must contain at least 14 bytes");
      return;
    }
    try
    {
      if (!paramBufferReader.getString(0, 6).equals("Exif\000\000"))
      {
        localExifSubIFDDirectory.addError("Exif data segment doesn't begin with 'Exif'");
        return;
      }
      extractIFD(paramMetadata, (ExifIFD0Directory)paramMetadata.getOrCreateDirectory(ExifIFD0Directory.class), 6, paramBufferReader);
    }
    catch (BufferBoundsException localBufferBoundsException)
    {
      localExifSubIFDDirectory.addError("Exif data segment ended prematurely");
    }
  }
  
  public void extractTiff(@NotNull BufferReader paramBufferReader, @NotNull Metadata paramMetadata)
  {
    ExifIFD0Directory localExifIFD0Directory = (ExifIFD0Directory)paramMetadata.getOrCreateDirectory(ExifIFD0Directory.class);
    try
    {
      extractIFD(paramMetadata, localExifIFD0Directory, 0, paramBufferReader);
    }
    catch (BufferBoundsException localBufferBoundsException)
    {
      localExifIFD0Directory.addError("Exif data segment ended prematurely");
    }
  }
  
  private void extractIFD(@NotNull Metadata paramMetadata, @NotNull ExifIFD0Directory paramExifIFD0Directory, int paramInt, @NotNull BufferReader paramBufferReader)
    throws BufferBoundsException
  {
    String str = paramBufferReader.getString(paramInt, 2);
    if ("MM".equals(str))
    {
      paramBufferReader.setMotorolaByteOrder(true);
    }
    else if ("II".equals(str))
    {
      paramBufferReader.setMotorolaByteOrder(false);
    }
    else
    {
      paramExifIFD0Directory.addError("Unclear distinction between Motorola/Intel byte ordering: " + str);
      return;
    }
    int i = paramBufferReader.getUInt16(2 + paramInt);
    if ((i != 42) && (i != 20306) && (i != 85))
    {
      paramExifIFD0Directory.addError("Unexpected TIFF marker after byte order identifier: 0x" + Integer.toHexString(i));
      return;
    }
    int j = paramBufferReader.getInt32(4 + paramInt) + paramInt;
    if (j >= paramBufferReader.getLength() - 1L)
    {
      paramExifIFD0Directory.addError("First exif directory offset is beyond end of Exif data segment");
      j = 14;
    }
    HashSet localHashSet = new HashSet();
    processDirectory(paramExifIFD0Directory, localHashSet, j, paramInt, paramMetadata, paramBufferReader);
    ExifThumbnailDirectory localExifThumbnailDirectory = (ExifThumbnailDirectory)paramMetadata.getDirectory(ExifThumbnailDirectory.class);
    if ((localExifThumbnailDirectory != null) && (localExifThumbnailDirectory.containsTag(259)))
    {
      Integer localInteger1 = localExifThumbnailDirectory.getInteger(513);
      Integer localInteger2 = localExifThumbnailDirectory.getInteger(514);
      if ((localInteger1 != null) && (localInteger2 != null)) {
        try
        {
          byte[] arrayOfByte = paramBufferReader.getBytes(paramInt + localInteger1.intValue(), localInteger2.intValue());
          localExifThumbnailDirectory.setThumbnailData(arrayOfByte);
        }
        catch (BufferBoundsException localBufferBoundsException)
        {
          paramExifIFD0Directory.addError("Invalid thumbnail data specification: " + localBufferBoundsException.getMessage());
        }
      }
    }
  }
  
  private void processDirectory(@NotNull Directory paramDirectory, @NotNull Set<Integer> paramSet, int paramInt1, int paramInt2, @NotNull Metadata paramMetadata, @NotNull BufferReader paramBufferReader)
    throws BufferBoundsException
  {
    if (paramSet.contains(Integer.valueOf(paramInt1))) {
      return;
    }
    paramSet.add(Integer.valueOf(paramInt1));
    if ((paramInt1 >= paramBufferReader.getLength()) || (paramInt1 < 0))
    {
      paramDirectory.addError("Ignored directory marked to start outside data segment");
      return;
    }
    int i = paramBufferReader.getUInt16(paramInt1);
    int j = 2 + 12 * i + 4;
    if (j + paramInt1 > paramBufferReader.getLength())
    {
      paramDirectory.addError("Illegally sized directory");
      return;
    }
    for (int k = 0; k < i; k++)
    {
      m = calculateTagOffset(paramInt1, k);
      int n = paramBufferReader.getUInt16(m);
      int i1 = paramBufferReader.getUInt16(m + 2);
      if ((i1 < 1) || (i1 > 12))
      {
        paramDirectory.addError("Invalid TIFF tag format code: " + i1);
        return;
      }
      int i2 = paramBufferReader.getInt32(m + 4);
      if (i2 < 0)
      {
        paramDirectory.addError("Negative TIFF tag component count");
      }
      else
      {
        int i3 = i2 * BYTES_PER_FORMAT[i1];
        int i5;
        int i4;
        if (i3 > 4)
        {
          i5 = paramBufferReader.getInt32(m + 8);
          if (i5 + i3 > paramBufferReader.getLength())
          {
            paramDirectory.addError("Illegal TIFF tag pointer offset");
            continue;
          }
          i4 = paramInt2 + i5;
        }
        else
        {
          i4 = m + 8;
        }
        if ((i4 < 0) || (i4 > paramBufferReader.getLength())) {
          paramDirectory.addError("Illegal TIFF tag pointer offset");
        } else if ((i3 < 0) || (i4 + i3 > paramBufferReader.getLength())) {
          paramDirectory.addError("Illegal number of bytes: " + i3);
        } else {
          switch (n)
          {
          case 34665: 
            i5 = paramInt2 + paramBufferReader.getInt32(i4);
            processDirectory(paramMetadata.getOrCreateDirectory(ExifSubIFDDirectory.class), paramSet, i5, paramInt2, paramMetadata, paramBufferReader);
            break;
          case 40965: 
            i5 = paramInt2 + paramBufferReader.getInt32(i4);
            processDirectory(paramMetadata.getOrCreateDirectory(ExifInteropDirectory.class), paramSet, i5, paramInt2, paramMetadata, paramBufferReader);
            break;
          case 34853: 
            i5 = paramInt2 + paramBufferReader.getInt32(i4);
            processDirectory(paramMetadata.getOrCreateDirectory(GpsDirectory.class), paramSet, i5, paramInt2, paramMetadata, paramBufferReader);
            break;
          case 37500: 
            processMakerNote(i4, paramSet, paramInt2, paramMetadata, paramBufferReader);
            break;
          default: 
            processTag(paramDirectory, n, i4, i2, i1, paramBufferReader);
          }
        }
      }
    }
    k = calculateTagOffset(paramInt1, i);
    int m = paramBufferReader.getInt32(k);
    if (m != 0)
    {
      m += paramInt2;
      if (m >= paramBufferReader.getLength()) {
        return;
      }
      if (m < paramInt1) {
        return;
      }
      ExifThumbnailDirectory localExifThumbnailDirectory = (ExifThumbnailDirectory)paramMetadata.getOrCreateDirectory(ExifThumbnailDirectory.class);
      processDirectory(localExifThumbnailDirectory, paramSet, m, paramInt2, paramMetadata, paramBufferReader);
    }
  }
  
  private void processMakerNote(int paramInt1, @NotNull Set<Integer> paramSet, int paramInt2, @NotNull Metadata paramMetadata, @NotNull BufferReader paramBufferReader)
    throws BufferBoundsException
  {
    Directory localDirectory = paramMetadata.getDirectory(ExifIFD0Directory.class);
    if (localDirectory == null) {
      return;
    }
    String str1 = localDirectory.getString(271);
    String str2 = paramBufferReader.getString(paramInt1, 3);
    String str3 = paramBufferReader.getString(paramInt1, 4);
    String str4 = paramBufferReader.getString(paramInt1, 5);
    String str5 = paramBufferReader.getString(paramInt1, 6);
    String str6 = paramBufferReader.getString(paramInt1, 7);
    String str7 = paramBufferReader.getString(paramInt1, 8);
    String str8 = paramBufferReader.getString(paramInt1, 12);
    if (("OLYMP".equals(str4)) || ("EPSON".equals(str4)) || ("AGFA".equals(str3)))
    {
      processDirectory(paramMetadata.getOrCreateDirectory(OlympusMakernoteDirectory.class), paramSet, paramInt1 + 8, paramInt2, paramMetadata, paramBufferReader);
    }
    else if ((str1 != null) && (str1.trim().toUpperCase().startsWith("NIKON")))
    {
      if ("Nikon".equals(str4)) {
        switch (paramBufferReader.getUInt8(paramInt1 + 6))
        {
        case 1: 
          processDirectory(paramMetadata.getOrCreateDirectory(NikonType1MakernoteDirectory.class), paramSet, paramInt1 + 8, paramInt2, paramMetadata, paramBufferReader);
          break;
        case 2: 
          processDirectory(paramMetadata.getOrCreateDirectory(NikonType2MakernoteDirectory.class), paramSet, paramInt1 + 18, paramInt1 + 10, paramMetadata, paramBufferReader);
          break;
        default: 
          localDirectory.addError("Unsupported Nikon makernote data ignored.");
          break;
        }
      } else {
        processDirectory(paramMetadata.getOrCreateDirectory(NikonType2MakernoteDirectory.class), paramSet, paramInt1, paramInt2, paramMetadata, paramBufferReader);
      }
    }
    else if (("SONY CAM".equals(str7)) || ("SONY DSC".equals(str7)))
    {
      processDirectory(paramMetadata.getOrCreateDirectory(SonyType1MakernoteDirectory.class), paramSet, paramInt1 + 12, paramInt2, paramMetadata, paramBufferReader);
    }
    else if (("SIGMA\000\000\000".equals(str7)) || ("FOVEON\000\000".equals(str7)))
    {
      processDirectory(paramMetadata.getOrCreateDirectory(SigmaMakernoteDirectory.class), paramSet, paramInt1 + 10, paramInt2, paramMetadata, paramBufferReader);
    }
    else
    {
      boolean bool;
      if ("SEMC MS\000\000\000\000\000".equals(str8))
      {
        bool = paramBufferReader.isMotorolaByteOrder();
        paramBufferReader.setMotorolaByteOrder(true);
        processDirectory(paramMetadata.getOrCreateDirectory(SonyType6MakernoteDirectory.class), paramSet, paramInt1 + 20, paramInt2, paramMetadata, paramBufferReader);
        paramBufferReader.setMotorolaByteOrder(bool);
      }
      else if ("KDK".equals(str2))
      {
        processDirectory(paramMetadata.getOrCreateDirectory(KodakMakernoteDirectory.class), paramSet, paramInt1 + 20, paramInt2, paramMetadata, paramBufferReader);
      }
      else if ("Canon".equalsIgnoreCase(str1))
      {
        processDirectory(paramMetadata.getOrCreateDirectory(CanonMakernoteDirectory.class), paramSet, paramInt1, paramInt2, paramMetadata, paramBufferReader);
      }
      else if ((str1 != null) && (str1.toUpperCase().startsWith("CASIO")))
      {
        if ("QVC\000\000\000".equals(str5)) {
          processDirectory(paramMetadata.getOrCreateDirectory(CasioType2MakernoteDirectory.class), paramSet, paramInt1 + 6, paramInt2, paramMetadata, paramBufferReader);
        } else {
          processDirectory(paramMetadata.getOrCreateDirectory(CasioType1MakernoteDirectory.class), paramSet, paramInt1, paramInt2, paramMetadata, paramBufferReader);
        }
      }
      else if (("FUJIFILM".equals(str7)) || ("Fujifilm".equalsIgnoreCase(str1)))
      {
        bool = paramBufferReader.isMotorolaByteOrder();
        paramBufferReader.setMotorolaByteOrder(false);
        int i = paramInt1 + paramBufferReader.getInt32(paramInt1 + 8);
        processDirectory(paramMetadata.getOrCreateDirectory(FujifilmMakernoteDirectory.class), paramSet, i, paramInt2, paramMetadata, paramBufferReader);
        paramBufferReader.setMotorolaByteOrder(bool);
      }
      else if ((str1 != null) && (str1.toUpperCase().startsWith("MINOLTA")))
      {
        processDirectory(paramMetadata.getOrCreateDirectory(OlympusMakernoteDirectory.class), paramSet, paramInt1, paramInt2, paramMetadata, paramBufferReader);
      }
      else if ("KYOCERA".equals(str6))
      {
        processDirectory(paramMetadata.getOrCreateDirectory(KyoceraMakernoteDirectory.class), paramSet, paramInt1 + 22, paramInt2, paramMetadata, paramBufferReader);
      }
      else if ("Panasonic\000\000\000".equals(paramBufferReader.getString(paramInt1, 12)))
      {
        processDirectory(paramMetadata.getOrCreateDirectory(PanasonicMakernoteDirectory.class), paramSet, paramInt1 + 12, paramInt2, paramMetadata, paramBufferReader);
      }
      else if ("AOC\000".equals(str3))
      {
        processDirectory(paramMetadata.getOrCreateDirectory(CasioType2MakernoteDirectory.class), paramSet, paramInt1 + 6, paramInt1, paramMetadata, paramBufferReader);
      }
      else if ((str1 != null) && ((str1.toUpperCase().startsWith("PENTAX")) || (str1.toUpperCase().startsWith("ASAHI"))))
      {
        processDirectory(paramMetadata.getOrCreateDirectory(PentaxMakernoteDirectory.class), paramSet, paramInt1, paramInt1, paramMetadata, paramBufferReader);
      }
    }
  }
  
  private void processTag(@NotNull Directory paramDirectory, int paramInt1, int paramInt2, int paramInt3, int paramInt4, @NotNull BufferReader paramBufferReader)
    throws BufferBoundsException
  {
    Object localObject;
    int m;
    switch (paramInt4)
    {
    case 7: 
      paramDirectory.setByteArray(paramInt1, paramBufferReader.getBytes(paramInt2, paramInt3));
      break;
    case 2: 
      String str = paramBufferReader.getNullTerminatedString(paramInt2, paramInt3);
      paramDirectory.setString(paramInt1, str);
      break;
    case 10: 
      if (paramInt3 == 1)
      {
        paramDirectory.setRational(paramInt1, new Rational(paramBufferReader.getInt32(paramInt2), paramBufferReader.getInt32(paramInt2 + 4)));
      }
      else if (paramInt3 > 1)
      {
        localObject = new Rational[paramInt3];
        for (m = 0; m < paramInt3; m++) {
          localObject[m] = new Rational(paramBufferReader.getInt32(paramInt2 + 8 * m), paramBufferReader.getInt32(paramInt2 + 4 + 8 * m));
        }
        paramDirectory.setRationalArray(paramInt1, (Rational[])localObject);
      }
      break;
    case 5: 
      if (paramInt3 == 1)
      {
        paramDirectory.setRational(paramInt1, new Rational(paramBufferReader.getUInt32(paramInt2), paramBufferReader.getUInt32(paramInt2 + 4)));
      }
      else if (paramInt3 > 1)
      {
        localObject = new Rational[paramInt3];
        for (m = 0; m < paramInt3; m++) {
          localObject[m] = new Rational(paramBufferReader.getUInt32(paramInt2 + 8 * m), paramBufferReader.getUInt32(paramInt2 + 4 + 8 * m));
        }
        paramDirectory.setRationalArray(paramInt1, (Rational[])localObject);
      }
      break;
    case 11: 
      if (paramInt3 == 1)
      {
        paramDirectory.setFloat(paramInt1, paramBufferReader.getFloat32(paramInt2));
      }
      else
      {
        localObject = new float[paramInt3];
        for (m = 0; m < paramInt3; m++) {
          localObject[m] = paramBufferReader.getFloat32(paramInt2 + m * 4);
        }
        paramDirectory.setFloatArray(paramInt1, (float[])localObject);
      }
      break;
    case 12: 
      if (paramInt3 == 1)
      {
        paramDirectory.setDouble(paramInt1, paramBufferReader.getDouble64(paramInt2));
      }
      else
      {
        localObject = new double[paramInt3];
        for (m = 0; m < paramInt3; m++) {
          localObject[m] = paramBufferReader.getDouble64(paramInt2 + m * 4);
        }
        paramDirectory.setDoubleArray(paramInt1, (double[])localObject);
      }
      break;
    case 6: 
      if (paramInt3 == 1)
      {
        paramDirectory.setInt(paramInt1, paramBufferReader.getInt8(paramInt2));
      }
      else
      {
        localObject = new int[paramInt3];
        for (m = 0; m < paramInt3; m++) {
          localObject[m] = paramBufferReader.getInt8(paramInt2 + m);
        }
        paramDirectory.setIntArray(paramInt1, (int[])localObject);
      }
      break;
    case 1: 
      if (paramInt3 == 1)
      {
        paramDirectory.setInt(paramInt1, paramBufferReader.getUInt8(paramInt2));
      }
      else
      {
        localObject = new int[paramInt3];
        for (m = 0; m < paramInt3; m++) {
          localObject[m] = paramBufferReader.getUInt8(paramInt2 + m);
        }
        paramDirectory.setIntArray(paramInt1, (int[])localObject);
      }
      break;
    case 3: 
      if (paramInt3 == 1)
      {
        int i = paramBufferReader.getUInt16(paramInt2);
        paramDirectory.setInt(paramInt1, i);
      }
      else
      {
        int[] arrayOfInt1 = new int[paramInt3];
        for (m = 0; m < paramInt3; m++) {
          arrayOfInt1[m] = paramBufferReader.getUInt16(paramInt2 + m * 2);
        }
        paramDirectory.setIntArray(paramInt1, arrayOfInt1);
      }
      break;
    case 8: 
      if (paramInt3 == 1)
      {
        int j = paramBufferReader.getInt16(paramInt2);
        paramDirectory.setInt(paramInt1, j);
      }
      else
      {
        int[] arrayOfInt2 = new int[paramInt3];
        for (m = 0; m < paramInt3; m++) {
          arrayOfInt2[m] = paramBufferReader.getInt16(paramInt2 + m * 2);
        }
        paramDirectory.setIntArray(paramInt1, arrayOfInt2);
      }
      break;
    case 4: 
    case 9: 
      if (paramInt3 == 1)
      {
        int k = paramBufferReader.getInt32(paramInt2);
        paramDirectory.setInt(paramInt1, k);
      }
      else
      {
        int[] arrayOfInt3 = new int[paramInt3];
        for (m = 0; m < paramInt3; m++) {
          arrayOfInt3[m] = paramBufferReader.getInt32(paramInt2 + m * 4);
        }
        paramDirectory.setIntArray(paramInt1, arrayOfInt3);
      }
      break;
    default: 
      paramDirectory.addError("Unknown format code " + paramInt4 + " for tag " + paramInt1);
    }
  }
  
  private int calculateTagOffset(int paramInt1, int paramInt2)
  {
    return paramInt1 + 2 + 12 * paramInt2;
  }
}
