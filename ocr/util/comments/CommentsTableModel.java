package ocr.util.comments;

import java.sql.Timestamp;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;




public class CommentsTableModel
  extends DefaultTableModel
{
  private static final long serialVersionUID = 1L;
  
  public CommentsTableModel() {}
  
  protected int sortCol = 0;
  protected boolean isSortAscending = true;
  
  public boolean isCellEditable(int row, int column) {
    if (getColumnName(column).equals("Archived")) {
      return true;
    }
    return false;
  }
  
  public Class<?> getColumnClass(int column) {
    if (getColumnName(column).equals("Document"))
      return GEDIComment.class;
    if (getColumnName(column).equals("Page"))
      return Integer.class;
    if (getColumnName(column).equals("Zone"))
      return Integer.class;
    if (getColumnName(column).equals("Date"))
      return Timestamp.class;
    if (getColumnName(column).equals("User"))
      return String.class;
    if (getColumnName(column).equals("Comment"))
      return String.class;
    if (getColumnName(column).equals("Archived")) {
      return Boolean.class;
    }
    return Object.class;
  }
  
  public String getColumnName(int column) {
    String str = "";
    switch (column) {
    case 0: 
      str = "Document";
      break;
    case 1: 
      str = "Page";
      break;
    case 2: 
      str = "Zone";
      break;
    case 3: 
      str = "Date";
      break;
    case 4: 
      str = "User";
      break;
    case 5: 
      str = "Comment";
      break;
    case 6: 
      str = "Archived";
    }
    
    
    if (column == sortCol)
      str = str + (isSortAscending ? "▲" : "▼");
    return str;
  }
  

  public int custom_compare(int col, Object v1_in, Object v2_in)
  {
    Vector<Object> v1 = (Vector)v1_in;
    Vector<Object> v2 = (Vector)v2_in;
    
    String xml1 = ((GEDIComment)v1.get(0)).getXml().toLowerCase();
    String xml2 = ((GEDIComment)v2.get(0)).getXml().toLowerCase();
    
    Integer page1 = (Integer)v1.get(1);
    Integer page2 = (Integer)v2.get(1);
    
    Integer zone1 = (Integer)v1.get(2);
    Integer zone2 = (Integer)v2.get(2);
    
    Timestamp date1 = (Timestamp)v1.get(3);
    Timestamp date2 = (Timestamp)v2.get(3);
    
    String user1 = ((String)v1.get(4)).toLowerCase();
    String user2 = ((String)v2.get(4)).toLowerCase();
    
    String comm1 = ((String)v1.get(5)).toLowerCase();
    String comm2 = ((String)v2.get(5)).toLowerCase();
    
    Boolean isArchived1 = (Boolean)v1.get(6);
    Boolean isArchived2 = (Boolean)v2.get(6);
    
    switch (col) {
    case 0: 
      if (!xml1.equals(xml2))
        return xml1.compareTo(xml2);
      if (page1 != page2)
        return page1.compareTo(page2);
      if (zone1 != zone2)
        return zone1.compareTo(zone2);
      if (date1 != date2)
        return date1.compareTo(date2);
      if (!user1.equals(user2))
        return user1.compareTo(user2);
      if (!comm1.equals(comm2))
        return comm1.compareTo(comm2);
      if (isArchived1 != isArchived2)
        return isArchived1.compareTo(isArchived2);
    case 1: 
      if (page1 != page2)
        return page1.compareTo(page2);
      if (zone1 != zone2)
        return zone1.compareTo(zone2);
      if (date1 != date2)
        return date1.compareTo(date2);
      if (!user1.equals(user2))
        return user1.compareTo(user2);
      if (!comm1.equals(comm2))
        return comm1.compareTo(comm2);
      if (isArchived1 != isArchived2)
        return isArchived1.compareTo(isArchived2);
      break;
    case 2: 
      if (zone1 != zone2)
        return zone1.compareTo(zone2);
      if (date1 != date2)
        return date1.compareTo(date2);
      if (!user1.equals(user2))
        return user1.compareTo(user2);
      if (!comm1.equals(comm2))
        return comm1.compareTo(comm2);
      if (isArchived1 != isArchived2)
        return isArchived1.compareTo(isArchived2);
      break;
    case 3: 
      if (date1 != date2)
        return date1.compareTo(date2);
      if (!user1.equals(user2))
        return user1.compareTo(user2);
      if (!comm1.equals(comm2))
        return comm1.compareTo(comm2);
      if (isArchived1 != isArchived2)
        return isArchived1.compareTo(isArchived2);
      break;
    case 4: 
      if (user1 != user2)
        return user1.compareTo(user2);
      if (!comm1.equals(comm2))
        return comm1.compareTo(comm2);
      if (isArchived1 != isArchived2)
        return isArchived1.compareTo(isArchived2);
      break;
    case 5: 
      if (comm1 != comm2)
        return comm1.compareTo(comm2);
      if (isArchived1 != isArchived2)
        return isArchived1.compareTo(isArchived2);
      break;
    case 6: 
      if (isArchived1 != isArchived2)
        return isArchived1.compareTo(isArchived2);
      break;
    }
    return 0;
  }
  
  public int getSortCol()
  {
    return sortCol;
  }
  
  public boolean isSortAscending() {
    return isSortAscending;
  }
  
  public void setSortCol(int sortCol)
  {
    this.sortCol = sortCol;
  }
  
  public void setSortAscending(boolean isSortAscending) {
    this.isSortAscending = isSortAscending;
  }
}
