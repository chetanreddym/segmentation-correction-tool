package javax.media.jai;

import com.sun.media.jai.util.ImagingListenerImpl;
import com.sun.media.jai.util.PropertyUtil;
import com.sun.media.jai.util.SunTileCache;
import com.sun.media.jai.util.SunTileScheduler;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.util.ImagingListener;



































































































































public final class JAI
{
  private static final int HINT_IMAGE_LAYOUT = 101;
  private static final int HINT_INTERPOLATION = 102;
  private static final int HINT_OPERATION_REGISTRY = 103;
  private static final int HINT_OPERATION_BOUND = 104;
  private static final int HINT_BORDER_EXTENDER = 105;
  private static final int HINT_TILE_CACHE = 106;
  private static final int HINT_TILE_SCHEDULER = 107;
  private static final int HINT_DEFAULT_COLOR_MODEL_ENABLED = 108;
  private static final int HINT_DEFAULT_COLOR_MODEL_METHOD = 109;
  private static final int HINT_TILE_CACHE_METRIC = 110;
  private static final int HINT_SERIALIZE_DEEP_COPY = 111;
  private static final int HINT_TILE_CODEC_FORMAT = 112;
  private static final int HINT_TILE_ENCODING_PARAM = 113;
  private static final int HINT_TILE_DECODING_PARAM = 114;
  private static final int HINT_RETRY_INTERVAL = 115;
  private static final int HINT_NUM_RETRIES = 116;
  private static final int HINT_NEGOTIATION_PREFERENCES = 117;
  private static final int HINT_DEFAULT_RENDERING_SIZE = 118;
  private static final int HINT_COLOR_MODEL_FACTORY = 119;
  private static final int HINT_REPLACE_INDEX_COLOR_MODEL = 120;
  private static final int HINT_TILE_FACTORY = 121;
  private static final int HINT_TILE_RECYCLER = 122;
  private static final int HINT_CACHED_TILE_RECYCLING_ENABLED = 123;
  private static final int HINT_TRANSFORM_ON_COLORMAP = 124;
  private static final int HINT_IMAGING_LISTENER = 125;
  public static RenderingHints.Key KEY_IMAGE_LAYOUT = new RenderingKey(101, ImageLayout.class);
  









  public static RenderingHints.Key KEY_INTERPOLATION = new RenderingKey(102, Interpolation.class);
  








  public static RenderingHints.Key KEY_OPERATION_REGISTRY = new RenderingKey(103, OperationRegistry.class);
  










  public static RenderingHints.Key KEY_OPERATION_BOUND = new RenderingKey(104, Integer.class);
  






  public static RenderingHints.Key KEY_BORDER_EXTENDER = new RenderingKey(105, BorderExtender.class);
  













  public static RenderingHints.Key KEY_TILE_CACHE = new RenderingKey(106, TileCache.class);
  











  public static RenderingHints.Key KEY_TILE_CACHE_METRIC = new RenderingKey(110, Object.class);
  












  public static RenderingHints.Key KEY_TILE_SCHEDULER = new RenderingKey(107, TileScheduler.class);
  













  public static RenderingHints.Key KEY_DEFAULT_COLOR_MODEL_ENABLED = new RenderingKey(108, Boolean.class);
  
















  public static RenderingHints.Key KEY_DEFAULT_COLOR_MODEL_METHOD = new RenderingKey(109, Method.class);
  











  public static final RenderingHints.Key KEY_DEFAULT_RENDERING_SIZE = new RenderingKey(118, Dimension.class);
  










  public static RenderingHints.Key KEY_COLOR_MODEL_FACTORY = new RenderingKey(119, ColorModelFactory.class);
  






































  public static RenderingHints.Key KEY_REPLACE_INDEX_COLOR_MODEL = new RenderingKey(120, Boolean.class);
  













  public static RenderingHints.Key KEY_TILE_FACTORY = new RenderingKey(121, TileFactory.class);
  












  public static RenderingHints.Key KEY_TILE_RECYCLER = new RenderingKey(122, TileRecycler.class);
  











  public static RenderingHints.Key KEY_CACHED_TILE_RECYCLING_ENABLED = new RenderingKey(123, Boolean.class);
  










  public static RenderingHints.Key KEY_SERIALIZE_DEEP_COPY = new RenderingKey(111, Boolean.class);
  










  public static RenderingHints.Key KEY_TILE_CODEC_FORMAT = new RenderingKey(112, String.class);
  










  public static RenderingHints.Key KEY_TILE_ENCODING_PARAM = new RenderingKey(113, TileCodecParameterList.class);
  










  public static RenderingHints.Key KEY_TILE_DECODING_PARAM = new RenderingKey(114, TileCodecParameterList.class);
  












  public static RenderingHints.Key KEY_RETRY_INTERVAL = new RenderingKey(115, Integer.class);
  












  public static RenderingHints.Key KEY_NUM_RETRIES = new RenderingKey(116, Integer.class);
  












  public static RenderingHints.Key KEY_NEGOTIATION_PREFERENCES = new RenderingKey(117, NegotiableCapabilitySet.class);
  














  public static RenderingHints.Key KEY_TRANSFORM_ON_COLORMAP = new RenderingKey(124, Boolean.class);
  










  public static RenderingHints.Key KEY_IMAGING_LISTENER = new RenderingKey(125, ImagingListener.class);
  




  private static final int DEFAULT_TILE_SIZE = 512;
  




  private static Dimension defaultTileSize = new Dimension(512, 512);
  





  private static Dimension defaultRenderingSize = new Dimension(0, 512);
  

  private OperationRegistry operationRegistry;
  

  private TileScheduler tileScheduler;
  

  private TileCache tileCache;
  

  private RenderingHints renderingHints;
  

  private ImagingListener imagingListener = ImagingListenerImpl.getInstance();
  
  private static JAI defaultInstance = new JAI(OperationRegistry.initializeRegistry(), new SunTileScheduler(), new SunTileCache(), new RenderingHints(null));
  







  private JAI(OperationRegistry operationRegistry, TileScheduler tileScheduler, TileCache tileCache, RenderingHints renderingHints)
  {
    this.operationRegistry = operationRegistry;
    this.tileScheduler = tileScheduler;
    this.tileCache = tileCache;
    this.renderingHints = renderingHints;
    
    this.renderingHints.put(KEY_OPERATION_REGISTRY, operationRegistry);
    this.renderingHints.put(KEY_TILE_CACHE, tileCache);
    this.renderingHints.put(KEY_TILE_SCHEDULER, tileScheduler);
    
    TileFactory rtf = new RecyclingTileFactory();
    this.renderingHints.put(KEY_TILE_FACTORY, rtf);
    this.renderingHints.put(KEY_TILE_RECYCLER, rtf);
    this.renderingHints.put(KEY_CACHED_TILE_RECYCLING_ENABLED, Boolean.FALSE);
    
    this.renderingHints.put(KEY_IMAGING_LISTENER, imagingListener);
  }
  



  public static final String getBuildVersion()
  {
    try
    {
      InputStream is = JAI.class.getResourceAsStream("buildVersion");
      if (is == null) {
        is = PropertyUtil.getFileFromClasspath("javax/media/jai/buildVersion");
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      

      StringWriter sw = new StringWriter();
      BufferedWriter writer = new BufferedWriter(sw);
      

      boolean append = false;
      String str;
      while ((str = reader.readLine()) != null) {
        if (append) { writer.newLine();
        }
        writer.write(str);
        append = true;
      }
      
      writer.close();
      return sw.getBuffer().toString();
    }
    catch (Exception e) {}
    return JaiI18N.getString("JAI13");
  }
  





  public static final void disableDefaultTileCache()
  {
    TileCache tmp = defaultInstance.getTileCache();
    if (tmp != null) {
      tmp.flush();
    }
    defaultInstancerenderingHints.remove(KEY_TILE_CACHE);
  }
  




  public static final void enableDefaultTileCache()
  {
    defaultInstancerenderingHints.put(KEY_TILE_CACHE, defaultInstance.getTileCache());
  }
  












  public static final void setDefaultTileSize(Dimension tileDimensions)
  {
    if ((tileDimensions != null) && ((width <= 0) || (height <= 0)))
    {
      throw new IllegalArgumentException();
    }
    
    defaultTileSize = tileDimensions != null ? (Dimension)tileDimensions.clone() : null;
  }
  








  public static final Dimension getDefaultTileSize()
  {
    return defaultTileSize != null ? (Dimension)defaultTileSize.clone() : null;
  }
  























  public static final void setDefaultRenderingSize(Dimension defaultSize)
  {
    if ((defaultSize != null) && (width <= 0) && (height <= 0))
    {

      throw new IllegalArgumentException(JaiI18N.getString("JAI8"));
    }
    
    defaultRenderingSize = defaultSize == null ? null : new Dimension(defaultSize);
  }
  








  public static final Dimension getDefaultRenderingSize()
  {
    return defaultRenderingSize == null ? null : new Dimension(defaultRenderingSize);
  }
  












  public static JAI getDefaultInstance()
  {
    return defaultInstance;
  }
  



  static RenderingHints mergeRenderingHints(RenderingHints defaultHints, RenderingHints hints)
  {
    RenderingHints mergedHints;
    

    RenderingHints mergedHints;
    

    if ((hints == null) || (hints.isEmpty())) {
      mergedHints = defaultHints; } else { RenderingHints mergedHints;
      if ((defaultHints == null) || (defaultHints.isEmpty())) {
        mergedHints = hints;
      } else {
        mergedHints = new RenderingHints(defaultHints);
        mergedHints.add(hints);
      }
    }
    return mergedHints;
  }
  






  public JAI()
  {
    operationRegistry = defaultInstanceoperationRegistry;
    tileScheduler = defaultInstancetileScheduler;
    tileCache = defaultInstancetileCache;
    renderingHints = ((RenderingHints)defaultInstancerenderingHints.clone());
  }
  








  public OperationRegistry getOperationRegistry()
  {
    return operationRegistry;
  }
  




  public void setOperationRegistry(OperationRegistry operationRegistry)
  {
    if (operationRegistry == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    this.operationRegistry = operationRegistry;
    renderingHints.put(KEY_OPERATION_REGISTRY, operationRegistry);
  }
  
  public TileScheduler getTileScheduler()
  {
    return tileScheduler;
  }
  






  public void setTileScheduler(TileScheduler tileScheduler)
  {
    if (tileScheduler == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    this.tileScheduler = tileScheduler;
    renderingHints.put(KEY_TILE_SCHEDULER, tileScheduler);
  }
  
  public TileCache getTileCache()
  {
    return tileCache;
  }
  







  public void setTileCache(TileCache tileCache)
  {
    if (tileCache == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    this.tileCache = tileCache;
    renderingHints.put(KEY_TILE_CACHE, tileCache);
  }
  














  /**
   * @deprecated
   */
  public static TileCache createTileCache(int tileCapacity, long memCapacity)
  {
    if (memCapacity < 0L) {
      throw new IllegalArgumentException(JaiI18N.getString("JAI10"));
    }
    return new SunTileCache(memCapacity);
  }
  















  public static TileCache createTileCache(long memCapacity)
  {
    if (memCapacity < 0L) {
      throw new IllegalArgumentException(JaiI18N.getString("JAI10"));
    }
    return new SunTileCache(memCapacity);
  }
  











  public static TileCache createTileCache()
  {
    return new SunTileCache();
  }
  










  public static TileScheduler createTileScheduler()
  {
    return new SunTileScheduler();
  }
  












































  public static RenderedOp create(String opName, ParameterBlock args, RenderingHints hints)
  {
    return defaultInstance.createNS(opName, args, hints);
  }
  















































































  public RenderedOp createNS(String opName, ParameterBlock args, RenderingHints hints)
  {
    if (opName == null)
      throw new IllegalArgumentException(JaiI18N.getString("JAI14"));
    if (args == null) {
      throw new IllegalArgumentException(JaiI18N.getString("JAI15"));
    }
    
    String modeName = "rendered";
    

    OperationDescriptor odesc = (OperationDescriptor)operationRegistry.getDescriptor(modeName, opName);
    

    if (odesc == null) {
      throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI0"));
    }
    

    if (!RenderedImage.class.isAssignableFrom(odesc.getDestClass(modeName))) {
      throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI2"));
    }
    






    StringBuffer msg = new StringBuffer();
    args = (ParameterBlock)args.clone();
    if (!odesc.validateArguments(modeName, args, msg)) {
      throw new IllegalArgumentException(msg.toString());
    }
    

    RenderingHints mergedHints = mergeRenderingHints(renderingHints, hints);
    
    RenderedOp op = new RenderedOp(operationRegistry, opName, args, mergedHints);
    


    if (odesc.isImmediate()) {
      PlanarImage im = null;
      im = op.getRendering();
      if (im == null)
      {
        return null;
      }
    }
    

    return op;
  }
  









































  public static Collection createCollection(String opName, ParameterBlock args, RenderingHints hints)
  {
    return defaultInstance.createCollectionNS(opName, args, hints);
  }
  












































































  public Collection createCollectionNS(String opName, ParameterBlock args, RenderingHints hints)
  {
    if (opName == null)
      throw new IllegalArgumentException(JaiI18N.getString("JAI14"));
    if (args == null) {
      throw new IllegalArgumentException(JaiI18N.getString("JAI15"));
    }
    
    String modeName = "collection";
    

    OperationDescriptor odesc = (OperationDescriptor)operationRegistry.getDescriptor(modeName, opName);
    

    if (odesc == null) {
      throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI0"));
    }
    

    Class destClass = odesc.getDestClass(modeName);
    
    if ((!RenderedImage.class.isAssignableFrom(destClass)) && (!CollectionImage.class.isAssignableFrom(destClass)))
    {
      throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI5"));
    }
    


    RenderingHints mergedHints = mergeRenderingHints(renderingHints, hints);
    




    StringBuffer msg = new StringBuffer();
    args = (ParameterBlock)args.clone();
    if (odesc.validateArguments(modeName, args, msg)) {
      if (RenderedImage.class.isAssignableFrom(destClass)) {
        Vector v = new Vector(1);
        v.add(new RenderedOp(operationRegistry, opName, args, mergedHints));
        
        return v;
      }
      CollectionOp cOp = new CollectionOp(operationRegistry, opName, args, mergedHints);
      


      if (odesc.isImmediate()) {
        Collection coll = null;
        coll = cOp.getCollection();
        if (coll == null) {
          return null;
        }
      }
      
      return cOp;
    }
    

    int numSources = odesc.getNumSources();
    Vector sources = args.getSources();
    




    Iterator[] iters = new Iterator[numSources];
    Iterator iter = null;
    int size = Integer.MAX_VALUE;
    for (int i = 0; i < numSources; i++) {
      Object s = sources.elementAt(i);
      if ((s instanceof Collection)) {
        iters[i] = ((Collection)s).iterator();
        if ((iter == null) || (((Collection)s).size() < size)) {
          iter = iters[i];
          size = ((Collection)s).size();
        }
      }
    }
    
    if (iter == null)
    {



      throw new IllegalArgumentException(msg.toString());
    }
    

    Collection col = null;
    for (int i = 0; i < numSources; i++) {
      Object s = sources.elementAt(i);
      if ((s instanceof Collection)) {
        try {
          col = (Collection)s.getClass().newInstance();
        }
        catch (Exception e)
        {
          sendExceptionToListener(JaiI18N.getString("JAI16") + s.getClass().getName(), e);
        }
      }
    }
    

    if (col == null) {
      col = new Vector();
    }
    

    Class[] sourceClasses = odesc.getSourceClasses(modeName);
    
    while (iter.hasNext()) {
      ParameterBlock pb = new ParameterBlock();
      pb.setParameters(args.getParameters());
      
      for (int i = 0; i < numSources; i++)
      {
        Object nextSource = null;
        if (iters[i] == null) {
          nextSource = sources.elementAt(i);
        } else {
          nextSource = iters[i].next();
        }
        




        if ((!sourceClasses[i].isAssignableFrom(nextSource.getClass())) && (!(nextSource instanceof Collection)))
        {
          throw new IllegalArgumentException(msg.toString());
        }
        pb.addSource(nextSource);
      }
      
      Collection c = createCollectionNS(opName, pb, mergedHints);
      if (((c instanceof Vector)) && (c.size() == 1) && ((((Vector)c).elementAt(0) instanceof RenderedOp)))
      {

        col.add(((Vector)c).elementAt(0));
      } else {
        col.add(c);
      }
    }
    
    return col;
  }
  













  public static RenderedOp create(String opName, ParameterBlock args)
  {
    return create(opName, args, null);
  }
  






  public static RenderedOp create(String opName, Object param)
  {
    ParameterBlock args = new ParameterBlock();
    args.add(param);
    return create(opName, args, null);
  }
  








  public static RenderedOp create(String opName, Object param1, Object param2)
  {
    ParameterBlock args = new ParameterBlock();
    args.add(param1);
    args.add(param2);
    return create(opName, args, null);
  }
  








  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, Object param1, int param2)
  {
    ParameterBlock args = new ParameterBlock();
    args.add(param1);
    args.add(param2);
    return create(opName, args, null);
  }
  









  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, Object param1, Object param2, Object param3)
  {
    ParameterBlock args = new ParameterBlock();
    args.add(param1);
    args.add(param2);
    args.add(param3);
    return create(opName, args, null);
  }
  










  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, int param1, int param2, Object param3)
  {
    ParameterBlock args = new ParameterBlock();
    args.add(param1);
    args.add(param2);
    args.add(param3);
    return create(opName, args, null);
  }
  











  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, Object param1, Object param2, Object param3, Object param4)
  {
    ParameterBlock args = new ParameterBlock();
    args.add(param1);
    args.add(param2);
    args.add(param3);
    args.add(param4);
    return create(opName, args, null);
  }
  











  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, Object param1, int param2, Object param3, int param4)
  {
    ParameterBlock args = new ParameterBlock();
    args.add(param1);
    args.add(param2);
    args.add(param3);
    args.add(param4);
    return create(opName, args, null);
  }
  






  public static RenderedOp create(String opName, RenderedImage src)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    return create(opName, args, null);
  }
  





  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, Collection srcCol)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(srcCol);
    return create(opName, args, null);
  }
  










  public static RenderedOp create(String opName, RenderedImage src, Object param)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param);
    return create(opName, args, null);
  }
  









  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, RenderedImage src, int param)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param);
    return create(opName, args, null);
  }
  












  public static RenderedOp create(String opName, RenderedImage src, Object param1, Object param2)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param1);
    args.add(param2);
    return create(opName, args, null);
  }
  











  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, RenderedImage src, Object param1, float param2)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param1);
    args.add(param2);
    return create(opName, args, null);
  }
  














  public static RenderedOp create(String opName, RenderedImage src, Object param1, Object param2, Object param3)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param1);
    args.add(param2);
    args.add(param3);
    return create(opName, args, null);
  }
  













  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, RenderedImage src, Object param1, int param2, int param3)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param1);
    args.add(param2);
    args.add(param3);
    return create(opName, args, null);
  }
  













  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, RenderedImage src, float param1, float param2, Object param3)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param1);
    args.add(param2);
    args.add(param3);
    return create(opName, args, null);
  }
  















  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, RenderedImage src, Object param1, Object param2, Object param3, Object param4)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param1);
    args.add(param2);
    args.add(param3);
    args.add(param4);
    return create(opName, args, null);
  }
  















  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, RenderedImage src, Object param1, Object param2, int param3, int param4)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param1);
    args.add(param2);
    args.add(param3);
    args.add(param4);
    return create(opName, args, null);
  }
  















  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, RenderedImage src, int param1, int param2, int param3, int param4)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param1);
    args.add(param2);
    args.add(param3);
    args.add(param4);
    return create(opName, args, null);
  }
  















  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, RenderedImage src, float param1, float param2, float param3, Object param4)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param1);
    args.add(param2);
    args.add(param3);
    args.add(param4);
    return create(opName, args, null);
  }
  

















  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, RenderedImage src, Object param1, Object param2, Object param3, Object param4, Object param5)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param1);
    args.add(param2);
    args.add(param3);
    args.add(param4);
    args.add(param5);
    return create(opName, args, null);
  }
  
















  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, RenderedImage src, float param1, float param2, float param3, float param4, Object param5)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param1);
    args.add(param2);
    args.add(param3);
    args.add(param4);
    args.add(param5);
    return create(opName, args, null);
  }
  

















  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, RenderedImage src, float param1, int param2, float param3, float param4, Object param5)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param1);
    args.add(param2);
    args.add(param3);
    args.add(param4);
    args.add(param5);
    return create(opName, args, null);
  }
  



















  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, RenderedImage src, Object param1, Object param2, Object param3, Object param4, Object param5, Object param6)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param1);
    args.add(param2);
    args.add(param3);
    args.add(param4);
    args.add(param5);
    args.add(param6);
    return create(opName, args, null);
  }
  


















  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, RenderedImage src, int param1, int param2, int param3, int param4, int param5, Object param6)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src);
    args.add(param1);
    args.add(param2);
    args.add(param3);
    args.add(param4);
    args.add(param5);
    args.add(param6);
    return create(opName, args, null);
  }
  









  public static RenderedOp create(String opName, RenderedImage src1, RenderedImage src2)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src1);
    args.addSource(src2);
    return create(opName, args, null);
  }
  


















  /**
   * @deprecated
   */
  public static RenderedOp create(String opName, RenderedImage src1, RenderedImage src2, Object param1, Object param2, Object param3, Object param4)
  {
    ParameterBlock args = new ParameterBlock();
    args.addSource(src1);
    args.addSource(src2);
    args.add(param1);
    args.add(param2);
    args.add(param3);
    args.add(param4);
    return create(opName, args, null);
  }
  







  public static Collection createCollection(String opName, ParameterBlock args)
  {
    return createCollection(opName, args, null);
  }
  









































  public static RenderableOp createRenderable(String opName, ParameterBlock args, RenderingHints hints)
  {
    return defaultInstance.createRenderableNS(opName, args, hints);
  }
  



































  public static RenderableOp createRenderable(String opName, ParameterBlock args)
  {
    return defaultInstance.createRenderableNS(opName, args, null);
  }
  





































































  public RenderableOp createRenderableNS(String opName, ParameterBlock args, RenderingHints hints)
  {
    if (opName == null)
      throw new IllegalArgumentException(JaiI18N.getString("JAI14"));
    if (args == null) {
      throw new IllegalArgumentException(JaiI18N.getString("JAI15"));
    }
    
    String modeName = "renderable";
    

    OperationDescriptor odesc = (OperationDescriptor)operationRegistry.getDescriptor(modeName, opName);
    

    if (odesc == null) {
      throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI0"));
    }
    

    if (!RenderableImage.class.isAssignableFrom(odesc.getDestClass(modeName))) {
      throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI4"));
    }
    






    StringBuffer msg = new StringBuffer();
    args = (ParameterBlock)args.clone();
    if (!odesc.validateArguments(modeName, args, msg)) {
      throw new IllegalArgumentException(msg.toString());
    }
    

    RenderableOp op = new RenderableOp(operationRegistry, opName, args, mergeRenderingHints(renderingHints, hints));
    



    return op;
  }
  

































































  /**
   * @deprecated
   */
  public RenderableOp createRenderableNS(String opName, ParameterBlock args)
  {
    return createRenderableNS(opName, args, null);
  }
  









































  public static Collection createRenderableCollection(String opName, ParameterBlock args, RenderingHints hints)
  {
    return defaultInstance.createRenderableCollectionNS(opName, args, hints);
  }
  





































  public static Collection createRenderableCollection(String opName, ParameterBlock args)
  {
    return defaultInstance.createRenderableCollectionNS(opName, args, null);
  }
  




































































  /**
   * @deprecated
   */
  public Collection createRenderableCollectionNS(String opName, ParameterBlock args)
  {
    return createRenderableCollectionNS(opName, args, null);
  }
  














































































  public Collection createRenderableCollectionNS(String opName, ParameterBlock args, RenderingHints hints)
  {
    if (opName == null)
      throw new IllegalArgumentException(JaiI18N.getString("JAI14"));
    if (args == null) {
      throw new IllegalArgumentException(JaiI18N.getString("JAI15"));
    }
    
    String modeName = "renderableCollection";
    

    OperationDescriptor odesc = (OperationDescriptor)operationRegistry.getDescriptor(modeName, opName);
    

    if (odesc == null) {
      throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI0"));
    }
    

    Class destClass = odesc.getDestClass(modeName);
    
    if ((!RenderableImage.class.isAssignableFrom(destClass)) && (!CollectionImage.class.isAssignableFrom(destClass)))
    {
      throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI6"));
    }
    






    StringBuffer msg = new StringBuffer();
    args = (ParameterBlock)args.clone();
    RenderingHints mergedHints = mergeRenderingHints(renderingHints, hints);
    
    if (odesc.validateArguments(modeName, args, msg)) {
      if (RenderableImage.class.isAssignableFrom(destClass)) {
        Vector v = new Vector(1);
        RenderableOp op = new RenderableOp(operationRegistry, opName, args, mergedHints);
        

        v.add(op);
        return v;
      }
      CollectionOp cOp = new CollectionOp(operationRegistry, opName, args, mergedHints, true);
      



      if (odesc.isImmediate()) {
        Collection coll = null;
        coll = cOp.getCollection();
        if (coll == null) {
          return null;
        }
      }
      
      return cOp;
    }
    

    int numSources = odesc.getNumSources();
    Vector sources = args.getSources();
    




    Iterator[] iters = new Iterator[numSources];
    Iterator iter = null;
    int size = Integer.MAX_VALUE;
    for (int i = 0; i < numSources; i++) {
      Object s = sources.elementAt(i);
      if ((s instanceof Collection)) {
        iters[i] = ((Collection)s).iterator();
        if ((iter == null) || (((Collection)s).size() < size)) {
          iter = iters[i];
          size = ((Collection)s).size();
        }
      }
    }
    
    if (iter == null)
    {



      throw new IllegalArgumentException(msg.toString());
    }
    

    Collection col = null;
    for (int i = 0; i < numSources; i++) {
      Object s = sources.elementAt(i);
      if ((s instanceof Collection)) {
        try {
          col = (Collection)s.getClass().newInstance();
        }
        catch (Exception e)
        {
          sendExceptionToListener(JaiI18N.getString("JAI16") + s.getClass().getName(), e);
        }
      }
    }
    

    if (col == null) {
      col = new Vector();
    }
    

    Class[] sourceClasses = odesc.getSourceClasses(modeName);
    
    while (iter.hasNext()) {
      ParameterBlock pb = new ParameterBlock();
      pb.setParameters(args.getParameters());
      
      for (int i = 0; i < numSources; i++)
      {
        Object nextSource = null;
        if (iters[i] == null) {
          nextSource = sources.elementAt(i);
        } else {
          nextSource = iters[i].next();
        }
        




        if ((!sourceClasses[i].isAssignableFrom(nextSource.getClass())) && (!(nextSource instanceof Collection)))
        {
          throw new IllegalArgumentException(msg.toString());
        }
        pb.addSource(nextSource);
      }
      
      Collection c = createRenderableCollectionNS(opName, pb, mergedHints);
      
      if (((c instanceof Vector)) && (c.size() == 1) && ((((Vector)c).elementAt(0) instanceof RenderableOp)))
      {

        col.add(((Vector)c).elementAt(0));
      } else {
        col.add(c);
      }
    }
    
    return col;
  }
  








  static class RenderingKey
    extends RenderingHints.Key
  {
    private static Class JAIclass = JAI.class;
    private Class objectClass;
    
    RenderingKey(int privateKey, Class objectClass)
    {
      super();
      this.objectClass = objectClass;
    }
    
    public boolean isCompatibleValue(Object val) {
      return objectClass.isInstance(val);
    }
  }
  






  public RenderingHints getRenderingHints()
  {
    return renderingHints;
  }
  









  public void setRenderingHints(RenderingHints hints)
  {
    if (hints == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    renderingHints = hints;
  }
  



  public void clearRenderingHints()
  {
    renderingHints = new RenderingHints(null);
  }
  







  public Object getRenderingHint(RenderingHints.Key key)
  {
    if (key == null) {
      throw new IllegalArgumentException(JaiI18N.getString("JAI7"));
    }
    return renderingHints.get(key);
  }
  










  public void setRenderingHint(RenderingHints.Key key, Object value)
  {
    if (key == null) {
      throw new IllegalArgumentException(JaiI18N.getString("JAI7"));
    }
    if (value == null) {
      throw new IllegalArgumentException(JaiI18N.getString("JAI9"));
    }
    try {
      renderingHints.put(key, value);
    } catch (Exception e) {
      throw new IllegalArgumentException(e.toString());
    }
  }
  



  public void removeRenderingHint(RenderingHints.Key key)
  {
    renderingHints.remove(key);
  }
  











  public void setImagingListener(ImagingListener listener)
  {
    if (listener == null)
      listener = ImagingListenerImpl.getInstance();
    renderingHints.put(KEY_IMAGING_LISTENER, listener);
    imagingListener = listener;
  }
  






  public ImagingListener getImagingListener()
  {
    return imagingListener;
  }
  
  private void sendExceptionToListener(String message, Exception e) {
    ImagingListener listener = getImagingListener();
    listener.errorOccurred(message, e, this, false);
  }
}
