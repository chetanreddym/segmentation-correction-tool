package com.sun.media.jai.mlib;

import com.sun.media.jai.util.DataBufferUtils;
import com.sun.media.jai.util.ImageUtil;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.FilePermission;
import java.io.PrintStream;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.util.ImagingListener;









































































































public class MediaLibAccessor
{
  private static final int COPY_MASK_SHIFT = 7;
  private static final int COPY_MASK_SIZE = 1;
  public static final int COPY_MASK = 128;
  public static final int UNCOPIED = 0;
  public static final int COPIED = 128;
  public static final int DATATYPE_MASK = 127;
  private static final int BINARY_MASK_SHIFT = 8;
  private static final int BINARY_MASK_SIZE = 1;
  public static final int BINARY_MASK = 256;
  public static final int NONBINARY = 0;
  public static final int BINARY = 256;
  public static final int TAG_BYTE_UNCOPIED = 0;
  public static final int TAG_USHORT_UNCOPIED = 1;
  public static final int TAG_SHORT_UNCOPIED = 2;
  public static final int TAG_INT_UNCOPIED = 3;
  public static final int TAG_FLOAT_UNCOPIED = 4;
  public static final int TAG_DOUBLE_UNCOPIED = 5;
  public static final int TAG_BYTE_COPIED = 128;
  public static final int TAG_USHORT_COPIED = 129;
  public static final int TAG_SHORT_COPIED = 130;
  public static final int TAG_INT_COPIED = 131;
  public static final int TAG_FLOAT_COPIED = 132;
  public static final int TAG_DOUBLE_COPIED = 133;
  protected Raster raster;
  protected Rectangle rect;
  protected int numBands;
  protected int[] bandOffsets;
  protected int formatTag;
  protected mediaLibImage[] mlimages = null;
  




  private boolean areBinaryDataPacked = false;
  
  private static boolean useMlibVar = false;
  private static boolean useMlibVarSet = false;
  
  private static synchronized boolean useMlib() {
    if (!useMlibVarSet) {
      setUseMlib();
      useMlibVarSet = true;
    }
    
    return useMlibVar;
  }
  


  private static void setUseMlib()
  {
    boolean disableMediaLib = false;
    try {
      disableMediaLib = Boolean.getBoolean("com.sun.media.jai.disableMediaLib");
    }
    catch (AccessControlException e) {}
    











    if (disableMediaLib) {
      useMlibVar = false;
      return;
    }
    
    try
    {
      SecurityManager securityManager = System.getSecurityManager();
      

      if ((securityManager != null) && (MediaLibAccessor.class.getClassLoader() != null))
      {
















        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        

        if (((osName.equals("Solaris")) || (osName.equals("SunOS"))) && (osArch.equals("sparc")))
        {
          FilePermission fp = new FilePermission("/usr/bin/uname", "execute");
          
          securityManager.checkPermission(fp);
        }
      }
      
      Boolean result = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run() {
          return new Boolean(Image.isAvailable());
        }
      });
      useMlibVar = result.booleanValue();
      if (!useMlibVar) {
        forwardToListener(JaiI18N.getString("MediaLibAccessor2"), new MediaLibLoadException());
      }
    }
    catch (NoClassDefFoundError ncdfe)
    {
      useMlibVar = false;
      forwardToListener(JaiI18N.getString("MediaLibAccessor3"), ncdfe);
    }
    catch (ClassFormatError cfe) {
      useMlibVar = false;
      forwardToListener(JaiI18N.getString("MediaLibAccessor3"), cfe);
    }
    catch (SecurityException se) {
      useMlibVar = false;
      forwardToListener(JaiI18N.getString("MediaLibAccessor4"), se);
    }
    
    if (!useMlibVar) {}
  }
  







  private static void forwardToListener(String message, Throwable thrown)
  {
    ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
    

    if (listener != null) {
      listener.errorOccurred(message, thrown, MediaLibAccessor.class, false);
    }
    else {
      System.err.println(message);
    }
  }
  




















  public static boolean isMediaLibCompatible(ParameterBlock args, ImageLayout layout)
  {
    if (!isMediaLibCompatible(args))
    {
      return false;
    }
    
    if (layout != null) {
      SampleModel sm = layout.getSampleModel(null);
      if ((sm != null) && (
        (!(sm instanceof ComponentSampleModel)) || (sm.getNumBands() > 4)))
      {
        return false;
      }
      

      ColorModel cm = layout.getColorModel(null);
      if ((cm != null) && (!(cm instanceof ComponentColorModel))) {
        return false;
      }
    }
    
    return true;
  }
  

















  public static boolean isMediaLibCompatible(ParameterBlock args)
  {
    if (!useMlib()) {
      return false;
    }
    
    int numSrcs = args.getNumSources();
    for (int i = 0; i < numSrcs; i++) {
      Object src = args.getSource(i);
      if ((!(src instanceof RenderedImage)) || (!isMediaLibCompatible((RenderedImage)src)))
      {
        return false;
      }
    }
    
    return true;
  }
  















  public static boolean isMediaLibCompatible(RenderedImage image)
  {
    if (!useMlib()) {
      return false;
    }
    
    SampleModel sm = image.getSampleModel();
    ColorModel cm = image.getColorModel();
    
    return ((sm instanceof ComponentSampleModel)) && (sm.getNumBands() <= 4) && ((cm == null) || ((cm instanceof ComponentColorModel)));
  }
  





















  public static boolean isMediaLibCompatible(SampleModel sm, ColorModel cm)
  {
    if (!useMlib()) {
      return false;
    }
    
    return ((sm instanceof ComponentSampleModel)) && (sm.getNumBands() <= 4) && ((cm == null) || ((cm instanceof ComponentColorModel)));
  }
  
























  public static boolean isMediaLibBinaryCompatible(ParameterBlock args, ImageLayout layout)
  {
    if (!useMlib()) {
      return false;
    }
    
    SampleModel sm = null;
    
    int numSrcs = args.getNumSources();
    for (int i = 0; i < numSrcs; i++) {
      Object src = args.getSource(i);
      if ((!(src instanceof RenderedImage)) || ((sm = ((RenderedImage)src).getSampleModel()) == null) || (!ImageUtil.isBinary(sm)))
      {

        return false;
      }
    }
    
    if ((layout != null) && 
      ((sm = layout.getSampleModel(null)) != null) && (!ImageUtil.isBinary(sm)))
    {
      return false;
    }
    

    return true;
  }
  







  public static boolean hasSameNumBands(ParameterBlock args, ImageLayout layout)
  {
    int numSrcs = args.getNumSources();
    
    if (numSrcs > 0) {
      RenderedImage src = args.getRenderedSource(0);
      int numBands = src.getSampleModel().getNumBands();
      
      for (int i = 1; i < numSrcs; i++) {
        src = args.getRenderedSource(i);
        if (src.getSampleModel().getNumBands() != numBands) {
          return false;
        }
      }
      
      if (layout != null) {
        SampleModel sm = layout.getSampleModel(null);
        if ((sm != null) && (sm.getNumBands() != numBands)) {
          return false;
        }
      }
    }
    
    return true;
  }
  






  public static int findCompatibleTag(Raster[] srcs, Raster dst)
  {
    SampleModel dstSM = dst.getSampleModel();
    int dstDT = dstSM.getDataType();
    
    int defaultDataType = dstSM.getDataType();
    
    boolean allComponentSampleModel = dstSM instanceof ComponentSampleModel;
    
    boolean allBinary = ImageUtil.isBinary(dstSM);
    

    if (srcs != null) {
      int numSources = srcs.length;
      
      for (int i = 0; i < numSources; i++) {
        SampleModel srcSampleModel = srcs[i].getSampleModel();
        if (!(srcSampleModel instanceof ComponentSampleModel)) {
          allComponentSampleModel = false;
        }
        if (!ImageUtil.isBinary(srcSampleModel)) {
          allBinary = false;
        }
        int srcDataType = srcSampleModel.getTransferType();
        if (srcDataType > defaultDataType) {
          defaultDataType = srcDataType;
        }
      }
    }
    
    if (allBinary)
    {


      return 256;
    }
    
    if ((!allComponentSampleModel) && (
      (defaultDataType == 0) || (defaultDataType == 1) || (defaultDataType == 2)))
    {

      defaultDataType = 3;
    }
    

    int tag = defaultDataType | 0x80;
    
    if (!allComponentSampleModel) {
      return tag;
    }
    
    SampleModel[] srcSM;
    
    SampleModel[] srcSM;
    if (srcs == null) {
      srcSM = new SampleModel[0];
    } else {
      srcSM = new SampleModel[srcs.length];
    }
    for (int i = 0; i < srcSM.length; i++) {
      srcSM[i] = srcs[i].getSampleModel();
      if (dstDT != srcSM[i].getDataType()) {
        return tag;
      }
    }
    if (isPixelSequential(dstSM)) {
      for (int i = 0; i < srcSM.length; i++) {
        if (!isPixelSequential(srcSM[i])) {
          return tag;
        }
      }
      for (int i = 0; i < srcSM.length; i++) {
        if (!hasMatchingBandOffsets((ComponentSampleModel)dstSM, (ComponentSampleModel)srcSM[i]))
        {
          return tag;
        }
      }
      return dstDT | 0x0;
    }
    return tag;
  }
  



  public static boolean isPixelSequential(SampleModel sm)
  {
    ComponentSampleModel csm = null;
    if ((sm instanceof ComponentSampleModel)) {
      csm = (ComponentSampleModel)sm;
    } else {
      return false;
    }
    int pixelStride = csm.getPixelStride();
    int[] bandOffsets = csm.getBandOffsets();
    int[] bankIndices = csm.getBankIndices();
    if (pixelStride != bandOffsets.length) {
      return false;
    }
    for (int i = 0; i < bandOffsets.length; i++) {
      if ((bandOffsets[i] >= pixelStride) || (bankIndices[i] != bankIndices[0]))
      {
        return false;
      }
      for (int j = i + 1; j < bandOffsets.length; j++) {
        if (bandOffsets[i] == bandOffsets[j]) {
          return false;
        }
      }
    }
    return true;
  }
  





  public static boolean hasMatchingBandOffsets(ComponentSampleModel dst, ComponentSampleModel src)
  {
    int[] srcBandOffsets = dst.getBandOffsets();
    int[] dstBandOffsets = src.getBandOffsets();
    if (srcBandOffsets.length != dstBandOffsets.length) {
      return false;
    }
    for (int i = 0; i < srcBandOffsets.length; i++) {
      if (srcBandOffsets[i] != dstBandOffsets[i]) {
        return false;
      }
    }
    return true;
  }
  
  public static int getMediaLibDataType(int formatTag) {
    int dataType = formatTag & 0x7F;
    switch (dataType) {
    case 0: 
      return 1;
    case 1: 
      return 6;
    case 2: 
      return 2;
    case 3: 
      return 3;
    case 5: 
      return 5;
    case 4: 
      return 4;
    }
    return -1;
  }
  









  public MediaLibAccessor(Raster raster, Rectangle rect, int formatTag, boolean preferPacked)
  {
    areBinaryDataPacked = preferPacked;
    
    this.raster = raster;
    this.rect = new Rectangle(rect);
    this.formatTag = formatTag;
    
    if (isBinary())
    {
      numBands = 1;
      bandOffsets = new int[] { 0 };
      



      mlimages = new mediaLibImage[1];
      int mlibType;
      int scanlineStride; byte[] bdata; if (areBinaryDataPacked) {
        int mlibType = 0;
        int scanlineStride = (width + 7) / 8;
        byte[] bdata = ImageUtil.getPackedBinaryData(raster, rect);
        

        if (bdata == ((DataBufferByte)raster.getDataBuffer()).getData())
        {
          this.formatTag |= 0x0;
        } else {
          this.formatTag |= 0x80;
        }
      } else {
        mlibType = 1;
        scanlineStride = width;
        bdata = ImageUtil.getUnpackedBinaryData(raster, rect);
        this.formatTag |= 0x80;
      }
      
      mlimages[0] = new mediaLibImage(mlibType, 1, width, height, scanlineStride, 0, bdata);
      






      return;
    }
    
    if ((formatTag & 0x80) == 0) {
      ComponentSampleModel csm = (ComponentSampleModel)raster.getSampleModel();
      

      numBands = csm.getNumBands();
      bandOffsets = csm.getBandOffsets();
      int dataOffset = raster.getDataBuffer().getOffset();
      dataOffset += (y - raster.getSampleModelTranslateY()) * csm.getScanlineStride() + (x - raster.getSampleModelTranslateX()) * csm.getPixelStride();
      





      int scanlineStride = csm.getScanlineStride();
      
      switch (formatTag & 0x7F) {
      case 0: 
        DataBufferByte dbb = (DataBufferByte)raster.getDataBuffer();
        mlimages = new mediaLibImage[1];
        mlimages[0] = new mediaLibImage(1, numBands, width, height, scanlineStride, dataOffset, dbb.getData());
        






        break;
      
      case 1: 
        DataBufferUShort dbus = (DataBufferUShort)raster.getDataBuffer();
        
        mlimages = new mediaLibImage[1];
        mlimages[0] = new mediaLibImage(6, numBands, width, height, scanlineStride, dataOffset, dbus.getData());
        






        break;
      case 2: 
        DataBufferShort dbs = (DataBufferShort)raster.getDataBuffer();
        mlimages = new mediaLibImage[1];
        mlimages[0] = new mediaLibImage(2, numBands, width, height, scanlineStride, dataOffset, dbs.getData());
        






        break;
      case 3: 
        DataBufferInt dbi = (DataBufferInt)raster.getDataBuffer();
        mlimages = new mediaLibImage[1];
        mlimages[0] = new mediaLibImage(3, numBands, width, height, scanlineStride, dataOffset, dbi.getData());
        






        break;
      case 4: 
        DataBuffer dbf = raster.getDataBuffer();
        mlimages = new mediaLibImage[1];
        mlimages[0] = new mediaLibImage(4, numBands, width, height, scanlineStride, dataOffset, DataBufferUtils.getDataFloat(dbf));
        






        break;
      case 5: 
        DataBuffer dbd = raster.getDataBuffer();
        mlimages = new mediaLibImage[1];
        mlimages[0] = new mediaLibImage(5, numBands, width, height, scanlineStride, dataOffset, DataBufferUtils.getDataDouble(dbd));
        






        break;
      default: 
        throw new IllegalArgumentException((formatTag & 0x7F) + JaiI18N.getString("MediaLibAccessor1"));
      }
    }
    else {
      numBands = raster.getNumBands();
      bandOffsets = new int[numBands];
      for (int i = 0; i < numBands; i++) {
        bandOffsets[i] = i;
      }
      int scanlineStride = width * numBands;
      
      switch (formatTag & 0x7F) {
      case 0: 
        byte[] bdata = new byte[width * height * numBands];
        mlimages = new mediaLibImage[1];
        mlimages[0] = new mediaLibImage(1, numBands, width, height, scanlineStride, 0, bdata);
        






        break;
      case 1: 
        short[] usdata = new short[width * height * numBands];
        mlimages = new mediaLibImage[1];
        mlimages[0] = new mediaLibImage(6, numBands, width, height, scanlineStride, 0, usdata);
        






        break;
      case 2: 
        short[] sdata = new short[width * height * numBands];
        mlimages = new mediaLibImage[1];
        mlimages[0] = new mediaLibImage(2, numBands, width, height, scanlineStride, 0, sdata);
        






        break;
      case 3: 
        int[] idata = new int[width * height * numBands];
        mlimages = new mediaLibImage[1];
        mlimages[0] = new mediaLibImage(3, numBands, width, height, scanlineStride, 0, idata);
        






        break;
      case 4: 
        float[] fdata = new float[width * height * numBands];
        mlimages = new mediaLibImage[1];
        mlimages[0] = new mediaLibImage(4, numBands, width, height, scanlineStride, 0, fdata);
        






        break;
      case 5: 
        double[] ddata = new double[width * height * numBands];
        mlimages = new mediaLibImage[1];
        mlimages[0] = new mediaLibImage(5, numBands, width, height, scanlineStride, 0, ddata);
        






        break;
      default: 
        throw new IllegalArgumentException((formatTag & 0x7F) + JaiI18N.getString("MediaLibAccessor1"));
      }
      copyDataFromRaster();
    }
  }
  



  public MediaLibAccessor(Raster raster, Rectangle rect, int formatTag)
  {
    this(raster, rect, formatTag, false);
  }
  



  public boolean isBinary()
  {
    return (formatTag & 0x100) == 256;
  }
  






  public mediaLibImage[] getMediaLibImages()
  {
    return mlimages;
  }
  




  public int getDataType()
  {
    return formatTag & 0x7F;
  }
  



  public boolean isDataCopy()
  {
    return (formatTag & 0x80) == 128;
  }
  
  public int[] getBandOffsets()
  {
    return bandOffsets;
  }
  




  public int[] getIntParameters(int band, int[] params)
  {
    int[] returnParams = new int[numBands];
    for (int i = 0; i < numBands; i++) {
      returnParams[i] = params[bandOffsets[(i + band)]];
    }
    return returnParams;
  }
  




  public int[][] getIntArrayParameters(int band, int[][] params)
  {
    int[][] returnParams = new int[numBands][];
    for (int i = 0; i < numBands; i++) {
      returnParams[i] = params[bandOffsets[(i + band)]];
    }
    return returnParams;
  }
  




  public double[] getDoubleParameters(int band, double[] params)
  {
    double[] returnParams = new double[numBands];
    for (int i = 0; i < numBands; i++) {
      returnParams[i] = params[bandOffsets[(i + band)]];
    }
    return returnParams;
  }
  






  private void copyDataFromRaster()
  {
    if ((raster.getSampleModel() instanceof ComponentSampleModel)) {
      ComponentSampleModel csm = (ComponentSampleModel)raster.getSampleModel();
      
      int rasScanlineStride = csm.getScanlineStride();
      int rasPixelStride = csm.getPixelStride();
      
      int subRasterOffset = (rect.y - raster.getSampleModelTranslateY()) * rasScanlineStride + (rect.x - raster.getSampleModelTranslateX()) * rasPixelStride;
      


      int[] rasBankIndices = csm.getBankIndices();
      int[] rasBandOffsets = csm.getBandOffsets();
      int[] rasDataOffsets = raster.getDataBuffer().getOffsets();
      
      if (rasDataOffsets.length == 1) {
        for (int i = 0; i < numBands; i++) {
          rasBandOffsets[i] += rasDataOffsets[0] + subRasterOffset;
        }
        
      } else if (rasDataOffsets.length == rasBandOffsets.length) {
        for (int i = 0; i < numBands; i++) {
          rasBandOffsets[i] += rasDataOffsets[i] + subRasterOffset;
        }
      }
      

      Object mlibDataArray = null;
      switch (getDataType()) {
      case 0: 
        byte[][] bArray = new byte[numBands][];
        for (int i = 0; i < numBands; i++) {
          bArray[i] = mlimages[0].getByteData();
        }
        mlibDataArray = bArray;
        break;
      case 1: 
        short[][] usArray = new short[numBands][];
        for (int i = 0; i < numBands; i++) {
          usArray[i] = mlimages[0].getUShortData();
        }
        mlibDataArray = usArray;
        break;
      case 2: 
        short[][] sArray = new short[numBands][];
        for (int i = 0; i < numBands; i++) {
          sArray[i] = mlimages[0].getShortData();
        }
        mlibDataArray = sArray;
        break;
      case 3: 
        int[][] iArray = new int[numBands][];
        for (int i = 0; i < numBands; i++) {
          iArray[i] = mlimages[0].getIntData();
        }
        mlibDataArray = iArray;
        break;
      case 4: 
        float[][] fArray = new float[numBands][];
        for (int i = 0; i < numBands; i++) {
          fArray[i] = mlimages[0].getFloatData();
        }
        mlibDataArray = fArray;
        break;
      case 5: 
        double[][] dArray = new double[numBands][];
        for (int i = 0; i < numBands; i++) {
          dArray[i] = mlimages[0].getDoubleData();
        }
        mlibDataArray = dArray;
      }
      
      


      Object rasDataArray = null;
      switch (csm.getDataType()) {
      case 0: 
        DataBufferByte dbb = (DataBufferByte)raster.getDataBuffer();
        
        byte[][] rasByteDataArray = new byte[numBands][];
        for (int i = 0; i < numBands; i++) {
          rasByteDataArray[i] = dbb.getData(rasBankIndices[i]);
        }
        
        rasDataArray = rasByteDataArray;
        
        break;
      case 1: 
        DataBufferUShort dbus = (DataBufferUShort)raster.getDataBuffer();
        
        short[][] rasUShortDataArray = new short[numBands][];
        for (int i = 0; i < numBands; i++) {
          rasUShortDataArray[i] = dbus.getData(rasBankIndices[i]);
        }
        
        rasDataArray = rasUShortDataArray;
        
        break;
      case 2: 
        DataBufferShort dbs = (DataBufferShort)raster.getDataBuffer();
        
        short[][] rasShortDataArray = new short[numBands][];
        for (int i = 0; i < numBands; i++) {
          rasShortDataArray[i] = dbs.getData(rasBankIndices[i]);
        }
        
        rasDataArray = rasShortDataArray;
        
        break;
      case 3: 
        DataBufferInt dbi = (DataBufferInt)raster.getDataBuffer();
        
        int[][] rasIntDataArray = new int[numBands][];
        for (int i = 0; i < numBands; i++) {
          rasIntDataArray[i] = dbi.getData(rasBankIndices[i]);
        }
        
        rasDataArray = rasIntDataArray;
        
        break;
      case 4: 
        DataBuffer dbf = raster.getDataBuffer();
        
        float[][] rasFloatDataArray = new float[numBands][];
        for (int i = 0; i < numBands; i++) {
          rasFloatDataArray[i] = DataBufferUtils.getDataFloat(dbf, rasBankIndices[i]);
        }
        
        rasDataArray = rasFloatDataArray;
        
        break;
      case 5: 
        DataBuffer dbd = raster.getDataBuffer();
        
        double[][] rasDoubleDataArray = new double[numBands][];
        for (int i = 0; i < numBands; i++) {
          rasDoubleDataArray[i] = DataBufferUtils.getDataDouble(dbd, rasBankIndices[i]);
        }
        
        rasDataArray = rasDoubleDataArray;
      }
      
      



      Image.Reformat(mlibDataArray, rasDataArray, numBands, rect.width, rect.height, getMediaLibDataType(getDataType()), bandOffsets, rect.width * numBands, numBands, getMediaLibDataType(csm.getDataType()), rasBandOffsets, rasScanlineStride, rasPixelStride);






    }
    else
    {






      switch (getDataType()) {
      case 3: 
        raster.getPixels(rect.x, rect.y, rect.width, rect.height, mlimages[0].getIntData());
        

        break;
      case 4: 
        raster.getPixels(rect.x, rect.y, rect.width, rect.height, mlimages[0].getFloatData());
        

        break;
      case 5: 
        raster.getPixels(rect.x, rect.y, rect.width, rect.height, mlimages[0].getDoubleData());
      }
      
    }
  }
  









  public void copyDataToRaster()
  {
    if (isDataCopy())
    {
      if (isBinary()) {
        if (areBinaryDataPacked) {
          ImageUtil.setPackedBinaryData(mlimages[0].getBitData(), (WritableRaster)raster, rect);
        }
        else
        {
          ImageUtil.setUnpackedBinaryData(mlimages[0].getByteData(), (WritableRaster)raster, rect);
        }
        

        return;
      }
      


      WritableRaster wr = (WritableRaster)raster;
      
      if ((wr.getSampleModel() instanceof ComponentSampleModel)) {
        ComponentSampleModel csm = (ComponentSampleModel)wr.getSampleModel();
        
        int rasScanlineStride = csm.getScanlineStride();
        int rasPixelStride = csm.getPixelStride();
        
        int subRasterOffset = (rect.y - raster.getSampleModelTranslateY()) * rasScanlineStride + (rect.x - raster.getSampleModelTranslateX()) * rasPixelStride;
        


        int[] rasBankIndices = csm.getBankIndices();
        int[] rasBandOffsets = csm.getBandOffsets();
        int[] rasDataOffsets = raster.getDataBuffer().getOffsets();
        
        if (rasDataOffsets.length == 1) {
          for (int i = 0; i < numBands; i++) {
            rasBandOffsets[i] += rasDataOffsets[0] + subRasterOffset;
          }
          
        } else if (rasDataOffsets.length == rasBandOffsets.length) {
          for (int i = 0; i < numBands; i++) {
            rasBandOffsets[i] += rasDataOffsets[i] + subRasterOffset;
          }
        }
        

        Object mlibDataArray = null;
        switch (getDataType()) {
        case 0: 
          byte[][] bArray = new byte[numBands][];
          for (int i = 0; i < numBands; i++) {
            bArray[i] = mlimages[0].getByteData();
          }
          mlibDataArray = bArray;
          break;
        case 1: 
          short[][] usArray = new short[numBands][];
          for (int i = 0; i < numBands; i++) {
            usArray[i] = mlimages[0].getUShortData();
          }
          mlibDataArray = usArray;
          break;
        case 2: 
          short[][] sArray = new short[numBands][];
          for (int i = 0; i < numBands; i++) {
            sArray[i] = mlimages[0].getShortData();
          }
          mlibDataArray = sArray;
          break;
        case 3: 
          int[][] iArray = new int[numBands][];
          for (int i = 0; i < numBands; i++) {
            iArray[i] = mlimages[0].getIntData();
          }
          mlibDataArray = iArray;
          break;
        case 4: 
          float[][] fArray = new float[numBands][];
          for (int i = 0; i < numBands; i++) {
            fArray[i] = mlimages[0].getFloatData();
          }
          mlibDataArray = fArray;
          break;
        case 5: 
          double[][] dArray = new double[numBands][];
          for (int i = 0; i < numBands; i++) {
            dArray[i] = mlimages[0].getDoubleData();
          }
          mlibDataArray = dArray;
        }
        
        

        byte[] tmpDataArray = null;
        Object rasDataArray = null;
        switch (csm.getDataType()) {
        case 0: 
          DataBufferByte dbb = (DataBufferByte)raster.getDataBuffer();
          
          byte[][] rasByteDataArray = new byte[numBands][];
          for (int i = 0; i < numBands; i++) {
            rasByteDataArray[i] = dbb.getData(rasBankIndices[i]);
          }
          
          tmpDataArray = rasByteDataArray[0];
          rasDataArray = rasByteDataArray;
          
          break;
        case 1: 
          DataBufferUShort dbus = (DataBufferUShort)raster.getDataBuffer();
          
          short[][] rasUShortDataArray = new short[numBands][];
          for (int i = 0; i < numBands; i++) {
            rasUShortDataArray[i] = dbus.getData(rasBankIndices[i]);
          }
          
          rasDataArray = rasUShortDataArray;
          
          break;
        case 2: 
          DataBufferShort dbs = (DataBufferShort)raster.getDataBuffer();
          
          short[][] rasShortDataArray = new short[numBands][];
          for (int i = 0; i < numBands; i++) {
            rasShortDataArray[i] = dbs.getData(rasBankIndices[i]);
          }
          
          rasDataArray = rasShortDataArray;
          
          break;
        case 3: 
          DataBufferInt dbi = (DataBufferInt)raster.getDataBuffer();
          
          int[][] rasIntDataArray = new int[numBands][];
          for (int i = 0; i < numBands; i++) {
            rasIntDataArray[i] = dbi.getData(rasBankIndices[i]);
          }
          
          rasDataArray = rasIntDataArray;
          
          break;
        case 4: 
          DataBuffer dbf = raster.getDataBuffer();
          
          float[][] rasFloatDataArray = new float[numBands][];
          for (int i = 0; i < numBands; i++) {
            rasFloatDataArray[i] = DataBufferUtils.getDataFloat(dbf, rasBankIndices[i]);
          }
          
          rasDataArray = rasFloatDataArray;
          
          break;
        case 5: 
          DataBuffer dbd = raster.getDataBuffer();
          
          double[][] rasDoubleDataArray = new double[numBands][];
          for (int i = 0; i < numBands; i++) {
            rasDoubleDataArray[i] = DataBufferUtils.getDataDouble(dbd, rasBankIndices[i]);
          }
          
          rasDataArray = rasDoubleDataArray;
        }
        
        



        Image.Reformat(rasDataArray, mlibDataArray, numBands, rect.width, rect.height, getMediaLibDataType(csm.getDataType()), rasBandOffsets, rasScanlineStride, rasPixelStride, getMediaLibDataType(getDataType()), bandOffsets, rect.width * numBands, numBands);






      }
      else
      {






        switch (getDataType()) {
        case 3: 
          wr.setPixels(rect.x, rect.y, rect.width, rect.height, mlimages[0].getIntData());
          

          break;
        case 4: 
          wr.setPixels(rect.x, rect.y, rect.width, rect.height, mlimages[0].getFloatData());
          

          break;
        case 5: 
          wr.setPixels(rect.x, rect.y, rect.width, rect.height, mlimages[0].getDoubleData());
        }
        
      }
    }
  }
  














  public void clampDataArrays()
  {
    if (!isDataCopy()) {
      return;
    }
    




    if ((raster.getSampleModel() instanceof ComponentSampleModel)) {
      return;
    }
    
    int[] bits = raster.getSampleModel().getSampleSize();
    






    boolean needClamp = false;
    boolean uniformBitSize = true;
    for (int i = 0; i < bits.length; i++) {
      int bitSize = bits[0];
      if (bits[i] < 32) {
        needClamp = true;
      }
      if (bits[i] != bitSize) {
        uniformBitSize = false;
      }
    }
    
    if (!needClamp) {
      return;
    }
    
    int dataType = raster.getDataBuffer().getDataType();
    double[] hiVals = new double[bits.length];
    double[] loVals = new double[bits.length];
    
    if ((dataType == 1) && (uniformBitSize) && (bits[0] == 16))
    {
      for (int i = 0; i < bits.length; i++) {
        hiVals[i] = 65535.0D;
        loVals[i] = 0.0D;
      }
    } else if ((dataType == 2) && (uniformBitSize) && (bits[0] == 16))
    {
      for (int i = 0; i < bits.length; i++) {
        hiVals[i] = 32767.0D;
        loVals[i] = -32768.0D;
      }
    } else if ((dataType == 3) && (uniformBitSize) && (bits[0] == 32))
    {
      for (int i = 0; i < bits.length; i++) {
        hiVals[i] = 2.147483647E9D;
        loVals[i] = -2.147483648E9D;
      }
    } else {
      for (int i = 0; i < bits.length; i++) {
        hiVals[i] = ((1 << bits[i]) - 1);
        loVals[i] = 0.0D;
      }
    }
    clampDataArray(hiVals, loVals);
  }
  
  private void clampDataArray(double[] hiVals, double[] loVals) {
    switch (getDataType()) {
    case 3: 
      clampIntArrays(toIntArray(hiVals), toIntArray(loVals));
      break;
    case 4: 
      clampFloatArrays(toFloatArray(hiVals), toFloatArray(loVals));
      break;
    case 5: 
      clampDoubleArrays(hiVals, loVals);
    }
  }
  
  private int[] toIntArray(double[] vals)
  {
    int[] returnVals = new int[vals.length];
    for (int i = 0; i < vals.length; i++) {
      returnVals[i] = ((int)vals[i]);
    }
    return returnVals;
  }
  
  private float[] toFloatArray(double[] vals) {
    float[] returnVals = new float[vals.length];
    for (int i = 0; i < vals.length; i++) {
      returnVals[i] = ((float)vals[i]);
    }
    return returnVals;
  }
  
  private void clampIntArrays(int[] hiVals, int[] loVals) {
    int width = rect.width;
    int height = rect.height;
    int scanlineStride = numBands * width;
    for (int k = 0; k < numBands; k++) {
      int[] data = mlimages[0].getIntData();
      int scanlineOffset = k;
      int hiVal = hiVals[k];
      int loVal = loVals[k];
      for (int j = 0; j < height; j++) {
        int pixelOffset = scanlineOffset;
        for (int i = 0; i < width; i++) {
          int tmp = data[pixelOffset];
          if (tmp < loVal) {
            data[pixelOffset] = loVal;
          } else if (tmp > hiVal) {
            data[pixelOffset] = hiVal;
          }
          pixelOffset += numBands;
        }
        scanlineOffset += scanlineStride;
      }
    }
  }
  
  private void clampFloatArrays(float[] hiVals, float[] loVals) {
    int width = rect.width;
    int height = rect.height;
    int scanlineStride = numBands * width;
    for (int k = 0; k < numBands; k++) {
      float[] data = mlimages[0].getFloatData();
      int scanlineOffset = k;
      float hiVal = hiVals[k];
      float loVal = loVals[k];
      for (int j = 0; j < height; j++) {
        int pixelOffset = scanlineOffset;
        for (int i = 0; i < width; i++) {
          float tmp = data[pixelOffset];
          if (tmp < loVal) {
            data[pixelOffset] = loVal;
          } else if (tmp > hiVal) {
            data[pixelOffset] = hiVal;
          }
          pixelOffset += numBands;
        }
        scanlineOffset += scanlineStride;
      }
    }
  }
  
  private void clampDoubleArrays(double[] hiVals, double[] loVals) {
    int width = rect.width;
    int height = rect.height;
    int scanlineStride = numBands * width;
    for (int k = 0; k < numBands; k++) {
      double[] data = mlimages[0].getDoubleData();
      int scanlineOffset = k;
      double hiVal = hiVals[k];
      double loVal = loVals[k];
      for (int j = 0; j < height; j++) {
        int pixelOffset = scanlineOffset;
        for (int i = 0; i < width; i++) {
          double tmp = data[pixelOffset];
          if (tmp < loVal) {
            data[pixelOffset] = loVal;
          } else if (tmp > hiVal) {
            data[pixelOffset] = hiVal;
          }
          pixelOffset += numBands;
        }
        scanlineOffset += scanlineStride;
      }
    }
  }
}
