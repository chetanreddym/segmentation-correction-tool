package javax.media.jai;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;

public abstract interface CachedTile
{
  public abstract RenderedImage getOwner();
  
  public abstract Raster getTile();
  
  public abstract Object getTileCacheMetric();
  
  public abstract long getTileTimeStamp();
  
  public abstract long getTileSize();
  
  public abstract int getAction();
}
