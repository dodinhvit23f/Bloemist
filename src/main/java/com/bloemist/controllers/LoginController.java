package com.bloemist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.bloemist.dto.AccountDetail;
import com.bloemist.events.MessageSuccess;
import com.bloemist.events.MessageWarning;
import com.bloemist.services.UserServiceI;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;
import com.constant.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public final class LoginController extends BaseController {

  UserServiceI userService;

  @FXML
  TextField userIdentify;
  @FXML
  TextField userPassword;
  @FXML
  CheckBox saveId;

  @Autowired
  public LoginController( ApplicationEventPublisher publisher,
      UserServiceI userService) {
    super(publisher);
    this.userService = userService;
  }

  public void login() {

    String username = userIdentify.getText();
    String password = userPassword.getText();
    userPassword.setText("");

    if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(password)) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_LOGIN_002));
      return;
    }

    AccountDetail account = userService.login(username, password);

    ApplicationVariable.setUser(account);

    if (ObjectUtils.isEmpty(account.getRole())) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_LOGIN_001));
      userPassword.setText("");
      return;
    }

    publisher.publishEvent(new MessageSuccess(Constants.SUSS_LOGIN_001));
    switchScene(ApplicationView.HOME);
  }


  public void registerAccount() {
    switchScene(ApplicationView.REGISTRATOR);
  }

  public void restoreAccount() {
    switchScene(ApplicationView.RECOVER_PASSWORD);
  }

}
