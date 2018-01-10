package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;


































public class PNGRIF
  implements RenderedImageFactory
{
  public PNGRIF() {}
  
  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    return CodecRIFUtil.create("png", paramBlock, renderHints);
  }
}
