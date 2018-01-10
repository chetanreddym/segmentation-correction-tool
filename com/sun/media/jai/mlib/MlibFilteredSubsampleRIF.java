package com.sun.media.jai.mlib;

import java.awt.RenderingHints;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;



























public class MlibFilteredSubsampleRIF
  implements RenderedImageFactory
{
  public MlibFilteredSubsampleRIF() {}
  
  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    BorderExtender extender = renderHints == null ? null : (BorderExtender)renderHints.get(JAI.KEY_BORDER_EXTENDER);
    
    ImageLayout layout = renderHints == null ? null : (ImageLayout)renderHints.get(JAI.KEY_IMAGE_LAYOUT);
    


    if ((!MediaLibAccessor.isMediaLibCompatible(paramBlock, layout)) || (!MediaLibAccessor.hasSameNumBands(paramBlock, layout)))
    {
      return null;
    }
    
    RenderedImage source = paramBlock.getRenderedSource(0);
    SampleModel sm = source.getSampleModel();
    boolean isBilevel = ((sm instanceof MultiPixelPackedSampleModel)) && (sm.getSampleSize(0) == 1) && ((sm.getDataType() == 0) || (sm.getDataType() == 1) || (sm.getDataType() == 3));
    



    if (isBilevel)
    {
      return null;
    }
    
    int scaleX = paramBlock.getIntParameter(0);
    int scaleY = paramBlock.getIntParameter(1);
    float[] qsFilter = (float[])paramBlock.getObjectParameter(2);
    Interpolation interp = (Interpolation)paramBlock.getObjectParameter(3);
    
    return new MlibFilteredSubsampleOpImage(source, extender, renderHints, layout, scaleX, scaleY, qsFilter, interp);
  }
}
