package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class KyoceraMakernoteDescriptor
  extends TagDescriptor<KyoceraMakernoteDirectory>
{
  public KyoceraMakernoteDescriptor(@NotNull KyoceraMakernoteDirectory paramKyoceraMakernoteDirectory)
  {
    super(paramKyoceraMakernoteDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 3584: 
      return getPrintImageMatchingInfoDescription();
    case 1: 
      return getProprietaryThumbnailDataDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getPrintImageMatchingInfoDescription()
  {
    byte[] arrayOfByte = ((KyoceraMakernoteDirectory)_directory).getByteArray(3584);
    if (arrayOfByte == null) {
      return null;
    }
    return "(" + arrayOfByte.length + " bytes)";
  }
  
  @Nullable
  public String getProprietaryThumbnailDataDescription()
  {
    byte[] arrayOfByte = ((KyoceraMakernoteDirectory)_directory).getByteArray(1);
    if (arrayOfByte == null) {
      return null;
    }
    return "(" + arrayOfByte.length + " bytes)";
  }
}
