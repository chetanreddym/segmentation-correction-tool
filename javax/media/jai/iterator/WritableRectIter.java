package javax.media.jai.iterator;

public abstract interface WritableRectIter
  extends RectIter
{
  public abstract void setSample(int paramInt);
  
  public abstract void setSample(int paramInt1, int paramInt2);
  
  public abstract void setSample(float paramFloat);
  
  public abstract void setSample(int paramInt, float paramFloat);
  
  public abstract void setSample(double paramDouble);
  
  public abstract void setSample(int paramInt, double paramDouble);
  
  public abstract void setPixel(int[] paramArrayOfInt);
  
  public abstract void setPixel(float[] paramArrayOfFloat);
  
  public abstract void setPixel(double[] paramArrayOfDouble);
}
