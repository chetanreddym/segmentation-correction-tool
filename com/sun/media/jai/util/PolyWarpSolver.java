package com.sun.media.jai.util;

import java.io.PrintStream;
import java.util.Random;
















public class PolyWarpSolver
{
  public PolyWarpSolver() {}
  
  private static double sign(double a, double b)
  {
    a = Math.abs(a);
    if (b >= 0.0D) {
      return a;
    }
    return -a;
  }
  
  private static final double square(double x)
  {
    return x * x;
  }
  
  private static final double sqrt(double x) {
    return Math.sqrt(x);
  }
  
  private static final double hypot(double x, double y) {
    double xabs = Math.abs(x);
    double yabs = Math.abs(y);
    
    if (xabs > yabs)
      return xabs * sqrt(square(yabs / xabs) + 1.0D);
    if (yabs != 0.0D) {
      return yabs * sqrt(square(xabs / yabs) + 1.0D);
    }
    return xabs;
  }
  

  public static double[][] matmul_t(double[][] A, double[][] B)
  {
    int rowsA = A.length;
    int colsA = A[0].length;
    
    int rowsB = B[0].length;
    int colsB = B.length;
    


    double[][] out = new double[rowsA][colsB];
    
    for (int i = 0; i < rowsA; i++) {
      double[] outi = out[i];
      double[] Ai = A[i];
      
      for (int j = 0; j < colsB; j++) {
        double tmp = 0.0D;
        for (int k = 0; k < colsA; k++) {
          tmp += Ai[k] * B[j][k];
        }
        outi[j] = tmp;
      }
    }
    
    return out;
  }
  




































  private static boolean SVD(double[][] a, double[] w, double[][] u, double[][] v)
  {
    int l = 0;
    int l1 = 0;
    int m = a.length;
    int n = a[0].length;
    
    double[] rv1 = new double[n];
    
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        u[i][j] = a[i][j];
      }
    }
    
    double g = 0.0D;
    double scale = 0.0D;
    double x = 0.0D;
    
    for (i = 0; i < n; i++) {
      l = i + 1;
      rv1[i] = (scale * g);
      g = 0.0D;
      double s = 0.0D;
      scale = 0.0D;
      
      if (i < m) {
        for (int k = i; k < m; k++) {
          scale += Math.abs(u[k][i]);
        }
        
        if (scale != 0.0D) {
          for (k = i; k < m; k++) {
            u[k][i] /= scale;
            s += square(u[k][i]);
          }
          
          double f = u[i][i];
          g = -sign(sqrt(s), f);
          double h = f * g - s;
          u[i][i] = (f - g);
          
          for (int j = l; j < n; j++) {
            s = 0.0D;
            
            for (k = i; k < m; k++) {
              s += u[k][i] * u[k][j];
            }
            f = s / h;
            for (k = i; k < m; k++) {
              u[k][j] += f * u[k][i];
            }
          }
          
          for (k = i; k < m; k++) {
            u[k][i] *= scale;
          }
        }
      }
      
      w[i] = (scale * g);
      g = 0.0D;
      s = 0.0D;
      scale = 0.0D;
      
      if ((i < m) && (i != n - 1)) {
        for (int k = l; k < n; k++) {
          scale += Math.abs(u[i][k]);
        }
        
        if (scale != 0.0D) {
          for (k = l; k < n; k++) {
            u[i][k] /= scale;
            s += square(u[i][k]);
          }
          
          double f = u[i][l];
          g = -sign(sqrt(s), f);
          double h = f * g - s;
          u[i][l] = (f - g);
          
          for (k = l; k < n; k++) {
            rv1[k] = (u[i][k] / h);
          }
          
          for (int j = l; j < m; j++) {
            s = 0.0D;
            
            for (k = l; k < n; k++) {
              s += u[j][k] * u[i][k];
            }
            
            for (k = l; k < n; k++) {
              u[j][k] += s * rv1[k];
            }
          }
          
          for (k = l; k < n; k++) {
            u[i][k] *= scale;
          }
        }
      }
      

      x = Math.max(x, Math.abs(w[i]) + Math.abs(rv1[i]));
    }
    
    for (i = n - 1; i >= 0; i--) {
      if (i != n - 1) {
        if (g != 0.0D) {
          for (int j = l; j < n; j++) {
            v[j][i] = (u[i][j] / u[i][l] / g);
          }
          
          for (j = l; j < n; j++) {
            double s = 0.0D;
            for (int k = l; k < n; k++) {
              s += u[i][k] * v[k][j];
            }
            for (k = l; k < n; k++) {
              v[k][j] += s * v[k][i];
            }
          }
        }
        
        for (int j = l; j < n; j++) {
          double tmp906_905 = 0.0D;v[j][i] = tmp906_905;v[i][j] = tmp906_905;
        }
      }
      
      v[i][i] = 1.0D;
      g = rv1[i];
      l = i;
    }
    
    int mn = Math.min(m, n);
    
    for (i = mn - 1; i >= 0; i--) {
      l = i + 1;
      g = w[i];
      
      if (i != n - 1) {
        for (int j = l; j < n; j++) {
          u[i][j] = 0.0D;
        }
      }
      
      if (g != 0.0D) {
        if (i != mn - 1) {
          for (int j = l; j < n; j++) {
            double s = 0.0D;
            
            for (int k = l; k < m; k++) {
              s += u[k][i] * u[k][j];
            }
            double f = s / u[i][i] / g;
            for (k = i; k < m; k++) {
              u[k][j] += f * u[k][i];
            }
          }
        }
        
        for (int j = i; j < m; j++) {
          u[j][i] /= g;
        }
      }
      for (int j = i; j < m; j++) {
        u[j][i] = 0.0D;
      }
      
      u[i][i] += 1.0D;
    }
    
    double tst1 = x;
    
    for (int k = n - 1; k >= 0; k--) {
      int k1 = k - 1;
      int its = 0;
      for (;;)
      {
        boolean flag = true;
        
        for (l = k; l >= 0; l--) {
          l1 = l - 1;
          double tst2 = tst1 + Math.abs(rv1[l]);
          if (tst2 == tst1) {
            flag = false;
            break;
          }
          
          tst2 = tst1 + Math.abs(w[l1]);
          if (tst2 == tst1) {
            flag = true;
            break;
          }
        }
        
        if (flag) {
          double c = 0.0D;
          double s = 1.0D;
          
          for (i = l; i < k + 1; i++) {
            double f = s * rv1[i];
            rv1[i] *= c;
            
            double tst2 = tst1 + Math.abs(f);
            if (tst2 != tst1) {
              g = w[i];
              
              double h = hypot(f, g);
              w[i] = h;
              c = g / h;
              s = -f / h;
              
              for (int j = 0; j < m; j++) {
                double y = u[j][l1];
                double z = u[j][i];
                u[j][l1] = (y * c + z * s);
                u[j][i] = (-y * s + z * c);
              }
            }
          }
        }
        
        double z = w[k];
        
        if (l == k) {
          if (z >= 0.0D) break;
          w[k] = (-z);
          for (int j = 0; j < n; j++) {
            v[j][k] = (-v[j][k]);
          }
        }
        


        if (its == 30) {
          return false;
        }
        
        its++;
        
        x = w[l];
        double y = w[k1];
        g = rv1[k1];
        double h = rv1[k];
        double f = 0.5D * ((g + z) / h * ((g - z) / y) + y / h - h / y);
        
        g = hypot(f, 1.0D);
        f = x - z / x * z + h / x * (y / (f + sign(g, f)) - h);
        
        double c = 1.0D;
        double s = 1.0D;
        
        for (int i1 = l; i1 <= k1; i1++) {
          i = i1 + 1;
          g = rv1[i];
          y = w[i];
          h = s * g;
          g = c * g;
          
          z = hypot(f, h);
          rv1[i1] = z;
          c = f / z;
          s = h / z;
          f = x * c + g * s;
          g = -x * s + g * c;
          h = y * s;
          y *= c;
          
          for (int j = 0; j < n; j++) {
            x = v[j][i1];
            z = v[j][i];
            v[j][i1] = (x * c + z * s);
            v[j][i] = (-x * s + z * c);
          }
          
          z = hypot(f, h);
          w[i1] = z;
          
          if (z != 0.0D) {
            c = f / z;
            s = h / z;
          }
          
          f = c * g + s * y;
          x = -s * g + c * y;
          
          for (j = 0; j < m; j++) {
            y = u[j][i1];
            z = u[j][i];
            u[j][i1] = (y * c + z * s);
            u[j][i] = (-y * s + z * c);
          }
        }
        
        rv1[l] = 0.0D;
        rv1[k] = f;
        w[k] = x;
      }
    }
    
    return true;
  }
  





















  public static float[] getCoeffs(float[] sourceCoords, int sourceOffset, float[] destCoords, int destOffset, int numCoords, float preScaleX, float preScaleY, float postScaleX, float postScaleY, int degree)
  {
    int equations = numCoords / 2;
    










    int unknowns = (degree + 1) * (degree + 2) / 2;
    float[] out = new float[2 * unknowns];
    

    if ((degree == 1) && (numCoords == 3))
    {


      double x0 = sourceCoords[0] * preScaleX;
      double y0 = sourceCoords[1] * preScaleY;
      double x1 = sourceCoords[2] * preScaleX;
      double y1 = sourceCoords[3] * preScaleY;
      double x2 = sourceCoords[4] * preScaleY;
      double y2 = sourceCoords[5] * preScaleY;
      
      double u0 = destCoords[0] / postScaleX;
      double v0 = destCoords[1] / postScaleY;
      double u1 = destCoords[2] / postScaleX;
      double v1 = destCoords[3] / postScaleY;
      double u2 = destCoords[4] / postScaleX;
      double v2 = destCoords[5] / postScaleY;
      
      double v0mv1 = v0 - v1;
      double v1mv2 = v1 - v2;
      double v2mv0 = v2 - v0;
      double u1mu0 = u1 - u0;
      double u2mu1 = u2 - u1;
      double u0mu2 = u0 - u2;
      double u1v2mu2v1 = u1 * v2 - u2 * v1;
      double u2v0mu0v2 = u2 * v0 - u0 * v2;
      double u0v1mu1v0 = u0 * v1 - u1 * v0;
      double invdet = 1.0D / (u0 * v1mv2 + v0 * u2mu1 + u1v2mu2v1);
      
      out[0] = ((float)((v1mv2 * x0 + v2mv0 * x1 + v0mv1 * x2) * invdet));
      out[1] = ((float)((u2mu1 * x0 + u0mu2 * x1 + u1mu0 * x2) * invdet));
      out[2] = ((float)((u1v2mu2v1 * x0 + u2v0mu0v2 * x1 + u0v1mu1v0 * x2) * invdet));
      
      out[3] = ((float)((v1mv2 * y0 + v2mv0 * y1 + v0mv1 * y2) * invdet));
      out[4] = ((float)((u2mu1 * y0 + u0mu2 * y1 + u1mu0 * y2) * invdet));
      out[5] = ((float)((u1v2mu2v1 * y0 + u2v0mu0v2 * y1 + u0v1mu1v0 * y2) * invdet));
      

      return out;
    }
    
    double[][] A = new double[equations][unknowns];
    












    double[] xpow = new double[degree + 1];
    double[] ypow = new double[degree + 1];
    
    for (int i = 0; i < equations; i++) {
      double[] Ai = A[i];
      double x = destCoords[(2 * i + destOffset)] / postScaleX;
      double y = destCoords[(2 * i + 1 + destOffset)] / postScaleY;
      
      double xtmp = 1.0D;
      double ytmp = 1.0D;
      for (int d = 0; d <= degree; d++) {
        xpow[d] = xtmp;
        ypow[d] = ytmp;
        xtmp *= x;
        ytmp *= y;
      }
      
      int index = 0;
      for (int deg = 0; deg <= degree; deg++) {
        for (int ydeg = 0; ydeg <= deg; ydeg++) {
          Ai[(index++)] = (xpow[(deg - ydeg)] * ypow[ydeg]);
        }
      }
    }
    
    double[][] V = new double[unknowns][unknowns];
    double[] W = new double[unknowns];
    double[][] U = new double[equations][unknowns];
    SVD(A, W, U, V);
    

    for (int j = 0; j < unknowns; j++) {
      double winv = W[j];
      if (winv != 0.0D) {
        winv = 1.0D / winv;
      }
      for (i = 0; i < unknowns; i++) {
        V[i][j] *= winv;
      }
    }
    

    double[][] VWINVUT = matmul_t(V, U);
    

    for (i = 0; i < unknowns; i++) {
      double tmp0 = 0.0D;
      double tmp1 = 0.0D;
      for (j = 0; j < equations; j++) {
        double val = VWINVUT[i][j];
        tmp0 += val * sourceCoords[(2 * j + sourceOffset)] * preScaleX;
        tmp1 += val * sourceCoords[(2 * j + 1 + sourceOffset)] * preScaleY;
      }
      out[i] = ((float)tmp0);
      out[(i + unknowns)] = ((float)tmp1);
    }
    
    return out;
  }
  


  private static Random myRandom = new Random(0L);
  private static double[] c0 = new double[6];
  private static double[] c1 = new double[6];
  private static double noise = 0.0D;
  
  private static float xpoly(float x, float y) {
    return (float)(c0[0] + c0[1] * x + c0[2] * y + c0[3] * x * x + c0[4] * x * y + c0[5] * y * y + myRandom.nextDouble() * noise);
  }
  

  private static float ypoly(float x, float y)
  {
    return (float)(c1[0] + c1[1] * x + c1[2] * y + c1[3] * x * x + c1[4] * x * y + c1[5] * y * y + myRandom.nextDouble() * noise);
  }
  


  private static void doTest(int equations, boolean print)
  {
    for (int i = 0; i < 6; i++) {
      c0[i] = (myRandom.nextDouble() * 100.0D);
      c1[i] = (myRandom.nextDouble() * 100.0D);
    }
    

    float[] destCoords = new float[2 * equations];
    for (int i = 0; i < 2 * equations; i++) {
      destCoords[i] = (myRandom.nextFloat() * 100.0F);
    }
    

    float[] sourceCoords = new float[2 * equations];
    for (int i = 0; i < equations; i++) {
      sourceCoords[(2 * i)] = xpoly(destCoords[(2 * i)], destCoords[(2 * i + 1)]);
      
      sourceCoords[(2 * i + 1)] = ypoly(destCoords[(2 * i)], destCoords[(2 * i + 1)]);
    }
    


    float[] coeffs = getCoeffs(sourceCoords, 0, destCoords, 0, sourceCoords.length, 0.5F, 0.5F, 2.0F, 2.0F, 2);
    





    if (print) {
      System.out.println("Using " + equations + " equations:");
      for (int i = 0; i < 6; i++) {
        System.out.println("c0[" + i + "] = " + c0[i] + ", recovered as " + coeffs[i] + " (ratio = " + c0[i] / coeffs[i] + ")");
        

        System.out.println("c1[" + i + "] = " + c1[i] + ", recovered as " + coeffs[(i + 6)] + " (ratio = " + c1[i] / coeffs[(i + 6)] + ")");
      }
    }
  }
  

  public static void main(String[] args)
  {
    for (int times = 0; times < 3; times++) {
      doTest(6 + 50 * times, true);
      System.out.println();
    }
    
    int trials = 10000;
    int points = 6;
    
    long startTime = System.currentTimeMillis();
    for (int times = 0; times < trials; times++) {
      doTest(points, false);
    }
    long endTime = System.currentTimeMillis();
    System.out.println("Did " + trials + " " + points + "-point solutions in " + (float)(endTime - startTime) / 1000.0F + " seconds.");
    

    System.out.println("Rate = " + trials * 1000.0F / (float)(endTime - startTime) + " trials/second");
  }
}
