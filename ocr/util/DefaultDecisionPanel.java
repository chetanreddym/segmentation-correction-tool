package ocr.util;

import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;













public class DefaultDecisionPanel
  extends JPanel
{
  JPanel buttonsPanel;
  JPanel topPanel;
  JButton firstButton = new JButton("Ok");
  JButton secondButton = new JButton("Cancel");
  






  public DefaultDecisionPanel(String labelOkButton, String labelCancelButton, JPanel topPanel)
  {
    setLayout(new BoxLayout(this, 1));
    
    this.topPanel = topPanel;
    
    if (labelOkButton != null) {
      firstButton.setText(labelOkButton);
    }
    if (labelCancelButton != null) {
      secondButton.setText(labelCancelButton);
    }
    buttonsPanel = new JPanel();
    buttonsPanel.add(firstButton);
    buttonsPanel.add(secondButton);
    
    add(topPanel);
    add(buttonsPanel);
  }
  






  public void addActionListenerToOkButton(ActionListener actionAdapter)
  {
    firstButton.addActionListener(actionAdapter);
  }
  




  public void addActionListenerToCancelButton(ActionListener actionListener)
  {
    secondButton.addActionListener(actionListener);
  }
  





  public JButton getOkButton()
  {
    return firstButton;
  }
  






  public JButton getCancelButton()
  {
    return secondButton;
  }
}
