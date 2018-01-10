package javax.media.jai.widget;

import java.awt.Adjustable;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.RenderedImage;
import java.util.Vector;










































/**
 * @deprecated
 */
public class ScrollingImagePanel
  extends ScrollPane
  implements AdjustmentListener, ComponentListener, MouseListener, MouseMotionListener
{
  protected ImageCanvas ic;
  protected RenderedImage im;
  protected int panelWidth;
  protected int panelHeight;
  protected Vector viewportListeners = new Vector();
  

  protected Point moveSource;
  

  public ScrollingImagePanel(RenderedImage im, int width, int height)
  {
    this.im = im;
    panelWidth = width;
    panelHeight = height;
    
    ic = new ImageCanvas(im);
    
    getHAdjustable().addAdjustmentListener(this);
    getVAdjustable().addAdjustmentListener(this);
    
    super.setSize(width, height);
    addComponentListener(this);
    add("Center", ic);
  }
  
  public void addViewportListener(ViewportListener l)
  {
    viewportListeners.addElement(l);
    l.setViewport(getXOrigin(), getYOrigin(), panelWidth, panelHeight);
  }
  

  public void removeViewportListener(ViewportListener l)
  {
    viewportListeners.removeElement(l);
  }
  
  private void notifyViewportListeners(int x, int y, int w, int h)
  {
    int numListeners = viewportListeners.size();
    for (int i = 0; i < numListeners; i++) {
      ViewportListener l = (ViewportListener)viewportListeners.elementAt(i);
      
      l.setViewport(x, y, w, h);
    }
  }
  





  public ImageCanvas getImageCanvas()
  {
    return ic;
  }
  
  public int getXOrigin()
  {
    return ic.getXOrigin();
  }
  
  public int getYOrigin()
  {
    return ic.getYOrigin();
  }
  



  public void setOrigin(int x, int y)
  {
    ic.setOrigin(x, y);
    notifyViewportListeners(x, y, panelWidth, panelHeight);
  }
  





  public synchronized void setCenter(int x, int y)
  {
    int sx = 0;
    int sy = 0;
    

    int iw = im.getWidth();
    int ih = im.getHeight();
    int vw = getViewportSizewidth;
    int vh = getViewportSizeheight;
    

    int fx = getHAdjustable().getBlockIncrement();
    int fy = getVAdjustable().getBlockIncrement();
    
    if (x < vw - iw / 2) {
      sx = 0;
    } else if (x > iw / 2) {
      sx = iw - vw;
    } else {
      sx = x + (iw - vw - fx) / 2;
    }
    
    if (y < vh - ih / 2) {
      sy = 0;
    } else if (y > ih / 2) {
      sy = ih - vh;
    } else {
      sy = y + (ih - vh - fy) / 2;
    }
    
    getHAdjustable().setValue(sx);
    getVAdjustable().setValue(sy);
    
    notifyViewportListeners(getXOrigin(), getYOrigin(), panelWidth, panelHeight);
  }
  
  public void set(RenderedImage im)
  {
    this.im = im;
    ic.set(im);
  }
  
  public int getXCenter()
  {
    return getXOrigin() + panelWidth / 2;
  }
  
  public int getYCenter()
  {
    return getYOrigin() + panelHeight / 2;
  }
  
  public Dimension getPreferredSize()
  {
    return new Dimension(panelWidth, panelHeight);
  }
  




  public void setBounds(int x, int y, int width, int height)
  {
    super.setBounds(x, y, width, height);
    
    int vpw = getViewportSizewidth;
    int vph = getViewportSizeheight;
    int imw = im.getWidth();
    int imh = im.getHeight();
    
    if ((vpw >= imw) && (vph >= imh)) {
      ic.setBounds(x, y, width, height);







    }
    else
    {







      ic.setBounds(x, y, vpw, vph);
    }
    
    panelWidth = width;
    panelHeight = height;
  }
  


  public void adjustmentValueChanged(AdjustmentEvent e) {}
  


  public void componentResized(ComponentEvent e)
  {
    notifyViewportListeners(getXOrigin(), getYOrigin(), panelWidth, panelHeight);
  }
  





  public void componentHidden(ComponentEvent e) {}
  





  public void componentMoved(ComponentEvent e) {}
  




  protected boolean beingDragged = false;
  
  protected Cursor defaultCursor = null;
  
  public void componentShown(ComponentEvent e) {}
  
  private synchronized void startDrag(Point p) { setCursor(Cursor.getPredefinedCursor(13));
    beingDragged = true;
    moveSource = p;
  }
  
  protected synchronized void updateDrag(Point moveTarget)
  {
    if (beingDragged) {
      int dx = moveSource.x - x;
      int dy = moveSource.y - y;
      moveSource = moveTarget;
      
      int x = getHAdjustable().getValue() + dx;
      int y = getVAdjustable().getValue() + dy;
      setOrigin(x, y);
    }
  }
  
  private synchronized void endDrag()
  {
    setCursor(Cursor.getPredefinedCursor(0));
    beingDragged = false;
  }
  
  public void mousePressed(MouseEvent me)
  {
    startDrag(me.getPoint());
  }
  
  public void mouseDragged(MouseEvent me)
  {
    updateDrag(me.getPoint());
  }
  
  public void mouseReleased(MouseEvent me)
  {
    endDrag();
  }
  
  public void mouseExited(MouseEvent me)
  {
    endDrag();
  }
  
  public void mouseClicked(MouseEvent me) {}
  
  public void mouseMoved(MouseEvent me) {}
  
  public void mouseEntered(MouseEvent me) {}
}
