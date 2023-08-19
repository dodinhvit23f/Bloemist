package com.bloemist.services;

public interface MailServiceI {

  void sendMail(String subject, String to, String text);

  void sendMail(String subject, String to, String text, String... content);
}
