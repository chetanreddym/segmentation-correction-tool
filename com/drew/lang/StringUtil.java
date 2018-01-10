package com.drew.lang;

import com.drew.lang.annotations.NotNull;
import java.util.Iterator;

public class StringUtil
{
  public StringUtil() {}
  
  public static String join(@NotNull Iterable<? extends CharSequence> paramIterable, @NotNull String paramString)
  {
    int i = 0;
    int j = paramString.length();
    Iterator localIterator = paramIterable.iterator();
    if (localIterator.hasNext()) {
      i += ((CharSequence)localIterator.next()).length() + j;
    }
    StringBuilder localStringBuilder = new StringBuilder(i);
    localIterator = paramIterable.iterator();
    if (localIterator.hasNext())
    {
      localStringBuilder.append((CharSequence)localIterator.next());
      while (localIterator.hasNext())
      {
        localStringBuilder.append(paramString);
        localStringBuilder.append((CharSequence)localIterator.next());
      }
    }
    return localStringBuilder.toString();
  }
  
  public static <T extends CharSequence> String join(@NotNull T[] paramArrayOfT, @NotNull String paramString)
  {
    int i = 0;
    int j = paramString.length();
    for (Object localObject2 : paramArrayOfT) {
      i += localObject2.length() + j;
    }
    ??? = new StringBuilder(i);
    ??? = 1;
    for (T ? : paramArrayOfT)
    {
      if (??? == 0) {
        ((StringBuilder)???).append(paramString);
      } else {
        ??? = 0;
      }
      ((StringBuilder)???).append(?);
    }
    return ((StringBuilder)???).toString();
  }
}
