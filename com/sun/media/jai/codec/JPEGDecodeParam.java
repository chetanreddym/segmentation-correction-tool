package com.sun.media.jai.codec;



























public class JPEGDecodeParam
  implements ImageDecodeParam
{
  private boolean decodeToCSM = true;
  









  public JPEGDecodeParam() {}
  








  public void setDecodeToCSM(boolean decodeToCSM)
  {
    this.decodeToCSM = decodeToCSM;
  }
  



  public boolean getDecodeToCSM()
  {
    return decodeToCSM;
  }
}
