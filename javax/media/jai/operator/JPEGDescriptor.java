package javax.media.jai.operator;

import com.sun.media.jai.codec.SeekableStream;
import java.awt.RenderingHints;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;


















































public class JPEGDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "JPEG" }, { "LocalName", "JPEG" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("JPEGDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/JPEGDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("JPEGDescriptor1") } };
  









  private static final String[] paramNames = { "stream" };
  



  private static final Class[] paramClasses = { SeekableStream.class };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT };
  


  public JPEGDescriptor()
  {
    super(resources, 0, paramClasses, paramNames, paramDefaults);
  }
  


















  public static RenderedOp create(SeekableStream stream, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("JPEG", "rendered");
    


    pb.setParameter("stream", stream);
    
    return JAI.create("JPEG", pb, hints);
  }
}
