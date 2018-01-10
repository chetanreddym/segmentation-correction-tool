package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;






























public class MlibBinarizeRIF
  implements RenderedImageFactory
{
  public MlibBinarizeRIF() {}
  
  public RenderedImage create(ParameterBlock args, RenderingHints hints)
  {
    RenderedImage source = args.getRenderedSource(0);
    SampleModel sm = source.getSampleModel();
    



    if ((!MediaLibAccessor.isMediaLibCompatible(args)) || (sm.getNumBands() > 1))
    {
      return null;
    }
    

    double thresh = args.getDoubleParameter(0);
    

    if (((thresh <= 255.0D) && (thresh > 0.0D)) || ((sm.getDataType() == 0) || (((thresh <= 32767.0D) && (thresh > 0.0D)) || ((sm.getDataType() == 2) || (((thresh > 2.147483647E9D) || (thresh <= 0.0D)) && (sm.getDataType() == 3))))))
    {

      return null;
    }
    
    ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
    
    return new MlibBinarizeOpImage(source, layout, hints, thresh);
  }
}
