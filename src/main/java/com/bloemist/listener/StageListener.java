package com.bloemist.listener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import jakarta.annotation.PostConstruct;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.bloemist.controllers.BaseController;
import com.bloemist.events.StageEvent;
import com.bloemist.manager.StageManager;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import static com.bloemist.manager.StageManager.getUrlFxmlFile;

/**
 * @author Do Dinh Tien
 * @date Dec 2, 2022
 */
@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StageListener implements ApplicationListener<StageEvent> {

  final ApplicationContext context;

  @Value("${application.image-url}")
  String iconResource;

  public StageListener(ApplicationContext context) {
    this.context = context;
  }
  private boolean isActive = Boolean.FALSE;

  @Override
  public void onApplicationEvent(StageEvent event) {
    try {
      var manager = (StageManager) event.getSource();
      var stage = manager.getStage();

      FXMLLoader fxmlLoader;

      if (Objects.isNull(ApplicationVariable.getUser()) ||
          !ApplicationVariable.getUser().isCanAccess()) {
        fxmlLoader = new FXMLLoader(getUrlFxmlFile(ApplicationView.LOGIN.getUrl()));
      } else {
        fxmlLoader = new FXMLLoader(manager.getUrlFxmlFile());
      }

      //fxmlLoader = new FXMLLoader(manager.getUrlFxmlFile());

      fxmlLoader.setControllerFactory(context::getBean);
      fxmlLoader.setCharset(StandardCharsets.UTF_8);

      var panel = (Pane) fxmlLoader.load();

      Scene scene = new Scene(panel);
      stage.setScene(scene);
      stage.setTitle(manager.getStageTitle());
      stage.getIcons()
          .add(new Image((getClass().getClassLoader().getResourceAsStream(iconResource))));
      stage.setMinWidth(panel.getPrefWidth());
      stage.setMinHeight(panel.getPrefHeight());

      stage.setOnCloseRequest(e -> {
        e.consume();
        ((BaseController) fxmlLoader.getController()).shutdown();
      });

      scene.setOnMouseMoved(mouseEvent -> isActive = Boolean.TRUE);
      scene.setOnKeyTyped(onKeyEvent -> isActive = Boolean.TRUE);

      stage.show();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setActive( boolean isActive){
    this.isActive = isActive;
  }
}
