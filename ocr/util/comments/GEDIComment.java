package ocr.util.comments;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class GEDIComment
{
  private String image;
  private String xml;
  private int pageNum;
  private int zoneID;
  private Timestamp date;
  private String user;
  private String comment;
  private String archived;
  public static final String dateFormat = "MMM dd, yy h:mm:ss a";
  
  public GEDIComment() {}
  
  public String toString()
  {
    return xml.trim();
  }
  
  public boolean equals(Object obj) {
    GEDIComment anotherComment = (GEDIComment)obj;
    
    return xml.equals(anotherComment.getXml());
  }
  
  public String getImage() {
    return image;
  }
  
  public void setImage(String image) { this.image = image; }
  
  public String getXml() {
    return xml.trim();
  }
  
  public void setXml(String xml) { this.xml = xml.trim(); }
  
  public int getPageNum() {
    return pageNum;
  }
  
  public void setPageNum(String pageNum) { this.pageNum = Integer.parseInt(pageNum.trim()); }
  
  public String getFormattedDate()
  {
    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yy h:mm:ss a");
    return formatter.format(date);
  }
  
  public Timestamp getDate() {
    return date;
  }
  
  public void setDate(Timestamp date) {
    this.date = date;
  }
  
  public void setDate(String date) {
    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yy h:mm:ss a");
    try
    {
      Date parsedDate = formatter.parse(date.trim());
      this.date = new Timestamp(parsedDate.getTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }
  
  public String getUser() {
    return user.trim();
  }
  
  public void setUser(String user) { this.user = user.trim(); }
  
  public String getComment() {
    return 
      comment.trim().replaceAll("\\s+", " ").replaceAll("\n", " ").replaceAll("'", "\"");
  }
  
  public void setComment(String comment) { this.comment = comment.trim().replaceAll("\\s+", " ").replaceAll("\n", " ")
      .replaceAll("'", "\"");
  }
  
  public int getZoneID() { return zoneID; }
  
  public void setZoneID(String zoneID) {
    this.zoneID = (zoneID == null ? 0 : Integer.parseInt(zoneID.trim()));
  }
  
  public void setArchived(String archived) {
    this.archived = archived.trim();
  }
  
  public String getArchived() {
    return archived.trim();
  }
  
  public boolean isArchived() {
    return Boolean.parseBoolean(archived.trim());
  }
  
  public String getImageExt() {
    return image.substring(image.lastIndexOf('.'), image.length());
  }
  
  public String getXmlExt() {
    return xml.substring(xml.lastIndexOf('.'), xml.length());
  }
  
  public String getImageNameWithoutExt() {
    return image.substring(0, image.lastIndexOf('.'));
  }
  
  public String getXmlNameWithoutExt() {
    return xml.substring(0, xml.lastIndexOf('.'));
  }
  

  public String getComposedImageFileName()
  {
    return getXmlNameWithoutExt() + getImageExt();
  }
}
