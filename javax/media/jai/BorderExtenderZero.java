package javax.media.jai;

import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;



























































public final class BorderExtenderZero
  extends BorderExtender
{
  BorderExtenderZero() {}
  
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
    int[] iData;
    int row;
    switch (raster.getSampleModel().getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      iData = new int[width * numBands];
      if ((validMinX > validMaxX) || (validMinY > validMaxY))
      {
        for (row = minY; row < maxY;) {
          raster.setPixels(minX, row, width, 1, iData);row++; continue;
          

          for (int row = minY; row < validMinY; row++) {
            raster.setPixels(minX, row, width, 1, iData);
          }
          for (row = validMinY; row < validMaxY; row++) {
            if (minX < validMinX) {
              raster.setPixels(minX, row, validMinX - minX, 1, iData);
            }
            
            if (validMaxX < maxX) {
              raster.setPixels(validMaxX, row, maxX - validMaxX, 1, iData);
            }
          }
          
          row = validMaxY; for (;;) { if (row < maxY) {
              raster.setPixels(minX, row, width, 1, iData);row++; continue;
              




              fData = new float[width * numBands];
              if ((validMinX > validMaxX) || (validMinY > validMaxY))
              {
                for (row = minY; row < maxY;) {
                  raster.setPixels(minX, row, width, 1, fData);row++; continue;
                  

                  for (int row = minY; row < validMinY; row++) {
                    raster.setPixels(minX, row, width, 1, fData);
                  }
                  for (row = validMinY; row < validMaxY; row++) {
                    if (minX < validMinX) {
                      raster.setPixels(minX, row, validMinX - minX, 1, fData);
                    }
                    
                    if (validMaxX < maxX) {
                      raster.setPixels(validMaxX, row, maxX - validMaxX, 1, fData);
                    }
                  }
                  
                  row = validMaxY; for (;;) { if (row < maxY) {
                      raster.setPixels(minX, row, width, 1, fData);row++; continue;
                      




                      dData = new double[width * numBands];
                      if ((validMinX > validMaxX) || (validMinY > validMaxY))
                      {
                        for (row = minY; row < maxY;) {
                          raster.setPixels(minX, row, width, 1, dData);row++; continue;
                          

                          for (int row = minY; row < validMinY; row++) {
                            raster.setPixels(minX, row, width, 1, dData);
                          }
                          for (row = validMinY; row < validMaxY; row++) {
                            if (minX < validMinX) {
                              raster.setPixels(minX, row, validMinX - minX, 1, dData);
                            }
                            
                            if (validMaxX < maxX) {
                              raster.setPixels(validMaxX, row, maxX - validMaxX, 1, dData);
                            }
                          }
                          
                          for (row = validMaxY; row < maxY; row++) {
                            raster.setPixels(minX, row, width, 1, dData);
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      break;
    }
    float[] fData;
    int row;
    double[] dData;
    int row;
  }
}
