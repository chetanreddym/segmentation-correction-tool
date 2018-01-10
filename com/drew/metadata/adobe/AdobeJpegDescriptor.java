package com.drew.metadata.adobe;

import com.drew.metadata.TagDescriptor;

public class AdobeJpegDescriptor
  extends TagDescriptor<AdobeJpegDirectory>
{
  public AdobeJpegDescriptor(AdobeJpegDirectory paramAdobeJpegDirectory)
  {
    super(paramAdobeJpegDirectory);
  }
  
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 3: 
      return getColorTransformDescription();
    case 0: 
      return getDctEncodeVersionDescription();
    }
    return super.getDescription(paramInt);
  }
  
  private String getDctEncodeVersionDescription()
  {
    Integer localInteger = ((AdobeJpegDirectory)_directory).getInteger(3);
    return localInteger.intValue() == 100 ? "100" : localInteger == null ? null : Integer.toString(localInteger.intValue());
  }
  
  private String getColorTransformDescription()
  {
    Integer localInteger = ((AdobeJpegDirectory)_directory).getInteger(3);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Unknown (RGB or CMYK)";
    case 1: 
      return "YCbCr";
    case 2: 
      return "YCCK";
    }
    return String.format("Unknown transform (%d)", new Object[] { localInteger });
  }
}
