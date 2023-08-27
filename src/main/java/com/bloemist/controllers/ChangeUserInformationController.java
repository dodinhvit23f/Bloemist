package com.bloemist.controllers;

import com.bloemist.events.MessageWarning;
import com.bloemist.services.IUserService;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;
import com.constant.Constants;
import com.utils.Utils;
import java.math.BigInteger;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class ChangeUserInformationController extends BaseController {

  final IUserService userService;

  @FXML
  TextField newEmpName;

  @FXML
  TextField newUseradd;

  @FXML
  TextField newUserPhone;

  @FXML
  PasswordField userPassword;

  @FXML
  ComboBox<String> newUserGender;

  @FXML
  DatePicker newUserDob;

  String secret;

  ChangeUserInformationController(ApplicationEventPublisher publisher,
      IUserService userService, @Value("${application.slat}") String secret) {
    super(publisher);
    this.userService = userService;
    this.secret = secret;

  }

  public void changeInformation() {

    if (ObjectUtils.isEmpty(newEmpName) || ObjectUtils.isEmpty(newUseradd)
        || ObjectUtils.isEmpty(newUserPhone) || ObjectUtils.isEmpty(newUserDob.getValue())
        || ObjectUtils.isEmpty(newUserGender.getValue())
        || ObjectUtils.isEmpty(userPassword.getText())) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_SUBSCRIBER_001));
      return;
    }
    var password = userPassword.getText();
    if (!Utils.hashPassword(password, secret).equals(ApplicationVariable.getUser().getPassword())) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_USER_INFO_003));
      return;
    }

    var userDetail = ApplicationVariable.getUser();

    String gender = newUserGender.getValue();
    var address = newUseradd.getText();
    var dob = Date.from(newUserDob.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    var fullName = newEmpName.getText();
    var phoneNumber = newUserPhone.getText();

    if (gender.equals(userDetail.getGender()) && address.equals(userDetail.getAddress())
        && dob.equals(userDetail.getDob()) && fullName.equals(userDetail.getFullName())
        && phoneNumber.endsWith(userDetail.getPhoneNumber())) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_USER_INFO_002));
      return;
    }

    if (dob.compareTo(Date.from(Instant.now())) >= BigInteger.ZERO.intValue()) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_SUBSCRIBER_002));
      return;
    }

    userDetail.setAddress(address);
    userDetail.setDob(dob);
    userDetail.setFullName(fullName);
    userDetail.setGender(gender);
    userDetail.setPhoneNumber(phoneNumber);

    Optional result = userService.updateUserInformation(userDetail);
    if(result.isPresent()){
      switchScene(ApplicationView.HOME);
    }
  }


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    newUserGender.setItems(FXCollections.observableArrayList(Constants.MALE, Constants.FEMALE));

    if (Objects.isNull(ApplicationVariable.getUser())) {
      ApplicationVariable
          .setUser(userService.getUserInformation(ApplicationVariable.getUser().getUsername()));
    }

    var userDetail = ApplicationVariable.getUser();
    newUserGender.setValue(userDetail.getGender());
    newEmpName.setText(userDetail.getFullName());
    newUseradd.setText(userDetail.getAddress());
    newUserPhone.setText(userDetail.getPhoneNumber());

    newUserDob
        .setValue(LocalDate.ofInstant(userDetail.getDob().toInstant(), ZoneId.systemDefault()));
  }
}
