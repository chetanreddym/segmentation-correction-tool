package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PackedImageData;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PlanarImage;

































































public class SubsampleBinaryToGrayOpImage
  extends GeometricOpImage
{
  protected float scaleX;
  protected float scaleY;
  protected float invScaleX;
  protected float invScaleY;
  private float floatTol;
  private int blockX;
  private int blockY;
  private int dWidth;
  private int dHeight;
  private int[] xValues;
  private int[] yValues;
  private int[] lut = new int['Ā'];
  





  protected byte[] lutGray;
  





  static ImageLayout layoutHelper(RenderedImage source, float scaleX, float scaleY, ImageLayout il, Map config)
  {
    ImageLayout layout = il == null ? new ImageLayout() : (ImageLayout)il.clone();
    




    int srcWidth = source.getWidth();
    int srcHeight = source.getHeight();
    
    float f_dw = scaleX * srcWidth;
    float f_dh = scaleY * srcHeight;
    float fTol = 0.1F * Math.min(scaleX / (f_dw + 1.0F), scaleY / (f_dh + 1.0F));
    
    int dWi = (int)f_dw;
    int dHi = (int)f_dh;
    


    if (Math.abs(Math.round(f_dw) - f_dw) < fTol) {
      dWi = Math.round(f_dw);
    }
    
    if (Math.abs(Math.round(f_dh) - f_dh) < fTol) {
      dHi = Math.round(f_dh);
    }
    

    layout.setMinX((int)(scaleX * source.getMinX()));
    layout.setMinY((int)(scaleY * source.getMinY()));
    
    layout.setWidth(dWi);
    layout.setHeight(dHi);
    

    SampleModel sm = layout.getSampleModel(null);
    
    if ((sm == null) || (sm.getDataType() != 0) || ((!(sm instanceof PixelInterleavedSampleModel)) && ((!(sm instanceof SinglePixelPackedSampleModel)) || (sm.getNumBands() != 1))))
    {





      sm = new PixelInterleavedSampleModel(0, 1, 1, 1, 1, new int[] { 0 });
    }
    





    layout.setSampleModel(sm);
    
    ColorModel cm = layout.getColorModel(null);
    
    if ((cm == null) || (!JDKWorkarounds.areCompatibleDataModels(sm, cm)))
    {

      layout.setColorModel(ImageUtil.getCompatibleColorModel(sm, config));
    }
    

    return layout;
  }
  

  private static Map configHelper(Map configuration)
  {
    Map config;
    
    Map config;
    if (configuration == null) {
      config = new RenderingHints(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
    }
    else
    {
      config = configuration;
      
      if (!config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)) {
        RenderingHints hints = (RenderingHints)configuration;
        config = (RenderingHints)hints.clone();
        config.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
      }
    }
    
    return config;
  }
  









































  public SubsampleBinaryToGrayOpImage(RenderedImage source, ImageLayout layout, Map config, float scaleX, float scaleY)
  {
    super(vectorize(source), layoutHelper(source, scaleX, scaleY, layout, config), configHelper(config), true, null, null, null);
    






    this.scaleX = scaleX;
    this.scaleY = scaleY;
    int srcMinX = source.getMinX();
    int srcMinY = source.getMinY();
    int srcWidth = source.getWidth();
    int srcHeight = source.getHeight();
    

    computeDestInfo(srcWidth, srcHeight);
    
    if (extender == null) {
      computableBounds = new Rectangle(0, 0, dWidth, dHeight);
    }
    else {
      computableBounds = getBounds();
    }
    


    buildLookupTables();
    

    computeXYValues(srcWidth, srcHeight, srcMinX, srcMinY);
  }
  













  public Point2D mapDestPoint(Point2D destPt)
  {
    if (destPt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Point2D pt = (Point2D)destPt.clone();
    
    pt.setLocation(destPt.getX() / scaleX, destPt.getY() / scaleY);
    
    return pt;
  }
  













  public Point2D mapSourcePoint(Point2D sourcePt)
  {
    if (sourcePt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Point2D pt = (Point2D)sourcePt.clone();
    
    pt.setLocation(sourcePt.getX() * scaleX, sourcePt.getY() * scaleY);
    
    return pt;
  }
  


















  protected Rectangle forwardMapRect(Rectangle sourceRect, int sourceIndex)
  {
    if (sourceRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sourceIndex != 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    

    int x0 = x - blockX + 1;
    int y0 = y - blockY + 1;
    x0 = x0 < 0 ? 0 : x0;
    y0 = y0 < 0 ? 0 : y0;
    
    int dx0 = (int)(x0 * scaleX);
    int dy0 = (int)(y0 * scaleY);
    while ((xValues[dx0] > x0) && (dx0 > 0)) {
      dx0--;
    }
    while ((yValues[dy0] > y0) && (dy0 > 0)) {
      dy0--;
    }
    
    int x1 = x + width - 1;
    int y1 = y + height - 1;
    
    int dx1 = Math.round(x1 * scaleX);
    int dy1 = Math.round(y1 * scaleY);
    dx1 = dx1 >= dWidth ? dWidth - 1 : dx1;
    dy1 = dy1 >= dHeight ? dHeight - 1 : dy1;
    while ((xValues[dx1] < x1) && (dx1 < dWidth - 1)) {
      dx1++;
    }
    while ((yValues[dy1] < y1) && (dy1 < dHeight - 1)) {
      dy1++;
    }
    
    dx0 += minX;
    dy0 += minY;
    dx1 += minX;
    dy1 += minY;
    

    return new Rectangle(dx0, dy0, dx1 - dx0 + 1, dy1 - dy0 + 1);
  }
  

















  protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex)
  {
    if (destRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sourceIndex != 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    


    int sx0 = xValues[(x - minX)];
    int sy0 = yValues[(y - minY)];
    int sx1 = xValues[(x - minX + width - 1)];
    int sy1 = yValues[(y - minY + height - 1)];
    
    return new Rectangle(sx0, sy0, sx1 - sx0 + blockX, sy1 - sy0 + blockY);
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    
    switch (source.getSampleModel().getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      byteLoop(source, dest, destRect);
      break;
    default: 
      throw new RuntimeException(JaiI18N.getString("SubsampleBinaryToGrayOpImage0"));
    }
  }
  
  private void byteLoop(Raster source, WritableRaster dest, Rectangle destRect)
  {
    PixelAccessor pa = new PixelAccessor(source.getSampleModel(), null);
    PackedImageData pid = pa.getPackedPixels(source, source.getBounds(), false, false);
    
    byte[] sourceData = data;
    int sourceDBOffset = offset;
    int dx = x;
    int dy = y;
    int dwi = width;
    int dhi = height;
    int sourceTransX = rect.x;
    int sourceTransY = rect.y;
    
    PixelInterleavedSampleModel destSM = (PixelInterleavedSampleModel)dest.getSampleModel();
    
    DataBufferByte destDB = (DataBufferByte)dest.getDataBuffer();
    
    int destTransX = dest.getSampleModelTranslateX();
    int destTransY = dest.getSampleModelTranslateY();
    int destScanlineStride = destSM.getScanlineStride();
    
    byte[] destData = destDB.getData();
    int destDBOffset = destDB.getOffset();
    
    int[] sbytenum = new int[dwi];
    int[] sstartbit = new int[dwi];
    
    int[] sAreaBitsOn = new int[dwi];
    for (int i = 0; i < dwi; i++) {
      int x = xValues[(dx + i - minX)];
      int sbitnum = bitOffset + (x - sourceTransX);
      sbytenum[i] = (sbitnum >> 3);
      sstartbit[i] = (sbitnum % 8);
    }
    
    for (int j = 0; j < dhi; j++)
    {
      for (int i = 0; i < dwi; i++) {
        sAreaBitsOn[i] = 0;
      }
      
      for (int y = yValues[(dy + j - minY)]; y < yValues[(dy + j - minY)] + blockY; y++)
      {
        int sourceYOffset = (y - sourceTransY) * lineStride + sourceDBOffset;
        

        int delement = 0;
        for (int i = 0; i < dwi; i++) {
          delement = 0;
          int sendbiti = sstartbit[i] + blockX - 1;
          int sendbytenumi = sbytenum[i] + (sendbiti >> 3);
          sendbiti %= 8;
          
          int selement = 0xFF & sourceData[(sourceYOffset + sbytenum[i])];
          
          if (sbytenum[i] == sendbytenumi) {
            selement <<= 24 + sstartbit[i];
            selement >>>= 31 - sendbiti + sstartbit[i];
            delement += lut[selement];
          } else {
            selement <<= 24 + sstartbit[i];
            selement >>>= 24;
            delement += lut[selement];
            for (int b = sbytenum[i] + 1; b < sendbytenumi; b++) {
              selement = 0xFF & sourceData[(sourceYOffset + b)];
              delement += lut[selement];
            }
            
            selement = 0xFF & sourceData[(sourceYOffset + sendbytenumi)];
            selement >>>= 7 - sendbiti;
            delement += lut[selement];
          }
          sAreaBitsOn[i] += delement;
        }
      }
      int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
      

      destYOffset += dx - destTransX;
      

      for (int i = 0; i < dwi; i++) {
        destData[(destYOffset + i)] = lutGray[sAreaBitsOn[i]];
      }
    }
  }
  


  private void computeDestInfo(int srcWidth, int srcHeight)
  {
    invScaleX = (1.0F / scaleX);
    invScaleY = (1.0F / scaleY);
    blockX = ((int)Math.ceil(invScaleX));
    blockY = ((int)Math.ceil(invScaleY));
    

    float f_dw = scaleX * srcWidth;
    float f_dh = scaleY * srcHeight;
    floatTol = (0.1F * Math.min(scaleX / (f_dw + 1.0F), scaleY / (f_dh + 1.0F)));
    
    dWidth = ((int)f_dw);
    dHeight = ((int)f_dh);
    


    if (Math.abs(Math.round(f_dw) - f_dw) < floatTol) {
      dWidth = Math.round(f_dw);
    }
    
    if (Math.abs(Math.round(f_dh) - f_dh) < floatTol) {
      dHeight = Math.round(f_dh);
    }
    
    if (Math.abs(Math.round(invScaleX) - invScaleX) < floatTol) {
      invScaleX = Math.round(invScaleX);
      blockX = ((int)invScaleX);
    }
    
    if (Math.abs(Math.round(invScaleY) - invScaleY) < floatTol) {
      invScaleY = Math.round(invScaleY);
      blockY = ((int)invScaleY);
    }
  }
  





  private final void buildLookupTables()
  {
    lut[0] = 0;lut[1] = 1;lut[2] = 1;lut[3] = 2;
    lut[4] = 1;lut[5] = 2;lut[6] = 2;lut[7] = 3;
    lut[8] = 1;lut[9] = 2;lut[10] = 2;lut[11] = 3;
    lut[12] = 2;lut[13] = 3;lut[14] = 3;lut[15] = 4;
    for (int i = 16; i < 256; i++) {
      lut[i] = (lut[(i & 0xF)] + lut[(i >> 4 & 0xF)]);
    }
    

    if (lutGray != null) return;
    lutGray = new byte[blockX * blockY + 1];
    for (int i = 0; i < lutGray.length; i++) {
      int tmp = Math.round(255.0F * i / (lutGray.length - 1.0F));
      lutGray[i] = (tmp > 255 ? -1 : (byte)tmp);
    }
    

    if (isMinWhite(getSourceImage(0).getColorModel())) {
      for (int i = 0; i < lutGray.length; i++) {
        lutGray[i] = ((byte)(255 - (0xFF & lutGray[i])));
      }
    }
  }
  


  private void computeXYValues(int srcWidth, int srcHeight, int srcMinX, int srcMinY)
  {
    if ((xValues == null) || (yValues == null)) {
      xValues = new int[dWidth];
      yValues = new int[dHeight];
    }
    

    for (int i = 0; i < dWidth; i++) {
      float tmp = invScaleX * i;
      xValues[i] = Math.round(tmp);
    }
    if (xValues[(dWidth - 1)] + blockX > srcWidth) {
      xValues[(dWidth - 1)] -= 1;
    }
    
    for (int i = 0; i < dHeight; i++) {
      float tmp = invScaleY * i;
      yValues[i] = Math.round(tmp);
    }
    if (yValues[(dHeight - 1)] + blockY > srcHeight) {
      yValues[(dHeight - 1)] -= 1;
    }
    

    if (srcMinX != 0)
      for (int i = 0; i < dWidth; i++) xValues[i] += srcMinX;
    if (srcMinY != 0) {
      for (int i = 0; i < dHeight; i++) { yValues[i] += srcMinY;
      }
    }
  }
  

  static boolean isMinWhite(ColorModel cm)
  {
    if ((cm == null) || (!(cm instanceof IndexColorModel))) { return false;
    }
    byte[] red = new byte['Ā'];
    ((IndexColorModel)cm).getReds(red);
    return red[0] == -1;
  }
}
