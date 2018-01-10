package ocr.gui.leftPanel;

import java.awt.event.ActionEvent;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import ocr.gui.OCRInterface;

public class EncodingPanel extends javax.swing.JPanel implements java.awt.event.ActionListener
{
  private static final long serialVersionUID = 1L;
  public static JRadioButton utf16Button = new JRadioButton("Unicode/UTF-16");
  public static JRadioButton utf8Button = new JRadioButton("UTF-8");
  private static ButtonGroup group = new ButtonGroup();
  
  public boolean utf8Selected = false;
  
  public static int UTF8 = 1; public static int UTF16 = 2;
  

  OCRInterface ocrIf;
  


  public EncodingPanel()
  {
    setAlignmentX(0.0F);
    setLayout(new javax.swing.BoxLayout(this, 0));
    
    ocrIf = OCRInterface.this_interface;
    
    utf16Button.setActionCommand("UTF-16");
    utf8Button.setActionCommand("UTF-8");
    utf8Button.setSelected(true);
    
    group.add(utf16Button);
    group.add(utf8Button);
    








    utf16Button.addActionListener(this);
    utf8Button.addActionListener(this);
    
    add(utf16Button);
    add(utf8Button);
  }
  


  public void actionPerformed(ActionEvent e)
  {
    ocrIf.loadETextWindow(ocrIf.getFilePath(), e.getActionCommand());
    
    if (e.getActionCommand().equals("UTF-8")) {
      utf8Selected = true;
    }
    else if (e.getActionCommand().equals("UTF-16")) {
      utf8Selected = false;
    }
    getAttsConfigUtilapply_button.setEnabled(true);
  }
  
  public int whichSelected() {
    if (utf8Button.isSelected())
      return UTF8;
    if (utf16Button.isSelected()) {
      return UTF16;
    }
    return 0;
  }
}
