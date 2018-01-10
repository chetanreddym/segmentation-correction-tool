package javax.media.jai;

import java.awt.geom.Point2D;
































public final class WarpCubic
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
  private float c13;
  private float c14;
  private float c15;
  private float c16;
  private float c17;
  private float c18;
  private float c19;
  private float c20;
  
  public WarpCubic(float[] xCoeffs, float[] yCoeffs, float preScaleX, float preScaleY, float postScaleX, float postScaleY)
  {
    super(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
    
    if ((xCoeffs.length != 10) || (yCoeffs.length != 10)) {
      throw new IllegalArgumentException(JaiI18N.getString("WarpCubic0"));
    }
    
    c1 = xCoeffs[0];
    c2 = xCoeffs[1];
    c3 = xCoeffs[2];
    c4 = xCoeffs[3];
    c5 = xCoeffs[4];
    c6 = xCoeffs[5];
    c7 = xCoeffs[6];
    c8 = xCoeffs[7];
    c9 = xCoeffs[8];
    c10 = xCoeffs[9];
    
    c11 = yCoeffs[0];
    c12 = yCoeffs[1];
    c13 = yCoeffs[2];
    c14 = yCoeffs[3];
    c15 = yCoeffs[4];
    c16 = yCoeffs[5];
    c17 = yCoeffs[6];
    c18 = yCoeffs[7];
    c19 = yCoeffs[8];
    c20 = yCoeffs[9];
  }
  









  public WarpCubic(float[] xCoeffs, float[] yCoeffs)
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
    float px3 = px2 * px1;
    

    float dddx = c7 * 6.0F * px3;
    float dddy = c17 * 6.0F * px3;
    
    float x1 = (x + 0.5F) * preScaleX;
    float x2 = x1 * x1;
    float x3 = x2 * x1;
    
    width += x;
    height += y;
    int index = 0;
    
    for (int j = y; j < height; j += periodY) {
      float y1 = (j + 0.5F) * preScaleY;
      float y2 = y1 * y1;
      float y3 = y2 * y1;
      

      float wx = c1 + c2 * x1 + c3 * y1 + c4 * x2 + c5 * x1 * y1 + c6 * y2 + c7 * x3 + c8 * x2 * y1 + c9 * x1 * y2 + c10 * y3;
      

      float wy = c11 + c12 * x1 + c13 * y1 + c14 * x2 + c15 * x1 * y1 + c16 * y2 + c17 * x3 + c18 * x2 * y1 + c19 * x1 * y2 + c20 * y3;
      



      float dx = c2 * px1 + c4 * (2.0F * x1 * px1 + px2) + c5 * px1 * y1 + c7 * (3.0F * x2 * px1 + 3.0F * x1 * px2 + px3) + c8 * (2.0F * x1 * px1 + px2) * y1 + c9 * px1 * y2;
      




      float dy = c12 * px1 + c14 * (2.0F * x1 * px1 + px2) + c15 * px1 * y1 + c17 * (3.0F * x2 * px1 + 3.0F * x1 * px2 + px3) + c18 * (2.0F * x1 * px1 + px2) * y1 + c19 * px1 * y2;
      






      float ddx = c4 * 2.0F * px2 + c7 * (6.0F * x1 * px2 + 6.0F * px3) + c8 * 2.0F * px2 * y1;
      

      float ddy = c14 * 2.0F * px2 + c17 * (6.0F * x1 * px2 + 6.0F * px3) + c18 * 2.0F * px2 * y1;
      


      for (int i = x; i < width; i += periodX) {
        destRect[(index++)] = (wx * postScaleX - 0.5F);
        destRect[(index++)] = (wy * postScaleY - 0.5F);
        
        wx += dx;
        wy += dy;
        dx += ddx;
        dy += ddy;
        ddx += dddx;
        ddy += dddy;
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
    double x3 = x2 * x1;
    
    double y1 = (destPt.getY() + 0.5D) * preScaleY;
    double y2 = y1 * y1;
    double y3 = y2 * y1;
    
    double sx = c1 + c2 * x1 + c3 * y1 + c4 * x2 + c5 * x1 * y1 + c6 * y2 + c7 * x3 + c8 * x2 * y1 + c9 * x1 * y2 + c10 * y3;
    

    double sy = c11 + c12 * x1 + c13 * y1 + c14 * x2 + c15 * x1 * y1 + c16 * y2 + c17 * x3 + c18 * x2 * y1 + c19 * x1 * y2 + c20 * y3;
    


    Point2D pt = (Point2D)destPt.clone();
    pt.setLocation(sx * postScaleX - 0.5D, sy * postScaleY - 0.5D);
    
    return pt;
  }
}
