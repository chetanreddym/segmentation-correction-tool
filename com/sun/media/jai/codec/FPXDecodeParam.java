package com.sun.media.jai.codec;


















public class FPXDecodeParam
  implements ImageDecodeParam
{
  private int resolution = -1;
  



  public FPXDecodeParam() {}
  



  public FPXDecodeParam(int resolution)
  {
    this.resolution = resolution;
  }
  




  public void setResolution(int resolution)
  {
    this.resolution = resolution;
  }
  


  public int getResolution()
  {
    return resolution;
  }
}
