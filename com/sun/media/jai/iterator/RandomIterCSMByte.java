package com.sun.media.jai.iterator;

import java.awt.Rectangle;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;













public class RandomIterCSMByte
  extends RandomIterCSM
{
  byte[][] bankData;
  
  public RandomIterCSMByte(RenderedImage im, Rectangle bounds)
  {
    super(im, bounds);
  }
  
  protected final void dataBufferChanged() {
    bankData = ((DataBufferByte)dataBuffer).getBankData();
  }
  
  public final int getSample(int x, int y, int b) {
    makeCurrent(x - boundsX, y - boundsY);
    return bankData[b][((x - sampleModelTranslateX) * pixelStride + (y - sampleModelTranslateY) * scanlineStride + bandOffsets[b])] & 0xFF;
  }
  

  public final float getSampleFloat(int x, int y, int b)
  {
    makeCurrent(x - boundsX, y - boundsX);
    return bankData[b][((x - sampleModelTranslateX) * pixelStride + (y - sampleModelTranslateY) * scanlineStride + bandOffsets[b])] & 0xFF;
  }
  

  public final double getSampleDouble(int x, int y, int b)
  {
    makeCurrent(x - boundsX, y - boundsX);
    return bankData[b][((x - sampleModelTranslateX) * pixelStride + (y - sampleModelTranslateY) * scanlineStride + bandOffsets[b])] & 0xFF;
  }
  

  public int[] getPixel(int x, int y, int[] iArray)
  {
    if (iArray == null) {
      iArray = new int[numBands];
    }
    
    int offset = (x - sampleModelTranslateX) * pixelStride + (y - sampleModelTranslateY) * scanlineStride;
    
    for (int b = 0; b < numBands; b++) {
      iArray[b] = (bankData[b][(offset + bandOffsets[b])] & 0xFF);
    }
    return iArray;
  }
}
