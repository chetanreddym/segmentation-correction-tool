package javax.media.jai;

import java.awt.image.Raster;
import java.util.EventListener;

public abstract interface TileComputationListener
  extends EventListener
{
  public abstract void tileComputed(Object paramObject, TileRequest[] paramArrayOfTileRequest, PlanarImage paramPlanarImage, int paramInt1, int paramInt2, Raster paramRaster);
  
  public abstract void tileCancelled(Object paramObject, TileRequest[] paramArrayOfTileRequest, PlanarImage paramPlanarImage, int paramInt1, int paramInt2);
  
  public abstract void tileComputationFailure(Object paramObject, TileRequest[] paramArrayOfTileRequest, PlanarImage paramPlanarImage, int paramInt1, int paramInt2, Throwable paramThrowable);
}
