package javax.media.jai;

import java.util.Arrays;
import java.util.NoSuchElementException;


































public class IntegerSequence
{
  private int min;
  private int max;
  private static final int DEFAULT_CAPACITY = 16;
  private int[] iArray = null;
  

  private int capacity = 0;
  

  private int numElts = 0;
  

  private boolean isSorted = false;
  

  private int currentIndex = -1;
  




  public IntegerSequence(int min, int max)
  {
    if (min > max)
      throw new IllegalArgumentException(JaiI18N.getString("IntegerSequence1"));
    this.min = min;
    this.max = max;
    
    capacity = 16;
    iArray = new int[capacity];
    numElts = 0;
    isSorted = true;
  }
  
  public IntegerSequence()
  {
    this(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }
  








  public void insert(int element)
  {
    if ((element < min) || (element > max)) {
      return;
    }
    
    if (numElts >= capacity) {
      int newCapacity = 2 * capacity;
      int[] newArray = new int[newCapacity];
      System.arraycopy(iArray, 0, newArray, 0, capacity);
      
      capacity = newCapacity;
      iArray = newArray;
    }
    isSorted = false;
    iArray[(numElts++)] = element;
  }
  
  public void startEnumeration()
  {
    if (!isSorted)
    {
      Arrays.sort(iArray, 0, numElts);
      

      int readPos = 1;
      int writePos = 1;
      int prevElt = iArray[0];
      




      for (readPos = 1; readPos < numElts; readPos++) {
        int currElt = iArray[readPos];
        if (currElt != prevElt) {
          iArray[(writePos++)] = currElt;
          prevElt = currElt;
        }
      }
      
      numElts = writePos;
      isSorted = true;
    }
    
    currentIndex = 0;
  }
  
  public boolean hasMoreElements()
  {
    return currentIndex < numElts;
  }
  







  public int nextElement()
  {
    if (currentIndex < numElts) {
      return iArray[(currentIndex++)];
    }
    throw new NoSuchElementException(JaiI18N.getString("IntegerSequence0"));
  }
  



  public int getNumElements()
  {
    return numElts;
  }
  

  public String toString()
  {
    String s;
    String s;
    if (numElts == 0) {
      s = "[<empty>]";
    } else {
      s = "[";
      
      startEnumeration();
      for (int i = 0; i < numElts - 1; i++) {
        s = s + iArray[i];
        s = s + ", ";
      }
      
      s = s + iArray[(numElts - 1)];
      s = s + "]";
    }
    
    return s;
  }
}
