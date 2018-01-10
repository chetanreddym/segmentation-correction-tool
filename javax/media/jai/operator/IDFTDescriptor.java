package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;



























































































































public class IDFTDescriptor
  extends OperationDescriptorImpl
{
  public static final DFTScalingType SCALING_NONE = DFTDescriptor.SCALING_NONE;
  





  public static final DFTScalingType SCALING_UNITARY = DFTDescriptor.SCALING_UNITARY;
  





  public static final DFTScalingType SCALING_DIMENSIONS = DFTDescriptor.SCALING_DIMENSIONS;
  





  public static final DFTDataNature REAL_TO_COMPLEX = DFTDescriptor.REAL_TO_COMPLEX;
  




  public static final DFTDataNature COMPLEX_TO_COMPLEX = DFTDescriptor.COMPLEX_TO_COMPLEX;
  





  public static final DFTDataNature COMPLEX_TO_REAL = DFTDescriptor.COMPLEX_TO_REAL;
  





  private static final String[][] resources = { { "GlobalName", "IDFT" }, { "LocalName", "IDFT" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("IDFTDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/IDFTDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion2") }, { "arg0Desc", JaiI18N.getString("IDFTDescriptor1") }, { "arg1Desc", JaiI18N.getString("IDFTDescriptor2") } };
  










  private static final Class[] paramClasses = { DFTScalingType.class, DFTDataNature.class };
  



  private static final String[] paramNames = { "scalingType", "dataNature" };
  



  private static final Object[] paramDefaults = { SCALING_DIMENSIONS, COMPLEX_TO_REAL };
  


  private static final String[] supportedModes = { "rendered", "renderable" };
  



  public IDFTDescriptor()
  {
    super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
  }
  















  public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateArguments(modeName, args, msg)) {
      return false;
    }
    
    if (!modeName.equalsIgnoreCase("rendered")) {
      return true;
    }
    
    EnumeratedParameter dataNature = (EnumeratedParameter)args.getObjectParameter(1);
    

    if (!dataNature.equals(REAL_TO_COMPLEX)) {
      RenderedImage src = args.getRenderedSource(0);
      
      if (src.getSampleModel().getNumBands() % 2 != 0) {
        msg.append(getName() + " " + JaiI18N.getString("IDFTDescriptor5"));
        
        return false;
      }
    }
    
    return true;
  }
  





  public PropertyGenerator[] getPropertyGenerators(String modeName)
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new IDFTPropertyGenerator();
    return pg;
  }
  
























  public static RenderedOp create(RenderedImage source0, DFTScalingType scalingType, DFTDataNature dataNature, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("IDFT", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("scalingType", scalingType);
    pb.setParameter("dataNature", dataNature);
    
    return JAI.create("IDFT", pb, hints);
  }
  























  public static RenderableOp createRenderable(RenderableImage source0, DFTScalingType scalingType, DFTDataNature dataNature, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("IDFT", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("scalingType", scalingType);
    pb.setParameter("dataNature", dataNature);
    
    return JAI.createRenderable("IDFT", pb, hints);
  }
}
