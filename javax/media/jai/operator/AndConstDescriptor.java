package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;











































































public class AndConstDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "AndConst" }, { "LocalName", "AndConst" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("AndConstDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AndConstDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("AndConstDescriptor1") } };
  















  private static final Class[] paramClasses = { new int[0].getClass() };
  



  private static final String[] paramNames = { "constants" };
  



  private static final Object[] paramDefaults = { { -1 } };
  


  private static final String[] supportedModes = { "rendered", "renderable" };
  



  public AndConstDescriptor()
  {
    super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
  }
  









  public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer message)
  {
    if (!super.validateArguments(modeName, args, message)) {
      return false;
    }
    
    if (!modeName.equalsIgnoreCase("rendered")) {
      return true;
    }
    RenderedImage src = args.getRenderedSource(0);
    
    int dtype = src.getSampleModel().getDataType();
    
    if ((dtype != 0) && (dtype != 1) && (dtype != 2) && (dtype != 3))
    {


      message.append(getName() + " " + JaiI18N.getString("AndConstDescriptor1"));
      
      return false;
    }
    
    int length = ((int[])args.getObjectParameter(0)).length;
    
    if (length < 1) {
      message.append(getName() + " " + JaiI18N.getString("AndConstDescriptor2"));
      
      return false;
    }
    
    return true;
  }
  





















  public static RenderedOp create(RenderedImage source0, int[] constants, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("AndConst", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("constants", constants);
    
    return JAI.create("AndConst", pb, hints);
  }
  




















  public static RenderableOp createRenderable(RenderableImage source0, int[] constants, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("AndConst", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("constants", constants);
    
    return JAI.createRenderable("AndConst", pb, hints);
  }
}
