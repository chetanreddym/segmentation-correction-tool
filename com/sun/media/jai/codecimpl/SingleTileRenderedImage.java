package com.sun.media.jai.codecimpl;

import java.awt.image.ColorModel;
import java.awt.image.Raster;






















public class SingleTileRenderedImage
  extends SimpleRenderedImage
{
  Raster ras;
  
  public SingleTileRenderedImage(Raster ras, ColorModel colorModel)
  {
    this.ras = ras;
    
    tileGridXOffset = (this.minX = ras.getMinX());
    tileGridYOffset = (this.minY = ras.getMinY());
    tileWidth = (this.width = ras.getWidth());
    tileHeight = (this.height = ras.getHeight());
    sampleModel = ras.getSampleModel();
    this.colorModel = colorModel;
  }
  


  public Raster getTile(int tileX, int tileY)
  {
    if ((tileX != 0) || (tileY != 0)) {
      throw new IllegalArgumentException(JaiI18N.getString("SingleTileRenderedImage0"));
    }
    return ras;
  }
}
