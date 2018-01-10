package text.highlight;







public class RedHighLight
  implements TermHighlighter
{
  public RedHighLight() {}
  





  public String highlightTerm(String term)
  {
    return "<font color='#990033'>" + term + "</font>";
  }
}
