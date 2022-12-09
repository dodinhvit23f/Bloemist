package com.bloemist.controllers;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.bloemist.dto.Account;
import com.bloemist.services.interfaces.UserServiceI;
import com.google.common.hash.Hashing;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@NoArgsConstructor
public class UserController {

  @Autowired
  UserServiceI userService;

  @FXML
  TextField userIdentify;
  @FXML
  PasswordField userPassword;
  @FXML
  CheckBox saveId;
  @FXML
  Button loginButton;
  @FXML
  Hyperlink createLink;
  @FXML
  Hyperlink forgotPassword;
  @FXML
  ImageView logoImg;

  public void login() {
    Hashing.sha256().hashString("qwe123", StandardCharsets.UTF_8).toString();
  }

  public void onClicked() {
    String username = userIdentify.getText();
    String password = userPassword.getText();
    userPassword.setText("");
    Alert alert;

    if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(password)) {
      alert = new Alert(AlertType.WARNING, "Tên đăng nhập hoặc mật khẩu không được để trống");
      alert.show();
      return;
    }

    Account account = userService.login(username, password);

    if (ObjectUtils.isEmpty(account.getRole())) {
      alert = new Alert(AlertType.ERROR, "Tên đăng nhập hoặc mật khẩu sai");
      alert.show();
      userPassword.setText("");
      return;
    }

    alert = new Alert(AlertType.CONFIRMATION, "Đăng nhập thành công");
    alert.show();

  }
}
