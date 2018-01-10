package com.sun.media.jai.mlib;

import com.sun.media.jai.util.ImageUtil;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.PlanarImage;
import javax.media.jai.WarpOpImage;
import javax.media.jai.WarpPolynomial;



























































final class MlibWarpPolynomialOpImage
  extends WarpOpImage
{
  private double[] xCoeffs;
  private double[] yCoeffs;
  private int filter;
  private double preScaleX;
  private double preScaleY;
  private double postScaleX;
  private double postScaleY;
  
  public MlibWarpPolynomialOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, WarpPolynomial warp, Interpolation interp, int filter, double[] backgroundValues)
  {
    super(source, layout, config, true, extender, interp, warp, backgroundValues);
    







    float[] xc = warp.getXCoeffs();
    float[] yc = warp.getYCoeffs();
    int size = xc.length;
    
    xCoeffs = new double[size];
    yCoeffs = new double[size];
    for (int i = 0; i < size; i++) {
      xCoeffs[i] = xc[i];
      yCoeffs[i] = yc[i];
    }
    
    this.filter = filter;
    
    preScaleX = warp.getPreScaleX();
    preScaleY = warp.getPreScaleY();
    postScaleX = warp.getPostScaleX();
    postScaleY = warp.getPostScaleY();
  }
  

















  protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex)
  {
    Rectangle wrect = super.backwardMapRect(destRect, sourceIndex);
    




    wrect.setBounds(x - 1, y - 1, width + 2, height + 2);
    

    return wrect;
  }
  
















  public Raster computeTile(int tileX, int tileY)
  {
    Point org = new Point(tileXToX(tileX), tileYToY(tileY));
    

    WritableRaster dest = createWritableRaster(sampleModel, org);
    

    Rectangle rect = new Rectangle(x, y, tileWidth, tileHeight);
    Rectangle destRect = rect.intersection(computableBounds);
    Rectangle destRect1 = rect.intersection(getBounds());
    if (destRect.isEmpty()) {
      if (setBackground) {
        ImageUtil.fillBackground(dest, destRect1, backgroundValues);
      }
      return dest;
    }
    

    Rectangle srcRect = backwardMapRect(destRect, 0).intersection(getSourceImage(0).getBounds());
    

    if (srcRect.isEmpty()) {
      if (setBackground) {
        ImageUtil.fillBackground(dest, destRect1, backgroundValues);
      }
      return dest;
    }
    
    if (!destRect1.equals(destRect))
    {
      ImageUtil.fillBordersWithBackgroundValues(destRect1, destRect, dest, backgroundValues);
    }
    

    int l = interp == null ? 0 : interp.getLeftPadding();
    int r = interp == null ? 0 : interp.getRightPadding();
    int t = interp == null ? 0 : interp.getTopPadding();
    int b = interp == null ? 0 : interp.getBottomPadding();
    
    srcRect = new Rectangle(x - l, y - t, width + l + r, height + t + b);
    




    Raster[] sources = new Raster[1];
    sources[0] = (getBorderExtender() != null ? getSourceImage(0).getExtendedData(srcRect, extender) : getSourceImage(0).getData(srcRect));
    


    computeRect(sources, dest, destRect);
    

    if (getSourceImage(0).overlapsMultipleTiles(srcRect)) {
      recycleTile(sources[0]);
    }
    
    return dest;
  }
  





  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    

    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    
    MediaLibAccessor srcMA = new MediaLibAccessor(source, source.getBounds(), formatTag);
    
    MediaLibAccessor dstMA = new MediaLibAccessor(dest, destRect, formatTag);
    

    mediaLibImage[] srcMLI = srcMA.getMediaLibImages();
    mediaLibImage[] dstMLI = dstMA.getMediaLibImages();
    
    switch (dstMA.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      if (setBackground) {
        for (int i = 0; i < dstMLI.length; i++) {
          Image.PolynomialWarp2(dstMLI[i], srcMLI[i], xCoeffs, yCoeffs, x, y, source.getMinX(), source.getMinY(), preScaleX, preScaleY, postScaleX, postScaleY, filter, 0, intBackgroundValues);



        }
        


      }
      else
      {


        for (int i = 0; i < dstMLI.length; i++) {
          Image.PolynomialWarp(dstMLI[i], srcMLI[i], xCoeffs, yCoeffs, x, y, source.getMinX(), source.getMinY(), preScaleX, preScaleY, postScaleX, postScaleY, filter, 0);
          








          MlibUtils.clampImage(dstMLI[i], getColorModel());
        } }
      break;
    
    case 4: 
    case 5: 
      if (setBackground) {
        for (int i = 0; i < dstMLI.length; i++) {
          Image.PolynomialWarp2_Fp(dstMLI[i], srcMLI[i], xCoeffs, yCoeffs, x, y, source.getMinX(), source.getMinY(), preScaleX, preScaleY, postScaleX, postScaleY, filter, 0, backgroundValues);



        }
        


      }
      else
      {


        for (int i = 0; i < dstMLI.length; i++) {
          Image.PolynomialWarp_Fp(dstMLI[i], srcMLI[i], xCoeffs, yCoeffs, x, y, source.getMinX(), source.getMinY(), preScaleX, preScaleY, postScaleX, postScaleY, filter, 0);
        }
      }
      







      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("Generic2"));
    }
    
    if (dstMA.isDataCopy()) {
      dstMA.clampDataArrays();
      dstMA.copyDataToRaster();
    }
  }
}
