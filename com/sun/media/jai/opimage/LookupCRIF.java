package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;
import javax.media.jai.LookupTableJAI;



























public class LookupCRIF
  extends CRIFImpl
{
  public LookupCRIF()
  {
    super("lookup");
  }
  








  public RenderedImage create(ParameterBlock args, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    

    return new LookupOpImage(args.getRenderedSource(0), renderHints, layout, (LookupTableJAI)args.getObjectParameter(0));
  }
}
