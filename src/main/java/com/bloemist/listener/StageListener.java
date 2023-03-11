package com.bloemist.listener;

import com.bloemist.events.StageEvent;
import com.bloemist.manager.StageManager;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * @author Do Dinh Tien
 * @date Dec 2, 2022
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StageListener implements ApplicationListener<StageEvent> {
  
  final ApplicationContext context;
  
  @Value("${application.image-url}")
  Resource iconResoure;
  
  public StageListener( ApplicationContext context) {
    this.context = context;
  }
  

  @Override
  public void onApplicationEvent(StageEvent event) {
    try {
      var manager = (StageManager) event.getSource();
      var stage = manager.getStage();

      FXMLLoader fxmlLoader = new FXMLLoader(manager.getUrlFxmlFile());
      fxmlLoader.setControllerFactory(context::getBean);

      var panel = (Pane) fxmlLoader.load();
      final double padding = 20;
      Scene scene = new Scene(panel);
      stage.setScene(scene);
      stage.setTitle(manager.getStageTitle());
      stage.getIcons().add(new Image(new FileInputStream(iconResoure.getFile())));
      stage.setMinWidth(panel.getPrefWidth() + padding);
      stage.setMinHeight(panel.getHeight() + padding);
      stage.setResizable(false);
      stage.show();

    } catch (IOException e) {
      e.printStackTrace();
    }


  }

}
