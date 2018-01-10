package javax.media.jai.util;

public abstract interface ImagingListener
{
  public abstract boolean errorOccurred(String paramString, Throwable paramThrowable, Object paramObject, boolean paramBoolean)
    throws RuntimeException;
}
