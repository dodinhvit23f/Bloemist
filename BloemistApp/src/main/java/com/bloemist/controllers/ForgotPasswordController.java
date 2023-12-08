package com.bloemist.controllers;

import com.bloemist.dto.AccountDetail;
import com.bloemist.events.MessageSuccess;
import com.bloemist.events.MessageWarning;
import com.bloemist.services.IUserService;
import com.bloemist.services.MailServiceI;
import com.bloemist.constant.ApplicationView;
import com.bloemist.constant.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class ForgotPasswordController extends BaseController {

  @FXML
  TextField userEmail;
  @FXML
  TextField userIdentify;
  @FXML
  TextField otp;
  private String otpConfirm;
  final IUserService userService;

  ForgotPasswordController(ApplicationEventPublisher publisher, IUserService userService, MailServiceI mailService) {
    super(publisher, mailService);
    this.userService = userService;
  }

  @Override
  public void cancel() {
    switchScene(ApplicationView.LOGIN);
  }

  @FXML
  public void verifyOTP() {
    if (otp.getText().length() == Constants.OTP_LENGTH) {
      if (otpConfirm.equals(otp.getText())) {
        String password = userService
            .resetPassword(AccountDetail.builder().username(userIdentify.getText()).build());

        sendMail(Constants.CONT_REST_PASSWORD_SUBJECT_001,
            userEmail.getText(),
            Constants.CONT_REST_PASSWORD, password);

        publisher.publishEvent(new MessageSuccess(
            Constants.SUSS_REST_PASSWORD_001));
        switchScene(ApplicationView.LOGIN);
        return;
      }

      publisher.publishEvent(new MessageWarning(
          Constants.ERR_REST_PASSWORD_002));
    }
  }

  public void sentOTP() {

    String username = userIdentify.getText();
    String email = userEmail.getText();

    String otpCode =
        userService.sendOTP(AccountDetail.builder().username(username).email(email).build());

    if (ObjectUtils.isEmpty(otpCode)) {
      publisher.publishEvent(new MessageWarning(
          Constants.ERR_REST_PASSWORD_001));
      return;
    }

    sendMail(Constants.CONT_REST_PASSWORD_SUBJECT,
        email,
        Constants.CONT_REST_PASSWORD_OTP,
        otpCode);

    otpConfirm = otpCode;
    this.otp.setVisible(Boolean.TRUE);
    userIdentify.setEditable(Boolean.FALSE);
    userEmail.setEditable(Boolean.FALSE);

  }

}
