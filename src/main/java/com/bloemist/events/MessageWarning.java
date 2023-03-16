package com.bloemist.events;

import lombok.Getter;

@Getter
public class MessageWarning extends RuntimeMessage {

  /**
   * 
   */
  private static final long serialVersionUID = -2762835189497410060L;
  private String additional;

  public MessageWarning(Object source) {
    super(source);
  }

  public MessageWarning(Object source, String additional) {
    super(source);
    this.additional = additional;
  }

}
