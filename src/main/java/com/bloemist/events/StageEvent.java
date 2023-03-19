package com.bloemist.events;

import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import com.bloemist.manager.StageManager;
import com.constant.ApplicationView;
import javafx.stage.Stage;
import lombok.Builder;


@Builder
@Getter
@Setter
public class StageEvent extends ApplicationEvent {

  private static final long serialVersionUID = 1L;
  private StageManager manager;
  private Pane printPane;

  public StageEvent(StageManager manager) {
    super(manager);
    this.manager = manager;
    printPane = null;
  }
  public StageEvent(StageManager manager, Pane pane) {
    super(manager);
    this.manager = manager;
    printPane = pane;
  }

  public void setView(ApplicationView view) {
    manager.setView(view);
  }

  public Stage getStage() {
    return manager.getStage();
  }


}
