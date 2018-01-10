package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class SonyType6MakernoteDescriptor
  extends TagDescriptor<SonyType6MakernoteDirectory>
{
  public SonyType6MakernoteDescriptor(@NotNull SonyType6MakernoteDirectory paramSonyType6MakernoteDirectory)
  {
    super(paramSonyType6MakernoteDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 8192: 
      return getMakerNoteThumbVersionDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getMakerNoteThumbVersionDescription()
  {
    int[] arrayOfInt = ((SonyType6MakernoteDirectory)_directory).getIntArray(8192);
    return convertBytesToVersionString(arrayOfInt, 2);
  }
}
