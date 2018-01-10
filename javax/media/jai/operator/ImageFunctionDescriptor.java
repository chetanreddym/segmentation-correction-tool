package javax.media.jai.operator;

import java.awt.RenderingHints;
import javax.media.jai.ImageFunction;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderedOp;


























































































































































public class ImageFunctionDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "ImageFunction" }, { "LocalName", "ImageFunction" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("ImageFunctionDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ImageFunctionDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("ImageFunctionDescriptor1") }, { "arg1Desc", JaiI18N.getString("ImageFunctionDescriptor2") }, { "arg2Desc", JaiI18N.getString("ImageFunctionDescriptor3") }, { "arg3Desc", JaiI18N.getString("ImageFunctionDescriptor4") }, { "arg4Desc", JaiI18N.getString("ImageFunctionDescriptor5") }, { "arg5Desc", JaiI18N.getString("ImageFunctionDescriptor6") }, { "arg6Desc", JaiI18N.getString("ImageFunctionDescriptor7") } };
  















  private static final Class[] paramClasses = { ImageFunction.class, Integer.class, Integer.class, Float.class, Float.class, Float.class, Float.class };
  






  private static final String[] paramNames = { "function", "width", "height", "xScale", "yScale", "xTrans", "yTrans" };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT, new Float(1.0F), new Float(1.0F), new Float(0.0F), new Float(0.0F) };
  




  public ImageFunctionDescriptor()
  {
    super(resources, 0, paramClasses, paramNames, paramDefaults);
  }
  





  public PropertyGenerator[] getPropertyGenerators()
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new ImageFunctionPropertyGenerator();
    return pg;
  }
  




































  public static RenderedOp create(ImageFunction function, Integer width, Integer height, Float xScale, Float yScale, Float xTrans, Float yTrans, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("ImageFunction", "rendered");
    


    pb.setParameter("function", function);
    pb.setParameter("width", width);
    pb.setParameter("height", height);
    pb.setParameter("xScale", xScale);
    pb.setParameter("yScale", yScale);
    pb.setParameter("xTrans", xTrans);
    pb.setParameter("yTrans", yTrans);
    
    return JAI.create("ImageFunction", pb, hints);
  }
}
