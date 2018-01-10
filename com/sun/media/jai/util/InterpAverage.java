package com.sun.media.jai.util;

import javax.media.jai.Interpolation;




























public class InterpAverage
  extends Interpolation
{
  public InterpAverage(int blockX, int blockY)
  {
    super(blockX, blockY, (blockX - 1) / 2, blockX - 1 - (blockX - 1) / 2, (blockY - 1) / 2, blockY - 1 - (blockY - 1) / 2, 32, 32);
    



    if ((blockX <= 0) || (blockY <= 0)) {
      throw new IllegalArgumentException("blockX <= 0 || blockY <= 0");
    }
  }
  



  public int interpolateH(int[] samples, int xfrac)
  {
    int numSamples = samples.length;
    double total = 0.0D;
    for (int i = 0; i < numSamples; i++) {
      total += samples[i] / numSamples;
    }
    return (int)(total + 0.5D);
  }
  



  public float interpolateH(float[] samples, float xfrac)
  {
    int numSamples = samples.length;
    float total = 0.0F;
    for (int i = 0; i < numSamples; i++) {
      total += samples[i] / numSamples;
    }
    return total;
  }
  



  public double interpolateH(double[] samples, float xfrac)
  {
    int numSamples = samples.length;
    double total = 0.0D;
    for (int i = 0; i < numSamples; i++) {
      total += samples[i] / numSamples;
    }
    return total;
  }
}
