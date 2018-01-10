package com.sun.media.jai.iterator;

import java.awt.Rectangle;
import java.awt.image.SampleModel;
import java.awt.image.WritableRenderedImage;
import javax.media.jai.iterator.WritableRookIter;














public class WritableRookIterFallback
  extends RookIterFallback
  implements WritableRookIter
{
  public WritableRookIterFallback(WritableRenderedImage im, Rectangle bounds)
  {
    super(im, bounds);
  }
  
  public void setSample(int s) {
    sampleModel.setSample(localX, localY, b, s, dataBuffer);
  }
  
  public void setSample(int b, int s) {
    sampleModel.setSample(localX, localY, b, s, dataBuffer);
  }
  
  public void setSample(float s) {
    sampleModel.setSample(localX, localY, b, s, dataBuffer);
  }
  
  public void setSample(int b, float s) {
    sampleModel.setSample(localX, localY, b, s, dataBuffer);
  }
  
  public void setSample(double s) {
    sampleModel.setSample(localX, localY, b, s, dataBuffer);
  }
  
  public void setSample(int b, double s) {
    sampleModel.setSample(localX, localY, b, s, dataBuffer);
  }
  
  public void setPixel(int[] iArray) {
    sampleModel.setPixel(localX, localY, iArray, dataBuffer);
  }
  
  public void setPixel(float[] fArray) {
    sampleModel.setPixel(localX, localY, fArray, dataBuffer);
  }
  
  public void setPixel(double[] dArray) {
    sampleModel.setPixel(localX, localY, dArray, dataBuffer);
  }
}
