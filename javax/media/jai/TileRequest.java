package javax.media.jai;

import java.awt.Point;

public abstract interface TileRequest
{
  public static final int TILE_STATUS_PENDING = 0;
  public static final int TILE_STATUS_PROCESSING = 1;
  public static final int TILE_STATUS_COMPUTED = 2;
  public static final int TILE_STATUS_CANCELLED = 3;
  public static final int TILE_STATUS_FAILED = 4;
  
  public abstract PlanarImage getImage();
  
  public abstract Point[] getTileIndices();
  
  public abstract TileComputationListener[] getTileListeners();
  
  public abstract boolean isStatusAvailable();
  
  public abstract int getTileStatus(int paramInt1, int paramInt2);
  
  public abstract void cancelTiles(Point[] paramArrayOfPoint);
}
