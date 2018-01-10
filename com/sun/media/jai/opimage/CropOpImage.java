package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.PointOpImage;
































final class CropOpImage
  extends PointOpImage
{
  private static ImageLayout layoutHelper(RenderedImage source, float originX, float originY, float width, float height)
  {
    Rectangle bounds = new Rectangle2D.Float(originX, originY, width, height).getBounds();
    

    return new ImageLayout(x, y, width, height, source.getTileGridXOffset(), source.getTileGridYOffset(), source.getTileWidth(), source.getTileHeight(), source.getSampleModel(), source.getColorModel());
  }
  






















  public CropOpImage(RenderedImage source, float originX, float originY, float width, float height)
  {
    super(source, layoutHelper(source, originX, originY, width, height), null, false);
  }
  







  public boolean computesUniqueTiles()
  {
    return false;
  }
  



  public Raster computeTile(int tileX, int tileY)
  {
    return getTile(tileX, tileY);
  }
  










  public Raster getTile(int tileX, int tileY)
  {
    Raster tile = null;
    

    if ((tileX >= getMinTileX()) && (tileX <= getMaxTileX()) && (tileY >= getMinTileY()) && (tileY <= getMaxTileY()))
    {





      tile = getSourceImage(0).getTile(tileX, tileY);
    }
    
    return tile;
  }
}
