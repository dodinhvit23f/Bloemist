package com.bloemist.manager;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import com.bloemist.message.Message;
import com.constant.ApplicationView;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class StageManager implements Serializable {

  private static final long serialVersionUID = 1L;

  Message message;
  Stage stage;
  ApplicationView view;
  String path;

  public void setView(ApplicationView view) {
    this.view = view;
  }

  public URL getUrlFxmlFile() {
    try {
      return new File(getPath()).toURI().toURL();
    } catch (MalformedURLException e) {
      System.exit(0);
    }
    return null;
  }

  public String getPath() {
    return String.join("/", path, view.getUrl());
  }

  public String getStageTitle() {
    return message.getMessage(view.getTitle());
  }
}
