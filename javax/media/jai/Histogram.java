package javax.media.jai;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.ListIterator;





































































public class Histogram
  implements Serializable
{
  private int[] numBins;
  private double[] lowValue;
  private double[] highValue;
  private int numBands;
  private double[] binWidth;
  private int[][] bins = (int[][])null;
  

  private int[] totals = null;
  

  private double[] mean = null;
  






  private static final int[] fill(int[] array, int newLength)
  {
    int[] newArray = null;
    
    if ((array == null) || (array.length == 0)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    if (newLength > 0) {
      newArray = new int[newLength];
      int oldLength = array.length;
      for (int i = 0; i < newLength; i++) {
        if (i < oldLength) {
          newArray[i] = array[i];
        } else {
          newArray[i] = array[0];
        }
      }
    }
    return newArray;
  }
  






  private static final double[] fill(double[] array, int newLength)
  {
    double[] newArray = null;
    
    if ((array == null) || (array.length == 0)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    if (newLength > 0) {
      newArray = new double[newLength];
      int oldLength = array.length;
      for (int i = 0; i < newLength; i++) {
        if (i < oldLength) {
          newArray[i] = array[i];
        } else {
          newArray[i] = array[0];
        }
      }
    }
    return newArray;
  }
  


































  public Histogram(int[] numBins, double[] lowValue, double[] highValue)
  {
    if ((numBins == null) || (lowValue == null) || (highValue == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    numBands = numBins.length;
    
    if ((lowValue.length != numBands) || (highValue.length != numBands)) {
      throw new IllegalArgumentException(JaiI18N.getString("Histogram0"));
    }
    

    if (numBands == 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Histogram1"));
    }
    

    for (int i = 0; i < numBands; i++) {
      if (numBins[i] <= 0) {
        throw new IllegalArgumentException(JaiI18N.getString("Histogram2"));
      }
      

      if (lowValue[i] >= highValue[i]) {
        throw new IllegalArgumentException(JaiI18N.getString("Histogram3"));
      }
    }
    

    this.numBins = ((int[])numBins.clone());
    this.lowValue = ((double[])lowValue.clone());
    this.highValue = ((double[])highValue.clone());
    
    binWidth = new double[numBands];
    

    for (int i = 0; i < numBands; i++) {
      binWidth[i] = ((highValue[i] - lowValue[i]) / numBins[i]);
    }
  }
  


































  public Histogram(int[] numBins, double[] lowValue, double[] highValue, int numBands)
  {
    this(fill(numBins, numBands), fill(lowValue, numBands), fill(highValue, numBands));
  }
  
























  public Histogram(int numBins, double lowValue, double highValue, int numBands)
  {
    if (numBands <= 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Histogram1"));
    }
    

    if (numBins <= 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Histogram2"));
    }
    

    if (lowValue >= highValue) {
      throw new IllegalArgumentException(JaiI18N.getString("Histogram3"));
    }
    

    this.numBands = numBands;
    this.numBins = new int[numBands];
    this.lowValue = new double[numBands];
    this.highValue = new double[numBands];
    binWidth = new double[numBands];
    
    double bw = (highValue - lowValue) / numBins;
    
    for (int i = 0; i < numBands; i++) {
      this.numBins[i] = numBins;
      this.lowValue[i] = lowValue;
      this.highValue[i] = highValue;
      binWidth[i] = bw;
    }
  }
  
  public int[] getNumBins()
  {
    return (int[])numBins.clone();
  }
  








  public int getNumBins(int band)
  {
    return numBins[band];
  }
  
  public double[] getLowValue()
  {
    return (double[])lowValue.clone();
  }
  








  public double getLowValue(int band)
  {
    return lowValue[band];
  }
  
  public double[] getHighValue()
  {
    return (double[])highValue.clone();
  }
  








  public double getHighValue(int band)
  {
    return highValue[band];
  }
  




  public int getNumBands()
  {
    return numBands;
  }
  




  public synchronized int[][] getBins()
  {
    if (bins == null) {
      bins = new int[numBands][];
      
      for (int i = 0; i < numBands; i++) {
        bins[i] = new int[numBins[i]];
      }
    }
    
    return bins;
  }
  








  public int[] getBins(int band)
  {
    getBins();
    return bins[band];
  }
  









  public int getBinSize(int band, int bin)
  {
    getBins();
    return bins[band][bin];
  }
  










  public double getBinLowValue(int band, int bin)
  {
    return lowValue[band] + bin * binWidth[band];
  }
  



  public void clearHistogram()
  {
    if (bins != null) {
      synchronized (bins) {
        for (int i = 0; i < numBands; i++) {
          int[] b = bins[i];
          int length = b.length;
          
          for (int j = 0; j < length; j++) {
            b[j] = 0;
          }
        }
      }
    }
  }
  










  public int[] getTotals()
  {
    if (totals == null) {
      getBins();
      
      synchronized (this) {
        totals = new int[numBands];
        
        for (int i = 0; i < numBands; i++) {
          int[] b = bins[i];
          int length = b.length;
          int t = 0;
          
          for (int j = 0; j < length; j++) {
            t += b[j];
          }
          
          totals[i] = t;
        }
      }
    }
    
    return totals;
  }
  

















  public int getSubTotal(int band, int minBin, int maxBin)
  {
    if ((minBin < 0) || (maxBin >= numBins[band])) {
      throw new ArrayIndexOutOfBoundsException(JaiI18N.getString("Histogram5"));
    }
    

    if (minBin > maxBin) {
      throw new IllegalArgumentException(JaiI18N.getString("Histogram10"));
    }
    

    int[] b = getBins(band);
    int total = 0;
    
    for (int i = minBin; i <= maxBin; i++) {
      total += b[i];
    }
    
    return total;
  }
  




  public double[] getMean()
  {
    if (mean == null) {
      getTotals();
      
      synchronized (this) {
        mean = new double[numBands];
        
        for (int i = 0; i < numBands; i++) {
          int[] counts = getBins(i);
          int nBins = numBins[i];
          double level = getLowValue(i);
          double bw = binWidth[i];
          
          double mu = 0.0D;
          double total = totals[i];
          
          for (int b = 0; b < nBins; b++) {
            mu += counts[b] / total * level;
            level += bw;
          }
          
          mean[i] = mu;
        }
      }
    }
    
    return mean;
  }
  





































  public void countPixels(Raster raster, ROI roi, int xStart, int yStart, int xPeriod, int yPeriod)
  {
    if (raster == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    SampleModel sampleModel = raster.getSampleModel();
    
    if (sampleModel.getNumBands() != numBands) {
      throw new IllegalArgumentException(JaiI18N.getString("Histogram4"));
    }
    

    Rectangle bounds = raster.getBounds();
    
    LinkedList rectList;
    if (roi == null) {
      LinkedList rectList = new LinkedList();
      rectList.addLast(bounds);
    } else {
      rectList = roi.getAsRectangleList(x, y, width, height);
      
      if (rectList == null) {
        return;
      }
    }
    
    PixelAccessor accessor = new PixelAccessor(sampleModel, null);
    
    ListIterator iterator = rectList.listIterator(0);
    
    while (iterator.hasNext()) {
      Rectangle r = (Rectangle)iterator.next();
      int tx = x;
      int ty = y;
      

      x = startPosition(tx, xStart, xPeriod);
      y = startPosition(ty, yStart, yPeriod);
      width = (tx + width - x);
      height = (ty + height - y);
      
      if ((width > 0) && (height > 0))
      {


        switch (sampleType) {
        case -1: 
        case 0: 
          countPixelsByte(accessor, raster, r, xPeriod, yPeriod);
          break;
        case 1: 
          countPixelsUShort(accessor, raster, r, xPeriod, yPeriod);
          break;
        case 2: 
          countPixelsShort(accessor, raster, r, xPeriod, yPeriod);
          break;
        case 3: 
          countPixelsInt(accessor, raster, r, xPeriod, yPeriod);
          break;
        case 4: 
          countPixelsFloat(accessor, raster, r, xPeriod, yPeriod);
          break;
        case 5: 
          countPixelsDouble(accessor, raster, r, xPeriod, yPeriod);
          break;
        default: 
          throw new RuntimeException(JaiI18N.getString("Histogram11"));
        }
        
      }
    }
  }
  
  private void countPixelsByte(PixelAccessor accessor, Raster raster, Rectangle rect, int xPeriod, int yPeriod)
  {
    UnpackedImageData uid = accessor.getPixels(raster, rect, 0, false);
    

    byte[][] byteData = uid.getByteData();
    int pixelStride = pixelStride * xPeriod;
    int lineStride = lineStride * yPeriod;
    int[] offsets = bandOffsets;
    
    for (int b = 0; b < numBands; b++) {
      byte[] data = byteData[b];
      int lineOffset = offsets[b];
      
      int[] bin = new int[numBins[b]];
      double low = lowValue[b];
      double high = highValue[b];
      double bwidth = binWidth[b];
      
      for (int h = 0; h < height; h += yPeriod) {
        int pixelOffset = lineOffset;
        lineOffset += lineStride;
        
        for (int w = 0; w < width; w += xPeriod) {
          int d = data[pixelOffset] & 0xFF;
          pixelOffset += pixelStride;
          
          if ((d >= low) && (d < high)) {
            int i = (int)((d - low) / bwidth);
            bin[i] += 1;
          }
        }
      }
      
      mergeBins(b, bin);
    }
  }
  


  private void countPixelsUShort(PixelAccessor accessor, Raster raster, Rectangle rect, int xPeriod, int yPeriod)
  {
    UnpackedImageData uid = accessor.getPixels(raster, rect, 1, false);
    

    short[][] shortData = uid.getShortData();
    int pixelStride = pixelStride * xPeriod;
    int lineStride = lineStride * yPeriod;
    int[] offsets = bandOffsets;
    
    for (int b = 0; b < numBands; b++) {
      short[] data = shortData[b];
      int lineOffset = offsets[b];
      
      int[] bin = new int[numBins[b]];
      double low = lowValue[b];
      double high = highValue[b];
      double bwidth = binWidth[b];
      
      for (int h = 0; h < height; h += yPeriod) {
        int pixelOffset = lineOffset;
        lineOffset += lineStride;
        
        for (int w = 0; w < width; w += xPeriod) {
          int d = data[pixelOffset] & 0xFFFF;
          pixelOffset += pixelStride;
          
          if ((d >= low) && (d < high)) {
            int i = (int)((d - low) / bwidth);
            bin[i] += 1;
          }
        }
      }
      
      mergeBins(b, bin);
    }
  }
  


  private void countPixelsShort(PixelAccessor accessor, Raster raster, Rectangle rect, int xPeriod, int yPeriod)
  {
    UnpackedImageData uid = accessor.getPixels(raster, rect, 2, false);
    

    short[][] shortData = uid.getShortData();
    int pixelStride = pixelStride * xPeriod;
    int lineStride = lineStride * yPeriod;
    int[] offsets = bandOffsets;
    
    for (int b = 0; b < numBands; b++) {
      short[] data = shortData[b];
      int lineOffset = offsets[b];
      
      int[] bin = new int[numBins[b]];
      double low = lowValue[b];
      double high = highValue[b];
      double bwidth = binWidth[b];
      
      for (int h = 0; h < height; h += yPeriod) {
        int pixelOffset = lineOffset;
        lineOffset += lineStride;
        
        for (int w = 0; w < width; w += xPeriod) {
          int d = data[pixelOffset];
          pixelOffset += pixelStride;
          
          if ((d >= low) && (d < high)) {
            int i = (int)((d - low) / bwidth);
            bin[i] += 1;
          }
        }
      }
      
      mergeBins(b, bin);
    }
  }
  


  private void countPixelsInt(PixelAccessor accessor, Raster raster, Rectangle rect, int xPeriod, int yPeriod)
  {
    UnpackedImageData uid = accessor.getPixels(raster, rect, 3, false);
    

    int[][] intData = uid.getIntData();
    int pixelStride = pixelStride * xPeriod;
    int lineStride = lineStride * yPeriod;
    int[] offsets = bandOffsets;
    
    for (int b = 0; b < numBands; b++) {
      int[] data = intData[b];
      int lineOffset = offsets[b];
      
      int[] bin = new int[numBins[b]];
      double low = lowValue[b];
      double high = highValue[b];
      double bwidth = binWidth[b];
      
      for (int h = 0; h < height; h += yPeriod) {
        int pixelOffset = lineOffset;
        lineOffset += lineStride;
        
        for (int w = 0; w < width; w += xPeriod) {
          int d = data[pixelOffset];
          pixelOffset += pixelStride;
          
          if ((d >= low) && (d < high)) {
            int i = (int)((d - low) / bwidth);
            bin[i] += 1;
          }
        }
      }
      
      mergeBins(b, bin);
    }
  }
  


  private void countPixelsFloat(PixelAccessor accessor, Raster raster, Rectangle rect, int xPeriod, int yPeriod)
  {
    UnpackedImageData uid = accessor.getPixels(raster, rect, 4, false);
    

    float[][] floatData = uid.getFloatData();
    int pixelStride = pixelStride * xPeriod;
    int lineStride = lineStride * yPeriod;
    int[] offsets = bandOffsets;
    
    for (int b = 0; b < numBands; b++) {
      float[] data = floatData[b];
      int lineOffset = offsets[b];
      
      int[] bin = new int[numBins[b]];
      double low = lowValue[b];
      double high = highValue[b];
      double bwidth = binWidth[b];
      
      for (int h = 0; h < height; h += yPeriod) {
        int pixelOffset = lineOffset;
        lineOffset += lineStride;
        
        for (int w = 0; w < width; w += xPeriod) {
          float d = data[pixelOffset];
          pixelOffset += pixelStride;
          
          if ((d >= low) && (d < high)) {
            int i = (int)((d - low) / bwidth);
            bin[i] += 1;
          }
        }
      }
      
      mergeBins(b, bin);
    }
  }
  


  private void countPixelsDouble(PixelAccessor accessor, Raster raster, Rectangle rect, int xPeriod, int yPeriod)
  {
    UnpackedImageData uid = accessor.getPixels(raster, rect, 5, false);
    

    double[][] doubleData = uid.getDoubleData();
    int pixelStride = pixelStride * xPeriod;
    int lineStride = lineStride * yPeriod;
    int[] offsets = bandOffsets;
    
    for (int b = 0; b < numBands; b++) {
      double[] data = doubleData[b];
      int lineOffset = offsets[b];
      
      int[] bin = new int[numBins[b]];
      double low = lowValue[b];
      double high = highValue[b];
      double bwidth = binWidth[b];
      
      for (int h = 0; h < height; h += yPeriod) {
        int pixelOffset = lineOffset;
        lineOffset += lineStride;
        
        for (int w = 0; w < width; w += xPeriod) {
          double d = data[pixelOffset];
          pixelOffset += pixelStride;
          
          if ((d >= low) && (d < high)) {
            int i = (int)((d - low) / bwidth);
            bin[i] += 1;
          }
        }
      }
      
      mergeBins(b, bin);
    }
  }
  
  private int startPosition(int pos, int start, int Period)
  {
    int t = (pos - start) % Period;
    return t == 0 ? pos : pos + (Period - t);
  }
  
  private void mergeBins(int band, int[] bin)
  {
    getBins();
    
    synchronized (bins) {
      int[] b = bins[band];
      int length = b.length;
      
      for (int i = 0; i < length; i++) {
        b[i] += bin[i];
      }
    }
  }
  






















  public double[] getMoment(int moment, boolean isAbsolute, boolean isCentral)
  {
    if (moment < 1) {
      throw new IllegalArgumentException(JaiI18N.getString("Histogram6"));
    }
    


    if (((moment == 1) || (isCentral)) && (mean == null)) {
      getMean();
    }
    

    if ((moment == 1) && (!isAbsolute) && (!isCentral)) {
      return mean;
    }
    
    double[] moments = new double[numBands];
    

    if ((moment == 1) && (isCentral)) {
      for (int band = 0; band < numBands; band++) {
        moments[band] = 0.0D;
      }
    }
    else {
      getTotals();
      
      for (int band = 0; band < numBands; band++)
      {
        int[] counts = getBins(band);
        int nBins = numBins[band];
        double level = getLowValue(band);
        double bw = binWidth[band];
        double total = totals[band];
        

        double mmt = 0.0D;
        
        if (isCentral)
        {
          double mu = mean[band];
          
          if ((isAbsolute) && (moment % 2 == 0))
          {
            for (int b = 0; b < nBins; b++) {
              mmt += Math.pow(level - mu, moment) * counts[b] / total;
              
              level += bw;
            }
            
          } else {
            for (int b = 0; b < nBins; b++) {
              mmt += Math.abs(Math.pow(level - mu, moment)) * counts[b] / total;
              
              level += bw;
            }
          }
        } else if ((isAbsolute) && (moment % 2 != 0))
        {
          for (int b = 0; b < nBins; b++) {
            mmt += Math.abs(Math.pow(level, moment)) * counts[b] / total;
            
            level += bw;
          }
        }
        else {
          for (int b = 0; b < nBins; b++) {
            mmt += Math.pow(level, moment) * counts[b] / total;
            level += bw;
          }
        }
        

        moments[band] = mmt;
      }
    }
    
    return moments;
  }
  









  public double[] getStandardDeviation()
  {
    getMean();
    
    double[] variance = getMoment(2, false, false);
    
    double[] stdev = new double[numBands];
    
    for (int i = 0; i < variance.length; i++) {
      stdev[i] = Math.sqrt(variance[i] - mean[i] * mean[i]);
    }
    
    return stdev;
  }
  











  public double[] getEntropy()
  {
    getTotals();
    
    double log2 = Math.log(2.0D);
    
    double[] entropy = new double[numBands];
    
    for (int band = 0; band < numBands; band++) {
      int[] counts = getBins(band);
      int nBins = numBins[band];
      double total = totals[band];
      
      double H = 0.0D;
      
      for (int b = 0; b < nBins; b++) {
        double p = counts[b] / total;
        if (p != 0.0D) {
          H -= p * (Math.log(p) / log2);
        }
      }
      
      entropy[band] = H;
    }
    
    return entropy;
  }
  





















  public Histogram getSmoothed(boolean isWeighted, int k)
  {
    if (k < 0)
      throw new IllegalArgumentException(JaiI18N.getString("Histogram7"));
    if (k == 0) {
      return this;
    }
    

    Histogram smoothedHistogram = new Histogram(getNumBins(), getLowValue(), getHighValue());
    


    int[][] smoothedBins = smoothedHistogram.getBins();
    

    getTotals();
    

    double[] weights = null;
    if (isWeighted) {
      int numWeights = 2 * k + 1;
      double denom = numWeights * numWeights;
      weights = new double[numWeights];
      for (int i = 0; i <= k; i++) {
        weights[i] = ((i + 1) / denom);
      }
      for (int i = k + 1; i < numWeights; i++) {
        weights[i] = weights[(numWeights - 1 - i)];
      }
    }
    
    for (int band = 0; band < numBands; band++)
    {
      int[] counts = getBins(band);
      int[] smoothedCounts = smoothedBins[band];
      int nBins = smoothedHistogram.getNumBins(band);
      

      int sum = 0;
      
      if (isWeighted) {
        for (int b = 0; b < nBins; b++)
        {
          int min = Math.max(b - k, 0);
          int max = Math.min(b + k, nBins);
          

          int offset = k > b ? k - b : 0;
          

          double acc = 0.0D;
          double weightTotal = 0.0D;
          for (int i = min; i < max; i++) {
            double w = weights[(offset++)];
            acc += counts[i] * w;
            weightTotal += w;
          }
          

          smoothedCounts[b] = ((int)(acc / weightTotal + 0.5D));
          

          sum += smoothedCounts[b];
        }
      } else {
        for (int b = 0; b < nBins; b++)
        {
          int min = Math.max(b - k, 0);
          int max = Math.min(b + k, nBins);
          

          int acc = 0;
          for (int i = min; i < max; i++) {
            acc += counts[i];
          }
          

          smoothedCounts[b] = ((int)(acc / (max - min + 1) + 0.5D));
          


          sum += smoothedCounts[b];
        }
      }
      


      double factor = totals[band] / sum;
      for (int b = 0; b < nBins; b++) {
        smoothedCounts[b] = ((int)(smoothedCounts[b] * factor + 0.5D));
      }
    }
    
    return smoothedHistogram;
  }
  














  public Histogram getGaussianSmoothed(double standardDeviation)
  {
    if (standardDeviation < 0.0D)
      throw new IllegalArgumentException(JaiI18N.getString("Histogram8"));
    if (standardDeviation == 0.0D) {
      return this;
    }
    

    Histogram smoothedHistogram = new Histogram(getNumBins(), getLowValue(), getHighValue());
    


    int[][] smoothedBins = smoothedHistogram.getBins();
    

    getTotals();
    

    int numWeights = (int)(5.16D * standardDeviation + 0.5D);
    if (numWeights % 2 == 0) {
      numWeights++;
    }
    

    double[] weights = new double[numWeights];
    int m = numWeights / 2;
    double var = standardDeviation * standardDeviation;
    double gain = 1.0D / Math.sqrt(6.283185307179586D * var);
    double exp = -1.0D / (2.0D * var);
    for (int i = m; i < numWeights; i++) {
      double del = i - m; double 
        tmp168_167 = (gain * Math.exp(exp * del * del));weights[(numWeights - 1 - i)] = tmp168_167;weights[i] = tmp168_167;
    }
    
    for (int band = 0; band < numBands; band++)
    {
      int[] counts = getBins(band);
      int[] smoothedCounts = smoothedBins[band];
      int nBins = smoothedHistogram.getNumBins(band);
      

      int sum = 0;
      
      for (int b = 0; b < nBins; b++)
      {
        int min = Math.max(b - m, 0);
        int max = Math.min(b + m, nBins);
        

        int offset = m > b ? m - b : 0;
        

        double acc = 0.0D;
        double weightTotal = 0.0D;
        for (int i = min; i < max; i++) {
          double w = weights[(offset++)];
          acc += counts[i] * w;
          weightTotal += w;
        }
        

        smoothedCounts[b] = ((int)(acc / weightTotal + 0.5D));
        

        sum += smoothedCounts[b];
      }
      


      double factor = totals[band] / sum;
      for (int b = 0; b < nBins; b++) {
        smoothedCounts[b] = ((int)(smoothedCounts[b] * factor + 0.5D));
      }
    }
    
    return smoothedHistogram;
  }
  












  public double[] getPTileThreshold(double p)
  {
    if ((p <= 0.0D) || (p >= 1.0D)) {
      throw new IllegalArgumentException(JaiI18N.getString("Histogram9"));
    }
    
    double[] thresholds = new double[numBands];
    getTotals();
    
    for (int band = 0; band < numBands; band++)
    {
      int nBins = numBins[band];
      int[] counts = getBins(band);
      

      int totalCount = totals[band];
      


      int numBinWidths = 0;
      int count = counts[0];
      int idx = 0;
      while (count / totalCount < p) {
        numBinWidths++;
        count += counts[(++idx)];
      }
      

      thresholds[band] = (getLowValue(band) + numBinWidths * binWidth[band]);
    }
    
    return thresholds;
  }
  













  public double[] getModeThreshold(double power)
  {
    double[] thresholds = new double[numBands];
    getTotals();
    
    for (int band = 0; band < numBands; band++)
    {
      int nBins = numBins[band];
      int[] counts = getBins(band);
      

      int mode1 = 0;
      int mode1Count = counts[0];
      for (int b = 1; b < nBins; b++) {
        if (counts[b] > mode1Count) {
          mode1 = b;
          mode1Count = counts[b];
        }
      }
      

      int mode2 = -1;
      double mode2count = 0.0D;
      for (int b = 0; b < nBins; b++) {
        double d = counts[b] * Math.pow(Math.abs(b - mode1), power);
        if (d > mode2count) {
          mode2 = b;
          mode2count = d;
        }
      }
      

      int min = mode1;
      int minCount = counts[mode1];
      for (int b = mode1 + 1; b <= mode2; b++) {
        if (counts[b] < minCount) {
          min = b;
          minCount = counts[b];
        }
      }
      
      thresholds[band] = ((int)((mode1 + mode2) / 2.0D + 0.5D));
    }
    
    return thresholds;
  }
  












  public double[] getIterativeThreshold()
  {
    double[] thresholds = new double[numBands];
    getTotals();
    
    for (int band = 0; band < numBands; band++)
    {
      int nBins = numBins[band];
      int[] counts = getBins(band);
      double bw = binWidth[band];
      

      double threshold = 0.5D * (getLowValue(band) + getHighValue(band));
      double mid1 = 0.5D * (getLowValue(band) + threshold);
      double mid2 = 0.5D * (threshold + getHighValue(band));
      

      if (totals[band] != 0)
      {

        int countDown = 1000;
        do
        {
          thresholds[band] = threshold;
          

          double total = totals[band];
          

          double level = getLowValue(band);
          

          double mean1 = 0.0D;
          double mean2 = 0.0D;
          

          int count1 = 0;
          

          for (int b = 0; b < nBins; b++)
          {
            if (level <= threshold) {
              int c = counts[b];
              mean1 += c * level;
              count1 += c;
            } else {
              mean2 += counts[b] * level;
            }
            

            level += bw;
          }
          

          if (count1 != 0) {
            mean1 /= count1;
          }
          else {
            mean1 = mid1;
          }
          if (total != count1) {
            mean2 /= (total - count1);
          }
          else {
            mean2 = mid2;
          }
          

          threshold = 0.5D * (mean1 + mean2);
          
          if (Math.abs(threshold - thresholds[band]) <= 1.0E-6D) break; countDown--; } while (countDown > 0);
      }
      else {
        thresholds[band] = threshold;
      }
    }
    
    return thresholds;
  }
  







  public double[] getMaxVarianceThreshold()
  {
    double[] thresholds = new double[numBands];
    getTotals();
    getMean();
    double[] variance = getMoment(2, false, false);
    
    for (int band = 0; band < numBands; band++)
    {
      int nBins = numBins[band];
      int[] counts = getBins(band);
      double total = totals[band];
      double mBand = mean[band];
      double bw = binWidth[band];
      
      double prob0 = 0.0D;
      double mean0 = 0.0D;
      double lv = getLowValue(band);
      double level = lv;
      double maxRatio = -1.7976931348623157E308D;
      int maxIndex = 0;
      int runLength = 0;
      
      for (int t = 0; t < nBins; level += bw) {
        double p = counts[t] / total;
        prob0 += p;
        if (prob0 != 0.0D)
        {


          double m0 = (mean0 += p * level) / prob0;
          
          double prob1 = 1.0D - prob0;
          
          if (prob1 != 0.0D)
          {


            double m1 = (mBand - mean0) / prob1;
            
            double var0 = 0.0D;
            double g = lv;
            for (int b = 0; b <= t; g += bw) {
              double del = g - m0;
              var0 += del * del * counts[b];b++;
            }
            
            var0 /= total;
            
            double var1 = 0.0D;
            for (int b = t + 1; b < nBins; g += bw) {
              double del = g - m1;
              var1 += del * del * counts[b];b++;
            }
            
            var1 /= total;
            
            if ((var0 == 0.0D) && (var1 == 0.0D) && (m1 != 0.0D)) {
              maxIndex = (int)(((m0 + m1) / 2.0D - getLowValue(band)) / bw + 0.5D);
              
              runLength = 0;
              break;
            }
            
            if ((var0 / prob0 >= 0.5D) && (var1 / prob1 >= 0.5D))
            {


              double mdel = m0 - m1;
              double ratio = prob0 * prob1 * mdel * mdel / (var0 + var1);
              
              if (ratio > maxRatio) {
                maxRatio = ratio;
                maxIndex = t;
                runLength = 0;
              } else if (ratio == maxRatio) {
                runLength++;
              }
            }
          }
        }
        t++;
      }
      




















































      thresholds[band] = (getLowValue(band) + (maxIndex + runLength / 2.0D + 0.5D) * bw);
    }
    

    return thresholds;
  }
  













  public double[] getMaxEntropyThreshold()
  {
    double[] thresholds = new double[numBands];
    getTotals();
    
    double[] entropy = getEntropy();
    
    double log2 = Math.log(2.0D);
    
    for (int band = 0; band < numBands; band++)
    {
      int nBins = numBins[band];
      int[] counts = getBins(band);
      double total = totals[band];
      double H = entropy[band];
      
      double P1 = 0.0D;
      double H1 = 0.0D;
      
      double maxCriterion = -1.7976931348623157E308D;
      int maxIndex = 0;
      int runLength = 0;
      
      for (int t = 0; t < nBins; t++) {
        double p = counts[t] / total;
        
        if (p != 0.0D)
        {


          P1 += p;
          
          H1 -= p * Math.log(p) / log2;
          
          double max1 = 0.0D;
          for (int b = 0; b <= t; b++) {
            if (counts[b] > max1) {
              max1 = counts[b];
            }
          }
          
          if (max1 != 0.0D)
          {


            double max2 = 0.0D;
            for (int b = t + 1; b < nBins; b++) {
              if (counts[b] > max2) {
                max2 = counts[b];
              }
            }
            
            if (max2 != 0.0D)
            {


              double ratio = H1 / H;
              
              double criterion = ratio * Math.log(P1) / Math.log(max1 / total) + (1.0D - ratio) * Math.log(1.0D - P1) / Math.log(max2 / total);
              


              if (criterion > maxCriterion) {
                maxCriterion = criterion;
                maxIndex = t;
                runLength = 0;
              } else if (criterion == maxCriterion) {
                runLength++;
              }
            }
          } } }
      thresholds[band] = (getLowValue(band) + (maxIndex + runLength / 2.0D + 0.5D) * binWidth[band]);
    }
    

    return thresholds;
  }
  












  public double[] getMinErrorThreshold()
  {
    double[] thresholds = new double[numBands];
    getTotals();
    getMean();
    
    for (int band = 0; band < numBands; band++)
    {
      int nBins = numBins[band];
      int[] counts = getBins(band);
      double total = totals[band];
      double lv = getLowValue(band);
      double bw = binWidth[band];
      
      int total1 = 0;
      int total2 = totals[band];
      double sum1 = 0.0D;
      double sum2 = mean[band] * total;
      
      double level = lv;
      
      double minCriterion = Double.MAX_VALUE;
      int minIndex = 0;
      int runLength = 0;
      
      double J0 = Double.MAX_VALUE;
      double J1 = Double.MAX_VALUE;
      double J2 = Double.MAX_VALUE;
      int Jcount = 0;
      
      for (int t = 0; t < nBins; level += bw) {
        int c = counts[t];
        
        total1 += c;
        total2 -= c;
        
        double incr = level * c;
        sum1 += incr;
        sum2 -= incr;
        
        if ((total1 != 0) && (sum1 != 0.0D))
        {
          if ((total2 == 0) || (sum2 == 0.0D)) {
            break;
          }
          
          double m1 = sum1 / total1;
          double m2 = sum2 / total2;
          
          double s1 = 0.0D;
          double g = lv;
          for (int b = 0; b <= t; g += bw) {
            double v = g - m1;
            s1 += counts[b] * v * v;b++;
          }
          
          s1 /= total1;
          
          if (s1 >= 0.5D)
          {


            double s2 = 0.0D;
            
            for (int b = t + 1; b < nBins; g += bw) {
              double v = g - m2;
              s2 += counts[b] * v * v;b++;
            }
            
            s2 /= total2;
            
            if (s2 >= 0.5D)
            {


              double P1 = total1 / total;
              double P2 = total2 / total;
              double J = 1.0D + P1 * Math.log(s1) + P2 * Math.log(s2) - 2.0D * (P1 * Math.log(P1) + P2 * Math.log(P2));
              



              Jcount++;
              
              J0 = J1;
              J1 = J2;
              J2 = J;
              
              if ((Jcount >= 3) && 
                (J1 <= J0) && (J1 <= J2)) {
                if (J1 < minCriterion) {
                  minCriterion = J1;
                  minIndex = t - 1;
                  runLength = 0;
                } else if (J1 == minCriterion) {
                  runLength++;
                }
              }
            }
          }
        }
        t++;
      }
      


































































      thresholds[band] = (minIndex == 0 ? mean[band] : getLowValue(band) + (minIndex + runLength / 2.0D + 0.5D) * bw);
    }
    


    return thresholds;
  }
  




  public double[] getMinFuzzinessThreshold()
  {
    double[] thresholds = new double[numBands];
    getTotals();
    getMean();
    
    for (int band = 0; band < numBands; band++)
    {
      int nBins = numBins[band];
      int[] counts = getBins(band);
      double total = totals[band];
      
      double bw = binWidth[band];
      
      int total1 = 0;
      int total2 = totals[band];
      double sum1 = 0.0D;
      double sum2 = mean[band] * total;
      
      double lv = getLowValue(band);
      double level = lv;
      double C = getHighValue(band) - lv;
      
      double minCriterion = Double.MAX_VALUE;
      int minIndex = 0;
      int runLength = 0;
      
      for (int t = 0; t < nBins; level += bw) {
        int c = counts[t];
        
        total1 += c;
        total2 -= c;
        
        double incr = level * c;
        sum1 += incr;
        sum2 -= incr;
        
        if ((total1 != 0) && (total2 != 0))
        {


          double m1 = sum1 / total1;
          double m2 = sum2 / total2;
          
          double g = lv;
          double E = 0.0D;
          for (int b = 0; b < nBins; g += bw) {
            double u = b <= t ? 1.0D / (1.0D + Math.abs(g - m1) / C) : 1.0D / (1.0D + Math.abs(g - m2) / C);
            

            double v = 1.0D - u;
            E += (-u * Math.log(u) - v * Math.log(v)) * (counts[b] / total);b++;
          }
          




          if (E < minCriterion) {
            minCriterion = E;
            minIndex = t;
            runLength = 0;
          } else if (E == minCriterion) {
            runLength++;
          }
        }
        t++;
      }
      

































      thresholds[band] = (lv + (minIndex + runLength / 2.0D + 0.5D) * bw);
    }
    
    return thresholds;
  }
}
