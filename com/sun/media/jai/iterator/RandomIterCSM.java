package com.sun.media.jai.iterator;

import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;















public abstract class RandomIterCSM
  extends RandomIterFallback
{
  protected ComponentSampleModel sampleModel;
  protected int pixelStride;
  protected int scanlineStride;
  protected int[] bandOffsets;
  protected int numBands;
  
  public RandomIterCSM(RenderedImage im, Rectangle bounds)
  {
    super(im, bounds);
    sampleModel = ((ComponentSampleModel)im.getSampleModel());
    numBands = sampleModel.getNumBands();
    pixelStride = sampleModel.getPixelStride();
    scanlineStride = sampleModel.getScanlineStride();
  }
  



  protected void dataBufferChanged() {}
  



  protected final void makeCurrent(int xLocal, int yLocal)
  {
    int xIDNew = xTiles[xLocal];
    int yIDNew = yTiles[yLocal];
    
    if ((xIDNew != xID) || (yIDNew != yID) || (dataBuffer == null)) {
      xID = xIDNew;
      yID = yIDNew;
      Raster tile = im.getTile(xID, yID);
      
      dataBuffer = tile.getDataBuffer();
      dataBufferChanged();
      
      bandOffsets = dataBuffer.getOffsets();
    }
  }
  
  public float getSampleFloat(int x, int y, int b) {
    return getSample(x, y, b);
  }
  
  public double getSampleDouble(int x, int y, int b) {
    return getSample(x, y, b);
  }
  
  public int[] getPixel(int x, int y, int[] iArray) {
    if (iArray == null) {
      iArray = new int[numBands];
    }
    for (int b = 0; b < numBands; b++) {
      iArray[b] = getSample(x, y, b);
    }
    return iArray;
  }
  
  public float[] getPixel(int x, int y, float[] fArray) {
    if (fArray == null) {
      fArray = new float[numBands];
    }
    for (int b = 0; b < numBands; b++) {
      fArray[b] = getSampleFloat(x, y, b);
    }
    return fArray;
  }
  
  public double[] getPixel(int x, int y, double[] dArray) {
    if (dArray == null) {
      dArray = new double[numBands];
    }
    for (int b = 0; b < numBands; b++) {
      dArray[b] = getSampleDouble(x, y, b);
    }
    return dArray;
  }
}
