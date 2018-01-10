package com.sun.media.jai.opimage;

import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;


























final class ConstantOpImage
  extends PatternOpImage
{
  private static Raster makePattern(SampleModel sampleModel, Number[] bandValues)
  {
    WritableRaster pattern = RasterFactory.createWritableRaster(sampleModel, new Point(0, 0));
    

    int width = sampleModel.getWidth();
    int height = sampleModel.getHeight();
    int dataType = sampleModel.getTransferType();
    int numBands = sampleModel.getNumBands();
    
    switch (dataType) {
    case 0: 
      int[] bvalues = new int[numBands];
      for (int i = 0; i < numBands; i++) {
        bvalues[i] = (bandValues[i].intValue() & 0xFF);
      }
      

      for (int x = 0; x < width; x++) {
        pattern.setPixel(x, 0, bvalues);
      }
      break;
    
    case 1: 
    case 2: 
    case 3: 
      int[] ivalues = new int[numBands];
      for (int i = 0; i < numBands; i++) {
        ivalues[i] = bandValues[i].intValue();
      }
      

      for (int x = 0; x < width; x++) {
        pattern.setPixel(x, 0, ivalues);
      }
      break;
    
    case 4: 
      float[] fvalues = new float[numBands];
      for (int i = 0; i < numBands; i++) {
        fvalues[i] = bandValues[i].floatValue();
      }
      

      for (int x = 0; x < width; x++) {
        pattern.setPixel(x, 0, fvalues);
      }
      break;
    
    case 5: 
      double[] dvalues = new double[numBands];
      for (int i = 0; i < numBands; i++) {
        dvalues[i] = bandValues[i].doubleValue();
      }
      

      for (int x = 0; x < width; x++) {
        pattern.setPixel(x, 0, dvalues);
      }
    }
    
    

    Object odata = pattern.getDataElements(0, 0, width, 1, null);
    

    for (int y = 1; y < height; y++) {
      pattern.setDataElements(0, y, width, 1, odata);
    }
    
    return pattern;
  }
  
  private static SampleModel makeSampleModel(int width, int height, Number[] bandValues)
  {
    int numBands = bandValues.length;
    int dataType;
    int dataType;
    if ((bandValues instanceof Byte[])) {
      dataType = 0;
    } else if ((bandValues instanceof Short[]))
    {
      int dataType = 1;
      
      Short[] shortValues = (Short[])bandValues;
      for (int i = 0; i < numBands; i++)
        if (shortValues[i].shortValue() < 0) {
          dataType = 2;
          break;
        }
    } else { int dataType;
      if ((bandValues instanceof Integer[])) {
        dataType = 3; } else { int dataType;
        if ((bandValues instanceof Float[])) {
          dataType = 4; } else { int dataType;
          if ((bandValues instanceof Double[])) {
            dataType = 5;
          } else
            dataType = 32;
        }
      } }
    return RasterFactory.createPixelInterleavedSampleModel(dataType, width, height, numBands);
  }
  


  private static Raster patternHelper(int width, int height, Number[] bandValues)
  {
    SampleModel sampleModel = makeSampleModel(width, height, bandValues);
    return makePattern(sampleModel, bandValues);
  }
  
  private static ColorModel colorModelHelper(Number[] bandValues) {
    SampleModel sampleModel = makeSampleModel(1, 1, bandValues);
    return PlanarImage.createColorModel(sampleModel);
  }
  











  public ConstantOpImage(int minX, int minY, int width, int height, int tileWidth, int tileHeight, Number[] bandValues)
  {
    super(patternHelper(tileWidth, tileHeight, bandValues), colorModelHelper(bandValues), minX, minY, width, height);
  }
}
