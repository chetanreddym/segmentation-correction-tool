package com.drew.metadata;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import java.lang.reflect.Array;

public abstract class TagDescriptor<T extends Directory>
{
  @NotNull
  protected final T _directory;
  
  public TagDescriptor(@NotNull T paramT)
  {
    _directory = paramT;
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    Object localObject = _directory.getObject(paramInt);
    if (localObject == null) {
      return null;
    }
    if (localObject.getClass().isArray())
    {
      int i = Array.getLength(localObject);
      if (i > 16)
      {
        String str = localObject.getClass().getComponentType().getName();
        return String.format("[%d %s%s]", new Object[] { Integer.valueOf(i), str, i == 1 ? "" : "s" });
      }
    }
    return _directory.getString(paramInt);
  }
  
  @Nullable
  public static String convertBytesToVersionString(@Nullable int[] paramArrayOfInt, int paramInt)
  {
    if (paramArrayOfInt == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; (i < 4) && (i < paramArrayOfInt.length); i++)
    {
      if (i == paramInt) {
        localStringBuilder.append('.');
      }
      char c = (char)paramArrayOfInt[i];
      if (c < '0') {
        c = (char)(c + '0');
      }
      if ((i != 0) || (c != '0')) {
        localStringBuilder.append(c);
      }
    }
    return localStringBuilder.toString();
  }
}
