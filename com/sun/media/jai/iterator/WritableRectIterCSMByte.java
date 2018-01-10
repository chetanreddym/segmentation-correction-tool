package com.sun.media.jai.iterator;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import javax.media.jai.iterator.WritableRectIter;












public class WritableRectIterCSMByte
  extends RectIterCSMByte
  implements WritableRectIter
{
  public WritableRectIterCSMByte(RenderedImage im, Rectangle bounds)
  {
    super(im, bounds);
  }
  
  public void setSample(int s) {
    bank[(offset + bandOffset)] = ((byte)s);
  }
  
  public void setSample(int b, int s) {
    bankData[b][(offset + bandOffsets[b])] = ((byte)s);
  }
  
  public void setSample(float s) {
    bank[(offset + bandOffset)] = ((byte)(int)s);
  }
  
  public void setSample(int b, float s) {
    bankData[b][(offset + bandOffsets[b])] = ((byte)(int)s);
  }
  
  public void setSample(double s) {
    bank[(offset + bandOffset)] = ((byte)(int)s);
  }
  
  public void setSample(int b, double s) {
    bankData[b][(offset + bandOffsets[b])] = ((byte)(int)s);
  }
  
  public void setPixel(int[] iArray) {
    for (int b = 0; b < numBands; b++) {
      bankData[b][(offset + bandOffsets[b])] = ((byte)iArray[b]);
    }
  }
  
  public void setPixel(float[] fArray) {
    for (int b = 0; b < numBands; b++) {
      bankData[b][(offset + bandOffsets[b])] = ((byte)(int)fArray[b]);
    }
  }
  
  public void setPixel(double[] dArray) {
    for (int b = 0; b < numBands; b++) {
      bankData[b][(offset + bandOffsets[b])] = ((byte)(int)dArray[b]);
    }
  }
}
