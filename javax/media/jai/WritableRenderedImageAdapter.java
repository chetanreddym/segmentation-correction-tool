package javax.media.jai;

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.TileObserver;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;









































public final class WritableRenderedImageAdapter
  extends RenderedImageAdapter
  implements WritableRenderedImage
{
  private WritableRenderedImage theWritableImage;
  
  public WritableRenderedImageAdapter(WritableRenderedImage im)
  {
    super(im);
    theWritableImage = im;
  }
  







  public final void addTileObserver(TileObserver tileObserver)
  {
    if (tileObserver == null) {
      throw new IllegalArgumentException(JaiI18N.getString("WritableRenderedImageAdapter0"));
    }
    theWritableImage.addTileObserver(tileObserver);
  }
  








  public final void removeTileObserver(TileObserver tileObserver)
  {
    if (tileObserver == null) {
      throw new IllegalArgumentException(JaiI18N.getString("WritableRenderedImageAdapter0"));
    }
    theWritableImage.removeTileObserver(tileObserver);
  }
  










  public final WritableRaster getWritableTile(int tileX, int tileY)
  {
    return theWritableImage.getWritableTile(tileX, tileY);
  }
  













  public final void releaseWritableTile(int tileX, int tileY)
  {
    theWritableImage.releaseWritableTile(tileX, tileY);
  }
  







  public final boolean isTileWritable(int tileX, int tileY)
  {
    return theWritableImage.isTileWritable(tileX, tileY);
  }
  






  public final Point[] getWritableTileIndices()
  {
    return theWritableImage.getWritableTileIndices();
  }
  





  public final boolean hasTileWriters()
  {
    return theWritableImage.hasTileWriters();
  }
  







  public final void setData(Raster raster)
  {
    if (raster == null) {
      throw new IllegalArgumentException(JaiI18N.getString("WritableRenderedImageAdapter1"));
    }
    theWritableImage.setData(raster);
  }
}
