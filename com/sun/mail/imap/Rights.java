package com.sun.mail.imap;

import java.util.Vector;









































public class Rights
  implements Cloneable
{
  private boolean[] rights = new boolean[''];
  
  public Rights() {}
  

  public static final class Right
  {
    private static Right[] cache = new Right[''];
    




    public static final Right LOOKUP = getInstance('l');
    




    public static final Right READ = getInstance('r');
    



    public static final Right KEEP_SEEN = getInstance('s');
    



    public static final Right WRITE = getInstance('w');
    



    public static final Right INSERT = getInstance('i');
    




    public static final Right POST = getInstance('p');
    




    public static final Right CREATE = getInstance('c');
    



    public static final Right DELETE = getInstance('d');
    



    public static final Right ADMINISTER = getInstance('a');
    

    char right;
    

    private Right(char paramChar)
    {
      if (paramChar >= '')
        throw new IllegalArgumentException("Right must be ASCII");
      right = paramChar;
    }
    



    public static synchronized Right getInstance(char paramChar)
    {
      if (paramChar >= '')
        throw new IllegalArgumentException("Right must be ASCII");
      if (cache[paramChar] == null)
        cache[paramChar] = new Right(paramChar);
      return cache[paramChar];
    }
    
    public String toString() {
      return String.valueOf(right);
    }
  }
  










  public Rights(Rights paramRights)
  {
    System.arraycopy(rights, 0, rights, 0, rights.length);
  }
  




  public Rights(String paramString)
  {
    for (int i = 0; i < paramString.length(); i++) {
      add(Right.getInstance(paramString.charAt(i)));
    }
  }
  



  public Rights(Right paramRight)
  {
    rights[right] = true;
  }
  




  public void add(Right paramRight)
  {
    rights[right] = true;
  }
  





  public void add(Rights paramRights)
  {
    for (int i = 0; i < rights.length; i++) {
      if (rights[i] != 0) {
        rights[i] = true;
      }
    }
  }
  


  public void remove(Right paramRight)
  {
    rights[right] = false;
  }
  





  public void remove(Rights paramRights)
  {
    for (int i = 0; i < rights.length; i++) {
      if (rights[i] != 0) {
        rights[i] = false;
      }
    }
  }
  


  public boolean contains(Right paramRight)
  {
    return rights[right];
  }
  






  public boolean contains(Rights paramRights)
  {
    for (int i = 0; i < rights.length; i++) {
      if ((rights[i] != 0) && (rights[i] == 0)) {
        return false;
      }
    }
    return true;
  }
  




  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Rights)) {
      return false;
    }
    Rights localRights = (Rights)paramObject;
    
    for (int i = 0; i < rights.length; i++) {
      if (rights[i] != rights[i])
        return false;
    }
    return true;
  }
  




  public int hashCode()
  {
    int i = 0;
    for (int j = 0; j < rights.length; j++)
      if (rights[j] != 0)
        i++;
    return i;
  }
  





  public Right[] getRights()
  {
    Vector localVector = new Vector();
    for (int i = 0; i < rights.length; i++)
      if (rights[i] != 0)
        localVector.addElement(Right.getInstance((char)i));
    Right[] arrayOfRight = new Right[localVector.size()];
    localVector.copyInto(arrayOfRight);
    return arrayOfRight;
  }
  


  public Object clone()
  {
    return new Rights(this);
  }
  
  public String toString() {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < rights.length; i++)
      if (rights[i] != 0)
        localStringBuffer.append((char)i);
    return localStringBuffer.toString();
  }
}
