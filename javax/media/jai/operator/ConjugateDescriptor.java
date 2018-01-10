package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;























































public class ConjugateDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Conjugate" }, { "LocalName", "Conjugate" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("ConjugateDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ConjugateDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") } };
  







  private static final String[] supportedModes = { "rendered", "renderable" };
  



  public ConjugateDescriptor()
  {
    super(resources, supportedModes, 1, null, null, null, null);
  }
  





  public PropertyGenerator[] getPropertyGenerators(String modeName)
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new ComplexPropertyGenerator();
    return pg;
  }
  








  protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateSources(modeName, args, msg)) {
      return false;
    }
    
    if (!modeName.equalsIgnoreCase("rendered")) {
      return true;
    }
    RenderedImage src = args.getRenderedSource(0);
    
    if (src.getSampleModel().getNumBands() % 2 != 0) {
      msg.append(getName() + " " + JaiI18N.getString("ConjugateDescriptor1"));
      
      return false;
    }
    
    return true;
  }
  


















  public static RenderedOp create(RenderedImage source0, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Conjugate", "rendered");
    


    pb.setSource("source0", source0);
    
    return JAI.create("Conjugate", pb, hints);
  }
  

















  public static RenderableOp createRenderable(RenderableImage source0, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Conjugate", "renderable");
    


    pb.setSource("source0", source0);
    
    return JAI.createRenderable("Conjugate", pb, hints);
  }
}
