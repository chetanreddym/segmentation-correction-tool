package com.sun.media.jai.rmi;

import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
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
import javax.media.jai.OperationRegistry;
import javax.media.jai.remote.SerializableRenderedImage;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoderFactory;
import javax.media.jai.tilecodec.TileEncoderFactory;
import javax.media.jai.util.CaselessStringKey;





























































































public final class SerializableRenderableImage
  implements RenderableImage, Serializable
{
  private static final int SERVER_TIMEOUT = 60000;
  private static final String CLOSE_MESSAGE = "CLOSE";
  private transient boolean isServer;
  private transient RenderableImage source;
  private float minX;
  private float minY;
  private float width;
  private float height;
  private transient Vector sources = null;
  

  private transient Hashtable properties = null;
  

  private boolean isDynamic;
  

  private InetAddress host;
  

  private int port;
  

  private transient boolean serverOpen = false;
  

  private transient ServerSocket serverSocket = null;
  







  private transient Thread serverThread;
  






  private static transient Hashtable remoteReferenceCount;
  






  private boolean useTileCodec = false;
  




  private OperationRegistry registry = null;
  

  private String formatName = null;
  

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
  






















  SerializableRenderableImage() {}
  






















  public SerializableRenderableImage(RenderableImage source, OperationRegistry registry, String formatName, TileCodecParameterList encodingParam, TileCodecParameterList decodingParam)
  {
    this(source);
    
    this.registry = registry;
    this.formatName = formatName;
    this.encodingParam = encodingParam;
    this.decodingParam = decodingParam;
    
    if (formatName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("SerializableRenderableImage2"));
    }
    

    if (!formatName.equals(encodingParam.getFormatName())) {
      throw new IllegalArgumentException(JaiI18N.getString("UseTileCodec0"));
    }
    

    if (!formatName.equals(decodingParam.getFormatName())) {
      throw new IllegalArgumentException(JaiI18N.getString("UseTileCodec1"));
    }
    

    TileEncoderFactory tileEncoderFactory = (TileEncoderFactory)registry.getFactory("tileEncoder", formatName);
    
    TileDecoderFactory tileDecoderFactory = (TileDecoderFactory)registry.getFactory("tileDecoder", formatName);
    
    if ((tileEncoderFactory == null) || (tileDecoderFactory == null)) {
      throw new RuntimeException(JaiI18N.getString("UseTileCodec2"));
    }
    useTileCodec = true;
  }
  











  public SerializableRenderableImage(RenderableImage source)
  {
    if (source == null) {
      throw new IllegalArgumentException(JaiI18N.getString("SerializableRenderableImage1"));
    }
    

    isServer = true;
    

    this.source = source;
    

    minX = source.getMinX();
    minY = source.getMinY();
    width = source.getWidth();
    height = source.getHeight();
    isDynamic = source.isDynamic();
    
    sources = new Vector();
    sources.add(source);
    
    properties = new Hashtable();
    String[] propertyNames = source.getPropertyNames();
    
    if (propertyNames != null) {
      for (int i = 0; i < propertyNames.length; i++) {
        String propertyName = propertyNames[i];
        properties.put(new CaselessStringKey(propertyName), source.getProperty(propertyName));
      }
    }
    

    try
    {
      host = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      throw new RuntimeException(e.getMessage());
    }
    

    serverOpen = false;
  }
  
  private class RenderingServer implements Runnable
  {
    RenderingServer(SerializableRenderableImage.1 x1) {
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
        catch (Exception e) {
          throw new RuntimeException(e.getMessage());
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
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
        

        Object obj = null;
        try {
          obj = objectIn.readObject();
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
        
        RenderedImage ri = null;
        

        if ((obj instanceof String)) {
          String str = (String)obj;
          
          if (str.equals("CLOSE"))
          {
            SerializableRenderableImage.decrementRemoteReferenceCount(this);
          }
          else {
            if (str.equals("createDefaultRendering"))
            {
              ri = source.createDefaultRendering();
            }
            else if (str.equals("createRendering"))
            {

              obj = null;
              try {
                obj = objectIn.readObject();
              } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
              }
              
              SerializableState ss = (SerializableState)obj;
              RenderContext rc = (RenderContext)ss.getObject();
              
              ri = source.createRendering(rc);
            }
            else if (str.equals("createScaledRendering"))
            {

              obj = null;
              try {
                obj = objectIn.readObject();
              } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
              }
              
              int w = ((Integer)obj).intValue();
              try
              {
                obj = objectIn.readObject();
              } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
              }
              
              int h = ((Integer)obj).intValue();
              try
              {
                obj = objectIn.readObject();
              } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
              }
              
              SerializableState ss = (SerializableState)obj;
              RenderingHints rh = (RenderingHints)ss.getObject();
              
              ri = source.createScaledRendering(w, h, rh);
            }
            SerializableRenderedImage sri;
            if (useTileCodec) {
              try {
                sri = new SerializableRenderedImage(ri, true, registry, formatName, encodingParam, decodingParam);
              }
              catch (NotSerializableException nse)
              {
                SerializableRenderedImage sri;
                

                throw new RuntimeException(nse.getMessage());
              }
            } else {
              sri = new SerializableRenderedImage(ri, true);
            }
            try
            {
              objectOut.writeObject(sri);
            } catch (Exception e) {
              throw new RuntimeException(e.getMessage());
            }
          }
        } else {
          throw new RuntimeException(JaiI18N.getString("SerializableRenderableImage0"));
        }
        














        try
        {
          objectOut.close();
          objectIn.close();
          out.close();
          in.close();
          socket.close();
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
      }
    }
    



    private RenderingServer() {}
  }
  


  public RenderedImage createDefaultRendering()
  {
    if (isServer) {
      return source.createDefaultRendering();
    }
    

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
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    
    try
    {
      objectOut.writeObject("createDefaultRendering");
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    

    Object object = null;
    try {
      object = objectIn.readObject();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    RenderedImage ri;
    RenderedImage ri;
    if ((object instanceof SerializableRenderedImage)) {
      ri = (RenderedImage)object;
    } else {
      ri = null;
    }
    
    try
    {
      out.close();
      objectOut.close();
      in.close();
      objectIn.close();
      socket.close();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    
    return ri;
  }
  

  public RenderedImage createRendering(RenderContext renderContext)
  {
    if (isServer) {
      return source.createRendering(renderContext);
    }
    

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
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    

    try
    {
      objectOut.writeObject("createRendering");
      objectOut.writeObject(SerializerFactory.getState(renderContext, null));
    }
    catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    

    Object object = null;
    try {
      object = objectIn.readObject();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    
    RenderedImage ri = (RenderedImage)object;
    
    try
    {
      out.close();
      objectOut.close();
      in.close();
      objectIn.close();
      socket.close();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    
    return ri;
  }
  


  public RenderedImage createScaledRendering(int w, int h, RenderingHints hints)
  {
    if (isServer) {
      return source.createScaledRendering(w, h, hints);
    }
    

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
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    

    try
    {
      objectOut.writeObject("createScaledRendering");
      objectOut.writeObject(new Integer(w));
      objectOut.writeObject(new Integer(h));
      objectOut.writeObject(SerializerFactory.getState(hints, null));
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    

    Object object = null;
    try {
      object = objectIn.readObject();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    
    RenderedImage ri = (RenderedImage)object;
    
    try
    {
      out.close();
      objectOut.close();
      in.close();
      objectIn.close();
      socket.close();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    
    return ri;
  }
  
  public float getHeight() {
    return height;
  }
  
  public float getMinX() {
    return minX;
  }
  
  public float getMinY() {
    return minY;
  }
  

  public Object getProperty(String name)
  {
    Object property = properties.get(new CaselessStringKey(name));
    return property == null ? Image.UndefinedProperty : property;
  }
  
  public String[] getPropertyNames() {
    String[] names = null;
    if (!properties.isEmpty()) {
      names = new String[properties.size()];
      Enumeration keys = properties.keys();
      int index = 0;
      
      while (keys.hasMoreElements()) {
        CaselessStringKey key = (CaselessStringKey)keys.nextElement();
        names[(index++)] = key.getName();
      }
    }
    return names;
  }
  





  public Vector getSources()
  {
    return sources;
  }
  


  public boolean isDynamic()
  {
    return isDynamic;
  }
  
  public float getWidth() {
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
      

      serverThread = new Thread(new RenderingServer(null));
      serverThread.start();
      

      incrementRemoteReferenceCount(this);
    }
  }
  




  private void closeClient()
  {
    Socket socket = connectToServer();
    


    OutputStream out = null;
    ObjectOutputStream objectOut = null;
    try {
      out = socket.getOutputStream();
      objectOut = new ObjectOutputStream(out);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    
    try
    {
      objectOut.writeObject("CLOSE");
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    
    try
    {
      out.close();
      objectOut.close();
      socket.close();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }
  




  private Socket connectToServer()
  {
    Socket socket = null;
    try {
      socket = new Socket(host, port);
      socket.setSoLinger(true, 1);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    
    return socket;
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
    }
    

    out.defaultWriteObject();
    

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
    

    out.writeObject(propertyTable);
  }
  





  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    isServer = false;
    source = null;
    serverOpen = false;
    serverSocket = null;
    serverThread = null;
    

    in.defaultReadObject();
    

    properties = ((Hashtable)in.readObject());
  }
}
