package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;

































public class GIFRIF
  implements RenderedImageFactory
{
  public GIFRIF() {}
  
  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    return CodecRIFUtil.create("gif", paramBlock, renderHints);
  }
}
