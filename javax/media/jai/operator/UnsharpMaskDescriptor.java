package javax.media.jai.operator;

import com.sun.media.jai.util.AreaOpPropertyGenerator;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderedOp;




















































































public class UnsharpMaskDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "UnsharpMask" }, { "LocalName", "UnsharpMask" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("UnsharpMaskDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/UnsharpMaskDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("UnsharpMaskDescriptor1") }, { "arg1Desc", JaiI18N.getString("UnsharpMaskDescriptor2") } };
  










  private static final String[] paramNames = { "kernel", "gain" };
  



  private static final Class[] paramClasses = { KernelJAI.class, Float.class };
  




  private static final Object[] paramDefaults = { new KernelJAI(3, 3, 1, 1, new float[] { 0.11111111F, 0.11111111F, 0.11111111F, 0.11111111F, 0.11111111F, 0.11111111F, 0.11111111F, 0.11111111F, 0.11111111F }), new Float(1.0F) };
  



  public UnsharpMaskDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  





  public PropertyGenerator[] getPropertyGenerators()
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new AreaOpPropertyGenerator();
    return pg;
  }
  
























  public static RenderedOp create(RenderedImage source0, KernelJAI kernel, Float gain, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("UnsharpMask", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("kernel", kernel);
    pb.setParameter("gain", gain);
    
    return JAI.create("UnsharpMask", pb, hints);
  }
}
