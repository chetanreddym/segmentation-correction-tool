package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderedOp;





















































































































































































































public class ShearDescriptor
  extends OperationDescriptorImpl
{
  public static final ShearDir SHEAR_HORIZONTAL = new ShearDir("SHEAR_HORIZONTAL", 0);
  
  public static final ShearDir SHEAR_VERTICAL = new ShearDir("SHEAR_VERTICAL", 1);
  





  private static final String[][] resources = { { "GlobalName", "Shear" }, { "LocalName", "Shear" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("ShearDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ShearDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion2") }, { "arg0Desc", JaiI18N.getString("ShearDescriptor1") }, { "arg1Desc", JaiI18N.getString("ShearDescriptor2") }, { "arg2Desc", JaiI18N.getString("ShearDescriptor3") }, { "arg3Desc", JaiI18N.getString("ShearDescriptor4") }, { "arg4Desc", JaiI18N.getString("ShearDescriptor5") }, { "arg5Desc", JaiI18N.getString("ShearDescriptor6") } };
  














  private static final String[] paramNames = { "shear", "shearDir", "xTrans", "yTrans", "interpolation", "backgroundValues" };
  




  private static final Class[] paramClasses = { Float.class, ShearDir.class, Float.class, Float.class, Interpolation.class, new double[0].getClass() };
  






  private static final Object[] paramDefaults = { new Float(0.0F), SHEAR_HORIZONTAL, new Float(0.0F), new Float(0.0F), Interpolation.getInstance(0), { 0.0D } };
  





  public ShearDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  





  public PropertyGenerator[] getPropertyGenerators()
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new ShearPropertyGenerator();
    return pg;
  }
  




































  public static RenderedOp create(RenderedImage source0, Float shear, ShearDir shearDir, Float xTrans, Float yTrans, Interpolation interpolation, double[] backgroundValues, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Shear", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("shear", shear);
    pb.setParameter("shearDir", shearDir);
    pb.setParameter("xTrans", xTrans);
    pb.setParameter("yTrans", yTrans);
    pb.setParameter("interpolation", interpolation);
    pb.setParameter("backgroundValues", backgroundValues);
    
    return JAI.create("Shear", pb, hints);
  }
}
