package javax.media.jai;

import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;


































public final class BorderExtenderReflect
  extends BorderExtender
{
  BorderExtenderReflect() {}
  
  private void flipX(WritableRaster raster)
  {
    int minX = raster.getMinX();
    int minY = raster.getMinY();
    int height = raster.getHeight();
    int width = raster.getWidth();
    int maxX = minX + width - 1;
    int numBands = raster.getNumBands();
    
    switch (raster.getSampleModel().getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      int[] iData0 = new int[height * numBands];
      int[] iData1 = new int[height * numBands];
      
      for (int i = 0; i < width / 2; i++) {
        raster.getPixels(minX + i, minY, 1, height, iData0);
        raster.getPixels(maxX - i, minY, 1, height, iData1);
        
        raster.setPixels(minX + i, minY, 1, height, iData1);
        raster.setPixels(maxX - i, minY, 1, height, iData0);
      }
      break;
    
    case 4: 
      float[] fData0 = new float[height * numBands];
      float[] fData1 = new float[height * numBands];
      
      for (int i = 0; i < width / 2; i++) {
        raster.getPixels(minX + i, minY, 1, height, fData0);
        raster.getPixels(maxX - i, minY, 1, height, fData1);
        
        raster.setPixels(minX + i, minY, 1, height, fData1);
        raster.setPixels(maxX - i, minY, 1, height, fData0);
      }
      break;
    
    case 5: 
      double[] dData0 = new double[height * numBands];
      double[] dData1 = new double[height * numBands];
      
      for (int i = 0; i < width / 2; i++) {
        raster.getPixels(minX + i, minY, 1, height, dData0);
        raster.getPixels(maxX - i, minY, 1, height, dData1);
        
        raster.setPixels(minX + i, minY, 1, height, dData1);
        raster.setPixels(maxX - i, minY, 1, height, dData0);
      }
    }
  }
  
  private void flipY(WritableRaster raster)
  {
    int minX = raster.getMinX();
    int minY = raster.getMinY();
    int height = raster.getHeight();
    int width = raster.getWidth();
    int maxY = minY + height - 1;
    int numBands = raster.getNumBands();
    
    switch (raster.getSampleModel().getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      int[] iData0 = new int[width * numBands];
      int[] iData1 = new int[width * numBands];
      
      for (int i = 0; i < height / 2; i++) {
        raster.getPixels(minX, minY + i, width, 1, iData0);
        raster.getPixels(minX, maxY - i, width, 1, iData1);
        
        raster.setPixels(minX, minY + i, width, 1, iData1);
        raster.setPixels(minX, maxY - i, width, 1, iData0);
      }
      break;
    
    case 4: 
      float[] fData0 = new float[width * numBands];
      float[] fData1 = new float[width * numBands];
      
      for (int i = 0; i < height / 2; i++) {
        raster.getPixels(minX, minY + i, width, 1, fData0);
        raster.getPixels(minX, maxY - i, width, 1, fData1);
        
        raster.setPixels(minX, minY + i, width, 1, fData1);
        raster.setPixels(minX, maxY - i, width, 1, fData0);
      }
      break;
    
    case 5: 
      double[] dData0 = new double[width * numBands];
      double[] dData1 = new double[width * numBands];
      
      for (int i = 0; i < height / 2; i++) {
        raster.getPixels(minX, minY + i, width, 1, dData0);
        raster.getPixels(minX, maxY - i, width, 1, dData1);
        
        raster.setPixels(minX, minY + i, width, 1, dData1);
        raster.setPixels(minX, maxY - i, width, 1, dData0);
      }
    }
    
  }
  



















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
    
    int validMinX = Math.max(imMinX, minX);
    int validMaxX = Math.min(imMinX + imWidth, maxX);
    int validMinY = Math.max(imMinY, minY);
    int validMaxY = Math.min(imMinY + imHeight, maxY);
    
    if ((validMinX > validMaxX) || (validMinY > validMaxY))
    {


      if (validMinX > validMaxX) {
        if (minX == validMinX) {
          minX = im.getMaxX() - 1;
        } else {
          maxX = im.getMinX();
        }
      }
      if (validMinY > validMaxY) {
        if (minY == validMinY) {
          minY = im.getMaxY() - 1;
        } else {
          maxY = im.getMinY();
        }
      }
      

      WritableRaster wr = raster.createCompatibleWritableRaster(minX, minY, maxX - minX, maxY - minY);
      




      extend(wr, im);
      

      Raster child = wr.createChild(raster.getMinX(), raster.getMinY(), raster.getWidth(), raster.getHeight(), raster.getMinX(), raster.getMinY(), null);
      




      JDKWorkarounds.setRect(raster, child, 0, 0);
      
      return;
    }
    
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


          boolean flipX = Math.abs(tileX) % 2 == 1;
          boolean flipY = Math.abs(tileY) % 2 == 1;
          


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
          int imX;
          int imX;
          if (flipX) { int imX;
            if (xOffset == 0) {
              imX = imMinX + imWidth - width;
            } else {
              imX = imMinX;
            }
          } else {
            imX = imMinX + xOffset;
          }
          int imY;
          int imY;
          if (flipY) { int imY;
            if (yOffset == 0) {
              imY = imMinY + imHeight - height;
            } else {
              imY = imMinY;
            }
          } else {
            imY = imMinY + yOffset;
          }
          


          WritableRaster child = RasterFactory.createWritableChild(raster, x, y, width, height, imX, imY, null);
          







          im.copyData(child);
          
          if (flipX) {
            flipX(child);
          }
          
          if (flipY) {
            flipY(child);
          }
        }
      }
    }
  }
}
