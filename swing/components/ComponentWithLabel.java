package swing.components;

import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;










public class ComponentWithLabel
  extends JPanel
{
  JLabel txt;
  private JComponent comp;
  public static final int X_AXIS = 0;
  public static final int Y_AXIS = 1;
  
  public ComponentWithLabel(String labelTxt, JComponent comp, int orient)
  {
    txt = new JLabel(labelTxt);
    this.comp = comp;
    if (orient == 1) {
      setLayout(new BoxLayout(this, 1));
    } else {
      setLayout(new FlowLayout(3));
    }
    add(txt);
    add(comp);
    txt.setAlignmentX(0.0F);
    comp.setAlignmentX(0.0F);
    setAlignmentX(0.0F);
  }
  

  public void setEnabled(boolean enabled)
  {
    comp.setEnabled(enabled);
    txt.setEnabled(enabled);
  }
}
