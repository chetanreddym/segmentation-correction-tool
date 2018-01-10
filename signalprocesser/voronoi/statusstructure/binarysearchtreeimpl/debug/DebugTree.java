package signalprocesser.voronoi.statusstructure.binarysearchtreeimpl.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import signalprocesser.voronoi.eventqueue.VSiteEvent;
import signalprocesser.voronoi.statusstructure.binarysearchtreeimpl.VInternalNode;
import signalprocesser.voronoi.statusstructure.binarysearchtreeimpl.VLeafNode;
import signalprocesser.voronoi.statusstructure.binarysearchtreeimpl.VNode;

public class DebugTree extends javax.swing.JDialog
{
  public static final int BOX_WIDTH = 120;
  public static final int BOX_HEIGHT = 50;
  public static final int GAP_BETWEENBOXES = 20;
  public static final java.awt.Font BOX_FONT = new java.awt.Font("Arial", 1, 9);
  
  private int sweepline = -1;
  private VNode rootnode = null;
  private TreePanel panel = null;
  
  public DebugTree(java.awt.Frame parent) {
    super(parent, false);
    initComponents();
    setTitle("Tree Display App");
    panel = new TreePanel();
    getContentPane().add(panel, "Center");
  }
  
  public void setRootNode(VNode _rootnode, int _sweepline) {
    rootnode = _rootnode;
    sweepline = _sweepline;
    repaint();
  }
  
  public class TreePanel extends javax.swing.JPanel
  {
    public TreePanel() {}
    
    public void paintComponent(java.awt.Graphics _g) {
      Graphics2D g = (Graphics2D)_g;
      

      double width = getWidth();
      double height = getHeight();
      

      g.setColor(Color.white);
      g.fillRect(0, 0, (int)width, (int)height);
      

      g.setColor(Color.black);
      g.drawString("sweepline=" + sweepline, 20, 20);
      
      drawNode(g, rootnode, 0, 0, (int)width);
    }
    
    public void drawNode(Graphics2D g, VNode currnode, int depth, int left, int right) {
      String text;
      String text;
      if (currnode == null) {
        text = "NULL VALUE";
      } else if (currnode.isInternalNode()) {
        VInternalNode internalnode = (VInternalNode)currnode;
        String text = id + " (" + v1.getX() + "," + v1.getY() + ") & (" + v2.getX() + "," + v2.getY() + ")";
        
        if (internalnode.getDepth() != depth + 1) {
          throw new RuntimeException("Part of tree not equal to expected depth; expected=" + (depth + 1) + ", actual=" + internalnode.getDepth());
        }
        
        VSiteEvent v1 = v1;
        VSiteEvent v2 = v2;
        

        v1.calcParabolaConstants(sweepline);
        v2.calcParabolaConstants(sweepline);
        

        double[] intersects = signalprocesser.voronoi.VoronoiShared.solveQuadratic(a - a, b - b, c - c);
        text = text + " = " + (int)intersects[0];
      } else {
        VLeafNode leafnode = (VLeafNode)currnode;
        text = "id #" + siteevent.id + " (" + siteevent.getX() + "," + siteevent.getY() + ")";
      }
      

      int center = left + (right - left) / 2;
      Rectangle rectangle = new Rectangle(center - 60, 20 + 70 * depth, 120, 50);
      g.setColor(new Color(240, 255, 235));
      g.fillRect(x, y, width, height);
      if (currnode == null) {
        g.setColor(Color.yellow);
      } else if (currnode.isInternalNode()) {
        g.setColor(Color.red);
      } else {
        g.setColor(Color.blue);
      }
      g.drawRect(x, y, width, height);
      signalprocesser.shared.TextToolkit.writeFromTop(g, DebugTree.BOX_FONT, Color.BLACK, text, rectangle);
      

      if ((currnode != null) && (currnode.isInternalNode())) {
        VInternalNode internalnode = (VInternalNode)currnode;
        drawNode(g, internalnode.getLeft(), depth + 1, left, center);
        drawNode(g, internalnode.getRight(), depth + 1, center, right);
      }
    }
  }
  






  private void initComponents()
  {
    setDefaultCloseOperation(2);
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosed(WindowEvent evt) {
        DebugTree.this.formWindowClosed(evt);
      }
      
    });
    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((width - 670) / 2, (height - 569) / 2, 670, 569);
  }
  



  private void formWindowClosed(WindowEvent evt) {}
  


  public static void main(String[] args)
  {
    DebugTree app = new DebugTree(new javax.swing.JFrame());
    app.setVisible(true);
    VInternalNode node = new VInternalNode();
    VLeafNode leaf1 = new VLeafNode(new VSiteEvent(new signalprocesser.voronoi.VPoint(1, 2)));
    VLeafNode leaf2 = new VLeafNode(new VSiteEvent(new signalprocesser.voronoi.VPoint(1, 2)));
    node.setLeft(leaf1);
    node.setRight(leaf2);
    app.setRootNode(node, 10);
  }
}
