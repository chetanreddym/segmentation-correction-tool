package com.sun.media.jai.mlib;

import com.sun.media.jai.util.ImageUtil;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import com.sun.medialib.mlib.mediaLibImageInterpTable;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationTable;
import javax.media.jai.PlanarImage;
import javax.media.jai.WarpGrid;
import javax.media.jai.WarpOpImage;
























































final class MlibWarpGridTableOpImage
  extends WarpOpImage
{
  private int xStart;
  private int xStep;
  private int xNumCells;
  private int xEnd;
  private int yStart;
  private int yStep;
  private int yNumCells;
  private int yEnd;
  private float[] xWarpPos;
  private float[] yWarpPos;
  private mediaLibImageInterpTable mlibInterpTableI;
  private mediaLibImageInterpTable mlibInterpTableF;
  private mediaLibImageInterpTable mlibInterpTableD;
  
  public MlibWarpGridTableOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, WarpGrid warp, Interpolation interp, double[] backgroundValues)
  {
    super(source, layout, config, true, extender, interp, warp, backgroundValues);
    







    mlibInterpTableI = null;
    mlibInterpTableF = null;
    mlibInterpTableD = null;
    
    xStart = warp.getXStart();
    xStep = warp.getXStep();
    xNumCells = warp.getXNumCells();
    xEnd = (xStart + xStep * xNumCells);
    
    yStart = warp.getYStart();
    yStep = warp.getYStep();
    yNumCells = warp.getYNumCells();
    yEnd = (yStart + yStep * yNumCells);
    
    xWarpPos = warp.getXWarpPos();
    yWarpPos = warp.getYWarpPos();
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
    

    Rectangle rect0 = new Rectangle(x, y, tileWidth, tileHeight);
    Rectangle destRect = rect0.intersection(computableBounds);
    Rectangle destRect1 = rect0.intersection(getBounds());
    
    if (destRect.isEmpty()) {
      if (setBackground) {
        ImageUtil.fillBackground(dest, destRect1, backgroundValues);
      }
      return dest;
    }
    
    if (!destRect1.equals(destRect))
    {
      ImageUtil.fillBordersWithBackgroundValues(destRect1, destRect, dest, backgroundValues);
    }
    
    Raster[] sources = new Raster[1];
    Rectangle srcBounds = getSourceImage(0).getBounds();
    
    int x0 = x;
    int x1 = x0 + width - 1;
    int y0 = y;
    int y1 = y0 + height - 1;
    
    if ((x0 >= xEnd) || (x1 < xStart) || (y0 >= yEnd) || (y1 < yStart))
    {
      Rectangle rect = srcBounds.intersection(destRect);
      
      if (!rect.isEmpty()) {
        sources[0] = getSourceImage(0).getData(rect);
        copyRect(sources, dest, rect);
        

        if (getSourceImage(0).overlapsMultipleTiles(rect)) {
          recycleTile(sources[0]);
        }
      }
      
      return dest;
    }
    
    if (x0 < xStart) {
      Rectangle rect = srcBounds.intersection(new Rectangle(x0, y0, xStart - x0, y1 - y0 + 1));
      

      if (!rect.isEmpty()) {
        sources[0] = getSourceImage(0).getData(rect);
        copyRect(sources, dest, rect);
        

        if (getSourceImage(0).overlapsMultipleTiles(rect)) {
          recycleTile(sources[0]);
        }
      }
      
      x0 = xStart;
    }
    
    if (x1 >= xEnd) {
      Rectangle rect = srcBounds.intersection(new Rectangle(xEnd, y0, x1 - xEnd + 1, y1 - y0 + 1));
      

      if (!rect.isEmpty()) {
        sources[0] = getSourceImage(0).getData(rect);
        copyRect(sources, dest, rect);
        

        if (getSourceImage(0).overlapsMultipleTiles(rect)) {
          recycleTile(sources[0]);
        }
      }
      
      x1 = xEnd - 1;
    }
    
    if (y0 < yStart) {
      Rectangle rect = srcBounds.intersection(new Rectangle(x0, y0, x1 - x0 + 1, yStart - y0));
      

      if (!rect.isEmpty()) {
        sources[0] = getSourceImage(0).getData(rect);
        copyRect(sources, dest, rect);
        

        if (getSourceImage(0).overlapsMultipleTiles(rect)) {
          recycleTile(sources[0]);
        }
      }
      
      y0 = yStart;
    }
    
    if (y1 >= yEnd) {
      Rectangle rect = srcBounds.intersection(new Rectangle(x0, yEnd, x1 - x0 + 1, y1 - yEnd + 1));
      

      if (!rect.isEmpty()) {
        sources[0] = getSourceImage(0).getData(rect);
        copyRect(sources, dest, rect);
        

        if (getSourceImage(0).overlapsMultipleTiles(rect)) {
          recycleTile(sources[0]);
        }
      }
      
      y1 = yEnd - 1;
    }
    

    destRect = new Rectangle(x0, y0, x1 - x0 + 1, y1 - y0 + 1);
    

    Rectangle srcRect = backwardMapRect(destRect, 0).intersection(srcBounds);
    
    if (!srcRect.isEmpty())
    {
      int l = interp == null ? 0 : interp.getLeftPadding();
      int r = interp == null ? 0 : interp.getRightPadding();
      int t = interp == null ? 0 : interp.getTopPadding();
      int b = interp == null ? 0 : interp.getBottomPadding();
      
      srcRect = new Rectangle(x - l, y - t, width + l + r, height + t + b);
      



      sources[0] = (getBorderExtender() != null ? getSourceImage(0).getExtendedData(srcRect, extender) : getSourceImage(0).getData(srcRect));
      


      computeRect(sources, dest, destRect);
      

      if (getSourceImage(0).overlapsMultipleTiles(srcRect)) {
        recycleTile(sources[0]);
      }
    }
    
    return dest;
  }
  






  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    
    Raster source = sources[0];
    
    MediaLibAccessor srcMA = new MediaLibAccessor(source, source.getBounds(), formatTag);
    
    MediaLibAccessor dstMA = new MediaLibAccessor(dest, destRect, formatTag);
    

    mediaLibImage[] srcMLI = srcMA.getMediaLibImages();
    mediaLibImage[] dstMLI = dstMA.getMediaLibImages();
    
    switch (dstMA.getDataType())
    {

    case 0: 
    case 1: 
    case 2: 
    case 3: 
      if (mlibInterpTableI == null) {
        InterpolationTable jtable = (InterpolationTable)interp;
        mlibInterpTableI = new mediaLibImageInterpTable(3, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), jtable.getHorizontalTableData(), jtable.getVerticalTableData());
      }
      











      if (setBackground) {
        for (int i = 0; i < dstMLI.length; i++) {
          Image.GridWarpTable2(dstMLI[i], srcMLI[i], xWarpPos, yWarpPos, source.getMinX(), source.getMinY(), xStart - x, xStep, xNumCells, yStart - y, yStep, yNumCells, mlibInterpTableI, 0, intBackgroundValues);



        }
        


      }
      else
      {


        for (int i = 0; i < dstMLI.length; i++) {
          Image.GridWarpTable(dstMLI[i], srcMLI[i], xWarpPos, yWarpPos, source.getMinX(), source.getMinY(), xStart - x, xStep, xNumCells, yStart - y, yStep, yNumCells, mlibInterpTableI, 0);
          








          MlibUtils.clampImage(dstMLI[i], getColorModel());
        }
      }
      break;
    
    case 4: 
      if (mlibInterpTableF == null) {
        InterpolationTable jtable = (InterpolationTable)interp;
        mlibInterpTableF = new mediaLibImageInterpTable(4, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), jtable.getHorizontalTableDataFloat(), jtable.getVerticalTableDataFloat());
      }
      











      if (setBackground) {
        for (int i = 0; i < dstMLI.length; i++) {
          Image.GridWarpTable2_Fp(dstMLI[i], srcMLI[i], xWarpPos, yWarpPos, source.getMinX(), source.getMinY(), xStart - x, xStep, xNumCells, yStart - y, yStep, yNumCells, mlibInterpTableF, 0, backgroundValues);



        }
        


      }
      else
      {


        for (int i = 0; i < dstMLI.length; i++) {
          Image.GridWarpTable_Fp(dstMLI[i], srcMLI[i], xWarpPos, yWarpPos, source.getMinX(), source.getMinY(), xStart - x, xStep, xNumCells, yStart - y, yStep, yNumCells, mlibInterpTableF, 0);
        }
      }
      







      break;
    
    case 5: 
      if (mlibInterpTableD == null) {
        InterpolationTable jtable = (InterpolationTable)interp;
        mlibInterpTableD = new mediaLibImageInterpTable(5, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), jtable.getHorizontalTableDataDouble(), jtable.getVerticalTableDataDouble());
      }
      










      if (setBackground) {
        for (int i = 0; i < dstMLI.length; i++) {
          Image.GridWarpTable2_Fp(dstMLI[i], srcMLI[i], xWarpPos, yWarpPos, source.getMinX(), source.getMinY(), xStart - x, xStep, xNumCells, yStart - y, yStep, yNumCells, mlibInterpTableD, 0, backgroundValues);



        }
        


      }
      else
      {


        for (int i = 0; i < dstMLI.length; i++) {
          Image.GridWarpTable_Fp(dstMLI[i], srcMLI[i], xWarpPos, yWarpPos, source.getMinX(), source.getMinY(), xStart - x, xStep, xNumCells, yStart - y, yStep, yNumCells, mlibInterpTableD, 0);
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
  






  private void copyRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    
    MediaLibAccessor srcMA = new MediaLibAccessor(sources[0], destRect, formatTag);
    
    MediaLibAccessor dstMA = new MediaLibAccessor(dest, destRect, formatTag);
    

    mediaLibImage[] srcMLI = srcMA.getMediaLibImages();
    mediaLibImage[] dstMLI = dstMA.getMediaLibImages();
    
    for (int i = 0; i < dstMLI.length; i++) {
      Image.Copy(dstMLI[i], srcMLI[i]);
    }
    
    if (dstMA.isDataCopy()) {
      dstMA.copyDataToRaster();
    }
  }
}
