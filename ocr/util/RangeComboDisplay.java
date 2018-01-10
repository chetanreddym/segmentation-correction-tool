package ocr.util;

import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

















class RangeComboDisplay
  extends JPanel
{
  JComboBox startCombo;
  JComboBox endCombo;
  JLabel labelBetweenCombos;
  JLabel labelBeforeCombos;
  
  RangeComboDisplay(Vector startComboData, Vector endComboData)
  {
    startCombo = new JComboBox(startComboData);
    endCombo = new JComboBox(endComboData);
    
    add(startCombo);
    add(endCombo);
  }
  




  RangeComboDisplay(Object[] startComboData, Object[] endComboData, String label, String prefixLabel)
  {
    startCombo = new JComboBox(startComboData);
    endCombo = new JComboBox(endComboData);
    labelBetweenCombos = new JLabel(label);
    labelBeforeCombos = new JLabel(prefixLabel);
    
    add(labelBeforeCombos);
    add(startCombo);
    add(labelBetweenCombos);
    add(endCombo);
  }
  






  public void updateData(Object[] startComboData, Object[] endComboData)
  {
    if (startComboData != null)
    {
      startCombo.removeAllItems();
      
      for (int i = 0; i < startComboData.length; i++) {
        startCombo.addItem(startComboData[i]);
      }
      startCombo.setSelectedIndex(0);
    }
    if (endComboData != null)
    {
      endCombo.removeAllItems();
      
      for (int i = 0; i < endComboData.length; i++) {
        endCombo.addItem(endComboData[i]);
      }
      endCombo.setSelectedIndex(0);
    }
    repaint();
  }
  



  public void setVisible(boolean aFlag)
  {
    super.setVisible(aFlag);
  }
  












  public Object[] getRangeSelected()
  {
    Object[] range = new Object[2];
    range[0] = startCombo.getSelectedItem();
    range[1] = endCombo.getSelectedItem();
    
    return range;
  }
}
