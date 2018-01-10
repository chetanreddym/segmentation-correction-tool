package javax.media.jai.operator;

import com.sun.media.jai.codec.FPXDecodeParam;
import com.sun.media.jai.codec.SeekableStream;
import java.awt.RenderingHints;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;























































public class FPXDescriptor
  extends OperationDescriptorImpl
{
  public static final Integer MAX_RESOLUTION = new Integer(-1);
  




  private static final String[][] resources = { { "GlobalName", "FPX" }, { "LocalName", "FPX" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("FPXDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/FPXDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("FPXDescriptor1") }, { "arg1Desc", JaiI18N.getString("FPXDescriptor2") } };
  










  private static final String[] paramNames = { "stream", "param" };
  



  private static final Class[] paramClasses = { SeekableStream.class, FPXDecodeParam.class };
  




  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, null };
  


  public FPXDescriptor()
  {
    super(resources, 0, paramClasses, paramNames, paramDefaults);
  }
  





















  public static RenderedOp create(SeekableStream stream, FPXDecodeParam param, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("FPX", "rendered");
    


    pb.setParameter("stream", stream);
    pb.setParameter("param", param);
    
    return JAI.create("FPX", pb, hints);
  }
}
