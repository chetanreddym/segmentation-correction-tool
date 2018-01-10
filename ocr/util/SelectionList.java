package ocr.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;













public class SelectionList
  extends JDialog
  implements ActionListener
{
  public final int CANCEL_SELECTED = 0;
  public final int OK_SELECTED = 1;
  public final int NONE_SELECTED = 2;
  
  int action = 2;
  
  DefaultDecisionPanel defaultDecisionPanel;
  
  JList selectionList;
  
  JScrollPane listScrollPane;
  

  public class CheckBoxRenderer
    extends JLabel
    implements ListCellRenderer
  {
    public CheckBoxRenderer() {}
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
      DefaultListModel model = (DefaultListModel)list.getModel();
      JCheckBox b = (JCheckBox)model.get(index);
      


      if (isSelected)
      {
        setForeground(list.getSelectionForeground());
        setBackground(list.getSelectionBackground());
      }
      else
      {
        setForeground(list.getForeground());
        setBackground(list.getBackground());
      }
      setText(b.getText());
      return this;
    }
  }
  

















  public SelectionList(JPanel topPanel, Object[] data, JFrame owner)
  {
    super(owner, true);
    if (data == null) {
      return;
    }
    defaultDecisionPanel = new DefaultDecisionPanel("OK", "Cancel", topPanel);
    defaultDecisionPanel.addActionListenerToOkButton(this);
    defaultDecisionPanel.addActionListenerToCancelButton(this);
    
    DefaultListModel model = new DefaultListModel();
    for (int i = 0; i < data.length; i++)
    {
      model.addElement(new JCheckBox(data[i].toString()));
    }
    
    selectionList = new JList(model);
    CheckBoxRenderer checkBoxRenderer = new CheckBoxRenderer();
    
    selectionList.setCellRenderer(checkBoxRenderer);
    
    Container c = getContentPane();
    c.setLayout(new BoxLayout(c, 1));
    
    c.add(defaultDecisionPanel);
    

    listScrollPane = new JScrollPane(selectionList);
    listScrollPane.setPreferredSize(new Dimension(200, 100));
    

    selectionList.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e)
      {
        JList list = (JList)e.getSource();
        
        int index = list.locationToIndex(e.getPoint());
        DefaultListModel model = (DefaultListModel)list.getModel();
        JCheckBox b = (JCheckBox)model.get(index);
        b.setSelected(!b.isSelected());
        




        list.repaint();
      }
      
    });
    JPanel bottomPanel = new JPanel();
    bottomPanel.add(listScrollPane);
    c.add(bottomPanel);
    setLocationRelativeTo(null);
    


    pack();
    setVisible(true);
  }
  





  public int getUserSelectedAction()
  {
    return action;
  }
  



  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource().equals(defaultDecisionPanel.getOkButton()))
    {
      action = 1;
      setVisible(false);
    }
    else if (e.getSource().equals(defaultDecisionPanel.getCancelButton()))
    {
      action = 0;
      setVisible(false);
    }
  }
}
