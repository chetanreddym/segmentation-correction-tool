package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;





















public class ColorConvertCRIF
  extends CRIFImpl
{
  public ColorConvertCRIF()
  {
    super("colorconvert");
  }
  








  public RenderedImage create(ParameterBlock args, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    

    return new ColorConvertOpImage(args.getRenderedSource(0), renderHints, layout, (ColorModel)args.getObjectParameter(0));
  }
}
