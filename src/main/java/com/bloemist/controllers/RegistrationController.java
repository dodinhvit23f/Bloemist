package com.bloemist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bloemist.services.interfaces.UserServiceI;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@NoArgsConstructor
public class RegistrationController {
  @Autowired
  UserServiceI userService;

  @FXML
  TextField userIdentify;
  @FXML
  TextField userEmail;
  @FXML
  TextField userPhone;
  @FXML
  TextField userAddress;

  @FXML
  PasswordField userPassword;
  @FXML
  PasswordField confirmPassword;
  @FXML
  DatePicker userDob;
  @FXML
  ComboBox<?> userGender;

  public void cancel() {

  }

  public void createAccount() {

  }

}
