package com.bloemist.configuration;

import com.bloemist.manager.StageManager;
import java.io.File;
import javafx.stage.Stage;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class AppConfiguration {

  @Bean(name = "messageResource")
  public MessageSource getMessageSource() {
    ReloadableResourceBundleMessageSource messageResource = new ReloadableResourceBundleMessageSource();
    messageResource.setBasename("classpath:messages");
    messageResource.setDefaultEncoding("UTF-8");
    messageResource.setUseCodeAsDefaultMessage(true);
    return messageResource;
  }
  @Lazy
  @Bean
  StageManager stageManager(Stage stage){

    String classpath = System.getProperty("java.class.path");
    String[] classpathEntries = classpath.split(File.pathSeparator);

    return StageManager.builder()
        .messageSource(getMessageSource())
        .stage(stage)
        .urlFxmlFile("ui/Login.fxml")
        .path(classpathEntries[0])
        .build();
  }
}
