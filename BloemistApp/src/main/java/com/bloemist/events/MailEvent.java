package com.bloemist.events;

import com.bloemist.message.Mail;
import lombok.Builder;
import org.springframework.context.ApplicationEvent;

@Builder

public class MailEvent extends ApplicationEvent {

  private static final long serialVersionUID = 1L;
  private Mail mail;
  public MailEvent(Mail source) {
    super(source);
    mail = source;
  }

  public Mail getMailMessage() {
    return this.mail;
  }

}
