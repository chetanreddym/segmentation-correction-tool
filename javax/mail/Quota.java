package javax.mail;










public class Quota
{
  public String quotaRoot;
  








  public Resource[] resources;
  









  public static class Resource
  {
    public String name;
    








    public long usage;
    







    public long limit;
    








    public Resource(String paramString, long paramLong1, long paramLong2)
    {
      name = paramString;
      usage = paramLong1;
      limit = paramLong2;
    }
  }
  















  public Quota(String paramString)
  {
    quotaRoot = paramString;
  }
  





  public void setResourceLimit(String paramString, long paramLong)
  {
    if (resources == null) {
      resources = new Resource[1];
      resources[0] = new Resource(paramString, 0L, paramLong);
      return;
    }
    for (int i = 0; i < resources.length; i++) {
      if (resources[i].name.equalsIgnoreCase(paramString)) {
        resources[i].limit = paramLong;
        return;
      }
    }
    Resource[] arrayOfResource = new Resource[resources.length + 1];
    System.arraycopy(resources, 0, arrayOfResource, 0, resources.length);
    arrayOfResource[(arrayOfResource.length - 1)] = new Resource(paramString, 0L, paramLong);
    resources = arrayOfResource;
  }
}
