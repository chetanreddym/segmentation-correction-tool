package com.sun.media.jai.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.DeferredData;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.UnpackedImageData;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;



















public final class ImageUtil
{
  private static final float FLOAT_MIN = -3.4028235E38F;
  private static long counter;
  public static final int BYTE_MASK = 255;
  public static final int USHORT_MASK = 65535;
  
  public ImageUtil() {}
  
  public static final byte clampByte(int in)
  {
    return in >= 0 ? (byte)in : in > 255 ? -1 : 0;
  }
  
  public static final short clampUShort(int in)
  {
    return in >= 0 ? (short)in : in > 65535 ? -1 : 0;
  }
  
  public static final short clampShort(int in)
  {
    return in >= 32768 ? (short)in : in > 32767 ? Short.MAX_VALUE : Short.MIN_VALUE;
  }
  

  public static final int clampInt(long in)
  {
    return in >= -2147483648L ? (int)in : in > 2147483647L ? Integer.MAX_VALUE : Integer.MIN_VALUE;
  }
  

  public static final float clampFloat(double in)
  {
    return in >= -3.4028234663852886E38D ? (float)in : in > 3.4028234663852886E38D ? Float.MAX_VALUE : -3.4028235E38F;
  }
  




  public static final byte clampRoundByte(float in)
  {
    return in >= 0.0F ? (byte)(int)(in + 0.5F) : in > 255.0F ? -1 : 0;
  }
  




  public static final byte clampRoundByte(double in)
  {
    return in >= 0.0D ? (byte)(int)(in + 0.5D) : in > 255.0D ? -1 : 0;
  }
  



  public static final short clampRoundUShort(float in)
  {
    return in >= 0.0F ? (short)(int)(in + 0.5F) : in > 65535.0F ? -1 : 0;
  }
  




  public static final short clampRoundUShort(double in)
  {
    return in >= 0.0D ? (short)(int)(in + 0.5D) : in > 65535.0D ? -1 : 0;
  }
  




  public static final short clampRoundShort(float in)
  {
    return in >= -32768.0F ? (short)(int)Math.floor(in + 0.5F) : in > 32767.0F ? Short.MAX_VALUE : Short.MIN_VALUE;
  }
  





  public static final short clampRoundShort(double in)
  {
    return in >= -32768.0D ? (short)(int)Math.floor(in + 0.5D) : in > 32767.0D ? Short.MAX_VALUE : Short.MIN_VALUE;
  }
  





  public static final int clampRoundInt(float in)
  {
    return in >= -2.14748365E9F ? (int)Math.floor(in + 0.5F) : in > 2.14748365E9F ? Integer.MAX_VALUE : Integer.MIN_VALUE;
  }
  





  public static final int clampRoundInt(double in)
  {
    return in >= -2.147483648E9D ? (int)Math.floor(in + 0.5D) : in > 2.147483647E9D ? Integer.MAX_VALUE : Integer.MIN_VALUE;
  }
  


  public static final byte clampBytePositive(int in)
  {
    return in > 255 ? -1 : (byte)in;
  }
  
  public static final byte clampByteNegative(int in)
  {
    return in < 0 ? 0 : (byte)in;
  }
  



  public static final short clampUShortPositive(int in)
  {
    return in > 65535 ? -1 : (short)in;
  }
  



  public static final short clampUShortNegative(int in)
  {
    return in < 0 ? 0 : (short)in;
  }
  
  public static final void copyRaster(RasterAccessor src, RasterAccessor dst)
  {
    int srcPixelStride = src.getPixelStride();
    int srcLineStride = src.getScanlineStride();
    int[] srcBandOffsets = src.getBandOffsets();
    
    int dstPixelStride = dst.getPixelStride();
    int dstLineStride = dst.getScanlineStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    
    int width = dst.getWidth() * dstPixelStride;
    int height = dst.getHeight() * dstLineStride;
    int bands = dst.getNumBands();
    
    switch (dst.getDataType()) {
    case 0: 
      byte[][] bSrcData = src.getByteDataArrays();
      byte[][] bDstData = dst.getByteDataArrays();
      
      for (int b = 0; b < bands; b++) {
        byte[] s = bSrcData[b];
        byte[] d = bDstData[b];
        
        int heightEnd = dstBandOffsets[b] + height;
        
        int dstLineOffset = dstBandOffsets[b];
        int srcLineOffset = srcBandOffsets[b];
        while (dstLineOffset < heightEnd)
        {


          int widthEnd = dstLineOffset + width;
          
          int dstPixelOffset = dstLineOffset;
          int srcPixelOffset = srcLineOffset;
          while (dstPixelOffset < widthEnd)
          {


            d[dstPixelOffset] = s[srcPixelOffset];dstPixelOffset += dstPixelStride;srcPixelOffset += srcPixelStride;
          }
          dstLineOffset += dstLineStride;
          srcLineOffset += srcLineStride;
        }
      }
      










      break;
    
    case 1: 
    case 2: 
      short[][] sSrcData = src.getShortDataArrays();
      short[][] sDstData = dst.getShortDataArrays();
      
      for (int b = 0; b < bands; b++) {
        short[] s = sSrcData[b];
        short[] d = sDstData[b];
        
        int heightEnd = dstBandOffsets[b] + height;
        
        int dstLineOffset = dstBandOffsets[b];
        int srcLineOffset = srcBandOffsets[b];
        while (dstLineOffset < heightEnd)
        {


          int widthEnd = dstLineOffset + width;
          
          int dstPixelOffset = dstLineOffset;
          int srcPixelOffset = srcLineOffset;
          while (dstPixelOffset < widthEnd)
          {


            d[dstPixelOffset] = s[srcPixelOffset];dstPixelOffset += dstPixelStride;srcPixelOffset += srcPixelStride;
          }
          dstLineOffset += dstLineStride;
          srcLineOffset += srcLineStride;
        }
      }
      










      break;
    
    case 3: 
      int[][] iSrcData = src.getIntDataArrays();
      int[][] iDstData = dst.getIntDataArrays();
      
      for (int b = 0; b < bands; b++) {
        int[] s = iSrcData[b];
        int[] d = iDstData[b];
        
        int heightEnd = dstBandOffsets[b] + height;
        
        int dstLineOffset = dstBandOffsets[b];
        int srcLineOffset = srcBandOffsets[b];
        while (dstLineOffset < heightEnd)
        {


          int widthEnd = dstLineOffset + width;
          
          int dstPixelOffset = dstLineOffset;
          int srcPixelOffset = srcLineOffset;
          while (dstPixelOffset < widthEnd)
          {


            d[dstPixelOffset] = s[srcPixelOffset];dstPixelOffset += dstPixelStride;srcPixelOffset += srcPixelStride;
          }
          dstLineOffset += dstLineStride;
          srcLineOffset += srcLineStride;
        }
      }
      










      break;
    
    case 4: 
      float[][] fSrcData = src.getFloatDataArrays();
      float[][] fDstData = dst.getFloatDataArrays();
      
      for (int b = 0; b < bands; b++) {
        float[] s = fSrcData[b];
        float[] d = fDstData[b];
        
        int heightEnd = dstBandOffsets[b] + height;
        
        int dstLineOffset = dstBandOffsets[b];
        int srcLineOffset = srcBandOffsets[b];
        while (dstLineOffset < heightEnd)
        {


          int widthEnd = dstLineOffset + width;
          
          int dstPixelOffset = dstLineOffset;
          int srcPixelOffset = srcLineOffset;
          while (dstPixelOffset < widthEnd)
          {


            d[dstPixelOffset] = s[srcPixelOffset];dstPixelOffset += dstPixelStride;srcPixelOffset += srcPixelStride;
          }
          dstLineOffset += dstLineStride;
          srcLineOffset += srcLineStride;
        }
      }
      










      break;
    
    case 5: 
      double[][] dSrcData = src.getDoubleDataArrays();
      double[][] dDstData = dst.getDoubleDataArrays();
      
      for (int b = 0; b < bands; b++) {
        double[] s = dSrcData[b];
        double[] d = dDstData[b];
        
        int heightEnd = dstBandOffsets[b] + height;
        
        int dstLineOffset = dstBandOffsets[b];
        int srcLineOffset = srcBandOffsets[b];
        while (dstLineOffset < heightEnd)
        {


          int widthEnd = dstLineOffset + width;
          
          int dstPixelOffset = dstLineOffset;
          int srcPixelOffset = srcLineOffset;
          while (dstPixelOffset < widthEnd)
          {


            d[dstPixelOffset] = s[srcPixelOffset];dstPixelOffset += dstPixelStride;srcPixelOffset += srcPixelStride;
          }
          dstLineOffset += dstLineStride;
          srcLineOffset += srcLineStride;
        }
      }
    }
    
    











    if (dst.isDataCopy()) {
      dst.clampDataArrays();
      dst.copyDataToRaster();
    }
  }
  





  public boolean areEqualSampleModels(SampleModel sm1, SampleModel sm2)
  {
    if (sm1 == sm2)
    {
      return true; }
    if ((sm1.getClass() == sm2.getClass()) && (sm1.getDataType() == sm2.getDataType()) && (sm1.getTransferType() == sm2.getTransferType()) && (sm1.getWidth() == sm2.getWidth()) && (sm1.getHeight() == sm2.getHeight()))
    {






      if ((sm1 instanceof ComponentSampleModel)) {
        ComponentSampleModel csm1 = (ComponentSampleModel)sm1;
        ComponentSampleModel csm2 = (ComponentSampleModel)sm2;
        return (csm1.getPixelStride() == csm2.getPixelStride()) && (csm1.getScanlineStride() == csm2.getScanlineStride()) && (Arrays.equals(csm1.getBankIndices(), csm2.getBankIndices())) && (Arrays.equals(csm1.getBandOffsets(), csm2.getBandOffsets()));
      }
      



      if ((sm1 instanceof MultiPixelPackedSampleModel)) {
        MultiPixelPackedSampleModel mpp1 = (MultiPixelPackedSampleModel)sm1;
        
        MultiPixelPackedSampleModel mpp2 = (MultiPixelPackedSampleModel)sm2;
        
        return (mpp1.getPixelBitStride() == mpp2.getPixelBitStride()) && (mpp1.getScanlineStride() == mpp2.getScanlineStride()) && (mpp1.getDataBitOffset() == mpp2.getDataBitOffset());
      }
      
      if ((sm1 instanceof SinglePixelPackedSampleModel)) {
        SinglePixelPackedSampleModel spp1 = (SinglePixelPackedSampleModel)sm1;
        
        SinglePixelPackedSampleModel spp2 = (SinglePixelPackedSampleModel)sm2;
        
        return (spp1.getScanlineStride() == spp2.getScanlineStride()) && (Arrays.equals(spp1.getBitMasks(), spp2.getBitMasks()));
      }
    }
    

    return false;
  }
  






  public static boolean isBinary(SampleModel sm)
  {
    return ((sm instanceof MultiPixelPackedSampleModel)) && (((MultiPixelPackedSampleModel)sm).getPixelBitStride() == 1) && (sm.getNumBands() == 1);
  }
  

















  public static byte[] getPackedBinaryData(Raster raster, Rectangle rect)
  {
    SampleModel sm = raster.getSampleModel();
    if (!isBinary(sm)) {
      throw new IllegalArgumentException(JaiI18N.getString("ImageUtil0"));
    }
    
    int rectX = x;
    int rectY = y;
    int rectWidth = width;
    int rectHeight = height;
    
    DataBuffer dataBuffer = raster.getDataBuffer();
    
    int dx = rectX - raster.getSampleModelTranslateX();
    int dy = rectY - raster.getSampleModelTranslateY();
    
    MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
    int lineStride = mpp.getScanlineStride();
    int eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
    int bitOffset = mpp.getBitOffset(dx);
    
    int numBytesPerRow = (rectWidth + 7) / 8;
    if (((dataBuffer instanceof DataBufferByte)) && (eltOffset == 0) && (bitOffset == 0) && (numBytesPerRow == lineStride) && (((DataBufferByte)dataBuffer).getData().length == numBytesPerRow * rectHeight))
    {



      return ((DataBufferByte)dataBuffer).getData();
    }
    
    byte[] binaryDataArray = new byte[numBytesPerRow * rectHeight];
    
    int b = 0;
    
    if (bitOffset == 0) {
      if ((dataBuffer instanceof DataBufferByte)) {
        byte[] data = ((DataBufferByte)dataBuffer).getData();
        int stride = numBytesPerRow;
        int offset = 0;
        for (int y = 0; y < rectHeight; y++) {
          System.arraycopy(data, eltOffset, binaryDataArray, offset, stride);
          

          offset += stride;
          eltOffset += lineStride;
        }
      } else if (((dataBuffer instanceof DataBufferShort)) || ((dataBuffer instanceof DataBufferUShort)))
      {
        short[] data = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
        


        for (int y = 0; y < rectHeight; y++) {
          int xRemaining = rectWidth;
          int i = eltOffset;
          while (xRemaining > 8) {
            short datum = data[(i++)];
            binaryDataArray[(b++)] = ((byte)(datum >>> 8 & 0xFF));
            binaryDataArray[(b++)] = ((byte)(datum & 0xFF));
            xRemaining -= 16;
          }
          if (xRemaining > 0) {
            binaryDataArray[(b++)] = ((byte)(data[i] >>> 8 & 0xFF));
          }
          eltOffset += lineStride;
        }
      } else if ((dataBuffer instanceof DataBufferInt)) {
        int[] data = ((DataBufferInt)dataBuffer).getData();
        
        for (int y = 0; y < rectHeight; y++) {
          int xRemaining = rectWidth;
          int i = eltOffset;
          while (xRemaining > 24) {
            int datum = data[(i++)];
            binaryDataArray[(b++)] = ((byte)(datum >>> 24 & 0xFF));
            binaryDataArray[(b++)] = ((byte)(datum >>> 16 & 0xFF));
            binaryDataArray[(b++)] = ((byte)(datum >>> 8 & 0xFF));
            binaryDataArray[(b++)] = ((byte)(datum & 0xFF));
            xRemaining -= 32;
          }
          int shift = 24;
          while (xRemaining > 0) {
            binaryDataArray[(b++)] = ((byte)(data[i] >>> shift & 0xFF));
            
            shift -= 8;
            xRemaining -= 8;
          }
          eltOffset += lineStride;
        }
      }
    }
    else if ((dataBuffer instanceof DataBufferByte)) {
      byte[] data = ((DataBufferByte)dataBuffer).getData();
      
      if ((bitOffset & 0x7) == 0) {
        int stride = numBytesPerRow;
        int offset = 0;
        for (int y = 0; y < rectHeight; y++) {
          System.arraycopy(data, eltOffset, binaryDataArray, offset, stride);
          

          offset += stride;
          eltOffset += lineStride;
        }
      } else {
        int leftShift = bitOffset & 0x7;
        int rightShift = 8 - leftShift;
        for (int y = 0; y < rectHeight; y++) {
          int i = eltOffset;
          int xRemaining = rectWidth;
          while (xRemaining > 0) {
            if (xRemaining > rightShift) {
              binaryDataArray[(b++)] = ((byte)((data[(i++)] & 0xFF) << leftShift | (data[i] & 0xFF) >>> rightShift));
            }
            else
            {
              binaryDataArray[(b++)] = ((byte)((data[i] & 0xFF) << leftShift));
            }
            
            xRemaining -= 8;
          }
          eltOffset += lineStride;
        }
      }
    } else if (((dataBuffer instanceof DataBufferShort)) || ((dataBuffer instanceof DataBufferUShort)))
    {
      short[] data = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
      


      for (int y = 0; y < rectHeight; y++) {
        int bOffset = bitOffset;
        for (int x = 0; x < rectWidth; bOffset += 8) {
          int i = eltOffset + bOffset / 16;
          int mod = bOffset % 16;
          int left = data[i] & 0xFFFF;
          if (mod <= 8) {
            binaryDataArray[(b++)] = ((byte)(left >>> 8 - mod));
          } else {
            int delta = mod - 8;
            int right = data[(i + 1)] & 0xFFFF;
            binaryDataArray[(b++)] = ((byte)(left << delta | right >>> 16 - delta));
          }
          x += 8;
        }
        











        eltOffset += lineStride;
      }
    } else if ((dataBuffer instanceof DataBufferInt)) {
      int[] data = ((DataBufferInt)dataBuffer).getData();
      
      for (int y = 0; y < rectHeight; y++) {
        int bOffset = bitOffset;
        for (int x = 0; x < rectWidth; bOffset += 8) {
          int i = eltOffset + bOffset / 32;
          int mod = bOffset % 32;
          int left = data[i];
          if (mod <= 24) {
            binaryDataArray[(b++)] = ((byte)(left >>> 24 - mod));
          }
          else {
            int delta = mod - 24;
            int right = data[(i + 1)];
            binaryDataArray[(b++)] = ((byte)(left << delta | right >>> 32 - delta));
          }
          x += 8;
        }
        












        eltOffset += lineStride;
      }
    }
    

    return binaryDataArray;
  }
  








  public static byte[] getUnpackedBinaryData(Raster raster, Rectangle rect)
  {
    SampleModel sm = raster.getSampleModel();
    if (!isBinary(sm)) {
      throw new IllegalArgumentException(JaiI18N.getString("ImageUtil0"));
    }
    
    int rectX = x;
    int rectY = y;
    int rectWidth = width;
    int rectHeight = height;
    
    DataBuffer dataBuffer = raster.getDataBuffer();
    
    int dx = rectX - raster.getSampleModelTranslateX();
    int dy = rectY - raster.getSampleModelTranslateY();
    
    MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
    int lineStride = mpp.getScanlineStride();
    int eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
    int bitOffset = mpp.getBitOffset(dx);
    
    byte[] bdata = new byte[rectWidth * rectHeight];
    int maxY = rectY + rectHeight;
    int maxX = rectX + rectWidth;
    int k = 0;
    
    if ((dataBuffer instanceof DataBufferByte)) {
      byte[] data = ((DataBufferByte)dataBuffer).getData();
      for (int y = rectY; y < maxY; y++) {
        int bOffset = eltOffset * 8 + bitOffset;
        for (int x = rectX; x < maxX; x++) {
          byte b = data[(bOffset / 8)];
          bdata[(k++)] = ((byte)(b >>> (7 - bOffset & 0x7) & 0x1));
          
          bOffset++;
        }
        eltOffset += lineStride;
      }
    } else if (((dataBuffer instanceof DataBufferShort)) || ((dataBuffer instanceof DataBufferUShort)))
    {
      short[] data = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
      

      for (int y = rectY; y < maxY; y++) {
        int bOffset = eltOffset * 16 + bitOffset;
        for (int x = rectX; x < maxX; x++) {
          short s = data[(bOffset / 16)];
          bdata[(k++)] = ((byte)(s >>> 15 - bOffset % 16 & 0x1));
          

          bOffset++;
        }
        eltOffset += lineStride;
      }
    } else if ((dataBuffer instanceof DataBufferInt)) {
      int[] data = ((DataBufferInt)dataBuffer).getData();
      for (int y = rectY; y < maxY; y++) {
        int bOffset = eltOffset * 32 + bitOffset;
        for (int x = rectX; x < maxX; x++) {
          int i = data[(bOffset / 32)];
          bdata[(k++)] = ((byte)(i >>> 31 - bOffset % 32 & 0x1));
          

          bOffset++;
        }
        eltOffset += lineStride;
      }
    }
    
    return bdata;
  }
  










  public static void setPackedBinaryData(byte[] binaryDataArray, WritableRaster raster, Rectangle rect)
  {
    SampleModel sm = raster.getSampleModel();
    if (!isBinary(sm)) {
      throw new IllegalArgumentException(JaiI18N.getString("ImageUtil0"));
    }
    
    int rectX = x;
    int rectY = y;
    int rectWidth = width;
    int rectHeight = height;
    
    DataBuffer dataBuffer = raster.getDataBuffer();
    
    int dx = rectX - raster.getSampleModelTranslateX();
    int dy = rectY - raster.getSampleModelTranslateY();
    
    MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
    int lineStride = mpp.getScanlineStride();
    int eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
    int bitOffset = mpp.getBitOffset(dx);
    
    int b = 0;
    
    if (bitOffset == 0) {
      if ((dataBuffer instanceof DataBufferByte)) {
        byte[] data = ((DataBufferByte)dataBuffer).getData();
        if (data == binaryDataArray)
        {
          return;
        }
        int stride = (rectWidth + 7) / 8;
        int offset = 0;
        for (int y = 0; y < rectHeight; y++) {
          System.arraycopy(binaryDataArray, offset, data, eltOffset, stride);
          

          offset += stride;
          eltOffset += lineStride;
        }
      } else if (((dataBuffer instanceof DataBufferShort)) || ((dataBuffer instanceof DataBufferUShort)))
      {
        short[] data = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
        


        for (int y = 0; y < rectHeight; y++) {
          int xRemaining = rectWidth;
          int i = eltOffset;
          while (xRemaining > 8) {
            data[(i++)] = ((short)((binaryDataArray[(b++)] & 0xFF) << 8 | binaryDataArray[(b++)] & 0xFF));
            

            xRemaining -= 16;
          }
          if (xRemaining > 0) {
            data[(i++)] = ((short)((binaryDataArray[(b++)] & 0xFF) << 8));
          }
          
          eltOffset += lineStride;
        }
      } else if ((dataBuffer instanceof DataBufferInt)) {
        int[] data = ((DataBufferInt)dataBuffer).getData();
        
        for (int y = 0; y < rectHeight; y++) {
          int xRemaining = rectWidth;
          int i = eltOffset;
          while (xRemaining > 24) {
            data[(i++)] = ((binaryDataArray[(b++)] & 0xFF) << 24 | (binaryDataArray[(b++)] & 0xFF) << 16 | (binaryDataArray[(b++)] & 0xFF) << 8 | binaryDataArray[(b++)] & 0xFF);
            



            xRemaining -= 32;
          }
          int shift = 24;
          while (xRemaining > 0) {
            data[i] |= (binaryDataArray[(b++)] & 0xFF) << shift;
            
            shift -= 8;
            xRemaining -= 8;
          }
          eltOffset += lineStride;
        }
      }
    } else {
      int stride = (rectWidth + 7) / 8;
      int offset = 0;
      if ((dataBuffer instanceof DataBufferByte)) {
        byte[] data = ((DataBufferByte)dataBuffer).getData();
        
        if ((bitOffset & 0x7) == 0) {
          for (int y = 0; y < rectHeight; y++) {
            System.arraycopy(binaryDataArray, offset, data, eltOffset, stride);
            

            offset += stride;
            eltOffset += lineStride;
          }
        } else {
          int rightShift = bitOffset & 0x7;
          int leftShift = 8 - rightShift;
          int leftShift8 = 8 + leftShift;
          int mask = (byte)(255 << leftShift);
          int mask1 = (byte)(mask ^ 0xFFFFFFFF);
          
          for (int y = 0; y < rectHeight; y++) {
            int i = eltOffset;
            int xRemaining = rectWidth;
            while (xRemaining > 0) {
              byte datum = binaryDataArray[(b++)];
              
              if (xRemaining > leftShift8)
              {

                data[i] = ((byte)(data[i] & mask | (datum & 0xFF) >>> rightShift));
                
                data[(++i)] = ((byte)((datum & 0xFF) << leftShift));
              } else if (xRemaining > leftShift)
              {


                data[i] = ((byte)(data[i] & mask | (datum & 0xFF) >>> rightShift));
                
                i++;
                data[i] = ((byte)(data[i] & mask1 | (datum & 0xFF) << leftShift));

              }
              else
              {
                int remainMask = (1 << leftShift - xRemaining) - 1;
                data[i] = ((byte)(data[i] & (mask | remainMask) | (datum & 0xFF) >>> rightShift & (remainMask ^ 0xFFFFFFFF)));
              }
              

              xRemaining -= 8;
            }
            eltOffset += lineStride;
          }
        }
      } else if (((dataBuffer instanceof DataBufferShort)) || ((dataBuffer instanceof DataBufferUShort)))
      {
        short[] data = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
        


        int rightShift = bitOffset & 0x7;
        int leftShift = 8 - rightShift;
        int leftShift16 = 16 + leftShift;
        int mask = (short)(255 << leftShift ^ 0xFFFFFFFF);
        int mask1 = (short)(65535 << leftShift);
        int mask2 = (short)(mask1 ^ 0xFFFFFFFF);
        
        for (int y = 0; y < rectHeight; y++) {
          int bOffset = bitOffset;
          int xRemaining = rectWidth;
          for (int x = 0; x < rectWidth; 
              xRemaining -= 8) {
            int i = eltOffset + (bOffset >> 4);
            int mod = bOffset & 0xF;
            int datum = binaryDataArray[(b++)] & 0xFF;
            if (mod <= 8)
            {
              if (xRemaining < 8)
              {
                datum &= 255 << 8 - xRemaining;
              }
              data[i] = ((short)(data[i] & mask | datum << leftShift));
            } else if (xRemaining > leftShift16)
            {
              data[i] = ((short)(data[i] & mask1 | datum >>> rightShift & 0xFFFF));
              data[(++i)] = ((short)(datum << leftShift & 0xFFFF));
            }
            else if (xRemaining > leftShift)
            {

              data[i] = ((short)(data[i] & mask1 | datum >>> rightShift & 0xFFFF));
              i++;
              data[i] = ((short)(data[i] & mask2 | datum << leftShift & 0xFFFF));

            }
            else
            {
              int remainMask = (1 << leftShift - xRemaining) - 1;
              data[i] = ((short)(data[i] & (mask1 | remainMask) | datum >>> rightShift & 0xFFFF & (remainMask ^ 0xFFFFFFFF)));
            }
            x += 8;bOffset += 8;
          }
          




























          eltOffset += lineStride;
        }
      } else if ((dataBuffer instanceof DataBufferInt)) {
        int[] data = ((DataBufferInt)dataBuffer).getData();
        int rightShift = bitOffset & 0x7;
        int leftShift = 8 - rightShift;
        int leftShift32 = 32 + leftShift;
        int mask = -1 << leftShift;
        int mask1 = mask ^ 0xFFFFFFFF;
        
        for (int y = 0; y < rectHeight; y++) {
          int bOffset = bitOffset;
          int xRemaining = rectWidth;
          for (int x = 0; x < rectWidth; 
              xRemaining -= 8) {
            int i = eltOffset + (bOffset >> 5);
            int mod = bOffset & 0x1F;
            int datum = binaryDataArray[(b++)] & 0xFF;
            if (mod <= 24)
            {
              int shift = 24 - mod;
              if (xRemaining < 8)
              {
                datum &= 255 << 8 - xRemaining;
              }
              data[i] = (data[i] & (255 << shift ^ 0xFFFFFFFF) | datum << shift);
            } else if (xRemaining > leftShift32)
            {
              data[i] = (data[i] & mask | datum >>> rightShift);
              data[(++i)] = (datum << leftShift);
            } else if (xRemaining > leftShift)
            {

              data[i] = (data[i] & mask | datum >>> rightShift);
              i++;
              data[i] = (data[i] & mask1 | datum << leftShift);
            }
            else {
              int remainMask = (1 << leftShift - xRemaining) - 1;
              data[i] = (data[i] & (mask | remainMask) | datum >>> rightShift & (remainMask ^ 0xFFFFFFFF));
            }
            x += 8;bOffset += 8;
          }
          


























          eltOffset += lineStride;
        }
      }
    }
  }
  













  public static void setUnpackedBinaryData(byte[] bdata, WritableRaster raster, Rectangle rect)
  {
    SampleModel sm = raster.getSampleModel();
    if (!isBinary(sm)) {
      throw new IllegalArgumentException(JaiI18N.getString("ImageUtil0"));
    }
    
    int rectX = x;
    int rectY = y;
    int rectWidth = width;
    int rectHeight = height;
    
    DataBuffer dataBuffer = raster.getDataBuffer();
    
    int dx = rectX - raster.getSampleModelTranslateX();
    int dy = rectY - raster.getSampleModelTranslateY();
    
    MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
    int lineStride = mpp.getScanlineStride();
    int eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
    int bitOffset = mpp.getBitOffset(dx);
    
    int k = 0;
    
    if ((dataBuffer instanceof DataBufferByte)) {
      byte[] data = ((DataBufferByte)dataBuffer).getData();
      for (int y = 0; y < rectHeight; y++) {
        int bOffset = eltOffset * 8 + bitOffset;
        for (int x = 0; x < rectWidth; x++) {
          if (bdata[(k++)] != 0) {
            int tmp180_179 = (bOffset / 8); byte[] tmp180_173 = data;tmp180_173[tmp180_179] = ((byte)(tmp180_173[tmp180_179] | (byte)(1 << (7 - bOffset & 0x7))));
          }
          
          bOffset++;
        }
        eltOffset += lineStride;
      }
    } else if (((dataBuffer instanceof DataBufferShort)) || ((dataBuffer instanceof DataBufferUShort)))
    {
      short[] data = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
      

      for (int y = 0; y < rectHeight; y++) {
        int bOffset = eltOffset * 16 + bitOffset;
        for (int x = 0; x < rectWidth; x++) {
          if (bdata[(k++)] != 0) {
            int tmp313_312 = (bOffset / 16); short[] tmp313_306 = data;tmp313_306[tmp313_312] = ((short)(tmp313_306[tmp313_312] | (short)(1 << 15 - bOffset % 16)));
          }
          

          bOffset++;
        }
        eltOffset += lineStride;
      }
    } else if ((dataBuffer instanceof DataBufferInt)) {
      int[] data = ((DataBufferInt)dataBuffer).getData();
      for (int y = 0; y < rectHeight; y++) {
        int bOffset = eltOffset * 32 + bitOffset;
        for (int x = 0; x < rectWidth; x++) {
          if (bdata[(k++)] != 0) {
            data[(bOffset / 32)] |= 1 << 31 - bOffset % 32;
          }
          

          bOffset++;
        }
        eltOffset += lineStride;
      }
    }
  }
  





  public static void fillBackground(WritableRaster raster, Rectangle rect, double[] backgroundValues)
  {
    rect = rect.intersection(raster.getBounds());
    int numBands = raster.getSampleModel().getNumBands();
    SampleModel sm = raster.getSampleModel();
    PixelAccessor accessor = new PixelAccessor(sm, null);
    
    if (isBinary(sm))
    {
      byte value = (byte)((int)backgroundValues[0] & 0x1);
      if (value == 0)
        return;
      int rectX = x;
      int rectY = y;
      int rectWidth = width;
      int rectHeight = height;
      
      int dx = rectX - raster.getSampleModelTranslateX();
      int dy = rectY - raster.getSampleModelTranslateY();
      
      DataBuffer dataBuffer = raster.getDataBuffer();
      MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
      int lineStride = mpp.getScanlineStride();
      int eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
      int bitOffset = mpp.getBitOffset(dx);
      
      switch (sm.getDataType())
      {
      case 0: 
        byte[] data = ((DataBufferByte)dataBuffer).getData();
        int bits = bitOffset & 0x7;
        int otherBits = bits == 0 ? 0 : 8 - bits;
        
        byte mask = (byte)(255 >> bits);
        int lineLength = (rectWidth - otherBits) / 8;
        int bits1 = rectWidth - otherBits & 0x7;
        byte mask1 = (byte)(255 << 8 - bits1);
        

        if (lineLength == 0) {
          mask = (byte)(mask & mask1);
          bits1 = 0;
        }
        
        for (int y = 0; y < rectHeight; y++) {
          int start = eltOffset;
          int end = start + lineLength;
          if (bits != 0) {
            int tmp303_300 = (start++); byte[] tmp303_296 = data;tmp303_296[tmp303_300] = ((byte)(tmp303_296[tmp303_300] | mask)); }
          while (start < end)
            data[(start++)] = -1;
          if (bits1 != 0) {
            int tmp338_336 = start; byte[] tmp338_334 = data;tmp338_334[tmp338_336] = ((byte)(tmp338_334[tmp338_336] | mask1)); }
          eltOffset += lineStride;
        }
        break;
      

      case 1: 
        short[] data = ((DataBufferUShort)dataBuffer).getData();
        int bits = bitOffset & 0xF;
        int otherBits = bits == 0 ? 0 : 16 - bits;
        
        short mask = (short)(65535 >> bits);
        int lineLength = (rectWidth - otherBits) / 16;
        int bits1 = rectWidth - otherBits & 0xF;
        short mask1 = (short)(65535 << 16 - bits1);
        

        if (lineLength == 0) {
          mask = (short)(mask & mask1);
          bits1 = 0;
        }
        
        for (int y = 0; y < rectHeight; y++) {
          int start = eltOffset;
          int end = start + lineLength;
          if (bits != 0) {
            int tmp482_479 = (start++); short[] tmp482_475 = data;tmp482_475[tmp482_479] = ((short)(tmp482_475[tmp482_479] | mask)); }
          while (start < end)
            data[(start++)] = -1;
          if (bits1 != 0) {
            int tmp520_517 = (start++); short[] tmp520_513 = data;tmp520_513[tmp520_517] = ((short)(tmp520_513[tmp520_517] | mask1)); }
          eltOffset += lineStride;
        }
        break;
      

      case 3: 
        int[] data = ((DataBufferInt)dataBuffer).getData();
        int bits = bitOffset & 0x1F;
        int otherBits = bits == 0 ? 0 : 32 - bits;
        
        int mask = -1 >> bits;
        int lineLength = (rectWidth - otherBits) / 32;
        int bits1 = rectWidth - otherBits & 0x1F;
        int mask1 = -1 << 32 - bits1;
        

        if (lineLength == 0) {
          mask &= mask1;
          bits1 = 0;
        }
        
        for (int y = 0; y < rectHeight; y++) {
          int start = eltOffset;
          int end = start + lineLength;
          if (bits != 0)
            data[(start++)] |= mask;
          while (start < end)
            data[(start++)] = -1;
          if (bits1 != 0)
            data[(start++)] |= mask1;
          eltOffset += lineStride;
        }
        break;
      }
    }
    else
    {
      int srcSampleType = sampleType == -1 ? 0 : sampleType;
      
      UnpackedImageData uid = accessor.getPixels(raster, rect, srcSampleType, false);
      
      rect = rect;
      int lineStride = lineStride;
      int pixelStride = pixelStride;
      
      switch (type) {
      case 0: 
        byte[][] bdata = uid.getByteData();
        for (int b = 0; b < numBands; b++) {
          byte value = (byte)(int)backgroundValues[b];
          byte[] bd = bdata[b];
          int lastLine = bandOffsets[b] + height * lineStride;
          
          for (int lo = bandOffsets[b]; lo < lastLine; lo += lineStride) {
            int lastPixel = lo + width * pixelStride;
            for (int po = lo; po < lastPixel; po += pixelStride) {
              bd[po] = value;
            }
          }
        }
        break;
      case 1: 
      case 2: 
        short[][] sdata = uid.getShortData();
        for (int b = 0; b < numBands; b++) {
          short value = (short)(int)backgroundValues[b];
          short[] sd = sdata[b];
          int lastLine = bandOffsets[b] + height * lineStride;
          
          for (int lo = bandOffsets[b]; lo < lastLine; lo += lineStride) {
            int lastPixel = lo + width * pixelStride;
            for (int po = lo; po < lastPixel; po += pixelStride) {
              sd[po] = value;
            }
          }
        }
        break;
      case 3: 
        int[][] idata = uid.getIntData();
        for (int b = 0; b < numBands; b++) {
          int value = (int)backgroundValues[b];
          int[] id = idata[b];
          int lastLine = bandOffsets[b] + height * lineStride;
          
          for (int lo = bandOffsets[b]; lo < lastLine; lo += lineStride) {
            int lastPixel = lo + width * pixelStride;
            for (int po = lo; po < lastPixel; po += pixelStride) {
              id[po] = value;
            }
          }
        }
        break;
      case 4: 
        float[][] fdata = uid.getFloatData();
        for (int b = 0; b < numBands; b++) {
          float value = (float)backgroundValues[b];
          float[] fd = fdata[b];
          int lastLine = bandOffsets[b] + height * lineStride;
          
          for (int lo = bandOffsets[b]; lo < lastLine; lo += lineStride) {
            int lastPixel = lo + width * pixelStride;
            for (int po = lo; po < lastPixel; po += pixelStride) {
              fd[po] = value;
            }
          }
        }
        break;
      case 5: 
        double[][] ddata = uid.getDoubleData();
        for (int b = 0; b < numBands; b++) {
          double value = backgroundValues[b];
          double[] dd = ddata[b];
          int lastLine = bandOffsets[b] + height * lineStride;
          
          for (int lo = bandOffsets[b]; lo < lastLine; lo += lineStride) {
            int lastPixel = lo + width * pixelStride;
            for (int po = lo; po < lastPixel; po += pixelStride) {
              dd[po] = value;
            }
          }
        }
      }
      
    }
  }
  





  public static void fillBordersWithBackgroundValues(Rectangle outerRect, Rectangle innerRect, WritableRaster raster, double[] backgroundValues)
  {
    int outerMaxX = x + width;
    int outerMaxY = y + height;
    
    int innerMaxX = x + width;
    int innerMaxY = y + height;
    
    if (x < x) {
      Rectangle rect = new Rectangle(x, y, x - x, outerMaxY - y);
      

      fillBackground(raster, rect, backgroundValues);
    }
    
    if (y < y) {
      Rectangle rect = new Rectangle(x, y, innerMaxX - x, y - y);
      

      fillBackground(raster, rect, backgroundValues);
    }
    
    if (outerMaxX > innerMaxX) {
      Rectangle rect = new Rectangle(innerMaxX, y, outerMaxX - innerMaxX, innerMaxY - y);
      

      fillBackground(raster, rect, backgroundValues);
    }
    
    if (outerMaxY > innerMaxY) {
      Rectangle rect = new Rectangle(x, innerMaxY, outerMaxX - x, outerMaxY - innerMaxY);
      

      fillBackground(raster, rect, backgroundValues);
    }
  }
  



























  public static KernelJAI getUnsharpMaskEquivalentKernel(KernelJAI kernel, float gain)
  {
    int width = kernel.getWidth();
    int height = kernel.getHeight();
    int xOrigin = kernel.getXOrigin();
    int yOrigin = kernel.getYOrigin();
    
    float[] oldData = kernel.getKernelData();
    float[] newData = new float[oldData.length];
    


    for (int k = 0; k < width * height; k++) {
      newData[k] = (-gain * oldData[k]);
    }
    k = yOrigin * width + xOrigin;
    newData[k] = (1.0F + gain * (1.0F - oldData[k]));
    
    return new KernelJAI(width, height, xOrigin, yOrigin, newData);
  }
  




  public static final Point[] getTileIndices(int txmin, int txmax, int tymin, int tymax)
  {
    if ((txmin > txmax) || (tymin > tymax)) {
      return null;
    }
    
    Point[] tileIndices = new Point[(txmax - txmin + 1) * (tymax - tymin + 1)];
    
    int k = 0;
    for (int tj = tymin; tj <= tymax; tj++) {
      for (int ti = txmin; ti <= txmax; ti++) {
        tileIndices[(k++)] = new Point(ti, tj);
      }
    }
    
    return tileIndices;
  }
  






  public static Vector evaluateParameters(Vector parameters)
  {
    if (parameters == null) {
      throw new IllegalArgumentException();
    }
    
    Vector paramEval = parameters;
    
    int size = parameters.size();
    for (int i = 0; i < size; i++) {
      Object element = parameters.get(i);
      if ((element instanceof DeferredData)) {
        if (paramEval == parameters) {
          paramEval = (Vector)parameters.clone();
        }
        paramEval.set(i, ((DeferredData)element).getData());
      }
    }
    
    return paramEval;
  }
  





  public static ParameterBlock evaluateParameters(ParameterBlock pb)
  {
    if (pb == null) {
      throw new IllegalArgumentException();
    }
    
    Vector parameters = pb.getParameters();
    Vector paramEval = evaluateParameters(parameters);
    return paramEval == parameters ? pb : new ParameterBlock(pb.getSources(), paramEval);
  }
  








  public static ColorModel getCompatibleColorModel(SampleModel sm, Map config)
  {
    ColorModel cm = null;
    
    if ((config == null) || (!Boolean.FALSE.equals(config.get(JAI.KEY_DEFAULT_COLOR_MODEL_ENABLED))))
    {




      if ((config != null) && (config.containsKey(JAI.KEY_DEFAULT_COLOR_MODEL_METHOD)))
      {

        Method cmMethod = (Method)config.get(JAI.KEY_DEFAULT_COLOR_MODEL_METHOD);
        


        Class[] paramTypes = cmMethod.getParameterTypes();
        if ((cmMethod.getModifiers() & 0x8) != 8)
        {

          throw new RuntimeException(JaiI18N.getString("ImageUtil1")); }
        if (cmMethod.getReturnType() != ColorModel.class)
        {
          throw new RuntimeException(JaiI18N.getString("ImageUtil2")); }
        if ((paramTypes.length != 1) || (!paramTypes[0].equals(SampleModel.class)))
        {

          throw new RuntimeException(JaiI18N.getString("ImageUtil3"));
        }
        

        try
        {
          Object[] args = { sm };
          cm = (ColorModel)cmMethod.invoke(null, args);
        } catch (Exception e) {
          String message = JaiI18N.getString("ImageUtil4") + cmMethod.getName();
          
          sendExceptionToListener(message, new ImagingException(message, e));

        }
        


      }
      else
      {

        cm = PlanarImage.createColorModel(sm);
      }
    }
    
    return cm;
  }
  



  public static String getStackTraceString(Exception e)
  {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(byteStream);
    e.printStackTrace(printStream);
    printStream.flush();
    String stackTraceString = byteStream.toString();
    printStream.close();
    return stackTraceString;
  }
  
  public static ImagingListener getImagingListener(RenderingHints hints) {
    ImagingListener listener = null;
    if (hints != null) {
      listener = (ImagingListener)hints.get(JAI.KEY_IMAGING_LISTENER);
    }
    if (listener == null)
      listener = JAI.getDefaultInstance().getImagingListener();
    return listener;
  }
  
  public static ImagingListener getImagingListener(RenderContext context) {
    return getImagingListener(context.getRenderingHints());
  }
  







  public static synchronized Object generateID(Object owner)
  {
    Class c = owner.getClass();
    counter += 1L;
    
    byte[] uid = new byte[32];
    int k = 0;
    int i = 7; for (int j = 0; i >= 0; j += 8) {
      uid[(k++)] = ((byte)(int)(counter >> j));i--; }
    int hash = c.hashCode();
    int i = 3; for (int j = 0; i >= 0; j += 8) {
      uid[(k++)] = ((byte)(hash >> j));i--; }
    hash = owner.hashCode();
    int i = 3; for (int j = 0; i >= 0; j += 8) {
      uid[(k++)] = ((byte)(hash >> j));i--; }
    long time = System.currentTimeMillis();
    int i = 7; for (int j = 0; i >= 0; j += 8) {
      uid[(k++)] = ((byte)(int)(time >> j));i--; }
    long rand = Double.doubleToLongBits(new Double(Math.random()).doubleValue());
    
    int i = 7; for (int j = 0; i >= 0; j += 8) {
      uid[(k++)] = ((byte)(int)(rand >> j));i--; }
    return new BigInteger(uid);
  }
  
  static void sendExceptionToListener(String message, Exception e) {
    ImagingListener listener = getImagingListener((RenderingHints)null);
    
    listener.errorOccurred(message, e, ImageUtil.class, false);
  }
}
