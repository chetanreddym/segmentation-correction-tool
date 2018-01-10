package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.UnpackedImageData;



































public class MedianCutOpImage
  extends ColorQuantizerOpImage
{
  private int histogramSize;
  private int[] counts;
  private int[] colors;
  private Cube[] partition;
  private int bits = 8;
  




  private int mask;
  




  HistogramHash histogram;
  





  public MedianCutOpImage(RenderedImage source, Map config, ImageLayout layout, int maxColorNum, int upperBound, ROI roi, int xPeriod, int yPeriod)
  {
    super(source, config, layout, maxColorNum, roi, xPeriod, yPeriod);
    
    colorMap = null;
    histogramSize = upperBound;
  }
  
  protected synchronized void train() {
    PlanarImage source = getSourceImage(0);
    if (roi == null) {
      roi = new ROIShape(source.getBounds());
    }
    
    int minTileX = source.getMinTileX();
    int maxTileX = source.getMaxTileX();
    int minTileY = source.getMinTileY();
    int maxTileY = source.getMaxTileY();
    int xStart = source.getMinX();
    int yStart = source.getMinY();
    
    histogram = new HistogramHash(histogramSize);
    for (;;)
    {
      histogram.init();
      int oldbits = bits;
      mask = (255 << 8 - bits & 0xFF);
      mask = (mask | mask << 8 | mask << 16);
      
      for (int y = minTileY; y <= maxTileY; y++) {
        for (int x = minTileX; x <= maxTileX; x++)
        {


          Rectangle tileRect = source.getTileRect(x, y);
          

          if (roi.intersects(tileRect))
          {


            if ((checkForSkippedTiles) && (x >= xStart) && (y >= yStart))
            {


              int offsetX = (xPeriod - (x - xStart) % xPeriod) % xPeriod;
              

              int offsetY = (yPeriod - (y - yStart) % yPeriod) % yPeriod;
              




              if ((offsetX >= width) || (offsetY >= height)) {}


            }
            else
            {

              computeHistogram(source.getData(tileRect));
              if (histogram.isFull())
                break;
            }
          }
        }
        if (histogram.isFull()) {
          break;
        }
      }
      
      if (oldbits == bits) {
        counts = histogram.getCounts();
        colors = histogram.getColors();
        break;
      }
    }
    
    medianCut(maxColorNum);
    setProperty("LUT", colorMap);
    setProperty("JAI.LookupTable", colorMap);
  }
  
  private void computeHistogram(Raster source) {
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
    int xStart = source.getMinX();
    int yStart = source.getMinY();
    
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
          computeHistogramByte(uid);
        }
      }
    }
  }
  
  private void computeHistogramByte(UnpackedImageData uid) {
    Rectangle rect = rect;
    byte[][] data = uid.getByteData();
    int lineStride = lineStride;
    int pixelStride = pixelStride;
    byte[] rBand = data[0];
    byte[] gBand = data[1];
    byte[] bBand = data[2];
    
    int lineInc = lineStride * yPeriod;
    int pixelInc = pixelStride * xPeriod;
    
    int lastLine = height * lineStride;
    
    for (int lo = 0; lo < lastLine; lo += lineInc) {
      int lastPixel = lo + width * pixelStride;
      
      for (int po = lo; po < lastPixel; po += pixelInc) {
        int p = (rBand[(po + bandOffsets[0])] & 0xFF) << 16 | (gBand[(po + bandOffsets[1])] & 0xFF) << 8 | bBand[(po + bandOffsets[2])] & 0xFF;
        

        if (!histogram.insert(p & mask)) {
          bits -= 1;
          return;
        }
      }
    }
  }
  









  public void medianCut(int expectedColorNum)
  {
    partition = new Cube[expectedColorNum];
    int numCubes = 0;
    Cube cube = new Cube();
    int numColors = 0;
    for (int i = 0; i < histogramSize; i++) {
      if (counts[i] != 0) {
        numColors++;
        count += counts[i];
      }
    }
    
    lower = 0;upper = (numColors - 1);
    level = 0;
    shrinkCube(cube);
    partition[(numCubes++)] = cube;
    


    while (numCubes < expectedColorNum)
    {
      int level = 255;
      int splitableCube = -1;
      
      for (int k = 0; k < numCubes; k++) {
        if ((partition[k].lower != partition[k].upper) && (partition[k].level < level))
        {
          level = partition[k].level;
          splitableCube = k;
        }
      }
      

      if (splitableCube == -1) {
        break;
      }
      
      cube = partition[splitableCube];
      level = level;
      

      int lr = 77 * (rmax - rmin);
      int lg = 150 * (gmax - gmin);
      int lb = 29 * (bmax - bmin);
      
      int longDimMask = 0;
      if ((lr >= lg) && (lr >= lb)) longDimMask = 16711680;
      if ((lg >= lr) && (lg >= lb)) longDimMask = 65280;
      if ((lb >= lr) && (lb >= lg)) { longDimMask = 255;
      }
      
      quickSort(colors, lower, upper, longDimMask);
      

      int count = 0;
      for (int median = lower; 
          median <= upper - 1; median++) {
        if (count >= count / 2) break;
        count += counts[median];
      }
      


      Cube cubeA = new Cube();
      lower = lower;
      upper = (median - 1);
      count = count;
      level += 1;
      shrinkCube(cubeA);
      partition[splitableCube] = cubeA;
      
      Cube cubeB = new Cube();
      lower = median;
      upper = upper;
      count -= count;
      level += 1;
      shrinkCube(cubeB);
      partition[(numCubes++)] = cubeB;
    }
    

    createLUT(numCubes);
  }
  


  private void shrinkCube(Cube cube)
  {
    int rmin = 255;
    int rmax = 0;
    int gmin = 255;
    int gmax = 0;
    int bmin = 255;
    int bmax = 0;
    for (int i = lower; i <= upper; i++) {
      int color = colors[i];
      int r = color >> 16;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      if (r > rmax) { rmax = r;
      } else if (r < rmin) { rmin = r;
      }
      if (g > gmax) { gmax = g;
      } else if (g < gmin) { gmin = g;
      }
      if (b > bmax) { bmax = b;
      } else if (b < bmin) { bmin = b;
      }
    }
    rmin = rmin;rmax = rmax;
    gmin = gmin;gmax = gmax;
    bmin = bmin;bmax = bmax;
  }
  

  private void createLUT(int ncubes)
  {
    if (colorMap == null) {
      colorMap = new LookupTableJAI(new byte[3][ncubes]);
    }
    
    byte[] rLUT = colorMap.getByteData(0);
    byte[] gLUT = colorMap.getByteData(1);
    byte[] bLUT = colorMap.getByteData(2);
    
    float scale = 255.0F / (mask & 0xFF);
    
    for (int k = 0; k < ncubes; k++) {
      Cube cube = partition[k];
      float rsum = 0.0F;float gsum = 0.0F;float bsum = 0.0F;
      
      for (int i = lower; i <= upper; i++) {
        int color = colors[i];
        int r = color >> 16;
        rsum += r * counts[i];
        int g = color >> 8 & 0xFF;
        gsum += g * counts[i];
        int b = color & 0xFF;
        bsum += b * counts[i];
      }
      

      rLUT[k] = ((byte)(int)(rsum / count * scale));
      gLUT[k] = ((byte)(int)(gsum / count * scale));
      bLUT[k] = ((byte)(int)(bsum / count * scale));
    }
  }
  

  void quickSort(int[] a, int lo0, int hi0, int longDimMask)
  {
    int lo = lo0;
    int hi = hi0;
    

    if (hi0 > lo0) {
      int mid = a[((lo0 + hi0) / 2)] & longDimMask;
      while (lo <= hi) {
        while ((lo < hi0) && ((a[lo] & longDimMask) < mid))
          lo++;
        while ((hi > lo0) && ((a[hi] & longDimMask) > mid))
          hi--;
        if (lo <= hi) {
          int t = a[lo];
          a[lo] = a[hi];
          a[hi] = t;
          
          t = counts[lo];
          counts[lo] = counts[hi];
          counts[hi] = t;
          
          lo++;
          hi--;
        }
      }
      if (lo0 < hi)
        quickSort(a, lo0, hi, longDimMask);
      if (lo < hi0) {
        quickSort(a, lo, hi0, longDimMask);
      }
    }
  }
}
