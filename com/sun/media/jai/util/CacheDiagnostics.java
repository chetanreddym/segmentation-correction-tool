package com.sun.media.jai.util;

public abstract interface CacheDiagnostics
{
  public abstract void enableDiagnostics();
  
  public abstract void disableDiagnostics();
  
  public abstract long getCacheTileCount();
  
  public abstract long getCacheMemoryUsed();
  
  public abstract long getCacheHitCount();
  
  public abstract long getCacheMissCount();
  
  public abstract void resetCounts();
}
