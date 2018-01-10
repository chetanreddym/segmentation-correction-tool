package com.sun.media.jai.widget;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import javax.swing.JPanel;



































public class DisplayJAI
  extends JPanel
  implements MouseListener, MouseMotionListener
{
  protected RenderedImage source = null;
  

  protected int originX = 0;
  

  protected int originY = 0;
  




  public DisplayJAI()
  {
    setLayout(null);
  }
  












  public DisplayJAI(RenderedImage image)
  {
    setLayout(null);
    
    if (image == null) {
      throw new IllegalArgumentException("image == null!");
    }
    
    source = image;
    

    int w = source.getWidth();
    int h = source.getHeight();
    Insets insets = getInsets();
    Dimension dim = new Dimension(w + left + right, h + top + bottom);
    


    setPreferredSize(dim);
  }
  








  public void setOrigin(int x, int y)
  {
    originX = x;
    originY = y;
    repaint();
  }
  
  public Point getOrigin()
  {
    return new Point(originX, originY);
  }
  











  public void set(RenderedImage im)
  {
    if (im == null) {
      throw new IllegalArgumentException("im == null!");
    }
    
    source = im;
    

    int w = source.getWidth();
    int h = source.getHeight();
    Insets insets = getInsets();
    Dimension dim = new Dimension(w + left + right, h + top + bottom);
    


    setPreferredSize(dim);
    revalidate();
    repaint();
  }
  













  public void set(RenderedImage im, int x, int y)
  {
    if (im == null) {
      throw new IllegalArgumentException("im == null!");
    }
    
    source = im;
    

    int w = source.getWidth();
    int h = source.getHeight();
    Insets insets = getInsets();
    Dimension dim = new Dimension(w + left + right, h + top + bottom);
    

    setPreferredSize(dim);
    
    originX = x;
    originY = y;
    
    revalidate();
    repaint();
  }
  




  public RenderedImage getSource()
  {
    return source;
  }
  


















  public synchronized void paintComponent(Graphics g)
  {
    Graphics2D g2d = (Graphics2D)g;
    

    if (source == null) {
      g2d.setColor(getBackground());
      g2d.fillRect(0, 0, getWidth(), getHeight());
      return;
    }
    

    Rectangle clipBounds = g2d.getClipBounds();
    g2d.setColor(getBackground());
    g2d.fillRect(x, y, width, height);
    




    Insets insets = getInsets();
    int tx = left + originX;
    int ty = top + originY;
    
    try
    {
      g2d.drawRenderedImage(source, AffineTransform.getTranslateInstance(tx, ty));
    }
    catch (OutOfMemoryError e) {}
  }
  
  public void mousePressed(MouseEvent e) {}
  
  public void mouseReleased(MouseEvent e) {}
  
  public void mouseMoved(MouseEvent e) {}
  
  public void mouseDragged(MouseEvent e) {}
  
  public void mouseEntered(MouseEvent e) {}
  
  public void mouseExited(MouseEvent e) {}
  
  public void mouseClicked(MouseEvent e) {}
}
