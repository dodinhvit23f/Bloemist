package com.bloemist.events;

import org.springframework.context.ApplicationEvent;

public class RuntimeMessage extends ApplicationEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public RuntimeMessage(Object source) {
    super(source);
  }

  public String getMessage(){
    return this.source.toString();
  }
}
