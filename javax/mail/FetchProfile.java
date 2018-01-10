package javax.mail;

import java.util.Vector;












































































public class FetchProfile
{
  public FetchProfile() {}
  
  public static class Item
  {
    public static final Item ENVELOPE = new Item("ENVELOPE");
    










    public static final Item CONTENT_INFO = new Item("CONTENT_INFO");
    



    public static final Item FLAGS = new Item("FLAGS");
    

    private String name;
    

    protected Item(String paramString)
    {
      name = paramString;
    }
  }
  




  private Vector specials = null;
  private Vector headers = null;
  









  public void add(Item paramItem)
  {
    if (specials == null)
      specials = new Vector();
    specials.addElement(paramItem);
  }
  





  public void add(String paramString)
  {
    if (headers == null)
      headers = new Vector();
    headers.addElement(paramString);
  }
  


  public boolean contains(Item paramItem)
  {
    return (specials != null) && (specials.contains(paramItem));
  }
  


  public boolean contains(String paramString)
  {
    return (headers != null) && (headers.contains(paramString));
  }
  




  public Item[] getItems()
  {
    if (specials == null) {
      return new Item[0];
    }
    Item[] arrayOfItem = new Item[specials.size()];
    specials.copyInto(arrayOfItem);
    return arrayOfItem;
  }
  




  public String[] getHeaderNames()
  {
    if (headers == null) {
      return new String[0];
    }
    String[] arrayOfString = new String[headers.size()];
    headers.copyInto(arrayOfString);
    return arrayOfString;
  }
}
