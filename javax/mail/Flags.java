package javax.mail;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;





































































public class Flags
  implements Cloneable, Serializable
{
  private int system_flags;
  private Hashtable user_flags;
  private static final int ANSWERED_BIT = 1;
  private static final int DELETED_BIT = 2;
  private static final int DRAFT_BIT = 4;
  private static final int FLAGGED_BIT = 8;
  private static final int RECENT_BIT = 16;
  private static final int SEEN_BIT = 32;
  private static final int USER_BIT = Integer.MIN_VALUE;
  public Flags() {}
  
  public static final class Flag
  {
    public static final Flag ANSWERED = new Flag(Flags.access$0());
    





    public static final Flag DELETED = new Flag(Flags.access$1());
    




    public static final Flag DRAFT = new Flag(Flags.access$2());
    




    public static final Flag FLAGGED = new Flag(Flags.access$3());
    







    public static final Flag RECENT = new Flag(Flags.access$4());
    









    public static final Flag SEEN = new Flag(Flags.access$5());
    









    public static final Flag USER = new Flag(Flags.access$6());
    private int bit;
    
    private Flag(int paramInt)
    {
      bit = paramInt;
    }
  }
  










  public Flags(Flags paramFlags)
  {
    system_flags = system_flags;
    if (user_flags != null) {
      user_flags = ((Hashtable)user_flags.clone());
    }
  }
  



  public Flags(Flag paramFlag)
  {
    system_flags |= bit;
  }
  




  public Flags(String paramString)
  {
    user_flags = new Hashtable(1);
    user_flags.put(paramString, paramString);
  }
  




  public void add(Flag paramFlag)
  {
    system_flags |= bit;
  }
  




  public void add(String paramString)
  {
    if (user_flags == null)
      user_flags = new Hashtable(1);
    user_flags.put(paramString.toLowerCase(), paramString);
  }
  





  public void add(Flags paramFlags)
  {
    system_flags |= system_flags;
    
    if (user_flags != null) {
      if (user_flags == null) {
        user_flags = new Hashtable(1);
      }
      Enumeration localEnumeration = user_flags.keys();
      
      while (localEnumeration.hasMoreElements()) {
        String str = (String)localEnumeration.nextElement();
        user_flags.put(str, user_flags.get(str));
      }
    }
  }
  




  public void remove(Flag paramFlag)
  {
    system_flags &= (bit ^ 0xFFFFFFFF);
  }
  




  public void remove(String paramString)
  {
    if (user_flags != null) {
      user_flags.remove(paramString.toLowerCase());
    }
  }
  




  public void remove(Flags paramFlags)
  {
    system_flags &= (system_flags ^ 0xFFFFFFFF);
    
    if (user_flags != null) {
      if (user_flags == null) {
        return;
      }
      Enumeration localEnumeration = user_flags.keys();
      while (localEnumeration.hasMoreElements()) {
        user_flags.remove(localEnumeration.nextElement());
      }
    }
  }
  



  public boolean contains(Flag paramFlag)
  {
    return (system_flags & bit) != 0;
  }
  




  public boolean contains(String paramString)
  {
    if (user_flags == null) {
      return false;
    }
    return user_flags.containsKey(paramString.toLowerCase());
  }
  







  public boolean contains(Flags paramFlags)
  {
    if ((system_flags & system_flags) != system_flags) {
      return false;
    }
    
    if (user_flags != null) {
      if (user_flags == null)
        return false;
      Enumeration localEnumeration = user_flags.keys();
      
      while (localEnumeration.hasMoreElements()) {
        if (!user_flags.containsKey(localEnumeration.nextElement())) {
          return false;
        }
      }
    }
    
    return true;
  }
  




  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Flags)) {
      return false;
    }
    Flags localFlags = (Flags)paramObject;
    

    if (system_flags != system_flags) {
      return false;
    }
    
    if ((user_flags == null) && (user_flags == null))
      return true;
    if ((user_flags != null) && (user_flags != null) && 
      (user_flags.size() == user_flags.size())) {
      Enumeration localEnumeration = user_flags.keys();
      
      while (localEnumeration.hasMoreElements()) {
        if (!user_flags.containsKey(localEnumeration.nextElement()))
          return false;
      }
      return true;
    }
    
    return false;
  }
  




  public int hashCode()
  {
    int i = system_flags;
    if (user_flags != null) {
      Enumeration localEnumeration = user_flags.keys();
      while (localEnumeration.hasMoreElements())
        i += ((String)localEnumeration.nextElement()).hashCode();
    }
    return i;
  }
  





  public Flag[] getSystemFlags()
  {
    Vector localVector = new Vector();
    if ((system_flags & 0x1) != 0)
      localVector.addElement(Flag.ANSWERED);
    if ((system_flags & 0x2) != 0)
      localVector.addElement(Flag.DELETED);
    if ((system_flags & 0x4) != 0)
      localVector.addElement(Flag.DRAFT);
    if ((system_flags & 0x8) != 0)
      localVector.addElement(Flag.FLAGGED);
    if ((system_flags & 0x10) != 0)
      localVector.addElement(Flag.RECENT);
    if ((system_flags & 0x20) != 0)
      localVector.addElement(Flag.SEEN);
    if ((system_flags & 0x80000000) != 0) {
      localVector.addElement(Flag.USER);
    }
    Flag[] arrayOfFlag = new Flag[localVector.size()];
    localVector.copyInto(arrayOfFlag);
    return arrayOfFlag;
  }
  





  public String[] getUserFlags()
  {
    Vector localVector = new Vector();
    if (user_flags != null) {
      localObject = user_flags.elements();
      
      while (((Enumeration)localObject).hasMoreElements()) {
        localVector.addElement(((Enumeration)localObject).nextElement());
      }
    }
    Object localObject = new String[localVector.size()];
    localVector.copyInto((Object[])localObject);
    return localObject;
  }
  


  public Object clone()
  {
    return new Flags(this);
  }
}
