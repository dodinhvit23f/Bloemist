package com.bloemist.events;

import com.bloemist.dto.Account;
import com.bloemist.manager.StageManager;
import com.constant.ApplicationView;
import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;


public class StageEvent extends ApplicationEvent {

  private static final long serialVersionUID = 1L;
  private static Account accountLogin;
  private StageManager manager;

  public StageEvent(StageManager manager, Account account) {
    super(manager);
    this.manager = manager;
    accountLogin = account;
  }

  public StageEvent(StageManager stage) {
    super(stage);
    manager = null;
  }


  public void setView(ApplicationView view) {
    manager.setView(view);
  }

  public void setAccount(Account account) {
    accountLogin = account;
  }

  public Stage getStage() {
    return manager.getStage();
  }

  public Account getAccount() {
    return accountLogin;
  }

}
