package ocr.manager;

import java.awt.Component;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import ocr.gui.OCRInterface;
import ocr.gui.ToolBarButton;
import ocr.gui.leftPanel.WorkmodeTable;













public class GlobalDisableManager
{
  public Vector opmodeComponents = new Vector();
  





  public Vector allOpmodeComponents = new Vector();
  




  public JButton newSpecialCharButt = null;
  




  public JButton createOppModeButt = null;
  




  public JMenuItem createMenuItem = null;
  







  public JButton[][] addToTrn = new JButton[3][2];
  





  OCRInterface ocrIF;
  






  public GlobalDisableManager() {}
  





  public void zonesSelected(int numZonesSelected)
  {
    if (newSpecialCharButt != null)
    {
      if (numZonesSelected == 1) {
        newSpecialCharButt.setEnabled(true);
      } else {
        newSpecialCharButt.setEnabled(false);
      }
    }
  }
  






















  public void updateOppModeButts()
  {
    ocrIF = OCRInterface.this_interface;
    









    for (int i = 0; i < allOpmodeComponents.size(); i++) {
      ((Component)allOpmodeComponents.get(i)).setEnabled(true);
    }
    



    if ((ocrIF.workmodeTables[OCRInterface.currWorkmode].isSelectedLocked()) || 
      (!ocrIF.a_file_opened))
    {
      for (int i = 0; i < opmodeComponents.size(); i++)
      {
        ((Component)opmodeComponents.get(i)).setEnabled(false);
      }
      ocrIF.setOppmode(0);
    }
    else
    {
      for (int i = 0; i < opmodeComponents.size(); i++)
      {
        ((Component)opmodeComponents.get(i)).setEnabled(true);
      }
    }
    









    checkPCButtons();
  }
  






  public void tableCellSelected(boolean isLocked, int rowSelected)
  {
    if (rowSelected < 0) {
      return;
    }
    ocrIF = OCRInterface.this_interface;
    

    updateOppModeButts();
  }
  







































  public void checkPCButtons()
  {
    for (int i = 0; i < allOpmodeComponents.size(); i++) {
      Component tbb = (Component)allOpmodeComponents.get(i);
      if (((tbb instanceof ToolBarButton)) && (tbb.getName() != null) && (
        (tbb.getName().equals("DrawPCLine")) || 
        (tbb.getName().equals("ErasePCL")) || 
        (tbb.getName().equals("Parent/Child")) || 
        (tbb.getName().equals("Hierarchy (On/Off)")))) {
        if (!ocrIF.getEnablePCButtons()) {
          tbb.setEnabled(false);
          ocr.tif.ImageDisplay.alwaysDestroyH = true;


        }
        else
        {


          tbb.setEnabled(true);
        }
      }
    }
  }
}
