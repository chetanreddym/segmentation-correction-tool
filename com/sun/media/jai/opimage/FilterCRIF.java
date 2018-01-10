package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;
import javax.media.jai.CRIFImpl;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
























final class FilterCRIF
  extends CRIFImpl
{
  private static final int STEPSIZE = 5;
  
  private static final KernelJAI createKernel(double p)
  {
    int STEPSIZE = 5;
    
    if (p == 0.0D) {
      return null;
    }
    
    double pAbs = Math.abs(p);
    int idx = (int)pAbs / STEPSIZE;
    double frac = 10.0F / STEPSIZE * (pAbs - idx * STEPSIZE);
    double blend = 0.010101010101010102D * (Math.pow(10.0D, 0.2D * frac) - 1.0D);
    
    int size;
    
    float[] data;
    if (idx * STEPSIZE == pAbs)
    {

      int size = 2 * idx + 1;
      float[] data = new float[size * size];
      float val = 1.0F / (size * size);
      Arrays.fill(data, val);
    }
    else {
      int size1 = 2 * idx + 1;
      size = size1 + 2;
      data = new float[size * size];
      float val1 = 1.0F / (size1 * size1) * (1.0F - (float)blend);
      int row = size;
      for (int j = 1; j < size - 1; j++) {
        for (int i = 1; i < size - 1; i++) {
          data[(row + i)] = val1;
        }
        row += size;
      }
      float val2 = 1.0F / (size * size) * (float)blend;
      for (int i = 0; i < data.length; i++) {
        data[i] += val2;
      }
    }
    

    if (p > 0.0D)
    {
      for (int i = 0; i < data.length; i++) {
        int tmp262_260 = i; float[] tmp262_258 = data;tmp262_258[tmp262_260] = ((float)(tmp262_258[tmp262_260] * -1.0D));
      }
      data[(data.length / 2)] += 2.0F;
    }
    
    return new KernelJAI(size, size, data);
  }
  



  public FilterCRIF() {}
  



  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    KernelJAI kernel = createKernel(paramBlock.getFloatParameter(0));
    
    return kernel == null ? paramBlock.getRenderedSource(0) : JAI.create("convolve", paramBlock.getRenderedSource(0), kernel);
  }
}
