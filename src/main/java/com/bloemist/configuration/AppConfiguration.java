package com.bloemist.configuration;

import com.bloemist.manager.StageManager;
import com.bloemist.message.Message;
import com.constant.ApplicationView;
import java.io.File;
import java.util.Properties;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class AppConfiguration {

  @Value("${spring.mail.username}")
  private String emailUsername;

  @Value("${spring.mail.password}")
  private String password;

  @Value("${spring.mail.port}")
  private int mailPort;

  @Value("${spring.mail.host}")
  private String emailHost;

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

  @Bean
  public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(emailHost);
    mailSender.setPort(mailPort);

    mailSender.setUsername(emailUsername);
    mailSender.setPassword(password);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");

    return mailSender;
  }

  @Lazy
  @Bean
  StageManager stageManager(Stage stage) {

    String classpath = System.getProperty("java.class.path");
    String[] classpathEntries = classpath.split(File.pathSeparator);

    return StageManager.builder().stage(stage).view(ApplicationView.LOGIN)
        .message(getMessage())
        .build();
  }
}
