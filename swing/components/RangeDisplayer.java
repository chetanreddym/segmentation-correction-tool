package swing.components;

import java.awt.Component;
import java.io.File;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
















class RangeDisplayer
  extends JPanel
{
  JComboBox startCombo;
  JComboBox endCombo;
  JLabel labelBetweenCombos;
  JLabel labelBeforeCombos;
  
  RangeDisplayer(Vector startComboData, Vector endComboData)
  {
    if ((startCombo != null) && (endCombo != null)) {
      startCombo = new JComboBox(startComboData);
      endCombo = new JComboBox(endComboData);
    } else {
      startCombo = new JComboBox();
      endCombo = new JComboBox();
    }
    
    add(startCombo);
    add(endCombo);
  }
  


  public void setListCellRenderer(ListCellRenderer renderer)
  {
    startCombo.setRenderer(renderer);
    endCombo.setRenderer(renderer);
  }
  




  RangeDisplayer(Object[] startComboData, Object[] endComboData, String label, String prefixLabel)
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
      if (startCombo.getItemCount() != 0)
        startCombo.setSelectedIndex(0);
    }
    if (endComboData != null)
    {
      endCombo.removeAllItems();
      
      for (int i = 0; i < endComboData.length; i++) {
        endCombo.addItem(endComboData[i]);
      }
      if (endCombo.getItemCount() != 0)
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
  


  public static ListCellRenderer getFileNameDisplayerRenderer()
  {
    ListCellRenderer renderer = new ListCellRenderer()
    {
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value == null) return new JLabel("");
        String result = ((File)value).getName();
        return new JLabel(result);
      }
    };
    return renderer;
  }
}
