package com.sun.media.jai.util;

import javax.media.jai.PlanarImage;

abstract interface Job
{
  public abstract void compute();
  
  public abstract boolean notDone();
  
  public abstract PlanarImage getOwner();
  
  public abstract boolean isBlocking();
  
  public abstract Exception getException();
}
