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






































































































































































public class DilateDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Dilate" }, { "LocalName", "Dilate" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("DilateDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jaiapi/<br>javax.media.jai.operator.DilateDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("DilateDescriptor1") } };
  









  private static final String[] paramNames = { "kernel" };
  



  private static final Class[] paramClasses = { KernelJAI.class };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT };
  


  public DilateDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  





  public PropertyGenerator[] getPropertyGenerators()
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new AreaOpPropertyGenerator();
    return pg;
  }
  





















  public static RenderedOp create(RenderedImage source0, KernelJAI kernel, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Dilate", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("kernel", kernel);
    
    return JAI.create("Dilate", pb, hints);
  }
}
