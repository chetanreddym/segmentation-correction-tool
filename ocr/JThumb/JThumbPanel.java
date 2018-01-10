package ocr.JThumb;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import javax.media.jai.PlanarImage;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import ocr.gui.OCRInterface;
import ocr.gui.Zone;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.LeftPanel;
import ocr.gui.leftPanel.TypeWindow;
import ocr.manager.zones.ZonesManager;










public class JThumbPanel
  extends JPanel
  implements ListCellRenderer
{
  JThumbImage image = null;
  boolean isSelected = false;
  boolean cellHasFocus = false;
  
  public Color selectedColor = Color.LIGHT_GRAY;
  public Color backgroundColor = Color.WHITE;
  
  protected Font font = new Font("SansSerif", 1, 12);
  
  public JThumbPanel() {}
  
  public void setImage(JThumbImage newImage) { image = newImage;
    repaint(0L, 0, 0, getWidth(), getHeight());
  }
  


  public void paint(Graphics g)
  {
    PlanarImage im = image.getThumb();
    

    if (im == null) {
      im = image.getNewThumb();
    }
    int imWidth = im.getWidth();
    int imHeight = im.getHeight();
    
    int width = getWidth();
    int height = getHeight();
    
    Graphics2D g2D = null;
    if ((g instanceof Graphics2D)) {
      g2D = (Graphics2D)g;
    } else {
      return;
    }
    
    RenderingHints rHints = g2D.getRenderingHints();
    rHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, 
      RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
    rHints.put(RenderingHints.KEY_RENDERING, 
      RenderingHints.VALUE_RENDER_SPEED);
    rHints.put(RenderingHints.KEY_COLOR_RENDERING, 
      RenderingHints.VALUE_COLOR_RENDER_SPEED);
    
    g2D.setRenderingHints(rHints);
    

    if ((isSelected) || (cellHasFocus)) {
      g2D.setColor(selectedColor);
      g2D.fillRect(0, 0, width, height);
    }
    else {
      g2D.setColor(backgroundColor);
      g2D.fillRect(0, 0, width, height);
    }
    

    AffineTransform at = new AffineTransform();
    
    double xtrans = (width - imWidth) / 2;
    double ytrans = (height - imHeight) / 2;
    
    at.translate(xtrans, ytrans);
    



    g2D.drawRenderedImage(im, at);
    



    g2D.setColor(Color.BLACK);
    g2D.setFont(font);
    g2D.drawString(image.fileName, 1, 12);
    
    drawZones(g, xtrans, ytrans, imWidth, imHeight);
    
    g2D.dispose();
  }
  







  public void drawZones(Graphics g, double x, double y, int imWidth, int imHeight)
  {
    OCRInterface ocrIF = image.listModel.ocrIF;
    

    for (Iterator iter = image.getZones().iterator(); iter.hasNext();)
    {
      Zone cur_zone = (Zone)iter.next();
      


















      Color cl = tbdPane.data_panel.t_window.getColor(cur_zone
        .getZoneType());
      
      float[] comps = cl.getComponents(null);
      cl = new Color(comps[0], comps[1], comps[2], comps[3] - TRANSPARENCY);
      
      g.setColor(cl);
      
      if (image.listModel.getList().getVisibleCells() <= 4) {
        cur_zone.draw(g, x, y, imWidth, imHeight, image.listModel.getRotate(), image.scale, false, false);
      }
    }
  }
  
















  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
  {
    this.isSelected = isSelected;
    this.cellHasFocus = cellHasFocus;
    setImage((JThumbImage)value);
    
    return this;
  }
}
