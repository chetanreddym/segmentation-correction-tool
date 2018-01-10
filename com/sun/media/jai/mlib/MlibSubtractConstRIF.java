package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;





























public class MlibSubtractConstRIF
  implements RenderedImageFactory
{
  public MlibSubtractConstRIF() {}
  
  public RenderedImage create(ParameterBlock args, RenderingHints hints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
    

    if ((!MediaLibAccessor.isMediaLibCompatible(args, layout)) || (!MediaLibAccessor.hasSameNumBands(args, layout)))
    {
      return null;
    }
    

    double[] constants = (double[])args.getObjectParameter(0);
    int length = constants.length;
    
    double[] negConstants = new double[length];
    
    for (int i = 0; i < length; i++) {
      negConstants[i] = (-constants[i]);
    }
    
    return new MlibAddConstOpImage(args.getRenderedSource(0), hints, layout, negConstants);
  }
}
