package com.bloemist.controllers;

import static com.bloemist.constant.Constants.MANAGER;
import static com.bloemist.constant.Constants.SUPERVISOR;

import com.bloemist.controllers.order.CreateOrderController;
import com.bloemist.constant.ApplicationVariable;
import com.bloemist.constant.ApplicationView;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    openCreateOrderDialog(context);
  }

  public static void openCreateOrderDialog(ApplicationContext context) throws IOException {

    if (!CreateOrderController.isPopup()) {
      Stage dialog = new Stage();

      dialog.initStyle(StageStyle.UNIFIED);
      dialog.initModality(Modality.WINDOW_MODAL);

      FXMLLoader fxmlLoader = new FXMLLoader(
          ResourceUtils.getURL((ApplicationView.CREATE_ORDER.getUrl())));
      fxmlLoader.setControllerFactory(context::getBean);
      fxmlLoader.setCharset(StandardCharsets.UTF_8);

      var panel = (Pane) fxmlLoader.load();
      Scene scene = new Scene(panel);
      dialog.setScene(scene);
      CreateOrderController.setPopup(Boolean.TRUE);
      dialog.show();

      dialog.setOnCloseRequest(event ->{
        CreateOrderController.setPopup(Boolean.FALSE);
      });
    }
  }

  @FXML
  public void manageOrder() {
    if (isManager()) {
      switchScene(ApplicationView.MASTER_ORDER);
    }
  }

  private boolean isManager() {
    return Objects.nonNull(ApplicationVariable.getUser()) &&
        (ApplicationVariable.getUser().getRole().contains(MANAGER) ||
            ApplicationVariable.getUser().getRole().contains(SUPERVISOR));
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

  public void managerStaff(ActionEvent actionEvent) {
    if (isManager()) {
      switchScene(ApplicationView.USER_APPOINTMENT);
    }
  }
}
