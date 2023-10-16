package com.bloemist.events;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Builder
@Getter
public class MessageLoading extends ApplicationEvent {

  boolean isPopUp;

  public MessageLoading(boolean source) {
    super(source);
    isPopUp = source;
  }
}
