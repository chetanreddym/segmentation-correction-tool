package ocr.tag;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import javax.swing.JTextField;

























class ShowString
  extends Frame
{
  FontMetrics fontM;
  String outString;
  
  ShowString(String target, String title)
  {
    setTitle(title);
    outString = target;
    

    JTextField jt = new JTextField();
    Font font = new Font("Arial Unicode MS", 0, 24);
    jt.setFont(font);
    jt.setText(target);
    add(jt);
    fontM = getFontMetrics(font);
    setFont(font);
    
    int size = 0;
    for (int i = 0; i < outString.length(); i++) {
      size += fontM.charWidth(outString.charAt(i));
    }
    size += 24;
    
    setSize(size, fontM.getHeight() + 60);
    setLocation(getSizewidth / 2, getSizeheight / 2);
    setVisible(true);
  }
}
