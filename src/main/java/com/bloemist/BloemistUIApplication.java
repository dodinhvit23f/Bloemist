package com.bloemist;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
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
    applicationContext = new SpringApplicationBuilder(BloemistUIApplication.class).run();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
    applicationContext.close();
    Platform.exit();
  }

  @Override
  public void start(Stage stage) throws Exception {
    applicationContext.publishEvent(new StageReadyEvent(stage));
  }

  static class StageReadyEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public StageReadyEvent(Stage stage) {
      super(stage);
    }
    
    public Stage getStage() {
      return (Stage) getSource();
    }

  }

}
