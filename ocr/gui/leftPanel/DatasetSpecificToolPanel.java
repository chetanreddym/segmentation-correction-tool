package ocr.gui.leftPanel;

import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import ocr.gui.OCRInterface;
import ocr.manager.PropertiesInfoHolder;








public class DatasetSpecificToolPanel
  extends JPanel
{
  private int dividerLocation = 250;
  



  OCRInterface ocrIF = OCRInterface.this_interface;
  



  PropertiesInfoHolder workmodeProps;
  



  int workmode;
  



  public WorkmodeTable dataTable;
  


  public TypeWindow t_window;
  


  public AttributeWindow a_window;
  


  private JSplitPane mainSPane;
  



  public DatasetSpecificToolPanel(int workmode)
  {
    this.workmode = workmode;
    workmodeProps = ocrIF.workmodeProps[workmode];
    
    t_window = new TypeWindow();
    a_window = new AttributeWindow();
    setLayout(new CardLayout());
    int datasetIndex = 0;
    
    mainSPane = new JSplitPane(0, 
      t_window, 
      a_window);
    
    mainSPane.setDividerLocation(dividerLocation);
    mainSPane.setDividerSize(6);
    mainSPane.setOneTouchExpandable(true);
    
    add(mainSPane, Integer.toString(datasetIndex));
  }
  






  public String getSelectedAssignID()
  {
    return TypeWindow.getSelectedAssignID();
  }
}
