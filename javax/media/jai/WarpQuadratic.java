package javax.media.jai;

import java.awt.geom.Point2D;









































public final class WarpQuadratic
  extends WarpPolynomial
{
  private float c1;
  private float c2;
  private float c3;
  private float c4;
  private float c5;
  private float c6;
  private float c7;
  private float c8;
  private float c9;
  private float c10;
  private float c11;
  private float c12;
  
  public WarpQuadratic(float[] xCoeffs, float[] yCoeffs, float preScaleX, float preScaleY, float postScaleX, float postScaleY)
  {
    super(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
    
    if ((xCoeffs.length != 6) || (yCoeffs.length != 6)) {
      throw new IllegalArgumentException(JaiI18N.getString("WarpQuadratic0"));
    }
    
    c1 = xCoeffs[0];
    c2 = xCoeffs[1];
    c3 = xCoeffs[2];
    c4 = xCoeffs[3];
    c5 = xCoeffs[4];
    c6 = xCoeffs[5];
    
    c7 = yCoeffs[0];
    c8 = yCoeffs[1];
    c9 = yCoeffs[2];
    c10 = yCoeffs[3];
    c11 = yCoeffs[4];
    c12 = yCoeffs[5];
  }
  










  public WarpQuadratic(float[] xCoeffs, float[] yCoeffs)
  {
    this(xCoeffs, yCoeffs, 1.0F, 1.0F, 1.0F, 1.0F);
  }
  






























  public float[] warpSparseRect(int x, int y, int width, int height, int periodX, int periodY, float[] destRect)
  {
    if (destRect == null) {
      destRect = new float[(width + periodX - 1) / periodX * ((height + periodY - 1) / periodY) * 2];
    }
    







    float px1 = periodX * preScaleX;
    float px2 = px1 * px1;
    

    float ddx = c4 * 2.0F * px2;
    float ddy = c10 * 2.0F * px2;
    
    float x1 = (x + 0.5F) * preScaleX;
    float x2 = x1 * x1;
    
    width += x;
    height += y;
    int index = 0;
    
    for (int j = y; j < height; j += periodY)
    {
      float y1 = (j + 0.5F) * preScaleY;
      float y2 = y1 * y1;
      

      float wx = c1 + c2 * x1 + c3 * y1 + c4 * x2 + c5 * x1 * y1 + c6 * y2;
      
      float wy = c7 + c8 * x1 + c9 * y1 + c10 * x2 + c11 * x1 * y1 + c12 * y2;
      


      float dx = c2 * px1 + c4 * (2.0F * x1 * px1 + px2) + c5 * px1 * y1;
      float dy = c8 * px1 + c10 * (2.0F * x1 * px1 + px2) + c11 * px1 * y1;
      
      for (int i = x; i < width; i += periodX) {
        destRect[(index++)] = (wx * postScaleX - 0.5F);
        destRect[(index++)] = (wy * postScaleY - 0.5F);
        
        wx += dx;
        wy += dy;
        dx += ddx;
        dy += ddy;
      }
    }
    
    return destRect;
  }
  































  public Point2D mapDestPoint(Point2D destPt)
  {
    if (destPt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    double x1 = (destPt.getX() + 0.5D) * preScaleX;
    double x2 = x1 * x1;
    
    double y1 = (destPt.getY() + 0.5D) * preScaleY;
    double y2 = y1 * y1;
    
    double x = c1 + c2 * x1 + c3 * y1 + c4 * x2 + c5 * x1 * y1 + c6 * y2;
    double y = c7 + c8 * x1 + c9 * y1 + c10 * x2 + c11 * x1 * y1 + c12 * y2;
    
    Point2D pt = (Point2D)destPt.clone();
    pt.setLocation(x * postScaleX - 0.5D, y * postScaleY - 0.5D);
    
    return pt;
  }
}
