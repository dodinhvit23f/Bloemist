package com.bloemist;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class BloemistApplication {
  
  public static void main(String[] args) {
    Application.launch(BloemistUIApplication.class, args);
  }

}
