package com.bloemist.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.bloemist.dto.AccountDetail;
import com.bloemist.message.Message;
import com.bloemist.message.MessageUtils;
import com.bloemist.services.UserServiceI;
import com.constant.ApplicationVariable;
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

    AccountDetail account = userService.login(username, password);
    
    ApplicationVariable.setUser(account);

    if (ObjectUtils.isEmpty(account.getRole())) {
      MessageUtils.showDialog(AlertType.ERROR, messageSource.getMessage(Constants.ERR_LOGIN_001));
      userPassword.setText("");
      return;
    }

    MessageUtils.showDialog(AlertType.CONFIRMATION, messageSource.getMessage(Constants.SUSS_LOGIN_001));
    switchScence(ApplicationView.HOME);
  }


  public void registerAccount() {
    switchScence(ApplicationView.REGISTRATOR);
  }

  public void restoreAccount() {
    switchScence(ApplicationView.RECOVER_PASSWORD);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
   
  }
}
