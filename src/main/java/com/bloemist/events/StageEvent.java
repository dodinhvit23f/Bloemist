package com.bloemist.events;

import org.springframework.context.ApplicationEvent;
import com.bloemist.dto.Account;
import javafx.stage.Stage;


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
  
  public Stage getStage() {
    return (Stage) getSource();
  }

}