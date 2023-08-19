package com.bloemist.message;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.mail.SimpleMailMessage;

@Builder
@Data
@AllArgsConstructor
public class Mail implements Serializable {
  SimpleMailMessage mailMessage;
  String[] content;
}
