package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.ROI;
import javax.media.jai.RenderedOp;
import javax.media.jai.util.Range;



























































































































































public class ColorQuantizerDescriptor
  extends OperationDescriptorImpl
{
  public static final ColorQuantizerType MEDIANCUT = new ColorQuantizerType("MEDIANCUT", 1);
  

  public static final ColorQuantizerType NEUQUANT = new ColorQuantizerType("NEUQUANT", 2);
  

  public static final ColorQuantizerType OCTTREE = new ColorQuantizerType("OCTTREE", 3);
  





  private static final String[][] resources = { { "GlobalName", "ColorQuantizer" }, { "LocalName", "ColorQuantizer" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("ColorQuantizerDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ColorQuantizerDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion2") }, { "arg0Desc", JaiI18N.getString("ColorQuantizerDescriptor1") }, { "arg1Desc", JaiI18N.getString("ColorQuantizerDescriptor2") }, { "arg2Desc", JaiI18N.getString("ColorQuantizerDescriptor3") }, { "arg3Desc", JaiI18N.getString("ColorQuantizerDescriptor4") }, { "arg4Desc", JaiI18N.getString("ColorQuantizerDescriptor5") }, { "arg5Desc", JaiI18N.getString("ColorQuantizerDescriptor6") } };
  














  private static final String[] paramNames = { "quantizationAlgorithm", "maxColorNum", "upperBound", "roi", "xPeriod", "yPeriod" };
  








  private static final Class[] paramClasses = { ColorQuantizerType.class, Integer.class, Integer.class, ROI.class, Integer.class, Integer.class };
  








  private static final Object[] paramDefaults = { MEDIANCUT, new Integer(256), null, null, new Integer(1), new Integer(1) };
  







  private static final String[] supportedModes = { "rendered" };
  


  public ColorQuantizerDescriptor()
  {
    super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
  }
  





  public Range getParamValueRange(int index)
  {
    switch (index) {
    case 1: 
    case 2: 
    case 4: 
    case 5: 
      return new Range(Integer.class, new Integer(1), null);
    }
    return null;
  }
  













  protected boolean validateParameters(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if ((args == null) || (msg == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (!super.validateParameters(modeName, args, msg)) {
      return false;
    }
    ColorQuantizerType algorithm = (ColorQuantizerType)args.getObjectParameter(0);
    
    if ((algorithm != MEDIANCUT) && (algorithm != NEUQUANT) && (algorithm != OCTTREE))
    {
      msg.append(getName() + " " + JaiI18N.getString("ColorQuantizerDescriptor7"));
      
      return false;
    }
    
    Integer secondOne = (Integer)args.getObjectParameter(2);
    if (secondOne == null) {
      int upperBound = 0;
      if (algorithm.equals(MEDIANCUT)) {
        upperBound = 32768;
      } else if (algorithm.equals(NEUQUANT)) {
        upperBound = 100;
      } else if (algorithm.equals(OCTTREE)) {
        upperBound = 65536;
      }
      args.set(upperBound, 2);
    }
    
    return true;
  }
  






























  public static RenderedOp create(RenderedImage source0, ColorQuantizerType algorithm, Integer maxColorNum, Integer upperBound, ROI roi, Integer xPeriod, Integer yPeriod, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("ColorQuantizer", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("quantizationAlgorithm", algorithm);
    pb.setParameter("maxColorNum", maxColorNum);
    pb.setParameter("upperBound", upperBound);
    pb.setParameter("roi", roi);
    pb.setParameter("xPeriod", xPeriod);
    pb.setParameter("yPeriod", yPeriod);
    
    return JAI.create("ColorQuantizer", pb, hints);
  }
}
