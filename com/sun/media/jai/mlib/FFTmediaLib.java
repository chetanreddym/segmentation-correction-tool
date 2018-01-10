package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.FFT;
import com.sun.media.jai.util.MathJAI;
import com.sun.medialib.mlib.Image;























public class FFTmediaLib
  extends FFT
{
  private boolean specialUnitaryScaling = false;
  

  private static final double SQUARE_ROOT_OF_2 = Math.sqrt(2.0D);
  








  public FFTmediaLib(boolean negatedExponent, Integer scaleType, int length)
  {
    super(negatedExponent, scaleType, length);
  }
  





  public void setLength(int length)
  {
    if ((lengthIsSet) && (length == this.length)) {
      return;
    }
    

    if (!MathJAI.isPositivePowerOf2(length)) {
      throw new RuntimeException(JaiI18N.getString("FFTmediaLib0"));
    }
    

    this.length = length;
    

    if ((!lengthIsSet) || (length != real.length)) {
      real = new double[length];
      imag = new double[length];
    }
    

    lengthIsSet = true;
    

    if (scaleType == SCALING_UNITARY)
    {

      int exponent = 0;
      int powerOfTwo = 1;
      while (powerOfTwo < length) {
        powerOfTwo <<= 1;
        exponent++;
      }
      

      specialUnitaryScaling = (exponent % 2 != 0);
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
          for (int i = 0; i < length; i++)
          {

            realFloat[offsetReal] = ((float)real[i]);
            imagFloat[offsetReal] = ((float)imag[i]);
            offsetReal += strideReal;
          }
        } else {
          for (int i = 0; i < length; i++) {
            realFloat[offsetReal] = ((float)real[i]);
            imagFloat[offsetImag] = ((float)imag[i]);
            offsetReal += strideReal;
            offsetImag += strideImag;
          }
        }
      } else {
        for (int i = 0; i < length; i++) {
          realFloat[offsetReal] = ((float)real[i]);
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
            realDouble[offsetReal] = real[i];
            imagDouble[offsetReal] = imag[i];
            offsetReal += strideReal;
          }
        } else {
          for (int i = 0; i < length; i++) {
            realDouble[offsetReal] = real[i];
            imagDouble[offsetImag] = imag[i];
            offsetReal += strideReal;
            offsetImag += strideImag;
          }
        }
      } else {
        for (int i = 0; i < length; i++) {
          realDouble[offsetReal] = real[i];
          offsetReal += strideReal;
        }
      }
      
      break;
    




    default: 
      throw new RuntimeException(dataType + JaiI18N.getString("FFTmediaLib1"));
    }
    
  }
  

  public void transform()
  {
    if (exponentSign < 0) {
      if (scaleType == SCALING_NONE) {
        Image.FFT_1(real, imag);
      } else if (scaleType == SCALING_UNITARY) {
        Image.FFT_3(real, imag);
        
        if (specialUnitaryScaling)
        {



          for (int i = 0; i < length; i++) {
            real[i] *= SQUARE_ROOT_OF_2;
            imag[i] *= SQUARE_ROOT_OF_2;
          }
        }
      } else if (scaleType == SCALING_DIMENSIONS) {
        Image.FFT_2(real, imag);
      }
    }
    else if (scaleType == SCALING_NONE) {
      Image.IFFT_2(real, imag);
    } else if (scaleType == SCALING_UNITARY) {
      Image.IFFT_3(real, imag);
      
      if (specialUnitaryScaling)
      {



        for (int i = 0; i < length; i++) {
          real[i] /= SQUARE_ROOT_OF_2;
          imag[i] /= SQUARE_ROOT_OF_2;
        }
      }
    } else if (scaleType == SCALING_DIMENSIONS) {
      Image.IFFT_1(real, imag);
    }
  }
}
