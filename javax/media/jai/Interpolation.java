package javax.media.jai;

import java.io.Serializable;












































































































































































































public abstract class Interpolation
  implements Serializable
{
  public static final int INTERP_NEAREST = 0;
  public static final int INTERP_BILINEAR = 1;
  public static final int INTERP_BICUBIC = 2;
  public static final int INTERP_BICUBIC_2 = 3;
  private static Interpolation nearestInstance = null;
  
  private static Interpolation bilinearInstance = null;
  
  private static Interpolation bicubicInstance = null;
  
  private static Interpolation bicubic2Instance = null;
  





  protected int leftPadding;
  





  protected int rightPadding;
  





  protected int topPadding;
  





  protected int bottomPadding;
  





  protected int subsampleBitsH;
  





  protected int subsampleBitsV;
  




  protected int width;
  




  protected int height;
  





  public static synchronized Interpolation getInstance(int type)
  {
    Interpolation interp = null;
    
    switch (type) {
    case 0: 
      if (nearestInstance == null) {
        interp = Interpolation.nearestInstance = new InterpolationNearest();
      } else {
        interp = nearestInstance;
      }
      break;
    case 1: 
      if (bilinearInstance == null) {
        interp = Interpolation.bilinearInstance = new InterpolationBilinear();
      } else {
        interp = bilinearInstance;
      }
      break;
    case 2: 
      if (bicubicInstance == null) {
        interp = Interpolation.bicubicInstance = new InterpolationBicubic(8);
      } else {
        interp = bicubicInstance;
      }
      break;
    case 3: 
      if (bicubic2Instance == null) {
        interp = Interpolation.bicubic2Instance = new InterpolationBicubic2(8);
      } else {
        interp = bicubic2Instance;
      }
      break;
    default: 
      throw new IllegalArgumentException(JaiI18N.getString("Interpolation0"));
    }
    
    
    return interp;
  }
  








  protected Interpolation() {}
  








  public Interpolation(int width, int height, int leftPadding, int rightPadding, int topPadding, int bottomPadding, int subsampleBitsH, int subsampleBitsV)
  {
    this.width = width;
    this.height = height;
    this.leftPadding = leftPadding;
    this.rightPadding = rightPadding;
    this.topPadding = topPadding;
    this.bottomPadding = bottomPadding;
    this.subsampleBitsH = subsampleBitsH;
    this.subsampleBitsV = subsampleBitsV;
  }
  
  public int getLeftPadding()
  {
    return leftPadding;
  }
  

  public int getRightPadding()
  {
    return rightPadding;
  }
  
  public int getTopPadding()
  {
    return topPadding;
  }
  

  public int getBottomPadding()
  {
    return bottomPadding;
  }
  
  public int getWidth()
  {
    return width;
  }
  

  public int getHeight()
  {
    return height;
  }
  





  public boolean isSeparable()
  {
    return true;
  }
  










  public int getSubsampleBitsH()
  {
    return subsampleBitsH;
  }
  




  public int getSubsampleBitsV()
  {
    return subsampleBitsV;
  }
  













  public abstract int interpolateH(int[] paramArrayOfInt, int paramInt);
  












  public int interpolateV(int[] samples, int yfrac)
  {
    return interpolateH(samples, yfrac);
  }
  









  public int interpolate(int[][] samples, int xfrac, int yfrac)
  {
    int[] interpH = new int[height];
    
    for (int i = 0; i < height; i++) {
      interpH[i] = interpolateH(samples[i], xfrac);
    }
    return interpolateV(interpH, yfrac);
  }
  













  public int interpolateH(int s0, int s1, int xfrac)
  {
    int[] s = new int[2];
    s[0] = s0;
    s[1] = s1;
    return interpolateH(s, xfrac);
  }
  














  public int interpolateH(int s_, int s0, int s1, int s2, int xfrac)
  {
    int[] s = new int[4];
    s[0] = s_;
    s[1] = s0;
    s[2] = s1;
    s[3] = s2;
    return interpolateH(s, xfrac);
  }
  
















  public int interpolateV(int s0, int s1, int yfrac)
  {
    int[] s = new int[2];
    s[0] = s0;
    s[1] = s1;
    return interpolateV(s, yfrac);
  }
  


















  public int interpolateV(int s_, int s0, int s1, int s2, int yfrac)
  {
    int[] s = new int[4];
    s[0] = s_;
    s[1] = s0;
    s[2] = s1;
    s[3] = s2;
    return interpolateV(s, yfrac);
  }
  


















  public int interpolate(int s00, int s01, int s10, int s11, int xfrac, int yfrac)
  {
    int[][] s = new int[4][4];
    s[0][0] = s00;
    s[0][1] = s01;
    s[1][0] = s10;
    s[1][1] = s11;
    return interpolate(s, xfrac, yfrac);
  }
  
































  public int interpolate(int s__, int s_0, int s_1, int s_2, int s0_, int s00, int s01, int s02, int s1_, int s10, int s11, int s12, int s2_, int s20, int s21, int s22, int xfrac, int yfrac)
  {
    int[][] s = new int[4][4];
    s[0][0] = s__;
    s[0][1] = s_0;
    s[0][2] = s_1;
    s[0][3] = s_2;
    s[1][0] = s0_;
    s[1][1] = s00;
    s[1][2] = s01;
    s[1][3] = s02;
    s[2][0] = s1_;
    s[2][1] = s10;
    s[2][2] = s11;
    s[2][3] = s12;
    s[3][0] = s2_;
    s[3][1] = s20;
    s[3][2] = s21;
    s[3][3] = s22;
    return interpolate(s, xfrac, yfrac);
  }
  












  public abstract float interpolateH(float[] paramArrayOfFloat, float paramFloat);
  












  public float interpolateV(float[] samples, float yfrac)
  {
    return interpolateH(samples, yfrac);
  }
  













  public float interpolate(float[][] samples, float xfrac, float yfrac)
  {
    float[] interpH = new float[height];
    
    for (int i = 0; i < height; i++) {
      interpH[i] = interpolateH(samples[i], xfrac);
    }
    return interpolateV(interpH, yfrac);
  }
  













  public float interpolateH(float s0, float s1, float xfrac)
  {
    float[] s = new float[2];
    s[0] = s0;
    s[1] = s1;
    return interpolateH(s, xfrac);
  }
  
















  public float interpolateH(float s_, float s0, float s1, float s2, float xfrac)
  {
    float[] s = new float[4];
    s[0] = s_;
    s[1] = s0;
    s[2] = s1;
    s[3] = s2;
    return interpolateH(s, xfrac);
  }
  

















  public float interpolateV(float s0, float s1, float yfrac)
  {
    float[] s = new float[2];
    s[0] = s0;
    s[1] = s1;
    return interpolateV(s, yfrac);
  }
  





















  public float interpolateV(float s_, float s0, float s1, float s2, float yfrac)
  {
    float[] s = new float[4];
    s[0] = s_;
    s[1] = s0;
    s[2] = s1;
    s[3] = s2;
    return interpolateV(s, yfrac);
  }
  



















  public float interpolate(float s00, float s01, float s10, float s11, float xfrac, float yfrac)
  {
    float[][] s = new float[4][4];
    s[0][0] = s00;
    s[0][1] = s01;
    s[1][0] = s10;
    s[1][1] = s11;
    return interpolate(s, xfrac, yfrac);
  }
  

































  public float interpolate(float s__, float s_0, float s_1, float s_2, float s0_, float s00, float s01, float s02, float s1_, float s10, float s11, float s12, float s2_, float s20, float s21, float s22, float xfrac, float yfrac)
  {
    float[][] s = new float[4][4];
    s[0][0] = s__;
    s[0][1] = s_0;
    s[0][2] = s_1;
    s[0][3] = s_2;
    s[1][0] = s0_;
    s[1][1] = s00;
    s[1][2] = s01;
    s[1][3] = s02;
    s[2][0] = s1_;
    s[2][1] = s10;
    s[2][2] = s11;
    s[2][3] = s12;
    s[3][0] = s2_;
    s[3][1] = s20;
    s[3][2] = s21;
    s[3][3] = s22;
    return interpolate(s, xfrac, yfrac);
  }
  












  public abstract double interpolateH(double[] paramArrayOfDouble, float paramFloat);
  











  public double interpolateV(double[] samples, float yfrac)
  {
    return interpolateH(samples, yfrac);
  }
  












  public double interpolate(double[][] samples, float xfrac, float yfrac)
  {
    double[] interpH = new double[height];
    
    for (int i = 0; i < height; i++) {
      interpH[i] = interpolateH(samples[i], xfrac);
    }
    return interpolateV(interpH, yfrac);
  }
  













  public double interpolateH(double s0, double s1, float xfrac)
  {
    double[] s = new double[2];
    s[0] = s0;
    s[1] = s1;
    return interpolateH(s, xfrac);
  }
  
















  public double interpolateH(double s_, double s0, double s1, double s2, float xfrac)
  {
    double[] s = new double[4];
    s[0] = s_;
    s[1] = s0;
    s[2] = s1;
    s[3] = s2;
    return interpolateH(s, xfrac);
  }
  

















  public double interpolateV(double s0, double s1, float yfrac)
  {
    double[] s = new double[2];
    s[0] = s0;
    s[1] = s1;
    return interpolateV(s, yfrac);
  }
  




















  public double interpolateV(double s_, double s0, double s1, double s2, float yfrac)
  {
    double[] s = new double[4];
    s[0] = s_;
    s[1] = s0;
    s[2] = s1;
    s[3] = s2;
    return interpolateV(s, yfrac);
  }
  



















  public double interpolate(double s00, double s01, double s10, double s11, float xfrac, float yfrac)
  {
    double[][] s = new double[4][4];
    s[0][0] = s00;
    s[0][1] = s01;
    s[1][0] = s10;
    s[1][1] = s11;
    return interpolate(s, xfrac, yfrac);
  }
  






























  public double interpolate(double s__, double s_0, double s_1, double s_2, double s0_, double s00, double s01, double s02, double s1_, double s10, double s11, double s12, double s2_, double s20, double s21, double s22, float xfrac, float yfrac)
  {
    double[][] s = new double[4][4];
    s[0][0] = s__;
    s[0][1] = s_0;
    s[0][2] = s_1;
    s[0][3] = s_2;
    s[1][0] = s0_;
    s[1][1] = s00;
    s[1][2] = s01;
    s[1][3] = s02;
    s[2][0] = s1_;
    s[2][1] = s10;
    s[2][2] = s11;
    s[2][3] = s12;
    s[3][0] = s2_;
    s[3][1] = s20;
    s[3][2] = s21;
    s[3][3] = s22;
    return interpolate(s, xfrac, yfrac);
  }
}
