package com.sun.mail.handlers;

import javax.activation.ActivationDataFlavor;














public class text_xml
  extends text_plain
{
  private static ActivationDataFlavor myDF = new ActivationDataFlavor(
    text_plain.class$java$lang$String = text_plain.class$("java.lang.String"), 
    "text/xml", 
    "XML String");
  
  protected ActivationDataFlavor getDF() {
    return myDF;
  }
  
  public text_xml() {}
}
