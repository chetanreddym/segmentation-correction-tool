package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import javax.media.jai.CRIFImpl;
import javax.media.jai.JAI;























public class CropCRIF
  extends CRIFImpl
{
  public CropCRIF()
  {
    super("crop");
  }
  








  public RenderedImage create(ParameterBlock args, RenderingHints renderHints)
  {
    RenderedImage src = args.getRenderedSource(0);
    

    float originX = args.getFloatParameter(0);
    float originY = args.getFloatParameter(1);
    float width = args.getFloatParameter(2);
    float height = args.getFloatParameter(3);
    

    return new CropOpImage(src, originX, originY, width, height);
  }
  













  public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock)
  {
    Rectangle2D dstRect2D = getBounds2D(paramBlock);
    



    AffineTransform tf = renderContext.getTransform();
    Rectangle2D rect = tf.createTransformedShape(dstRect2D).getBounds2D();
    

    if ((rect.getWidth() < 1.0D) || (rect.getHeight() < 1.0D)) {
      double w = Math.max(rect.getWidth(), 1.0D);
      double h = Math.max(rect.getHeight(), 1.0D);
      rect.setRect(rect.getMinX(), rect.getMinY(), w, h);
    }
    

    ParameterBlock pb = new ParameterBlock();
    pb.addSource(paramBlock.getRenderedSource(0));
    pb.set((float)rect.getMinX(), 0);
    pb.set((float)rect.getMinY(), 1);
    pb.set((float)rect.getWidth(), 2);
    pb.set((float)rect.getHeight(), 3);
    

    return JAI.create("crop", pb, renderContext.getRenderingHints());
  }
  







  public Rectangle2D getBounds2D(ParameterBlock paramBlock)
  {
    return new Rectangle2D.Float(paramBlock.getFloatParameter(0), paramBlock.getFloatParameter(1), paramBlock.getFloatParameter(2), paramBlock.getFloatParameter(3));
  }
}
