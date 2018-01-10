package ocr.manager;








public class Zonetype
{
  public int id = 0;
  public String name = "";
  public String color = "#000000";
  public boolean editable = false;
  
  public Zonetype(int id_num, String name_shape, String color_shape, boolean is_shape_editable)
  {
    id = id_num;
    name = name_shape;
    color = color_shape;
    editable = is_shape_editable;
  }
  
  public int get_id()
  {
    return id;
  }
  
  public String get_name()
  {
    return name;
  }
  
  public String get_color()
  {
    return color;
  }
  
  public boolean is_editable()
  {
    return editable;
  }
  
  public String toString()
  {
    return "id=" + id + " name=" + name + " color=" + color + " editable=" + editable;
  }
}
