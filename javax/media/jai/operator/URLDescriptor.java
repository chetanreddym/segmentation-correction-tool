package javax.media.jai.operator;

import com.sun.media.jai.codec.ImageDecodeParam;
import java.awt.RenderingHints;
import java.net.URL;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;





























































public class URLDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "URL" }, { "LocalName", "URL" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("URLDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/URLDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("URLDescriptor1") }, { "arg1Desc", JaiI18N.getString("URLDescriptor2") } };
  










  private static final String[] paramNames = { "URL", "param" };
  



  private static final Class[] paramClasses = { URL.class, ImageDecodeParam.class };
  




  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, null };
  


  public URLDescriptor()
  {
    super(resources, 0, paramClasses, paramNames, paramDefaults);
  }
  





















  public static RenderedOp create(URL URL, ImageDecodeParam param, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("URL", "rendered");
    


    pb.setParameter("URL", URL);
    pb.setParameter("param", param);
    
    return JAI.create("URL", pb, hints);
  }
}
