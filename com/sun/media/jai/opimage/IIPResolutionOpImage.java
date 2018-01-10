package com.sun.media.jai.opimage;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;





























public class IIPResolutionOpImage
  extends OpImage
{
  private static final int TILE_SIZE = 64;
  private static final int TILE_BLOCK_WIDTH = 8;
  private static final int TILE_BLOCK_HEIGHT = 2;
  private static final char BLANK = ' ';
  private static final char COLON = ':';
  private static final char SLASH = '/';
  private static final char CR = '\r';
  private static final char LF = '\n';
  private static final int CS_COLORLESS = 0;
  private static final int CS_MONOCHROME = 1;
  private static final int CS_PHOTOYCC = 2;
  private static final int CS_NIFRGB = 3;
  private static final int CS_PLANE_ALPHA = 32766;
  private static final int TILE_UNCOMPRESSED = 0;
  private static final int TILE_SINGLE_COLOR = 1;
  private static final int TILE_JPEG = 2;
  private static final int TILE_INVALID = -1;
  private static ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
  


  private String URLString;
  

  private int resolution;
  

  private int subImage;
  

  private int colorSpaceType;
  

  private boolean hasAlpha;
  

  private boolean isAlphaPremultilpied;
  

  private int minTileX;
  

  private int minTileY;
  

  private int numXTiles;
  

  private JPEGDecodeParam[] decodeParamCache = new JPEGDecodeParam['ÿ'];
  

  private boolean arePropertiesInitialized = false;
  

  private int tileBlockWidth = 8;
  private int tileBlockHeight = 2;
  

  private RenderingHints renderHints;
  


  private static final void YCbCrToNIFRGB(Raster raster)
  {
    byte[] data = ((DataBufferByte)raster.getDataBuffer()).getData();
    
    int offset = 0;
    int length = data.length;
    int MASK1 = 255;
    int MASK2 = 65280;
    if (raster.getSampleModel().getNumBands() == 3) {
      while (offset < length) {
        float Y = data[offset] & 0xFF;
        float Cb = data[(offset + 1)] & 0xFF;
        float Cr = data[(offset + 2)] & 0xFF;
        
        int R = (int)(Y + 1.402F * Cr - 178.255F);
        int G = (int)(Y - 0.34414F * Cb - 0.71414F * Cr + 135.4307F);
        int B = (int)(Y + 1.772F * Cb - 225.43F);
        
        int imask = R >> 5 & 0x18;
        data[(offset++)] = ((byte)((R & MASK1 >> imask | MASK2 >> imask) & 0xFF));
        

        imask = G >> 5 & 0x18;
        data[(offset++)] = ((byte)((G & MASK1 >> imask | MASK2 >> imask) & 0xFF));
        

        imask = B >> 5 & 0x18;
        data[(offset++)] = ((byte)((B & MASK1 >> imask | MASK2 >> imask) & 0xFF));
      }
    }
    
    while (offset < length) {
      float Y = data[offset] & 0xFF;
      float Cb = data[(offset + 1)] & 0xFF;
      float Cr = data[(offset + 2)] & 0xFF;
      
      int R = (int)(-Y - 1.402F * Cr - 433.255F);
      int G = (int)(-Y + 0.34414F * Cb + 0.71414F * Cr + 119.5693F);
      int B = (int)(-Y - 1.772F * Cb - 480.43F);
      
      int imask = R >> 5 & 0x18;
      data[(offset++)] = ((byte)((R & MASK1 >> imask | MASK2 >> imask) & 0xFF));
      

      imask = G >> 5 & 0x18;
      data[(offset++)] = ((byte)((G & MASK1 >> imask | MASK2 >> imask) & 0xFF));
      

      imask = B >> 5 & 0x18;
      data[(offset++)] = ((byte)((B & MASK1 >> imask | MASK2 >> imask) & 0xFF));
      

      offset++;
    }
  }
  







  private static InputStream postCommands(String URLSpec, String[] commands)
  {
    StringBuffer spec = new StringBuffer(URLSpec + "&OBJ=iip,1.0");
    
    if (commands != null)
    {
      for (int i = 0; i < commands.length; i++) {
        spec.append("&" + commands[i]);
      }
    }
    

    InputStream stream = null;
    try {
      URL url = new URL(spec.toString());
      stream = url.openStream();
    } catch (Exception e) {
      String message = JaiI18N.getString("IIPResolution4") + spec.toString();
      
      listener.errorOccurred(message, new ImagingException(message, e), IIPResolutionOpImage.class, false);
    }
    


    return stream;
  }
  






  private static String getLabel(InputStream stream)
  {
    boolean charsAppended = false;
    StringBuffer buf = new StringBuffer(16);
    try {
      int i;
      while ((i = stream.read()) != -1) {
        char c = (char)(0xFF & i);
        if ((c == '/') || (c == ':')) {
          break;
        }
        buf.append(c);
        charsAppended = true;
      }
    } catch (Exception e) {
      String message = JaiI18N.getString("IIPResolution5");
      
      listener.errorOccurred(message, new ImagingException(message, e), IIPResolutionOpImage.class, false);
    }
    


    return charsAppended ? buf.toString().toLowerCase() : null;
  }
  




  private static int getLength(InputStream stream)
  {
    return Integer.valueOf(getLabel(stream)).intValue();
  }
  



  private static InputStream checkError(String label, InputStream stream, boolean throwException)
  {
    String iipObjectResponse;
    

    if (label.equals("error")) {
      int length = Integer.valueOf(getLabel(stream)).intValue();
      byte[] b = new byte[length];
      try {
        stream.read(b);
      } catch (Exception e) {
        String message = JaiI18N.getString("IIPResolution6");
        
        listener.errorOccurred(message, new ImagingException(message, e), IIPResolutionOpImage.class, false);
      }
      

      String msg = new String(b);
      if (throwException) {
        throwIIPException(msg);
      } else {
        printIIPException(msg);
      }
    } else if (label.startsWith("iip"))
    {
      iipObjectResponse = getDataAsString(stream, false);
    }
    
    return stream;
  }
  




  private static byte[] getDataAsByteArray(InputStream stream)
  {
    int length = getLength(stream);
    byte[] b = new byte[length];
    try
    {
      stream.read(b);
      stream.read();
      stream.read();
    } catch (Exception e) {
      String message = JaiI18N.getString("IIPResolution7");
      listener.errorOccurred(message, new ImagingException(message, e), IIPResolutionOpImage.class, false);
    }
    


    return b;
  }
  



  private static String getDataAsString(InputStream stream, boolean hasLength)
  {
    String str = null;
    if (hasLength) {
      try {
        int length = getLength(stream);
        byte[] b = new byte[length];
        stream.read(b);
        stream.read();
        stream.read();
        str = new String(b);
      } catch (Exception e) {
        String message = JaiI18N.getString("IIPResolution7");
        listener.errorOccurred(message, new ImagingException(message, e), IIPResolutionOpImage.class, false);
      }
    }
    else
    {
      StringBuffer buf = new StringBuffer(16);
      try {
        int i;
        while ((i = stream.read()) != -1) {
          char c = (char)(0xFF & i);
          if (c == '\r') {
            stream.read();
            break;
          }
          buf.append(c);
        }
        str = buf.toString();
      } catch (Exception e) {
        String message = JaiI18N.getString("IIPResolution7");
        listener.errorOccurred(message, new ImagingException(message, e), IIPResolutionOpImage.class, false);
      }
    }
    


    return str;
  }
  



  private static void flushData(InputStream stream, boolean hasLength)
  {
    if (hasLength) {
      try {
        int length = getLength(stream);
        long numSkipped = stream.skip(length);
        if (numSkipped == length) {
          stream.read();
          stream.read();
        }
      } catch (Exception e) {
        String message = JaiI18N.getString("IIPResolution8");
        listener.errorOccurred(message, new ImagingException(message, e), IIPResolutionOpImage.class, false);
      }
    } else {
      try
      {
        int i;
        
        while ((i = stream.read()) != -1) {
          if ((char)(0xFF & i) == '\r') {
            stream.read();
          }
        }
      }
      catch (Exception e) {
        String message = JaiI18N.getString("IIPResolution8");
        listener.errorOccurred(message, new ImagingException(message, e), IIPResolutionOpImage.class, false);
      }
    }
  }
  





  private static int[] stringToIntArray(String s)
  {
    Vector v = new Vector();
    int lastBlank = 0;
    int nextBlank = s.indexOf(' ', 0);
    do {
      v.add(Integer.valueOf(s.substring(lastBlank, nextBlank)));
      lastBlank = nextBlank + 1;
      nextBlank = s.indexOf(' ', lastBlank);
    } while (nextBlank != -1);
    v.add(Integer.valueOf(s.substring(lastBlank)));
    

    int length = v.size();
    int[] intArray = new int[length];
    for (int i = 0; i < length; i++) {
      intArray[i] = ((Integer)v.get(i)).intValue();
    }
    
    return intArray;
  }
  



  private static float[] stringToFloatArray(String s)
  {
    Vector v = new Vector();
    int lastBlank = 0;
    int nextBlank = s.indexOf(' ', 0);
    do {
      v.add(Float.valueOf(s.substring(lastBlank, nextBlank)));
      lastBlank = nextBlank + 1;
      nextBlank = s.indexOf(' ', lastBlank);
    } while (nextBlank != -1);
    v.add(Float.valueOf(s.substring(lastBlank)));
    

    int length = v.size();
    float[] floatArray = new float[length];
    for (int i = 0; i < length; i++) {
      floatArray[i] = ((Float)v.get(i)).floatValue();
    }
    
    return floatArray;
  }
  


  private static String formatIIPErrorMessage(String msg)
  {
    return new String(JaiI18N.getString("IIPResolutionOpImage0") + " " + msg);
  }
  


  private static void throwIIPException(String msg)
  {
    throw new RuntimeException(formatIIPErrorMessage(msg));
  }
  


  private static void printIIPException(String msg)
  {
    System.err.println(formatIIPErrorMessage(msg));
  }
  

  private static void closeStream(InputStream stream)
  {
    try
    {
      stream.close();
    }
    catch (Exception e) {}
  }
  







  private static ImageLayout layoutHelper(String URLSpec, int level, int subImage)
  {
    ImageLayout il = new ImageLayout();
    

    il.setTileGridXOffset(0);
    il.setTileGridYOffset(0);
    

    il.setTileWidth(64);
    il.setTileHeight(64);
    

    il.setMinX(0);
    il.setMinY(0);
    


    int maxWidth = -1;
    int maxHeight = -1;
    int numRes = -1;
    int resolution = -1;
    String[] cmd = { "OBJ=Max-size", "OBJ=Resolution-number" };
    InputStream stream = postCommands(URLSpec, cmd);
    String label = null;
    while ((label = getLabel(stream)) != null) {
      if (label.equals("max-size")) {
        String data = getDataAsString(stream, false);
        int[] wh = stringToIntArray(data);
        maxWidth = wh[0];
        maxHeight = wh[1];
      } else if (label.equals("resolution-number")) {
        String data = getDataAsString(stream, false);
        numRes = Integer.valueOf(data).intValue();
        if (level < 0) {
          resolution = 0;
        } else if (level >= numRes) {
          resolution = numRes - 1;
        } else {
          resolution = level;
        }
      } else {
        checkError(label, stream, true);
      }
    }
    closeStream(stream);
    

    int w = maxWidth;
    int h = maxHeight;
    for (int i = numRes - 1; i > resolution; i--) {
      w = (w + 1) / 2;
      h = (h + 1) / 2;
    }
    il.setWidth(w);
    il.setHeight(h);
    

    boolean hasAlpha = false;
    boolean isAlphaPremultiplied = false;
    cmd = new String[] { "OBJ=Colorspace," + resolution + "," + subImage };
    stream = postCommands(URLSpec, cmd);
    int colorSpaceIndex = 0;
    int numBands = 0;
    while ((label = getLabel(stream)) != null) {
      if (label.startsWith("colorspace")) {
        int[] ia = stringToIntArray(getDataAsString(stream, false));
        numBands = ia[3];
        switch (ia[2]) {
        case 1: 
          colorSpaceIndex = 1003;
          break;
        case 2: 
          colorSpaceIndex = 1002;
          break;
        case 3: 
          colorSpaceIndex = 1000;
          break;
        default: 
          colorSpaceIndex = numBands < 3 ? 1003 : 1000;
        }
        
        for (int j = 1; j <= numBands; j++) {
          if (ia[(3 + j)] == 32766) {
            hasAlpha = true;
          }
        }
        isAlphaPremultiplied = ia[1] == 1;
      } else {
        checkError(label, stream, true);
      }
    }
    closeStream(stream);
    

    ColorSpace cs = ColorSpace.getInstance(colorSpaceIndex);
    int dtSize = DataBuffer.getDataTypeSize(0);
    int[] bits = new int[numBands];
    for (int i = 0; i < numBands; i++) {
      bits[i] = dtSize;
    }
    int transparency = hasAlpha ? 3 : 1;
    
    ColorModel cm = new ComponentColorModel(cs, bits, hasAlpha, isAlphaPremultiplied, transparency, 0);
    


    il.setColorModel(cm);
    

    int[] bandOffsets = new int[numBands];
    for (int i = 0; i < numBands; i++) {
      bandOffsets[i] = i;
    }
    il.setSampleModel(RasterFactory.createPixelInterleavedSampleModel(0, 64, 64, numBands, numBands * 64, bandOffsets));
    






    return il;
  }
  













  public IIPResolutionOpImage(Map config, String URLSpec, int level, int subImage)
  {
    super((Vector)null, layoutHelper(URLSpec, level, subImage), config, false);
    



    renderHints = ((RenderingHints)config);
    

    URLString = URLSpec;
    this.subImage = subImage;
    

    String[] cmd = { "OBJ=Resolution-number" };
    InputStream stream = postCommands(cmd);
    String label = null;
    while ((label = getLabel(stream)) != null) {
      if (label.equals("resolution-number")) {
        String data = getDataAsString(stream, false);
        int numRes = Integer.valueOf(data).intValue();
        if (level < 0) {
          resolution = 0;
        } else if (level >= numRes) {
          resolution = (numRes - 1);
        } else {
          resolution = level;
        }
      } else {
        checkError(label, stream, true);
      }
    }
    endResponse(stream);
    

    ColorSpace cs = colorModel.getColorSpace();
    if (cs.isCS_sRGB()) {
      colorSpaceType = 3;
    } else if (cs.equals(ColorSpace.getInstance(1003))) {
      colorSpaceType = 1;
    } else {
      colorSpaceType = 2;
    }
    hasAlpha = colorModel.hasAlpha();
    isAlphaPremultilpied = colorModel.isAlphaPremultiplied();
    minTileX = getMinTileX();
    minTileY = getMinTileY();
    numXTiles = getNumXTiles();
  }
  


  private InputStream postCommands(String[] commands)
  {
    return postCommands(URLString, commands);
  }
  



  private void endResponse(InputStream stream)
  {
    closeStream(stream);
  }
  






  public Raster computeTile(int tileX, int tileY)
  {
    Raster raster = null;
    



    if (((tileX - minTileX) % tileBlockWidth == 0) && ((tileY - minTileY) % tileBlockHeight == 0))
    {
      int endTileX = tileX + tileBlockWidth - 1;
      if (endTileX > getMaxTileX()) {
        endTileX = getMaxTileX();
      }
      int endTileY = tileY + tileBlockHeight - 1;
      if (endTileY > getMaxTileY()) {
        endTileY = getMaxTileY();
      }
      raster = getTileBlock(tileX, tileY, endTileX, endTileY);
    } else if ((raster = getTileFromCache(tileX, tileY)) == null) {
      raster = getTileBlock(tileX, tileY, tileX, tileY);
    }
    
    return raster;
  }
  







  private Point getTileXY(String label, Point xy)
  {
    int beginIndex = label.indexOf(",", label.indexOf(",") + 1) + 1;
    int endIndex = label.lastIndexOf(",");
    int tile = Integer.valueOf(label.substring(beginIndex, endIndex)).intValue();
    


    int tileX = (tile + minTileX) % numXTiles;
    int tileY = (tile + minTileX - tileX) / numXTiles + minTileY;
    

    if (xy == null) {
      xy = new Point(tileX, tileY);
    } else {
      xy.setLocation(tileX, tileY);
    }
    
    return xy;
  }
  





  private Raster getTileBlock(int upperLeftTileX, int upperLeftTileY, int lowerRightTileX, int lowerRightTileY)
  {
    int startTile = (upperLeftTileY - minTileY) * numXTiles + upperLeftTileX - minTileX;
    
    int endTile = (lowerRightTileY - minTileY) * numXTiles + lowerRightTileX - minTileX;
    

    String cmd = null;
    if (startTile == endTile) {
      cmd = new String("til=" + resolution + "," + startTile + "," + subImage);
    } else {
      cmd = new String("til=" + resolution + "," + startTile + "-" + endTile + "," + subImage);
    }
    
    InputStream stream = postCommands(new String[] { cmd });
    int compressionType = -1;
    int compressionSubType = -1;
    byte[] data = null;
    String label = null;
    Raster upperLeftTile = null;
    Point tileXY = new Point();
    while ((label = getLabel(stream)) != null) {
      if (label.startsWith("tile")) {
        int length = getLength(stream);
        
        byte[] header = new byte[8];
        try {
          stream.read(header);
        } catch (Exception e) {
          throwIIPException(JaiI18N.getString("IIPResolutionOpImage1"));
        }
        
        length -= 8;
        
        compressionType = header[3] << 24 | header[2] << 16 | header[1] << 8 | header[0];
        



        compressionSubType = header[7] << 24 | header[6] << 16 | header[5] << 8 | header[4];
        




        if (length != 0) {
          data = new byte[length];
          try {
            int numBytesRead = 0;
            int offset = 0;
            do {
              numBytesRead = stream.read(data, offset, length - offset);
              
              offset += numBytesRead;
            } while ((offset < length) && (numBytesRead != -1));
            if (numBytesRead != -1) {
              stream.read();
              stream.read();
            }
          } catch (Exception e) {
            throwIIPException(JaiI18N.getString("IIPResolutionOpImage2"));
          }
        }
        
        getTileXY(label, tileXY);
        int tileX = (int)tileXY.getX();
        int tileY = (int)tileXY.getY();
        int tx = tileXToX(tileX);
        int ty = tileYToY(tileY);
        
        Raster raster = null;
        
        switch (compressionType) {
        case 0: 
          raster = getUncompressedTile(tx, ty, data);
          break;
        case 1: 
          raster = getSingleColorTile(tx, ty, compressionSubType);
          break;
        case 2: 
          raster = getJPEGTile(tx, ty, compressionSubType, data);
          break;
        case -1: 
        default: 
          raster = createWritableRaster(sampleModel, new Point(tx, ty));
        }
        
        

        if ((tileX == upperLeftTileX) && (tileY == upperLeftTileY)) {
          upperLeftTile = raster;
        }
        else {
          addTileToCache(tileX, tileY, raster);
        }
      } else {
        checkError(label, stream, true);
      }
    }
    
    endResponse(stream);
    
    return upperLeftTile;
  }
  


  private Raster getUncompressedTile(int tx, int ty, byte[] data)
  {
    DataBuffer dataBuffer = new DataBufferByte(data, data.length);
    
    return Raster.createRaster(sampleModel, dataBuffer, new Point(tx, ty));
  }
  


  private Raster getSingleColorTile(int tx, int ty, int color)
  {
    byte R = (byte)(color & 0xFF);
    byte G = (byte)(color >> 8 & 0xFF);
    byte B = (byte)(color >> 16 & 0xFF);
    byte A = (byte)(color >> 24 & 0xFF);
    
    int numBands = sampleModel.getNumBands();
    int length = tileWidth * tileHeight * numBands;
    byte[] data = new byte[length];
    int i = 0;
    switch (numBands) {
    }
    while (i < length) {
      data[(i++)] = R; continue;
      


      while (i < length) {
        data[(i++)] = R;
        data[(i++)] = A; continue;
        


        while (i < length) {
          data[(i++)] = R;
          data[(i++)] = G;
          data[(i++)] = B;
        }
        

        while (i < length) {
          data[(i++)] = R;
          data[(i++)] = G;
          data[(i++)] = B;
          data[(i++)] = A;
        }
      }
    }
    DataBuffer dataBuffer = new DataBufferByte(data, data.length);
    
    return Raster.createRaster(sampleModel, dataBuffer, new Point(tx, ty));
  }
  


  private Raster getJPEGTile(int tx, int ty, int subType, byte[] data)
  {
    int tableIndex = subType >> 24 & 0xFF;
    boolean colorConversion = (subType & 0xFF0000) != 0;
    JPEGDecodeParam decodeParam = null;
    if (tableIndex != 0) {
      decodeParam = getJPEGDecodeParam(tableIndex);
    }
    
    ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
    JPEGImageDecoder decoder = decodeParam == null ? JPEGCodec.createJPEGDecoder(byteStream) : JPEGCodec.createJPEGDecoder(byteStream, decodeParam);
    


    Raster raster = null;
    try {
      raster = decoder.decodeAsRaster().createTranslatedChild(tx, ty);
    }
    catch (Exception e) {
      ImagingListener listener = ImageUtil.getImagingListener(renderHints);
      
      listener.errorOccurred(JaiI18N.getString("IIPResolutionOpImage3"), new ImagingException(e), this, false);
    }
    






    closeStream(byteStream);
    
    if ((colorSpaceType == 3) && (colorConversion)) {
      YCbCrToNIFRGB(raster);
    }
    
    return raster;
  }
  





  private synchronized JPEGDecodeParam getJPEGDecodeParam(int tableIndex)
  {
    JPEGDecodeParam decodeParam = decodeParamCache[(tableIndex - 1)];
    
    if (decodeParam == null) {
      String cmd = new String("OBJ=Comp-group,2," + tableIndex);
      
      InputStream stream = postCommands(new String[] { cmd });
      String label = null;
      while ((label = getLabel(stream)) != null) {
        if (label.startsWith("comp-group")) {
          byte[] table = getDataAsByteArray(stream);
          ByteArrayInputStream tableStream = new ByteArrayInputStream(table);
          
          JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(tableStream);
          
          try
          {
            decoder.decodeAsRaster();
          }
          catch (Exception e) {}
          
          decodeParam = decoder.getJPEGDecodeParam();
        } else {
          checkError(label, stream, true);
        }
      }
      
      endResponse(stream);
      
      if (decodeParam != null) {
        decodeParamCache[(tableIndex - 1)] = decodeParam;
      }
    }
    
    return decodeParam;
  }
  



  private synchronized void initializeIIPProperties()
  {
    if (!arePropertiesInitialized) {
      String[] cmd = { "OBJ=IIP", "OBJ=Basic-info", "OBJ=View-info", "OBJ=Summary-info", "OBJ=Copyright" };
      

      InputStream stream = postCommands(cmd);
      String label = null;
      while ((label = getLabel(stream)) != null) {
        String name = label;
        Object value = null;
        if (label.equals("error")) {
          flushData(stream, true);
        } else if ((label.startsWith("colorspace")) || (label.equals("max-size")))
        {
          if (label.startsWith("colorspace")) {
            name = "colorspace";
          }
          value = stringToIntArray(getDataAsString(stream, false));
        } else if (label.equals("resolution-number")) {
          value = Integer.valueOf(getDataAsString(stream, false));
        } else if ((label.equals("aspect-ratio")) || (label.equals("contrast-adjust")) || (label.equals("filtering-value")))
        {

          value = Float.valueOf(getDataAsString(stream, false));
        } else if (label.equals("affine-transform")) {
          float[] a = (float[])stringToFloatArray(getDataAsString(stream, false));
          

          value = new AffineTransform(a[0], a[1], a[3], a[4], a[5], a[7]);
        }
        else if (label.equals("color-twist")) {
          value = stringToFloatArray(getDataAsString(stream, false));
        } else if (label.equals("roi")) {
          name = "roi-iip";
          float[] rect = stringToFloatArray(getDataAsString(stream, false));
          
          value = new Rectangle2D.Float(rect[0], rect[1], rect[2], rect[3]);
        }
        else if ((label.equals("copyright")) || (label.equals("title")) || (label.equals("subject")) || (label.equals("author")) || (label.equals("keywords")) || (label.equals("comment")) || (label.equals("last-author")) || (label.equals("rev-number")) || (label.equals("app-name")))
        {







          value = getDataAsString(stream, true);
        } else if ((label.equals("iip")) || (label.equals("iip-server")) || (label.equals("edit-time")) || (label.equals("last-printed")) || (label.equals("create-dtm")) || (label.equals("last-save-dtm")))
        {




          value = getDataAsString(stream, false);
        } else {
          flushData(stream, false);
        }
        if ((name != null) && (value != null)) {
          setProperty(name, value);
        }
      }
      endResponse(stream);
      arePropertiesInitialized = true;
    }
  }
  


  public String[] getPropertyNames()
  {
    initializeIIPProperties();
    return super.getPropertyNames();
  }
  


  public Object getProperty(String name)
  {
    initializeIIPProperties();
    return super.getProperty(name);
  }
  








  public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex)
  {
    throw new IllegalArgumentException(JaiI18N.getString("AreaOpImage0"));
  }
  








  public Rectangle mapDestRect(Rectangle destRect, int sourceIndex)
  {
    throw new IllegalArgumentException(JaiI18N.getString("AreaOpImage0"));
  }
  

  protected void finalize()
    throws Throwable
  {
    super.finalize();
  }
}
