package com.bloemist;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import javafx.application.Application;

@SpringBootApplication
@ComponentScan(basePackages = "com.bloemist")
public class BloemistApplication {
  
  public static void main(String[] args) {
    Application.launch(BloemistUIApplication.class, args);
  }

}
