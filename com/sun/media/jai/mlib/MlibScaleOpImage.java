package com.sun.media.jai.mlib;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.ScaleOpImage;















































abstract class MlibScaleOpImage
  extends ScaleOpImage
{
  public MlibScaleOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float scaleX, float scaleY, float transX, float transY, Interpolation interp, boolean cobbleSources)
  {
    super(source, layout, config, cobbleSources, extender, interp, scaleX, scaleY, transX, transY);
    













    this.extender = (extender == null ? BorderExtender.createInstance(1) : extender);
  }
  
























  protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex)
  {
    Rectangle srcRect = super.backwardMapRect(destRect, sourceIndex);
    Rectangle paddedSrcRect = new Rectangle(x - 1, y - 1, width + 2, height + 2);
    



    return paddedSrcRect;
  }
}