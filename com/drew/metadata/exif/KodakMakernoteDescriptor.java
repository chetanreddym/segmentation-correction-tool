package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

public class KodakMakernoteDescriptor
  extends TagDescriptor<KodakMakernoteDirectory>
{
  public KodakMakernoteDescriptor(@NotNull KodakMakernoteDirectory paramKodakMakernoteDirectory)
  {
    super(paramKodakMakernoteDirectory);
  }
}
