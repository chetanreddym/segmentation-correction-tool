package javax.media.jai.remote;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.RemoteImage;
import javax.media.jai.TileCache;
import javax.media.jai.tilecodec.TileCodecDescriptor;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoder;
import javax.media.jai.tilecodec.TileDecoderFactory;
import javax.media.jai.tilecodec.TileEncoder;
import javax.media.jai.tilecodec.TileEncoderFactory;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

















































































































































public final class SerializableRenderedImage
  implements RenderedImage, Serializable
{
  private static final int SERVER_TIMEOUT = 60000;
  private static final String CLOSE_MESSAGE = "CLOSE";
  private static final String CLOSE_ACK = "CLOSE_ACK";
  private Object UID;
  private transient boolean isServer;
  private boolean isSourceRemote;
  private transient RenderedImage source;
  private int minX;
  private int minY;
  private int width;
  private int height;
  private int minTileX;
  private int minTileY;
  private int numXTiles;
  private int numYTiles;
  private int tileWidth;
  private int tileHeight;
  private int tileGridXOffset;
  private int tileGridYOffset;
  private transient SampleModel sampleModel = null;
  

  private transient ColorModel colorModel = null;
  

  private transient Vector sources = null;
  

  private transient Hashtable properties = null;
  

  private boolean useDeepCopy;
  

  private Rectangle imageBounds;
  

  private transient Raster imageRaster;
  

  private InetAddress host;
  

  private int port;
  

  private transient boolean serverOpen = false;
  

  private transient ServerSocket serverSocket = null;
  





  private transient Thread serverThread;
  




  private String formatName;
  




  private transient OperationRegistry registry;
  




  private static transient Hashtable remoteReferenceCount;
  




  private boolean useTileCodec = false;
  

  private transient TileDecoderFactory tileDecoderFactory = null;
  

  private transient TileEncoderFactory tileEncoderFactory = null;
  

  private TileCodecParameterList encodingParam = null;
  private TileCodecParameterList decodingParam = null;
  








  private static synchronized void incrementRemoteReferenceCount(Object o)
  {
    if (remoteReferenceCount == null) {
      remoteReferenceCount = new Hashtable();
      remoteReferenceCount.put(o, new Integer(1));
    } else {
      Integer count = (Integer)remoteReferenceCount.get(o);
      if (count == null) {
        remoteReferenceCount.put(o, new Integer(1));
      } else {
        remoteReferenceCount.put(o, new Integer(count.intValue() + 1));
      }
    }
  }
  









  private static synchronized void decrementRemoteReferenceCount(Object o)
  {
    if (remoteReferenceCount != null) {
      Integer count = (Integer)remoteReferenceCount.get(o);
      if (count != null) {
        if (count.intValue() == 1) {
          remoteReferenceCount.remove(o);
        } else {
          remoteReferenceCount.put(o, new Integer(count.intValue() - 1));
        }
      }
    }
  }
  


























  SerializableRenderedImage() {}
  


























  public SerializableRenderedImage(RenderedImage source, boolean useDeepCopy, OperationRegistry registry, String formatName, TileCodecParameterList encodingParam, TileCodecParameterList decodingParam)
    throws NotSerializableException
  {
    this(source, useDeepCopy, false);
    


    if (formatName == null) {
      return;
    }
    this.formatName = formatName;
    

    if (registry == null)
      registry = JAI.getDefaultInstance().getOperationRegistry();
    this.registry = registry;
    

    if (encodingParam == null) {
      TileCodecDescriptor tcd = getTileCodecDescriptor("tileEncoder", formatName);
      
      encodingParam = tcd.getDefaultParameters("tileEncoder");
    } else if (!formatName.equals(encodingParam.getFormatName())) {
      throw new IllegalArgumentException(JaiI18N.getString("UseTileCodec0"));
    }
    

    if (decodingParam == null) {
      TileCodecDescriptor tcd = getTileCodecDescriptor("tileDecoder", formatName);
      
      decodingParam = tcd.getDefaultParameters("tileDecoder");
    } else if (!formatName.equals(decodingParam.getFormatName())) {
      throw new IllegalArgumentException(JaiI18N.getString("UseTileCodec1"));
    }
    
    tileEncoderFactory = ((TileEncoderFactory)registry.getFactory("tileEncoder", formatName));
    
    tileDecoderFactory = ((TileDecoderFactory)registry.getFactory("tileDecoder", formatName));
    
    if ((tileEncoderFactory == null) || (tileDecoderFactory == null)) {
      throw new RuntimeException(JaiI18N.getString("UseTileCodec2"));
    }
    this.encodingParam = encodingParam;
    this.decodingParam = decodingParam;
    useTileCodec = true;
  }
  


















  public SerializableRenderedImage(RenderedImage source, boolean useDeepCopy)
  {
    this(source, useDeepCopy, true);
  }
  














  public SerializableRenderedImage(RenderedImage source)
  {
    this(source, false, true);
  }
  














  private SerializableRenderedImage(RenderedImage source, boolean useDeepCopy, boolean checkDataBuffer)
  {
    UID = ImageUtil.generateID(this);
    
    if (source == null) {
      throw new IllegalArgumentException(JaiI18N.getString("SerializableRenderedImage0"));
    }
    
    SampleModel sm = source.getSampleModel();
    if ((sm != null) && (SerializerFactory.getSerializer(sm.getClass()) == null))
    {
      throw new IllegalArgumentException(JaiI18N.getString("SerializableRenderedImage2"));
    }
    
    ColorModel cm = source.getColorModel();
    if ((cm != null) && (SerializerFactory.getSerializer(cm.getClass()) == null))
    {
      throw new IllegalArgumentException(JaiI18N.getString("SerializableRenderedImage3"));
    }
    
    if (checkDataBuffer) {
      Raster ras = source.getTile(source.getMinTileX(), source.getMinTileY());
      if (ras != null) {
        DataBuffer db = ras.getDataBuffer();
        if ((db != null) && (SerializerFactory.getSerializer(db.getClass()) == null))
        {
          throw new IllegalArgumentException(JaiI18N.getString("SerializableRenderedImage4"));
        }
      }
    }
    
    isServer = true;
    

    this.useDeepCopy = useDeepCopy;
    

    this.source = source;
    

    isSourceRemote = (source instanceof RemoteImage);
    

    minX = source.getMinX();
    minY = source.getMinY();
    width = source.getWidth();
    height = source.getHeight();
    minTileX = source.getMinTileX();
    minTileY = source.getMinTileY();
    numXTiles = source.getNumXTiles();
    numYTiles = source.getNumYTiles();
    tileWidth = source.getTileWidth();
    tileHeight = source.getTileHeight();
    tileGridXOffset = source.getTileGridXOffset();
    tileGridYOffset = source.getTileGridYOffset();
    sampleModel = source.getSampleModel();
    colorModel = source.getColorModel();
    sources = new Vector();
    sources.add(source);
    properties = new Hashtable();
    

    String[] propertyNames = source.getPropertyNames();
    if (propertyNames != null) {
      for (int i = 0; i < propertyNames.length; i++) {
        properties.put(propertyNames[i], source.getProperty(propertyNames[i]));
      }
    }
    


    imageBounds = new Rectangle(minX, minY, width, height);
    
    try
    {
      host = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      throw new RuntimeException(e.getMessage());
    }
    

    serverOpen = false;
  }
  
  private class TileServer implements Runnable
  {
    TileServer(SerializableRenderedImage.1 x1) {
      this();
    }
    












    public void run()
    {
      while (serverOpen)
      {
        Socket socket = null;
        try {
          socket = serverSocket.accept();
          socket.setSoLinger(true, 1);
        }
        catch (InterruptedIOException e) {
          continue;
        }
        catch (SocketException e) {
          sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage5"), new ImagingException(JaiI18N.getString("SerializableRenderedImage5"), e));
        }
        catch (IOException e)
        {
          sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage6"), new ImagingException(JaiI18N.getString("SerializableRenderedImage6"), e));
        }
        



        InputStream in = null;
        OutputStream out = null;
        ObjectInputStream objectIn = null;
        ObjectOutputStream objectOut = null;
        try {
          in = socket.getInputStream();
          out = socket.getOutputStream();
          objectIn = new ObjectInputStream(in);
          objectOut = new ObjectOutputStream(out);
        } catch (IOException e) {
          sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage7"), new ImagingException(JaiI18N.getString("SerializableRenderedImage7"), e));
        }
        



        Object obj = null;
        try {
          obj = objectIn.readObject();
        } catch (IOException e) {
          sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage8"), new ImagingException(JaiI18N.getString("SerializableRenderedImage8"), e));
        }
        catch (ClassNotFoundException e)
        {
          sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage9"), new ImagingException(JaiI18N.getString("SerializableRenderedImage9"), e));
        }
        


        if (((obj instanceof String)) && (((String)obj).equals("CLOSE")))
        {
          try
          {
            objectOut.writeObject("CLOSE_ACK");
          } catch (IOException e) {
            sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage17"), new ImagingException(JaiI18N.getString("SerializableRenderedImage17"), e));
          }
          





          SerializableRenderedImage.decrementRemoteReferenceCount(this);
        } else if ((obj instanceof Rectangle))
        {

          Raster raster = source.getData((Rectangle)obj);
          


          if (useTileCodec) {
            byte[] buf = SerializableRenderedImage.this.encodeRasterToByteArray(raster);
            try {
              objectOut.writeObject(buf);
            } catch (IOException e) {
              sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage10"), new ImagingException(JaiI18N.getString("SerializableRenderedImage10"), e));
            }
          }
          else
          {
            try
            {
              objectOut.writeObject(SerializerFactory.getState(raster, null));
            } catch (IOException e) {
              sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage10"), new ImagingException(JaiI18N.getString("SerializableRenderedImage10"), e));
            }
          }
        }
        















        try
        {
          objectOut.flush();
          socket.shutdownOutput();
          socket.shutdownInput();
          objectOut.close();
          objectIn.close();
          out.close();
          in.close();
          socket.close();
        } catch (IOException e) {
          sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage10"), new ImagingException(JaiI18N.getString("SerializableRenderedImage10"), e));
        }
      }
    }
    

    private TileServer() {}
  }
  
  public WritableRaster copyData(WritableRaster dest)
  {
    if ((isServer) || (isSourceRemote)) {
      return source.copyData(dest);
    }
    
    Rectangle region;
    if (dest == null) {
      Rectangle region = imageBounds;
      SampleModel destSM = getSampleModel().createCompatibleSampleModel(width, height);
      

      dest = Raster.createWritableRaster(destSM, new Point(x, y));
    }
    else
    {
      region = dest.getBounds().intersection(imageBounds);
    }
    
    if (!region.isEmpty()) {
      int startTileX = PlanarImage.XToTileX(x, tileGridXOffset, tileWidth);
      

      int startTileY = PlanarImage.YToTileY(y, tileGridYOffset, tileHeight);
      

      int endTileX = PlanarImage.XToTileX(x + width - 1, tileGridXOffset, tileWidth);
      

      int endTileY = PlanarImage.YToTileY(y + height - 1, tileGridYOffset, tileHeight);
      


      SampleModel[] sampleModels = { getSampleModel() };
      int tagID = RasterAccessor.findCompatibleTag(sampleModels, dest.getSampleModel());
      


      RasterFormatTag srcTag = new RasterFormatTag(getSampleModel(), tagID);
      
      RasterFormatTag dstTag = new RasterFormatTag(dest.getSampleModel(), tagID);
      

      for (int ty = startTileY; ty <= endTileY; ty++) {
        for (int tx = startTileX; tx <= endTileX; tx++) {
          Raster tile = getTile(tx, ty);
          Rectangle subRegion = region.intersection(tile.getBounds());
          

          RasterAccessor s = new RasterAccessor(tile, subRegion, srcTag, getColorModel());
          

          RasterAccessor d = new RasterAccessor(dest, subRegion, dstTag, null);
          

          ImageUtil.copyRaster(s, d);
        }
      }
    }
    
    return dest;
  }
  
  public ColorModel getColorModel() {
    return colorModel;
  }
  
  public Raster getData() {
    if ((isServer) || (isSourceRemote)) {
      return source.getData();
    }
    
    return getData(imageBounds);
  }
  
  public Raster getData(Rectangle rect) {
    Raster raster = null;
    



    if ((isServer) || (isSourceRemote)) {
      raster = source.getData(rect);
    } else if (useDeepCopy) {
      raster = imageRaster.createChild(x, y, width, height, x, y, null);



    }
    else
    {


      Socket socket = connectToServer();
      


      OutputStream out = null;
      ObjectOutputStream objectOut = null;
      InputStream in = null;
      ObjectInputStream objectIn = null;
      try {
        out = socket.getOutputStream();
        objectOut = new ObjectOutputStream(out);
        in = socket.getInputStream();
        objectIn = new ObjectInputStream(in);
      } catch (IOException e) {
        sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage7"), new ImagingException(JaiI18N.getString("SerializableRenderedImage7"), e));
      }
      


      try
      {
        objectOut.writeObject(rect);
      } catch (IOException e) {
        sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage10"), new ImagingException(JaiI18N.getString("SerializableRenderedImage10"), e));
      }
      



      Object object = null;
      try {
        object = objectIn.readObject();
      } catch (IOException e) {
        sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage8"), new ImagingException(JaiI18N.getString("SerializableRenderedImage8"), e));
      }
      catch (ClassNotFoundException e)
      {
        sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage9"), new ImagingException(JaiI18N.getString("SerializableRenderedImage9"), e));
      }
      

      if (useTileCodec) {
        byte[] buf = (byte[])object;
        raster = decodeRasterFromByteArray(buf);
      }
      else {
        if (!(object instanceof SerializableState)) {
          raster = null;
        }
        SerializableState ss = (SerializableState)object;
        Class c = ss.getObjectClass();
        if (Raster.class.isAssignableFrom(c)) {
          raster = (Raster)ss.getObject();
        } else {
          raster = null;
        }
      }
      try
      {
        objectOut.flush();
        socket.shutdownOutput();
        socket.shutdownInput();
        objectOut.close();
        out.close();
        objectIn.close();
        in.close();
        socket.close();
      } catch (IOException e) {
        String message = JaiI18N.getString("SerializableRenderedImage11");
        
        sendExceptionToListener(message, new ImagingException(message, e));
      }
      




      if (imageBounds.equals(rect))
      {
        closeClient();
        
        imageRaster = raster;
        useDeepCopy = true;
      }
    }
    
    return raster;
  }
  
  public int getHeight() {
    return height;
  }
  
  public int getMinTileX() {
    return minTileX;
  }
  
  public int getMinTileY() {
    return minTileY;
  }
  
  public int getMinX() {
    return minX;
  }
  
  public int getMinY() {
    return minY;
  }
  
  public int getNumXTiles() {
    return numXTiles;
  }
  
  public int getNumYTiles() {
    return numYTiles;
  }
  


  public Object getProperty(String name)
  {
    Object property = properties.get(name);
    return property == null ? Image.UndefinedProperty : property;
  }
  
  public String[] getPropertyNames() {
    String[] names = null;
    if (!properties.isEmpty()) {
      names = new String[properties.size()];
      Enumeration keys = properties.keys();
      int index = 0;
      while (keys.hasMoreElements())
      {


        names[(index++)] = ((String)keys.nextElement());
      }
    }
    return names;
  }
  
  public SampleModel getSampleModel() {
    return sampleModel;
  }
  





  public Vector getSources()
  {
    return sources;
  }
  
  public Raster getTile(int tileX, int tileY) {
    if ((isServer) || (isSourceRemote)) {
      return source.getTile(tileX, tileY);
    }
    
    TileCache cache = JAI.getDefaultInstance().getTileCache();
    if (cache != null) {
      Raster tile = cache.getTile(this, tileX, tileY);
      if (tile != null) {
        return tile;
      }
    }
    
    Rectangle imageBounds = new Rectangle(getMinX(), getMinY(), getWidth(), getHeight());
    
    Rectangle destRect = imageBounds.intersection(new Rectangle(tileXToX(tileX), tileYToY(tileY), getTileWidth(), getTileHeight()));
    




    Raster tile = getData(destRect);
    
    if (cache != null) {
      cache.add(this, tileX, tileY, tile);
    }
    
    return tile;
  }
  






  public Object getImageID()
  {
    return UID;
  }
  











  private int tileXToX(int tx)
  {
    return PlanarImage.tileXToX(tx, getTileGridXOffset(), getTileWidth());
  }
  











  private int tileYToY(int ty)
  {
    return PlanarImage.tileYToY(ty, getTileGridYOffset(), getTileHeight());
  }
  
  public int getTileGridXOffset() {
    return tileGridXOffset;
  }
  
  public int getTileGridYOffset() {
    return tileGridYOffset;
  }
  
  public int getTileHeight() {
    return tileHeight;
  }
  
  public int getTileWidth() {
    return tileWidth;
  }
  
  public int getWidth() {
    return width;
  }
  











  private synchronized void openServer()
    throws IOException, SocketException
  {
    if (!serverOpen)
    {
      serverSocket = new ServerSocket(0);
      

      serverSocket.setSoTimeout(60000);
      

      port = serverSocket.getLocalPort();
      

      serverOpen = true;
      

      serverThread = new Thread(new TileServer(null));
      serverThread.setDaemon(true);
      serverThread.start();
      

      incrementRemoteReferenceCount(this);
    }
  }
  





  private void closeClient()
  {
    Socket socket = connectToServer();
    


    OutputStream out = null;
    ObjectOutputStream objectOut = null;
    ObjectInputStream objectIn = null;
    try {
      out = socket.getOutputStream();
      objectOut = new ObjectOutputStream(out);
      objectIn = new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage7"), new ImagingException(JaiI18N.getString("SerializableRenderedImage7"), e));
    }
    


    try
    {
      objectOut.writeObject("CLOSE");
    } catch (IOException e) {
      sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage13"), new ImagingException(JaiI18N.getString("SerializableRenderedImage13"), e));
    }
    

    try
    {
      objectIn.readObject();
    } catch (IOException e) {
      sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage8"), new ImagingException(JaiI18N.getString("SerializableRenderedImage8"), e));

    }
    catch (ClassNotFoundException cnfe)
    {
      sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage9"), new ImagingException(JaiI18N.getString("SerializableRenderedImage9"), cnfe));
    }
    



    try
    {
      objectOut.flush();
      socket.shutdownOutput();
      objectOut.close();
      out.close();
      objectIn.close();
      socket.close();
    } catch (IOException e) {
      sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage11"), new ImagingException(JaiI18N.getString("SerializableRenderedImage11"), e));
    }
  }
  







  private Socket connectToServer()
  {
    Socket socket = null;
    try {
      socket = new Socket(host, port);
      socket.setSoLinger(true, 1);
    } catch (IOException e) {
      sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage14"), new ImagingException(JaiI18N.getString("SerializableRenderedImage14"), e));
    }
    


    return socket;
  }
  



  private byte[] encodeRasterToByteArray(Raster raster)
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    TileEncoder encoder = tileEncoderFactory.createEncoder(bos, encodingParam, raster.getSampleModel());
    

    try
    {
      encoder.encode(raster);
      return bos.toByteArray();
    } catch (IOException e) {
      sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage15"), new ImagingException(JaiI18N.getString("SerializableRenderedImage15"), e));
    }
    

    return null;
  }
  


  private Raster decodeRasterFromByteArray(byte[] buf)
  {
    ByteArrayInputStream bis = new ByteArrayInputStream(buf);
    




    if (tileDecoderFactory == null)
    {

      if (registry == null)
        registry = JAI.getDefaultInstance().getOperationRegistry();
      tileDecoderFactory = ((TileDecoderFactory)registry.getFactory("tileDecoder", formatName));
      

      TileCodecParameterList temp = decodingParam;
      
      if (temp != null) {
        TileCodecDescriptor tcd = getTileCodecDescriptor("tileDecoder", formatName);
        
        ParameterListDescriptor pld = tcd.getParameterListDescriptor("tileDecoder");
        
        decodingParam = new TileCodecParameterList(formatName, new String[] { "tileDecoder" }, pld);
        


        String[] names = pld.getParamNames();
        
        if (names != null) {
          for (int i = 0; i < names.length; i++) {
            decodingParam.setParameter(names[i], temp.getObjectParameter(names[i]));
          }
        }
      } else {
        decodingParam = getTileCodecDescriptor("tileDecoder", formatName).getDefaultParameters("tileDecoder");
      }
    }
    
    TileDecoder decoder = tileDecoderFactory.createDecoder(bis, decodingParam);
    try
    {
      return decoder.decode();
    } catch (IOException e) {
      sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage16"), new ImagingException(JaiI18N.getString("SerializableRenderedImage16"), e));
    }
    

    return null;
  }
  


  protected void finalize()
    throws Throwable
  {
    dispose();
    

    super.finalize();
  }
  


























  public void dispose()
  {
    if (isServer) {
      if (serverOpen)
      {
        serverOpen = false;
        
        try
        {
          serverThread.join(120000L);
        }
        catch (Exception e) {}
        

        try
        {
          serverSocket.close();

        }
        catch (Exception e) {}
      }
    }
    else {
      closeClient();
    }
  }
  




  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    if (!useDeepCopy) {
      try
      {
        openServer();
      } catch (Exception e1) {
        if (((e1 instanceof SocketException)) && 
          (serverSocket != null)) {
          try {
            serverSocket.close();
          }
          catch (IOException e2) {}
        }
        



        serverOpen = false;
        useDeepCopy = true;
      }
    }
    

    out.defaultWriteObject();
    

    if (isSourceRemote) {
      String remoteClass = source.getClass().getName();
      out.writeObject(source.getProperty(remoteClass + ".serverName"));
      out.writeObject(source.getProperty(remoteClass + ".id"));
    }
    

    Hashtable propertyTable = properties;
    boolean propertiesCloned = false;
    Enumeration keys = propertyTable.keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      if (!(properties.get(key) instanceof Serializable)) {
        if (!propertiesCloned) {
          propertyTable = (Hashtable)properties.clone();
          propertiesCloned = true;
        }
        propertyTable.remove(key);
      }
    }
    

    out.writeObject(SerializerFactory.getState(sampleModel, null));
    out.writeObject(SerializerFactory.getState(colorModel, null));
    out.writeObject(propertyTable);
    

    if (useDeepCopy) {
      if (useTileCodec) {
        out.writeObject(encodeRasterToByteArray(source.getData()));
      } else {
        out.writeObject(SerializerFactory.getState(source.getData(), null));
      }
    }
  }
  






  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    isServer = false;
    source = null;
    serverOpen = false;
    serverSocket = null;
    serverThread = null;
    colorModel = null;
    

    in.defaultReadObject();
    
    if (isSourceRemote)
    {
      String serverName = (String)in.readObject();
      Long id = (Long)in.readObject();
      

      source = new RemoteImage(serverName + "::" + id.longValue(), (RenderedImage)null);
    }
    


    SerializableState smState = (SerializableState)in.readObject();
    sampleModel = ((SampleModel)smState.getObject());
    SerializableState cmState = (SerializableState)in.readObject();
    colorModel = ((ColorModel)cmState.getObject());
    properties = ((Hashtable)in.readObject());
    

    if (useDeepCopy) {
      if (useTileCodec) {
        imageRaster = decodeRasterFromByteArray((byte[])in.readObject());
      }
      else {
        SerializableState rasState = (SerializableState)in.readObject();
        
        imageRaster = ((Raster)rasState.getObject());
      }
    }
  }
  
  private TileCodecDescriptor getTileCodecDescriptor(String registryMode, String formatName) {
    if (registry == null) {
      return (TileCodecDescriptor)JAI.getDefaultInstance().getOperationRegistry().getDescriptor(registryMode, formatName);
    }
    
    return (TileCodecDescriptor)registry.getDescriptor(registryMode, formatName);
  }
  
  void sendExceptionToListener(String message, Exception e) {
    ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
    listener.errorOccurred(message, e, this, false);
  }
}
