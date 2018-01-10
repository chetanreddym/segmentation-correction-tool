package signalprocesser.shared;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class JCollapsiblePanel extends JPanel implements java.awt.event.MouseListener
{
  private static final int DIST_FROM_LEFT = 30;
  private static final int DIST_FROM_TOP = 2;
  private static final int RECT_WIDTH = 14;
  private static final int RECT_HEIGHT = 10;
  private static final int TRIANGLE_MARGINLEFT = 3;
  private static final int TRIANGLE_MARGINTOP = 2;
  private boolean iscollapsed = false;
  private JPanel innercomponent = null;
  
  public JCollapsiblePanel()
  {
    super.addMouseListener(this);
    

    if (innercomponent == null) {
      innercomponent = new JPanel();
    }
    super.setLayout(new java.awt.BorderLayout());
    super.add(innercomponent, "Center");
  }
  
  public JCollapsiblePanel(LayoutManager layout)
  {
    super.addMouseListener(this);
    

    if (innercomponent == null) {
      innercomponent = new JPanel(layout);
    } else {
      innercomponent.setLayout(layout);
    }
    super.setLayout(new java.awt.BorderLayout());
    super.add(innercomponent, "Center");
  }
  
  public void setCollapsed(boolean b) {
    iscollapsed = b;
    super.removeAll();
    revalidate();
  }
  
  public void paint(Graphics g)
  {
    super.paint(g);
    

    int width = getWidth();
    

    g.setColor(super.getBackground());
    g.fill3DRect(width - 30, 2, 14, 10, true);
    

    int[] xcoord = new int[3];
    int[] ycoord = new int[3];
    xcoord[0] = (width - 30 + 3 - 1);
    xcoord[1] = (width - 30 + 14 - 3 - 1);
    xcoord[2] = (width - 30 + 7 - 1);
    if (!iscollapsed) {
      ycoord[0] = 4;
      ycoord[1] = ycoord[0];
      ycoord[2] = 10;
    } else {
      ycoord[0] = 9;
      ycoord[1] = ycoord[0];
      ycoord[2] = 3;
    }
    g.setColor(java.awt.Color.blue);
    g.fillPolygon(xcoord, ycoord, 3);
  }
  
  public void mouseClicked(MouseEvent e)
  {
    if ((getWidth() - 30 <= e.getX()) && (e.getY() <= 12) && 
      (e.getX() <= getWidth() - 30 + 14) && (2 <= e.getY())) {
      if (iscollapsed) {
        iscollapsed = false;
        super.add(innercomponent, "Center");
      } else {
        iscollapsed = true;
        super.removeAll();
      }
      revalidate();
    }
  }
  
  public void mouseEntered(MouseEvent e) {}
  
  public void mouseExited(MouseEvent e) {}
  
  public void mousePressed(MouseEvent e) {}
  
  public void mouseReleased(MouseEvent e) {}
  
  public void setLayout(LayoutManager layout) { if (innercomponent == null) {
      innercomponent = new JPanel();
    }
    






    innercomponent.setLayout(layout);
  }
  
  public Component add(Component comp) {
    return innercomponent.add(comp);
  }
  
  public Component add(Component comp, int index) { return innercomponent.add(comp, index); }
  
  public void add(Component comp, Object constraints) {
    innercomponent.add(comp, constraints);
  }
  
  public void add(Component comp, Object constraints, int index) { innercomponent.add(comp, constraints, index); }
  
  public Component add(String name, Component comp) {
    return innercomponent.add(name, comp);
  }
}
