package com.sun.media.jai.iterator;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;













public class RandomIterCSMInt
  extends RandomIterCSM
{
  public RandomIterCSMInt(RenderedImage im, Rectangle bounds)
  {
    super(im, bounds);
  }
  
  public final int getSample(int x, int y, int b) {
    return 0;
  }
}
