package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;


























































































































































































public class TranslateDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Translate" }, { "LocalName", "Translate" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("TranslateDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/TranslateDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("TranslateDescriptor1") }, { "arg1Desc", JaiI18N.getString("TranslateDescriptor2") }, { "arg2Desc", JaiI18N.getString("TranslateDescriptor3") } };
  











  private static final String[] paramNames = { "xTrans", "yTrans", "interpolation" };
  



  private static final Class[] paramClasses = { Float.class, Float.class, Interpolation.class };
  




  private static final Object[] paramDefaults = { new Float(0.0F), new Float(0.0F), Interpolation.getInstance(0) };
  



  public TranslateDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  
  public boolean isRenderableSupported()
  {
    return true;
  }
  





  public PropertyGenerator[] getPropertyGenerators()
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new TranslatePropertyGenerator();
    return pg;
  }
  



























  public static RenderedOp create(RenderedImage source0, Float xTrans, Float yTrans, Interpolation interpolation, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Translate", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("xTrans", xTrans);
    pb.setParameter("yTrans", yTrans);
    pb.setParameter("interpolation", interpolation);
    
    return JAI.create("Translate", pb, hints);
  }
  


























  public static RenderableOp createRenderable(RenderableImage source0, Float xTrans, Float yTrans, Interpolation interpolation, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Translate", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("xTrans", xTrans);
    pb.setParameter("yTrans", yTrans);
    pb.setParameter("interpolation", interpolation);
    
    return JAI.createRenderable("Translate", pb, hints);
  }
}
