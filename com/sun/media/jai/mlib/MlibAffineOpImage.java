package com.sun.media.jai.mlib;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.PlanarImage;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;
































class MlibAffineOpImage
  extends GeometricOpImage
{
  protected double[] f_transform;
  protected double[] m_transform;
  protected double[] medialib_tr;
  protected AffineTransform transform;
  protected AffineTransform i_transform;
  protected Interpolation interp;
  private Rectangle srcimg;
  private Rectangle padimg;
  protected BorderExtender extender;
  private Rectangle theDest;
  private ImagingListener listener;
  public int lpad;
  public int rpad;
  public int tpad;
  public int bpad;
  
  private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, AffineTransform forward_tr)
  {
    ImageLayout newLayout;
    ImageLayout newLayout;
    if (layout != null) {
      newLayout = (ImageLayout)layout.clone();
    } else {
      newLayout = new ImageLayout();
    }
    



    float sx0 = source.getMinX();
    float sy0 = source.getMinY();
    float sw = source.getWidth();
    float sh = source.getHeight();
    





    Point2D[] pts = new Point2D[4];
    pts[0] = new Point2D.Float(sx0, sy0);
    pts[1] = new Point2D.Float(sx0 + sw, sy0);
    pts[2] = new Point2D.Float(sx0 + sw, sy0 + sh);
    pts[3] = new Point2D.Float(sx0, sy0 + sh);
    

    forward_tr.transform(pts, 0, pts, 0, 4);
    
    float dx0 = Float.MAX_VALUE;
    float dy0 = Float.MAX_VALUE;
    float dx1 = -3.4028235E38F;
    float dy1 = -3.4028235E38F;
    for (int i = 0; i < 4; i++) {
      float px = (float)pts[i].getX();
      float py = (float)pts[i].getY();
      
      dx0 = Math.min(dx0, px);
      dy0 = Math.min(dy0, py);
      dx1 = Math.max(dx1, px);
      dy1 = Math.max(dy1, py);
    }
    




    int lw = (int)(dx1 - dx0);
    int lh = (int)(dy1 - dy0);
    








    int i_dx0 = (int)Math.floor(dx0);
    int lx0; int lx0; if (Math.abs(dx0 - i_dx0) <= 0.5D) {
      lx0 = i_dx0;
    } else {
      lx0 = (int)Math.ceil(dx0);
    }
    
    int i_dy0 = (int)Math.floor(dy0);
    int ly0; int ly0; if (Math.abs(dy0 - i_dy0) <= 0.5D) {
      ly0 = i_dy0;
    } else {
      ly0 = (int)Math.ceil(dy0);
    }
    



    newLayout.setMinX(lx0);
    newLayout.setMinY(ly0);
    newLayout.setWidth(lw);
    newLayout.setHeight(lh);
    
    return newLayout;
  }
  




















  public MlibAffineOpImage(RenderedImage source, ImageLayout layout, Map config, BorderExtender extender, AffineTransform transform, Interpolation interp, double[] backgroundValues)
  {
    super(vectorize(source), layoutHelper(layout, source, transform), config, true, extender, interp, backgroundValues);
    









    this.interp = interp;
    

    this.extender = extender;
    

    listener = ImageUtil.getImagingListener((RenderingHints)config);
    

    lpad = interp.getLeftPadding();
    rpad = interp.getRightPadding();
    tpad = interp.getTopPadding();
    bpad = interp.getBottomPadding();
    




    srcimg = new Rectangle(getSourceImage(0).getMinX(), getSourceImage(0).getMinY(), getSourceImage(0).getWidth(), getSourceImage(0).getHeight());
    


    padimg = new Rectangle(srcimg.x - lpad, srcimg.y - tpad, srcimg.width + lpad + rpad, srcimg.height + tpad + bpad);
    



    if (extender == null)
    {








      float sx0 = srcimg.x;
      float sy0 = srcimg.y;
      float sw = srcimg.width;
      float sh = srcimg.height;
      



      float f_lpad = lpad;
      float f_rpad = rpad;
      float f_tpad = tpad;
      float f_bpad = bpad;
      



      if (((interp instanceof InterpolationBilinear)) || ((interp instanceof InterpolationBicubic)) || ((interp instanceof InterpolationBicubic2)))
      {

        f_lpad = (float)(f_lpad + 0.5D);
        f_tpad = (float)(f_tpad + 0.5D);
        f_rpad = (float)(f_rpad + 0.5D);
        f_bpad = (float)(f_bpad + 0.5D);
      }
      




      sx0 += f_lpad;
      sy0 += f_tpad;
      sw -= f_lpad + f_rpad;
      sh -= f_tpad + f_bpad;
      




      Point2D[] pts = new Point2D[4];
      pts[0] = new Point2D.Float(sx0, sy0);
      pts[1] = new Point2D.Float(sx0 + sw, sy0);
      pts[2] = new Point2D.Float(sx0 + sw, sy0 + sh);
      pts[3] = new Point2D.Float(sx0, sy0 + sh);
      

      transform.transform(pts, 0, pts, 0, 4);
      
      float dx0 = Float.MAX_VALUE;
      float dy0 = Float.MAX_VALUE;
      float dx1 = -3.4028235E38F;
      float dy1 = -3.4028235E38F;
      for (int i = 0; i < 4; i++) {
        float px = (float)pts[i].getX();
        float py = (float)pts[i].getY();
        
        dx0 = Math.min(dx0, px);
        dy0 = Math.min(dy0, py);
        dx1 = Math.max(dx1, px);
        dy1 = Math.max(dy1, py);
      }
      







      int lx0 = (int)Math.ceil(dx0);
      int ly0 = (int)Math.ceil(dy0);
      int lx1 = (int)Math.floor(dx1);
      int ly1 = (int)Math.floor(dy1);
      
      theDest = new Rectangle(lx0, ly0, lx1 - lx0, ly1 - ly0);

    }
    else
    {
      theDest = getBounds();
    }
    
    try
    {
      i_transform = transform.createInverse();
    } catch (NoninvertibleTransformException e) {
      String message = JaiI18N.getString("MlibAffineOpImage0");
      listener.errorOccurred(message, new ImagingException(message, e), this, false);
    }
    



    this.transform = ((AffineTransform)transform.clone());
    





    f_transform = new double[6];
    transform.getMatrix(f_transform);
    





    medialib_tr = new double[6];
    medialib_tr[0] = f_transform[0];
    medialib_tr[1] = f_transform[2];
    medialib_tr[2] = f_transform[4];
    medialib_tr[3] = f_transform[1];
    medialib_tr[4] = f_transform[3];
    medialib_tr[5] = f_transform[5];
    



    m_transform = new double[6];
    m_transform[0] = f_transform[0];
    m_transform[1] = f_transform[2];
    m_transform[2] = f_transform[4];
    m_transform[3] = f_transform[1];
    m_transform[4] = f_transform[3];
    m_transform[5] = f_transform[5];
  }
  














  public Point2D mapDestPoint(Point2D destPt)
  {
    if (destPt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Point2D dpt = (Point2D)destPt.clone();
    dpt.setLocation(dpt.getX() + 0.5D, dpt.getY() + 0.5D);
    
    Point2D spt = i_transform.transform(dpt, null);
    spt.setLocation(spt.getX() - 0.5D, spt.getY() - 0.5D);
    
    return spt;
  }
  













  public Point2D mapSourcePoint(Point2D sourcePt)
  {
    if (sourcePt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Point2D spt = (Point2D)sourcePt.clone();
    spt.setLocation(spt.getX() + 0.5D, spt.getY() + 0.5D);
    
    Point2D dpt = transform.transform(spt, null);
    dpt.setLocation(dpt.getX() - 0.5D, dpt.getY() - 0.5D);
    
    return dpt;
  }
  



  protected Rectangle forwardMapRect(Rectangle sourceRect, int sourceIndex)
  {
    return transform.createTransformedShape(sourceRect).getBounds();
  }
  







  protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex)
  {
    float dx0 = x;
    float dy0 = y;
    float dw = width;
    float dh = height;
    
    Point2D[] pts = new Point2D[4];
    pts[0] = new Point2D.Float(dx0, dy0);
    pts[1] = new Point2D.Float(dx0 + dw, dy0);
    pts[2] = new Point2D.Float(dx0 + dw, dy0 + dh);
    pts[3] = new Point2D.Float(dx0, dy0 + dh);
    
    i_transform.transform(pts, 0, pts, 0, 4);
    
    float f_sx0 = Float.MAX_VALUE;
    float f_sy0 = Float.MAX_VALUE;
    float f_sx1 = -3.4028235E38F;
    float f_sy1 = -3.4028235E38F;
    for (int i = 0; i < 4; i++) {
      float px = (float)pts[i].getX();
      float py = (float)pts[i].getY();
      
      f_sx0 = Math.min(f_sx0, px);
      f_sy0 = Math.min(f_sy0, py);
      f_sx1 = Math.max(f_sx1, px);
      f_sy1 = Math.max(f_sy1, py);
    }
    
    int s_x0 = 0;int s_y0 = 0;int s_x1 = 0;int s_y1 = 0;
    

    if ((interp instanceof InterpolationNearest)) {
      s_x0 = (int)Math.floor(f_sx0);
      s_y0 = (int)Math.floor(f_sy0);
      s_x1 = (int)Math.ceil(f_sx1);
      s_y1 = (int)Math.ceil(f_sy1);
    } else {
      s_x0 = (int)Math.floor(f_sx0 - 0.5D);
      s_y0 = (int)Math.floor(f_sy0 - 0.5D);
      s_x1 = (int)Math.ceil(f_sx1);
      s_y1 = (int)Math.ceil(f_sy1);
    }
    



    return new Rectangle(s_x0, s_y0, s_x1 - s_x0, s_y1 - s_y0);
  }
  








  public Raster computeTile(int tileX, int tileY)
  {
    Point org = new Point(tileXToX(tileX), tileYToY(tileY));
    WritableRaster dest = createWritableRaster(sampleModel, org);
    



    Rectangle rect = new Rectangle(x, y, tileWidth, tileHeight);
    








    Rectangle destRect = rect.intersection(theDest);
    Rectangle destRect1 = rect.intersection(getBounds());
    if ((width <= 0) || (height <= 0)) {
      if (setBackground) {
        ImageUtil.fillBackground(dest, destRect1, backgroundValues);
      }
      
      return dest;
    }
    



    Rectangle srcRect = mapDestRect(destRect, 0);
    if (extender == null) {
      srcRect = srcRect.intersection(srcimg);
    } else {
      srcRect = srcRect.intersection(padimg);
    }
    
    if ((width <= 0) || (height <= 0))
    {
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
    

    if (extender == null) {
      sources[0] = getSourceImage(0).getData(srcRect);
    } else {
      sources[0] = getSourceImage(0).getExtendedData(srcRect, extender);
    }
    
    computeRect(sources, dest, destRect);
    

    if (getSourceImage(0).overlapsMultipleTiles(srcRect)) {
      recycleTile(sources[0]);
    }
    
    return dest;
  }
}
