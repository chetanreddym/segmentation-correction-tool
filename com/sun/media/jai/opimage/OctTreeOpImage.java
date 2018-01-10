package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.UnpackedImageData;


































































public class OctTreeOpImage
  extends ColorQuantizerOpImage
{
  private int treeSize;
  private int maxTreeDepth = 8;
  




  private int[] squares = new int[(this.maxColorNum << 1) + 1];
  















  public OctTreeOpImage(RenderedImage source, Map config, ImageLayout layout, int maxColorNum, int upperBound, ROI roi, int xPeriod, int yPeriod)
  {
    super(source, config, layout, maxColorNum, roi, xPeriod, yPeriod);
    for (int i = -this.maxColorNum; i <= this.maxColorNum; i++) {
      squares[(i + this.maxColorNum)] = (i * i);
    }
    
















    colorMap = null;
    treeSize = upperBound;
  }
  
  protected synchronized void train() {
    Cube cube = new Cube(getSourceImage(0), maxColorNum);
    cube.constructTree();
    cube.reduction();
    cube.assignment();
    
    colorMap = new LookupTableJAI(colormap);
    setProperty("LUT", colorMap);
    setProperty("JAI.LookupTable", colorMap);
  }
  
  class Cube {
    PlanarImage source;
    int max_colors;
    byte[][] colormap = new byte[3][];
    
    Node root;
    
    int depth;
    
    int colors;
    
    int nodes;
    

    Cube(PlanarImage source, int max_colors)
    {
      this.source = source;
      this.max_colors = max_colors;
      
      int i = max_colors;
      

      for (depth = 0; i != 0; depth += 1) {
        i >>>= 1;
      }
      
      if (depth > maxTreeDepth) {
        depth = maxTreeDepth;
      } else if (depth < 2) {
        depth = 2;
      }
      
      root = new Node(this);
    }
    
    void constructTree() {
      if (roi == null) {
        roi = new ROIShape(source.getBounds());
      }
      
      int minTileX = source.getMinTileX();
      int maxTileX = source.getMaxTileX();
      int minTileY = source.getMinTileY();
      int maxTileY = source.getMaxTileY();
      int xStart = source.getMinX();
      int yStart = source.getMinY();
      
      for (int y = minTileY; y <= maxTileY; y++) {
        for (int x = minTileX; x <= maxTileX; x++)
        {


          Rectangle tileRect = source.getTileRect(x, y);
          

          if (roi.intersects(tileRect))
          {

            if ((checkForSkippedTiles) && (x >= xStart) && (y >= yStart))
            {


              int offsetX = (xPeriod - (x - xStart) % xPeriod) % xPeriod;
              

              int offsetY = (yPeriod - (y - yStart) % yPeriod) % yPeriod;
              




              if ((offsetX >= width) || (offsetY >= height)) {}

            }
            else
            {

              constructTree(source.getData(tileRect));
            } }
        }
      }
    }
    
    private void constructTree(Raster source) {
      if (!isInitialized) {
        srcPA = new PixelAccessor(getSourceImage(0));
        srcSampleType = (srcPA.sampleType == -1 ? 0 : srcPA.sampleType);
        
        isInitialized = true;
      }
      
      Rectangle srcBounds = getSourceImage(0).getBounds().intersection(source.getBounds());
      
      LinkedList rectList;
      
      if (roi == null) {
        LinkedList rectList = new LinkedList();
        rectList.addLast(srcBounds);
      } else {
        rectList = roi.getAsRectangleList(x, y, width, height);
        


        if (rectList == null) {
          return;
        }
      }
      ListIterator iterator = rectList.listIterator(0);
      int xStart = source.getMinX();
      int yStart = source.getMinY();
      
      while (iterator.hasNext()) {
        Rectangle rect = srcBounds.intersection((Rectangle)iterator.next());
        int tx = x;
        int ty = y;
        

        x = ColorQuantizerOpImage.startPosition(tx, xStart, xPeriod);
        y = ColorQuantizerOpImage.startPosition(ty, yStart, yPeriod);
        width = (tx + width - x);
        height = (ty + height - y);
        
        if (!rect.isEmpty())
        {


          UnpackedImageData uid = srcPA.getPixels(source, rect, srcSampleType, false);
          
          switch (type) {
          case 0: 
            constructTreeByte(uid);
          }
          
        }
      }
    }
    




































    private void constructTreeByte(UnpackedImageData uid)
    {
      Rectangle rect = rect;
      byte[][] data = uid.getByteData();
      int lineStride = lineStride;
      int pixelStride = pixelStride;
      byte[] rBand = data[0];
      byte[] gBand = data[1];
      byte[] bBand = data[2];
      
      int lineInc = lineStride * yPeriod;
      int pixelInc = pixelStride * xPeriod;
      
      int lastLine = height * lineStride;
      
      for (int lo = 0; lo < lastLine; lo += lineInc) {
        int lastPixel = lo + width * pixelStride;
        
        for (int po = lo; po < lastPixel; po += pixelInc) {
          int red = rBand[(po + bandOffsets[0])] & 0xFF;
          int green = gBand[(po + bandOffsets[1])] & 0xFF;
          int blue = bBand[(po + bandOffsets[2])] & 0xFF;
          

          if (nodes > treeSize) {
            root.pruneLevel();
            depth -= 1;
          }
          


          Node node = root;
          for (int level = 1; level <= depth; level++) {
            int id = (red > mid_red ? 1 : 0) | (green > mid_green ? 1 : 0) << 1 | (blue > mid_blue ? 1 : 0) << 2;
            

            if (child[id] == null) {
              node = new Node(node, id, level);
            } else
              node = child[id];
            number_pixels += 1;
          }
          
          unique += 1;
          total_red += red;
          total_green += green;
          total_blue += blue;
        }
      }
    }
    











    void reduction()
    {
      int totalSamples = (source.getWidth() + xPeriod - 1) / xPeriod * (source.getHeight() + yPeriod - 1) / yPeriod;
      
      int threshold = Math.max(1, totalSamples / (max_colors * 8));
      while (colors > max_colors) {
        colors = 0;
        threshold = root.reduce(threshold, Integer.MAX_VALUE);
      }
    }
    



















    void assignment()
    {
      colormap = new byte[3][colors];
      
      colors = 0;
      root.colormap();
    }
    

    class Node
    {
      OctTreeOpImage.Cube cube;
      
      Node parent;
      
      Node[] child;
      
      int nchild;
      
      int id;
      
      int level;
      
      int mid_red;
      
      int mid_green;
      
      int mid_blue;
      
      int number_pixels;
      
      int unique;
      
      int total_red;
      
      int total_green;
      
      int total_blue;
      
      int color_number;
      

      Node(OctTreeOpImage.Cube cube)
      {
        this.cube = cube;
        parent = this;
        child = new Node[8];
        id = 0;
        level = 0;
        
        number_pixels = Integer.MAX_VALUE;
        
        mid_red = (maxColorNum + 1 >> 1);
        mid_green = (maxColorNum + 1 >> 1);
        mid_blue = (maxColorNum + 1 >> 1);
      }
      
      Node(Node parent, int id, int level) {
        cube = cube;
        this.parent = parent;
        child = new Node[8];
        this.id = id;
        this.level = level;
        

        cube.nodes += 1;
        if (level == cube.depth) {
          cube.colors += 1;
        }
        

        nchild += 1;
        child[id] = this;
        

        int bi = 1 << maxTreeDepth - level >> 1;
        mid_red += ((id & 0x1) > 0 ? bi : -bi);
        mid_green += ((id & 0x2) > 0 ? bi : -bi);
        mid_blue += ((id & 0x4) > 0 ? bi : -bi);
      }
      



      void pruneChild()
      {
        parent.nchild -= 1;
        parent.unique += unique;
        parent.total_red += total_red;
        parent.total_green += total_green;
        parent.total_blue += total_blue;
        parent.child[id] = null;
        cube.nodes -= 1;
        cube = null;
        parent = null;
      }
      


      void pruneLevel()
      {
        if (nchild != 0) {
          for (int id = 0; id < 8; id++) {
            if (child[id] != null) {
              child[id].pruneLevel();
            }
          }
        }
        if (level == cube.depth) {
          pruneChild();
        }
      }
      






      int reduce(int threshold, int next_threshold)
      {
        if (nchild != 0) {
          for (int id = 0; id < 8; id++) {
            if (child[id] != null) {
              next_threshold = child[id].reduce(threshold, next_threshold);
            }
          }
        }
        
        if (number_pixels <= threshold) {
          pruneChild();
        } else {
          if (unique != 0) {
            cube.colors += 1;
          }
          
          if (number_pixels < next_threshold) {
            next_threshold = number_pixels;
          }
        }
        
        return next_threshold;
      }
      





      void colormap()
      {
        if (nchild != 0) {
          for (int id = 0; id < 8; id++) {
            if (child[id] != null) {
              child[id].colormap();
            }
          }
        }
        if (unique != 0) {
          cube.colormap[0][cube.colors] = ((byte)((total_red + (unique >> 1)) / unique));
          
          cube.colormap[1][cube.colors] = ((byte)((total_green + (unique >> 1)) / unique));
          
          cube.colormap[2][cube.colors] = ((byte)((total_blue + (unique >> 1)) / unique));
          
          color_number = (cube.colors++);
        }
      }
    }
  }
}
