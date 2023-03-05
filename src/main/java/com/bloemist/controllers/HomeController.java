package com.bloemist.controllers;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;

@Component
public final class HomeController extends BaseController {

  protected HomeController(ApplicationEventPublisher publisher) {
    super(publisher);
  }

  public void changePassword() {
    switchScene(ApplicationView.CHANGE_PASSWORD);
  }
  
  public void logout() {
    ApplicationVariable.setUser(null);
    switchScene(ApplicationView.LOGIN);
  }
  
  public void changeUserInformation() {
    switchScene(ApplicationView.CHANGE_USER_INFO);
  }
  
  public void manageOrder() {
    switchScene(ApplicationView.INQUIRY_ORDER);
  }
  
  public void createOrder() {
    switchScene(ApplicationView.CREATE_ORDER);
  }

}
