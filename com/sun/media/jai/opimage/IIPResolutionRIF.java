package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;




























public class IIPResolutionRIF
  implements RenderedImageFactory
{
  public IIPResolutionRIF() {}
  
  public RenderedImage create(ParameterBlock args, RenderingHints hints)
  {
    return new IIPResolutionOpImage(hints, (String)args.getObjectParameter(0), args.getIntParameter(1), args.getIntParameter(2));
  }
}
