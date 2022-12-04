package com.bloemist.controllers;

import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;
import com.bloemist.services.interfaces.UserServiceI;
import com.google.common.hash.Hashing;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
  
  UserServiceI userService;
  
  @FXML
  private TextField userID; 
  
  @FXML
  private TextField userPassword;

  public void login() {
    Hashing.sha256()
    .hashString("qwe123", StandardCharsets.UTF_8)
    .toString();
  }
}
