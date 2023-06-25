package com.bloemist.events;

import com.bloemist.manager.StageManager;
import com.constant.ApplicationView;
import javafx.stage.Stage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


@Builder
@Getter
@Setter
public class StageEvent extends ApplicationEvent {

  private static final long serialVersionUID = 1L;
  private StageManager manager;

  public StageEvent(StageManager manager) {
    super(manager);
    this.manager = manager;
  }

  public void setView(ApplicationView view) {
    manager.setView(view);
  }

  public Stage getStage() {
    return manager.getStage();
  }


}
