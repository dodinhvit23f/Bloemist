package com.bloemist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.bloemist.dto.Account;
import com.bloemist.events.StageEvent;
import com.bloemist.message.Message;
import com.bloemist.message.MessageUtils;
import com.bloemist.services.UserServiceI;
import com.constant.ApplicationView;
import com.constant.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
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
  public LoginController(Message messageSource, ApplicationEventPublisher publisher,
      UserServiceI userService) {
    super(messageSource, publisher);
    this.userService = userService;
  }

  public void login() {

    String username = userIdentify.getText();
    String password = userPassword.getText();
    userPassword.setText("");

    if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(password)) {
      MessageUtils.showDialog(AlertType.WARNING, messageSource.getMessage(Constants.ERR_LOGIN_002));
      return;
    }

    Account account = userService.login(username, password);

    if (ObjectUtils.isEmpty(account.getRole())) {
      MessageUtils.showDialog(AlertType.ERROR, messageSource.getMessage(Constants.ERR_LOGIN_001));
      userPassword.setText("");
      return;
    }

    MessageUtils.showDialog(AlertType.ERROR, messageSource.getMessage(Constants.SUSS_LOGIN_001));
  }


  public void registerAccount() {
    stageManager.setView(ApplicationView.REGISTRATOR);
    publisher.publishEvent(new StageEvent(stageManager, null));
  }

  public void restoreAccount() {

  }
}
