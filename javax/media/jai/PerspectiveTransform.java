package javax.media.jai;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Point2D.Float;
import java.io.Serializable;

































public final class PerspectiveTransform
  implements Cloneable, Serializable
{
  private static final double PERSPECTIVE_DIVIDE_EPSILON = 1.0E-10D;
  double m00;
  double m01;
  double m02;
  double m10;
  double m11;
  double m12;
  double m20;
  double m21;
  double m22;
  
  public PerspectiveTransform()
  {
    m00 = (this.m11 = this.m22 = 1.0D);
    m01 = (this.m02 = this.m10 = this.m12 = this.m20 = this.m21 = 0.0D);
  }
  


  /**
   * @deprecated
   */
  public PerspectiveTransform(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22)
  {
    this.m00 = m00;
    this.m01 = m01;
    this.m02 = m02;
    this.m10 = m10;
    this.m11 = m11;
    this.m12 = m12;
    this.m20 = m20;
    this.m21 = m21;
    this.m22 = m22;
  }
  


  /**
   * @deprecated
   */
  public PerspectiveTransform(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22)
  {
    this.m00 = m00;
    this.m01 = m01;
    this.m02 = m02;
    this.m10 = m10;
    this.m11 = m11;
    this.m12 = m12;
    this.m20 = m20;
    this.m21 = m21;
    this.m22 = m22;
  }
  





  /**
   * @deprecated
   */
  public PerspectiveTransform(float[] flatmatrix)
  {
    if (flatmatrix == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    m00 = flatmatrix[0];
    m01 = flatmatrix[1];
    m02 = flatmatrix[2];
    m10 = flatmatrix[3];
    m11 = flatmatrix[4];
    m12 = flatmatrix[5];
    m20 = flatmatrix[6];
    m21 = flatmatrix[7];
    m22 = flatmatrix[8];
  }
  




  /**
   * @deprecated
   */
  public PerspectiveTransform(float[][] matrix)
  {
    if (matrix == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    m00 = matrix[0][0];
    m01 = matrix[0][1];
    m02 = matrix[0][2];
    m10 = matrix[1][0];
    m11 = matrix[1][1];
    m12 = matrix[1][2];
    m20 = matrix[2][0];
    m21 = matrix[2][1];
    m22 = matrix[2][2];
  }
  






  /**
   * @deprecated
   */
  public PerspectiveTransform(double[] flatmatrix)
  {
    if (flatmatrix == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    m00 = flatmatrix[0];
    m01 = flatmatrix[1];
    m02 = flatmatrix[2];
    m10 = flatmatrix[3];
    m11 = flatmatrix[4];
    m12 = flatmatrix[5];
    m20 = flatmatrix[6];
    m21 = flatmatrix[7];
    m22 = flatmatrix[8];
  }
  





  public PerspectiveTransform(double[][] matrix)
  {
    if (matrix == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    m00 = matrix[0][0];
    m01 = matrix[0][1];
    m02 = matrix[0][2];
    m10 = matrix[1][0];
    m11 = matrix[1][1];
    m12 = matrix[1][2];
    m20 = matrix[2][0];
    m21 = matrix[2][1];
    m22 = matrix[2][2];
  }
  




  public PerspectiveTransform(AffineTransform transform)
  {
    if (transform == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    m00 = transform.getScaleX();
    m01 = transform.getShearX();
    m02 = transform.getTranslateX();
    m10 = transform.getShearY();
    m11 = transform.getScaleY();
    m12 = transform.getTranslateY();
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 1.0D;
  }
  


  private final void makeAdjoint()
  {
    double m00p = m11 * m22 - m12 * m21;
    double m01p = m12 * m20 - m10 * m22;
    double m02p = m10 * m21 - m11 * m20;
    double m10p = m02 * m21 - m01 * m22;
    double m11p = m00 * m22 - m02 * m20;
    double m12p = m01 * m20 - m00 * m21;
    double m20p = m01 * m12 - m02 * m11;
    double m21p = m02 * m10 - m00 * m12;
    double m22p = m00 * m11 - m01 * m10;
    

    m00 = m00p;
    m01 = m10p;
    m02 = m20p;
    m10 = m01p;
    m11 = m11p;
    m12 = m21p;
    m20 = m02p;
    m21 = m12p;
    m22 = m22p;
  }
  



  private final void normalize()
  {
    double invscale = 1.0D / m22;
    m00 *= invscale;
    m01 *= invscale;
    m02 *= invscale;
    m10 *= invscale;
    m11 *= invscale;
    m12 *= invscale;
    m20 *= invscale;
    m21 *= invscale;
    m22 = 1.0D;
  }
  



  private static final void getSquareToQuad(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, PerspectiveTransform tx)
  {
    double dx3 = x0 - x1 + x2 - x3;
    double dy3 = y0 - y1 + y2 - y3;
    
    m22 = 1.0D;
    
    if ((dx3 == 0.0D) && (dy3 == 0.0D)) {
      m00 = (x1 - x0);
      m01 = (x2 - x1);
      m02 = x0;
      m10 = (y1 - y0);
      m11 = (y2 - y1);
      m12 = y0;
      m20 = 0.0D;
      m21 = 0.0D;
    } else {
      double dx1 = x1 - x2;
      double dy1 = y1 - y2;
      double dx2 = x3 - x2;
      double dy2 = y3 - y2;
      
      double invdet = 1.0D / (dx1 * dy2 - dx2 * dy1);
      m20 = ((dx3 * dy2 - dx2 * dy3) * invdet);
      m21 = ((dx1 * dy3 - dx3 * dy1) * invdet);
      m00 = (x1 - x0 + m20 * x1);
      m01 = (x3 - x0 + m21 * x3);
      m02 = x0;
      m10 = (y1 - y0 + m20 * y1);
      m11 = (y3 - y0 + m21 * y3);
      m12 = y0;
    }
  }
  













  public static PerspectiveTransform getSquareToQuad(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3)
  {
    PerspectiveTransform tx = new PerspectiveTransform();
    getSquareToQuad(x0, y0, x1, y1, x2, y2, x3, y3, tx);
    return tx;
  }
  














  public static PerspectiveTransform getSquareToQuad(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3)
  {
    return getSquareToQuad(x0, y0, x1, y1, x2, y2, x3, y3);
  }
  

















  public static PerspectiveTransform getQuadToSquare(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3)
  {
    PerspectiveTransform tx = new PerspectiveTransform();
    getSquareToQuad(x0, y0, x1, y1, x2, y2, x3, y3, tx);
    tx.makeAdjoint();
    return tx;
  }
  













  public static PerspectiveTransform getQuadToSquare(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3)
  {
    return getQuadToSquare(x0, y0, x1, y1, x2, y2, x3, y3);
  }
  




















  public static PerspectiveTransform getQuadToQuad(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, double x0p, double y0p, double x1p, double y1p, double x2p, double y2p, double x3p, double y3p)
  {
    PerspectiveTransform tx1 = getQuadToSquare(x0, y0, x1, y1, x2, y2, x3, y3);
    

    PerspectiveTransform tx2 = getSquareToQuad(x0p, y0p, x1p, y1p, x2p, y2p, x3p, y3p);
    

    tx1.concatenate(tx2);
    return tx1;
  }
  


















  public static PerspectiveTransform getQuadToQuad(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3, float x0p, float y0p, float x1p, float y1p, float x2p, float y2p, float x3p, float y3p)
  {
    return getQuadToQuad(x0, y0, x1, y1, x2, y2, x3, y3, x0p, y0p, x1p, y1p, x2p, y2p, x3p, y3p);
  }
  










  public double getDeterminant()
  {
    return m00 * (m11 * m22 - m12 * m21) - m01 * (m10 * m22 - m12 * m20) + m02 * (m10 * m21 - m11 * m20);
  }
  











  /**
   * @deprecated
   */
  public double[] getMatrix(double[] flatmatrix)
  {
    if (flatmatrix == null) {
      flatmatrix = new double[9];
    }
    
    flatmatrix[0] = m00;
    flatmatrix[1] = m01;
    flatmatrix[2] = m02;
    flatmatrix[3] = m10;
    flatmatrix[4] = m11;
    flatmatrix[5] = m12;
    flatmatrix[6] = m20;
    flatmatrix[7] = m21;
    flatmatrix[8] = m22;
    
    return flatmatrix;
  }
  










  public double[][] getMatrix(double[][] matrix)
  {
    if (matrix == null) {
      matrix = new double[3][3];
    }
    
    matrix[0][0] = m00;
    matrix[0][1] = m01;
    matrix[0][2] = m02;
    matrix[1][0] = m10;
    matrix[1][1] = m11;
    matrix[1][2] = m12;
    matrix[2][0] = m20;
    matrix[2][1] = m21;
    matrix[2][2] = m22;
    
    return matrix;
  }
  









  public void translate(double tx, double ty)
  {
    PerspectiveTransform Tx = new PerspectiveTransform();
    Tx.setToTranslation(tx, ty);
    concatenate(Tx);
  }
  













  public void rotate(double theta)
  {
    PerspectiveTransform Tx = new PerspectiveTransform();
    Tx.setToRotation(theta);
    concatenate(Tx);
  }
  














  public void rotate(double theta, double x, double y)
  {
    PerspectiveTransform Tx = new PerspectiveTransform();
    Tx.setToRotation(theta, x, y);
    concatenate(Tx);
  }
  












  public void scale(double sx, double sy)
  {
    PerspectiveTransform Tx = new PerspectiveTransform();
    Tx.setToScale(sx, sy);
    concatenate(Tx);
  }
  
















  public void shear(double shx, double shy)
  {
    PerspectiveTransform Tx = new PerspectiveTransform();
    Tx.setToShear(shx, shy);
    concatenate(Tx);
  }
  


  public void setToIdentity()
  {
    m00 = (this.m11 = this.m22 = 1.0D);
    m01 = (this.m10 = this.m02 = this.m20 = this.m12 = this.m21 = 0.0D);
  }
  












  public void setToTranslation(double tx, double ty)
  {
    m00 = 1.0D;
    m01 = 0.0D;
    m02 = tx;
    m10 = 0.0D;
    m11 = 1.0D;
    m12 = ty;
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 1.0D;
  }
  











  public void setToRotation(double theta)
  {
    m00 = Math.cos(theta);
    m01 = (-Math.sin(theta));
    m02 = 0.0D;
    m10 = (-m01);
    m11 = m00;
    m12 = 0.0D;
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 1.0D;
  }
  

















  public void setToRotation(double theta, double x, double y)
  {
    setToRotation(theta);
    double sin = m10;
    double oneMinusCos = 1.0D - m00;
    m02 = (x * oneMinusCos + y * sin);
    m12 = (y * oneMinusCos - x * sin);
  }
  












  public void setToScale(double sx, double sy)
  {
    m00 = sx;
    m01 = 0.0D;
    m02 = 0.0D;
    m10 = 0.0D;
    m11 = sy;
    m12 = 0.0D;
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 1.0D;
  }
  
















  public void setToShear(double shx, double shy)
  {
    m00 = 1.0D;
    m01 = shx;
    m02 = 0.0D;
    m10 = shy;
    m11 = 1.0D;
    m12 = 0.0D;
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 1.0D;
  }
  



  public void setTransform(AffineTransform Tx)
  {
    if (Tx == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    m00 = Tx.getScaleX();
    m01 = Tx.getShearX();
    m02 = Tx.getTranslateX();
    m10 = Tx.getShearY();
    m11 = Tx.getScaleY();
    m12 = Tx.getTranslateY();
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 1.0D;
  }
  



  public void setTransform(PerspectiveTransform Tx)
  {
    if (Tx == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    m00 = m00;
    m01 = m01;
    m02 = m02;
    m10 = m10;
    m11 = m11;
    m12 = m12;
    m20 = m20;
    m21 = m21;
    m22 = m22;
  }
  





  /**
   * @deprecated
   */
  public void setTransform(float m00, float m10, float m20, float m01, float m11, float m21, float m02, float m12, float m22)
  {
    this.m00 = m00;
    this.m01 = m01;
    this.m02 = m02;
    this.m10 = m10;
    this.m11 = m11;
    this.m12 = m12;
    this.m20 = m20;
    this.m21 = m21;
    this.m22 = m22;
  }
  









  public void setTransform(double[][] matrix)
  {
    if (matrix == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    m00 = matrix[0][0];
    m01 = matrix[0][1];
    m02 = matrix[0][2];
    m10 = matrix[1][0];
    m11 = matrix[1][1];
    m12 = matrix[1][2];
    m20 = matrix[2][0];
    m21 = matrix[2][1];
    m22 = matrix[2][2];
  }
  



  public void concatenate(AffineTransform Tx)
  {
    if (Tx == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    


    double tx_m00 = Tx.getScaleX();
    double tx_m01 = Tx.getShearX();
    double tx_m02 = Tx.getTranslateX();
    double tx_m10 = Tx.getShearY();
    double tx_m11 = Tx.getScaleY();
    double tx_m12 = Tx.getTranslateY();
    
    double m00p = m00 * tx_m00 + m10 * tx_m01 + m20 * tx_m02;
    double m01p = m01 * tx_m00 + m11 * tx_m01 + m21 * tx_m02;
    double m02p = m02 * tx_m00 + m12 * tx_m01 + m22 * tx_m02;
    double m10p = m00 * tx_m10 + m10 * tx_m11 + m20 * tx_m12;
    double m11p = m01 * tx_m10 + m11 * tx_m11 + m21 * tx_m12;
    double m12p = m02 * tx_m10 + m12 * tx_m11 + m22 * tx_m12;
    double m20p = m20;
    double m21p = m21;
    double m22p = m22;
    
    m00 = m00p;
    m10 = m10p;
    m20 = m20p;
    m01 = m01p;
    m11 = m11p;
    m21 = m21p;
    m02 = m02p;
    m12 = m12p;
    m22 = m22p;
  }
  



  public void concatenate(PerspectiveTransform Tx)
  {
    if (Tx == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    double m00p = m00 * m00 + m10 * m01 + m20 * m02;
    double m10p = m00 * m10 + m10 * m11 + m20 * m12;
    double m20p = m00 * m20 + m10 * m21 + m20 * m22;
    double m01p = m01 * m00 + m11 * m01 + m21 * m02;
    double m11p = m01 * m10 + m11 * m11 + m21 * m12;
    double m21p = m01 * m20 + m11 * m21 + m21 * m22;
    double m02p = m02 * m00 + m12 * m01 + m22 * m02;
    double m12p = m02 * m10 + m12 * m11 + m22 * m12;
    double m22p = m02 * m20 + m12 * m21 + m22 * m22;
    
    m00 = m00p;
    m10 = m10p;
    m20 = m20p;
    m01 = m01p;
    m11 = m11p;
    m21 = m21p;
    m02 = m02p;
    m12 = m12p;
    m22 = m22p;
  }
  



  public void preConcatenate(AffineTransform Tx)
  {
    if (Tx == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    


    double tx_m00 = Tx.getScaleX();
    double tx_m01 = Tx.getShearX();
    double tx_m02 = Tx.getTranslateX();
    double tx_m10 = Tx.getShearY();
    double tx_m11 = Tx.getScaleY();
    double tx_m12 = Tx.getTranslateY();
    
    double m00p = tx_m00 * m00 + tx_m10 * m01;
    double m01p = tx_m01 * m00 + tx_m11 * m01;
    double m02p = tx_m02 * m00 + tx_m12 * m01 + m02;
    double m10p = tx_m00 * m10 + tx_m10 * m11;
    double m11p = tx_m01 * m10 + tx_m11 * m11;
    double m12p = tx_m02 * m10 + tx_m12 * m11 + m12;
    double m20p = tx_m00 * m20 + tx_m10 * m21;
    double m21p = tx_m01 * m20 + tx_m11 * m21;
    double m22p = tx_m02 * m20 + tx_m12 * m21 + m22;
    
    m00 = m00p;
    m10 = m10p;
    m20 = m20p;
    m01 = m01p;
    m11 = m11p;
    m21 = m21p;
    m02 = m02p;
    m12 = m12p;
    m22 = m22p;
  }
  



  public void preConcatenate(PerspectiveTransform Tx)
  {
    if (Tx == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    double m00p = m00 * m00 + m10 * m01 + m20 * m02;
    double m10p = m00 * m10 + m10 * m11 + m20 * m12;
    double m20p = m00 * m20 + m10 * m21 + m20 * m22;
    double m01p = m01 * m00 + m11 * m01 + m21 * m02;
    double m11p = m01 * m10 + m11 * m11 + m21 * m12;
    double m21p = m01 * m20 + m11 * m21 + m21 * m22;
    double m02p = m02 * m00 + m12 * m01 + m22 * m02;
    double m12p = m02 * m10 + m12 * m11 + m22 * m12;
    double m22p = m02 * m20 + m12 * m21 + m22 * m22;
    
    m00 = m00p;
    m10 = m10p;
    m20 = m20p;
    m01 = m01p;
    m11 = m11p;
    m21 = m21p;
    m02 = m02p;
    m12 = m12p;
    m22 = m22p;
  }
  





  public PerspectiveTransform createInverse()
    throws NoninvertibleTransformException, CloneNotSupportedException
  {
    PerspectiveTransform tx = (PerspectiveTransform)clone();
    tx.makeAdjoint();
    if (Math.abs(m22) < 1.0E-10D) {
      throw new NoninvertibleTransformException(JaiI18N.getString("PerspectiveTransform0"));
    }
    tx.normalize();
    return tx;
  }
  














  public PerspectiveTransform createAdjoint()
    throws CloneNotSupportedException
  {
    PerspectiveTransform tx = (PerspectiveTransform)clone();
    tx.makeAdjoint();
    return tx;
  }
  











  public Point2D transform(Point2D ptSrc, Point2D ptDst)
  {
    if (ptSrc == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (ptDst == null) {
      if ((ptSrc instanceof Point2D.Double)) {
        ptDst = new Point2D.Double();
      } else {
        ptDst = new Point2D.Float();
      }
    }
    
    double x = ptSrc.getX();
    double y = ptSrc.getY();
    double w = m20 * x + m21 * y + m22;
    ptDst.setLocation((m00 * x + m01 * y + m02) / w, (m10 * x + m11 * y + m12) / w);
    

    return ptDst;
  }
  















  public void transform(Point2D[] ptSrc, int srcOff, Point2D[] ptDst, int dstOff, int numPts)
  {
    if ((ptSrc == null) || (ptDst == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    while (numPts-- > 0)
    {
      Point2D src = ptSrc[(srcOff++)];
      Point2D dst = ptDst[(dstOff++)];
      if (dst == null) {
        if ((src instanceof Point2D.Double)) {
          dst = new Point2D.Double();
        } else {
          dst = new Point2D.Float();
        }
        ptDst[(dstOff - 1)] = dst;
      }
      
      double x = src.getX();
      double y = src.getY();
      double w = m20 * x + m21 * y + m22;
      
      if (w == 0.0D) {
        dst.setLocation(x, y);
      } else {
        dst.setLocation((m00 * x + m01 * y + m02) / w, (m10 * x + m11 * y + m12) / w);
      }
    }
  }
  

















  public void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts)
  {
    if (srcPts == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (dstPts == null) {
      dstPts = new float[numPts * 2 + dstOff];
    }
    
    while (numPts-- > 0) {
      float x = srcPts[(srcOff++)];
      float y = srcPts[(srcOff++)];
      double w = m20 * x + m21 * y + m22;
      
      if (w == 0.0D) {
        dstPts[(dstOff++)] = x;
        dstPts[(dstOff++)] = y;
      } else {
        dstPts[(dstOff++)] = ((float)((m00 * x + m01 * y + m02) / w));
        dstPts[(dstOff++)] = ((float)((m10 * x + m11 * y + m12) / w));
      }
    }
  }
  
















  public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts)
  {
    if (srcPts == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (dstPts == null) {
      dstPts = new double[numPts * 2 + dstOff];
    }
    
    while (numPts-- > 0) {
      double x = srcPts[(srcOff++)];
      double y = srcPts[(srcOff++)];
      double w = m20 * x + m21 * y + m22;
      
      if (w == 0.0D) {
        dstPts[(dstOff++)] = x;
        dstPts[(dstOff++)] = y;
      } else {
        dstPts[(dstOff++)] = ((m00 * x + m01 * y + m02) / w);
        dstPts[(dstOff++)] = ((m10 * x + m11 * y + m12) / w);
      }
    }
  }
  

















  public void transform(float[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts)
  {
    if (srcPts == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (dstPts == null) {
      dstPts = new double[numPts * 2 + dstOff];
    }
    
    while (numPts-- > 0) {
      float x = srcPts[(srcOff++)];
      float y = srcPts[(srcOff++)];
      double w = m20 * x + m21 * y + m22;
      
      if (w == 0.0D) {
        dstPts[(dstOff++)] = x;
        dstPts[(dstOff++)] = y;
      } else {
        dstPts[(dstOff++)] = ((m00 * x + m01 * y + m02) / w);
        dstPts[(dstOff++)] = ((m10 * x + m11 * y + m12) / w);
      }
    }
  }
  

















  public void transform(double[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts)
  {
    if (srcPts == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (dstPts == null) {
      dstPts = new float[numPts * 2 + dstOff];
    }
    
    while (numPts-- > 0) {
      double x = srcPts[(srcOff++)];
      double y = srcPts[(srcOff++)];
      double w = m20 * x + m21 * y + m22;
      
      if (w == 0.0D) {
        dstPts[(dstOff++)] = ((float)x);
        dstPts[(dstOff++)] = ((float)y);
      } else {
        dstPts[(dstOff++)] = ((float)((m00 * x + m01 * y + m02) / w));
        dstPts[(dstOff++)] = ((float)((m10 * x + m11 * y + m12) / w));
      }
    }
  }
  













  public Point2D inverseTransform(Point2D ptSrc, Point2D ptDst)
    throws NoninvertibleTransformException
  {
    if (ptSrc == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (ptDst == null) {
      if ((ptSrc instanceof Point2D.Double)) {
        ptDst = new Point2D.Double();
      } else {
        ptDst = new Point2D.Float();
      }
    }
    
    double x = ptSrc.getX();
    double y = ptSrc.getY();
    
    double tmp_x = (m11 * m22 - m12 * m21) * x + (m02 * m21 - m01 * m22) * y + (m01 * m12 - m02 * m11);
    

    double tmp_y = (m12 * m20 - m10 * m22) * x + (m00 * m22 - m02 * m20) * y + (m02 * m10 - m00 * m12);
    

    double w = (m10 * m21 - m11 * m20) * x + (m01 * m20 - m00 * m21) * y + (m00 * m11 - m01 * m10);
    


    double wabs = w;
    if (w < 0.0D) {
      wabs = -w;
    }
    if (wabs < 1.0E-10D) {
      throw new NoninvertibleTransformException(JaiI18N.getString("PerspectiveTransform1"));
    }
    


    ptDst.setLocation(tmp_x / w, tmp_y / w);
    
    return ptDst;
  }
  




















  public void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts)
    throws NoninvertibleTransformException
  {
    if (srcPts == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (dstPts == null) {
      dstPts = new double[numPts * 2 + dstOff];
    }
    
    while (numPts-- > 0) {
      double x = srcPts[(srcOff++)];
      double y = srcPts[(srcOff++)];
      
      double tmp_x = (m11 * m22 - m12 * m21) * x + (m02 * m21 - m01 * m22) * y + (m01 * m12 - m02 * m11);
      

      double tmp_y = (m12 * m20 - m10 * m22) * x + (m00 * m22 - m02 * m20) * y + (m02 * m10 - m00 * m12);
      

      double w = (m10 * m21 - m11 * m20) * x + (m01 * m20 - m00 * m21) * y + (m00 * m11 - m01 * m10);
      


      double wabs = w;
      if (w < 0.0D) {
        wabs = -w;
      }
      if (wabs < 1.0E-10D) {
        throw new NoninvertibleTransformException(JaiI18N.getString("PerspectiveTransform1"));
      }
      

      dstPts[(dstOff++)] = (tmp_x / w);
      dstPts[(dstOff++)] = (tmp_y / w);
    }
  }
  


  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("Perspective transform matrix\n");
    sb.append(m00);
    sb.append("\t");
    sb.append(m01);
    sb.append("\t");
    sb.append(m02);
    sb.append("\n");
    sb.append(m10);
    sb.append("\t");
    sb.append(m11);
    sb.append("\t");
    sb.append(m12);
    sb.append("\n");
    sb.append(m20);
    sb.append("\t");
    sb.append(m21);
    sb.append("\t");
    sb.append(m22);
    sb.append("\n");
    return new String(sb);
  }
  



  public boolean isIdentity()
  {
    return (m01 == 0.0D) && (m02 == 0.0D) && (m10 == 0.0D) && (m12 == 0.0D) && (m20 == 0.0D) && (m21 == 0.0D) && (m22 != 0.0D) && (m00 / m22 == 1.0D) && (m11 / m22 == 1.0D);
  }
  




  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  





  public boolean equals(Object obj)
  {
    if (!(obj instanceof PerspectiveTransform)) {
      return false;
    }
    
    PerspectiveTransform a = (PerspectiveTransform)obj;
    
    return (m00 == m00) && (m10 == m10) && (m20 == m20) && (m01 == m01) && (m11 == m11) && (m21 == m21) && (m02 == m02) && (m12 == m12) && (m22 == m22);
  }
}
