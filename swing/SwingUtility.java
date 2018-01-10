package swing;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;



public class SwingUtility
{
  public static final String BOXLAYOUT = "BoxLayout";
  
  public SwingUtility() {}
  
  public static void setBoxLayout(JComponent cp, int orientation)
  {
    if (orientation == 0) {
      cp.setLayout(new BoxLayout(cp, 0));
    } else if (orientation == 1) {
      cp.setLayout(new BoxLayout(cp, 1));
    }
  }
  


  public static void setFlowLayout(JComponent cp, boolean leading)
  {
    if (leading) {
      cp.setLayout(new FlowLayout(3));
    } else {
      cp.setLayout(new FlowLayout());
    }
  }
  



  public static void setBorderTitle(JComponent panel, String title, boolean makeTitleBold)
  {
    if (!makeTitleBold) {
      panel.setBorder(BorderFactory.createTitledBorder(title));
    } else {
      Border b = BorderFactory.createLineBorder(Color.black);
      Font f = new Font(panel.getFont().getFontName(), 1, panel.getFont().getSize());
      panel.setBorder(new TitledBorder(BorderFactory.createTitledBorder(b, title, 0, 0, f)));
    }
  }
  




  public static void setComponentFontSize(JComponent component, int newFontSize)
  {
    Font f = new Font(component.getFont().getFontName(), component.getFont().getStyle(), newFontSize);
    component.setFont(f);
  }
  
  public static void setComponentFontStyle(JComponent component, int fontStyle) {
    Font f = new Font(component.getFont().getFontName(), fontStyle, component.getFont().getSize());
    component.setFont(f);
  }
  
  public static int getScreenWidth()
  {
    return (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
  }
  
  public static int getScreenHeight()
  {
    return (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
  }
  
  public static void setSystemDependentLookAndFeel()
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
  }
  






  public static void showPanelInDialog(JFrame frm, JPanel pnl, String sTitle, JButton btn)
  {
    JDialog dlg = new JDialog(frm, sTitle, true);
    dlg.getContentPane().add(pnl);
    dlg.getRootPane().setDefaultButton(btn);
    dlg.pack();
    
    dlg.setDefaultCloseOperation(2);
    dlg.setLocationRelativeTo(JOptionPane.getRootFrame());
    dlg.setSize(pnl.getSize());
    dlg.setLocationRelativeTo(null);
    
    dlg.invalidate();dlg.validate();dlg.repaint();
    dlg.setVisible(true);
  }
  


  public static void hideDialog(JPanel pnl)
  {
    Container c = pnl.getParent();
    
    while (c != null) {
      if ((c instanceof JDialog)) break;
      c = c.getParent();
    }
    JDialog dg = (JDialog)c;
    
    if (dg != null) {
      dg.dispose();
    }
  }
}
