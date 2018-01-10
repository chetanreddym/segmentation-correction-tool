package javax.media.jai;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

























public final class WarpAffine
  extends WarpPolynomial
{
  private float c1;
  private float c2;
  private float c3;
  private float c4;
  private float c5;
  private float c6;
  private float invc1;
  private float invc2;
  private float invc3;
  private float invc4;
  private float invc5;
  private float invc6;
  private AffineTransform transform;
  private AffineTransform invTransform;
  
  private static final float[] xCoeffsHelper(AffineTransform transform)
  {
    float[] coeffs = new float[3];
    coeffs[0] = ((float)transform.getTranslateX());
    coeffs[1] = ((float)transform.getScaleX());
    coeffs[2] = ((float)transform.getShearX());
    return coeffs;
  }
  
  private static final float[] yCoeffsHelper(AffineTransform transform) {
    float[] coeffs = new float[3];
    coeffs[0] = ((float)transform.getTranslateY());
    coeffs[1] = ((float)transform.getShearY());
    coeffs[2] = ((float)transform.getScaleY());
    return coeffs;
  }
  


























  public WarpAffine(float[] xCoeffs, float[] yCoeffs, float preScaleX, float preScaleY, float postScaleX, float postScaleY)
  {
    super(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
    
    if ((xCoeffs.length != 3) || (yCoeffs.length != 3)) {
      throw new IllegalArgumentException(JaiI18N.getString("WarpAffine0"));
    }
    

    c1 = xCoeffs[0];
    c2 = xCoeffs[1];
    c3 = xCoeffs[2];
    
    c4 = yCoeffs[0];
    c5 = yCoeffs[1];
    c6 = yCoeffs[2];
    
    transform = getTransform();
    
    try
    {
      invTransform = transform.createInverse();
      
      invc1 = ((float)invTransform.getTranslateX());
      invc2 = ((float)invTransform.getScaleX());
      invc3 = ((float)invTransform.getShearX());
      
      invc4 = ((float)invTransform.getTranslateY());
      invc5 = ((float)invTransform.getShearY());
      invc6 = ((float)invTransform.getScaleY());
    }
    catch (NoninvertibleTransformException e) {
      invTransform = null;
    }
  }
  








  public WarpAffine(float[] xCoeffs, float[] yCoeffs)
  {
    this(xCoeffs, yCoeffs, 1.0F, 1.0F, 1.0F, 1.0F);
  }
  













  public WarpAffine(AffineTransform transform, float preScaleX, float preScaleY, float postScaleX, float postScaleY)
  {
    this(xCoeffsHelper(transform), yCoeffsHelper(transform), preScaleX, preScaleY, postScaleX, postScaleY);
  }
  







  public WarpAffine(AffineTransform transform)
  {
    this(transform, 1.0F, 1.0F, 1.0F, 1.0F);
  }
  





  public AffineTransform getTransform()
  {
    return new AffineTransform(c2, c5, c3, c6, c1, c4);
  }
  




























  public float[] warpSparseRect(int x, int y, int width, int height, int periodX, int periodY, float[] destRect)
  {
    if (destRect == null) {
      destRect = new float[(width + periodX - 1) / periodX * ((height + periodY - 1) / periodY) * 2];
    }
    
























    float px1 = periodX * preScaleX;
    
    float dx = c2 * px1 * postScaleX;
    float dy = c5 * px1 * postScaleY;
    
    float x1 = (x + 0.5F) * preScaleX;
    
    width += x;
    height += y;
    int index = 0;
    
    for (int j = y; j < height; j += periodY) {
      float y1 = (j + 0.5F) * preScaleY;
      

      float wx = (c1 + c2 * x1 + c3 * y1) * postScaleX - 0.5F;
      float wy = (c4 + c5 * x1 + c6 * y1) * postScaleY - 0.5F;
      
      for (int i = x; i < width; i += periodX) {
        destRect[(index++)] = wx;
        destRect[(index++)] = wy;
        
        wx += dx;
        wy += dy;
      }
    }
    
    return destRect;
  }
  














  public Rectangle mapDestRect(Rectangle destRect)
  {
    if (destRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    int dx0 = x;
    int dx1 = x + width;
    int dy0 = y;
    int dy1 = y + height;
    



    float[] pt = mapDestPoint(dx0, dy0);
    float sx0 = pt[0];
    float sx1 = pt[0];
    float sy0 = pt[1];
    float sy1 = pt[1];
    
    pt = mapDestPoint(dx1, dy0);
    sx0 = Math.min(sx0, pt[0]);
    sx1 = Math.max(sx1, pt[0]);
    sy0 = Math.min(sy0, pt[1]);
    sy1 = Math.max(sy1, pt[1]);
    
    pt = mapDestPoint(dx0, dy1);
    sx0 = Math.min(sx0, pt[0]);
    sx1 = Math.max(sx1, pt[0]);
    sy0 = Math.min(sy0, pt[1]);
    sy1 = Math.max(sy1, pt[1]);
    
    pt = mapDestPoint(dx1, dy1);
    sx0 = Math.min(sx0, pt[0]);
    sx1 = Math.max(sx1, pt[0]);
    sy0 = Math.min(sy0, pt[1]);
    sy1 = Math.max(sy1, pt[1]);
    
    int x = (int)Math.floor(sx0);
    int y = (int)Math.floor(sy0);
    int w = (int)Math.ceil(sx1 - x);
    int h = (int)Math.ceil(sy1 - y);
    
    return new Rectangle(x, y, w, h);
  }
  














  public Rectangle mapSourceRect(Rectangle srcRect)
  {
    if (srcRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    




    if (invTransform == null) {
      return null;
    }
    
    int sx0 = x;
    int sx1 = x + width;
    int sy0 = y;
    int sy1 = y + height;
    



    float[] pt = mapSrcPoint(sx0, sy0);
    float dx0 = pt[0];
    float dx1 = pt[0];
    float dy0 = pt[1];
    float dy1 = pt[1];
    
    pt = mapSrcPoint(sx1, sy0);
    dx0 = Math.min(dx0, pt[0]);
    dx1 = Math.max(dx1, pt[0]);
    dy0 = Math.min(dy0, pt[1]);
    dy1 = Math.max(dy1, pt[1]);
    
    pt = mapSrcPoint(sx0, sy1);
    dx0 = Math.min(dx0, pt[0]);
    dx1 = Math.max(dx1, pt[0]);
    dy0 = Math.min(dy0, pt[1]);
    dy1 = Math.max(dy1, pt[1]);
    
    pt = mapSrcPoint(sx1, sy1);
    dx0 = Math.min(dx0, pt[0]);
    dx1 = Math.max(dx1, pt[0]);
    dy0 = Math.min(dy0, pt[1]);
    dy1 = Math.max(dy1, pt[1]);
    
    int x = (int)Math.floor(dx0);
    int y = (int)Math.floor(dy0);
    int w = (int)Math.ceil(dx1 - x);
    int h = (int)Math.ceil(dy1 - y);
    
    return new Rectangle(x, y, w, h);
  }
  








  private float[] mapDestPoint(int x, int y)
  {
    float fx = (x + 0.5F) * preScaleX;
    float fy = (y + 0.5F) * preScaleY;
    
    float[] p = new float[2];
    p[0] = ((c1 + c2 * fx + c3 * fy) * postScaleX - 0.5F);
    p[1] = ((c4 + c5 * fx + c6 * fy) * postScaleY - 0.5F);
    
    return p;
  }
  








  private float[] mapSrcPoint(int x, int y)
  {
    float fx = (x + 0.5F) * preScaleX;
    float fy = (y + 0.5F) * preScaleY;
    
    float[] p = new float[2];
    p[0] = ((invc1 + invc2 * fx + invc3 * fy) * postScaleX - 0.5F);
    p[1] = ((invc4 + invc5 * fx + invc6 * fy) * postScaleY - 0.5F);
    
    return p;
  }
  

























  public Point2D mapDestPoint(Point2D destPt)
  {
    if (destPt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    double dx = (destPt.getX() + 0.5D) * preScaleX;
    double dy = (destPt.getY() + 0.5D) * preScaleY;
    
    Point2D pt = (Point2D)destPt.clone();
    
    pt.setLocation((c1 + c2 * dx + c3 * dy) * postScaleX - 0.5D, (c4 + c5 * dx + c6 * dy) * postScaleY - 0.5D);
    

    return pt;
  }
  




























  public Point2D mapSourcePoint(Point2D sourcePt)
  {
    if (sourcePt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (invTransform == null) {
      return null;
    }
    
    double sx = (sourcePt.getX() + 0.5D) / postScaleX;
    double sy = (sourcePt.getY() + 0.5D) / postScaleY;
    
    Point2D pt = (Point2D)sourcePt.clone();
    
    pt.setLocation((invc1 + invc2 * sx + invc3 * sy) / preScaleX - 0.5D, (invc4 + invc5 * sx + invc6 * sy) / preScaleY - 0.5D);
    

    return pt;
  }
}
