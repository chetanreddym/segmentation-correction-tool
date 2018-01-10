package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
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











































final class MagnitudePhaseOpImage
  extends PointOpImage
{
  public static final int MAGNITUDE = 1;
  public static final int MAGNITUDE_SQUARED = 2;
  public static final int PHASE = 3;
  protected int operationType;
  private double phaseGain = 1.0D;
  

  private double phaseBias = 0.0D;
  














  public MagnitudePhaseOpImage(RenderedImage source, Map config, ImageLayout layout, int operationType)
  {
    super(source, layout, config, true);
    

    this.operationType = operationType;
    

    boolean needNewSampleModel = false;
    


    int dataType = sampleModel.getTransferType();
    if ((layout != null) && (dataType != layout.getSampleModel(source).getTransferType()))
    {
      dataType = layout.getSampleModel(source).getTransferType();
      needNewSampleModel = true;
    }
    

    int numBands = sampleModel.getNumBands();
    if (numBands > source.getSampleModel().getNumBands() / 2) {
      numBands = source.getSampleModel().getNumBands() / 2;
      needNewSampleModel = true;
    }
    

    if (needNewSampleModel) {
      sampleModel = RasterFactory.createComponentSampleModel(sampleModel, dataType, sampleModel.getWidth(), sampleModel.getHeight(), numBands);
      




      if ((colorModel != null) && (!JDKWorkarounds.areCompatibleDataModels(sampleModel, colorModel)))
      {

        colorModel = ImageUtil.getCompatibleColorModel(sampleModel, config);
      }
    }
    

    if (operationType == 3)
    {
      switch (dataType) {
      case 0: 
        phaseGain = 40.58451048843331D;
        phaseBias = 3.141592653589793D;
        break;
      case 2: 
        phaseGain = 5215.030020292134D;
        phaseBias = 3.141592653589793D;
        break;
      case 1: 
        phaseGain = 10430.219195527361D;
        phaseBias = 3.141592653589793D;
        break;
      case 3: 
        phaseGain = 3.4178263762906086E8D;
        phaseBias = 3.141592653589793D;
        break;
      }
      
    }
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    

    RasterAccessor srcAccessor = new RasterAccessor(sources[0], destRect, formatTags[0], getSourceImage(0).getColorModel());
    


    RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    


    switch (dstAccessor.getDataType()) {
    case 0: 
      computeRectByte(srcAccessor, dstAccessor, height, width);
      
      break;
    case 2: 
      computeRectShort(srcAccessor, dstAccessor, height, width);
      
      break;
    case 1: 
      computeRectUShort(srcAccessor, dstAccessor, height, width);
      
      break;
    case 3: 
      computeRectInt(srcAccessor, dstAccessor, height, width);
      
      break;
    case 4: 
      computeRectFloat(srcAccessor, dstAccessor, height, width);
      
      break;
    case 5: 
      computeRectDouble(srcAccessor, dstAccessor, height, width);
      
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("MagnitudePhaseOpImage0"));
    }
    
    if (dstAccessor.needsClamping()) {
      dstAccessor.clampDataArrays();
    }
    

    dstAccessor.copyDataToRaster();
  }
  



  private void computeRectDouble(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols)
  {
    int srcPixelStride = srcAccessor.getPixelStride();
    int srcScanlineStride = srcAccessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    

    int numDstBands = sampleModel.getNumBands();
    for (int dstBand = 0; dstBand < numDstBands; dstBand++)
    {
      int srcBandReal = 2 * dstBand;
      int srcBandImag = srcBandReal + 1;
      

      double[] srcReal = srcAccessor.getDoubleDataArray(srcBandReal);
      double[] srcImag = srcAccessor.getDoubleDataArray(srcBandImag);
      double[] dstData = dstAccessor.getDoubleDataArray(dstBand);
      

      int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
      int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
      int dstOffset = dstAccessor.getBandOffset(dstBand);
      

      int srcLineReal = srcOffsetReal;
      int srcLineImag = srcOffsetImag;
      int dstLine = dstOffset;
      
      for (int row = 0; row < numRows; row++)
      {
        int srcPixelReal = srcLineReal;
        int srcPixelImag = srcLineImag;
        int dstPixel = dstLine;
        

        switch (operationType) {
        case 1: 
          for (int col = 0; col < numCols; col++) {
            double real = srcReal[srcPixelReal];
            double imag = srcImag[srcPixelImag];
            
            dstData[dstPixel] = Math.sqrt(real * real + imag * imag);
            

            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
          break;
        case 2: 
          for (int col = 0; col < numCols; col++) {
            double real = srcReal[srcPixelReal];
            double imag = srcImag[srcPixelImag];
            
            dstData[dstPixel] = (real * real + imag * imag);
            
            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
          break;
        case 3: 
          for (int col = 0; col < numCols; col++) {
            double real = srcReal[srcPixelReal];
            double imag = srcImag[srcPixelImag];
            
            dstData[dstPixel] = Math.atan2(imag, real);
            
            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
        }
        
        

        srcLineReal += srcScanlineStride;
        srcLineImag += srcScanlineStride;
        dstLine += dstScanlineStride;
      }
    }
  }
  



  private void computeRectFloat(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols)
  {
    int srcPixelStride = srcAccessor.getPixelStride();
    int srcScanlineStride = srcAccessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    

    int numDstBands = sampleModel.getNumBands();
    for (int dstBand = 0; dstBand < numDstBands; dstBand++)
    {
      int srcBandReal = 2 * dstBand;
      int srcBandImag = srcBandReal + 1;
      

      float[] srcReal = srcAccessor.getFloatDataArray(srcBandReal);
      float[] srcImag = srcAccessor.getFloatDataArray(srcBandImag);
      float[] dstData = dstAccessor.getFloatDataArray(dstBand);
      

      int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
      int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
      int dstOffset = dstAccessor.getBandOffset(dstBand);
      

      int srcLineReal = srcOffsetReal;
      int srcLineImag = srcOffsetImag;
      int dstLine = dstOffset;
      
      for (int row = 0; row < numRows; row++)
      {
        int srcPixelReal = srcLineReal;
        int srcPixelImag = srcLineImag;
        int dstPixel = dstLine;
        

        switch (operationType) {
        case 1: 
          for (int col = 0; col < numCols; col++) {
            float real = srcReal[srcPixelReal];
            float imag = srcImag[srcPixelImag];
            
            dstData[dstPixel] = ImageUtil.clampFloat(Math.sqrt(real * real + imag * imag));
            

            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
          break;
        case 2: 
          for (int col = 0; col < numCols; col++) {
            float real = srcReal[srcPixelReal];
            float imag = srcImag[srcPixelImag];
            
            dstData[dstPixel] = (real * real + imag * imag);
            
            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
          break;
        case 3: 
          for (int col = 0; col < numCols; col++) {
            float real = srcReal[srcPixelReal];
            float imag = srcImag[srcPixelImag];
            
            dstData[dstPixel] = ImageUtil.clampFloat(Math.atan2(imag, real));
            

            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
        }
        
        

        srcLineReal += srcScanlineStride;
        srcLineImag += srcScanlineStride;
        dstLine += dstScanlineStride;
      }
    }
  }
  



  private void computeRectInt(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols)
  {
    int srcPixelStride = srcAccessor.getPixelStride();
    int srcScanlineStride = srcAccessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    

    int numDstBands = sampleModel.getNumBands();
    for (int dstBand = 0; dstBand < numDstBands; dstBand++)
    {
      int srcBandReal = 2 * dstBand;
      int srcBandImag = srcBandReal + 1;
      

      int[] srcReal = srcAccessor.getIntDataArray(srcBandReal);
      int[] srcImag = srcAccessor.getIntDataArray(srcBandImag);
      int[] dstData = dstAccessor.getIntDataArray(dstBand);
      

      int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
      int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
      int dstOffset = dstAccessor.getBandOffset(dstBand);
      

      int srcLineReal = srcOffsetReal;
      int srcLineImag = srcOffsetImag;
      int dstLine = dstOffset;
      
      for (int row = 0; row < numRows; row++)
      {
        int srcPixelReal = srcLineReal;
        int srcPixelImag = srcLineImag;
        int dstPixel = dstLine;
        

        switch (operationType) {
        case 1: 
          for (int col = 0; col < numCols; col++) {
            int real = srcReal[srcPixelReal];
            int imag = srcImag[srcPixelImag];
            
            dstData[dstPixel] = ImageUtil.clampRoundInt(Math.sqrt(real * real + imag * imag));
            

            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
          break;
        case 2: 
          for (int col = 0; col < numCols; col++) {
            int real = srcReal[srcPixelReal];
            int imag = srcImag[srcPixelImag];
            
            dstData[dstPixel] = (real * real + imag * imag);
            
            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
          break;
        case 3: 
          for (int col = 0; col < numCols; col++) {
            int real = srcReal[srcPixelReal];
            int imag = srcImag[srcPixelImag];
            
            dstData[dstPixel] = ImageUtil.clampRoundInt((Math.atan2(imag, real) + phaseBias) * phaseGain);
            


            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
        }
        
        

        srcLineReal += srcScanlineStride;
        srcLineImag += srcScanlineStride;
        dstLine += dstScanlineStride;
      }
    }
  }
  



  private void computeRectUShort(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols)
  {
    int srcPixelStride = srcAccessor.getPixelStride();
    int srcScanlineStride = srcAccessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    

    int numDstBands = sampleModel.getNumBands();
    for (int dstBand = 0; dstBand < numDstBands; dstBand++)
    {
      int srcBandReal = 2 * dstBand;
      int srcBandImag = srcBandReal + 1;
      

      short[] srcReal = srcAccessor.getShortDataArray(srcBandReal);
      short[] srcImag = srcAccessor.getShortDataArray(srcBandImag);
      short[] dstData = dstAccessor.getShortDataArray(dstBand);
      

      int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
      int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
      int dstOffset = dstAccessor.getBandOffset(dstBand);
      

      int srcLineReal = srcOffsetReal;
      int srcLineImag = srcOffsetImag;
      int dstLine = dstOffset;
      
      for (int row = 0; row < numRows; row++)
      {
        int srcPixelReal = srcLineReal;
        int srcPixelImag = srcLineImag;
        int dstPixel = dstLine;
        

        switch (operationType) {
        case 1: 
          for (int col = 0; col < numCols; col++) {
            int real = srcReal[srcPixelReal] & 0xFFFF;
            int imag = srcImag[srcPixelImag] & 0xFFFF;
            
            dstData[dstPixel] = ImageUtil.clampRoundUShort(Math.sqrt(real * real + imag * imag));
            

            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
          break;
        case 2: 
          for (int col = 0; col < numCols; col++) {
            int real = srcReal[srcPixelReal] & 0xFFFF;
            int imag = srcImag[srcPixelImag] & 0xFFFF;
            
            dstData[dstPixel] = ImageUtil.clampUShort(real * real + imag * imag);
            

            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
          break;
        case 3: 
          for (int col = 0; col < numCols; col++) {
            int real = srcReal[srcPixelReal] & 0xFFFF;
            int imag = srcImag[srcPixelImag] & 0xFFFF;
            
            dstData[dstPixel] = ImageUtil.clampRoundUShort((Math.atan2(imag, real) + phaseBias) * phaseGain);
            


            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
        }
        
        

        srcLineReal += srcScanlineStride;
        srcLineImag += srcScanlineStride;
        dstLine += dstScanlineStride;
      }
    }
  }
  



  private void computeRectShort(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols)
  {
    int srcPixelStride = srcAccessor.getPixelStride();
    int srcScanlineStride = srcAccessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    

    int numDstBands = sampleModel.getNumBands();
    for (int dstBand = 0; dstBand < numDstBands; dstBand++)
    {
      int srcBandReal = 2 * dstBand;
      int srcBandImag = srcBandReal + 1;
      

      short[] srcReal = srcAccessor.getShortDataArray(srcBandReal);
      short[] srcImag = srcAccessor.getShortDataArray(srcBandImag);
      short[] dstData = dstAccessor.getShortDataArray(dstBand);
      

      int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
      int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
      int dstOffset = dstAccessor.getBandOffset(dstBand);
      

      int srcLineReal = srcOffsetReal;
      int srcLineImag = srcOffsetImag;
      int dstLine = dstOffset;
      
      for (int row = 0; row < numRows; row++)
      {
        int srcPixelReal = srcLineReal;
        int srcPixelImag = srcLineImag;
        int dstPixel = dstLine;
        

        switch (operationType) {
        case 1: 
          for (int col = 0; col < numCols; col++) {
            short real = srcReal[srcPixelReal];
            short imag = srcImag[srcPixelImag];
            
            dstData[dstPixel] = ImageUtil.clampRoundShort(Math.sqrt(real * real + imag * imag));
            

            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
          break;
        case 2: 
          for (int col = 0; col < numCols; col++) {
            short real = srcReal[srcPixelReal];
            short imag = srcImag[srcPixelImag];
            
            dstData[dstPixel] = ImageUtil.clampShort(real * real + imag * imag);
            
            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
          break;
        case 3: 
          for (int col = 0; col < numCols; col++) {
            short real = srcReal[srcPixelReal];
            short imag = srcImag[srcPixelImag];
            
            dstData[dstPixel] = ImageUtil.clampRoundShort((Math.atan2(imag, real) + phaseBias) * phaseGain);
            


            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
        }
        
        

        srcLineReal += srcScanlineStride;
        srcLineImag += srcScanlineStride;
        dstLine += dstScanlineStride;
      }
    }
  }
  



  private void computeRectByte(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols)
  {
    int srcPixelStride = srcAccessor.getPixelStride();
    int srcScanlineStride = srcAccessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    

    int numDstBands = sampleModel.getNumBands();
    for (int dstBand = 0; dstBand < numDstBands; dstBand++)
    {
      int srcBandReal = 2 * dstBand;
      int srcBandImag = srcBandReal + 1;
      

      byte[] srcReal = srcAccessor.getByteDataArray(srcBandReal);
      byte[] srcImag = srcAccessor.getByteDataArray(srcBandImag);
      byte[] dstData = dstAccessor.getByteDataArray(dstBand);
      

      int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
      int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
      int dstOffset = dstAccessor.getBandOffset(dstBand);
      

      int srcLineReal = srcOffsetReal;
      int srcLineImag = srcOffsetImag;
      int dstLine = dstOffset;
      
      for (int row = 0; row < numRows; row++)
      {
        int srcPixelReal = srcLineReal;
        int srcPixelImag = srcLineImag;
        int dstPixel = dstLine;
        

        switch (operationType) {
        case 1: 
          for (int col = 0; col < numCols; col++) {
            int real = srcReal[srcPixelReal] & 0xFF;
            int imag = srcImag[srcPixelImag] & 0xFF;
            
            dstData[dstPixel] = ImageUtil.clampRoundByte(Math.sqrt(real * real + imag * imag));
            

            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
          break;
        case 2: 
          for (int col = 0; col < numCols; col++) {
            int real = srcReal[srcPixelReal] & 0xFF;
            int imag = srcImag[srcPixelImag] & 0xFF;
            
            dstData[dstPixel] = ImageUtil.clampByte(real * real + imag * imag);
            

            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
          break;
        case 3: 
          for (int col = 0; col < numCols; col++) {
            int real = srcReal[srcPixelReal] & 0xFF;
            int imag = srcImag[srcPixelImag] & 0xFF;
            
            dstData[dstPixel] = ImageUtil.clampRoundByte((Math.atan2(imag, real) + phaseBias) * phaseGain);
            


            srcPixelReal += srcPixelStride;
            srcPixelImag += srcPixelStride;
            dstPixel += dstPixelStride;
          }
        }
        
        

        srcLineReal += srcScanlineStride;
        srcLineImag += srcScanlineStride;
        dstLine += dstScanlineStride;
      }
    }
  }
}
