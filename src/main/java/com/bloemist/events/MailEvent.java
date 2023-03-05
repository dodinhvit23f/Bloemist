package com.bloemist.events;

import org.springframework.context.ApplicationEvent;
import com.bloemist.message.Mail;
import lombok.Builder;

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
