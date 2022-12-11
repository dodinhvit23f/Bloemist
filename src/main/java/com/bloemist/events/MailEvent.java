package com.bloemist.events;

import org.springframework.context.ApplicationEvent;
import org.springframework.mail.SimpleMailMessage;
import lombok.Builder;

@Builder
public class MailEvent extends ApplicationEvent {

  private static final long serialVersionUID = 1L;

  private SimpleMailMessage message;

  public MailEvent(SimpleMailMessage source) {
    super(source);
    message = source;
  }

  public SimpleMailMessage getMailMessage() {
    return this.message;
  }

}
