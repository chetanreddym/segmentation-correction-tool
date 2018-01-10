package text.highlight;







public class HTMLHighLight
  implements TermHighlighter
{
  public HTMLHighLight() {}
  





  public String highlightTerm(String term)
  {
    return "<b>" + term + "</b>";
  }
}
