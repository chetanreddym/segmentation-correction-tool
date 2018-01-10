package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;





































































public class NeuQuantOpImage
  extends ColorQuantizerOpImage
{
  protected static final int prime1 = 499;
  protected static final int prime2 = 491;
  protected static final int prime3 = 487;
  protected static final int prime4 = 503;
  protected static final int minpicturebytes = 1509;
  private int ncycles;
  private final int maxnetpos = this.maxColorNum - 1;
  private final int netbiasshift = 4;
  

  private final int intbiasshift = 16;
  private final int intbias = 65536;
  private final int gammashift = 10;
  private final int gamma = 1024;
  private final int betashift = 10;
  private final int beta = 64;
  private final int betagamma = 65536;
  

  private final int initrad = this.maxColorNum >> 3;
  private final int radiusbiasshift = 6;
  private final int radiusbias = 64;
  private final int initradius = initrad * 64;
  private final int radiusdec = 30;
  

  private final int alphabiasshift = 10;
  private final int initalpha = 1024;
  

  private int alphadec;
  
  private final int radbiasshift = 8;
  private final int radbias = 256;
  private final int alpharadbshift = 18;
  private final int alpharadbias = 262144;
  

  private int[][] network;
  
  private int[] netindex = new int['Ā'];
  
  private int[] bias = new int[this.maxColorNum];
  private int[] freq = new int[this.maxColorNum];
  private int[] radpower = new int[initrad];
  











  public NeuQuantOpImage(RenderedImage source, Map config, ImageLayout layout, int maxColorNum, int upperBound, ROI roi, int xPeriod, int yPeriod)
  {
    super(source, config, layout, maxColorNum, roi, xPeriod, yPeriod);
    
    colorMap = null;
    ncycles = upperBound;
  }
  

  protected synchronized void train()
  {
    network = new int[maxColorNum][];
    for (int i = 0; i < maxColorNum; i++) {
      network[i] = new int[4];
      int[] p = network[i]; int 
        tmp54_53 = (p[2] = (i << 12) / maxColorNum);p[1] = tmp54_53;p[0] = tmp54_53;
      freq[i] = (65536 / maxColorNum);
      bias[i] = 0;
    }
    
    PlanarImage source = getSourceImage(0);
    Rectangle rect = source.getBounds();
    
    if (roi != null) {
      rect = roi.getBounds();
    }
    RandomIter iterator = RandomIterFactory.create(source, rect);
    
    int samplefac = xPeriod * yPeriod;
    int startX = x / xPeriod;
    int startY = y / yPeriod;
    int offsetX = x % xPeriod;
    int offsetY = y % yPeriod;
    int pixelsPerLine = (width - 1) / xPeriod + 1;
    int numSamples = pixelsPerLine * ((height - 1) / yPeriod + 1);
    

    if (numSamples < 1509) {
      samplefac = 1;
    }
    alphadec = (30 + (samplefac - 1) / 3);
    int pix = 0;
    
    int delta = numSamples / ncycles;
    int alpha = 1024;
    int radius = initradius;
    
    int rad = radius >> 6;
    if (rad <= 1)
      rad = 0;
    for (int i = 0; i < rad; i++)
      radpower[i] = (alpha * ((rad * rad - i * i) * 256 / (rad * rad)));
    int step;
    int step;
    if (numSamples < 1509) {
      step = 3; } else { int step;
      if (numSamples % 499 != 0) {
        step = 1497;
      } else { int step;
        if (numSamples % 491 != 0) {
          step = 1473;
        } else { int step;
          if (numSamples % 487 != 0) {
            step = 1461;
          } else
            step = 1509;
        }
      }
    }
    int[] pixel = new int[3];
    
    for (int i = 0; i < numSamples;) {
      int y = (pix / pixelsPerLine + startY) * yPeriod + offsetY;
      int x = (pix % pixelsPerLine + startX) * xPeriod + offsetX;
      try
      {
        iterator.getPixel(x, y, pixel);
      } catch (Exception e) {}
      continue;
      

      int b = pixel[2] << 4;
      int g = pixel[1] << 4;
      int r = pixel[0] << 4;
      
      int j = contest(b, g, r);
      
      altersingle(alpha, j, b, g, r);
      if (rad != 0) {
        alterneigh(rad, j, b, g, r);
      }
      pix += step;
      if (pix >= numSamples) {
        pix -= numSamples;
      }
      i++;
      if (i % delta == 0) {
        alpha -= alpha / alphadec;
        radius -= radius / 30;
        rad = radius >> 6;
        if (rad <= 1)
          rad = 0;
        for (j = 0; j < rad; j++) {
          radpower[j] = (alpha * ((rad * rad - j * j) * 256 / (rad * rad)));
        }
      }
    }
    unbiasnet();
    inxbuild();
    createLUT();
    setProperty("LUT", colorMap);
    setProperty("JAI.LookupTable", colorMap);
  }
  
  private void createLUT() {
    colorMap = new LookupTableJAI(new byte[3][maxColorNum]);
    byte[][] map = colorMap.getByteData();
    int[] index = new int[maxColorNum];
    for (int i = 0; i < maxColorNum; i++)
      index[network[i][3]] = i;
    for (int i = 0; i < maxColorNum; i++) {
      int j = index[i];
      map[2][i] = ((byte)network[j][0]);
      map[1][i] = ((byte)network[j][1]);
      map[0][i] = ((byte)network[j][2]);
    }
  }
  


  private void inxbuild()
  {
    int previouscol = 0;
    int startpos = 0;
    for (int i = 0; i < maxColorNum; i++) {
      int[] p = network[i];
      int smallpos = i;
      int smallval = p[1];
      

      for (int j = i + 1; j < maxColorNum; j++) {
        int[] q = network[j];
        if (q[1] < smallval) {
          smallpos = j;
          smallval = q[1];
        }
      }
      int[] q = network[smallpos];
      
      if (i != smallpos) {
        j = q[0];q[0] = p[0];p[0] = j;
        j = q[1];q[1] = p[1];p[1] = j;
        j = q[2];q[2] = p[2];p[2] = j;
        j = q[3];q[3] = p[3];p[3] = j;
      }
      
      if (smallval != previouscol) {
        netindex[previouscol] = (startpos + i >> 1);
        for (j = previouscol + 1; j < smallval; j++)
          netindex[j] = i;
        previouscol = smallval;
        startpos = i;
      }
    }
    netindex[previouscol] = (startpos + maxnetpos >> 1);
    for (int j = previouscol + 1; j < 256; j++) {
      netindex[j] = maxnetpos;
    }
  }
  

  protected byte findNearestEntry(int r, int g, int b)
  {
    int bestd = 1000;
    int best = -1;
    int i = netindex[g];
    int j = i - 1;
    
    while ((i < maxColorNum) || (j >= 0)) {
      if (i < maxColorNum) {
        int[] p = network[i];
        int dist = p[1] - g;
        if (dist >= bestd) {
          i = maxColorNum;
        } else {
          i++;
          if (dist < 0)
            dist = -dist;
          int a = p[0] - b;
          if (a < 0)
            a = -a;
          dist += a;
          if (dist < bestd) {
            a = p[2] - r;
            if (a < 0)
              a = -a;
            dist += a;
            if (dist < bestd) {
              bestd = dist;
              best = p[3];
            }
          }
        }
      }
      
      if (j >= 0) {
        int[] p = network[j];
        int dist = g - p[1];
        if (dist >= bestd) {
          j = -1;
        } else {
          j--;
          if (dist < 0)
            dist = -dist;
          int a = p[0] - b;
          if (a < 0)
            a = -a;
          dist += a;
          if (dist < bestd) {
            a = p[2] - r;
            if (a < 0)
              a = -a;
            dist += a;
            if (dist < bestd) {
              bestd = dist;
              best = p[3];
            }
          }
        }
      }
    }
    return (byte)best;
  }
  


  private void unbiasnet()
  {
    for (int i = 0; i < maxColorNum; i++) {
      network[i][0] >>= 4;
      network[i][1] >>= 4;
      network[i][2] >>= 4;
      network[i][3] = i;
    }
  }
  



  private void alterneigh(int rad, int i, int b, int g, int r)
  {
    int lo = i - rad;
    if (lo < -1)
      lo = -1;
    int hi = i + rad;
    if (hi > maxColorNum) {
      hi = maxColorNum;
    }
    int j = i + 1;
    int k = i - 1;
    int m = 1;
    while ((j < hi) || (k > lo)) {
      int a = radpower[(m++)];
      if (j < hi) {
        int[] p = network[(j++)];
        
        p[0] -= a * (p[0] - b) / 262144;
        p[1] -= a * (p[1] - g) / 262144;
        p[2] -= a * (p[2] - r) / 262144;
      }
      
      if (k > lo) {
        int[] p = network[(k--)];
        
        p[0] -= a * (p[0] - b) / 262144;
        p[1] -= a * (p[1] - g) / 262144;
        p[2] -= a * (p[2] - r) / 262144;
      }
    }
  }
  



  private void altersingle(int alpha, int i, int b, int g, int r)
  {
    int[] n = network[i];
    n[0] -= alpha * (n[0] - b) / 1024;
    n[1] -= alpha * (n[1] - g) / 1024;
    n[2] -= alpha * (n[2] - r) / 1024;
  }
  





  private int contest(int b, int g, int r)
  {
    int bestd = Integer.MAX_VALUE;
    int bestbiasd = bestd;
    int bestpos = -1;
    int bestbiaspos = bestpos;
    
    for (int i = 0; i < maxColorNum; i++) {
      int[] n = network[i];
      int dist = n[0] - b;
      if (dist < 0)
        dist = -dist;
      int a = n[1] - g;
      if (a < 0)
        a = -a;
      dist += a;
      a = n[2] - r;
      if (a < 0)
        a = -a;
      dist += a;
      if (dist < bestd) {
        bestd = dist;
        bestpos = i;
      }
      int biasdist = dist - (bias[i] >> 12);
      if (biasdist < bestbiasd) {
        bestbiasd = biasdist;
        bestbiaspos = i;
      }
      int betafreq = freq[i] >> 10;
      freq[i] -= betafreq;
      bias[i] += (betafreq << 10);
    }
    freq[bestpos] += 64;
    bias[bestpos] -= 65536;
    return bestbiaspos;
  }
}
