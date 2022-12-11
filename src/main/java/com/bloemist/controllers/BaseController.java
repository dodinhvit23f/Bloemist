package com.bloemist.controllers;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import com.bloemist.events.MailEvent;
import com.bloemist.events.StageEvent;
import com.bloemist.manager.StageManager;
import com.bloemist.message.Message;
import com.constant.ApplicationView;
import javafx.fxml.Initializable;

public abstract class BaseController implements Initializable{

  protected Message messageSource;
  protected ApplicationEventPublisher publisher;
  
  @Autowired
  @Lazy
  StageManager stageManager;
  
  @Autowired
  protected BaseController(Message messageSource, ApplicationEventPublisher publisher) {
    this.messageSource = messageSource;
    this.publisher = publisher;
  }
  
  protected void swichScence(ApplicationView view) {
    stageManager.setView(view);
    publisher.publishEvent(new StageEvent(stageManager, null));
  }
  
  protected void sendMail(String subject, String to, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setText(text);
    message.setTo(to);
    message.setSubject(subject);
    message.setSentDate(new Date());
    
    publisher.publishEvent(new MailEvent(message));
  }
  
}
