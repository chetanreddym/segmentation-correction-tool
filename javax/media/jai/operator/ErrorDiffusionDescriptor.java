package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
























































public class ErrorDiffusionDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "ErrorDiffusion" }, { "LocalName", "ErrorDiffusion" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("ErrorDiffusionDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ErrorDiffusionDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("ErrorDiffusionDescriptor1") }, { "arg1Desc", JaiI18N.getString("ErrorDiffusionDescriptor2") } };
  










  private static final String[] paramNames = { "colorMap", "errorKernel" };
  



  private static final Class[] paramClasses = { LookupTableJAI.class, KernelJAI.class };
  




  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, KernelJAI.ERROR_FILTER_FLOYD_STEINBERG };
  




  public ErrorDiffusionDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  
























  public static RenderedOp create(RenderedImage source0, LookupTableJAI colorMap, KernelJAI errorKernel, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("ErrorDiffusion", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("colorMap", colorMap);
    pb.setParameter("errorKernel", errorKernel);
    
    return JAI.create("ErrorDiffusion", pb, hints);
  }
}
