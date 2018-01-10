package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
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
import javax.media.jai.operator.ShearDescriptor;























public class ShearRIF
  implements RenderedImageFactory
{
  public ShearRIF() {}
  
  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    


    BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
    
    RenderedImage source = paramBlock.getRenderedSource(0);
    
    float shear_amt = paramBlock.getFloatParameter(0);
    EnumeratedParameter shear_dir = (EnumeratedParameter)paramBlock.getObjectParameter(1);
    

    float xTrans = paramBlock.getFloatParameter(2);
    float yTrans = paramBlock.getFloatParameter(3);
    
    Object arg1 = paramBlock.getObjectParameter(4);
    Interpolation interp = (Interpolation)arg1;
    
    double[] backgroundValues = (double[])paramBlock.getObjectParameter(5);
    

    AffineTransform tr = new AffineTransform();
    
    if (shear_dir.equals(ShearDescriptor.SHEAR_HORIZONTAL))
    {
      tr.setTransform(1.0D, 0.0D, shear_amt, 1.0D, xTrans, 0.0D);
    }
    else {
      tr.setTransform(1.0D, shear_amt, 0.0D, 1.0D, 0.0D, yTrans);
    }
    

    if ((interp instanceof InterpolationNearest)) {
      SampleModel sm = source.getSampleModel();
      boolean isBinary = ((sm instanceof MultiPixelPackedSampleModel)) && (sm.getSampleSize(0) == 1) && ((sm.getDataType() == 0) || (sm.getDataType() == 1) || (sm.getDataType() == 3));
      



      if (isBinary) {
        return new AffineNearestBinaryOpImage(source, extender, renderHints, layout, tr, interp, backgroundValues);
      }
      





      return new AffineNearestOpImage(source, extender, renderHints, layout, tr, interp, backgroundValues);
    }
    




    if ((interp instanceof InterpolationBilinear)) {
      return new AffineBilinearOpImage(source, extender, renderHints, layout, tr, interp, backgroundValues);
    }
    




    if ((interp instanceof InterpolationBicubic)) {
      return new AffineBicubicOpImage(source, extender, renderHints, layout, tr, interp, backgroundValues);
    }
    




    if ((interp instanceof InterpolationBicubic2)) {
      return new AffineBicubic2OpImage(source, extender, renderHints, layout, tr, interp, backgroundValues);
    }
    





    return new AffineGeneralOpImage(source, extender, renderHints, layout, tr, interp, backgroundValues);
  }
}
