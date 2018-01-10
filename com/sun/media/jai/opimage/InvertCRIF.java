package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;




















public class InvertCRIF
  extends CRIFImpl
{
  public InvertCRIF()
  {
    super("invert");
  }
  







  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    

    return new InvertOpImage(paramBlock.getRenderedSource(0), renderHints, layout);
  }
}
