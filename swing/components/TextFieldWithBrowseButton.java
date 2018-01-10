package swing.components;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileFilter;












public class TextFieldWithBrowseButton
  extends JPanel
{
  JTextField field;
  JButton btn;
  
  public TextFieldWithBrowseButton(String fieldTxt, String browseBtnLabel, final FileFilter fileFilter)
  {
    field = new JTextField(fieldTxt);
    field.setColumns(20);
    btn = new JButton(browseBtnLabel);
    
    ActionListener action = new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser chooser = new JFileChooser("C:");
        chooser.setFileFilter(fileFilter);
        
        int selection = chooser.showDialog(TextFieldWithBrowseButton.this, "Select");
        
        if (selection != 1) {
          field.setText(chooser.getSelectedFile().getAbsolutePath());
        }
        
      }
      
    };
    btn.addActionListener(action);
    setLayout(new FlowLayout(3));
    add(field);
    add(btn);
  }
  





  public TextFieldWithBrowseButton(String fieldTxt, String browseBtnLabel, final FileFilter fileFilter, CaretListener caretListener)
  {
    field = new JTextField(fieldTxt);
    field.setColumns(20);
    btn = new JButton(browseBtnLabel);
    
    ActionListener action = new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser chooser = new JFileChooser("C:");
        chooser.setFileFilter(fileFilter);
        
        int selection = chooser.showDialog(TextFieldWithBrowseButton.this, "Select");
        
        if (selection != 1) {
          field.setText(chooser.getSelectedFile().getAbsolutePath());
        }
        
      }
      
    };
    btn.addActionListener(action);
    field.addCaretListener(caretListener);
    setLayout(new FlowLayout(3));
    add(field);
    add(btn);
  }
  



  public String getFieldText()
  {
    return field.getText();
  }
  
  public void setText(String txt) { field.setText(txt); }
}
