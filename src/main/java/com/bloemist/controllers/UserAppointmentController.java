package com.bloemist.controllers;

import com.bloemist.dto.Account;
import com.bloemist.dto.AccountApprovement;
import com.bloemist.events.MessageWarning;
import com.bloemist.services.IUserService;
import com.constant.ApplicationVariable;
import com.constant.Constants;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class UserAppointmentController extends BaseController {

  @FXML
  ComboBox<String> pickLevel;
  @FXML
  ComboBox<String> pickUser;
  @FXML
  ComboBox<String> pickDivision;

  IUserService userService;

  UserAppointmentController(ApplicationEventPublisher publisher,
                            IUserService userService) {
    super(publisher);
    this.userService = userService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    pickLevel.setItems(FXCollections.observableArrayList(Constants.SUPERVISOR, Constants.STAFF));

    pickDivision.setItems(FXCollections.observableArrayList(Constants.FLORIST));

    pickUser.setItems(FXCollections.observableArrayList(
        userService.findApprovableUser()
            .stream()
            .map(Account::getUsername)
            .collect(Collectors.toList())));

  }

  public void approvePosition() {
    String userName = pickUser.getSelectionModel().getSelectedItem();
    String role = pickLevel.getSelectionModel().getSelectedItem();
    String division = pickDivision.getSelectionModel().getSelectedItem();

    if (ObjectUtils.isEmpty(userName) ||
        ObjectUtils.isEmpty(role) ||
        ObjectUtils.isEmpty(division)) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_USER_APPROVEMENT_002));
      return;
    }

    userService.approveUserRole(AccountApprovement
        .builder()
        .approver(ApplicationVariable.getUser())
        .approvedUser(Account.builder()
            .username(userName)
            .role(role)
            .division(division)
            .build())
        .build());

  }

}
