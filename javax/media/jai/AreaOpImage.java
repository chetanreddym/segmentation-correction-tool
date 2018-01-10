package javax.media.jai;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;







































































public abstract class AreaOpImage
  extends OpImage
{
  protected int leftPadding;
  protected int rightPadding;
  protected int topPadding;
  protected int bottomPadding;
  protected BorderExtender extender = null;
  

  private Rectangle theDest;
  


  private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source)
  {
    if ((layout != null) && (source != null) && ((layout.getValidMask() & 0xF) != 0))
    {



      Rectangle sourceRect = new Rectangle(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());
      





      Rectangle dstRect = new Rectangle(layout.getMinX(source), layout.getMinY(source), layout.getWidth(source), layout.getHeight(source));
      




      if (dstRect.intersection(sourceRect).isEmpty()) {
        throw new IllegalArgumentException(JaiI18N.getString("AreaOpImage0"));
      }
    }
    

    return layout;
  }
  
  private static Map configHelper(Map configuration)
  {
    Map config;
    Map config;
    if (configuration == null) {
      config = new RenderingHints(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.TRUE);
    }
    else
    {
      config = configuration;
      


      if (!config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)) {
        RenderingHints hints = new RenderingHints(null);
        
        hints.putAll(configuration);
        config = hints;
        config.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.TRUE);
      }
    }
    
    return config;
  }
  
























































  public AreaOpImage(RenderedImage source, ImageLayout layout, Map configuration, boolean cobbleSources, BorderExtender extender, int leftPadding, int rightPadding, int topPadding, int bottomPadding)
  {
    super(vectorize(source), layoutHelper(layout, source), configHelper(configuration), cobbleSources);
    



    this.extender = extender;
    this.leftPadding = leftPadding;
    this.rightPadding = rightPadding;
    this.topPadding = topPadding;
    this.bottomPadding = bottomPadding;
    
    if (extender == null)
    {
      int d_x0 = getMinX() + leftPadding;
      int d_y0 = getMinY() + topPadding;
      
      int d_w = getWidth() - leftPadding - rightPadding;
      d_w = Math.max(d_w, 0);
      
      int d_h = getHeight() - topPadding - bottomPadding;
      d_h = Math.max(d_h, 0);
      
      theDest = new Rectangle(d_x0, d_y0, d_w, d_h);
    } else {
      theDest = getBounds();
    }
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
  








  public BorderExtender getBorderExtender()
  {
    return extender;
  }
  


















  public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex)
  {
    if (sourceRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    

    int lpad = getLeftPadding();
    int rpad = getRightPadding();
    int tpad = getTopPadding();
    int bpad = getBottomPadding();
    
    return new Rectangle(x + lpad, y + tpad, width - lpad - rpad, height - tpad - bpad);
  }
  



















  public Rectangle mapDestRect(Rectangle destRect, int sourceIndex)
  {
    if (destRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    

    int lpad = getLeftPadding();
    int rpad = getRightPadding();
    int tpad = getTopPadding();
    int bpad = getBottomPadding();
    
    return new Rectangle(x - lpad, y - tpad, width + lpad + rpad, height + tpad + bpad);
  }
  






















  public Raster computeTile(int tileX, int tileY)
  {
    if (!cobbleSources) {
      return super.computeTile(tileX, tileY);
    }
    

    Point org = new Point(tileXToX(tileX), tileYToY(tileY));
    WritableRaster dest = createWritableRaster(sampleModel, org);
    

    Rectangle rect = new Rectangle(x, y, sampleModel.getWidth(), sampleModel.getHeight());
    


    Rectangle destRect = rect.intersection(theDest);
    if ((width <= 0) || (height <= 0)) {
      return dest;
    }
    

    PlanarImage s = getSource(0);
    






    destRect = destRect.intersection(s.getBounds());
    Rectangle srcRect = new Rectangle(destRect);
    x -= getLeftPadding();
    width += getLeftPadding() + getRightPadding();
    y -= getTopPadding();
    height += getTopPadding() + getBottomPadding();
    




    IntegerSequence srcXSplits = new IntegerSequence();
    IntegerSequence srcYSplits = new IntegerSequence();
    

    s.getSplits(srcXSplits, srcYSplits, srcRect);
    

    IntegerSequence xSplits = new IntegerSequence(x, x + width);
    

    xSplits.insert(x);
    xSplits.insert(x + width);
    
    srcXSplits.startEnumeration();
    while (srcXSplits.hasMoreElements()) {
      int xsplit = srcXSplits.nextElement();
      int lsplit = xsplit - getLeftPadding();
      int rsplit = xsplit + getRightPadding();
      xSplits.insert(lsplit);
      xSplits.insert(rsplit);
    }
    

    IntegerSequence ySplits = new IntegerSequence(y, y + height);
    

    ySplits.insert(y);
    ySplits.insert(y + height);
    
    srcYSplits.startEnumeration();
    while (srcYSplits.hasMoreElements()) {
      int ysplit = srcYSplits.nextElement();
      int tsplit = ysplit - getBottomPadding();
      int bsplit = ysplit + getTopPadding();
      ySplits.insert(tsplit);
      ySplits.insert(bsplit);
    }
    





    Raster[] sources = new Raster[1];
    
    ySplits.startEnumeration();
    int y2; for (int y1 = ySplits.nextElement(); ySplits.hasMoreElements(); y1 = y2) {
      y2 = ySplits.nextElement();
      
      int h = y2 - y1;
      int py1 = y1 - getTopPadding();
      int py2 = y2 + getBottomPadding();
      int ph = py2 - py1;
      
      xSplits.startEnumeration();
      int x2; for (int x1 = xSplits.nextElement(); 
          xSplits.hasMoreElements(); 
          x1 = x2) {
        x2 = xSplits.nextElement();
        
        int w = x2 - x1;
        int px1 = x1 - getLeftPadding();
        int px2 = x2 + getRightPadding();
        int pw = px2 - px1;
        

        Rectangle srcSubRect = new Rectangle(px1, py1, pw, ph);
        sources[0] = (extender != null ? s.getExtendedData(srcSubRect, extender) : s.getData(srcSubRect));
        



        Rectangle dstSubRect = new Rectangle(x1, y1, w, h);
        computeRect(sources, dest, dstSubRect);
        

        if (s.overlapsMultipleTiles(srcSubRect)) {
          recycleTile(sources[0]);
        }
      }
    }
    return dest;
  }
}
