package com.sun.activation.viewers;

import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextComponent;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.activation.DataHandler;

public class TextEditor extends Panel implements javax.activation.CommandObject, java.awt.event.ActionListener
{
  private TextArea text_area = null;
  private GridBagLayout panel_gb = null;
  private Panel button_panel = null;
  private Button save_button = null;
  
  private java.io.File text_file = null;
  private String text_buffer = null;
  private InputStream data_ins = null;
  private FileInputStream fis = null;
  
  private DataHandler _dh = null;
  private boolean DEBUG = false;
  

  public TextEditor()
  {
    panel_gb = new GridBagLayout();
    setLayout(panel_gb);
    
    button_panel = new Panel();
    
    button_panel.setLayout(new java.awt.FlowLayout());
    save_button = new Button("SAVE");
    button_panel.add(save_button);
    addGridComponent(this, 
      button_panel, 
      panel_gb, 
      0, 0, 
      1, 1, 
      1, 0);
    

    text_area = new TextArea("This is text", 24, 80, 
      1);
    
    text_area.setEditable(true);
    
    addGridComponent(this, 
      text_area, 
      panel_gb, 
      0, 1, 
      1, 2, 
      1, 1);
    

    save_button.addActionListener(this);
  }
  












  private void addGridComponent(Container paramContainer, Component paramComponent, GridBagLayout paramGridBagLayout, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    GridBagConstraints localGridBagConstraints = new GridBagConstraints();
    gridx = paramInt1;
    gridy = paramInt2;
    gridwidth = paramInt3;
    gridheight = paramInt4;
    fill = 1;
    weighty = paramInt6;
    weightx = paramInt5;
    anchor = 10;
    paramGridBagLayout.setConstraints(paramComponent, localGridBagConstraints);
    paramContainer.add(paramComponent);
  }
  
  public void setCommandContext(String paramString, DataHandler paramDataHandler) throws IOException
  {
    _dh = paramDataHandler;
    setInputStream(_dh.getInputStream());
  }
  





  public void setInputStream(InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte['Ѐ'];
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int i = 0;
    

    while ((i = paramInputStream.read(arrayOfByte)) > 0)
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    paramInputStream.close();
    



    text_buffer = localByteArrayOutputStream.toString();
    

    text_area.setText(text_buffer);
  }
  
  private void performSaveOperation() {
    OutputStream localOutputStream = null;
    try {
      localOutputStream = _dh.getOutputStream();
    }
    catch (Exception localException) {}
    String str = text_area.getText();
    

    if (localOutputStream == null) {
      System.out.println("Invalid outputstream in TextEditor!");
      System.out.println("not saving!");
    }
    try
    {
      localOutputStream.write(str.getBytes());
      localOutputStream.flush();
      localOutputStream.close();
    }
    catch (IOException localIOException) {
      System.out.println("TextEditor Save Operation failed with: " + localIOException);
    }
  }
  
  public void addNotify()
  {
    super.addNotify();
    invalidate();
  }
  
  public java.awt.Dimension getPreferredSize() {
    return text_area.getMinimumSize(24, 80);
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    if (paramActionEvent.getSource() == save_button)
    {

      performSaveOperation();
    }
  }
}
