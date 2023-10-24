package com.bloemist.schedule;

import java.io.IOException;
import java.util.Objects;

import jakarta.annotation.PostConstruct;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bloemist.events.StageEvent;
import com.bloemist.listener.StageListener;
import com.bloemist.manager.StageManager;
import com.constant.ApplicationView;
import com.google.api.client.auth.oauth2.Credential;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

@Service
@RequiredArgsConstructor()
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InactiveScreenSchedule {

  final StageListener stageListener;
  final ApplicationEventPublisher publisher;
  final Credential credential;

  @Lazy
  @Autowired
  StageManager stageManager;

  private final Duration inactivityDuration = Duration.minutes(35D);  // Duration for the timeout

  private void enableInactiveTime() {
    Timeline inactivityTimer = new Timeline(new KeyFrame(inactivityDuration, event -> {
      if (!stageListener.isActive()) {

        if(!stageManager.getView().equals(ApplicationView.LOGIN)){
          stageManager.setView(ApplicationView.LOGIN);
          publisher.publishEvent(new StageEvent(stageManager));
        }

        enableInactiveTime();
        return;
      }
      stageListener.setActive(Boolean.FALSE);
      enableInactiveTime();
    }));

    inactivityTimer.setCycleCount(1); // Run once
    inactivityTimer.play();
  }

  @Scheduled(cron = "/30 * * * *")
  void resetToken() throws IOException {
    if(Objects.isNull(credential.getExpiresInSeconds()) ||
        credential.getExpiresInSeconds() < 200){
      credential.refreshToken();
    }
  }

  @PostConstruct
  void init() {
    enableInactiveTime();
  }

}
