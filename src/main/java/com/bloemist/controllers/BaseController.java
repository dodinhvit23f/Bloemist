package com.bloemist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import com.bloemist.manager.StageManager;
import com.bloemist.message.Message;

public abstract class BaseController {
  @Autowired
  protected Message messageSource;
  @Autowired
  protected ApplicationEventPublisher publisher;
  @Autowired
  protected StageManager stageManager;
}
