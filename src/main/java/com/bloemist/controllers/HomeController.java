package com.bloemist.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import org.springframework.context.ApplicationEventPublisher;
import com.bloemist.message.Message;

public class HomeController extends BaseController {

  protected HomeController(Message messageSource, ApplicationEventPublisher publisher) {
    super(messageSource, publisher);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    
  }

}
