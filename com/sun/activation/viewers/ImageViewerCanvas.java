package com.sun.activation.viewers;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;






public class ImageViewerCanvas
  extends Canvas
{
  private Image canvas_image = null;
  











  public void setImage(Image paramImage)
  {
    canvas_image = paramImage;
    invalidate();
    repaint();
  }
  



  public Dimension getPreferredSize()
  {
    Dimension localDimension = null;
    
    if (canvas_image == null)
    {
      localDimension = new Dimension(200, 200);
    }
    else {
      localDimension = new Dimension(canvas_image.getWidth(this), 
        canvas_image.getHeight(this));
    }
    return localDimension;
  }
  



  public void paint(Graphics paramGraphics)
  {
    if (canvas_image != null) {
      paramGraphics.drawImage(canvas_image, 0, 0, this);
    }
  }
  
  public ImageViewerCanvas() {}
}
