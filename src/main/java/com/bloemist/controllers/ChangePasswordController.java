package com.bloemist.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.bloemist.dto.Account;
import com.bloemist.message.Message;
import com.bloemist.message.MessageUtils;
import com.bloemist.services.UserServiceI;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;
import com.constant.Constants;
import com.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;

@Component
public final class ChangePasswordController extends BaseController {

  @FXML
  PasswordField userPassword;
  @FXML
  PasswordField confirmNewpassword;
  @FXML
  PasswordField newPassword;

  UserServiceI userService;

  protected ChangePasswordController(Message messageSource, ApplicationEventPublisher publisher,
      UserServiceI userService) {
    super(messageSource, publisher);
    this.userService = userService;
  }

  public void confirm() {
    String oldPassword = userPassword.getText();
    String password = newPassword.getText();
    String confirmPassword = confirmNewpassword.getText();

    if (ObjectUtils.isEmpty(oldPassword) ||
        ObjectUtils.isEmpty(password)||
        ObjectUtils.isEmpty(confirmPassword)) {
      MessageUtils.showDialog(AlertType.ERROR,
          messageSource.getMessage(Constants.ERR_CHANGE_PASSWORD_004));
      return;
    }

      if (oldPassword.equals(password)) {
        MessageUtils.showDialog(AlertType.ERROR,
            messageSource.getMessage(Constants.ERR_CHANGE_PASSWORD_001));
        return;
      }

    if (!password.equals(confirmPassword)) {
      MessageUtils.showDialog(AlertType.ERROR,
          messageSource.getMessage(Constants.ERR_CHANGE_PASSWORD_002));
      return;
    }
    
    Account accountLogin = ApplicationVariable.getUser();
    
    if(!Utils.hashPassword(oldPassword).equals(accountLogin.getPassword())) {
      MessageUtils.showDialog(AlertType.ERROR,
          messageSource.getMessage(Constants.ERR_CHANGE_PASSWORD_003));
      return;
    }
    
    userService.changeUserPassword(accountLogin, password);
    
    sendMail(messageSource.getMessage(Constants.SUB_CHANGE_PASSWORD_001),
        accountLogin.getEmail(),
        messageSource.getMessage(Constants.CONT_CHANGE_PASSWORD_001));
 
    MessageUtils.showDialog(AlertType.CONFIRMATION,
        messageSource.getMessage(Constants.SUSS_CHANGE_PASSWORD_001));
  }

  public void cancel() {
    switchScence(ApplicationView.HOME);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

}
