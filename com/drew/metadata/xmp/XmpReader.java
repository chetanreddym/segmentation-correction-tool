package com.drew.metadata.xmp;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;
import java.util.Calendar;

public class XmpReader
  implements MetadataReader
{
  private static final int FMT_STRING = 1;
  private static final int FMT_RATIONAL = 2;
  private static final int FMT_INT = 3;
  private static final int FMT_DOUBLE = 4;
  @NotNull
  private static final String SCHEMA_XMP_PROPERTIES = "http://ns.adobe.com/xap/1.0/";
  @NotNull
  private static final String SCHEMA_EXIF_SPECIFIC_PROPERTIES = "http://ns.adobe.com/exif/1.0/";
  @NotNull
  private static final String SCHEMA_EXIF_ADDITIONAL_PROPERTIES = "http://ns.adobe.com/exif/1.0/aux/";
  @NotNull
  private static final String SCHEMA_EXIF_TIFF_PROPERTIES = "http://ns.adobe.com/tiff/1.0/";
  @NotNull
  private static final String SCHEMA_DUBLIN_CORE_SPECIFIC_PROPERTIES = "http://purl.org/dc/elements/1.1/";
  
  public XmpReader() {}
  
  public void extract(@NotNull BufferReader paramBufferReader, @NotNull Metadata paramMetadata)
  {
    if (paramBufferReader == null) {
      throw new NullPointerException("reader");
    }
    if (paramMetadata == null) {
      throw new NullPointerException("metadata");
    }
    XmpDirectory localXmpDirectory = (XmpDirectory)paramMetadata.getOrCreateDirectory(XmpDirectory.class);
    if (paramBufferReader.getLength() <= 30L)
    {
      localXmpDirectory.addError("Xmp data segment must contain at least 30 bytes");
      return;
    }
    String str1;
    try
    {
      str1 = paramBufferReader.getString(0, 29);
    }
    catch (BufferBoundsException localBufferBoundsException1)
    {
      localXmpDirectory.addError("Unable to read XMP preamble");
      return;
    }
    if (!"http://ns.adobe.com/xap/1.0/\000".equals(str1))
    {
      localXmpDirectory.addError("Xmp data segment doesn't begin with 'http://ns.adobe.com/xap/1.0/'");
      return;
    }
    try
    {
      byte[] arrayOfByte;
      try
      {
        arrayOfByte = paramBufferReader.getBytes(29, (int)(paramBufferReader.getLength() - 29L));
      }
      catch (BufferBoundsException localBufferBoundsException2)
      {
        localXmpDirectory.addError("Unable to read XMP data");
        return;
      }
      XMPMeta localXMPMeta = XMPMetaFactory.parseFromBuffer(arrayOfByte);
      localXmpDirectory.setXMPMeta(localXMPMeta);
      processXmpTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/exif/1.0/aux/", "aux:LensInfo", 6, 1);
      processXmpTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/exif/1.0/aux/", "aux:Lens", 7, 1);
      processXmpTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/exif/1.0/aux/", "aux:SerialNumber", 8, 1);
      processXmpTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/exif/1.0/aux/", "aux:Firmware", 9, 1);
      processXmpTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/tiff/1.0/", "tiff:Make", 1, 1);
      processXmpTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/tiff/1.0/", "tiff:Model", 2, 1);
      processXmpTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/exif/1.0/", "exif:ExposureTime", 3, 1);
      processXmpTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/exif/1.0/", "exif:ExposureProgram", 12, 3);
      processXmpTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/exif/1.0/", "exif:ApertureValue", 11, 2);
      processXmpTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/exif/1.0/", "exif:FNumber", 5, 2);
      processXmpTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/exif/1.0/", "exif:FocalLength", 10, 2);
      processXmpTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/exif/1.0/", "exif:ShutterSpeedValue", 4, 2);
      processXmpDateTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/exif/1.0/", "exif:DateTimeOriginal", 13);
      processXmpDateTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/exif/1.0/", "exif:DateTimeDigitized", 14);
      processXmpTag(localXMPMeta, localXmpDirectory, "http://ns.adobe.com/xap/1.0/", "xmp:Rating", 4097, 4);
      XMPIterator localXMPIterator = localXMPMeta.iterator();
      while (localXMPIterator.hasNext())
      {
        XMPPropertyInfo localXMPPropertyInfo = (XMPPropertyInfo)localXMPIterator.next();
        String str2 = localXMPPropertyInfo.getPath();
        Object localObject = localXMPPropertyInfo.getValue();
        if ((str2 != null) && (localObject != null)) {
          localXmpDirectory.addProperty(str2, localObject.toString());
        }
      }
    }
    catch (XMPException localXMPException)
    {
      localXmpDirectory.addError("Error parsing XMP segment: " + localXMPException.getMessage());
    }
  }
  
  private void processXmpTag(@NotNull XMPMeta paramXMPMeta, @NotNull XmpDirectory paramXmpDirectory, @NotNull String paramString1, @NotNull String paramString2, int paramInt1, int paramInt2)
    throws XMPException
  {
    String str = paramXMPMeta.getPropertyString(paramString1, paramString2);
    if (str == null) {
      return;
    }
    switch (paramInt2)
    {
    case 2: 
      String[] arrayOfString = str.split("/", 2);
      if (arrayOfString.length == 2) {
        try
        {
          Rational localRational = new Rational(Float.parseFloat(arrayOfString[0]), Float.parseFloat(arrayOfString[1]));
          paramXmpDirectory.setRational(paramInt1, localRational);
        }
        catch (NumberFormatException localNumberFormatException1)
        {
          paramXmpDirectory.addError(String.format("Unable to parse XMP property %s as a Rational.", new Object[] { paramString2 }));
        }
      } else {
        paramXmpDirectory.addError("Error in rational format for tag " + paramInt1);
      }
      break;
    case 3: 
      try
      {
        paramXmpDirectory.setInt(paramInt1, Integer.valueOf(str).intValue());
      }
      catch (NumberFormatException localNumberFormatException2)
      {
        paramXmpDirectory.addError(String.format("Unable to parse XMP property %s as an int.", new Object[] { paramString2 }));
      }
    case 4: 
      try
      {
        paramXmpDirectory.setDouble(paramInt1, Double.valueOf(str).doubleValue());
      }
      catch (NumberFormatException localNumberFormatException3)
      {
        paramXmpDirectory.addError(String.format("Unable to parse XMP property %s as an double.", new Object[] { paramString2 }));
      }
    case 1: 
      paramXmpDirectory.setString(paramInt1, str);
      break;
    default: 
      paramXmpDirectory.addError(String.format("Unknown format code %d for tag %d", new Object[] { Integer.valueOf(paramInt2), Integer.valueOf(paramInt1) }));
    }
  }
  
  void processXmpDateTag(@NotNull XMPMeta paramXMPMeta, @NotNull XmpDirectory paramXmpDirectory, @NotNull String paramString1, @NotNull String paramString2, int paramInt)
    throws XMPException
  {
    Calendar localCalendar = paramXMPMeta.getPropertyCalendar(paramString1, paramString2);
    if (localCalendar == null) {
      return;
    }
    paramXmpDirectory.setDate(paramInt, localCalendar.getTime());
  }
}
