package javax.media.jai.operator;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncodeParam;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.OutputStream;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
































































public class EncodeDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Encode" }, { "LocalName", "Encode" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("EncodeDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/EncodeDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("EncodeDescriptor1") }, { "arg1Desc", JaiI18N.getString("EncodeDescriptor2") }, { "arg2Desc", JaiI18N.getString("EncodeDescriptor3") } };
  











  private static final String[] paramNames = { "stream", "format", "param" };
  



  private static final Class[] paramClasses = { OutputStream.class, String.class, ImageEncodeParam.class };
  





  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, "tiff", null };
  


  private static final String[] supportedModes = { "rendered" };
  


  public EncodeDescriptor()
  {
    super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
  }
  











  public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if (!modeName.equalsIgnoreCase("rendered")) {
      return true;
    }
    
    if (args.getNumParameters() < 3) {
      args = (ParameterBlock)args.clone();
      args.set(null, 2);
    }
    
    if (!super.validateArguments(modeName, args, msg)) {
      return false;
    }
    

    String format = (String)args.getObjectParameter(1);
    

    ImageCodec codec = ImageCodec.getCodec(format);
    

    if (codec == null) {
      msg.append(getName() + " " + JaiI18N.getString("EncodeDescriptor4"));
      
      return false;
    }
    

    ImageEncodeParam param = (ImageEncodeParam)args.getObjectParameter(2);
    

    RenderedImage src = args.getRenderedSource(0);
    

    if (!codec.canEncodeImage(src, param)) {
      msg.append(getName() + " " + JaiI18N.getString("EncodeDescriptor5"));
      
      return false;
    }
    
    return true;
  }
  





  public boolean isImmediate()
  {
    return true;
  }
  



























  public static RenderedOp create(RenderedImage source0, OutputStream stream, String format, ImageEncodeParam param, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Encode", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("stream", stream);
    pb.setParameter("format", format);
    pb.setParameter("param", param);
    
    return JAI.create("Encode", pb, hints);
  }
}
