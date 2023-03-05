package com.bloemist.controllers;

import com.bloemist.events.MailEvent;
import com.bloemist.events.StageEvent;
import com.bloemist.manager.StageManager;
import com.bloemist.message.Mail;
import com.constant.ApplicationView;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;


public abstract class BaseController implements Initializable {

  protected ApplicationEventPublisher publisher;

  @Lazy
  @Autowired
  protected StageManager stageManager;

  @Autowired
  protected BaseController(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  protected void switchScene(ApplicationView view) {
    stageManager.setView(view);
    publisher.publishEvent(new StageEvent(stageManager));
  }

  protected void sendMail(String subject, String to, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setText(text);
    message.setTo(to);
    message.setSubject(subject);
    message.setSentDate(new Date());

    publisher.publishEvent(new MailEvent(Mail
        .builder()
        .mailMessage(message)
        .build()));
  }

  protected void sendMail(String subject, String to, String text, String content) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setText(text);
    message.setTo(to);
    message.setSubject(subject);
    message.setSentDate(new Date());

    publisher.publishEvent(new MailEvent(Mail
        .builder()
        .mailMessage(message)
        .content(content)
        .build()));
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
