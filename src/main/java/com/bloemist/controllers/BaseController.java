package com.bloemist.controllers;

import com.bloemist.events.StageEvent;
import com.bloemist.manager.StageManager;
import com.bloemist.services.MailServiceI;
import com.constant.ApplicationView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;


public abstract class BaseController implements Initializable {

  protected ApplicationEventPublisher publisher;

  @Lazy
  @Autowired
  protected StageManager stageManager;

  protected MailServiceI mailService;

  @Autowired
  protected BaseController(ApplicationEventPublisher publisher, MailServiceI mailService) {
    this.publisher = publisher;
    this.mailService = mailService;
  }

  @Autowired
  protected BaseController(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  protected void switchScene(ApplicationView view, ApplicationView previousView) {
    stageManager.setPreviousView(previousView);
    switchScene(view);
  }

  protected void switchScene(ApplicationView view) {
    stageManager.setView(view);
    publisher.publishEvent(new StageEvent(stageManager));
  }

  protected void sendMail(String subject, String to, String text) {
    mailService.sendMail(subject, to, text);
  }

  protected void sendMail(String subject, String to, String text, String content) {
    mailService.sendMail(subject, to, text, content);
  }

  public void cancel() {
    switchScene(ApplicationView.HOME);
  }

  public Alert confirmDialog() {
    Alert alert = new Alert(AlertType.CONFIRMATION, "Xác Nhận", ButtonType.YES,
        ButtonType.NO);
    alert.showAndWait();
    return alert;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) { //NOSONAR

  }
}
