package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.opimage.TranslateIntOpImage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.InterpolationTable;





























public class MlibTranslateRIF
  implements RenderedImageFactory
{
  private static final float TOLERANCE = 0.01F;
  
  public MlibTranslateRIF() {}
  
  public RenderedImage create(ParameterBlock args, RenderingHints hints)
  {
    RenderedImage source = args.getRenderedSource(0);
    float xTrans = args.getFloatParameter(0);
    float yTrans = args.getFloatParameter(1);
    Interpolation interp = (Interpolation)args.getObjectParameter(2);
    

    ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
    
    if ((Math.abs(xTrans - (int)xTrans) < 0.01F) && (Math.abs(yTrans - (int)yTrans) < 0.01F) && (layout == null))
    {

      return new TranslateIntOpImage(source, hints, (int)xTrans, (int)yTrans);
    }
    



    if ((!MediaLibAccessor.isMediaLibCompatible(args, layout)) || (!MediaLibAccessor.hasSameNumBands(args, layout)) || (source.getTileWidth() >= 32768) || (source.getTileHeight() >= 32768))
    {




      return null;
    }
    

    BorderExtender extender = RIFUtil.getBorderExtenderHint(hints);
    




    float xScale = 1.0F;
    float yScale = 1.0F;
    if ((interp instanceof InterpolationNearest)) {
      return new MlibScaleNearestOpImage(source, extender, hints, layout, xScale, yScale, xTrans, yTrans, interp);
    }
    


    if ((interp instanceof InterpolationBilinear)) {
      return new MlibScaleBilinearOpImage(source, extender, hints, layout, xScale, yScale, xTrans, yTrans, interp);
    }
    


    if (((interp instanceof InterpolationBicubic)) || ((interp instanceof InterpolationBicubic2)))
    {
      return new MlibScaleBicubicOpImage(source, extender, hints, layout, xScale, yScale, xTrans, yTrans, interp);
    }
    


    if ((interp instanceof InterpolationTable)) {
      return new MlibScaleTableOpImage(source, extender, hints, layout, xScale, yScale, xTrans, yTrans, interp);
    }
    



    return null;
  }
}
