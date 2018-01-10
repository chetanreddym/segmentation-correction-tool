package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;


































































public class SubtractConstDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "SubtractConst" }, { "LocalName", "SubtractConst" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("SubtractConstDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/SubtractConstDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("SubtractConstDescriptor1") } };
  















  private static final Class[] paramClasses = { new double[0].getClass() };
  



  private static final String[] paramNames = { "constants" };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT };
  


  public SubtractConstDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  
  public boolean isRenderableSupported()
  {
    return true;
  }
  







  protected boolean validateParameters(ParameterBlock args, StringBuffer message)
  {
    if (!super.validateParameters(args, message)) {
      return false;
    }
    
    int length = ((double[])args.getObjectParameter(0)).length;
    if (length < 1) {
      message.append(getName() + " " + JaiI18N.getString("SubtractConstDescriptor2"));
      
      return false;
    }
    
    return true;
  }
  





















  public static RenderedOp create(RenderedImage source0, double[] constants, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("SubtractConst", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("constants", constants);
    
    return JAI.create("SubtractConst", pb, hints);
  }
  




















  public static RenderableOp createRenderable(RenderableImage source0, double[] constants, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("SubtractConst", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("constants", constants);
    
    return JAI.createRenderable("SubtractConst", pb, hints);
  }
}
