package com.bloemist.manager;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import com.bloemist.message.Message;
import com.constant.ApplicationView;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class StageManager implements Serializable {

  private static final long serialVersionUID = 1L;
  
  Stage stage;
  Message message;
  ApplicationView view;
  String path;


  public void setView(ApplicationView view) {
    this.view = view;
  }

  public URL getUrlFxmlFile() {
    try {
      return new File(String.join("/", path, view.getUrl())).toURI().toURL();
    } catch (MalformedURLException e) {
      System.exit(0);
    }
    return null;
  }

  public String getStageTitle() {
    return message.getMessage(view.getTitle());
  }
}
