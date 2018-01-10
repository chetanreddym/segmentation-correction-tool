package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.InterpolationTable;
import javax.media.jai.operator.ShearDescriptor;





























public class MlibShearRIF
  implements RenderedImageFactory
{
  public MlibShearRIF() {}
  
  public RenderedImage create(ParameterBlock args, RenderingHints hints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
    
    Interpolation interp = (Interpolation)args.getObjectParameter(4);
    
    RenderedImage source = args.getRenderedSource(0);
    
    if ((!MediaLibAccessor.isMediaLibCompatible(args, layout)) || (!MediaLibAccessor.hasSameNumBands(args, layout)) || (source.getTileWidth() >= 32768) || (source.getTileHeight() >= 32768))
    {




      return null;
    }
    

    BorderExtender extender = RIFUtil.getBorderExtenderHint(hints);
    
    float shear_amt = args.getFloatParameter(0);
    EnumeratedParameter shear_dir = (EnumeratedParameter)args.getObjectParameter(1);
    
    float xTrans = args.getFloatParameter(2);
    float yTrans = args.getFloatParameter(3);
    double[] backgroundValues = (double[])args.getObjectParameter(5);
    

    AffineTransform tr = new AffineTransform();
    
    if (shear_dir.equals(ShearDescriptor.SHEAR_HORIZONTAL))
    {
      tr.setTransform(1.0D, 0.0D, shear_amt, 1.0D, xTrans, 0.0D);
    }
    else {
      tr.setTransform(1.0D, shear_amt, 0.0D, 1.0D, 0.0D, yTrans);
    }
    
    if ((interp instanceof InterpolationNearest)) {
      return new MlibAffineNearestOpImage(source, extender, hints, layout, tr, interp, backgroundValues);
    }
    


    if ((interp instanceof InterpolationBilinear)) {
      return new MlibAffineBilinearOpImage(source, extender, hints, layout, tr, interp, backgroundValues);
    }
    


    if (((interp instanceof InterpolationBicubic)) || ((interp instanceof InterpolationBicubic2)))
    {
      return new MlibAffineBicubicOpImage(source, extender, hints, layout, tr, interp, backgroundValues);
    }
    


    if ((interp instanceof InterpolationTable)) {
      return new MlibAffineTableOpImage(source, extender, hints, layout, tr, interp, backgroundValues);
    }
    



    return null;
  }
}
