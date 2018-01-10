package javax.media.jai;

import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;






















































public final class BorderExtenderConstant
  extends BorderExtender
{
  private double[] constants;
  
  public BorderExtenderConstant(double[] constants)
  {
    this.constants = constants;
  }
  
  private int clamp(int band, int min, int max) {
    int length = constants.length;
    double c;
    double c; if (length == 1) {
      c = constants[0];
    } else if (band < length) {
      c = constants[band];
    } else {
      throw new UnsupportedOperationException(JaiI18N.getString("BorderExtenderConstant0"));
    }
    
    return c > min ? (int)c : c > max ? max : min;
  }
  





  public final double[] getConstants()
  {
    return (double[])constants;
  }
  





















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
    


    int dataType = raster.getSampleModel().getDataType();
    float[] fData; int row; int[] iData; int row; if (dataType == 4) {
      float[] fBandData = new float[numBands];
      for (int b = 0; b < numBands; b++) {
        fBandData[b] = (b < constants.length ? (float)constants[b] : 0.0F);
      }
      
      fData = new float[width * numBands];
      int index = 0;
      for (int i = 0; i < width; i++) {
        for (int b = 0; b < numBands; b++) {
          fData[(index++)] = fBandData[b];
        }
      }
      
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
          
          for (row = validMaxY; row < maxY; row++)
            raster.setPixels(minX, row, width, 1, fData);
        } } } else { double[] dData;
      int row;
      if (dataType == 5) {
        double[] dBandData = new double[numBands];
        for (int b = 0; b < numBands; b++) {
          dBandData[b] = (b < constants.length ? constants[b] : 0.0D);
        }
        
        dData = new double[width * numBands];
        int index = 0;
        for (int i = 0; i < width; i++) {
          for (int b = 0; b < numBands; b++) {
            dData[(index++)] = dBandData[b];
          }
        }
        
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
            
            for (row = validMaxY; row < maxY; row++)
              raster.setPixels(minX, row, width, 1, dData);
          }
        }
      } else {
        int[] iBandData = new int[numBands];
        switch (dataType) {
        case 0: 
          for (int b = 0; b < numBands; b++) {
            iBandData[b] = clamp(b, 0, 255);
          }
          break;
        case 2: 
          for (int b = 0; b < numBands; b++) {
            iBandData[b] = clamp(b, 32768, 32767);
          }
          break;
        case 1: 
          for (int b = 0; b < numBands; b++) {
            iBandData[b] = clamp(b, 0, 65535);
          }
          break;
        case 3: 
          for (int b = 0; b < numBands; b++) {
            iBandData[b] = clamp(b, Integer.MIN_VALUE, Integer.MAX_VALUE);
          }
          
          break;
        default: 
          throw new IllegalArgumentException(JaiI18N.getString("Generic3"));
        }
        
        iData = new int[width * numBands];
        int index = 0;
        for (int i = 0; i < width; i++) {
          for (int b = 0; b < numBands; b++) {
            iData[(index++)] = iBandData[b];
          }
        }
        
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
            
            for (row = validMaxY; row < maxY; row++) {
              raster.setPixels(minX, row, width, 1, iData);
            }
          }
        }
      }
    }
  }
}
