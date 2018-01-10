package com.sun.media.jai.mlib;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ROI;
import javax.media.jai.util.ImagingListener;






























public class MlibHistogramRIF
  implements RenderedImageFactory
{
  public MlibHistogramRIF() {}
  
  public RenderedImage create(ParameterBlock args, RenderingHints hints)
  {
    if (!MediaLibAccessor.isMediaLibCompatible(args)) {
      return null;
    }
    

    RenderedImage src = args.getRenderedSource(0);
    int dataType = src.getSampleModel().getDataType();
    if ((dataType == 4) || (dataType == 5))
    {
      return null;
    }
    

    ROI roi = (ROI)args.getObjectParameter(0);
    if ((roi != null) && (!roi.equals(new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight()))))
    {

      return null;
    }
    

    int xPeriod = args.getIntParameter(1);
    int yPeriod = args.getIntParameter(2);
    int[] numBins = (int[])args.getObjectParameter(3);
    double[] lowValueFP = (double[])args.getObjectParameter(4);
    double[] highValueFP = (double[])args.getObjectParameter(5);
    int maxPixelValue;
    int maxPixelValue;
    int maxPixelValue;
    int minPixelValue;
    int maxPixelValue; switch (dataType) {
    case 2: 
      int minPixelValue = 32768;
      maxPixelValue = 32767;
      break;
    case 1: 
      int minPixelValue = 0;
      maxPixelValue = 65535;
      break;
    case 3: 
      int minPixelValue = Integer.MIN_VALUE;
      maxPixelValue = Integer.MAX_VALUE;
      break;
    case 0: 
    default: 
      minPixelValue = 0;
      maxPixelValue = 255;
    }
    
    for (int i = 0; i < lowValueFP.length; i++) {
      if ((lowValueFP[i] < minPixelValue) || (lowValueFP[i] > maxPixelValue))
      {
        return null;
      }
    }
    for (int i = 0; i < highValueFP.length; i++) {
      if ((highValueFP[i] <= minPixelValue) || (highValueFP[i] > maxPixelValue + 1))
      {
        return null;
      }
    }
    
    MlibHistogramOpImage op = null;
    try {
      op = new MlibHistogramOpImage(src, xPeriod, yPeriod, numBins, lowValueFP, highValueFP);
    }
    catch (Exception e)
    {
      ImagingListener listener = ImageUtil.getImagingListener(hints);
      String message = JaiI18N.getString("MlibHistogramRIF0");
      listener.errorOccurred(message, e, this, false);
    }
    
    return op;
  }
}
