package com.bloemist.listener;

import com.bloemist.events.MailEvent;
import com.bloemist.message.Message;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MailListener {
  
  @Autowired
  JavaMailSender mailSender;
  @Autowired
  Message messageSource;
  @Value("${spring.mail.username}")
  private String mailBot;

  @Async
  @EventListener
  public void onApplicationEvent(MailEvent event) {
    SimpleMailMessage message = event.getMailMessage().getMailMessage();
    message.setFrom(mailBot);
    message.setSubject(messageSource.getMessage(message.getSubject()));
    String content = event.getMailMessage().getContent();

    if (Objects.isNull(content)) {
      message.setText(messageSource.getMessage(message.getText()));
    } else {
      message.setText(
          String.format(
              messageSource.getMessage(message.getText()), content));
    }

    mailSender.send(message);
  }

}
