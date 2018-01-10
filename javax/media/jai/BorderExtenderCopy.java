package javax.media.jai;

import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;































































public final class BorderExtenderCopy
  extends BorderExtender
{
  BorderExtenderCopy() {}
  
  public final void extend(WritableRaster raster, PlanarImage im)
  {
    if ((raster == null) || (im == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    int width = raster.getWidth();
    int height = raster.getHeight();
    int numBands = raster.getNumBands();
    
    int minX = raster.getMinX();
    int maxX = minX + width;
    int minY = raster.getMinY();
    int maxY = minY + height;
    
    int validMinX = Math.max(im.getMinX(), minX);
    int validMaxX = Math.min(im.getMaxX(), maxX);
    int validMinY = Math.max(im.getMinY(), minY);
    int validMaxY = Math.min(im.getMaxY(), maxY);
    
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
    int size = Math.max(width, height);
    

    switch (raster.getSampleModel().getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      int[] iData = new int[size * numBands];
      
      if (minX < validMinX) {
        x = validMinX;
        y = validMinY;
        width = 1;
        height = (validMaxY - validMinY);
        
        if (height > 0) {
          Raster leftEdge = im.getData(rect);
          leftEdge.getPixels(validMinX, validMinY, 1, height, iData);
          

          for (int col = minX; col < validMinX; col++) {
            raster.setPixels(col, validMinY, 1, height, iData);
          }
        }
      }
      

      if (validMaxX < maxX) {
        x = (validMaxX - 1);
        y = validMinY;
        width = 1;
        height = (validMaxY - validMinY);
        
        if (height > 0) {
          Raster rightEdge = im.getData(rect);
          rightEdge.getPixels(validMaxX - 1, validMinY, 1, height, iData);
          

          for (int col = validMaxX; col < maxX; col++) {
            raster.setPixels(col, validMinY, 1, height, iData);
          }
        }
      }
      

      if (minY < validMinY) {
        x = minX;
        y = validMinY;
        width = width;
        height = 1;
        
        Raster topRow = im.getExtendedData(rect, this);
        topRow.getPixels(minX, validMinY, width, 1, iData);
        for (int row = minY; row < validMinY; row++) {
          raster.setPixels(minX, row, width, 1, iData);
        }
      }
      
      if (validMaxY < maxY) {
        x = minX;
        y = (validMaxY - 1);
        width = width;
        height = 1;
        
        Raster bottomRow = im.getExtendedData(rect, this);
        bottomRow.getPixels(minX, validMaxY - 1, width, 1, iData);
        
        for (int row = validMaxY; row < maxY; row++) {
          raster.setPixels(minX, row, width, 1, iData);
        }
      }
      
      break;
    case 4: 
      float[] fData = new float[size * numBands];
      
      if (minX < validMinX) {
        x = validMinX;
        y = validMinY;
        width = 1;
        height = (validMaxY - validMinY);
        
        if (height > 0) {
          Raster leftEdge = im.getData(rect);
          leftEdge.getPixels(validMinX, validMinY, 1, height, fData);
          

          for (int col = minX; col < validMinX; col++) {
            raster.setPixels(col, validMinY, 1, height, fData);
          }
        }
      }
      

      if (validMaxX < maxX) {
        x = (validMaxX - 1);
        y = validMinY;
        width = 1;
        height = (validMaxY - validMinY);
        
        if (height > 0) {
          Raster rightEdge = im.getData(rect);
          rightEdge.getPixels(validMaxX - 1, validMinY, 1, height, fData);
          

          for (int col = validMaxX; col < maxX; col++) {
            raster.setPixels(col, validMinY, 1, height, fData);
          }
        }
      }
      

      if (minY < validMinY) {
        x = minX;
        y = validMinY;
        width = width;
        height = 1;
        
        Raster topRow = im.getExtendedData(rect, this);
        topRow.getPixels(minX, validMinY, width, 1, fData);
        for (int row = minY; row < validMinY; row++) {
          raster.setPixels(minX, row, width, 1, fData);
        }
      }
      
      if (validMaxY < maxY) {
        x = minX;
        y = (validMaxY - 1);
        width = width;
        height = 1;
        
        Raster bottomRow = im.getExtendedData(rect, this);
        bottomRow.getPixels(minX, validMaxY - 1, width, 1, fData);
        
        for (int row = validMaxY; row < maxY; row++) {
          raster.setPixels(minX, row, width, 1, fData);
        }
      }
      
      break;
    case 5: 
      double[] dData = new double[size * numBands];
      
      if (minX < validMinX) {
        x = validMinX;
        y = validMinY;
        width = 1;
        height = (validMaxY - validMinY);
        
        if (height > 0) {
          Raster leftEdge = im.getData(rect);
          leftEdge.getPixels(validMinX, validMinY, 1, height, dData);
          

          for (int col = minX; col < validMinX; col++) {
            raster.setPixels(col, validMinY, 1, height, dData);
          }
        }
      }
      

      if (validMaxX < maxX) {
        x = (validMaxX - 1);
        y = validMinY;
        width = 1;
        height = (validMaxY - validMinY);
        
        if (height > 0) {
          Raster rightEdge = im.getData(rect);
          rightEdge.getPixels(validMaxX - 1, validMinY, 1, height, dData);
          

          for (int col = validMaxX; col < maxX; col++) {
            raster.setPixels(col, validMinY, 1, height, dData);
          }
        }
      }
      

      if (minY < validMinY) {
        x = minX;
        y = validMinY;
        width = width;
        height = 1;
        
        Raster topRow = im.getExtendedData(rect, this);
        topRow.getPixels(minX, validMinY, width, 1, dData);
        for (int row = minY; row < validMinY; row++) {
          raster.setPixels(minX, row, width, 1, dData);
        }
      }
      
      if (validMaxY < maxY) {
        x = minX;
        y = (validMaxY - 1);
        width = width;
        height = 1;
        
        Raster bottomRow = im.getExtendedData(rect, this);
        bottomRow.getPixels(minX, validMaxY - 1, width, 1, dData);
        
        for (int row = validMaxY; row < maxY; row++) {
          raster.setPixels(minX, row, width, 1, dData);
        }
      }
      break;
    }
  }
}
