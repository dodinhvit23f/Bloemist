package com.bloemist.controllers;

import com.bloemist.controllers.order.CreateOrderController;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;

@Component
public final class HomeController extends BaseController {

  final ApplicationContext context;

  @FXML
  public TextField screenCode;

  private HomeController(ApplicationEventPublisher publisher, ApplicationContext context) {
    super(publisher);
    this.context = context;
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
  public void createOrder() throws IOException {
    final Stage dialog = new Stage();
    dialog.initModality(Modality.APPLICATION_MODAL);
    FXMLLoader fxmlLoader = new FXMLLoader(
        ResourceUtils.getURL((ApplicationView.CREATE_ORDER.getUrl())));
    fxmlLoader.setControllerFactory(context::getBean);
    fxmlLoader.setCharset(StandardCharsets.UTF_8);

    var panel = (Pane) fxmlLoader.load();
    Scene scene = new Scene(panel);
    dialog.setScene(scene);

    dialog.initOwner(stageManager.getStage());
    dialog.show();

    var controller = (CreateOrderController) fxmlLoader.getController();
    controller.setPopup(Boolean.TRUE);
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
