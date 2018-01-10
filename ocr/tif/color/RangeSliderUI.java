package ocr.tif.color;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.basic.BasicSliderUI.TrackListener;




class RangeSliderUI
  extends BasicSliderUI
{
  private Color rangeColor = Color.GRAY;
  

  private Rectangle upperThumbRect;
  

  private boolean upperThumbSelected;
  

  private transient boolean lowerDragging;
  

  private transient boolean upperDragging;
  

  public RangeSliderUI(RangeSlider b)
  {
    super(b);
  }
  



  public void installUI(JComponent c)
  {
    upperThumbRect = new Rectangle();
    super.installUI(c);
  }
  



  protected BasicSliderUI.TrackListener createTrackListener(JSlider slider)
  {
    return new RangeTrackListener();
  }
  



  protected ChangeListener createChangeListener(JSlider slider)
  {
    return new ChangeHandler();
  }
  




  protected void calculateThumbSize()
  {
    super.calculateThumbSize();
    

    upperThumbRect.setSize(thumbRect.width, thumbRect.height);
  }
  




  protected void calculateThumbLocation()
  {
    super.calculateThumbLocation();
    

    if (slider.getSnapToTicks()) {
      int upperValue = slider.getValue() + slider.getExtent();
      int snappedValue = upperValue;
      int majorTickSpacing = slider.getMajorTickSpacing();
      int minorTickSpacing = slider.getMinorTickSpacing();
      int tickSpacing = 0;
      
      if (minorTickSpacing > 0) {
        tickSpacing = minorTickSpacing;
      } else if (majorTickSpacing > 0) {
        tickSpacing = majorTickSpacing;
      }
      
      if (tickSpacing != 0)
      {
        if ((upperValue - slider.getMinimum()) % tickSpacing != 0) {
          float temp = (upperValue - slider.getMinimum()) / tickSpacing;
          int whichTick = Math.round(temp);
          snappedValue = slider.getMinimum() + whichTick * tickSpacing;
        }
        
        if (snappedValue != upperValue) {
          slider.setExtent(snappedValue - slider.getValue());
        }
      }
    }
    


    if (slider.getOrientation() == 0) {
      int upperPosition = xPositionForValue(slider.getValue() + slider.getExtent());
      upperThumbRect.x = (upperPosition - upperThumbRect.width / 2);
      upperThumbRect.y = trackRect.y;
    }
    else {
      int upperPosition = yPositionForValue(slider.getValue() + slider.getExtent());
      upperThumbRect.x = trackRect.x;
      upperThumbRect.y = (upperPosition - upperThumbRect.height / 2);
    }
  }
  



  protected Dimension getThumbSize()
  {
    return new Dimension(12, 12);
  }
  




  public void paint(Graphics g, JComponent c)
  {
    super.paint(g, c);
    
    Rectangle clipRect = g.getClipBounds();
    if (upperThumbSelected)
    {
      if (clipRect.intersects(thumbRect)) {
        paintLowerThumb(g);
      }
      if (clipRect.intersects(upperThumbRect)) {
        paintUpperThumb(g);
      }
    }
    else
    {
      if (clipRect.intersects(upperThumbRect)) {
        paintUpperThumb(g);
      }
      if (clipRect.intersects(thumbRect)) {
        paintLowerThumb(g);
      }
    }
  }
  




  public void paintTrack(Graphics g)
  {
    super.paintTrack(g);
    
    Rectangle trackBounds = trackRect;
    
    if (slider.getOrientation() == 0)
    {

      int lowerX = thumbRect.x + thumbRect.width / 2;
      int upperX = upperThumbRect.x + upperThumbRect.width / 2;
      

      int cy = height / 2 - 2;
      

      Color oldColor = g.getColor();
      g.translate(x, y + cy);
      

      g.setColor(rangeColor);
      for (int y = 0; y <= 3; y++) {
        g.drawLine(lowerX - x, y, upperX - x, y);
      }
      

      g.translate(-x, -(y + cy));
      g.setColor(oldColor);

    }
    else
    {
      int lowerY = thumbRect.x + thumbRect.width / 2;
      int upperY = upperThumbRect.x + upperThumbRect.width / 2;
      

      int cx = width / 2 - 2;
      

      Color oldColor = g.getColor();
      g.translate(x + cx, y);
      

      g.setColor(rangeColor);
      for (int x = 0; x <= 3; x++) {
        g.drawLine(x, lowerY - y, x, upperY - y);
      }
      

      g.translate(-(x + cx), -y);
      g.setColor(oldColor);
    }
  }
  





  public void paintThumb(Graphics g) {}
  




  private void paintLowerThumb(Graphics g)
  {
    Rectangle knobBounds = thumbRect;
    int w = width;
    int h = height;
    

    Graphics2D g2d = (Graphics2D)g.create();
    

    Shape thumbShape = createThumbShape(w - 1, h - 1);
    

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
      RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.translate(x, y);
    

    g2d.setColor(Color.BLACK);
    g2d.fill(thumbShape);
    

    g2d.setColor(Color.BLACK);
    g2d.draw(thumbShape);
    

    g2d.dispose();
  }
  


  private void paintUpperThumb(Graphics g)
  {
    Rectangle knobBounds = upperThumbRect;
    int w = width;
    int h = height;
    

    Graphics2D g2d = (Graphics2D)g.create();
    

    Shape thumbShape = createThumbShape(w - 1, h - 1);
    

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
      RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.translate(x, y);
    

    g2d.setColor(Color.BLACK);
    g2d.fill(thumbShape);
    

    g2d.setColor(Color.BLACK);
    g2d.draw(thumbShape);
    

    g2d.dispose();
  }
  



  private Shape createThumbShape(int width, int height)
  {
    Ellipse2D shape = new Ellipse2D.Double(0.0D, 0.0D, width, height);
    return shape;
  }
  





  private void setUpperThumbLocation(int x, int y)
  {
    Rectangle upperUnionRect = new Rectangle();
    upperUnionRect.setBounds(upperThumbRect);
    
    upperThumbRect.setLocation(x, y);
    
    SwingUtilities.computeUnion(upperThumbRect.x, upperThumbRect.y, upperThumbRect.width, upperThumbRect.height, upperUnionRect);
    slider.repaint(x, y, width, height);
  }
  



  public void scrollByBlock(int direction)
  {
    synchronized (slider) {
      int blockIncrement = (slider.getMaximum() - slider.getMinimum()) / 10;
      if ((blockIncrement <= 0) && (slider.getMaximum() > slider.getMinimum())) {
        blockIncrement = 1;
      }
      int delta = blockIncrement * (direction > 0 ? 1 : -1);
      
      if (upperThumbSelected) {
        int oldValue = ((RangeSlider)slider).getUpperValue();
        ((RangeSlider)slider).setUpperValue(oldValue + delta);
      } else {
        int oldValue = slider.getValue();
        slider.setValue(oldValue + delta);
      }
    }
  }
  



  public void scrollByUnit(int direction)
  {
    synchronized (slider) {
      int delta = 1 * (direction > 0 ? 1 : -1);
      
      if (upperThumbSelected) {
        int oldValue = ((RangeSlider)slider).getUpperValue();
        ((RangeSlider)slider).setUpperValue(oldValue + delta);
      } else {
        int oldValue = slider.getValue();
        slider.setValue(oldValue + delta);
      }
    }
  }
  
  public class ChangeHandler
    implements ChangeListener
  {
    public ChangeHandler() {}
    
    public void stateChanged(ChangeEvent arg0)
    {
      if ((!lowerDragging) && (!upperDragging)) {
        calculateThumbLocation();
        slider.repaint();
      }
    }
  }
  
  public class RangeTrackListener extends BasicSliderUI.TrackListener
  {
    public RangeTrackListener() {
      super();
    }
    
    public void mousePressed(MouseEvent e) {
      if (!slider.isEnabled()) {
        return;
      }
      
      currentMouseX = e.getX();
      currentMouseY = e.getY();
      
      if (slider.isRequestFocusEnabled()) {
        slider.requestFocus();
      }
      



      boolean lowerPressed = false;
      boolean upperPressed = false;
      if (upperThumbSelected) {
        if (upperThumbRect.contains(currentMouseX, currentMouseY)) {
          upperPressed = true;
        } else if (thumbRect.contains(currentMouseX, currentMouseY)) {
          lowerPressed = true;
        }
      }
      else if (thumbRect.contains(currentMouseX, currentMouseY)) {
        lowerPressed = true;
      } else if (upperThumbRect.contains(currentMouseX, currentMouseY)) {
        upperPressed = true;
      }
      


      if (lowerPressed) {
        switch (slider.getOrientation()) {
        case 1: 
          offset = (currentMouseY - thumbRect.y);
          break;
        case 0: 
          offset = (currentMouseX - thumbRect.x);
        }
        
        upperThumbSelected = false;
        lowerDragging = true;
        return;
      }
      lowerDragging = false;
      

      if (upperPressed) {
        switch (slider.getOrientation()) {
        case 1: 
          offset = (currentMouseY - upperThumbRect.y);
          break;
        case 0: 
          offset = (currentMouseX - upperThumbRect.x);
        }
        
        upperThumbSelected = true;
        upperDragging = true;
        return;
      }
      upperDragging = false;
    }
    
    public void mouseReleased(MouseEvent e)
    {
      lowerDragging = false;
      upperDragging = false;
      slider.setValueIsAdjusting(false);
      super.mouseReleased(e);
    }
    
    public void mouseDragged(MouseEvent e)
    {
      if (!slider.isEnabled()) {
        return;
      }
      
      currentMouseX = e.getX();
      currentMouseY = e.getY();
      
      if (lowerDragging) {
        slider.setValueIsAdjusting(true);
        moveLowerThumb();
      }
      else if (upperDragging) {
        slider.setValueIsAdjusting(true);
        moveUpperThumb();
      }
    }
    
    public boolean shouldScroll(int direction)
    {
      return false;
    }
    



    private void moveLowerThumb()
    {
      int thumbMiddle = 0;
      
      switch (slider.getOrientation()) {
      case 1: 
        int halfThumbHeight = thumbRect.height / 2;
        int thumbTop = currentMouseY - offset;
        int trackTop = trackRect.y;
        int trackBottom = trackRect.y + (trackRect.height - 1);
        int vMax = yPositionForValue(slider.getValue() + slider.getExtent());
        

        if (drawInverted()) {
          trackBottom = vMax;
        } else {
          trackTop = vMax;
        }
        thumbTop = Math.max(thumbTop, trackTop - halfThumbHeight);
        thumbTop = Math.min(thumbTop, trackBottom - halfThumbHeight);
        
        setThumbLocation(thumbRect.x, thumbTop);
        

        thumbMiddle = thumbTop + halfThumbHeight;
        slider.setValue(valueForYPosition(thumbMiddle));
        break;
      
      case 0: 
        int halfThumbWidth = thumbRect.width / 2;
        int thumbLeft = currentMouseX - offset;
        int trackLeft = trackRect.x;
        int trackRight = trackRect.x + (trackRect.width - 1);
        int hMax = xPositionForValue(slider.getValue() + slider.getExtent());
        

        if (drawInverted()) {
          trackLeft = hMax;
        } else {
          trackRight = hMax;
        }
        thumbLeft = Math.max(thumbLeft, trackLeft - halfThumbWidth);
        thumbLeft = Math.min(thumbLeft, trackRight - halfThumbWidth);
        
        setThumbLocation(thumbLeft, thumbRect.y);
        

        thumbMiddle = thumbLeft + halfThumbWidth;
        slider.setValue(valueForXPosition(thumbMiddle));
        break;
      }
      
    }
    





    private void moveUpperThumb()
    {
      int thumbMiddle = 0;
      
      switch (slider.getOrientation()) {
      case 1: 
        int halfThumbHeight = thumbRect.height / 2;
        int thumbTop = currentMouseY - offset;
        int trackTop = trackRect.y;
        int trackBottom = trackRect.y + (trackRect.height - 1);
        int vMin = yPositionForValue(slider.getValue());
        

        if (drawInverted()) {
          trackTop = vMin;
        } else {
          trackBottom = vMin;
        }
        thumbTop = Math.max(thumbTop, trackTop - halfThumbHeight);
        thumbTop = Math.min(thumbTop, trackBottom - halfThumbHeight);
        
        RangeSliderUI.this.setUpperThumbLocation(thumbRect.x, thumbTop);
        

        thumbMiddle = thumbTop + halfThumbHeight;
        slider.setExtent(valueForYPosition(thumbMiddle) - slider.getValue());
        break;
      
      case 0: 
        int halfThumbWidth = thumbRect.width / 2;
        int thumbLeft = currentMouseX - offset;
        int trackLeft = trackRect.x;
        int trackRight = trackRect.x + (trackRect.width - 1);
        int hMin = xPositionForValue(slider.getValue());
        

        if (drawInverted()) {
          trackRight = hMin;
        } else {
          trackLeft = hMin;
        }
        thumbLeft = Math.max(thumbLeft, trackLeft - halfThumbWidth);
        thumbLeft = Math.min(thumbLeft, trackRight - halfThumbWidth);
        
        RangeSliderUI.this.setUpperThumbLocation(thumbLeft, thumbRect.y);
        

        thumbMiddle = thumbLeft + halfThumbWidth;
        slider.setExtent(valueForXPosition(thumbMiddle) - slider.getValue());
        break;
      }
    }
  }
}
