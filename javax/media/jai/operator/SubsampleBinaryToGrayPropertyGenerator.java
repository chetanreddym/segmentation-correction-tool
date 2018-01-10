package javax.media.jai.operator;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderedOp;
























class SubsampleBinaryToGrayPropertyGenerator
  extends PropertyGeneratorImpl
{
  public SubsampleBinaryToGrayPropertyGenerator()
  {
    super(new String[] { "ROI" }, new Class[] { ROI.class }, new Class[] { RenderedOp.class });
  }
  








  public Object getProperty(String name, Object opNode)
  {
    validate(name, opNode);
    
    if (((opNode instanceof RenderedOp)) && (name.equalsIgnoreCase("roi")))
    {
      RenderedOp op = (RenderedOp)opNode;
      
      ParameterBlock pb = op.getParameterBlock();
      

      RenderedImage src = pb.getRenderedSource(0);
      
      Object property = src.getProperty("ROI");
      if ((property == null) || (property.equals(Image.UndefinedProperty)) || (!(property instanceof ROI)))
      {

        return Image.UndefinedProperty;
      }
      ROI srcROI = (ROI)property;
      

      Rectangle srcBounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
      




      if (!srcBounds.contains(srcROI.getBounds())) {
        srcROI = srcROI.intersect(new ROIShape(srcBounds));
      }
      

      float sx = pb.getFloatParameter(0);
      float sy = pb.getFloatParameter(1);
      

      AffineTransform transform = new AffineTransform(sx, 0.0D, 0.0D, sy, 0.0D, 0.0D);
      


      ROI dstROI = srcROI.transform(transform);
      

      Rectangle dstBounds = op.getBounds();
      

      if (!dstBounds.contains(dstROI.getBounds())) {
        dstROI = dstROI.intersect(new ROIShape(dstBounds));
      }
      

      return dstROI;
    }
    
    return Image.UndefinedProperty;
  }
}
