package com.sun.media.jai.codec;

public abstract interface StreamSegmentMapper
{
  public abstract StreamSegment getStreamSegment(long paramLong, int paramInt);
  
  public abstract void getStreamSegment(long paramLong, int paramInt, StreamSegment paramStreamSegment);
}
