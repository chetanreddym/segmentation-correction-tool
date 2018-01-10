package ocr.gui.leftPanel;

import javax.swing.table.DefaultTableModel;













public class CustomTableModel
  extends DefaultTableModel
{
  public CustomTableModel() {}
  
  public Class getColumnClass(int column)
  {
    return getValueAt(0, column).getClass();
  }
  

  public boolean isCellEditable(int row, int column)
  {
    return false;
  }
}
