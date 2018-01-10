package ocr.tif.color;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.media.jai.PlanarImage;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import ocr.gui.BrowserToolBar;
import ocr.gui.OCRInterface;
import ocr.gui.ToolBarButton;
import ocr.gui.workflows.PolyTranscribeToolBar;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageReaderDrawer;


















public class ColorImageToolbar
  extends JToolBar
{
  private static final long serialVersionUID = 1L;
  private JLabel lowerValue;
  private JLabel upperValue;
  private CompoundBorder sel_border = BrowserToolBar.sel_border;
  private CompoundBorder normal_border = BrowserToolBar.normal_border;
  private ToolBarButton colorBtn;
  private ToolBarButton bwBtn;
  private RangeSlider threshold;
  private BufferedImage image;
  
  public ColorImageToolbar()
  {
    setLayout(new FlowLayout(0, 5, 5));
    
    colorBtn = new ToolBarButton(new ImageIcon(ColorImageToolbar.class.getResource("/images/colorBtn.png")));
    bwBtn = new ToolBarButton(new ImageIcon(ColorImageToolbar.class.getResource("/images/bwBtn.png")));
    
    add(colorBtn);
    add(bwBtn);
    
    colorBtn.setToolTipText("Original image");
    bwBtn.setToolTipText("Convert to BW image");
    


    addBWBtnListener();
    addColorBtnListener();
    
    addSeparator();
    
    add(new JLabel("Threshold"));
    add(Box.createHorizontalStrut(5));
    add(valuesPanel());
    add(Box.createHorizontalStrut(10));
    add(getThresholdSlider());
    
    setRollover(true);
    setFloatable(true);
    
    image = null;
  }
  
  private void addBWBtnListener() {
    bwBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0) {
        bwBtn.setBorder(sel_border);
        bwBtn.setSelected(true);
        
        convertToBW();
        
        colorBtn.setBorder(normal_border);
      }
    });
  }
  
  private void addColorBtnListener()
  {
    colorBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0) {
        colorBtn.setBorder(sel_border);
        
        ColorImageToolbar.this.loadOriginalImage();
        
        bwBtn.setSelected(false);
        bwBtn.setBorder(normal_border);
      }
    });
  }
  
  private JPanel valuesPanel()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, 1));
    panel.setPreferredSize(new Dimension(100, 32));
    lowerValue = new JLabel("lower value: ");
    upperValue = new JLabel("upper value: ");
    panel.add(lowerValue);
    panel.add(upperValue);
    return panel;
  }
  
  private RangeSlider getThresholdSlider() {
    String[] lowerUpperThresholdValues = OCRInterface.this_interface
      .getLowerUpperThresholdValues().split(",");
    
    int MIN = 0;
    int MAX = 255;
    int INIT_LOWER = Integer.parseInt(lowerUpperThresholdValues[0]);
    int INIT_UPPER = Integer.parseInt(lowerUpperThresholdValues[1]);
    
    threshold = new RangeSlider();
    
    threshold.setPreferredSize(new Dimension(500, 30));
    Font font = new Font("Serif", 1, 10);
    
    threshold.setFont(font);
    
    threshold.setMinimum(0);
    threshold.setMaximum(MAX);
    
    threshold.setUpperValue(INIT_UPPER);
    threshold.setValue(INIT_LOWER);
    
    threshold.setMajorTickSpacing(15);
    threshold.setMinorTickSpacing(5);
    threshold.setPaintLabels(true);
    
    threshold.addChangeListener(new ChangeListener()
    {
      public void stateChanged(ChangeEvent e) {
        RangeSlider slider = (RangeSlider)e.getSource();
        lowerValue.setText("lower value: " + String.valueOf(slider.getValue()));
        upperValue.setText("upper value: " + String.valueOf(slider.getUpperValue()));
        if (!bwBtn.getBorder().equals(sel_border)) {
          bwBtn.setBorder(sel_border);
          bwBtn.setSelected(true);
        }
        if (colorBtn.getBorder().equals(sel_border))
          colorBtn.setBorder(normal_border);
        convertToBW();
        ColorImageToolbar.this.saveLowerUpperValues(slider.getValue(), slider.getUpperValue());
      }
      

    });
    return threshold;
  }
  
  public void convertToBW() {
    if (image == null) {
      image = OCRInterface.currentHWObj.getOriginalImage().getAsBufferedImage();
    }
    



    if (image == null) {
      JOptionPane.showMessageDialog(OCRInterface.this_interface, 
        "Faild to convert to Black and White.\nWrong file format.", 
        "Error", 0);
      colorBtn.setBorder(normal_border);
      bwBtn.setBorder(normal_border);
      return;
    }
    
    BufferedImage biout = OCRInterface.this_interface.getCanvas()
      .convertToBW(image, threshold.getValue(), threshold.getUpperValue());
    







    PlanarImage img = PlanarImage.wrapRenderedImage(biout);
    
    OCRInterface.currentHWObj.setImageFile(img);
    
    if (OCRInterface.this_interface.getEnablePolygonTranscription()) {
      this_interfacePolyTranscribePanel.setDisplayedImageImage(img);
    }
    if (!bwBtn.getBorder().equals(sel_border)) {
      bwBtn.setBorder(sel_border);
      bwBtn.setSelected(true);
    }
  }
  
  private void loadOriginalImage() {
    currentHWObjcurr_canvas.rotateImageUsingRotationBits(true);
    
    if (OCRInterface.this_interface.getEnablePolygonTranscription())
    {
      this_interfacePolyTranscribePanel.setDisplayedImageImage(null);
    }
    bwBtn.setSelected(false);
  }
  











  public void resetBtn()
  {
    bwBtn.setBorder(new JButton().getBorder());
    colorBtn.setBorder(new JButton().getBorder());
  }
  


  private void saveLowerUpperValues(int lower, int upper)
  {
    OCRInterface.this_interface.setLowerUpperThresholdValues(lower + "," + upper);
  }
  
  public void setImage(BufferedImage image) {
    this.image = image;
  }
  
  public boolean isBlackAndWhite() {
    return bwBtn.isSelected();
  }
}
