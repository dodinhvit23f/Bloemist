package com.bloemist.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import com.bloemist.message.Message;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;

@Component
public final class HomeController extends BaseController {

  protected HomeController(Message messageSource, ApplicationEventPublisher publisher) {
    super(messageSource, publisher);
  }

  public void changePassword() {
    switchScence(ApplicationView.CHANGE_PASSWORD);
  }
  
  public void logout() {
    ApplicationVariable.setUser(null);
    switchScence(ApplicationView.LOGIN);
  }
  
  public void changeUserInformation() {
    switchScence(ApplicationView.CHANGE_USER_INFO);
  }
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    
  }

}
