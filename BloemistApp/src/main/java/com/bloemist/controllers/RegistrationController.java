package com.bloemist.controllers;

import com.bloemist.dto.AccountDetail;
import com.bloemist.events.MessageWarning;
import com.bloemist.services.IUserService;
import com.bloemist.services.MailServiceI;
import com.bloemist.constant.ApplicationView;
import com.bloemist.constant.Constants;
import java.math.BigInteger;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class RegistrationController extends BaseController {

  @Autowired
  IUserService userService;

  RegistrationController(ApplicationEventPublisher publisher,
      MailServiceI mailService) {
    super(publisher, mailService);
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
  TextField empName;
  @FXML
  PasswordField userPassword;
  @FXML
  PasswordField confirmPassword;
  @FXML
  DatePicker userDob;
  @FXML
  ComboBox<String> userGender;

  public void createAccount() {

    String userName = userIdentify.getText();
    String phoneNumber = userPhone.getText();
    String gender = userGender.getValue();
    String password = userPassword.getText();
    String secondPassword = this.confirmPassword.getText();
    String email = userEmail.getText();
    String address = userAddress.getText();
    String fullName = empName.getText();

    Date dob = Date.from(userDob.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

    if (ObjectUtils.isEmpty(userName) || ObjectUtils.isEmpty(phoneNumber)
        || ObjectUtils.isEmpty(gender) || ObjectUtils.isEmpty(password)
        || ObjectUtils.isEmpty(secondPassword) || ObjectUtils.isEmpty(email)
        || ObjectUtils.isEmpty(dob) || ObjectUtils.isEmpty(address)
        || ObjectUtils.isArray(fullName)) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_SUBSCRIBER_001));
      return;
    }

    if (dob.compareTo(Date.from(Instant.now())) >= BigInteger.ZERO.intValue()) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_SUBSCRIBER_002));
      return;
    }

    if (secondPassword.contentEquals(password)) {

      if(userService
          .createAccount(AccountDetail.builder()
              .username(userName)
              .phoneNumber(phoneNumber)
              .gender(gender)
              .password(secondPassword)
              .email(email)
              .address(address)
              .dob(dob)
              .fullName(fullName)
              .build())){
        mailService.sendMail(Constants.SUSS_MAIL_REGISTRATION_SUBJECT,
            email,
            Constants.SUSS_MAIL_REGISTRATION_MESSAGE, userName, password);
        switchScene(ApplicationView.LOGIN);
      }

      return;
    }

    publisher.publishEvent(new MessageWarning(Constants.ERR_SUBSCRIBER_003));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    userGender.setItems(FXCollections.observableArrayList(Constants.MALE, Constants.FEMALE));
  }

  @Override
  public void cancel() {
    switchScene(ApplicationView.LOGIN);
  }
}
