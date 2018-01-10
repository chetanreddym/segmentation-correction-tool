package javax.media.jai.operator;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.OperationNode;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;

















































public class NullDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Null" }, { "LocalName", "Null" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("NullDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/NullDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") } };
  







  private static final String[] supportedModes = { "rendered", "renderable" };
  



  public NullDescriptor()
  {
    super(resources, supportedModes, 1, null, null, null, null);
  }
  





  private static ParameterBlock foolSourceValidation(ParameterBlock args)
  {
    if (args.getNumSources() > 1) {
      Vector singleSource = new Vector();
      singleSource.add(args.getSource(0));
      args = new ParameterBlock(singleSource, args.getParameters());
    }
    return args;
  }
  











  protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if ((args == null) || (msg == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return super.validateSources(modeName, foolSourceValidation(args), msg);
  }
  

































  public Object getInvalidRegion(String modeName, ParameterBlock oldParamBlock, RenderingHints oldHints, ParameterBlock newParamBlock, RenderingHints newHints, OperationNode node)
  {
    if ((modeName == null) || (oldParamBlock == null) || (newParamBlock == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("NullDescriptor1"));
    }
    
    if ((oldParamBlock.getNumSources() < 1) || (newParamBlock.getNumSources() < 1))
    {

      throw new IllegalArgumentException(JaiI18N.getString("NullDescriptor2"));
    }
    
    return oldParamBlock.getSource(0).equals(newParamBlock.getSource(0)) ? new Rectangle() : null;
  }
  



















  public static RenderedOp create(RenderedImage source0, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Null", "rendered");
    


    pb.setSource("source0", source0);
    
    return JAI.create("Null", pb, hints);
  }
  

















  public static RenderableOp createRenderable(RenderableImage source0, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Null", "renderable");
    


    pb.setSource("source0", source0);
    
    return JAI.createRenderable("Null", pb, hints);
  }
}
