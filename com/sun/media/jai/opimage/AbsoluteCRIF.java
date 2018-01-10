package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;


























public class AbsoluteCRIF
  extends CRIFImpl
{
  public AbsoluteCRIF()
  {
    super("absolute");
  }
  








  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    

    return new AbsoluteOpImage(paramBlock.getRenderedSource(0), renderHints, layout);
  }
}
