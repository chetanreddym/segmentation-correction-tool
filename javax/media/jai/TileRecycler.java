package javax.media.jai;

import java.awt.image.Raster;

public abstract interface TileRecycler
{
  public abstract void recycleTile(Raster paramRaster);
}
