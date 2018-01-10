package javax.media.jai.remote;

import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;

public abstract interface RemoteCRIF
  extends RemoteRIF
{
  public abstract RenderContext mapRenderContext(String paramString1, String paramString2, int paramInt, RenderContext paramRenderContext, ParameterBlock paramParameterBlock, RenderableImage paramRenderableImage)
    throws RemoteImagingException;
  
  public abstract RemoteRenderedImage create(String paramString1, String paramString2, RenderContext paramRenderContext, ParameterBlock paramParameterBlock)
    throws RemoteImagingException;
  
  public abstract Rectangle2D getBounds2D(String paramString1, String paramString2, ParameterBlock paramParameterBlock)
    throws RemoteImagingException;
  
  public abstract Object getProperty(String paramString1, String paramString2, ParameterBlock paramParameterBlock, String paramString3)
    throws RemoteImagingException;
  
  public abstract String[] getPropertyNames(String paramString1, String paramString2)
    throws RemoteImagingException;
  
  public abstract boolean isDynamic(String paramString1, String paramString2)
    throws RemoteImagingException;
}
