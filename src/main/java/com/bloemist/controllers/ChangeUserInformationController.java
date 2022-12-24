package com.bloemist.controllers;

import java.math.BigInteger;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.bloemist.message.Message;
import com.bloemist.message.MessageUtils;
import com.bloemist.services.UserServiceI;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;
import com.constant.Constants;
import com.utils.Utils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class ChangeUserInformationController extends BaseController {

  UserServiceI userService;

  @FXML
  TextField newEmpname;

  @FXML
  TextField newUseradd;

  @FXML
  TextField newUserphone;

  @FXML
  PasswordField userPassword;

  @FXML
  ComboBox<String> newUsergender;

  @FXML
  DatePicker newUserdob;

  protected ChangeUserInformationController(Message messageSource,
      ApplicationEventPublisher publisher, UserServiceI userService) {
    super(messageSource, publisher);
    this.userService = userService;
  }

  public void cancel() {
    switchScence(ApplicationView.HOME);
  }

  public void changeInfomation() {
    
    if (ObjectUtils.isEmpty(newEmpname) || ObjectUtils.isEmpty(newUseradd)
        || ObjectUtils.isEmpty(newUserphone) || ObjectUtils.isEmpty(newUserdob.getValue())
        || ObjectUtils.isEmpty(newUsergender.getValue()) || ObjectUtils.isEmpty(userPassword.getText())) {
      MessageUtils.showDialog(AlertType.ERROR,
          messageSource.getMessage(Constants.ERR_REGISRATOR_001));
      return;
    }
    var password = userPassword.getText();
  if(!Utils.hashPassword(password).equals(ApplicationVariable.getUser().getPassword())) {
    MessageUtils.showDialog(AlertType.ERROR,
        messageSource.getMessage(Constants.ERR_USER_INFO_003));
      return;
    }
    
    var userDetail = ApplicationVariable.getUser();
    
    String gender = newUsergender.getValue();
    var address = newUseradd.getText();
    var dob = Date.from(newUserdob.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    var fullName = newEmpname.getText();
    var phoneNumber = newUserphone.getText();
    
    if(gender.equals(userDetail.getGender()) &&
        address.equals(userDetail.getAddress()) &&
        dob.equals(userDetail.getDob()) &&
        fullName.equals(userDetail.getFullName()) &&
        phoneNumber.endsWith(userDetail.getPhoneNumber())) {
      MessageUtils.showDialog(AlertType.ERROR,
          messageSource.getMessage(Constants.ERR_USER_INFO_002));
      return;
    }
    
    if (dob.compareTo(Date.from(Instant.now())) >= BigInteger.ZERO.intValue()) {
      MessageUtils.showDialog(AlertType.ERROR,
          messageSource.getMessage(Constants.ERR_REGISRATOR_002));
      return;
    }
    
    userDetail.setAddress(address);
    userDetail.setDob(dob);
    userDetail.setFullName(fullName);
    userDetail.setGender(gender);
    userDetail.setPhoneNumber(phoneNumber);
    
    MessageUtils.showDialog(AlertType.INFORMATION,
        messageSource.getMessage( userService.updateUserInformation(userDetail)));

  }


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    newUsergender.setItems(FXCollections.observableArrayList(Constants.MALE, Constants.FEMALE));

    if (Objects.isNull(ApplicationVariable.getUser())) {
      ApplicationVariable
          .setUser(userService.getUserInformation(ApplicationVariable.getUser().getUsername()));
    }
    var userDetail = ApplicationVariable.getUser();
    newUsergender.setValue(userDetail.getGender());
    newEmpname.setText(userDetail.getFullName());
    newUseradd.setText(userDetail.getAddress());
    newUserphone.setText(userDetail.getPhoneNumber());



    newUserdob
        .setValue(LocalDate.ofInstant(userDetail.getDob().toInstant(), ZoneId.systemDefault()));
  }
}
