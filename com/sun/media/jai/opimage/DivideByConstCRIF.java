package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;





















public class DivideByConstCRIF
  extends CRIFImpl
{
  public DivideByConstCRIF()
  {
    super("dividebyconst");
  }
  








  public RenderedImage create(ParameterBlock args, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    


    double[] constants = (double[])args.getObjectParameter(0);
    int length = constants.length;
    
    double[] invConstants = new double[length];
    
    for (int i = 0; i < length; i++) {
      invConstants[i] = (1.0D / constants[i]);
    }
    
    return new MultiplyConstOpImage(args.getRenderedSource(0), renderHints, layout, invConstants);
  }
}
