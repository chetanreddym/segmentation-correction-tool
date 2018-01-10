package javax.media.jai.operator;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.BorderExtender;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.OperationNode;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.RenderedOp;







































































public class BorderDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Border" }, { "LocalName", "Border" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("BorderDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/BorderDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion2") }, { "arg0Desc", JaiI18N.getString("BorderDescriptor1") }, { "arg1Desc", JaiI18N.getString("BorderDescriptor2") }, { "arg2Desc", JaiI18N.getString("BorderDescriptor3") }, { "arg3Desc", JaiI18N.getString("BorderDescriptor4") }, { "arg4Desc", JaiI18N.getString("BorderDescriptor5") } };
  













  private static final String[] paramNames = { "leftPad", "rightPad", "topPad", "bottomPad", "type" };
  



  private static final Class[] paramClasses = { Integer.class, Integer.class, Integer.class, Integer.class, BorderExtender.class };
  





  private static final Object[] paramDefaults = { new Integer(0), new Integer(0), new Integer(0), new Integer(0), BorderExtender.createInstance(0) };
  



  public BorderDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  


































  public Object getInvalidRegion(String modeName, ParameterBlock oldParamBlock, RenderingHints oldHints, ParameterBlock newParamBlock, RenderingHints newHints, OperationNode node)
  {
    if ((modeName == null) || (((getNumSources() > 0) || (getNumParameters() > 0)) && ((oldParamBlock == null) || (newParamBlock == null))))
    {


      throw new IllegalArgumentException(JaiI18N.getString("BorderDescriptor6"));
    }
    
    int numSources = getNumSources();
    
    if ((numSources > 0) && ((oldParamBlock.getNumSources() != numSources) || (newParamBlock.getNumSources() != numSources)))
    {


      throw new IllegalArgumentException(JaiI18N.getString("BorderDescriptor7"));
    }
    

    int numParams = getParameterListDescriptor(modeName).getNumParameters();
    
    if ((numParams > 0) && ((oldParamBlock.getNumParameters() != numParams) || (newParamBlock.getNumParameters() != numParams)))
    {


      throw new IllegalArgumentException(JaiI18N.getString("BorderDescriptor8"));
    }
    


    if ((!modeName.equalsIgnoreCase("rendered")) || ((oldHints == null) && (newHints != null)) || ((oldHints != null) && (newHints == null)) || ((oldHints != null) && (!oldHints.equals(newHints))) || (!oldParamBlock.getSource(0).equals(newParamBlock.getSource(0))) || (oldParamBlock.getIntParameter(0) != newParamBlock.getIntParameter(0)) || (oldParamBlock.getIntParameter(2) != newParamBlock.getIntParameter(2)))
    {







      return null;
    }
    
    Shape invalidRegion = null;
    
    if (!oldParamBlock.getObjectParameter(4).equals(newParamBlock.getObjectParameter(4)))
    {



      RenderedImage src = oldParamBlock.getRenderedSource(0);
      int leftPad = oldParamBlock.getIntParameter(0);
      int topPad = oldParamBlock.getIntParameter(2);
      

      Rectangle srcBounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
      



      Rectangle dstBounds = new Rectangle(x - leftPad, y - topPad, width + leftPad + oldParamBlock.getIntParameter(1), height + topPad + oldParamBlock.getIntParameter(3));
      







      Area invalidArea = new Area(dstBounds);
      invalidArea.subtract(new Area(srcBounds));
      invalidRegion = invalidArea;
    }
    else if (((newParamBlock.getIntParameter(1) < oldParamBlock.getIntParameter(1)) && (newParamBlock.getIntParameter(3) <= oldParamBlock.getIntParameter(3))) || ((newParamBlock.getIntParameter(3) < oldParamBlock.getIntParameter(3)) && (newParamBlock.getIntParameter(1) <= oldParamBlock.getIntParameter(1))))
    {









      RenderedImage src = oldParamBlock.getRenderedSource(0);
      int leftPad = oldParamBlock.getIntParameter(0);
      int topPad = oldParamBlock.getIntParameter(2);
      

      Rectangle srcBounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
      



      Rectangle oldBounds = new Rectangle(x - leftPad, y - topPad, width + leftPad + oldParamBlock.getIntParameter(1), height + topPad + oldParamBlock.getIntParameter(3));
      







      Rectangle newBounds = new Rectangle(x - leftPad, y - topPad, width + leftPad + newParamBlock.getIntParameter(1), height + topPad + newParamBlock.getIntParameter(3));
      







      Area invalidArea = new Area(oldBounds);
      invalidArea.subtract(new Area(newBounds));
      invalidRegion = invalidArea;

    }
    else
    {
      invalidRegion = new Rectangle();
    }
    
    return invalidRegion;
  }
  

































  public static RenderedOp create(RenderedImage source0, Integer leftPad, Integer rightPad, Integer topPad, Integer bottomPad, BorderExtender type, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Border", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("leftPad", leftPad);
    pb.setParameter("rightPad", rightPad);
    pb.setParameter("topPad", topPad);
    pb.setParameter("bottomPad", bottomPad);
    pb.setParameter("type", type);
    
    return JAI.create("Border", pb, hints);
  }
}
