package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.SeekableStream;
import java.io.IOException;




























































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































class NoEOFStream
  extends SeekableStream
{
  private SeekableStream stream;
  
  NoEOFStream(SeekableStream ss)
  {
    if (ss == null) {
      throw new IllegalArgumentException();
    }
    
    stream = ss;
  }
  
  public int read() throws IOException {
    int b = stream.read();
    return b < 0 ? 0 : b;
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    int count = stream.read(b, off, len);
    return count < 0 ? len : count;
  }
  
  public long getFilePointer() throws IOException {
    return stream.getFilePointer();
  }
  
  public void seek(long pos) throws IOException {
    stream.seek(pos);
  }
}
