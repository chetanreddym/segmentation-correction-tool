package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;



























public class MlibGradientRIF
  implements RenderedImageFactory
{
  public MlibGradientRIF() {}
  
  public RenderedImage create(ParameterBlock args, RenderingHints hints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
    

    if ((!MediaLibAccessor.isMediaLibCompatible(args, layout)) || (!MediaLibAccessor.hasSameNumBands(args, layout)))
    {
      return null;
    }
    

    BorderExtender extender = RIFUtil.getBorderExtenderHint(hints);
    
    RenderedImage source = args.getRenderedSource(0);
    




    KernelJAI kern_h = (KernelJAI)args.getObjectParameter(0);
    KernelJAI kern_v = (KernelJAI)args.getObjectParameter(1);
    

    int kWidth = kern_h.getWidth();
    int kHeight = kern_v.getHeight();
    


    float[] khdata = kern_h.getKernelData();
    float[] kvdata = kern_v.getKernelData();
    if ((khdata[0] == -1.0F) && (khdata[1] == -2.0F) && (khdata[2] == -1.0F) && (khdata[3] == 0.0F) && (khdata[4] == 0.0F) && (khdata[5] == 0.0F) && (khdata[6] == 1.0F) && (khdata[7] == 2.0F) && (khdata[8] == 1.0F) && (kvdata[0] == -1.0F) && (kvdata[1] == 0.0F) && (kvdata[2] == 1.0F) && (kvdata[3] == -2.0F) && (kvdata[4] == 0.0F) && (kvdata[5] == 2.0F) && (kvdata[6] == -1.0F) && (kvdata[7] == 0.0F) && (kvdata[8] == 1.0F) && (kWidth == 3) && (kHeight == 3))
    {





      return new MlibSobelOpImage(source, extender, hints, layout, kern_h);
    }
    



    return new MlibGradientOpImage(source, extender, hints, layout, kern_h, kern_v);
  }
}
