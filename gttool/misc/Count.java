package gttool.misc;

public class Count implements Comparable<Count>, Cloneable {
  private int count;
  
  public Count() {
    count = 0;
  }
  
  public Count add() {
    count += 1;
    return this;
  }
  
  public Count sub() {
    if (count > 0)
      count -= 1;
    return this;
  }
  
  public boolean equals(Object o) {
    return ((o instanceof Count)) && (count == count);
  }
  

  public String toString() { return new Integer(count).toString(); }
  
  public Object clone() {
    Count clone = new Count();
    count = count;
    return clone;
  }
  





  public int compareTo(Count c)
  {
    if (count < count)
      return -1;
    if (count > count)
      return 1;
    return 0;
  }
}
