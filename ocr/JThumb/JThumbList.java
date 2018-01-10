package ocr.JThumb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import ocr.gui.OCRInterface;
import ocr.gui.leftPanel.WorkmodeTable;
















public class JThumbList
  extends JList
{
  public int THUMB_WIDTH = 300;
  public int THUMB_HEIGHT = 300;
  
  public JThumbListModel listModel = null;
  public JThumbPanel cellRenderer = new JThumbPanel();
  
  public JPopupMenu popup = new JPopupMenu();
  
  private int visibleCells = 1;
  private int viewWidth = 0;
  private int viewHeight = 0;
  
  public OCRInterface ocrIF = null;
  
  public int[] indices = null;
  public final JThumbListModel finalModel;
  
  public JThumbList(JThumbListModel listModel, int w, int h)
  {
    super(listModel);
    this.listModel = listModel;
    finalModel = listModel;
    
    viewWidth = w;
    viewHeight = h;
    
    listModel.setList(this);
    ocrIF = ocrIF;
    

    addMouseListener(new MouseAdapter()
    {
      public void mousePressed(MouseEvent evt) { maybeShowPopup(evt); }
      public void mouseReleased(MouseEvent evt) { maybeShowPopup(evt); }
      
      public void mouseClicked(MouseEvent evt) {
        maybeShowPopup(evt);
        
        JList list = (JList)evt.getSource();
        if (evt.getClickCount() == 2)
        {
          int index = list.locationToIndex(evt.getPoint());
          if (indices != null) {
            index = indices[index];
          }
          System.out.println("this is selected: " + index);
          


          OCRInterface.this_interface.selectImagePanel();
          this_interfaceocrTable
            .processSelectionEvent(index, 1, true);
        }
      }
      





      private void maybeShowPopup(MouseEvent evt)
      {
        if (evt.isPopupTrigger()) {
          popup.show(evt.getComponent(), 
            evt.getX(), evt.getY());
        }
        
      }
      
    });
    setSelectionMode(2);
    
    setCellRenderer(cellRenderer);
    setFixedCellWidth(THUMB_WIDTH);
    setFixedCellHeight(THUMB_HEIGHT);
    

    setLayoutOrientation(2);
    setVisibleRowCount(0);
    

    ActionListener menuListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Preview Selected"))
        {
          Object[] images = getSelectedValues();
          finalModel.setVisibleImages(images);
          indices = getSelectedIndices();

        }
        
      }
      

    };
    JMenuItem menuItem = new JMenuItem("Preview Selected");
    menuItem.addActionListener(menuListener);
    popup.add(menuItem);
  }
  






  public void resizeCells(int viewWidth, int viewHeight)
  {
    this.viewHeight = viewHeight;
    this.viewWidth = viewWidth;
    THUMB_WIDTH = ((int)((viewWidth - 18) / Math.sqrt(visibleCells)));
    THUMB_HEIGHT = ((int)((viewHeight - 2) / Math.sqrt(visibleCells)));
    setFixedCellWidth(THUMB_WIDTH);
    setFixedCellHeight(THUMB_HEIGHT);
    if (viewHeight >= 0) {
      ocrIF.pane.getVerticalScrollBar().setUnitIncrement((int)((viewHeight - 2) / Math.sqrt(visibleCells)));
      ocrIF.pane.getVerticalScrollBar().setBlockIncrement(viewHeight - 2);
    }
  }
  




  public void resizeCells(int visCells)
  {
    setVisibleCells(visCells);
    THUMB_WIDTH = ((int)((viewWidth - 18) / Math.sqrt(visibleCells)));
    THUMB_HEIGHT = ((int)((viewHeight - 2) / Math.sqrt(visibleCells)));
    setFixedCellWidth(THUMB_WIDTH);
    setFixedCellHeight(THUMB_HEIGHT);
    if (viewHeight >= 0) {
      ocrIF.pane.getVerticalScrollBar().setUnitIncrement((int)((viewHeight - 2) / Math.sqrt(visibleCells)));
      ocrIF.pane.getVerticalScrollBar().setBlockIncrement(viewHeight - 2);
    }
    selectIndex(getSelectedIndex());
  }
  
  private void setVisibleCells(int s)
  {
    visibleCells = s;
  }
  
  public int getVisibleCells() {
    return visibleCells;
  }
  
  public JThumbListModel getListModel() {
    return listModel;
  }
  



  public void selectIndex(int index)
  {
    addSelectionInterval(index, index);
    ensureIndexIsVisible(index + visibleCells / 2);
    ensureIndexIsVisible(index - visibleCells / 2);
    ensureIndexIsVisible(index);
  }
}
