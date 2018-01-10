package com.sun.mail.handlers;

import javax.activation.ActivationDataFlavor;














public class text_html
  extends text_plain
{
  private static ActivationDataFlavor myDF = new ActivationDataFlavor(
    String.class, 
    "text/html", 
    "HTML String");
  
  protected ActivationDataFlavor getDF() {
    return myDF;
  }
  
  public text_html() {}
}
