package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.util.Range;

































































public class ConstantDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Constant" }, { "LocalName", "Constant" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("ConstantDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ConstantDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("ConstantDescriptor1") }, { "arg1Desc", JaiI18N.getString("ConstantDescriptor2") }, { "arg2Desc", JaiI18N.getString("ConstantDescriptor3") } };
  











  private static final Class[] paramClasses = { Float.class, Float.class, new Number[0].getClass() };
  





  private static final String[] paramNames = { "width", "height", "bandValues" };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT };
  


  private static final String[] supportedModes = { "rendered", "renderable" };
  



  private static final Object[] validParamValues = { new Range(Float.class, new Float(0.0F), false, null, false), new Range(Float.class, new Float(0.0F), false, null, false), null };
  




  public ConstantDescriptor()
  {
    super(resources, supportedModes, 0, paramNames, paramClasses, paramDefaults, validParamValues);
  }
  










  protected boolean validateParameters(String modeName, ParameterBlock args, StringBuffer message)
  {
    if (!super.validateParameters(modeName, args, message)) {
      return false;
    }
    
    int length = ((Number[])args.getObjectParameter(2)).length;
    if (length < 1) {
      message.append(getName() + " " + JaiI18N.getString("ConstantDescriptor4"));
      
      return false;
    }
    
    if (modeName.equalsIgnoreCase("rendered")) {
      int width = Math.round(args.getFloatParameter(0));
      int height = Math.round(args.getFloatParameter(1));
      
      if ((width < 1) || (height < 1)) {
        message.append(getName() + " " + JaiI18N.getString("ConstantDescriptor5"));
        
        return false;
      }
    } else if (modeName.equalsIgnoreCase("renderable")) {
      float width = args.getFloatParameter(0);
      float height = args.getFloatParameter(1);
      
      if ((width <= 0.0F) || (height <= 0.0F)) {
        message.append(getName() + " " + JaiI18N.getString("ConstantDescriptor6"));
        
        return false;
      }
    }
    
    return true;
  }
  
























  public static RenderedOp create(Float width, Float height, Number[] bandValues, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Constant", "rendered");
    


    pb.setParameter("width", width);
    pb.setParameter("height", height);
    pb.setParameter("bandValues", bandValues);
    
    return JAI.create("Constant", pb, hints);
  }
  























  public static RenderableOp createRenderable(Float width, Float height, Number[] bandValues, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Constant", "renderable");
    


    pb.setParameter("width", width);
    pb.setParameter("height", height);
    pb.setParameter("bandValues", bandValues);
    
    return JAI.createRenderable("Constant", pb, hints);
  }
}
