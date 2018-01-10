package javax.media.jai;

import java.awt.Rectangle;
import java.awt.image.Raster;









































































public final class UnpackedImageData
{
  public final Raster raster;
  public final Rectangle rect;
  public final int type;
  public final Object data;
  public final int pixelStride;
  public final int lineStride;
  public final int[] bandOffsets;
  public final boolean convertToDest;
  
  public UnpackedImageData(Raster raster, Rectangle rect, int type, Object data, int pixelStride, int lineStride, int[] bandOffsets, boolean convertToDest)
  {
    this.raster = raster;
    this.rect = rect;
    this.type = type;
    this.data = data;
    this.pixelStride = pixelStride;
    this.lineStride = lineStride;
    this.bandOffsets = bandOffsets;
    this.convertToDest = convertToDest;
  }
  






  public byte[][] getByteData()
  {
    return type == 0 ? (byte[][])data : (byte[][])null;
  }
  





  public byte[] getByteData(int b)
  {
    byte[][] d = getByteData();
    return d == null ? null : d[b];
  }
  






  public short[][] getShortData()
  {
    return (type == 1) || (type == 2) ? (short[][])data : (short[][])null;
  }
  







  public short[] getShortData(int b)
  {
    short[][] d = getShortData();
    return d == null ? null : d[b];
  }
  






  public int[][] getIntData()
  {
    return type == 3 ? (int[][])data : (int[][])null;
  }
  





  public int[] getIntData(int b)
  {
    int[][] d = getIntData();
    return d == null ? null : d[b];
  }
  






  public float[][] getFloatData()
  {
    return type == 4 ? (float[][])data : (float[][])null;
  }
  





  public float[] getFloatData(int b)
  {
    float[][] d = getFloatData();
    return d == null ? null : d[b];
  }
  






  public double[][] getDoubleData()
  {
    return type == 5 ? (double[][])data : (double[][])null;
  }
  





  public double[] getDoubleData(int b)
  {
    double[][] d = getDoubleData();
    return d == null ? null : d[b];
  }
  



  public int getOffset(int b)
  {
    return bandOffsets[b];
  }
  


  public int getMinOffset()
  {
    int min = bandOffsets[0];
    for (int i = 1; i < bandOffsets.length; i++) {
      min = Math.min(min, bandOffsets[i]);
    }
    return min;
  }
  


  public int getMaxOffset()
  {
    int max = bandOffsets[0];
    for (int i = 1; i < bandOffsets.length; i++) {
      max = Math.max(max, bandOffsets[i]);
    }
    return max;
  }
}
