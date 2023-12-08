package com.bloemist.listener;

import com.bloemist.controllers.LoadingController;
import com.bloemist.events.MessageLoading;
import com.bloemist.events.MessageSuccess;
import com.bloemist.events.MessageWarning;
import com.bloemist.message.Message;
import com.bloemist.constant.ApplicationView;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageListener {

  final Message messageSource;
  final ApplicationContext context;
  private AtomicReference<Stage> loadingDialog = new AtomicReference<>();

  @EventListener
  public void onSuccess(MessageSuccess event) {
    new Alert(AlertType.CONFIRMATION,
        messageSource.getMessage(event.getMessage()),
        ButtonType.OK).show();
  }


  @EventListener
  public void onWarning(MessageWarning event) {
    if (ObjectUtils.isEmpty(event.getAdditional())) {
      new Alert(AlertType.WARNING,
          messageSource.getMessage(event.getMessage()),
          ButtonType.OK).show();
      return;
    }
    new Alert(AlertType.WARNING,
        String.format(messageSource.getMessage(event.getMessage()),
            event.getAdditional()),
        ButtonType.OK).show();

  }

  @EventListener
  public void onLoading(MessageLoading event) throws IOException {
    if (event.isPopUp()) {
      openLoadingDialog(context);
      return;
    }
    if (Objects.isNull(loadingDialog.get())) {
      setCountDownEvent(() -> loadingDialog.get().close(), 500);
    }
    loadingDialog.get().close();
  }


  public void openLoadingDialog(ApplicationContext context) throws IOException {

    if (!LoadingController.isPopupScreen()) {
      if (Objects.isNull(loadingDialog.get())) {
        loadingDialog.set(new Stage());
        loadingDialog.get().initStyle(StageStyle.UNIFIED);
        loadingDialog.get().initModality(Modality.WINDOW_MODAL);

        FXMLLoader fxmlLoader = new FXMLLoader(
            ResourceUtils.getURL((ApplicationView.LOADING.getUrl())));
        fxmlLoader.setControllerFactory(context::getBean);
        fxmlLoader.setCharset(StandardCharsets.UTF_8);

        var panel = (Pane) fxmlLoader.load();
        Scene scene = new Scene(panel);
        loadingDialog.get().setScene(scene);
      }
      LoadingController.setPopup(Boolean.TRUE);
      loadingDialog.get().show();
    }
  }

  protected void setCountDownEvent(Runnable runnable, int delayMilliseconds) {
    var task = new TimerTask() {
      @Override
      public void run() {
        runnable.run();
      }
    };
    new Timer().schedule(task, delayMilliseconds);
  }
}
