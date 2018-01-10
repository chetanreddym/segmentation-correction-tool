package com.sun.media.jai.rmi;

import com.sun.media.jai.util.DataBufferUtils;
import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;




















public class DataBufferState
  extends SerializableStateImpl
{
  private static Class[] J2DDataBufferClasses = null;
  
  private transient DataBuffer dataBuffer;
  
  static
  {
    try
    {
      Class dbfClass = Class.forName("java.awt.image.DataBufferFloat");
      Class dbdClass = Class.forName("java.awt.image.DataBufferDouble");
      J2DDataBufferClasses = new Class[] { dbfClass, dbdClass };
    }
    catch (ClassNotFoundException e) {}
  }
  
  public static Class[] getSupportedClasses()
  {
    Class[] supportedClasses = null;
    if (J2DDataBufferClasses != null)
    {
      supportedClasses = new Class[] { DataBufferByte.class, DataBufferShort.class, DataBufferUShort.class, DataBufferInt.class, J2DDataBufferClasses[0], J2DDataBufferClasses[1], javax.media.jai.DataBufferFloat.class, javax.media.jai.DataBufferDouble.class, com.sun.media.jai.codecimpl.util.DataBufferFloat.class, com.sun.media.jai.codecimpl.util.DataBufferDouble.class };





    }
    else
    {





      supportedClasses = new Class[] { DataBufferByte.class, DataBufferShort.class, DataBufferUShort.class, DataBufferInt.class, javax.media.jai.DataBufferFloat.class, javax.media.jai.DataBufferDouble.class, com.sun.media.jai.codecimpl.util.DataBufferFloat.class, com.sun.media.jai.codecimpl.util.DataBufferDouble.class };
    }
    









    return supportedClasses;
  }
  







  public DataBufferState(Class c, Object o, RenderingHints h)
  {
    super(c, o, h);
  }
  






  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    DataBuffer dataBuffer = (DataBuffer)theObject;
    

    int dataType = dataBuffer.getDataType();
    out.writeInt(dataType);
    out.writeObject(dataBuffer.getOffsets());
    out.writeInt(dataBuffer.getSize());
    Object dataArray = null;
    switch (dataType) {
    case 0: 
      dataArray = ((DataBufferByte)dataBuffer).getBankData();
      break;
    case 2: 
      dataArray = ((DataBufferShort)dataBuffer).getBankData();
      break;
    case 1: 
      dataArray = ((DataBufferUShort)dataBuffer).getBankData();
      break;
    case 3: 
      dataArray = ((DataBufferInt)dataBuffer).getBankData();
      break;
    case 4: 
      dataArray = DataBufferUtils.getBankDataFloat(dataBuffer);
      break;
    case 5: 
      dataArray = DataBufferUtils.getBankDataDouble(dataBuffer);
      break;
    default: 
      throw new RuntimeException(JaiI18N.getString("DataBufferState0"));
    }
    out.writeObject(dataArray);
  }
  




  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    DataBuffer dataBuffer = null;
    

    int dataType = -1;
    int[] offsets = null;
    int size = -1;
    Object dataArray = null;
    dataType = in.readInt();
    offsets = (int[])in.readObject();
    size = in.readInt();
    dataArray = in.readObject();
    

    switch (dataType) {
    case 0: 
      dataBuffer = new DataBufferByte((byte[][])dataArray, size, offsets);
      
      break;
    case 2: 
      dataBuffer = new DataBufferShort((short[][])dataArray, size, offsets);
      
      break;
    case 1: 
      dataBuffer = new DataBufferUShort((short[][])dataArray, size, offsets);
      
      break;
    case 3: 
      dataBuffer = new DataBufferInt((int[][])dataArray, size, offsets);
      
      break;
    case 4: 
      dataBuffer = DataBufferUtils.createDataBufferFloat((float[][])dataArray, size, offsets);
      
      break;
    case 5: 
      dataBuffer = DataBufferUtils.createDataBufferDouble((double[][])dataArray, size, offsets);
      
      break;
    default: 
      throw new RuntimeException(JaiI18N.getString("DataBufferState0"));
    }
    
    theObject = dataBuffer;
  }
}
