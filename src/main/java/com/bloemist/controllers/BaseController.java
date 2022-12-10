package com.bloemist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import com.bloemist.message.Message;

public abstract class BaseController {

  protected Message messageSource;
  protected ApplicationEventPublisher publisher;
  
  @Autowired
  protected BaseController(Message messageSource, ApplicationEventPublisher publisher) {
    this.messageSource = messageSource;
    this.publisher = publisher;
  }
  
}
