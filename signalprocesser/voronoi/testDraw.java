package signalprocesser.voronoi;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JFrame;
import signalprocesser.voronoi.representation.RepresentationFactory;
import signalprocesser.voronoi.representation.RepresentationInterface;
import signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation;
import signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff;
import signalprocesser.voronoi.representation.triangulation.VHalfEdge;
import signalprocesser.voronoi.shapegeneration.ShapeGenerationException;
import signalprocesser.voronoi.statusstructure.VLinkedNode;

public class testDraw
{
  private ArrayList<VPoint> points = new ArrayList();
  private ArrayList<VPoint> borderpoints = null;
  private TestRepresentationWrapper representationwrapper = new TestRepresentationWrapper();
  private signalprocesser.voronoi.representation.AbstractRepresentation representation;
  
  public static void main(String[] args) {
    new testDraw();
  }
  








  testDraw()
  {
    JFrame frame = new JFrame();
    

    frame.getContentPane().add(new MyComponent());
    

    int frameWidth = 300;
    int frameHeight = 300;
    frame.setSize(frameWidth, frameHeight);
    frame.setVisible(true);
  }
  
  class MyComponent extends javax.swing.JComponent {
    MyComponent() {}
    
    public void paint(Graphics g) {
      Graphics2D g2d = (Graphics2D)g;
      
      try
      {
        ArrayList<VPoint> newpoints = null;
        ArrayList<VPoint> newborderpoints = null;
        

        System.out.println("points file/" + points.size() + ": " + points);
        


        Rectangle shapebounds = new Rectangle(120, 60, 268, 242);
        Font font = new Font("Arial", 1, 200);
        
        try
        {
          newborderpoints = signalprocesser.voronoi.shapegeneration.ShapeGeneration.createShapeOutline("R", shapebounds, font);
        }
        catch (ShapeGenerationException e1) {
          e1.printStackTrace();
        }
        
        boolean splitlonglines = true;
        int shapepoints = Integer.MAX_VALUE;
        int shapepoint_mindensity = 15;
        int internalpoints = Integer.MAX_VALUE;
        int internal_mindensity = 15;
        

        try
        {
          points = signalprocesser.voronoi.shapegeneration.ShapeGeneration.addRandomPoints(newborderpoints, splitlonglines, 
            shapepoints, shapepoint_mindensity, 
            internalpoints, internal_mindensity);
        } catch (ShapeGenerationException e) {
          e.printStackTrace();
        }
        


        borderpoints = newborderpoints;
        

        points = RepresentationFactory.convertPointsToTriangulationPoints(points);
        




        System.out.println("borderpoints/" + borderpoints.size() + ": " + borderpoints);
        
        representation = RepresentationFactory.createTriangulationRepresentation();
        System.out.println("representation: " + representation.getClass());
        
        testDraw.TestRepresentationWrapper.access$0(representationwrapper, representation);
        TriangulationRepresentation.CalcCutOff calccutoff = new TriangulationRepresentation.CalcCutOff() {
          public int calculateCutOff(TriangulationRepresentation rep) {
            return rep.getMaxLengthOfMinimumSpanningTree();
          }
          

        };
        ((TriangulationRepresentation)representation).setCalcCutOff(calccutoff);
        







        VoronoiAlgorithm.generateVoronoi(representationwrapper, points);
        






        System.out.println("points2/" + points.size() + ": " + points);
        



















        VHalfEdge outeredge = ((TriangulationRepresentation)representation).findOuterEdge();
        ArrayList<String> pointsStr = new ArrayList();
        
        ArrayList<Point> plist = new ArrayList();
        
        if ((outeredge == null) || (next == null)) {
          return;
        }
        
        System.out.println("outeredge: " + outeredge.getLength());
        
        VHalfEdge curredge = outeredge;
        do
        {
          plist.add(new Point(curredge.getX(), curredge.getY()));
          plist.add(new Point(next.getX(), next.getY()));








        }
        while ((next).next != null) && (curredge != outeredge));
        

        Polygon poly = new Polygon();
        for (Point p : plist) {
          poly.addPoint(x, y);
        }
        g2d.drawPolygon(poly);
        


        BufferedWriter out = null;
        
        try
        {
          FileWriter fstream = new FileWriter("out3.txt");
          out = new BufferedWriter(fstream);

        }
        catch (Exception e)
        {
          System.err.println("Error: " + e.getMessage());
        }
        
        for (String p : pointsStr)
        {

          out.write(p + "\n");
        }
        
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  





  public class TestRepresentationWrapper
    implements RepresentationInterface
  {
    private final ArrayList<VPoint> circleevents = new ArrayList();
    
    private RepresentationInterface innerrepresentation = null;
    


    public TestRepresentationWrapper() {}
    

    public void beginAlgorithm(Collection<VPoint> points)
    {
      circleevents.clear();
      

      if (innerrepresentation != null) {
        innerrepresentation.beginAlgorithm(points);
      }
    }
    

    public void siteEvent(VLinkedNode n1, VLinkedNode n2, VLinkedNode n3)
    {
      if (innerrepresentation != null) {
        innerrepresentation.siteEvent(n1, n2, n3);
      }
    }
    
    public void circleEvent(VLinkedNode n1, VLinkedNode n2, VLinkedNode n3, int circle_x, int circle_y) {
      circleevents.add(new VPoint(circle_x, circle_y));
      

      if (innerrepresentation != null) {
        innerrepresentation.circleEvent(n1, n2, n3, circle_x, circle_y);
      }
    }
    

    public void endAlgorithm(Collection<VPoint> points, int lastsweeplineposition, VLinkedNode headnode)
    {
      if (innerrepresentation != null) {
        innerrepresentation.endAlgorithm(points, lastsweeplineposition, headnode);
      }
    }
  }
}
