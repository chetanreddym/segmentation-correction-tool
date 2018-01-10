package com.sun.activation.viewers;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import javax.activation.CommandObject;
import javax.activation.DataHandler;

public class ImageViewer
  extends Panel implements CommandObject
{
  private ImageViewerCanvas canvas = null;
  


  private Image image = null;
  private DataHandler _dh = null;
  
  private boolean DEBUG = false;
  



  public ImageViewer()
  {
    canvas = new ImageViewerCanvas();
    add(canvas);
  }
  

  public void setCommandContext(String paramString, DataHandler paramDataHandler)
    throws IOException
  {
    _dh = paramDataHandler;
    setInputStream(_dh.getInputStream());
  }
  



  private void setInputStream(InputStream paramInputStream)
    throws IOException
  {
    MediaTracker localMediaTracker = new MediaTracker(this);
    int i = 0;
    byte[] arrayOfByte = new byte['Ѐ'];
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    
    while ((i = paramInputStream.read(arrayOfByte)) > 0)
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    paramInputStream.close();
    

    image = getToolkit().createImage(localByteArrayOutputStream.toByteArray());
    
    localMediaTracker.addImage(image, 0);
    try
    {
      localMediaTracker.waitForID(0);
      localMediaTracker.waitForAll();
      if (localMediaTracker.statusID(0, true) != 8) {
        System.out.println("Error occured in image loading = " + 
          localMediaTracker.getErrorsID(0));
      }
      
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new IOException("Error reading image data");
    }
    
    canvas.setImage(image);
    if (DEBUG) {
      System.out.println("calling invalidate");
    }
  }
  
  public void addNotify() {
    super.addNotify();
    invalidate();
    validate();
    doLayout();
  }
  
  public Dimension getPreferredSize() {
    return canvas.getPreferredSize();
  }
}
