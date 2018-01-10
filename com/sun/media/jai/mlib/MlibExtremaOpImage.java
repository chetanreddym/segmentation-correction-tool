package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.ExtremaOpImage;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.ArrayList;
import javax.media.jai.ROI;








































final class MlibExtremaOpImage
  extends ExtremaOpImage
{
  private int[] minCount;
  private int[] maxCount;
  private int[][] minLocs;
  private int[][] maxLocs;
  
  public MlibExtremaOpImage(RenderedImage source, ROI roi, int xStart, int yStart, int xPeriod, int yPeriod, boolean saveLocations, int maxRuns)
  {
    super(source, roi, xStart, yStart, xPeriod, yPeriod, saveLocations, maxRuns);
  }
  


  protected void accumulateStatistics(String name, Raster source, Object stats)
  {
    int numBands = sampleModel.getNumBands();
    
    initializeState(source);
    


    Rectangle tileRect = source.getBounds();
    

    int offsetX = (xPeriod - (x - xStart) % xPeriod) % xPeriod;
    

    int offsetY = (yPeriod - (y - yStart) % yPeriod) % yPeriod;
    





    if ((offsetX >= width) || (offsetY >= height)) {
      return;
    }
    

    int formatTag = MediaLibAccessor.findCompatibleTag(null, source);
    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, tileRect, formatTag);
    


    mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
    


    if (!saveLocations) {
      switch (srcAccessor.getDataType()) {
      case 0: 
      case 1: 
      case 2: 
      case 3: 
        int[] imin = new int[numBands];
        int[] imax = new int[numBands];
        
        for (int i = 0; i < srcML.length; i++) {
          Image.Extrema2(imin, imax, srcML[i], offsetX, offsetY, xPeriod, yPeriod);
        }
        


        imin = srcAccessor.getIntParameters(0, imin);
        imax = srcAccessor.getIntParameters(0, imax);
        

        for (int i = 0; i < numBands; i++) {
          extrema[0][i] = Math.min(imin[i], extrema[0][i]);
          
          extrema[1][i] = Math.max(imax[i], extrema[1][i]);
        }
        
        break;
      
      case 4: 
      case 5: 
        double[] dmin = new double[numBands];
        double[] dmax = new double[numBands];
        for (int i = 0; i < srcML.length; i++) {
          Image.Extrema2_Fp(dmin, dmax, srcML[i], offsetX, offsetY, xPeriod, yPeriod);
        }
        


        dmin = srcAccessor.getDoubleParameters(0, dmin);
        dmax = srcAccessor.getDoubleParameters(0, dmax);
        

        for (int i = 0; i < numBands; i++) {
          extrema[0][i] = Math.min(dmin[i], extrema[0][i]);
          
          extrema[1][i] = Math.max(dmax[i], extrema[1][i]);
        }
      }
    }
    else
    {
      Rectangle loc = source.getBounds();
      int xOffset = x;
      int yOffset = y;
      
      switch (srcAccessor.getDataType()) {
      case 0: 
      case 1: 
      case 2: 
      case 3: 
        int[] imin = new int[numBands];
        int[] imax = new int[numBands];
        
        for (int i = 0; i < numBands; i++) {
          imin[i] = ((int)extrema[0][i]);
          imax[i] = ((int)extrema[1][i]);
        }
        
        for (int i = 0; i < srcML.length; i++) {
          Image.ExtremaLocations(imin, imax, srcML[i], offsetX, offsetY, xPeriod, yPeriod, saveLocations, maxRuns, minCount, maxCount, minLocs, maxLocs);
        }
        





        imin = srcAccessor.getIntParameters(0, imin);
        imax = srcAccessor.getIntParameters(0, imax);
        minCount = srcAccessor.getIntParameters(0, minCount);
        maxCount = srcAccessor.getIntParameters(0, maxCount);
        minLocs = srcAccessor.getIntArrayParameters(0, minLocs);
        maxLocs = srcAccessor.getIntArrayParameters(0, maxLocs);
        

        for (int i = 0; i < numBands; i++) {
          ArrayList minList = minLocations[i];
          ArrayList maxList = maxLocations[i];
          if (imin[i] < extrema[0][i]) {
            minList.clear();
            extrema[0][i] = imin[i];
          }
          
          int[] minBuf = minLocs[i];
          int[] maxBuf = maxLocs[i];
          
          int j = 0; for (int k = 0; j < minCount[i]; j++) {
            minList.add(new int[] { minBuf[(k++)] + xOffset, minBuf[(k++)] + yOffset, minBuf[(k++)] });
          }
          if (imax[i] > extrema[1][i]) {
            maxList.clear();
            extrema[1][i] = imax[i];
          }
          
          int j = 0; for (int k = 0; j < maxCount[i]; j++)
            maxList.add(new int[] { maxBuf[(k++)] + xOffset, maxBuf[(k++)] + yOffset, maxBuf[(k++)] });
        }
        break;
      
      case 4: 
      case 5: 
        double[] dmin = new double[numBands];
        double[] dmax = new double[numBands];
        
        for (int i = 0; i < numBands; i++) {
          dmin[i] = extrema[0][i];
          dmax[i] = extrema[1][i];
        }
        
        for (int i = 0; i < srcML.length; i++) {
          Image.ExtremaLocations_Fp(dmin, dmax, srcML[i], offsetX, offsetY, xPeriod, yPeriod, saveLocations, maxRuns, minCount, maxCount, minLocs, maxLocs);
        }
        





        dmin = srcAccessor.getDoubleParameters(0, dmin);
        dmax = srcAccessor.getDoubleParameters(0, dmax);
        minCount = srcAccessor.getIntParameters(0, minCount);
        maxCount = srcAccessor.getIntParameters(0, maxCount);
        minLocs = srcAccessor.getIntArrayParameters(0, minLocs);
        maxLocs = srcAccessor.getIntArrayParameters(0, maxLocs);
        

        for (int i = 0; i < numBands; i++) {
          ArrayList minList = minLocations[i];
          ArrayList maxList = maxLocations[i];
          if (dmin[i] < extrema[0][i]) {
            minList.clear();
            extrema[0][i] = dmin[i];
          }
          
          int[] minBuf = minLocs[i];
          int[] maxBuf = maxLocs[i];
          
          int j = 0; for (int k = 0; j < minCount[i]; j++) {
            minList.add(new int[] { minBuf[(k++)] + xOffset, minBuf[(k++)] + yOffset, minBuf[(k++)] });
          }
          if (dmax[i] > extrema[1][i]) {
            maxList.clear();
            extrema[1][i] = dmax[i];
          }
          
          int j = 0; for (int k = 0; j < maxCount[i]; j++) {
            maxList.add(new int[] { maxBuf[(k++)] + xOffset, maxBuf[(k++)] + yOffset, maxBuf[(k++)] });
          }
        }
      }
      
    }
    if (name.equalsIgnoreCase("extrema")) {
      double[][] ext = (double[][])stats;
      for (int i = 0; i < numBands; i++) {
        ext[0][i] = extrema[0][i];
        ext[1][i] = extrema[1][i];
      }
    } else if (name.equalsIgnoreCase("minimum")) {
      double[] min = (double[])stats;
      for (int i = 0; i < numBands; i++) {
        min[i] = extrema[0][i];
      }
    } else if (name.equalsIgnoreCase("maximum")) {
      double[] max = (double[])stats;
      for (int i = 0; i < numBands; i++) {
        max[i] = extrema[1][i];
      }
    } else if (name.equalsIgnoreCase("minLocations")) {
      ArrayList[] minLoc = (ArrayList[])stats;
      for (int i = 0; i < numBands; i++) {
        minLoc[i] = minLocations[i];
      }
    } else if (name.equalsIgnoreCase("maxLocations")) {
      ArrayList[] maxLoc = (ArrayList[])stats;
      for (int i = 0; i < numBands; i++)
        maxLoc[i] = maxLocations[i];
    }
  }
  
  protected void initializeState(Raster source) {
    if (extrema == null) {
      int numBands = sampleModel.getNumBands();
      minCount = new int[numBands];
      maxCount = new int[numBands];
      
      minLocs = new int[numBands][];
      maxLocs = new int[numBands][];
      
      int size = (getTileWidth() + 1) / 2 * getTileHeight();
      
      for (int i = 0; i < numBands; i++) {
        minLocs[i] = new int[size];
        maxLocs[i] = new int[size];
      }
      
      super.initializeState(source);
    }
  }
}
