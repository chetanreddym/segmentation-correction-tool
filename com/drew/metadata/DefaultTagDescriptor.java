package com.drew.metadata;

import com.drew.lang.annotations.NotNull;

public class DefaultTagDescriptor
  extends TagDescriptor<Directory>
{
  public DefaultTagDescriptor(@NotNull Directory paramDirectory)
  {
    super(paramDirectory);
  }
  
  @NotNull
  public String getTagName(int paramInt)
  {
    for (String str = Integer.toHexString(paramInt).toUpperCase(); str.length() < 4; str = "0" + str) {}
    return "Unknown tag 0x" + str;
  }
}
