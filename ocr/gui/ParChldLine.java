package ocr.gui;

import gttool.document.DLZone;
import java.awt.Point;
import java.awt.geom.Line2D.Float;
import java.util.LinkedList;
import java.util.Vector;










public class ParChldLine
{
  private Line2D.Float l2d = null;
  private Point parpt = null; private Point chpt = null;
  
  public ParChldLine(Vector<DLZone> vec, int i, LinkedList<DLZone> childList, int j) {
    Point parpt = ((DLZone)vec.elementAt(i)).dlGetZoneOrigin();
    Point chpt = ((DLZone)childList.get(j)).dlGetZoneOrigin();
    l2d = new Line2D.Float(parpt, chpt);
  }
  
  public Line2D.Float getLine() {
    return l2d;
  }
  
  public Point getParentPoint() {
    return parpt;
  }
  
  public Point getChildPoint() {
    return chpt;
  }
  
  public int getX1() {
    return (int)l2d.getX1();
  }
  
  public int getY1() {
    return (int)l2d.getY1();
  }
  
  public int getX2() {
    return (int)l2d.getX2();
  }
  
  public int getY2() {
    return (int)l2d.getY2();
  }
}
