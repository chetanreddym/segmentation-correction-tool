package javax.media.jai;

import java.awt.Rectangle;
import java.awt.image.WritableRaster;






























































public class BorderExtenderWrap
  extends BorderExtender
{
  BorderExtenderWrap() {}
  
  public final void extend(WritableRaster raster, PlanarImage im)
  {
    if ((raster == null) || (im == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    int width = raster.getWidth();
    int height = raster.getHeight();
    
    int minX = raster.getMinX();
    int maxX = minX + width;
    int minY = raster.getMinY();
    int maxY = minY + height;
    
    int imMinX = im.getMinX();
    int imMinY = im.getMinY();
    int imWidth = im.getWidth();
    int imHeight = im.getHeight();
    
    Rectangle rect = new Rectangle();
    





    int minTileX = PlanarImage.XToTileX(minX, imMinX, imWidth);
    int maxTileX = PlanarImage.XToTileX(maxX - 1, imMinX, imWidth);
    int minTileY = PlanarImage.YToTileY(minY, imMinY, imHeight);
    int maxTileY = PlanarImage.YToTileY(maxY - 1, imMinY, imHeight);
    

    for (int tileY = minTileY; tileY <= maxTileY; tileY++) {
      int ty = tileY * imHeight + imMinY;
      for (int tileX = minTileX; tileX <= maxTileX; tileX++) {
        int tx = tileX * imWidth + imMinX;
        

        if ((tileX != 0) || (tileY != 0))
        {




          x = tx;
          y = ty;
          width = imWidth;
          height = imHeight;
          
          int xOffset = 0;
          if (x < minX) {
            xOffset = minX - x;
            x = minX;
            width -= xOffset;
          }
          int yOffset = 0;
          if (y < minY) {
            yOffset = minY - y;
            y = minY;
            height -= yOffset;
          }
          if (x + width > maxX) {
            width = (maxX - x);
          }
          if (y + height > maxY) {
            height = (maxY - y);
          }
          


          WritableRaster child = RasterFactory.createWritableChild(raster, x, y, width, height, imMinX + xOffset, imMinY + yOffset, null);
          







          im.copyData(child);
        }
      }
    }
  }
}
