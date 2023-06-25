package com.bloemist.listener;

import java.util.Objects;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.bloemist.events.MessageSuccess;
import com.bloemist.events.MessageWarning;
import com.bloemist.message.Message;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.util.ObjectUtils;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MessageListener {

  private Message messageSource;

  @EventListener
  public void onSuccess(MessageSuccess event) {
    new Alert(AlertType.CONFIRMATION, messageSource.getMessage(event.getMessage())).show();
  }


  @EventListener
  public void onWarning(MessageWarning event) {
    if (ObjectUtils.isEmpty(event.getAdditional())) {
      new Alert(AlertType.WARNING,
          messageSource.getMessage(event.getMessage())).show();
      return;
    }
    new Alert(AlertType.WARNING,
        String.format(messageSource.getMessage(event.getMessage()),
            event.getAdditional())).show();

  }

}
