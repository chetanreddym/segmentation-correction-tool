package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageFunction;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.SourcelessOpImage;



























final class ImageFunctionOpImage
  extends SourcelessOpImage
{
  protected ImageFunction function;
  protected float xScale;
  protected float yScale;
  protected float xTrans;
  protected float yTrans;
  
  private static SampleModel sampleModelHelper(int numBands, ImageLayout layout)
  {
    SampleModel sampleModel;
    if ((layout != null) && (layout.isValid(256))) {
      SampleModel sampleModel = layout.getSampleModel(null);
      
      if (sampleModel.getNumBands() != numBands) {
        throw new RuntimeException(JaiI18N.getString("ImageFunctionRIF0"));
      }
    }
    else {
      sampleModel = RasterFactory.createBandedSampleModel(4, 1, 1, numBands);
    }
    



    return sampleModel;
  }
  











  public ImageFunctionOpImage(ImageFunction function, int minX, int minY, int width, int height, float xScale, float yScale, float xTrans, float yTrans, Map config, ImageLayout layout)
  {
    super(layout, config, sampleModelHelper(function.getNumElements() * (function.isComplex() ? 2 : 1), layout), minX, minY, width, height);
    






    this.function = function;
    this.xScale = xScale;
    this.yScale = yScale;
    this.xTrans = xTrans;
    this.yTrans = yTrans;
  }
  






  protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect)
  {
    int dataType = sampleModel.getTransferType();
    int numBands = sampleModel.getNumBands();
    

    int length = width * height;
    Object data;
    Object data; if (dataType == 5) {
      data = function.isComplex() ? new double[2][length] : new double[length];
    }
    else {
      data = function.isComplex() ? new float[2][length] : new float[length];
    }
    

    if (dataType == 5) {
      double[] real = function.isComplex() ? ((double[][])data)[0] : (double[])data;
      
      double[] imag = function.isComplex() ? ((double[][])data)[1] : null;
      

      int element = 0;
      for (int band = 0; band < numBands; band++) {
        function.getElements(xScale * (x - xTrans), yScale * (y - yTrans), xScale, yScale, width, height, element++, real, imag);
        




        dest.setSamples(x, y, width, height, band, (double[])real);
        

        if (function.isComplex()) {
          dest.setSamples(x, y, width, height, ++band, imag);
        }
        
      }
    }
    else
    {
      float[] real = function.isComplex() ? ((float[][])data)[0] : (float[])data;
      
      float[] imag = function.isComplex() ? ((float[][])data)[1] : null;
      

      int element = 0;
      for (int band = 0; band < numBands; band++) {
        function.getElements(xScale * (x - xTrans), yScale * (y - yTrans), xScale, yScale, width, height, element++, real, imag);
        




        dest.setSamples(x, y, width, height, band, real);
        

        if (function.isComplex()) {
          dest.setSamples(x, y, width, height, ++band, imag);
        }
      }
    }
  }
}
