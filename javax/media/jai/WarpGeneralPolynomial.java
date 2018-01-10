package javax.media.jai;































































public final class WarpGeneralPolynomial
  extends WarpPolynomial
{
  public WarpGeneralPolynomial(float[] xCoeffs, float[] yCoeffs, float preScaleX, float preScaleY, float postScaleX, float postScaleY)
  {
    super(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
  }
  










  public WarpGeneralPolynomial(float[] xCoeffs, float[] yCoeffs)
  {
    this(xCoeffs, yCoeffs, 1.0F, 1.0F, 1.0F, 1.0F);
  }
  


























  public float[] warpSparseRect(int x, int y, int width, int height, int periodX, int periodY, float[] destRect)
  {
    if (destRect == null) {
      destRect = new float[2 * ((width + periodX - 1) / periodX) * ((height + periodY - 1) / periodY)];
    }
    



    float[] xPows = new float[degree + 1];
    float[] yPows = new float[degree + 1];
    xPows[0] = 1.0F;
    yPows[0] = 1.0F;
    
    width += x;
    height += y;
    int index = 0;
    
    for (int j = y; j < height; j += periodY)
    {
      float y1 = (j + 0.5F) * preScaleY;
      for (int n = 1; n <= degree; n++) {
        yPows[n] = (yPows[(n - 1)] * y1);
      }
      
      for (int i = x; i < width; i += periodX)
      {
        float x1 = (i + 0.5F) * preScaleX;
        for (int n = 1; n <= degree; n++) {
          xPows[n] = (xPows[(n - 1)] * x1);
        }
        
        float wx = 0.0F;
        float wy = 0.0F;
        int c = 0;
        
        for (int nx = 0; nx <= degree; nx++) {
          for (int ny = 0; ny <= nx; ny++) {
            float t = xPows[(nx - ny)] * yPows[ny];
            wx += xCoeffs[c] * t;
            wy += yCoeffs[c] * t;
            c++;
          }
        }
        
        destRect[(index++)] = (wx * postScaleX - 0.5F);
        destRect[(index++)] = (wy * postScaleY - 0.5F);
      }
    }
    
    return destRect;
  }
}
