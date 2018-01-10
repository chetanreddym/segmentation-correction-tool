package javax.media.jai;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;















class PropertyGeneratorFromSource
  extends PropertyGeneratorImpl
{
  int sourceIndex;
  String propertyName;
  
  PropertyGeneratorFromSource(int sourceIndex, String propertyName)
  {
    super(new String[] { propertyName }, new Class[] { Object.class }, new Class[] { OperationNode.class });
    


    if (propertyName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    this.sourceIndex = sourceIndex;
    this.propertyName = propertyName;
  }
  
  public Object getProperty(String name, Object opNode)
  {
    if ((name == null) || (opNode == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if ((sourceIndex >= 0) && ((opNode instanceof OperationNode)) && (propertyName.equalsIgnoreCase(name)))
    {

      OperationNode op = (OperationNode)opNode;
      Vector sources = op.getParameterBlock().getSources();
      if ((sources != null) && (sourceIndex < sources.size())) {
        Object src = sources.elementAt(sourceIndex);
        if ((src instanceof PropertySource)) {
          return ((PropertySource)src).getProperty(name);
        }
      }
    }
    
    return Image.UndefinedProperty;
  }
}
