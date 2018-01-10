package com.sun.activation.viewers;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextComponent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.CommandObject;
import javax.activation.DataHandler;

public class TextViewer
  extends Panel
  implements CommandObject
{
  private TextArea text_area = null;
  

  private File text_file = null;
  private String text_buffer = null;
  
  private DataHandler _dh = null;
  private boolean DEBUG = false;
  

  public TextViewer()
  {
    setLayout(new GridLayout(1, 1));
    
    text_area = new TextArea("", 24, 80, 
      1);
    text_area.setEditable(false);
    
    add(text_area);
  }
  
  public void setCommandContext(String paramString, DataHandler paramDataHandler) throws IOException
  {
    _dh = paramDataHandler;
    setInputStream(_dh.getInputStream());
  }
  




  public void setInputStream(InputStream paramInputStream)
    throws IOException
  {
    int i = 0;
    
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte = new byte['Ѐ'];
    
    while ((i = paramInputStream.read(arrayOfByte)) > 0) {
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    }
    paramInputStream.close();
    


    text_buffer = localByteArrayOutputStream.toString();
    

    text_area.setText(text_buffer);
  }
  
  public void addNotify()
  {
    super.addNotify();
    invalidate();
  }
  
  public Dimension getPreferredSize() {
    return text_area.getMinimumSize(24, 80);
  }
}
