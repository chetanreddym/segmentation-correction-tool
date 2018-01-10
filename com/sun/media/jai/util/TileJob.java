package com.sun.media.jai.util;

import java.awt.Point;
import java.awt.image.Raster;
import javax.media.jai.PlanarImage;
















































































































































































































































































































































final class TileJob
  implements Job
{
  final SunTileScheduler scheduler;
  final boolean isBlocking;
  final PlanarImage owner;
  final Point[] tileIndices;
  final Raster[] tiles;
  final int offset;
  final int numTiles;
  boolean done = false;
  Exception exception = null;
  



  TileJob(SunTileScheduler scheduler, boolean isBlocking, PlanarImage owner, Point[] tileIndices, Raster[] tiles, int offset, int numTiles)
  {
    this.scheduler = scheduler;
    this.isBlocking = isBlocking;
    this.owner = owner;
    this.tileIndices = tileIndices;
    this.tiles = tiles;
    this.offset = offset;
    this.numTiles = numTiles;
  }
  


  public void compute()
  {
    exception = scheduler.compute(owner, tileIndices, tiles, offset, numTiles, null);
    
    done = true;
  }
  



  public boolean notDone()
  {
    return !done;
  }
  
  public PlanarImage getOwner()
  {
    return owner;
  }
  
  public boolean isBlocking()
  {
    return isBlocking;
  }
  
  public Exception getException()
  {
    return exception;
  }
}
