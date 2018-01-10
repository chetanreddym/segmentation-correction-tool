package ocr.manager;

import java.awt.Component;
import java.awt.Container;
import java.io.PrintStream;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;



public class SpringUtilities
{
  public SpringUtilities() {}
  
  public static void printSizes(Component c)
  {
    System.out.println("minimumSize = " + c.getMinimumSize());
    System.out.println("preferredSize = " + c.getPreferredSize());
    System.out.println("maximumSize = " + c.getMaximumSize());
  }
  
















  public static void makeGrid(Container parent, int rows, int cols, int initialX, int initialY, int xPad, int yPad)
  {
    try
    {
      layout = (SpringLayout)parent.getLayout();
    } catch (ClassCastException exc) { SpringLayout layout;
      exc.printStackTrace();
      System.err.println("The first argument to makeGrid must use SpringLayout."); return;
    }
    
    SpringLayout layout;
    Spring xPadSpring = Spring.constant(xPad);
    Spring yPadSpring = Spring.constant(yPad);
    Spring initialXSpring = Spring.constant(initialX);
    Spring initialYSpring = Spring.constant(initialY);
    int max = rows * cols;
    


    Spring maxWidthSpring = layout.getConstraints(parent.getComponent(0))
      .getWidth();
    Spring maxHeightSpring = layout.getConstraints(parent.getComponent(0))
      .getWidth();
    for (int i = 1; i < max; i++) {
      SpringLayout.Constraints cons = layout.getConstraints(
        parent.getComponent(i));
      
      maxWidthSpring = Spring.max(maxWidthSpring, cons.getWidth());
      maxHeightSpring = Spring.max(maxHeightSpring, cons.getHeight());
    }
    


    for (int i = 0; i < max; i++) {
      SpringLayout.Constraints cons = layout.getConstraints(
        parent.getComponent(i));
      
      cons.setWidth(maxWidthSpring);
      cons.setHeight(maxHeightSpring);
    }
    


    SpringLayout.Constraints lastCons = null;
    SpringLayout.Constraints lastRowCons = null;
    for (int i = 0; i < max; i++) {
      SpringLayout.Constraints cons = layout.getConstraints(
        parent.getComponent(i));
      if (i % cols == 0) {
        lastRowCons = lastCons;
        cons.setX(initialXSpring);
      } else {
        cons.setX(Spring.sum(lastCons.getConstraint("East"), 
          xPadSpring));
      }
      
      if (i / cols == 0) {
        cons.setY(initialYSpring);
      } else {
        cons.setY(Spring.sum(lastRowCons.getConstraint("South"), 
          yPadSpring));
      }
      lastCons = cons;
    }
    

    SpringLayout.Constraints pCons = layout.getConstraints(parent);
    pCons.setConstraint("South", 
      Spring.sum(
      Spring.constant(yPad), 
      lastCons.getConstraint("South")));
    pCons.setConstraint("East", 
      Spring.sum(
      Spring.constant(xPad), 
      lastCons.getConstraint("East")));
  }
  



  private static SpringLayout.Constraints getConstraintsForCell(int row, int col, Container parent, int cols)
  {
    SpringLayout layout = (SpringLayout)parent.getLayout();
    Component c = parent.getComponent(row * cols + col);
    return layout.getConstraints(c);
  }
  

















  public static void makeCompactGrid(Container parent, int rows, int cols, int initialX, int initialY, int xPad, int yPad)
  {
    try
    {
      layout = (SpringLayout)parent.getLayout();
    } catch (ClassCastException exc) { SpringLayout layout;
      System.err.println("The first argument to makeCompactGrid must use SpringLayout."); return;
    }
    
    SpringLayout layout;
    
    Spring x = Spring.constant(initialX);
    for (int c = 0; c < cols; c++) {
      Spring width = Spring.constant(0);
      for (int r = 0; r < rows; r++) {
        width = Spring.max(width, 
          getConstraintsForCell(r, c, parent, cols)
          .getWidth());
      }
      for (int r = 0; r < rows; r++) {
        SpringLayout.Constraints constraints = 
          getConstraintsForCell(r, c, parent, cols);
        constraints.setX(x);
        constraints.setWidth(width);
      }
      x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
    }
    

    Spring y = Spring.constant(initialY);
    for (int r = 0; r < rows; r++) {
      Spring height = Spring.constant(0);
      for (int c = 0; c < cols; c++) {
        height = Spring.max(height, 
          getConstraintsForCell(r, c, parent, cols)
          .getHeight());
      }
      for (int c = 0; c < cols; c++) {
        SpringLayout.Constraints constraints = 
          getConstraintsForCell(r, c, parent, cols);
        constraints.setY(y);
        constraints.setHeight(height);
      }
      y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
    }
    

    SpringLayout.Constraints pCons = layout.getConstraints(parent);
    pCons.setConstraint("South", y);
    pCons.setConstraint("East", x);
  }
}
