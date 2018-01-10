package com.drew.metadata;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

public class Tag
{
  private final int _tagType;
  @NotNull
  private final Directory _directory;
  
  public Tag(int paramInt, @NotNull Directory paramDirectory)
  {
    _tagType = paramInt;
    _directory = paramDirectory;
  }
  
  public int getTagType()
  {
    return _tagType;
  }
  
  @NotNull
  public String getTagTypeHex()
  {
    for (String str = Integer.toHexString(_tagType); str.length() < 4; str = "0" + str) {}
    return "0x" + str;
  }
  
  @Nullable
  public String getDescription()
  {
    return _directory.getDescription(_tagType);
  }
  
  @NotNull
  public String getTagName()
  {
    return _directory.getTagName(_tagType);
  }
  
  @NotNull
  public String getDirectoryName()
  {
    return _directory.getName();
  }
  
  @NotNull
  public String toString()
  {
    String str = getDescription();
    if (str == null) {
      str = _directory.getString(getTagType()) + " (unable to formulate description)";
    }
    return "[" + _directory.getName() + "] " + getTagName() + " - " + str;
  }
}
