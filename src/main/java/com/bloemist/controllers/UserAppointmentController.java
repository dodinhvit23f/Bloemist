package com.bloemist.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.bloemist.dto.Account;
import com.bloemist.dto.AccountApprovement;
import com.bloemist.message.Message;
import com.bloemist.message.MessageUtils;
import com.bloemist.services.UserServiceI;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;
import com.constant.Constants;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class UserAppointmentController extends BaseController {

  @FXML
  ComboBox<String> pickLevel;
  @FXML
  ComboBox<String> pickUser;
  @FXML
  ComboBox<String> pickDivision;
  
  UserServiceI userService;
  
  protected UserAppointmentController(Message messageSource,
      ApplicationEventPublisher publisher,
      UserServiceI userService) {
    super(messageSource, publisher);
    this.userService = userService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    pickLevel.setItems(FXCollections.observableArrayList(Constants.SUPERVISOR, Constants.STAFF));
    
    pickUser.setItems(FXCollections.observableArrayList(
        userService.findApprovableUser()
        .stream()
        .map(Account::getUser)
        .collect(Collectors.toList())));
    
  }
  
  public void cancel() {
    this.switchScence(ApplicationView.LOGIN);
  }
  
  public void approvePosition() 
  {
    String userName = pickUser.getSelectionModel().getSelectedItem();
    String role = pickLevel.getSelectionModel().getSelectedItem();
    
    if(ObjectUtils.isEmpty(userName)||
        ObjectUtils.isEmpty(role)) {
      MessageUtils.showDialog(AlertType.ERROR,
          messageSource.getMessage(Constants.ERR_USER_APPROVEMENT_002));
      return;
    }
    
    
    String code = userService.approveUserRole(AccountApprovement
        .builder()
        .approver(ApplicationVariable.getUser())
        .approvedUser(Account.builder()
            .user(userName)
            .role(role)
            .build())
        .build());
    
    MessageUtils.showDialog(AlertType.CONFIRMATION, messageSource.getMessage(code));
  }
 
}
