package ocr.util.comments;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import ocr.manager.GlobalHotkeyManager;

















public class CommentsDialog
  extends JToolBar
{
  private static final long serialVersionUID = 1L;
  private JPanel centralPanel = null;
  private JPanel mainPanel = null;
  private JPanel topPanel = null;
  private JScrollPane tableScrollPane = null;
  private CommentsTable table = null;
  private JPanel bottomPanel = null;
  private JLabel currDataLabel = null;
  private JScrollPane textScrollPane = null;
  private JTextArea textArea = null;
  
  private Vector<GEDIComment> commentsToShow;
  private CommentsParser commParser;
  private JCheckBox showAllCheckBox = null;
  private JPanel tipPanel = null;
  private JLabel totalLabel = null;
  private JLabel tipLabel = null;
  private JCheckBox showArchivedCheckBox = null;
  private JLabel commentsForLabel = null;
  private JLabel commentsFoundLabel = null;
  private JPanel saveInfoPanel = null;
  private JPanel savePanel = null;
  private JRadioButton enterRBtn = null;
  private JRadioButton ctrlEnterRBtn = null;
  private JPanel checkBoxesPanel = null;
  private JCheckBox sortAbsoluteCheckBox = null;
  

  private int sortCol;
  
  private boolean isAscending;
  

  public CommentsDialog(Vector<GEDIComment> allComments, CommentsParser commParser)
  {
    commentsToShow = allComments;
    this.commParser = commParser;
    initialize();
    table.selectRowsForCurrDoc();
    updateLabels();
  }
  


  private void initialize()
  {
    setPreferredSize(new Dimension(940, 594));
    setSize(940, 594);
    setMinimumSize(new Dimension(0, 0));
    add(getCentralPane());
    setFloatable(true);
  }
  





  private JPanel getCentralPane()
  {
    if (centralPanel == null) {
      BorderLayout borderLayout4 = new BorderLayout();
      borderLayout4.setVgap(0);
      centralPanel = new JPanel();
      centralPanel.setLayout(borderLayout4);
      centralPanel.add(getMainPanel(), "Center");
      centralPanel.add(getBottomPanel(), "South");
    }
    return centralPanel;
  }
  




  private JPanel getMainPanel()
  {
    if (mainPanel == null) {
      BorderLayout borderLayout2 = new BorderLayout();
      borderLayout2.setHgap(40);
      borderLayout2.setVgap(3);
      mainPanel = new JPanel();
      mainPanel.setLayout(borderLayout2);
      mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
      mainPanel.add(getTableScrollPane(), "Center");
      mainPanel.add(getTopPanel(), "North");
      mainPanel.add(getTipPanel(), "South");
    }
    return mainPanel;
  }
  





  private JPanel getTopPanel()
  {
    if (topPanel == null) {
      commentsFoundLabel = new JLabel();
      commentsFoundLabel.setFont(new Font("Arial", 1, 12));
      commentsFoundLabel.setPreferredSize(new Dimension(309, 45));
      commentsFoundLabel.setHorizontalAlignment(2);
      commentsForLabel = new JLabel();
      commentsForLabel.setText(" Comments for: ");
      BorderLayout borderLayout1 = new BorderLayout();
      borderLayout1.setHgap(0);
      borderLayout1.setVgap(0);
      topPanel = new JPanel();
      topPanel.setLayout(borderLayout1);
      topPanel.add(getCheckBoxesPanel(), "North");
      topPanel.add(commentsForLabel, "South");
    }
    return topPanel;
  }
  




  private JScrollPane getTableScrollPane()
  {
    if (tableScrollPane == null) {
      tableScrollPane = new JScrollPane();
      tableScrollPane.setViewportView(getTable());
    }
    return tableScrollPane;
  }
  




  private CommentsTable getTable()
  {
    if (table == null) {
      boolean showArchived = showArchivedCheckBox == null ? false : showArchivedCheckBox.isSelected();
      table = new CommentsTable(commentsToShow, showArchived);
      table.getModel().setSortCol(sortCol);
      table.getModel().setSortAscending(isAscending);
      addTableModelListener();
      addColumnListener();
    }
    return table;
  }
  




  private JPanel getBottomPanel()
  {
    if (bottomPanel == null) {
      BorderLayout borderLayout = new BorderLayout();
      borderLayout.setVgap(2);
      borderLayout.setHgap(0);
      bottomPanel = new JPanel();
      bottomPanel.setPreferredSize(new Dimension(80, 55));
      bottomPanel.setLayout(borderLayout);
      bottomPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10), BorderFactory.createEtchedBorder(1)));
      bottomPanel.add(getTextScrollPane(), "Center");
      bottomPanel.add(getSaveInfoPanel(), "North");
    }
    return bottomPanel;
  }
  






  private JScrollPane getTextScrollPane()
  {
    if (textScrollPane == null) {
      textScrollPane = new JScrollPane();
      textScrollPane.setPreferredSize(new Dimension(3, 30));
      textScrollPane.setViewportView(getTextArea());
    }
    return textScrollPane;
  }
  




  private JTextArea getTextArea()
  {
    if (textArea == null) {
      textArea = new JTextArea();
      textArea.setWrapStyleWord(true);
      




      textArea.addAncestorListener(new AncestorListener() {
        public void ancestorAdded(AncestorEvent ae) {
          ae.getComponent().requestFocus();
        }
        
        public void ancestorRemoved(AncestorEvent ae) {}
        
        public void ancestorMoved(AncestorEvent ae) {}
      });
    }
    return textArea;
  }
  








  public void setGlobalHotkeyManagerEnabled(boolean b)
  {
    GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
    hotkeyManager.setEnabled(b);
  }
  



  private void addNewComment()
  {
    if (textArea.getText().trim().isEmpty()) {
      return;
    }
    commParser.insertComment(textArea.getText().trim());
    

    update(true);
  }
  




  private void updateTable(Vector<GEDIComment> comments)
  {
    sortCol = table.getModel().getSortCol();
    isAscending = table.getModel().isSortAscending();
    commentsToShow = comments;
    textArea.setText("");
    table = null;
    tableScrollPane.setViewportView(getTable());
  }
  


  private void sortTable()
  {
    if (sortAbsoluteCheckBox.isSelected()) {
      CommentsTable tmp29_26 = table;tmp29_26.getClass();table.sortAllRowsBy(table.getModel(), new CommentsTable.AbsoluteComparator(tmp29_26, sortCol, isAscending));
    } else {
      CommentsTable tmp70_67 = table;tmp70_67.getClass();table.sortAllRowsBy(table.getModel(), new CommentsTable.AdvancedComparator(tmp70_67, sortCol, isAscending));
    }
    table.selectRowsForCurrDoc();
  }
  



  private void addColumnListener()
  {
    JTableHeader header = table.getTableHeader();
    header.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        TableColumnModel colModel = table.getColumnModel();
        int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
        int modelIndex = colModel.getColumn(columnModelIndex).getModelIndex();
        
        if (modelIndex < 0)
          return;
        if (sortCol == modelIndex) {
          isAscending = (!isAscending);
        } else {
          sortCol = modelIndex;
        }
        table.getModel().setSortCol(sortCol);
        table.getModel().setSortAscending(isAscending);
        
        CommentsDialog.this.sortTable();
        
        table.getTableHeader().repaint();
      }
    });
  }
  







  private synchronized void addTableModelListener()
  {
    table.getModel().addTableModelListener(new TableModelListener() {
      public synchronized void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int realRowIndex = table.convertRowIndexToModel(row);
        
        if ((e.getType() == -1) || (realRowIndex == -1)) {
          return;
        }
        
        TableModel model = (TableModel)e.getSource();
        GEDIComment commentObj = (GEDIComment)model.getValueAt(realRowIndex, 0);
        Boolean archived = (Boolean)model.getValueAt(realRowIndex, 6);
        commentObj.setArchived(Boolean.toString(archived.booleanValue()));
        commParser.setArchived(commentObj);
        
        if ((!showArchivedCheckBox.isSelected()) && (e.getType() == 0)) {
          table.getModel().removeRow(realRowIndex);
          table.revalidate();
          table.repaint();
          table.setTotalHidden(table.getTotalHidden() + 1);
        }
        
        update(false);
        
        boolean newCommentsFound = false;
        for (int i = 0; i < table.getRowCount(); i++) {
          GEDIComment c = (GEDIComment)model.getValueAt(i, 0);
          if (!c.isArchived()) {
            newCommentsFound = true;
            break;
          }
        }
        
        commParser.setHasNewComments(newCommentsFound);
      }
    });
  }
  





  private JCheckBox getShowAllCheckBox()
  {
    if (showAllCheckBox == null) {
      showAllCheckBox = new JCheckBox();
      showAllCheckBox.setText("Show all   ");
      showAllCheckBox.setHorizontalAlignment(2);
      showAllCheckBox.setHorizontalTextPosition(11);
      
      showAllCheckBox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == 1) {
            sortAbsoluteCheckBox.setEnabled(true);
          } else {
            sortAbsoluteCheckBox.setEnabled(false);
            sortAbsoluteCheckBox.setSelected(true);
          }
          update(true);
        }
      });
    }
    return showAllCheckBox;
  }
  
  public JPanel getCentralPanel() {
    return centralPanel;
  }
  




  private JPanel getTipPanel()
  {
    if (tipPanel == null) {
      tipLabel = new JLabel();
      tipLabel.setText("<html>Tip: to view entire Comment, move mouse over or click right mouse button on it<br>Tip: comment in <font color=red>red</font> is the newest one among selected");
      
      totalLabel = new JLabel();
      totalLabel.setText("Total: ");
      BorderLayout borderLayout3 = new BorderLayout();
      borderLayout3.setHgap(10);
      borderLayout3.setVgap(10);
      tipPanel = new JPanel();
      tipPanel.setPreferredSize(new Dimension(100, 32));
      tipPanel.setLayout(borderLayout3);
      tipPanel.add(totalLabel, "West");
      tipPanel.add(tipLabel, "East");
    }
    return tipPanel;
  }
  




  private JCheckBox getShowArchivedCheckBox()
  {
    if (showArchivedCheckBox == null) {
      showArchivedCheckBox = new JCheckBox();
      showArchivedCheckBox.setText("Show archived   ");
      showArchivedCheckBox.setHorizontalAlignment(4);
      showArchivedCheckBox.setHorizontalTextPosition(11);
      
      showArchivedCheckBox.setEnabled(true);
      
      showArchivedCheckBox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          update(true);
        }
      });
    }
    
    return showArchivedCheckBox;
  }
  
  public boolean isHideArchived() {
    return showArchivedCheckBox.isSelected();
  }
  









  public void update(boolean reloadTable)
  {
    if ((reloadTable) && (showAllCheckBox.isSelected())) {
      updateTable(commParser.getAllComments());
    } else if ((reloadTable) && (!showAllCheckBox.isSelected())) {
      updateTable(commParser.getCommentsForCurrDoc());
    }
    if (reloadTable) {
      sortTable();
    }
    table.selectRowsForCurrDoc();
    
    if (textArea != null) {
      textArea.requestFocus();
    }
    updateLabels();
  }
  


  public void updateLabels()
  {
    setTotalLabel();
    setNoCommentsLabel();
    setSaveCommCombination();
    setCommentsForLabel();
    setCurrDataLabel();
  }
  
  public CommentsTable getCurrTable() {
    return table;
  }
  
  public boolean isShowAll() {
    return showAllCheckBox.isSelected();
  }
  
  public void setCurrDataLabel() {
    String str = CommentsParser.getCurrXml() + 
      "&nbsp;&nbsp;&nbsp;PAGE: " + CommentsParser.getCurrPageNum();
    
    if (CommentsParser.getCurrZoneID() != null) {
      str = str + "&nbsp;&nbsp;&nbsp;ZONE: " + CommentsParser.getCurrZoneID();
    }
    
    currDataLabel.setText("<html>Add new comment to:&nbsp;&nbsp;&nbsp;<font color=blue>" + str + "</font></html>");
    
    if (textArea != null)
      textArea.requestFocus();
  }
  
  public void setNoCommentsLabel() {
    if (commentsFoundLabel == null) {
      return;
    }
    if ((table.getRowCount() == 0) || (table.getSelectedRowCount() == 0)) {
      String str = " ZONE " + CommentsParser.getCurrZoneID();
      if (CommentsParser.getCurrZoneID() == null)
        str = " PAGE " + CommentsParser.getCurrPageNum();
      commentsFoundLabel.setText("<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;NO COMMENTS FOUND FOR&nbsp;<font color=red>" + 
        str + 
        "</font>&nbsp;&nbsp;(or all comments are hidden/archived)");
      commentsFoundLabel.setVisible(true);
    }
    else {
      String str = " ZONE " + CommentsParser.getCurrZoneID();
      if (CommentsParser.getCurrZoneID() == null)
        str = " PAGE " + CommentsParser.getCurrPageNum();
      commentsFoundLabel.setText("<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + 
        table.getSelectedRowCount() + " COMMENTS FOUND FOR&nbsp;<font color=red>" + str + 
        "</font>");
      commentsFoundLabel.setVisible(true);
    }
  }
  
  public void setTotalLabel() {
    if (totalLabel == null)
      return;
    String str = "<br> Selected:&nbsp;&nbsp;" + table.getSelectedRowCount() + "</html>";
    
    totalLabel.setText("<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Total:&nbsp;&nbsp;" + 
      table.getRowCount() + " shown, " + table.getTotalHidden() + " hidden" + str);
  }
  
  public void setCommentsForLabel() {
    if (commentsForLabel == null) {
      return;
    }
    String txt = "";
    
    if (showAllCheckBox.isSelected()) {
      txt = "Comments for:  all in the directory";
    } else {
      txt = "Comments for:  " + CommentsParser.getCurrXml();
    }
    commentsForLabel.setText(txt);
  }
  




  private JPanel getSaveInfoPanel()
  {
    if (saveInfoPanel == null) {
      saveInfoPanel = new JPanel();
      currDataLabel = new JLabel();
      currDataLabel.setText("Comments to:");
      saveInfoPanel.setLayout(new BorderLayout());
      saveInfoPanel.setPreferredSize(new Dimension(0, 20));
      saveInfoPanel.add(currDataLabel, "Center");
      saveInfoPanel.add(getSavePanel(), "East");
    }
    return saveInfoPanel;
  }
  




  private JPanel getSavePanel()
  {
    if (savePanel == null) {
      BorderLayout borderLayout5 = new BorderLayout();
      borderLayout5.setHgap(5);
      savePanel = new JPanel();
      savePanel.setLayout(borderLayout5);
      savePanel.add(getEnterRBtn(), "West");
      savePanel.add(getCtrlEnterRBtn(), "East");
      ButtonGroup group = new ButtonGroup();
      group.add(getEnterRBtn());
      group.add(getCtrlEnterRBtn());
    }
    return savePanel;
  }
  




  private JRadioButton getEnterRBtn()
  {
    if (enterRBtn == null) {
      enterRBtn = new JRadioButton();
      enterRBtn.setText("Enter to save");
      enterRBtn.setSelected(true);
      enterRBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CommentsDialog.this.setSaveCommCombination();
        }
      });
    }
    return enterRBtn;
  }
  




  private JRadioButton getCtrlEnterRBtn()
  {
    if (ctrlEnterRBtn == null) {
      ctrlEnterRBtn = new JRadioButton();
      ctrlEnterRBtn.setText("CTRL+Enter to save");
      ctrlEnterRBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CommentsDialog.this.setSaveCommCombination();
        }
      });
    }
    return ctrlEnterRBtn;
  }
  


  private void setSaveCommCombination()
  {
    if (textArea != null)
      textArea.requestFocus();
    textArea.getInputMap().remove(KeyStroke.getKeyStroke(10, 0));
    textArea.getInputMap().remove(KeyStroke.getKeyStroke(10, 2));
    if (enterRBtn.isSelected()) {
      textArea.getInputMap().put(KeyStroke.getKeyStroke(10, 0), "enter_save");
      textArea.getActionMap().put("enter_save", new AbstractAction() {
        private static final long serialVersionUID = 1L;
        
        public void actionPerformed(ActionEvent e) { CommentsDialog.this.addNewComment(); }
      });
    }
    else
    {
      textArea.getInputMap().put(KeyStroke.getKeyStroke(10, 
        2), "ctrl__enter_save");
      textArea.getActionMap().put("ctrl__enter_save", new AbstractAction() {
        private static final long serialVersionUID = 1L;
        
        public void actionPerformed(ActionEvent e) { CommentsDialog.this.addNewComment(); }
      });
    }
  }
  





  private JPanel getCheckBoxesPanel()
  {
    if (checkBoxesPanel == null) {
      checkBoxesPanel = new JPanel();
      checkBoxesPanel.setLayout(new BoxLayout(getCheckBoxesPanel(), 0));
      checkBoxesPanel.setPreferredSize(new Dimension(0, 30));
      checkBoxesPanel.add(getShowAllCheckBox(), null);
      checkBoxesPanel.add(getShowArchivedCheckBox(), null);
      checkBoxesPanel.add(getSortAbsoluteCheckBox(), null);
      checkBoxesPanel.add(commentsFoundLabel, null);
    }
    return checkBoxesPanel;
  }
  




  private JCheckBox getSortAbsoluteCheckBox()
  {
    if (sortAbsoluteCheckBox == null) {
      sortAbsoluteCheckBox = new JCheckBox();
      sortAbsoluteCheckBox.setText("Sorting absolute   ");
      sortAbsoluteCheckBox.setHorizontalTextPosition(11);
      sortAbsoluteCheckBox.setSelected(true);
      sortAbsoluteCheckBox.setEnabled(false);
      sortAbsoluteCheckBox.setHorizontalAlignment(0);
      sortAbsoluteCheckBox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == 2) {
            int col = sortCol;
            sortCol = 0;
            CommentsDialog.this.sortTable();
            sortCol = col;
          }
          CommentsDialog.this.sortTable();
        }
      });
    }
    return sortAbsoluteCheckBox;
  }
}
