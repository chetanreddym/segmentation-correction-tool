package ocr.gui.leftPanel;

import com.sun.media.jai.widget.DisplayJAI;
import gttool.document.DLZone;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.renderable.ParameterBlock;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import ocr.gui.OCRInterface;
import ocr.gui.Zone;
import ocr.manager.zones.ZonesManager;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageReaderDrawer;
import ocr.tif.ImageReaderDrawer.MyScrollPane;





















public class PopUpChip
  extends JFrame
{
  private Vector<DLZone> visibleZoneVector = new Vector();
  

  private Vector<DLZone> allExistingZoneVector;
  
  private JScrollPane popUpScroll;
  
  private JPanel popUpPanel;
  
  private int w = 0; private int h = 0; private int ltx = 0; private int lty = 0;
  

  private ImageReaderDrawer imgreadobj;
  

  public JFrame frame;
  

  private Map<JButton, DLZone> chipToZone;
  

  private ActionListener chipListener;
  


  public PopUpChip()
  {
    if (WorkmodeTable.popUpImageChips != null) {
      popUpImageChipsframe.dispose();
    }
    frame = new JFrame();
    
    Container content = getContentPane();
    content.setLayout(new FlowLayout());
    

    imgreadobj = OCRInterface.currentHWObj;
    

    allExistingZoneVector = imgreadobj.curr_canvas.getShapeVec().getAsVector();
    

    visibleZoneVector = getVisibleZones(allExistingZoneVector);
    
    if (allExistingZoneVector.size() == 0) {
      JOptionPane.showMessageDialog(null, "There are no created zones.");
      return;
    }
    
    if (visibleZoneVector.size() == 0) {
      JOptionPane.showMessageDialog(null, "Zones of all types are invisible.\nTurn on \"VISIBLE\" mark and try again.");
      return;
    }
    

    ChipDisplay();
  }
  













  public void ChipDisplay()
  {
    popUpPanel = new JPanel();
    popUpPanel.setBackground(Color.lightGray);
    popUpScroll = new JScrollPane(popUpPanel);
    
    PlanarImage currPage = OCRInterface.currentHWObj.getOriginalImage();
    PlanarImage chipImage = null;
    
    chipListener = new imageChipListener(null);
    
    chipToZone = new HashMap();
    
    for (DLZone curZone : visibleZoneVector) {
      final JButton button = new JButton();
      
      Border border = new LineBorder(Color.BLACK, 4);
      button.setBorder(border);
      

      Rectangle r = ((Zone)curZone).get_Bounds();
      ltx = x;
      lty = y;
      w = width;
      h = height;
      try
      {
        chipImage = CropDescriptor.create(
          currPage, 
          new Float(ltx), 
          new Float(lty), 
          new Float(w), 
          new Float(h), 
          null);
      } catch (IllegalArgumentException iae) {
        JOptionPane.showMessageDialog(null, "XML file problem. Some zone(s) is outside the image.");
        return;
      }
      
      chipImage = TranslateDescriptor.create(
        chipImage, 
        new Float(-chipImage.getMinX()), 
        new Float(-chipImage.getMinY()), 
        null, 
        null);
      
      int width_chip = chipImage.getWidth();
      
      float scale = 300.0F / width_chip;
      
      ParameterBlock pb = new ParameterBlock();
      pb.addSource(chipImage);
      pb.add(scale);
      pb.add(scale);
      pb.add(0.0F);
      pb.add(0.0F);
      pb.add(new InterpolationNearest());
      
      PlanarImage scaledImage = JAI.create("scale", pb);
      
      DisplayJAI charChip = new DisplayJAI(scaledImage);
      
      button.add(charChip);
      

      chipToZone.put(button, curZone);
      

      button.addActionListener(chipListener);
      




      button.addMouseListener(new MouseAdapter()
      {
        public void mousePressed(MouseEvent me) {
          Border border = new LineBorder(Color.BLACK, 4);
          button.setBorder(border);
        }
        
        public void mouseClicked(MouseEvent me) {}
        
        public void mouseReleased(MouseEvent me)
        {
          OCRInterface.this_interface.getCanvas().paintCanvas();
          Border border = new LineBorder(Color.BLACK, 4);
          button.setBorder(border);
        }
        
        public void mouseExited(MouseEvent me) {
          Border border = new LineBorder(Color.BLACK, 4);
          button.setBorder(border);
        }
        
        public void mouseEntered(MouseEvent me) {
          Border border = new LineBorder(Color.ORANGE, 4);
          button.setBorder(border);
        }
        

      });
      popUpPanel.setLayout(new BoxLayout(popUpPanel, 3));
      popUpPanel.add(button);
      

      setPopUpWindow();
    }
  }
  

  private void setPopUpWindow()
  {
    frame.setTitle("Image Chips");
    
    frame.setPreferredSize(new Dimension(340, 
      OCRInterface.this_interface.getHeight()));
    frame.setResizable(false);
    frame.setLocation(0, 0);
    frame.add(popUpScroll);
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(2);
  }
  










  public Vector<DLZone> getVisibleZones(Vector<DLZone> allExistingZoneVector)
  {
    Vector<DLZone> visibleZones = new Vector();
    for (DLZone zone : allExistingZoneVector) {
      if (this_interfacetbdPane.data_panel.t_window.isVisible(((Zone)zone).getZoneType()))
        visibleZones.add(zone);
    }
    return visibleZones;
  }
  


  private class imageChipListener
    implements ActionListener
  {
    private imageChipListener() {}
    


    public void actionPerformed(ActionEvent event)
    {
      Object clickedImageChip = event.getSource();
      
      for (Object imageChip : chipToZone.keySet()) {
        if (imageChip == clickedImageChip)
        {
          OCRInterface.this_interface.getCanvas().reset();
          

          DLZone zoneToBeSelected = (DLZone)chipToZone.get(imageChip);
          

          OCRInterface.this_interface.getCanvas().addToSelected((Zone)zoneToBeSelected);
          

          ImageReaderDrawer.MyScrollPane picscroll = imgreadobj.getPictureScrollPane();
          
          float currentScale = OCRInterface.this_interface.getCanvas().getScale();
          
          Point p = new Point(dlGetZoneOriginx, dlGetZoneOriginy);
          
          int selectedZoneX = (int)(x * currentScale);
          int selectedZoneY = (int)(y * currentScale);
          
          int viewPortWidth = picscroll.getViewport().getWidth();
          int viewPortHeight = picscroll.getViewport().getHeight();
          

          int verticalBarPosition = selectedZoneY - viewPortHeight / 2;
          int horizontalBarPosition = selectedZoneX - viewPortWidth / 2;
          
          picscroll.getVerticalScrollBar().setValue(verticalBarPosition);
          picscroll.getHorizontalScrollBar().setValue(horizontalBarPosition);
        }
      }
    }
  }
}
