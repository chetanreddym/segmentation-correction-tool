package signalprocesser.voronoi.representation.voronoicell;

import java.util.Collection;
import signalprocesser.voronoi.VPoint;








public class BestVoronoiCells
{
  private int numberstored;
  private double[] bestareas;
  private VVoronoiCell[] bestcells;
  
  public BestVoronoiCells(int number)
  {
    bestareas = new double[number];
    bestcells = new VVoronoiCell[number];
  }
  
  public BestVoronoiCells(int number, Collection<VPoint> voronoicells) { this(number);
    findBest(voronoicells);
  }
  


  public int getBestStored()
  {
    return numberstored;
  }
  
  public double getBestArea(int index) {
    return bestareas[index];
  }
  
  public VVoronoiCell getBestCell(int index) {
    return bestcells[index];
  }
  
  public double getTotalAreaOfBest() {
    double sum = 0.0D;
    for (int x = 0; x < numberstored; x++) sum += bestareas[x];
    return sum;
  }
  
  public double getAverageArea() {
    double sum = 0.0D;
    if (numberstored == 0) return -1.0D;
    for (int x = 0; x < numberstored; x++) sum += bestareas[x];
    return sum / numberstored;
  }
  
  public int getAverageX() {
    int tmp = 0;
    if (numberstored == 0) return -1;
    for (int x = 0; x < numberstored; x++) tmp += bestcells[x].x;
    return tmp / numberstored;
  }
  
  public int getAverageY() {
    int tmp = 0;
    if (numberstored == 0) return -1;
    for (int x = 0; x < numberstored; x++) tmp += bestcells[x].y;
    return tmp / numberstored;
  }
  



  public void findBest(Collection<VPoint> voronoicells)
  {
    numberstored = 0;
    


    for (VPoint point : voronoicells)
    {
      VVoronoiCell cell = (VVoronoiCell)point;
      double area = cell.getAreaOfCell();
      

      if (area >= 0.0D)
      {

        if (numberstored == 0) {
          numberstored = 1;
          bestareas[0] = area;
          bestcells[0] = cell;
        }
        else {
          int index = numberstored;
          while (bestareas[(index - 1)] > area) {
            if (index > 1) {
              index--;

            }
            else
            {
              index = 0;
              break;
            }
          }
          
          if (index < bestareas.length)
          {
            if (numberstored < bestareas.length) {
              int tmp = numberstored;
              

              numberstored += 1;
            }
            for (int tmp = bestareas.length - 1; 
                


                tmp > index; tmp--) {
              bestareas[tmp] = bestareas[(tmp - 1)];
              bestcells[tmp] = bestcells[(tmp - 1)];
            }
            

            bestareas[index] = area;
            bestcells[index] = cell;
          }
        }
      }
    }
    
    for (int tmp = numberstored; tmp < bestareas.length; tmp++) {
      bestareas[tmp] = 0.0D;
      bestcells[tmp] = null;
    }
  }
}
