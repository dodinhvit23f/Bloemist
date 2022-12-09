package com.bloemist;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import com.bloemist.events.StageEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BloemistUIApplication extends Application {

  ConfigurableApplicationContext applicationContext;
 

  @Override
  public void init() throws Exception {
    super.init();
    applicationContext = new SpringApplicationBuilder(BloemistApplication.class)
        .run(getParameters().getRaw().toArray(new String[0]));
  }

  @Override
  public void stop() throws Exception {
    super.stop();
    applicationContext.close();
    Platform.exit();
  }

  @Override
  public void start(Stage stage) throws Exception {
    applicationContext.publishEvent(new StageEvent(stage));
  }


}
