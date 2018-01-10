package com.sun.media.jai.mlib;

import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import javax.media.jai.Histogram;
import javax.media.jai.StatisticsOpImage;





























final class MlibHistogramOpImage
  extends StatisticsOpImage
{
  private int[] numBins;
  private double[] lowValueFP;
  private double[] highValueFP;
  private int[] lowValue;
  private int[] highValue;
  private int numBands;
  private int[] bandIndexMap;
  private boolean reorderBands = false;
  









  public MlibHistogramOpImage(RenderedImage source, int xPeriod, int yPeriod, int[] numBins, double[] lowValueFP, double[] highValueFP)
  {
    super(source, null, source.getMinX(), source.getMinY(), xPeriod, yPeriod);
    





    numBands = sampleModel.getNumBands();
    

    this.numBins = new int[numBands];
    this.lowValueFP = new double[numBands];
    this.highValueFP = new double[numBands];
    

    for (int b = 0; b < numBands; b++) {
      this.numBins[b] = (numBins.length == 1 ? numBins[0] : numBins[b]);
      
      this.lowValueFP[b] = (lowValueFP.length == 1 ? lowValueFP[0] : lowValueFP[b]);
      
      this.highValueFP[b] = (highValueFP.length == 1 ? highValueFP[0] : highValueFP[b]);
    }
    





    lowValue = new int[this.lowValueFP.length];
    for (int i = 0; i < this.lowValueFP.length; i++) {
      lowValue[i] = ((int)Math.ceil(this.lowValueFP[i]));
    }
    




    highValue = new int[this.highValueFP.length];
    for (int i = 0; i < this.highValueFP.length; i++) {
      highValue[i] = ((int)Math.ceil(this.highValueFP[i]));
    }
    

    if (numBands > 1) {
      ComponentSampleModel csm = (ComponentSampleModel)sampleModel;
      
      TreeMap indexMap = new TreeMap();
      

      int[] indices = csm.getBankIndices();
      boolean checkBanks = false;
      for (int i = 1; i < numBands; i++) {
        if (indices[i] != indices[(i - 1)]) {
          checkBanks = true;
          break;
        }
      }
      

      if (checkBanks) {
        for (int i = 0; i < numBands; i++) {
          indexMap.put(new Integer(indices[i]), new Integer(i));
        }
        
        bandIndexMap = new int[numBands];
        Iterator bankIter = indexMap.keySet().iterator();
        int k = 0;
        while (bankIter.hasNext()) {
          int idx = ((Integer)indexMap.get(bankIter.next())).intValue();
          
          if (idx != k) {
            reorderBands = true;
          }
          bandIndexMap[(k++)] = idx;
        }
      }
      


      if (!reorderBands) {
        indexMap.clear();
        
        if (bandIndexMap == null) {
          bandIndexMap = new int[numBands];
        }
        
        int[] offsets = csm.getBandOffsets();
        for (int i = 0; i < numBands; i++) {
          indexMap.put(new Integer(offsets[i]), new Integer(i));
        }
        
        Iterator offsetIter = indexMap.keySet().iterator();
        int k = 0;
        while (offsetIter.hasNext()) {
          int idx = ((Integer)indexMap.get(offsetIter.next())).intValue();
          
          if (idx != k) {
            reorderBands = true;
          }
          bandIndexMap[(k++)] = idx;
        }
      }
    }
  }
  
  protected String[] getStatisticsNames() {
    String[] names = new String[1];
    names[0] = "histogram";
    return names;
  }
  
  protected Object createStatistics(String name) {
    if (name.equalsIgnoreCase("histogram")) {
      return new Histogram(numBins, lowValueFP, highValueFP);
    }
    return java.awt.Image.UndefinedProperty;
  }
  



  protected void accumulateStatistics(String name, Raster source, Object stats)
  {
    Histogram histogram = (Histogram)stats;
    int numBands = histogram.getNumBands();
    int[][] histJAI = histogram.getBins();
    

    Rectangle tileRect = source.getBounds();
    
    int[][] histo;
    int[][] histo;
    if ((!reorderBands) && (tileRect.equals(getBounds())))
    {
      histo = histJAI;
    }
    else {
      histo = new int[numBands][];
      for (int i = 0; i < numBands; i++) {
        histo[i] = new int[histogram.getNumBins(i)];
      }
    }
    

    int formatTag = MediaLibAccessor.findCompatibleTag(null, source);
    MediaLibAccessor accessor = new MediaLibAccessor(source, tileRect, formatTag);
    
    mediaLibImage[] img = accessor.getMediaLibImages();
    

    int offsetX = (xPeriod - (x - xStart) % xPeriod) % xPeriod;
    int offsetY = (yPeriod - (y - yStart) % yPeriod) % yPeriod;
    
    if (histo == histJAI) {
      synchronized (histogram)
      {
        com.sun.medialib.mlib.Image.Histogram2(histo, img[0], lowValue, highValue, offsetX, offsetY, xPeriod, yPeriod);
      }
    }
    

    com.sun.medialib.mlib.Image.Histogram2(histo, img[0], lowValue, highValue, offsetX, offsetY, xPeriod, yPeriod);
    


    synchronized (histogram) {
      for (int i = 0; i < numBands; i++) {
        int numBins = histo[i].length;
        int[] binsBandJAI = reorderBands ? histJAI[bandIndexMap[i]] : histJAI[i];
        
        int[] binsBand = histo[i];
        for (int j = 0; j < numBins; j++) {
          binsBandJAI[j] += binsBand[j];
        }
      }
    }
  }
}
