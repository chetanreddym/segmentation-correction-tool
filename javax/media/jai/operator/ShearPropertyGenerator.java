package javax.media.jai.operator;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderedOp;





















class ShearPropertyGenerator
  extends PropertyGeneratorImpl
{
  public ShearPropertyGenerator()
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
      

      Interpolation interp = (Interpolation)pb.getObjectParameter(4);
      

      Rectangle srcBounds = null;
      PlanarImage dst = op.getRendering();
      if (((dst instanceof GeometricOpImage)) && (((GeometricOpImage)dst).getBorderExtender() == null))
      {
        srcBounds = new Rectangle(src.getMinX() + interp.getLeftPadding(), src.getMinY() + interp.getTopPadding(), src.getWidth() - interp.getWidth() + 1, src.getHeight() - interp.getHeight() + 1);

      }
      else
      {

        srcBounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
      }
      




      Interpolation interpNN = (interp instanceof InterpolationNearest) ? interp : Interpolation.getInstance(0);
      



      float sv = pb.getFloatParameter(0);
      EnumeratedParameter shearDir = (EnumeratedParameter)pb.getObjectParameter(1);
      
      float tx = pb.getFloatParameter(2);
      float ty = pb.getFloatParameter(3);
      

      AffineTransform transform = new AffineTransform(1.0D, shearDir == ShearDescriptor.SHEAR_VERTICAL ? sv : 0.0D, shearDir == ShearDescriptor.SHEAR_HORIZONTAL ? sv : 0.0D, 1.0D, shearDir == ShearDescriptor.SHEAR_HORIZONTAL ? tx : 0.0D, shearDir == ShearDescriptor.SHEAR_VERTICAL ? ty : 0.0D);
      







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
