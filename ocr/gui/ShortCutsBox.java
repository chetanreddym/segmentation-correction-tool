package ocr.gui;

import gttool.misc.Attribute;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import ocr.gui.leftPanel.TypeWindow;
import ocr.util.AttributeConfigUtil;








public class ShortCutsBox
  extends JDialog
{
  private JPanel mainPane = new JPanel();
  
  public ShortCutsBox(OCRInterface parent)
  {
    super(parent, "Shortcut Keys");
    getContentPane().add(mainPane);
    setDefaultCloseOperation(2);
    
    mainPane.setLayout(new BoxLayout(mainPane, 1));
    

    JPanel gen = new JPanel(new BorderLayout());
    gen.setBorder(new TitledBorder("General Shortcuts Keys"));
    gen.add(new JScrollPane(generalShortcuts()), "Center");
    gen.setPreferredSize(new Dimension(400, 650));
    
    mainPane.add(gen);
    
    JPanel draw = new JPanel(new BorderLayout());
    draw.setBorder(new TitledBorder("Drawing Shortcuts Keys"));
    draw.add(new JScrollPane(drawingShortcuts()), "Center");
    draw.setPreferredSize(new Dimension(400, 200));
    
    mainPane.add(draw);
    
    JPanel zoneShortcuts = new JPanel(new BorderLayout());
    zoneShortcuts.setBorder(new TitledBorder("Zone Shortcut Keys"));
    zoneShortcuts.add(new JScrollPane(ZoneShortcutsTable()), "Center");
    
    mainPane.add(zoneShortcuts);
    
    JPanel atts = new JPanel(new BorderLayout());
    atts.setBorder(new TitledBorder("Zone and Page Attribute Shortcut Keys"));
    atts.add(new JScrollPane(keyBindingsTable()), "Center");
    
    mainPane.add(atts);
    

    JPanel okBut = new JPanel(new BorderLayout());
    okBut.add(new JButton("OK") {
      public void fireActionPerformed(ActionEvent ae) {
        Done();
      }
    }, "East");
    mainPane.add(okBut);
    
    mainPane.setPreferredSize(new Dimension(400, 750));
    
    addESCClose();
    
    pack();
    setVisible(true);
  }
  
  private JTable ZoneShortcutsTable()
  {
    String[] cols = { "Function Key", "Zone" };
    MyTableModel model = new MyTableModel(cols, 0);
    JTable ret = new JTable(model);
    
    LinkedHashMap<String, String> map = OCRInterface.getAttsConfigUtil().getFunctionKeyBindings();
    
    for (String funcKey : map.keySet()) {
      String zone = (String)map.get(funcKey);
      if ((!funcKey.equals(TypeWindow.NONE)) && 
        (!zone.equals(TypeWindow.NONE))) {
        model.addRow(new Object[] { funcKey, zone });
      }
    }
    return ret;
  }
  




  private JTable generalShortcuts()
  {
    MyTableModel model = new MyTableModel(0, 0);
    JTable ret = new JTable(model);
    

    model.addColumn("Command", new String[] {
      "Load File/Directory", 
      "Merge Workspaces", 
      "Save Document", 
      "Save Workspace", 
      "Save Work Space As", 
      "Save Document w/ Workspace", 
      "Exit", 
      "Undo", 
      "Redo", 
      "Merge Zones", 
      "Split Zones", 
      "Search zone", 
      "Select all zones", 
      "Preferences", 
      "Delete Selected Zone", 
      "Move to Next Zone", 
      "Move to Previous Zone", 
      "Clear Type & Shape Selection", 
      "Increase Size of Electronic Text", 
      "Decrease Size of Electronic Text", 
      "Hide Sidebar", 
      "Show Sidebar", 
      "Resize Image Browser Table" });
    
    model.addColumn("Key", new String[] {
      "alt+O", 
      "alt+M", 
      "alt+S", 
      "alt+W", 
      "alt+A", 
      "alt+D", 
      "alt+X", 
      "ctrl+Z", 
      "ctrl+Y", 
      "ctrl+R", 
      "ctrl+S", 
      "ctrl+F", 
      "ctrl+A", 
      "alt+enter", 
      "del", 
      "Tab", 
      "shift+Tab", 
      "esc", 
      "ctrl++", 
      "ctrl+-", 
      "alt+Left Arrow", 
      "alt+Right Arrow", 
      "ctrl+Up Arrow/Down Arrow" });
    


    return ret;
  }
  




  private JTable drawingShortcuts()
  {
    MyTableModel model = new MyTableModel(0, 0);
    JTable ret = new JTable(model);
    

    model.addColumn("Command", new String[] {
      "Draw Normal Zone", 
      "Draw Oriented Zone", 
      "Draw Polygon Zone" });
    
    model.addColumn("Key", new String[] {
      "shift-F1", 
      "shift-F2", 
      "shift-F3" });
    return ret;
  }
  
  private JTable keyBindingsTable()
  {
    String[] cols = { "Zone Name", "Shortcut Key", "Attribute Name", "Attribute Value" };
    MyTableModel model = new MyTableModel(cols, 0);
    JTable ret = new JTable(model);
    




    Map<String, Map<String, Attribute>> a = OCRInterface.getAttsConfigUtil().getAttsHotKeyList();
    
    String[] keySpace = { "0", "1", "2", "3", "4", "5", 
      "6", "7", "8", "9" };
    int j;
    int i;
    for (Iterator localIterator = a.keySet().iterator(); localIterator.hasNext(); 
        i < j)
    {
      String k = (String)localIterator.next();
      String[] arrayOfString1; j = (arrayOfString1 = keySpace).length;i = 0; continue;String key = arrayOfString1[i];
      Attribute att = (Attribute)((Map)a.get(k)).get(key);
      if (att != null)
      {
        Vector<String> row = new Vector();
        row.add(k);
        if (k.equals("DL_PAGE")) {
          row.add("alt+" + key);
        } else {
          row.add("ctrl+" + key);
        }
        row.add(att.getName());
        row.add(att.getValue());
        model.addRow(row);
      }
      i++;
    }
    

















    return ret;
  }
  
  public void Done()
  {
    dispose();
  }
  
  class MyTableModel extends DefaultTableModel
  {
    public MyTableModel(int col, int row) {
      super(row);
    }
    
    public MyTableModel(String[] col, int row) {
      super(row);
    }
    
    public boolean isCellEditable(int row, int column)
    {
      return false;
    }
  }
  






  public void addESCClose()
  {
    KeyStroke esc = KeyStroke.getKeyStroke('\033');
    Action actionListener = new AbstractAction() {
      public void actionPerformed(ActionEvent actionEvent) {
        Done();
      }
    };
    InputMap inputMap = getRootPane().getInputMap(2);
    inputMap.put(esc, "ESCAPE");
    getRootPane().getActionMap().put("ESCAPE", actionListener);
  }
}
