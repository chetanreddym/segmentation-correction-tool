package com.lotontech.talk;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

public class LOTONtalkApplet extends Applet implements java.awt.event.ActionListener
{
  private String speech;
  private LOTONtalk speaker = new LOTONtalk(this);
  
  public void init()
  {
    setLayout(new java.awt.GridLayout(1, 1));
    
    speech = getParameter("speech");
    String str1 = getParameter("buttonText");
    String str2 = getParameter("buttonBackground");
    String str3 = getParameter("buttonForeground");
    String str4 = getParameter("buttonFontSize");
    
    if ((speech != null) && (str1 != null))
    {
      Button localButton = new Button(str1);
      int i;
      int j; int k; StringTokenizer localStringTokenizer; if (str2 != null)
      {
        i = 0;
        j = 0;
        k = 0;
        
        localStringTokenizer = new StringTokenizer(str2, ",");
        if (localStringTokenizer.hasMoreTokens()) i = new Integer(localStringTokenizer.nextToken()).intValue();
        if (localStringTokenizer.hasMoreTokens()) j = new Integer(localStringTokenizer.nextToken()).intValue();
        if (localStringTokenizer.hasMoreTokens()) { k = new Integer(localStringTokenizer.nextToken()).intValue();
        }
        localButton.setBackground(new Color(i, j, k));
      }
      
      if (str3 != null)
      {
        i = 0;
        j = 0;
        k = 0;
        
        localStringTokenizer = new StringTokenizer(str3, ",");
        if (localStringTokenizer.hasMoreTokens()) i = new Integer(localStringTokenizer.nextToken()).intValue();
        if (localStringTokenizer.hasMoreTokens()) j = new Integer(localStringTokenizer.nextToken()).intValue();
        if (localStringTokenizer.hasMoreTokens()) { k = new Integer(localStringTokenizer.nextToken()).intValue();
        }
        localButton.setForeground(new Color(i, j, k));
      }
      
      if (str4 != null)
      {
        i = new Integer(str4).intValue();
        Font localFont = new Font("Roman", 1, i);
        localButton.setFont(localFont);
      }
      
      localButton.addActionListener(this);
      add(localButton);
    }
    
    if ((speech != null) && (str1 == null)) speaker.sayPhoneWord(speech, false);
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    speaker.sayPhoneWord(speech, false);
  }
  
  public LOTONtalkApplet() {}
}
