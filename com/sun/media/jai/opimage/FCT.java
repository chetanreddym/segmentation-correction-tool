package com.sun.media.jai.opimage;

import javax.media.jai.operator.DFTDescriptor;
import javax.media.jai.operator.DFTScalingType;























public class FCT
{
  protected boolean isForwardTransform;
  private FFT fft = null;
  



  public FCT() {}
  



  public FCT(boolean isForwardTransform, int length)
  {
    this.isForwardTransform = isForwardTransform;
    

    fft = new FFT(isForwardTransform, new Integer(DFTDescriptor.SCALING_NONE.getValue()), length);
  }
  






  public void setLength(int length)
  {
    fft.setLength(length);
  }
  












  public void setData(int dataType, Object data, int offset, int stride, int count)
  {
    if (isForwardTransform) {
      fft.setFCTData(dataType, data, offset, stride, count);
    } else {
      fft.setIFCTData(dataType, data, offset, stride, count);
    }
  }
  










  public void getData(int dataType, Object data, int offset, int stride)
  {
    if (isForwardTransform) {
      fft.getFCTData(dataType, data, offset, stride);
    } else {
      fft.getIFCTData(dataType, data, offset, stride);
    }
  }
  


  public void transform()
  {
    fft.transform();
  }
}
