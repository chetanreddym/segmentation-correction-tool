package ocr.gui.fileDrop;

import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class FileDropExample
{
  public FileDropExample() {}
  
  public static void main(String[] args)
  {
    JFrame frame = new JFrame("FileDrop");
    
    JTextArea text = new JTextArea();
    frame.getContentPane().add(
      new javax.swing.JScrollPane(text), 
      "Center");
    
    new FileDrop(System.out, text, new FileDropListener() {
      public void filesDropped(File[] files) {
        for (int i = 0; i < files.length; i++) {
          try {
            append(files[i].getCanonicalPath() + "\n");

          }
          catch (IOException localIOException) {}
        }
      }
    });
    frame.setBounds(100, 100, 300, 400);
    frame.setDefaultCloseOperation(3);
    frame.setVisible(true);
  }
}
