package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;


























public class LogCRIF
  extends CRIFImpl
{
  public LogCRIF()
  {
    super("log");
  }
  








  public RenderedImage create(ParameterBlock pb, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    

    return new LogOpImage(pb.getRenderedSource(0), renderHints, layout);
  }
}
