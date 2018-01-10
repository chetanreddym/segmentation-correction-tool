package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;























public class ConvolveRIF
  implements RenderedImageFactory
{
  public ConvolveRIF() {}
  
  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    


    BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
    
    KernelJAI unRotatedKernel = (KernelJAI)paramBlock.getObjectParameter(0);
    
    KernelJAI kJAI = unRotatedKernel.getRotatedKernel();
    
    int dataType = paramBlock.getRenderedSource(0).getSampleModel().getDataType();
    
    boolean dataTypeOk = (dataType == 0) || (dataType == 2) || (dataType == 3);
    


    if ((kJAI.getWidth() == 3) && (kJAI.getHeight() == 3) && (kJAI.getXOrigin() == 1) && (kJAI.getYOrigin() == 1) && (dataTypeOk))
    {

      return new Convolve3x3OpImage(paramBlock.getRenderedSource(0), extender, renderHints, layout, kJAI);
    }
    


    if (kJAI.isSeparable()) {
      return new SeparableConvolveOpImage(paramBlock.getRenderedSource(0), extender, renderHints, layout, kJAI);
    }
    




    return new ConvolveOpImage(paramBlock.getRenderedSource(0), extender, renderHints, layout, kJAI);
  }
}
