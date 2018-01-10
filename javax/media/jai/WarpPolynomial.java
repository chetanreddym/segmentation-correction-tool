package javax.media.jai;

import com.sun.media.jai.util.PolyWarpSolver;
import java.awt.geom.Point2D;












































































































public abstract class WarpPolynomial
  extends Warp
{
  protected float[] xCoeffs;
  protected float[] yCoeffs;
  protected float preScaleX;
  protected float preScaleY;
  protected float postScaleX;
  protected float postScaleY;
  protected int degree;
  
  public WarpPolynomial(float[] xCoeffs, float[] yCoeffs, float preScaleX, float preScaleY, float postScaleX, float postScaleY)
  {
    if ((xCoeffs == null) || (yCoeffs == null) || (xCoeffs.length < 1) || (yCoeffs.length < 1) || (xCoeffs.length != yCoeffs.length))
    {

      throw new IllegalArgumentException(JaiI18N.getString("WarpPolynomial0"));
    }
    

    int numCoeffs = xCoeffs.length;
    degree = -1;
    while (numCoeffs > 0) {
      degree += 1;
      numCoeffs -= degree + 1;
    }
    if (numCoeffs != 0) {
      throw new IllegalArgumentException(JaiI18N.getString("WarpPolynomial0"));
    }
    

    this.xCoeffs = ((float[])xCoeffs.clone());
    this.yCoeffs = ((float[])yCoeffs.clone());
    this.preScaleX = preScaleX;
    this.preScaleY = preScaleY;
    this.postScaleX = postScaleX;
    this.postScaleY = postScaleY;
  }
  







  public WarpPolynomial(float[] xCoeffs, float[] yCoeffs)
  {
    this(xCoeffs, yCoeffs, 1.0F, 1.0F, 1.0F, 1.0F);
  }
  





  public float[] getXCoeffs()
  {
    return (float[])xCoeffs.clone();
  }
  





  public float[] getYCoeffs()
  {
    return (float[])yCoeffs.clone();
  }
  





  public float[][] getCoeffs()
  {
    float[][] coeffs = new float[2][];
    coeffs[0] = ((float[])xCoeffs.clone());
    coeffs[1] = ((float[])yCoeffs.clone());
    
    return coeffs;
  }
  
  public float getPreScaleX()
  {
    return preScaleX;
  }
  
  public float getPreScaleY()
  {
    return preScaleY;
  }
  
  public float getPostScaleX()
  {
    return postScaleX;
  }
  
  public float getPostScaleY()
  {
    return postScaleY;
  }
  




  public int getDegree()
  {
    return degree;
  }
  
















































  public static WarpPolynomial createWarp(float[] sourceCoords, int sourceOffset, float[] destCoords, int destOffset, int numCoords, float preScaleX, float preScaleY, float postScaleX, float postScaleY, int degree)
  {
    int minNumPoints = (degree + 1) * (degree + 2);
    if ((sourceOffset + minNumPoints > sourceCoords.length) || (destOffset + minNumPoints > destCoords.length))
    {

      throw new IllegalArgumentException(JaiI18N.getString("WarpPolynomial1"));
    }
    
    float[] coeffs = PolyWarpSolver.getCoeffs(sourceCoords, sourceOffset, destCoords, destOffset, numCoords, preScaleX, preScaleY, postScaleX, postScaleY, degree);
    





    int numCoeffs = coeffs.length / 2;
    float[] xCoeffs = new float[numCoeffs];
    float[] yCoeffs = new float[numCoeffs];
    
    for (int i = 0; i < numCoeffs; i++) {
      xCoeffs[i] = coeffs[i];
      yCoeffs[i] = coeffs[(i + numCoeffs)];
    }
    
    if (degree == 1) {
      return new WarpAffine(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
    }
    
    if (degree == 2) {
      return new WarpQuadratic(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
    }
    
    if (degree == 3) {
      return new WarpCubic(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
    }
    

    return new WarpGeneralPolynomial(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
  }
  









































  public Point2D mapDestPoint(Point2D destPt)
  {
    if (destPt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    double dx = (destPt.getX() + 0.5D) * preScaleX;
    double dy = (destPt.getY() + 0.5D) * preScaleY;
    
    double sx = 0.0D;
    double sy = 0.0D;
    int c = 0;
    
    for (int nx = 0; nx <= degree; nx++) {
      for (int ny = 0; ny <= nx; ny++) {
        double t = Math.pow(dx, nx - ny) * Math.pow(dy, ny);
        sx += xCoeffs[c] * t;
        sy += yCoeffs[c] * t;
        c++;
      }
    }
    
    Point2D pt = (Point2D)destPt.clone();
    pt.setLocation(sx * postScaleX - 0.5D, sy * postScaleY - 0.5D);
    
    return pt;
  }
}
