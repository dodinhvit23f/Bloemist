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
import com.constant.ApplicationView;
import com.constant.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgotPasswordController extends BaseController {

  @FXML
  TextField userEmail;
  @FXML
  TextField userIdentify;
  @FXML
  TextField otp;

  private String otpConfirm;

  @Autowired
  UserServiceI userService;

  protected ForgotPasswordController(Message messageSource, ApplicationEventPublisher publisher) {
    super(messageSource, publisher);
  }

  public void cancel() {
    swichScence(ApplicationView.LOGIN);
  }

  public void verifyOTP() {
    if (otp.getText().length() == Constants.OTP_LENGTH) {
      if (otpConfirm.equals(otp.getText())) {
        String password = userService
            .resetPassword(AccountDetail.builder().username(userIdentify.getText()).build());

        sendMail(messageSource.getMessage(Constants.CONT_REST_PASSWORD_SUBJECT_001),
            userEmail.getText(),
            String.format(messageSource.getMessage(Constants.CONT_REST_PASSWORD), password));
        MessageUtils.showDialog(AlertType.INFORMATION,
            messageSource.getMessage(Constants.SUSS_REST_PASSWORD_001));
        swichScence(ApplicationView.LOGIN);
        return;
      }

      MessageUtils.showDialog(AlertType.ERROR,
          messageSource.getMessage(Constants.ERR_REST_PASSWORD_002));
    }
  }

  public void sentOTP() {

    String username = userIdentify.getText();
    String email = userEmail.getText();

    String otpGenarate =
        userService.sendOTP(AccountDetail.builder().username(username).email(email).build());

    if (ObjectUtils.isEmpty(otpGenarate)) {
      MessageUtils.showDialog(AlertType.ERROR,
          messageSource.getMessage(Constants.ERR_REST_PASSWORD_001));
      return;
    }
    
    sendMail(messageSource.getMessage(Constants.CONT_REST_PASSWORD_SUBJECT),
        email,
        String.format(messageSource.getMessage(Constants.CONT_REST_PASSWORD_OTP), otpGenarate));


    otpConfirm = otpGenarate;
    otp.setVisible(Boolean.TRUE);
    userIdentify.setEditable(Boolean.FALSE);
    userEmail.setEditable(Boolean.FALSE);

  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    otp.textProperty().addListener((observable, oldValue, newValue) -> {
      verifyOTP();
    });

  }

}
