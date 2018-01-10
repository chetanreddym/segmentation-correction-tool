package javax.media.jai;

import java.awt.Point;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public abstract interface TileFactory
{
  public abstract boolean canReclaimMemory();
  
  public abstract boolean isMemoryCache();
  
  public abstract long getMemoryUsed();
  
  public abstract void flush();
  
  public abstract WritableRaster createTile(SampleModel paramSampleModel, Point paramPoint);
}
