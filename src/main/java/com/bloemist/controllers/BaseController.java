package com.bloemist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import com.bloemist.manager.StageManager;
import com.bloemist.message.Message;

public abstract class BaseController {

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
  
}
