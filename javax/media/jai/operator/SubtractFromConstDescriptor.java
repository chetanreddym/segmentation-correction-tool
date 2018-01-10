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


































































public class SubtractFromConstDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "SubtractFromConst" }, { "LocalName", "SubtractFromConst" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("SubtractFromConstDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/SubtractFromConstDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("SubtractFromConstDescriptor1") } };
  















  private static final Class[] paramClasses = { new double[0].getClass() };
  



  private static final String[] paramNames = { "constants" };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT };
  


  public SubtractFromConstDescriptor()
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
      message.append(getName() + " " + JaiI18N.getString("SubtractFromConstDescriptor2"));
      
      return false;
    }
    
    return true;
  }
  





















  public static RenderedOp create(RenderedImage source0, double[] constants, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("SubtractFromConst", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("constants", constants);
    
    return JAI.create("SubtractFromConst", pb, hints);
  }
  




















  public static RenderableOp createRenderable(RenderableImage source0, double[] constants, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("SubtractFromConst", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("constants", constants);
    
    return JAI.createRenderable("SubtractFromConst", pb, hints);
  }
}
