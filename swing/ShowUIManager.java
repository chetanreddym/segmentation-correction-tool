package swing;

import javax.swing.UIDefaults;

public class ShowUIManager {
  public ShowUIManager() {}
  
  public static void main(String[] args) {
    UIDefaults defaults = javax.swing.UIManager.getDefaults();
    System.out.println("Count Item = " + defaults.size());
    String[] colName = { "Key", "Value" };
    String[][] rowData = new String[defaults.size()][2];
    int i = 0;
    for (java.util.Enumeration e = defaults.keys(); e.hasMoreElements(); i++) {
      Object key = e.nextElement();
      rowData[i][0] = key.toString();
      rowData[i][1] = defaults.get(key);
      System.out.println(rowData[i][0] + " ,, " + rowData[i][1]);
    }
    javax.swing.JFrame f = new javax.swing.JFrame("UIDefaults Key-Value sheet");
    javax.swing.JTable t = new javax.swing.JTable(rowData, colName);
    f.setContentPane(new javax.swing.JScrollPane(t));
    
    f.pack();
    f.setVisible(true);
  }
}
