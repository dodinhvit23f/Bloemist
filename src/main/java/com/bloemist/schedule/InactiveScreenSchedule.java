package com.bloemist.schedule;

import jakarta.annotation.PostConstruct;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.bloemist.events.StageEvent;
import com.bloemist.listener.StageListener;
import com.bloemist.manager.StageManager;
import com.constant.ApplicationView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

@Service
@RequiredArgsConstructor()
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InactiveScreenSchedule {

  final StageListener stageListener;
  final ApplicationEventPublisher publisher;

  @Lazy
  @Autowired
  StageManager stageManager;

  private final Duration inactivityDuration = Duration.minutes(5D);  // Duration for the timeout

  private void enableInactiveTime() {
    Timeline inactivityTimer = new Timeline(new KeyFrame(inactivityDuration, event -> {
      if (!stageListener.isActive()) {

        stageManager.setView(ApplicationView.LOGIN);
        publisher.publishEvent(new StageEvent(stageManager));

        enableInactiveTime();
        return;
      }
      stageListener.setActive(Boolean.FALSE);
      enableInactiveTime();
    }));

    inactivityTimer.setCycleCount(1); // Run once
    inactivityTimer.play();
  }

  @PostConstruct
  void init() {
    enableInactiveTime();
  }

}
