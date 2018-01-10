package com.drew.metadata.jfif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class JfifDescriptor
  extends TagDescriptor<JfifDirectory>
{
  public JfifDescriptor(@NotNull JfifDirectory paramJfifDirectory)
  {
    super(paramJfifDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 8: 
      return getImageResXDescription();
    case 10: 
      return getImageResYDescription();
    case 5: 
      return getImageVersionDescription();
    case 7: 
      return getImageResUnitsDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getImageVersionDescription()
  {
    Integer localInteger = ((JfifDirectory)_directory).getInteger(5);
    if (localInteger == null) {
      return null;
    }
    return String.format("%d.%d", new Object[] { Integer.valueOf((localInteger.intValue() & 0xFF00) >> 8), Integer.valueOf(localInteger.intValue() & 0xFF) });
  }
  
  @Nullable
  public String getImageResYDescription()
  {
    Integer localInteger = ((JfifDirectory)_directory).getInteger(10);
    if (localInteger == null) {
      return null;
    }
    return String.format("%d dot%s", new Object[] { localInteger, localInteger.intValue() == 1 ? "" : "s" });
  }
  
  @Nullable
  public String getImageResXDescription()
  {
    Integer localInteger = ((JfifDirectory)_directory).getInteger(8);
    if (localInteger == null) {
      return null;
    }
    return String.format("%d dot%s", new Object[] { localInteger, localInteger.intValue() == 1 ? "" : "s" });
  }
  
  @Nullable
  public String getImageResUnitsDescription()
  {
    Integer localInteger = ((JfifDirectory)_directory).getInteger(7);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "none";
    case 1: 
      return "inch";
    case 2: 
      return "centimetre";
    }
    return "unit";
  }
}
