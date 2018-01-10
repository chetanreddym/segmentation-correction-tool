package javax.media.jai.iterator;

public abstract interface RookIter
  extends RectIter
{
  public abstract void prevLine();
  
  public abstract boolean prevLineDone();
  
  public abstract void endLines();
  
  public abstract void prevPixel();
  
  public abstract boolean prevPixelDone();
  
  public abstract void endPixels();
  
  public abstract void prevBand();
  
  public abstract boolean prevBandDone();
  
  public abstract void endBands();
}
