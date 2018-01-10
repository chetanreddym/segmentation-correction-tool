package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;



















public class BinarizeCRIF
  extends CRIFImpl
{
  public BinarizeCRIF()
  {
    super("binarize");
  }
  








  public RenderedImage create(ParameterBlock args, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    
    return new BinarizeOpImage(args.getRenderedSource(0), renderHints, layout, args.getDoubleParameter(0));
  }
}
