package javax.media.jai;

public abstract interface ImageFunction
{
  public abstract boolean isComplex();
  
  public abstract int getNumElements();
  
  public abstract void getElements(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2);
  
  public abstract void getElements(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2);
}
