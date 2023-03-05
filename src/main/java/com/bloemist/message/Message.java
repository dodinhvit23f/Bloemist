package com.bloemist.message;

import java.io.Serializable;
import java.util.Locale;
import org.springframework.context.MessageSource;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Builder
public class Message implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -773050879034937921L;
  MessageSource messageSource; //NOSONAR

  public String getMessage(String code) {
    return messageSource.getMessage(code, null, Locale.ENGLISH);
  }
}
