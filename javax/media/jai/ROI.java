package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;





































public class ROI
  implements Serializable
{
  private transient RandomIter iter = null;
  

  transient PlanarImage theImage = null;
  

  int threshold = 127;
  










  protected static LinkedList mergeRunLengthList(LinkedList rectList)
  {
    if (rectList == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    if (rectList.size() > 1)
    {


      for (int mergeIndex = 0; 
          mergeIndex < rectList.size() - 1; 
          mergeIndex++)
      {
        ListIterator rectIter = rectList.listIterator(mergeIndex);
        Rectangle mergeRect = (Rectangle)rectIter.next();
        
        while (rectIter.hasNext()) {
          Rectangle runRect = (Rectangle)rectIter.next();
          

          int abuttingY = y + height;
          
          if ((y == abuttingY) && (x == x) && (width == width))
          {

            mergeRect = new Rectangle(x, y, width, height + height);
            




            rectIter.remove();
            

            rectList.set(mergeIndex, mergeRect);
          } else { if (y > abuttingY) {
              break;
            }
          }
        }
      }
    }
    




    return rectList;
  }
  








  protected ROI() {}
  







  public ROI(RenderedImage im)
  {
    this(im, 127);
  }
  










  public ROI(RenderedImage im, int threshold)
  {
    if (im == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    SampleModel sm = im.getSampleModel();
    
    if (sm.getNumBands() != 1) {
      throw new IllegalArgumentException(JaiI18N.getString("ROI0"));
    }
    
    this.threshold = threshold;
    


    if ((threshold >= 1) && (ImageUtil.isBinary(sm))) {
      theImage = PlanarImage.wrapRenderedImage(im);

    }
    else
    {
      ParameterBlockJAI pbj = new ParameterBlockJAI("binarize");
      
      pbj.setSource("source0", im);
      pbj.setParameter("threshold", threshold);
      
      theImage = JAI.create("binarize", pbj, null);
    }
  }
  
  private RandomIter getIter()
  {
    if (iter == null) {
      iter = RandomIterFactory.create(theImage, null);
    }
    return iter;
  }
  
  public int getThreshold()
  {
    return threshold;
  }
  
  public void setThreshold(int threshold)
  {
    this.threshold = threshold;
    ((RenderedOp)theImage).setParameter(threshold, 0);
    iter = null;
    getIter();
  }
  
  public Rectangle getBounds()
  {
    return new Rectangle(theImage.getMinX(), theImage.getMinY(), theImage.getWidth(), theImage.getHeight());
  }
  



  public Rectangle2D getBounds2D()
  {
    return new Rectangle2D.Float(theImage.getMinX(), theImage.getMinY(), theImage.getWidth(), theImage.getHeight());
  }
  









  public boolean contains(Point p)
  {
    if (p == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return contains(x, y);
  }
  






  public boolean contains(Point2D p)
  {
    if (p == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return contains((int)p.getX(), (int)p.getY());
  }
  






  public boolean contains(int x, int y)
  {
    int minX = theImage.getMinX();
    int minY = theImage.getMinY();
    
    return (x >= minX) && (x < minX + theImage.getWidth()) && (y >= minY) && (y < minY + theImage.getHeight()) && (getIter().getSample(x, y, 0) >= 1);
  }
  










  public boolean contains(double x, double y)
  {
    return contains((int)x, (int)y);
  }
  









  public boolean contains(Rectangle rect)
  {
    if (rect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (!rect.equals(rect.intersection(getBounds()))) {
      return false;
    }
    
    byte[] packedData = ImageUtil.getPackedBinaryData(theImage.getData(), rect);
    




    int leftover = width % 8;
    
    if (leftover == 0)
    {
      for (int i = 0; i < packedData.length; i++) {
        if ((packedData[i] & 0xFF) != 255) {
          return false;
        }
      }
    } else {
      int mask = (1 << leftover) - 1 << 8 - leftover;
      
      int y = 0; for (int k = 0; y < height; y++) {
        for (int x = 0; x < width - leftover; k++) {
          if ((packedData[k] & 0xFF) != 255) {
            return false;
          }
          x += 8;
        }
        


        if ((packedData[k] & mask) != mask) {
          return false;
        }
        k++;
      }
    }
    
    return true;
  }
  









  public boolean contains(Rectangle2D rect)
  {
    if (rect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    Rectangle r = new Rectangle((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
    


    return contains(r);
  }
  










  public boolean contains(int x, int y, int w, int h)
  {
    Rectangle r = new Rectangle(x, y, w, h);
    return contains(r);
  }
  











  public boolean contains(double x, double y, double w, double h)
  {
    Rectangle rect = new Rectangle((int)x, (int)y, (int)w, (int)h);
    
    return contains(rect);
  }
  








  public boolean intersects(Rectangle rect)
  {
    if (rect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Rectangle r = rect.intersection(getBounds());
    
    if (r.isEmpty()) {
      return false;
    }
    
    byte[] packedData = ImageUtil.getPackedBinaryData(theImage.getData(), r);
    




    int leftover = width % 8;
    
    if (leftover == 0)
    {
      for (int i = 0; i < packedData.length; i++) {
        if ((packedData[i] & 0xFF) != 0) {
          return true;
        }
      }
    } else {
      int mask = (1 << leftover) - 1 << 8 - leftover;
      
      int y = 0; for (int k = 0; y < height; y++) {
        for (int x = 0; x < width - leftover; k++) {
          if ((packedData[k] & 0xFF) != 0) {
            return true;
          }
          x += 8;
        }
        

        if ((packedData[k] & mask) != 0)
          return true;
        k++;
      }
    }
    
    return false;
  }
  








  public boolean intersects(Rectangle2D r)
  {
    if (r == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Rectangle rect = new Rectangle((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
    


    return intersects(rect);
  }
  









  public boolean intersects(int x, int y, int w, int h)
  {
    Rectangle rect = new Rectangle(x, y, w, h);
    return intersects(rect);
  }
  









  public boolean intersects(double x, double y, double w, double h)
  {
    Rectangle rect = new Rectangle((int)x, (int)y, (int)w, (int)h);
    
    return intersects(rect);
  }
  




  private static PlanarImage createBinaryImage(Rectangle r)
  {
    if ((x == 0) && (y == 0))
    {
      BufferedImage bi = new BufferedImage(width, height, 12);
      


      return PlanarImage.wrapRenderedImage(bi);
    }
    

    SampleModel sm = new MultiPixelPackedSampleModel(0, width, height, 1);
    



    return new TiledImage(x, y, width, height, x, y, sm, PlanarImage.createColorModel(sm));
  }
  











  private ROI createOpROI(ROI roi, String op)
  {
    if (roi == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    PlanarImage imThis = getAsImage();
    PlanarImage imROI = roi.getAsImage();
    

    Rectangle boundsThis = imThis.getBounds();
    Rectangle boundsROI = imROI.getBounds();
    
    PlanarImage imDest;
    
    PlanarImage imDest;
    
    if ((op.equals("and")) || (boundsThis.equals(boundsROI))) {
      imDest = JAI.create(op, imThis, imROI);
    } else { PlanarImage imDest;
      if ((op.equals("subtract")) || (boundsThis.contains(boundsROI)))
      {
        PlanarImage imBounds = createBinaryImage(boundsThis);
        
        imBounds = JAI.create("overlay", imBounds, imROI);
        imDest = JAI.create(op, imThis, imBounds);
      } else { PlanarImage imDest;
        if (boundsROI.contains(boundsThis))
        {
          PlanarImage imBounds = createBinaryImage(boundsROI);
          
          imBounds = JAI.create("overlay", imBounds, imThis);
          imDest = JAI.create(op, imBounds, imROI);
        }
        else
        {
          Rectangle merged = boundsThis.union(boundsROI);
          
          PlanarImage imBoundsThis = createBinaryImage(merged);
          PlanarImage imBoundsROI = createBinaryImage(merged);
          
          imBoundsThis = JAI.create("overlay", imBoundsThis, imThis);
          imBoundsROI = JAI.create("overlay", imBoundsROI, imROI);
          imDest = JAI.create(op, imBoundsThis, imBoundsROI);
        }
      } }
    return new ROI(imDest, threshold);
  }
  










  public ROI add(ROI roi)
  {
    return createOpROI(roi, "add");
  }
  










  public ROI subtract(ROI roi)
  {
    return createOpROI(roi, "subtract");
  }
  









  public ROI intersect(ROI roi)
  {
    return createOpROI(roi, "and");
  }
  











  public ROI exclusiveOr(ROI roi)
  {
    return createOpROI(roi, "xor");
  }
  











  public ROI transform(AffineTransform at, Interpolation interp)
  {
    if (at == null) {
      throw new IllegalArgumentException(JaiI18N.getString("ROI5"));
    }
    
    if (interp == null) {
      throw new IllegalArgumentException(JaiI18N.getString("ROI6"));
    }
    
    ParameterBlock paramBlock = new ParameterBlock();
    paramBlock.add(at);
    paramBlock.add(interp);
    return performImageOp("Affine", paramBlock, 0, null);
  }
  








  public ROI transform(AffineTransform at)
  {
    if (at == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return transform(at, Interpolation.getInstance(0));
  }
  























  public ROI performImageOp(RenderedImageFactory RIF, ParameterBlock paramBlock, int sourceIndex, RenderingHints renderHints)
  {
    if ((RIF == null) || (paramBlock == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    ParameterBlock pb = (ParameterBlock)paramBlock.clone();
    Vector sources = pb.getSources();
    sources.insertElementAt(getAsImage(), sourceIndex);
    


    RenderedImage im = RIF.create(pb, renderHints);
    return new ROI(im, threshold);
  }
  






















  public ROI performImageOp(String name, ParameterBlock paramBlock, int sourceIndex, RenderingHints renderHints)
  {
    if ((name == null) || (paramBlock == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    ParameterBlock pb = (ParameterBlock)paramBlock.clone();
    Vector sources = pb.getSources();
    sources.insertElementAt(getAsImage(), sourceIndex);
    


    RenderedImage im = JAI.create(name, pb, renderHints);
    return new ROI(im, threshold);
  }
  








  public Shape getAsShape()
  {
    return null;
  }
  







  public PlanarImage getAsImage()
  {
    return theImage;
  }
  






























  public int[][] getAsBitmask(int x, int y, int width, int height, int[][] mask)
  {
    Rectangle rect = getBounds().intersection(new Rectangle(x, y, width, height));
    


    if (rect.isEmpty()) {
      return (int[][])null;
    }
    

    int bitmaskIntWidth = (width + 31) / 32;
    

    if (mask == null) {
      mask = new int[height][bitmaskIntWidth];
    } else if ((mask.length < height) || (mask[0].length < bitmaskIntWidth)) {
      throw new RuntimeException(JaiI18N.getString("ROI3"));
    }
    
    byte[] data = ImageUtil.getPackedBinaryData(theImage.getData(), rect);
    




    int leftover = width % 8;
    
    if (leftover != 0) {
      int datamask = (1 << leftover) - 1 << 8 - leftover;
      int linestride = (width + 7) / 8;
      
      for (int i = linestride - 1; i < data.length; i += linestride) {
        data[i] = ((byte)(data[i] & datamask));
      }
    }
    
    int lineStride = (width + 7) / 8;
    int leftOver = lineStride % 4;
    

    int ncols = (lineStride - leftOver) / 4;
    
    int row = 0; for (int k = 0; row < height; row++) {
      int[] maskRow = mask[row];
      
      for (int col = 0; col < ncols; col++) {
        maskRow[col] = ((data[k] & 0xFF) << 24 | (data[(k + 1)] & 0xFF) << 16 | (data[(k + 2)] & 0xFF) << 8 | (data[(k + 3)] & 0xFF) << 0);
        


        k += 4;
      }
      
      switch (leftOver) {
      case 0: 
        break; case 1:  maskRow[(col++)] = ((data[k] & 0xFF) << 24);
        break;
      case 2:  maskRow[(col++)] = ((data[k] & 0xFF) << 24 | (data[(k + 1)] & 0xFF) << 16);
        
        break;
      case 3:  maskRow[(col++)] = ((data[k] & 0xFF) << 24 | (data[(k + 1)] & 0xFF) << 16 | (data[(k + 2)] & 0xFF) << 8);
      }
      
      


      k += leftOver;
      
      Arrays.fill(maskRow, col, bitmaskIntWidth, 0);
    }
    

    for (row = height; row < height; row++) {
      Arrays.fill(mask[row], 0);
    }
    
    return mask;
  }
  














  public LinkedList getAsRectangleList(int x, int y, int width, int height)
  {
    return getAsRectangleList(x, y, width, height, true);
  }
  

















  protected LinkedList getAsRectangleList(int x, int y, int width, int height, boolean mergeRectangles)
  {
    Rectangle bounds = getBounds();
    Rectangle rect = new Rectangle(x, y, width, height);
    if (!bounds.intersects(rect)) {
      return null;
    }
    

    if (!bounds.contains(rect)) {
      rect = bounds.intersection(rect);
      x = x;
      y = y;
      width = width;
      height = height;
    }
    
    byte[] data = ImageUtil.getPackedBinaryData(theImage.getData(), rect);
    




    int lineStride = (width + 7) / 8;
    int leftover = width % 8;
    int mask = leftover == 0 ? 255 : (1 << leftover) - 1 << 8 - leftover;
    

    LinkedList rectList = new LinkedList();
    





    int row = 0; for (int k = 0; row < height; row++)
    {
      int start = -1;
      
      int col = 0; for (int cnt = 0; col < lineStride; k++)
      {
        int val = data[k] & (col == lineStride - 1 ? mask : 255);
        
        if (val == 0) {
          if (start >= 0) {
            rectList.addLast(new Rectangle(x + start, y + row, col * 8 - start, 1));
            
            start = -1;
          }
        }
        else if (val == 255) {
          if (start < 0) {
            start = col * 8;
          }
        }
        else {
          for (int bit = 7; bit >= 0; bit--) {
            if ((val & 1 << bit) == 0) {
              if (start >= 0) {
                rectList.addLast(new Rectangle(x + start, y + row, col * 8 + (7 - bit) - start, 1));
                
                start = -1;
              }
            }
            else if (start < 0) {
              start = col * 8 + (7 - bit);
            }
          }
        }
        col++;
      }
      






























      if (start >= 0) {
        rectList.addLast(new Rectangle(x + start, y + row, col * 8 - start, 1));
      }
    }
    


    return mergeRectangles ? mergeRunLengthList(rectList) : rectList;
  }
  



  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    if (theImage != null) {
      out.writeBoolean(true);
      RenderingHints hints = new RenderingHints(null);
      hints.put(JAI.KEY_SERIALIZE_DEEP_COPY, new Boolean(true));
      out.writeObject(SerializerFactory.getState(theImage, hints));
    } else {
      out.writeBoolean(false);
    }
  }
  




  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    if (in.readBoolean()) {
      SerializableState ss = (SerializableState)in.readObject();
      RenderedImage ri = (RenderedImage)ss.getObject();
      theImage = PlanarImage.wrapRenderedImage(ri);
    } else {
      theImage = null;
    }
    iter = null;
  }
}
