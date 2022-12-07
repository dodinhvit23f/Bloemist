package com.bloemist.manager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StageManager {
  Stage stage;
  private static final String SUFFIX_TITLE = "title";
  private MessageSource messageSource;
  private String urlFxmlFile;
  private String path;

  public StageManager() {
  }


  public StageManager(Stage stage, MessageSource messageSource, String urlFxmlFile, String path) {
    this.stage = stage;
    this.messageSource = messageSource;
    this.urlFxmlFile = urlFxmlFile;
    this.path = path;
  }

  public void setUrlFxmlFile(String urlFxmlFile) {
    this.urlFxmlFile = urlFxmlFile;
  }

  public URL getUrlFxmlFile() {
    try {
      return new File(String.join("/", path, urlFxmlFile)).toURI().toURL();
    } catch (MalformedURLException e) {
      System.exit(0);
    }
    return null;
  }

  public String getStageTitle() {
    return messageSource.getMessage(
        String.join("-",
            urlFxmlFile.replace("ui/","")
                .split("\\.")[0],
            SUFFIX_TITLE), null, Locale.US);
  }
}
