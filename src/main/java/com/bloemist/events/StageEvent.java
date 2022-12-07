package com.bloemist.events;

import com.bloemist.manager.StageManager;
import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class StageEvent extends ApplicationEvent {

  private static final long serialVersionUID = 1L;

  public StageEvent(StageManager stage) {
    super(stage);
  }

  public Stage getStage() {
    return ((StageManager) getSource()).getStage();
  }

}
