package com.bloemist;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import javafx.application.Application;

@SpringBootApplication
public class BloemistApplication {
  
  public static void main(String[] args) {
    Application.launch(BloemistUIApplication.class, args);
  }

}
