package com.bloemist.events;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.bloemist.BloemistUIApplication.StageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * @author Do Dinh Tien
 * @date Dec 2, 2022
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
  
  @Value("classpath:ui/[0012]RetailPrice.fxml")
  Resource homeResoure;
  
  @Value("classpath:Img/ICON.png")
  Resource imageResoure;

  @Override
  public void onApplicationEvent(StageReadyEvent event) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(homeResoure.getURL());
      Parent parent = fxmlLoader.load();
      
      var stage = (Stage) event.getSource();
      Scene scene = new Scene(parent);
      stage.setScene(scene);
      stage.setTitle("Tiêu đề ghi tại StageInitializer sẽ đổi tự động");
      stage.getIcons().add(new Image(imageResoure.getURL().getFile().substring(1)));
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
   
  }

}
