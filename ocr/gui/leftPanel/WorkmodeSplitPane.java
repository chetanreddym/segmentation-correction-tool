package ocr.gui.leftPanel;

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;



























public class WorkmodeSplitPane
  extends JSplitPane
{
  WorkmodeTable dataTable;
  DatasetSpecificToolPanel bottomPanel;
  int workmode;
  
  WorkmodeSplitPane(int workmode, WorkmodeTable dataTable, DatasetSpecificToolPanel bottomPanel)
  {
    super(0);
    
    JScrollPane tableScroll = new JScrollPane(dataTable);
    tableScroll.setPreferredSize(new Dimension(270, 300));
    setTopComponent(tableScroll);
    




    setBottomComponent(bottomPanel);
    

    setContinuousLayout(true);
    setOneTouchExpandable(true);
    setDividerLocation(200);
    setDividerSize(6);
    setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    

    this.dataTable = dataTable;
    this.bottomPanel = bottomPanel;
    this.workmode = workmode;
    

    toolPanel = bottomPanel;
    dataTable = dataTable;
  }
  
  public int getSelectedAssignID()
  {
    return 30;
  }
}
