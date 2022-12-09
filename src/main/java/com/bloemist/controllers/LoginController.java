package com.bloemist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.bloemist.dto.Account;
import com.bloemist.events.StageEvent;
import com.bloemist.services.interfaces.UserServiceI;
import com.constant.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@NoArgsConstructor
public class LoginController extends BaseController {

  @Autowired
  UserServiceI userService;
  @FXML
  TextField userIdentify;
  @FXML
  TextField userPassword;
  @FXML
  CheckBox saveId;

  public void login() {

    String username = userIdentify.getText();
    String password = userPassword.getText();
    userPassword.setText("");

    if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(password)) {
      showDialog(AlertType.WARNING, messageSource.getMessage(Constants.ERR_LOGIN_002));
      return;
    }

    Account account = userService.login(username, password);

    if (ObjectUtils.isEmpty(account.getRole())) {
      showDialog(AlertType.ERROR, messageSource.getMessage(Constants.ERR_LOGIN_001));
      userPassword.setText("");
      return;
    }

    showDialog(AlertType.ERROR, messageSource.getMessage(Constants.SUSS_LOGIN_001));
  }

  private void showDialog(AlertType alertType, String message) {
    Alert alert = new Alert(alertType, message);
    alert.show();
  }

  public void registerAccount() {
     publisher.publishEvent(new StageEvent(this.stageManager, null));
  }

  public void restoreAccount() {

  }
}
