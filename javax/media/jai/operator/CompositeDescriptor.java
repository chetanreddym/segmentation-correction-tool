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









































































































































public class CompositeDescriptor
  extends OperationDescriptorImpl
{
  public static final CompositeDestAlpha NO_DESTINATION_ALPHA = new CompositeDestAlpha("NO_DESTINATION_ALPHA", 0);
  


  public static final CompositeDestAlpha DESTINATION_ALPHA_FIRST = new CompositeDestAlpha("DESTINATION_ALPHA_FIRST", 1);
  


  public static final CompositeDestAlpha DESTINATION_ALPHA_LAST = new CompositeDestAlpha("DESTINATION_ALPHA_LAST", 2);
  





  protected static final String[][] resources = { { "GlobalName", "Composite" }, { "LocalName", "Composite" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("CompositeDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/CompositeDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion2") }, { "arg0Desc", JaiI18N.getString("CompositeDescriptor1") }, { "arg1Desc", JaiI18N.getString("CompositeDescriptor2") }, { "arg2Desc", JaiI18N.getString("CompositeDescriptor3") }, { "arg3Desc", JaiI18N.getString("CompositeDescriptor4") } };
  











  private static final Class[][] sourceClasses = { { RenderedImage.class, RenderedImage.class }, { RenderableImage.class, RenderableImage.class } };
  










  private static final Class[][] paramClasses = { { RenderedImage.class, RenderedImage.class, Boolean.class, CompositeDestAlpha.class }, { RenderableImage.class, RenderableImage.class, Boolean.class, CompositeDestAlpha.class } };
  














  private static final String[] paramNames = { "source1Alpha", "source2Alpha", "alphaPremultiplied", "destAlpha" };
  




  private static final Object[][] paramDefaults = { { NO_PARAMETER_DEFAULT, null, Boolean.FALSE, NO_DESTINATION_ALPHA }, { NO_PARAMETER_DEFAULT, null, Boolean.FALSE, NO_DESTINATION_ALPHA } };
  









  private static final String[] supportedModes = { "rendered", "renderable" };
  



  public CompositeDescriptor()
  {
    super(resources, supportedModes, null, sourceClasses, paramNames, paramClasses, paramDefaults, (Object[][])null);
  }
  

















  public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateArguments(modeName, args, msg)) {
      return false;
    }
    
    if (!modeName.equalsIgnoreCase("rendered")) {
      return true;
    }
    RenderedImage src1 = args.getRenderedSource(0);
    RenderedImage src2 = args.getRenderedSource(1);
    
    SampleModel s1sm = src1.getSampleModel();
    SampleModel s2sm = src2.getSampleModel();
    if ((s1sm.getNumBands() != s2sm.getNumBands()) || (s1sm.getTransferType() != s2sm.getTransferType()))
    {
      msg.append(getName() + " " + JaiI18N.getString("CompositeDescriptor8"));
      
      return false;
    }
    

    RenderedImage afa1 = (RenderedImage)args.getObjectParameter(0);
    if ((src1.getMinX() != afa1.getMinX()) || (src1.getMinY() != afa1.getMinY()) || (src1.getWidth() != afa1.getWidth()) || (src1.getHeight() != afa1.getHeight()))
    {


      msg.append(getName() + " " + JaiI18N.getString("CompositeDescriptor12"));
      
      return false;
    }
    
    SampleModel a1sm = afa1.getSampleModel();
    if (s1sm.getTransferType() != a1sm.getTransferType()) {
      msg.append(getName() + " " + JaiI18N.getString("CompositeDescriptor13"));
      
      return false;
    }
    
    RenderedImage afa2 = (RenderedImage)args.getObjectParameter(1);
    if (afa2 != null) {
      if ((src2.getMinX() != afa2.getMinX()) || (src2.getMinY() != afa2.getMinY()) || (src2.getWidth() != afa2.getWidth()) || (src2.getHeight() != afa2.getHeight()))
      {


        msg.append(getName() + " " + JaiI18N.getString("CompositeDescriptor15"));
        
        return false;
      }
      
      SampleModel a2sm = afa2.getSampleModel();
      if (s2sm.getTransferType() != a2sm.getTransferType()) {
        msg.append(getName() + " " + JaiI18N.getString("CompositeDescriptor16"));
        
        return false;
      }
    }
    
    return true;
  }
  

































  public static RenderedOp create(RenderedImage source0, RenderedImage source1, RenderedImage source1Alpha, RenderedImage source2Alpha, Boolean alphaPremultiplied, CompositeDestAlpha destAlpha, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Composite", "rendered");
    


    pb.setSource("source0", source0);
    pb.setSource("source1", source1);
    
    pb.setParameter("source1Alpha", source1Alpha);
    pb.setParameter("source2Alpha", source2Alpha);
    pb.setParameter("alphaPremultiplied", alphaPremultiplied);
    pb.setParameter("destAlpha", destAlpha);
    
    return JAI.create("Composite", pb, hints);
  }
  
































  public static RenderableOp createRenderable(RenderableImage source0, RenderableImage source1, RenderableImage source1Alpha, RenderableImage source2Alpha, Boolean alphaPremultiplied, CompositeDestAlpha destAlpha, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Composite", "renderable");
    


    pb.setSource("source0", source0);
    pb.setSource("source1", source1);
    
    pb.setParameter("source1Alpha", source1Alpha);
    pb.setParameter("source2Alpha", source2Alpha);
    pb.setParameter("alphaPremultiplied", alphaPremultiplied);
    pb.setParameter("destAlpha", destAlpha);
    
    return JAI.createRenderable("Composite", pb, hints);
  }
}
