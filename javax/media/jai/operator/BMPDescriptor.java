package javax.media.jai.operator;

import com.sun.media.jai.codec.SeekableStream;
import java.awt.RenderingHints;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;

























































public class BMPDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "BMP" }, { "LocalName", "BMP" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("BMPDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/BMPDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("BMPDescriptor1") } };
  









  private static final String[] paramNames = { "stream" };
  



  private static final Class[] paramClasses = { SeekableStream.class };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT };
  


  public BMPDescriptor()
  {
    super(resources, 0, paramClasses, paramNames, paramDefaults);
  }
  


















  public static RenderedOp create(SeekableStream stream, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("BMP", "rendered");
    


    pb.setParameter("stream", stream);
    
    return JAI.create("BMP", pb, hints);
  }
}
