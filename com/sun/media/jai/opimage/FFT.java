package com.sun.media.jai.opimage;

import com.sun.media.jai.util.MathJAI;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import javax.media.jai.operator.DFTDescriptor;
import javax.media.jai.operator.DFTScalingType;



















public class FFT
{
  public static final int SCALING_NONE = DFTDescriptor.SCALING_NONE.getValue();
  





  public static final int SCALING_UNITARY = DFTDescriptor.SCALING_UNITARY.getValue();
  





  public static final int SCALING_DIMENSIONS = DFTDescriptor.SCALING_DIMENSIONS.getValue();
  


  protected boolean lengthIsSet = false;
  


  protected int exponentSign;
  


  protected int scaleType;
  


  protected int length;
  


  private int nbits;
  


  private int[] index;
  

  private double scaleFactor;
  

  private double[] wr;
  

  private double[] wi;
  

  private double[] wrFCT;
  

  private double[] wiFCT;
  

  protected double[] real;
  

  protected double[] imag;
  


  public FFT(boolean negatedExponent, Integer scaleType, int length)
  {
    exponentSign = (negatedExponent ? -1 : 1);
    

    this.scaleType = scaleType.intValue();
    

    setLength(length);
  }
  





  public void setLength(int length)
  {
    if ((lengthIsSet) && (length == this.length)) {
      return;
    }
    

    if (!MathJAI.isPositivePowerOf2(length)) {
      throw new RuntimeException(JaiI18N.getString("FFT0"));
    }
    

    this.length = length;
    

    if (scaleType == SCALING_NONE) {
      scaleFactor = 1.0D;
    } else if (scaleType == SCALING_UNITARY) {
      scaleFactor = (1.0D / Math.sqrt(length));
    } else if (scaleType == SCALING_DIMENSIONS) {
      scaleFactor = (1.0D / length);
    }
    else
    {
      throw new RuntimeException(JaiI18N.getString("FFT1"));
    }
    

    int power = 1;
    nbits = 0;
    while (power < length) {
      nbits += 1;
      power <<= 1;
    }
    

    initBitReversalLUT();
    

    calculateCoefficientLUTs();
    

    if ((!lengthIsSet) || (length > real.length)) {
      real = new double[length];
      imag = new double[length];
    }
    

    lengthIsSet = true;
  }
  



  private void initBitReversalLUT()
  {
    index = new int[length];
    for (int i = 0; i < length; i++) {
      int l = i;
      int power = length >> 1;
      int irev = 0;
      for (int k = 0; k < nbits; k++) {
        int j = l & 0x1;
        if (j != 0) {
          irev += power;
        }
        l >>= 1;
        power >>= 1;
        
        index[i] = irev;
      }
    }
  }
  


  private void calculateCoefficientLUTs()
  {
    wr = new double[nbits];
    wi = new double[nbits];
    
    int inode = 1;
    double cons = exponentSign * 3.141592653589793D;
    
    for (int bit = 0; bit < nbits; bit++) {
      wr[bit] = Math.cos(cons / inode);
      wi[bit] = Math.sin(cons / inode);
      inode *= 2;
    }
  }
  


  private void calculateFCTLUTs()
  {
    wrFCT = new double[length];
    wiFCT = new double[length];
    
    for (int i = 0; i < length; i++) {
      double factor = i == 0 ? Math.sqrt(1.0D / length) : Math.sqrt(2.0D / length);
      

      double freq = 3.141592653589793D * i / (2.0D * length);
      wrFCT[i] = (factor * Math.cos(freq));
      wiFCT[i] = (factor * Math.sin(freq));
    }
  }
  

















  public void setData(int dataType, Object realArg, int offsetReal, int strideReal, Object imagArg, int offsetImag, int strideImag, int count)
  {
    switch (dataType)
    {
    case 4: 
      float[] realFloat = (float[])realArg;
      if (imagArg != null) {
        float[] imagFloat = (float[])imagArg;
        if ((offsetReal == offsetImag) && (strideReal == strideImag))
        {
          for (int i = 0; i < count; i++) {
            real[i] = realFloat[offsetReal];
            imag[i] = imagFloat[offsetReal];
            offsetReal += strideReal;
          }
        } else {
          for (int i = 0; i < count; i++) {
            real[i] = realFloat[offsetReal];
            imag[i] = imagFloat[offsetImag];
            offsetReal += strideReal;
            offsetImag += strideImag;
          }
        }
      } else {
        for (int i = 0; i < count; i++) {
          real[i] = realFloat[offsetReal];
          offsetReal += strideReal;
        }
      }
      
      break;
    
    case 5: 
      double[] realDouble = (double[])realArg;
      if ((strideReal == 1) && (strideImag == 1)) {
        System.arraycopy(realDouble, offsetReal, real, 0, count);
        
        if (imagArg != null) {
          System.arraycopy((double[])imagArg, offsetImag, imag, 0, count);
        }
      }
      else if (imagArg != null) {
        double[] imagDouble = (double[])imagArg;
        if ((offsetReal == offsetImag) && (strideReal == strideImag))
        {
          for (int i = 0; i < count; i++) {
            real[i] = realDouble[offsetReal];
            imag[i] = imagDouble[offsetReal];
            offsetReal += strideReal;
          }
        } else {
          for (int i = 0; i < count; i++) {
            real[i] = realDouble[offsetReal];
            imag[i] = imagDouble[offsetImag];
            offsetReal += strideReal;
            offsetImag += strideImag;
          }
        }
      } else {
        for (int i = 0; i < count; i++) {
          real[i] = realDouble[offsetReal];
          offsetReal += strideReal;
        }
      }
      
      break;
    




    default: 
      throw new RuntimeException(dataType + JaiI18N.getString("FFT2"));
    }
    
    
    if (count < length) {
      Arrays.fill(real, count, length, 0.0D);
      if (imagArg != null) {
        Arrays.fill(imag, count, length, 0.0D);
      }
    }
    
    if (imagArg == null) {
      Arrays.fill(imag, 0, length, 0.0D);
    }
  }
  















  public void getData(int dataType, Object realArg, int offsetReal, int strideReal, Object imagArg, int offsetImag, int strideImag)
  {
    switch (dataType)
    {
    case 4: 
      float[] realFloat = (float[])realArg;
      if (imagArg != null) {
        float[] imagFloat = (float[])imagArg;
        if ((offsetReal == offsetImag) && (strideReal == strideImag))
        {
          for (int i = 0; i < length; i++) {
            int idx = index[i];
            realFloat[offsetReal] = ((float)real[idx]);
            imagFloat[offsetReal] = ((float)imag[idx]);
            offsetReal += strideReal;
          }
        } else {
          for (int i = 0; i < length; i++) {
            int idx = index[i];
            realFloat[offsetReal] = ((float)real[idx]);
            imagFloat[offsetImag] = ((float)imag[idx]);
            offsetReal += strideReal;
            offsetImag += strideImag;
          }
        }
      } else {
        for (int i = 0; i < length; i++) {
          realFloat[offsetReal] = ((float)real[index[i]]);
          offsetReal += strideReal;
        }
      }
      
      break;
    
    case 5: 
      double[] realDouble = (double[])realArg;
      if (imagArg != null) {
        double[] imagDouble = (double[])imagArg;
        if ((offsetReal == offsetImag) && (strideReal == strideImag))
        {
          for (int i = 0; i < length; i++) {
            int idx = index[i];
            realDouble[offsetReal] = real[idx];
            imagDouble[offsetReal] = imag[idx];
            offsetReal += strideReal;
          }
        } else {
          for (int i = 0; i < length; i++) {
            int idx = index[i];
            realDouble[offsetReal] = real[idx];
            imagDouble[offsetImag] = imag[idx];
            offsetReal += strideReal;
            offsetImag += strideImag;
          }
        }
      } else {
        for (int i = 0; i < length; i++) {
          realDouble[offsetReal] = real[index[i]];
          offsetReal += strideReal;
        }
      }
      
      break;
    




    default: 
      throw new RuntimeException(dataType + JaiI18N.getString("FFT2"));
    }
    
  }
  













  public void setFCTData(int dataType, Object data, int offset, int stride, int count)
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
      throw new RuntimeException(dataType + JaiI18N.getString("FFT2"));
    }
    
    
    Arrays.fill(imag, 0, length, 0.0D);
  }
  












  public void getFCTData(int dataType, Object data, int offset, int stride)
  {
    if ((wrFCT == null) || (wrFCT.length != length)) {
      calculateFCTLUTs();
    }
    
    switch (dataType)
    {
    case 4: 
      float[] realFloat = (float[])data;
      for (int i = 0; i < length; i++) {
        int idx = index[i];
        realFloat[offset] = ((float)(wrFCT[i] * real[idx] + wiFCT[i] * imag[idx]));
        
        offset += stride;
      }
      
      break;
    
    case 5: 
      double[] realDouble = (double[])data;
      for (int i = 0; i < length; i++) {
        int idx = index[i];
        realDouble[offset] = (wrFCT[i] * real[idx] + wiFCT[i] * imag[idx]);
        
        offset += stride;
      }
      
      break;
    




    default: 
      throw new RuntimeException(dataType + JaiI18N.getString("FFT2"));
    }
    
  }
  












  public void setIFCTData(int dataType, Object data, int offset, int stride, int count)
  {
    if ((wrFCT == null) || (wrFCT.length != length)) {
      calculateFCTLUTs();
    }
    

    switch (dataType)
    {
    case 4: 
      float[] realFloat = (float[])data;
      for (int i = 0; i < count; i++) {
        float r = realFloat[offset];
        real[i] = (r * wrFCT[i]);
        imag[i] = (r * wiFCT[i]);
        offset += stride;
      }
      
      break;
    
    case 5: 
      double[] realDouble = (double[])data;
      for (int i = 0; i < count; i++) {
        double r = realDouble[offset];
        real[i] = (r * wrFCT[i]);
        imag[i] = (r * wiFCT[i]);
        offset += stride;
      }
      
      break;
    




    default: 
      throw new RuntimeException(dataType + JaiI18N.getString("FFT2"));
    }
    
    
    if (count < length) {
      Arrays.fill(real, count, length, 0.0D);
      Arrays.fill(imag, count, length, 0.0D);
    }
  }
  












  public void getIFCTData(int dataType, Object data, int offset, int stride)
  {
    switch (dataType)
    {
    case 4: 
      float[] realFloat = (float[])data;
      int k = length - 1;
      for (int i = 0; i < k; i++) {
        realFloat[offset] = ((float)real[index[i]]);
        offset += stride;
        realFloat[offset] = ((float)real[index[(k--)]]);
        offset += stride;
      }
      
      break;
    
    case 5: 
      double[] realDouble = (double[])data;
      int k = length - 1;
      for (int i = 0; i < k; i++) {
        realDouble[offset] = ((float)real[index[i]]);
        offset += stride;
        realDouble[offset] = ((float)real[index[(k--)]]);
        offset += stride;
      }
      
      break;
    




    default: 
      throw new RuntimeException(dataType + JaiI18N.getString("FFT2"));
    }
    
  }
  



  public void transform()
  {
    Integer i18n = new Integer(length);
    NumberFormat numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault());
    
    if ((real.length < length) || (imag.length < length)) {
      throw new RuntimeException(numberFormatter.format(i18n) + JaiI18N.getString("FFT3"));
    }
    
    int inode = 1;
    
    for (int l = 0; l < nbits; l++) {
      double cosp = 1.0D;
      double sinp = 0.0D;
      int ipair = 2 * inode;
      for (int k = 0; k < inode; k++) {
        for (int i = k; i < length; i += ipair) {
          int j = i + inode;
          int iIndex = index[i];
          int jIndex = index[j];
          double rtemp = real[jIndex] * cosp - imag[jIndex] * sinp;
          double itemp = imag[jIndex] * cosp + real[jIndex] * sinp;
          real[iIndex] -= rtemp;
          imag[iIndex] -= itemp;
          real[iIndex] += rtemp;
          imag[iIndex] += itemp;
        }
        double costmp = cosp;
        cosp = cosp * wr[l] - sinp * wi[l];
        sinp = costmp * wi[l] + sinp * wr[l];
      }
      inode *= 2;
    }
    
    if (scaleFactor != 1.0D) {
      for (int i = 0; i < length; i++) {
        real[i] *= scaleFactor;
        imag[i] *= scaleFactor;
      }
    }
  }
}
