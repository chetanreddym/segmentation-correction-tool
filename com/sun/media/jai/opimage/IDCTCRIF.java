package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;




















public class IDCTCRIF
  extends CRIFImpl
{
  public IDCTCRIF()
  {
    super("idct");
  }
  






  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    

    RenderedImage source = paramBlock.getRenderedSource(0);
    
    return new DCTOpImage(source, renderHints, layout, new FCT(false, 2));
  }
}
