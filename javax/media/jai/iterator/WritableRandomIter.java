package javax.media.jai.iterator;

public abstract interface WritableRandomIter
  extends RandomIter
{
  public abstract void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void setSample(int paramInt1, int paramInt2, int paramInt3, float paramFloat);
  
  public abstract void setSample(int paramInt1, int paramInt2, int paramInt3, double paramDouble);
  
  public abstract void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt);
  
  public abstract void setPixel(int paramInt1, int paramInt2, float[] paramArrayOfFloat);
  
  public abstract void setPixel(int paramInt1, int paramInt2, double[] paramArrayOfDouble);
}
