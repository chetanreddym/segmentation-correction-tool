package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;












public class SubsampleAverageCRIF
  extends CRIFImpl
{
  public SubsampleAverageCRIF()
  {
    super("SubsampleAverage");
  }
  









  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    
    RenderedImage source = paramBlock.getRenderedSource(0);
    double scaleX = paramBlock.getDoubleParameter(0);
    double scaleY = paramBlock.getDoubleParameter(1);
    


    if ((scaleX == 1.0D) && (scaleY == 1.0D)) {
      return source;
    }
    
    return new SubsampleAverageOpImage(source, layout, renderHints, scaleX, scaleY);
  }
  






  public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock)
  {
    return paramBlock.getRenderedSource(0);
  }
  















  public RenderContext mapRenderContext(int i, RenderContext renderContext, ParameterBlock paramBlock, RenderableImage image)
  {
    double scaleX = paramBlock.getDoubleParameter(0);
    double scaleY = paramBlock.getDoubleParameter(1);
    
    AffineTransform scale = new AffineTransform(scaleX, 0.0D, 0.0D, scaleY, 0.0D, 0.0D);
    

    RenderContext RC = (RenderContext)renderContext.clone();
    AffineTransform usr2dev = RC.getTransform();
    usr2dev.concatenate(scale);
    RC.setTransform(usr2dev);
    return RC;
  }
  




  public Rectangle2D getBounds2D(ParameterBlock paramBlock)
  {
    RenderableImage source = paramBlock.getRenderableSource(0);
    
    double scaleX = paramBlock.getDoubleParameter(0);
    double scaleY = paramBlock.getDoubleParameter(1);
    

    float x0 = source.getMinX();
    float y0 = source.getMinY();
    float w = source.getWidth();
    float h = source.getHeight();
    

    float d_x0 = (float)(x0 * scaleX);
    float d_y0 = (float)(y0 * scaleY);
    float d_w = (float)(w * scaleX);
    float d_h = (float)(h * scaleY);
    
    return new Rectangle2D.Float(d_x0, d_y0, d_w, d_h);
  }
}
