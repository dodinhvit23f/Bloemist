package com.bloemist.manager;

import com.bloemist.message.Message;
import com.bloemist.constant.ApplicationView;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.URL;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.util.ResourceUtils;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StageManager implements Serializable {

  private static final long serialVersionUID = 1L;
  public static final String PATH_DIRECTION = "/";

  Message message;
  Stage stage;
  ApplicationView view;
  ApplicationView previousView;

  public StageManager(Message message, Stage stage, ApplicationView view,
                      ApplicationView previousView) {
    super();
    this.message = message;
    this.stage = stage;
    this.view = view;
    this.previousView = previousView;
  }

  public StageManager() {

  }

  public void setView(ApplicationView view) {
    this.view = view;
  }

  public URL getUrlFxmlFile() {
    try {
      return ResourceUtils.getURL(view.getUrl());
    } catch (FileNotFoundException e) {
      System.exit(0);
    }
    return null;
  }

  public static URL getUrlFxmlFile(String viewUrl) {
    try {
      return ResourceUtils.getURL(viewUrl);
    } catch (FileNotFoundException e) {
      System.exit(0);
    }
    return null;
  }

  public String getPath(String url) {
    return String.join(PATH_DIRECTION, url);
  }

  public String getStageTitle() {
    return message.getMessage(view.getTitle());
  }

  public void setPreviousView(ApplicationView view) {
    this.previousView = view;
  }

  public void setStage(Stage stage){
    this.stage = stage;
  }
}
