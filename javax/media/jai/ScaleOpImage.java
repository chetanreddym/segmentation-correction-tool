package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.Rational;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;






































































































public abstract class ScaleOpImage
  extends GeometricOpImage
{
  protected float scaleX;
  protected float scaleY;
  protected float transX;
  protected float transY;
  protected Rational scaleXRational;
  protected Rational scaleYRational;
  protected long scaleXRationalNum;
  protected long scaleXRationalDenom;
  protected long scaleYRationalNum;
  protected long scaleYRationalDenom;
  protected Rational invScaleXRational;
  protected Rational invScaleYRational;
  protected long invScaleXRationalNum;
  protected long invScaleXRationalDenom;
  protected long invScaleYRationalNum;
  protected long invScaleYRationalDenom;
  protected Rational transXRational;
  protected Rational transYRational;
  protected long transXRationalNum;
  protected long transXRationalDenom;
  protected long transYRationalNum;
  protected long transYRationalDenom;
  protected static float rationalTolerance = 1.0E-6F;
  













  private int lpad;
  













  private int rpad;
  













  private int tpad;
  













  private int bpad;
  














  private static ImageLayout layoutHelper(RenderedImage source, float scaleX, float scaleY, float transX, float transY, Interpolation interp, ImageLayout il)
  {
    Rational scaleXRational = Rational.approximate(scaleX, rationalTolerance);
    

    Rational scaleYRational = Rational.approximate(scaleY, rationalTolerance);
    

    long scaleXRationalNum = num;
    long scaleXRationalDenom = denom;
    long scaleYRationalNum = num;
    long scaleYRationalDenom = denom;
    
    Rational transXRational = Rational.approximate(transX, rationalTolerance);
    

    Rational transYRational = Rational.approximate(transY, rationalTolerance);
    

    long transXRationalNum = num;
    long transXRationalDenom = denom;
    long transYRationalNum = num;
    long transYRationalDenom = denom;
    
    ImageLayout layout = il == null ? new ImageLayout() : (ImageLayout)il.clone();
    

    int x0 = source.getMinX();
    int y0 = source.getMinY();
    int w = source.getWidth();
    int h = source.getHeight();
    









    long dx0Num = x0;
    long dx0Denom = 1L;
    
    long dy0Num = y0;
    long dy0Denom = 1L;
    

    long dx1Num = x0 + w;
    long dx1Denom = 1L;
    

    long dy1Num = y0 + h;
    long dy1Denom = 1L;
    
    dx0Num *= scaleXRationalNum;
    dx0Denom *= scaleXRationalDenom;
    
    dy0Num *= scaleYRationalNum;
    dy0Denom *= scaleYRationalDenom;
    
    dx1Num *= scaleXRationalNum;
    dx1Denom *= scaleXRationalDenom;
    
    dy1Num *= scaleYRationalNum;
    dy1Denom *= scaleYRationalDenom;
    

    dx0Num = 2L * dx0Num - dx0Denom;
    dx0Denom *= 2L;
    
    dy0Num = 2L * dy0Num - dy0Denom;
    dy0Denom *= 2L;
    

    dx1Num = 2L * dx1Num - 3L * dx1Denom;
    dx1Denom *= 2L;
    
    dy1Num = 2L * dy1Num - 3L * dy1Denom;
    dy1Denom *= 2L;
    



    dx0Num = dx0Num * transXRationalDenom + transXRationalNum * dx0Denom;
    dx0Denom *= transXRationalDenom;
    

    dy0Num = dy0Num * transYRationalDenom + transYRationalNum * dy0Denom;
    dy0Denom *= transYRationalDenom;
    

    dx1Num = dx1Num * transXRationalDenom + transXRationalNum * dx1Denom;
    dx1Denom *= transXRationalDenom;
    

    dy1Num = dy1Num * transYRationalDenom + transYRationalNum * dy1Denom;
    dy1Denom *= transYRationalDenom;
    



    int l_x0 = Rational.ceil(dx0Num, dx0Denom);
    int l_y0 = Rational.ceil(dy0Num, dy0Denom);
    
    int l_x1 = Rational.ceil(dx1Num, dx1Denom);
    int l_y1 = Rational.ceil(dy1Num, dy1Denom);
    

    layout.setMinX(l_x0);
    layout.setMinY(l_y0);
    

    layout.setWidth(l_x1 - l_x0 + 1);
    layout.setHeight(l_y1 - l_y0 + 1);
    
    return layout;
  }
  


  private static Map configHelper(RenderedImage source, Map configuration, Interpolation interp)
  {
    Map config = configuration;
    


    if ((ImageUtil.isBinary(source.getSampleModel())) && ((interp == null) || ((interp instanceof InterpolationNearest)) || ((interp instanceof InterpolationBilinear))))
    {




      if (configuration == null) {
        config = new RenderingHints(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);




      }
      else if (!config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)) {
        RenderingHints hints = new RenderingHints(null);
        
        hints.putAll(configuration);
        config = hints;
        config.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.TRUE);
      }
    }
    


    return config;
  }
  























































  public ScaleOpImage(RenderedImage source, ImageLayout layout, Map configuration, boolean cobbleSources, BorderExtender extender, Interpolation interp, float scaleX, float scaleY, float transX, float transY)
  {
    super(vectorize(source), layoutHelper(source, scaleX, scaleY, transX, transY, interp, layout), configHelper(source, configuration, interp), cobbleSources, extender, interp, null);
    







    this.scaleX = scaleX;
    this.scaleY = scaleY;
    this.transX = transX;
    this.transY = transY;
    




    scaleXRational = Rational.approximate(scaleX, rationalTolerance);
    scaleYRational = Rational.approximate(scaleY, rationalTolerance);
    
    scaleXRationalNum = scaleXRational.num;
    scaleXRationalDenom = scaleXRational.denom;
    scaleYRationalNum = scaleYRational.num;
    scaleYRationalDenom = scaleYRational.denom;
    
    transXRational = Rational.approximate(transX, rationalTolerance);
    transYRational = Rational.approximate(transY, rationalTolerance);
    
    transXRationalNum = transXRational.num;
    transXRationalDenom = transXRational.denom;
    transYRationalNum = transYRational.num;
    transYRationalDenom = transYRational.denom;
    

    invScaleXRational = new Rational(scaleXRational);
    invScaleXRational.invert();
    invScaleYRational = new Rational(scaleYRational);
    invScaleYRational.invert();
    invScaleXRationalNum = invScaleXRational.num;
    invScaleXRationalDenom = invScaleXRational.denom;
    invScaleYRationalNum = invScaleYRational.num;
    invScaleYRationalDenom = invScaleYRational.denom;
    
    lpad = interp.getLeftPadding();
    rpad = interp.getRightPadding();
    tpad = interp.getTopPadding();
    bpad = interp.getBottomPadding();
    
    if (extender == null)
    {

      int x0 = source.getMinX();
      int y0 = source.getMinY();
      int w = source.getWidth();
      int h = source.getHeight();
      
      long dy1Denom;
      
      long dx0Denom;
      long dy0Denom;
      long dx1Denom;
      long dy1Denom;
      if ((interp instanceof InterpolationNearest))
      {

        long dx0Num = x0;
        long dx0Denom = 1L;
        
        long dy0Num = y0;
        long dy0Denom = 1L;
        





        long dx1Num = x0 + w;
        long dx1Denom = 1L;
        

        long dy1Num = y0 + h;
        dy1Denom = 1L;

      }
      else
      {
        dx0Num = 2 * x0 + 1;
        dx0Denom = 2L;
        
        dy0Num = 2 * y0 + 1;
        dy0Denom = 2L;
        


        dx1Num = 2 * x0 + 2 * w + 1;
        dx1Denom = 2L;
        
        dy1Num = 2 * y0 + 2 * h + 1;
        dy1Denom = 2L;
        

        dx0Num += dx0Denom * lpad;
        
        dy0Num += dy0Denom * tpad;
        

        dx1Num -= dx1Denom * rpad;
        
        dy1Num -= dy1Denom * bpad;
      }
      



      dx0Num *= scaleXRationalNum;
      dx0Denom *= scaleXRationalDenom;
      

      long dx0Num = dx0Num * transXRationalDenom + transXRationalNum * dx0Denom;
      dx0Denom *= transXRationalDenom;
      

      dy0Num *= scaleYRationalNum;
      dy0Denom *= scaleYRationalDenom;
      

      long dy0Num = dy0Num * transYRationalDenom + transYRationalNum * dy0Denom;
      dy0Denom *= transYRationalDenom;
      

      dx1Num *= scaleXRationalNum;
      dx1Denom *= scaleXRationalDenom;
      

      long dx1Num = dx1Num * transXRationalDenom + transXRationalNum * dx1Denom;
      dx1Denom *= transXRationalDenom;
      

      dy1Num *= scaleYRationalNum;
      dy1Denom *= scaleYRationalDenom;
      

      long dy1Num = dy1Num * transYRationalDenom + transYRationalNum * dy1Denom;
      dy1Denom *= transYRationalDenom;
      





      dx0Num = 2L * dx0Num - dx0Denom;
      dx0Denom *= 2L;
      
      dy0Num = 2L * dy0Num - dy0Denom;
      dy0Denom *= 2L;
      
      int l_x0 = Rational.ceil(dx0Num, dx0Denom);
      int l_y0 = Rational.ceil(dy0Num, dy0Denom);
      

      dx1Num = 2L * dx1Num - dx1Denom;
      dx1Denom *= 2L;
      
      dy1Num = 2L * dy1Num - dy1Denom;
      dy1Denom *= 2L;
      
      int l_x1 = Rational.floor(dx1Num, dx1Denom);
      
      if (l_x1 * dx1Denom == dx1Num) {
        l_x1--;
      }
      
      int l_y1 = Rational.floor(dy1Num, dy1Denom);
      if (l_y1 * dy1Denom == dy1Num) {
        l_y1--;
      }
      
      computableBounds = new Rectangle(l_x0, l_y0, l_x1 - l_x0 + 1, l_y1 - l_y0 + 1);

    }
    else
    {
      computableBounds = getBounds();
    }
  }
  



  /**
   * @deprecated
   */
  public int getLeftPadding()
  {
    return interp == null ? 0 : interp.getLeftPadding();
  }
  



  /**
   * @deprecated
   */
  public int getRightPadding()
  {
    return interp == null ? 0 : interp.getRightPadding();
  }
  



  /**
   * @deprecated
   */
  public int getTopPadding()
  {
    return interp == null ? 0 : interp.getTopPadding();
  }
  



  /**
   * @deprecated
   */
  public int getBottomPadding()
  {
    return interp == null ? 0 : interp.getBottomPadding();
  }
  





























  public Point2D mapDestPoint(Point2D destPt, int sourceIndex)
  {
    if (destPt == null)
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    if (sourceIndex != 0) {
      throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
    }
    
    Point2D pt = (Point2D)destPt.clone();
    
    pt.setLocation((destPt.getX() - transX + 0.5D) / scaleX - 0.5D, (destPt.getY() - transY + 0.5D) / scaleY - 0.5D);
    

    return pt;
  }
  





























  public Point2D mapSourcePoint(Point2D sourcePt, int sourceIndex)
  {
    if (sourcePt == null)
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    if (sourceIndex != 0) {
      throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
    }
    
    Point2D pt = (Point2D)sourcePt.clone();
    
    pt.setLocation(scaleX * (sourcePt.getX() + 0.5D) + transX - 0.5D, scaleY * (sourcePt.getY() + 0.5D) + transY - 0.5D);
    

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
    

    int x0 = x;
    int y0 = y;
    int w = width;
    int h = height;
    
    long dy1Denom;
    
    long dx0Denom;
    long dy0Denom;
    long dx1Denom;
    long dy1Denom;
    if ((interp instanceof InterpolationNearest))
    {

      long dx0Num = x0;
      long dx0Denom = 1L;
      
      long dy0Num = y0;
      long dy0Denom = 1L;
      






      long dx1Num = x0 + w;
      long dx1Denom = 1L;
      

      long dy1Num = y0 + h;
      dy1Denom = 1L;

    }
    else
    {
      dx0Num = 2 * x0 + 1;
      dx0Denom = 2L;
      
      dy0Num = 2 * y0 + 1;
      dy0Denom = 2L;
      


      dx1Num = 2 * x0 + 2 * w + 1;
      dx1Denom = 2L;
      
      dy1Num = 2 * y0 + 2 * h + 1;
      dy1Denom = 2L;
    }
    



    dx0Num *= scaleXRationalNum;
    dx0Denom *= scaleXRationalDenom;
    

    dy0Num *= scaleYRationalNum;
    dy0Denom *= scaleYRationalDenom;
    

    dx1Num *= scaleXRationalNum;
    dx1Denom *= scaleXRationalDenom;
    

    dy1Num *= scaleYRationalNum;
    dy1Denom *= scaleYRationalDenom;
    



    long dx0Num = dx0Num * transXRationalDenom + transXRationalNum * dx0Denom;
    dx0Denom *= transXRationalDenom;
    

    long dy0Num = dy0Num * transYRationalDenom + transYRationalNum * dy0Denom;
    dy0Denom *= transYRationalDenom;
    

    long dx1Num = dx1Num * transXRationalDenom + transXRationalNum * dx1Denom;
    dx1Denom *= transXRationalDenom;
    

    long dy1Num = dy1Num * transYRationalDenom + transYRationalNum * dy1Denom;
    dy1Denom *= transYRationalDenom;
    




    dx0Num = 2L * dx0Num - dx0Denom;
    dx0Denom *= 2L;
    
    dy0Num = 2L * dy0Num - dy0Denom;
    dy0Denom *= 2L;
    
    int l_x0 = Rational.ceil(dx0Num, dx0Denom);
    int l_y0 = Rational.ceil(dy0Num, dy0Denom);
    

    dx1Num = 2L * dx1Num - dx1Denom;
    dx1Denom *= 2L;
    
    dy1Num = 2L * dy1Num - dy1Denom;
    dy1Denom *= 2L;
    
    int l_x1 = Rational.floor(dx1Num, dx1Denom);
    if (l_x1 * dx1Denom == dx1Num) {
      l_x1--;
    }
    
    int l_y1 = Rational.floor(dy1Num, dy1Denom);
    if (l_y1 * dy1Denom == dy1Num) {
      l_y1--;
    }
    
    return new Rectangle(l_x0, l_y0, l_x1 - l_x0 + 1, l_y1 - l_y0 + 1);
  }
  



















  protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex)
  {
    if (destRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sourceIndex != 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    

    int x0 = x;
    int y0 = y;
    int w = width;
    int h = height;
    






    long sx0Num = x0 * 2 + 1;
    long sx0Denom = 2L;
    
    long sy0Num = y0 * 2 + 1;
    long sy0Denom = 2L;
    






    long sx1Num = 2 * x0 + 2 * w - 1;
    long sx1Denom = 2L;
    

    long sy1Num = 2 * y0 + 2 * h - 1;
    long sy1Denom = 2L;
    

    sx0Num = sx0Num * transXRationalDenom - transXRationalNum * sx0Denom;
    sx0Denom *= transXRationalDenom;
    
    sy0Num = sy0Num * transYRationalDenom - transYRationalNum * sy0Denom;
    sy0Denom *= transYRationalDenom;
    
    sx1Num = sx1Num * transXRationalDenom - transXRationalNum * sx1Denom;
    sx1Denom *= transXRationalDenom;
    
    sy1Num = sy1Num * transYRationalDenom - transYRationalNum * sy1Denom;
    sy1Denom *= transYRationalDenom;
    



    sx0Num *= invScaleXRationalNum;
    sx0Denom *= invScaleXRationalDenom;
    
    sy0Num *= invScaleYRationalNum;
    sy0Denom *= invScaleYRationalDenom;
    
    sx1Num *= invScaleXRationalNum;
    sx1Denom *= invScaleXRationalDenom;
    
    sy1Num *= invScaleYRationalNum;
    sy1Denom *= invScaleYRationalDenom;
    
    int s_x0 = 0;int s_y0 = 0;int s_x1 = 0;int s_y1 = 0;
    if ((interp instanceof InterpolationNearest))
    {

      s_x0 = Rational.floor(sx0Num, sx0Denom);
      s_y0 = Rational.floor(sy0Num, sy0Denom);
      

      s_x1 = Rational.floor(sx1Num, sx1Denom);
      

      s_y1 = Rational.floor(sy1Num, sy1Denom);

    }
    else
    {

      s_x0 = Rational.floor(2L * sx0Num - sx0Denom, 2L * sx0Denom);
      

      s_y0 = Rational.floor(2L * sy0Num - sy0Denom, 2L * sy0Denom);
      

      s_x1 = Rational.floor(2L * sx1Num - sx1Denom, 2L * sx1Denom);
      

      s_y1 = Rational.floor(2L * sy1Num - sy1Denom, 2L * sy1Denom);
    }
    
    return new Rectangle(s_x0, s_y0, s_x1 - s_x0 + 1, s_y1 - s_y0 + 1);
  }
  



































  public Raster computeTile(int tileX, int tileY)
  {
    if (!cobbleSources) {
      return super.computeTile(tileX, tileY);
    }
    

    int orgX = tileXToX(tileX);
    int orgY = tileYToY(tileY);
    

    WritableRaster dest = createWritableRaster(sampleModel, new Point(orgX, orgY));
    

    Rectangle rect = new Rectangle(orgX, orgY, tileWidth, tileHeight);
    


    Rectangle destRect = rect.intersection(computableBounds);
    if ((width <= 0) || (height <= 0))
    {
      return dest;
    }
    

    Rectangle srcRect = mapDestRect(destRect, 0);
    Raster[] sources = new Raster[1];
    





    PlanarImage source0 = getSource(0);
    
    IntegerSequence srcXSplits = new IntegerSequence();
    IntegerSequence srcYSplits = new IntegerSequence();
    source0.getSplits(srcXSplits, srcYSplits, srcRect);
    
    if ((srcXSplits.getNumElements() == 1) && (srcYSplits.getNumElements() == 1))
    {



      if (extender == null) {
        sources[0] = source0.getData(srcRect);
      } else {
        sources[0] = source0.getExtendedData(srcRect, extender);
      }
      

      computeRect(sources, dest, destRect);

    }
    else
    {
      int srcTileWidth = source0.getTileWidth();
      int srcTileHeight = source0.getTileHeight();
      
      srcYSplits.startEnumeration();
      while (srcYSplits.hasMoreElements())
      {
        int ysplit = srcYSplits.nextElement();
        
        srcXSplits.startEnumeration();
        while (srcXSplits.hasMoreElements())
        {
          int xsplit = srcXSplits.nextElement();
          

          Rectangle srcTile = new Rectangle(xsplit, ysplit, srcTileWidth, srcTileHeight);
          




          Rectangle newSrcRect = srcRect.intersection(srcTile);
          









          if (!(interp instanceof InterpolationNearest))
          {
            if (width <= interp.getWidth())
            {







              Rectangle wSrcRect = new Rectangle();
              

              x = x;
              y = (y - tpad - 1);
              width = (2 * (lpad + rpad + 1));
              height = (height + bpad + tpad + 2);
              

              wSrcRect = wSrcRect.intersection(source0.getBounds());
              

              Rectangle wDestRect = mapSourceRect(wSrcRect, 0);
              





              wDestRect = wDestRect.intersection(destRect);
              
              if ((width > 0) && (height > 0))
              {

                if (extender == null) {
                  sources[0] = source0.getData(wSrcRect);
                } else {
                  sources[0] = source0.getExtendedData(wSrcRect, extender);
                }
                



                computeRect(sources, dest, wDestRect);
              }
            }
            
            if (height <= interp.getHeight())
            {







              Rectangle hSrcRect = new Rectangle();
              

              x = (x - lpad - 1);
              y = y;
              width = (width + lpad + rpad + 2);
              
              height = (2 * (tpad + bpad + 1));
              
              hSrcRect = hSrcRect.intersection(source0.getBounds());
              

              Rectangle hDestRect = mapSourceRect(hSrcRect, 0);
              





              hDestRect = hDestRect.intersection(destRect);
              
              if ((width > 0) && (height > 0))
              {

                if (extender == null) {
                  sources[0] = source0.getData(hSrcRect);
                } else {
                  sources[0] = source0.getExtendedData(hSrcRect, extender);
                }
                



                computeRect(sources, dest, hDestRect);
              }
            }
          }
          

          if ((width > 0) && (height > 0))
          {


            Rectangle newDestRect = mapSourceRect(newSrcRect, 0);
            



            newDestRect = newDestRect.intersection(destRect);
            
            if ((width > 0) && (height > 0))
            {


              if (extender == null) {
                sources[0] = source0.getData(newSrcRect);
              } else {
                sources[0] = source0.getExtendedData(newSrcRect, extender);
              }
              



              computeRect(sources, dest, newDestRect);
            }
            















            if (!(interp instanceof InterpolationNearest)) {
              Rectangle RTSrcRect = new Rectangle();
              


              x = (x + width - 1 - rpad - lpad);
              
              y = y;
              













              width = (2 * (lpad + rpad + 1));
              height = height;
              
              Rectangle RTDestRect = mapSourceRect(RTSrcRect, 0);
              

              RTDestRect = RTDestRect.intersection(destRect);
              


              RTSrcRect = mapDestRect(RTDestRect, 0);
              
              if ((width > 0) && (height > 0))
              {

                if (extender == null) {
                  sources[0] = source0.getData(RTSrcRect);
                } else {
                  sources[0] = source0.getExtendedData(RTSrcRect, extender);
                }
                


                computeRect(sources, dest, RTDestRect);
              }
              

              Rectangle BTSrcRect = new Rectangle();
              

              x = x;
              y = (y + height - 1 - bpad - tpad);
              














              width = width;
              height = (2 * (tpad + bpad + 1));
              
              Rectangle BTDestRect = mapSourceRect(BTSrcRect, 0);
              

              BTDestRect = BTDestRect.intersection(destRect);
              


              BTSrcRect = mapDestRect(BTDestRect, 0);
              

              if ((width > 0) && (height > 0))
              {


                if (extender == null) {
                  sources[0] = source0.getData(BTSrcRect);
                } else {
                  sources[0] = source0.getExtendedData(BTSrcRect, extender);
                }
                


                computeRect(sources, dest, BTDestRect);
              }
              

              Rectangle LRTSrcRect = new Rectangle();
              

              x = (x + width - 1 - rpad - lpad);
              
              y = (y + height - 1 - bpad - tpad);
              


              width = (2 * (rpad + lpad + 1));
              height = (2 * (tpad + bpad + 1));
              
              Rectangle LRTDestRect = mapSourceRect(LRTSrcRect, 0);
              

              LRTDestRect = LRTDestRect.intersection(destRect);
              

              LRTSrcRect = mapDestRect(LRTDestRect, 0);
              
              if ((width > 0) && (height > 0))
              {

                if (extender == null) {
                  sources[0] = source0.getData(LRTSrcRect);
                } else {
                  sources[0] = source0.getExtendedData(LRTSrcRect, extender);
                }
                


                computeRect(sources, dest, LRTDestRect);
              }
            }
          }
        }
      }
    }
    

    return dest;
  }
}
