package javax.media.jai;










public class InterpolationTable
  extends Interpolation
{
  protected int precisionBits;
  








  private int round;
  








  private int numSubsamplesH;
  








  private int numSubsamplesV;
  








  protected double[] dataHd;
  








  protected double[] dataVd;
  








  protected float[] dataHf;
  








  protected float[] dataVf;
  








  protected int[] dataHi;
  








  protected int[] dataVi;
  









  public InterpolationTable(int keyX, int keyY, int width, int height, int subsampleBitsH, int subsampleBitsV, int precisionBits, int[] dataH, int[] dataV)
  {
    leftPadding = keyX;
    topPadding = keyY;
    this.width = width;
    rightPadding = (width - keyX - 1);
    
    this.precisionBits = precisionBits;
    if (precisionBits > 0) {
      round = (1 << precisionBits - 1);
    }
    
    this.subsampleBitsH = subsampleBitsH;
    numSubsamplesH = (1 << subsampleBitsH);
    int entriesH = width * numSubsamplesH;
    if (dataH.length != entriesH) {
      throw new IllegalArgumentException(JaiI18N.getString("InterpolationTable0"));
    }
    

    double prec = 1 << precisionBits;
    

    dataHi = ((int[])dataH.clone());
    dataHf = new float[entriesH];
    dataHd = new double[entriesH];
    
    for (int i = 0; i < entriesH; i++) {
      double d = dataHi[i] / prec;
      dataHf[i] = ((float)d);
      dataHd[i] = d;
    }
    
    if (dataV != null) {
      this.height = height;
      this.subsampleBitsV = subsampleBitsV;
      numSubsamplesV = (1 << subsampleBitsV);
      int entriesV = height * numSubsamplesV;
      if (dataV.length != entriesV) {
        throw new IllegalArgumentException(JaiI18N.getString("InterpolationTable1"));
      }
      

      dataVi = ((int[])dataV.clone());
      dataVf = new float[entriesV];
      dataVd = new double[entriesV];
      for (i = 0; i < entriesV; i++) {
        double d = dataVi[i] / prec;
        dataVf[i] = ((float)d);
        dataVd[i] = d;
      }
    } else {
      this.height = width;
      this.subsampleBitsV = subsampleBitsH;
      numSubsamplesV = numSubsamplesH;
      dataVf = dataHf;
      dataVi = dataHi;
      dataVd = dataHd;
    }
    bottomPadding = (this.height - keyY - 1);
  }
  

















  public InterpolationTable(int key, int width, int subsampleBits, int precisionBits, int[] data)
  {
    this(key, key, width, width, subsampleBits, subsampleBits, precisionBits, data, null);
  }
  




















































  public InterpolationTable(int keyX, int keyY, int width, int height, int subsampleBitsH, int subsampleBitsV, int precisionBits, float[] dataH, float[] dataV)
  {
    leftPadding = keyX;
    topPadding = keyY;
    this.width = width;
    rightPadding = (width - keyX - 1);
    
    this.precisionBits = precisionBits;
    if (precisionBits > 0) {
      round = (1 << precisionBits - 1);
    }
    
    this.subsampleBitsH = subsampleBitsH;
    numSubsamplesH = (1 << subsampleBitsH);
    int entriesH = width * numSubsamplesH;
    if (dataH.length != entriesH) {
      throw new IllegalArgumentException(JaiI18N.getString("InterpolationTable0"));
    }
    

    float prec = 1 << precisionBits;
    

    dataHf = ((float[])dataH.clone());
    dataHi = new int[entriesH];
    dataHd = new double[entriesH];
    
    for (int i = 0; i < entriesH; i++) {
      float f = dataHf[i];
      dataHi[i] = Math.round(f * prec);
      dataHd[i] = f;
    }
    
    if (dataV != null) {
      this.height = height;
      this.subsampleBitsV = subsampleBitsV;
      numSubsamplesV = (1 << subsampleBitsV);
      int entriesV = height * numSubsamplesV;
      if (dataV.length != entriesV) {
        throw new IllegalArgumentException(JaiI18N.getString("InterpolationTable1"));
      }
      

      dataVf = ((float[])dataV.clone());
      dataVi = new int[entriesV];
      dataVd = new double[entriesV];
      for (i = 0; i < entriesV; i++) {
        float f = dataVf[i];
        dataVi[i] = Math.round(f * prec);
        dataVd[i] = f;
      }
    } else {
      this.height = width;
      this.subsampleBitsV = subsampleBitsH;
      numSubsamplesV = numSubsamplesH;
      dataVf = dataHf;
      dataVi = dataHi;
      dataVd = dataHd;
    }
    bottomPadding = (this.height - keyY - 1);
  }
  


















  public InterpolationTable(int key, int width, int subsampleBits, int precisionBits, float[] data)
  {
    this(key, key, width, width, subsampleBits, subsampleBits, precisionBits, data, null);
  }
  


















































  public InterpolationTable(int keyX, int keyY, int width, int height, int subsampleBitsH, int subsampleBitsV, int precisionBits, double[] dataH, double[] dataV)
  {
    leftPadding = keyX;
    topPadding = keyY;
    this.width = width;
    rightPadding = (width - keyX - 1);
    
    this.precisionBits = precisionBits;
    if (precisionBits > 0) {
      round = (1 << precisionBits - 1);
    }
    
    this.subsampleBitsH = subsampleBitsH;
    numSubsamplesH = (1 << subsampleBitsH);
    int entriesH = width * numSubsamplesH;
    if (dataH.length != entriesH) {
      throw new IllegalArgumentException(JaiI18N.getString("InterpolationTable0"));
    }
    

    double prec = 1 << precisionBits;
    

    dataHd = ((double[])dataH.clone());
    dataHi = new int[entriesH];
    dataHf = new float[entriesH];
    for (int i = 0; i < entriesH; i++) {
      double d = dataHd[i];
      dataHi[i] = ((int)Math.round(d * prec));
      dataHf[i] = ((float)d);
    }
    
    if (dataV != null) {
      this.height = height;
      this.subsampleBitsV = subsampleBitsV;
      numSubsamplesV = (1 << subsampleBitsV);
      int entriesV = height * numSubsamplesV;
      if (dataV.length != entriesV) {
        throw new IllegalArgumentException(JaiI18N.getString("InterpolationTable1"));
      }
      

      dataVd = ((double[])dataV.clone());
      dataVi = new int[entriesV];
      dataVf = new float[entriesV];
      for (i = 0; i < entriesV; i++) {
        double d = dataVd[i];
        dataVi[i] = ((int)Math.round(d * prec));
        dataVf[i] = ((float)d);
      }
    } else {
      this.height = width;
      this.subsampleBitsV = subsampleBitsH;
      numSubsamplesV = numSubsamplesH;
      dataVd = dataHd;
      dataVf = dataHf;
      dataVi = dataHi;
    }
    bottomPadding = (this.height - keyY - 1);
  }
  


















  public InterpolationTable(int key, int width, int subsampleBits, int precisionBits, double[] data)
  {
    this(key, key, width, width, subsampleBits, subsampleBits, precisionBits, data, null);
  }
  





  public int getPrecisionBits()
  {
    return precisionBits;
  }
  
































  public int[] getHorizontalTableData()
  {
    return dataHi;
  }
  
































  public int[] getVerticalTableData()
  {
    return dataVi;
  }
  

































  public float[] getHorizontalTableDataFloat()
  {
    return dataHf;
  }
  

































  public float[] getVerticalTableDataFloat()
  {
    return dataVf;
  }
  

































  public double[] getHorizontalTableDataDouble()
  {
    return dataHd;
  }
  

































  public double[] getVerticalTableDataDouble()
  {
    return dataVd;
  }
  












  public int interpolateH(int[] samples, int xfrac)
  {
    int sum = 0;
    int offset = width * xfrac;
    
    for (int i = 0; i < width; i++) {
      sum += dataHi[(offset + i)] * samples[i];
    }
    return sum + round >> precisionBits;
  }
  












  public int interpolateV(int[] samples, int yfrac)
  {
    int sum = 0;
    int offset = width * yfrac;
    
    for (int i = 0; i < width; i++) {
      sum += dataVi[(offset + i)] * samples[i];
    }
    return sum + round >> precisionBits;
  }
  















  public int interpolateH(int s0, int s1, int xfrac)
  {
    int offset = 2 * xfrac;
    int sum = dataHi[offset] * s0;
    sum += dataHi[(offset + 1)] * s1;
    return sum + round >> precisionBits;
  }
  

















  public int interpolateH(int s_, int s0, int s1, int s2, int xfrac)
  {
    int offset = 4 * xfrac;
    int sum = dataHi[offset] * s_;
    sum += dataHi[(offset + 1)] * s0;
    sum += dataHi[(offset + 2)] * s1;
    sum += dataHi[(offset + 3)] * s2;
    return sum + round >> precisionBits;
  }
  















  public int interpolateV(int s0, int s1, int yfrac)
  {
    int offset = 2 * yfrac;
    int sum = dataVi[offset] * s0;
    sum += dataVi[(offset + 1)] * s1;
    return sum + round >> precisionBits;
  }
  

















  public int interpolateV(int s_, int s0, int s1, int s2, int yfrac)
  {
    int offset = 4 * yfrac;
    int sum = dataVi[offset] * s_;
    sum += dataVi[(offset + 1)] * s0;
    sum += dataVi[(offset + 2)] * s1;
    sum += dataVi[(offset + 3)] * s2;
    return sum + round >> precisionBits;
  }
  






















  public int interpolate(int s00, int s01, int s10, int s11, int xfrac, int yfrac)
  {
    int offsetX = 2 * xfrac;
    int sum0 = dataHi[offsetX] * s00 + dataHi[(offsetX + 1)] * s01;
    int sum1 = dataHi[offsetX] * s10 + dataHi[(offsetX + 1)] * s11;
    

    sum0 = sum0 + round >> precisionBits;
    sum1 = sum1 + round >> precisionBits;
    

    int offsetY = 2 * yfrac;
    int sum = dataVi[offsetY] * sum0 + dataVi[(offsetY + 1)] * sum1;
    
    return sum + round >> precisionBits;
  }
  





































  public int interpolate(int s__, int s_0, int s_1, int s_2, int s0_, int s00, int s01, int s02, int s1_, int s10, int s11, int s12, int s2_, int s20, int s21, int s22, int xfrac, int yfrac)
  {
    int offsetX = 4 * xfrac;
    int offsetX1 = offsetX + 1;
    int offsetX2 = offsetX + 2;
    int offsetX3 = offsetX + 3;
    
    long sum_ = dataHi[offsetX] * s__;
    sum_ += dataHi[offsetX1] * s_0;
    sum_ += dataHi[offsetX2] * s_1;
    sum_ += dataHi[offsetX3] * s_2;
    
    long sum0 = dataHi[offsetX] * s0_;
    sum0 += dataHi[offsetX1] * s00;
    sum0 += dataHi[offsetX2] * s01;
    sum0 += dataHi[offsetX3] * s02;
    
    long sum1 = dataHi[offsetX] * s1_;
    sum1 += dataHi[offsetX1] * s10;
    sum1 += dataHi[offsetX2] * s11;
    sum1 += dataHi[offsetX3] * s12;
    
    long sum2 = dataHi[offsetX] * s2_;
    sum2 += dataHi[offsetX1] * s20;
    sum2 += dataHi[offsetX2] * s21;
    sum2 += dataHi[offsetX3] * s22;
    

    sum_ = sum_ + round >> precisionBits;
    sum0 = sum0 + round >> precisionBits;
    sum1 = sum1 + round >> precisionBits;
    sum2 = sum2 + round >> precisionBits;
    

    int offsetY = 4 * yfrac;
    long sum = dataVi[offsetY] * sum_;
    sum += dataVi[(offsetY + 1)] * sum0;
    sum += dataVi[(offsetY + 2)] * sum1;
    sum += dataVi[(offsetY + 3)] * sum2;
    
    return (int)(sum + round >> precisionBits);
  }
  






































  public int interpolateF(int s__, int s_0, int s_1, int s_2, int s0_, int s00, int s01, int s02, int s1_, int s10, int s11, int s12, int s2_, int s20, int s21, int s22, int xfrac, int yfrac)
  {
    int offsetX = 4 * xfrac;
    
    float sum_ = dataHf[offsetX] * s__;
    sum_ += dataHf[(offsetX + 1)] * s_0;
    sum_ += dataHf[(offsetX + 2)] * s_1;
    sum_ += dataHf[(offsetX + 3)] * s_2;
    
    float sum0 = dataHf[offsetX] * s0_;
    sum0 += dataHf[(offsetX + 1)] * s00;
    sum0 += dataHf[(offsetX + 2)] * s01;
    sum0 += dataHf[(offsetX + 3)] * s02;
    
    float sum1 = dataHf[offsetX] * s1_;
    sum1 += dataHf[(offsetX + 1)] * s10;
    sum1 += dataHf[(offsetX + 2)] * s11;
    sum1 += dataHf[(offsetX + 3)] * s12;
    
    float sum2 = dataHf[offsetX] * s2_;
    sum2 += dataHf[(offsetX + 1)] * s20;
    sum2 += dataHf[(offsetX + 2)] * s21;
    sum2 += dataHf[(offsetX + 3)] * s22;
    

    int offsetY = 4 * yfrac;
    float sum = dataVf[offsetY] * sum_;
    sum += dataVf[(offsetY + 1)] * sum0;
    sum += dataVf[(offsetY + 2)] * sum1;
    sum += dataVf[(offsetY + 3)] * sum2;
    
    int isum = (int)sum;
    
    return isum;
  }
  











  public float interpolateH(float[] samples, float xfrac)
  {
    float sum = 0.0F;
    int ifrac = (int)(xfrac * numSubsamplesH);
    int offset = width * ifrac;
    
    for (int i = 0; i < width; i++) {
      sum += dataHf[(offset + i)] * samples[i];
    }
    return sum;
  }
  











  public float interpolateV(float[] samples, float yfrac)
  {
    float sum = 0.0F;
    int ifrac = (int)(yfrac * numSubsamplesV);
    int offset = width * ifrac;
    
    for (int i = 0; i < width; i++) {
      sum += dataVf[(offset + i)] * samples[i];
    }
    return sum;
  }
  













  public float interpolateH(float s0, float s1, float xfrac)
  {
    float sum = 0.0F;
    int ifrac = (int)(xfrac * numSubsamplesH);
    
    int offset = 2 * ifrac;
    
    sum = dataHf[offset] * s0 + dataHf[(offset + 1)] * s1;
    return sum;
  }
  
















  public float interpolateH(float s_, float s0, float s1, float s2, float xfrac)
  {
    int ifrac = (int)(xfrac * numSubsamplesH);
    
    int offset = 4 * ifrac;
    
    float sum = dataHf[offset] * s_;
    sum += dataHf[(offset + 1)] * s0;
    sum += dataHf[(offset + 2)] * s1;
    sum += dataHf[(offset + 3)] * s2;
    return sum;
  }
  













  public float interpolateV(float s0, float s1, float yfrac)
  {
    int ifrac = (int)(yfrac * numSubsamplesV);
    
    int offset = 2 * ifrac;
    float sum = dataVf[offset] * s0;
    sum += dataVf[(offset + 1)] * s1;
    return sum;
  }
  
















  public float interpolateV(float s_, float s0, float s1, float s2, float yfrac)
  {
    int ifrac = (int)(yfrac * numSubsamplesV);
    
    int offset = 4 * ifrac;
    float sum = dataVf[offset] * s_;
    sum += dataVf[(offset + 1)] * s0;
    sum += dataVf[(offset + 2)] * s1;
    sum += dataVf[(offset + 3)] * s2;
    return sum;
  }
  


















  public float interpolate(float s00, float s01, float s10, float s11, float xfrac, float yfrac)
  {
    int ifrac = (int)(xfrac * numSubsamplesH);
    
    int offsetX = 2 * ifrac;
    float sum0 = dataHf[offsetX] * s00 + dataHf[(offsetX + 1)] * s01;
    float sum1 = dataHf[offsetX] * s10 + dataHf[(offsetX + 1)] * s11;
    

    ifrac = (int)(yfrac * numSubsamplesV);
    int offsetY = 2 * ifrac;
    float sum = dataVf[offsetY] * sum0 + dataVf[(offsetY + 1)] * sum1;
    
    return sum;
  }
  

































  public float interpolate(float s__, float s_0, float s_1, float s_2, float s0_, float s00, float s01, float s02, float s1_, float s10, float s11, float s12, float s2_, float s20, float s21, float s22, float xfrac, float yfrac)
  {
    int ifrac = (int)(xfrac * numSubsamplesH);
    
    int offsetX = 4 * ifrac;
    int offsetX1 = offsetX + 1;
    int offsetX2 = offsetX + 2;
    int offsetX3 = offsetX + 3;
    
    float sum_ = dataHf[offsetX] * s__;
    sum_ += dataHf[offsetX1] * s_0;
    sum_ += dataHf[offsetX2] * s_1;
    sum_ += dataHf[offsetX3] * s_2;
    
    float sum0 = dataHf[offsetX] * s0_;
    sum0 += dataHf[offsetX1] * s00;
    sum0 += dataHf[offsetX2] * s01;
    sum0 += dataHf[offsetX3] * s02;
    
    float sum1 = dataHf[offsetX] * s1_;
    sum1 += dataHf[offsetX1] * s10;
    sum1 += dataHf[offsetX2] * s11;
    sum1 += dataHf[offsetX3] * s12;
    
    float sum2 = dataHf[offsetX] * s2_;
    sum2 += dataHf[offsetX1] * s20;
    sum2 += dataHf[offsetX2] * s21;
    sum2 += dataHf[offsetX3] * s22;
    

    ifrac = (int)(yfrac * numSubsamplesV);
    int offsetY = 4 * ifrac;
    float sum = dataVf[offsetY] * sum_;
    sum += dataVf[(offsetY + 1)] * sum0;
    sum += dataVf[(offsetY + 2)] * sum1;
    sum += dataVf[(offsetY + 3)] * sum2;
    
    return sum;
  }
  











  public double interpolateH(double[] samples, float xfrac)
  {
    double sum = 0.0D;
    int ifrac = (int)(xfrac * numSubsamplesH);
    int offset = width * ifrac;
    
    for (int i = 0; i < width; i++) {
      sum += dataHd[(offset + i)] * samples[i];
    }
    return sum;
  }
  











  public double interpolateV(double[] samples, float yfrac)
  {
    double sum = 0.0D;
    int ifrac = (int)(yfrac * numSubsamplesV);
    int offset = width * ifrac;
    
    for (int i = 0; i < width; i++) {
      sum += dataVd[(offset + i)] * samples[i];
    }
    return sum;
  }
  













  public double interpolateH(double s0, double s1, float xfrac)
  {
    double sum = 0.0D;
    int ifrac = (int)(xfrac * numSubsamplesH);
    
    int offset = 2 * ifrac;
    
    sum = dataHd[offset] * s0 + dataHd[(offset + 1)] * s1;
    return sum;
  }
  
















  public double interpolateH(double s_, double s0, double s1, double s2, float xfrac)
  {
    int ifrac = (int)(xfrac * numSubsamplesH);
    
    int offset = 4 * ifrac;
    
    double sum = dataHd[offset] * s_;
    sum += dataHd[(offset + 1)] * s0;
    sum += dataHd[(offset + 2)] * s1;
    sum += dataHd[(offset + 3)] * s2;
    return sum;
  }
  













  public double interpolateV(double s0, double s1, float yfrac)
  {
    int ifrac = (int)(yfrac * numSubsamplesV);
    
    int offset = 2 * ifrac;
    double sum = dataVd[offset] * s0;
    sum += dataVd[(offset + 1)] * s1;
    return sum;
  }
  
















  public double interpolateV(double s_, double s0, double s1, double s2, float yfrac)
  {
    int ifrac = (int)(yfrac * numSubsamplesV);
    
    int offset = 4 * ifrac;
    double sum = dataVd[offset] * s_;
    sum += dataVd[(offset + 1)] * s0;
    sum += dataVd[(offset + 2)] * s1;
    sum += dataVd[(offset + 3)] * s2;
    return sum;
  }
  


















  public double interpolate(double s00, double s01, double s10, double s11, float xfrac, float yfrac)
  {
    int ifrac = (int)(xfrac * numSubsamplesH);
    
    int offsetX = 2 * ifrac;
    double sum0 = dataHd[offsetX] * s00 + dataHd[(offsetX + 1)] * s01;
    double sum1 = dataHd[offsetX] * s10 + dataHd[(offsetX + 1)] * s11;
    

    ifrac = (int)(yfrac * numSubsamplesV);
    int offsetY = 2 * ifrac;
    double sum = dataVd[offsetY] * sum0 + dataVd[(offsetY + 1)] * sum1;
    
    return sum;
  }
  

































  public double interpolate(double s__, double s_0, double s_1, double s_2, double s0_, double s00, double s01, double s02, double s1_, double s10, double s11, double s12, double s2_, double s20, double s21, double s22, float xfrac, float yfrac)
  {
    int ifrac = (int)(xfrac * numSubsamplesH);
    
    int offsetX = 4 * ifrac;
    int offsetX1 = offsetX + 1;
    int offsetX2 = offsetX + 2;
    int offsetX3 = offsetX + 3;
    
    double sum_ = dataHd[offsetX] * s__;
    sum_ += dataHd[offsetX1] * s_0;
    sum_ += dataHd[offsetX2] * s_1;
    sum_ += dataHd[offsetX3] * s_2;
    
    double sum0 = dataHd[offsetX] * s0_;
    sum0 += dataHd[offsetX1] * s00;
    sum0 += dataHd[offsetX2] * s01;
    sum0 += dataHd[offsetX3] * s02;
    
    double sum1 = dataHd[offsetX] * s1_;
    sum1 += dataHd[offsetX1] * s10;
    sum1 += dataHd[offsetX2] * s11;
    sum1 += dataHd[offsetX3] * s12;
    
    double sum2 = dataHd[offsetX] * s2_;
    sum2 += dataHd[offsetX1] * s20;
    sum2 += dataHd[offsetX2] * s21;
    sum2 += dataHd[offsetX3] * s22;
    

    ifrac = (int)(yfrac * numSubsamplesV);
    int offsetY = 4 * ifrac;
    double sum = dataVd[offsetY] * sum_;
    sum += dataVd[(offsetY + 1)] * sum0;
    sum += dataVd[(offsetY + 2)] * sum1;
    sum += dataVd[(offsetY + 3)] * sum2;
    
    return sum;
  }
}
