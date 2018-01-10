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











































































public class ThresholdDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Threshold" }, { "LocalName", "Threshold" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("ThresholdDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ThresholdDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("ThresholdDescriptor1") }, { "arg1Desc", JaiI18N.getString("ThresholdDescriptor2") }, { "arg2Desc", JaiI18N.getString("ThresholdDescriptor3") } };
  











  private static final String[] paramNames = { "low", "high", "constants" };
  



  private static final Class[] paramClasses = { new double[0].getClass(), new double[0].getClass(), new double[0].getClass() };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT };
  


  public ThresholdDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  
  public boolean isRenderableSupported()
  {
    return true;
  }
  

  protected boolean validateParameters(ParameterBlock args, StringBuffer msg)
  {
    int numParams = args.getNumParameters();
    if (numParams < 3) {
      msg.append(getName() + " " + JaiI18N.getString("ThresholdDescriptor4"));
      
      return false;
    }
    
    for (int i = 0; i < 3; i++) {
      Object p = args.getObjectParameter(i);
      
      if (p == null) {
        msg.append(getName() + " " + JaiI18N.getString("ThresholdDescriptor5"));
        
        return false;
      }
      
      if (!(p instanceof double[])) {
        msg.append(getName() + " " + JaiI18N.getString("ThresholdDescriptor6"));
        
        return false;
      }
      
      if (((double[])p).length < 1) {
        msg.append(getName() + " " + JaiI18N.getString("ThresholdDescriptor7"));
        
        return false;
      }
    }
    
    return true;
  }
  



























  public static RenderedOp create(RenderedImage source0, double[] low, double[] high, double[] constants, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Threshold", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("low", low);
    pb.setParameter("high", high);
    pb.setParameter("constants", constants);
    
    return JAI.create("Threshold", pb, hints);
  }
  


























  public static RenderableOp createRenderable(RenderableImage source0, double[] low, double[] high, double[] constants, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Threshold", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("low", low);
    pb.setParameter("high", high);
    pb.setParameter("constants", constants);
    
    return JAI.createRenderable("Threshold", pb, hints);
  }
}
