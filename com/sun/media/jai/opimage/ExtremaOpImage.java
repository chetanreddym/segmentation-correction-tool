package com.sun.media.jai.opimage;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.StatisticsOpImage;
import javax.media.jai.UnpackedImageData;




























public class ExtremaOpImage
  extends StatisticsOpImage
{
  protected double[][] extrema;
  protected ArrayList[] minLocations;
  protected ArrayList[] maxLocations;
  protected int[] minCounts;
  protected int[] maxCounts;
  protected boolean saveLocations;
  protected int maxRuns;
  protected int numMinLocations = 0;
  
  protected int numMaxLocations = 0;
  
  private boolean isInitialized = false;
  
  private PixelAccessor srcPA;
  private int srcSampleType;
  
  private final boolean tileIntersectsROI(int tileX, int tileY)
  {
    if (roi == null) {
      return true;
    }
    return roi.intersects(tileXToX(tileX), tileYToY(tileY), tileWidth, tileHeight);
  }
  













  public ExtremaOpImage(RenderedImage source, ROI roi, int xStart, int yStart, int xPeriod, int yPeriod, boolean saveLocations, int maxRuns)
  {
    super(source, roi, xStart, yStart, xPeriod, yPeriod);
    
    extrema = ((double[][])null);
    this.saveLocations = saveLocations;
    this.maxRuns = maxRuns;
  }
  
  public Object getProperty(String name)
  {
    int numBands = sampleModel.getNumBands();
    
    if (extrema == null)
    {

      return super.getProperty(name); }
    if (name.equalsIgnoreCase("extrema")) {
      double[][] stats = new double[2][numBands];
      for (int i = 0; i < numBands; i++) {
        stats[0][i] = extrema[0][i];
        stats[1][i] = extrema[1][i];
      }
      return stats; }
    if (name.equalsIgnoreCase("minimum")) {
      double[] stats = new double[numBands];
      for (int i = 0; i < numBands; i++) {
        stats[i] = extrema[0][i];
      }
      return stats; }
    if (name.equalsIgnoreCase("maximum")) {
      double[] stats = new double[numBands];
      for (int i = 0; i < numBands; i++) {
        stats[i] = extrema[1][i];
      }
      return stats; }
    if ((saveLocations) && (name.equalsIgnoreCase("minLocations")))
      return minLocations;
    if ((saveLocations) && (name.equalsIgnoreCase("maxLocations"))) {
      return maxLocations;
    }
    
    return Image.UndefinedProperty;
  }
  
  protected String[] getStatisticsNames() {
    return new String[] { "extrema", "maximum", "minimum", "maxLocations", "minLocations" };
  }
  
  protected Object createStatistics(String name)
  {
    int numBands = sampleModel.getNumBands();
    Object stats = null;
    
    if (name.equalsIgnoreCase("extrema")) {
      stats = new double[2][numBands];
    } else if ((name.equalsIgnoreCase("minimum")) || (name.equalsIgnoreCase("maximum")))
    {
      stats = new double[numBands];
    } else if ((saveLocations) && ((name.equalsIgnoreCase("minLocations")) || (name.equalsIgnoreCase("maxLocations"))))
    {

      stats = new ArrayList[numBands];
    } else {
      stats = Image.UndefinedProperty;
    }
    return stats;
  }
  
  private final int startPosition(int pos, int start, int period) {
    int t = (pos - start) % period;
    return t == 0 ? pos : pos + (period - t);
  }
  

  protected void accumulateStatistics(String name, Raster source, Object stats)
  {
    if (!isInitialized) {
      srcPA = new PixelAccessor(getSourceImage(0));
      srcSampleType = (srcPA.sampleType == -1 ? 0 : srcPA.sampleType);
      
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


        initializeState(source);
        
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
    if (name.equalsIgnoreCase("extrema")) {
      double[][] ext = (double[][])stats;
      for (int i = 0; i < srcPA.numBands; i++) {
        ext[0][i] = extrema[0][i];
        ext[1][i] = extrema[1][i];
      }
    } else if (name.equalsIgnoreCase("minimum")) {
      double[] min = (double[])stats;
      for (int i = 0; i < srcPA.numBands; i++) {
        min[i] = extrema[0][i];
      }
    } else if (name.equalsIgnoreCase("maximum")) {
      double[] max = (double[])stats;
      for (int i = 0; i < srcPA.numBands; i++) {
        max[i] = extrema[1][i];
      }
    } else if (name.equalsIgnoreCase("minLocations")) {
      ArrayList[] minLoc = (ArrayList[])stats;
      for (int i = 0; i < srcPA.numBands; i++) {
        minLoc[i] = minLocations[i];
      }
    } else if (name.equalsIgnoreCase("maxLocations")) {
      ArrayList[] maxLoc = (ArrayList[])stats;
      for (int i = 0; i < srcPA.numBands; i++)
        maxLoc[i] = maxLocations[i];
    }
  }
  
  private void accumulateStatisticsByte(UnpackedImageData uid) {
    Rectangle rect = rect;
    byte[][] data = uid.getByteData();
    int lineStride = lineStride;
    int pixelStride = pixelStride;
    
    int lineInc = lineStride * yPeriod;
    int pixelInc = pixelStride * xPeriod;
    
    if (!saveLocations) {
      for (int b = 0; b < srcPA.numBands; b++) {
        int min = (int)extrema[0][b];
        int max = (int)extrema[1][b];
        
        byte[] d = data[b];
        int lastLine = bandOffsets[b] + height * lineStride;
        
        for (int lo = bandOffsets[b]; lo < lastLine; lo += lineInc) {
          int lastPixel = lo + width * pixelStride;
          
          for (int po = lo; po < lastPixel; po += pixelInc) {
            int p = d[po] & 0xFF;
            
            if (p < min) {
              min = p;
            } else if (p > max) {
              max = p;
            }
          }
        }
        extrema[0][b] = min;
        extrema[1][b] = max;
      }
    } else {
      for (int b = 0; b < srcPA.numBands; b++) {
        int min = (int)extrema[0][b];
        int max = (int)extrema[1][b];
        ArrayList minList = minLocations[b];
        ArrayList maxList = maxLocations[b];
        int minCount = minCounts[b];
        int maxCount = maxCounts[b];
        
        byte[] d = data[b];
        int lastLine = bandOffsets[b] + height * lineStride;
        
        int lo = bandOffsets[b]; for (int y = y; lo < lastLine; 
            y += yPeriod)
        {
          int lastPixel = lo + width * pixelStride;
          int minStart = 0;
          int maxStart = 0;
          int minLength = 0;
          int maxLength = 0;
          
          int po = lo; for (int x = x; po < lastPixel;)
          {

            int p = d[po] & 0xFF;
            
            if (p < min) {
              min = p;
              minStart = x;
              minLength = 1;
              minList.clear();
              minCount = 0;
            } else if (p > max) {
              max = p;
              maxStart = x;
              maxLength = 1;
              maxList.clear();
              maxCount = 0;
            } else {
              if (p == min) {
                if (minLength == 0)
                  minStart = x;
                minLength++;
              } else if ((minLength > 0) && (minCount < maxRuns)) {
                minList.add(new int[] { minStart, y, minLength });
                minCount++;
                minLength = 0;
              }
              
              if (p == max) {
                if (maxLength == 0)
                  maxStart = x;
                maxLength++;
              } else if ((maxLength > 0) && (maxCount < maxRuns)) {
                maxList.add(new int[] { maxStart, y, maxLength });
                maxCount++;
                maxLength = 0;
              }
            }
            po += pixelInc;
            x += xPeriod;
          }
          




































          if ((maxLength > 0) && (maxCount < maxRuns)) {
            maxList.add(new int[] { maxStart, y, maxLength });
            maxCount++;
          }
          
          if ((minLength > 0) && (minCount < maxRuns)) {
            minList.add(new int[] { minStart, y, minLength });
            minCount++;
          }
          lo += lineInc;
        }
        
























































        extrema[0][b] = min;
        extrema[1][b] = max;
        minCounts[b] = minCount;
        maxCounts[b] = maxCount;
      }
    }
  }
  
  private void accumulateStatisticsUShort(UnpackedImageData uid) {
    Rectangle rect = rect;
    short[][] data = uid.getShortData();
    int lineStride = lineStride;
    int pixelStride = pixelStride;
    
    int lineInc = lineStride * yPeriod;
    int pixelInc = pixelStride * xPeriod;
    
    if (!saveLocations) {
      for (int b = 0; b < srcPA.numBands; b++) {
        int min = (int)extrema[0][b];
        int max = (int)extrema[1][b];
        
        short[] d = data[b];
        int lastLine = bandOffsets[b] + height * lineStride;
        
        for (int lo = bandOffsets[b]; lo < lastLine; lo += lineInc) {
          int lastPixel = lo + width * pixelStride;
          
          for (int po = lo; po < lastPixel; po += pixelInc) {
            int p = d[po] & 0xFFFF;
            
            if (p < min) {
              min = p;
            } else if (p > max) {
              max = p;
            }
          }
        }
        extrema[0][b] = min;
        extrema[1][b] = max;
      }
    } else {
      for (int b = 0; b < srcPA.numBands; b++) {
        int min = (int)extrema[0][b];
        int max = (int)extrema[1][b];
        ArrayList minList = minLocations[b];
        ArrayList maxList = maxLocations[b];
        int minCount = minCounts[b];
        int maxCount = maxCounts[b];
        
        short[] d = data[b];
        int lastLine = bandOffsets[b] + height * lineStride;
        
        int lo = bandOffsets[b]; for (int y = y; lo < lastLine; 
            y += yPeriod)
        {
          int lastPixel = lo + width * pixelStride;
          int minStart = 0;
          int maxStart = 0;
          int minLength = 0;
          int maxLength = 0;
          
          int po = lo; for (int x = x; po < lastPixel;)
          {

            int p = d[po] & 0xFFFF;
            
            if (p < min) {
              min = p;
              minStart = x;
              minLength = 1;
              minList.clear();
              minCount = 0;
            } else if (p > max) {
              max = p;
              maxStart = x;
              maxLength = 1;
              maxList.clear();
              maxCount = 0;
            } else {
              if (p == min) {
                if (minLength == 0)
                  minStart = x;
                minLength++;
              } else if ((minLength > 0) && (minCount < maxRuns)) {
                minList.add(new int[] { minStart, y, minLength });
                minCount++;
                minLength = 0;
              }
              
              if (p == max) {
                if (maxLength == 0)
                  maxStart = x;
                maxLength++;
              } else if ((maxLength > 0) && (maxCount < maxRuns)) {
                maxList.add(new int[] { maxStart, y, maxLength });
                maxCount++;
                maxLength = 0;
              }
            }
            po += pixelInc;
            x += xPeriod;
          }
          




































          if ((maxLength > 0) && (maxCount < maxRuns)) {
            maxList.add(new int[] { maxStart, y, maxLength });
            maxCount++;
          }
          
          if ((minLength > 0) && (minCount < maxRuns)) {
            minList.add(new int[] { minStart, y, minLength });
            minCount++;
          }
          lo += lineInc;
        }
        
























































        extrema[0][b] = min;
        extrema[1][b] = max;
        minCounts[b] = minCount;
        maxCounts[b] = maxCount;
      }
    }
  }
  
  private void accumulateStatisticsShort(UnpackedImageData uid) {
    Rectangle rect = rect;
    short[][] data = uid.getShortData();
    int lineStride = lineStride;
    int pixelStride = pixelStride;
    
    int lineInc = lineStride * yPeriod;
    int pixelInc = pixelStride * xPeriod;
    
    if (!saveLocations) {
      for (int b = 0; b < srcPA.numBands; b++) {
        int min = (int)extrema[0][b];
        int max = (int)extrema[1][b];
        
        short[] d = data[b];
        int lastLine = bandOffsets[b] + height * lineStride;
        
        for (int lo = bandOffsets[b]; lo < lastLine; lo += lineInc) {
          int lastPixel = lo + width * pixelStride;
          
          for (int po = lo; po < lastPixel; po += pixelInc) {
            int p = d[po];
            
            if (p < min) {
              min = p;
            } else if (p > max) {
              max = p;
            }
          }
        }
        extrema[0][b] = min;
        extrema[1][b] = max;
      }
    } else {
      for (int b = 0; b < srcPA.numBands; b++) {
        int min = (int)extrema[0][b];
        int max = (int)extrema[1][b];
        ArrayList minList = minLocations[b];
        ArrayList maxList = maxLocations[b];
        int minCount = minCounts[b];
        int maxCount = maxCounts[b];
        
        short[] d = data[b];
        int lastLine = bandOffsets[b] + height * lineStride;
        
        int lo = bandOffsets[b]; for (int y = y; lo < lastLine; 
            y += yPeriod)
        {
          int lastPixel = lo + width * pixelStride;
          int minStart = 0;
          int maxStart = 0;
          int minLength = 0;
          int maxLength = 0;
          
          int po = lo; for (int x = x; po < lastPixel;)
          {

            int p = d[po];
            
            if (p < min) {
              min = p;
              minStart = x;
              minLength = 1;
              minList.clear();
              minCount = 0;
            } else if (p > max) {
              max = p;
              maxStart = x;
              maxLength = 1;
              maxList.clear();
              maxCount = 0;
            } else {
              if (p == min) {
                if (minLength == 0)
                  minStart = x;
                minLength++;
              } else if ((minLength > 0) && (minCount < maxRuns)) {
                minList.add(new int[] { minStart, y, minLength });
                minCount++;
                minLength = 0;
              }
              
              if (p == max) {
                if (maxLength == 0)
                  maxStart = x;
                maxLength++;
              } else if ((maxLength > 0) && (maxCount < maxRuns)) {
                maxList.add(new int[] { maxStart, y, maxLength });
                maxCount++;
                maxLength = 0;
              }
            }
            po += pixelInc;
            x += xPeriod;
          }
          




































          if ((maxLength > 0) && (maxCount < maxRuns)) {
            maxList.add(new int[] { maxStart, y, maxLength });
            maxCount++;
          }
          
          if ((minLength > 0) && (minCount < maxRuns)) {
            minList.add(new int[] { minStart, y, minLength });
            minCount++;
          }
          lo += lineInc;
        }
        
























































        extrema[0][b] = min;
        extrema[1][b] = max;
        minCounts[b] = minCount;
        maxCounts[b] = maxCount;
      }
    }
  }
  
  private void accumulateStatisticsInt(UnpackedImageData uid) {
    Rectangle rect = rect;
    int[][] data = uid.getIntData();
    int lineStride = lineStride;
    int pixelStride = pixelStride;
    
    int lineInc = lineStride * yPeriod;
    int pixelInc = pixelStride * xPeriod;
    
    if (!saveLocations) {
      for (int b = 0; b < srcPA.numBands; b++) {
        int min = (int)extrema[0][b];
        int max = (int)extrema[1][b];
        
        int[] d = data[b];
        int lastLine = bandOffsets[b] + height * lineStride;
        
        for (int lo = bandOffsets[b]; lo < lastLine; lo += lineInc) {
          int lastPixel = lo + width * pixelStride;
          
          for (int po = lo; po < lastPixel; po += pixelInc) {
            int p = d[po];
            
            if (p < min) {
              min = p;
            } else if (p > max) {
              max = p;
            }
          }
        }
        extrema[0][b] = min;
        extrema[1][b] = max;
      }
    } else {
      for (int b = 0; b < srcPA.numBands; b++) {
        int min = (int)extrema[0][b];
        int max = (int)extrema[1][b];
        ArrayList minList = minLocations[b];
        ArrayList maxList = maxLocations[b];
        int minCount = minCounts[b];
        int maxCount = maxCounts[b];
        
        int[] d = data[b];
        int lastLine = bandOffsets[b] + height * lineStride;
        
        int lo = bandOffsets[b]; for (int y = y; lo < lastLine; 
            y += yPeriod)
        {
          int lastPixel = lo + width * pixelStride;
          int minStart = 0;
          int maxStart = 0;
          int minLength = 0;
          int maxLength = 0;
          
          int po = lo; for (int x = x; po < lastPixel;)
          {

            int p = d[po];
            
            if (p < min) {
              min = p;
              minStart = x;
              minLength = 1;
              minList.clear();
              minCount = 0;
            } else if (p > max) {
              max = p;
              maxStart = x;
              maxLength = 1;
              maxList.clear();
              maxCount = 0;
            } else {
              if (p == min) {
                if (minLength == 0)
                  minStart = x;
                minLength++;
              } else if ((minLength > 0) && (minCount < maxRuns)) {
                minList.add(new int[] { minStart, y, minLength });
                minCount++;
                minLength = 0;
              }
              
              if (p == max) {
                if (maxLength == 0)
                  maxStart = x;
                maxLength++;
              } else if ((maxLength > 0) && (maxCount < maxRuns)) {
                maxList.add(new int[] { maxStart, y, maxLength });
                maxCount++;
                maxLength = 0;
              }
            }
            po += pixelInc;
            x += xPeriod;
          }
          




































          if ((maxLength > 0) && (maxCount < maxRuns)) {
            maxList.add(new int[] { maxStart, y, maxLength });
            maxCount++;
          }
          
          if ((minLength > 0) && (minCount < maxRuns)) {
            minList.add(new int[] { minStart, y, minLength });
            minCount++;
          }
          lo += lineInc;
        }
        
























































        extrema[0][b] = min;
        extrema[1][b] = max;
        minCounts[b] = minCount;
        maxCounts[b] = maxCount;
      }
    }
  }
  
  private void accumulateStatisticsFloat(UnpackedImageData uid) {
    Rectangle rect = rect;
    float[][] data = uid.getFloatData();
    int lineStride = lineStride;
    int pixelStride = pixelStride;
    
    int lineInc = lineStride * yPeriod;
    int pixelInc = pixelStride * xPeriod;
    
    if (!saveLocations) {
      for (int b = 0; b < srcPA.numBands; b++) {
        float min = (float)extrema[0][b];
        float max = (float)extrema[1][b];
        
        float[] d = data[b];
        int lastLine = bandOffsets[b] + height * lineStride;
        
        for (int lo = bandOffsets[b]; lo < lastLine; lo += lineInc) {
          int lastPixel = lo + width * pixelStride;
          
          for (int po = lo; po < lastPixel; po += pixelInc) {
            float p = d[po];
            
            if (p < min) {
              min = p;
            } else if (p > max) {
              max = p;
            }
          }
        }
        extrema[0][b] = min;
        extrema[1][b] = max;
      }
    } else {
      for (int b = 0; b < srcPA.numBands; b++) {
        float min = (float)extrema[0][b];
        float max = (float)extrema[1][b];
        ArrayList minList = minLocations[b];
        ArrayList maxList = maxLocations[b];
        int minCount = minCounts[b];
        int maxCount = maxCounts[b];
        
        float[] d = data[b];
        int lastLine = bandOffsets[b] + height * lineStride;
        
        int lo = bandOffsets[b]; for (int y = y; lo < lastLine; 
            y += yPeriod)
        {
          int lastPixel = lo + width * pixelStride;
          int minStart = 0;
          int maxStart = 0;
          int minLength = 0;
          int maxLength = 0;
          
          int po = lo; for (int x = x; po < lastPixel;)
          {

            float p = d[po];
            
            if (p < min) {
              min = p;
              minStart = x;
              minLength = 1;
              minList.clear();
              minCount = 0;
            } else if (p > max) {
              max = p;
              maxStart = x;
              maxLength = 1;
              maxList.clear();
              maxCount = 0;
            } else {
              if (p == min) {
                if (minLength == 0)
                  minStart = x;
                minLength++;
              } else if ((minLength > 0) && (minCount < maxRuns)) {
                minList.add(new int[] { minStart, y, minLength });
                minCount++;
                minLength = 0;
              }
              
              if (p == max) {
                if (maxLength == 0)
                  maxStart = x;
                maxLength++;
              } else if ((maxLength > 0) && (maxCount < maxRuns)) {
                maxList.add(new int[] { maxStart, y, maxLength });
                maxCount++;
                maxLength = 0;
              }
            }
            po += pixelInc;
            x += xPeriod;
          }
          




































          if ((maxLength > 0) && (maxCount < maxRuns)) {
            maxList.add(new int[] { maxStart, y, maxLength });
            maxCount++;
          }
          
          if ((minLength > 0) && (minCount < maxRuns)) {
            minList.add(new int[] { minStart, y, minLength });
            minCount++;
          }
          lo += lineInc;
        }
        
























































        extrema[0][b] = min;
        extrema[1][b] = max;
        minCounts[b] = minCount;
        maxCounts[b] = maxCount;
      }
    }
  }
  
  private void accumulateStatisticsDouble(UnpackedImageData uid) {
    Rectangle rect = rect;
    double[][] data = uid.getDoubleData();
    int lineStride = lineStride;
    int pixelStride = pixelStride;
    
    int lineInc = lineStride * yPeriod;
    int pixelInc = pixelStride * xPeriod;
    
    if (!saveLocations) {
      for (int b = 0; b < srcPA.numBands; b++) {
        double min = extrema[0][b];
        double max = extrema[1][b];
        
        double[] d = data[b];
        int lastLine = bandOffsets[b] + height * lineStride;
        
        for (int lo = bandOffsets[b]; lo < lastLine; lo += lineInc) {
          int lastPixel = lo + width * pixelStride;
          
          for (int po = lo; po < lastPixel; po += pixelInc) {
            double p = d[po];
            
            if (p < min) {
              min = p;
            } else if (p > max) {
              max = p;
            }
          }
        }
        extrema[0][b] = min;
        extrema[1][b] = max;
      }
    } else {
      for (int b = 0; b < srcPA.numBands; b++) {
        double min = extrema[0][b];
        double max = extrema[1][b];
        ArrayList minList = minLocations[b];
        ArrayList maxList = maxLocations[b];
        int minCount = minCounts[b];
        int maxCount = maxCounts[b];
        
        double[] d = data[b];
        int lastLine = bandOffsets[b] + height * lineStride;
        
        int lo = bandOffsets[b]; for (int y = y; lo < lastLine; 
            y += yPeriod)
        {
          int lastPixel = lo + width * pixelStride;
          int minStart = 0;
          int maxStart = 0;
          int minLength = 0;
          int maxLength = 0;
          
          int po = lo; for (int x = x; po < lastPixel;)
          {

            double p = d[po];
            
            if (p < min) {
              min = p;
              minStart = x;
              minLength = 1;
              minList.clear();
              minCount = 0;
            } else if (p > max) {
              max = p;
              maxStart = x;
              maxLength = 1;
              maxList.clear();
              maxCount = 0;
            } else {
              if (p == min) {
                if (minLength == 0)
                  minStart = x;
                minLength++;
              } else if ((minLength > 0) && (minCount < maxRuns)) {
                minList.add(new int[] { minStart, y, minLength });
                minCount++;
                minLength = 0;
              }
              
              if (p == max) {
                if (maxLength == 0)
                  maxStart = x;
                maxLength++;
              } else if ((maxLength > 0) && (maxCount < maxRuns)) {
                maxList.add(new int[] { maxStart, y, maxLength });
                maxCount++;
                maxLength = 0;
              }
            }
            po += pixelInc;
            x += xPeriod;
          }
          




































          if ((maxLength > 0) && (maxCount < maxRuns)) {
            maxList.add(new int[] { maxStart, y, maxLength });
            maxCount++;
          }
          
          if ((minLength > 0) && (minCount < maxRuns)) {
            minList.add(new int[] { minStart, y, minLength });
            minCount++;
          }
          lo += lineInc;
        }
        
























































        extrema[0][b] = min;
        extrema[1][b] = max;
        minCounts[b] = minCount;
        maxCounts[b] = maxCount;
      }
    }
  }
  
  protected void initializeState(Raster source) {
    if (extrema == null) {
      int numBands = sampleModel.getNumBands();
      extrema = new double[2][numBands];
      
      Rectangle rect = source.getBounds();
      





      if (roi != null) {
        LinkedList rectList = roi.getAsRectangleList(x, y, width, height);
        


        if (rectList == null) {
          return;
        }
        ListIterator iterator = rectList.listIterator(0);
        if (iterator.hasNext()) {
          rect = rect.intersection((Rectangle)iterator.next());
        }
      }
      
      x = startPosition(x, xStart, xPeriod);
      y = startPosition(y, yStart, yPeriod);
      source.getPixel(x, y, extrema[0]);
      
      for (int i = 0; i < numBands; i++) {
        extrema[1][i] = extrema[0][i];
      }
      
      if (saveLocations) {
        minLocations = new ArrayList[numBands];
        maxLocations = new ArrayList[numBands];
        minCounts = new int[numBands];
        maxCounts = new int[numBands];
        for (int i = 0; i < numBands; i++) {
          minLocations[i] = new ArrayList();
          maxLocations[i] = new ArrayList(); int 
            tmp280_279 = 0;maxCounts[i] = tmp280_279;minCounts[i] = tmp280_279;
        }
      }
    }
  }
}
