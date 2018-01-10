package javax.mail.search;

import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Message;

































public final class FlagTerm
  extends SearchTerm
{
  protected boolean set;
  protected Flags flags;
  
  public FlagTerm(Flags paramFlags, boolean paramBoolean)
  {
    flags = paramFlags;
    set = paramBoolean;
  }
  


  public Flags getFlags()
  {
    return (Flags)flags.clone();
  }
  


  public boolean getTestSet()
  {
    return set;
  }
  





  public boolean match(Message paramMessage)
  {
    try
    {
      Flags localFlags = paramMessage.getFlags();
      if (set) {
        if (localFlags.contains(flags)) {
          return true;
        }
        return false;
      }
      




      Flags.Flag[] arrayOfFlag = flags.getSystemFlags();
      

      for (int i = 0; i < arrayOfFlag.length; i++) {
        if (localFlags.contains(arrayOfFlag[i]))
        {
          return false;
        }
      }
      String[] arrayOfString = flags.getUserFlags();
      

      for (int j = 0; j < arrayOfString.length; j++) {
        if (localFlags.contains(arrayOfString[j]))
        {
          return false;
        }
      }
      return true;
    }
    catch (Exception localException) {}
    return false;
  }
  



  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof FlagTerm))
      return false;
    FlagTerm localFlagTerm = (FlagTerm)paramObject;
    return (set == set) && (flags.equals(flags));
  }
  


  public int hashCode()
  {
    if (set) return flags.hashCode(); return flags.hashCode() ^ 0xFFFFFFFF;
  }
}
