package com.bloemist.controllers;

import com.bloemist.dto.Account;
import com.bloemist.events.MessageSuccess;
import com.bloemist.events.MessageWarning;
import com.bloemist.services.UserServiceI;
import com.constant.ApplicationVariable;
import com.constant.Constants;
import com.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public final class ChangePasswordController extends BaseController {

  @FXML
  PasswordField userPassword;
  @FXML
  PasswordField confirmNewPassword;
  @FXML
  PasswordField newPassword;
  UserServiceI userService;

  ChangePasswordController(ApplicationEventPublisher publisher,
      UserServiceI userService) {
    super(publisher);
    this.userService = userService;
  }

  public void confirm() {
    String oldPassword = userPassword.getText();
    String password = newPassword.getText();
    String confirmPassword = confirmNewPassword.getText();

    if (ObjectUtils.isEmpty(oldPassword) ||
        ObjectUtils.isEmpty(password) ||
        ObjectUtils.isEmpty(confirmPassword)) {

      publisher.publishEvent(
          new MessageWarning(Constants.ERR_CHANGE_PASSWORD_004));
      return;
    }

    if (oldPassword.equals(password)) {
      publisher.publishEvent(
          new MessageWarning(Constants.ERR_CHANGE_PASSWORD_001));
      return;
    }

    if (!password.equals(confirmPassword)) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_CHANGE_PASSWORD_002));
      return;
    }

    Account accountLogin = ApplicationVariable.getUser();

    if (!Utils.hashPassword(oldPassword).equals(accountLogin.getPassword())) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_CHANGE_PASSWORD_003));
      return;
    }

     userService.changeUserPassword(accountLogin, password);

    sendMail(Constants.SUB_CHANGE_PASSWORD_001,
        accountLogin.getEmail(),
        Constants.CONT_CHANGE_PASSWORD_001);

    publisher.publishEvent(new MessageSuccess(Constants.SUSS_CHANGE_PASSWORD_001));
  }

}
