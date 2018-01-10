package javax.media.jai.operator;

import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.SeekableStream;
import java.awt.RenderingHints;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;































































public class StreamDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Stream" }, { "LocalName", "Stream" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("StreamDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/StreamDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("StreamDescriptor1") }, { "arg1Desc", JaiI18N.getString("StreamDescriptor2") } };
  










  private static final String[] paramNames = { "stream", "param" };
  



  private static final Class[] paramClasses = { SeekableStream.class, ImageDecodeParam.class };
  




  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, null };
  


  public StreamDescriptor()
  {
    super(resources, 0, paramClasses, paramNames, paramDefaults);
  }
  





















  public static RenderedOp create(SeekableStream stream, ImageDecodeParam param, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Stream", "rendered");
    


    pb.setParameter("stream", stream);
    pb.setParameter("param", param);
    
    return JAI.create("Stream", pb, hints);
  }
}
