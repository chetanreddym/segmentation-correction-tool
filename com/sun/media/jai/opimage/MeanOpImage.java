package com.sun.media.jai.opimage;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.StatisticsOpImage;
import javax.media.jai.UnpackedImageData;
























public class MeanOpImage
  extends StatisticsOpImage
{
  private boolean isInitialized = false;
  

  private double[] totalPixelValue;
  

  private int totalPixelCount;
  
  private PixelAccessor srcPA;
  
  private int srcSampleType;
  

  private final boolean tileIntersectsROI(int tileX, int tileY)
  {
    if (roi == null) {
      return true;
    }
    return roi.intersects(tileXToX(tileX), tileYToY(tileY), tileWidth, tileHeight);
  }
  











  public MeanOpImage(RenderedImage source, ROI roi, int xStart, int yStart, int xPeriod, int yPeriod)
  {
    super(source, roi, xStart, yStart, xPeriod, yPeriod);
  }
  
  protected String[] getStatisticsNames() {
    return new String[] { "mean" };
  }
  
  protected Object createStatistics(String name) {
    Object stats;
    Object stats;
    if (name.equalsIgnoreCase("mean")) {
      stats = new double[sampleModel.getNumBands()];
    } else {
      stats = Image.UndefinedProperty;
    }
    return stats;
  }
  
  private final int startPosition(int pos, int start, int period) {
    int t = (pos - start) % period;
    if (t == 0) {
      return pos;
    }
    return pos + (period - t);
  }
  


  protected void accumulateStatistics(String name, Raster source, Object stats)
  {
    if (!isInitialized) {
      srcPA = new PixelAccessor(getSourceImage(0));
      srcSampleType = (srcPA.sampleType == -1 ? 0 : srcPA.sampleType);
      

      totalPixelValue = new double[srcPA.numBands];
      totalPixelCount = 0;
      isInitialized = true;
    }
    
    Rectangle srcBounds = getSourceImage(0).getBounds().intersection(source.getBounds());
    
    LinkedList rectList;
    
    if (roi == null) {
      LinkedList rectList = new LinkedList();
      rectList.addLast(srcBounds);
    } else {
      rectList = roi.getAsRectangleList(x, y, width, height);
      


      if (rectList == null) {
        return;
      }
    }
    ListIterator iterator = rectList.listIterator(0);
    
    while (iterator.hasNext()) {
      Rectangle rect = srcBounds.intersection((Rectangle)iterator.next());
      int tx = x;
      int ty = y;
      

      x = startPosition(tx, xStart, xPeriod);
      y = startPosition(ty, yStart, yPeriod);
      width = (tx + width - x);
      height = (ty + height - y);
      
      if (!rect.isEmpty())
      {


        UnpackedImageData uid = srcPA.getPixels(source, rect, srcSampleType, false);
        

        switch (type) {
        case 0: 
          accumulateStatisticsByte(uid);
          break;
        case 1: 
          accumulateStatisticsUShort(uid);
          break;
        case 2: 
          accumulateStatisticsShort(uid);
          break;
        case 3: 
          accumulateStatisticsInt(uid);
          break;
        case 4: 
          accumulateStatisticsFloat(uid);
          break;
        case 5: 
          accumulateStatisticsDouble(uid);
        }
        
      }
    }
    if (name.equalsIgnoreCase("mean"))
    {

      double[] mean = (double[])stats;
      if (totalPixelCount != 0) {
        for (int i = 0; i < srcPA.numBands; i++) {
          mean[i] = (totalPixelValue[i] / totalPixelCount);
        }
      }
    }
  }
  
  private void accumulateStatisticsByte(UnpackedImageData uid)
  {
    Rectangle rect = rect;
    byte[][] data = uid.getByteData();
    int lineStride = lineStride;
    int pixelStride = pixelStride;
    
    int lineInc = lineStride * yPeriod;
    int pixelInc = pixelStride * xPeriod;
    
    for (int b = 0; b < srcPA.numBands; b++) {
      byte[] d = data[b];
      int lastLine = bandOffsets[b] + height * lineStride;
      
      for (int lo = bandOffsets[b]; lo < lastLine; lo += lineInc) {
        int lastPixel = lo + width * pixelStride;
        
        for (int po = lo; po < lastPixel; po += pixelInc) {
          totalPixelValue[b] += (d[po] & 0xFF);
        }
      }
    }
    totalPixelCount += (int)Math.ceil(height / yPeriod) * (int)Math.ceil(width / xPeriod);
  }
  
  private void accumulateStatisticsUShort(UnpackedImageData uid)
  {
    Rectangle rect = rect;
    short[][] data = uid.getShortData();
    int lineStride = lineStride;
    int pixelStride = pixelStride;
    
    int lineInc = lineStride * yPeriod;
    int pixelInc = pixelStride * xPeriod;
    
    for (int b = 0; b < srcPA.numBands; b++) {
      short[] d = data[b];
      int lastLine = bandOffsets[b] + height * lineStride;
      
      for (int lo = bandOffsets[b]; lo < lastLine; lo += lineInc) {
        int lastPixel = lo + width * pixelStride;
        
        for (int po = lo; po < lastPixel; po += pixelInc) {
          totalPixelValue[b] += (d[po] & 0xFFFF);
        }
      }
    }
    totalPixelCount += (int)Math.ceil(height / yPeriod) * (int)Math.ceil(width / xPeriod);
  }
  
  private void accumulateStatisticsShort(UnpackedImageData uid)
  {
    Rectangle rect = rect;
    short[][] data = uid.getShortData();
    int lineStride = lineStride;
    int pixelStride = pixelStride;
    
    int lineInc = lineStride * yPeriod;
    int pixelInc = pixelStride * xPeriod;
    
    for (int b = 0; b < srcPA.numBands; b++) {
      short[] d = data[b];
      int lastLine = bandOffsets[b] + height * lineStride;
      
      for (int lo = bandOffsets[b]; lo < lastLine; lo += lineInc) {
        int lastPixel = lo + width * pixelStride;
        
        for (int po = lo; po < lastPixel; po += pixelInc) {
          totalPixelValue[b] += d[po];
        }
      }
    }
    totalPixelCount += (int)Math.ceil(height / yPeriod) * (int)Math.ceil(width / xPeriod);
  }
  
  private void accumulateStatisticsInt(UnpackedImageData uid)
  {
    Rectangle rect = rect;
    int[][] data = uid.getIntData();
    int lineStride = lineStride;
    int pixelStride = pixelStride;
    
    int lineInc = lineStride * yPeriod;
    int pixelInc = pixelStride * xPeriod;
    
    for (int b = 0; b < srcPA.numBands; b++) {
      int[] d = data[b];
      int lastLine = bandOffsets[b] + height * lineStride;
      
      for (int lo = bandOffsets[b]; lo < lastLine; lo += lineInc) {
        int lastPixel = lo + width * pixelStride;
        
        for (int po = lo; po < lastPixel; po += pixelInc) {
          totalPixelValue[b] += d[po];
        }
      }
    }
    totalPixelCount += (int)Math.ceil(height / yPeriod) * (int)Math.ceil(width / xPeriod);
  }
  
  private void accumulateStatisticsFloat(UnpackedImageData uid)
  {
    Rectangle rect = rect;
    float[][] data = uid.getFloatData();
    int lineStride = lineStride;
    int pixelStride = pixelStride;
    
    int lineInc = lineStride * yPeriod;
    int pixelInc = pixelStride * xPeriod;
    
    for (int b = 0; b < srcPA.numBands; b++) {
      float[] d = data[b];
      int lastLine = bandOffsets[b] + height * lineStride;
      
      for (int lo = bandOffsets[b]; lo < lastLine; lo += lineInc) {
        int lastPixel = lo + width * pixelStride;
        
        for (int po = lo; po < lastPixel; po += pixelInc) {
          totalPixelValue[b] += d[po];
        }
      }
    }
    totalPixelCount += (int)Math.ceil(height / yPeriod) * (int)Math.ceil(width / xPeriod);
  }
  
  private void accumulateStatisticsDouble(UnpackedImageData uid)
  {
    Rectangle rect = rect;
    double[][] data = uid.getDoubleData();
    int lineStride = lineStride;
    int pixelStride = pixelStride;
    
    int lineInc = lineStride * yPeriod;
    int pixelInc = pixelStride * xPeriod;
    
    for (int b = 0; b < srcPA.numBands; b++) {
      double[] d = data[b];
      int lastLine = bandOffsets[b] + height * lineStride;
      
      for (int lo = bandOffsets[b]; lo < lastLine; lo += lineInc) {
        int lastPixel = lo + width * pixelStride;
        
        for (int po = lo; po < lastPixel; po += pixelInc) {
          totalPixelValue[b] += d[po];
        }
      }
    }
    totalPixelCount += (int)Math.ceil(height / yPeriod) * (int)Math.ceil(width / xPeriod);
  }
}
