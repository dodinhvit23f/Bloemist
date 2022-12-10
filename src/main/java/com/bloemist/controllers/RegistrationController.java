package com.bloemist.controllers;

import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.bloemist.dto.AccountDetail;
import com.bloemist.events.StageEvent;
import com.bloemist.message.Message;
import com.bloemist.message.MessageUtils;
import com.bloemist.services.UserServiceI;
import com.constant.ApplicationView;
import com.constant.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public final class RegistrationController extends BaseController {

  @Autowired
  UserServiceI userService;

  protected RegistrationController(Message messageSource, ApplicationEventPublisher publisher) {
    super(messageSource, publisher);
  }

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
  ComboBox<String> userGender;

  public void cancel() {
    stageManager.setView(ApplicationView.LOGIN);
    publisher.publishEvent(StageEvent.builder().manager(stageManager).build());
  }

  public void createAccount() {
    
    String userName = userIdentify.getText();
    String phoneNumber = userPhone.getText();
    String gender = userGender.getValue();
    String password = userPassword.getText();
    String comfirmPassword = userPassword.getText();
    String email = userEmail.getText();
    Date dob = Date.from(userDob.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

    if (ObjectUtils.isEmpty(userName) || ObjectUtils.isEmpty(phoneNumber)
        || ObjectUtils.isEmpty(gender) || ObjectUtils.isEmpty(password)
        || ObjectUtils.isEmpty(comfirmPassword) || ObjectUtils.isEmpty(email)
        || ObjectUtils.isEmpty(dob)) {
      MessageUtils.showDialog(AlertType.ERROR, Constants.ERR_REGISRATOR_001);
      return;
    }

    if (dob.compareTo(Date.from(Instant.now())) >= BigInteger.ZERO.intValue()) {
      MessageUtils.showDialog(AlertType.ERROR, Constants.ERR_REGISRATOR_002);
      return;
    }
    
    if(comfirmPassword.contentEquals(password)) {
      String code = userService.createAccount( AccountDetail.builder()
          .userName(userName)
          .phoneNumber(phoneNumber)
          .gender(gender)
          .password(comfirmPassword)
          .email(email)
          .dob(dob)
          .build());
      
      MessageUtils.showDialog(AlertType.ERROR, code);
      return;
    }
    
    MessageUtils.showDialog(AlertType.ERROR, Constants.ERR_REGISRATOR_003);
    
   
  }

}
