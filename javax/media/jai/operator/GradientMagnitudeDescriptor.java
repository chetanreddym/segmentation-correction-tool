package javax.media.jai.operator;

import com.sun.media.jai.util.AreaOpPropertyGenerator;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderedOp;













































































public class GradientMagnitudeDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "GradientMagnitude" }, { "LocalName", "GradientMagnitude" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("GradientMagnitudeDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jaiapi/javax.media.jai.operator.GradientMagnitudeDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", "A gradient mask." }, { "arg1Desc", "A gradient mask orthogonal to the first one." } };
  










  private static final String[] paramNames = { "mask1", "mask2" };
  




  private static final Class[] paramClasses = { KernelJAI.class, KernelJAI.class };
  




  private static final Object[] paramDefaults = { KernelJAI.GRADIENT_MASK_SOBEL_HORIZONTAL, KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL };
  



  public GradientMagnitudeDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  







  protected boolean validateParameters(ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateParameters(args, msg)) {
      return false;
    }
    
    KernelJAI h_kernel = (KernelJAI)args.getObjectParameter(0);
    KernelJAI v_kernel = (KernelJAI)args.getObjectParameter(1);
    

    if ((h_kernel.getWidth() != v_kernel.getWidth()) || (h_kernel.getHeight() != v_kernel.getHeight()))
    {
      msg.append(getName() + " " + JaiI18N.getString("GradientMagnitudeDescriptor1"));
      
      return false;
    }
    
    return true;
  }
  





  public PropertyGenerator[] getPropertyGenerators()
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new AreaOpPropertyGenerator();
    return pg;
  }
  
























  public static RenderedOp create(RenderedImage source0, KernelJAI mask1, KernelJAI mask2, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("GradientMagnitude", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("mask1", mask1);
    pb.setParameter("mask2", mask2);
    
    return JAI.create("GradientMagnitude", pb, hints);
  }
}
