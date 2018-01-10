package javax.media.jai.operator;

import com.sun.media.jai.codec.ImageDecodeParam;
import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.InputStream;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;





































































public class FileLoadDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "FileLoad" }, { "LocalName", "FileLoad" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("FileLoadDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/FileLoadDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("FileLoadDescriptor1") }, { "arg1Desc", JaiI18N.getString("FileLoadDescriptor4") }, { "arg2Desc", JaiI18N.getString("FileLoadDescriptor5") } };
  











  private static final String[] paramNames = { "filename", "param", "checkFileLocally" };
  



  private static final Class[] paramClasses = { String.class, ImageDecodeParam.class, Boolean.class };
  





  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, null, Boolean.TRUE };
  


  public FileLoadDescriptor()
  {
    super(resources, 0, paramClasses, paramNames, paramDefaults);
  }
  








  protected boolean validateParameters(ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateParameters(args, msg)) {
      return false;
    }
    
    Boolean checkFile = (Boolean)args.getObjectParameter(2);
    if (checkFile.booleanValue()) {
      String filename = (String)args.getObjectParameter(0);
      File f = new File(filename);
      boolean fileExists = f.exists();
      if (!fileExists)
      {


        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        if (is == null) {
          msg.append("\"" + filename + "\": " + JaiI18N.getString("FileLoadDescriptor2"));
          
          return false;
        }
      }
      else if (!f.canRead()) {
        msg.append("\"" + filename + "\": " + JaiI18N.getString("FileLoadDescriptor3"));
        
        return false;
      }
    }
    
    return true;
  }
  
























  public static RenderedOp create(String filename, ImageDecodeParam param, Boolean checkFileLocally, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("FileLoad", "rendered");
    


    pb.setParameter("filename", filename);
    pb.setParameter("param", param);
    pb.setParameter("checkFileLocally", checkFileLocally);
    
    return JAI.create("FileLoad", pb, hints);
  }
}
