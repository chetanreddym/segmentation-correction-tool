package javax.media.jai;

import java.awt.geom.Point2D;




















































































public final class WarpGrid
  extends Warp
{
  private int xStart;
  private int yStart;
  private int xEnd;
  private int yEnd;
  private int xStep;
  private int yStep;
  private int xNumCells;
  private int yNumCells;
  private float[] xWarpPos;
  private float[] yWarpPos;
  
  private void initialize(int xStart, int xStep, int xNumCells, int yStart, int yStep, int yNumCells, float[] warpPositions)
  {
    this.xStart = xStart;
    this.yStart = yStart;
    
    xEnd = (xStart + xStep * xNumCells);
    yEnd = (yStart + yStep * yNumCells);
    
    this.xStep = xStep;
    this.yStep = yStep;
    
    this.xNumCells = xNumCells;
    this.yNumCells = yNumCells;
    
    int xNumGrids = xNumCells + 1;
    int yNumGrids = yNumCells + 1;
    
    int numNodes = yNumGrids * xNumGrids;
    
    xWarpPos = new float[numNodes];
    yWarpPos = new float[numNodes];
    
    int index = 0;
    for (int idx = 0; idx < numNodes; idx++) {
      xWarpPos[idx] = warpPositions[(index++)];
      yWarpPos[idx] = warpPositions[(index++)];
    }
  }
  










































  public WarpGrid(int xStart, int xStep, int xNumCells, int yStart, int yStep, int yNumCells, float[] warpPositions)
  {
    if (warpPositions.length != 2 * (xNumCells + 1) * (yNumCells + 1)) {
      throw new IllegalArgumentException(JaiI18N.getString("WarpGrid0"));
    }
    
    initialize(xStart, xStep, xNumCells, yStart, yStep, yNumCells, warpPositions);
  }
  





















  public WarpGrid(Warp master, int xStart, int xStep, int xNumCells, int yStart, int yStep, int yNumCells)
  {
    int size = 2 * (xNumCells + 1) * (yNumCells + 1);
    
    float[] warpPositions = new float[size];
    warpPositions = master.warpSparseRect(xStart, yStart, xNumCells * xStep + 1, yNumCells * yStep + 1, xStep, yStep, warpPositions);
    




    initialize(xStart, xStep, xNumCells, yStart, yStep, yNumCells, warpPositions);
  }
  


  public int getXStart()
  {
    return xStart;
  }
  
  public int getYStart()
  {
    return yStart;
  }
  
  public int getXStep()
  {
    return xStep;
  }
  
  public int getYStep()
  {
    return yStep;
  }
  
  public int getXNumCells()
  {
    return xNumCells;
  }
  
  public int getYNumCells()
  {
    return yNumCells;
  }
  
  public float[] getXWarpPos()
  {
    return xWarpPos;
  }
  
  public float[] getYWarpPos()
  {
    return yWarpPos;
  }
  




















  private float[] noWarpSparseRect(int x1, int x2, int y1, int y2, int periodX, int periodY, int offset, int stride, float[] destRect)
  {
    if (destRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    for (int j = y1; j <= y2; j += periodY) {
      int index = offset;
      offset += stride;
      
      for (int i = x1; i <= x2; i += periodX) {
        destRect[(index++)] = i;
        destRect[(index++)] = j;
      }
    }
    
    return destRect;
  }
  


























  public float[] warpSparseRect(int x, int y, int width, int height, int periodX, int periodY, float[] destRect)
  {
    int stride = 2 * ((width + periodX - 1) / periodX);
    
    if (destRect == null) {
      destRect = new float[stride * ((height + periodY - 1) / periodY)];
    }
    
    int x1 = x;
    int x2 = x + width - 1;
    int y1 = y;
    int y2 = y + height - 1;
    
    if ((y1 >= yEnd) || (y2 < yStart) || (x1 >= xEnd) || (x2 < xStart))
    {
      return noWarpSparseRect(x1, x2, y1, y2, periodX, periodY, 0, stride, destRect);
    }
    

    if (y1 < yStart) {
      int periods = (yStart - y1 + periodY - 1) / periodY;
      noWarpSparseRect(x1, x2, y1, yStart - 1, periodX, periodY, 0, stride, destRect);
      
      y1 += periods * periodY;
    }
    
    if (y2 >= yEnd) {
      int periods = (yEnd - y + periodY - 1) / periodY;
      noWarpSparseRect(x1, x2, y + periods * periodY, y2, periodX, periodY, periods * stride, stride, destRect);
      


      y2 = y + (periods - 1) * periodY;
    }
    
    if (x1 < xStart) {
      int periods = (xStart - x1 + periodX - 1) / periodX;
      noWarpSparseRect(x1, xStart - 1, y1, y2, periodX, periodY, (y1 - y) / periodY * stride, stride, destRect);
      
      x1 += periods * periodX;
    }
    
    if (x2 >= xEnd) {
      int periods = (xEnd - x + periodX - 1) / periodX;
      noWarpSparseRect(x + periods * periodX, x2, y1, y2, periodX, periodY, (y1 - y) / periodY * stride + periods * 2, stride, destRect);
      



      x2 = x + (periods - 1) * periodX;
    }
    








    int[] cellPoints = new int[xNumCells];
    for (int i = x1; i <= x2; i += periodX) {
      cellPoints[((i - xStart) / xStep)] += 1;
    }
    
    int offset = (y1 - y) / periodY * stride + (x1 - x) / periodX * 2;
    

    int xNumGrids = xNumCells + 1;
    

    float deltaX = periodX / xStep;
    

    for (int j = y1; j <= y2; j += periodY) {
      int index = offset;
      offset += stride;
      
      int yCell = (j - yStart) / yStep;
      int yGrid = yStart + yCell * yStep;
      float yFrac = (j + 0.5F - yGrid) / yStep;
      

      float deltaTop = (1.0F - yFrac) * deltaX;
      float deltaBottom = yFrac * deltaX;
      
      int i = x1;
      while (i <= x2)
      {
        int xCell = (i - xStart) / xStep;
        int xGrid = xStart + xCell * xStep;
        float xFrac = (i + 0.5F - xGrid) / xStep;
        
        int nodeOffset = yCell * xNumGrids + xCell;
        float wx0 = xWarpPos[nodeOffset];
        float wy0 = yWarpPos[nodeOffset];
        float wx1 = xWarpPos[(++nodeOffset)];
        float wy1 = yWarpPos[nodeOffset];
        nodeOffset += xNumCells;
        float wx2 = xWarpPos[nodeOffset];
        float wy2 = yWarpPos[nodeOffset];
        float wx3 = xWarpPos[(++nodeOffset)];
        float wy3 = yWarpPos[nodeOffset];
        
        float s = wx0 + (wx1 - wx0) * xFrac;
        float t = wy0 + (wy1 - wy0) * xFrac;
        float u = wx2 + (wx3 - wx2) * xFrac;
        float v = wy2 + (wy3 - wy2) * xFrac;
        
        float wx = s + (u - s) * yFrac;
        float wy = t + (v - t) * yFrac;
        

        float dx = (wx1 - wx0) * deltaTop + (wx3 - wx2) * deltaBottom;
        float dy = (wy1 - wy0) * deltaTop + (wy3 - wy2) * deltaBottom;
        

        int nPoints = cellPoints[xCell];
        for (int k = 0; k < nPoints; k++) {
          destRect[(index++)] = (wx - 0.5F);
          destRect[(index++)] = (wy - 0.5F);
          
          wx += dx;
          wy += dy;
          i += periodX;
        }
      }
    }
    
    return destRect;
  }
  

































  public Point2D mapDestPoint(Point2D destPt)
  {
    if (destPt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    float[] sxy = warpSparseRect((int)destPt.getX(), (int)destPt.getY(), 2, 2, 1, 1, null);
    

    double wtRight = destPt.getX() - (int)destPt.getX();
    double wtLeft = 1.0D - wtRight;
    double wtBottom = destPt.getY() - (int)destPt.getY();
    double wtTop = 1.0D - wtBottom;
    
    Point2D pt = (Point2D)destPt.clone();
    pt.setLocation((sxy[0] * wtLeft + sxy[2] * wtRight) * wtTop + (sxy[4] * wtLeft + sxy[6] * wtRight) * wtBottom, (sxy[1] * wtLeft + sxy[3] * wtRight) * wtTop + (sxy[5] * wtLeft + sxy[7] * wtRight) * wtBottom);
    



    return pt;
  }
}
