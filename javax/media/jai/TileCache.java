package javax.media.jai;

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Comparator;

public abstract interface TileCache
{
  public abstract void add(RenderedImage paramRenderedImage, int paramInt1, int paramInt2, Raster paramRaster);
  
  public abstract void add(RenderedImage paramRenderedImage, int paramInt1, int paramInt2, Raster paramRaster, Object paramObject);
  
  public abstract void remove(RenderedImage paramRenderedImage, int paramInt1, int paramInt2);
  
  public abstract Raster getTile(RenderedImage paramRenderedImage, int paramInt1, int paramInt2);
  
  public abstract Raster[] getTiles(RenderedImage paramRenderedImage);
  
  public abstract void removeTiles(RenderedImage paramRenderedImage);
  
  public abstract void addTiles(RenderedImage paramRenderedImage, Point[] paramArrayOfPoint, Raster[] paramArrayOfRaster, Object paramObject);
  
  public abstract Raster[] getTiles(RenderedImage paramRenderedImage, Point[] paramArrayOfPoint);
  
  public abstract void flush();
  
  public abstract void memoryControl();
  
  /**
   * @deprecated
   */
  public abstract void setTileCapacity(int paramInt);
  
  /**
   * @deprecated
   */
  public abstract int getTileCapacity();
  
  public abstract void setMemoryCapacity(long paramLong);
  
  public abstract long getMemoryCapacity();
  
  public abstract void setMemoryThreshold(float paramFloat);
  
  public abstract float getMemoryThreshold();
  
  public abstract void setTileComparator(Comparator paramComparator);
  
  public abstract Comparator getTileComparator();
}
