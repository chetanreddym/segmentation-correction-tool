package javax.media.jai;

import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;



































































public class ImageLayout
  implements Cloneable, Serializable
{
  public static final int MIN_X_MASK = 1;
  public static final int MIN_Y_MASK = 2;
  public static final int WIDTH_MASK = 4;
  public static final int HEIGHT_MASK = 8;
  public static final int TILE_GRID_X_OFFSET_MASK = 16;
  public static final int TILE_GRID_Y_OFFSET_MASK = 32;
  public static final int TILE_WIDTH_MASK = 64;
  public static final int TILE_HEIGHT_MASK = 128;
  public static final int SAMPLE_MODEL_MASK = 256;
  public static final int COLOR_MODEL_MASK = 512;
  int minX = 0;
  

  int minY = 0;
  

  int width = 0;
  

  int height = 0;
  

  int tileGridXOffset = 0;
  

  int tileGridYOffset = 0;
  

  int tileWidth = 0;
  

  int tileHeight = 0;
  

  transient SampleModel sampleModel = null;
  

  transient ColorModel colorModel = null;
  

  protected int validMask = 0;
  












  public ImageLayout() {}
  












  public ImageLayout(int minX, int minY, int width, int height, int tileGridXOffset, int tileGridYOffset, int tileWidth, int tileHeight, SampleModel sampleModel, ColorModel colorModel)
  {
    setMinX(minX);
    setMinY(minY);
    setWidth(width);
    setHeight(height);
    setTileGridXOffset(tileGridXOffset);
    setTileGridYOffset(tileGridYOffset);
    setTileWidth(tileWidth);
    setTileHeight(tileHeight);
    if (sampleModel != null) {
      setSampleModel(sampleModel);
    }
    if (colorModel != null) {
      setColorModel(colorModel);
    }
  }
  












  public ImageLayout(int minX, int minY, int width, int height)
  {
    setMinX(minX);
    setMinY(minY);
    setWidth(width);
    setHeight(height);
  }
  
















  public ImageLayout(int tileGridXOffset, int tileGridYOffset, int tileWidth, int tileHeight, SampleModel sampleModel, ColorModel colorModel)
  {
    setTileGridXOffset(tileGridXOffset);
    setTileGridYOffset(tileGridYOffset);
    setTileWidth(tileWidth);
    setTileHeight(tileHeight);
    if (sampleModel != null) {
      setSampleModel(sampleModel);
    }
    if (colorModel != null) {
      setColorModel(colorModel);
    }
  }
  





  public ImageLayout(RenderedImage im)
  {
    this(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight(), im.getTileGridXOffset(), im.getTileGridYOffset(), im.getTileWidth(), im.getTileHeight(), im.getSampleModel(), im.getColorModel());
  }
  




















  public int getValidMask()
  {
    return validMask;
  }
  





  public final boolean isValid(int mask)
  {
    return (validMask & mask) == mask;
  }
  






  public ImageLayout setValid(int mask)
  {
    validMask |= mask;
    return this;
  }
  









  public ImageLayout unsetValid(int mask)
  {
    validMask &= (mask ^ 0xFFFFFFFF);
    return this;
  }
  





  public ImageLayout unsetImageBounds()
  {
    unsetValid(15);
    


    return this;
  }
  





  public ImageLayout unsetTileLayout()
  {
    unsetValid(240);
    


    return this;
  }
  







  public int getMinX(RenderedImage fallback)
  {
    if (isValid(1)) {
      return minX;
    }
    if (fallback == null) {
      return 0;
    }
    return fallback.getMinX();
  }
  







  public ImageLayout setMinX(int minX)
  {
    this.minX = minX;
    setValid(1);
    return this;
  }
  







  public int getMinY(RenderedImage fallback)
  {
    if (isValid(2)) {
      return minY;
    }
    if (fallback == null) {
      return 0;
    }
    return fallback.getMinY();
  }
  







  public ImageLayout setMinY(int minY)
  {
    this.minY = minY;
    setValid(2);
    return this;
  }
  







  public int getWidth(RenderedImage fallback)
  {
    if (isValid(4)) {
      return width;
    }
    if (fallback == null) {
      return 0;
    }
    return fallback.getWidth();
  }
  








  public ImageLayout setWidth(int width)
  {
    if (width <= 0) {
      throw new IllegalArgumentException(JaiI18N.getString("ImageLayout0"));
    }
    this.width = width;
    setValid(4);
    return this;
  }
  







  public int getHeight(RenderedImage fallback)
  {
    if (isValid(8)) {
      return height;
    }
    if (fallback == null) {
      return 0;
    }
    return fallback.getHeight();
  }
  








  public ImageLayout setHeight(int height)
  {
    if (height <= 0) {
      throw new IllegalArgumentException(JaiI18N.getString("ImageLayout0"));
    }
    this.height = height;
    setValid(8);
    return this;
  }
  







  public int getTileGridXOffset(RenderedImage fallback)
  {
    if (isValid(16)) {
      return tileGridXOffset;
    }
    if (fallback == null) {
      return 0;
    }
    return fallback.getTileGridXOffset();
  }
  







  public ImageLayout setTileGridXOffset(int tileGridXOffset)
  {
    this.tileGridXOffset = tileGridXOffset;
    setValid(16);
    return this;
  }
  







  public int getTileGridYOffset(RenderedImage fallback)
  {
    if (isValid(32)) {
      return tileGridYOffset;
    }
    if (fallback == null) {
      return 0;
    }
    return fallback.getTileGridYOffset();
  }
  







  public ImageLayout setTileGridYOffset(int tileGridYOffset)
  {
    this.tileGridYOffset = tileGridYOffset;
    setValid(32);
    return this;
  }
  







  public int getTileWidth(RenderedImage fallback)
  {
    if (isValid(64)) {
      return tileWidth;
    }
    if (fallback == null) {
      return 0;
    }
    return fallback.getTileWidth();
  }
  









  public ImageLayout setTileWidth(int tileWidth)
  {
    if (tileWidth <= 0) {
      throw new IllegalArgumentException(JaiI18N.getString("ImageLayout0"));
    }
    this.tileWidth = tileWidth;
    setValid(64);
    return this;
  }
  







  public int getTileHeight(RenderedImage fallback)
  {
    if (isValid(128)) {
      return tileHeight;
    }
    if (fallback == null) {
      return 0;
    }
    return fallback.getTileHeight();
  }
  









  public ImageLayout setTileHeight(int tileHeight)
  {
    if (tileHeight <= 0) {
      throw new IllegalArgumentException(JaiI18N.getString("ImageLayout0"));
    }
    this.tileHeight = tileHeight;
    setValid(128);
    return this;
  }
  







  public SampleModel getSampleModel(RenderedImage fallback)
  {
    if (isValid(256)) {
      return sampleModel;
    }
    if (fallback == null) {
      return null;
    }
    return fallback.getSampleModel();
  }
  







  public ImageLayout setSampleModel(SampleModel sampleModel)
  {
    this.sampleModel = sampleModel;
    setValid(256);
    return this;
  }
  







  public ColorModel getColorModel(RenderedImage fallback)
  {
    if (isValid(512)) {
      return colorModel;
    }
    if (fallback == null) {
      return null;
    }
    return fallback.getColorModel();
  }
  







  public ImageLayout setColorModel(ColorModel colorModel)
  {
    this.colorModel = colorModel;
    setValid(512);
    return this;
  }
  
  public String toString()
  {
    String s = "ImageLayout[";
    boolean first = true;
    
    if (isValid(1)) {
      s = s + "MIN_X=" + minX;
      first = false;
    }
    
    if (isValid(2)) {
      if (!first) {
        s = s + ", ";
      }
      s = s + "MIN_Y=" + minY;
      first = false;
    }
    
    if (isValid(4)) {
      if (!first) {
        s = s + ", ";
      }
      s = s + "WIDTH=" + width;
      first = false;
    }
    
    if (isValid(8)) {
      if (!first) {
        s = s + ", ";
      }
      s = s + "HEIGHT=" + height;
      first = false;
    }
    
    if (isValid(16)) {
      if (!first) {
        s = s + ", ";
      }
      s = s + "TILE_GRID_X_OFFSET=" + tileGridXOffset;
      first = false;
    }
    
    if (isValid(32)) {
      if (!first) {
        s = s + ", ";
      }
      s = s + "TILE_GRID_Y_OFFSET=" + tileGridYOffset;
      first = false;
    }
    
    if (isValid(64)) {
      if (!first) {
        s = s + ", ";
      }
      s = s + "TILE_WIDTH=" + tileWidth;
      first = false;
    }
    
    if (isValid(128)) {
      if (!first) {
        s = s + ", ";
      }
      s = s + "TILE_HEIGHT=" + tileHeight;
      first = false;
    }
    
    if (isValid(256)) {
      if (!first) {
        s = s + ", ";
      }
      s = s + "SAMPLE_MODEL=" + sampleModel;
      first = false;
    }
    
    if (isValid(512)) {
      if (!first) {
        s = s + ", ";
      }
      s = s + "COLOR_MODEL=" + colorModel;
    }
    
    s = s + "]";
    return s;
  }
  

  public Object clone()
  {
    try
    {
      return super.clone();
    } catch (CloneNotSupportedException e) {}
    return null;
  }
  




  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    

    if (isValid(256)) {
      out.writeObject(SerializerFactory.getState(sampleModel, null));
    }
    

    if (isValid(512)) {
      out.writeObject(SerializerFactory.getState(colorModel, null));
    }
  }
  




  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    

    if (isValid(256)) {
      Object object = in.readObject();
      if (!(object instanceof SerializableState)) {
        sampleModel = null;
      }
      SerializableState ss = (SerializableState)object;
      Class c = ss.getObjectClass();
      if (SampleModel.class.isAssignableFrom(c)) {
        sampleModel = ((SampleModel)ss.getObject());
      } else {
        sampleModel = null;
      }
    }
    
    if (isValid(512)) {
      Object object = in.readObject();
      if (!(object instanceof SerializableState)) {
        colorModel = null;
      }
      SerializableState ss = (SerializableState)object;
      Class c = ss.getObjectClass();
      if (ColorModel.class.isAssignableFrom(c)) {
        colorModel = ((ColorModel)ss.getObject());
      } else {
        colorModel = null;
      }
    }
  }
  











  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ImageLayout)) {
      return false;
    }
    ImageLayout il = (ImageLayout)obj;
    
    return (validMask == validMask) && (width == width) && (height == height) && (minX == minX) && (minY == minY) && (tileHeight == tileHeight) && (tileWidth == tileWidth) && (tileGridXOffset == tileGridXOffset) && (tileGridYOffset == tileGridYOffset) && (sampleModel.equals(sampleModel)) && (colorModel.equals(colorModel));
  }
  

















  public int hashCode()
  {
    int code = 0;int i = 1;
    


    code += width * i++;
    code += height * i++;
    code += minX * i++;
    code += minY * i++;
    code += tileHeight * i++;
    code += tileWidth * i++;
    code += tileGridXOffset * i++;
    code += tileGridYOffset * i++;
    
    code ^= sampleModel.hashCode();
    code ^= validMask;
    code ^= colorModel.hashCode();
    
    return code;
  }
}
