package ocr.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;













public class WrapLayout
  extends FlowLayout
{
  private static final long serialVersionUID = 1L;
  
  public WrapLayout() {}
  
  public WrapLayout(int align)
  {
    super(align);
  }
  














  public WrapLayout(int align, int hgap, int vgap)
  {
    super(align, hgap, vgap);
  }
  









  public Dimension preferredLayoutSize(Container target)
  {
    return layoutSize(target, true);
  }
  









  public Dimension minimumLayoutSize(Container target)
  {
    Dimension minimum = layoutSize(target, false);
    width -= getHgap() + 1;
    return minimum;
  }
  









  private Dimension layoutSize(Container target, boolean preferred)
  {
    synchronized (target.getTreeLock())
    {




      int targetWidth = getSizewidth;
      
      if (targetWidth == 0) {
        targetWidth = Integer.MAX_VALUE;
      }
      int hgap = getHgap();
      int vgap = getVgap();
      Insets insets = target.getInsets();
      int horizontalInsetsAndGap = left + right + 
        hgap * 2;
      int maxWidth = targetWidth - horizontalInsetsAndGap;
      


      Dimension dim = new Dimension(0, 0);
      int rowWidth = 0;
      int rowHeight = 0;
      
      int nmembers = target.getComponentCount();
      
      for (int i = 0; i < nmembers; i++) {
        Component m = target.getComponent(i);
        
        if (m.isVisible()) {
          Dimension d = preferred ? m.getPreferredSize() : m
            .getMinimumSize();
          


          if (rowWidth + width > maxWidth) {
            addRow(dim, rowWidth, rowHeight);
            rowWidth = 0;
            rowHeight = 0;
          }
          


          if (rowWidth != 0) {
            rowWidth += hgap;
          }
          
          rowWidth += width;
          rowHeight = Math.max(rowHeight, height);
        }
      }
      
      addRow(dim, rowWidth, rowHeight);
      
      width += horizontalInsetsAndGap;
      height += top + bottom + vgap * 2;
      





      Container scrollPane = SwingUtilities.getAncestorOfClass(
        JScrollPane.class, target);
      
      if (scrollPane != null) {
        width -= hgap + 1;
      }
      
      return dim;
    }
  }
  









  private void addRow(Dimension dim, int rowWidth, int rowHeight)
  {
    width = Math.max(width, rowWidth);
    
    if (height > 0) {
      height += getVgap();
    }
    
    height += rowHeight;
  }
}
