package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.FCT;
import com.sun.media.jai.util.MathJAI;
import com.sun.medialib.mlib.Image;
import java.util.Arrays;





















public class FCTmediaLib
  extends FCT
{
  private int length;
  private boolean lengthIsSet = false;
  


  private double[] wr;
  


  private double[] wi;
  

  protected double[] real;
  

  protected double[] imag;
  


  public FCTmediaLib(boolean isForwardTransform, int length)
  {
    this.isForwardTransform = isForwardTransform;
    setLength(length);
  }
  







  public void setLength(int length)
  {
    if ((lengthIsSet) && (length == this.length)) {
      return;
    }
    

    if (!MathJAI.isPositivePowerOf2(length)) {
      throw new RuntimeException(JaiI18N.getString("FCTmediaLib0"));
    }
    

    this.length = length;
    

    if ((real == null) || (length != real.length)) {
      real = new double[length];
      imag = new double[length];
    }
    

    calculateFCTLUTs();
    

    lengthIsSet = true;
  }
  


  private void calculateFCTLUTs()
  {
    wr = new double[length];
    wi = new double[length];
    
    for (int i = 0; i < length; i++) {
      double factor = i == 0 ? Math.sqrt(1.0D / length) : Math.sqrt(2.0D / length);
      

      double freq = 3.141592653589793D * i / (2.0D * length);
      wr[i] = (factor * Math.cos(freq));
      wi[i] = (factor * Math.sin(freq));
    }
  }
  












  public void setData(int dataType, Object data, int offset, int stride, int count)
  {
    if (isForwardTransform) {
      setFCTData(dataType, data, offset, stride, count);
    } else {
      setIFCTData(dataType, data, offset, stride, count);
    }
  }
  










  public void getData(int dataType, Object data, int offset, int stride)
  {
    if (isForwardTransform) {
      getFCTData(dataType, data, offset, stride);
    } else {
      getIFCTData(dataType, data, offset, stride);
    }
  }
  













  private void setFCTData(int dataType, Object data, int offset, int stride, int count)
  {
    switch (dataType)
    {
    case 4: 
      float[] realFloat = (float[])data;
      for (int i = 0; i < count; i++) {
        imag[i] = realFloat[offset];
        offset += stride;
      }
      for (int i = count; i < length; i++) {
        imag[i] = 0.0D;
      }
      int k = length - 1;
      int j = 0;
      for (int i = 0; i < k; i++) {
        real[i] = imag[(j++)];
        real[(k--)] = imag[(j++)];
      }
      
      break;
    
    case 5: 
      double[] realDouble = (double[])data;
      for (int i = 0; i < count; i++) {
        imag[i] = realDouble[offset];
        offset += stride;
      }
      for (int i = count; i < length; i++) {
        imag[i] = 0.0D;
      }
      int k = length - 1;
      int j = 0;
      for (int i = 0; i < k; i++) {
        real[i] = imag[(j++)];
        real[(k--)] = imag[(j++)];
      }
      
      break;
    




    default: 
      throw new RuntimeException(dataType + JaiI18N.getString("FCTmediaLib1"));
    }
    
    
    Arrays.fill(imag, 0, length, 0.0D);
  }
  











  private void getFCTData(int dataType, Object data, int offset, int stride)
  {
    switch (dataType)
    {
    case 4: 
      float[] realFloat = (float[])data;
      for (int i = 0; i < length; i++) {
        realFloat[offset] = ((float)(wr[i] * real[i] + wi[i] * imag[i]));
        
        offset += stride;
      }
      
      break;
    
    case 5: 
      double[] realDouble = (double[])data;
      for (int i = 0; i < length; i++) {
        realDouble[offset] = (wr[i] * real[i] + wi[i] * imag[i]);
        
        offset += stride;
      }
      
      break;
    




    default: 
      throw new RuntimeException(dataType + JaiI18N.getString("FCTmediaLib1"));
    }
    
  }
  












  private void setIFCTData(int dataType, Object data, int offset, int stride, int count)
  {
    switch (dataType)
    {
    case 4: 
      float[] realFloat = (float[])data;
      for (int i = 0; i < count; i++) {
        float r = realFloat[offset];
        real[i] = (r * wr[i]);
        imag[i] = (r * wi[i]);
        offset += stride;
      }
      
      break;
    
    case 5: 
      double[] realDouble = (double[])data;
      for (int i = 0; i < count; i++) {
        double r = realDouble[offset];
        real[i] = (r * wr[i]);
        imag[i] = (r * wi[i]);
        offset += stride;
      }
      
      break;
    




    default: 
      throw new RuntimeException(dataType + JaiI18N.getString("FCTmediaLib1"));
    }
    
    
    if (count < length) {
      Arrays.fill(real, count, length, 0.0D);
      Arrays.fill(imag, count, length, 0.0D);
    }
  }
  











  private void getIFCTData(int dataType, Object data, int offset, int stride)
  {
    switch (dataType)
    {
    case 4: 
      float[] realFloat = (float[])data;
      int k = length - 1;
      for (int i = 0; i < k; i++) {
        realFloat[offset] = ((float)real[i]);
        offset += stride;
        realFloat[offset] = ((float)real[(k--)]);
        offset += stride;
      }
      
      break;
    
    case 5: 
      double[] realDouble = (double[])data;
      int k = length - 1;
      for (int i = 0; i < k; i++) {
        realDouble[offset] = ((float)real[i]);
        offset += stride;
        realDouble[offset] = ((float)real[(k--)]);
        offset += stride;
      }
      
      break;
    




    default: 
      throw new RuntimeException(dataType + JaiI18N.getString("FCTmediaLib1"));
    }
    
  }
  

  public void transform()
  {
    if (isForwardTransform) {
      Image.FFT_1(real, imag);
    } else {
      Image.IFFT_2(real, imag);
    }
  }
}
