package javax.media.jai;

import java.awt.image.Kernel;
import java.io.Serializable;







































public class KernelJAI
  implements Serializable
{
  public static final KernelJAI ERROR_FILTER_FLOYD_STEINBERG = new KernelJAI(3, 2, 1, 0, new float[] { 0.0F, 0.0F, 0.4375F, 0.1875F, 0.3125F, 0.0625F });
  











  public static final KernelJAI ERROR_FILTER_JARVIS = new KernelJAI(5, 3, 2, 0, new float[] { 0.0F, 0.0F, 0.0F, 0.14583333F, 0.104166664F, 0.0625F, 0.104166664F, 0.14583333F, 0.104166664F, 0.0625F, 0.020833334F, 0.0625F, 0.104166664F, 0.0625F, 0.020833334F });
  












  public static final KernelJAI ERROR_FILTER_STUCKI = new KernelJAI(5, 3, 2, 0, new float[] { 0.0F, 0.0F, 0.0F, 0.16666667F, 0.11904762F, 0.04761905F, 0.0952381F, 0.1904762F, 0.0952381F, 0.04761905F, 0.023809524F, 0.04761905F, 0.0952381F, 0.04761905F, 0.023809524F });
  







  public static final KernelJAI[] DITHER_MASK_441 = { new KernelJAI(4, 4, 1, 1, new float[] { 0.9375F, 0.4375F, 0.8125F, 0.3125F, 0.1875F, 0.6875F, 0.0625F, 0.5625F, 0.75F, 0.25F, 0.875F, 0.375F, 0.0F, 0.5F, 0.125F, 0.625F }) };
  










  public static final KernelJAI[] DITHER_MASK_443 = { new KernelJAI(4, 4, 1, 1, new float[] { 0.0F, 0.5F, 0.125F, 0.625F, 0.75F, 0.25F, 0.875F, 0.375F, 0.1875F, 0.6875F, 0.0625F, 0.5625F, 0.9375F, 0.4375F, 0.8125F, 0.3125F }), new KernelJAI(4, 4, 1, 1, new float[] { 0.625F, 0.125F, 0.5F, 0.0F, 0.375F, 0.875F, 0.25F, 0.75F, 0.5625F, 0.0625F, 0.6875F, 0.1875F, 0.3125F, 0.8125F, 0.4375F, 0.9375F }), new KernelJAI(4, 4, 1, 1, new float[] { 0.9375F, 0.4375F, 0.8125F, 0.3125F, 0.1875F, 0.6875F, 0.0625F, 0.5625F, 0.75F, 0.25F, 0.875F, 0.375F, 0.0F, 0.5F, 0.125F, 0.625F }) };
  



















  public static final KernelJAI GRADIENT_MASK_SOBEL_VERTICAL = new KernelJAI(3, 3, 1, 1, new float[] { -1.0F, -2.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F });
  







  public static final KernelJAI GRADIENT_MASK_SOBEL_HORIZONTAL = new KernelJAI(3, 3, 1, 1, new float[] { -1.0F, 0.0F, 1.0F, -2.0F, 0.0F, 2.0F, -1.0F, 0.0F, 1.0F });
  


  protected int width;
  


  protected int height;
  


  protected int xOrigin;
  


  protected int yOrigin;
  

  protected float[] data = null;
  

  protected float[] dataH = null;
  

  protected float[] dataV = null;
  

  protected boolean isSeparable = false;
  

  protected boolean isHorizontallySymmetric = false;
  

  protected boolean isVerticallySymmetric = false;
  

  protected KernelJAI rotatedKernel = null;
  


  private synchronized void checkSeparable()
  {
    float floatZeroTol = 1.0E-5F;
    
    if (isSeparable) return;
    if ((width <= 1) || (height <= 1)) { return;
    }
    









    float maxData = 0.0F;
    int imax = 0;int jmax = 0;
    
    for (int k = 0; k < data.length; k++) {
      float tmp = Math.abs(data[k]);
      if (tmp > maxData) {
        imax = k;
        maxData = tmp;
      }
    }
    



    if (maxData < floatZeroTol / data.length) {
      isSeparable = false;
      return;
    }
    
    float[] tmpRow = new float[width];
    float fac = 1.0F / data[imax];
    

    jmax = imax % width;
    imax /= width;
    

    for (int j = 0; j < width; j++) {
      tmpRow[j] = (data[(imax * width + j)] * fac);
    }
    



    int i = 0; for (int i0 = 0; i < height; i0 += width) {
      for (int j = 0; j < width; j++) {
        float tmp = Math.abs(data[(i0 + jmax)] * tmpRow[j] - data[(i0 + j)]);
        if (tmp > floatZeroTol) {
          isSeparable = false; return;
        }
      }
      i++;
    }
    








    dataH = tmpRow;
    dataV = new float[height];
    for (int i = 0; i < height; i++) {
      dataV[i] = data[(jmax + i * width)];
    }
    isSeparable = true;
    







    float sumH = 0.0F;float sumV = 0.0F;
    for (int j = 0; j < width; j++) sumH += dataH[j];
    for (int j = 0; j < height; j++) { sumV += dataV[j];
    }
    if ((Math.abs(sumH) >= Math.abs(sumV)) && (Math.abs(sumH) > floatZeroTol)) {
      fac = 1.0F / sumH;
      for (int j = 0; j < width; j++) dataH[j] *= fac;
      for (int j = 0; j < height; j++) dataV[j] *= sumH;
    } else if ((Math.abs(sumH) < Math.abs(sumV)) && (Math.abs(sumV) > floatZeroTol))
    {
      fac = 1.0F / sumV;
      for (int j = 0; j < width; j++) dataH[j] *= sumV;
      for (int j = 0; j < height; j++) { dataV[j] *= fac;
      }
    }
  }
  
  private void classifyKernel()
  {
    if (!isSeparable) {
      checkSeparable();
    }
    isHorizontallySymmetric = false;
    isVerticallySymmetric = false;
  }
  





















  public KernelJAI(int width, int height, int xOrigin, int yOrigin, float[] data)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    this.width = width;
    this.height = height;
    this.xOrigin = xOrigin;
    this.yOrigin = yOrigin;
    this.data = ((float[])data.clone());
    if (width <= 0) {
      throw new IllegalArgumentException(JaiI18N.getString("KernelJAI0"));
    }
    if (height <= 0) {
      throw new IllegalArgumentException(JaiI18N.getString("KernelJAI1"));
    }
    if (width * height != data.length) {
      throw new IllegalArgumentException(JaiI18N.getString("KernelJAI2"));
    }
    classifyKernel();
  }
  




























  public KernelJAI(int width, int height, int xOrigin, int yOrigin, float[] dataH, float[] dataV)
  {
    if ((dataH == null) || (dataV == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (width <= 0) {
      throw new IllegalArgumentException(JaiI18N.getString("KernelJAI0"));
    }
    
    if (height <= 0) {
      throw new IllegalArgumentException(JaiI18N.getString("KernelJAI1"));
    }
    
    if (width != dataH.length) {
      throw new IllegalArgumentException(JaiI18N.getString("KernelJAI3"));
    }
    
    if (height != dataV.length) {
      throw new IllegalArgumentException(JaiI18N.getString("KernelJAI4"));
    }
    
    this.width = width;
    this.height = height;
    this.xOrigin = xOrigin;
    this.yOrigin = yOrigin;
    this.dataH = ((float[])dataH.clone());
    this.dataV = ((float[])dataV.clone());
    data = new float[dataH.length * dataV.length];
    
    int rowOffset = 0;
    for (int i = 0; i < dataV.length; i++) {
      float vValue = dataV[i];
      for (int j = 0; j < dataH.length; j++) {
        data[(rowOffset + j)] = (vValue * dataH[j]);
      }
      rowOffset += dataH.length;
    }
    isSeparable = true;
    classifyKernel();
  }
  














  public KernelJAI(int width, int height, float[] data)
  {
    this(width, height, width / 2, height / 2, data);
  }
  






  public KernelJAI(Kernel k)
  {
    this(k.getWidth(), k.getHeight(), k.getXOrigin(), k.getYOrigin(), k.getKernelData(null));
  }
  

  public int getWidth()
  {
    return width;
  }
  
  public int getHeight()
  {
    return height;
  }
  
  public int getXOrigin()
  {
    return xOrigin;
  }
  
  public int getYOrigin()
  {
    return yOrigin;
  }
  
  public float[] getKernelData()
  {
    return (float[])data.clone();
  }
  




  public float[] getHorizontalKernelData()
  {
    if (dataH == null) {
      return null;
    }
    return (float[])dataH.clone();
  }
  




  public float[] getVerticalKernelData()
  {
    if (dataV == null) {
      return null;
    }
    return (float[])dataV.clone();
  }
  





  public float getElement(int xIndex, int yIndex)
  {
    if (!isSeparable) {
      return data[(yIndex * width + xIndex)];
    }
    return dataH[xIndex] * dataV[yIndex];
  }
  



  public boolean isSeparable()
  {
    return isSeparable;
  }
  
  public boolean isHorizontallySymmetric()
  {
    return isHorizontallySymmetric;
  }
  
  public boolean isVerticallySymmetric()
  {
    return isVerticallySymmetric;
  }
  


  public int getLeftPadding()
  {
    return xOrigin;
  }
  


  public int getRightPadding()
  {
    return width - xOrigin - 1;
  }
  


  public int getTopPadding()
  {
    return yOrigin;
  }
  


  public int getBottomPadding()
  {
    return height - yOrigin - 1;
  }
  





  public KernelJAI getRotatedKernel()
  {
    if (rotatedKernel == null) {
      if (isSeparable) {
        float[] rotDataH = new float[width];
        float[] rotDataV = new float[height];
        for (int i = 0; i < width; i++) {
          rotDataH[i] = dataH[(width - 1 - i)];
        }
        for (int i = 0; i < height; i++) {
          rotDataV[i] = dataV[(height - 1 - i)];
        }
        rotatedKernel = new KernelJAI(width, height, width - 1 - xOrigin, height - 1 - yOrigin, rotDataH, rotDataV);


      }
      else
      {


        int length = data.length;
        float[] newData = new float[data.length];
        for (int i = 0; i < length; i++) {
          newData[i] = data[(length - 1 - i)];
        }
        rotatedKernel = new KernelJAI(width, height, width - 1 - xOrigin, height - 1 - yOrigin, newData);
      }
    }
    




    return rotatedKernel;
  }
}
