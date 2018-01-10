package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.BorderExtender;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.TileCache;



















public class AffineCRIF
  extends CRIFImpl
{
  private static final float TOLERANCE = 0.01F;
  
  public AffineCRIF()
  {
    super("affine");
  }
  




  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    

    TileCache cache = RIFUtil.getTileCacheHint(renderHints);
    

    BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
    
    RenderedImage source = paramBlock.getRenderedSource(0);
    
    Object arg0 = paramBlock.getObjectParameter(0);
    AffineTransform transform = (AffineTransform)arg0;
    
    Object arg1 = paramBlock.getObjectParameter(1);
    Interpolation interp = (Interpolation)arg1;
    
    double[] backgroundValues = (double[])paramBlock.getObjectParameter(2);
    
    SampleModel sm = source.getSampleModel();
    boolean isBinary = ((sm instanceof MultiPixelPackedSampleModel)) && (sm.getSampleSize(0) == 1) && ((sm.getDataType() == 0) || (sm.getDataType() == 1) || (sm.getDataType() == 3));
    






    double[] tr = new double[6];
    transform.getMatrix(tr);
    




    if ((tr[0] == 1.0D) && (tr[3] == 1.0D) && (tr[2] == 0.0D) && (tr[1] == 0.0D) && (tr[4] == 0.0D) && (tr[5] == 0.0D))
    {





      return new CopyOpImage(source, renderHints, layout);
    }
    








    if ((tr[0] == 1.0D) && (tr[3] == 1.0D) && (tr[2] == 0.0D) && (tr[1] == 0.0D) && (Math.abs(tr[4] - (int)tr[4]) < 0.009999999776482582D) && (Math.abs(tr[5] - (int)tr[5]) < 0.009999999776482582D) && (layout == null))
    {






      return new TranslateIntOpImage(source, renderHints, (int)tr[4], (int)tr[5]);
    }
    








    if ((tr[0] > 0.0D) && (tr[2] == 0.0D) && (tr[1] == 0.0D) && (tr[3] > 0.0D))
    {



      if ((interp instanceof InterpolationNearest)) {
        if (isBinary) {
          return new ScaleNearestBinaryOpImage(source, extender, renderHints, layout, (float)tr[0], (float)tr[3], (float)tr[4], (float)tr[5], interp);
        }
        







        return new ScaleNearestOpImage(source, extender, renderHints, layout, (float)tr[0], (float)tr[3], (float)tr[4], (float)tr[5], interp);
      }
      







      if ((interp instanceof InterpolationBilinear)) {
        if (isBinary) {
          return new ScaleBilinearBinaryOpImage(source, extender, renderHints, layout, (float)tr[0], (float)tr[3], (float)tr[4], (float)tr[5], interp);
        }
        








        return new ScaleBilinearOpImage(source, extender, renderHints, layout, (float)tr[0], (float)tr[3], (float)tr[4], (float)tr[5], interp);
      }
      







      if (((interp instanceof InterpolationBicubic)) || ((interp instanceof InterpolationBicubic2)))
      {
        return new ScaleBicubicOpImage(source, extender, renderHints, layout, (float)tr[0], (float)tr[3], (float)tr[4], (float)tr[5], interp);
      }
      







      return new ScaleGeneralOpImage(source, extender, renderHints, layout, (float)tr[0], (float)tr[3], (float)tr[4], (float)tr[5], interp);
    }
    










    if ((interp instanceof InterpolationNearest)) {
      if (isBinary) {
        return new AffineNearestBinaryOpImage(source, extender, renderHints, layout, transform, interp, backgroundValues);
      }
      





      return new AffineNearestOpImage(source, extender, renderHints, layout, transform, interp, backgroundValues);
    }
    





    if ((interp instanceof InterpolationBilinear)) {
      return new AffineBilinearOpImage(source, extender, renderHints, layout, transform, interp, backgroundValues);
    }
    




    if ((interp instanceof InterpolationBicubic)) {
      return new AffineBicubicOpImage(source, extender, renderHints, layout, transform, interp, backgroundValues);
    }
    




    if ((interp instanceof InterpolationBicubic2)) {
      return new AffineBicubic2OpImage(source, extender, renderHints, layout, transform, interp, backgroundValues);
    }
    





    return new AffineGeneralOpImage(source, extender, renderHints, layout, transform, interp, backgroundValues);
  }
  













  public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock)
  {
    return paramBlock.getRenderedSource(0);
  }
  














  public RenderContext mapRenderContext(int i, RenderContext renderContext, ParameterBlock paramBlock, RenderableImage image)
  {
    Object arg0 = paramBlock.getObjectParameter(0);
    AffineTransform affine = (AffineTransform)arg0;
    
    RenderContext RC = (RenderContext)renderContext.clone();
    AffineTransform usr2dev = RC.getTransform();
    usr2dev.concatenate(affine);
    RC.setTransform(usr2dev);
    return RC;
  }
  



  public Rectangle2D getBounds2D(ParameterBlock paramBlock)
  {
    RenderableImage source = paramBlock.getRenderableSource(0);
    Object arg0 = paramBlock.getObjectParameter(0);
    AffineTransform forward_tr = (AffineTransform)arg0;
    
    Object arg1 = paramBlock.getObjectParameter(1);
    Interpolation interp = (Interpolation)arg1;
    


    double[] tr = new double[6];
    forward_tr.getMatrix(tr);
    



    if ((tr[0] == 1.0D) && (tr[3] == 1.0D) && (tr[2] == 0.0D) && (tr[1] == 0.0D) && (tr[4] == 0.0D) && (tr[5] == 0.0D))
    {




      return new Rectangle2D.Float(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());
    }
    







    if ((tr[0] == 1.0D) && (tr[3] == 1.0D) && (tr[2] == 0.0D) && (tr[1] == 0.0D) && (Math.abs(tr[4] - (int)tr[4]) < 0.009999999776482582D) && (Math.abs(tr[5] - (int)tr[5]) < 0.009999999776482582D))
    {




      return new Rectangle2D.Float(source.getMinX() + (float)tr[4], source.getMinY() + (float)tr[5], source.getWidth(), source.getHeight());
    }
    







    if ((tr[0] > 0.0D) && (tr[2] == 0.0D) && (tr[1] == 0.0D) && (tr[3] > 0.0D))
    {



      float x0 = source.getMinX();
      float y0 = source.getMinY();
      float w = source.getWidth();
      float h = source.getHeight();
      

      float d_x0 = x0 * (float)tr[0] + (float)tr[4];
      float d_y0 = y0 * (float)tr[3] + (float)tr[5];
      float d_w = w * (float)tr[0];
      float d_h = h * (float)tr[3];
      
      return new Rectangle2D.Float(d_x0, d_y0, d_w, d_h);
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
    




    float lw = dx1 - dx0;
    float lh = dy1 - dy0;
    
    return new Rectangle2D.Float(dx0, dy0, lw, lh);
  }
}
