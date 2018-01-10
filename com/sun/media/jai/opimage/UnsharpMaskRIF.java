package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;

























public class UnsharpMaskRIF
  implements RenderedImageFactory
{
  public UnsharpMaskRIF() {}
  
  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    


    BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
    


    KernelJAI unRotatedKernel = ImageUtil.getUnsharpMaskEquivalentKernel((KernelJAI)paramBlock.getObjectParameter(0), paramBlock.getFloatParameter(1));
    



    KernelJAI kJAI = unRotatedKernel.getRotatedKernel();
    
    RenderedImage source = paramBlock.getRenderedSource(0);
    int dataType = source.getSampleModel().getDataType();
    
    boolean dataTypeOk = (dataType == 0) || (dataType == 2) || (dataType == 3);
    


    if ((kJAI.getWidth() == 3) && (kJAI.getHeight() == 3) && (kJAI.getXOrigin() == 1) && (kJAI.getYOrigin() == 1) && (dataTypeOk))
    {
      return new Convolve3x3OpImage(source, extender, renderHints, layout, kJAI);
    }
    


    if (kJAI.isSeparable()) {
      return new SeparableConvolveOpImage(source, extender, renderHints, layout, kJAI);
    }
    




    return new ConvolveOpImage(source, extender, renderHints, layout, kJAI);
  }
}
