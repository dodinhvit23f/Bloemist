package com.bloemist.configuration;

import com.bloemist.manager.StageManager;
import com.bloemist.message.Message;
import com.constant.ApplicationView;
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
    ReloadableResourceBundleMessageSource messageResource =
        new ReloadableResourceBundleMessageSource();
    messageResource.setBasename("classpath:messages");
    messageResource.setDefaultEncoding("UTF-8");
    messageResource.setUseCodeAsDefaultMessage(true);
    return messageResource;
  }

  @Bean
  Message getMessage() {
    return Message.builder()
        .messageSource(getMessageSource())
        .build();
  }
  

  @Lazy
  @Bean
  StageManager stageManager(Stage stage) {

    String classpath = System.getProperty("java.class.path");
    String[] classpathEntries = classpath.split(File.pathSeparator);

    return StageManager.builder()
        .stage(stage)
        .view(ApplicationView.LOGIN)
        .message(getMessage())
        .path(classpathEntries[0]).build();
  }
}
