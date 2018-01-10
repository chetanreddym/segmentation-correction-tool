package ocr.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ButtonModel;
import javax.swing.JToggleButton;











public class DClickToggleButton
  extends JToggleButton
  implements MouseListener
{
  boolean mySelected = false;
  

  static final long serialVersionUID = 43729823L;
  

  public DClickToggleButton(String text)
  {
    super(text);
    super.addMouseListener(this);
  }
  





  public void setSelected(boolean selected)
  {
    super.setSelected(selected);
    mySelected = selected;
  }
  



  public void mouseClicked(MouseEvent e)
  {
    int numClicks = e.getClickCount();
    if (numClicks > 1)
    {
      mySelected = (!mySelected);
      getModel().setSelected(mySelected);
    }
  }
  
  public void mousePressed(MouseEvent e)
  {
    getModel().setSelected(mySelected);
  }
  
  public void mouseReleased(MouseEvent e) {
    getModel().setSelected(mySelected);
  }
  
  public void mouseEntered(MouseEvent e) {
    getModel().setSelected(mySelected);
  }
  
  public void mouseExited(MouseEvent e) {
    getModel().setSelected(mySelected);
  }
}
