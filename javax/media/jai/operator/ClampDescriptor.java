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










































































public class ClampDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Clamp" }, { "LocalName", "Clamp" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("ClampDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ClampDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("ClampDescriptor1") }, { "arg1Desc", JaiI18N.getString("ClampDescriptor2") } };
  










  private static final Class[] paramClasses = { new double[0].getClass(), new double[0].getClass() };
  



  private static final String[] paramNames = { "low", "high" };
  



  private static final Object[] paramDefaults = { { 0.0D }, { 255.0D } };
  


  public ClampDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  
  public boolean isRenderableSupported()
  {
    return true;
  }
  








  protected boolean validateParameters(ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateParameters(args, msg)) {
      return false;
    }
    
    double[] low = (double[])args.getObjectParameter(0);
    double[] high = (double[])args.getObjectParameter(1);
    
    if ((low.length < 1) || (high.length < 1)) {
      msg.append(getName() + " " + JaiI18N.getString("ClampDescriptor3"));
      
      return false;
    }
    
    int length = Math.min(low.length, high.length);
    for (int i = 0; i < length; i++) {
      if (low[i] > high[i]) {
        msg.append(getName() + " " + JaiI18N.getString("ClampDescriptor4"));
        
        return false;
      }
    }
    
    return true;
  }
  
























  public static RenderedOp create(RenderedImage source0, double[] low, double[] high, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Clamp", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("low", low);
    pb.setParameter("high", high);
    
    return JAI.create("Clamp", pb, hints);
  }
  























  public static RenderableOp createRenderable(RenderableImage source0, double[] low, double[] high, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Clamp", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("low", low);
    pb.setParameter("high", high);
    
    return JAI.createRenderable("Clamp", pb, hints);
  }
}
