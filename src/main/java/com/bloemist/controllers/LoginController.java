package com.bloemist.controllers;

import com.bloemist.dto.AccountDetail;
import com.bloemist.events.MessageWarning;
import com.bloemist.services.IUserService;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;
import com.constant.Constants;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public final class LoginController extends BaseController {

  IUserService userService;

  @FXML
  TextField userIdentify;
  @FXML
  TextField userPassword;
  @FXML
  CheckBox saveId;
  @FXML
  Button loginButton;

  @Autowired
  public LoginController(ApplicationEventPublisher publisher,
      IUserService userService) {
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

    if (ObjectUtils.isEmpty(account.getUsername())) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_LOGIN_001));
      userPassword.setText("");
      return;
    }

    if (ObjectUtils.isEmpty(account.getRole())) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_LOGIN_003));
      userPassword.setText("");
      return;
    }

    switchScene(ApplicationView.HOME);
  }


  public void registerAccount() {
    switchScene(ApplicationView.REGISTRATOR);
  }

  public void restoreAccount() {
    switchScene(ApplicationView.RECOVER_PASSWORD);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    super.initialize(location, resources);
    loginButton.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
      if (event.getCode().equals(KeyCode.ENTER)){
        login();
      }
    });
  }
}
