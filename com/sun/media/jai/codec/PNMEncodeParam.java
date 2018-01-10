package com.sun.media.jai.codec;






















public class PNMEncodeParam
  implements ImageEncodeParam
{
  private boolean raw = true;
  





  public PNMEncodeParam() {}
  





  public void setRaw(boolean raw)
  {
    this.raw = raw;
  }
  


  public boolean getRaw()
  {
    return raw;
  }
}
