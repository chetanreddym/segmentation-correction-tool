package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;


















































































































































































































public class FilteredSubsampleOpImage
  extends GeometricOpImage
{
  protected int scaleX;
  protected int scaleY;
  protected int hParity;
  protected int vParity;
  protected float[] hKernel;
  protected float[] vKernel;
  
  private static float[] convolveFullKernels(float[] a, float[] b)
  {
    int lenA = a.length;
    int lenB = b.length;
    float[] c = new float[lenA + lenB - 1];
    
    for (int k = 0; k < c.length; k++) {
      for (int j = Math.max(0, k - lenB + 1); j <= Math.min(k, lenA - 1); j++)
        c[k] += a[j] * b[(k - j)];
    }
    return c;
  }
  






























  private static float[] convolveSymmetricKernels(int aParity, int bParity, float[] a, float[] b)
  {
    int lenA = a.length;
    int lenB = b.length;
    int lenTmpA = 2 * lenA - aParity;
    int lenTmpB = 2 * lenB - bParity;
    int lenTmpC = lenTmpA + lenTmpB - 1;
    float[] tmpA = new float[lenTmpA];
    float[] tmpB = new float[lenTmpB];
    
    float[] c = new float[(lenTmpC + 1) / 2];
    

    for (int k = 0; k < lenTmpA; k++) {
      tmpA[k] = a[Math.abs(k - lenA + (aParity - 1) * (k / lenA) + 1)];
    }
    
    for (int k = 0; k < lenTmpB; k++) {
      tmpB[k] = b[Math.abs(k - lenB + (bParity - 1) * (k / lenB) + 1)];
    }
    
    float[] tmpC = convolveFullKernels(tmpA, tmpB);
    

    int cParity = tmpC.length % 2;
    for (int k = 0; k < c.length; k++) {
      c[k] = tmpC[(lenTmpC - c.length - k - 1 + cParity)];
    }
    return c;
  }
  















  private static float[] combineFilters(int scaleFactor, int resampleType, float[] qsFilter)
  {
    if (scaleFactor % 2 == 1) { return (float[])qsFilter.clone();
    }
    int qsParity = 1;
    int resampParity = 0;
    
    switch (resampleType) {
    case 0: 
      return (float[])qsFilter.clone();
    case 1: 
      float[] bilinearKernel = { 0.5F };
      return convolveSymmetricKernels(qsParity, resampParity, qsFilter, bilinearKernel);
    
    case 2: 
      float[] bicubicKernel = { 0.5625F, -0.0625F };
      return convolveSymmetricKernels(qsParity, resampParity, qsFilter, bicubicKernel);
    
    case 3: 
      float[] bicubic2Kernel = { 0.625F, -0.125F };
      return convolveSymmetricKernels(qsParity, resampParity, qsFilter, bicubic2Kernel);
    }
    
    throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample0"));
  }
  















  private static int filterParity(int scaleFactor, int resampleType)
  {
    if ((scaleFactor % 2 == 1) || (resampleType == 0)) {
      return 1;
    }
    

    return 0;
  }
  


















  private static final ImageLayout layoutHelper(RenderedImage source, Interpolation interp, int scaleX, int scaleY, int filterSize, ImageLayout il)
  {
    if ((scaleX < 1) || (scaleY < 1)) {
      throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample1"));
    }
    
    if (filterSize < 1) {
      throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample2"));
    }
    


    Rectangle bounds = forwardMapRect(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight(), scaleX, scaleY);
    




    ImageLayout layout = il == null ? new ImageLayout(x, y, width, height) : (ImageLayout)il.clone();
    



    if (il != null) {
      layout.setWidth(width);
      layout.setHeight(height);
      layout.setMinX(x);
      layout.setMinY(y);
    }
    
    return layout;
  }
  

























  public FilteredSubsampleOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, int scaleX, int scaleY, float[] qsFilter, Interpolation interp)
  {
    super(vectorize(source), layoutHelper(source, interp, scaleX, scaleY, qsFilter.length, layout), config, true, extender, interp, null);
    


    int resampleType;
    


    int resampleType;
    

    if ((interp instanceof InterpolationNearest)) {
      resampleType = 0; } else { int resampleType;
      if ((interp instanceof InterpolationBilinear)) {
        resampleType = 1; } else { int resampleType;
        if ((interp instanceof InterpolationBicubic)) {
          resampleType = 2;
        } else if ((interp instanceof InterpolationBicubic2)) {
          resampleType = 3;
        } else {
          throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample3"));
        }
      }
    }
    
    hParity = filterParity(scaleX, resampleType);
    vParity = filterParity(scaleY, resampleType);
    hKernel = combineFilters(scaleX, resampleType, qsFilter);
    vKernel = combineFilters(scaleY, resampleType, qsFilter);
    
    this.scaleX = scaleX;
    this.scaleY = scaleY;
  }
  















  public Point2D mapDestPoint(Point2D destPt)
  {
    if (destPt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Point2D pt = (Point2D)destPt.clone();
    
    pt.setLocation(destPt.getX() * scaleX, destPt.getY() * scaleY);
    
    return pt;
  }
  













  public Point2D mapSourcePoint(Point2D sourcePt)
  {
    if (sourcePt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Point2D pt = (Point2D)sourcePt.clone();
    
    pt.setLocation(sourcePt.getX() / scaleX, sourcePt.getY() / scaleY);
    
    return pt;
  }
  

















  public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex)
  {
    if (sourceIndex != 0) {
      throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample4"));
    }
    
    int xOffset = x + hKernel.length - hParity - scaleX / 2;
    int yOffset = y + vKernel.length - vParity - scaleY / 2;
    int rectWidth = width - 2 * hKernel.length + hParity + 1;
    int rectHeight = height - 2 * vKernel.length + vParity + 1;
    return forwardMapRect(xOffset, yOffset, rectWidth, rectHeight, scaleX, scaleY);
  }
  












  private static final Rectangle forwardMapRect(int x, int y, int w, int h, int scaleX, int scaleY)
  {
    float sx = 1.0F / scaleX;
    float sy = 1.0F / scaleY;
    
    x = Math.round(x * sx);
    y = Math.round(y * sy);
    
    return new Rectangle(x, y, Math.round((x + w) * sx) - x, Math.round((y + h) * sy) - y);
  }
  









  protected final Rectangle forwardMapRect(Rectangle srcRect, int srcIndex)
  {
    int x = x;
    int y = y;
    int w = width;
    int h = height;
    float sx = 1.0F / scaleX;
    float sy = 1.0F / scaleY;
    
    x = Math.round(x * sx);
    y = Math.round(y * sy);
    
    return new Rectangle(x, y, Math.round((x + w) * sx) - x, Math.round((y + h) * sy) - y);
  }
  








  protected final Rectangle backwardMapRect(Rectangle destRect, int srcIncex)
  {
    int x = x;
    int y = y;
    int w = width;
    int h = height;
    
    return new Rectangle(x * scaleX, y * scaleY, (x + w) * scaleX - x, (y + h) * scaleY - y);
  }
  


















  public Rectangle mapDestRect(Rectangle destRect, int sourceIndex)
  {
    if (sourceIndex != 0) {
      throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample4"));
    }
    
    int xOffset = x * scaleX - hKernel.length + hParity + scaleX / 2;
    int yOffset = y * scaleY - vKernel.length + vParity + scaleY / 2;
    int rectWidth = width * scaleX + 2 * hKernel.length - hParity - 1;
    int rectHeight = height * scaleY + 2 * vKernel.length - vParity - 1;
    return new Rectangle(xOffset, yOffset, rectWidth, rectHeight);
  }
  













  public void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    

    RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    



    RasterAccessor src = new RasterAccessor(sources[0], mapDestRect(destRect, 0), formatTags[0], getSourceImage(0).getColorModel());
    



    switch (dst.getDataType()) {
    case 0: 
      computeRectByte(src, dst);
      break;
    case 1: 
      computeRectUShort(src, dst);
      break;
    case 2: 
      computeRectShort(src, dst);
      break;
    case 3: 
      computeRectInt(src, dst);
      break;
    case 4: 
      computeRectFloat(src, dst);
      break;
    case 5: 
      computeRectDouble(src, dst);
      break;
    default: 
      throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample5"));
    }
    
    


    if (dst.isDataCopy()) {
      dst.clampDataArrays();
      dst.copyDataToRaster();
    }
  }
  







  protected void computeRectByte(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    

    byte[][] dstDataArrays = dst.getByteDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    

    byte[][] srcDataArrays = src.getByteDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    

    int kernelNx = 2 * hKernel.length - hParity;
    int kernelNy = 2 * vKernel.length - vParity;
    int stepDown = (kernelNy - 1) * srcScanlineStride;
    int stepRight = (kernelNx - 1) * srcPixelStride;
    


    float vCtr = vKernel[0];
    float hCtr = hKernel[0];
    

    for (int band = 0; band < dnumBands; band++) {
      byte[] dstData = dstDataArrays[band];
      byte[] srcData = srcDataArrays[band];
      int srcScanlineOffset = srcBandOffsets[band];
      int dstScanlineOffset = dstBandOffsets[band];
      

      for (int ySrc = 0; ySrc < scaleY * dheight; ySrc += scaleY) {
        int dInd = dstScanlineOffset;
        for (int xSrc = 0; xSrc < scaleX * dwidth; xSrc += scaleX)
        {
          int upLeft0 = xSrc * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
          int upRight0 = upLeft0 + stepRight;
          int dnLeft0 = upLeft0 + stepDown;
          int dnRight0 = upRight0 + stepDown;
          

          float sum = 0.0F;
          

          for (int iy = vKernel.length - 1; iy > vParity - 1; iy--) {
            int upLeft = upLeft0;
            int upRight = upRight0;
            int dnLeft = dnLeft0;
            int dnRight = dnRight0;
            

            for (int ix = hKernel.length - 1; ix > hParity - 1; ix--) {
              float kk = hKernel[ix] * vKernel[iy];
              sum += kk * ((srcData[upLeft] & 0xFF) + (srcData[upRight] & 0xFF) + (srcData[dnLeft] & 0xFF) + (srcData[dnRight] & 0xFF));
              
              upLeft += srcPixelStride;
              upRight -= srcPixelStride;
              dnLeft += srcPixelStride;
              dnRight -= srcPixelStride;
            }
            upLeft0 += srcScanlineStride;
            upRight0 += srcScanlineStride;
            dnLeft0 -= srcScanlineStride;
            dnRight0 -= srcScanlineStride;
          }
          



          if (hParity == 1) {
            int xUp = (xSrc + hKernel.length - 1) * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
            
            int xDown = xUp + stepDown;
            int kInd = vKernel.length - 1;
            while (xUp < xDown) {
              float kk = hCtr * vKernel[(kInd--)];
              sum += kk * ((srcData[xUp] & 0xFF) + (srcData[xDown] & 0xFF));
              xUp += srcScanlineStride;
              xDown -= srcScanlineStride;
            }
          }
          

          if (vParity == 1) {
            int xLeft = xSrc * srcPixelStride + (ySrc + vKernel.length - 1) * srcScanlineStride + srcScanlineOffset;
            

            int xRight = xLeft + stepRight;
            int kInd = hKernel.length - 1;
            while (xLeft < xRight) {
              float kk = vCtr * hKernel[(kInd--)];
              sum += kk * ((srcData[xLeft] & 0xFF) + (srcData[xRight] & 0xFF));
              xLeft += srcPixelStride;
              xRight -= srcPixelStride;
            }
            

            if (hParity == 1) { sum += vCtr * hCtr * (srcData[xLeft] & 0xFF);
            }
          }
          

          if (sum < 0.0D) sum = 0.0F;
          if (sum > 255.0D) { sum = 255.0F;
          }
          dstData[dInd] = ((byte)(int)(sum + 0.5D));
          
          dInd += dstPixelStride;
        }
        
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  











  protected void computeRectUShort(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    

    short[][] dstDataArrays = dst.getShortDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    

    short[][] srcDataArrays = src.getShortDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    

    int kernelNx = 2 * hKernel.length - hParity;
    int kernelNy = 2 * vKernel.length - vParity;
    int stepDown = (kernelNy - 1) * srcScanlineStride;
    int stepRight = (kernelNx - 1) * srcPixelStride;
    


    float vCtr = vKernel[0];
    float hCtr = hKernel[0];
    

    for (int band = 0; band < dnumBands; band++) {
      short[] dstData = dstDataArrays[band];
      short[] srcData = srcDataArrays[band];
      int srcScanlineOffset = srcBandOffsets[band];
      int dstScanlineOffset = dstBandOffsets[band];
      

      for (int ySrc = 0; ySrc < scaleY * dheight; ySrc += scaleY) {
        int dInd = dstScanlineOffset;
        for (int xSrc = 0; xSrc < scaleX * dwidth; xSrc += scaleX)
        {
          int upLeft0 = xSrc * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
          int upRight0 = upLeft0 + stepRight;
          int dnLeft0 = upLeft0 + stepDown;
          int dnRight0 = upRight0 + stepDown;
          

          float sum = 0.0F;
          

          for (int iy = vKernel.length - 1; iy > vParity - 1; iy--) {
            int upLeft = upLeft0;
            int upRight = upRight0;
            int dnLeft = dnLeft0;
            int dnRight = dnRight0;
            

            for (int ix = hKernel.length - 1; ix > hParity - 1; ix--) {
              float kk = hKernel[ix] * vKernel[iy];
              sum += kk * ((srcData[upLeft] & 0xFFFF) + (srcData[upRight] & 0xFFFF) + (srcData[dnLeft] & 0xFFFF) + (srcData[dnRight] & 0xFFFF));
              


              upLeft += srcPixelStride;
              upRight -= srcPixelStride;
              dnLeft += srcPixelStride;
              dnRight -= srcPixelStride;
            }
            upLeft0 += srcScanlineStride;
            upRight0 += srcScanlineStride;
            dnLeft0 -= srcScanlineStride;
            dnRight0 -= srcScanlineStride;
          }
          




          if (hParity == 1) {
            int xUp = (xSrc + hKernel.length - 1) * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
            
            int xDown = xUp + stepDown;
            int kInd = vKernel.length - 1;
            while (xUp < xDown) {
              float kk = hCtr * vKernel[(kInd--)];
              sum += kk * ((srcData[xUp] & 0xFFFF) + (srcData[xDown] & 0xFFFF));
              
              xUp += srcScanlineStride;
              xDown -= srcScanlineStride;
            }
          }
          

          if (vParity == 1) {
            int xLeft = xSrc * srcPixelStride + (ySrc + vKernel.length - 1) * srcScanlineStride + srcScanlineOffset;
            

            int xRight = xLeft + stepRight;
            int kInd = hKernel.length - 1;
            while (xLeft < xRight) {
              float kk = vCtr * hKernel[(kInd--)];
              sum += kk * ((srcData[xLeft] & 0xFFFF) + (srcData[xRight] & 0xFFFF));
              
              xLeft += srcPixelStride;
              xRight -= srcPixelStride;
            }
            

            if (hParity == 1) { sum += vCtr * hCtr * (srcData[xLeft] & 0xFFFF);
            }
          }
          int val = (int)(sum + 0.5D);
          dstData[dInd] = ((short)(val < 0 ? 0 : val > 65535 ? 65535 : val));
          
          dInd += dstPixelStride;
        }
        
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  











  protected void computeRectShort(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    

    short[][] dstDataArrays = dst.getShortDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    

    short[][] srcDataArrays = src.getShortDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    

    int kernelNx = 2 * hKernel.length - hParity;
    int kernelNy = 2 * vKernel.length - vParity;
    int stepDown = (kernelNy - 1) * srcScanlineStride;
    int stepRight = (kernelNx - 1) * srcPixelStride;
    


    float vCtr = vKernel[0];
    float hCtr = hKernel[0];
    

    for (int band = 0; band < dnumBands; band++) {
      short[] dstData = dstDataArrays[band];
      short[] srcData = srcDataArrays[band];
      int srcScanlineOffset = srcBandOffsets[band];
      int dstScanlineOffset = dstBandOffsets[band];
      

      for (int ySrc = 0; ySrc < scaleY * dheight; ySrc += scaleY) {
        int dInd = dstScanlineOffset;
        for (int xSrc = 0; xSrc < scaleX * dwidth; xSrc += scaleX)
        {
          int upLeft0 = xSrc * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
          int upRight0 = upLeft0 + stepRight;
          int dnLeft0 = upLeft0 + stepDown;
          int dnRight0 = upRight0 + stepDown;
          

          float sum = 0.0F;
          

          for (int iy = vKernel.length - 1; iy > vParity - 1; iy--) {
            int upLeft = upLeft0;
            int upRight = upRight0;
            int dnLeft = dnLeft0;
            int dnRight = dnRight0;
            

            for (int ix = hKernel.length - 1; ix > hParity - 1; ix--) {
              float kk = hKernel[ix] * vKernel[iy];
              sum += kk * (srcData[upLeft] + srcData[upRight] + srcData[dnLeft] + srcData[dnRight]);
              


              upLeft += srcPixelStride;
              upRight -= srcPixelStride;
              dnLeft += srcPixelStride;
              dnRight -= srcPixelStride;
            }
            upLeft0 += srcScanlineStride;
            upRight0 += srcScanlineStride;
            dnLeft0 -= srcScanlineStride;
            dnRight0 -= srcScanlineStride;
          }
          




          if (hParity == 1) {
            int xUp = (xSrc + hKernel.length - 1) * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
            
            int xDown = xUp + stepDown;
            int kInd = vKernel.length - 1;
            while (xUp < xDown) {
              float kk = hCtr * vKernel[(kInd--)];
              sum += kk * (srcData[xUp] + srcData[xDown]);
              
              xUp += srcScanlineStride;
              xDown -= srcScanlineStride;
            }
          }
          

          if (vParity == 1) {
            int xLeft = xSrc * srcPixelStride + (ySrc + vKernel.length - 1) * srcScanlineStride + srcScanlineOffset;
            

            int xRight = xLeft + stepRight;
            int kInd = hKernel.length - 1;
            while (xLeft < xRight) {
              float kk = vCtr * hKernel[(kInd--)];
              sum += kk * (srcData[xLeft] + srcData[xRight]);
              
              xLeft += srcPixelStride;
              xRight -= srcPixelStride;
            }
            

            if (hParity == 1) { sum += vCtr * hCtr * srcData[xLeft];
            }
          }
          
          dstData[dInd] = ImageUtil.clampShort((int)(sum + 0.5D));
          dInd += dstPixelStride;
        }
        

        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  











  protected void computeRectInt(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    

    int[][] dstDataArrays = dst.getIntDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    

    int[][] srcDataArrays = src.getIntDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    

    int kernelNx = 2 * hKernel.length - hParity;
    int kernelNy = 2 * vKernel.length - vParity;
    int stepDown = (kernelNy - 1) * srcScanlineStride;
    int stepRight = (kernelNx - 1) * srcPixelStride;
    


    double vCtr = vKernel[0];
    double hCtr = hKernel[0];
    

    for (int band = 0; band < dnumBands; band++) {
      int[] dstData = dstDataArrays[band];
      int[] srcData = srcDataArrays[band];
      int srcScanlineOffset = srcBandOffsets[band];
      int dstScanlineOffset = dstBandOffsets[band];
      

      for (int ySrc = 0; ySrc < scaleY * dheight; ySrc += scaleY) {
        int dInd = dstScanlineOffset;
        for (int xSrc = 0; xSrc < scaleX * dwidth; xSrc += scaleX)
        {
          int upLeft0 = xSrc * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
          int upRight0 = upLeft0 + stepRight;
          int dnLeft0 = upLeft0 + stepDown;
          int dnRight0 = upRight0 + stepDown;
          

          double sum = 0.0D;
          

          for (int iy = vKernel.length - 1; iy > vParity - 1; iy--) {
            int upLeft = upLeft0;
            int upRight = upRight0;
            int dnLeft = dnLeft0;
            int dnRight = dnRight0;
            

            for (int ix = hKernel.length - 1; ix > hParity - 1; ix--) {
              double kk = hKernel[ix] * vKernel[iy];
              sum += kk * (srcData[upLeft] + srcData[upRight] + srcData[dnLeft] + srcData[dnRight]);
              
              upLeft += srcPixelStride;
              upRight -= srcPixelStride;
              dnLeft += srcPixelStride;
              dnRight -= srcPixelStride;
            }
            upLeft0 += srcScanlineStride;
            upRight0 += srcScanlineStride;
            dnLeft0 -= srcScanlineStride;
            dnRight0 -= srcScanlineStride;
          }
          




          if (hParity == 1) {
            int xUp = (xSrc + hKernel.length - 1) * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
            
            int xDown = xUp + stepDown;
            int kInd = vKernel.length - 1;
            while (xUp < xDown) {
              double kk = hCtr * vKernel[(kInd--)];
              sum += kk * (srcData[xUp] + srcData[xDown]);
              xUp += srcScanlineStride;
              xDown -= srcScanlineStride;
            }
          }
          

          if (vParity == 1) {
            int xLeft = xSrc * srcPixelStride + (ySrc + vKernel.length - 1) * srcScanlineStride + srcScanlineOffset;
            

            int xRight = xLeft + stepRight;
            int kInd = hKernel.length - 1;
            while (xLeft < xRight) {
              double kk = vCtr * hKernel[(kInd--)];
              sum += kk * (srcData[xLeft] + srcData[xRight]);
              xLeft += srcPixelStride;
              xRight -= srcPixelStride;
            }
            

            if (hParity == 1) { sum += vCtr * hCtr * srcData[xLeft];
            }
          }
          
          dstData[dInd] = ImageUtil.clampInt((int)(sum + 0.5D));
          
          dInd += dstPixelStride;
        }
        
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  











  protected void computeRectFloat(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    

    float[][] dstDataArrays = dst.getFloatDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    

    float[][] srcDataArrays = src.getFloatDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    

    int kernelNx = 2 * hKernel.length - hParity;
    int kernelNy = 2 * vKernel.length - vParity;
    int stepDown = (kernelNy - 1) * srcScanlineStride;
    int stepRight = (kernelNx - 1) * srcPixelStride;
    


    double vCtr = vKernel[0];
    double hCtr = hKernel[0];
    

    for (int band = 0; band < dnumBands; band++) {
      float[] dstData = dstDataArrays[band];
      float[] srcData = srcDataArrays[band];
      int srcScanlineOffset = srcBandOffsets[band];
      int dstScanlineOffset = dstBandOffsets[band];
      

      for (int ySrc = 0; ySrc < scaleY * dheight; ySrc += scaleY) {
        int dInd = dstScanlineOffset;
        for (int xSrc = 0; xSrc < scaleX * dwidth; xSrc += scaleX)
        {
          int upLeft0 = xSrc * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
          int upRight0 = upLeft0 + stepRight;
          int dnLeft0 = upLeft0 + stepDown;
          int dnRight0 = upRight0 + stepDown;
          

          double sum = 0.0D;
          

          for (int iy = vKernel.length - 1; iy > vParity - 1; iy--) {
            int upLeft = upLeft0;
            int upRight = upRight0;
            int dnLeft = dnLeft0;
            int dnRight = dnRight0;
            

            for (int ix = hKernel.length - 1; ix > hParity - 1; ix--) {
              double kk = hKernel[ix] * vKernel[iy];
              sum += kk * (srcData[upLeft] + srcData[upRight] + srcData[dnLeft] + srcData[dnRight]);
              
              upLeft += srcPixelStride;
              upRight -= srcPixelStride;
              dnLeft += srcPixelStride;
              dnRight -= srcPixelStride;
            }
            upLeft0 += srcScanlineStride;
            upRight0 += srcScanlineStride;
            dnLeft0 -= srcScanlineStride;
            dnRight0 -= srcScanlineStride;
          }
          




          if (hParity == 1) {
            int xUp = (xSrc + hKernel.length - 1) * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
            
            int xDown = xUp + stepDown;
            int kInd = vKernel.length - 1;
            while (xUp < xDown) {
              double kk = hCtr * vKernel[(kInd--)];
              sum += kk * (srcData[xUp] + srcData[xDown]);
              xUp += srcScanlineStride;
              xDown -= srcScanlineStride;
            }
          }
          

          if (vParity == 1) {
            int xLeft = xSrc * srcPixelStride + (ySrc + vKernel.length - 1) * srcScanlineStride + srcScanlineOffset;
            

            int xRight = xLeft + stepRight;
            int kInd = hKernel.length - 1;
            while (xLeft < xRight) {
              double kk = vCtr * hKernel[(kInd--)];
              sum += kk * (srcData[xLeft] + srcData[xRight]);
              xLeft += srcPixelStride;
              xRight -= srcPixelStride;
            }
            

            if (hParity == 1) { sum += vCtr * hCtr * srcData[xLeft];
            }
          }
          
          dstData[dInd] = ImageUtil.clampFloat(sum);
          
          dInd += dstPixelStride;
        }
        
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  











  protected void computeRectDouble(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    

    double[][] dstDataArrays = dst.getDoubleDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    

    double[][] srcDataArrays = src.getDoubleDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    

    int kernelNx = 2 * hKernel.length - hParity;
    int kernelNy = 2 * vKernel.length - vParity;
    int stepDown = (kernelNy - 1) * srcScanlineStride;
    int stepRight = (kernelNx - 1) * srcPixelStride;
    


    double vCtr = vKernel[0];
    double hCtr = hKernel[0];
    

    for (int band = 0; band < dnumBands; band++) {
      double[] dstData = dstDataArrays[band];
      double[] srcData = srcDataArrays[band];
      int srcScanlineOffset = srcBandOffsets[band];
      int dstScanlineOffset = dstBandOffsets[band];
      

      for (int ySrc = 0; ySrc < scaleY * dheight; ySrc += scaleY) {
        int dInd = dstScanlineOffset;
        for (int xSrc = 0; xSrc < scaleX * dwidth; xSrc += scaleX)
        {
          int upLeft0 = xSrc * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
          int upRight0 = upLeft0 + stepRight;
          int dnLeft0 = upLeft0 + stepDown;
          int dnRight0 = upRight0 + stepDown;
          

          double sum = 0.0D;
          

          for (int iy = vKernel.length - 1; iy > vParity - 1; iy--) {
            int upLeft = upLeft0;
            int upRight = upRight0;
            int dnLeft = dnLeft0;
            int dnRight = dnRight0;
            

            for (int ix = hKernel.length - 1; ix > hParity - 1; ix--) {
              double kk = hKernel[ix] * vKernel[iy];
              sum += kk * (srcData[upLeft] + srcData[upRight] + srcData[dnLeft] + srcData[dnRight]);
              
              upLeft += srcPixelStride;
              upRight -= srcPixelStride;
              dnLeft += srcPixelStride;
              dnRight -= srcPixelStride;
            }
            upLeft0 += srcScanlineStride;
            upRight0 += srcScanlineStride;
            dnLeft0 -= srcScanlineStride;
            dnRight0 -= srcScanlineStride;
          }
          




          if (hParity == 1) {
            int xUp = (xSrc + hKernel.length - 1) * srcPixelStride + ySrc * srcScanlineStride + srcScanlineOffset;
            
            int xDown = xUp + stepDown;
            int kInd = vKernel.length - 1;
            while (xUp < xDown) {
              double kk = hCtr * vKernel[(kInd--)];
              sum += kk * (srcData[xUp] + srcData[xDown]);
              xUp += srcScanlineStride;
              xDown -= srcScanlineStride;
            }
          }
          

          if (vParity == 1) {
            int xLeft = xSrc * srcPixelStride + (ySrc + vKernel.length - 1) * srcScanlineStride + srcScanlineOffset;
            

            int xRight = xLeft + stepRight;
            int kInd = hKernel.length - 1;
            while (xLeft < xRight) {
              double kk = vCtr * hKernel[(kInd--)];
              sum += kk * (srcData[xLeft] + srcData[xRight]);
              xLeft += srcPixelStride;
              xRight -= srcPixelStride;
            }
            

            if (hParity == 1) { sum += vCtr * hCtr * srcData[xLeft];
            }
          }
          
          dstData[dInd] = sum;
          
          dInd += dstPixelStride;
        }
        
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
}
