package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.Histogram;
import javax.media.jai.ImageLayout;





























public class MatchCDFCRIF
  extends CRIFImpl
{
  private static void createHistogramMap(float[] CDFin, float[] CDFout, double lowValue, double binWidth, int numBins, float[] abscissa, float[] ordinate)
  {
    double x = lowValue;
    int j = 0;
    int jMax = numBins - 1;
    

    for (int i = 0; i < numBins; i++)
    {

      float w = CDFin[i];
      while ((CDFout[j] < w) && (j < jMax)) { j++;
      }
      
      abscissa[i] = ((float)x);
      ordinate[i] = ((float)(j * binWidth));
      

      x += binWidth;
    }
  }
  










  private static float[][][] createSpecificationMap(Histogram histIn, float[][] CDFOut)
  {
    int numBands = histIn.getNumBands();
    float[][][] bp = new float[numBands][][];
    

    float[] CDFin = null;
    for (int band = 0; band < numBands; band++)
    {
      int numBins = histIn.getNumBins(band);
      bp[band] = new float[2][];
      bp[band][0] = new float[numBins];
      bp[band][1] = new float[numBins];
      

      int[] binsIn = histIn.getBins(band);
      long binTotalIn = binsIn[0];
      for (int i = 1; i < numBins; i++) {
        binTotalIn += binsIn[i];
      }
      

      if ((CDFin == null) || (CDFin.length < numBins)) {
        CDFin = new float[numBins];
      }
      


      CDFin[0] = (binsIn[0] / (float)binTotalIn);
      for (int i = 1; i < numBins; i++) {
        CDFin[i] = (CDFin[(i - 1)] + binsIn[i] / (float)binTotalIn);
      }
      

      double binWidth = (histIn.getHighValue(band) - histIn.getLowValue(band)) / numBins;
      
      createHistogramMap(CDFin, CDFOut.length > 1 ? CDFOut[band] : CDFOut[0], histIn.getLowValue(band), binWidth, numBins, bp[band][0], bp[band][1]);
    }
    



    return bp;
  }
  
  public MatchCDFCRIF()
  {
    super("matchcdf");
  }
  








  public RenderedImage create(ParameterBlock args, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    


    RenderedImage src = args.getRenderedSource(0);
    Histogram hist = (Histogram)src.getProperty("histogram");
    float[][] CDF = (float[][])args.getObjectParameter(0);
    float[][][] bp = createSpecificationMap(hist, CDF);
    
    return new PiecewiseOpImage(src, renderHints, layout, bp);
  }
}
