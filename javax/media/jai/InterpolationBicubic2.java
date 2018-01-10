package javax.media.jai;









public final class InterpolationBicubic2
  extends InterpolationTable
{
  private static final int PRECISION_BITS = 8;
  







  private static final float A = -1.0F;
  







  private static final float A3 = 1.0F;
  







  private static final float A2 = -2.0F;
  







  private static final float A0 = 1.0F;
  







  private static final float B3 = -1.0F;
  







  private static final float B2 = 5.0F;
  







  private static final float B1 = -8.0F;
  






  private static final float B0 = 4.0F;
  







  private static float[] dataHelper(int subsampleBits)
  {
    int one = 1 << subsampleBits;
    int arrayLength = one * 4;
    float[] tableValues = new float[arrayLength];
    

    float onef = one;
    
    int count = 0;
    for (int i = 0; i < one; i++) {
      float t = i;
      float f = i / onef;
      
      tableValues[(count++)] = bicubic(f + 1.0F);
      tableValues[(count++)] = bicubic(f);
      tableValues[(count++)] = bicubic(f - 1.0F);
      tableValues[(count++)] = bicubic(f - 2.0F);
    }
    

    return tableValues;
  }
  














  private static float bicubic(float x)
  {
    if (x < 0.0F) {
      x = -x;
    }
    

    if (x >= 1.0F) {
      return ((-1.0F * x + 5.0F) * x + -8.0F) * x + 4.0F;
    }
    return (1.0F * x + -2.0F) * x * x + 1.0F;
  }
  












  public InterpolationBicubic2(int subsampleBits)
  {
    super(1, 1, 4, 4, subsampleBits, subsampleBits, 8, dataHelper(subsampleBits), null);
  }
}
