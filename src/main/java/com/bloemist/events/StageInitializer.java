package com.bloemist.events;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.bloemist.BloemistUIApplication;
import com.bloemist.BloemistUIApplication.StageReadyEvent;
import com.bloemist.controllers.UserController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * @author Do Dinh Tien
 * @date Dec 2, 2022
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
  
  @Value("classpath:ui/[0001]Login.fxml")
  Resource homeResoure;
  
  @Value("${application.image-url}")
  Resource iconResoure;
  
  final ApplicationContext context;

  @Override
  public void onApplicationEvent(StageReadyEvent event) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(homeResoure.getURL());
      fxmlLoader.setControllerFactory(context::getBean);
      var panel = (AnchorPane) fxmlLoader.load();
      var stage = (Stage) event.getSource();
      
      Scene scene = new Scene(panel);
      stage.setScene(scene);

      stage.setTitle("Đăng nhập");
      stage.getIcons().add(new Image(iconResoure.getURL().getFile().substring(1)));

      stage.show();
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    
   
  }

}
