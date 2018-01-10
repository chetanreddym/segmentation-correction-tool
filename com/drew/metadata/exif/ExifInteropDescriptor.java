package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class ExifInteropDescriptor
  extends TagDescriptor<ExifInteropDirectory>
{
  public ExifInteropDescriptor(@NotNull ExifInteropDirectory paramExifInteropDirectory)
  {
    super(paramExifInteropDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return getInteropIndexDescription();
    case 2: 
      return getInteropVersionDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getInteropVersionDescription()
  {
    int[] arrayOfInt = ((ExifInteropDirectory)_directory).getIntArray(2);
    return convertBytesToVersionString(arrayOfInt, 2);
  }
  
  @Nullable
  public String getInteropIndexDescription()
  {
    String str = ((ExifInteropDirectory)_directory).getString(1);
    if (str == null) {
      return null;
    }
    return "Unknown (" + str + ")";
  }
}
