package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;






























public class MlibThresholdRIF
  implements RenderedImageFactory
{
  public MlibThresholdRIF() {}
  
  public RenderedImage create(ParameterBlock args, RenderingHints hints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
    

    if ((!MediaLibAccessor.isMediaLibCompatible(args, layout)) || (!MediaLibAccessor.hasSameNumBands(args, layout)))
    {
      return null;
    }
    
    return new MlibThresholdOpImage(args.getRenderedSource(0), hints, layout, (double[])args.getObjectParameter(0), (double[])args.getObjectParameter(1), (double[])args.getObjectParameter(2));
  }
}