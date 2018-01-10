package signalprocesser.voronoi.tools;

import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxRenderer;



public class CountryListModel
  extends AbstractListModel
  implements ComboBoxModel
{
  private JComboBox combobox;
  private String selectedcountry;
  private ArrayList<String> countries;
  
  public CountryListModel(JComboBox _combobox, ArrayList<String> _countries)
  {
    combobox = _combobox;
    countries = _countries;
    if (countries.size() >= 1) {
      selectedcountry = formatHumanReadable((String)countries.get(0));
    }
    

    combobox.setRenderer(new CountryListRender());
  }
  


  public int getSize()
  {
    return countries.size();
  }
  
  public Object getElementAt(int index) { return formatHumanReadable((String)countries.get(index)); }
  
  public Object getSelectedItem() {
    return selectedcountry == null ? null : selectedcountry;
  }
  
  public void setSelectedItem(Object _selectedcountry) { selectedcountry = ((String)_selectedcountry); }
  
  public String getSelectedCountry()
  {
    int index = combobox.getSelectedIndex();
    if ((index < 0) || (index >= countries.size())) {
      return null;
    }
    return (String)countries.get(index);
  }
  




  public static String formatHumanReadable(String filename)
  {
    int index = filename.lastIndexOf('.');
    if (index > 0) {
      filename = filename.substring(0, index);
    }
    return filename.replace('_', ' ');
  }
  
  public static class CountryListRender extends BasicComboBoxRenderer
  {
    public CountryListRender() {}
    
    public Dimension getSize() {
      Dimension dimension = super.getSize();
      width = -1;
      return dimension;
    }
    
    public Dimension getPreferredSize() { Dimension dimension = super.getPreferredSize();
      width = -1;
      return dimension;
    }
  }
}
