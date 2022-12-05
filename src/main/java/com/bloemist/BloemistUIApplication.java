package com.bloemist;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import javafx.application.Application;
import javafx.application.HostServices;
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
    
    ApplicationContextInitializer<GenericApplicationContext> initializer = new ApplicationContextInitializer<GenericApplicationContext>() {
      
      @Override
      public void initialize(GenericApplicationContext applicationContext) {
        applicationContext.registerBean(Application.class, () -> BloemistUIApplication.this);
        applicationContext.registerBean(HostServices.class,BloemistUIApplication.this::getHostServices);
        applicationContext.registerBean(Parameters.class, BloemistUIApplication.this::getParameters);
      }
    };
    
    applicationContext = new SpringApplicationBuilder()
        .sources(BloemistApplication.class)
        .initializers(initializer)
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
    applicationContext.publishEvent(new StageReadyEvent(stage));
  }

  public class StageReadyEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public StageReadyEvent(Stage stage) {
      super(stage);
    }
    
    public Stage getStage() {
      return (Stage) getSource();
    }

  }

}
