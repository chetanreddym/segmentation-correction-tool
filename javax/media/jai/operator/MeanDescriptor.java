package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.ROI;
import javax.media.jai.RenderedOp;







































































public class MeanDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Mean" }, { "LocalName", "Mean" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("MeanDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/MeanDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("MeanDescriptor1") }, { "arg1Desc", JaiI18N.getString("MeanDescriptor2") }, { "arg2Desc", JaiI18N.getString("MeanDescriptor3") } };
  











  private static final String[] paramNames = { "roi", "xPeriod", "yPeriod" };
  



  private static final Class[] paramClasses = { ROI.class, Integer.class, Integer.class };
  





  private static final Object[] paramDefaults = { null, new Integer(1), new Integer(1) };
  


  public MeanDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  



  public Number getParamMinValue(int index)
  {
    if (index == 0)
      return null;
    if ((index == 1) || (index == 2)) {
      return new Integer(1);
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  




























  public static RenderedOp create(RenderedImage source0, ROI roi, Integer xPeriod, Integer yPeriod, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Mean", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("roi", roi);
    pb.setParameter("xPeriod", xPeriod);
    pb.setParameter("yPeriod", yPeriod);
    
    return JAI.create("Mean", pb, hints);
  }
}
