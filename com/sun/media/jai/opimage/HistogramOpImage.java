package com.sun.media.jai.opimage;

import java.awt.Image;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import javax.media.jai.Histogram;
import javax.media.jai.ROI;
import javax.media.jai.StatisticsOpImage;




























final class HistogramOpImage
  extends StatisticsOpImage
{
  private int[] numBins;
  private double[] lowValue;
  private double[] highValue;
  private int numBands;
  
  private final boolean tileIntersectsROI(int tileX, int tileY)
  {
    if (roi == null) {
      return true;
    }
    return roi.intersects(tileXToX(tileX), tileYToY(tileY), tileWidth, tileHeight);
  }
  














  public HistogramOpImage(RenderedImage source, ROI roi, int xStart, int yStart, int xPeriod, int yPeriod, int[] numBins, double[] lowValue, double[] highValue)
  {
    super(source, roi, xStart, yStart, xPeriod, yPeriod);
    
    numBands = source.getSampleModel().getNumBands();
    
    this.numBins = new int[numBands];
    this.lowValue = new double[numBands];
    this.highValue = new double[numBands];
    
    for (int b = 0; b < numBands; b++) {
      this.numBins[b] = (numBins.length == 1 ? numBins[0] : numBins[b]);
      
      this.lowValue[b] = (lowValue.length == 1 ? lowValue[0] : lowValue[b]);
      
      this.highValue[b] = (highValue.length == 1 ? highValue[0] : highValue[b]);
    }
  }
  
  protected String[] getStatisticsNames()
  {
    String[] names = new String[1];
    names[0] = "histogram";
    return names;
  }
  
  protected Object createStatistics(String name) {
    if (name.equalsIgnoreCase("histogram")) {
      return new Histogram(numBins, lowValue, highValue);
    }
    return Image.UndefinedProperty;
  }
  


  protected void accumulateStatistics(String name, Raster source, Object stats)
  {
    Histogram histogram = (Histogram)stats;
    histogram.countPixels(source, roi, xStart, yStart, xPeriod, yPeriod);
  }
}
