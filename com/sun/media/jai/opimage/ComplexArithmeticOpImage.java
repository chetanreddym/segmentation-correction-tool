package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;























final class ComplexArithmeticOpImage
  extends PointOpImage
{
  protected boolean isDivision = false;
  

  private int[] s1r;
  

  private int[] s1i;
  

  private int[] s2r;
  

  private int[] s2i;
  


  private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source)
  {
    ImageLayout il;
    
    ImageLayout il;
    
    if (layout == null) {
      il = new ImageLayout();
    } else {
      il = (ImageLayout)layout.clone();
    }
    
    if (il.isValid(256)) {
      SampleModel sm = il.getSampleModel(null);
      int nBands = sm.getNumBands();
      if (nBands % 2 != 0) {
        nBands++;
        sm = RasterFactory.createComponentSampleModel(sm, sm.getTransferType(), sm.getWidth(), sm.getHeight(), nBands);
        



        il.setSampleModel(sm);
        

        ColorModel cm = layout.getColorModel(null);
        if ((cm != null) && (!JDKWorkarounds.areCompatibleDataModels(sm, cm)))
        {

          il.unsetValid(512);
        }
      }
    }
    
    return il;
  }
  






















  public ComplexArithmeticOpImage(RenderedImage source1, RenderedImage source2, Map config, ImageLayout layout, boolean isDivision)
  {
    super(source1, source2, layoutHelper(layout, source1), config, true);
    

    this.isDivision = isDivision;
    

    int numBands1 = source1.getSampleModel().getNumBands();
    int numBands2 = source2.getSampleModel().getNumBands();
    

    int numBandsDst = Math.min(numBands1, numBands2);
    
    int numBandsFromHint = 0;
    if (layout != null) {
      numBandsFromHint = layout.getSampleModel(null).getNumBands();
    }
    if ((layout != null) && (layout.isValid(256)) && (((numBands1 == 2) && (numBands2 > 2)) || ((numBands2 == 2) && (numBands1 > 2)) || ((numBands1 >= numBandsFromHint) && (numBands2 >= numBandsFromHint) && (numBandsFromHint > 0))))
    {


      if (numBandsFromHint % 2 == 0) {
        numBandsDst = numBandsFromHint;
        

        numBandsDst = Math.min(Math.max(numBands1, numBands2), numBandsDst);
      }
    }
    

    if (numBandsDst != sampleModel.getNumBands()) {
      sampleModel = RasterFactory.createComponentSampleModel(sampleModel, sampleModel.getTransferType(), sampleModel.getWidth(), sampleModel.getHeight(), numBandsDst);
      






      if ((colorModel != null) && (!JDKWorkarounds.areCompatibleDataModels(sampleModel, colorModel)))
      {

        colorModel = ImageUtil.getCompatibleColorModel(sampleModel, config);
      }
    }
    


    int numElements = sampleModel.getNumBands() / 2;
    s1r = new int[numElements];
    s1i = new int[numElements];
    s2r = new int[numElements];
    s2i = new int[numElements];
    int s1Inc = numBands1 > 2 ? 2 : 0;
    int s2Inc = numBands2 > 2 ? 2 : 0;
    int i1 = 0;
    int i2 = 0;
    for (int b = 0; b < numElements; b++) {
      s1r[b] = i1;
      s1i[b] = (i1 + 1);
      s2r[b] = i2;
      s2i[b] = (i2 + 1);
      i1 += s1Inc;
      i2 += s2Inc;
    }
    

    permitInPlaceOperation();
  }
  










  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    
    RasterAccessor src1Accessor = new RasterAccessor(sources[0], destRect, formatTags[0], getSourceImage(0).getColorModel());
    


    RasterAccessor src2Accessor = new RasterAccessor(sources[1], destRect, formatTags[1], getSourceImage(1).getColorModel());
    


    RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[2], getColorModel());
    



    switch (dstAccessor.getDataType()) {
    case 0: 
      computeRectByte(src1Accessor, src2Accessor, dstAccessor);
      break;
    case 2: 
      computeRectShort(src1Accessor, src2Accessor, dstAccessor);
      break;
    case 1: 
      computeRectUShort(src1Accessor, src2Accessor, dstAccessor);
      break;
    case 3: 
      computeRectInt(src1Accessor, src2Accessor, dstAccessor);
      break;
    case 4: 
      computeRectFloat(src1Accessor, src2Accessor, dstAccessor);
      break;
    case 5: 
      computeRectDouble(src1Accessor, src2Accessor, dstAccessor);
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("ComplexArithmeticOpImage0"));
    }
    
    if (dstAccessor.needsClamping()) {
      dstAccessor.clampDataArrays();
    }
    
    dstAccessor.copyDataToRaster();
  }
  


  private void computeRectDouble(RasterAccessor src1Accessor, RasterAccessor src2Accessor, RasterAccessor dstAccessor)
  {
    int numRows = dstAccessor.getHeight();
    int numCols = dstAccessor.getWidth();
    

    int src1PixelStride = src1Accessor.getPixelStride();
    int src1ScanlineStride = src1Accessor.getScanlineStride();
    int src2PixelStride = src2Accessor.getPixelStride();
    int src2ScanlineStride = src2Accessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    

    int numElements = sampleModel.getNumBands() / 2;
    for (int element = 0; element < numElements; element++)
    {
      int realBand = 2 * element;
      int imagBand = realBand + 1;
      

      double[] src1Real = src1Accessor.getDoubleDataArray(s1r[element]);
      double[] src1Imag = src1Accessor.getDoubleDataArray(s1i[element]);
      double[] src2Real = src2Accessor.getDoubleDataArray(s2r[element]);
      double[] src2Imag = src2Accessor.getDoubleDataArray(s2i[element]);
      double[] dstReal = dstAccessor.getDoubleDataArray(realBand);
      double[] dstImag = dstAccessor.getDoubleDataArray(imagBand);
      

      int src1OffsetReal = src1Accessor.getBandOffset(s1r[element]);
      int src1OffsetImag = src1Accessor.getBandOffset(s1i[element]);
      int src2OffsetReal = src2Accessor.getBandOffset(s2r[element]);
      int src2OffsetImag = src2Accessor.getBandOffset(s2i[element]);
      int dstOffsetReal = dstAccessor.getBandOffset(realBand);
      int dstOffsetImag = dstAccessor.getBandOffset(imagBand);
      

      int src1LineReal = src1OffsetReal;
      int src1LineImag = src1OffsetImag;
      int src2LineReal = src2OffsetReal;
      int src2LineImag = src2OffsetImag;
      int dstLineReal = dstOffsetReal;
      int dstLineImag = dstOffsetImag;
      
      for (int row = 0; row < numRows; row++)
      {
        int src1PixelReal = src1LineReal;
        int src1PixelImag = src1LineImag;
        int src2PixelReal = src2LineReal;
        int src2PixelImag = src2LineImag;
        int dstPixelReal = dstLineReal;
        int dstPixelImag = dstLineImag;
        

        if (isDivision) {
          for (int col = 0; col < numCols; col++) {
            double a = src1Real[src1PixelReal];
            double b = src1Imag[src1PixelImag];
            double c = src2Real[src2PixelReal];
            double d = src2Imag[src2PixelImag];
            
            double denom = c * c + d * d;
            dstReal[dstPixelReal] = ((a * c + b * d) / denom);
            dstImag[dstPixelImag] = ((b * c - a * d) / denom);
            
            src1PixelReal += src1PixelStride;
            src1PixelImag += src1PixelStride;
            src2PixelReal += src2PixelStride;
            src2PixelImag += src2PixelStride;
            dstPixelReal += dstPixelStride;
            dstPixelImag += dstPixelStride;
          }
        } else {
          for (int col = 0; col < numCols; col++) {
            double a = src1Real[src1PixelReal];
            double b = src1Imag[src1PixelImag];
            double c = src2Real[src2PixelReal];
            double d = src2Imag[src2PixelImag];
            
            dstReal[dstPixelReal] = (a * c - b * d);
            dstImag[dstPixelImag] = (a * d + b * c);
            
            src1PixelReal += src1PixelStride;
            src1PixelImag += src1PixelStride;
            src2PixelReal += src2PixelStride;
            src2PixelImag += src2PixelStride;
            dstPixelReal += dstPixelStride;
            dstPixelImag += dstPixelStride;
          }
        }
        

        src1LineReal += src1ScanlineStride;
        src1LineImag += src1ScanlineStride;
        src2LineReal += src2ScanlineStride;
        src2LineImag += src2ScanlineStride;
        dstLineReal += dstScanlineStride;
        dstLineImag += dstScanlineStride;
      }
    }
  }
  


  private void computeRectFloat(RasterAccessor src1Accessor, RasterAccessor src2Accessor, RasterAccessor dstAccessor)
  {
    int numRows = dstAccessor.getHeight();
    int numCols = dstAccessor.getWidth();
    

    int src1PixelStride = src1Accessor.getPixelStride();
    int src1ScanlineStride = src1Accessor.getScanlineStride();
    int src2PixelStride = src2Accessor.getPixelStride();
    int src2ScanlineStride = src2Accessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    

    int numElements = sampleModel.getNumBands() / 2;
    for (int element = 0; element < numElements; element++)
    {
      int realBand = 2 * element;
      int imagBand = realBand + 1;
      

      float[] src1Real = src1Accessor.getFloatDataArray(s1r[element]);
      float[] src1Imag = src1Accessor.getFloatDataArray(s1i[element]);
      float[] src2Real = src2Accessor.getFloatDataArray(s2r[element]);
      float[] src2Imag = src2Accessor.getFloatDataArray(s2i[element]);
      float[] dstReal = dstAccessor.getFloatDataArray(realBand);
      float[] dstImag = dstAccessor.getFloatDataArray(imagBand);
      

      int src1OffsetReal = src1Accessor.getBandOffset(s1r[element]);
      int src1OffsetImag = src1Accessor.getBandOffset(s1i[element]);
      int src2OffsetReal = src2Accessor.getBandOffset(s2r[element]);
      int src2OffsetImag = src2Accessor.getBandOffset(s2i[element]);
      int dstOffsetReal = dstAccessor.getBandOffset(realBand);
      int dstOffsetImag = dstAccessor.getBandOffset(imagBand);
      

      int src1LineReal = src1OffsetReal;
      int src1LineImag = src1OffsetImag;
      int src2LineReal = src2OffsetReal;
      int src2LineImag = src2OffsetImag;
      int dstLineReal = dstOffsetReal;
      int dstLineImag = dstOffsetImag;
      
      for (int row = 0; row < numRows; row++)
      {
        int src1PixelReal = src1LineReal;
        int src1PixelImag = src1LineImag;
        int src2PixelReal = src2LineReal;
        int src2PixelImag = src2LineImag;
        int dstPixelReal = dstLineReal;
        int dstPixelImag = dstLineImag;
        

        if (isDivision) {
          for (int col = 0; col < numCols; col++) {
            float a = src1Real[src1PixelReal];
            float b = src1Imag[src1PixelImag];
            float c = src2Real[src2PixelReal];
            float d = src2Imag[src2PixelImag];
            
            float denom = c * c + d * d;
            dstReal[dstPixelReal] = ((a * c + b * d) / denom);
            dstImag[dstPixelImag] = ((b * c - a * d) / denom);
            
            src1PixelReal += src1PixelStride;
            src1PixelImag += src1PixelStride;
            src2PixelReal += src2PixelStride;
            src2PixelImag += src2PixelStride;
            dstPixelReal += dstPixelStride;
            dstPixelImag += dstPixelStride;
          }
        } else {
          for (int col = 0; col < numCols; col++) {
            float a = src1Real[src1PixelReal];
            float b = src1Imag[src1PixelImag];
            float c = src2Real[src2PixelReal];
            float d = src2Imag[src2PixelImag];
            
            dstReal[dstPixelReal] = (a * c - b * d);
            dstImag[dstPixelImag] = (a * d + b * c);
            
            src1PixelReal += src1PixelStride;
            src1PixelImag += src1PixelStride;
            src2PixelReal += src2PixelStride;
            src2PixelImag += src2PixelStride;
            dstPixelReal += dstPixelStride;
            dstPixelImag += dstPixelStride;
          }
        }
        

        src1LineReal += src1ScanlineStride;
        src1LineImag += src1ScanlineStride;
        src2LineReal += src2ScanlineStride;
        src2LineImag += src2ScanlineStride;
        dstLineReal += dstScanlineStride;
        dstLineImag += dstScanlineStride;
      }
    }
  }
  


  private void computeRectInt(RasterAccessor src1Accessor, RasterAccessor src2Accessor, RasterAccessor dstAccessor)
  {
    int numRows = dstAccessor.getHeight();
    int numCols = dstAccessor.getWidth();
    

    int src1PixelStride = src1Accessor.getPixelStride();
    int src1ScanlineStride = src1Accessor.getScanlineStride();
    int src2PixelStride = src2Accessor.getPixelStride();
    int src2ScanlineStride = src2Accessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    

    int numElements = sampleModel.getNumBands() / 2;
    for (int element = 0; element < numElements; element++)
    {
      int realBand = 2 * element;
      int imagBand = realBand + 1;
      

      int[] src1Real = src1Accessor.getIntDataArray(s1r[element]);
      int[] src1Imag = src1Accessor.getIntDataArray(s1i[element]);
      int[] src2Real = src2Accessor.getIntDataArray(s2r[element]);
      int[] src2Imag = src2Accessor.getIntDataArray(s2i[element]);
      int[] dstReal = dstAccessor.getIntDataArray(realBand);
      int[] dstImag = dstAccessor.getIntDataArray(imagBand);
      

      int src1OffsetReal = src1Accessor.getBandOffset(s1r[element]);
      int src1OffsetImag = src1Accessor.getBandOffset(s1i[element]);
      int src2OffsetReal = src2Accessor.getBandOffset(s2r[element]);
      int src2OffsetImag = src2Accessor.getBandOffset(s2i[element]);
      int dstOffsetReal = dstAccessor.getBandOffset(realBand);
      int dstOffsetImag = dstAccessor.getBandOffset(imagBand);
      

      int src1LineReal = src1OffsetReal;
      int src1LineImag = src1OffsetImag;
      int src2LineReal = src2OffsetReal;
      int src2LineImag = src2OffsetImag;
      int dstLineReal = dstOffsetReal;
      int dstLineImag = dstOffsetImag;
      
      for (int row = 0; row < numRows; row++)
      {
        int src1PixelReal = src1LineReal;
        int src1PixelImag = src1LineImag;
        int src2PixelReal = src2LineReal;
        int src2PixelImag = src2LineImag;
        int dstPixelReal = dstLineReal;
        int dstPixelImag = dstLineImag;
        

        if (isDivision) {
          for (int col = 0; col < numCols; col++) {
            int a = src1Real[src1PixelReal];
            int b = src1Imag[src1PixelImag];
            int c = src2Real[src2PixelReal];
            int d = src2Imag[src2PixelImag];
            
            float denom = c * c + d * d;
            dstReal[dstPixelReal] = ImageUtil.clampRoundInt((a * c + b * d) / denom);
            
            dstImag[dstPixelImag] = ImageUtil.clampRoundInt((b * c - a * d) / denom);
            

            src1PixelReal += src1PixelStride;
            src1PixelImag += src1PixelStride;
            src2PixelReal += src2PixelStride;
            src2PixelImag += src2PixelStride;
            dstPixelReal += dstPixelStride;
            dstPixelImag += dstPixelStride;
          }
        } else {
          for (int col = 0; col < numCols; col++) {
            long a = src1Real[src1PixelReal];
            long b = src1Imag[src1PixelImag];
            long c = src2Real[src2PixelReal];
            long d = src2Imag[src2PixelImag];
            
            dstReal[dstPixelReal] = ImageUtil.clampInt(a * c - b * d);
            dstImag[dstPixelImag] = ImageUtil.clampInt(a * d + b * c);
            
            src1PixelReal += src1PixelStride;
            src1PixelImag += src1PixelStride;
            src2PixelReal += src2PixelStride;
            src2PixelImag += src2PixelStride;
            dstPixelReal += dstPixelStride;
            dstPixelImag += dstPixelStride;
          }
        }
        

        src1LineReal += src1ScanlineStride;
        src1LineImag += src1ScanlineStride;
        src2LineReal += src2ScanlineStride;
        src2LineImag += src2ScanlineStride;
        dstLineReal += dstScanlineStride;
        dstLineImag += dstScanlineStride;
      }
    }
  }
  


  private void computeRectUShort(RasterAccessor src1Accessor, RasterAccessor src2Accessor, RasterAccessor dstAccessor)
  {
    int numRows = dstAccessor.getHeight();
    int numCols = dstAccessor.getWidth();
    

    int src1PixelStride = src1Accessor.getPixelStride();
    int src1ScanlineStride = src1Accessor.getScanlineStride();
    int src2PixelStride = src2Accessor.getPixelStride();
    int src2ScanlineStride = src2Accessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    

    int numElements = sampleModel.getNumBands() / 2;
    for (int element = 0; element < numElements; element++)
    {
      int realBand = 2 * element;
      int imagBand = realBand + 1;
      

      short[] src1Real = src1Accessor.getShortDataArray(s1r[element]);
      short[] src1Imag = src1Accessor.getShortDataArray(s1i[element]);
      short[] src2Real = src2Accessor.getShortDataArray(s2r[element]);
      short[] src2Imag = src2Accessor.getShortDataArray(s2i[element]);
      short[] dstReal = dstAccessor.getShortDataArray(realBand);
      short[] dstImag = dstAccessor.getShortDataArray(imagBand);
      

      int src1OffsetReal = src1Accessor.getBandOffset(s1r[element]);
      int src1OffsetImag = src1Accessor.getBandOffset(s1i[element]);
      int src2OffsetReal = src2Accessor.getBandOffset(s2r[element]);
      int src2OffsetImag = src2Accessor.getBandOffset(s2i[element]);
      int dstOffsetReal = dstAccessor.getBandOffset(realBand);
      int dstOffsetImag = dstAccessor.getBandOffset(imagBand);
      

      int src1LineReal = src1OffsetReal;
      int src1LineImag = src1OffsetImag;
      int src2LineReal = src2OffsetReal;
      int src2LineImag = src2OffsetImag;
      int dstLineReal = dstOffsetReal;
      int dstLineImag = dstOffsetImag;
      
      for (int row = 0; row < numRows; row++)
      {
        int src1PixelReal = src1LineReal;
        int src1PixelImag = src1LineImag;
        int src2PixelReal = src2LineReal;
        int src2PixelImag = src2LineImag;
        int dstPixelReal = dstLineReal;
        int dstPixelImag = dstLineImag;
        

        if (isDivision) {
          for (int col = 0; col < numCols; col++) {
            int a = src1Real[src1PixelReal] & 0xFFFF;
            int b = src1Imag[src1PixelImag] & 0xFFFF;
            int c = src2Real[src2PixelReal] & 0xFFFF;
            int d = src2Imag[src2PixelImag] & 0xFFFF;
            
            int denom = c * c + d * d;
            dstReal[dstPixelReal] = ImageUtil.clampUShort((a * c + b * d) / denom);
            dstImag[dstPixelImag] = ImageUtil.clampUShort((b * c - a * d) / denom);
            
            src1PixelReal += src1PixelStride;
            src1PixelImag += src1PixelStride;
            src2PixelReal += src2PixelStride;
            src2PixelImag += src2PixelStride;
            dstPixelReal += dstPixelStride;
            dstPixelImag += dstPixelStride;
          }
        } else {
          for (int col = 0; col < numCols; col++) {
            int a = src1Real[src1PixelReal] & 0xFFFF;
            int b = src1Imag[src1PixelImag] & 0xFFFF;
            int c = src2Real[src2PixelReal] & 0xFFFF;
            int d = src2Imag[src2PixelImag] & 0xFFFF;
            
            dstReal[dstPixelReal] = ImageUtil.clampUShort(a * c - b * d);
            dstImag[dstPixelImag] = ImageUtil.clampUShort(a * d + b * c);
            
            src1PixelReal += src1PixelStride;
            src1PixelImag += src1PixelStride;
            src2PixelReal += src2PixelStride;
            src2PixelImag += src2PixelStride;
            dstPixelReal += dstPixelStride;
            dstPixelImag += dstPixelStride;
          }
        }
        

        src1LineReal += src1ScanlineStride;
        src1LineImag += src1ScanlineStride;
        src2LineReal += src2ScanlineStride;
        src2LineImag += src2ScanlineStride;
        dstLineReal += dstScanlineStride;
        dstLineImag += dstScanlineStride;
      }
    }
  }
  


  private void computeRectShort(RasterAccessor src1Accessor, RasterAccessor src2Accessor, RasterAccessor dstAccessor)
  {
    int numRows = dstAccessor.getHeight();
    int numCols = dstAccessor.getWidth();
    

    int src1PixelStride = src1Accessor.getPixelStride();
    int src1ScanlineStride = src1Accessor.getScanlineStride();
    int src2PixelStride = src2Accessor.getPixelStride();
    int src2ScanlineStride = src2Accessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    

    int numElements = sampleModel.getNumBands() / 2;
    for (int element = 0; element < numElements; element++)
    {
      int realBand = 2 * element;
      int imagBand = realBand + 1;
      

      short[] src1Real = src1Accessor.getShortDataArray(s1r[element]);
      short[] src1Imag = src1Accessor.getShortDataArray(s1i[element]);
      short[] src2Real = src2Accessor.getShortDataArray(s2r[element]);
      short[] src2Imag = src2Accessor.getShortDataArray(s2i[element]);
      short[] dstReal = dstAccessor.getShortDataArray(realBand);
      short[] dstImag = dstAccessor.getShortDataArray(imagBand);
      

      int src1OffsetReal = src1Accessor.getBandOffset(s1r[element]);
      int src1OffsetImag = src1Accessor.getBandOffset(s1i[element]);
      int src2OffsetReal = src2Accessor.getBandOffset(s2r[element]);
      int src2OffsetImag = src2Accessor.getBandOffset(s2i[element]);
      int dstOffsetReal = dstAccessor.getBandOffset(realBand);
      int dstOffsetImag = dstAccessor.getBandOffset(imagBand);
      

      int src1LineReal = src1OffsetReal;
      int src1LineImag = src1OffsetImag;
      int src2LineReal = src2OffsetReal;
      int src2LineImag = src2OffsetImag;
      int dstLineReal = dstOffsetReal;
      int dstLineImag = dstOffsetImag;
      
      for (int row = 0; row < numRows; row++)
      {
        int src1PixelReal = src1LineReal;
        int src1PixelImag = src1LineImag;
        int src2PixelReal = src2LineReal;
        int src2PixelImag = src2LineImag;
        int dstPixelReal = dstLineReal;
        int dstPixelImag = dstLineImag;
        

        if (isDivision) {
          for (int col = 0; col < numCols; col++) {
            int a = src1Real[src1PixelReal];
            int b = src1Imag[src1PixelImag];
            int c = src2Real[src2PixelReal];
            int d = src2Imag[src2PixelImag];
            
            int denom = c * c + d * d;
            dstReal[dstPixelReal] = ImageUtil.clampShort((a * c + b * d) / denom);
            dstImag[dstPixelImag] = ImageUtil.clampShort((b * c - a * d) / denom);
            
            src1PixelReal += src1PixelStride;
            src1PixelImag += src1PixelStride;
            src2PixelReal += src2PixelStride;
            src2PixelImag += src2PixelStride;
            dstPixelReal += dstPixelStride;
            dstPixelImag += dstPixelStride;
          }
        } else {
          for (int col = 0; col < numCols; col++) {
            int a = src1Real[src1PixelReal];
            int b = src1Imag[src1PixelImag];
            int c = src2Real[src2PixelReal];
            int d = src2Imag[src2PixelImag];
            
            dstReal[dstPixelReal] = ImageUtil.clampShort(a * c - b * d);
            dstImag[dstPixelImag] = ImageUtil.clampShort(a * d + b * c);
            
            src1PixelReal += src1PixelStride;
            src1PixelImag += src1PixelStride;
            src2PixelReal += src2PixelStride;
            src2PixelImag += src2PixelStride;
            dstPixelReal += dstPixelStride;
            dstPixelImag += dstPixelStride;
          }
        }
        

        src1LineReal += src1ScanlineStride;
        src1LineImag += src1ScanlineStride;
        src2LineReal += src2ScanlineStride;
        src2LineImag += src2ScanlineStride;
        dstLineReal += dstScanlineStride;
        dstLineImag += dstScanlineStride;
      }
    }
  }
  


  private void computeRectByte(RasterAccessor src1Accessor, RasterAccessor src2Accessor, RasterAccessor dstAccessor)
  {
    int numRows = dstAccessor.getHeight();
    int numCols = dstAccessor.getWidth();
    

    int src1PixelStride = src1Accessor.getPixelStride();
    int src1ScanlineStride = src1Accessor.getScanlineStride();
    int src2PixelStride = src2Accessor.getPixelStride();
    int src2ScanlineStride = src2Accessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    

    int numElements = sampleModel.getNumBands() / 2;
    for (int element = 0; element < numElements; element++)
    {
      int realBand = 2 * element;
      int imagBand = realBand + 1;
      

      byte[] src1Real = src1Accessor.getByteDataArray(s1r[element]);
      byte[] src1Imag = src1Accessor.getByteDataArray(s1i[element]);
      byte[] src2Real = src2Accessor.getByteDataArray(s2r[element]);
      byte[] src2Imag = src2Accessor.getByteDataArray(s2i[element]);
      byte[] dstReal = dstAccessor.getByteDataArray(realBand);
      byte[] dstImag = dstAccessor.getByteDataArray(imagBand);
      

      int src1OffsetReal = src1Accessor.getBandOffset(s1r[element]);
      int src1OffsetImag = src1Accessor.getBandOffset(s1i[element]);
      int src2OffsetReal = src2Accessor.getBandOffset(s2r[element]);
      int src2OffsetImag = src2Accessor.getBandOffset(s2i[element]);
      int dstOffsetReal = dstAccessor.getBandOffset(realBand);
      int dstOffsetImag = dstAccessor.getBandOffset(imagBand);
      

      int src1LineReal = src1OffsetReal;
      int src1LineImag = src1OffsetImag;
      int src2LineReal = src2OffsetReal;
      int src2LineImag = src2OffsetImag;
      int dstLineReal = dstOffsetReal;
      int dstLineImag = dstOffsetImag;
      
      for (int row = 0; row < numRows; row++)
      {
        int src1PixelReal = src1LineReal;
        int src1PixelImag = src1LineImag;
        int src2PixelReal = src2LineReal;
        int src2PixelImag = src2LineImag;
        int dstPixelReal = dstLineReal;
        int dstPixelImag = dstLineImag;
        

        if (isDivision) {
          for (int col = 0; col < numCols; col++) {
            int a = src1Real[src1PixelReal] & 0xFF;
            int b = src1Imag[src1PixelImag] & 0xFF;
            int c = src2Real[src2PixelReal] & 0xFF;
            int d = src2Imag[src2PixelImag] & 0xFF;
            
            int denom = c * c + d * d;
            dstReal[dstPixelReal] = ImageUtil.clampByte((a * c + b * d) / denom);
            dstImag[dstPixelImag] = ImageUtil.clampByte((b * c - a * d) / denom);
            
            src1PixelReal += src1PixelStride;
            src1PixelImag += src1PixelStride;
            src2PixelReal += src2PixelStride;
            src2PixelImag += src2PixelStride;
            dstPixelReal += dstPixelStride;
            dstPixelImag += dstPixelStride;
          }
        } else {
          for (int col = 0; col < numCols; col++) {
            int a = src1Real[src1PixelReal] & 0xFF;
            int b = src1Imag[src1PixelImag] & 0xFF;
            int c = src2Real[src2PixelReal] & 0xFF;
            int d = src2Imag[src2PixelImag] & 0xFF;
            
            dstReal[dstPixelReal] = ImageUtil.clampByte(a * c - b * d);
            dstImag[dstPixelImag] = ImageUtil.clampByte(a * d + b * c);
            
            src1PixelReal += src1PixelStride;
            src1PixelImag += src1PixelStride;
            src2PixelReal += src2PixelStride;
            src2PixelImag += src2PixelStride;
            dstPixelReal += dstPixelStride;
            dstPixelImag += dstPixelStride;
          }
        }
        

        src1LineReal += src1ScanlineStride;
        src1LineImag += src1ScanlineStride;
        src2LineReal += src2ScanlineStride;
        src2LineImag += src2ScanlineStride;
        dstLineReal += dstScanlineStride;
        dstLineImag += dstScanlineStride;
      }
    }
  }
}
