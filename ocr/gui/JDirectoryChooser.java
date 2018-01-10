package ocr.gui;

import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.TreePath;

public class JDirectoryChooser extends javax.swing.JComponent
{
  static final long serialVersionUID = 78322893L;
  public static boolean cancel_selected = false;
  


  public JDirectoryChooser()
  {
    this(FileSystemView.getFileSystemView());
  }
  



  public JDirectoryChooser(File currentDirectory)
  {
    this();
    if (currentDirectory.exists()) { setCurrentDirectory(currentDirectory);
    }
  }
  


  public JDirectoryChooser(String currentDirectory)
  {
    this();
    if (currentDirectory != null) { setCurrentDirectory(new File(currentDirectory));
    }
    
    File[] view_roots = foo.getFileSystemView().getRoots();
    System.out.println();
    System.out.println("CONSTRUCTOR ::::::: currentDirectory -> " + currentDirectory + " -- totalRoots -> " + 
      view_roots.length);
    System.out.println("foo.getname() -> " + foo.getName());
    for (int i = 0; i < view_roots.length; i++)
      System.out.println("view_roots[" + i + "]:  " + view_roots[i].getAbsolutePath());
    System.out.println();
  }
  






  public JDirectoryChooser(File currentDirectory, FileSystemView view)
  {
    this(view);
    setCurrentDirectory(currentDirectory);
  }
  




  public JDirectoryChooser(String currentDirectory, FileSystemView view)
  {
    this(view);
    setCurrentDirectory(new File(currentDirectory));
  }
  



  public JDirectoryChooser(FileSystemView view)
  {
    setup(view);
    ButtonListener al = new ButtonListener(null);
    
    tree = new JTree();
    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    tree.setCellRenderer(new FileRenderer(null));
    tree.getSelectionModel().setSelectionMode(1);
    tree.addTreeSelectionListener(al);
    
    approve.addActionListener(al);
    cancel.addActionListener(al);
    approve.setEnabled(false);
    
    setLayout(new java.awt.BorderLayout());
    add(new javax.swing.JScrollPane(tree));
    
    javax.swing.JPanel p = new javax.swing.JPanel(new java.awt.FlowLayout());
    p.add(approve);
    p.add(cancel);
    add(p, "South");
  }
  
  public void addNotify()
  {
    tree.setModel(model);
    if (currentDirectory != null)
    {
      File dir = currentDirectory;
      FileSystemView view = foo.getFileSystemView();
      




      Vector<File> v = new Vector();
      for (;;)
      {
        System.out.println("Dir is ---------------------------------------------->  " + dir);
        if (dir == null) return;
        v.addElement(dir);
        
        if (view.isRoot(dir)) {
          break;
        }
        







        dir = new File(dir.getParent());
      }
      Object[] files = new File[v.size() + 1];
      Object node = model.getRoot();
      

      System.out.println("model.getRoot ================= >>> " + model.getRoot());
      File tmp_file = new File(node.toString());
      File[] root_list = File.listRoots();
      for (int i = 0; i < root_list.length; i++) {
        System.out.println("(" + i + ")  :  " + root_list[i].getAbsolutePath());
      }
      
      files[0] = node;
      for (int i = 1; i < files.length; i++)
      {
        File here = (File)v.elementAt(files.length - i - 1);
        int index = model.getIndexOfChild(node, here); Object 
          tmp281_278 = model.getChild(node, index);node = tmp281_278;files[i] = tmp281_278;
      }
      TreePath path = new TreePath(files);
      tree.setSelectionPath(path);
      tree.scrollPathToVisible(path);
    }
    super.addNotify();
  }
  


  public void setCurrentDirectory(File dir)
  {
    currentDirectory = dir;
  }
  


  public void setFileFilter(javax.swing.filechooser.FileFilter fileFilter)
  {
    foo.setFileFilter(fileFilter);
    model.changed();
  }
  



  public javax.swing.filechooser.FileFilter getFileFilter()
  {
    return foo.getFileFilter();
  }
  



  public void setFileHidingEnabled(boolean hide)
  {
    foo.setFileHidingEnabled(hide);
    model.changed();
  }
  




  public boolean isFileHidingEnabled()
  {
    return foo.isFileHidingEnabled();
  }
  




  public void setFileSelectionMode(int mode)
  {
    foo.setFileSelectionMode(mode);
    model.changed();
  }
  




  public int getFileSelectionMode()
  {
    return foo.getFileSelectionMode();
  }
  


  public void setMultiSelectionEnabled(boolean enable)
  {
    foo.setMultiSelectionEnabled(enable);
    tree.getSelectionModel().setSelectionMode(enable ? 4 : 
      1);
  }
  


  public boolean isMultiSelectionEnabled()
  {
    return foo.isMultiSelectionEnabled();
  }
  


  public File getSelectedFile()
  {
    return (File)tree.getLastSelectedPathComponent();
  }
  


  public File[] getSelectedFiles()
  {
    TreePath[] paths = tree.getSelectionPaths();
    File[] result = new File[paths.length];
    for (int i = 0; i < paths.length; i++) result[i] = ((File)paths[i].getLastPathComponent());
    return result;
  }
  
  protected void setup(FileSystemView view) {
    foo.setFileSystemView(view);
  }
  
  public void updateUI() {
    super.updateUI();
    foo.updateUI();
  }
  





  public int showDialog(java.awt.Component parent)
  {
    Frame frame = (parent instanceof Frame) ? (Frame)parent : 
      (Frame)javax.swing.SwingUtilities.getAncestorOfClass(Frame.class, parent);
    
    String title = null;
    
    title = foo.getDialogTitle();
    if (title == null) { foo.getUI().getDialogTitle(foo);
    }
    returnValue = 1;
    
    dialog = new JDialog(frame, title, true);
    dialog.getContentPane().add(this, "Center");
    
    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
    
    return returnValue;
  }
  


  public void setDialogTitle(String dialogTitle)
  {
    foo.setDialogTitle(dialogTitle);
  }
  


  public String getDialogTitle()
  {
    return foo.getDialogTitle();
  }
  



  public void setFileView(javax.swing.filechooser.FileView fileView)
  {
    foo.setFileView(fileView);
  }
  




  public javax.swing.filechooser.FileView getFileView() { return foo.getFileView(); }
  
  public FileTreeModel model = new FileTreeModel();
  private JFileChooser foo = new JFileChooser();
  private JButton approve = new JButton("Select");
  private JButton cancel = new JButton("Cancel");
  private File currentDirectory = null;
  
  private JTree tree;
  private JDialog dialog;
  private int returnValue;
  public static int APPROVE_OPTION = 0;
  public static int CANCEL_OPTION = 1;
  public static final int DIRECTORIES_ONLY = 1;
  public static final int FILES_AND_DIRECTORIES = 2;
  
  private class ButtonListener implements java.awt.event.ActionListener, javax.swing.event.TreeSelectionListener
  {
    private ButtonListener() {}
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
      if (e.getSource() == approve)
      {
        returnValue = 0;
        JDirectoryChooser.cancel_selected = false;
      }
      else {
        JDirectoryChooser.cancel_selected = true; }
      dialog.dispose();
      dialog = null;
    }
    

    public void valueChanged(javax.swing.event.TreeSelectionEvent e) { approve.setEnabled(tree.getSelectionCount() > 0); }
  }
  
  private class FileRenderer extends javax.swing.tree.DefaultTreeCellRenderer {
    static final long serialVersionUID = 78322342893L;
    
    private FileRenderer() {}
    
    public java.awt.Component getTreeCellRendererComponent(JTree tree, Object node, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      java.awt.Component comp = super.getTreeCellRendererComponent(tree, node, selected, expanded, leaf, row, hasFocus);
      if (((comp instanceof JLabel)) && ((node instanceof File)))
      {
        JLabel label = (JLabel)comp;
        File f = (File)node;
        label.setText(foo.getName(f));
        label.setIcon(foo.getIcon(f));
      }
      return comp;
    }
  }
  
  public class FileTreeModel implements javax.swing.tree.TreeModel {
    public FileTreeModel() {}
    
    public Object getChild(Object node, int index) { return children(node)[index]; }
    
    public int getChildCount(Object node)
    {
      return children(node).length;
    }
    
    public int getIndexOfChild(Object node, Object child)
    {
      System.out.println("getIndexOfChild (" + node.toString() + ", " + child.toString() + ")");
      
      File[] children = children(node);
      System.out.println("The length of children arr ==  " + children.length + "+++++++++++++++++++++++++++++++++++++++++++++++++++++");
      
      for (int i = 0; i < children.length; i++)
      {
        System.out.println("***Children[" + i + "]***  -> " + children[i].getAbsolutePath() + " ***CHILD*** -> " + child.toString());
        if (children[i].equals(child)) { return i;
        }
      }
      






      System.out.println("getIndexOfChild (" + node.toString() + ", " + child.toString() + ")");
      

      throw new RuntimeException("This can't happen");
    }
    
    public Object getRoot() {
      return JDirectoryChooser.root;
    }
    
    public boolean isLeaf(Object node)
    {
      return (node != JDirectoryChooser.root) && (!((File)node).isDirectory());
    }
    




    public void valueForPathChanged(TreePath p1, Object p2) {}
    



    public void addTreeModelListener(TreeModelListener l)
    {
      listenerList.add(TreeModelListener.class, l);
    }
    






    public void removeTreeModelListener(TreeModelListener l)
    {
      listenerList.remove(TreeModelListener.class, l);
    }
    
    public File[] children(Object node) {
      System.out.println("Inside the children() function...........  CACHE_SIZE : 10");
      
      for (int i = 0; i < 10; i++)
      {
        if (cachedNode[i] != null) {
          System.out.println("node   " + node.toString() + "  cachedNode [" + i + "]  -->                        " + cachedNode[i].getAbsolutePath());
        } else
          System.out.println("cachedNode [" + i + "] is null");
        if (node == cachedNode[i]) { return cachedChildren[i];
        }
      }
      System.out.println("out of that stupid loop at lastttttttttttttttttttttttttttt                     111");
      
      Window w = null;
      java.awt.Cursor oldCursor = null;
      if ((tree != null) && (tree.isVisible()))
      {
        w = (Window)javax.swing.SwingUtilities.getAncestorOfClass(Window.class, tree);
        if (w != null)
        {
          oldCursor = w.getCursor();
          w.setCursor(java.awt.Cursor.getPredefinedCursor(3));
        }
      }
      
      try
      {
        FileSystemView view = FileSystemView.getFileSystemView();
        File dir = (File)node;
        boolean isRoot = dir == JDirectoryChooser.root;
        File[] children = isRoot ? view.getRoots() : view.getFiles(dir, foo.isFileHidingEnabled());
        if (!isRoot)
        {
          boolean dirOnly = foo.getFileSelectionMode() == 1;
          if ((foo.getFileFilter() != null) || (dirOnly))
          {
            Vector<File> v = new Vector();
            for (int i = 0; i < children.length; i++)
            {
              File f = children[i];
              if (((!dirOnly) || (f.isDirectory())) && 
                (foo.accept(f))) v.addElement(children[i]);
            }
            if (v.size() != children.length)
            {
              children = new File[v.size()];
              v.copyInto(children);
            }
          }
        }
        cachedNode[nextCache] = dir;
        cachedChildren[nextCache] = children;
        nextCache = ((nextCache + 1) % 10);
        
        System.out.println("out of that stupid loop at lastttttttttttttttttttttttttttt                     222");
        return children;
      }
      finally
      {
        if (w != null) w.setCursor(oldCursor);
      }
    }
    
    void changed()
    {
      cachedNode = new File[10];
      cachedChildren = new File[10][];
      fireTreeStructureChanged(new javax.swing.event.TreeModelEvent(this, new TreePath(JDirectoryChooser.root)));
    }
    
    protected void fireTreeStructureChanged(javax.swing.event.TreeModelEvent e) {
      Object[] listeners = listenerList.getListenerList();
      

      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
        if (listeners[i] == TreeModelListener.class)
          ((TreeModelListener)listeners[(i + 1)]).treeStructureChanged(e); }
    }
    
    private EventListenerList listenerList = new EventListenerList();
    private final int CACHE_SIZE = 10;
    private File[] cachedNode = new File[10];
    private File[][] cachedChildren = new File[10][];
    private int nextCache = 0; }
  
  private static File root = new File("root");
  public static javax.swing.JFrame frame = new javax.swing.JFrame();
}
