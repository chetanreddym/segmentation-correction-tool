package com.sun.media.jai.opimage;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;
import com.sun.media.jai.util.ImageUtil;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.renderable.RenderableImageOp;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Vector;
import javax.media.jai.CRIFImpl;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.MultiResolutionRenderableImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.TiledImage;
import javax.media.jai.operator.TransposeDescriptor;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;



























































public class IIPCRIF
  extends CRIFImpl
{
  private static final int MASK_FILTER = 1;
  private static final int MASK_COLOR_TWIST = 2;
  private static final int MASK_CONTRAST = 4;
  private static final int MASK_ROI_SOURCE = 8;
  private static final int MASK_TRANSFORM = 16;
  private static final int MASK_ASPECT_RATIO = 32;
  private static final int MASK_ROI_DESTINATION = 64;
  private static final int MASK_ROTATION = 128;
  private static final int MASK_MIRROR_AXIS = 256;
  private static final int MASK_ICC_PROFILE = 512;
  private static final int MASK_JPEG_QUALITY = 1024;
  private static final int MASK_JPEG_TABLE = 2048;
  private static final int VENDOR_HP = 0;
  private static final int VENDOR_LIVE_PICTURE = 1;
  private static final int VENDOR_KODAK = 2;
  private static final int VENDOR_UNREGISTERED = 255;
  private static final int VENDOR_EXPERIMENTAL = 999;
  private static final int SERVER_CVT_JPEG = 1;
  private static final int SERVER_CVT_FPX = 2;
  private static final int SERVER_CVT_MJPEG = 4;
  private static final int SERVER_CVT_MFPX = 8;
  private static final int SERVER_CVT_M2JPEG = 16;
  private static final int SERVER_CVT_M2FPX = 32;
  private static final int SERVER_CVT_JTL = 64;
  private static final int SERVER_JPEG_PARTIAL = 5;
  private static final int SERVER_JPEG_FULL = 21;
  private static final int SERVER_FPX_PARTIAL = 10;
  private static final int SERVER_FPX_FULL = 42;
  private static final double[][] YCCA_TO_RGBA = { { 1.3584D, 0.0D, 1.8215D, 0.0D }, { 1.3584D, -0.4303D, -0.9271D, 0.0D }, { 1.3584D, 2.2179D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 1.0D } };
  






  private static final double[][] YCCA_TO_RGBA_CONST = { { -249.55D }, { 194.14D }, { -345.99D }, { 0.0D } };
  


  private static final double[][] RGBA_TO_YCCA = { { 0.220018D, 0.432276D, 0.083867D, 0.0D }, { -0.134755D, -0.264756D, 0.399511D, 0.0D }, { 0.384918D, -0.322373D, -0.062544D, 0.0D }, { 0.0D, 0.0D, 0.0D, 1.0D } };
  






  private static final double[][] RGBA_TO_YCCA_CONST = { { 5.726E-4D }, { 155.9984D }, { 137.0022D }, { 0.0D } };
  


  private static final double[][] YCC_TO_RGB = { { 1.3584D, 0.0D, 1.8215D }, { 1.3584D, -0.4303D, -0.9271D }, { 1.3584D, 2.2179D, 0.0D } };
  





  private static final double[][] YCC_TO_RGB_CONST = { { -249.55D }, { 194.14D }, { -345.99D } };
  


  private static final double[][] RGB_TO_YCC = { { 0.220018D, 0.432276D, 0.083867D }, { -0.134755D, -0.264756D, 0.399511D }, { 0.384918D, -0.322373D, -0.062544D } };
  





  private static final double[][] RGB_TO_YCC_CONST = { { 5.726E-4D }, { 155.9984D }, { 137.0022D } };
  



  private static final int getOperationMask(ParameterBlock pb)
  {
    int opMask = 0;
    


    if (pb.getFloatParameter(2) != 0.0F) {
      opMask |= 0x1;
    }
    if (pb.getObjectParameter(3) != null) {
      opMask |= 0x2;
    }
    if (Math.abs(pb.getFloatParameter(4) - 1.0F) > 0.01F) {
      opMask |= 0x4;
    }
    if (pb.getObjectParameter(5) != null) {
      opMask |= 0x8;
    }
    AffineTransform tf = (AffineTransform)pb.getObjectParameter(6);
    if (!tf.isIdentity()) {
      opMask |= 0x10;
    }
    if (pb.getObjectParameter(7) != null) {
      opMask |= 0x20;
    }
    if (pb.getObjectParameter(8) != null) {
      opMask |= 0x40;
    }
    if (pb.getIntParameter(9) != 0) {
      opMask |= 0x80;
    }
    if (pb.getObjectParameter(10) != null) {
      opMask |= 0x100;
    }
    if (pb.getObjectParameter(11) != null) {
      opMask |= 0x200;
    }
    if (pb.getObjectParameter(12) != null) {
      opMask |= 0x400;
    }
    if (pb.getObjectParameter(13) != null) {
      opMask |= 0x800;
    }
    
    return opMask;
  }
  



  private static final int getServerCapabilityMask(String URLSpec, RenderedImage lowRes)
  {
    int vendorID = 255;
    int serverMask = 0;
    

    if ((lowRes.getProperty("iip-server") != null) && (lowRes.getProperty("iip-server") != Image.UndefinedProperty))
    {
      String serverString = (String)lowRes.getProperty("iip-server");
      int dot = serverString.indexOf(".");
      vendorID = Integer.valueOf(serverString.substring(0, dot)).intValue();
      
      serverMask = Integer.valueOf(serverString.substring(dot + 1)).intValue();
    }
    





    if ((serverMask != 127) && (vendorID != 0) && (vendorID != 1) && (vendorID != 2))
    {


      int[] maxSize = (int[])lowRes.getProperty("max-size");
      String rgn = "&RGN=0.0,0.0," + 64.0F / maxSize[0] + "," + 64.0F / maxSize[1];
      


      if (canDecode(URLSpec, "&CNT=0.9&WID=64&CVT=JPEG", "JPEG"))
      {
        serverMask = 21;
      } else if (canDecode(URLSpec, "&CNT=0.9&WID=64&CVT=FPX", "FPX"))
      {
        serverMask = 42;
      } else if (canDecode(URLSpec, rgn + "&CVT=JPEG", "JPEG"))
      {
        serverMask = 5;
      } else if (canDecode(URLSpec, rgn + "&CVT=FPX", "FPX"))
      {
        serverMask = 10;
      }
    }
    
    return serverMask;
  }
  







  private static boolean canDecode(String base, String suffix, String fmt)
  {
    StringBuffer buf = new StringBuffer(base);
    
    URL url = null;
    InputStream stream = null;
    RenderedImage rendering = null;
    
    boolean itWorks = false;
    try
    {
      buf.append(suffix);
      url = new URL(buf.toString());
      stream = url.openStream();
      ImageDecoder decoder = ImageCodec.createImageDecoder(fmt, stream, null);
      
      rendering = decoder.decodeAsRenderedImage();
      itWorks = true;
    } catch (Exception e) {
      itWorks = false;
    }
    
    return itWorks;
  }
  







  private static final double[][] matrixMultiply(double[][] A, double[][] B)
  {
    if (A[0].length != B.length) {
      throw new RuntimeException(JaiI18N.getString("IIPCRIF0"));
    }
    
    int nRows = A.length;
    int nCols = B[0].length;
    double[][] C = new double[nRows][nCols];
    
    int nSum = A[0].length;
    for (int r = 0; r < nRows; r++) {
      for (int c = 0; c < nCols; c++) {
        C[r][c] = 0.0D;
        for (int k = 0; k < nSum; k++) {
          C[r][c] += A[r][k] * B[k][c];
        }
      }
    }
    
    return C;
  }
  





  private static final double[][] composeMatrices(double[][] A, double[][] b)
  {
    int nRows = A.length;
    if (nRows != b.length)
      throw new RuntimeException(JaiI18N.getString("IIPCRIF1"));
    if (b[0].length != 1) {
      throw new RuntimeException(JaiI18N.getString("IIPCRIF2"));
    }
    int nCols = A[0].length;
    
    double[][] bcMatrix = new double[nRows][nCols + 1];
    
    for (int r = 0; r < nRows; r++) {
      for (int c = 0; c < nCols; c++) {
        bcMatrix[r][c] = A[r][c];
      }
      bcMatrix[r][nCols] = b[r][0];
    }
    
    return bcMatrix;
  }
  








  private static final double[][] getColorTwistMatrix(ColorModel colorModel, ParameterBlock pb)
  {
    float[] ctwParam = (float[])pb.getObjectParameter(3);
    double[][] ctw = new double[4][4];
    int k = 0;
    for (int r = 0; r < 4; r++) {
      for (int c = 0; c < 4; c++) {
        ctw[r][c] = ctwParam[(k++)];
      }
    }
    


    double[][] H = (double[][])null;
    double[][] d = (double[][])null;
    int csType = colorModel.getColorSpace().getType();
    if ((csType == 6) || (csType == 5))
    {

      H = matrixMultiply(matrixMultiply(YCCA_TO_RGBA, ctw), RGBA_TO_YCCA);
      
      d = YCCA_TO_RGBA_CONST;
    } else {
      H = ctw;
      d = new double[][] { { 0.0D }, { 0.0D }, { 0.0D }, { 0.0D } };
    }
    

    double[][] A = (double[][])null;
    double[][] b = (double[][])null;
    if (csType == 6) {
      if (colorModel.hasAlpha()) {
        A = new double[][] { { 1.0D, 0.0D }, { 1.0D, 0.0D }, { 1.0D, 0.0D }, { 0.0D, 1.0D } };
        
        b = new double[][] { { 0.0D }, { 0.0D }, { 0.0D }, { 0.0D } };
      } else {
        A = new double[][] { { 1.0D }, { 1.0D }, { 1.0D }, { 0.0D } };
        b = new double[][] { { 0.0D }, { 0.0D }, { 0.0D }, { 255.0D } };
      }
    } else if (!colorModel.hasAlpha()) {
      A = new double[][] { { 1.0D, 0.0D, 0.0D }, { 0.0D, 1.0D, 0.0D }, { 0.0D, 0.0D, 1.0D }, { 0.0D, 0.0D, 0.0D } };
      



      b = new double[][] { { 0.0D }, { 0.0D }, { 0.0D }, { 255.0D } };
    } else {
      A = new double[][] { { 1.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 1.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 1.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 1.0D } };
      



      b = new double[][] { { 0.0D }, { 0.0D }, { 0.0D }, { 0.0D } };
    }
    

    boolean truncateChroma = false;
    if ((csType == 6) && (ctwParam[4] == 0.0F) && (ctwParam[7] == 0.0F) && (ctwParam[8] == 0.0F) && (ctwParam[11] == 0.0F))
    {

      truncateChroma = true;
    }
    boolean truncateAlpha = false;
    if ((!colorModel.hasAlpha()) && (ctwParam[15] == 1.0F)) {
      truncateAlpha = true;
    }
    


    double[][] T = (double[][])null;
    if ((truncateAlpha) && (truncateChroma)) {
      T = new double[][] { { 1.0D, 0.0D, 0.0D, 0.0D } };
    } else if (truncateChroma) {
      T = new double[][] { { 1.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 1.0D } };

    }
    else if (truncateAlpha) {
      T = new double[][] { { 1.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 1.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 1.0D, 0.0D } };

    }
    else
    {
      T = new double[][] { { 1.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 1.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 1.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 1.0D } };
    }
    





    double[][] TH = matrixMultiply(T, H);
    double[][] THA = matrixMultiply(TH, A);
    double[][] THb = matrixMultiply(TH, b);
    double[][] THd = matrixMultiply(TH, d);
    double[][] Td = matrixMultiply(T, d);
    
    for (int r = 0; r < THb.length; r++) {
      for (int c = 0; c < THb[r].length; c++) {
        THb[r][c] += Td[r][c] - THd[r][c];
      }
    }
    

    return composeMatrices(THA, THb);
  }
  




  private static final LookupTableJAI createContrastLUT(float K, int numBands)
  {
    byte[] contrastTable = new byte['Ā'];
    
    double p = 0.4300000071525574D;
    

    for (int i = 0; i < 256; i++) {
      float j = (i - 127.5F) / 255.0F;
      float f = 0.0F;
      if (j < 0.0F) {
        f = (float)(-p * Math.pow(-j / p, K));
      } else if (j > 0.0F) {
        f = (float)(p * Math.pow(j / p, K));
      }
      int val = (int)(f * 255.0F + 127.5F);
      if (val < 0) {
        contrastTable[i] = 0;
      } else if (val > 255) {
        contrastTable[i] = -1;
      } else {
        contrastTable[i] = ((byte)(val & 0xFF));
      }
    }
    

    byte[][] data = new byte[numBands][];
    


    if (numBands % 2 == 1) {
      for (int i = 0; i < numBands; i++) {
        data[i] = contrastTable;
      }
    } else {
      for (int i = 0; i < numBands - 1; i++) {
        data[i] = contrastTable;
      }
      data[(numBands - 1)] = new byte['Ā'];
      byte[] b = data[(numBands - 1)];
      for (int i = 0; i < 256; i++) {
        b[i] = ((byte)i);
      }
    }
    
    return new LookupTableJAI(data);
  }
  
  public IIPCRIF()
  {
    super("IIP");
  }
  







  private RenderedImage serverProc(int serverMask, RenderContext renderContext, ParameterBlock paramBlock, int opMask, RenderedImage lowRes)
  {
    if (((serverMask & 0x15) != 21) && ((serverMask & 0x2A) != 42) && ((serverMask & 0x5) != 5) && ((serverMask & 0xA) != 10))
    {


      return null;
    }
    
    ImagingListener listener = ImageUtil.getImagingListener(renderContext);
    

    boolean isJPEG = false;
    boolean isFull = false;
    if ((serverMask & 0x15) == 21) {
      isJPEG = isFull = 1;
    } else if ((serverMask & 0x2A) == 42) {
      isJPEG = false;
      isFull = true;
    } else if ((serverMask & 0x5) == 5) {
      isJPEG = true;
      isFull = false;
    }
    

    StringBuffer buf = new StringBuffer((String)paramBlock.getObjectParameter(0));
    



    if ((opMask & 0x1) != 0) {
      buf.append("&FTR=" + paramBlock.getFloatParameter(2));
    }
    

    if ((opMask & 0x2) != 0) {
      buf.append("&CTW=");
      float[] ctw = (float[])paramBlock.getObjectParameter(3);
      for (int i = 0; i < ctw.length; i++) {
        buf.append(ctw[i]);
        if (i != ctw.length - 1) {
          buf.append(",");
        }
      }
    }
    

    if ((opMask & 0x4) != 0) {
      buf.append("&CNT=" + paramBlock.getFloatParameter(4));
    }
    

    if ((opMask & 0x8) != 0) {
      Rectangle2D roi = (Rectangle2D)paramBlock.getObjectParameter(5);
      
      buf.append("&ROI=" + roi.getX() + "," + roi.getY() + "," + roi.getWidth() + "," + roi.getHeight());
    }
    













    AffineTransform postTransform = new AffineTransform();
    

    AffineTransform at = (AffineTransform)renderContext.getTransform().clone();
    


    if ((at.getTranslateX() != 0.0D) || (at.getTranslateY() != 0.0D)) {
      postTransform.setToTranslation(at.getTranslateX(), at.getTranslateY());
      
      double[] m = new double[6];
      at.getMatrix(m);
      at.setTransform(m[0], m[1], m[2], m[3], 0.0D, 0.0D);
    }
    

    Rectangle2D rgn = null;
    if ((opMask & 0x40) != 0) {
      rgn = (Rectangle2D)paramBlock.getObjectParameter(8);
    } else {
      float aspectRatio = 1.0F;
      if ((opMask & 0x20) != 0) {
        aspectRatio = paramBlock.getFloatParameter(7);
      } else {
        aspectRatio = ((Float)lowRes.getProperty("aspect-ratio")).floatValue();
      }
      
      rgn = new Rectangle2D.Float(0.0F, 0.0F, aspectRatio, 1.0F);
    }
    


    Rectangle dstROI = at.createTransformedShape(rgn).getBounds();
    


    AffineTransform scale = AffineTransform.getScaleInstance(dstROI.getWidth() / rgn.getWidth(), dstROI.getHeight() / rgn.getHeight());
    




    try
    {
      at.preConcatenate(scale.createInverse());
    } catch (Exception e) {
      String message = JaiI18N.getString("IIPCRIF6");
      listener.errorOccurred(message, new ImagingException(message, e), this, false);
    }
    





    AffineTransform afn = (AffineTransform)paramBlock.getObjectParameter(6);
    try
    {
      afn.preConcatenate(at.createInverse());
    } catch (Exception e) {
      String message = JaiI18N.getString("IIPCRIF6");
      listener.errorOccurred(message, new ImagingException(message, e), this, false);
    }
    



    if (isFull)
    {

      buf.append("&WID=" + width + "&HEI=" + height);
    }
    


































    double[] matrix = new double[6];
    afn.getMatrix(matrix);
    buf.append("&AFN=" + matrix[0] + "," + matrix[2] + ",0," + matrix[4] + "," + matrix[1] + "," + matrix[3] + ",0," + matrix[5] + ",0,0,1,0,0,0,0,1");
    




    if ((opMask & 0x20) != 0) {
      buf.append("&RAR=" + paramBlock.getFloatParameter(7));
    }
    

    if ((opMask & 0x40) != 0) {
      Rectangle2D dstRGN = (Rectangle2D)paramBlock.getObjectParameter(8);
      
      buf.append("&RGN=" + dstRGN.getX() + "," + dstRGN.getY() + "," + dstRGN.getWidth() + "," + dstRGN.getHeight());
    }
    


    if ((isFull) && (
      ((opMask & 0x80) != 0) || ((opMask & 0x100) != 0)))
    {
      buf.append("&RFM=" + paramBlock.getIntParameter(9));
      if ((opMask & 0x100) != 0) {
        String axis = (String)paramBlock.getObjectParameter(10);
        if (axis.equalsIgnoreCase("x")) {
          buf.append(",0");
        } else {
          buf.append(",90");
        }
      }
    }
    


    if (((opMask & 0x200) == 0) || 
    






      (isJPEG)) {
      if ((opMask & 0x400) != 0) {
        buf.append("&QLT=" + paramBlock.getIntParameter(12));
      }
      
      if ((opMask & 0x800) != 0) {
        buf.append("&CIN=" + paramBlock.getIntParameter(13));
      }
    }
    

    String format = isJPEG ? "JPEG" : "FPX";
    

    buf.append("&CVT=" + format);
    


    InputStream stream = null;
    RenderedImage rendering = null;
    try {
      URL url = new URL(buf.toString());
      stream = url.openStream();
      MemoryCacheSeekableStream sStream = new MemoryCacheSeekableStream(stream);
      
      rendering = JAI.create(format, sStream);
    } catch (Exception e) {
      String message = JaiI18N.getString("IIPCRIF7") + " " + buf.toString();
      
      listener.errorOccurred(message, new ImagingException(message, e), this, false);
    }
    




    if (!isFull) {
      postTransform.scale(dstROI.getWidth() / rendering.getWidth(), dstROI.getHeight() / rendering.getHeight());
    }
    


    if (!postTransform.isIdentity()) {
      Interpolation interp = Interpolation.getInstance(0);
      
      RenderingHints hints = renderContext.getRenderingHints();
      if ((hints != null) && (hints.containsKey(JAI.KEY_INTERPOLATION))) {
        interp = (Interpolation)hints.get(JAI.KEY_INTERPOLATION);
      }
      rendering = JAI.create("affine", rendering, postTransform, interp);
    }
    

    return rendering;
  }
  






  private RenderedImage clientProc(RenderContext renderContext, ParameterBlock paramBlock, int opMask, RenderedImage lowRes)
  {
    AffineTransform at = renderContext.getTransform();
    RenderingHints hints = renderContext.getRenderingHints();
    
    ImagingListener listener = ImageUtil.getImagingListener(renderContext);
    

    int[] maxSize = (int[])lowRes.getProperty("max-size");
    int maxWidth = maxSize[0];
    int maxHeight = maxSize[1];
    int numLevels = ((Integer)lowRes.getProperty("resolution-number")).intValue();
    


    float aspectRatioSource = maxWidth / maxHeight;
    float aspectRatio = (opMask & 0x20) != 0 ? paramBlock.getFloatParameter(7) : aspectRatioSource;
    


    Rectangle2D bounds2D = new Rectangle2D.Float(0.0F, 0.0F, aspectRatio, 1.0F);
    

    int height;
    

    if (at.isIdentity()) {
      AffineTransform afn = (AffineTransform)paramBlock.getObjectParameter(6);
      
      Rectangle2D bounds = afn.createTransformedShape(bounds2D).getBounds2D();
      
      double H = maxHeight * bounds.getHeight();
      double W = maxHeight * bounds.getWidth();
      double m = Math.max(H, W / aspectRatioSource);
      int height = (int)(m + 0.5D);
      int width = (int)(aspectRatioSource * m + 0.5D);
      at = AffineTransform.getScaleInstance(width, height);
      renderContext = (RenderContext)renderContext.clone();
      renderContext.setTransform(at);
    } else {
      Rectangle bounds = at.createTransformedShape(bounds2D).getBounds();
      int width = width;
      height = height;
    }
    

    int res = numLevels - 1;
    int hRes = maxHeight;
    while (res > 0) {
      hRes = (int)((hRes + 1.0F) / 2.0F);
      if (hRes < height) {
        break;
      }
      res--;
    }
    

    int[] subImageArray = (int[])paramBlock.getObjectParameter(1);
    int subImage = subImageArray.length < res + 1 ? 0 : subImageArray[res];
    if (subImage < 0) {
      subImage = 0;
    }
    ParameterBlock pb = new ParameterBlock();
    pb.add(paramBlock.getObjectParameter(0)).add(res).add(subImage);
    RenderedImage iipRes = JAI.create("iipresolution", pb);
    Vector sources = new Vector(1);
    sources.add(iipRes);
    RenderableImage ri = new MultiResolutionRenderableImage(sources, 0.0F, 0.0F, 1.0F);
    



    if ((opMask & 0x1) != 0) {
      float filter = paramBlock.getFloatParameter(2);
      pb = new ParameterBlock().addSource(ri).add(filter);
      ri = new RenderableImageOp(new FilterCRIF(), pb);
    }
    




    int nBands = iipRes.getSampleModel().getNumBands();
    if ((opMask & 0x2) != 0) {
      double[][] ctw = getColorTwistMatrix(iipRes.getColorModel(), paramBlock);
      
      pb = new ParameterBlock().addSource(ri).add(ctw);
      ri = JAI.createRenderable("bandcombine", pb);
      nBands = ctw.length;
    }
    

    if ((opMask & 0x4) != 0) {
      int csType = iipRes.getColorModel().getColorSpace().getType();
      boolean isPYCC = (csType != 6) && (csType != 5);
      


      if (isPYCC) { double[][] matrix;
        double[][] matrix;
        if (nBands == 3) {
          matrix = composeMatrices(YCC_TO_RGB, YCC_TO_RGB_CONST);
        } else {
          matrix = composeMatrices(YCCA_TO_RGBA, YCCA_TO_RGBA_CONST);
        }
        pb = new ParameterBlock().addSource(ri).add(matrix);
        ri = JAI.createRenderable("bandcombine", pb);
      }
      
      float contrast = paramBlock.getFloatParameter(4);
      LookupTableJAI lut = createContrastLUT(contrast, nBands);
      
      pb = new ParameterBlock().addSource(ri).add(lut);
      ri = JAI.createRenderable("lookup", pb);
      
      if (isPYCC) { double[][] matrix;
        double[][] matrix;
        if (nBands == 3) {
          matrix = composeMatrices(RGB_TO_YCC, RGB_TO_YCC_CONST);
        } else {
          matrix = composeMatrices(RGBA_TO_YCCA, RGBA_TO_YCCA_CONST);
        }
        pb = new ParameterBlock().addSource(ri).add(matrix);
        ri = JAI.createRenderable("bandcombine", pb);
      }
    }
    

    if ((opMask & 0x8) != 0)
    {
      Rectangle2D rect = (Rectangle2D)paramBlock.getObjectParameter(5);
      

      if (!rect.intersects(0.0D, 0.0D, aspectRatioSource, 1.0D)) {
        throw new RuntimeException(JaiI18N.getString("IIPCRIF5"));
      }
      

      Rectangle2D rectS = new Rectangle2D.Float(0.0F, 0.0F, aspectRatioSource, 1.0F);
      


      if (!rect.equals(rectS))
      {
        rect = rect.createIntersection(rectS);
        

        pb = new ParameterBlock().addSource(ri);
        pb.add((float)rect.getMinX()).add((float)rect.getMinY());
        pb.add((float)rect.getWidth()).add((float)rect.getHeight());
        ri = JAI.createRenderable("crop", pb);
      }
    }
    









    if ((opMask & 0x10) != 0) {
      AffineTransform afn = (AffineTransform)paramBlock.getObjectParameter(6);
      
      try
      {
        afn = afn.createInverse();
      }
      catch (NoninvertibleTransformException e) {
        listener.errorOccurred(JaiI18N.getString("AffineNotInvertible"), e, this, false);
      }
      

      pb = new ParameterBlock().addSource(ri).add(afn);
      if ((hints != null) && (hints.containsKey(JAI.KEY_INTERPOLATION))) {
        pb.add(hints.get(JAI.KEY_INTERPOLATION));
      }
      ri = JAI.createRenderable("affine", pb);
    }
    


    Rectangle2D rgn = (opMask & 0x40) != 0 ? (Rectangle2D)paramBlock.getObjectParameter(8) : bounds2D;
    


    if (rgn.isEmpty()) {
      throw new RuntimeException(JaiI18N.getString("IIPCRIF3"));
    }
    

    Rectangle2D riRect = new Rectangle2D.Float(ri.getMinX(), ri.getMinY(), ri.getWidth(), ri.getHeight());
    





    if (!rgn.equals(riRect))
    {
      rgn = rgn.createIntersection(riRect);
      

      pb = new ParameterBlock().addSource(ri);
      pb.add((float)rgn.getMinX()).add((float)rgn.getMinY());
      pb.add((float)rgn.getWidth()).add((float)rgn.getHeight());
      ri = JAI.createRenderable("crop", pb);
    }
    

    return ri.createRendering(renderContext);
  }
  




  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    RenderableImage iipImage = JAI.createRenderable("iip", paramBlock);
    
    return iipImage.createDefaultRendering();
  }
  






  public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock)
  {
    int opMask = getOperationMask(paramBlock);
    
    ImagingListener listener = ImageUtil.getImagingListener(renderContext);
    

    ParameterBlock pb = new ParameterBlock();
    int[] subImageArray = (int[])paramBlock.getObjectParameter(1);
    pb.add(paramBlock.getObjectParameter(0)).add(0).add(subImageArray[0]);
    RenderedImage lowRes = JAI.create("iipresolution", pb);
    

    int serverMask = getServerCapabilityMask((String)paramBlock.getObjectParameter(0), lowRes);
    


    RenderedImage rendering = null;
    

    if (((serverMask & 0x15) == 21) || ((serverMask & 0x2A) == 42) || ((serverMask & 0x5) == 5) || ((serverMask & 0xA) == 10))
    {



      rendering = serverProc(serverMask, renderContext, paramBlock, opMask, lowRes);
    }
    else
    {
      rendering = clientProc(renderContext, paramBlock, opMask, lowRes);
      


      if ((opMask & 0x8) != 0)
      {
        Rectangle2D rgn = (Rectangle2D)paramBlock.getObjectParameter(5);
        


        AffineTransform at = (AffineTransform)((AffineTransform)paramBlock.getObjectParameter(6)).clone();
        


        if (!at.isIdentity()) {
          try {
            at = at.createInverse();
          } catch (Exception e) {
            String message = JaiI18N.getString("IIPCRIF6");
            listener.errorOccurred(message, new ImagingException(message, e), this, false);
          }
        }
        








        at.preConcatenate(renderContext.getTransform());
        

        ROIShape roi = new ROIShape(at.createTransformedShape(rgn));
        

        TiledImage ti = new TiledImage(rendering.getMinX(), rendering.getMinY(), rendering.getWidth(), rendering.getHeight(), rendering.getTileGridXOffset(), rendering.getTileGridYOffset(), rendering.getSampleModel(), rendering.getColorModel());
        








        ti.set(rendering, roi);
        

        pb = new ParameterBlock();
        pb.add(ti.getWidth());
        pb.add(ti.getHeight());
        Byte[] bandValues = new Byte[ti.getSampleModel().getNumBands()];
        
        for (int b = 0; b < bandValues.length; b++) {
          bandValues[b] = new Byte(-1);
        }
        pb.add(bandValues);
        
        ImageLayout il = new ImageLayout();
        il.setSampleModel(ti.getSampleModel());
        RenderingHints rh = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, il);
        

        PlanarImage constImage = JAI.create("constant", pb, rh);
        

        ROI complementROI = new ROIShape(ti.getBounds()).subtract(roi);
        


        int maxTileY = ti.getMaxTileY();
        int maxTileX = ti.getMaxTileX();
        for (int j = ti.getMinTileY(); j <= maxTileY; j++) {
          for (int i = ti.getMinTileX(); i <= maxTileX; i++) {
            if (!roi.intersects(ti.getTileRect(i, j))) {
              ti.setData(constImage.getTile(i, j), complementROI);
            }
          }
        }
        


        rendering = ti;
      }
    }
    




    if (((serverMask & 0x15) != 21) && ((serverMask & 0x2A) != 42))
    {
      if ((opMask & 0x80) != 0)
      {

        EnumeratedParameter transposeType = null;
        switch (paramBlock.getIntParameter(9)) {
        case 90: 
          transposeType = TransposeDescriptor.ROTATE_270;
          break;
        case 180: 
          transposeType = TransposeDescriptor.ROTATE_180;
          break;
        case 270: 
          transposeType = TransposeDescriptor.ROTATE_90;
        }
        
        if (transposeType != null) {
          rendering = JAI.create("transpose", rendering, transposeType);
        }
      }
      

      if ((opMask & 0x100) != 0) {
        String axis = (String)paramBlock.getObjectParameter(10);
        EnumeratedParameter transposeType = axis.equalsIgnoreCase("x") ? TransposeDescriptor.FLIP_VERTICAL : TransposeDescriptor.FLIP_HORIZONTAL;
        


        rendering = JAI.create("transpose", rendering, transposeType);
      }
    }
    
    return rendering;
  }
  




  public Rectangle2D getBounds2D(ParameterBlock paramBlock)
  {
    int opMask = getOperationMask(paramBlock);
    
    if ((opMask & 0x40) != 0) {
      return (Rectangle2D)paramBlock.getObjectParameter(8);
    }
    float aspectRatioDestination;
    float aspectRatioDestination;
    if ((opMask & 0x20) != 0) {
      aspectRatioDestination = paramBlock.getFloatParameter(7);
    }
    else {
      ParameterBlock pb = new ParameterBlock();
      int[] subImageArray = (int[])paramBlock.getObjectParameter(1);
      pb.add(paramBlock.getObjectParameter(0));
      pb.add(0).add(subImageArray[0]);
      RenderedImage lowRes = JAI.create("iipresolution", pb);
      
      int[] maxSize = (int[])lowRes.getProperty("max-size");
      
      aspectRatioDestination = maxSize[0] / maxSize[1];
    }
    
    return new Rectangle2D.Float(0.0F, 0.0F, aspectRatioDestination, 1.0F);
  }
  
  public static void main(String[] args) {
    int nr = 0;
    int nc = 0;
    
    double[][] x = matrixMultiply(RGBA_TO_YCCA, YCCA_TO_RGBA);
    nr = x.length;
    nc = x[0].length;
    for (int r = 0; r < nr; r++) {
      for (int c = 0; c < nc; c++) {
        System.out.print(x[r][c] + " ");
      }
      System.out.println("");
    }
    System.out.println("");
    
    x = matrixMultiply(RGB_TO_YCC, YCC_TO_RGB);
    nr = x.length;
    nc = x[0].length;
    for (int r = 0; r < nr; r++) {
      for (int c = 0; c < nc; c++) {
        System.out.print(x[r][c] + " ");
      }
      System.out.println("");
    }
    System.out.println("");
    
    double[][] b = { { 1.0D }, { 2.0D }, { 3.0D }, { 4.0D } };
    double[][] A = composeMatrices(YCCA_TO_RGBA, b);
    nr = A.length;
    nc = A[0].length;
    for (int r = 0; r < nr; r++) {
      for (int c = 0; c < nc; c++) {
        System.out.print(A[r][c] + " ");
      }
      System.out.println("");
    }
    System.out.println("");
    
    double[][] d4 = matrixMultiply(RGBA_TO_YCCA, YCCA_TO_RGBA_CONST);
    nr = d4.length;
    nc = d4[0].length;
    for (int r = 0; r < nr; r++) {
      for (int c = 0; c < nc; c++) {
        System.out.print(-d4[r][c] + " ");
      }
      System.out.println("");
    }
    System.out.println("");
    
    double[][] d3 = matrixMultiply(RGB_TO_YCC, YCC_TO_RGB_CONST);
    nr = d3.length;
    nc = d3[0].length;
    for (int r = 0; r < nr; r++) {
      for (int c = 0; c < nc; c++) {
        System.out.print(-d3[r][c] + " ");
      }
      System.out.println("");
    }
    System.out.println("");
  }
}
