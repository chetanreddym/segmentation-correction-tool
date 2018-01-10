package com.sun.media.jai.opimage;

import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.media.jai.util.MathJAI;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.UntiledOpImage;
import javax.media.jai.operator.DFTDescriptor;














































public class DFTOpImage
  extends UntiledOpImage
{
  FFT fft;
  protected boolean complexSrc;
  protected boolean complexDst;
  
  private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, EnumeratedParameter dataNature)
  {
    ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
    


    il.setMinX(source.getMinX());
    il.setMinY(source.getMinY());
    



    int currentWidth = il.getWidth(source);
    int currentHeight = il.getHeight(source);
    int newWidth;
    int newWidth;
    int newHeight; if ((currentWidth == 1) && (currentHeight == 1)) { int newHeight;
      newWidth = newHeight = 1; } else { int newHeight;
      if ((currentWidth == 1) && (currentHeight > 1)) {
        int newWidth = 1;
        newHeight = MathJAI.nextPositivePowerOf2(currentHeight); } else { int newHeight;
        if ((currentWidth > 1) && (currentHeight == 1)) {
          int newWidth = MathJAI.nextPositivePowerOf2(currentWidth);
          newHeight = 1;
        } else {
          newWidth = MathJAI.nextPositivePowerOf2(currentWidth);
          newHeight = MathJAI.nextPositivePowerOf2(currentHeight);
        } } }
    il.setWidth(newWidth);
    il.setHeight(newHeight);
    

    boolean isComplexSource = !dataNature.equals(DFTDescriptor.REAL_TO_COMPLEX);
    
    boolean isComplexDest = !dataNature.equals(DFTDescriptor.COMPLEX_TO_REAL);
    


    boolean createNewSampleModel = false;
    

    SampleModel srcSampleModel = source.getSampleModel();
    int requiredNumBands = srcSampleModel.getNumBands();
    if ((isComplexSource) && (!isComplexDest)) {
      requiredNumBands /= 2;
    } else if ((!isComplexSource) && (isComplexDest)) {
      requiredNumBands *= 2;
    }
    

    SampleModel sm = il.getSampleModel(source);
    int numBands = sm.getNumBands();
    if (numBands != requiredNumBands) {
      numBands = requiredNumBands;
      createNewSampleModel = true;
    }
    

    int dataType = sm.getTransferType();
    if ((dataType != 4) && (dataType != 5))
    {
      dataType = 4;
      createNewSampleModel = true;
    }
    

    if (createNewSampleModel) {
      sm = RasterFactory.createComponentSampleModel(sm, dataType, newWidth, newHeight, numBands);
      



      il.setSampleModel(sm);
      

      ColorModel cm = il.getColorModel(null);
      if ((cm != null) && (!JDKWorkarounds.areCompatibleDataModels(sm, cm)))
      {

        il.unsetValid(512);
      }
    }
    
    return il;
  }
  


















  public DFTOpImage(RenderedImage source, Map config, ImageLayout layout, EnumeratedParameter dataNature, FFT fft)
  {
    super(source, config, layoutHelper(layout, source, dataNature));
    

    this.fft = fft;
    

    complexSrc = (!dataNature.equals(DFTDescriptor.REAL_TO_COMPLEX));
    complexDst = (!dataNature.equals(DFTDescriptor.COMPLEX_TO_REAL));
  }
  












  public Point2D mapDestPoint(Point2D destPt)
  {
    if (destPt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return null;
  }
  









  public Point2D mapSourcePoint(Point2D sourcePt)
  {
    if (sourcePt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return null;
  }
  








  protected void computeImage(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    

    if ((width == 1) && (height == 1)) {
      int nDstBands = sampleModel.getNumBands();
      double[] srcPixel = new double[source.getSampleModel().getNumBands()];
      
      source.getPixel(x, y, srcPixel);
      if ((complexSrc) && (complexDst)) {
        dest.setPixel(x, y, srcPixel);
      } else if (complexSrc) {
        for (int i = 0; i < nDstBands; i++)
        {
          dest.setSample(x, y, i, srcPixel[(2 * i)]);
        }
      } else if (complexDst) {
        for (int i = 0; i < nDstBands; i++)
        {
          dest.setSample(x, y, i, i % 2 == 0 ? srcPixel[(i / 2)] : 0.0D);
        }
        
      }
      else {
        throw new RuntimeException(JaiI18N.getString("DFTOpImage1"));
      }
      return;
    }
    

    fft.setLength(width > 1 ? getWidth() : getHeight());
    

    int srcWidth = source.getWidth();
    int srcHeight = source.getHeight();
    int srcX = source.getMinX();
    int srcY = source.getMinY();
    

    RasterFormatTag[] formatTags = getFormatTags();
    
    RasterAccessor srcAccessor = new RasterAccessor(source, new Rectangle(srcX, srcY, srcWidth, srcHeight), formatTags[0], getSourceImage(0).getColorModel());
    



    RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    


    int srcDataType = srcAccessor.getDataType();
    int dstDataType = dstAccessor.getDataType();
    

    int srcPixelStride = srcAccessor.getPixelStride();
    int srcScanlineStride = srcAccessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    int dstPixelStrideImag = 1;
    int dstLineStrideImag = width;
    if (complexDst) {
      dstPixelStrideImag = dstPixelStride;
      dstLineStrideImag = dstScanlineStride;
    }
    

    int srcBandIndex = 0;
    int srcBandStride = complexSrc ? 2 : 1;
    int dstBandIndex = 0;
    int dstBandStride = complexDst ? 2 : 1;
    

    int numComponents = complexDst ? dest.getSampleModel().getNumBands() / 2 : dest.getSampleModel().getNumBands();
    



    for (int comp = 0; comp < numComponents; comp++)
    {
      Object srcReal = srcAccessor.getDataArray(srcBandIndex);
      

      Object srcImag = null;
      if (complexSrc) {
        srcImag = srcAccessor.getDataArray(srcBandIndex + 1);
      }
      

      Object dstReal = dstAccessor.getDataArray(dstBandIndex);
      Object dstImag = null;
      if (complexDst) {
        dstImag = dstAccessor.getDataArray(dstBandIndex + 1);



      }
      else if (dstDataType == 4) {
        dstImag = new float[width * height];
      } else {
        dstImag = new double[width * height];
      }
      

      if (width > 1)
      {
        fft.setLength(getWidth());
        

        int srcOffsetReal = srcAccessor.getBandOffset(srcBandIndex);
        
        int srcOffsetImag = 0;
        if (complexSrc) {
          srcOffsetImag = srcAccessor.getBandOffset(srcBandIndex + 1);
        }
        


        int dstOffsetReal = dstAccessor.getBandOffset(dstBandIndex);
        
        int dstOffsetImag = 0;
        if (complexDst) {
          dstOffsetImag = dstAccessor.getBandOffset(dstBandIndex + 1);
        }
        


        for (int row = 0; row < srcHeight; row++)
        {
          fft.setData(srcDataType, srcReal, srcOffsetReal, srcPixelStride, srcImag, srcOffsetImag, srcPixelStride, srcWidth);
          




          fft.transform();
          

          fft.getData(dstDataType, dstReal, dstOffsetReal, dstPixelStride, dstImag, dstOffsetImag, dstPixelStrideImag);
          



          srcOffsetReal += srcScanlineStride;
          srcOffsetImag += srcScanlineStride;
          dstOffsetReal += dstScanlineStride;
          dstOffsetImag += dstLineStrideImag;
        }
      }
      
      if (width == 1)
      {







        int srcOffsetReal = srcAccessor.getBandOffset(srcBandIndex);
        
        int srcOffsetImag = 0;
        if (complexSrc) {
          srcOffsetImag = srcAccessor.getBandOffset(srcBandIndex + 1);
        }
        


        int dstOffsetReal = dstAccessor.getBandOffset(dstBandIndex);
        
        int dstOffsetImag = 0;
        if (complexDst) {
          dstOffsetImag = dstAccessor.getBandOffset(dstBandIndex + 1);
        }
        


        fft.setData(srcDataType, srcReal, srcOffsetReal, srcScanlineStride, srcImag, srcOffsetImag, srcScanlineStride, srcHeight);
        




        fft.transform();
        

        fft.getData(dstDataType, dstReal, dstOffsetReal, dstScanlineStride, dstImag, dstOffsetImag, dstLineStrideImag);

      }
      else if (height > 1)
      {
        fft.setLength(getHeight());
        

        int dstOffsetReal = dstAccessor.getBandOffset(dstBandIndex);
        
        int dstOffsetImag = 0;
        if (complexDst) {
          dstOffsetImag = dstAccessor.getBandOffset(dstBandIndex + 1);
        }
        


        for (int col = 0; col < width; col++)
        {
          fft.setData(dstDataType, dstReal, dstOffsetReal, dstScanlineStride, dstImag, dstOffsetImag, dstLineStrideImag, height);
          




          fft.transform();
          

          fft.getData(dstDataType, dstReal, dstOffsetReal, dstScanlineStride, complexDst ? dstImag : null, dstOffsetImag, dstLineStrideImag);
          




          dstOffsetReal += dstPixelStride;
          dstOffsetImag += dstPixelStrideImag;
        }
      }
      

      srcBandIndex += srcBandStride;
      dstBandIndex += dstBandStride;
    }
    
    if (dstAccessor.needsClamping()) {
      dstAccessor.clampDataArrays();
    }
    

    dstAccessor.copyDataToRaster();
  }
}
