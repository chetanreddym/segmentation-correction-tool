package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;




























public class MlibAndConstRIF
  implements RenderedImageFactory
{
  public MlibAndConstRIF() {}
  
  public RenderedImage create(ParameterBlock args, RenderingHints hints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
    

    if ((!MediaLibAccessor.isMediaLibCompatible(args, layout)) || (!MediaLibAccessor.hasSameNumBands(args, layout)))
    {
      return null;
    }
    

    if (layout != null) {
      SampleModel sm = layout.getSampleModel(null);
      
      if (sm != null) {
        int dtype = sm.getDataType();
        if ((dtype == 4) || (dtype == 5))
        {
          return null;
        }
      }
    }
    
    return new MlibAndConstOpImage(args.getRenderedSource(0), hints, layout, (int[])args.getObjectParameter(0));
  }
}
