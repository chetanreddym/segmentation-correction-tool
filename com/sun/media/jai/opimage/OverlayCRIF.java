package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;



















public class OverlayCRIF
  extends CRIFImpl
{
  public OverlayCRIF()
  {
    super("overlay");
  }
  








  public RenderedImage create(ParameterBlock args, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    

    return new OverlayOpImage(args.getRenderedSource(0), args.getRenderedSource(1), renderHints, layout);
  }
}
