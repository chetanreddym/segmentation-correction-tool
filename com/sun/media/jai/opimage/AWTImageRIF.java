package com.sun.media.jai.opimage;

import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;





























public class AWTImageRIF
  implements RenderedImageFactory
{
  public AWTImageRIF() {}
  
  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    

    Image awtImage = (Image)paramBlock.getObjectParameter(0);
    

    if ((awtImage instanceof RenderedImage)) {
      return (RenderedImage)awtImage;
    }
    

    return new AWTImageOpImage(renderHints, layout, awtImage);
  }
}
