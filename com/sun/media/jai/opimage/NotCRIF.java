package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;























public class NotCRIF
  extends CRIFImpl
{
  public NotCRIF()
  {
    super("not");
  }
  









  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    

    return new NotOpImage(paramBlock.getRenderedSource(0), renderHints, layout);
  }
}
