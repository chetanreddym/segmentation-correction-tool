package ocr.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import ocr.manager.GlobalHotkeyManager;








public class TableUtils
{
  private static TableUtils instance = new TableUtils();
  
  private TableUtils() {}
  
  public static TableUtils getInstance()
  {
    return instance;
  }
  
  public RowEditorModel getNewRowEditorModel() {
    return new RowEditorModel();
  }
  




  public static void setUpIntegerEditor(JTable table)
  {
    final WholeNumberField integerField = new WholeNumberField(0, 5);
    integerField.setHorizontalAlignment(4);
    
    DefaultCellEditor integerEditor = new DefaultCellEditor(integerField)
    {
      private static final long serialVersionUID = 6037366595228920705L;
      



      public Object getCellEditorValue()
      {
        return new Integer(integerField.getValue());
      }
    };
    table.setDefaultEditor(Integer.class, integerEditor);
  }
  

  public static void setUpColorEditor(JTable table)
  {
    JButton button = new JButton("")
    {
      private static final long serialVersionUID = 3264540584436765332L;
      




      public void setText(String s) {}
    };
    button.setBackground(Color.white);
    button.setBorderPainted(false);
    button.setMargin(new Insets(0, 0, 0, 0));
    


    final ColorEditor colorEditor = new ColorEditor(button);
    table.setDefaultEditor(Color.class, colorEditor);
    

    final JColorChooser colorChooser = new JColorChooser();
    ActionListener okListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        currentColor = colorChooser.getColor();
      }
    };
    final JDialog dialog = JColorChooser.createDialog(button, 
      "Pick a Color", true, colorChooser, okListener, null);
    
    dialog.setAlwaysOnTop(true);
    



    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { setBackground(colorEditorcurrentColor);
        colorChooser.setColor(colorEditorcurrentColor);
        


        dialog.setVisible(true);
      }
    });
  }
  
  public static void setUpColorRenderer(JTable table) {
    table.setDefaultRenderer(Color.class, new ColorRenderer(true));
  }
  

  static class ColorEditor
    extends DefaultCellEditor
  {
    private static final long serialVersionUID = 2119462670023064890L;
    
    Color currentColor = null;
    
    public ColorEditor(JButton b) {
      super();
      

      editorComponent = b;
      setClickCountToStart(1);
      

      b.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          fireEditingStopped();
        }
      });
    }
    
    protected void fireEditingStopped() {
      super.fireEditingStopped();
    }
    
    public Object getCellEditorValue() {
      return currentColor;
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
      ((JButton)editorComponent).setText(value.toString());
      currentColor = ((Color)value);
      return editorComponent;
    }
  }
  

  static class ColorRenderer
    extends JLabel
    implements TableCellRenderer
  {
    private static final long serialVersionUID = -1242267039932659803L;
    Border unselectedBorder = null;
    
    Border selectedBorder = null;
    
    boolean isBordered = true;
    
    public ColorRenderer(boolean isBordered)
    {
      this.isBordered = isBordered;
      setOpaque(true);
    }
    

    public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column)
    {
      setBackground((Color)color);
      if (isBordered) {
        if (isSelected) {
          if (selectedBorder == null) {
            selectedBorder = BorderFactory.createMatteBorder(2, 5, 
              2, 5, table.getSelectionBackground());
          }
          setBorder(selectedBorder);
        } else {
          if (unselectedBorder == null) {
            unselectedBorder = BorderFactory.createMatteBorder(2, 
              5, 2, 5, table.getBackground());
          }
          setBorder(unselectedBorder);
        }
      }
      return this;
    }
  }
  



  public class JTableA
    extends JTable
  {
    private static final long serialVersionUID = 2385855679443552126L;
    

    protected TableUtils.RowEditorModel rm;
    


    public JTableA(TableModel tm)
    {
      super();
      rm = getNewRowEditorModel();
      
      ListSelectionModel rowSM = getSelectionModel();
      rowSM.addListSelectionListener(new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e) {
          if (e.getValueIsAdjusting())
          {
            return;
          }
          
          GlobalHotkeyManager ghm = GlobalHotkeyManager.getInstance();
          
          ListSelectionModel lsm = 
            (ListSelectionModel)e.getSource();
          if (lsm.isSelectionEmpty()) {
            ghm.setEnabled(true);
          }
          else {
            ghm.setEnabled(false);
          }
        }
      });
    }
    

    public void setRowEditorModel(TableUtils.RowEditorModel rm)
    {
      this.rm = rm;
    }
    
    public TableUtils.RowEditorModel getRowEditorModel() {
      return rm;
    }
    
    public TableCellEditor getCellEditor(int row, int col) {
      TableCellEditor tmpEditor = null;
      if (rm != null)
        tmpEditor = rm.getEditor(row);
      if (tmpEditor != null)
        return tmpEditor;
      return super.getCellEditor(row, col);
    }
    
    public void clear() {
      rm.clear();
    }
    
    public void cancelCellEditing() {
      rm.cancelCellEditing();
    }
  }
  

  public class RowEditorModel
  {
    private Hashtable<Integer, TableCellEditor> data;
    
    private Hashtable<Integer, JComboBox> dataBoxes;
    private Hashtable<Integer, JTextField> dataBoxesText;
    
    public RowEditorModel()
    {
      data = new Hashtable();
      dataBoxes = new Hashtable();
    }
    
    public void addEditorForRow(int row, TableCellEditor e, JComboBox c) {
      Integer i = new Integer(row);
      data.put(i, e);
      dataBoxes.put(i, c);
    }
    
    public void addEditorForRowText(int row, TableCellEditor e, JTextField c)
    {
      Integer i = new Integer(row);
      data.put(i, e);
      dataBoxesText.put(i, c);
    }
    
    public void removeEditorForRow(int row) {
      TableCellEditor e = (TableCellEditor)data.remove(new Integer(row));
      dataBoxes.remove(new Integer(row));
      e.cancelCellEditing();
    }
    
    public TableCellEditor getEditor(int row) {
      return (TableCellEditor)data.get(new Integer(row));
    }
    
    public JComboBox getBox(int row) {
      return (JComboBox)dataBoxes.get(new Integer(row));
    }
    
    public void clear() {
      for (TableCellEditor t : data.values()) {
        t.cancelCellEditing();
        t = null;
      }
      data.clear();
      dataBoxes.clear();
    }
    
    public void cancelCellEditing()
    {
      for (TableCellEditor t : data.values()) {
        t.cancelCellEditing();
      }
    }
  }
}
