package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.ROI;
import javax.media.jai.RenderedOp;































































































































public class ExtremaDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Extrema" }, { "LocalName", "Extrema" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("ExtremaDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ExtremaDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("ExtremaDescriptor1") }, { "arg1Desc", JaiI18N.getString("ExtremaDescriptor2") }, { "arg2Desc", JaiI18N.getString("ExtremaDescriptor3") }, { "arg3Desc", JaiI18N.getString("ExtremaDescriptor4") }, { "arg4Desc", JaiI18N.getString("ExtremaDescriptor5") } };
  













  private static final String[] paramNames = { "roi", "xPeriod", "yPeriod", "saveLocations", "maxRuns" };
  



  private static final Class[] paramClasses = { ROI.class, Integer.class, Integer.class, Boolean.class, Integer.class };
  







  private static final Object[] paramDefaults = { null, new Integer(1), new Integer(1), Boolean.FALSE, new Integer(1) };
  



  public ExtremaDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  



  public Number getParamMinValue(int index)
  {
    if ((index == 0) || (index == 3))
      return null;
    if ((index == 1) || (index == 2) || (index == 4)) {
      return new Integer(1);
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  


































  public static RenderedOp create(RenderedImage source0, ROI roi, Integer xPeriod, Integer yPeriod, Boolean saveLocations, Integer maxRuns, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Extrema", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("roi", roi);
    pb.setParameter("xPeriod", xPeriod);
    pb.setParameter("yPeriod", yPeriod);
    pb.setParameter("saveLocations", saveLocations);
    pb.setParameter("maxRuns", maxRuns);
    
    return JAI.create("Extrema", pb, hints);
  }
}
