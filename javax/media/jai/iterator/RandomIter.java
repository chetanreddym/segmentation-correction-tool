package javax.media.jai.iterator;

public abstract interface RandomIter
{
  public abstract int getSample(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract float getSampleFloat(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract double getSampleDouble(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract int[] getPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt);
  
  public abstract float[] getPixel(int paramInt1, int paramInt2, float[] paramArrayOfFloat);
  
  public abstract double[] getPixel(int paramInt1, int paramInt2, double[] paramArrayOfDouble);
  
  public abstract void done();
}
