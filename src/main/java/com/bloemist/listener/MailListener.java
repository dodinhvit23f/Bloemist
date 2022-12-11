package com.bloemist.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import com.bloemist.events.MailEvent;

@Component
@EnableAsync
public class MailListener {

  @Autowired
  JavaMailSender mailSender;
  @Value("${spring.mail.username}")
  private String mailBot;

  @Async
  @EventListener
  public void onApplicationEvent(MailEvent event) {
    var message = event.getMailMessage();
    message.setFrom(mailBot);
    
    mailSender.send(message);
  }

}
