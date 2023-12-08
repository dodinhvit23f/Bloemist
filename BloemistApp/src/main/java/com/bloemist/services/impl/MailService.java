package com.bloemist.services.impl;

import com.bloemist.events.MailEvent;
import com.bloemist.message.Mail;
import com.bloemist.services.MailServiceI;
import java.util.Date;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MailService implements MailServiceI {

  final ApplicationEventPublisher publisher;


  @Override
  public void sendMail(String subject, String to, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setText(text);
    message.setTo(to);
    message.setSubject(subject);
    message.setSentDate(new Date());

    publisher.publishEvent(new MailEvent(Mail
        .builder()
        .mailMessage(message)
        .build()));
  }

  @Override
  public void sendMail(String subject, String to, String text, String... content) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setText(text);
    message.setTo(to);
    message.setSubject(subject);
    message.setSentDate(new Date());

    publisher.publishEvent(new MailEvent(Mail
        .builder()
        .mailMessage(message)
        .content(content)
        .build()));
  }

}
