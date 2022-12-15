package com.bloemist.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import com.bloemist.message.Message;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;

@Component
public class HomeController extends BaseController {

  protected HomeController(Message messageSource, ApplicationEventPublisher publisher) {
    super(messageSource, publisher);
  }

  public void changePassword() {
    swichScence(ApplicationView.CHANGE_PASSWORD);
  }
  
  public void logout() {
    ApplicationVariable.setUser(null);
    swichScence(ApplicationView.LOGIN);
  }
  
  public void changeUserInformation() {
    
  }
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    
  }

}
