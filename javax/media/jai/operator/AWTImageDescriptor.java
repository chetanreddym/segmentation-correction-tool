package javax.media.jai.operator;

import java.awt.Image;
import java.awt.RenderingHints;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;

















































public class AWTImageDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "AWTImage" }, { "LocalName", "AWTImage" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("AWTImageDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AWTImageDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("AWTImageDescriptor1") } };
  









  private static final Class[] paramClasses = { Image.class };
  



  private static final String[] paramNames = { "awtImage" };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT };
  


  public AWTImageDescriptor()
  {
    super(resources, 0, paramClasses, paramNames, paramDefaults);
  }
  


















  public static RenderedOp create(Image awtImage, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("AWTImage", "rendered");
    


    pb.setParameter("awtImage", awtImage);
    
    return JAI.create("AWTImage", pb, hints);
  }
}
