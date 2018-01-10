package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;


























public class MlibErodeRIF
  implements RenderedImageFactory
{
  public MlibErodeRIF() {}
  
  public RenderedImage create(ParameterBlock args, RenderingHints hints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
    

    boolean isBinary = false;
    if ((!MediaLibAccessor.isMediaLibCompatible(args, layout)) || (!MediaLibAccessor.hasSameNumBands(args, layout)))
    {
      if (!MediaLibAccessor.isMediaLibBinaryCompatible(args, layout)) {
        return null;
      }
      isBinary = true;
    }
    

    BorderExtender extender = RIFUtil.getBorderExtenderHint(hints);
    
    RenderedImage source = args.getRenderedSource(0);
    
    KernelJAI unRotatedKernel = (KernelJAI)args.getObjectParameter(0);
    KernelJAI kJAI = unRotatedKernel.getRotatedKernel();
    
    int kWidth = kJAI.getWidth();
    int kHeight = kJAI.getHeight();
    int xOri = kJAI.getXOrigin();
    int yOri = kJAI.getYOrigin();
    int numB = source.getSampleModel().getNumBands();
    


    if ((xOri != 1) || (yOri != 1) || (kWidth != 3) || (kHeight != 3) || (numB != 1)) {
      return null;
    }
    


    float[] kdata = kJAI.getKernelData();
    
    if (((isBinary) && (isKernel3Square1(kdata))) || ((!isBinary) && (isKernel3Square0(kdata))))
    {
      return new MlibErode3SquareOpImage(source, extender, hints, layout);
    }
    


    if ((isBinary) && (isKernel3Plus1(kdata)))
    {

      return new MlibErode3PlusOpImage(source, extender, hints, layout);
    }
    


    return null;
  }
  


  private boolean isKernel3Plus1(float[] kdata)
  {
    return (kdata[0] == 0.0F) && (kdata[1] == 1.0F) && (kdata[2] == 0.0F) && (kdata[3] == 1.0F) && (kdata[4] == 1.0F) && (kdata[5] == 1.0F) && (kdata[6] == 0.0F) && (kdata[7] == 1.0F) && (kdata[8] == 0.0F);
  }
  



  private boolean isKernel3Square0(float[] kdata)
  {
    return (kdata[0] == 0.0F) && (kdata[1] == 0.0F) && (kdata[2] == 0.0F) && (kdata[3] == 0.0F) && (kdata[4] == 0.0F) && (kdata[5] == 0.0F) && (kdata[6] == 0.0F) && (kdata[7] == 0.0F) && (kdata[8] == 0.0F);
  }
  



  private boolean isKernel3Square1(float[] kdata)
  {
    return (kdata[0] == 1.0F) && (kdata[1] == 1.0F) && (kdata[2] == 1.0F) && (kdata[3] == 1.0F) && (kdata[4] == 1.0F) && (kdata[5] == 1.0F) && (kdata[6] == 1.0F) && (kdata[7] == 1.0F) && (kdata[8] == 1.0F);
  }
}
