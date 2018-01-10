package javax.media.jai.operator;

import com.sun.media.jai.codec.SeekableStream;
import java.awt.RenderingHints;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;


















































public class GIFDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "GIF" }, { "LocalName", "GIF" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("GIFDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/GIFDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("GIFDescriptor1") } };
  









  private static final String[] paramNames = { "stream" };
  



  private static final Class[] paramClasses = { SeekableStream.class };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT };
  


  public GIFDescriptor()
  {
    super(resources, 0, paramClasses, paramNames, paramDefaults);
  }
  


















  public static RenderedOp create(SeekableStream stream, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("GIF", "rendered");
    


    pb.setParameter("stream", stream);
    
    return JAI.create("GIF", pb, hints);
  }
}
