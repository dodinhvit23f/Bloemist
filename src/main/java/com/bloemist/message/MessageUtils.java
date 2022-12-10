package com.bloemist.message;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public final class MessageUtils {
  private MessageUtils(){
    
  }
  public static void showDialog(AlertType alertType, String message) {
    Alert alert = new Alert(alertType, message);
    alert.show();
  }
}
