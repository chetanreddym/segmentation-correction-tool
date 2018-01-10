package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;





















public class PhaseCRIF
  extends CRIFImpl
{
  public PhaseCRIF()
  {
    super("phase");
  }
  






  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    

    RenderedImage source = paramBlock.getRenderedSource(0);
    
    return new MagnitudePhaseOpImage(source, renderHints, layout, 3);
  }
}
