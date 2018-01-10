package javax.media.jai;































public class ColorCube
  extends LookupTableJAI
{
  public static final ColorCube BYTE_496 = createColorCube(0, 38, new int[] { 4, 9, 6 });
  






  public static final ColorCube BYTE_855 = createColorCube(0, 54, new int[] { 8, 5, 5 });
  



  private int[] dimension;
  



  private int[] dimsLessOne;
  



  private int[] multipliers;
  



  private int adjustedOffset;
  



  private int dataType;
  



  private int numBands;
  




  public static ColorCube createColorCube(int dataType, int offset, int[] dimension)
  {
    ColorCube colorCube;
    


    ColorCube colorCube;
    


    ColorCube colorCube;
    


    ColorCube colorCube;
    


    ColorCube colorCube;
    


    ColorCube colorCube;
    


    switch (dataType) {
    case 0: 
      colorCube = createColorCubeByte(offset, dimension);
      break;
    case 2: 
      colorCube = createColorCubeShort(offset, dimension);
      break;
    case 1: 
      colorCube = createColorCubeUShort(offset, dimension);
      break;
    case 3: 
      colorCube = createColorCubeInt(offset, dimension);
      break;
    case 4: 
      colorCube = createColorCubeFloat(offset, dimension);
      break;
    case 5: 
      colorCube = createColorCubeDouble(offset, dimension);
      break;
    default: 
      throw new RuntimeException(JaiI18N.getString("ColorCube0"));
    }
    
    return colorCube;
  }
  










  public static ColorCube createColorCube(int dataType, int[] dimension)
  {
    if (dimension == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return createColorCube(dataType, 0, dimension);
  }
  












  private static ColorCube createColorCubeByte(int offset, int[] dimension)
  {
    ColorCube colorCube = new ColorCube(createDataArrayByte(offset, dimension), offset);
    
    colorCube.initFields(offset, dimension);
    return colorCube;
  }
  












  private static ColorCube createColorCubeShort(int offset, int[] dimension)
  {
    ColorCube colorCube = new ColorCube(createDataArrayShort(offset, dimension), offset, false);
    

    colorCube.initFields(offset, dimension);
    return colorCube;
  }
  













  private static ColorCube createColorCubeUShort(int offset, int[] dimension)
  {
    ColorCube colorCube = new ColorCube(createDataArrayUShort(offset, dimension), offset, true);
    

    colorCube.initFields(offset, dimension);
    return colorCube;
  }
  












  private static ColorCube createColorCubeInt(int offset, int[] dimension)
  {
    ColorCube colorCube = new ColorCube(createDataArrayInt(offset, dimension), offset);
    
    colorCube.initFields(offset, dimension);
    return colorCube;
  }
  












  private static ColorCube createColorCubeFloat(int offset, int[] dimension)
  {
    ColorCube colorCube = new ColorCube(createDataArrayFloat(offset, dimension), offset);
    
    colorCube.initFields(offset, dimension);
    return colorCube;
  }
  












  private static ColorCube createColorCubeDouble(int offset, int[] dimension)
  {
    ColorCube colorCube = new ColorCube(createDataArrayDouble(offset, dimension), offset);
    

    colorCube.initFields(offset, dimension);
    return colorCube;
  }
  























  private static Object createDataArray(int dataType, int offset, int[] dimension)
  {
    int nbands = dimension.length;
    if (nbands == 0) {
      throw new RuntimeException(JaiI18N.getString("ColorCube1"));
    }
    

    for (int band = 0; band < nbands; band++) {
      if (dimension[band] == 0) {
        throw new RuntimeException(JaiI18N.getString("ColorCube2"));
      }
    }
    

    int[] dimensionAbs = new int[nbands];
    for (int band = 0; band < nbands; band++) {
      dimensionAbs[band] = Math.abs(dimension[band]);
    }
    

    double floatSize = dimensionAbs[0];
    for (int band = 1; band < nbands; band++) {
      floatSize *= dimensionAbs[band];
    }
    if (floatSize > 2.147483647E9D)
    {


      throw new RuntimeException(JaiI18N.getString("ColorCube3"));
    }
    int size = (int)floatSize;
    Object dataArray;
    Object dataArray;
    Object dataArray;
    Object dataArray;
    Object dataArray;
    double dataMin; double dataMax; Object dataArray; switch (dataType) {
    case 0: 
      double dataMin = 0.0D;
      double dataMax = 255.0D;
      dataArray = new byte[nbands][size];
      break;
    case 2: 
      double dataMin = -32768.0D;
      double dataMax = 32767.0D;
      dataArray = new short[nbands][size];
      break;
    case 1: 
      double dataMin = 0.0D;
      double dataMax = 65535.0D;
      dataArray = new short[nbands][size];
      break;
    case 3: 
      double dataMin = -2.147483648E9D;
      double dataMax = 2.147483647E9D;
      dataArray = new int[nbands][size];
      break;
    case 4: 
      double dataMin = -3.4028234663852886E38D;
      double dataMax = 3.4028234663852886E38D;
      dataArray = new float[nbands][size];
      break;
    case 5: 
      dataMin = -1.7976931348623157E308D;
      dataMax = Double.MAX_VALUE;
      dataArray = new double[nbands][size];
      break;
    default: 
      throw new RuntimeException(JaiI18N.getString("ColorCube7"));
    }
    
    
    if (size + offset > dataMax) {
      throw new RuntimeException(JaiI18N.getString("ColorCube4"));
    }
    

    int[] multipliers = new int[nbands];
    multipliers[0] = 1;
    for (int band = 1; band < nbands; band++) {
      multipliers[band] = (multipliers[(band - 1)] * dimensionAbs[(band - 1)]);
    }
    

    for (int band = 0; band < nbands; band++)
    {
      int idimension = dimensionAbs[band];
      double delta;
      double delta; if (idimension == 1)
      {
        delta = 0.0D; } else { double delta;
        if ((dataType == 4) || (dataType == 5))
        {
          delta = 1.0D / (idimension - 1);
        } else {
          delta = (dataMax - dataMin) / (idimension - 1);
        }
      }
      double start;
      double start;
      if (dimension[band] < 0) {
        delta = -delta;
        start = dataMax;
      } else {
        start = dataMin;
      }
      int repeatCount = multipliers[band];
      
      byte[][] byteData;
      int index;
      switch (dataType) {
      case 0: 
        byteData = (byte[][])dataArray;
        index = 0; }
      while (index < size) {
        double val = start;
        for (int i = 0; i < idimension; i++) {
          for (int j = 0; j < repeatCount; j++) {
            byteData[band][index] = ((byte)((int)(val + 0.5D) & 0xFF));
            
            index++;
          }
          val += delta;
        }
        




        continue;short[][] shortData = (short[][])dataArray;
        int index = 0;
        while (index < size) {
          double val = start;
          for (int i = 0; i < idimension; i++) {
            for (int j = 0; j < repeatCount; j++) {
              shortData[band][index] = ((short)(int)(val + 0.5D));
              index++;
            }
            val += delta;
          }
          



          continue;int[][] intData = (int[][])dataArray;
          int index = 0;
          while (index < size) {
            double val = start;
            for (int i = 0; i < idimension; i++) {
              for (int j = 0; j < repeatCount; j++) {
                intData[band][index] = ((int)(val + 0.5D));
                index++;
              }
              val += delta;
            }
            



            continue;float[][] floatData = (float[][])dataArray;
            int index = 0;
            while (index < size) {
              double val = start;
              for (int i = 0; i < idimension; i++) {
                for (int j = 0; j < repeatCount; j++) {
                  floatData[band][index] = ((float)val);
                  index++;
                }
                val += delta;
              }
              



              continue;double[][] doubleData = (double[][])dataArray;
              int index = 0;
              while (index < size) {
                double val = start;
                for (int i = 0; i < idimension; i++) {
                  for (int j = 0; j < repeatCount; j++) {
                    doubleData[band][index] = val;
                    index++;
                  }
                  val += delta;
                }
                


                continue;throw new RuntimeException(JaiI18N.getString("ColorCube5"));
              }
            }
          } } } }
    return dataArray;
  }
  












  private static byte[][] createDataArrayByte(int offset, int[] dimension)
  {
    return (byte[][])createDataArray(0, offset, dimension);
  }
  













  private static short[][] createDataArrayShort(int offset, int[] dimension)
  {
    return (short[][])createDataArray(2, offset, dimension);
  }
  













  private static short[][] createDataArrayUShort(int offset, int[] dimension)
  {
    return (short[][])createDataArray(1, offset, dimension);
  }
  













  private static int[][] createDataArrayInt(int offset, int[] dimension)
  {
    return (int[][])createDataArray(3, offset, dimension);
  }
  













  private static float[][] createDataArrayFloat(int offset, int[] dimension)
  {
    return (float[][])createDataArray(4, offset, dimension);
  }
  













  private static double[][] createDataArrayDouble(int offset, int[] dimension)
  {
    return (double[][])createDataArray(5, offset, dimension);
  }
  









  protected ColorCube(byte[][] data, int offset)
  {
    super(data, offset);
  }
  










  protected ColorCube(short[][] data, int offset, boolean isUShort)
  {
    super(data, offset, isUShort);
  }
  








  protected ColorCube(int[][] data, int offset)
  {
    super(data, offset);
  }
  








  protected ColorCube(float[][] data, int offset)
  {
    super(data, offset);
  }
  








  protected ColorCube(double[][] data, int offset)
  {
    super(data, offset);
  }
  






  private void initFields(int offset, int[] dimension)
  {
    this.dimension = dimension;
    

    multipliers = new int[dimension.length];
    dimsLessOne = new int[dimension.length];
    

    multipliers[0] = 1;
    for (int i = 1; i < multipliers.length; i++) {
      multipliers[i] = (multipliers[(i - 1)] * Math.abs(dimension[(i - 1)]));
    }
    


    for (int i = 0; i < multipliers.length; i++) {
      if (dimension[i] < 0) {
        multipliers[i] = (-multipliers[i]);
      }
      dimsLessOne[i] = (Math.abs(dimension[i]) - 1);
    }
    

    adjustedOffset = offset;
    for (int i = 0; i < dimension.length; i++) {
      if ((dimension[i] > 1) && (multipliers[i] < 0)) {
        adjustedOffset += Math.abs(multipliers[i]) * dimsLessOne[i];
      }
    }
    


    dataType = getDataType();
    numBands = getNumBands();
  }
  




  public int[] getDimension()
  {
    return dimension;
  }
  



  public int[] getDimsLessOne()
  {
    return dimsLessOne;
  }
  



  public int[] getMultipliers()
  {
    return multipliers;
  }
  




  public int getAdjustedOffset()
  {
    return adjustedOffset;
  }
  











  public int findNearestEntry(float[] pixel)
  {
    int index = -1;
    
    index = adjustedOffset;
    
    switch (dataType) {
    case 0: 
      for (int band = 0; band < numBands; band++) {
        int tmp = (int)(pixel[band] * dimsLessOne[band]);
        
        if ((tmp & 0xFF) > 127) {
          tmp += 256;
        }
        
        index += (tmp >> 8) * multipliers[band];
      }
      break;
    case 2: 
      for (int band = 0; band < numBands; band++) {
        int tmp = (int)(pixel[band] - -32768.0F) * dimsLessOne[band];
        

        if ((tmp & 0xFFFF) > 32767) {
          tmp += 65536;
        }
        
        index += (tmp >> 16) * multipliers[band];
      }
      break;
    case 1: 
      for (int band = 0; band < numBands; band++) {
        int tmp = (int)(pixel[band] * dimsLessOne[band]);
        
        if ((tmp & 0xFFFF) > 32767) {
          tmp += 65536;
        }
        
        index += (tmp >> 16) * multipliers[band];
      }
      break;
    case 3: 
      for (int band = 0; band < numBands; band++) {
        long tmp = ((pixel[band] - -2.14748365E9F) * dimsLessOne[band]);
        

        if (tmp > 2147483647L) {
          tmp += 0L;
        }
        
        index += (int)(tmp >> 32) * multipliers[band];
      }
      break;
    case 4: 
      for (int band = 0; band < numBands; band++) {
        float ftmp = pixel[band] * dimsLessOne[band];
        int itmp = (int)ftmp;
        
        if (ftmp - itmp >= 0.5F) {
          itmp++;
        }
        
        index += itmp * multipliers[band];
      }
      break;
    case 5: 
      for (int band = 0; band < numBands; band++) {
        double ftmp = pixel[band] * dimsLessOne[band];
        int itmp = (int)ftmp;
        
        if (ftmp - itmp >= 0.5D) {
          itmp++;
        }
        
        index += itmp * multipliers[band];
      }
      break;
    default: 
      throw new RuntimeException(JaiI18N.getString("ColorCube6"));
    }
    
    return index;
  }
}
