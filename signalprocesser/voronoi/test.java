package signalprocesser.voronoi;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import javax.swing.JComponent;
import signalprocesser.voronoi.representation.AbstractRepresentation;
import signalprocesser.voronoi.representation.RepresentationFactory;
import signalprocesser.voronoi.representation.RepresentationInterface;
import signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation;
import signalprocesser.voronoi.representation.triangulation.VHalfEdge;
import signalprocesser.voronoi.shapegeneration.ShapeGeneration;
import signalprocesser.voronoi.shapegeneration.ShapeGenerationException;
import signalprocesser.voronoi.statusstructure.VLinkedNode;





public class test
  extends JComponent
{
  private ArrayList<VPoint> points = new ArrayList();
  private ArrayList<VPoint> borderpoints = null;
  private TestRepresentationWrapper representationwrapper = new TestRepresentationWrapper();
  private AbstractRepresentation representation;
  
  test()
  {
    try {
      ArrayList<VPoint> newpoints = null;
      ArrayList<VPoint> newborderpoints = null;
      loadPoints("voronoipoints2.txt");
      
      System.out.println("points file/" + points.size() + ": " + points);
      


      Rectangle shapebounds = new Rectangle(120, 60, 268, 242);
      Font font = new Font("Arial", 1, 200);
      
      try
      {
        newborderpoints = ShapeGeneration.createShapeOutline("S", shapebounds, font);
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
        newpoints = ShapeGeneration.addRandomPoints(newborderpoints, splitlonglines, 
          shapepoints, shapepoint_mindensity, 
          internalpoints, internal_mindensity);
      } catch (ShapeGenerationException e) {
        e.printStackTrace();
      }
      
      System.out.println("newpoints/" + newpoints.size() + ": " + newpoints);
      
      borderpoints = newborderpoints;
      

      points = RepresentationFactory.convertPointsToTriangulationPoints(newpoints);
      




      System.out.println("points1/" + points.size() + ": " + points);
      
      representation = RepresentationFactory.createTriangulationRepresentation();
      System.out.println("representation: " + representation.getClass());
      
      representationwrapper.innerrepresentation = representation;
      







      VoronoiAlgorithm.generateVoronoi(representationwrapper, points);
      





      System.out.println("points2/" + points.size() + ": " + points);
      







      ArrayList<VPoint> outer = ((TriangulationRepresentation)representation).getPointsFormingOutterBoundary();
      
      System.out.println("outer: " + outer.size());
      
      Collection<VPoint> vertex = representation).vertexpoints;
      
      Vector<Point> list = new Vector();
      
      for (VPoint vp : vertex) {
        list.add(new Point(x, y));
      }
      
      VHalfEdge outeredge = ((TriangulationRepresentation)representation).findOuterEdge();
      Object pointsStr = new ArrayList();
      
      if ((outeredge == null) || (next == null)) {
        return;
      }
      
      System.out.println("outeredge: " + vertex);
      
      VHalfEdge curredge = outeredge;
      

      do
      {
        ((ArrayList)pointsStr).add(Integer.toString(curredge.getX()) + "," + Integer.toString(curredge.getY()));
        ((ArrayList)pointsStr).add(Integer.toString(next.getX()) + "," + Integer.toString(next.getY()));




      }
      while ((next).next != null) && (curredge != outeredge));
      
      System.out.println(pointsStr);
      
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
      
      for (String p : (ArrayList)pointsStr)
      {

        out.write(p + "\n");
      }
      
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  


  public static void main(String[] args)
  {
    test t = new test();
  }
  

  public void paintComponent(Graphics _g)
  {
    System.out.println("paintComponent");
    Graphics2D g = (Graphics2D)_g;
    
    if (representation != null) {
      g.setColor(Color.magenta);
      
      try
      {
        representation.paint(g);
      } catch (Error e) {
        throw e;
      } catch (RuntimeException e) {
        throw e;
      }
    }
  }
  
  private void loadPoints(String filename) throws IOException
  {
    points.clear();
    
    try
    {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      
      String line;
      while ((line = reader.readLine()) != null) { String line;
        if (line.trim().length() > 0)
        {
          String[] values;
          
          if (line.indexOf(',') > 0) {
            values = line.split(",", 2); } else { String[] values;
            if (line.indexOf(' ') > 0) {
              values = line.split(" ", 2);
            } else {
              throw new IOException("Expected value line to be comma or space seperated - except found neither");
            }
          }
          String[] values;
          int x = Integer.parseInt(values[0]);
          int y = Integer.parseInt(values[1]);
          

          points.add(new VPoint(x, y));
        } }
      reader.close();
    }
    catch (FileNotFoundException localFileNotFoundException) {}
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
