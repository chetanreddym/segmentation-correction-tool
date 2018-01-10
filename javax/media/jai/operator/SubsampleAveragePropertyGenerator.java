package javax.media.jai.operator;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.Interpolation;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;






























class SubsampleAveragePropertyGenerator
  implements PropertyGenerator
{
  public SubsampleAveragePropertyGenerator() {}
  
  public String[] getPropertyNames()
  {
    String[] properties = new String[1];
    properties[0] = "ROI";
    return properties;
  }
  



  public Class getClass(String propertyName)
  {
    if (propertyName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("SubsampleAveragePropertyGenerator0"));
    }
    if (propertyName.equalsIgnoreCase("roi")) {
      return ROI.class;
    }
    
    return null;
  }
  


  public boolean canGenerateProperties(Object opNode)
  {
    if (opNode == null) {
      throw new IllegalArgumentException(JaiI18N.getString("SubsampleAveragePropertyGenerator1"));
    }
    

    return opNode instanceof RenderedOp;
  }
  







  public Object getProperty(String name, Object opNode)
  {
    if ((name == null) || (opNode == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("SubsampleAveragePropertyGenerator2"));
    }
    if (!canGenerateProperties(opNode)) {
      throw new IllegalArgumentException(opNode.getClass().getName() + JaiI18N.getString("SubsampleAveragePropertyGenerator3"));
    }
    


    return (opNode instanceof RenderedOp) ? getProperty(name, (RenderedOp)opNode) : null;
  }
  







  public Object getProperty(String name, RenderedOp op)
  {
    if ((name == null) || (op == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("SubsampleAveragePropertyGenerator4"));
    }
    

    if (name.equals("roi")) {
      ParameterBlock pb = op.getParameterBlock();
      

      PlanarImage src = (PlanarImage)pb.getRenderedSource(0);
      Object property = src.getProperty("ROI");
      if ((property == null) || (property.equals(Image.UndefinedProperty)) || (!(property instanceof ROI)))
      {

        return null;
      }
      ROI srcROI = (ROI)property;
      

      Rectangle srcBounds = null;
      PlanarImage dst = op.getRendering();
      if (((dst instanceof GeometricOpImage)) && (((GeometricOpImage)dst).getBorderExtender() == null))
      {
        GeometricOpImage geomIm = (GeometricOpImage)dst;
        Interpolation interp = geomIm.getInterpolation();
        srcBounds = new Rectangle(src.getMinX() + interp.getLeftPadding(), src.getMinY() + interp.getTopPadding(), src.getWidth() - interp.getWidth() + 1, src.getHeight() - interp.getHeight() + 1);

      }
      else
      {

        srcBounds = src.getBounds();
      }
      

      if (!srcBounds.contains(srcROI.getBounds())) {
        srcROI = srcROI.intersect(new ROIShape(srcBounds));
      }
      

      double sx = pb.getDoubleParameter(0);
      double sy = pb.getDoubleParameter(1);
      

      AffineTransform transform = new AffineTransform(sx, 0.0D, 0.0D, sy, 0.0D, 0.0D);
      


      ROI dstROI = srcROI.transform(transform);
      

      Rectangle dstBounds = op.getBounds();
      

      if (!dstBounds.contains(dstROI.getBounds())) {
        dstROI = dstROI.intersect(new ROIShape(dstBounds));
      }
      

      return dstROI;
    }
    return null;
  }
  







  public Object getProperty(String name, RenderableOp op)
  {
    if ((name == null) || (op == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("SubsampleAveragePropertyGenerator2"));
    }
    

    return null;
  }
}
