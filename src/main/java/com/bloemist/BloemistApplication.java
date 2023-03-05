package com.bloemist;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import javafx.application.Application;

@EnableAsync
@SpringBootApplication
public class BloemistApplication {
  
  public static void main(String[] args) {
    Application.launch(BloemistUIApplication.class, args);
  }

}
