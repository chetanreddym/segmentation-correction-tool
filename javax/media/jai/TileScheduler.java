package javax.media.jai;

import java.awt.Point;
import java.awt.image.Raster;

public abstract interface TileScheduler
{
  public abstract Raster scheduleTile(OpImage paramOpImage, int paramInt1, int paramInt2);
  
  public abstract Raster[] scheduleTiles(OpImage paramOpImage, Point[] paramArrayOfPoint);
  
  public abstract TileRequest scheduleTiles(PlanarImage paramPlanarImage, Point[] paramArrayOfPoint, TileComputationListener[] paramArrayOfTileComputationListener);
  
  public abstract void cancelTiles(TileRequest paramTileRequest, Point[] paramArrayOfPoint);
  
  public abstract void prefetchTiles(PlanarImage paramPlanarImage, Point[] paramArrayOfPoint);
  
  public abstract void setParallelism(int paramInt);
  
  public abstract int getParallelism();
  
  public abstract void setPrefetchParallelism(int paramInt);
  
  public abstract int getPrefetchParallelism();
  
  public abstract void setPriority(int paramInt);
  
  public abstract int getPriority();
  
  public abstract void setPrefetchPriority(int paramInt);
  
  public abstract int getPrefetchPriority();
}
