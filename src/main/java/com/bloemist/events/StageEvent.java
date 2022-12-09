package com.bloemist.events;

import com.bloemist.dto.Account;
import com.bloemist.manager.StageManager;
import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;


public class StageEvent extends ApplicationEvent {

  private static final long serialVersionUID = 1L;
  private static Account accountLogin;

  public StageEvent(Stage stage) {
    super(stage);
  }

  public StageEvent(Stage stage, Account account) {
    super(stage);
    accountLogin = account;
  }

  public StageEvent(StageManager stage) {
    super(stage);
  }

  public Stage getStage() {
    return ((StageManager) getSource()).getStage();
  }
  
  public Account getAccount() {
    return accountLogin;
  }

}
