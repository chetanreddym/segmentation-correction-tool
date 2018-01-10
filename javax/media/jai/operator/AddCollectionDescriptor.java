package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import java.util.Collection;
import java.util.Iterator;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;





































































public class AddCollectionDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "AddCollection" }, { "LocalName", "AddCollection" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("AddCollectionDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AddCollectionDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") } };
  








  private static final Class[][] sourceClasses = { { Collection.class }, { Collection.class } };
  



  private static final String[] supportedModes = { "rendered", "renderable" };
  



  public AddCollectionDescriptor()
  {
    super(resources, supportedModes, null, sourceClasses, (ParameterListDescriptor)null);
  }
  



  protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateSources(modeName, args, msg)) {
      return false;
    }
    
    Collection col = (Collection)args.getSource(0);
    
    if (col.size() < 2) {
      msg.append(getName() + " " + JaiI18N.getString("AddCollectionDescriptor1"));
      
      return false;
    }
    
    Iterator iter = col.iterator();
    if (modeName.equalsIgnoreCase("rendered"))
      while (iter.hasNext()) {
        Object o = iter.next();
        if (!(o instanceof RenderedImage)) {
          msg.append(getName() + " " + JaiI18N.getString("AddCollectionDescriptor2"));
          
          return false;
        }
      }
    if (modeName.equalsIgnoreCase("renderable")) {
      while (iter.hasNext()) {
        Object o = iter.next();
        if (!(o instanceof RenderableImage)) {
          msg.append(getName() + " " + JaiI18N.getString("AddCollectionDescriptor3"));
          
          return false;
        }
      }
    }
    
    return true;
  }
  


















  public static RenderedOp create(Collection source0, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("AddCollection", "rendered");
    


    pb.setSource("source0", source0);
    
    return JAI.create("AddCollection", pb, hints);
  }
  

















  public static RenderableOp createRenderable(Collection source0, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("AddCollection", "renderable");
    


    pb.setSource("source0", source0);
    
    return JAI.createRenderable("AddCollection", pb, hints);
  }
}
