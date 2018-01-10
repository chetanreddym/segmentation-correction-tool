package javax.media.jai;

import java.awt.image.Raster;


































































final class SnapshotProxy
  extends PlanarImage
{
  Snapshot parent;
  
  SnapshotProxy(Snapshot parent)
  {
    super(new ImageLayout(parent), null, null);
    this.parent = parent;
  }
  






  public Raster getTile(int tileX, int tileY)
  {
    return parent.getTile(tileX, tileY);
  }
  
  public void dispose()
  {
    parent.dispose();
  }
}
