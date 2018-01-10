package gttool.misc;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

































public class WaitDialog
  extends JFrame
{
  private JLabel jLabel1 = new JLabel();
  private JLabel jLabel2 = new JLabel();
  

  Rectangle bounds;
  

  private static WaitDialog dlg = new WaitDialog();
  

  private WaitDialog()
  {
    try
    {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  




  private void jbInit()
    throws Exception
  {
    getContentPane().setLayout(null);
    setSize(new Dimension(400, 150));
    setResizable(false);
    
    setTitle("Please wait");
    bounds = getGraphicsConfiguration().getBounds();
    
    URL imgURL = null;
    if (imgURL != null) {
      ImageIcon img = new ImageIcon(imgURL);
      jLabel1.setIcon(img);
    }
    jLabel1.setBounds(new Rectangle(15, 20, 65, 85));
    jLabel2.setText("Please wait while the system is processing...");
    jLabel2.setForeground(SystemColor.textText);
    jLabel2.setBounds(new Rectangle(90, 30, 300, 65));
    jLabel2.setFont(new Font("Dialog", 1, 13));
    getContentPane().add(jLabel2, null);
    
    getContentPane().add(jLabel1, null);
  }
  



  public static void showDialog()
  {
    dlg.setCursor(Cursor.getPredefinedCursor(3));
    dlg.setLocation((int)dlgbounds.getWidth() / 2 - 200, (int)dlgbounds.getHeight() / 2 - 75);
    
    dlg.setVisible(true);
    dlg.paint(dlg.getGraphics());
    dlg.setFocusable(true);
    
    dlg.toFront();
  }
  




  public static void hideDialog()
  {
    dlg.setVisible(false);
    dlg.hide();
  }
}
