package com.bloemist.controllers;

import com.constant.ApplicationVariable;
import com.constant.ApplicationView;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public final class HomeController extends BaseController {

  @FXML
  public TextField screenCode;

  private HomeController(ApplicationEventPublisher publisher) {
    super(publisher);
  }

  @FXML
  public void changePassword() {
    switchScene(ApplicationView.CHANGE_PASSWORD);
  }

  @FXML
  public void logout() {
    ApplicationVariable.setUser(null);
    switchScene(ApplicationView.LOGIN);
  }

  @FXML
  public void changeUserInformation() {
    switchScene(ApplicationView.CHANGE_USER_INFO);
  }

  @FXML
  public void staffOrder() {
    switchScene(ApplicationView.INQUIRY_ORDER);
  }

  @FXML
  public void createOrder() {
    switchScene(ApplicationView.CREATE_ORDER);
  }

  @FXML
  public void manageOrder() {
    switchScene(ApplicationView.MASTER_ORDER);
  }

  @FXML
  public void jumpToScreen() {
    if (ObjectUtils.isEmpty(screenCode.getText())) {
      Alert alert = new Alert(AlertType.WARNING, "Xin chọn mã màn hình");
      alert.showAndWait();
      return;
    }

    String code = screenCode.getText().trim();
    Optional<ApplicationView> view = ApplicationView.findViewByCode(code);

    if (view.isEmpty()) {
      Alert alert = new Alert(AlertType.WARNING, "Mã màn không tồn tại");
      alert.showAndWait();
      return;
    }

    switchScene(view.get());
  }
}
