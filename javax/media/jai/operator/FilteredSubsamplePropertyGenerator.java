package javax.media.jai.operator;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.WarpOpImage;
























class FilteredSubsamplePropertyGenerator
  extends PropertyGeneratorImpl
{
  public FilteredSubsamplePropertyGenerator()
  {
    super(new String[] { "FilteredSubsample" }, new Class[] { Boolean.TYPE }, new Class[] { RenderedOp.class, RenderableOp.class });
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

        return null;
      }
      ROI srcROI = (ROI)property;
      

      Rectangle srcBounds = null;
      PlanarImage dst = op.getRendering();
      if (((dst instanceof WarpOpImage)) && (!((OpImage)dst).hasExtender(0))) {
        WarpOpImage warpIm = (WarpOpImage)dst;
        srcBounds = new Rectangle(src.getMinX() + warpIm.getLeftPadding(), src.getMinY() + warpIm.getTopPadding(), src.getWidth() - warpIm.getWidth() + 1, src.getHeight() - warpIm.getHeight() + 1);

      }
      else
      {

        srcBounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
      }
      




      if (!srcBounds.contains(srcROI.getBounds())) {
        srcROI = srcROI.intersect(new ROIShape(srcBounds));
      }
      

      float sx = 1.0F / pb.getIntParameter(1);
      float sy = 1.0F / pb.getIntParameter(2);
      

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
  

  public String[] getPropertyNames()
  {
    String[] properties = new String[1];
    properties[0] = "roi";
    return properties;
  }
}
