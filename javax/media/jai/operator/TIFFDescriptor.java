package javax.media.jai.operator;

import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import java.awt.RenderingHints;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;









































































































































public class TIFFDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "TIFF" }, { "LocalName", "TIFF" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("TIFFDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/TIFFDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("TIFFDescriptor1") }, { "arg1Desc", JaiI18N.getString("TIFFDescriptor2") }, { "arg2Desc", JaiI18N.getString("TIFFDescriptor3") } };
  











  private static final String[] paramNames = { "stream", "param", "page" };
  



  private static final Class[] paramClasses = { SeekableStream.class, TIFFDecodeParam.class, Integer.class };
  





  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, null, new Integer(0) };
  


  public TIFFDescriptor()
  {
    super(resources, 0, paramClasses, paramNames, paramDefaults);
  }
  
























  public static RenderedOp create(SeekableStream stream, TIFFDecodeParam param, Integer page, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("TIFF", "rendered");
    


    pb.setParameter("stream", stream);
    pb.setParameter("param", param);
    pb.setParameter("page", page);
    
    return JAI.create("TIFF", pb, hints);
  }
}
