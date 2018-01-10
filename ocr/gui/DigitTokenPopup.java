package ocr.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import ocr.manager.GlobalHotkeyManager;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageDisplay.ZoneVector;












public class DigitTokenPopup
  extends JButton
{
  private static final long serialVersionUID = 1L;
  private JTextField digitTxt;
  private AbstractButton indicDigits;
  private boolean indicDigitsSelected;
  private final char LRO = '‭';
  private final char PDF = '‬';
  private final char LTR = '‎';
  private final char RTL = '‏';
  
  private final int Arabic_Indic_base = 1584;
  
  public DigitTokenPopup()
  {
    setFocusable(false);
    setPreferredSize(new Dimension(95, 24));
    setIcon(new ImageIcon(DigitTokenPopup.class.getResource("/images/digittoken.jpg")));
    setToolTipText("CTRL+Space");
    addActionListener(new DigitTokenListener(null));
    digitTokenWindowListener();
    indicDigitsSelected = OCRInterface.this_interface.getIndicDigitsON();
  }
  





  private class DigitTokenListener
    implements ActionListener
  {
    private DigitTokenListener() {}
    




    public void actionPerformed(ActionEvent arg0)
    {
      GlobalHotkeyManager.getInstance().setEnabled(false);
      DigitTokenPopup.this.showDigitTokenWindow();
      GlobalHotkeyManager.getInstance().setEnabled(true);
    }
  }
  
  public void digitTokenWindowListener() {
    GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
    hotkeyManager.setEnabled(true);
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(32, 
      2), "OPEN_DIGIT_TOKEN_WINDOW");
    hotkeyManager.getActionMap().put("OPEN_DIGIT_TOKEN_WINDOW", new AbstractAction() {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) { GlobalHotkeyManager.getInstance().setEnabled(false);
        DigitTokenPopup.this.showDigitTokenWindow();
        GlobalHotkeyManager.getInstance().setEnabled(true);
      }
    });
  }
  
  private void showDigitTokenWindow() {
    if (!OCRInterface.this_interface.getEnableDigitTokenPopup()) {
      return;
    }
    if (ImageDisplay.getActiveZones().isEmpty()) {
      JOptionPane.showMessageDialog(
        OCRInterface.this_interface, 
        "Select a zone.", 
        "Error", 
        2);
      return;
    }
    final JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout(20, 10));
    panel.add(new JLabel("Enter digit token:"), "North");
    digitTxt = new JTextField();
    digitTxt.setText(Character.toString('‭'));
    




    digitTxt.addAncestorListener(new AncestorListener()
    {
      public void ancestorAdded(AncestorEvent ae) { ae.getComponent().requestFocus(); }
      
      public void ancestorRemoved(AncestorEvent ae) {}
      
      public void ancestorMoved(AncestorEvent ae) {}
    });
    digitTxt.addKeyListener(new KeyListener()
    {
      public void keyPressed(KeyEvent e) {}
      




      public void keyReleased(KeyEvent e) {}
      



      public void keyTyped(KeyEvent e)
      {
        Character c = Character.valueOf(e.getKeyChar());
        if ((indicDigits.isSelected()) && 
          (Character.isDigit(c.charValue())) && 
          (c.charValue() >= '0') && (c.charValue() <= '9')) {
          e.setKeyChar((char)('ذ' + c.charValue()));
        }
        

        if (Character.isLetter(e.getKeyChar())) {
          JOptionPane.showMessageDialog(
            panel, 
            "Not a digit!", 
            "Warning", 
            0);
          e.consume();
        }
        
      }
      
    });
    panel.add(digitTxt, "Center");
    
    indicDigits = new JCheckBox("  Indic Digits (CTRL+Q to change)");
    indicDigits.setFocusable(false);
    indicDigits.setSelected(indicDigitsSelected);
    indicDigits.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent arg0) {
        if (arg0.getStateChange() == 1) {
          indicDigitsSelected = true;
        } else {
          indicDigitsSelected = false;
        }
        OCRInterface.this_interface.setIndicDigitsON(indicDigitsSelected);
      }
      

    });
    panel.add(indicDigits, "South");
    
    digitTxt.getInputMap().put(KeyStroke.getKeyStroke(81, 
      2), "INDIC_DIGITS");
    digitTxt.getActionMap().put("INDIC_DIGITS", new AbstractAction() {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) { if (indicDigits.isSelected()) {
          indicDigits.setSelected(false);
        } else {
          indicDigits.setSelected(true);
        }
        indicDigitsSelected = indicDigits.isSelected();

      }
      

    });
    int ret = JOptionPane.showConfirmDialog(
      null, 
      panel, 
      "Digit Token", 
      2, 
      -1);
    
    if ((ret == 2) || (ret == -1)) {
      GlobalHotkeyManager.getInstance().setEnabled(true);
      return;
    }
    
    insertDigitString();
    GlobalHotkeyManager.getInstance().setEnabled(true);
  }
  
  private String getDigitString()
  {
    String str = digitTxt.getText().trim();
    
    char[] chars = str.toCharArray();
    
    if ((chars[0] == '‭') && (chars.length == 1))
      return "";
    if ((chars[0] == '‭') && (Character.isWhitespace(chars[1]))) {
      str = str.substring(2, str.length());
    } else if (chars[0] == '‭') {
      str = str.substring(1, str.length());
    }
    
    str = '‭' + str + '‬';
    return '‎' + str + '‏';
  }
  

  private void insertDigitString()
  {
    Zone zone = (Zone)ImageDisplay.getActiveZones().firstElement();
    String contents = zone.getContents();
    
    if ((contents == null) || (contents.isEmpty())) {
      contents = getDigitString();
    } else {
      contents = 
      
        contents.substring(0, contents.length() - caret) + getDigitString() + contents.substring(contents.length() - caret, contents.length());
    }
    zone.setContents(contents);
  }
  



  public void setHotkey()
  {
    digitTokenWindowListener();
  }
}
