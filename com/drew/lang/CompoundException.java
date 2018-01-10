package com.drew.lang;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import java.io.PrintStream;
import java.io.PrintWriter;

public class CompoundException
  extends Exception
{
  private static final long serialVersionUID = -9207883813472069925L;
  @Nullable
  private final Throwable _innerException;
  
  public CompoundException(@Nullable String paramString)
  {
    this(paramString, null);
  }
  
  public CompoundException(@Nullable Throwable paramThrowable)
  {
    this(null, paramThrowable);
  }
  
  public CompoundException(@Nullable String paramString, @Nullable Throwable paramThrowable)
  {
    super(paramString);
    _innerException = paramThrowable;
  }
  
  @Nullable
  public Throwable getInnerException()
  {
    return _innerException;
  }
  
  @NotNull
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(super.toString());
    if (_innerException != null)
    {
      localStringBuilder.append("\n");
      localStringBuilder.append("--- inner exception ---");
      localStringBuilder.append("\n");
      localStringBuilder.append(_innerException.toString());
    }
    return localStringBuilder.toString();
  }
  
  public void printStackTrace(@NotNull PrintStream paramPrintStream)
  {
    super.printStackTrace(paramPrintStream);
    if (_innerException != null)
    {
      paramPrintStream.println("--- inner exception ---");
      _innerException.printStackTrace(paramPrintStream);
    }
  }
  
  public void printStackTrace(@NotNull PrintWriter paramPrintWriter)
  {
    super.printStackTrace(paramPrintWriter);
    if (_innerException != null)
    {
      paramPrintWriter.println("--- inner exception ---");
      _innerException.printStackTrace(paramPrintWriter);
    }
  }
  
  public void printStackTrace()
  {
    super.printStackTrace();
    if (_innerException != null)
    {
      System.err.println("--- inner exception ---");
      _innerException.printStackTrace();
    }
  }
}
