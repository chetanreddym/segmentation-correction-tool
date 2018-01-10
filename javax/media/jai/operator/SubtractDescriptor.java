package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;







































































public class SubtractDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Subtract" }, { "LocalName", "Subtract" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("SubtractDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/SubtractDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") } };
  







  public SubtractDescriptor()
  {
    super(resources, 2, null, null, null);
  }
  
  public boolean isRenderableSupported()
  {
    return true;
  }
  





















  public static RenderedOp create(RenderedImage source0, RenderedImage source1, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Subtract", "rendered");
    


    pb.setSource("source0", source0);
    pb.setSource("source1", source1);
    
    return JAI.create("Subtract", pb, hints);
  }
  




















  public static RenderableOp createRenderable(RenderableImage source0, RenderableImage source1, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Subtract", "renderable");
    


    pb.setSource("source0", source0);
    pb.setSource("source1", source1);
    
    return JAI.createRenderable("Subtract", pb, hints);
  }
}
