package com.drew.metadata;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Metadata
{
  @NotNull
  private final Map<Class<? extends Directory>, Directory> _directoryByClass = new HashMap();
  @NotNull
  private final Collection<Directory> _directoryList = new ArrayList();
  
  public Metadata() {}
  
  @NotNull
  public Iterable<Directory> getDirectories()
  {
    return _directoryList;
  }
  
  public int getDirectoryCount()
  {
    return _directoryList.size();
  }
  
  @NotNull
  public <T extends Directory> T getOrCreateDirectory(@NotNull Class<T> paramClass)
  {
    if (_directoryByClass.containsKey(paramClass)) {
      return (Directory)_directoryByClass.get(paramClass);
    }
    Directory localDirectory;
    try
    {
      localDirectory = (Directory)paramClass.newInstance();
    }
    catch (Exception localException)
    {
      throw new RuntimeException("Cannot instantiate provided Directory type: " + paramClass.toString());
    }
    _directoryByClass.put(paramClass, localDirectory);
    _directoryList.add(localDirectory);
    return localDirectory;
  }
  
  @Nullable
  public <T extends Directory> T getDirectory(@NotNull Class<T> paramClass)
  {
    return (Directory)_directoryByClass.get(paramClass);
  }
  
  public boolean containsDirectory(Class<? extends Directory> paramClass)
  {
    return _directoryByClass.containsKey(paramClass);
  }
  
  public boolean hasErrors()
  {
    Iterator localIterator = _directoryList.iterator();
    while (localIterator.hasNext())
    {
      Directory localDirectory = (Directory)localIterator.next();
      if (localDirectory.hasErrors()) {
        return true;
      }
    }
    return false;
  }
}
