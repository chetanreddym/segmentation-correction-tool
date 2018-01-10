package javax.media.jai.operator;

import com.sun.media.jai.codec.PNGDecodeParam;
import com.sun.media.jai.codec.SeekableStream;
import java.awt.RenderingHints;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;


























































































































public class PNGDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "PNG" }, { "LocalName", "PNG" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("PNGDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/PNGDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("PNGDescriptor1") }, { "arg1Desc", JaiI18N.getString("PNGDescriptor2") } };
  










  private static final String[] paramNames = { "stream", "param" };
  



  private static final Class[] paramClasses = { SeekableStream.class, PNGDecodeParam.class };
  




  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, null };
  


  public PNGDescriptor()
  {
    super(resources, 0, paramClasses, paramNames, paramDefaults);
  }
  





















  public static RenderedOp create(SeekableStream stream, PNGDecodeParam param, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("PNG", "rendered");
    


    pb.setParameter("stream", stream);
    pb.setParameter("param", param);
    
    return JAI.create("PNG", pb, hints);
  }
}
