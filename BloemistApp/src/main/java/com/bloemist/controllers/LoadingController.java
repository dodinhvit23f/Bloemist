package com.bloemist.controllers;

import java.util.concurrent.atomic.AtomicBoolean;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class LoadingController {

  @FXML
  private ProgressIndicator progressIndicator;

  public static final AtomicBoolean isPopup = new AtomicBoolean(Boolean.FALSE);

  public void initialize() {
    progressIndicator.visibleProperty()
        .setValue(Boolean.TRUE);
  }

  public static boolean isPopupScreen() {
    return isPopup.get();
  }

  public static void setPopup(boolean vale) {
    isPopup.set(vale);
  }
}
