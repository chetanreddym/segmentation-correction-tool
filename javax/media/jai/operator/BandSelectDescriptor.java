package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;






























































public class BandSelectDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "BandSelect" }, { "LocalName", "BandSelect" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("BandSelectDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/BandSelectDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("BandSelectDescriptor1") } };
  









  private static final Class[] paramClasses = { new int[0].getClass() };
  



  private static final String[] paramNames = { "bandIndices" };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT };
  


  private static final String[] supportedModes = { "rendered", "renderable" };
  



  public BandSelectDescriptor()
  {
    super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
  }
  










  public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer message)
  {
    if (!super.validateArguments(modeName, args, message)) {
      return false;
    }
    
    if (!modeName.equalsIgnoreCase("rendered")) {
      return true;
    }
    int[] indices = (int[])args.getObjectParameter(0);
    if (indices.length < 1) {
      message.append(getName() + " " + JaiI18N.getString("BandSelectDescriptor2"));
      
      return false;
    }
    
    RenderedImage src = args.getRenderedSource(0);
    
    int bands = src.getSampleModel().getNumBands();
    for (int i = 0; i < indices.length; i++) {
      if ((indices[i] < 0) || (indices[i] >= bands)) {
        message.append(getName() + " " + JaiI18N.getString("BandSelectDescriptor3"));
        
        return false;
      }
    }
    
    return true;
  }
  





















  public static RenderedOp create(RenderedImage source0, int[] bandIndices, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("BandSelect", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("bandIndices", bandIndices);
    
    return JAI.create("BandSelect", pb, hints);
  }
  




















  public static RenderableOp createRenderable(RenderableImage source0, int[] bandIndices, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("BandSelect", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("bandIndices", bandIndices);
    
    return JAI.createRenderable("BandSelect", pb, hints);
  }
}
