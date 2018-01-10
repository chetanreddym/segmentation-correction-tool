package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderedOp;
import javax.media.jai.Warp;











































































































































































































public class WarpDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Warp" }, { "LocalName", "Warp" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("WarpDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/WarpDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("WarpDescriptor1") }, { "arg1Desc", JaiI18N.getString("WarpDescriptor2") }, { "arg2Desc", JaiI18N.getString("WarpDescriptor3") } };
  











  private static final String[] paramNames = { "warp", "interpolation", "backgroundValues" };
  



  private static final Class[] paramClasses = { Warp.class, Interpolation.class, new double[0].getClass() };
  




  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, Interpolation.getInstance(0), { 0.0D } };
  




  public WarpDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  





  public PropertyGenerator[] getPropertyGenerators()
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new WarpPropertyGenerator();
    return pg;
  }
  



























  public static RenderedOp create(RenderedImage source0, Warp warp, Interpolation interpolation, double[] backgroundValues, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Warp", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("warp", warp);
    pb.setParameter("interpolation", interpolation);
    pb.setParameter("backgroundValues", backgroundValues);
    
    return JAI.create("Warp", pb, hints);
  }
}
