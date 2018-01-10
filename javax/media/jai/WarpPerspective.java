package javax.media.jai;

import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

























public final class WarpPerspective
  extends Warp
{
  private PerspectiveTransform transform;
  private PerspectiveTransform invTransform;
  
  public WarpPerspective(PerspectiveTransform transform)
  {
    if (transform == null) {
      throw new IllegalArgumentException(JaiI18N.getString("WarpPerspective0"));
    }
    
    this.transform = transform;
    

    try
    {
      invTransform = transform.createInverse();
    } catch (NoninvertibleTransformException e) {
      invTransform = null;
    } catch (CloneNotSupportedException e) {
      invTransform = null;
    }
  }
  






  public PerspectiveTransform getTransform()
  {
    return (PerspectiveTransform)transform.clone();
  }
  



























  public float[] warpSparseRect(int x, int y, int width, int height, int periodX, int periodY, float[] destRect)
  {
    if (destRect == null) {
      destRect = new float[2 * ((width + periodX - 1) / periodX) * ((height + periodY - 1) / periodY)];
    }
    

    double[][] matrix = new double[3][3];
    matrix = transform.getMatrix(matrix);
    float m00 = (float)matrix[0][0];
    float m01 = (float)matrix[0][1];
    float m02 = (float)matrix[0][2];
    float m10 = (float)matrix[1][0];
    float m11 = (float)matrix[1][1];
    float m12 = (float)matrix[1][2];
    float m20 = (float)matrix[2][0];
    float m21 = (float)matrix[2][1];
    float m22 = (float)matrix[2][2];
    





    float dx = m00 * periodX;
    float dy = m10 * periodX;
    float dw = m20 * periodX;
    
    float sx = x + 0.5F;
    
    width += x;
    height += y;
    int index = 0;
    
    for (int j = y; j < height; j += periodY) {
      float sy = j + 0.5F;
      
      float wx = m00 * sx + m01 * sy + m02;
      float wy = m10 * sx + m11 * sy + m12;
      float w = m20 * sx + m21 * sy + m22;
      
      for (int i = x; i < width; i += periodX) {
        float tx;
        float ty;
        try { float tx = wx / w;
          ty = wy / w;
        } catch (ArithmeticException e) {
          float ty;
          tx = i + 0.5F;
          ty = j + 0.5F;
        }
        
        destRect[(index++)] = (tx - 0.5F);
        destRect[(index++)] = (ty - 0.5F);
        
        wx += dx;
        wy += dy;
        w += dw;
      }
    }
    
    return destRect;
  }
  











  public Rectangle mapDestRect(Rectangle destRect)
  {
    if (destRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    int x0 = x;
    int x1 = x + width;
    int y0 = y;
    int y1 = y + height;
    
    Point2D[] pts = new Point2D[4];
    pts[0] = new Point2D.Float(x0, y0);
    pts[1] = new Point2D.Float(x1, y0);
    pts[2] = new Point2D.Float(x0, y1);
    pts[3] = new Point2D.Float(x1, y1);
    
    transform.transform(pts, 0, pts, 0, 4);
    
    int minX = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxY = Integer.MIN_VALUE;
    
    for (int i = 0; i < 4; i++) {
      int px = (int)pts[i].getX();
      int py = (int)pts[i].getY();
      
      minX = Math.min(minX, px);
      maxX = Math.max(maxX, px);
      minY = Math.min(minY, py);
      maxY = Math.max(maxY, py);
    }
    
    return new Rectangle(minX, minY, maxX - minX, maxY - minY);
  }
  












  public Rectangle mapSourceRect(Rectangle srcRect)
  {
    if (srcRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    if (invTransform == null) {
      return null;
    }
    
    int x0 = x;
    int x1 = x + width;
    int y0 = y;
    int y1 = y + height;
    
    Point2D[] pts = new Point2D[4];
    pts[0] = new Point2D.Float(x0, y0);
    pts[1] = new Point2D.Float(x1, y0);
    pts[2] = new Point2D.Float(x0, y1);
    pts[3] = new Point2D.Float(x1, y1);
    
    invTransform.transform(pts, 0, pts, 0, 4);
    
    int minX = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxY = Integer.MIN_VALUE;
    
    for (int i = 0; i < 4; i++) {
      int px = (int)pts[i].getX();
      int py = (int)pts[i].getY();
      
      minX = Math.min(minX, px);
      maxX = Math.max(maxX, px);
      minY = Math.min(minY, py);
      maxY = Math.max(maxY, py);
    }
    
    return new Rectangle(minX, minY, maxX - minX, maxY - minY);
  }
  
















  public Point2D mapDestPoint(Point2D destPt)
  {
    if (destPt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return transform.transform(destPt, null);
  }
  


















  public Point2D mapSourcePoint(Point2D sourcePt)
  {
    if (sourcePt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return invTransform != null ? invTransform.transform(sourcePt, null) : null;
  }
}
